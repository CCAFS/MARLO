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

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * This configuration file is now for properties but can contain other base level beans as well. Note that these
 * beans get loaded first.
 */
@Configuration
@PropertySource("classpath:marlo.properties")
@ComponentScan("org.cgiar.ccafs.marlo")
public class ApplicationContextConfig {


  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

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


}
