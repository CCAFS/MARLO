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

package org.cgiar.ccafs.marlo.config;

import org.cgiar.ccafs.marlo.security.APCustomRealm;

import javax.inject.Named;

import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * We need to set the DefaultWebSecurityManager's realm but we need the realm to initialize first. Therefore
 * we have a context listener that gets called once the application is ready.
 * 
 * @author GrantL
 */
@Named
public class ShiroSpringStartupListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

  private ApplicationContext ctx;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    try {

      // Initialize realms
      final APCustomRealm mainRealm = (APCustomRealm) ctx.getBean("realm");
      final DefaultWebSecurityManager sm = (DefaultWebSecurityManager) ctx.getBean("securityManager");
      sm.setRealm(mainRealm);
    } catch (Exception e) {
      System.out.println("Error loading: " + e.getMessage());
      throw new Error("Critical system error", e);
    }

  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    this.ctx = applicationContext;
  }

}
