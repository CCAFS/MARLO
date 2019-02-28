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
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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

@PropertySource("classpath:clarisa.properties")
// Disable for production environment.
@Profile("!" + ApplicationContextConfig.SPRING_PROFILE_PRODUCTION)

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
				.select().apis(RequestHandlerSelectors.basePackage("org.cgiar.ccafs.marlo.rest"))
				.paths(PathSelectors.any()).build().apiInfo(this.apiInfo())// .enableUrlTemplating(true)
				.tags(new Tag(this.env.getProperty("allcontrol.tag"), this.env.getProperty("allcontrol.description"),
						Integer.parseInt(this.env.getProperty("allcontrol.order"))), this.getTags())
				.genericModelSubstitutes(Optional.class);

	}

	private ApiInfo apiInfo() {

		ApiInfo apiInfo = new ApiInfo(null, null, null, null, null, null, null, Collections.emptyList());

		return apiInfo;
	}

	private Tag[] getTags() {
		Tag[] tags = new Tag[15];
		tags[0] = new Tag(this.env.getProperty("institution.tag"), this.env.getProperty("institution.description"),
				Integer.parseInt(this.env.getProperty("institution.order")));

		tags[1] = new Tag(this.env.getProperty("generalcontrol.tag"),
				this.env.getProperty("generalcontrol.description"),
				Integer.parseInt(this.env.getProperty("generalcontrol.order")));

		tags[2] = new Tag(this.env.getProperty("srfcontrol.tag"), this.env.getProperty("srfcontrol.description"),
				Integer.parseInt(this.env.getProperty("srfcontrol.order")));

		tags[3] = new Tag(this.env.getProperty("table1.tag"), this.env.getProperty("table1.description"),
				Integer.parseInt(this.env.getProperty("table1.order")));

		tags[4] = new Tag(this.env.getProperty("table2.tag"), this.env.getProperty("table2.description"),
				Integer.parseInt(this.env.getProperty("table2.order")));

		tags[5] = new Tag(this.env.getProperty("table3.tag"), this.env.getProperty("table3.description"),
				Integer.parseInt(this.env.getProperty("table3.order")));

		tags[6] = new Tag(this.env.getProperty("table4.tag"), this.env.getProperty("table4.description"),
				Integer.parseInt(this.env.getProperty("table4.order")));

		tags[7] = new Tag(this.env.getProperty("table5.tag"), this.env.getProperty("table5.description"),
				Integer.parseInt(this.env.getProperty("table5.order")));

		tags[8] = new Tag(this.env.getProperty("table6.tag"), this.env.getProperty("table6.description"),
				Integer.parseInt(this.env.getProperty("table6.order")));

		tags[9] = new Tag(this.env.getProperty("table7.tag"), this.env.getProperty("table7.description"),
				Integer.parseInt(this.env.getProperty("table7.order")));

		tags[10] = new Tag(this.env.getProperty("table8.tag"), this.env.getProperty("table8.description"),
				Integer.parseInt(this.env.getProperty("table8.order")));

		tags[11] = new Tag(this.env.getProperty("table9.tag"), this.env.getProperty("table9.description"),
				Integer.parseInt(this.env.getProperty("table9.order")));

		tags[12] = new Tag(this.env.getProperty("table10.tag"), this.env.getProperty("table10.description"),
				Integer.parseInt(this.env.getProperty("table10.order")));

		tags[13] = new Tag(this.env.getProperty("table11.tag"), this.env.getProperty("table11.description"),
				Integer.parseInt(this.env.getProperty("table11.order")));

		tags[14] = new Tag(this.env.getProperty("table12.tag"), this.env.getProperty("table12.description"),
				Integer.parseInt(this.env.getProperty("table12.order")));

		return tags;
	}

	private SecurityScheme securityScheme() {
		return new BasicAuth("basicAuth");
	}

}
