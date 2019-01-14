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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
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
  private ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager;


  // Front-end
  private List<Integer> allYears;
  private long deliverableID;
  private List<Deliverable> deliverables;
  private List<DeliverableType> deliverablesType;
  private GlobalUnit loggedCrp;
  private Project project;
  private long projectID;
  private List<ProjectLp6ContributionDeliverable> selectedDeliverables;


  @Inject
  public DeliverableListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    DeliverableTypeManager deliverableTypeManager, DeliverableManager deliverableManager,
    ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager, PhaseManager phaseManager,
    DeliverableInfoManager deliverableInfoManager, SectionStatusManager sectionStatusManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.projectLp6ContributionDeliverableManager = projectLp6ContributionDeliverableManager;
    this.deliverableManager = deliverableManager;
    this.phaseManager = phaseManager;
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
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.addDeliverablePhase(phase.getNext(), deliverable);
        }
      }
    } else {
      if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
        this.addDeliverablePhase(phase.getNext(), deliverable);
      }
    }


    if (deliverableID > 0) {
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


  @Override
  public String delete() {

    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    deliverableID =
      // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0]));
      Long
        .parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0]));


    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    for (SectionStatus sectionStatus : deliverable.getSectionStatuses()) {
      sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
    }

    projectID = deliverable.getProject().getId();

    if (deliverable != null) {
      deliverableManager.saveDeliverable(deliverable);
      deliverableManager.deleteDeliverable(deliverableID);
      this.addActionMessage("message:" + this.getText("deleting.success"));
    }
    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
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

          openA.sort((p1, p2) -> p1.getDeliverableInfo(this.getActualPhase())
            .isRequieriedReporting(this.getCurrentCycleYear())
            .compareTo(p2.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getCurrentCycleYear())));

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


          openA.sort((p1, p2) -> p1.getDeliverableInfo(this.getActualPhase())
            .isRequieriedReporting(this.getCurrentCycleYear())
            .compareTo(p2.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getCurrentCycleYear())));
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
            deliverable.setResponsiblePartner(this.responsiblePartner(deliverable));

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


      if (this.isReportingActive() || this.isUpKeepActive()) {
        deliverables.sort(
          (p1, p2) -> p1.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getCurrentCycleYear())
            .compareTo(p2.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getCurrentCycleYear())));

      }
      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_LIST_BASE_PERMISSION, params));

      // Get selected deliverables
      if (projectLp6ContributionDeliverableManager.findAll() != null) {

        selectedDeliverables = projectLp6ContributionDeliverableManager.findAll().stream()
          .filter(d -> d.getPhase() == this.getActualPhase() && d.getDeliverable().getId() == deliverableID)
          .collect(Collectors.toList());
      }

      if (selectedDeliverables.size() > 0) {

      }


    } catch (Exception e) {
      projectID = -1;
    }

  }


  private DeliverablePartnership responsiblePartner(Deliverable deliverable) {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
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
