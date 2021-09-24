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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipPersonManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;


/**
 * DeliverablesReplicationAction:
 * 
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class CrpDeliverablesAction extends BaseAction {

  private static final long serialVersionUID = 6392973543544674655L;

  // Managers
  private DeliverableManager deliverableManager;
  private DeliverableInfoManager deliverableInfoManager;
  private PhaseManager phaseManager;
  private ProjectManager projectManager;
  private GlobalUnitManager globalUnitManager;
  private DeliverableUserPartnershipManager deliverableUserPartnershipManager;
  private DeliverableUserPartnershipPersonManager deliverableUserPartnerPersonshipManager;


  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private List<Phase> phases;
  private long selectedPhaseID;
  private Long phaseID;
  private Long deliverableID;
  private Long projectID;
  private List<Deliverable> deliverables;
  private List<Project> projects;
  private String moveToSelection;


  @Inject
  public CrpDeliverablesAction(APConfig config, PhaseManager phaseManager, DeliverableManager deliverableManager,
    DeliverableInfoManager deliverableInfoManager, ProjectManager projectManager, GlobalUnitManager globalUnitManager,
    DeliverableUserPartnershipManager deliverableUserPartnershipManager,
    DeliverableUserPartnershipPersonManager deliverableUserPartnerPersonshipManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.deliverableManager = deliverableManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.projectManager = projectManager;
    this.globalUnitManager = globalUnitManager;
    this.deliverableUserPartnershipManager = deliverableUserPartnershipManager;
    this.deliverableUserPartnerPersonshipManager = deliverableUserPartnerPersonshipManager;
  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }

  public Long getDeliverableID() {
    return deliverableID;
  }

  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  public String getEntityByPhaseList() {
    return entityByPhaseList;
  }

  public String getMoveToSelection() {
    return moveToSelection;
  }


  @Override
  public Long getPhaseID() {
    return phaseID;
  }

  @Override
  public List<Phase> getPhases() {
    return phases;
  }

  public Long getProjectID() {
    return projectID;
  }

  public List<Project> getProjects() {
    return projects;
  }

  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  public void moveDeliverablesPhase() {

    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    Phase phaseToMove = phaseManager.getPhaseById(phaseID);

    if (deliverable != null && deliverable.getDeliverableInfo(this.getActualPhase()) != null) {
      List<DeliverableInfo> deliverableInfos = deliverableInfoManager
        .findAll().stream().filter(di -> di != null && di.getDeliverable() != null && di.isActive()
          && di.getDeliverable().getId() != null && di.getDeliverable().getId().equals(deliverable.getId()))
        .collect(Collectors.toList());

      if (deliverableInfos != null && !deliverableInfos.isEmpty()) {

        // Change the created phase in deliverable and create the deliverable info for the 'phase to move'
        deliverable.setPhase(phaseToMove);
        deliverableManager.saveDeliverable(deliverable);

        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(this.getActualPhase());
        DeliverableInfo deliverableInfoToMove = deliverableInfo;


        if (deliverable.getDeliverableInfo(phaseToMove) == null) {
          deliverableInfoToMove.setPhase(phaseToMove);
          deliverableInfoManager.saveDeliverableInfo(deliverableInfoToMove);
        }

        for (DeliverableInfo info : deliverableInfos) {
          if (info.getPhase() != null && info.getPhase().getId() != null) {

            // Disable deliverables info with previous phases to 'phase to move'
            if (info.getPhase().getId() < phaseToMove.getId()) {
              info.setActive(false);
              deliverableInfoManager.saveDeliverableInfo(info);
            }

            // Create deliverables info with next phases to 'phase to move'
            if (info.getPhase().getId() > phaseToMove.getId()) {
              info.setActive(true);
              deliverableInfoManager.saveDeliverableInfo(info);
            }
          }
        }

        // Create deliverable info in phases (when deliverable is moved to previous phases)
        List<Phase> phaseList =
          phases.stream().filter(f -> f.getId() < this.getActualPhase().getId() && f.getId() >= phaseToMove.getId())
            .collect(Collectors.toList());
        for (Phase phase : phaseList) {
          // If deliverable info doesnt exist in the phase, tis created
          if (deliverableInfoManager.getDeliverablesInfoByPhase(phase) == null) {
            deliverableInfoToMove.setPhase(phase);
            deliverableInfoManager.saveDeliverableInfo(deliverableInfoToMove);
          }
          deliverableInfoToMove.setActive(true);
          deliverableInfoManager.saveDeliverableInfo(deliverableInfoToMove);
        }

      }
    }

  }

  public void moveDeliverablesProject() {
    if (deliverableID != 0) {
      Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
      Project project = projectManager.getProjectById(projectID);

      // Change the project for selected deliverable
      if (deliverable != null) {
        deliverable.setProject(project);
        deliverableManager.saveDeliverable(deliverable);
      }
    }
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    crps = globalUnitManager.findAll().stream()
      .filter(c -> c.isMarlo() && c.isActive() && c.getAcronym().equals(this.getCurrentCrp().getAcronym()))
      .collect(Collectors.toList());

    phases = phaseManager.findAll().stream()
      .filter(c -> c.isActive() && c.getCrp() != null && this.getCurrentCrp() != null
        && c.getCrp().getId().equals(this.getCurrentCrp().getId()) && (c.getId() > this.getActualPhase().getId()))
      .collect(Collectors.toList());

    deliverables = deliverableManager.getDeliverablesByPhase(this.getActualPhase().getId()).stream()
      .filter(d -> d != null && d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
        && d.getDeliverableInfo(this.getActualPhase()).isActive()
        && d.getDeliverableInfo(this.getActualPhase()).getStatus() != 5 && d.getProject() != null
        && d.getProject().getProjecInfoPhase(this.getActualPhase()) != null && d.getProject().isActive()
        && d.getProject().getProjecInfoPhase(this.getActualPhase()).isActive()
        && d.getProject().getProjecInfoPhase(this.getActualPhase()).getStatus() != null
        && d.getProject().getProjecInfoPhase(this.getActualPhase()).getStatus() != 5)
      .collect(Collectors.toList());

    if (deliverables != null && !deliverables.isEmpty()) {
      for (Deliverable deliverable : deliverables) {
        if (deliverable != null && deliverable.getId() != null) {
          deliverable = deliverableManager.getDeliverableById(deliverable.getId());
          deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
        }
      }
    }

    String[] statuses = null;
    projects = projectManager.getActiveProjectsByPhase(this.getActualPhase(), 0, statuses).stream()
      .filter(p -> p != null && p.isActive() && p.getProjecInfoPhase(this.getActualPhase()) != null
        && p.getProjecInfoPhase(this.getActualPhase()).isActive() && p.getProjectInfo() != null
        && p.getProjectInfo().getStatus() != null && p.getProjectInfo().getStatus() != 3
        && p.getProjectInfo().getStatus() != 5)
      .collect(Collectors.toList());

    if (projects != null && !projects.isEmpty()) {
      for (Project project : projects) {
        if (project != null && project.getId() != null) {
          project = projectManager.getProjectById(project.getId());
          project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        }
      }
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (deliverableID != null && deliverableID != 0 && moveToSelection != null && !moveToSelection.isEmpty()) {

        switch (moveToSelection) {
          case "project":
            if (projectID != null && projectID != 0) {
              this.moveDeliverablesProject();
            }
            break;
          case "phase":
            if (phaseID != null && phaseID != 0) {
              this.moveDeliverablesPhase();
            }
            break;
        }
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }

  public void setDeliverableID(Long deliverableID) {
    this.deliverableID = deliverableID;
  }

  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setEntityByPhaseList(String entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }

  public void setMoveToSelection(String moveToSelection) {
    this.moveToSelection = moveToSelection;
  }

  @Override
  public void setPhaseID(Long phaseID) {
    this.phaseID = phaseID;
  }

  public void setPhases(List<Phase> phases) {
    this.phases = phases;
  }

  public void setProjectID(Long projectID) {
    this.projectID = projectID;
  }

  public void setProjects(List<Project> projects) {
    this.projects = projects;
  }

  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }

  public void validateDeliverablePartners() {

    // Get deliverables partners
    List<DeliverableUserPartnership> deliverableUserPartnerships =
      deliverableUserPartnershipManager.findByDeliverableID(deliverableID);

    if (deliverableUserPartnerships != null && !deliverableUserPartnerships.isEmpty()) {
      for (DeliverableUserPartnership userPartnerships : deliverableUserPartnerships) {
        List<DeliverableUserPartnershipPerson> persons = userPartnerships.getDeliverableUserPartnershipPersons()
          .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
        if (persons != null && !persons.isEmpty()) {
          // Compare with project partners
        }
      }

    }

    // Get project Partners

  }


}