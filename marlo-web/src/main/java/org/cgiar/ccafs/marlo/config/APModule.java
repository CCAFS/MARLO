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

import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.security.authentication.DBAuthenticator;
import org.cgiar.ccafs.marlo.security.authentication.LDAPAuthenticator;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PropertiesManager;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hernán David Carvajal
 * @author Héctor Fabio Tobón
 * @author Chirstian David Garcia
 */
public class APModule implements Module {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(APModule.class);

  private PropertiesManager properties;

  @Override
  public void configure(Binder binder) {
    // We are configuring google guice using annotation. However you can do it here if you want.
    binder.bind(Authenticator.class).annotatedWith(Names.named("LDAP")).to(LDAPAuthenticator.class);
    binder.bind(Authenticator.class).annotatedWith(Names.named("DB")).to(DBAuthenticator.class);

    binder.bind(SessionFactory.class).toProvider(HibernateSessionFactoryProvider.class).in(Singleton.class);
    // In addition, we are using this place to configure other stuffs.
    ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);

    properties = new PropertiesManager();

    LOG.info("----- DATABASE CONNECTION -----");
    LOG.info(properties.getPropertiesAsString(APConfig.MYSQL_USER));
    LOG.info(properties.getPropertiesAsString(APConfig.MYSQL_HOST));
    LOG.info(properties.getPropertiesAsString(APConfig.MYSQL_DATABASE));


  }

}
