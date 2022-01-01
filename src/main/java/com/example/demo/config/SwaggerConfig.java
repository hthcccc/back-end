package com.example.demo.config;


import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration  //告诉Spring容器，这个类是一个配置类
@EnableSwagger2  //启用Swagger2
@EnableSwaggerBootstrapUI
//@EnableWebMvc  //必须用这个，不然会有空指针报错问题
public class SwaggerConfig /*implements WebMvcConfigurer*/ {

//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("test")
//                .apiInfo(apiInfo("接口管理文档", ApiGroupInfo.VERSION))
//                .select()
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .paths(PathSelectors.any())
//                .build();
//    }

    /* 分组(使用注解进行分组): (版本1.0.1)*/
    @Bean
    public Docket userAPI(){
        return createBean(ApiGroupInfo.User,"userApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket goodAPI(){
        return createBean(ApiGroupInfo.Good,"goodApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket commentAPI(){
        return createBean(ApiGroupInfo.Comment,"commentApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket orderAPI(){
        return createBean(ApiGroupInfo.Order,"orderApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket refundAPI(){
        return createBean(ApiGroupInfo.Refund,"refundApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket subscribeAPI(){
        return createBean(ApiGroupInfo.Subscribe,"subscribeApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket historyAPI(){
        return createBean(ApiGroupInfo.History,"historyApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket favorityAPI(){
        return createBean(ApiGroupInfo.Favority,"favorityApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket noticeAPI(){
        return createBean(ApiGroupInfo.Notice,"noticeApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket adminAPI(){
        return createBean(ApiGroupInfo.Admin,"adminApi", ApiGroupInfo.VERSION);
    }

    @Bean
    public Docket recommendAPI(){
        return createBean(ApiGroupInfo.Recommend,"recommendApi", ApiGroupInfo.VERSION);
    }

    // 通过注解进行分组
    private Docket createBean(String groupName,String desc,String version){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(groupName)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .select()
                .apis(new Predicate<RequestHandler>() {
                    @Override
                    public boolean apply(RequestHandler input) {
                        Optional<ApiGroup> controllerAnnotation = input.findAnnotation(ApiGroup.class);
                        Set<ApiGroup> apiGroups = controllerAnnotation.asSet();
                        if(!CollectionUtils.isEmpty(apiGroups)){
                            Iterator<ApiGroup> iterator = apiGroups.iterator();
                            while (iterator.hasNext()) {
                                ApiGroup next = iterator.next();
                                if (Arrays.asList(next.group()).contains(groupName)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                })
                .paths(PathSelectors.any())//过滤的接口
                .build()
                .apiInfo(apiInfo(desc, version));
    }

    private ApiInfo apiInfo(String desc,String version) {
        return new ApiInfoBuilder()
                .title("二手交易平台") // 项目名称
                .description(desc) // 简介
                .version(version) // 版本
                .contact(new Contact("hth","116.62.208.68","2576556093@qq.com"))
                .build();
    }





//    @Bean
//    public Docket createUserApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("user")
//                .apiInfo(apiUserInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.example.demo.controller"))// com包下所有API都交给Swagger2管理
//                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
//                .paths(Predicates.not(PathSelectors.regex("/error.*")))
//                .build();
//    }
//
//    /**
//     * 此处主要是API文档页面显示信息
//     */
//    private ApiInfo apiUserInfo() {
//        return new ApiInfoBuilder()
//                .title("userAPI") // 标题
//                .description("用户") // 描述
//                .termsOfServiceUrl("https://www.tongji.edu.cn") // 服务网址，一般写公司地址
//                .version("1.0") // 版本
//                .build();
//    }

//    @Override   //重载拦截器
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }



}
