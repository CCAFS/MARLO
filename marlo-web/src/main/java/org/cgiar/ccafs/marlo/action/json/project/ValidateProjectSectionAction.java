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

package org.cgiar.ccafs.marlo.action.json.project;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.ProjectLocationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidateProjectSectionAction extends BaseAction {

  private static final long serialVersionUID = 2334147747892988744L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ValidateProjectSectionAction.class);

  // Model
  private boolean existProject;
  private boolean validSection;
  private String sectionName;
  private int projectID;

  // Managers
  @Inject
  private SectionStatusManager sectionStatusManager;
  @Inject
  private ProjectManager projectManager;

  @Inject
  ProjectLocationValidator locationValidator;


  public ValidateProjectSectionAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {
    if (existProject && validSection) {
      // getting the current section status.
      switch (ProjectSectionStatusEnum.valueOf(sectionName.toUpperCase())) {
        case LOCATIONS:
          this.validateProjectLocations();
          break;
        default:
          break;
      }
    }

    return SUCCESS;
  }

  @Override
  public void prepare() throws Exception {
    sectionName = this.getRequest().getParameter(APConstants.SECTION_NAME);

    projectID = -1;
    if (this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID) != null) {
      try {
        projectID = Integer.parseInt(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID));
      } catch (NumberFormatException e) {
        LOG.error("There was an exception trying to parse the project id = {} ",
          this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID));
      }
    }

    // Validate if project exists.
    existProject = projectManager.existProject(projectID);

    // Validate if the section exists.
    List<String> sections = new ArrayList<>();
    sections.add("description");
    sections.add("partners");
    sections.add("locations");
    sections.add("deliverablesList");

    validSection = sections.contains(sectionName);


  }

  private void validateProjectLocations() {
    // Getting the project information.
    Project project = projectManager.getProjectById(projectID);
    project.setLocations(
      new ArrayList<>(project.getProjectLocations().stream().filter(pl -> pl.isActive()).collect(Collectors.toList())));

    locationValidator.validate(this, project);
  }

}
