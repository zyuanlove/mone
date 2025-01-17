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

package com.xiaomi.youpin.gateway.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosNaming;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "tesla_gateway", autoRefreshed = true)
public class NacosConfiguration {


    @Value("${dubbo.registry.address}")
    private String nacosAddress;


    @Bean
    public NacosNaming nacosNaming() {
        NacosNaming nacosNaming = new NacosNaming();
        String address = nacosAddress.split("//")[1];
        nacosNaming.setServerAddr(address);
        nacosNaming.init();
        return nacosNaming;
    }

    @Bean
    public NacosNamingService nacosNamingService() {
        String address = nacosAddress.split("//")[1];
        return new NacosNamingService(address);
    }

}
