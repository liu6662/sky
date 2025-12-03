package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenApiConfig {

    @Bean
    public OpenAPI skyTakeOutOpenAPI() {
        log.info("开始生成接口文档...");
        return new OpenAPI()
                .info(new Info()
                        .title("外卖平台 API 文档") // 文档标题
                        .description("sky-take-out 项目的接口文档，支持在线调试") // 描述
                        .version("1.0.0") // 接口版本
                );
    }
}