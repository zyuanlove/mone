/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Data
public class ApiInfoUpdateParam {

    private Long id;

    private String name;

    private String description;

    private String url;

    private String httpMethod;

    private String path;

    private Integer routeType; // 0 - http; 1 - dubbo

    private Integer groupId;

    private String application;

    private String serviceName;

    private String methodName;

    private String serviceGroup;

    private String serviceVersion;

    private Integer status;

    private String paramTemplate;

    private String contentType;

    private boolean allowCache;

    private boolean allowLog;

    private boolean allowMock;

    private boolean allowScript;

    private boolean allowCors;

    private boolean allowToken;

    private boolean offline;

    private boolean allowAuth;

    private boolean allowIpAntiBrush;

    private boolean allowUidAntiBrush;

    private boolean allowPreview;

    /**
     * 接口是否支持dubbo传header
     */
    private boolean allowHeaders;

    private String creator;

    private String entryClassName;

    /**
     * 租户
     */
    private String tenement;

    /**
     * 是否使用Qps限制
     */
    private boolean useQpsLimit;

    @Setter
    @Getter
    private int ipAntiBrushLimit;

    @Setter
    @Getter
    private int uidAntiBrushLimit;

    /**
     * 缓存过期时间(毫秒)
     */
    @Setter
    @Getter
    private int cacheExpire;

    private String mockData;

    /**
     * mock数据描述
     */
    private String mockDataDesc;

    private String script;

    private String scriptParams;

    private String scriptMethodName;

    private int scriptType;

    /**
     * 并发调用限制，默认50
     */
    private int invokeLimit;


    /**
     * QPS限制值
     */
    private int qpsLimit;



    /**
     * 调用超时设置
     */
    private int timeout;

    private String token;

    /**
     * API 编排配置
     */
    private String groupConfig;

    private String gitProjectId;

    private String gitToken;

    private String gitPath;

    private String gitBranch;

    private String commit;

    private String dsIds;


    private String gitName;


    private String gitGroup;


    private String commitId;


    /**
     * script 脚本的jar 名称
     */
    private String jarName;

    /**
     * filter
     */
    private String filterParams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getParamTemplate() {
        return paramTemplate;
    }

    public void setParamTemplate(String paramTemplate) {
        this.paramTemplate = paramTemplate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isAllowCache() {
        return allowCache;
    }

    public void setAllowCache(boolean allowCache) {
        this.allowCache = allowCache;
    }

    public boolean isAllowLog() {
        return allowLog;
    }

    public void setAllowLog(boolean allowLog) {
        this.allowLog = allowLog;
    }

    public boolean isAllowMock() {
        return allowMock;
    }

    public void setAllowMock(boolean allowMock) {
        this.allowMock = allowMock;
    }

    public boolean isAllowScript() {
        return allowScript;
    }

    public void setAllowScript(boolean allowScript) {
        this.allowScript = allowScript;
    }

    public boolean isAllowCors() {
        return allowCors;
    }

    public void setAllowCors(boolean allowCors) {
        this.allowCors = allowCors;
    }

    public boolean isAllowToken() {
        return allowToken;
    }

    public void setAllowToken(boolean allowToken) {
        this.allowToken = allowToken;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isAllowAuth() {
        return allowAuth;
    }

    public void setAllowAuth(boolean allowAuth) {
        this.allowAuth = allowAuth;
    }

    public boolean isUseQpsLimit() {
        return useQpsLimit;
    }

    public void setUseQpsLimit(boolean useQpsLimit) {
        this.useQpsLimit = useQpsLimit;
    }

    public boolean isAllowPreview() {
        return allowPreview;
    }

    public boolean isAllowHeaders() {
        return allowHeaders;
    }

    public void setAllowHeaders(boolean allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public void setAllowPreview(boolean allowPreview) {
        this.allowPreview = allowPreview;
    }

    public int getIpAntiBrushLimit() {
        return ipAntiBrushLimit;
    }

    public void setIpAntiBrushLimit(int ipAntiBrushLimit) {
        this.ipAntiBrushLimit = ipAntiBrushLimit;
    }

    public int getUidAntiBrushLimit() {
        return uidAntiBrushLimit;
    }

    public void setUidAntiBrushLimit(int uidAntiBrushLimit) {
        this.uidAntiBrushLimit = uidAntiBrushLimit;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }

    public void setCacheExpire(int cacheExpire) {
        this.cacheExpire = cacheExpire;
    }

    public int getQpsLimit() {
        return qpsLimit;
    }

    public void setQpsLimit(int qpsLimit) {
        this.qpsLimit = qpsLimit;
    }

    public String getMockData() {
        return mockData;
    }

    public void setMockData(String mockData) {
        this.mockData = mockData;
    }

    public String getMockDataDesc() {
        return mockDataDesc;
    }

    public void setMockDataDesc(String mockDataDesc) {
        this.mockDataDesc = mockDataDesc;
    }

    public String getDsIds() {
        return dsIds;
    }

    public void setDsIds(String dsIds) {
        this.dsIds = dsIds;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScriptMethodName() {
        return scriptMethodName;
    }

    public void setScriptMethodName(String scriptMethodName) {
        this.scriptMethodName = scriptMethodName;
    }

    public String getScriptParams() {
        return scriptParams;
    }

    public void setScriptParams(String scriptParams) {
        this.scriptParams = scriptParams;
    }

    public int getScriptType() {
        return scriptType;
    }

    public void setScriptType(int scriptType) {
        this.scriptType = scriptType;
    }

    public int getInvokeLimit() {
        return invokeLimit;
    }

    public void setInvokeLimit(int invokeLimit) {
        this.invokeLimit = invokeLimit;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGroupConfig() {
        return groupConfig;
    }

    public void setGroupConfig(String groupConfig) {
        this.groupConfig = groupConfig;
    }

    public String getGitProjectId() {
        return gitProjectId;
    }

    public void setGitProjectId(String gitProjectId) {
        this.gitProjectId = gitProjectId;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public boolean isAllowIpAntiBrush() {
        return allowIpAntiBrush;
    }

    public void setAllowIpAntiBrush(boolean allowIpAntiBrush) {
        this.allowIpAntiBrush = allowIpAntiBrush;
    }

    public boolean isAllowUidAntiBrush() {
        return allowUidAntiBrush;
    }

    public void setAllowUidAntiBrush(boolean allowUidAntiBrush) {
        this.allowUidAntiBrush = allowUidAntiBrush;
    }

    public String getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(String filterParams) {
        this.filterParams = filterParams;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "ApiInfoUpdateParam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", path='" + path + '\'' +
                ", routeType=" + routeType +
                ", groupId=" + groupId +
                ", application='" + application + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", serviceGroup='" + serviceGroup + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", status=" + status +
                ", paramTemplate='" + paramTemplate + '\'' +
                ", contentType='" + contentType + '\'' +
                ", allowCache=" + allowCache +
                ", allowLog=" + allowLog +
                ", allowMock=" + allowMock +
                ", allowScript=" + allowScript +
                ", allowCors=" + allowCors +
                ", allowToken=" + allowToken +
                ", alloHeaders=" + allowHeaders +
                ", offline=" + offline +
                ", allowAuth=" + allowAuth +
                ", allowIpAntiBrush=" + allowIpAntiBrush +
                ", allowUidAntiBrush=" + allowUidAntiBrush +
                ", allowPreview=" + allowPreview +
                ", useQpsLimit=" + useQpsLimit +
                ", ipAntiBrushLimit=" + ipAntiBrushLimit +
                ", uidAntiBrushLimit=" + uidAntiBrushLimit +
                ", cacheExpire=" + cacheExpire +
                ", mockData='" + mockData + '\'' +
                ", mockDataDesc='" + mockDataDesc + '\'' +
                ", script='" + script + '\'' +
                ", scriptParams='" + scriptParams + '\'' +
                ", scriptMethodName='" + scriptMethodName + '\'' +
                ", scriptType=" + scriptType +
                ", invokeLimit=" + invokeLimit +
                ", qpsLimit=" + qpsLimit +
                ", timeout=" + timeout +
                ", token='" + token + '\'' +
                ", groupConfig='" + groupConfig + '\'' +
                ", gitProjectId='" + gitProjectId + '\'' +
                ", gitToken='" + gitToken + '\'' +
                ", gitPath='" + gitPath + '\'' +
                ", gitBranch='" + gitBranch + '\'' +
                ", commit='" + commit + '\'' +
                ", dsIds='" + dsIds + '\'' +
                ", filterParams='" + filterParams + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiInfoUpdateParam that = (ApiInfoUpdateParam) o;
        return allowCache == that.allowCache &&
                allowLog == that.allowLog &&
                allowMock == that.allowMock &&
                allowScript == that.allowScript &&
                allowCors == that.allowCors &&
                allowToken == that.allowToken &&
                offline == that.offline &&
                allowAuth == that.allowAuth &&
                allowIpAntiBrush == that.allowIpAntiBrush &&
                allowUidAntiBrush == that.allowUidAntiBrush &&
                allowPreview == that.allowPreview &&
                allowHeaders == that.allowHeaders &&
                useQpsLimit == that.useQpsLimit &&
                ipAntiBrushLimit == that.ipAntiBrushLimit &&
                uidAntiBrushLimit == that.uidAntiBrushLimit &&
                cacheExpire == that.cacheExpire &&
                scriptType == that.scriptType &&
                invokeLimit == that.invokeLimit &&
                qpsLimit == that.qpsLimit &&
                timeout == that.timeout &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(url, that.url) &&
                Objects.equals(httpMethod, that.httpMethod) &&
                Objects.equals(path, that.path) &&
                Objects.equals(routeType, that.routeType) &&
                Objects.equals(groupId, that.groupId) &&
                Objects.equals(application, that.application) &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(methodName, that.methodName) &&
                Objects.equals(serviceGroup, that.serviceGroup) &&
                Objects.equals(serviceVersion, that.serviceVersion) &&
                Objects.equals(status, that.status) &&
                Objects.equals(paramTemplate, that.paramTemplate) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(mockData, that.mockData) &&
                Objects.equals(mockDataDesc, that.mockDataDesc) &&
                Objects.equals(script, that.script) &&
                Objects.equals(scriptParams, that.scriptParams) &&
                Objects.equals(scriptMethodName, that.scriptMethodName) &&
                Objects.equals(token, that.token) &&
                Objects.equals(groupConfig, that.groupConfig) &&
                Objects.equals(gitProjectId, that.gitProjectId) &&
                Objects.equals(gitToken, that.gitToken) &&
                Objects.equals(gitPath, that.gitPath) &&
                Objects.equals(gitBranch, that.gitBranch) &&
                Objects.equals(commit, that.commit) &&
                Objects.equals(dsIds, that.dsIds) &&
                Objects.equals(filterParams, that.filterParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url, httpMethod, path, routeType, groupId, application, serviceName, methodName, serviceGroup, serviceVersion, status, paramTemplate, contentType, allowCache, allowLog, allowMock, allowScript, allowCors, allowToken, offline, allowAuth, allowIpAntiBrush, allowUidAntiBrush, allowPreview, useQpsLimit, ipAntiBrushLimit, uidAntiBrushLimit, cacheExpire, mockData, mockDataDesc, script, scriptParams, scriptMethodName, scriptType, invokeLimit, qpsLimit, timeout, token, groupConfig, gitProjectId, gitToken, gitPath, gitBranch, commit, dsIds, filterParams);
    }
}
