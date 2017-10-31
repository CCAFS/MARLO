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

package org.cgiar.ccafs.marlo.action.center.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterImpactStatementManager;
import org.cgiar.ccafs.marlo.data.model.CenterImpactStatement;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SubIdosStatementAction extends BaseAction {

  private static final long serialVersionUID = -1454381659676327100L;
  private static final Logger LOG = LoggerFactory.getLogger(SubIdosStatementAction.class);

  private ICenterImpactStatementManager statementManager;

  private String idoStatementID;
  private List<Map<String, Object>> subIdos;


  @Inject
  public SubIdosStatementAction(APConfig config, ICenterImpactStatementManager statementManager) {
    super(config);

    this.statementManager = statementManager;
  }


  @Override
  public String execute() throws Exception {
    long idoId = 0;
    try {
      idoId = Long.parseLong(idoStatementID);
    } catch (NumberFormatException e) {
      LOG.error("There was an exception trying to parse the parent id = {} ", idoStatementID);
    }

    CenterImpactStatement centerImpactStatement = statementManager.getResearchImpactStatementById(idoId);

    SrfIdo srfIdo = centerImpactStatement.getSrfIdo();
    List<SrfSubIdo> subIdosDB =
      srfIdo.getSrfSubIdos().stream().filter(c1 -> c1.isActive()).collect(Collectors.toList());

    subIdos = new ArrayList<Map<String, Object>>();
    for (SrfSubIdo srfSubIdo : subIdosDB) {
      Map<String, Object> mapSubIdo = new HashMap<String, Object>();
      mapSubIdo.put("id", srfSubIdo.getId());
      if (srfSubIdo.getSrfIdo().isIsCrossCutting()) {
        mapSubIdo.put("description", "CrossCutting:" + srfSubIdo.getDescription());
      } else {
        mapSubIdo.put("description", srfSubIdo.getDescription());
      }

      subIdos.add(mapSubIdo);
    }
    return SUCCESS;
  }

  public List<Map<String, Object>> getSubIdos() {
    return subIdos;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    // Verify if there is a programID parameter
    if (parameters.get(APConstants.IDO_ID) == null) {
      idoStatementID = "";
      return;
    }

    // If there is a parameter take its values
    idoStatementID = StringUtils.trim(((String[]) parameters.get(APConstants.IDO_ID))[0]);
  }

  public void setSubIdos(List<Map<String, Object>> subIdos) {
    this.subIdos = subIdos;
  }

}
