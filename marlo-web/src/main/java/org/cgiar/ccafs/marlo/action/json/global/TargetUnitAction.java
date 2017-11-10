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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class TargetUnitAction extends BaseAction {


  private static final long serialVersionUID = 5653293395155918564L;


  private SrfTargetUnitManager srfTargetUnitManager;

  private String queryParameter;


  private Map<String, Object> newTargetUnit;

  private String message;


  @Inject
  public TargetUnitAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;
  }

  @Override
  public String execute() throws Exception {
    this.newTargetUnit = new HashMap<>();
    List<SrfTargetUnit> targetUnits = srfTargetUnitManager.findAll().stream()
      .filter(tg -> tg.isActive() && tg.getName().toLowerCase().trim().equals(queryParameter.toLowerCase().trim()))
      .collect(Collectors.toList());

    if (targetUnits.size() > 0) {
      newTargetUnit.put("status", false);
      newTargetUnit.put("id", targetUnits.get(0).getId());
      newTargetUnit.put("name", targetUnits.get(0).getName());
      return SUCCESS;
    } else {

      SrfTargetUnit targetUnit = new SrfTargetUnit();

      targetUnit.setName(queryParameter);
      targetUnit.setActive(true);
      targetUnit.setActiveSince(new Date());
      targetUnit.setCreatedBy(this.getCurrentUser());
      targetUnit.setModificationJustification("Create by : " + this.getCurrentUser().getComposedCompleteName());
      targetUnit.setModifiedBy(this.getCurrentUser());


      targetUnit = srfTargetUnitManager.saveSrfTargetUnit(targetUnit);

      newTargetUnit.put("status", true);
      newTargetUnit.put("id", targetUnit.getId());
      newTargetUnit.put("name", queryParameter);

      return SUCCESS;
    }
  }

  public String getMessage() {
    return message;
  }

  public Map<String, Object> getNewTargetUnit() {
    return newTargetUnit;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    queryParameter = StringUtils.trim(parameters.get(APConstants.TARGET_UNIT_NAME).getMultipleValues()[0]);
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setNewTargetUnit(Map<String, Object> newTargetUnit) {
    this.newTargetUnit = newTargetUnit;
  }


}
