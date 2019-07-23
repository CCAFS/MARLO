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

package org.marlo.core;


import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
@ComponentScan({"org.marlo.controller"})
public class CoreAppContextConfig {

  public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
  public static final String SPRING_PROFILE_USERTEST = "test";
  public static final String SPRING_PROFILE_PRODUCTION = "pro";

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Inject
  private ConfigurableEnvironment environment;

  private final Logger log = LoggerFactory.getLogger(CoreAppContextConfig.class);

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
