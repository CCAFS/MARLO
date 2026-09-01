package org.cgiar.ccafs.marlo;
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


import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.CognitoTokenValidator;
import org.cgiar.ccafs.marlo.security.authentication.DBAuthenticator;
import org.cgiar.ccafs.marlo.security.authentication.LDAPAuthenticator;
import org.cgiar.ccafs.marlo.security.impl.CognitoTokenValidatorImpl;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for MARLO Security using Apache Shiro.
 */
@Configuration
public class MarloShiroConfiguration {

  @Bean
  public APCustomRealm apCustomRealm(DBAuthenticator dbAuthenticator,
                                      LDAPAuthenticator ldapAuthenticator,
                                      UserManager userManager,
                                      APConfig apConfig) {
      return new APCustomRealm(dbAuthenticator, ldapAuthenticator, userManager, apConfig);
  }

  /**
   * CHG-COGNITO-AUTH-001-T06: singleton wiring for the Cognito token validator (design.md DD-5), placed
   * alongside the realm's own hand-construction above since neither is discovered by classpath scanning.
   * <p>
   * <b>Not injected into {@link APCustomRealm} itself.</b> The realm's {@code doGetAuthenticationInfo}
   * never has a raw ID token to hand to {@link CognitoTokenValidator#validate(String, String)} -- a
   * {@code CognitoAuthenticationToken} carries only an already-validated {@code CognitoAssertion}
   * (design.md 2.1, DD-5) -- so wiring the validator into the realm's constructor would add a dependency
   * the realm structurally cannot call. This bean exists for the callback/login actions (T08, T09) that do
   * hold a raw token.
   * <p>
   * <b>Must stay a plain singleton.</b> A prototype-scoped bean would give every injection point its own
   * JWKS cache, silently multiplying design.md 12's "exactly one outbound MARLO-to-AWS call per login"
   * measure -- a forward note from the independent T05 audit. A bare {@code @Bean} method is singleton by
   * default, so no {@code @Scope} is added.
   * <p>
   * Constructing {@link CognitoTokenValidatorImpl} performs no I/O: {@link APConfig} returns {@code ""},
   * never {@code null}, for every unset Cognito key (design.md 9.3), so this bean builds safely during
   * Spring context startup even on an environment with no Cognito configuration at all.
   */
  @Bean
  public CognitoTokenValidator cognitoTokenValidator(APConfig apConfig) {
      return new CognitoTokenValidatorImpl(apConfig);
  }

  /**
   * The realm @APCustomRealm is discovered and initialized by Spring classpath scanning and is injected with other
   * dependencies which is why it is not configured here. The @ShiroSpringStartupListener will then set the realm on the
   * securityManager when notified by an @ApplicationEvent.
   */
  // @Bean(name = "securityManager")
  // public DefaultWebSecurityManager securityManager(APCustomRealm apCustomRealm) {
  //   DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
  //   securityManager.setRealm(apCustomRealm);
  //   SecurityUtils.setSecurityManager(securityManager);
  //   return securityManager;
  // }

  @Bean
  public SimpleCookie sessionIdCookie() {
      SimpleCookie cookie = new SimpleCookie("JSESSIONID");
      cookie.setHttpOnly(true);
      cookie.setPath("/");
      return cookie;
  }

  @Bean
  public DefaultWebSessionManager sessionManager() {
      DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
      sessionManager.setGlobalSessionTimeout(30 * 60 * 1000L); // 30 min
      sessionManager.setSessionIdCookieEnabled(true);
      sessionManager.setSessionIdCookie(sessionIdCookie());
      sessionManager.setSessionValidationSchedulerEnabled(true);
      // sessionManager.setDeleteInvalidSessions(true);
      return sessionManager;
  }

  @Bean(name = "securityManager")
  public WebSecurityManager securityManager(APCustomRealm apCustomRealm) {
      DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
      manager.setRealm(apCustomRealm);
      manager.setSessionManager(sessionManager());
      return manager;
  }

  @Bean
  public static MethodInvokingFactoryBean methodInvokingFactoryBean(org.apache.shiro.mgt.SecurityManager securityManager) {
      MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
      bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
      bean.setArguments(new Object[]{securityManager});
      return bean;
  }

  @Bean(name = "shiroFilterFactoryBean")
  public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) throws Exception {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    shiroFilterFactoryBean.setSecurityManager(securityManager);

    shiroFilterFactoryBean.setLoginUrl("/login.do");
    shiroFilterFactoryBean.setSuccessUrl("/");
    shiroFilterFactoryBean.setUnauthorizedUrl("/403");

    // RESTfull services basic authentication filter setup.
    Map<String, String> filterChainDefinitionMap = new HashMap<String, String>();
    filterChainDefinitionMap.put("/swagger/index.html", "anon");
    filterChainDefinitionMap.put("/swagger/home.html", "anon");
    filterChainDefinitionMap.put("/swagger/api.html", "anon");
    filterChainDefinitionMap.put("/api/**", "authcBasic");
    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);


    return shiroFilterFactoryBean;
  }

  @Bean(name = "lifecycleBeanPostProcessor")
  public LifecycleBeanPostProcessor vetLifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }
}
