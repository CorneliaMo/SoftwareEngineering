package com.szu.afternoon5.softwareengineeringbackend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

/**
 * 微信接口客户端配置，提供自定义反序列化能力的 {@link WebClient}。
 * <p>
 * 如需接入代理、限流或监控，可在构建 {@code WebClient} 时追加过滤器或自定义 ExchangeStrategies。
 */
@Configuration
public class WechatClientConfig {

    /**
     * 构建用于访问微信开放接口的 WebClient，定制 JSON 解码以兼容文本返回值。
     * 若后续增加重试、超时策略，可在此处统一封装。
     */
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
