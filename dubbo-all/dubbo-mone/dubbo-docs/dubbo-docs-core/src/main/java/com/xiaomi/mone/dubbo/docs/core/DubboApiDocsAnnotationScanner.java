/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.dubbo.docs.core;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.xiaomi.mone.dubbo.docs.annotations.ApiDoc;
import com.xiaomi.mone.dubbo.docs.annotations.ApiDocClassDefine;
import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.dubbo.docs.core.beans.*;
import com.xiaomi.mone.dubbo.docs.utils.ClassTypeUtil;
import com.xiaomi.mone.dubbo.docs.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.apidocs.core.providers.DubboDocProviderImpl;
import org.apache.dubbo.apidocs.core.providers.IDubboDocProvider;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.xiaomi.mone.dubbo.docs.core.Constants.*;
import static java.util.Optional.ofNullable;

/**
 * Scan and process dubbo doc annotations.
 */
@Import({DubboDocProviderImpl.class})
public class DubboApiDocsAnnotationScanner implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DubboApiDocsAnnotationScanner.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApplicationConfig application;

    @Autowired
    private RegistryConfig registry;

    @Autowired
    private ProtocolConfig protocol;

    @Autowired(required = false)
    private ProviderConfig providerConfig;

    public static final Gson gson = new Gson();

    public static final String DEFAULT_ENV = "staging";

    @Value("${MiApi.notifyUrl:http://127.0.0.1:8999/OpenApi/dubboApiUpdateNotify}")
    private String notifyUrl;

    @Value("${MiApi.opUser:default_user}")
    private String opUser;

    @Value("${MiApi.updateMsg:auto_update}")
    private String updateMsg;

    @Value("${MiApi.autoUpdate:false}")
    private boolean autoUpdate;

    @Value("${MiApi.mavenAddr:''}")
    private String mavenAddr;

    @Value("${MiApi.api.pom.path:''}")
    private String apiPomPath;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // Register dubbo doc provider
        IDubboDocProvider dubboDocProvider = applicationContext.getBean(IDubboDocProvider.class);
        exportDubboService(IDubboDocProvider.class, dubboDocProvider, false);

//        if (!apiPomPath.isEmpty()){
//            mavenAddr = parsePom(apiPomPath);
//        }

        LOG.info("================= Dubbo API Docs--Start scanning and processing doc annotations ================");

        Map<String, Object> apiModules = applicationContext.getBeansWithAnnotation(ApiModule.class);
        List<String> serviceNameList = new ArrayList<>(apiModules.size());
        apiModules.forEach((key, apiModuleTemp) -> {
            Class<?> apiModuleClass;
            if (AopUtils.isAopProxy(apiModuleTemp)) {
                apiModuleClass = AopUtils.getTargetClass(apiModuleTemp);
            } else {
                apiModuleClass = apiModuleTemp.getClass();
            }
            ApiModule moduleAnn = apiModuleClass.getAnnotation(ApiModule.class);
            if (!apiModuleClass.isAnnotationPresent(Service.class) && !apiModuleClass.isAnnotationPresent(DubboService.class)) {
                LOG.warn("【Warning】" + apiModuleClass.getName() + " @ApiModule annotation is used, " +
                        "but it is not a dubbo provider (without " + Service.class.getName() + " or " +
                        DubboService.class.getName() + " annotation)");
                return;
            }
            boolean async;
            String apiVersion;
            String apiGroup;
            if (apiModuleClass.isAnnotationPresent(Service.class)) {
                Service dubboService = apiModuleClass.getAnnotation(Service.class);
                async = dubboService.async();
                apiVersion = dubboService.version();
                apiGroup = dubboService.group();
            } else {
                DubboService dubboService = apiModuleClass.getAnnotation(DubboService.class);
                async = dubboService.async();
                apiVersion = dubboService.version();
                apiGroup = dubboService.group();
            }

            if (!apiPomPath.isEmpty() && !apiPomPath.equals("''")){
                try {
                    mavenAddr = parsePom(apiModuleClass,moduleAnn.apiInterface(),apiPomPath);
                } catch (IOException e) {
                    LOG.warn("parse pom error");
                }
            }
            String version = getSupplierValueIfAbsent(apiVersion, () -> ofNullable(providerConfig).map(ProviderConfig::getVersion).orElse(""));
            String group = getSupplierValueIfAbsent(apiGroup, () -> ofNullable(providerConfig).map(ProviderConfig::getGroup).orElse(""));

            apiVersion = applicationContext.getEnvironment().resolvePlaceholders(version);
            apiGroup = applicationContext.getEnvironment().resolvePlaceholders(group);

            ModuleCacheItem moduleCacheItem = new ModuleCacheItem();
            DubboApiDocsCache.addApiModule(moduleAnn.apiInterface().getCanonicalName(), moduleCacheItem);
            //module name
            moduleCacheItem.setModuleDocName(moduleAnn.value());
            //interface name containing package path
            moduleCacheItem.setModuleClassName(moduleAnn.apiInterface().getCanonicalName());
            //for notify
            serviceNameList.add(moduleAnn.apiInterface().getCanonicalName());
            //module version
            moduleCacheItem.setModuleVersion(apiVersion);
            //module group
            moduleCacheItem.setModuleGroup(apiGroup);

            Method[] apiModuleMethods = apiModuleClass.getMethods();
            // API basic information list in module cache
            List<ApiCacheItem> moduleApiList = new ArrayList<>(apiModuleMethods.length);
            moduleCacheItem.setModuleApiList(moduleApiList);
            for (Method method : apiModuleMethods) {
                if (method.isAnnotationPresent(ApiDoc.class)) {
                    processApiDocAnnotation(method, moduleApiList, moduleAnn, async, moduleCacheItem, apiVersion, apiGroup);
                }
            }

        });
        LOG.info("================= Dubbo API Docs-- doc annotations scanning and processing completed ================");

        //notify miapi to update
        if (autoUpdate) {
            new Thread(() -> serviceNameList.forEach(serviceName -> {
                Map<String, String> header = new HashMap<>();
                header.put("form_data", "true");
                Map<String, String> body = new HashMap<>();
                if (!opUser.isEmpty()) {
                    body.put("opUsername", opUser);
                }
                if (!updateMsg.isEmpty()) {
                    body.put("updateMsg", updateMsg);
                }
                body.put("env", DEFAULT_ENV);
                body.put("moduleClassName", serviceName);
                String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
                body.put("ip", host);
                Set<String> set = DubboProtocol.getDubboProtocol().getExporterMap().keySet();
                String port = "";
                if (!set.isEmpty()) {
                    String key = (String) set.toArray()[0];
                    port = key.substring(key.lastIndexOf(":") + 1);
                }
                body.put("port", port);
                try {
                    if (!host.isEmpty() && !port.isEmpty()) {
                        HttpUtils.post(notifyUrl, header, gson.toJson(body), 30000);
                    }
                } catch (Exception ignored) {
                }
            })).start();
        }
    }

    /**
     * get supplier value if @param value is blank
     *
     * @param value    value
     * @param supplier supplier lambda
     * @return return value if not blank, or return supplier value
     */
    private String getSupplierValueIfAbsent(String value, Supplier<String> supplier) {
        if (StringUtils.isBlank(value)) {
            value = supplier.get();

            if (StringUtils.isBlank(value)) {
                value = "";
            }
        }
        return value;
    }

    private void processApiDocAnnotation(Method method, List<ApiCacheItem> moduleApiList, ApiModule moduleAnn,
                                         boolean async, ModuleCacheItem moduleCacheItem, String apiVersion, String apiGroup) {
        ApiDoc dubboApi = method.getAnnotation(ApiDoc.class);

        // API basic information in API list in module
        ApiCacheItem apiListItem = new ApiCacheItem();
        moduleApiList.add(apiListItem);
        // API method name
        apiListItem.setApiName(method.getName());
        // API name
        apiListItem.setApiDocName(dubboApi.value());
        // API description
        apiListItem.setDescription(dubboApi.description());
        // API version
        apiListItem.setApiVersion(apiVersion);
        // API group
        apiListItem.setApiGroup(apiGroup);

        //接口maven依赖
        apiListItem.setMavenAddr(mavenAddr);

        // Description of API return data
//        apiListItem.setApiRespDec(dubboApi.responseClassDescription());

        // API details in cache, contain interface parameters and response information
        ApiCacheItem apiParamsAndResp = new ApiCacheItem();

        String key = String.format("%s.%s", moduleAnn.apiInterface().getCanonicalName(), method.getName());
        DubboApiDocsCache.addApiParamsAndResp(key, apiParamsAndResp);

        Class<?>[] argsClass = method.getParameterTypes();
        Type[] parametersTypes = method.getGenericParameterTypes();
//        List<ApiParamsCacheItem> paramList = new ArrayList<>(argsClass.length);
        List<LayerItem> paramLayerList = new ArrayList<>();
        LayerItem responseLayer = new LayerItem("root", method.getReturnType(), method.getGenericReturnType());
        apiParamsAndResp.setAsync(async);
        apiParamsAndResp.setApiName(method.getName());
        apiParamsAndResp.setApiDocName(dubboApi.value());
        apiParamsAndResp.setApiVersion(apiVersion);
        apiParamsAndResp.setApiGroup(apiGroup);
        apiParamsAndResp.setDescription(dubboApi.description());
        apiParamsAndResp.setMavenAddr(mavenAddr);
        apiParamsAndResp.setApiModelClass(moduleCacheItem.getModuleClassName());
        LayerItem layerItemRes = processLayer(responseLayer);
        apiParamsAndResp.setResponseLayer(layerItemRes);
        Object defaultRes = initWithDefaultValue(layerItemRes);
        apiParamsAndResp.setResponse(gson.toJson(defaultRes));
        apiParamsAndResp.setParamsLayerList(paramLayerList);

        //方法参数上的注解
        Annotation[][] methodAnno = method.getParameterAnnotations();

        for (int i = 0; i < argsClass.length; i++) {
            Class<?> argClass = argsClass[i];
            Type parameterType = parametersTypes[i];
            LayerItem paramLayer = new LayerItem("arg_"+i, argClass, parameterType);
            LayerItem layerItem = processLayer(paramLayer);
            ApiDocClassDefine apiDocClassDefine = null;
            for (Annotation annotation : methodAnno[i]){
                if (annotation instanceof ApiDocClassDefine){
                    apiDocClassDefine = (ApiDocClassDefine) annotation;
                    layerItem.setExampleValue(apiDocClassDefine.value());
                    layerItem.setRequired(apiDocClassDefine.required());
                    layerItem.setDesc(apiDocClassDefine.description());
                    layerItem.setAllowableValues(apiDocClassDefine.allowableValues());
                    layerItem.setDefaultValue(apiDocClassDefine.defaultValue());
                }
            }
            paramLayerList.add(layerItem);
        }
        Object defaultReq = initWithDefaultValue(paramLayerList);
        apiParamsAndResp.setRequest(gson.toJson(defaultReq));
    }

    private Object initWithDefaultValue(List<LayerItem> layerItems) {
        try {
            List<Object> reqList = new ArrayList<>();
            if (!layerItems.isEmpty()){
                layerItems.forEach(layerItem -> reqList.add(initWithDefaultValue0(layerItem)));
            }
            return reqList;
        } catch (Exception e) {
            LOG.warn("DubboApiDocsAnnotationScanner.initWithDefaultValue, error msg: " + e.getMessage());
            return EMPTY_OBJECT_INSTANCE;
        }
    }

    private Object initWithDefaultValue(LayerItem layerItem) {
        try {
            return initWithDefaultValue0(layerItem);
        } catch (Exception e) {
            LOG.warn("DubboApiDocsAnnotationScanner.initWithDefaultValue, error msg: " + e.getMessage());
            return EMPTY_OBJECT_INSTANCE;
        }
    }

    private Object initWithDefaultValue0(LayerItem layerItem) {
        Class<?> classType = layerItem.getItemClass();
        String defaultValue = layerItem.getDefaultValue();
        if (Integer.class.isAssignableFrom(classType) || int.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? 0 : Integer.valueOf(defaultValue);
        } else if (Byte.class.isAssignableFrom(classType) || byte.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? (byte) 0 : defaultValue;
        } else if (Long.class.isAssignableFrom(classType) || long.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? 0L : Long.valueOf(defaultValue);
        } else if (Double.class.isAssignableFrom(classType) || double.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isNumber(defaultValue)) ? 0.0D : Double.valueOf(defaultValue);
        } else if (Float.class.isAssignableFrom(classType) || float.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isNumber(defaultValue)) ? 0.0F : Float.valueOf(defaultValue);
        } else if (String.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue)
                    ? (StringUtils.isEmpty(layerItem.getExampleValue()) ? "demoString" : layerItem.getExampleValue())
                    : defaultValue;
        } else if (Character.class.isAssignableFrom(classType) || char.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? 'c' : defaultValue;
        } else if (Short.class.isAssignableFrom(classType) || short.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? (short) 0 : Short.valueOf(defaultValue);
        } else if (Boolean.class.isAssignableFrom(classType) || boolean.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? false : Boolean.valueOf(defaultValue);
        } else if (Date.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + Date.class.getName() + "】yyyy-MM-dd HH:mm:ss" : defaultValue;
        } else if (LocalDate.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + LocalDate.class.getName() + "】yyyy-MM-dd" : defaultValue;
        } else if (LocalDateTime.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + LocalDateTime.class.getName() + "】yyyy-MM-dd HH:mm:ss" : defaultValue;
        } else if (BigDecimal.class.isAssignableFrom(classType)) {
            return 0;
        } else if (BigInteger.class.isAssignableFrom(classType)) {
            return 0;
        } else if (Enum.class.isAssignableFrom(classType)) {
            Object[] enumConstants = classType.getEnumConstants();
            StringBuilder sb = new StringBuilder(ENUM_VALUES_SEPARATOR);
            try {
                Method getName = classType.getMethod(METHOD_NAME_NAME);
                for (Object obj : enumConstants) {
                    sb.append(getName.invoke(obj)).append(ENUM_VALUES_SEPARATOR);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOG.error(e.getMessage(), e);
            }
            return sb.toString();
        } else if (Map.class.isAssignableFrom(classType)) {
            Map<Object, Object> resMap = new HashMap<>();
            resMap.put(initWithDefaultValue(layerItem.getItemValue().get(0)), initWithDefaultValue(layerItem.getItemValue().get(1)));
            return resMap;
        } else if (classType.isArray() || Collection.class.isAssignableFrom(classType)) {
            List<Object> resList = new ArrayList<>();
            resList.add(initWithDefaultValue(layerItem.getItemValue().get(0)));
            return resList;
        } else {
            if (layerItem.getItemValue() == null) {
                return EMPTY_OBJECT_INSTANCE;
            }

            Map<String, Object> res = new HashMap<>();
            for (LayerItem perLayerItem : layerItem.getItemValue()) {
                res.put(perLayerItem.getItemName(), initWithDefaultValue(perLayerItem));
            }
            return res;
        }
    }

    private LayerItem processLayer(LayerItem layerItem) {
        try {
            Map<String, Type> genericParams = new HashMap<>();
            return processLayer0(layerItem, 1, genericParams, false);
        } catch (Exception e) {
            LOG.warn("[DubboApiDocsAnnotationScanner.processLayer], something wrong, message: {}", e);
            return layerItem;
        }
    }

    private LayerItem processLayer0(LayerItem layerItem, int layer, Map<String, Type> genericParams, boolean isTerminal) {
        if (layerItem == null
                || ClassTypeUtil.isBaseType(layerItem.getItemClass())
                || isTerminal) {
            return layerItem;
        }
        int nowLayer = layer + 1;
        if (nowLayer > 15) {
            LOG.warn("[DubboApiDocsAnnotationScanner.processLayer0], The depth of bean has exceeded 10 layers, the deeper layer will be ignored! " +
                    "Please modify the parameter structure or check whether there is circular reference in bean!");
            return layerItem;
        }
        List<LayerItem> layerItems = new ArrayList<>();
        layerItem.setItemValue(layerItems);
        if (layerItem.getItemType() instanceof ParameterizedTypeImpl) {
            if ((List.class.isAssignableFrom(layerItem.getItemClass()))
                    || Set.class.isAssignableFrom(layerItem.getItemClass())
                    || Queue.class.isAssignableFrom(layerItem.getItemClass())) {
                Type type = ((ParameterizedTypeImpl) layerItem.getItemType()).getActualTypeArguments()[0];
                LayerItem paramLayerItem;
                if (genericParams.keySet().contains(type.getTypeName())) {
                    paramLayerItem = initLayerItem("item", genericParams.get(type.getTypeName()));
                } else {
                    paramLayerItem = initLayerItem("item", type);
                }
                layerItems.add(paramLayerItem);
                processLayer0(paramLayerItem, nowLayer, genericParams, false);
            } else if (Map.class.isAssignableFrom(layerItem.getItemClass())) {
                Type[] types = ((ParameterizedTypeImpl) layerItem.getItemType()).getActualTypeArguments();
                LayerItem paramLayerItemKey = initLayerItem("key", types[0]);
                LayerItem paramLayerItemValue = initLayerItem("value", types[1]);
                layerItems.add(paramLayerItemKey);
                processLayer0(paramLayerItemKey, nowLayer, genericParams, false);
                layerItems.add(paramLayerItemValue);
                processLayer0(paramLayerItemValue, nowLayer, genericParams, false);
            } else {
                // 泛型的处理
                Type[] types = ((ParameterizedTypeImpl) layerItem.getItemType()).getActualTypeArguments();
                if (types.length > 0) {
                    TypeVariable[] typeParameters = layerItem.getItemClass().getTypeParameters();
                    List<String> names = Arrays.stream(typeParameters).map(TypeVariable::getName).collect(Collectors.toList());
                    for (int i = 0; i < types.length && i < names.size(); i++) {
                        genericParams.put(names.get(i), types[i]);
                    }
                }
                List<Field> allFields = ClassTypeUtil.getAllFields(null, layerItem.getItemClass());
                if (allFields.size() > 0) {
                    for (Field field : allFields) {
                        if (SKIP_FIELD_SERIALVERSIONUID.equals(field.getName()) || SKIP_FIELD_THIS$0.equals(field.getName())) {
                            continue;
                        }
                        if (field.isAnnotationPresent(ApiDocClassDefine.class) && field.getAnnotation(ApiDocClassDefine.class).ignore()) {
                            continue;
                        }
                        LayerItem paramLayerItem;
                        if (genericParams.containsKey(field.getGenericType().getTypeName())) {
                            paramLayerItem = initLayerItem(field.getName(), genericParams.get(field.getGenericType().getTypeName()));
                        } else {
                            paramLayerItem = new LayerItem(field.getName(), field.getType(), field.getGenericType());
                        }
                        layerItems.add(paramLayerItem);
                        if (field.isAnnotationPresent(ApiDocClassDefine.class)) {
                            // Handling @ApiDocClassDefine annotations on properties
                            ApiDocClassDefine apiDocClassDefine = field.getAnnotation(ApiDocClassDefine.class);
                            paramLayerItem.setExampleValue(apiDocClassDefine.value());
                            paramLayerItem.setRequired(apiDocClassDefine.required());
                            paramLayerItem.setDesc(apiDocClassDefine.description());
                            paramLayerItem.setAllowableValues(apiDocClassDefine.allowableValues());
                            paramLayerItem.setDefaultValue(apiDocClassDefine.defaultValue());
                        }
                        processLayer0(paramLayerItem, nowLayer, genericParams, false);
                    }
                } else {
                    return layerItem;
                }
            }

        } else {
            if (Map.class.isAssignableFrom(layerItem.getItemClass()) || List.class.isAssignableFrom(layerItem.getItemClass())
                    || Set.class.isAssignableFrom(layerItem.getItemClass())
                    || Queue.class.isAssignableFrom(layerItem.getItemClass())){
                return layerItem;
            }
            List<Field> allFields = ClassTypeUtil.getAllFields(null, layerItem.getItemClass());
            if (allFields.size() > 0) {
                for (Field field : allFields) {
                    if (SKIP_FIELD_SERIALVERSIONUID.equals(field.getName()) || SKIP_FIELD_THIS$0.equals(field.getName()) || field.isSynthetic()) {
                        continue;
                    }
                    if (field.isAnnotationPresent(ApiDocClassDefine.class) && field.getAnnotation(ApiDocClassDefine.class).ignore()) {
                        continue;
                    }
                    boolean isTerminal0 = false;
                    if (field.getGenericType().getTypeName().contains(layerItem.getItemClassStr()) && !field.getGenericType().getTypeName().contains("$")) {
                        isTerminal0 = true;
                        String[] pArr = field.getGenericType().getTypeName().split(layerItem.getItemClassStr());
                        if (pArr.length==2 && !pArr[1].startsWith(">")){
                            isTerminal0 = false;
                        }
                    }
                    LayerItem paramLayerItem = new LayerItem(field.getName(), field.getType(), field.getGenericType());
                    layerItems.add(paramLayerItem);
                    if (field.isAnnotationPresent(ApiDocClassDefine.class)) {
                        // Handling @ApiDocClassDefine annotations on properties
                        ApiDocClassDefine apiDocClassDefine = field.getAnnotation(ApiDocClassDefine.class);
                        paramLayerItem.setExampleValue(apiDocClassDefine.value());
                        paramLayerItem.setRequired(apiDocClassDefine.required());
                        paramLayerItem.setDesc(apiDocClassDefine.description());
                        paramLayerItem.setAllowableValues(apiDocClassDefine.allowableValues());
                        paramLayerItem.setDefaultValue(apiDocClassDefine.defaultValue());
                    }
                    processLayer0(paramLayerItem, nowLayer, genericParams, isTerminal0);
                }
            } else {
                return layerItem;
            }
        }

        return layerItem;
    }

    private LayerItem initLayerItem(String itemName, Type itemType) {
        if (itemType instanceof ParameterizedTypeImpl) {
            return new LayerItem(itemName, ((ParameterizedTypeImpl) itemType).getRawType(), itemType);
        } else if (itemType instanceof Class) {
            return new LayerItem(itemName, (Class) itemType, itemType);
        } else if (itemType.getTypeName().contains("?")){
            Class classTmp = null;
            String className = itemType.getTypeName().substring("? extends".length()+1);
            try {
                classTmp = Class.forName(className);
            } catch (ClassNotFoundException ignored) {
                return null;
            }
            return new LayerItem(itemName,classTmp,itemType);
        } else {
            return null;
        }
    }

    private <I, T> void exportDubboService(Class<I> serviceClass, T serviceImplInstance, boolean async) {
        ServiceConfig<T> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setInterface(serviceClass);
        service.setRef(serviceImplInstance);
        service.setAsync(async);
        service.export();
    }

    private String parsePom(Class targetClass,Class interfaceClass,String path) throws IOException {
        String res = "";
        // 创建SAXReader
        SAXReader reader = new SAXReader();
        Document doc;
        // judge if in ide
        if (checkRunInIDEA()){
            //本地ide启动
            //直接读取指定文件
            try {
                path = path.substring(path.indexOf("/")+1);
                doc = reader.read(path);
                res = parsePom(doc);
            } catch (DocumentException e) {
                return res;
            }
        }else {
            //打包部署到测试||线上
            InputStream inputStream = null;
            try {
                String resourceName = "/META-INF/maven/"+path;
                URL url = null;
                try {
                    url = Resources.getResource(targetClass, resourceName);
                } catch (Exception e) {
                    LOG.error("load maven addr error,resource path:"+resourceName+",err:{}",e);
                    url = Resources.getResource(interfaceClass, resourceName);
                }
                inputStream = url.openStream();
                doc = reader.read(inputStream);
                res = parsePom(doc);
            } catch (Exception e) {
                LOG.error("load maven addr error:{}",e);
                return res;
            }finally {
                if (Objects.nonNull(inputStream)){
                    inputStream.close();
                }
            }
        }
        return res;
    }

    private String parsePom(Document doc){
        StringBuilder res = new StringBuilder();
        String groupId = "";
        String artifactId = "";
        String version = "";
        try {
            if (Objects.nonNull(doc)){
                // 获取根节点list
                Element root = doc.getRootElement();
                // 获取list下的所有子节点emp
                List<Element> elements = root.elements();

                // 遍历集合取出parent的内容信息.
                Element parent = elements.stream().filter(it -> it.getName().equals("parent")).findFirst().orElse(null);
                if (parent == null) {
                    return res.toString();
                }

                List<Element> subEles = parent.elements();
                for (Element element :
                        subEles) {
                    //parent下的版本，若外层无指定，则使用该版本
                    if (element.getName().equals("version")){
                        version = element.getText();
                    }
                    //所属groupId
                    if (element.getName().equals("groupId")){
                        groupId = element.getText();
                    }
                }

                //api包的 artifactId
                Element oArtifactId = elements.stream().filter(it -> it.getName().equals("artifactId")).findFirst().orElse(null);
                if (oArtifactId == null){
                    return res.toString();
                }
                artifactId = oArtifactId.getText();

                //api包下的version
                Element oVersion = elements.stream().filter(it -> it.getName().equals("version")).findFirst().orElse(null);
                if (null != oVersion){
                    version = oVersion.getText();
                }
                res.append("<dependency>\n");
                res.append("    <groupId>").append(groupId).append("</groupId>\n");
                res.append("    <artifactId>").append(artifactId).append("</artifactId>\n");
                res.append("    <version>").append(version).append("</version>\n");
                res.append("</dependency>");
            }
        } catch (Exception e) {
            return res.toString();
        }
        return res.toString();
    }

    private static boolean checkRunInIDEA() {
        try {
            Class.forName("com.intellij.rt.execution.application.AppMainV2");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }


}
