package org.cgiar.ccafs.marlo.action.json.crpadmin;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectByDeliverableAction extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(ProjectByDeliverableAction.class);

  // Parameters
  private List<Map<String, Object>> projects;
  private long selectedDelivearbleID;
  private long deliverablePhase;

  // Managers
  private DeliverableManager deliverableManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private ProjectInfoManager projectInfoManager;


  @Inject
  public ProjectByDeliverableAction(APConfig config, DeliverableManager deliverableManager,
    ProjectManager projectManager, PhaseManager phaseManager, ProjectInfoManager projectInfoManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.projectInfoManager = projectInfoManager;
  }


  @Override
  public String execute() throws Exception {

    Deliverable deliverable = deliverableManager.getDeliverableById(selectedDelivearbleID);
    Phase phase = null;

    if (deliverable != null && deliverable.getProject() != null && deliverable.getProject().getId() != null) {
      Map<String, Object> projectMap;
      projects = new ArrayList<>();
      Project project = projectManager.getProjectById(deliverable.getProject().getId());
      phase = phaseManager.getPhaseById(deliverablePhase);

      if (deliverablePhase != 0 && project != null && phase != null) {
        projectMap = new HashMap<>();
        projectMap.put("id", project.getId());
        project.setProjectInfo(projectInfoManager.getProjectInfoByProjectPhase(project.getId(), phase.getId()));
        if (project.getProjectInfo() != null) {
          projectMap.put("title", project.getProjecInfoPhase(phase).getTitle());
        }
        projects.add(projectMap);
      }
    }

    return SUCCESS;

  }

  public long getDeliverablePhase() {
    return deliverablePhase;
  }

  public List<Map<String, Object>> getProjects() {
    return projects;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    selectedDelivearbleID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.DELIVERABLE_ID).getMultipleValues()[0]));
    deliverablePhase = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
  }

  public void setDeliverablePhase(long deliverablePhase) {
    this.deliverablePhase = deliverablePhase;
  }

  public void setProjects(List<Map<String, Object>> projects) {
    this.projects = projects;
  }

}