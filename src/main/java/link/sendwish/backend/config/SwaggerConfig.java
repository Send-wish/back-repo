package link.sendwish.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan("link.sendwish.backend.controller")
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build().apiInfo(apiInfo()).useDefaultResponseMessages(false);
    }

    /**
     * API Info
     */
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Swagger Sample", "APIs Sample", "Sample Doc 0.1v", "", "Author Name",
                "This sentence will be display.", "/");
        return apiInfo;
    }


    /** Swagger UI 를 Resource Handler 에 등록 */

	 @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 registry.addResourceHandler("swagger-ui.html").addResourceLocations(
	"classpath:/META-INF/resources/");
	 registry.addResourceHandler("/webjars/**").addResourceLocations(
	 "classpath:/META-INF/resources/webjars/"); }

}