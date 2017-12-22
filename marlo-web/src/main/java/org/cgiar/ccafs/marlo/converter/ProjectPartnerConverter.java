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

import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;

import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.util.StrutsTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectPartnerConverter extends StrutsTypeConverter {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectPartnerConverter.class);
  private final ProjectPartnerManager projectPartnerManager;

  @Inject
  public ProjectPartnerConverter(ProjectPartnerManager projectPartnerManager) {
    this.projectPartnerManager = projectPartnerManager;
  }

  @Override
  public Object convertFromString(@SuppressWarnings("rawtypes") Map context, String[] values,
    @SuppressWarnings("rawtypes") Class toClass) {
    if (toClass == ProjectPartner.class) {
      String id = values[0];
      try {
        ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(Long.parseLong(id));
        LOG.debug(">> convertFromString > id = {} ", id);
        return projectPartner;
      } catch (NumberFormatException e) {
        // Do Nothing
        LOG.error("Problem to convert User from String (convertFromString) for projectPartner_id = {} ", id,
          e.getMessage());
      }
    }
    return null;
  }

  @Override
  public String convertToString(@SuppressWarnings("rawtypes") Map context, Object o) {
    if (o != null) {
      ProjectPartner projectPartner = (ProjectPartner) o;
      LOG.debug(">> convertToString > id = {} ", projectPartner.getId());
      return projectPartner.getId() + "";
    }
    return null;
  }

}
