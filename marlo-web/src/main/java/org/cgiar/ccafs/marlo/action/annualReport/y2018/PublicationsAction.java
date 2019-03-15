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

package org.cgiar.ccafs.marlo.action.annualReport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverableDTO;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.Publications2018Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class PublicationsAction extends BaseAction {

  private static final long serialVersionUID = 3381503750646285390L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;
  private UserManager userManager;
  private Publications2018Validator validator;
  private DeliverableManager deliverableManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressDeliverableManager reportSynthesisFlagshipProgressDeliverableManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<Deliverable> deliverables;
  private Phase actualPhase;
  private Integer totalOpenAccess = 0;
  private Integer totalLimited = 0;
  private Integer totalIsis = 0;
  private Integer totalNoIsis = 0;
  private Integer total = 0;


  @Inject
  public PublicationsAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, Publications2018Validator validator,
    CrpProgramManager crpProgramManager, DeliverableManager deliverableManager, ProjectFocusManager projectFocusManager,
    ProjectManager projectManager, ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressDeliverableManager reportSynthesisFlagshipProgressDeliverableManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.deliverableManager = deliverableManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressDeliverableManager = reportSynthesisFlagshipProgressDeliverableManager;
  }


  /**
   * Method to fill the list of deliverables selected by flagships
   * 
   * @param flagshipsLiaisonInstitutions
   * @return
   */
  public List<ReportSynthesisFlagshipProgressDeliverableDTO>
    fillFpPlannedList(List<LiaisonInstitution> flagshipsLiaisonInstitutions) {
    List<ReportSynthesisFlagshipProgressDeliverableDTO> flagshipPlannedList = new ArrayList<>();

    if (deliverableManager.findAll() != null) {

      // Get global unit deliverables
      List<Deliverable> deliverables = new ArrayList<>(deliverableManager.findAll().stream()
        .filter(d -> d.isActive() && d.getDeliverableInfo(actualPhase) != null && d.getDeliverableInfo().isRequired()
          && d.getDeliverableInfo().getDeliverableType() != null
          && d.getDeliverableInfo().getDeliverableType().getId() == 63)
        .collect(Collectors.toList()));

      // Fill all deliverables of the global unit
      for (Deliverable deliverable : deliverables) {
        ReportSynthesisFlagshipProgressDeliverableDTO dto = new ReportSynthesisFlagshipProgressDeliverableDTO();
        if (deliverable.getProject() != null) {
          deliverable.getProject().setProjectInfo(deliverable.getProject().getProjecInfoPhase(actualPhase));
          if (deliverable.getProject().getProjectInfo().getAdministrative() != null
            && deliverable.getProject().getProjectInfo().getAdministrative()) {
            dto.setLiaisonInstitutions(new ArrayList<>());
            dto.getLiaisonInstitutions().add(this.liaisonInstitution);
          } else {
            List<ProjectFocus> projectFocuses = new ArrayList<>(deliverable.getProject().getProjectFocuses().stream()
              .filter(pf -> pf.isActive() && pf.getPhase().getId() == actualPhase.getId())
              .collect(Collectors.toList()));
            List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
            for (ProjectFocus projectFocus : projectFocuses) {
              liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
                .filter(li -> li.isActive() && li.getCrpProgram() != null
                  && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                  && li.getCrp() != null && li.getCrp().equals(this.getLoggedCrp()))
                .collect(Collectors.toList()));
            }
            dto.setLiaisonInstitutions(liaisonInstitutions);
          }
        } else {
          // Fill Supplementary Deliverable programs
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          List<DeliverableProgram> deliverablePrograms = deliverable.getDeliverablePrograms().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().equals(actualPhase)).collect(Collectors.toList());
          for (DeliverableProgram deliverableProgram : deliverablePrograms) {
            liaisonInstitutions.addAll(deliverableProgram.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && li.getCrp() != null && li.getCrp().equals(this.getLoggedCrp()))
              .collect(Collectors.toList()));

          }
          if (liaisonInstitutions != null && !liaisonInstitutions.isEmpty()) {
            dto.setLiaisonInstitutions(liaisonInstitutions);
          } else {
            dto.setLiaisonInstitutions(new ArrayList<>());
            dto.getLiaisonInstitutions().add(this.liaisonInstitution);
          }
        }

        dto.setDeliverable(deliverable);
        flagshipPlannedList.add(dto);
      }

      // Get deleted deliverables
      List<ReportSynthesisFlagshipProgressDeliverable> flagshipProgressDeliverables = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress()
              .getReportSynthesisFlagshipProgressDeliverables() != null) {
              List<ReportSynthesisFlagshipProgressDeliverable> reportSynthesisFlagshipProgressDeliverables =
                new ArrayList<>(
                  reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables()
                    .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (reportSynthesisFlagshipProgressDeliverables != null
                || !reportSynthesisFlagshipProgressDeliverables.isEmpty()) {
                for (ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable : reportSynthesisFlagshipProgressDeliverables) {
                  flagshipProgressDeliverables.add(reportSynthesisFlagshipProgressDeliverable);
                }
              }
            }
          }
        }
      }

      // Get list of deliverables to remove
      List<ReportSynthesisFlagshipProgressDeliverableDTO> removeList = new ArrayList<>();
      for (ReportSynthesisFlagshipProgressDeliverableDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitution.getId());
          if (reportSynthesis != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

              ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverableNew =
                new ReportSynthesisFlagshipProgressDeliverable();
              flagshipProgressDeliverableNew = new ReportSynthesisFlagshipProgressDeliverable();
              flagshipProgressDeliverableNew.setDeliverable(dto.getDeliverable());
              flagshipProgressDeliverableNew
                .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

              if (flagshipProgressDeliverables.contains(flagshipProgressDeliverableNew)) {
                removeLiaison.add(liaisonInstitution);
              }
            }
          }
        }

        for (LiaisonInstitution li : removeLiaison) {
          dto.getLiaisonInstitutions().remove(li);
        }

        if (dto.getLiaisonInstitutions().isEmpty()) {
          removeList.add(dto);
        }
      }

      // Remove Deliverables unselected by flagships
      for (ReportSynthesisFlagshipProgressDeliverableDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }

    }
    return flagshipPlannedList;
  }


  private void fillProjectDeliverablesList(LiaisonInstitution liaisonInstitution) {
    deliverables = new ArrayList<>();
    if (this.isFlagship()) {
      // Fill Project Deliverables of the current flagship
      if (projectFocusManager.findAll() != null) {
        List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
          .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
            && pf.getPhase() != null && pf.getPhase().getId() == actualPhase.getId())
          .collect(Collectors.toList()));

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          List<Deliverable> plannedDeliverables = new ArrayList<>(project.getDeliverables().stream()
            .filter(d -> d.isActive() && d.getDeliverableInfo(actualPhase) != null
              && d.getDeliverableInfo().isCompleted() && d.getDeliverableInfo().getDeliverableType() != null
              && d.getDeliverableInfo().getDeliverableType().getId() == 63)
            .collect(Collectors.toList()));
          for (Deliverable deliverable : plannedDeliverables) {
            deliverable.getDeliverableInfo(actualPhase);
            deliverable.setUsers(deliverable.getUsers(actualPhase));
            deliverable.setDissemination(deliverable.getDissemination(actualPhase));
            deliverable.setPublication(deliverable.getPublication(actualPhase));
            deliverable.setMetadataElements(deliverable.getMetadataElements(actualPhase));
            deliverables.add(deliverable);
          }
        }
      }

      // Fill Supplementary Deliverables
      List<DeliverableProgram> deliverablePrograms = liaisonInstitution.getCrpProgram().getDeliverablePrograms()
        .stream()
        .filter(dp -> dp.isActive() && dp.getPhase().equals(actualPhase) && dp.getDeliverable() != null
          && dp.getDeliverable().isActive() && dp.getDeliverable().getDeliverableInfo(actualPhase) != null
          && dp.getDeliverable().getDeliverableInfo().isRequired()
          && dp.getDeliverable().getDeliverableInfo().getDeliverableType() != null
          && dp.getDeliverable().getDeliverableInfo().getDeliverableType().getId() == 63)
        .collect(Collectors.toList());

      if (deliverablePrograms != null && !deliverablePrograms.isEmpty()) {
        for (DeliverableProgram deliverableProgram : deliverablePrograms) {
          Deliverable deliverable = deliverableProgram.getDeliverable();
          deliverable.getDeliverableInfo(actualPhase);
          deliverable.setUsers(deliverable.getUsers(actualPhase));
          deliverable.setDissemination(deliverable.getDissemination(actualPhase));
          deliverable.setPublication(deliverable.getPublication(actualPhase));
          deliverable.setMetadataElements(deliverable.getMetadataElements(actualPhase));
          deliverables.add(deliverable);
        }
      }
    } else {
      // Fill Project Deliverables of the PMU, removing flagship deletions
      liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressDeliverableDTO> flagshipPlannedList =
        this.fillFpPlannedList(liaisonInstitutions);

      for (ReportSynthesisFlagshipProgressDeliverableDTO reportSynthesisFlagshipProgressDeliverableDTO : flagshipPlannedList) {

        Deliverable deliverable = reportSynthesisFlagshipProgressDeliverableDTO.getDeliverable();
        deliverable.getDeliverableInfo(actualPhase);
        deliverable.setUsers(deliverable.getUsers(actualPhase));
        deliverable.setDissemination(deliverable.getDissemination(actualPhase));
        deliverable.setPublication(deliverable.getPublication(actualPhase));
        deliverable.setMetadataElements(deliverable.getMetadataElements(actualPhase));
        deliverable.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        deliverable.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressDeliverableDTO.getLiaisonInstitutions());
        deliverables.add(deliverable);

      }
    }

  }


  public Long firstFlagship() {
    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
  }


  private void
    flagshipProgressprojectDeliverablesNewData(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> selectedDeliverablesIds = new ArrayList<>();

    for (Deliverable deliverable : deliverables) {
      selectedDeliverablesIds.add(deliverable.getId());
    }

    // Add Deliverable (active =0)
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getDeliverablesValue() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getDeliverablesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisFlagshipProgress().getDeliverablesValue().trim()
        .split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }

      for (Long deliverableId : selectedDeliverablesIds) {
        int index = stList.indexOf(deliverableId);
        if (index < 0) {
          selectedPs.add(deliverableId);
        }
      }

      for (ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverable : reportSynthesisFlagshipProgressDB
        .getReportSynthesisFlagshipProgressDeliverables().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(flagshipProgressDeliverable.getDeliverable().getId())) {
          reportSynthesisFlagshipProgressDeliverableManager
            .deleteReportSynthesisFlagshipProgressDeliverable(flagshipProgressDeliverable.getId());
        }
      }

      for (Long deliverableId : selectedPs) {
        Deliverable deliverable = deliverableManager.getDeliverableById(deliverableId);

        ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverableNew =
          new ReportSynthesisFlagshipProgressDeliverable();

        flagshipProgressDeliverableNew.setDeliverable(deliverable);
        flagshipProgressDeliverableNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressDeliverable> flagshipProgressDeliverables =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressDeliverables().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!flagshipProgressDeliverables.contains(flagshipProgressDeliverableNew)) {
          flagshipProgressDeliverableNew = reportSynthesisFlagshipProgressDeliverableManager
            .saveReportSynthesisFlagshipProgressDeliverable(flagshipProgressDeliverableNew);
        }

      }
    } else {

      // Delete Deliverable (Save with active=1)
      for (Long deliverableId : selectedDeliverablesIds) {
        Deliverable deliverable = deliverableManager.getDeliverableById(deliverableId);

        ReportSynthesisFlagshipProgressDeliverable flagshipProgressPlannedDeliverableNew =
          new ReportSynthesisFlagshipProgressDeliverable();

        flagshipProgressPlannedDeliverableNew.setDeliverable(deliverable);
        flagshipProgressPlannedDeliverableNew.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

        List<ReportSynthesisFlagshipProgressDeliverable> reportSynthesisFlagshipProgressDeliverables =
          reportSynthesisFlagshipProgressDB.getReportSynthesisFlagshipProgressDeliverables().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!reportSynthesisFlagshipProgressDeliverables.contains(flagshipProgressPlannedDeliverableNew)) {
          flagshipProgressPlannedDeliverableNew = reportSynthesisFlagshipProgressDeliverableManager
            .saveReportSynthesisFlagshipProgressDeliverable(flagshipProgressPlannedDeliverableNew);
        }
      }
    }

  }

  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + actualPhase.getName() + "_"
      + actualPhase.getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }


  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }


  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public Long getSynthesisID() {
    return synthesisID;
  }


  public Integer getTotal() {
    return total;
  }


  public Integer getTotalIsis() {
    return totalIsis;
  }


  public Integer getTotalLimited() {
    return totalLimited;
  }


  public Integer getTotalNoIsis() {
    return totalNoIsis;
  }


  public Integer getTotalOpenAccess() {
    return totalOpenAccess;
  }


  public String getTransaction() {
    return transaction;
  }


  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }


  @Override
  public boolean isPMU() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;

  }

  @Override
  public String next() {
    String result = this.save();
    if (result.equals(BaseAction.SUCCESS)) {
      return BaseAction.NEXT;
    } else {
      return result;
    }
  }


  @Override
  public void prepare() throws Exception {
    this.actualPhase = this.getActualPhase();

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    // If there is a history version being loaded
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ReportSynthesis history = (ReportSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        reportSynthesis = history;
        synthesisID = reportSynthesis.getId();
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      // Get Liaison institution ID Parameter
      try {
        liaisonInstitutionID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
      } catch (NumberFormatException e) {
        User user = userManager.getUser(this.getCurrentUser().getId());
        if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
          List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
            .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
              && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId()
              && lu.getLiaisonInstitution().getInstitution() == null)
            .collect(Collectors.toList()));
          if (!liaisonUsers.isEmpty()) {
            boolean isLeader = false;
            for (LiaisonUser liaisonUser : liaisonUsers) {
              LiaisonInstitution institution = liaisonUser.getLiaisonInstitution();
              if (institution.isActive()) {
                if (institution.getCrpProgram() != null) {
                  if (institution.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                } else {
                  if (institution.getAcronym() != null && institution.getAcronym().equals("PMU")) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                }
              }
            }
            if (!isLeader) {
              liaisonInstitutionID = this.firstFlagship();
            }
          } else {
            liaisonInstitutionID = this.firstFlagship();
          }
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      }

      try {
        synthesisID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)));
        reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

        if (!reportSynthesis.getPhase().equals(actualPhase)) {
          reportSynthesis = reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitutionID);
          if (reportSynthesis == null) {
            reportSynthesis = this.createReportSynthesis(actualPhase.getId(), liaisonInstitutionID);
          }
          synthesisID = reportSynthesis.getId();
        }
      } catch (Exception e) {
        reportSynthesis = reportSynthesisManager.findSynthesis(actualPhase.getId(), liaisonInstitutionID);
        if (reportSynthesis == null) {
          reportSynthesis = this.createReportSynthesis(actualPhase.getId(), liaisonInstitutionID);
        }
        synthesisID = reportSynthesis.getId();

      }
    }

    if (reportSynthesis != null) {

      ReportSynthesis reportSynthesisDB = reportSynthesisManager.getReportSynthesisById(synthesisID);
      synthesisID = reportSynthesisDB.getId();
      liaisonInstitutionID = reportSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      this.fillProjectDeliverablesList(liaisonInstitution);

      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        reportSynthesis = (ReportSynthesis) autoSaveReader.readFromJson(jReader);
        synthesisID = reportSynthesis.getId();
        this.setDraft(true);
      } else {
        this.setDraft(false);
        // Check if ToC relation is null -create it
        if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
          ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
          // create one to one relation
          reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
          flagshipProgress.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        reportSynthesis.getReportSynthesisFlagshipProgress().setDeliverables(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisFlagshipProgress()
          .getReportSynthesisFlagshipProgressDeliverables() != null
          && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables()
            .isEmpty()) {
          for (ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverable : reportSynthesis
            .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisFlagshipProgress().getDeliverables()
              .add(flagshipProgressDeliverable.getDeliverable());
          }
        }
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    /** Graphs and Tables */
    List<Deliverable> selectedDeliverables = new ArrayList<Deliverable>();
    if (deliverables != null && !deliverables.isEmpty()) {
      deliverables.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      selectedDeliverables.addAll(deliverables);
      // Remove unchecked deliverables
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getDeliverables() != null
        && !reportSynthesis.getReportSynthesisFlagshipProgress().getDeliverables().isEmpty()) {
        for (Deliverable deliverable : reportSynthesis.getReportSynthesisFlagshipProgress().getDeliverables()) {
          selectedDeliverables.remove(deliverable);
        }
      }
      total = selectedDeliverables.size();

      if (selectedDeliverables != null && !selectedDeliverables.isEmpty()) {
        if (selectedDeliverables != null && !selectedDeliverables.isEmpty()) {
          for (Deliverable deliverable : selectedDeliverables) {

            // Chart: Deliverables open access
            List<DeliverableDissemination> deliverableDisseminations =
              deliverable.getDeliverableInfo(actualPhase).getDeliverable().getDeliverableDisseminations().stream()
                .filter(dd -> dd.isActive() && dd.getPhase() != null && dd.getPhase().equals(actualPhase))
                .collect(Collectors.toList());
            if (deliverableDisseminations != null && !deliverableDisseminations.isEmpty()) {
              deliverable.getDeliverableInfo(actualPhase).getDeliverable()
                .setDissemination(deliverableDisseminations.get(0));
              if (deliverable.getDeliverableInfo(actualPhase).getDeliverable().getDissemination()
                .getIsOpenAccess() != null) {
                // Journal Articles by Open Access
                if (deliverable.getDeliverableInfo(actualPhase).getDeliverable().getDissemination().getIsOpenAccess()) {
                  totalOpenAccess++;
                } else {
                  totalLimited++;
                }
              } else {
                totalLimited++;
              }
            } else {
              totalLimited++;
            }

            // Chart: Deliverables by ISI
            List<DeliverablePublicationMetadata> deliverablePublicationMetadatas =
              deliverable.getDeliverableInfo(actualPhase).getDeliverable().getDeliverablePublicationMetadatas().stream()
                .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(actualPhase))
                .collect(Collectors.toList());
            if (deliverablePublicationMetadatas != null && !deliverablePublicationMetadatas.isEmpty()) {
              deliverable.getDeliverableInfo(actualPhase).getDeliverable()
                .setPublication(deliverablePublicationMetadatas.get(0));
              // Journal Articles by ISI status
              if (deliverable.getDeliverableInfo(actualPhase).getDeliverable().getPublication()
                .getIsiPublication() != null) {
                if (deliverable.getDeliverableInfo(actualPhase).getDeliverable().getPublication().getIsiPublication()) {
                  totalIsis++;
                } else {
                  totalNoIsis++;
                }
              } else {
                totalNoIsis++;
              }
            } else {
              totalNoIsis++;
            }
          }
        }
      }
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedDeliverables() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedDeliverables().clear();
      }
    }

  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFlagshipProgress();

      this.flagshipProgressprojectDeliverablesNewData(reportSynthesisFlagshipProgressDB);

      if (reportSynthesis.getReportSynthesisFlagshipProgress().getPlannedDeliverables() == null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().setPlannedDeliverables(new ArrayList<>());
      }

      reportSynthesisFlagshipProgressDB =
        reportSynthesisFlagshipProgressManager.saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressDB);

      List<String> relationsName = new ArrayList<>();
      reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

      /**
       * The following is required because we need to update something on the @ReportSynthesis if we want a row created
       * in the auditlog table.
       */
      this.setModificationJustification(reportSynthesis);

      reportSynthesisManager.save(reportSynthesis, this.getActionName(), relationsName, actualPhase);

      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      this.getActionMessages();
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
      return NOT_AUTHORIZED;
    }
  }


  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }


  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }


  public void setTotal(Integer total) {
    this.total = total;
  }

  public void setTotalIsis(Integer totalIsis) {
    this.totalIsis = totalIsis;
  }

  public void setTotalLimited(Integer totalLimited) {
    this.totalLimited = totalLimited;
  }


  public void setTotalNoIsis(Integer totalNoIsis) {
    this.totalNoIsis = totalNoIsis;
  }

  public void setTotalOpenAccess(Integer totalOpenAccess) {
    this.totalOpenAccess = totalOpenAccess;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, reportSynthesis, true);
    }
  }
}