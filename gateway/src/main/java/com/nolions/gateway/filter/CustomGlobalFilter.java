package com.nolions.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("CustomGlobalFilter::filter(), request, path = {}", exchange.getRequest().getPath());
        log.info("CustomGlobalFilter::filter(), request, body = {}", exchange.getRequest().getBody());
        log.info("CustomGlobalFilter::filter(), request, method = {}", exchange.getRequest().getMethod());
        log.info("CustomGlobalFilter::filter(), request, headers = {}", exchange.getRequest().getHeaders());

        ServerHttpResponse originalResp = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResp.bufferFactory();

        ServerHttpResponseDecorator decoratedResp = new ServerHttpResponseDecorator(originalResp) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    Flux<DataBuffer> logged = fluxBody.map(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String respBody = new String(bytes, StandardCharsets.UTF_8);
                        log.info("CustomGlobalFilter::filter(), response, body = {}", respBody);
                        log.info("CustomGlobalFilter::filter(), response, status = {}", getStatusCode());
                        return bufferFactory.wrap(bytes);
                    });
                    return super.writeWith(logged);
                }

                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate()
                .response(decoratedResp)
                .build());
    }

    @Override
    public int getOrder() {
        // 数字越小越先执行；可根据需要调整顺序
        return -1;
    }
}
