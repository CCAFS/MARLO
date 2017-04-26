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
import org.cgiar.ccafs.marlo.data.manager.CrpLocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class CustomLocationsAdminAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -8068503147148935293L;


  private LocElementTypeManager locElementTypeManager;
  private CrpLocElementTypeManager crpLocElementTypeManager;


  private List<LocElementType> locElementTypeList;


  @Inject
  public CustomLocationsAdminAction(APConfig config, LocElementTypeManager locElementTypeManager,
    CrpLocElementTypeManager crpLocElementTypeManager) {
    super(config);
    this.locElementTypeManager = locElementTypeManager;
    this.crpLocElementTypeManager = crpLocElementTypeManager;

  }


  public List<LocElementType> getLocElementTypeList() {
    return locElementTypeList;
  }


  @Override
  public void prepare() throws Exception {


    locElementTypeList = new ArrayList<>();
    if (locElementTypeManager.findAll() != null) {
      List<LocElementType> locElementTypes = locElementTypeManager.findAll().stream()
        .filter(c -> c.isActive() && c.getCrp() == null).collect(Collectors.toList());
      locElementTypeList.addAll(locElementTypes);
    }
    if (this.isHttpPost()) {
      locElementTypeList.clear();
    }

  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {

      List<LocElementType> locElementsPreview = locElementTypeManager.findAll().stream()
        .filter(c -> c.isActive() && c.getCrp() == null).collect(Collectors.toList());

      if (locElementsPreview != null) {
        for (LocElementType locElementType : locElementsPreview) {
          if (!locElementTypeList.contains(locElementType)) {
            locElementTypeManager.deleteLocElementType(locElementType.getId());

          }
        }
      }

      for (LocElementType locElementType : locElementTypeList) {
        if (locElementType != null) {
          if (locElementType.getId() == null) {

            locElementType.setActive(true);
            locElementType.setCreatedBy(this.getCurrentUser());
            locElementType.setModifiedBy(this.getCurrentUser());
            locElementType.setModificationJustification("");
            locElementType.setActiveSince(new Date());
            locElementType.setCrp(null);

            locElementType.setScope(false);

            locElementTypeManager.saveLocElementType(locElementType);
          } else {
            LocElementType locElementTypeDB = locElementTypeManager.getLocElementTypeById(locElementType.getId());

            locElementType.setActive(true);
            locElementType.setCreatedBy(locElementTypeDB.getCreatedBy());
            locElementType.setModifiedBy(this.getCurrentUser());
            locElementType.setModificationJustification("");
            locElementType.setActiveSince(locElementTypeDB.getActiveSince());
            locElementType.setScope(locElementTypeDB.isScope());
            locElementTypeManager.saveLocElementType(locElementType);
          }
        }
      }


      this.addActionMessage(this.getText("saving.saved"));

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setLocElementTypeList(List<LocElementType> locElementTypeList) {
    this.locElementTypeList = locElementTypeList;
  }


}