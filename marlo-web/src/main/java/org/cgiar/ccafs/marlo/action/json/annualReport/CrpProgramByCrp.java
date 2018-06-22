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

package org.cgiar.ccafs.marlo.action.json.annualReport;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
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
public class CrpProgramByCrp extends BaseAction {


  private static final long serialVersionUID = -6010866345601586878L;


  private GlobalUnitManager globalUnitManager;

  private List<Map<String, Object>> crpPrograms;


  private String message;

  private long crpID;

  @Inject
  public CrpProgramByCrp(APConfig config, GlobalUnitManager globalUnitManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
  }

  @Override
  public String execute() throws Exception {

    crpPrograms = new ArrayList<>();

    Map<String, Object> crpProgram;

    GlobalUnit globalUnit = null;
    if (crpID != -1) {
      globalUnit = globalUnitManager.getGlobalUnitById(crpID);
    }

    if (globalUnit != null) {
      List<CrpProgram> crpProgramList = new ArrayList<>(globalUnit.getCrpPrograms().stream()
        .filter(fg -> fg.isActive() && fg.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));

      for (CrpProgram crpPr : crpProgramList) {
        crpProgram = new HashMap<>();
        crpProgram.put("id", crpPr.getId());
        crpProgram.put("description", crpPr.getName());
        crpProgram.put("acronym", crpPr.getAcronym());
        crpPrograms.add(crpProgram);
      }
    }


    return SUCCESS;
  }

  public List<Map<String, Object>> getCrpPrograms() {
    return crpPrograms;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    crpID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_ID).getMultipleValues()[0]));
  }


  public void setCrpPrograms(List<Map<String, Object>> crpPrograms) {
    this.crpPrograms = crpPrograms;
  }

  public void setMessage(String message) {
    this.message = message;
  }


}
