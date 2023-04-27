package com.z7.bespoke.frame.swagger.config;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.z7.bespoke.frame.swagger.support.EnumDescriptor;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目名称：review-frame
 * 类 名 称：Knife4jConfig
 * 类 描 述：TODO Knife4j配置
 * 创建时间：2023/4/27 10:24 上午
 * 创 建 人：z7
 */
@Slf4j
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig implements WebMvcConfigurer {

    /*对于管控了权限的应用，应放行以下资源*/
    public static String[] RESOURCE_URLS = new String[]{"/webjars/**", "/swagger**", "/v2/api-docs", "/doc.html"};

    @Value("${spring.application.name:default}")
    private String applicationName;

    @Bean
    public Docket docket() {
        // 指定使用Swagger2规范
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        // 简介(支持Markdown语法)
                        .description("# API简介")
                        // 服务地址
                        .termsOfServiceUrl("http://local.idea-aedi.com/")
                        // 作者及联系信息
                        .contact(new Contact("Z-SEVEN", "https://github.com/z7-x/review-frame", "790534238@qq.com"))
                        // api版本
                        .version("1.0.0")
                        .build())
                //分组名称(微服务项目可以用微服务名分组)
                .groupName(applicationName)
                .select()
                // 定位api
                .apis(
                        RequestHandlerSelectors.basePackage(getProjectBasePackage())
                                .and(RequestHandlerSelectors.withClassAnnotation(RestController.class)
                                        .or(RequestHandlerSelectors.withClassAnnotation(Controller.class))
                                )
                )
                .paths(PathSelectors.any())
                .build();

        docket.securitySchemes(securitySchemes()).securityContexts(securityContexts());
        return docket;
    }

    private List<SecurityScheme> securitySchemes() {
        // 设置请求头信息
        List<SecurityScheme> result = new ArrayList<>();
        // 第一个参数name，自定义即可。 在配置securityContexts时，通过此name对应到apiKey即可
        // 第二个参数，header name自定义即可。 如：JWT_TOKEN_KEY=Auth-Token，然后在代码里request.getHeader(JWT_TOKEN_KEY)取值
        String JWT_TOKEN_KEY="Auth-Token";
        ApiKey apiKey = new ApiKey("ApiKey", JWT_TOKEN_KEY, "header");
        result.add(apiKey);
        return result;
    }

    private List<SecurityContext> securityContexts() {
        // 设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        List<SecurityReference> securityReferences = defaultAuth();
        result.add(
                SecurityContext.builder().securityReferences(securityReferences).forPaths(
                        // 当直接使用swagger文档发送请求时，这些api需要满足securityReferences认证要求
                        PathSelectors.regex("/.*")
                                .and(
                                        // 当直接使用swagger文档发送请求时，这些api不需要满足securityReferences认证要求. '.*'表示匹配所有
                                        PathSelectors.regex("/hello.*").or(PathSelectors.regex("/hi.*"))
                                                .negate()
                                )
                ).build()
        );
        return result;
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        // 这里指定使用哪个apiKey进行认证鉴权. 这里指定使用上面名为ApiKey的apiKey
        result.add(new SecurityReference("ApiKey", authorizationScopes));
        return result;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 获取项目包前缀
     */
    private String getProjectBasePackage() {
        String projectBasePackage;
        String currPackageName = this.getClass().getPackage().getName();
        String[] packageItemArr = currPackageName.split("\\.");
        if (packageItemArr.length > 3) {
            projectBasePackage = String.join(".", packageItemArr[0], packageItemArr[1], packageItemArr[2]);
        } else {
            projectBasePackage = currPackageName;
        }
        log.info("Base package to scan api is -> {}", projectBasePackage);
        return projectBasePackage;
    }

    /**
     * 显示自定义枚举类型注释
     * <p>
     * <br/> 参考<a
     * href="https://blog.gelu.me/2021/Knife4j-Swagger%E8%87%AA%E5%AE%9A%E4%B9%89%E6%9E%9A%E4%B8%BE%E7%B1%BB%E5%9E%8B
     * /">here</a>
     */
    @Component
    @SuppressWarnings("unchecked")
    public static class Knife4jSwaggerEnumPlugin implements ModelPropertyBuilderPlugin, ParameterBuilderPlugin {

        private static final Field parameterDescriptionField;

        private static final Field modelPropertyBuilderDescriptionField;


        static {
            // swagger3用: parameterDescriptionField = ReflectionUtils.findField(RequestParameterBuilder.class, "description");
            parameterDescriptionField = ReflectionUtils.findField(ParameterBuilder.class, "description");
            Objects.requireNonNull(parameterDescriptionField, "parameterDescriptionField should noe be null.");
            ReflectionUtils.makeAccessible(parameterDescriptionField);

            // swagger3用: modelPropertyBuilderDescriptionField = ReflectionUtils.findField(PropertySpecificationBuilder.class, "description");
            modelPropertyBuilderDescriptionField = ReflectionUtils.findField(ModelPropertyBuilder.class, "description");
            Objects.requireNonNull(modelPropertyBuilderDescriptionField, "ModelPropertyBuilder_descriptionField should noe be null.");
            ReflectionUtils.makeAccessible(modelPropertyBuilderDescriptionField);
        }

        /**
         * {@link ApiModelProperty}相关
         * <p>
         * 主要处理枚举对象直接作为方法参数的内部字段的情况. 如：
         * <pre>
         *  &nbsp;  @Data
         *  &nbsp;  public class LoginTokenRespVO {
         *  &nbsp;
         *  &nbsp;      @ApiModelProperty("用户类型")
         *  &nbsp;      private UserTypeEnum userType;
         *  &nbsp;  }
         * </pre>
         */
        @Override
        public void apply(ModelPropertyContext context) {
            Optional<BeanPropertyDefinition> optional = context.getBeanPropertyDefinition();
            if (!optional.isPresent()) {
                return;
            }
            // 对应被@ApiModelProperty标注的字段
            BeanPropertyDefinition beanPropertyDefinition = optional.get();
            Class<?> fieldType = beanPropertyDefinition.getField().getRawType();
            if (!Enum.class.isAssignableFrom(fieldType)) {
                return;
            }
            Class<Enum<?>> enumType = (Class<Enum<?>>) fieldType;
            Enum<?>[] enumConstants = enumType.getEnumConstants();
            // swagger3用: PropertySpecificationBuilder modelPropertyBuilder = context.getSpecificationBuilder();
            ModelPropertyBuilder modelPropertyBuilder = context.getBuilder();
            Object oldValue = ReflectionUtils.getField(modelPropertyBuilderDescriptionField, modelPropertyBuilder);
            // 解析枚举
            List<String> enumDescList =
                    Arrays.stream(enumConstants).map(this::obtainEnumDescription).collect(Collectors.toList());
            /*
             * swagger3用:
             *   modelPropertyBuilder.description((oldValue == null ? BaseConstant.EMPTY : oldValue) + buildHtmlUnOrderList(enumDescList))
             *                       .type(new ModelSpecificationBuilder().scalarModel(ScalarType.UUID).build());
             */
            modelPropertyBuilder.description((oldValue == null ? "" : oldValue) + buildHtmlUnOrderList(enumDescList))
                    .type(context.getResolver().resolve(Enum.class));
        }

        /**
         * {@link ApiParam}、{@link io.swagger.v3.oas.annotations.Parameter}相关.
         * <p> 主要处理：枚举对象直接作为方法参数的情况. 如：
         * <pre>
         *  &nbsp;  @PostMapping("/test1")
         *  &nbsp;  @ApiOperation(value = "测试1")
         *  &nbsp;  public void test1(@ApiParam(value = "用户类型", required = true) UserTypeEnum userTypeEnum)
         * </pre>
         */
        @Override
        public void apply(ParameterContext context) {
            Class<?> type = context.resolvedMethodParameter().getParameterType().getErasedType();
            // swagger3用: RequestParameterBuilder parameterBuilder = context.requestParameterBuilder();
            ParameterBuilder parameterBuilder = context.parameterBuilder();
            if (!Enum.class.isAssignableFrom(type)) {
                return;
            }
            Class<Enum<?>> enumType = (Class<Enum<?>>) type;
            Enum<?>[] enumConstants = enumType.getEnumConstants();
            // 解析枚举
            List<String> enumDescList = Arrays.stream(enumConstants).map(this::obtainEnumDescription).collect(Collectors.toList());
            Object oldValue = ReflectionUtils.getField(parameterDescriptionField, parameterBuilder);
            parameterBuilder.description((oldValue == null ? "" : oldValue) + buildHtmlUnOrderList(enumDescList));
        }

        /**
         * 此插件是否支持处理该DocumentationType
         */
        @Override
        public boolean supports(DocumentationType documentationType) {
            return true;
        }

        /**
         * 获取枚举描述
         *
         * @param enumObj 枚举对象
         *
         * @return 枚举描述
         */
        private String obtainEnumDescription(@NonNull Enum<?> enumObj) {
            String name = enumObj.name();
            /*
             * 枚举说明器示例:
             *
             * public interface EnumDescriptor {
             *     // 获取枚举项说明
             *     String obtainDescription();
             * }
             */
            if (enumObj instanceof EnumDescriptor) {
                return name + "：" + ((EnumDescriptor) enumObj).obtainDescription();
            }
            return name;
        }

        /**
         * 构建无序列表html
         *
         * @param itemList 列表元素
         *
         * @return 无序列表html
         */
        private String buildHtmlUnOrderList(@Nullable List<String> itemList) {
            if (CollectionUtils.isEmpty(itemList)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<ul>");
            for (String item : itemList) {
                sb.append("<li>");
                sb.append(item);
                sb.append("</li>");
            }
            sb.append("</ul>");
            return sb.toString();
        }
    }
}
