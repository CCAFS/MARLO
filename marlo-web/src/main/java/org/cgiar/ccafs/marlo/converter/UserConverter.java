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

package org.cgiar.ccafs.marlo.converter;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.util.StrutsTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UserConverter extends StrutsTypeConverter {

  private static final Logger LOG = LoggerFactory.getLogger(UserConverter.class);
  private final UserManager userManager;

  @Inject
  public UserConverter(UserManager userManager) {
    this.userManager = userManager;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    // Is this a bug, should it not be User.class?
    if (toClass == Institution.class) {
      String id = values[0];
      try {
        User user = userManager.getUser(Long.parseLong(id));
        LOG.debug(">> convertFromString > id = {} ", id);
        return user;
      } catch (NumberFormatException e) {
        // Do Nothing
        LOG.error("Problem to convert User from String (convertFromString) for user_id = {} ", id, e.getMessage());
      }
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public String convertToString(Map context, Object o) {
    if (o != null) {
      User user = (User) o;
      LOG.debug(">> convertToString > id = {} ", user.getId());
      return user.getId() + "";
    }
    return null;
  }

}
