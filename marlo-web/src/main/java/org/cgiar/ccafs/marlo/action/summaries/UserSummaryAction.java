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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSummaryAction extends BaseSummariesAction implements Summary {

  private static Logger LOG = LoggerFactory.getLogger(UserSummaryAction.class);
  private static final long serialVersionUID = -7520542049583979099L;

  // Parameters
  private byte[] bytesXLSX;
  InputStream inputStream;
  private long startTime;
  // Keys to be searched
  List<String> keys = new ArrayList<String>();

  // Managers
  private UserManager userManager;
  private GlobalUnitManager crpManager;
  private PhaseManager phaseManager;

  @Inject
  public UserSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager, UserManager userManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {

    try {
      String parameters = this.getRequest().getParameter("keys");
      if (parameters != null) {
        if (parameters.isEmpty()) {
          // Empty keys
        } else {
          keys = Arrays.asList(parameters.split(","));
        }
      }
      this.getUsersTableModel();
    } catch (Exception e) {
      LOG.error("Error generating UserSummary " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
    return INPUT;

  }

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("UserSummary-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
    return fileName.toString();
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }

  private void getUsersTableModel() {
    String keysString = "";
    int countKeys = 0;
    if (!keys.isEmpty()) {
      for (GlobalUnit globalUnit : crpManager.findAll().stream().filter(gu -> gu.isActive())
        .collect(Collectors.toList())) {

        Phase globalUnitPhase = phaseManager.findCycle(this.getSelectedPhase().getDescription(),
          this.getSelectedPhase().getYear(), this.getSelectedPhase().getUpkeep(), globalUnit.getId());

        for (String key : keys) {
          Boolean hasResponsabilities = false;
          User user = userManager.getUserByEmail(key);
          if (user != null) {

            List<UserRole> userRoles = user.getUserRoles().stream()
              .filter(ur -> ur.getRole().getCrp().equals(globalUnit)).collect(Collectors.toList());
            String roles = "";
            Set<String> noRepeatRoles = new HashSet<>();
            if (userRoles != null && !userRoles.isEmpty()) {
              hasResponsabilities = true;
              for (UserRole userRole : userRoles) {
                noRepeatRoles.add(userRole.getRole().getAcronymDimanic());
              }
              roles = StringUtils.join(noRepeatRoles, ',');
            }

            List<CrpProgramLeader> crpFlagshipLeaders = user.getCrpProgramLeaders().stream()
              .filter(cpl -> cpl.isActive() && !cpl.isManager()
                && cpl.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && cpl.getCrpProgram().getCrp().equals(globalUnit))
              .collect(Collectors.toList());
            String flagshipLeaders = "";
            Set<String> noRepeatFlagshipsLeaders = new HashSet<>();
            if (crpFlagshipLeaders != null && !crpFlagshipLeaders.isEmpty()) {
              hasResponsabilities = true;
              for (CrpProgramLeader crpProgramLeader : crpFlagshipLeaders) {
                noRepeatFlagshipsLeaders.add(crpProgramLeader.getCrpProgram().getAcronym());
              }
              flagshipLeaders = StringUtils.join(noRepeatFlagshipsLeaders, ',');
            }


            List<CrpProgramLeader> crpFlagshipManagers = user.getCrpProgramLeaders().stream()
              .filter(cpl -> cpl.isActive() && cpl.isManager()
                && cpl.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && cpl.getCrpProgram().getCrp().equals(globalUnit))
              .collect(Collectors.toList());
            String flagshipManagers = "";
            Set<String> noRepeatFlagshipsManagers = new HashSet<>();
            if (crpFlagshipManagers != null && !crpFlagshipManagers.isEmpty()) {
              hasResponsabilities = true;
              for (CrpProgramLeader crpProgramLeader : crpFlagshipManagers) {
                noRepeatFlagshipsManagers.add(crpProgramLeader.getCrpProgram().getAcronym());
              }
              flagshipManagers = StringUtils.join(noRepeatFlagshipsManagers, ',');
            }

            List<CrpProgramLeader> crpRegionLeaders = user.getCrpProgramLeaders().stream()
              .filter(cpl -> cpl.isActive() && !cpl.isManager()
                && cpl.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
                && cpl.getCrpProgram().getCrp().equals(globalUnit))
              .collect(Collectors.toList());
            String regionLeaders = "";
            Set<String> noRepeatRegionsLeaders = new HashSet<>();
            if (crpRegionLeaders != null && !crpRegionLeaders.isEmpty()) {
              hasResponsabilities = true;
              for (CrpProgramLeader crpProgramLeader : crpRegionLeaders) {
                noRepeatRegionsLeaders.add(crpProgramLeader.getCrpProgram().getAcronym());
              }
              regionLeaders = StringUtils.join(noRepeatRegionsLeaders, ',');
            }

            List<CrpProgramLeader> crpRegionManagers = user.getCrpProgramLeaders().stream()
              .filter(cpl -> cpl.isActive() && cpl.isManager()
                && cpl.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
                && cpl.getCrpProgram().getCrp().equals(globalUnit))
              .collect(Collectors.toList());
            String regionManagers = "";
            Set<String> noRepeatRegionsManagers = new HashSet<>();
            if (crpRegionManagers != null && !crpRegionManagers.isEmpty()) {
              hasResponsabilities = true;
              for (CrpProgramLeader crpProgramLeader : crpRegionManagers) {
                noRepeatRegionsManagers.add(crpProgramLeader.getCrpProgram().getAcronym());
              }
              regionManagers = StringUtils.join(noRepeatRegionsManagers, ',');
            }


            List<CrpClusterActivityLeader> crpClusterActivityLeaders = user.getCrpClusterActivityLeaders().stream()
              .filter(cl -> cl.isActive() && cl.getCrpClusterOfActivity().isActive()
                && cl.getCrpClusterOfActivity().getPhase().equals(globalUnitPhase))
              .collect(Collectors.toList());
            String clusterActivityLeaders = "";
            Set<String> noRepeatclusterActivityLeaders = new HashSet<>();
            if (crpClusterActivityLeaders != null && !crpClusterActivityLeaders.isEmpty()) {
              hasResponsabilities = true;
              for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterActivityLeaders) {
                noRepeatclusterActivityLeaders.add(crpClusterActivityLeader.getCrpClusterOfActivity().getIdentifier());
              }
              clusterActivityLeaders = StringUtils.join(noRepeatclusterActivityLeaders, ',');
            }

            // TODO: Site Integration(s)
            // TODO: Management Liaison Institution(s)


            List<ProjectPartnerPerson> projectPartnerPersonLeaders = user.getProjectPartnerPersons().stream()
              .filter(ppp -> ppp.isActive() && ppp.getContactType().equalsIgnoreCase("PL")
                && ppp.getProjectPartner().isActive() && ppp.getProjectPartner().getPhase().equals(globalUnitPhase))
              .collect(Collectors.toList());
            String personLeaders = "";
            Set<String> noRepeatPersonLeaders = new HashSet<>();
            if (projectPartnerPersonLeaders != null && !projectPartnerPersonLeaders.isEmpty()) {
              hasResponsabilities = true;
              for (ProjectPartnerPerson projectPartnerPersonLeader : projectPartnerPersonLeaders) {
                noRepeatPersonLeaders
                  .add(projectPartnerPersonLeader.getProjectPartner().getProject().getId().toString());
              }
              personLeaders = StringUtils.join(noRepeatPersonLeaders, ',');
            }

            List<ProjectPartnerPerson> projectPartnerPersonCoordinators = user.getProjectPartnerPersons().stream()
              .filter(ppp -> ppp.isActive() && ppp.getContactType().equalsIgnoreCase("PC")
                && ppp.getProjectPartner().isActive() && ppp.getProjectPartner().getPhase().equals(globalUnitPhase))
              .collect(Collectors.toList());
            String personCoordinators = "";
            Set<String> noRepeatPersonCoordinators = new HashSet<>();
            if (projectPartnerPersonCoordinators != null && !projectPartnerPersonCoordinators.isEmpty()) {
              hasResponsabilities = true;
              for (ProjectPartnerPerson projectPartnerPersonCoordinator : projectPartnerPersonCoordinators) {
                noRepeatPersonCoordinators
                  .add(projectPartnerPersonCoordinator.getProjectPartner().getProject().getId().toString());
              }
              personCoordinators = StringUtils.join(noRepeatPersonCoordinators, ',');
            }

            if (hasResponsabilities) {
              System.out.println("Global Unit: " + globalUnit.getAcronym());
              System.out.println("User ID: " + user.getId());
              System.out.println("Name: " + user.getComposedCompleteName());
              System.out.println("Email: " + user.getEmail());
              System.out.println("Last Login (Coordinated Universal Time): " + user.getLastLogin());
              System.out.println("Role(s): " + roles);
              System.out.println("Flagship(s) Leader: " + flagshipLeaders);
              System.out.println("Flagship(s) Manager: " + flagshipManagers);
              System.out.println("Region(s) Leader: " + regionLeaders);
              System.out.println("Region(s) Manager: " + regionManagers);
              System.out.println("Cluster Leader: " + clusterActivityLeaders);
              System.out.println("Project(s) leader: " + personLeaders);
              System.out.println("Project(s) coordinator: " + personCoordinators);
            }

          } else {
            System.out.println("User not found for email " + key);
          }


        }
      }

    } else {
      System.out.println("No email added");
    }


  }

  @Override
  public void prepare() throws Exception {
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

}
