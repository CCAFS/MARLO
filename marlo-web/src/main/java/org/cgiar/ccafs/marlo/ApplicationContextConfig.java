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
   * 
   * @hjimenez : this beans does not work. temporally solution is down the struts version to 2.5.10, to able the merge
   *           whit annuality branch
   * @return
   */
  // @Bean
  // public LocalizedTextProvider getLocalizedTextProvider() {
  // // If this is not suitable try the GlobalLocalizedTextProvider
  // return new StrutsLocalizedTextProvider();
  // }

}
