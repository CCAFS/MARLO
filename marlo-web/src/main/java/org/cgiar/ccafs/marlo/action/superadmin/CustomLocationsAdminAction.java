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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class CustomLocationsAdminAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -8068503147148935293L;


  private final LocElementTypeManager locElementTypeManager;

  private List<LocElementType> locElementTypeList;


  @Inject
  public CustomLocationsAdminAction(APConfig config, LocElementTypeManager locElementTypeManager) {
    super(config);
    this.locElementTypeManager = locElementTypeManager;
  }


  public List<LocElementType> getLocElementTypeList() {
    return locElementTypeList;
  }


  @Override
  public void prepare() throws Exception {
    if (!this.isHttpPost()) {
      locElementTypeList = new ArrayList<>();
      if (locElementTypeManager.findAll() != null) {
        List<LocElementType> locElementTypes = locElementTypeManager.findAll().stream()
          .filter(c -> c.isActive() && c.getCrp() == null).toList();
        locElementTypeList.addAll(locElementTypes);
      }
    } else {
      if (locElementTypeList == null) {
        locElementTypeList = new ArrayList<>();
      }
    }
  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      this.deleteRemovedLocElementTypes();
      this.saveLocElementTypes();
      this.addActionMessage("message:" + this.getText("saving.saved"));
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  private void deleteRemovedLocElementTypes() {
    List<LocElementType> existing = locElementTypeManager.findAll().stream()
      .filter(c -> c.isActive() && c.getCrp() == null).toList();

    List<Long> incomingIds = locElementTypeList.stream()
      .map(LocElementType::getId)
      .filter(Objects::nonNull)
      .toList();

    if (existing != null) {
      for (LocElementType locElementType : existing) {
        if (!incomingIds.contains(locElementType.getId())) {
          locElementTypeManager.deleteLocElementType(locElementType.getId());
        }
      }
    }
  }

  private void saveLocElementTypes() {
    for (LocElementType locElementType : locElementTypeList) {
      if (locElementType != null) {
        if (locElementType.getId() == null) {
          locElementType.setCrp(null);
          locElementType.setScope(false);
          locElementTypeManager.saveLocElementType(locElementType);
        } else {
          LocElementType locElementTypeDB = locElementTypeManager.getLocElementTypeById(locElementType.getId());
          locElementTypeDB.setHasCoordinates(locElementType.getHasCoordinates());
          locElementTypeDB.setLocElementType(locElementType.getLocElementType());
          locElementTypeDB.setName(locElementType.getName());
          locElementTypeManager.saveLocElementType(locElementTypeDB);
        }
      }
    }
  }


  public void setLocElementTypeList(List<LocElementType> locElementTypeList) {
    this.locElementTypeList = locElementTypeList;
  }


}