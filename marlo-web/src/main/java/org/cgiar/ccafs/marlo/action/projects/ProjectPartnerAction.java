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
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipResearchPhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.TipParametersManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDTO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectDTO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerDTO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipResearchPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPersonDTO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.TipParameters;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.utils.SendMailS;
import org.cgiar.ccafs.marlo.validation.projects.ProjectPartnersValidator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectPartnerAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 7833194831832715444L;


  private static Logger LOG = LoggerFactory.getLogger(ProjectPartnerAction.class);


  /**
   * Helper method to read a stream into memory.
   * 
   * @param stream
   * @return
   * @throws IOException
   */
  public static byte[] readFully(InputStream stream) throws IOException {
    byte[] buffer = new byte[8192];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    int bytesRead;
    if (buffer != null) {
      while ((bytesRead = stream.read(buffer)) != -1) {
        baos.write(buffer, 0, bytesRead);
      }
    }
    return baos.toByteArray();
  }

  ProjectDTO projectDTO = new ProjectDTO();

  // Managers
  private final ProjectPartnerManager projectPartnerManager;
  private final ProjectPartnerPersonManager projectPartnerPersonManager;
  private final ProjectPartnerContributionManager projectPartnerContributionManager;
  private final ProjectPartnerPartnershipManager projectPartnerPartnershipManager;
  private final ProjectPartnerPartnershipLocationManager projectPartnerPartnershipLocationManager;
  private final InstitutionManager institutionManager;
  private final InstitutionTypeManager institutionTypeManager;
  private final LocElementManager locationManager;
  private final UserManager userManager;
  private final UserRoleManager userRoleManager;
  private final RoleManager roleManager;
  private final ProjectManager projectManager;
  private final ProjectInfoManager projectInfoManager;
  private final CrpPpaPartnerManager crpPpaPartnerManager;
  private final ProjectPartnerLocationManager projectPartnerLocationManager;
  private final InstitutionLocationManager institutionLocationManager;
  private final GlobalUnitManager crpManager;
  private final CrpUserManager crpUserManager;
  private final GlobalUnitProjectManager globalUnitProjectManager;
  private final RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager;
  private final RepIndGeographicScopeManager repIndGeographicScopeManager;
  private final RepIndRegionManager repIndRegionManager;
  private final ProjectPartnerPartnershipResearchPhaseManager projectPartnerPartnershipResearchPhaseManager;
  private final AuditLogManager auditLogManager;
  private final DeliverableManager deliverableManager;
  private final PartnerDivisionManager partnerDivisionManager;
  private final ProjectPolicyCenterManager projectPolicyCenterManager;
  private final ProjectPolicyManager projectPolicyManager;
  private final ProjectInnovationCenterManager projectInnovationCenterManager;
  private final ProjectInnovationManager projectInnovationManager;
  private final ProjectExpectedStudyCenterManager projectExpectedStudyCenterManager;
  private final ProjectExpectedStudyManager projectExpectedStudyManager;
  private final ActivityManager activityManager;
  private final TipParametersManager tipParametersManager;

  // Variables
  private final ProjectPartnersValidator projectPartnersValidator;
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  // Model for the view
  private List<InstitutionType> intitutionTypes;
  private Map<String, String> partnerPersonTypes; // List of partner person types (CP, PL, PC).
  private List<LocElement> countries;
  private List<Institution> allInstitutions; // Is used to list all the partner institutions that have the system.
  private List<Institution> allPPAInstitutions; // Is used to list all the PPA partners institutions
  private List<ProjectPartner> projectPPAPartners; // Is used to list all the PPA partners that belongs to the project.
  private List<User> allUsers; // will be used to list all the project leaders that have the system.
  private List<RepIndPhaseResearchPartnership> allRepIndResearchPhases;
  private List<RepIndGeographicScope> allRepIndGeographicScope;
  private List<RepIndRegion> allRepIndRegions;
  private List<PartnerDivision> divisions;
  private Role plRole;
  private Role pcRole;
  private String transaction;
  private final HistoryComparator historyComparator;
  // Util
  private final SendMailS sendMail;

  @Inject
  public ProjectPartnerAction(APConfig config, ProjectPartnerManager projectPartnerManager,
    InstitutionManager institutionManager, LocElementManager locationManager, ProjectManager projectManager,
    CrpPpaPartnerManager crpPpaPartnerManager, GlobalUnitManager crpManager, UserManager userManager,
    InstitutionTypeManager institutionTypeManager, SendMailS sendMail, RoleManager roleManager,
    ProjectPartnerContributionManager projectPartnerContributionManager, UserRoleManager userRoleManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, AuditLogManager auditLogManager,
    ProjectPartnersValidator projectPartnersValidator, HistoryComparator historyComparator,
    ProjectComponentLessonManager projectComponentLessonManager, CrpUserManager crpUserManager,
    ProjectPartnerLocationManager projectPartnerLocationManager, InstitutionLocationManager institutionLocationManager,
    ProjectInfoManager projectInfoManager, GlobalUnitProjectManager globalUnitProjectManager,
    RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, RepIndRegionManager repIndRegionManager,
    ProjectPartnerPartnershipManager projectPartnerPartnershipManager,
    ProjectPartnerPartnershipLocationManager projectPartnerPartnershipLocationManager,
    ProjectPartnerPartnershipResearchPhaseManager projectPartnerPartnershipResearchPhaseManager,
    DeliverableManager deliverableManager, PartnerDivisionManager partnerDivisionManager,
    ProjectPolicyCenterManager projectPolicyCenterManager, ProjectPolicyManager projectPolicyManager,
    ProjectInnovationCenterManager projectInnovationCenterManager, ProjectInnovationManager projectInnovationManager,
    ProjectExpectedStudyCenterManager projectExpectedStudyCenterManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, ActivityManager activityManager,
    TipParametersManager tipParametersManager) {
    super(config);
    this.projectPartnersValidator = projectPartnersValidator;
    this.auditLogManager = auditLogManager;
    this.projectPartnerManager = projectPartnerManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;
    this.projectPartnerLocationManager = projectPartnerLocationManager;
    this.locationManager = locationManager;
    this.historyComparator = historyComparator;
    this.projectManager = projectManager;
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.institutionLocationManager = institutionLocationManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.sendMail = sendMail;
    this.roleManager = roleManager;
    this.projectPartnerContributionManager = projectPartnerContributionManager;
    this.userRoleManager = userRoleManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.crpUserManager = crpUserManager;
    this.projectInfoManager = projectInfoManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.repIndPhaseResearchPartnershipManager = repIndPhaseResearchPartnershipManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.repIndRegionManager = repIndRegionManager;
    this.projectPartnerPartnershipManager = projectPartnerPartnershipManager;
    this.projectPartnerPartnershipLocationManager = projectPartnerPartnershipLocationManager;
    this.projectPartnerPartnershipResearchPhaseManager = projectPartnerPartnershipResearchPhaseManager;
    this.deliverableManager = deliverableManager;
    this.partnerDivisionManager = partnerDivisionManager;
    this.projectPolicyCenterManager = projectPolicyCenterManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectInnovationCenterManager = projectInnovationCenterManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectExpectedStudyCenterManager = projectExpectedStudyCenterManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.activityManager = activityManager;
    this.tipParametersManager = tipParametersManager;
  }

  public void addCrpUser(User user) {
    user = userManager.getUser(user.getId());
    CrpUser crpUser = new CrpUser();
    crpUser.setUser(user);
    crpUser.setCrp(loggedCrp);

    List<CrpUser> userCrp = user.getCrpUsers().stream().filter(cu -> cu.isActive() && cu.getCrp().equals(loggedCrp))
      .collect(Collectors.toList());

    if (userCrp == null || userCrp.isEmpty()) {
      crpUserManager.saveCrpUser(crpUser);
    }
  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }

  public void checkCrpUserByRole(User user) {
    user = userManager.getUser(user.getId());
    List<UserRole> crpUserRoles =
      user.getUserRoles().stream().filter(ur -> ur.getRole().getCrp().equals(loggedCrp)).collect(Collectors.toList());
    if (crpUserRoles == null || crpUserRoles.isEmpty()) {
      List<CrpUser> crpUsers = user.getCrpUsers().stream().filter(cu -> cu.isActive() && cu.getCrp().equals(loggedCrp))
        .collect(Collectors.toList());
      for (CrpUser crpUser : crpUsers) {
        crpUserManager.deleteCrpUser(crpUser.getId());
      }
    }
  }

  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  @Override
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
  }

  private void deletePartnershipLocations(List<ProjectPartnerPartnershipLocation> locationsDB) {
    if (locationsDB != null) {
      for (ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation : locationsDB) {
        projectPartnerPartnershipLocationManager
          .deleteProjectPartnerPartnershipLocation(projectPartnerPartnershipLocation.getId());
      }
    }
  }


  public List<Activity> getActivitiesData(long id) {
    List<Activity> activities = new ArrayList<Activity>();
    for (ProjectPartnerDTO projectPartnerDTO : projectDTO.getProjectPartner()) {
      for (ProjectPartnerPersonDTO projectPartnerPersonDTO : projectPartnerDTO.getProjectPartnerPersonDTO()) {
        if (projectPartnerPersonDTO.getId() == id) {
          return projectPartnerPersonDTO.getActivity();
        }
      }

    }
    return activities;
  }


  // cgamboa 16/05/2024 getActivitiesLedByUser was be updated
  public List<Activity> getActivitiesLedByUser(long userID) {
    List<Activity> activities = new ArrayList<Activity>();
    int qunatityActivity = 0;
    try {
      qunatityActivity =
        activityManager.getActivitiesByProjectAndUserQuantity(projectID, this.getActualPhase().getId(), userID);
      if (qunatityActivity > 0) {
        activities = activityManager.getActivitiesByProject(projectID, this.getActualPhase().getId()).stream()
          .filter(c -> c.isActive() && c.getProjectPartnerPerson() != null && c.getActivityStatus() != null
            && c.getActivityStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            && c.getProjectPartnerPerson().getId().longValue() == userID && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());

      }
    } catch (Exception e) {
      LOG.error(" enable to get acitivities in getActivitiesLedByUser   function ");
    }
    return activities;


  }

  public List<Activity> getActivitiesLedByUserCustom(long userID, List<Activity> activitiesOut) {
    List<Activity> activities = new ArrayList<Activity>();
    try {

      activities = activitiesOut.stream()
        .filter(c -> c.isActive() && c.getProjectPartnerPerson() != null && c.getActivityStatus() != null
          && c.getActivityStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          && c.getProjectPartnerPerson().getId().longValue() == userID && c.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList());


    } catch (Exception e) {
      LOG.error(" enable to get acitivities in getActivitiesLedByUser   function ");
    }
    return activities;


  }

  public List<Activity> getActivitiesLedByUserOld(long userID) {
    Project project = projectManager.getProjectById(projectID);
    List<Activity> activities = project.getActivities().stream()
      .filter(c -> c.isActive() && c.getProjectPartnerPerson() != null && c.getActivityStatus() != null
        && c.getActivityStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
        && c.getProjectPartnerPerson().getId().longValue() == userID && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList());
    return activities;

  }

  public List<Institution> getAllInstitutions() {
    return allInstitutions;
  }

  public List<Institution> getAllPPAInstitutions() {
    return allPPAInstitutions;
  }

  public List<RepIndGeographicScope> getAllRepIndGeographicScope() {
    return allRepIndGeographicScope;
  }

  public List<RepIndRegion> getAllRepIndRegions() {
    return allRepIndRegions;
  }

  public List<RepIndPhaseResearchPartnership> getAllRepIndResearchPhases() {
    return allRepIndResearchPhases;
  }


  public List<User> getAllUsers() {
    return allUsers;
  }

  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<LocElement> getCountries() {
    return countries;
  }

  public List<Deliverable> getDeliverablesLedByPartner(Long projectPartnerID) {
    List<Deliverable> deliverablesLeads = new ArrayList<>();
    if (projectPartnerID != null && projectPartnerID != 0) {
      ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(projectPartnerID);

      if (projectPartner != null) {
        /// cgamboa 17/05/2024 getDeliverablesLeadByInstitution was changed by
        /// getDeliverablesLeadByInstitutionAndProject
        List<Deliverable> deliverables = deliverableManager
          // .getDeliverablesLeadByInstitution(projectPartner.getInstitution().getId(), this.getActualPhase().getId());
          .getDeliverablesLeadByInstitutionAndProject(projectPartner.getInstitution().getId(),
            this.getActualPhase().getId(), projectID);
        for (Deliverable deliverable : deliverables) {
          if (deliverable.getProject() != null && deliverable.getProject().getId().equals(projectID)) {
            deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
            if (!deliverablesLeads.contains(deliverable)) {
              if (deliverable.getDeliverableInfo() != null
                && deliverable.getDeliverableInfo().getYear() >= this.getActualPhase().getYear()) {
                if (deliverable.isActive()) {
                  deliverablesLeads.add(deliverable);
                }
              } else {
                if (deliverable.getDeliverableInfo() != null && deliverable.getDeliverableInfo().getStatus()
                  .intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                  if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                    && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()) {

                    if (deliverable.isActive()) {
                      deliverablesLeads.add(deliverable);
                    }
                  }
                }
              }
            }
          }

        }
      }

    }
    return deliverablesLeads;
  }

  /**
   * get deliverables list by user
   * 
   * @author IBD
   * @param userId user id
   * @return deliverables (DTO) list
   */
  public List<DeliverableDTO> getDeliverablesLedByUser(long userID) {
    List<DeliverableDTO> deliverablesLeadsTmp = new ArrayList<>();
    List<Deliverable> deliverablesLeads = new ArrayList<>();
    try {
      // Retrieve deliverables for the specified user and current phase
      List<Deliverable> deliverables =
        deliverableManager.getDeliverablesLeadByUser(userID, this.getActualPhase().getId());

      if (deliverables != null) {
        for (Deliverable deliverable : deliverables) {
          if (deliverable.getProject() != null && deliverable.getProject().getId().equals(projectID)) {
            deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
            if (deliverable.getDeliverableInfo() != null && deliverable.getDeliverableInfo().getStatus() != null
              && (deliverable.getDeliverableInfo().getStatus() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())
                || deliverable.getDeliverableInfo().getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))) {
              if (!deliverablesLeads.contains(deliverable)) {
                if (deliverable.getDeliverableInfo().getYear() >= this.getActualPhase().getYear()) {

                  if (deliverable.isActive()) {
                    deliverablesLeads.add(deliverable);
                  }

                } else {
                  if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                    .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                    if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                      && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()) {
                      if (deliverable.isActive()) {
                        deliverablesLeads.add(deliverable);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Convert Deliverable objects to DeliverableDTO using stream and lambda
      deliverablesLeadsTmp = deliverablesLeads.stream().map(deliverable -> {
        DeliverableDTO dto = new DeliverableDTO();
        dto.setId(deliverable.getId());
        dto.setTitle(deliverable.getDeliverableInfo() != null && deliverable.getDeliverableInfo().getTitle() != null
          ? deliverable.getDeliverableInfo().getTitle() : "");
        return dto;
      }).collect(Collectors.toList());

    } catch (Exception e) {
      LOG.error("Error retrieving deliverables for user " + userID, e);
    }
    return deliverablesLeadsTmp;
  }


  public List<Deliverable> getDeliverablesLedByUserOld(long userID) {
    List<Deliverable> deliverablesLeads = new ArrayList<>();
    List<Deliverable> deliverables =
      // cgamboa 16/05/2024 getDeliverablesLeadByUser was changed by getDeliverablesLeadByUser
      // deliverableManager.getDeliverablesLeadByUser(userID, this.getActualPhase().getId());
      deliverableManager.getDeliverablesLeadByUserAndProject(userID, this.getActualPhase().getId(), projectID);
    if (deliverables != null) {
      for (Deliverable deliverable : deliverables) {
        if (deliverable.getProject() != null && deliverable.getProject().getId().equals(projectID)) {
          deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
          if (deliverable.getDeliverableInfo() != null && deliverable.getDeliverableInfo().getStatus() != null
            && (deliverable.getDeliverableInfo().getStatus() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())
              || deliverable.getDeliverableInfo().getStatus() == Integer
                .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))) {
            if (!deliverablesLeads.contains(deliverable)) {
              if (deliverable.getDeliverableInfo().getYear() >= this.getActualPhase().getYear()) {

                if (deliverable.isActive()) {
                  deliverablesLeads.add(deliverable);
                }
              } else {

                if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                  if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                    && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()) {

                    if (deliverable.isActive()) {
                      deliverablesLeads.add(deliverable);
                    }
                  }
                }
              }
            }
          }
        }

      }
    }
    return deliverablesLeads;

  }

  public int getDeliverablesLedByUserTest(long userID) {
    return 1;
  }


  public List<PartnerDivision> getDivisions() {
    return divisions;
  }

  public List<ProjectInnovation> getInnovationContributingByPartner(Long projectPartnerID) {

    List<ProjectInnovation> innovationContributings = new ArrayList<>();
    if (projectPartnerID != null && projectPartnerID != 0) {
      ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(projectPartnerID);
      if (projectPartner != null && projectPartner.getInstitution() != null) {
        /// cgamboa 16/05/2024 findAll() was changed by findAllByInsitutionAndPhase(long institutionId, long phaseId)
        List<ProjectInnovationCenter> innovationCenters = projectInnovationCenterManager
          .findAllByInsitutionAndPhase(projectPartner.getInstitution().getId(), this.getActualPhase().getId());

        if (innovationCenters != null) {
          innovationCenters = innovationCenters.stream()
            .filter(p -> p != null && p.getPhase() != null && p.getPhase().getId().equals(this.getActualPhase().getId())
              && p.getInstitution() != null
              && p.getInstitution().getId().equals(projectPartner.getInstitution().getId()))
            .collect(Collectors.toList());
        }
        if (innovationCenters != null) {
          for (ProjectInnovationCenter projectInnovationCenter : innovationCenters) {
            if (projectInnovationCenter != null && projectInnovationCenter.getProjectInnovation() != null
              && projectInnovationCenter.getProjectInnovation().getId() != null) {
              ProjectInnovation projectInnovation = new ProjectInnovation();
              projectInnovation = projectInnovationManager
                .getProjectInnovationById(projectInnovationCenter.getProjectInnovation().getId());
              if (projectInnovation != null && projectInnovation.getProject() != null
                && projectInnovation.getProject().getId() != null
                && projectInnovation.getId().equals(project.getId())) {
                projectInnovation
                  .setProjectInnovationInfo(projectInnovation.getProjectInnovationInfo(this.getActualPhase()));
                innovationContributings.add(projectInnovation);
              }
            }
          }
        }
      }

    }
    return innovationContributings;
  }


  public List<InstitutionType> getIntitutionTypes() {
    return intitutionTypes;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public Map<String, String> getPartnerPersonTypes() {
    return partnerPersonTypes;
  }


  public List<ProjectPolicy> getPolicyContributingByPartner(Long projectPartnerID) {
    List<ProjectPolicy> policyContributings = new ArrayList<>();
    if (projectPartnerID != null && projectPartnerID != 0) {
      ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(projectPartnerID);
      if (projectPartner != null && projectPartner.getInstitution() != null) {
        List<ProjectPolicyCenter> policyCenters = projectPolicyCenterManager.findAll().stream()
          .filter(p -> p != null && p.getPhase() != null && p.getPhase().getId().equals(this.getActualPhase().getId())
            && p.getInstitution() != null && p.getInstitution().getId().equals(projectPartner.getInstitution().getId()))
          .collect(Collectors.toList());
        if (policyCenters != null) {
          for (ProjectPolicyCenter projectPolicyCenter : policyCenters) {
            if (projectPolicyCenter != null && projectPolicyCenter.getProjectPolicy() != null
              && projectPolicyCenter.getProjectPolicy().getId() != null) {
              ProjectPolicy projectPolicy = new ProjectPolicy();
              projectPolicy = projectPolicyManager.getProjectPolicyById(projectPolicyCenter.getProjectPolicy().getId());
              if (projectPolicy != null && projectPolicy.getProject() != null
                && projectPolicy.getProject().getId() != null
                && projectPolicy.getProject().getId().equals(project.getId())) {
                projectPolicy.setProjectPolicyInfo(projectPolicy.getProjectPolicyInfo(this.getActualPhase()));
                policyContributings.add(projectPolicy);
              }
            }
          }
        }
      }

    }
    return policyContributings;
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


  public List<ProjectExpectedStudy> getStudyContributingByPartner(Long projectExpectedID) {
    List<ProjectExpectedStudy> studyContributings = new ArrayList<>();
    if (projectExpectedID != null && projectExpectedID != 0) {
      ProjectPartner projectPartner = projectPartnerManager.getProjectPartnerById(projectExpectedID);
      if (projectPartner != null && projectPartner.getInstitution() != null) {
        // cgamboa 16/05/2024 findAll() was changed by findAllByInsituttionAndPhase(long institutionId, long phaseId)
        List<ProjectExpectedStudyCenter> studyCenters = projectExpectedStudyCenterManager
          .findAllByInsituttionAndPhase(projectPartner.getInstitution().getId(), this.getActualPhase().getId());

        if (studyCenters != null) {
          studyCenters = studyCenters.stream()
            .filter(p -> p != null && p.getPhase() != null && p.getPhase().getId().equals(this.getActualPhase().getId())
              && p.getInstitution() != null
              && p.getInstitution().getId().equals(projectPartner.getInstitution().getId()))
            .collect(Collectors.toList());
        }
        if (studyCenters != null) {
          for (ProjectExpectedStudyCenter projectExpectedStudyCenter : studyCenters) {
            if (projectExpectedStudyCenter != null && projectExpectedStudyCenter.getProjectExpectedStudy() != null
              && projectExpectedStudyCenter.getProjectExpectedStudy().getId() != null) {
              ProjectExpectedStudy projectExpectedStudy = new ProjectExpectedStudy();
              projectExpectedStudy = projectExpectedStudyManager
                .getProjectExpectedStudyById(projectExpectedStudyCenter.getProjectExpectedStudy().getId());
              if (projectExpectedStudy != null && projectExpectedStudy.getProject() != null
                && projectExpectedStudy.getProject().getId() != null
                && projectExpectedStudy.getProject().getId().equals(project.getId())) {
                projectExpectedStudy
                  .setProjectExpectedStudyInfo(projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()));
                studyContributings.add(projectExpectedStudy);
              }
            }
          }
        }
      }

    }
    return studyContributings;
  }


  public String getTransaction() {
    return transaction;
  }

  /**
   * This method will validate if the user is deactivated. If so, it will send an email indicating the credentials to
   * access.
   * 
   * @param leader is a PartnerPerson object that could be the leader or the coordinator.
   */
  private void notifyNewUserCreated(User user) {

    if (user != null && user.getId() != null) {
      user = userManager.getUser(user.getId());

      if (!user.isActive()) {
        String toEmail = user.getEmail();
        String ccEmail = "";
        String bbcEmails = this.config.getEmailNotification();
        String subject = this.getText("email.newUser.subject", new String[] {user.getFirstName()});
        // Setting the password
        String password = this.getText("email.outlookPassword");
        if (!user.isCgiarUser()) {
          // Generating a random password.
          password = RandomStringUtils.randomNumeric(6);


        }

        // Building the Email message:
        StringBuilder message = new StringBuilder();
        message.append(this.getText("email.dear", new String[] {user.getFirstName()}));

        // get CRPAdmin contacts
        String crpAdmins = "";
        long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
        Role roleAdmin = roleManager.getRoleById(adminRol);
        List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
          .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
        for (UserRole userRole : userRoles) {
          if (crpAdmins.isEmpty()) {
            crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
          } else {
            crpAdmins +=
              ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
          }
        }

        message.append(this.getText("email.newUser.part1", new String[] {this.getText("email.newUser.listRoles"),
          config.getBaseUrl(), user.getEmail(), password, this.getText("email.support", new String[] {crpAdmins})}));
        message.append(this.getText("email.bye"));

        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("user", user);
        mapUser.put("password", password);
        this.getUsersToActive().add(mapUser);
        // Send UserManual.pdf
        String contentType = "application/pdf";
        String fileName;
        if (this.isAiccra()) {
          fileName = APConstants.AICCRA_PDF_MANUAL_NAME;
        } else {
          fileName = APConstants.MARLO_PDF_MANUAL_NAME;
        }
        byte[] buffer = null;
        InputStream inputStream = null;

        try {
          inputStream = this.getClass().getResourceAsStream("/manual/" + fileName);
          buffer = readFully(inputStream);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }

        // TODO Disable temporally CIAT MARLO send email.
        if (!this.isCenterGlobalUnit()) {
          if (this.validateEmailNotification()) {
            if (buffer != null && fileName != null && contentType != null) {
              sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName,
                true);
            } else {
              sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
            }
          }
        }
      }
    }

  }

  /**
   * This method notify the user that is been assigned as Project Leader/Coordinator for a specific project.
   * 
   * @param userAssigned is the user that is being assigned.
   * @param role is the role (Project Leader or Project Coordinator).
   */
  private void notifyRoleAssigned(User userAssigned, Role role) {

    boolean leaderOrCoordinatorAsigment = false;
    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject =

      globalUnitProjectManager.findByProjectAndGlobalUnitId(project.getId(), loggedCrp.getId());
    userAssigned = userManager.getUser(userAssigned.getId());
    Project project = projectManager.getProjectById(this.projectID);

    // TO will be the new user
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();

      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += ", " + userRole.getUser().getEmail();
      }
    }
    if (!crpAdminsEmail.isEmpty()) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpAdminsEmail;
      } else {
        ccEmail += ", " + crpAdminsEmail;
      }
    }


    // new method
    // Project leader CC emails
    try {
      if (role.getId() == plRole.getId().longValue()) {
        List<CrpProgram> crpPrograms = projectManager.getPrograms(project.getId(),
          ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue(), this.getActualPhase().getId());

        if (crpPrograms != null) {
          if (crpPrograms.size() > 1) {
            LOG.warn("Crp programs should be 1");

            if (crpPrograms.get(0) != null) {
              CrpProgram crpProgram = crpPrograms.get(0);
              for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
                .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
                if (ccEmail.isEmpty()) {
                  ccEmail += crpProgramLeader.getUser().getEmail();
                } else {
                  ccEmail += ", " + crpProgramLeader.getUser().getEmail();
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error("Error getting crpPrograms and crp programs leaders " + e);
    }

    // project coordinator CC emails
    if (role.getId() == pcRole.getId().longValue()) {
      ProjectPartnerPerson projectLeader = project.getLeaderPersonDB(this.getActualPhase());
      if (projectLeader != null && projectLeader.getUser() != null && projectLeader.getUser().getEmail() != null) {
        if (ccEmail == null || ccEmail.isEmpty()) {
          ccEmail = projectLeader.getUser().getEmail();
        } else {
          ccEmail += ", " + projectLeader.getUser().getEmail();
        }
      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // Subject
    String projectRole = null;
    if (role.getId() == plRole.getId()) {
      projectRole = this.getText("email.project.assigned.PL");
    } else {
      projectRole = this.getText("email.project.assigned.PC");
    }
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();

    String projectAcronym = null;
    if (project != null && project.getAcronym() != null) {
      projectAcronym = project.getAcronym();
    } else {
      projectAcronym = "C" + project.getId();
    }
    String subject = this.getText("email.project.assigned.subject", new String[] {projectRole, crp, projectAcronym});


    // message
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.project.assigned",
      new String[] {projectRole, crp, project.getProjecInfoPhase(this.getActualPhase()).getTitle(),
        project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}));
    if (role.getId() == plRole.getId()) {
      message.append(this.getText("email.project.leader.responsabilities"));
    } else {
      message.append(this.getText("email.project.coordinator.responsabilities"));
    }
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    // TODO Disable temporally CIAT MARLO send email.
    if (!this.isCenterGlobalUnit()) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }

    // TIP notification email
    if (((role == plRole) || (role == pcRole)) && this.hasSpecificities(APConstants.TIP_NOTIFICATION_EMAIL_ACTIVE)
      && !userAssigned.isCgiarUser()) {
      this.sendRequestEmailToTIP(userAssigned);
    }
  }

  /**
   * This method notify the the user that he/she stopped contributing to a specific project.
   * 
   * @param userUnassigned is the user that stopped contribution.
   * @param role is the user role that stopped contributing (Project Leader or Project Coordinator).
   */
  private void notifyRoleUnassigned(User userUnassigned, Role role) {
    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject =

      globalUnitProjectManager.findByProjectAndGlobalUnitId(project.getId(), loggedCrp.getId());
    // Send email to the new user and the P&R notification email.
    // TO
    String toEmail = userUnassigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = this.getCurrentUser().getEmail();
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();

      } else {
        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += ", " + userRole.getUser().getEmail();
      }
    }
    if (!crpAdminsEmail.isEmpty()) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpAdminsEmail;
      } else {
        ccEmail += ", " + crpAdminsEmail;
      }
    }

    // new method

    // Project leader CC emails
    if (role.getId() == plRole.getId().longValue()) {
      List<CrpProgram> crpPrograms = projectManager.getPrograms(project.getId(),
        ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue(), this.getActualPhase().getId());

      if (crpPrograms != null) {
        if (crpPrograms.size() > 1) {
          LOG.warn("Crp programs should be 1");
        }
        CrpProgram crpProgram = crpPrograms.get(0);
        for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
          .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
          if (ccEmail.isEmpty()) {
            ccEmail += crpProgramLeader.getUser().getEmail();
          } else {
            ccEmail += ", " + crpProgramLeader.getUser().getEmail();
          }
        }
      }
    }

    // project coordinator CC emails
    if (role.getId() == pcRole.getId().longValue()) {
      ProjectPartnerPerson projectLeader = project.getLeaderPersonDB(this.getActualPhase());
      if (projectLeader != null && projectLeader.getUser() != null && projectLeader.getUser().getEmail() != null) {
        ccEmail += ", " + projectLeader.getUser().getEmail();
      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // subject
    String projectAcronym = null;
    if (project != null && project.getAcronym() != null) {
      projectAcronym = project.getAcronym();
    } else {
      projectAcronym = "C" + project.getId();
    }
    String projectRole = null;
    Project project = projectManager.getProjectById(this.projectID);
    if (role.getId() == plRole.getId()) {
      projectRole = this.getText("email.project.assigned.PL");
    } else {
      projectRole = this.getText("email.project.assigned.PC");
    }
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    String subject = this.getText("email.project.unAssigned.subject", new String[] {projectRole, crp, projectAcronym});


    userUnassigned = userManager.getUser(userUnassigned.getId());

    // message
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userUnassigned.getFirstName()}));

    if (role.getId() == plRole.getId().longValue()) {
      message.append(this.getText("email.project.leader.unAssigned",
        new String[] {projectRole, crp, project.getProjecInfoPhase(this.getActualPhase()).getTitle(),
          project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}));
    } else {
      message.append(this.getText("email.project.coordinator.unAssigned",
        new String[] {projectRole, crp, project.getProjecInfoPhase(this.getActualPhase()).getTitle(),
          project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}));
    }

    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.bye"));

    // TODO Disable temporally CIAT MARLO send email.
    if (!this.isCenterGlobalUnit()) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }


  @Override
  public void prepare() throws Exception {

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);
      if (history != null) {
        project = history;
        project
          .setPartners(project.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (ProjectPartner projectPartner : project.getPartners()) {
          projectPartner.setPartnerPersons(projectPartner.getProjectPartnerPersons().stream()
            .filter(ppp -> ppp.isActive()).collect(Collectors.toList()));
        }
        ProjectPartner leader = project.getLeader();
        if (leader != null) {
          // First we remove the element from the array.
          project.getPartners().remove(leader);
          // then we add it to the first position.
          project.getPartners().add(0, leader);

          // Or you could use a comparator and sort or just order the elements in javascript!
        }

        Collections.sort(project.getPartners(),
          (p1, p2) -> Boolean.compare(this.isPPA(p2.getInstitution()), this.isPPA(p1.getInstitution())));


        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        for (ProjectPartner projectPartner : project.getPartners()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(projectPartner, transaction, specialList,
            "project.partners[" + i + "]", "project", 1));
          int j = 0;
          for (ProjectPartnerPerson partnerPerson : projectPartner.getProjectPartnerPersons()) {
            int[] indexPartners = new int[2];
            indexPartners[0] = i;
            indexPartners[1] = j;
            differences.addAll(historyComparator.getDifferencesList(partnerPerson, transaction, specialList,
              "project.partners[" + i + "].partnerPersons[" + j + "]", "project.projectPartner", 2));
            j++;
          }
          int k = 0;

          for (ProjectPartnerContribution projectPartnerContribution : projectPartner
            .getProjectPartnerContributions()) {
            differences
              .addAll(historyComparator.getDifferencesList(projectPartnerContribution, transaction, specialList,
                "project.partners[" + i + "].partnerContributors[" + k + "]", "project.partnerContributors", 2));
            k++;
          } ;

          List<ProjectPartnerOverall> overalls =
            projectPartner.getProjectPartnerOveralls().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          if (!overalls.isEmpty()) {
            if (!historyComparator
              .getDifferencesList(overalls.get(0), transaction, specialList,
                "project.partners[" + i + "].partnerContributors[" + k + "]", "project.partnerContributors", 2)
              .isEmpty()) {
              if (!differences.contains("project.overall")) {
                differences.add(new HistoryDifference(UUID.randomUUID().toString(), "project.overall", true, "", ""));
              }
            }
          }
          k = 0;
          for (ProjectPartnerPartnership projectPartnerPartnership : projectPartner.getProjectPartnerPartnerships()) {
            specialList.put(APConstants.PROJECT_PARTNER_PARTNERSHIP_COUNTRY_RELATION, "partnershipLocationsIds");
            differences.addAll(historyComparator.getDifferencesList(projectPartnerPartnership, transaction, specialList,
              "project.partners[" + i + "].projectPartnerPartnership[" + k + "]",
              "project.projectPartner.partnerPartnerships", 2));
            k++;
          }

          i++;
        }

        if (this.isLessonsActive()) {
          this.loadLessons(loggedCrp, project);
        }
        if ((project.getProjecInfoPhase(this.getActualPhase())) != null) {
          project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        }

        this.setDifferences(differences);

      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      project = projectManager.getProjectById(projectID);
      if ((project.getProjecInfoPhase(this.getActualPhase())) != null) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
      }
    }


    if (project != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);

        // We load some BD objects, since the draft only keeps IDs and some data is shown with a different labe
        Project projectDb = projectManager.getProjectById(project.getId());
        project.getProjectInfo().setPhase(projectDb.getProjecInfoPhase(this.getActualPhase()).getPhase());

        this.projectPPAPartners = new ArrayList<ProjectPartner>();
        if (project.getPartners() != null) {
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
                  pp.getInstitution().setLocations(pp.getInstitution().getInstitutionsLocations().stream()
                    .filter(c -> c.isActive()).collect(Collectors.toList()));
                }
              }


            }

            if (pp.getSelectedLocations() != null) {

              List<InstitutionLocation> locElements = new ArrayList<>();
              for (InstitutionLocation locElement : pp.getSelectedLocations()) {
                LocElement locElementDB =
                  locationManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

                if (locElementDB != null && pp.getInstitution() != null && pp.getInstitution().getId() != null) {
                  InstitutionLocation institutionLocation = institutionLocationManager
                    .findByLocation(locElementDB.getId(), pp.getInstitution().getId().longValue());
                  locElements.add(institutionLocation);
                }

              }
              pp.getSelectedLocations().clear();
              pp.getSelectedLocations().addAll(locElements);
            }


            if (pp.getPartnerPersons() != null) {
              for (ProjectPartnerPerson projectPartnerPerson : pp.getPartnerPersons()) {

                if (projectPartnerPerson.getUser().getId() != null) {
                  projectPartnerPerson.setUser(userManager.getUser(projectPartnerPerson.getUser().getId()));

                }

                if (projectPartnerPerson.getPartnerDivision() != null
                  && projectPartnerPerson.getPartnerDivision().getId() != null) {
                  projectPartnerPerson.setPartnerDivision(
                    partnerDivisionManager.getPartnerDivisionById(projectPartnerPerson.getPartnerDivision().getId()));
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
            if (pp.getProjectPartnerPartnership() != null) {
              ProjectPartnerPartnership projectPartnerPartnership = pp.getProjectPartnerPartnership();
              // Countries
              if (projectPartnerPartnership.getPartnershipLocationsIsosText() != null) {
                String[] locationsIsos = projectPartnerPartnership.getPartnershipLocationsIsosText().replace("[", "")
                  .replace("]", "").split(",");
                List<String> locations = new ArrayList<>();
                for (String value : Arrays.asList(locationsIsos)) {
                  locations.add(value.trim());
                }
                projectPartnerPartnership.setPartnershipLocationsIsos(locations);
              }

              // Research Phases
              if (projectPartnerPartnership.getResearchPhasesIdsText() != null) {
                String[] researchPhasesIds =
                  projectPartnerPartnership.getResearchPhasesIdsText().replace("[", "").replace("]", "").split(",");
                List<Long> researchPhases = new ArrayList<>();
                for (String value : Arrays.asList(researchPhasesIds)) {
                  researchPhases.add(Long.parseLong(value.trim()));
                }
                projectPartnerPartnership.setResearchPhasesIds(researchPhases);
              }
            }
          }
        }
        this.setDraft(true);
      } else {

        this.setDraft(false);

        if (project.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader()) {
          project.setPartners(project.getProjectPartners().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));

        } else {
          List<ProjectPartner> partnes = new ArrayList<>();
          for (ProjectPartner projectPartner : project.getProjectPartners().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
            Institution inst = institutionManager.getInstitutionById(projectPartner.getInstitution().getId());
            if (!inst.getCrpPpaPartners().stream()
              .filter(insti -> insti.isActive() && insti.getCrp().getId().longValue() == this.getCrpID().longValue())
              .collect(Collectors.toList()).isEmpty()) {
              partnes.add(projectPartner);
            }

          }
          project.setPartners(partnes);

        }
        this.projectPPAPartners = new ArrayList<ProjectPartner>();
        for (ProjectPartner projectPartner : project.getPartners()) {
          projectPartner.setSelectedLocations(new ArrayList<>());
          for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {
            projectPartner.getSelectedLocations().add(projectPartnerLocation.getInstitutionLocation());
          }


          projectPartner.getInstitution().setLocations(projectPartner.getInstitution().getInstitutionsLocations()
            .stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          projectPartner.setPartnerPersons(
            projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

          // Parnership
          if (projectPartner.getProjectPartnerPartnerships() != null) {
            projectPartner.setPartnerPartnerships(new ArrayList<>(projectPartner.getProjectPartnerPartnerships()
              .stream().filter(c -> c.isActive()).collect(Collectors.toList())));

            if (projectPartner.getPartnerPartnerships().size() > 0) {
              projectPartner.setProjectPartnerPartnership(projectPartner.getPartnerPartnerships().get(0));

              // Partnership Locations
              if (projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipLocations() == null) {
                projectPartner.getProjectPartnerPartnership().setPartnershipLocations(new ArrayList<>());
              } else {
                List<ProjectPartnerPartnershipLocation> locations =
                  projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipLocations().stream()
                    .filter(pl -> pl.isActive()).collect(Collectors.toList());
                projectPartner.getProjectPartnerPartnership().setPartnershipLocations(locations);

              }
              if (projectPartner.getProjectPartnerPartnership().getPartnershipLocations() != null) {
                for (ProjectPartnerPartnershipLocation location : projectPartner.getProjectPartnerPartnership()
                  .getPartnershipLocations()) {
                  projectPartner.getProjectPartnerPartnership().getPartnershipLocationsIsos()
                    .add(location.getLocation().getIsoAlpha2());
                }
              }

              // Partnership Research Phases
              if (projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipResearchPhases() == null) {
                projectPartner.getProjectPartnerPartnership().setPartnershipResearchPhases(new ArrayList<>());
              } else {
                List<ProjectPartnerPartnershipResearchPhase> partnershipResearchPhases =
                  projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipResearchPhases().stream()
                    .filter(rf -> rf.isActive()).collect(Collectors.toList());
                projectPartner.getProjectPartnerPartnership().setPartnershipResearchPhases(partnershipResearchPhases);
              }

              if (projectPartner.getProjectPartnerPartnership().getPartnershipResearchPhases() != null) {
                for (ProjectPartnerPartnershipResearchPhase partnershipResearchPhase : projectPartner
                  .getProjectPartnerPartnership().getPartnershipResearchPhases()) {
                  projectPartner.getProjectPartnerPartnership().getResearchPhasesIds()
                    .add(partnershipResearchPhase.getRepIndPhaseResearchPartnership().getId());
                }
              }

            } else {
              projectPartner.setProjectPartnerPartnership(new ProjectPartnerPartnership());
            }

          }

          if (this.isPPA(projectPartner.getInstitution())) {
            this.projectPPAPartners.add(projectPartner);

          }

          List<ProjectPartnerContribution> contributors = new ArrayList<>();

          List<ProjectPartnerContribution> partnerContributions = projectPartner.getProjectPartnerContributions()
            .stream().filter(c -> c.isActive() && c.getProjectPartner().isActive()).collect(Collectors.toList());
          for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
            contributors.add(projectPartnerContribution);
          }
          projectPartner.setPartnerContributors(contributors);

          Institution institution = projectPartner.getInstitution();
          if (institution != null) {
            List<InstitutionLocation> institutionLocations = new ArrayList<>();
            institutionLocations.addAll(institution.getLocations());
            for (InstitutionLocation institutionLocation : institutionLocations) {
              if (projectPartner.getSelectedLocations() != null) {
                if (projectPartner.getSelectedLocations().contains(institutionLocation)) {
                  institution.getLocations().remove(institutionLocation);

                }
              }

            }
          }
        }

        if (this.isLessonsActive()) {
          this.loadLessons(loggedCrp, project);
        }


      }
    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_PARTNER_BASE_PERMISSION, params));
    plRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_PL_ROLE)));
    pcRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_PC_ROLE)));

    // Getting the list of all institutions
    Project projectDb = projectManager.getProjectById(project.getId());
    Boolean isLeaderEdit = projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader();
    Boolean isAdministrative = projectDb.getProjecInfoPhase(this.getActualPhase()).getAdministrative();
    project.getProjectInfo().setProjectEditLeader(isLeaderEdit);
    project.getProjectInfo().setAdministrative(isAdministrative);

    if (!isLeaderEdit) {
      allInstitutions = new ArrayList<>();
      try {
        /// cgamboa 15/05/2024 crpPpaPartnerManager.findAll().stream()
        // .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive()
        // && c.getPhase().equals(this.getActualPhase()) was changed by CrpPpaPartnerList
        List<CrpPpaPartner> CrpPpaPartnerList =
          crpPpaPartnerManager.findByCrpAndPhaseComplete(loggedCrp.getId().longValue(), this.getActualPhase().getId());
        for (CrpPpaPartner crpPpaPartner : CrpPpaPartnerList.stream().collect(Collectors.toList())) {
          allInstitutions.add(crpPpaPartner.getInstitution());
        }


      } catch (Exception e) {
        LOG.error(" unable to get allInstitutions ");
      }

    } else {
      allInstitutions = institutionManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    }
    allInstitutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    // Getting the list of all PPA institutions
    allPPAInstitutions = new ArrayList<>();

    try {
      List<CrpPpaPartner> CrpPpaPartnerList =
        crpPpaPartnerManager.findByCrpAndPhaseComplete(loggedCrp.getId().longValue(), this.getActualPhase().getId());

      /// cgamboa 15/05/2024 crpPpaPartnerManager.findAll().stream()
      // .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive()
      // && c.getPhase().equals(this.getActualPhase()) was changed by CrpPpaPartnerList
      for (CrpPpaPartner crpPpaPartner : CrpPpaPartnerList.stream().collect(Collectors.toList())) {
        allPPAInstitutions.add(crpPpaPartner.getInstitution());
      }
      allPPAInstitutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    } catch (Exception e) {
      LOG.error(" unable to get allPPAInstitutions ");
    }


    /// cgamboa 15/05/2024 locationManager.findAll .stream().filter(c -> c.isActive() && c.getLocElementType().getId()
    /// == 2)function was changed by findAllToCountries
    // Getting all the countries
    countries = locationManager.findAllToCountries().stream().collect(Collectors.toList());


    // Getting all partner types
    intitutionTypes = institutionTypeManager.findAll();
    if (this.isReportingActive()) {
      allRepIndResearchPhases = repIndPhaseResearchPartnershipManager.findAll();
      allRepIndGeographicScope = repIndGeographicScopeManager.findAll();
      allRepIndRegions = repIndRegionManager.findAll();
    }

    // Setup partner divisions
    divisions = new ArrayList<>(
      partnerDivisionManager.findAll().stream().filter(pd -> pd.isActive()).collect(Collectors.toList()));

    ProjectPartner leader = project.getLeader();
    if (leader != null) {
      // First we remove the element from the array.
      project.getPartners().remove(leader);
      // then we add it to the first position.
      project.getPartners().add(0, leader);
    }


    if (project.getPartners() != null) {
      Collections.sort(project.getPartners(),
        (p1, p2) -> Boolean.compare(this.isPPA(p2.getInstitution()), this.isPPA(p1.getInstitution())));
    }

    partnerPersonTypes = new HashMap<>();
    partnerPersonTypes.put(APConstants.PROJECT_PARTNER_CP, this.getText("projectPartners.types.CP"));

    if (this.hasPermission("leader")) {
      partnerPersonTypes.put(APConstants.PROJECT_PARTNER_PL, this.getText("projectPartners.types.PL"));
    }
    if (this.hasPermission("coordinator")) {
      partnerPersonTypes.put(APConstants.PROJECT_PARTNER_PC, this.getText("projectPartners.types.PC"));
    }


    if (this.isHttpPost()) {
      project.getPartners().clear();
    }

    this.setCustomDataToPartnerShipPerson(project);


  }

  /**
   * Delete projectPartner if it is not in the list of partners sent back from the UI.
   * 
   * @param previouslyEnteredPartner
   */
  private void removeDeletedPartners(ProjectPartner previouslyEnteredPartner) {
    if (project.getPartners() == null || !project.getPartners().contains(previouslyEnteredPartner)) {
      Project previousProject = projectManager.getProjectById(project.getId());
      ProjectPartnerPerson previousLeader = previousProject.getLeaderPerson(this.getActualPhase());
      List<ProjectPartnerPerson> previousCoordinators = previousProject.getCoordinatorPersons(this.getActualPhase());

      for (ProjectPartner previousPartner : previousProject.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
        if (project.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader()) {

          this.removeProjectIndicatorsCenter(previouslyEnteredPartner);
          projectPartnerManager.deleteProjectPartner(previouslyEnteredPartner.getId());

        } else {

          // Check to see if the user has priviliges for this crp
          Institution inst = institutionManager.getInstitutionById(previouslyEnteredPartner.getInstitution().getId());
          if (!inst.getCrpPpaPartners().stream()
            .filter(insti -> insti.isActive() && insti.getCrp().getId().longValue() == this.getCrpID().longValue())
            .collect(Collectors.toList()).isEmpty()) {
            this.removeProjectIndicatorsCenter(previouslyEnteredPartner);
            projectPartnerManager.deleteProjectPartner(previouslyEnteredPartner.getId());
          }
        }
      }
    }
  }


  private void removeProjectExpectedEstudiesCenter(ProjectPartner previouslyEnteredPartner) {
    Project projectTemp = projectManager.getProjectById(projectID);
    Institution institution = previouslyEnteredPartner.getInstitution();
    if (institution != null && institution.getId() != null
      && (institution.isPPA(this.getActualPhase().getCrp().getId(), this.getActualPhase())
        || institution.getInstitutionType().getId().longValue() == APConstants.INSTITUTION_CGIAR_CENTER_TYPE)) {
      List<ProjectExpectedStudy> projectStudiesList =
        projectTemp.getProjectExpectedStudies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (ProjectExpectedStudy projectStudy : projectStudiesList) {
        ProjectExpectedStudy ExpectedStudyTemp =
          projectExpectedStudyManager.getProjectExpectedStudyById(projectStudy.getId());
        List<ProjectExpectedStudyCenter> projectExpectedStudyCenterList =
          ExpectedStudyTemp.getProjectExpectedStudyCenters().stream()
            .filter(o -> o.isActive() && o != null && o.getPhase().getId().equals(this.getActualPhase().getId()))
            .collect(Collectors.toList());
        for (ProjectExpectedStudyCenter projectExpectedStudyCenter : projectExpectedStudyCenterList) {
          ProjectExpectedStudyCenter projectExpectedStudyCenterTemp =
            projectExpectedStudyCenterManager.getProjectExpectedStudyCenterById(projectExpectedStudyCenter.getId());
          if (projectExpectedStudyCenterTemp != null) {
            if (projectExpectedStudyCenterTemp.getInstitution().getId().equals(institution.getId())) {
              projectExpectedStudyCenterManager
                .deleteProjectExpectedStudyCenter(projectExpectedStudyCenterTemp.getId());
            }
          }
        }
      }
    }
  }

  private void removeProjectIndicatorsCenter(ProjectPartner previouslyEnteredPartner) {
    this.removeProjectExpectedEstudiesCenter(previouslyEnteredPartner);
    this.removeProjectInnovatiosCenter(previouslyEnteredPartner);
    this.removeProjectPoliciesCenter(previouslyEnteredPartner);
  }

  private void removeProjectInnovatiosCenter(ProjectPartner previouslyEnteredPartner) {
    Project projectTemp = projectManager.getProjectById(projectID);
    Institution institution = previouslyEnteredPartner.getInstitution();
    if (institution != null && institution.getId() != null
      && (institution.isPPA(this.getActualPhase().getCrp().getId(), this.getActualPhase())
        || institution.getInstitutionType().getId().longValue() == APConstants.INSTITUTION_CGIAR_CENTER_TYPE)) {
      List<ProjectInnovation> projectInnovationList =
        projectTemp.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (ProjectInnovation projectInnovation : projectInnovationList) {
        ProjectInnovation innovationTemp = projectInnovationManager.getProjectInnovationById(projectInnovation.getId());
        List<ProjectInnovationCenter> projectInnovationCentersList =
          innovationTemp.getProjectInnovationCenters().stream()
            .filter(
              o -> o.isActive() && o.getId() != null && o.getPhase().getId().equals(this.getActualPhase().getId()))
            .collect(Collectors.toList());
        for (ProjectInnovationCenter projectInnovationCenter : projectInnovationCentersList) {
          ProjectInnovationCenter projectPolicyCenterTemp =
            projectInnovationCenterManager.getProjectInnovationCenterById(projectInnovationCenter.getId());
          if (projectPolicyCenterTemp != null) {
            if (projectPolicyCenterTemp.getInstitution().getId().equals(institution.getId())) {
              projectInnovationCenterManager.deleteProjectInnovationCenter(projectPolicyCenterTemp.getId());
            }
          }
        }
      }
    }
  }

  private void removeProjectPartnerPersons(ProjectPartner projectPartnerClient, ProjectPartner projectPartnerDB) {
    for (ProjectPartnerPerson partnerPerson : projectPartnerDB.getProjectPartnerPersons()) {
      if (projectPartnerClient.getPartnerPersons() == null
        || !projectPartnerClient.getPartnerPersons().contains(partnerPerson)) {
        // Disable delete of deliverable partnership
        // for (DeliverablePartnership deliverablePartnership : partnerPerson.getDeliverablePartnerships().stream()
        // .filter(c -> c.isActive()).collect(Collectors.toList())) {
        // deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
        // }
        projectPartnerPersonManager.deleteProjectPartnerPerson(partnerPerson.getId());
      }
    }
  }

  private void removeProjectPoliciesCenter(ProjectPartner previouslyEnteredPartner) {
    Project projectTemp = projectManager.getProjectById(projectID);
    Institution institution = previouslyEnteredPartner.getInstitution();
    if (institution != null && institution.getId() != null
      && (institution.isPPA(this.getActualPhase().getCrp().getId(), this.getActualPhase())
        || institution.getInstitutionType().getId().longValue() == APConstants.INSTITUTION_CGIAR_CENTER_TYPE)) {
      List<ProjectPolicy> projectPolicyList =
        projectTemp.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (ProjectPolicy projectPolicy : projectPolicyList) {
        ProjectPolicy policyTemp = projectPolicyManager.getProjectPolicyById(projectPolicy.getId());
        List<ProjectPolicyCenter> projectPolicyCentersList = policyTemp.getProjectPolicyCenters().stream()
          .filter(o -> o.isActive() && o.getId() != null && o.getPhase().getId().equals(this.getActualPhase().getId()))
          .collect(Collectors.toList());
        for (ProjectPolicyCenter projectPolicyCenter : projectPolicyCentersList) {
          ProjectPolicyCenter projectPolicyCenterTemp =
            projectPolicyCenterManager.getProjectPolicyCenterById(projectPolicyCenter.getId());
          if (projectPolicyCenterTemp != null) {
            if (projectPolicyCenterTemp.getInstitution().getId().equals(institution.getId())) {
              projectPolicyCenterManager.deleteProjectPolicyCenter(projectPolicyCenterTemp.getId());
            }
          }
        }
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      this.setUsersToActive(new ArrayList<>());

      Project projectDB = projectManager.getProjectById(projectID);
      List<ProjectPartnerPerson> previousCoordinators = projectDB.getCoordinatorPersonsDB(this.getActualPhase());
      ProjectPartnerPerson previousLeader = projectDB.getLeaderPersonDB(this.getActualPhase());

      List<ProjectPartner> partnersDB = projectDB.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


      for (ProjectPartner projectPartnerDB : partnersDB) {
        if (!this.isCenterGlobalUnit()) {
          // Delete CIAT (Center) Shared Project Relation
          List<GlobalUnit> centerIns = new ArrayList<>();
          centerIns = projectPartnerDB.getInstitution().getGlobalUnits().stream()
            .filter(i -> i.getGlobalUnitType().getId().equals(4) && i.isActive()).collect(Collectors.toList());

          if (!centerIns.isEmpty()) {
            for (GlobalUnit globalUnit : centerIns) {
              GlobalUnitProject globalUnitProject =
                globalUnitProjectManager.findByProjectAndGlobalUnitId(projectDB.getId(), globalUnit.getId());

              if (globalUnitProject != null && !globalUnitProject.isOrigin()) {
                globalUnitProjectManager.deleteGlobalUnitProject(globalUnitProject.getId());
              }
            }
          }
          //
        }
        this.removeDeletedPartners(projectPartnerDB);
      }
      if (project.getPartners() != null) {

        // Looping through the UI partner list
        for (ProjectPartner projectPartnerClient : project.getPartners()) {
          ProjectPartner projectPartnerDB = null;
          if (projectPartnerClient.getId() == null) {

            projectPartnerClient.setProject(project);
            projectPartnerClient.setPhase(this.getActualPhase());
            projectPartnerDB = projectPartnerManager.saveProjectPartner(projectPartnerClient);

            if (!this.isCenterGlobalUnit()) {
              // Shared project if the partner is a Center
              List<GlobalUnit> centerIns = new ArrayList<>();
              centerIns = projectPartnerDB.getInstitution().getGlobalUnits().stream()
                .filter(i -> i.getGlobalUnitType().getId().equals(4) && i.isActive()).collect(Collectors.toList());

              if (!centerIns.isEmpty()) {

                GlobalUnit centerGlobalUnit = centerIns.get(0);

                GlobalUnitProject globalUnitProject =
                  globalUnitProjectManager.findByProjectAndGlobalUnitId(projectDB.getId(), centerGlobalUnit.getId());

                if (globalUnitProject == null) {
                  // Setting the Global Unit Project
                  GlobalUnitProject globalUnitProjectNew = new GlobalUnitProject();
                  globalUnitProjectNew.setProject(project);
                  globalUnitProjectNew.setGlobalUnit(loggedCrp);
                  globalUnitProjectNew.setOrigin(false);
                  globalUnitProjectManager.saveGlobalUnitProject(globalUnitProjectNew);
                }

              }
            }

          } else {
            projectPartnerDB = projectPartnerManager.getProjectPartnerById(projectPartnerClient.getId());
            projectPartnerDB.setProject(project);
            projectPartnerDB.setResponsibilities(projectPartnerClient.getResponsibilities());
            projectPartnerDB.setPhase(projectPartnerDB.getPhase());
            projectPartnerDB.setPartnerPersons(projectPartnerClient.getPartnerPersons());
            projectPartnerDB.setSelectedLocations(projectPartnerClient.getSelectedLocations());
            projectPartnerDB.setSubDepartment(projectPartnerClient.getSubDepartment());
            projectPartnerDB.setPartnerContributors(projectPartnerDB.getPartnerContributors());
            try {
              projectPartnerDB = projectPartnerManager.saveProjectPartner(projectPartnerDB);
            } catch (Exception e) {
              LOG.warn("Error saving projectPartnerManager.saveProjectPartner " + e);
            }

            if (!this.isCenterGlobalUnit()) {
              // Shared project if the partner is a Center
              List<GlobalUnit> centerIns = new ArrayList<>();
              centerIns = projectPartnerDB.getInstitution().getGlobalUnits().stream()
                .filter(i -> i.getGlobalUnitType().getId().equals(4) && i.isActive()).collect(Collectors.toList());

              if (!centerIns.isEmpty()) {

                GlobalUnit centerGlobalUnit = centerIns.get(0);

                GlobalUnitProject globalUnitProject =
                  globalUnitProjectManager.findByProjectAndGlobalUnitId(projectDB.getId(), centerGlobalUnit.getId());

                if (globalUnitProject == null) {
                  // Setting the Global Unit Project
                  GlobalUnitProject globalUnitProjectNew = new GlobalUnitProject();
                  globalUnitProjectNew.setProject(project);
                  globalUnitProjectNew.setGlobalUnit(loggedCrp);
                  globalUnitProjectNew.setOrigin(false);
                  globalUnitProjectManager.saveGlobalUnitProject(globalUnitProjectNew);
                }

              }
            }
          }


          this.removeProjectPartnerPersons(projectPartnerClient, projectPartnerDB);
          this.saveProjectPartnerPersons(projectPartnerClient, projectPartnerDB);
          this.saveProjectPartnerContributions(projectPartnerClient, projectPartnerDB);
          this.saveLocations(projectPartnerClient, projectPartnerDB);
          this.saveProjectPartnership(projectPartnerClient, projectPartnerDB);
          // This is to add projectPartner to generate correct auditlog.
          projectPartnerDB
            .setInstitution(institutionManager.getInstitutionById(projectPartnerDB.getInstitution().getId()));
          projectDB.getProjectPartners().add(projectPartnerDB);

        }
      }
      if (this.isLessonsActive() && this.isReportingActive()) {
        this.saveLessons(loggedCrp, project);
      }

      ProjectPartnerPerson leader = project.getLeaderPerson(this.getActualPhase());
      // Notify user if the project leader was created.

      this.updateRoles(previousLeader, leader, plRole);


      this.updateRoles(previousCoordinators, project.getCoordinatorPersons(this.getActualPhase()), pcRole);
      // project = projectManager.getProjectById(projectID);

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_PARTNERS_RELATION);
      relationsName.add(APConstants.PROJECT_LESSONS_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);

      if (this.isReportingActive() && projectDB.getProjectInfo() != null && project.getProjectInfo() != null
        && project.getProjectInfo().getPartnerOverall() != null) {
        projectDB.getProjectInfo().setPartnerOverall(project.getProjectInfo().getPartnerOverall());
      }

      /**
       * The following is required because we need to update something on the @Project if we want a row
       * created in the auditlog table.
       */
      this.setModificationJustification(projectDB);
      projectManager.saveProject(projectDB, this.getActionName(), relationsName, this.getActualPhase());

      this.addUsers();
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }
    }
    return NOT_AUTHORIZED;

  }

  /**
   * @param partner - the projectPartner edited in the UI
   */
  public void saveLocations(ProjectPartner projectPartnerClient, ProjectPartner projectPartnerDB) {

    /**
     * This is a small optimization to return the locations pre-fetched rather than get them one by one.
     */


    List<ProjectPartnerLocation> projectPartnerLocationsDB =
      projectPartnerDB.getProjectPartnerLocations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (ProjectPartnerLocation projectPartnerLocationDB : projectPartnerLocationsDB) {
      String isoAlpha2 = projectPartnerLocationDB.getInstitutionLocation().getLocElement().getIsoAlpha2();
      // Check to see if an element in the collection has the same isoAplha2 code
      if (projectPartnerClient.getSelectedLocations().stream()
        .filter(c -> c.getLocElement().getIsoAlpha2().equals(isoAlpha2)).collect(Collectors.toList()).isEmpty()) {
        // The location does not exist anymore so delete it.
        LOG.debug("Deleting : " + projectPartnerLocationDB);
        projectPartnerLocationManager.deleteProjectPartnerLocation(projectPartnerLocationDB.getId());
      }
    }
    for (InstitutionLocation updatedInstitutionLocationClient : projectPartnerClient.getSelectedLocations()) {
      String isoAlpha2 = updatedInstitutionLocationClient.getLocElement().getIsoAlpha2();
      // Check to see if the location is already saved by comparing the isoAplpha2 codes.
      if (projectPartnerLocationsDB.stream()
        .filter(c -> c.isActive() && isoAlpha2.equals(c.getInstitutionLocation().getLocElement().getIsoAlpha2()))
        .collect(Collectors.toList()).isEmpty()) {
        LocElement locElement =
          locationManager.getLocElementByISOCode(updatedInstitutionLocationClient.getLocElement().getIsoAlpha2());
        InstitutionLocation institutionLocation =
          institutionLocationManager.findByLocation(locElement.getId(), projectPartnerClient.getInstitution().getId());
        ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();
        partnerLocation.setInstitutionLocation(institutionLocation);
        partnerLocation.setProjectPartner(projectPartnerDB);
        partnerLocation = projectPartnerLocationManager.saveProjectPartnerLocation(partnerLocation);
        LOG.debug("Saving : " + partnerLocation);
        // This is to add projectPartnerLocation to generate correct auditlog.
        projectPartnerDB.getProjectPartnerLocations().add(partnerLocation);
      }
    }


  }


  private ProjectPartner saveProjectPartner(ProjectPartner projectPartnerClient, Project projectDB) {
    if (projectPartnerClient.getId() == null) {
      // New entity
      projectPartnerClient.setProject(projectDB);

      projectPartnerClient = projectPartnerManager.saveProjectPartner(projectPartnerClient);

    } else {
      // Existing entity
      ProjectPartner projectPartnerDB = projectPartnerManager.getProjectPartnerById(projectPartnerClient.getId());
      projectPartnerDB.setProject(projectDB);
      projectPartnerDB.setResponsibilities(projectPartnerClient.getResponsibilities());
      projectPartnerDB.setSubDepartment(projectPartnerClient.getSubDepartment());
      projectPartnerDB = projectPartnerManager.saveProjectPartner(projectPartnerDB);

      return projectPartnerDB;
    }
    return projectPartnerClient;
  }

  private void saveProjectPartnerContributions(ProjectPartner projectPartnerClient, ProjectPartner projectPersonDB) {

    List<ProjectPartnerContribution> partnerContributions =
      projectPersonDB.getProjectPartnerContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    for (ProjectPartnerContribution partnerContribution : partnerContributions) {
      if (projectPartnerClient.getPartnerContributors() == null
        || !projectPartnerClient.getPartnerContributors().contains(partnerContribution)) {
        projectPartnerContributionManager.deleteProjectPartnerContribution(partnerContribution.getId());
      }
    }
    if (projectPartnerClient.getPartnerContributors() != null) {

      for (ProjectPartnerContribution partnerContributionClient : projectPartnerClient.getPartnerContributors()) {
        /**
         * Do nothing if the id is notn ull as we do not have the ability to update any details for the
         * PartnerContributors
         */
        if (partnerContributionClient.getId() == null) {
          partnerContributionClient.setProjectPartner(projectPartnerClient);
          // This looks like we are setting the partnerContribution to the first PPA partner???
          if (partnerContributionClient.getProjectPartnerContributor().getId() == null) {
            Long institutionId = partnerContributionClient.getProjectPartnerContributor().getInstitution().getId();
            List<ProjectPartner> partenerContributor = project.getPartners().stream()
              .filter(c -> c.getInstitution().getId().equals(institutionId)).collect(Collectors.toList());
            if (!partenerContributor.isEmpty()) {
              partnerContributionClient.getProjectPartnerContributor().setId(partenerContributor.get(0).getId());
              LOG.debug("User didn't select a ProjectPartnerContributor for projectPartner : " + projectPersonDB.getId()
                + ", setting the projectPartnerContributor to projectPartner with id = "
                + partenerContributor.get(0).getId());
            }

          }
          partnerContributionClient =
            projectPartnerContributionManager.saveProjectPartnerContribution(partnerContributionClient);
          // This is to add projectPartnerContribution to generate correct auditlog.
          projectPersonDB.getProjectPartnerContributions().add(partnerContributionClient);

        }
      }
    }
  }

  private ProjectPartnerPerson saveProjectPartnerPerson(ProjectPartner projectPartnerDB,
    ProjectPartnerPerson partnerPersonClient) {
    if (partnerPersonClient.getUser() == null
      || (partnerPersonClient.getUser().getId() == null || partnerPersonClient.getUser().getId().longValue() == -1)) {
      partnerPersonClient.setUser(null);
      return partnerPersonClient;
    } else {

      if (partnerPersonClient.getId() == null) {
        partnerPersonClient.setProjectPartner(projectPartnerDB);
        if (partnerPersonClient.getUser() == null || (partnerPersonClient.getUser().getId() == null
          || partnerPersonClient.getUser().getId().longValue() == -1)) {
          partnerPersonClient.setUser(null);
        } else {
          partnerPersonClient.setUser(userManager.getUser(partnerPersonClient.getUser().getId()));
        }
        if (partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PL)
          || partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PC)) {
          this.notifyNewUserCreated(partnerPersonClient.getUser());
        }

        if (partnerPersonClient.getPartnerDivision() != null
          && partnerPersonClient.getPartnerDivision().getId() != null) {
          partnerPersonClient.setPartnerDivision(
            partnerDivisionManager.getPartnerDivisionById(partnerPersonClient.getPartnerDivision().getId()));
        }
        partnerPersonClient = projectPartnerPersonManager.saveProjectPartnerPerson(partnerPersonClient);

      } else {

        ProjectPartnerPerson dbPerson =
          projectPartnerPersonManager.getProjectPartnerPersonById(partnerPersonClient.getId());
        dbPerson.setContactType(partnerPersonClient.getContactType());
        if (partnerPersonClient.getUser() == null || (partnerPersonClient.getUser().getId() == null
          || partnerPersonClient.getUser().getId().longValue() == -1)) {
          dbPerson.setUser(null);
        } else {
          dbPerson.setUser(userManager.getUser(partnerPersonClient.getUser().getId()));
        }
        if (dbPerson.getUser() != null && partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PL)
          || partnerPersonClient.getContactType().equals(APConstants.PROJECT_PARTNER_PC)) {
          this.notifyNewUserCreated(dbPerson.getUser());
        }
        if (partnerPersonClient.getPartnerDivision() != null
          && partnerPersonClient.getPartnerDivision().getId() != null) {
          dbPerson.setPartnerDivision(
            partnerDivisionManager.getPartnerDivisionById(partnerPersonClient.getPartnerDivision().getId()));
        }
        dbPerson = projectPartnerPersonManager.saveProjectPartnerPerson(dbPerson);
        return dbPerson;
      }
    }

    return partnerPersonClient;
  }

  /**
   * @param projectPartnerClient - the projectPartner edited in the UI
   * @param projectPartnerDB - the projectPartner entity retrieved from the database.
   */
  private void saveProjectPartnerPersons(ProjectPartner projectPartnerClient, ProjectPartner projectPartnerDB) {
    if (projectPartnerClient.getPartnerPersons() != null) {
      for (ProjectPartnerPerson partnerPersonClient : projectPartnerClient.getPartnerPersons()) {
        if (partnerPersonClient.getUser() != null && partnerPersonClient.getUser().getId() != null) {
          ProjectPartnerPerson projectPartnerPersonDB =
            this.saveProjectPartnerPerson(projectPartnerDB, partnerPersonClient);
          // This is to add projectPartnerPerson to generate correct auditlog.
          projectPartnerDB.getProjectPartnerPersons().add(projectPartnerPersonDB);
        }
      }

      for (ProjectPartnerPerson partnerPerson : projectPartnerClient.getPartnerPersons()) {

        if (partnerPerson.getUser() != null
          && (partnerPerson.getUser().getId() != null && partnerPerson.getUser().getId().longValue() != -1)) {

          User userDB = userManager.getUser(partnerPerson.getUser().getId());
          // Not sure what we are doing here - creating a new CrpUser for some reason?
          if (userDB.getCrpUsers().stream().filter(c -> c.getCrp().getId().equals(loggedCrp.getId()))
            .collect(Collectors.toList()).isEmpty()) {
            CrpUser crpUser = new CrpUser();
            crpUser.setUser(userDB);
            crpUser.setCrp(loggedCrp);
            crpUserManager.saveCrpUser(crpUser);

          }
        }
      }
    }
  }

  private void saveProjectPartnership(ProjectPartner projectPartnerClient, ProjectPartner projectPartnerDB) {
    if (projectPartnerClient.getProjectPartnerPartnership() != null) {
      ProjectPartnerPartnership partnershipClient = projectPartnerClient.getProjectPartnerPartnership();
      ProjectPartnerPartnership partnershipUpdate = new ProjectPartnerPartnership();

      if (partnershipClient.getId() != null && partnershipClient.getId() != -1) {
        partnershipUpdate =
          projectPartnerPartnershipManager.getProjectPartnerPartnershipById(partnershipClient.getId());
      } else {
        partnershipUpdate.setProjectPartner(projectPartnerClient);
      }

      partnershipUpdate.setMainArea(partnershipClient.getMainArea());

      // Save to avoid null exception in relation with partnership_locations
      if (partnershipUpdate.getId() == null || partnershipUpdate.getId() == -1) {
        projectPartnerPartnershipManager.saveProjectPartnerPartnership(partnershipUpdate);
      }

      // Partnership Phases Save and Delete
      List<ProjectPartnerPartnershipResearchPhase> partnershipResearchPhasesDB = new ArrayList<>();
      if (partnershipClient.getId() != null && partnershipClient.getId() != -1) {
        partnershipResearchPhasesDB = projectPartnerPartnershipResearchPhaseManager
          .findParnershipResearchPhaseByPartnership(partnershipClient.getId());
      }
      if (partnershipResearchPhasesDB == null) {
        partnershipResearchPhasesDB = new ArrayList<>();
      }

      if (partnershipClient.getResearchPhasesIds() != null && !partnershipClient.getResearchPhasesIds().isEmpty()) {
        List<ProjectPartnerPartnershipResearchPhase> partnershipResearchClienteList = new ArrayList<>();
        for (Long researchPhasesIds : partnershipClient.getResearchPhasesIds()) {
          ProjectPartnerPartnershipResearchPhase partnershipResearchPhaseSave =
            new ProjectPartnerPartnershipResearchPhase();
          partnershipResearchPhaseSave.setRepIndPhaseResearchPartnership(
            repIndPhaseResearchPartnershipManager.getRepIndPhaseResearchPartnershipById(researchPhasesIds));
          partnershipResearchPhaseSave.setProjectPartnerPartnership(partnershipUpdate);
          if (!partnershipResearchPhasesDB.contains(partnershipResearchPhaseSave)) {
            projectPartnerPartnershipResearchPhaseManager
              .saveProjectPartnerPartnershipResearchPhase(partnershipResearchPhaseSave);
            partnershipUpdate.getProjectPartnerPartnershipResearchPhases().add(partnershipResearchPhaseSave);
          }
          partnershipResearchClienteList.add(partnershipResearchPhaseSave);
        }
        for (ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhaseDB : partnershipResearchPhasesDB) {
          if (!partnershipResearchClienteList.contains(projectPartnerPartnershipResearchPhaseDB)) {
            projectPartnerPartnershipResearchPhaseManager
              .deleteProjectPartnerPartnershipResearchPhase(projectPartnerPartnershipResearchPhaseDB.getId());
          }
        }
      } else {
        // Delete DB Research Phases
        for (ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase : partnershipResearchPhasesDB) {
          projectPartnerPartnershipResearchPhaseManager
            .deleteProjectPartnerPartnershipResearchPhase(projectPartnerPartnershipResearchPhase.getId());
        }
      }


      // Partnership Locations
      List<ProjectPartnerPartnershipLocation> locationsDB = new ArrayList<>();
      if (partnershipClient.getId() != null && partnershipClient.getId() != -1) {
        locationsDB =
          projectPartnerPartnershipLocationManager.findParnershipLocationByPartnership(partnershipClient.getId());
      }
      if (partnershipClient.getGeographicScope() != null && partnershipClient.getGeographicScope().getId() != -1) {
        partnershipUpdate.setGeographicScope(partnershipClient.getGeographicScope());
        RepIndGeographicScope repIndGeographicScope =
          repIndGeographicScopeManager.getRepIndGeographicScopeById(partnershipUpdate.getGeographicScope().getId());

        // Global
        if (repIndGeographicScope.getId().equals(this.getReportingIndGeographicScopeGlobal())) {

          partnershipUpdate.setRegion(null);
          this.deletePartnershipLocations(locationsDB);

        } else
        // Regional
        if (repIndGeographicScope.getId().equals(this.getReportingIndGeographicScopeRegional())) {

          if (partnershipClient.getRegion() != null && partnershipClient.getRegion().getId() != -1) {
            partnershipUpdate.setRegion(partnershipClient.getRegion());
          } else {
            partnershipUpdate.setRegion(null);
          }
          this.deletePartnershipLocations(locationsDB);

        } else {
          // Multi-national || National || Sub-national
          // Save Locations

          if (partnershipClient.getPartnershipLocationsIsos() != null
            || !partnershipClient.getPartnershipLocationsIsos().isEmpty()) {
            if (locationsDB == null) {
              locationsDB = new ArrayList<>();
            }

            List<ProjectPartnerPartnershipLocation> locationsSave = new ArrayList<>();
            for (String locationIsoAlpha2 : partnershipClient.getPartnershipLocationsIsos()) {
              ProjectPartnerPartnershipLocation locationPartnership = new ProjectPartnerPartnershipLocation();
              locationPartnership.setLocation(locationManager.getLocElementByISOCode(locationIsoAlpha2));
              locationPartnership.setProjectPartnerPartnership(partnershipUpdate);
              locationsSave.add(locationPartnership);
              if (!locationsDB.contains(locationPartnership)) {
                projectPartnerPartnershipLocationManager.saveProjectPartnerPartnershipLocation(locationPartnership);
                partnershipUpdate.getProjectPartnerPartnershipLocations().add(locationPartnership);
              }
            }
            for (ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation : locationsDB) {
              if (!locationsSave.contains(projectPartnerPartnershipLocation)) {
                projectPartnerPartnershipLocationManager
                  .deleteProjectPartnerPartnershipLocation(projectPartnerPartnershipLocation.getId());
              }
            }
          }

        }

      } else {
        partnershipUpdate.setGeographicScope(null);
        partnershipUpdate.setRegion(null);
        this.deletePartnershipLocations(locationsDB);
      }

      projectPartnerPartnershipManager.saveProjectPartnerPartnership(partnershipUpdate);
      projectPartnerDB.getProjectPartnerPartnerships().add(partnershipUpdate);

    }

  }

  /**
   * Send request Email to TIP team to add new contact when cluster leader or coordinator is add
   */
  public void sendRequestEmailToTIP(User user) {
    try {
      List<TipParameters> tipParameters = tipParametersManager.findAll();
      if (tipParameters != null) {
        TipParameters tipParameter = tipParameters.get(0);
        String toEmail = tipParameter.getTipEmail();
        String ccEmail = this.getCurrentUser().getEmail();
        String bbcEmails = this.config.getEmailNotification();
        String subject = (tipParameter.getEmailSubject() != null && !tipParameter.getEmailSubject().isEmpty())
          ? tipParameter.getEmailSubject() + " " + user.getFirstName() + " " + user.getLastName()
          : "Request to add user " + user.getFirstName() + " " + user.getLastName();
        subject = subject.replace("{param1}", user.getFirstName() + " " + user.getLastName());
        String emailText = tipParameter.getEmailText();

        emailText = emailText.replace("{param1}", user.getFirstName() + " " + user.getLastName());
        emailText = emailText.replace("{param2}", user.getEmail());

        if (this.validateEmailNotification()) {
          sendMail.sendTemporalMethod(toEmail, ccEmail, bbcEmails, subject, emailText, null, null, null, true);
        }
      }
    } catch (Exception e) {
      LOG.error("error getting TIP parameters " + e);
    }
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


  /**
   * set custom data to be used from partnershipperson section
   * 
   * @author IBD
   * @param Project project
   */

  public void setCustomDataToPartnerShipPerson(Project project) {
    List<Activity> activities = new ArrayList<Activity>();
    try {
      // puntobase
      List<ProjectPartnerDTO> projectPartnerDTOList = new ArrayList<ProjectPartnerDTO>();
      activities = activityManager.getActivitiesByProject(projectID, this.getActualPhase().getId());
      for (ProjectPartner projectPartner : project.getPartners()) {

        ProjectPartnerDTO projectPartnerDTO = new ProjectPartnerDTO();
        projectPartnerDTO.setId(projectPartner.getId());

        List<ProjectPartnerPersonDTO> projectPartnerPersonDTOList = new ArrayList<ProjectPartnerPersonDTO>();
        for (ProjectPartnerPerson partnerPerson : projectPartner.getProjectPartnerPersons()) {
          ProjectPartnerPersonDTO projectPartnerPersonDTOTmp = new ProjectPartnerPersonDTO();
          projectPartnerPersonDTOTmp.setId(partnerPerson.getId());
          projectPartnerPersonDTOTmp.setActivity(this.getActivitiesLedByUserCustom(partnerPerson.getId(), activities));


          projectPartnerPersonDTOList.add(projectPartnerPersonDTOTmp);
        }

        projectPartnerDTO.setProjectPartnerPersonDTO(projectPartnerPersonDTOList);
        projectPartnerDTOList.add(projectPartnerDTO);

      }

      projectDTO.setProjectPartner(projectPartnerDTOList);

    } catch (Exception e) {
      LOG.error(" unable to get custom data in prepareTwo function");
    }
  }


  public void setDivisions(List<PartnerDivision> divisions) {
    this.divisions = divisions;
  }


  public void setIntitutionTypes(List<InstitutionType> intitutionTypes) {
    this.intitutionTypes = intitutionTypes;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
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


  /**
   * This method updates the role for each user (Leader/Coordinator) into the database, and notifies by email what has
   * been done.
   * 
   * @param previousPartnerPerson is the previous leader/coordinator that has assigned the project before.
   * @param partnerPerson the current leader/coordinator associated to the project.
   * @param role is the new role assignated (leader/coordinator).
   */
  private void updateRoles(List<ProjectPartnerPerson> previousPartnerPerson, List<ProjectPartnerPerson> partnerPerson,
    Role role) {
    long roleId = role.getId();

    String roleAcronym = role.getAcronym();
    if (previousPartnerPerson != null && partnerPerson != null) {
      for (ProjectPartnerPerson projectPartnerPerson : partnerPerson) {

        if (projectPartnerPerson.getUser() != null && projectPartnerPerson.getUser().getId() != null) {
          if (!previousPartnerPerson.contains(projectPartnerPerson)) {
            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRole.setUser(projectPartnerPerson.getUser());

            role = roleManager.getRoleById(role.getId());
            if (!role.getUserRoles().contains(userRole)) {
              userRoleManager.saveUserRole(userRole);
              this.addCrpUser(projectPartnerPerson.getUser());
            }


            // Notifying user is assigned as Project Leader/Coordinator.
            if (projectPartnerPerson.getUser() != null) {
              this.notifyRoleAssigned(projectPartnerPerson.getUser(), role);
            }

          }
        }

      }

      for (ProjectPartnerPerson projectPartnerPerson : previousPartnerPerson) {
        if (projectPartnerPerson.getUser() != null && projectPartnerPerson.getUser().getId() != null) {
          if (!partnerPerson.contains(projectPartnerPerson)) {

            List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(projectPartnerPerson.getUser().getId());
            if (rolesUser != null) {
              rolesUser =
                rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
              if (!rolesUser.isEmpty()) {
                if (projectPartnerPerson.getUser().getProjectPartnerPersons().stream()
                  .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym)
                    && c.getProjectPartner().getProject() != null && c.getProjectPartner().getProject().getId()
                      .longValue() != projectPartnerPerson.getProjectPartner().getProject().getId().longValue())
                  .collect(Collectors.toList()).size() == 0) {
                  userRoleManager.deleteUserRole(rolesUser.get(0).getId());
                  this.checkCrpUserByRole(projectPartnerPerson.getUser());
                }
              }
            }
            // Notifying user that is not the project leader anymore
            this.notifyRoleUnassigned(projectPartnerPerson.getUser(), role);
          }
        }
      }
    }
  }

  /**
   * This method updates the role for each user (Leader/Coordinator) into the database, and notifies by email what has
   * been done.
   * 
   * @param previousPartnerPerson is the previous leader/coordinator that has assigned the project before.
   * @param partnerPerson the current leader/coordinator associated to the project.
   * @param role is the new role assignated (leader/coordinator).
   */
  private void updateRoles(ProjectPartnerPerson previousPartnerPerson, ProjectPartnerPerson partnerPerson, Role role) {
    long roleId = role.getId();

    String roleAcronym = role.getAcronym();
    if (previousPartnerPerson == null && partnerPerson != null) {

      UserRole userRole = new UserRole();
      userRole.setRole(role);
      userRole.setUser(partnerPerson.getUser());

      role = roleManager.getRoleById(role.getId());
      if (!role.getUserRoles().contains(userRole)) {
        if (userRole.getUser() != null) {
          userRoleManager.saveUserRole(userRole);
          this.addCrpUser(partnerPerson.getUser());
        }

      }


      if (partnerPerson.getUser() != null) {
        this.notifyRoleAssigned(partnerPerson.getUser(), role);
      }
      // Notifying user is assigned as Project Leader/Coordinator.

    } else if (previousPartnerPerson != null && partnerPerson == null) {

      List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(previousPartnerPerson.getUser().getId());
      if (rolesUser != null) {
        rolesUser =
          rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
        if (!rolesUser.isEmpty()) {
          if (previousPartnerPerson.getUser().getProjectPartnerPersons().stream()
            .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym) && c.getProjectPartner().getProject()
              .getId().longValue() != previousPartnerPerson.getProjectPartner().getProject().getId().longValue())
            .collect(Collectors.toList()).size() == 0) {
            userRoleManager.deleteUserRole(rolesUser.get(0).getId());
            this.checkCrpUserByRole(previousPartnerPerson.getUser());
          }

        }
      }

      // Notifying user that is not the project leader anymore
      this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
    } else if (previousPartnerPerson != null && partnerPerson != null && partnerPerson.getUser() != null
      && partnerPerson.getUser().getId() != null && previousPartnerPerson.getUser() != null) {
      if (!partnerPerson.getUser().getId().equals(previousPartnerPerson.getUser().getId())) {
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(partnerPerson.getUser());

        role = roleManager.getRoleById(role.getId());
        if (!role.getUserRoles().contains(userRole)) {
          userRoleManager.saveUserRole(userRole);
          this.addCrpUser(partnerPerson.getUser());
        }
        // Notifying user is assigned as Project Leader/Coordinator.
        if (partnerPerson.getUser() != null) {
          this.notifyRoleAssigned(partnerPerson.getUser(), role);
        } // Deleting role.
        List<UserRole> rolesUser = userRoleManager.getUserRolesByUserId(previousPartnerPerson.getUser().getId());
        if (rolesUser != null) {
          rolesUser =
            rolesUser.stream().filter(c -> c.getRole().getId().longValue() == roleId).collect(Collectors.toList());
          if (!rolesUser.isEmpty()) {
            if (previousPartnerPerson.getUser().getProjectPartnerPersons().stream()
              .filter(c -> c.isActive() && c.getContactType().equals(roleAcronym) && c.getProjectPartner().getProject()
                .getId().longValue() != previousPartnerPerson.getProjectPartner().getProject().getId().longValue())
              .collect(Collectors.toList()).size() == 0) {

              userRoleManager.deleteUserRole(rolesUser.get(0).getId());
              this.checkCrpUserByRole(previousPartnerPerson.getUser());
            }
          }
        }
        // Notifying user that is not the project leader anymore
        this.notifyRoleUnassigned(previousPartnerPerson.getUser(), role);
      }
    }
    // this.clearPermissionsCache();
  }


  @Override
  public void validate() {
    if (save) {
      boolean hasErrors = projectPartnersValidator.validate(this, project, true);
      if (hasErrors) {
        if (project.getPartners() != null) {
          for (ProjectPartner projectPartner : project.getPartners()) {

            if (projectPartner.getInstitution() != null && projectPartner.getInstitution().getId() != null) {
              projectPartner
                .setInstitution(institutionManager.getInstitutionById(projectPartner.getInstitution().getId()));
            }

            if (projectPartner.getPartnerPersons() != null) {
              for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getPartnerPersons()) {
                if (projectPartnerPerson.getUser() != null && projectPartnerPerson.getUser().getId() != null
                  && projectPartnerPerson.getUser().getId().longValue() != -1) {
                  projectPartnerPerson.setUser(userManager.getUser(projectPartnerPerson.getUser().getId()));
                }
              }
            }

            if (projectPartner.getPartnerContributors() != null) {
              for (ProjectPartnerContribution projectPartnerContribution : projectPartner.getPartnerContributors()) {

                projectPartnerContribution.getProjectPartnerContributor()
                  .setInstitution(institutionManager.getInstitutionById(
                    projectPartnerContribution.getProjectPartnerContributor().getInstitution().getId()));
              }
            }
            if (projectPartner.getProjectPartnerPartnership() != null
              && projectPartner.getProjectPartnerPartnership().getId().longValue() != -1) {
              projectPartner.setProjectPartnerPartnership(projectPartnerPartnershipManager
                .getProjectPartnerPartnershipById(projectPartner.getProjectPartnerPartnership().getId()));
              // Partnership Locations
              List<ProjectPartnerPartnershipLocation> partnerPartnershipLocations =
                projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipLocations().stream()
                  .filter(p -> p.isActive()).collect(Collectors.toList());
              for (ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation : partnerPartnershipLocations) {
                projectPartner.getProjectPartnerPartnership().getPartnershipLocations()
                  .add(projectPartnerPartnershipLocation);
              }

              // Partnership Research Phases
              List<ProjectPartnerPartnershipResearchPhase> partnershipResearchPhases =
                projectPartner.getProjectPartnerPartnership().getProjectPartnerPartnershipResearchPhases().stream()
                  .filter(rf -> rf.isActive()).collect(Collectors.toList());
              for (ProjectPartnerPartnershipResearchPhase partnershipResearchPhase : partnershipResearchPhases) {
                projectPartner.getProjectPartnerPartnership().getPartnershipResearchPhases()
                  .add(partnershipResearchPhase);
              }
            }
          }
        }

      }
    }
  }

}

