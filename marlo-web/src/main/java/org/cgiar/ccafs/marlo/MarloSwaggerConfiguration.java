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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

// Disable for production environment.
@Profile("!" + ApplicationContextConfig.SPRING_PROFILE_PRODUCTION)
@Configuration
@EnableSwagger2
public class MarloSwaggerConfiguration extends WebMvcConfigurerAdapter {

	// Config required for Swagger UI if not using Spring Boot.
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("**").addResourceLocations("/WEB-INF/swagger/dist/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Bean
	public Docket api() {

		return new Docket(DocumentationType.SWAGGER_2).securitySchemes(Collections.singletonList(this.securityScheme()))
				.select().apis(RequestHandlerSelectors.basePackage("org.cgiar.ccafs.marlo.rest"))
				.paths(PathSelectors.any()).build().apiInfo(this.apiInfo())// .enableUrlTemplating(true)
				.tags(new Tag("All AR 2018 Control Lists", "All control list used to populate all tables of AR 2018",
						100), this.getTags())
				.genericModelSubstitutes(Optional.class);

	}

	private ApiInfo apiInfo() {

		ApiInfo apiInfo = new ApiInfo(null, null, null, null, null, null, null, Collections.emptyList());

		return apiInfo;
	}

	private Tag[] getTags() {
		Tag[] tags = new Tag[5];
		tags[0] = new Tag("Table 1 - Evidence on Progress towards SRF targets",
				"This table has available high-quality evidences on progress that was published or made available in 2018",
				5);
		tags[1] = new Tag("Table 2 - CRP Policies",
				"Control list used to populate the policies indicator as per the AR2018 template", 10);
		tags[2] = new Tag("Institutions Lists", "All control List about MARLO intitutions ", 100);

		tags[3] = new Tag("General Control Lists", "General pourpose control List ", 105);
		tags[4] = new Tag("SRF Lists", "All SRF related control List ", 110);

		return tags;
	}

	private SecurityScheme securityScheme() {
		return new BasicAuth("basicAuth");
	}

}
