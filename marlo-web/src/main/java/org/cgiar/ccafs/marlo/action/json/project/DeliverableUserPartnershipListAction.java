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
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableUserPartnershipListAction extends BaseAction {


  private static final long serialVersionUID = 4663544283175165587L;


  private Long projectId;
  private Long institutionId;
  private Long phaseId;


  private InstitutionManager institutionManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private ProjectPartnerManager projectPartnerManager;


  private List<Map<String, Object>> users;

  @Inject
  public DeliverableUserPartnershipListAction(APConfig config, CrpClusterOfActivityManager crpClusterOfActivityManager,
    InstitutionManager institutionManager, ProjectManager projectManager, PhaseManager phaseManager,
    ProjectPartnerManager projectPartnerManager) {
    super(config);
    this.institutionManager = institutionManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.projectPartnerManager = projectPartnerManager;
  }

  @Override
  public String execute() throws Exception {

    users = new ArrayList<>();
    Map<String, Object> user;

    Institution institution = institutionManager.getInstitutionById(institutionId);
    Project project = projectManager.getProjectById(projectId);
    Phase phase = phaseManager.getPhaseById(phaseId);

    if (project != null && institution != null && phase != null) {

      List<ProjectPartner> partnersTmp = projectPartnerManager.findAll().stream()
        .filter(pp -> pp.isActive() && pp.getProject().getId().equals(project.getId())
          && pp.getPhase().getId().equals(phase.getId()) && pp.getInstitution().getId().equals(institution.getId()))
        .collect(Collectors.toList());

      if (partnersTmp != null && !partnersTmp.isEmpty()) {
        ProjectPartner projectPartner = partnersTmp.get(0);
        List<ProjectPartnerPerson> partnerPersons = new ArrayList<>(
          projectPartner.getProjectPartnerPersons().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()));
        for (ProjectPartnerPerson projectPartnerPerson : partnerPersons) {

          user = new HashMap<String, Object>();
          user.put("id", projectPartnerPerson.getUser().getId());
          user.put("name", projectPartnerPerson.getUser().getComposedCompleteName());
          users.add(user);
        }
      }

    }

    return SUCCESS;
  }


  public List<Map<String, Object>> getUsers() {
    return users;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    projectId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]));
    institutionId =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0]));
    phaseId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
  }


  public void setUsers(List<Map<String, Object>> users) {
    this.users = users;
  }


}
