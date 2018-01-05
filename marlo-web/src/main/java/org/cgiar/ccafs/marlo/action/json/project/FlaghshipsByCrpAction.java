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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlaghshipsByCrpAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(FlaghshipsByCrpAction.class);
  private List<Map<String, Object>> flagships;

  private Long crpID;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public FlaghshipsByCrpAction(APConfig config, GlobalUnitManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }


  @Override
  public String execute() throws Exception {
    flagships = new ArrayList<Map<String, Object>>();
    Map<String, Object> flagShip;
    List<CrpProgram> crpPrograms = crpManager.getGlobalUnitById(crpID).getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());;
    for (CrpProgram crpProgram : crpPrograms) {
      try {
        flagShip = new HashMap<String, Object>();
        flagShip.put("id", crpProgram.getId());
        flagShip.put("description", crpProgram.getComposedName());

        this.flagships.add(flagShip);
      } catch (Exception e) {
        logger.error("unable to add flagship to flagships list", e);
        /**
         * Original code swallows the exception and didn't even log it. Now we at least log it,
         * but we need to revisit to see if we should continue processing or re-throw the exception.
         */
      }
    }
    return SUCCESS;

  }


  public List<Map<String, Object>> getFlagships() {
    return flagships;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    // crpID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_ID))[0]));
    crpID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_ID).getMultipleValues()[0]));
  }


  public void setFlagships(List<Map<String, Object>> flagships) {
    this.flagships = flagships;
  }


}
