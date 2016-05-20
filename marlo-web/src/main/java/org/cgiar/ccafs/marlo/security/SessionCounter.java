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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import com.google.inject.Inject;

/**
 * This class implements the attributes change of an session.
 *
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Chirstian David Garcia - CIAT/CCAFS
 */
public class SessionCounter implements HttpSessionAttributeListener {

  public static List<User> users;
  @Inject
  private UserManager userManager;

  @Inject
  private BaseSecurityContext securityContext;

  @Override
  public void attributeAdded(HttpSessionBindingEvent se) {
    if (users == null) {
      users = new ArrayList<User>();
    }
    if (se.getName().equals(APConstants.SESSION_USER)) {
      if (!users.contains(se.getValue())) {
        users.add((User) se.getValue());
      } else {
        User duplicateUser = new User();
        duplicateUser.setId(new Long(-1));
        se.getSession().setAttribute(APConstants.SESSION_USER, duplicateUser);
      }
    }

  }

  @Override
  public void attributeRemoved(HttpSessionBindingEvent se) {
    if (se.getName().equals(APConstants.SESSION_USER)) {
      users.remove(se.getValue());
    }
  }

  @Override
  public void attributeReplaced(HttpSessionBindingEvent se) {
    // TODO Auto-generated method stub

  }


}
