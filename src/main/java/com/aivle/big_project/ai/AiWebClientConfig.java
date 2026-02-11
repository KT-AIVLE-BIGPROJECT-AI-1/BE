package com.aivle.big_project.ai;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiWebClientConfig {

    @Bean
    public WebClient aiHttpWebClient(AiProperties props) {
        int timeoutSeconds = (int) Math.ceil(props.responseTimeoutMs() / 1000.0);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.connectTimeoutMs())
                .responseTimeout(Duration.ofMillis(props.responseTimeoutMs()))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(timeoutSeconds))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds))
                );

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(props.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(errorMappingFilter());

        return builder.build();
    }

    private static ExchangeFilterFunction errorMappingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(resp -> {
            if (resp.statusCode().is2xxSuccessful()) {
                return reactor.core.publisher.Mono.just(resp);
            }
            return resp.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> reactor.core.publisher.Mono.error(
                            new AiClientException(resp.statusCode().value(), body)
                    ));
        });
    }
}
