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

package com.xiaomi.youpin.gateway.nacos;

public interface Nacos {

    String getConfig(NacosConfig nacosConfig) throws Exception;

    /**
     * 获取nacos配置属性
     *
     * p.s. 当前只支持properties和text类型
     * @param nacosConfig
     * @param configType
     * @param key
     * @return
     */
    String getConfigValue(NacosConfig nacosConfig, ConfigType configType, String key);

}
