package com.szu.afternoon5.softwareengineeringbackend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class WechatClientConfig {

    @Bean
    @Qualifier("wechatWebClient")
    public WebClient wechatWebClient() {
        JsonMapper jsonMapper = JsonMapper.builder()
                // 如果你有特殊配置，可以在这里加（模块、时间格式等等）
                .build();

        JacksonJsonDecoder decoder =
                new JacksonJsonDecoder(jsonMapper,
                        MediaType.APPLICATION_JSON,
                        MediaType.TEXT_PLAIN);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(cfg -> cfg.defaultCodecs().jacksonJsonDecoder(decoder))
                .build();

        return WebClient.builder()
                .baseUrl("https://api.weixin.qq.com/")
                .exchangeStrategies(strategies)
                .build();
    }
}
