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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.ProjectSectionValidator;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class TestAction extends BaseAction {


  private static final long serialVersionUID = 4663544283175165587L;

  private ProjectSectionValidator<TestAction> validateProject;
  private GlobalUnitManager crpManager;

  @Inject
  public TestAction(APConfig config, ProjectSectionValidator<TestAction> validateProject,
    GlobalUnitManager crpManager) {
    super(config);

    this.validateProject = validateProject;
    this.crpManager = crpManager;
  }

  @Override
  public String execute() throws Exception {
    List<GlobalUnit> crps = crpManager.findAll().stream().filter(c -> c.isMarlo()).collect(Collectors.toList());
    // for (GlobalUnit crp : crps) {
    // List<Project> projects = crp.getProjects().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    // for (Project project : projects) {
    // validateProject.validateProjectOutcomes(this, project.getId());
    // }
    // }

    return SUCCESS;

  }


  @Override
  public void prepare() throws Exception {

  }


}
