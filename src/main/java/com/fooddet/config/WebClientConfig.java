// com.fooddet.config.WebClientConfig
package com.fooddet.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(FastapiProps.class)
@RequiredArgsConstructor
public class WebClientConfig {

    private final FastapiProps props;

    @Bean
    public WebClient fastapiClient(WebClient.Builder builder) {
        HttpClient http = HttpClient.create()
                .responseTimeout(Duration.ofMillis(props.getReadTimeoutMs()))
                // 연결 타임아웃은 아래 방식으로 충분 (Netty ChannelOption 직접 참조 안 함)
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectTimeoutMs());

        return builder
                .baseUrl(props.getBaseUrl())
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(http))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build())
                .build();
    }
}


