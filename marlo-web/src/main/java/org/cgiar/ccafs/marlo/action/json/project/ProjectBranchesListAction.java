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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectBranchesListAction extends BaseAction {


  private static final long serialVersionUID = -4692988228947863628L;


  private List<Map<String, Object>> branches;

  private long institutionID;

  private InstitutionManager institutionManager;

  @Inject
  public ProjectBranchesListAction(APConfig config, InstitutionManager institutionManager) {
    super(config);
    this.institutionManager = institutionManager;
  }

  @Override
  public String execute() throws Exception {
    Institution institution = institutionManager.getInstitutionById(institutionID);
    Map<String, Object> branch;
    branches = new ArrayList<>();

    branch = new HashMap<String, Object>();
    branch.put("id", institution.getId());
    branch.put("name", institution.getBranchName());
    branch.put("iso", institution.getLocElement().getIsoAlpha2());

    branches.add(branch);

    for (Institution institutionBranch : institution.getBranches().stream().filter(b -> b.isActive())
      .collect(Collectors.toList())) {
      branch = new HashMap<String, Object>();
      branch.put("id", institutionBranch.getId());
      branch.put("name", institutionBranch.getBranchName());
      branch.put("iso", institutionBranch.getLocElement().getIsoAlpha2());
      branches.add(branch);
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getBranches() {
    return branches;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    institutionID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_REQUEST_ID))[0]));
  }

  public void setBranches(List<Map<String, Object>> branches) {
    this.branches = branches;
  }

}
