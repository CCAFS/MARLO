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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class PentahoListener implements ServletContextListener {

  private static Class<?> clazz = ResourceManager.class;
  public static final String KEY_NAME = clazz.getName();

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    //

  }

  @Override
  public void contextInitialized(ServletContextEvent event) {

    try {
      ClassicEngineBoot.getInstance().start();
      ResourceManager manager = new ResourceManager();
      manager.registerDefaults();

      event.getServletContext().setAttribute(KEY_NAME, manager);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}