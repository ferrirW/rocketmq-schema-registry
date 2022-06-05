/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
//
//    /**
//     * Configure Spring Fox.
//     *
//     * @param config The configuration
//     * @return The spring fox docket.
//     */
//    @Bean
//    public Docket api(final Config config) {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(
//                        /**
//                         * public ApiInfo(
//                         String title,
//                         String description,
//                         String version,
//                         String termsOfServiceUrl,
//                         Contact contact,
//                         String license,
//                         String licenseUrl,
//                         Collection<VendorExtension> vendorExtensions)
//                         */
//                        new ApiInfo(
//                                "Metacat API",
//                                "The set of APIs available in this version of metacat",
//                                "1.1.0", // TODO: Swap out with dynamic from config
//                                null,
//                                new Contact("Netflix, Inc.", "https://jobs.netflix.com/", null),
//                                "Apache 2.0",
//                                "http://www.apache.org/licenses/LICENSE-2.0",
//                                Lists.newArrayList()
//                        )
//                )
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.netflix.metacat.main.api"))
//                .paths(PathSelectors.any())
//                .build()
//                .pathMapping("/")
//                .useDefaultResponseMessages(false);
//    }


    @Bean
    public Docket docket(Environment environment) {
        //设置要显示swagger的环境，只有dev、test这两个环境才会显示swagger的api文档
        Profiles profiles = Profiles.of("dev","test");
        boolean isEnable = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(isEnable) //是否启动swagger，如果是false，则swagger不能在浏览器中访问
                .select()
                //RequestHandlerSelectors.basePackage("com.ligang.swagger.controller") 指定需要扫描的包（一般使用这种方式）
                //RequestHandlerSelectors.any() 扫描全部
                //RequestHandlerSelectors.none() 不扫描
                //RequestHandlerSelectors.withClassAnnotation()
                //RequestHandlerSelectors.withMethodAnnotation()
                .apis(RequestHandlerSelectors.basePackage("org.apache.rocketmq.schema.registry.core"))
                //PathSelectors.ant("/swagger/**") 过滤某些路径（/swagger/**这些路径的接口不扫描）
                .build();
    }


    private ApiInfo apiInfo(){

        //作者信息
        Contact contact = new Contact(
                "王帆",
                "wangfan8@xiaomi.com",
                "wangfan8@xiaomi.com");

        return new ApiInfo(
                "RocketMQ Schema Registry",
                "RocketMQ Schema Registry Swagger API Document",
                "V1.0",
                "wangfan8@xiaomi.com",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }
}
