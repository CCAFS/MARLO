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

import org.cgiar.ccafs.marlo.config.MarloLocalizedTextProvider;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * This configuration file is now for properties but can contain other base level beans as well. Note that these
 * beans get loaded first.
 * Set the spring.profiles.active to be a different property by setting the java systemProperty for example
 * -Dspring.active.profile=test
 */
@Configuration
@PropertySource({"classpath:config/marlo-${spring.profiles.active:dev}.properties"})
@ComponentScan({"org.cgiar.ccafs.marlo.utils", "org.cgiar.ccafs.marlo.config", "org.cgiar.ccafs.marlo.ocs",
  "org.cgiar.ccafs.marlo.security", "org.cgiar.ccafs.marlo.web", "org.cgiar.ccafs.marlo.data.dao",
  "org.cgiar.ccafs.marlo.data.manager", "org.cgiar.ccafs.marlo.validation", "org.cgiar.ccafs.marlo.action"})
public class ApplicationContextConfig {

  public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
  public static final String SPRING_PROFILE_USERTEST = "test";
  public static final String SPRING_PROFILE_PRODUCTION = "pro";

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Inject
  private ConfigurableEnvironment environment;

  private final Logger log = LoggerFactory.getLogger(ApplicationContextConfig.class);

  /**
   * Returns a LocalizedTextProvider to be used by our internationalization interceptor.
   * Ensure beans that use or extend struts2 classes are listed as constants within the struts.xml config file.
   * For example the bean defined below needs the following entry otherwise the default implementation will be
   * used. Note that the value field references the bean name attribute.
   * <constant name="struts.localizedTextProvider" value="marloLocalizedTextProvider" />
   * Also note that the @DefaultConfiguration createBootstrapContainer method creates the default
   * StrutsLocalizedTextProvider anyway, which seems strange.
   * 
   * @return
   */
  @Bean(name = "marloLocalizedTextProvider")
  public LocalizedTextProvider getLocalizedTextProvider() {
    return new MarloLocalizedTextProvider();
  }

  @PostConstruct
  public void initApplication() {
    if (environment.getActiveProfiles().length == 0) {
      log.warn("No Spring profile configured, will default to dev");
      environment.setActiveProfiles(SPRING_PROFILE_DEVELOPMENT);
    } else {
      log.info("Running with Spring profile(s) : {}", Arrays.toString(environment.getActiveProfiles()));
      Collection<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
      if (activeProfiles.contains(SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(SPRING_PROFILE_PRODUCTION)) {
        String message = "You have misconfigured your application! "
          + "It should not run with both the 'dev' and 'pro' profiles at the same time.";
        log.error(message);
        throw new RuntimeException(message);
      }
      if (activeProfiles.contains(SPRING_PROFILE_USERTEST) && activeProfiles.contains(SPRING_PROFILE_PRODUCTION)) {
        String message = "You have misconfigured your application! "
          + "It should not run with both the 'test' and 'pro' profiles at the same time.";
        log.error(message);
        throw new RuntimeException(message);
      }
      if (activeProfiles.contains(SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(SPRING_PROFILE_USERTEST)) {
        String message = "You have misconfigured your application! "
          + "It should not run with both the 'test' and 'dev' profiles at the same time.";
        log.error(message);
        throw new RuntimeException(message);
      }
    }
  }


}
