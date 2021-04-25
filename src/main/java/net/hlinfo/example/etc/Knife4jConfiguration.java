package net.hlinfo.example.etc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;

import cn.hutool.core.collection.CollectionUtil;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


@Configuration
@EnableSwagger2WebMvc
@EnableKnife4j
//@Import(BeanValidatorPluginsConfiguration.class)
@Profile({"dev", "test"})
public class Knife4jConfiguration {
	
	 @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                  //分组名称
                .groupName("demoapi")
                .select()
                   //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("net.hlinfo.example.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(getGlobalRequestParameters());
        return docket;
    }
	 private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Spring Boot示例工程API文档")
                .description("Spring Boot示例工程 RESTful APIs")
                .termsOfServiceUrl("http://hlinfo.net/")
                .contact(new Contact("呐喊","http://hlinfo.net/","service@hlinfo.net"))
                .version("1.0")
                .build();
    }
	private ApiKey apiKey() {
        return new ApiKey("BearerToken", "Authorization", "header");
    }
    private ApiKey apiKey1() {
        return new ApiKey("BearerToken1", "Authorization-x", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }
    private SecurityContext securityContext1() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth1())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return CollectionUtil.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
    }
    List<SecurityReference> defaultAuth1() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return CollectionUtil.newArrayList(new SecurityReference("BearerToken1", authorizationScopes));
    }
    
	//生成全局通用参数
	private List<Parameter> getGlobalRequestParameters() {
	    List<Parameter> parameters = new ArrayList<>();
	    parameters.add(new ParameterBuilder()
	            .name("token")
	            .description("接口校验参数")
	            .modelRef(new ModelRef("String"))
	            .required(false)
	            .parameterType("header")
	            .required(false)
	            .build());
	     return parameters;
    }
	
	//生成全局通用参数 v3.x
	/*private List<RequestParameter> getGlobalRequestParameters() {
	    List<RequestParameter> parameters = new ArrayList<>();
	    parameters.add(new RequestParameterBuilder()
	            .name("token")
	            .description("接口校验参数")
	            .required(false)
	            .in(ParameterType.HEADER)
	            .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
	            .required(false)
	            .build());
	     return parameters;
    }*/
}
