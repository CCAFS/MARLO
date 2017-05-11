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

import org.cgiar.ccafs.marlo.data.model.Project;

import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;

@Rule(name = "CCAFSProjectRule", description = "CCAFSProjectRule")
public class CCAFSProjectRule {

  private Project project;


  private final long crpID = 1;

  public CCAFSProjectRule(Project project) {
    super();
    this.project = project;
  }

  @Action
  public void calculateCustomID() throws Exception {
    project.setCustomID("P" + project.getId());

  }

  @Condition
  public boolean checkCRP() {

    return project.getCrp().getId().longValue() == crpID;
  }

  public Project getProject() {
    return project;
  }


  public void setProject(Project project) {
    this.project = project;
  }
}
