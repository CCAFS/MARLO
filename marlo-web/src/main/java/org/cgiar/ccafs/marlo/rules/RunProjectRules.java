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


package org.cgiar.ccafs.marlo.rules;

import org.cgiar.ccafs.marlo.config.RulesListener;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.rules.project.CCAFSProjectRule;
import org.cgiar.ccafs.marlo.rules.project.PIMProjectRule;

import org.apache.struts2.ServletActionContext;
import org.easyrules.api.RulesEngine;

public class RunProjectRules {

  private Project project;


  public Project getProject() {
    return project;
  }

  public void runRules() {
    RulesEngine rulesEngine =
      (RulesEngine) ServletActionContext.getServletContext().getAttribute(RulesListener.KEY_NAME);
    rulesEngine.clearRules();
    rulesEngine.registerRule(new CCAFSProjectRule(project));
    rulesEngine.registerRule(new PIMProjectRule(project));
    rulesEngine.fireRules();
  }


  public void setProject(Project project) {
    this.project = project;
  }


}
