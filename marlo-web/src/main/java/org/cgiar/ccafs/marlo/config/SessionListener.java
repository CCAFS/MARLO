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


import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Inject;

public class SessionListener implements ServletContextListener {

  ServletContext context;

  @Inject
  private CrpManager crpmanger;

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    //

  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    context = event.getServletContext();

    try {

      System.out.println("entra al listner");


      Crp crp = (Crp) context.getAttribute(APConstants.SESSION_CRP);
      if (crp != null) {
        crp = crpmanger.getCrpById(crp.getId());
        System.out.println("HAS CRP!!!" + crp.getAcronym());

        for (CrpParameter parameter : crp.getCrpParameters()) {
          if (parameter.isActive()) {
            context.setAttribute(parameter.getKey(), parameter.getValue());
          }
        }
      }
    } catch (Exception e) {

    }
  }
}