package cat.corredors.backoffice.users.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Sets;

import cat.corredors.backoffice.users.controller.MemberRestController;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {
	  
    @Bean
    public Docket api() {
    	ModelRef errorModel = new ModelRef("ResponseError");
    	List<ResponseMessage> responseMessages = Arrays.asList(
    			new ResponseMessageBuilder().code(401).message("Null or invalid token").responseModel(errorModel).build(),
    	        new ResponseMessageBuilder().code(500).message("Unexpected error").responseModel(errorModel).build());

        return new Docket(DocumentationType.SWAGGER_2)
        	.useDefaultResponseMessages(false)
            .protocols(Sets.newHashSet("http"))
            .select()
                .apis(RequestHandlerSelectors.basePackage(MemberRestController.class.getPackage().getName()))
                .paths(PathSelectors.any())
                .build()
                    .apiInfo(this.getMetaData())
                    .globalResponseMessage(RequestMethod.POST, responseMessages)
                    .globalResponseMessage(RequestMethod.PUT, responseMessages)
                    .globalResponseMessage(RequestMethod.GET, responseMessages)
                    .globalResponseMessage(RequestMethod.DELETE, responseMessages);
    }

    private ApiInfo getMetaData() {
        return new ApiInfoBuilder()
                .title("BACKOFFICE API")
                .description("\"corredors.cat\"")
                .version("1.0.0-SNAPSHOT")
                .license("CC0 de Creative Commons")
                .licenseUrl("https://creativecommons.org/publicdomain/zero/1.0/legalcode")
                .contact(new Contact("corredors.cat", "https://www.corredors.cat", "backoffice@backoffice.corredors.cat"))
                .build();
    }
}