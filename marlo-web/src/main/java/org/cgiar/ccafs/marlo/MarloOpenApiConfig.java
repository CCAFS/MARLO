package org.cgiar.ccafs.marlo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@PropertySource("classpath:clarisa.properties")
@Profile("!" + ApplicationContextConfig.SPRING_PROFILE_PRODUCTION)
@Configuration
public class MarloOpenApiConfig {

  @Bean
  public OpenAPI marloOpenAPI() {

    final String securitySchemeName = "basicAuth";

    return new OpenAPI()
        .info(new Info()
            .title("MARLO API")
            .description("Managing Agricultural Research for Learning & Outcomes Platform")
            .version("v1"))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .schemaRequirement(securitySchemeName,
            new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic"));
  }
}
