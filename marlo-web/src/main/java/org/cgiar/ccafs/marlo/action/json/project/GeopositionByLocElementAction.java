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
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocGeopositionManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class GeopositionByLocElementAction extends BaseAction {


  private static final long serialVersionUID = -3927332195536071744L;


  private LocGeopositionManager geopositionManager;

  private LocElementManager locElementManager;

  private long locElementID;
  private List<Map<String, Object>> geopositions;

  @Inject
  public GeopositionByLocElementAction(APConfig config, LocGeopositionManager geopositionManager,
    LocElementManager locElementManager) {
    super(config);
    this.geopositionManager = geopositionManager;
    this.locElementManager = locElementManager;
  }

  @Override
  public String execute() throws Exception {
    geopositions = new ArrayList<>();

    LocElement element = locElementManager.getLocElementById(locElementID);

    Map<String, Object> geoposition = new HashMap<>();
    if (element.getLocGeoposition() != null) {
      geoposition.put("id", element.getLocGeoposition().getId());
      geoposition.put("latitude", element.getLocGeoposition().getLatitude());
      geoposition.put("longitude", element.getLocGeoposition().getLongitude());

      geopositions.add(geoposition);
    }


    return SUCCESS;
  }

  public List<Map<String, Object>> getGeopositions() {
    return geopositions;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // locElementID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.LOC_ELEMENT_ID))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    locElementID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.LOC_ELEMENT_ID).getMultipleValues()[0]));

  }

  public void setGeopositions(List<Map<String, Object>> geopositions) {
    this.geopositions = geopositions;
  }

}
