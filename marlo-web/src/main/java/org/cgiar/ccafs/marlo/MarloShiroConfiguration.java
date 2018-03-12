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

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for MARLO Security using Apache Shiro.
 */
@Configuration
public class MarloShiroConfiguration {


  /**
   * The realm @APCustomRealm is discovered and initialized by Spring classpath scanning and is injected with other
   * dependencies which is why it is not configured here. The @ShiroSpringStartupListener will then set the realm on the
   * securityManager when notified by an @ApplicationEvent.
   */
  @Bean(name = "securityManager")
  public DefaultWebSecurityManager securityManager() {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    return securityManager;
  }

  @Bean(name = "shiroFilter")
  public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) throws Exception {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    shiroFilterFactoryBean.setSecurityManager(securityManager);

    shiroFilterFactoryBean.setLoginUrl("/login.do");
    shiroFilterFactoryBean.setSuccessUrl("/");
    shiroFilterFactoryBean.setUnauthorizedUrl("/403");

    // RESTfull services basic authentication filter setup.
    Map<String, String> filterChainDefinitionMap = new HashMap<>();
    filterChainDefinitionMap.put("/api/**", "authcBasic");
    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

    return shiroFilterFactoryBean;
  }

  @Bean(name = "lifecycleBeanPostProcessor")
  public LifecycleBeanPostProcessor vetLifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }
}
