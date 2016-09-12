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

package org.cgiar.ccafs.marlo.action.bilaterals;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBilateralCofinancingManager;
import org.cgiar.ccafs.marlo.data.model.AgreementStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CofundedAction extends BaseAction {


  private static final long serialVersionUID = -3919022306156272887L;


  private CrpManager crpManager;


  private ProjectBilateralCofinancingManager projectBilateralCofinancingManager;


  private InstitutionManager institutionManager;


  private LiaisonInstitutionManager liaisonInstitutionManager;


  private Crp loggedCrp;


  private long projectID;


  private ProjectBilateralCofinancing project;


  private Map<String, String> status;


  private List<LiaisonInstitution> liaisonInstitutions;

  private List<Institution> institutions;


  @Inject
  public CofundedAction(APConfig config, CrpManager crpManager,
    ProjectBilateralCofinancingManager projectBilateralCofinancingManager, InstitutionManager institutionManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectBilateralCofinancingManager = projectBilateralCofinancingManager;
    this.institutionManager = institutionManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public ProjectBilateralCofinancing getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public Map<String, String> getStatus() {
    return status;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    project = projectBilateralCofinancingManager.getProjectBilateralCofinancingById(projectID);

    if (project != null) {

      status = new HashMap<>();
      List<AgreementStatusEnum> list = Arrays.asList(AgreementStatusEnum.values());
      for (AgreementStatusEnum agreementStatusEnum : list) {
        status.put(agreementStatusEnum.getStatusId(), agreementStatusEnum.getStatus());
      }

      institutions = institutionManager.findAll().stream()
        .filter(i -> i.isActive() && i.getInstitutionType().getId() == APConstants.INSTITUTION_DONOR_TYPE)
        .collect(Collectors.toList());

      liaisonInstitutions = liaisonInstitutionManager.findAll().stream()
        .filter(li -> li.isActive() && li.getCrp().getId() == loggedCrp.getId()).collect(Collectors.toList());

    }


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_W3_COFUNDED_BASE_PERMISSION, params));
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setProject(ProjectBilateralCofinancing project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setStatus(Map<String, String> status) {
    this.status = status;
  }

}
