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
		Tag[] tags = new Tag[15];
		tags[0] = new Tag("Institutions Lists", "All control List about MARLO intitutions ", 110);
		tags[1] = new Tag("General Control Lists", "General pourpose control List ", 115);
		tags[2] = new Tag("SRF Lists", "All SRF related control List ", 120);
		tags[3] = new Tag("Table 1 - Evidence on Progress towards SRF targets",
				"Control list used to populate the table 1 indicator as per the AR2018 template", 5);
		tags[4] = new Tag("Table 2 - CRP Policies",
				"Control list used to populate the policies indicator as per the AR2018 template", 10);
		tags[5] = new Tag("Table 3 - Outcome/ Impact Case Reports",
				"Control list used to populate the policies indicator as per the AR2018 template", 15);
		tags[6] = new Tag("Table 4 - CRP Innovations",
				"Control list used to populate the innovation indicator as per the AR2018 template", 20);

		tags[7] = new Tag("Table 5 - Status of Planned Outcomes and Milestones",
				"Control list used to populate this indicator as per the AR2018 template", 25);

		tags[8] = new Tag("Table 6 - Peer-reviewed publicationss",
				"Control list used to populate this indicator as per the AR2018 template", 30);

		tags[9] = new Tag("Table 7 - Key external partnerships",
				"Control list used to populate this indicator as per the AR2018 template", 35);

		tags[10] = new Tag("Table 8 - Internal Cross-CGIAR Collaborations",
				"Control list used to populate this indicator as per the AR2018 template", 40);

		tags[11] = new Tag("Table 9 - Monitoring, Evaluation, \n Learning and Impact Assessment (MELIA)",
				"Control list used to populate this indicator as per the AR2018 template", 45);

		tags[12] = new Tag("Table 10 - Update on Actions Taken in Response to Relevant Evaluations",
				"Control list used to populate this indicator as per the AR2018 template", 50);

		tags[13] = new Tag("Table 11 - W1/2 Use",
				"Control list used to populate this indicator as per the AR2018 template", 55);

		tags[14] = new Tag("Table 12 - CRP Financial Report",
				"Control list used to populate this indicator as per the AR2018 template", 60);

		return tags;
	}

	private SecurityScheme securityScheme() {
		return new BasicAuth("basicAuth");
	}

}
