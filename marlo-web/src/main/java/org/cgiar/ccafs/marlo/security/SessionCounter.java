/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.security;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SessionCounter implements HttpSessionListener {

  public static List<User> users = new ArrayList<User>();
  @Inject
  private UserManager userManager;

  @Inject
  private BaseSecurityContext securityContext;

  @Override
  public void sessionCreated(HttpSessionEvent event) {

    // HttpSession session = event.getSession();
    // int userId = (Integer) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
    // User user = userManager.getUser(userId);
    // session.setAttribute(APConstants.SESSION_USER, user);
    // users.add(user);

  }

  @Override
  public void sessionDestroyed(HttpSessionEvent event) {
    // TODO Auto-generated method stub
  }

}
