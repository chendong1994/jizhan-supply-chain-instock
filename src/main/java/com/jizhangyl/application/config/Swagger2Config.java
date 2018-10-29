package com.jizhangyl.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * @author 杨贤达
 * @date 2018/9/3 14:13
 * @description Swagger2 配置
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jizhangyl.application"))
//                .paths(PathSelectors.regex("/jizhangyl/.*"))
                .paths(PathSelectors.regex("/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("集栈供应链 & 飞翩物流 API")
                .description("集栈供应链 & 飞翩物流 API")
                .termsOfServiceUrl("http://127.0.0.1:8762/")
                .contact("yangxianda")
                .version("1.0.0")
                .build();
    }
}