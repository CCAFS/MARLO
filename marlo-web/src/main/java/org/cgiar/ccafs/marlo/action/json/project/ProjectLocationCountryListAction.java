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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocationCountryListAction extends BaseAction {


  private static final long serialVersionUID = 7165684771318861734L;


  private List<Map<String, Object>> locElements;

  private String message;


  private long parentId;

  private int modelClass;

  private LocElementTypeManager locElementTypeManager;
  private CrpProgramManager crpProgramManager;

  @Inject
  public ProjectLocationCountryListAction(APConfig config, LocElementTypeManager locElementTypeManager,
    CrpProgramManager crpProgramManager) {
    super(config);
    this.locElementTypeManager = locElementTypeManager;
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {
    Map<String, Object> locElement = new HashMap<String, Object>();
    switch (modelClass) {
      case 1:
        LocElementType elementType = locElementTypeManager.getLocElementTypeById(parentId);
        for (LocElement element : elementType.getLocElements().stream().filter(le -> le.isActive())
          .collect(Collectors.toList())) {
          // locElement.put("id", )
        }
        break;

      case 2:
        break;
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getLocElements() {
    return locElements;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public void prepare() throws Exception {

    Map<String, Object> parameters = this.getParameters();
    parentId = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.TARGET_UNIT_NAME))[0]));
    modelClass = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.TARGET_UNIT_NAME))[0]));


  }

  public void setLocElements(List<Map<String, Object>> locElements) {
    this.locElements = locElements;
  }

  public void setMessage(String message) {
    this.message = message;
  }


}
