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

import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;

import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.util.StrutsTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class LiasonUserConverter extends StrutsTypeConverter {

  private static final Logger LOG = LoggerFactory.getLogger(LiasonUserConverter.class);
  private final LiaisonUserManager liaisonUserManager;

  @Inject
  public LiasonUserConverter(LiaisonUserManager liaisonUserManager) {
    this.liaisonUserManager = liaisonUserManager;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    // Is this a bug, should it not be LiasonUser.class?
    if (toClass == LiaisonInstitution.class) {
      String id = values[0];
      try {
        LiaisonUser liaisonUser = liaisonUserManager.getLiaisonUserById(Long.parseLong(id));
        LOG.debug(">> convertFromString > id = {} ", id);
        return liaisonUser;
      } catch (NumberFormatException e) {
        // Do Nothing
        LOG.error("Problem to convert User from String (convertFromString) for LiaisonUser_id = {} ", id,
          e.getMessage());
      }
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public String convertToString(Map context, Object o) {
    if (o != null) {
      LiaisonUser liaisonUser = (LiaisonUser) o;
      LOG.debug(">> convertToString > id = {} ", liaisonUser.getId());
      return liaisonUser.getId() + "";
    }
    return null;
  }

}
