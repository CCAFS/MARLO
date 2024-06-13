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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.dispatcher.Parameter;


public class ProgressActiveValidationAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 8027160696406597679L;
  /**
   * 
   */
  private Map<String, String> status;

  @Inject
  public ProgressActiveValidationAction(APConfig config) {
    super(config);
  }

  @Override
  public String execute() throws Exception {

    status = new HashMap<>();

    if (this.isUpKeepActive()) {
      status.put("isProgress", "true");
    } else {
      status.put("isProgress", "false");
    }

    return SUCCESS;
  }

  public Map<String, String> getStatus() {
    return status;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
  }


  public void setStatus(Map<String, String> keyOutputs) {
    this.status = keyOutputs;
  }
}

