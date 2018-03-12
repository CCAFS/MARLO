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

import org.cgiar.ccafs.marlo.logging.LoggingAspect;

import java.util.Collections;

import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableSwagger2
@ComponentScan(basePackages = {"org.cgiar.ccafs.marlo.rest"})
public class MarloRestApiConfig extends WebMvcConfigurerAdapter {

  // Config required for Swagger UI if not using Spring Boot.
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select()
      .apis(RequestHandlerSelectors.basePackage("org.cgiar.ccafs.marlo.rest")).paths(PathSelectors.any()).build()
      .apiInfo(this.apiInfo());
  }


  private ApiInfo apiInfo() {

    ApiInfo apiInfo = new ApiInfo("MARLO REST API",
      "A list of operations provided by the MARLO REST API.  "
        + "Please note that access to this page does not necessarily mean that you have authorization to perform all actions.",
      "v1", "Terms of service URL - to be confirmed", new Contact("Hector Tobon", "", "h.f.tobon@cgiar.org"),
      "License of API - To be confirmed", "API license URL - to be confirmed", Collections.emptyList());

    return apiInfo;
  }

  /**
   * The following aspect bean below checks to see if Shiro annotations are present.
   */
  @Bean
  public AuthorizationAttributeSourceAdvisor
    getAuthorizationAttributeSourceAdvisor(WebSecurityManager securityManager) {
    AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
    advisor.setSecurityManager(securityManager);
    return advisor;
  }

  /**
   * The following bean is to allow Shiro annotations using Spring AOP.
   * This bean does not proxy our Controllers if configured in the parent context.
   */
  @Bean
  @DependsOn("lifecycleBeanPostProcessor")
  public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
    DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
    proxyCreator.setProxyTargetClass(true);
    return proxyCreator;
  }

  @Bean
  public LoggingAspect loggingAspect() {
    return new LoggingAspect();
  }

}
