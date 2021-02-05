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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author German C. Martinez - CIAT/CCAFS
 */
public class InstitutionById extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 2999679358601654825L;

  private Map<String, Object> institutionMap;
  private InstitutionManager institutionManager;

  private Integer institutionId;

  @Inject
  public InstitutionById(APConfig config, InstitutionManager institutionManager) {
    super(config);

    this.institutionManager = institutionManager;
  }

  @Override
  public String execute() throws Exception {
    Institution institution =
      (institutionId != null) ? institutionManager.getInstitutionById(this.institutionId) : null;

    if (institution != null) {
      this.institutionMap = new HashMap<>();
      this.institutionMap.put("institutionId", institution.getId());
      this.institutionMap.put("institutionName", institution.getName());
    } else {
      this.institutionMap = Collections.emptyMap();
    }

    return SUCCESS;
  }

  public Map<String, Object> getInstitution() {
    return institutionMap;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    // If there are parameters, take its values
    try {
      this.institutionId =
        Integer.valueOf(StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      this.institutionId = null;
    }
  }

  public void setInstitution(Map<String, Object> institution) {
    this.institutionMap = institution;
  }
}
