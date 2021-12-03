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
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableListAction extends BaseAction {


  private static final long serialVersionUID = -823169163612346982L;


  // Managers
  private GlobalUnitManager crpManager;
  private DeliverableManager deliverableManager;
  private PhaseManager phaseManager;
  private DeliverableTypeManager deliverableTypeManager;
  private DeliverableInfoManager deliverableInfoManager;
  private ProjectManager projectManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectDeliverableSharedManager projectDeliverableSharedManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressDeliverableManager flagshipProgressDeliverableManager;


  // Front-end
  private List<Integer> allYears;
  private long deliverableID;
  private List<Deliverable> deliverables;
  private List<Deliverable> currentDeliverableList;
  private List<DeliverableType> deliverablesType;
  private GlobalUnit loggedCrp;
  private Project project;
  private long projectID;


  @Inject
  public DeliverableListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    DeliverableTypeManager deliverableTypeManager, DeliverableManager deliverableManager, PhaseManager phaseManager,
    DeliverableInfoManager deliverableInfoManager, SectionStatusManager sectionStatusManager,
    ProjectDeliverableSharedManager projectDeliverableSharedManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressDeliverableManager flagshipProgressDeliverableManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.deliverableManager = deliverableManager;
    this.phaseManager = phaseManager;
    this.projectDeliverableSharedManager = projectDeliverableSharedManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.flagshipProgressDeliverableManager = flagshipProgressDeliverableManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  @Override
  public String add() {

    Deliverable deliverable = new Deliverable();

    deliverable.setProject(project);
    deliverable.setCreateDate(new Date());
    deliverable.setPhase(this.getActualPhase());
    deliverableID = deliverableManager.saveDeliverable(deliverable).getId();


    Phase phase = this.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());
    DeliverableInfo deliverableInfo = new DeliverableInfo();
    deliverableInfo.setDeliverable(deliverable);
    deliverableInfo.setPhase(phase);
    deliverableInfo.setYear(this.getCurrentCycleYear());
    deliverableInfo.setStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
    deliverableInfo.setModificationJustification("New expected deliverable created");
    deliverableInfoManager.saveDeliverableInfo(deliverableInfo);

    // Replicate only for Planning
    // if (phase.getDescription().equals(APConstants.REPORTING)) {
    // if (phase.getNext() != null && phase.getNext().getNext() != null) {
    // Phase upkeepPhase = phase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.addDeliverablePhase(upkeepPhase, deliverable);
    // }
    // }
    // } else {
    // if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
    // this.addDeliverablePhase(phase.getNext(), deliverable);
    // }
    // }

    if (deliverableID > 0) {
      SectionStatus sectionStatus =
        this.sectionStatusManager.getSectionStatusByIndicator(this.getCurrentCycle(), this.getCurrentCycleYear(),
          this.isUpKeepActive(), ProjectSectionStatusEnum.DELIVERABLES.getStatus(), this.project.getId());
      if (sectionStatus != null) {
        sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
      }

      this.createSynthesisAssociation(deliverable);

      return SUCCESS;
    }

    return INPUT;
  }

  public void addDeliverablePhase(Phase phase, Deliverable deliverable) {
    phase = phaseManager.getPhaseById(phase.getId());
    DeliverableInfo deliverableInfo = new DeliverableInfo();
    deliverableInfo.setDeliverable(deliverable);
    deliverableInfo.setPhase(phase);
    deliverableInfo.setYear(this.getCurrentCycleYear());
    deliverableInfo.setStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
    deliverableInfo.setModificationJustification("New expected deliverable created");
    deliverableInfoManager.saveDeliverableInfo(deliverableInfo);
    if (phase.getNext() != null) {
      this.addDeliverablePhase(phase.getNext(), deliverable);
    }
  }

  public boolean canEdit(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (this.isPlanningActive() && !this.isUpKeepActive()) {
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getCurrentCycleYear()) {
        return true;
      }
      return false;
    }
    return true;
  }


  private void createSynthesisAssociation(Deliverable deliverable) {
    LiaisonInstitution liaisonInstitution = this.liaisonInstitutionManager.findByAcronymAndCrp("PMU", this.getCrpID());
    if (liaisonInstitution != null) {
      ReportSynthesis reportSynthesis =
        this.reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());
      if (reportSynthesis != null) {
        ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverable =
          new ReportSynthesisFlagshipProgressDeliverable();
        // isActive means excluded
        flagshipProgressDeliverable.setActive(true);
        flagshipProgressDeliverable.setCreatedBy(this.getCurrentUser());
        flagshipProgressDeliverable.setDeliverable(deliverable);
        flagshipProgressDeliverable
          .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

        flagshipProgressDeliverable = this.flagshipProgressDeliverableManager
          .saveReportSynthesisFlagshipProgressDeliverable(flagshipProgressDeliverable);
      }
    }
  }

  @Override
  public String delete() {

    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    Phase phase = this.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());
    deliverableID =
      // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0]));
      Long
        .parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0]));


    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (deliverable != null) {
      if (deliverable.getSectionStatuses() != null) {
        for (SectionStatus sectionStatus : deliverable.getSectionStatuses()) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
      }

      projectID = deliverable.getProject().getId();
      project = deliverable.getProject();

      deliverableManager.saveDeliverable(deliverable);
      deliverableManager.deleteDeliverable(deliverableID);
      this.addActionMessage("message:" + this.getText("deleting.success"));

      SectionStatus sectionStatus =
        this.sectionStatusManager.getSectionStatusByIndicator(this.getCurrentCycle(), this.getCurrentCycleYear(),
          this.isUpKeepActive(), ProjectSectionStatusEnum.DELIVERABLES.getStatus(), this.projectID);

      this.loadAllDeliverables(phase);
      this.loadCurrentDeliverables();

      List<Deliverable> activeDeliverables = CollectionUtils.emptyIfNull(this.currentDeliverableList).stream()
        .filter(d -> d != null && d.getId() != null && d.isActive()).collect(Collectors.toList());

      if (this.isEmpty(activeDeliverables)) {
        if (sectionStatus == null) {
          sectionStatus = new SectionStatus();
          sectionStatus.setCycle(this.getCurrentCycle());
          sectionStatus.setYear(this.getCurrentCycleYear());
          sectionStatus.setUpkeep(this.isUpKeepActive());
          sectionStatus.setSectionName(ProjectSectionStatusEnum.DELIVERABLES.getStatus());
          sectionStatus.setProject(this.project);
        }

        sectionStatus.setMissingFields(APConstants.STATUS_EMPTY_DELIVERABLE_LIST);
        sectionStatus = this.sectionStatusManager.saveSectionStatus(sectionStatus);
      }
    }
    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }

  public List<Deliverable> getCurrentDeliverableList() {
    return currentDeliverableList;
  }

  public long getDeliverableID() {
    return deliverableID;
  }

  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  /**
   * @param open: Load Ongoing and Extended if true
   * @param closed: Load Cancelled if true, Completed if false
   * @return
   */
  public List<Deliverable> getDeliverables(boolean open, boolean closed) {

    try {
      if (open) {
        if (this.isPlanningActive() && !this.isUpKeepActive()) {
          List<Deliverable> openA = deliverables.stream()
            .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
              && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
                || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == -1))))
            .collect(Collectors.toList());
          return openA;
        } else {
          // Reporting
          List<Deliverable> openA = deliverables.stream()
            .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
              && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
                || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0))))
            .collect(Collectors.toList());

          if (closed) {
            openA.addAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));

            openA.addAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this
                  .getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));
          }

          openA.sort((p1, p2) -> this.isDeliverableComplete(p1.getId(), this.getActualPhase().getId())
            .compareTo(this.isDeliverableComplete(p2.getId(), this.getActualPhase().getId())));

          HashSet<Deliverable> deliverables = new HashSet<>();
          deliverables.addAll(openA);
          openA.clear();
          openA.addAll(deliverables);
          return openA;
        }


      } else {
        if (this.isPlanningActive()) {
          List<Deliverable> openA = new ArrayList<>();
          if (closed) {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Cancelled.getStatusId()))))))
              .collect(Collectors.toList());

          } else {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Complete.getStatusId()))))))
              .collect(Collectors.toList());
          }

          return openA;
        } else {
          List<Deliverable> openA = new ArrayList<>();
          if (closed) {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Cancelled.getStatusId()))))))
              .collect(Collectors.toList());

          } else {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Complete.getStatusId()))))))
              .collect(Collectors.toList());
          }

          if (closed) {
            openA.removeAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));

            openA.removeAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this
                  .getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));
          }


          openA.sort((p1, p2) -> this.isDeliverableComplete(p1.getId(), this.getActualPhase().getId())
            .compareTo(this.isDeliverableComplete(p2.getId(), this.getActualPhase().getId())));
          HashSet<Deliverable> deliverables = new HashSet<>();
          deliverables.addAll(openA);
          openA.clear();
          openA.addAll(deliverables);
          return openA;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public List<DeliverableType> getDeliverablesType() {
    return deliverablesType;
  }

  public int getIndexDeliverables(long id) {
    Deliverable activity = new Deliverable();
    activity.setId(id);
    return deliverables.indexOf(activity);

  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  /**
   * @param phase
   */
  private void loadAllDeliverables(Phase phase) {
    if (project.getDeliverables() != null) {

      List<DeliverableInfo> infos = deliverableInfoManager.getDeliverablesInfoByProjectAndPhase(phase, project);
      deliverables = new ArrayList<>();
      if (infos != null && !infos.isEmpty()) {
        for (DeliverableInfo deliverableInfo : infos) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.setDeliverableInfo(deliverableInfo);
          deliverables.add(deliverable);
        }
      }

      for (Deliverable deliverable : deliverables) {
        deliverable.setResponsiblePartnership(this.responsiblePartner(deliverable));

        // Gets the Deliverable Funding Source Data without the full information.
        List<DeliverableFundingSource> fundingSources =
          new ArrayList<>(deliverable.getDeliverableFundingSources().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
        for (DeliverableFundingSource deliverableFundingSource : fundingSources) {
          deliverableFundingSource.getFundingSource().setFundingSourceInfo(
            deliverableFundingSource.getFundingSource().getFundingSourceInfo(this.getActualPhase()));
        }

        deliverable.setFundingSources(fundingSources);
      }
    }
  }


  /*
   * Copy method from project.getCurrentDeliverables to allow add the shared deliverables to list
   */
  public void loadCurrentDeliverables() {
    currentDeliverableList = new ArrayList<>();
    currentDeliverableList = this.getDeliverables().stream().filter(
      d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null && !d.getDeliverableInfo().isPrevious())
      .collect(Collectors.toList());

    // Load Shared deliverables
    List<ProjectDeliverableShared> deliverableShared = CollectionUtils
      .emptyIfNull(
        this.projectDeliverableSharedManager.getByProjectAndPhase(project.getId(), this.getActualPhase().getId()))
      .stream()
      .filter(px -> px != null && px.getId() != null && px.isActive() && px.getDeliverable() != null
        && px.getDeliverable().getId() != null && px.getDeliverable().isActive()
        && px.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null)
      .collect(Collectors.toList());

    if (deliverableShared != null && !deliverableShared.isEmpty()) {
      for (ProjectDeliverableShared deliverableS : deliverableShared) {
        if (!currentDeliverableList.contains(deliverableS.getDeliverable())) {
          currentDeliverableList.add(deliverableS.getDeliverable());
        }
      }
    }

    if (currentDeliverableList != null && !currentDeliverableList.isEmpty()) {
      currentDeliverableList.stream().sorted((d1, d2) -> d1.getId().compareTo((d2.getId())))
        .collect(Collectors.toList());
      // deliverables.addAll(currentDeliverableList);
    }

  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase = this.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      project = projectManager.getProjectById(projectID);

      if (project != null) {

        allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();

        if (deliverableTypeManager.findAll() != null) {
          deliverablesType = new ArrayList<>(deliverableTypeManager.findAll());
        }

        this.loadAllDeliverables(phase);

        this.loadCurrentDeliverables();
      }


      if (this.isReportingActive() || this.isUpKeepActive()) {
        deliverables.sort((p1, p2) -> this.isDeliverableComplete(p1.getId(), this.getActualPhase().getId())
          .compareTo(this.isDeliverableComplete(p2.getId(), this.getActualPhase().getId())));

      }
      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_LIST_BASE_PERMISSION, params));

    } catch (Exception e) {
      projectID = -1;
    }

  }

  private List<DeliverableUserPartnership> responsiblePartner(Deliverable deliverable) {
    try {
      List<DeliverableUserPartnership> partnerships = deliverable.getDeliverableUserPartnerships().stream()
        .filter(dp -> dp.isActive()
          && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
        .collect(Collectors.toList());

      return partnerships;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public String save() {
    return SUCCESS;
  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  public void setCurrentDeliverableList(List<Deliverable> currentDeliverableList) {
    this.currentDeliverableList = currentDeliverableList;
  }

  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }

  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setDeliverablesType(List<DeliverableType> deliverablesType) {
    this.deliverablesType = deliverablesType;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }
}
