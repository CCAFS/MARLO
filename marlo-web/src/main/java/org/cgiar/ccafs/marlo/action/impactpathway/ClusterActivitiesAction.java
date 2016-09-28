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


package org.cgiar.ccafs.marlo.action.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterActivityLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.SendMail;
import org.cgiar.ccafs.marlo.validation.impactpathway.ClusterActivitiesValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia
 */
public class ClusterActivitiesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -2049759808815382048L;
  private RoleManager roleManager;
  private UserRoleManager userRoleManager;
  private CrpManager crpManager;
  private CrpProgramManager crpProgramManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private CrpClusterOfActivityManager crpClusterOfActivityManager;
  private CrpClusterKeyOutputOutcomeManager crpClusterKeyOutputOutcomeManager;
  private CrpClusterActivityLeaderManager crpClusterActivityLeaderManager;
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;
  private UserManager userManager;
  private Crp loggedCrp;
  private Role roleCl;
  private long clRol;
  private List<CrpProgram> programs;
  private CrpProgram selectedProgram;
  private long crpProgramID;
  private List<CrpClusterOfActivity> clusterofActivities;
  private List<CrpProgramOutcome> outcomes;

  private ClusterActivitiesValidator validator;


  private AuditLogManager auditLogManager;

  private String transaction;
  // Util
  private SendMail sendMail;

  @Inject
  public ClusterActivitiesAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpManager crpManager, UserManager userManager, CrpProgramManager crpProgramManager,
    CrpClusterOfActivityManager crpClusterOfActivityManager, ClusterActivitiesValidator validator,
    CrpClusterActivityLeaderManager crpClusterActivityLeaderManager, AuditLogManager auditLogManager, SendMail sendMail,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    CrpClusterKeyOutputOutcomeManager crpClusterKeyOutputOutcomeManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.crpClusterActivityLeaderManager = crpClusterActivityLeaderManager;
    this.auditLogManager = auditLogManager;
    this.validator = validator;
    this.sendMail = sendMail;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpClusterKeyOutputOutcomeManager = crpClusterKeyOutputOutcomeManager;
  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
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

  private Path getAutoSaveFilePath() {
    String composedClassName = selectedProgram.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = selectedProgram.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public long getClRol() {
    return clRol;
  }

  public List<CrpClusterOfActivity> getClusterofActivities() {
    return clusterofActivities;
  }

  public long getCrpProgramID() {
    return crpProgramID;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }


  public List<CrpProgram> getPrograms() {
    return programs;
  }


  public Role getRoleCl() {
    return roleCl;
  }

  public CrpProgram getSelectedProgram() {
    return selectedProgram;
  }


  public String getTransaction() {
    return transaction;
  }


  /**
   * @param userAssigned is the user been assigned
   * @param role is the role(Cluster Leader)
   * @param crpClusterPreview is the crpCluster
   */
  private void notifyRoleAssigned(User userAssigned, Role role, CrpClusterOfActivity crpClusterPreview) {
    String ClusterRole = this.getText("cluster.role");
    String ClusterRoleAcronym = this.getText("cluster.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.cluster.assigned", new String[] {ClusterRole,
      crpClusterPreview.getCrpProgram().getName(), crpClusterPreview.getCrpProgram().getAcronym()}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    String toEmail = null;
    String ccEmail = null;
    if (config.isProduction()) {
      // Send email to the new user and the P&R notification email.
      // TO
      toEmail = userAssigned.getEmail();
      // CC will be the user who is making the modification.
      if (this.getCurrentUser() != null) {
        ccEmail = this.getCurrentUser().getEmail();
      }
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    sendMail.send(toEmail, ccEmail, bbcEmails,
      this.getText("email.cluster.assigned.subject",
        new String[] {loggedCrp.getName(), ClusterRoleAcronym, crpClusterPreview.getCrpProgram().getAcronym()}),
      message.toString(), null, null, null, true);
  }


  private void notifyRoleUnassigned(User userAssigned, Role role, CrpClusterOfActivity crpClusterOfActivity) {
    System.out.println("notifica correcto");
    String ClusterRole = this.getText("cluster.role");
    String ClusterRoleAcronym = this.getText("cluster.role.acronym");

    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.cluster.unassigned", new String[] {ClusterRole,
      crpClusterOfActivity.getCrpProgram().getName(), crpClusterOfActivity.getCrpProgram().getAcronym()}));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));

    String toEmail = null;
    String ccEmail = null;
    if (config.isProduction()) {
      // Send email to the new user and the P&R notification email.
      // TO
      toEmail = userAssigned.getEmail();
      // CC will be the user who is making the modification.
      if (this.getCurrentUser() != null) {
        ccEmail = this.getCurrentUser().getEmail();
      }
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    sendMail.send(toEmail, ccEmail, bbcEmails,
      this.getText("email.cluster.unassigned.subject",
        new String[] {loggedCrp.getName(), ClusterRoleAcronym, crpClusterOfActivity.getCrpProgram().getAcronym()}),
      message.toString(), null, null, null, true);
  }


  @Override
  public void prepare() throws Exception {

    // Get the Users list that have the pmu role in this crp.
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    clRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_CL_ROLE));
    roleCl = roleManager.getRoleById(clRol);
    clusterofActivities = new ArrayList<>();
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CrpProgram history = (CrpProgram) auditLogManager.getHistory(transaction);
      if (history != null) {
        crpProgramID = history.getId();
        selectedProgram = history;
        clusterofActivities.addAll(history.getCrpClusterOfActivities());

        this.setEditable(false);
        this.setCanEdit(false);
        programs = new ArrayList<>();
        programs.add(history);
        for (CrpClusterOfActivity crpClusterOfActivity : clusterofActivities) {

          crpClusterOfActivity.setLeaders(crpClusterOfActivity.getCrpClusterActivityLeaders().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));

          crpClusterOfActivity.setKeyOutputs(crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));


          for (CrpClusterKeyOutput crpClusterKeyOutput : crpClusterOfActivity.getKeyOutputs()) {
            crpClusterKeyOutput.setKeyOutputOutcomes(crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList()));
          }
        }
      } else {
        programs = new ArrayList<>();
        this.transaction = "-1";
      }


    } else {
      List<CrpProgram> allPrograms = loggedCrp.getCrpPrograms().stream()
        .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
        .collect(Collectors.toList());
      crpProgramID = -1;

      if (allPrograms != null) {

        this.programs = allPrograms;
        try {
          crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
        } catch (Exception e) {

          User user = userManager.getUser(this.getCurrentUser().getId());
          List<CrpProgramLeader> userLeads = user.getCrpProgramLeaders().stream()
            .filter(c -> c.isActive() && c.getCrpProgram().isActive()
              && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList());
          if (!userLeads.isEmpty()) {
            crpProgramID = userLeads.get(0).getCrpProgram().getId();
          } else {
            if (!this.programs.isEmpty()) {
              crpProgramID = this.programs.get(0).getId();
            }
          }


        }

      } else {
        programs = new ArrayList<>();
      }

      if (crpProgramID != -1) {
        selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);

        clusterofActivities.addAll(
          selectedProgram.getCrpClusterOfActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (CrpClusterOfActivity crpClusterOfActivity : clusterofActivities) {

          crpClusterOfActivity.setLeaders(crpClusterOfActivity.getCrpClusterActivityLeaders().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));
          crpClusterOfActivity.setKeyOutputs(crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));
          for (CrpClusterKeyOutput crpClusterKeyOutput : crpClusterOfActivity.getKeyOutputs()) {
            crpClusterKeyOutput.setKeyOutputOutcomes(crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList()));
          }
        }
      }

      if (selectedProgram != null) {

        Path path = this.getAutoSaveFilePath();

        if (path.toFile().exists()) {

          BufferedReader reader = null;

          reader = new BufferedReader(new FileReader(path.toFile()));

          Gson gson = new GsonBuilder().create();


          JsonObject jReader = gson.fromJson(reader, JsonObject.class);

          AutoSaveReader autoSaveReader = new AutoSaveReader();

          selectedProgram = (CrpProgram) autoSaveReader.readFromJson(jReader);
          clusterofActivities = selectedProgram.getClusterofActivities();
          selectedProgram.setAcronym(crpProgramManager.getCrpProgramById(selectedProgram.getId()).getAcronym());
          selectedProgram.setModifiedBy(userManager.getUser(selectedProgram.getModifiedBy().getId()));
          selectedProgram.setCrp(loggedCrp);
          if (clusterofActivities == null) {
            clusterofActivities = new ArrayList<>();
          }
          for (CrpClusterOfActivity clusterOfActivity : clusterofActivities) {


            if (clusterOfActivity.getKeyOutputs() == null) {
              clusterOfActivity.setKeyOutputs(new ArrayList<>());
            }
            if (clusterOfActivity.getLeaders() != null) {
              for (CrpClusterActivityLeader leaders : clusterOfActivity.getLeaders()) {
                if (leaders.getUser() != null && leaders.getUser().getId() != null) {
                  leaders.setUser(userManager.getUser(leaders.getUser().getId()));
                }
              }
            }

            if (clusterOfActivity.getKeyOutputs() != null) {
              for (CrpClusterKeyOutput keyOuput : clusterOfActivity.getKeyOutputs()) {
                if (keyOuput.getKeyOutputOutcomes() != null) {
                  for (CrpClusterKeyOutputOutcome keyOuputOutcome : keyOuput.getKeyOutputOutcomes()) {
                    keyOuputOutcome.setCrpProgramOutcome(crpProgramOutcomeManager
                      .getCrpProgramOutcomeById(keyOuputOutcome.getCrpProgramOutcome().getId()));
                  }
                }
              }
            }
          }

          reader.close();
          this.setDraft(true);
        } else {
          this.setDraft(false);
        }
        String params[] = {loggedCrp.getAcronym(), selectedProgram.getId().toString()};
        this.setBasePermission(this.getText(Permission.IMPACT_PATHWAY_BASE_PERMISSION, params));
        selectedProgram = crpProgramManager.getCrpProgramById(selectedProgram.getId());
        outcomes =
          selectedProgram.getCrpProgramOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        if (!selectedProgram.getSubmissions().isEmpty()) {
          this.setCanEdit(false);
          this.setEditable(false);
          this.setSubmission(selectedProgram.getSubmissions().stream().collect(Collectors.toList()).get(0));
        }
      }
      if (this.isHttpPost()) {
        clusterofActivities.clear();
      }

    }

  }

  @Override
  public String save() {
    System.out.println("============");

    if (this.hasPermission("*")) {

      /*
       * Removing outcomes
       */
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
      for (CrpClusterOfActivity crpClusterOfActivity : selectedProgram.getCrpClusterOfActivities().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        if (!clusterofActivities.contains(crpClusterOfActivity)) {
          // if (crpClusterOfActivity.getCrpMilestones().isEmpty() &&
          // crpProgramOutcome.getCrpOutcomeSubIdos().isEmpty()) {
          crpClusterOfActivityManager.deleteCrpClusterOfActivity(crpClusterOfActivity.getId());
          // }
        }
      }
      /*
       * Save outcomes
       */
      for (CrpClusterOfActivity crpClusterOfActivity : clusterofActivities) {

        if (crpClusterOfActivity.getId() == null) {
          crpClusterOfActivity.setActive(true);

          crpClusterOfActivity.setCreatedBy(this.getCurrentUser());
          crpClusterOfActivity.setModifiedBy(this.getCurrentUser());
          crpClusterOfActivity.setModificationJustification("");
          crpClusterOfActivity.setActiveSince(new Date());

        } else {
          CrpClusterOfActivity db =
            crpClusterOfActivityManager.getCrpClusterOfActivityById(crpClusterOfActivity.getId());
          crpClusterOfActivity.setActive(true);
          crpClusterOfActivity.setCreatedBy(db.getCreatedBy());
          crpClusterOfActivity.setModifiedBy(this.getCurrentUser());
          crpClusterOfActivity.setModificationJustification("");
          crpClusterOfActivity.setActiveSince(db.getActiveSince());
        }
        crpClusterOfActivity.setCrpProgram(selectedProgram);
        crpClusterOfActivityManager.saveCrpClusterOfActivity(crpClusterOfActivity);

        /*
         * Check leaders
         */
        CrpClusterOfActivity crpClusterPrev =
          crpClusterOfActivityManager.getCrpClusterOfActivityById(crpClusterOfActivity.getId());
        for (CrpClusterActivityLeader leaderPreview : crpClusterPrev.getCrpClusterActivityLeaders().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {

          if (crpClusterOfActivity.getLeaders() == null) {
            crpClusterOfActivity.setLeaders(new ArrayList<>());
          }
          if (!crpClusterOfActivity.getLeaders().contains(leaderPreview)) {
            crpClusterActivityLeaderManager.deleteCrpClusterActivityLeader(leaderPreview.getId());
            User user = userManager.getUser(leaderPreview.getUser().getId());

            List<CrpClusterActivityLeader> existsUserLeader =
              user.getCrpClusterActivityLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());


            if (existsUserLeader == null || existsUserLeader.isEmpty()) {


              List<UserRole> clUserRoles =
                user.getUserRoles().stream().filter(ur -> ur.getRole().equals(roleCl)).collect(Collectors.toList());
              if (clUserRoles != null || !clUserRoles.isEmpty()) {
                for (UserRole userRole : clUserRoles) {
                  userRoleManager.deleteUserRole(userRole.getId());
                  this.notifyRoleUnassigned(userRole.getUser(), userRole.getRole(), crpClusterOfActivity);
                }
              }
            }
          }
        }
        /*
         * Save leaders
         */
        if (crpClusterOfActivity.getLeaders() != null) {
          for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getLeaders()) {
            if (crpClusterActivityLeader.getId() == null) {
              crpClusterActivityLeader.setActive(true);
              crpClusterActivityLeader.setCrpClusterOfActivity(crpClusterOfActivity);
              crpClusterActivityLeader.setCreatedBy(this.getCurrentUser());
              crpClusterActivityLeader.setModifiedBy(this.getCurrentUser());
              crpClusterActivityLeader.setModificationJustification("");
              crpClusterActivityLeader.setActiveSince(new Date());
              CrpClusterOfActivity crpClusterPreview =
                crpClusterOfActivityManager.getCrpClusterOfActivityById(crpClusterOfActivity.getId());
              if (crpClusterPreview.getCrpClusterActivityLeaders().stream()
                .filter(c -> c.isActive() && c.getUser().equals(crpClusterActivityLeader.getUser()))
                .collect(Collectors.toList()).isEmpty()) {
                crpClusterActivityLeaderManager.saveCrpClusterActivityLeader(crpClusterActivityLeader);
              }

              User user = userManager.getUser(crpClusterActivityLeader.getUser().getId());
              UserRole userRole = new UserRole();
              userRole.setUser(user);
              userRole.setRole(this.roleCl);
              if (!user.getUserRoles().contains(userRole)) {
                userRoleManager.saveUserRole(userRole);
                this.notifyRoleAssigned(userRole.getUser(), userRole.getRole(), crpClusterPreview);
              }
            }
          }
        }


        /*
         * Check key outputs
         */
        crpClusterPrev = crpClusterOfActivityManager.getCrpClusterOfActivityById(crpClusterOfActivity.getId());
        for (CrpClusterKeyOutput keyPreview : crpClusterPrev.getCrpClusterKeyOutputs().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {

          if (crpClusterOfActivity.getKeyOutputs() == null) {
            crpClusterOfActivity.setKeyOutputs(new ArrayList<>());
          }
          if (!crpClusterOfActivity.getKeyOutputs().contains(keyPreview)) {
            crpClusterKeyOutputManager.deleteCrpClusterKeyOutput(keyPreview.getId());

          }
        }
        /*
         * Save key outputs
         */
        if (crpClusterOfActivity.getKeyOutputs() != null) {
          for (CrpClusterKeyOutput crpClusterKeyOutput : crpClusterOfActivity.getKeyOutputs()) {
            if (crpClusterKeyOutput.getId() == null) {
              crpClusterKeyOutput.setCreatedBy(this.getCurrentUser());

              crpClusterKeyOutput.setActiveSince(new Date());

            } else {
              CrpClusterKeyOutput crpClusterKeyOutputPrev =
                crpClusterKeyOutputManager.getCrpClusterKeyOutputById(crpClusterKeyOutput.getId());
              crpClusterKeyOutput.setCreatedBy(crpClusterKeyOutputPrev.getCreatedBy());
              crpClusterKeyOutput.setActiveSince(crpClusterKeyOutputPrev.getActiveSince());

            }
            crpClusterKeyOutput.setActive(true);
            crpClusterKeyOutput.setCrpClusterOfActivity(crpClusterOfActivity);
            crpClusterKeyOutput.setModifiedBy(this.getCurrentUser());
            crpClusterKeyOutput.setModificationJustification("");
            crpClusterKeyOutputManager.saveCrpClusterKeyOutput(crpClusterKeyOutput);

            /*
             * deleting key ouputs otucomes
             */
            CrpClusterKeyOutput crpClusterKeyOutputPrev =
              crpClusterKeyOutputManager.getCrpClusterKeyOutputById(crpClusterKeyOutput.getId());
            for (CrpClusterKeyOutputOutcome keyOutputOutcome : crpClusterKeyOutputPrev.getCrpClusterKeyOutputOutcomes()
              .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {

              if (crpClusterKeyOutput.getKeyOutputOutcomes() == null) {
                crpClusterKeyOutput.setKeyOutputOutcomes(new ArrayList<>());
              }
              if (!crpClusterKeyOutput.getKeyOutputOutcomes().contains(keyOutputOutcome)) {
                crpClusterKeyOutputOutcomeManager.deleteCrpClusterKeyOutputOutcome(keyOutputOutcome.getId());

              }
            }
            /*
             * Save key outputs otucomes
             */
            if (crpClusterKeyOutput.getKeyOutputOutcomes() != null) {
              for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : crpClusterKeyOutput.getKeyOutputOutcomes()) {
                if (crpClusterKeyOutputOutcome.getId() == null) {
                  crpClusterKeyOutputOutcome.setCreatedBy(this.getCurrentUser());

                  crpClusterKeyOutputOutcome.setActiveSince(new Date());

                } else {
                  CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcomePrev = crpClusterKeyOutputOutcomeManager
                    .getCrpClusterKeyOutputOutcomeById(crpClusterKeyOutputOutcome.getId());
                  crpClusterKeyOutputOutcome.setCreatedBy(crpClusterKeyOutputOutcomePrev.getCreatedBy());
                  crpClusterKeyOutputOutcome.setActiveSince(crpClusterKeyOutputOutcomePrev.getActiveSince());

                }
                crpClusterKeyOutputOutcome.setActive(true);
                crpClusterKeyOutputOutcome.setCrpClusterKeyOutput(crpClusterKeyOutput);
                crpClusterKeyOutputOutcome.setModifiedBy(this.getCurrentUser());
                crpClusterKeyOutputOutcome.setModificationJustification("");
                crpClusterKeyOutputOutcomeManager.saveCrpClusterKeyOutputOutcome(crpClusterKeyOutputOutcome);
              }
            }

          }
        }


      }
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
      selectedProgram.setActiveSince(new Date());
      selectedProgram.setModifiedBy(this.getCurrentUser());
      selectedProgram.setAction(this.getActionName());
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROGRAM_ACTIVITIES_RELATION);
      crpProgramManager.saveCrpProgram(selectedProgram, this.getActionName(), relationsName);
      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      messages = this.getActionMessages();

      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }


  }

  public void setClRol(long clRol) {
    this.clRol = clRol;
  }


  public void setClusterofActivities(List<CrpClusterOfActivity> clusterofActivities) {
    this.clusterofActivities = clusterofActivities;
  }


  public void setCrpProgramID(long crpProgramID) {
    this.crpProgramID = crpProgramID;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setOutcomes(List<CrpProgramOutcome> outcomes) {
    this.outcomes = outcomes;
  }


  public void setPrograms(List<CrpProgram> programs) {
    this.programs = programs;
  }


  public void setRoleCl(Role roleCl) {
    this.roleCl = roleCl;
  }


  public void setSelectedProgram(CrpProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }

  public void setTransaction(String transactionID) {
    this.transaction = transactionID;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, clusterofActivities, selectedProgram, true);
    }
  }

}
