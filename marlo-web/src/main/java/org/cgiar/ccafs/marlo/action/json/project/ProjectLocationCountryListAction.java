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

  private LocElementTypeManager locElementTypeManager;

  @Inject
  public ProjectLocationCountryListAction(APConfig config, LocElementTypeManager locElementTypeManager) {
    super(config);
    this.locElementTypeManager = locElementTypeManager;
  }

  @Override
  public String execute() throws Exception {
    Map<String, Object> locElement = new HashMap<String, Object>();

    LocElementType elementType = locElementTypeManager.getLocElementTypeById(parentId);
    if (elementType != null) {
      for (LocElement element : elementType.getLocElements().stream().filter(le -> le.isActive())
        .collect(Collectors.toList())) {
        locElement.put("id", element.getId());
        locElement.put("name", element.getName());
      }
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
    parentId = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.ELEMENT_TYPE_ID))[0]));
  }

  public void setLocElements(List<Map<String, Object>> locElements) {
    this.locElements = locElements;
  }

  public void setMessage(String message) {
    this.message = message;
  }


}
