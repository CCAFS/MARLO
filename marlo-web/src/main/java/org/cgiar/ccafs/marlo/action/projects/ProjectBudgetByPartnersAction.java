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
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerOverallManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.SendMail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ProjectBudgetByPartnersAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 7833194831832715444L;

  private InstitutionManager institutionManager;

  private UserManager userManager;
  private RoleManager roleManager;
  private ProjectManager projectManager;
  private CrpManager crpManager;

  private long projectID;
  private Crp loggedCrp;

  private Project project;
  // Model for the view
  private List<InstitutionType> intitutionTypes;
  private Map<String, String> partnerPersonTypes; // List of partner person types (CP, PL, PC).

  private List<LocElement> countries;
  private List<Institution> allInstitutions; // Is used to list all the partner institutions that have the system.
  private List<Institution> allPPAInstitutions; // Is used to list all the PPA partners institutions
  private List<ProjectPartner> projectPPAPartners; // Is used to list all the PPA partners that belongs to the project.
  private List<User> allUsers; // will be used to list all the project leaders that have the system.

  private AuditLogManager auditLogManager;
  private String transaction;


  @Inject
  public ProjectBudgetByPartnersAction(APConfig config, ProjectPartnerManager projectPartnerManager,
    InstitutionManager institutionManager, LocElementManager locationManager, ProjectManager projectManager,
    CrpPpaPartnerManager crpPpaPartnerManager, CrpManager crpManager,
    ProjectPartnerOverallManager projectPartnerOverallManager, UserManager userManager,
    InstitutionTypeManager institutionTypeManager, SendMail sendMail, RoleManager roleManager,
    ProjectPartnerContributionManager projectPartnerContributionManager, UserRoleManager userRoleManager) {
    super(config);


    this.institutionManager = institutionManager;

    this.projectManager = projectManager;
    this.userManager = userManager;
    this.crpManager = crpManager;

    this.roleManager = roleManager;


  }


  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
      System.out.println(fileDeleted);
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionWarning(this.getText("cancel.autoSave") + validationMessage);
    } else {
      this.addActionMessage(this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }


  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
  }


  public List<Institution> getAllInstitutions() {
    return allInstitutions;
  }

  public List<Institution> getAllPPAInstitutions() {
    return allPPAInstitutions;
  }

  public List<User> getAllUsers() {
    return allUsers;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<LocElement> getCountries() {
    return countries;
  }


  public List<InstitutionType> getIntitutionTypes() {
    return intitutionTypes;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public Map<String, String> getPartnerPersonTypes() {
    return partnerPersonTypes;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public List<ProjectPartner> getProjectPPAPartners() {
    return projectPPAPartners;
  }


  public String getTransaction() {
    return transaction;
  }

  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return true;
        }
      }

    }

    return false;
  }


  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);
      if (history != null) {
        project = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      project = projectManager.getProjectById(projectID);
    }


    if (project != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectEditLeader(projectDb.isProjectEditLeader());
        this.projectPPAPartners = new ArrayList<ProjectPartner>();
        for (ProjectPartner pp : project.getPartners()) {


          if (pp.getInstitution() != null) {

            if (pp.getInstitution().getId() != null || pp.getInstitution().getId() != -1) {
              Institution inst = institutionManager.getInstitutionById(pp.getInstitution().getId());
              if (inst != null) {
                if (inst.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
                  .size() > 0) {
                  this.projectPPAPartners.add(pp);

                }
                pp.setInstitution(inst);
              }
            }


          }

          if (pp.getPartnerPersons() != null) {
            for (ProjectPartnerPerson projectPartnerPerson : pp.getPartnerPersons()) {

              if (projectPartnerPerson.getUser().getId() != null) {
                projectPartnerPerson.setUser(userManager.getUser(projectPartnerPerson.getUser().getId()));

              }
            }
          }

          if (pp.getPartnerContributors() != null) {
            for (ProjectPartnerContribution projectPartnerContribution : pp.getPartnerContributors()) {

              if (projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId() != null) {
                projectPartnerContribution.getProjectPartnerContributor()
                  .setInstitution(institutionManager.getInstitutionById(
                    projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId()));
              }

            }
          }


        }
        reader.close();
        this.setDraft(true);
      } else {

        this.setDraft(false);
        project
          .setPartners(project.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        for (ProjectPartner projectPartner : project.getPartners()) {
          projectPartner.setPartnerPersons(
            projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        }
        this.projectPPAPartners = new ArrayList<ProjectPartner>();
        for (ProjectPartner pp : project.getPartners()) {

          if (this.isPPA(pp.getInstitution())) {
            this.projectPPAPartners.add(pp);

          }

          List<ProjectPartnerContribution> contributors = new ArrayList<>();


          List<ProjectPartnerContribution> partnerContributions =
            pp.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
            contributors.add(projectPartnerContribution);
          }
          pp.setPartnerContributors(contributors);
        }

        if (this.isLessonsActive()) {
          this.loadLessons(loggedCrp, project);
        }


      }
    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_PARTNER_BASE_PERMISSION, params));


    ProjectPartner leader = project.getLeader();
    if (leader != null) {
      // First we remove the element from the array.
      project.getPartners().remove(leader);
      // then we add it to the first position.
      project.getPartners().add(0, leader);
    }

    if (this.isHttpPost()) {
      project.getPartners().clear();
    }

  }

  @Override
  public String save() {
    return SUCCESS;
  }


  public void setAllInstitutions(List<Institution> allInstitutions) {
    this.allInstitutions = allInstitutions;
  }


  public void setAllPPAInstitutions(List<Institution> allPPAInstitutions) {
    this.allPPAInstitutions = allPPAInstitutions;
  }


  public void setAllUsers(List<User> allUsers) {
    this.allUsers = allUsers;
  }


  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }


  public void setIntitutionTypes(List<InstitutionType> intitutionTypes) {
    this.intitutionTypes = intitutionTypes;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPartnerPersonTypes(Map<String, String> partnerPersonTypes) {
    this.partnerPersonTypes = partnerPersonTypes;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectPPAPartners(List<ProjectPartner> projectPPAPartners) {
    this.projectPPAPartners = projectPPAPartners;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    if (save) {
    }
  }
}
