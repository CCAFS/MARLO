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
 * @author Christian Garcia- CIAT/CCAFS
 */
public class CrpProgramsGlobalUnitAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 2270859686229584756L;


  private Long globalUnitId;


  private GlobalUnitManager globalUnitManager;


  private List<Map<String, Object>> crpPrograms;

  @Inject
  public CrpProgramsGlobalUnitAction(APConfig config, GlobalUnitManager globalUnitManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
  }

  @Override
  public String execute() throws Exception {

    crpPrograms = new ArrayList<>();
    Map<String, Object> crpProgramMap;

    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitId.longValue());
    if (globalUnit != null) {
      List<CrpProgram> crpPrograms = globalUnit.getCrpPrograms().stream()
        .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      crpPrograms.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
      for (CrpProgram crpProgram : crpPrograms) {
        crpProgramMap = new HashMap<String, Object>();
        crpProgramMap.put("id", crpProgram.getId());
        crpProgramMap.put("description", crpProgram.getName());
        crpProgramMap.put("acronym", crpProgram.getAcronym());

        this.crpPrograms.add(crpProgramMap);
      }

    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getCrpPrograms() {
    return crpPrograms;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    globalUnitId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_ID).getMultipleValues()[0]));
  }


  public void setCrpPrograms(List<Map<String, Object>> keyOutputs) {
    this.crpPrograms = keyOutputs;
  }


}
