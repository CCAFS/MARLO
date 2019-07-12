/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@PropertySource("classpath:clarisa.properties")
// Disable for production environment.
@Profile("!" + ApplicationContextConfig.SPRING_PROFILE_PRODUCTION)
@Import({BeanValidatorPluginsConfiguration.class})
@Configuration
@EnableSwagger2
public class MarloSwaggerConfiguration extends WebMvcConfigurerAdapter {

  @Autowired
  private Environment env;

  // Config required for Swagger UI if not using Spring Boot.
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("**").addResourceLocations("/WEB-INF/swagger/dist/");

    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Bean
  public Docket api() {

    return new Docket(DocumentationType.SWAGGER_2).securitySchemes(Collections.singletonList(this.securityScheme()))
      .select().apis(RequestHandlerSelectors.basePackage("org.cgiar.ccafs.marlo.rest")).paths(PathSelectors.any())
      .build().apiInfo(this.apiInfo())
      .tags(new Tag(this.env.getProperty("allcontrol.tag"), this.env.getProperty("allcontrol.description"),
        Integer.parseInt(this.env.getProperty("allcontrol.order"))), this.getTags())
      .genericModelSubstitutes(Optional.class);

  }

  private ApiInfo apiInfo() {

    ApiInfo apiInfo = new ApiInfo(null, null, null, null, null, null, null, Collections.emptyList());

    return apiInfo;
  }

  private Tag[] getTags() {
    Tag[] tags = new Tag[17];

    // 13 tables of AR2017 template

    for (int i = 0; i <= 12; i++) {
      Tag tag = new Tag(this.env.getProperty("table" + (i + 1) + ".tag"),
        this.env.getProperty("table" + (i + 1) + ".description"),
        Integer.parseInt(this.env.getProperty("table" + (i + 1) + ".order")));
      tags[i] = tag;
    }

    tags[13] = new Tag(this.env.getProperty("institution.tag"), this.env.getProperty("institution.description"),
      Integer.parseInt(this.env.getProperty("institution.order")));

    tags[14] = new Tag(this.env.getProperty("generalcontrol.tag"), this.env.getProperty("generalcontrol.description"),
      Integer.parseInt(this.env.getProperty("generalcontrol.order")));

    tags[15] = new Tag(this.env.getProperty("impactpathwaycontrol.tag"),
      this.env.getProperty("impactpathwaycontrol.description"),
      Integer.parseInt(this.env.getProperty("impactpathwaycontrol.order")));

    tags[16] = new Tag(this.env.getProperty("srfcontrol.tag"), this.env.getProperty("srfcontrol.description"),
      Integer.parseInt(this.env.getProperty("srfcontrol.order")));

    return tags;
  }

  private SecurityScheme securityScheme() {
    return new BasicAuth("basicAuth");
  }


}
