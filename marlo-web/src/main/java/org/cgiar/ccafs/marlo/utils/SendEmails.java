package org.cgiar.ccafs.marlo.utils;
import org.cgiar.ccafs.marlo.ApplicationContextConfig;
import org.cgiar.ccafs.marlo.action.json.project.ProjectLeaderEditAction;
import org.cgiar.ccafs.marlo.action.json.project.ValidateProjectSectionAction;
import org.cgiar.ccafs.marlo.action.summaries.ReportingSummaryAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.RoleDAO;
import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

public class SendEmails {

  private static ReportingSummaryAction action;
  private static ValidateProjectSectionAction validateProjectSectionAction;

  private static SendMailS sendMail;
  private static UserDAO userDAO;
  private static ProjectDAO dao;
  private static ProjectLeaderEditAction projectLeaderEditAction;
  private static RoleDAO roleDAO;
  private static GlobalUnitProjectManager globalUnitProjectManager;
  private static GlobalUnitManager globalUnitManager;
  private static List<User> users;
  private static List<String> usersEmail;

  public static void main(String[] args) {

    users = new ArrayList<>();
    usersEmail = new ArrayList<>();
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationContextConfig.class);
    SessionFactory sessionFactory = ctx.getBean(SessionFactory.class);
    sessionFactory.getCurrentSession().beginTransaction();
    userDAO = ctx.getBean(UserDAO.class);
    roleDAO = ctx.getBean(RoleDAO.class);
    dao = ctx.getBean(ProjectDAO.class);
    sendMail = ctx.getBean(SendMailS.class);
    action = ctx.getBean(ReportingSummaryAction.class);
    validateProjectSectionAction = ctx.getBean(ValidateProjectSectionAction.class);
    projectLeaderEditAction = ctx.getBean(ProjectLeaderEditAction.class);
    globalUnitManager = ctx.getBean(GlobalUnitManager.class);
    globalUnitProjectManager = ctx.getBean(GlobalUnitProjectManager.class);
    action.setUsersToActive(new ArrayList<Map<String, Object>>());
    AuditLogContextProvider.push(new AuditLogContext());
    List<Map<String, Object>> list = userDAO.findCustomQuery(
      " select p.id from  projects p  inner join global_unit_projects gp on gp.project_id=p.id where p.id_consolidado is not null  and gp.global_unit_id=21  and p.id>880; ");
    action.setSession(new HashMap<>());
    action.loadProvider(action.getSession());
    action.getSession().put(APConstants.CRP_ADMIN_ROLE, "83");
    action.getSession().put(APConstants.CRP_EMAIL_CC_FL_FM_CL, true);
    action.getSession().put(APConstants.CRP_PMU_ROLE, "84");
    action.getSession().put(APConstants.CRP_PL_ROLE, "91");
    action.getSession().put(APConstants.CRP_PL_ROLE, "91");
    action.getSession().put(APConstants.CRP_LESSONS_ACTIVE, true);
    action.setPhaseID(new Long(7));
    List<Phase> phases = globalUnitManager.getGlobalUnitById(21).getPhases().stream().filter(c -> c.isActive())
      .collect(Collectors.toList());
    phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
    Map<Long, Phase> allPhasesMap = new HashMap<>();
    for (Phase phase : phases) {
      allPhasesMap.put(phase.getId(), phase);
    }
    action.getSession().put(APConstants.ALL_PHASES, allPhasesMap);
    Long plRoleID = Long.parseLong((String) action.getSession().get(APConstants.CRP_PL_ROLE));
    Role plRole = roleDAO.find(plRoleID);
    for (Map<String, Object> map : list) {
      Long id = Long.parseLong(map.get("id").toString());
      Project project = dao.find(id);
      for (ProjectPartner projectPartner : project.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().longValue() == action.getPhaseID().longValue())
        .collect(Collectors.toList())) {
        for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          if (projectPartnerPerson.getContactType().equals("PL")) {
            if (!usersEmail.contains(projectPartnerPerson.getUser().getEmail())) {
              notifyNewUserCreated(projectPartnerPerson.getUser());
            }
            notifyRoleAssigned(projectPartnerPerson.getUser(), plRole, project,
              globalUnitProjectManager.findByProjectId(project.getId()).getGlobalUnit());
          }
        }
      }
      for (Phase phase : phases) {
        if (phase.getYear() == 2017 || phase.getYear() == 2018) {
          System.out.println("VALIDATE PROJECT " + project.getId());
          validateProjectSectionAction.setProjectID(project.getId());
          validateProjectSectionAction.setSession(action.getSession());
          validateProjectSectionAction.setValidSection(true);
          validateProjectSectionAction.setExistProject(true);
          validateProjectSectionAction.setInvalidFields(new HashMap<>());
          validateProjectSectionAction.setValidationMessage(new StringBuilder());
          validateProjectSectionAction.loadProvider(action.getSession());
          validateProjectSectionAction.setPhaseID(phase.getId());
          validateProjectSectionAction
            .setCrpID(globalUnitProjectManager.findByProjectId(project.getId()).getGlobalUnit().getId());
          validateProjectSectionAction.setMissingFields(new StringBuilder());
          validateProjectSectionAction.setSectionName(ProjectSectionStatusEnum.DESCRIPTION.getStatus());
          try {
            validateProjectSectionAction.execute();
          } catch (Exception e) {
            e.printStackTrace();
          }
          validateProjectSectionAction.setMissingFields(new StringBuilder());
          validateProjectSectionAction.setSectionName(ProjectSectionStatusEnum.PARTNERS.getStatus());
          try {
            validateProjectSectionAction.execute();
          } catch (Exception e) {
            e.printStackTrace();
          }
          validateProjectSectionAction.setMissingFields(new StringBuilder());
          validateProjectSectionAction.setSectionName(ProjectSectionStatusEnum.BUDGET.getStatus());
          try {
            validateProjectSectionAction.execute();
          } catch (Exception e) {
            e.printStackTrace();
          }
          projectLeaderEditAction.setProjectId(project.getId());
          projectLeaderEditAction.setProjectStatus(true);
          projectLeaderEditAction.setSession(action.getSession());
          projectLeaderEditAction.setInvalidFields(new HashMap<>());
          projectLeaderEditAction.setValidationMessage(new StringBuilder());
          projectLeaderEditAction.loadProvider(action.getSession());
          projectLeaderEditAction.setPhaseID(phase.getId());
          projectLeaderEditAction
            .setCrpID(globalUnitProjectManager.findByProjectId(project.getId()).getGlobalUnit().getId());
          projectLeaderEditAction.setMissingFields(new StringBuilder());
          try {
            if (projectLeaderEditAction.isCompletePreProject(project.getId())) {
              projectLeaderEditAction.execute();
            }
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }
    list = userDAO.findCustomQuery(
      " select p.id from  projects p  inner join global_unit_projects gp on gp.project_id=p.id where p.id_consolidado is not null  and gp.global_unit_id=22  ; ");
    action.setSession(new HashMap<>());
    action.loadProvider(action.getSession());
    action.getSession().put(APConstants.CRP_ADMIN_ROLE, "98");
    action.getSession().put(APConstants.CRP_EMAIL_CC_FL_FM_CL, true);
    action.getSession().put(APConstants.CRP_PMU_ROLE, "99");
    action.getSession().put(APConstants.CRP_PL_ROLE, "106");
    action.setPhaseID(new Long(8));
    action.getSession().put(APConstants.CRP_LESSONS_ACTIVE, true);
    phases = globalUnitManager.getGlobalUnitById(22).getPhases().stream().filter(c -> c.isActive())
      .collect(Collectors.toList());
    phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
    allPhasesMap = new HashMap<>();
    for (Phase phase : phases) {
      allPhasesMap.put(phase.getId(), phase);
    }
    action.getSession().put(APConstants.ALL_PHASES, allPhasesMap);
    plRoleID = Long.parseLong((String) action.getSession().get(APConstants.CRP_PL_ROLE));
    plRole = roleDAO.find(plRoleID);
    for (Map<String, Object> map : list) {
      Long id = Long.parseLong(map.get("id").toString());
      Project project = dao.find(id);
      for (ProjectPartner projectPartner : project.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().longValue() == action.getPhaseID().longValue())
        .collect(Collectors.toList())) {
        for (ProjectPartnerPerson projectPartnerPerson : projectPartner.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          if (projectPartnerPerson.getContactType().equals("PL")) {
            if (!usersEmail.contains(projectPartnerPerson.getUser().getEmail())) {
              notifyNewUserCreated(projectPartnerPerson.getUser());
            }
            notifyRoleAssigned(projectPartnerPerson.getUser(), plRole, project,
              globalUnitProjectManager.findByProjectId(project.getId()).getGlobalUnit());
          }
        }
      }
      for (Phase phase : phases) {
        if (phase.getYear() == 2017 || phase.getYear() == 2018 && phase.getDescription().equals("Planning")) {
          System.out.println("VALIDATE PROJECT " + project.getId());
          validateProjectSectionAction.setProjectID(project.getId());
          validateProjectSectionAction.setSession(action.getSession());
          validateProjectSectionAction.setValidSection(true);
          validateProjectSectionAction.setExistProject(true);
          validateProjectSectionAction.setInvalidFields(new HashMap<>());
          validateProjectSectionAction.setValidationMessage(new StringBuilder());
          validateProjectSectionAction.loadProvider(action.getSession());
          validateProjectSectionAction.setPhaseID(phase.getId());
          validateProjectSectionAction
            .setCrpID(globalUnitProjectManager.findByProjectId(project.getId()).getGlobalUnit().getId());
          validateProjectSectionAction.setMissingFields(new StringBuilder());
          validateProjectSectionAction.setSectionName(ProjectSectionStatusEnum.DESCRIPTION.getStatus());
          try {
            validateProjectSectionAction.execute();
          } catch (Exception e) {
            e.printStackTrace();
          }
          validateProjectSectionAction.setMissingFields(new StringBuilder());
          validateProjectSectionAction.setSectionName(ProjectSectionStatusEnum.PARTNERS.getStatus());
          try {
            validateProjectSectionAction.execute();
          } catch (Exception e) {
            e.printStackTrace();
          }
          validateProjectSectionAction.setMissingFields(new StringBuilder());
          validateProjectSectionAction.setSectionName(ProjectSectionStatusEnum.BUDGET.getStatus());
          try {
            validateProjectSectionAction.execute();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    action.addUsers();
    sessionFactory.getCurrentSession().getTransaction().commit();

  }

  private static void notifyNewUserCreated(User user) {

    if (user != null && user.getId() != null) {
      user = userDAO.getUser(user.getId());

      if (!user.isActive()) {
        user.setActive(true);
        // user = userDAO.saveUser(user);
        String toEmail = user.getEmail();
        String ccEmail = "";
        String bbcEmails = action.getConfig().getEmailNotification();
        String subject = action.getText("email.newUser.subject", new String[] {user.getFirstName()});
        // Setting the password
        String password = action.getText("email.outlookPassword");
        if (!user.isCgiarUser()) {
          // Generating a random password.
          password = RandomStringUtils.randomNumeric(6);


        }

        // Building the Email message:
        StringBuilder message = new StringBuilder();
        message.append(action.getText("email.dear", new String[] {user.getFirstName()}));

        // get CRPAdmin contacts
        String crpAdmins = "";
        long adminRol = Long.parseLong((String) action.getSession().get(APConstants.CRP_ADMIN_ROLE));
        Role roleAdmin = roleDAO.find(adminRol);
        List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
          .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
        for (UserRole userRole : userRoles) {
          if (crpAdmins.isEmpty()) {
            crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
          } else {
            crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
          }
        }

        message.append(action.getText("email.newUser.part1",
          new String[] {action.getText("email.newUser.listRoles"), action.getConfig().getBaseUrl(), user.getEmail(),
            password, action.getText("email.support", new String[] {crpAdmins})}));
        // message.append(action);

        // Saving the new user configuration.
        // user.setActive(true);
        // userManager.saveUser(user, this.getCurrentUser());
        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("user", user);
        mapUser.put("password", password);
        action.getUsersToActive().add(mapUser);
        usersEmail.add(user.getEmail());
        users.add(user);
        // Send UserManual.pdf
        String contentType = "application/pdf";
        String fileName = "Introduction_To_MARLO_v2.2.pdf";
        byte[] buffer = null;
        InputStream inputStream = null;

        try {
          inputStream = action.getClass().getResourceAsStream("/manual/Introduction_To_MARLO_v2.2.pdf");
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

  }

  private static void notifyRoleAssigned(User userAssigned, Role role, Project project, GlobalUnit loggedCrp) {


    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject =

      globalUnitProjectManager.findByProjectAndGlobalUnitId(project.getId(), loggedCrp.getId());
    userAssigned = userDAO.getUser(userAssigned.getId());


    // TO will be the new user
    String toEmail = userAssigned.getEmail();
    // CC will be the user who is making the modification.
    String ccEmail = "c.d.garcia@cgiar.org";
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) action.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleDAO.find(adminRol);
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

    // Copy to FL, CL and FM depending on CRP_EMAIL_CC_FL_FM_CL specificity
    if (action.hasSpecificities(APConstants.CRP_EMAIL_CC_FL_FM_CL)) {
      // CC for leaders and coordinators
      // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
      Long crpPmuRole = Long.parseLong((String) action.getSession().get(APConstants.CRP_PMU_ROLE));
      Role roleCrpPmu = roleDAO.find(crpPmuRole);
      // If Managment liason is PMU
      if (project.getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution() != null
        && project.getProjecInfoPhase(action.getActualPhase()).getLiaisonUser() != null) {
        if (project.getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution().getAcronym()
          .equals(roleCrpPmu.getAcronym())) {
          if (ccEmail.isEmpty()) {
            ccEmail += project.getProjecInfoPhase(action.getActualPhase()).getLiaisonUser().getUser().getEmail();
          } else {
            ccEmail += ", " + project.getProjecInfoPhase(action.getActualPhase()).getLiaisonUser().getUser().getEmail();
          }
        } else if (project.getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution() != null
          && project.getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution().getCrpProgram() != null
          && project.getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution().getCrpProgram()
            .getProgramType() == 1) {
          // If Managment liason is FL
          List<CrpProgram> crpPrograms = globalUnitProject
            .getGlobalUnit().getCrpPrograms().stream().filter(cp -> cp.getId() == project
              .getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId())
            .collect(Collectors.toList());
          if (crpPrograms != null) {
            if (crpPrograms.size() > 1) {
              // LOG.warn("Crp programs should be 1");
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
            // CC will be also other Cluster Leaders
            for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
              .filter(cl -> cl.isActive() && cl.getPhase().equals(action.getActualPhase()))
              .collect(Collectors.toList())) {
              for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity
                .getCrpClusterActivityLeaders().stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
                if (ccEmail.isEmpty()) {
                  ccEmail += crpClusterActivityLeader.getUser().getEmail();
                } else {
                  ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
                }
              }
            }
          }
        }
      }
    }

    // BBC will be our gmail notification email.
    String bbcEmails = action.getConfig().getEmailNotification();

    // Subject
    String projectRole = null;
    Long plRoleID = Long.parseLong((String) action.getSession().get(APConstants.CRP_PL_ROLE));
    Role plRole = roleDAO.find(plRoleID);
    if (role.getId() == plRole.getId()) {
      projectRole = action.getText("email.project.assigned.PL");
    } else {
      projectRole = action.getText("email.project.assigned.PC");
    }
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();

    String subject = action.getText("email.project.assigned.subject",
      new String[] {projectRole, crp, project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)});


    // message
    StringBuilder message = new StringBuilder();
    // Building the Email message:
    message.append(action.getText("email.dear", new String[] {userAssigned.getFirstName()}));
    message.append(action.getText("email.project.assigned",
      new String[] {projectRole, crp, project.getProjecInfoPhase(action.getActualPhase()).getTitle(),
        project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER)}));
    if (role.getId() == plRole.getId()) {
      message.append(action.getText("email.project.leader.responsabilities"));
    } else {
      message.append(action.getText("email.project.coordinator.responsabilities"));
    }
    message.append(action.getText("email.support", new String[] {crpAdmins}));
    message.append(action.getText("email.getStarted"));
    message.append(action.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  public static byte[] readFully(InputStream stream) throws IOException {
    byte[] buffer = new byte[8192];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    int bytesRead;
    while ((bytesRead = stream.read(buffer)) != -1) {
      baos.write(buffer, 0, bytesRead);
    }
    return baos.toByteArray();
  }
}

