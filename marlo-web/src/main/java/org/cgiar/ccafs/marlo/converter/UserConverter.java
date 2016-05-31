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
package org.cgiar.ccafs.marlo.converter;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.Map;

import com.google.inject.Inject;
import org.apache.struts2.util.StrutsTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserConverter extends StrutsTypeConverter {

  // LOG
  private static final Logger LOG = LoggerFactory.getLogger(UserConverter.class);

  // Manager
  private UserManager userManager;

  @Inject
  public UserConverter(UserManager userManager) {
    this.userManager = userManager;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    System.out.println("Hola");
    if (toClass == User.class) {
      String id = values[0];
      try {
        User user = userManager.getUser(Long.parseLong(id));
        LOG.debug(">> convertFromString > id = {} ", id);
        return user;
      } catch (NumberFormatException e) {
        LOG.error("Problem to convert User from String (convertFromString) for user_id = {} ", id, e.getMessage());
      }
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public String convertToString(Map context, Object o) {
    System.out.println("Hola");
    User user = (User) o;
    LOG.debug(">> convertToString > id = {} ", user.getId());
    return user.getId() + "";
  }

}
