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

import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.util.StrutsTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectPartnerPersonConverter extends StrutsTypeConverter {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectPartnerPersonConverter.class);
  private final ProjectPartnerPersonManager projectPartnerPersonManager;

  @Inject
  public ProjectPartnerPersonConverter(ProjectPartnerPersonManager projectPartnerPersonManager) {
    this.projectPartnerPersonManager = projectPartnerPersonManager;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    if (toClass == ProjectPartnerPerson.class) {
      String id = values[0];
      try {
        ProjectPartnerPerson projectPartnerPerson =
          projectPartnerPersonManager.getProjectPartnerPersonById(Long.parseLong(id));
        LOG.debug(">> convertFromString > id = {} ", id);
        return projectPartnerPerson;
      } catch (NumberFormatException e) {
        // Do Nothing
        LOG.error("Problem to convert User from String (convertFromString) for projectPartnerPerson_id = {} ", id,
          e.getMessage());
      }
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public String convertToString(Map context, Object o) {
    if (o != null) {
      ProjectPartnerPerson projectPartnerPerson = (ProjectPartnerPerson) o;
      LOG.debug(">> convertToString > id = {} ", projectPartnerPerson.getId());
      return projectPartnerPerson.getId() + "";
    }
    return null;
  }

}
