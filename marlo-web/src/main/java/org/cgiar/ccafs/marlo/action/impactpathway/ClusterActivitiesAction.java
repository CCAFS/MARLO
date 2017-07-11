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
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
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
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.utils.SendMailS;
import org.cgiar.ccafs.marlo.validation.impactpathway.ClusterActivitiesValidator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia
 */
public class ClusterActivitiesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -2049759808815382048L;

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
    while ((bytesRead = stream.read(buffer)) != -1) {
      baos.write(buffer, 0, bytesRead);
    }
    return baos.toByteArray();
  }

  private AuditLogManager auditLogManager;
  private long clRol;
  private List<CrpClusterOfActivity> clusterofActivities;
  private CrpClusterActivityLeaderManager crpClusterActivityLeaderManager;
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;
  private CrpClusterKeyOutputOutcomeManager crpClusterKeyOutputOutcomeManager;
  private CrpClusterOfActivityManager crpClusterOfActivityManager;
  private CrpManager crpManager;
  private long crpProgramID;
  private CrpProgramManager crpProgramManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private CrpUserManager crpUserManager;
  private Crp loggedCrp;
  private List<CrpProgramOutcome> outcomes;
  private List<CrpProgram> programs;
  private Role roleCl;
  private RoleManager roleManager;
  private CrpProgram selectedProgram;

  private HistoryComparator historyComparator;

  // Util
  private SendMailS sendMail;


  private String transaction;

  private UserManager userManager;
  private UserRoleManager userRoleManager;

  private ClusterActivitiesValidator validator;

  @Inject
  public ClusterActivitiesAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpManager crpManager, UserManager userManager, CrpProgramManager crpProgramManager,
    CrpClusterOfActivityManager crpClusterOfActivityManager, ClusterActivitiesValidator validator,
    CrpClusterActivityLeaderManager crpClusterActivityLeaderManager, AuditLogManager auditLogManager,
    SendMailS sendMail, CrpClusterKeyOutputManager crpClusterKeyOutputManager, HistoryComparator historyComparator,
    CrpProgramOutcomeManager crpProgramOutcomeManager,
    CrpClusterKeyOutputOutcomeManager crpClusterKeyOutputOutcomeManager, CrpUserManager crpUserManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.historyComparator = historyComparator;
    this.crpProgramManager = crpProgramManager;
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.crpClusterActivityLeaderManager = crpClusterActivityLeaderManager;
    this.auditLogManager = auditLogManager;
    this.validator = validator;
    this.sendMail = sendMail;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpClusterKeyOutputOutcomeManager = crpClusterKeyOutputOutcomeManager;
    this.crpUserManager = crpUserManager;
  }

  public void addCrpUser(User user) {
    user = userManager.getUser(user.getId());
    CrpUser crpUser = new CrpUser();
    crpUser.setUser(user);
    crpUser.setCrp(loggedCrp);

    List<CrpUser> userCrp = user.getCrpUsers().stream().filter(cu -> cu.isActive() && cu.getCrp().equals(loggedCrp))
      .collect(Collectors.toList());

    if (userCrp == null || userCrp.isEmpty()) {
      crpUser.setActive(true);
      crpUser.setActiveSince(new Date());
      crpUser.setCreatedBy(this.getCurrentUser());
      crpUser.setModifiedBy(this.getCurrentUser());
      crpUser.setModificationJustification("");
      crpUserManager.saveCrpUser(crpUser);
    }
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
   * This method will validate if the user is deactivated. If so, it will send an email indicating the credentials to
   * access.
   * 
   * @param user is a User object that could be the leader.
   */
  private void notifyNewUserCreated(User user) {
    user = userManager.getUser(user.getId());

    if (!user.isActive()) {
      String toEmail = user.getEmail();
      String ccEmail = null;
      String bbcEmails = this.config.getEmailNotification();
      String subject = this.getText("email.newUser.subject", new String[] {user.getFirstName()});
      // Setting the password
      String password = this.getText("email.outlookPassword");
      if (!user.isCgiarUser()) {
        // Generating a random password.
        password = RandomStringUtils.randomNumeric(6);
        // Applying the password to the user.
        user.setPassword(password);
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
          crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        } else {
          crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        }
      }

      message.append(this.getText("email.newUser.part1", new String[] {this.getText("email.newUser.listRoles"),
        config.getBaseUrl(), user.getEmail(), password, this.getText("email.support", new String[] {crpAdmins})}));
      message.append(this.getText("email.bye"));

      // Saving the new user configuration.
      user.setActive(true);
      userManager.saveUser(user, this.getCurrentUser());

      // Send UserManual.pdf
      String contentType = "application/pdf";
      String fileName = "Introduction_To_MARLO_v2.1.pdf";
      byte[] buffer = null;
      InputStream inputStream = null;

      try {
        inputStream = this.getClass().getResourceAsStream("/manual/Introduction_To_MARLO_v2.1.pdf");
        buffer = readFully(inputStream);
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }

      if (buffer != null && fileName != null && contentType != null) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer, contentType, fileName, true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }


  /**
   * @param userAssigned is the user been assigned
   * @param role is the role(Cluster Leader)
   * @param crpClusterPreview is the crpCluster
   */
  private void notifyRoleAssigned(User userAssigned, Role role, CrpClusterOfActivity crpClusterPreview) {
    // Send email to the new user and the P&R notification email.
    // TO
    String toEmail = userAssigned.getEmail();;
    String ccEmail = null;
    // CC will be the user who is making the modification.
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
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
    // Also others Cluster Leaders
    for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterPreview.getCrpClusterActivityLeaders().stream()
      .filter(cal -> cal.isActive() && cal.getUser().isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpClusterActivityLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
      }
    }
    // Also crp program Leaders
    for (CrpProgramLeader crpProgramLeader : crpClusterPreview.getCrpProgram().getCrpProgramLeaders().stream()
      .filter(l -> l.isActive() && l.getUser().isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpProgramLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpProgramLeader.getUser().getEmail();
      }
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // Subject
    String subject = this.getText("email.cluster.assigned.subject",
      new String[] {crpClusterPreview.getIdentifier(), loggedCrp.getName()});

    // Message
    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.cluster.assigned", new String[] {crpClusterPreview.getIdentifier(),
      crpClusterPreview.getDescription(), loggedCrp.getName(), this.getText("email.cluster.responsabilities")}));
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  private void notifyRoleUnassigned(User userAssigned, Role role, CrpClusterOfActivity crpClusterOfActivity) {
    // Send email to the new user and the P&R notification email.
    // TO
    String toEmail = userAssigned.getEmail();
    String ccEmail = null;
    // CC will be the user who is making the modification.
    if (this.getCurrentUser() != null) {
      ccEmail = this.getCurrentUser().getEmail();
    }
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
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
    // Also others Cluster Leaders
    for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
      .stream().filter(cal -> cal.isActive() && cal.getUser().isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpClusterActivityLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
      }
    }
    // Also crp program Leaders
    for (CrpProgramLeader crpProgramLeader : crpClusterOfActivity.getCrpProgram().getCrpProgramLeaders().stream()
      .filter(l -> l.isActive() && l.getUser().isActive()).collect(Collectors.toList())) {
      if (ccEmail.isEmpty()) {
        ccEmail += crpProgramLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + crpProgramLeader.getUser().getEmail();
      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // Subject
    String subject = this.getText("email.cluster.unassigned.subject",
      new String[] {crpClusterOfActivity.getIdentifier(), loggedCrp.getName()});


    // Message
    userAssigned = userManager.getUser(userAssigned.getId());
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(this.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(this.getText("email.cluster.unassigned",
      new String[] {crpClusterOfActivity.getIdentifier(), crpClusterOfActivity.getDescription(), loggedCrp.getName()}));
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
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

        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        for (CrpClusterOfActivity crpClusterOfActivity : clusterofActivities) {


          differences.addAll(historyComparator.getDifferencesList(crpClusterOfActivity, transaction, specialList,
            "clusterofActivities[" + i + "]", "clusterofActivities", 1));

          crpClusterOfActivity.setLeaders(crpClusterOfActivity.getCrpClusterActivityLeaders().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));

          int j = 0;
          for (CrpClusterActivityLeader clusterActivityLeader : crpClusterOfActivity.getLeaders()) {

            differences.addAll(historyComparator.getDifferencesList(clusterActivityLeader, transaction, specialList,
              "clusterofActivities[" + i + "].leaders[" + j + "]", "clusterofActivities", 2));

            j++;
          }
          crpClusterOfActivity.setKeyOutputs(crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList()));

          j = 0;


          int k = 0;
          for (CrpClusterKeyOutput crpClusterKeyOutput : crpClusterOfActivity.getKeyOutputs()) {

            differences.addAll(historyComparator.getDifferencesList(crpClusterKeyOutput, transaction, specialList,
              "clusterofActivities[" + i + "].keyOutputs[" + j + "]", "clusterofActivities", 2));


            crpClusterKeyOutput.setKeyOutputOutcomes(crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList()));
            for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : crpClusterKeyOutput.getKeyOutputOutcomes()) {
              differences.addAll(historyComparator.getDifferencesList(crpClusterKeyOutputOutcome, transaction,
                specialList, "clusterofActivities[" + i + "].keyOutputs[" + j + "].keyOutputOutcomes[" + k + "]",
                "clusterofActivities", 3));
              k++;
            }
            j++;

          }

          i++;
        }
        i = 0;


        this.setDifferences(differences);
      } else {
        programs = new ArrayList<>();
        this.transaction = "-1";
      }


    } else {
      List<CrpProgram> allPrograms = loggedCrp.getCrpPrograms().stream()
        .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
        .collect(Collectors.toList());
      allPrograms.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
      crpProgramID = -1;

      if (allPrograms != null) {

        this.programs = allPrograms;
        try {
          crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
        } catch (Exception e) {

          User user = userManager.getUser(this.getCurrentUser().getId());
          List<CrpProgramLeader> userLeads = user.getCrpProgramLeaders().stream()
            .filter(c -> c.isActive() && c.getCrpProgram().isActive() && c.getCrpProgram() != null

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

          try {
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
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        if (!selectedProgram.getSubmissions().stream().filter(c -> (c.isUnSubmit() == null || !c.isUnSubmit()))
          .collect(Collectors.toList()).isEmpty()) {

          if (!(this.canAccessSuperAdmin() || this.canAcessCrpAdmin())) {
            this.setCanEdit(false);
            this.setEditable(false);
          }

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

    if (this.hasPermission("canEdit")) {

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

                this.checkCrpUserByRole(user);
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
                this.addCrpUser(userRole.getUser());
                this.notifyNewUserCreated(userRole.getUser());
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
            for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : keyPreview.getCrpClusterKeyOutputOutcomes()) {
              crpClusterKeyOutputOutcomeManager.deleteCrpClusterKeyOutputOutcome(crpClusterKeyOutputOutcome.getId());
            }
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
                  crpClusterKeyOutputOutcome.setActive(true);
                } else {
                  CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcomePrev = crpClusterKeyOutputOutcomeManager
                    .getCrpClusterKeyOutputOutcomeById(crpClusterKeyOutputOutcome.getId());
                  crpClusterKeyOutputOutcome.setCreatedBy(crpClusterKeyOutputOutcomePrev.getCreatedBy());
                  crpClusterKeyOutputOutcome.setActiveSince(crpClusterKeyOutputOutcomePrev.getActiveSince());
                  crpClusterKeyOutputOutcome.setActive(crpClusterKeyOutputOutcomePrev.isActive());

                }

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
      selectedProgram.setModificationJustification(this.getJustification());
      selectedProgram.setAction(this.getActionName());
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROGRAM_ACTIVITIES_RELATION);
      crpProgramManager.saveCrpProgram(selectedProgram, this.getActionName(), relationsName);


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

    } else

    {

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
