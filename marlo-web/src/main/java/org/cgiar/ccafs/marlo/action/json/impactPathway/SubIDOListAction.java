/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.action.json.impactPathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
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
 * @author Christian Garcia - CIAT/CCAFS
 */
public class SubIDOListAction extends BaseAction {

  private static final long serialVersionUID = -987364875799543189L;
  private static final Logger LOG = LoggerFactory.getLogger(SubIDOListAction.class);

  private SrfIdoManager srfIdoManager;
  private List<Map<String, Object>> subIdos;
  private String idoID;

  @Inject
  public SubIDOListAction(APConfig config, SrfIdoManager srfIdoManager) {
    super(config);
    this.srfIdoManager = srfIdoManager;

  }

  @Override
  public String execute() throws Exception {
    long idoId = 0;
    try {
      idoId = Long.parseLong(idoID);
    } catch (NumberFormatException e) {
      LOG.error("There was an exception trying to parse the parent id = {} ", idoID);
    }

    SrfIdo srfIdo = srfIdoManager.getSrfIdoById(idoId);
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

    // Verify if there is a programID parameter
    if (this.getRequest().getParameter(APConstants.IDO_ID) == null) {
      idoID = "";
      return;
    }


    // If there is a parameter take its values
    idoID = StringUtils.trim(this.getRequest().getParameter(APConstants.IDO_ID));
  }

  public void setSubIdos(List<Map<String, Object>> subIdos) {
    this.subIdos = subIdos;
  }
}
