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

package com.xiaomi.miapi.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.feishu.FeiShu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class FeiShuService {

    private FeiShu feiShu;

    @NacosValue("${feishu.appId}")
    private String appId = "";

    @NacosValue("${feishu.appSecret}")
    private String appSecret = "";

    @PostConstruct
    private void init() {
        feiShu = new FeiShu(appId, appSecret);
    }

    public void sendMsg2Person(String username, String msg) {
        if (StringUtils.isEmpty(username)) {
            log.error("username is null");
            return;
        }
        try {
            log.info("FeiShuService#sendMsg personal msg: {}", msg);
            feiShu.sendMsgByEmail(username + "@xiaomi.com", msg);
        } catch (Exception e) {
            log.error("FeiShuService#send personal msg Throwable" + e.getMessage(), e);
        }
    }

    public void sendCard2Person(String username, String card) {
        if (StringUtils.isEmpty(username)) {
            log.error("username is null");
            return;
        }
        try {
            log.info("FeiShuService#sendCard2Person personal msg: {}", card);
            feiShu.sendCardByEmail(username + "@xiaomi.com", card);
        } catch (Exception e) {
            log.error("FeiShuService#send personal msg Throwable" + e.getMessage(), e);
        }
    }
}
