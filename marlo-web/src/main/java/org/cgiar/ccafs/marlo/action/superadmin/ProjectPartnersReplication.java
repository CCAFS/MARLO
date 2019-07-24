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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.action.projects.ProjectBudgetByPartnersAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.ProjectSectionValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ProjectBudgetByPartnersReplicationAction:
 * 
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class ProjectPartnersReplication extends BaseAction {

  private static final long serialVersionUID = 6392973543544674655L;

  private static Logger logger = LoggerFactory.getLogger(ProjectPartnersReplication.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private ProjectInfoManager projectInfoManager;
  private ProjectSectionValidator<ProjectBudgetByPartnersAction> projectSectionValidator;
  private ProjectPartnerManager projectPartnerManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase phase;
  private Project project;

  @Inject
  public ProjectPartnersReplication(APConfig config, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    ProjectManager projectManager, ProjectPartnerManager projectPartnerManager,
    ProjectSectionValidator<ProjectBudgetByPartnersAction> projectSectionValidator,
    ProjectInfoManager projectInfoManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectManager = projectManager;
    this.projectPartnerManager = projectPartnerManager;
    this.projectSectionValidator = projectSectionValidator;
    this.projectInfoManager = projectInfoManager;
  }

  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + phase.getName() + "_" + phase.getYear()
      + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<GlobalUnit> getCrps() {
    return crps;
  }


  public String getEntityByPhaseList() {
    return entityByPhaseList;
  }


  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  @Override
  public void prepare() throws Exception {
    super.prepare();
    crps = globalUnitManager.findAll().stream().filter(c -> c.isMarlo() && c.isActive()).collect(Collectors.toList());
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (entityByPhaseList != null && !entityByPhaseList.isEmpty()) {
        logger.debug("Start replication for phase: " + selectedPhaseID);
        phase = phaseManager.getPhaseById(selectedPhaseID);

        for (String id : entityByPhaseList.trim().split(",")) {
          logger.debug("Replicating project: " + id);
          project = projectManager.getProjectById(new Long(id.trim()));

          if (project.getProjecInfoPhase(phase) != null && phase.getNext() != null) {

            // Load relations for auditlog
            List<String> relationsName = new ArrayList<>();
            relationsName.add(APConstants.PROJECT_PARTNERS_RELATION);
            relationsName.add(APConstants.PROJECT_LESSONS_RELATION);
            relationsName.add(APConstants.PROJECT_INFO_RELATION);

            for (ProjectPartner projectPartner : project.getProjectPartners().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList())) {
              if (projectPartner != null) {

                projectPartnerManager.saveProjectPartner(projectPartner);
                this.projectSectionValidator.validateProjectParnters(this, project.getId(), this.getCurrentCrp());

              }
            }

            // Project info
            project.getProjecInfoPhase(phase).setModificationJustification(this.getJustification());
            projectInfoManager.saveProjectInfo(project.getProjecInfoPhase(phase));
            // Project
            this.setModificationJustification(project);
            projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());

            // Delete autosave
            Path path = this.getAutoSaveFilePath();

            if (path.toFile().exists()) {
              path.toFile().delete();
            }

          }
        }
        logger.debug("Finished replication succesfully");
      } else {
        logger.debug("No project selected");
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }

  public void setEntityByPhaseList(String entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }


  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }

}