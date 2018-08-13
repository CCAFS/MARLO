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

package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class GlobalUnitsAction extends BaseAction {

  private static final long serialVersionUID = -3176376975023397974L;

  private final Logger logger = LoggerFactory.getLogger(GlobalUnitsAction.class);

  // Parameters
  private List<Map<String, Object>> globalUnits;

  // Managers
  private GlobalUnitManager crpManager;

  @Inject
  public GlobalUnitsAction(APConfig config, GlobalUnitManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }


  @Override
  public String execute() throws Exception {
    globalUnits = new ArrayList<Map<String, Object>>();

    // Get Active and MARLO global Units
    List<GlobalUnit> globalUnitList =
      crpManager.findAll().stream().filter(c -> c.isActive() && c.isMarlo()).collect(Collectors.toList());

    if (globalUnitList != null && !globalUnitList.isEmpty()) {
      globalUnitList.sort(Comparator.comparing(GlobalUnit::getAcronym));
      // Build the list into a Map
      for (GlobalUnit globalUnit : globalUnitList) {
        try {
          Map<String, Object> globalUnitMap = new HashMap<String, Object>();
          globalUnitMap.put("id", globalUnit.getId());
          globalUnitMap.put("name", globalUnit.getName());
          globalUnitMap.put("acronym", globalUnit.getAcronym());
          this.globalUnits.add(globalUnitMap);
        } catch (Exception e) {
          logger.error("Unable to add GlobalUnit to GlobalUnit list", e);
        }
      }
    }

    return SUCCESS;

  }


  public List<Map<String, Object>> getGlobalUnits() {
    return globalUnits;
  }


  @Override
  public void prepare() throws Exception {
  }


  public void setGlobalUnits(List<Map<String, Object>> globalUnits) {
    this.globalUnits = globalUnits;
  }


}
