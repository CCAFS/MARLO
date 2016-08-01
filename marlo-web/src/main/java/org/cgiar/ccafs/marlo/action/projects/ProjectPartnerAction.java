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


package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerOverallManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ProjectPartnerAction extends BaseAction {


  private static final long serialVersionUID = 7833194831832715444L;
  private ProjectPartnerManager projectPartnerManager;
  private ProjectPartnerOverallManager projectPartnerOverallManager;
  private InstitutionManager institutionManager;
  private InstitutionTypeManager institutionTypeManager;
  private LocElementManager locationManager;
  private ProjectManager projectManager;
  private UserManager userManager;
  private CrpPpaPartnerManager crpPpaPartnerManager;
  private RoleManager roleManager;
  private String overall;
  private long projectID;

  private Project previousProject;

  private Project project;
  // Model for the view
  private List<InstitutionType> intitutionTypes;
  private Map<String, String> partnerPersonTypes; // List of partner person types (CP, PL, PC).

  private List<LocElement> countries;
  private List<Institution> allInstitutions; // Is used to list all the partner institutions that have the system.
  private List<Institution> allPPAInstitutions; // Is used to list all the PPA partners institutions
  private List<ProjectPartner> projectPPAPartners; // Is used to list all the PPA partners that belongs to the project.
  private List<User> allUsers; // will be used to list all the project leaders that have the system.
  // Util
  private SendMail sendMail;

  @Inject
  public ProjectPartnerAction(APConfig config, ProjectPartnerManager projectPartnerManager,
    InstitutionManager institutionManager, LocElementManager locationManager, ProjectManager projectManager,
    UserManager userManager, InstitutionTypeManager institutionTypeManager, CrpPpaPartnerManager crpPpaPartnerManager,
    RoleManager roleManager, ProjectPartnerOverallManager projectPartnerOverallManager, SendMail sendMail) {
    super(config);
    this.institutionTypeManager = institutionTypeManager;
    this.projectPartnerManager = projectPartnerManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locationManager = locationManager;
    this.projectManager = projectManager;
    this.userManager = userManager;
    this.roleManager = roleManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.projectPartnerOverallManager = projectPartnerOverallManager;
  }

  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    // Getting the project identified with the id parameter.
    project = projectManager.getProjectById(projectID);

    // Getting the list of all institutions
    allInstitutions = institutionManager.findAll();

    // Getting the list of all PPA institutions
    allPPAInstitutions = new ArrayList<>();
    for (CrpPpaPartner crpPpaPartner : crpPpaPartnerManager.findAll().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      allPPAInstitutions.add(crpPpaPartner.getInstitution());
    }

    // Getting all the countries
    countries = locationManager.findAll().stream().filter(c -> c.isActive() && c.getLocElementType().getId() == 2)
      .collect(Collectors.toList());

    // Getting all partner types
    intitutionTypes = institutionTypeManager.findAll();

    project.setPartners(project.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    if (!project.getPartners().isEmpty()) {
      List<ProjectPartnerOverall> overalls = project.getPartners().get(0).getProjectPartnerOveralls().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList());
      if (!overalls.isEmpty()) {
        overall = overalls.get(0).getOverall();
      }
    }
    for (ProjectPartner projectPartner : project.getPartners()) {
      projectPartner.setPartnerPersons(
        projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    }
    ProjectPartner leader = project.getLeader();
    if (leader != null) {
      // First we remove the element from the array.
      project.getPartners().remove(leader);
      // then we add it to the first position.
      project.getPartners().add(0, leader);
    }
    // Getting the list of PPA Partners for this project
    this.projectPPAPartners = new ArrayList<ProjectPartner>();
    for (ProjectPartner pp : project.getProjectPartners()) {

      // if (pp.getInstitution()) {
      // this.projectPPAPartners.add(pp);
      // }
    }
  }

}
