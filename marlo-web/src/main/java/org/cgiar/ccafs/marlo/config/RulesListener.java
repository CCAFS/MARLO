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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.easyrules.api.RulesEngine;
import org.easyrules.core.RulesEngineBuilder;

public class RulesListener implements ServletContextListener {

  private static Class<?> clazz = RulesEngine.class;
  public static final String KEY_NAME = clazz.getName();

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    //

  }

  @Override
  public void contextInitialized(ServletContextEvent event) {

    RulesEngine fizzBuzzEngine = RulesEngineBuilder.aNewRulesEngine().build();
    fizzBuzzEngine.clearRules();
    event.getServletContext().setAttribute(KEY_NAME, fizzBuzzEngine);

  }

}
