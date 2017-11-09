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


package org.cgiar.ccafs.marlo.rules.project;

import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.stream.Collectors;

import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;

@Rule(name = "PIMProjectRule", description = "PIMProjectRule")
public class PIMProjectRule {

  private Project project;


  private final long crpID = 3;

  public PIMProjectRule(Project project) {
    super();
    this.project = project;
  }

  @Action
  public void calculateCustomID() throws Exception {
    project.setCustomID("PIM" + project.getId());

  }

  @Condition
  public boolean checkCRP() {
    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject = project.getGlobalUnitProjects().stream()
      .filter(gu -> gu.isActive() && gu.isOrigin()).collect(Collectors.toList()).get(0);

    boolean condiction = globalUnitProject.getGlobalUnit().getId().longValue() == crpID;
    return condiction;
  }

  public Project getProject() {
    return project;
  }


  public void setProject(Project project) {
    this.project = project;
  }
}
