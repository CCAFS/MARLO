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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
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

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableListAction extends BaseAction {


  private static final long serialVersionUID = -823169163612346982L;


  private List<Integer> allYears;

  private CrpManager crpManager;


  private long deliverableID;
  private DeliverableManager deliverableManager;
  private DeliverableFundingSourceManager deliverableFundingSourceManager;
  private FundingSourceManager fundingSourceManager;

  // Front-end
  private List<Deliverable> deliverables;
  private List<DeliverableType> deliverablesType;

  private PhaseManager phaseManager;
  private DeliverableTypeManager deliverableTypeManager;
  private DeliverableInfoManager deliverableInfoManager;

  private Crp loggedCrp;
  private Project project;

  private long projectID;
  // Managers
  private ProjectManager projectManager;
  private SectionStatusManager sectionStatusManager;

  @Inject
  public DeliverableListAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    DeliverableTypeManager deliverableTypeManager, DeliverableManager deliverableManager, PhaseManager phaseManager,
    DeliverableInfoManager deliverableInfoManager, SectionStatusManager sectionStatusManager, DeliverableFundingSourceManager deliverableFundingSourceManager,
    FundingSourceManager fundingSourceManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.deliverableManager = deliverableManager;
    this.phaseManager = phaseManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.fundingSourceManager = fundingSourceManager;
  }


  @Override
  public String add() {

    Deliverable deliverable = new Deliverable();

    deliverable.setProject(project);
    deliverable.setCreateDate(new Date());
    deliverable.setCreatedBy(this.getCurrentUser());
    deliverable.setActive(true);
    deliverable.setActiveSince(new Date());
    deliverable.setPhase(this.getActualPhase());
    deliverableID = deliverableManager.saveDeliverable(deliverable).getId();


    List<Integer> years = project.getProjecInfoPhase(this.getActualPhase()).getAllYearsPhase();
    for (Integer year : years) {
      DeliverableInfo deliverableInfo = new DeliverableInfo();
      deliverableInfo.setDeliverable(deliverable);
      deliverableInfo.setPhase(phaseManager.findCycle(APConstants.PLANNING, year, this.getCrpID()));
      deliverableInfo.setYear(this.getCurrentCycleYear());

      deliverableInfo.setModifiedBy(this.getCurrentUser());
      deliverableInfo.setModificationJustification("New expected deliverable created");
      deliverableInfoManager.saveDeliverableInfo(deliverableInfo);

      DeliverableInfo deliverableInfoReporting = new DeliverableInfo();
      deliverableInfoReporting.setDeliverable(deliverable);
      deliverableInfoReporting.setPhase(phaseManager.findCycle(APConstants.REPORTING, year, this.getCrpID()));
      deliverableInfoReporting.setYear(this.getCurrentCycleYear());

      deliverableInfoReporting.setModifiedBy(this.getCurrentUser());
      deliverableInfoReporting.setModificationJustification("New expected deliverable created");
      deliverableInfoManager.saveDeliverableInfo(deliverableInfo);


    }


    if (deliverableID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }

  public boolean canEdit(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (this.isPlanningActive()) {
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getCurrentCycleYear()) {
        return true;
      }
      return false;
    }
    return true;
  }


  @Override
  public String delete() {

    Map<String, Object> parameters = this.getParameters();
    deliverableID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0]));


    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    for (SectionStatus sectionStatus : deliverable.getSectionStatuses()) {
      sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
    }

    projectID = deliverable.getProject().getId();

    if (deliverable != null) {
      deliverable.setActiveSince(new Date());
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

  public List<Deliverable> getDeliverables(boolean open, boolean closed) {

    try {
      if (open) {
        if (this.isPlanningActive()) {
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

          List<Deliverable> openA = deliverables.stream()
            .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
              && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
                || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())
                || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0))))
            .collect(Collectors.toList());

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

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
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

          List<DeliverableInfo> infos = phase.getDeliverableInfos()
            .stream().filter(c -> c.getDeliverable().getProject() != null
              && c.getDeliverable().getProject().equals(project) && c.getDeliverable().isActive())
            .collect(Collectors.toList());
          deliverables = new ArrayList<>();
          for (DeliverableInfo deliverableInfo : infos) {
            Deliverable deliverable = deliverableInfo.getDeliverable();
            deliverable.setDeliverableInfo(deliverableInfo);
            deliverables.add(deliverable);
          }

          for (Deliverable deliverable : deliverables) {
            deliverable.setResponsiblePartner(this.responsiblePartner(deliverable));

            // Gets the Deliverable Funding Source Data without the full information.
            List<DeliverableFundingSource> fundingSources = new ArrayList<>(deliverable.getDeliverableFundingSources()
              .stream().filter(c -> c.isActive()).collect(Collectors.toList()));


            deliverable.setFundingSources(fundingSources);
          }
        }
      }


      if (this.isReportingActive()) {
        deliverables.sort(
          (p1, p2) -> p1.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getCurrentCycleYear())
            .compareTo(p2.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getCurrentCycleYear())));

      }
      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_LIST_BASE_PERMISSION, params));

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

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


}
