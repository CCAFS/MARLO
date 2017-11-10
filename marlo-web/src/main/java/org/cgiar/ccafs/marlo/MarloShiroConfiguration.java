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

import org.cgiar.ccafs.marlo.security.APCustomRealm;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Configuration for MARLO Security using Apache Shiro.
 */
@Configuration
public class MarloShiroConfiguration {

  @Bean(name = "lifecycleBeanPostProcessor")
  public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }

  @Bean(name = "realm")
  @DependsOn("lifecycleBeanPostProcessor")
  public APCustomRealm realm() {
    return new APCustomRealm();
  }

  @Bean(name = "securityManager")
  public DefaultWebSecurityManager securityManager() {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    // securityManager.setRealm(this.realm());
    return securityManager;
  }

  @Bean(name = "shiroFilter")
  public ShiroFilterFactoryBean shiroFilter() throws Exception {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    shiroFilterFactoryBean.setSecurityManager(this.securityManager());

    shiroFilterFactoryBean.setLoginUrl("/login.do");
    shiroFilterFactoryBean.setSuccessUrl("/");
    shiroFilterFactoryBean.setUnauthorizedUrl("/403");

    return shiroFilterFactoryBean;
  }
}
