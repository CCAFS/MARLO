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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpLocationsAction extends BaseAction {


  private static final long serialVersionUID = 7866923077836156028L;

  private CrpManager crpManager;
  private LocElementManager locElementManager;
  private LocElementTypeManager locElementTypeManager;
  private Crp loggedCrp;

  @Inject
  public CrpLocationsAction(APConfig config, CrpManager crpManager, LocElementManager locElementManager,
    LocElementTypeManager locElementTypeManager) {
    super(config);
    this.crpManager = crpManager;
    this.locElementManager = locElementManager;
    this.locElementTypeManager = locElementTypeManager;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    String params[] = {loggedCrp.getAcronym()};

    if (loggedCrp.getLocElementTypes() != null) {
      loggedCrp.setLocationElementTypes(new ArrayList<LocElementType>(loggedCrp.getLocElementTypes()));

      for (int i = 0; i < loggedCrp.getLocationElementTypes().size(); i++) {
        loggedCrp.getLocationElementTypes().get(i)
          .setLocationElements(new ArrayList<LocElement>(loggedCrp.getLocationElementTypes().get(i).getLocElements()));
      }

    }

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      loggedCrp.getLocationElementTypes().clear();
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      if (loggedCrp.getLocElementTypes() != null) {
        List<LocElementType> locElementTypesPrew = (List<LocElementType>) loggedCrp.getLocElementTypes();
        for (LocElementType locElementType : locElementTypesPrew) {
          if (!loggedCrp.getLocationElementTypes().contains(locElementType)) {
            locElementTypeManager.deleteLocElementType(locElementType.getId());
          }
        }

        for (LocElementType locElementType : loggedCrp.getLocationElementTypes()) {
          if (locElementType.getId() == null) {
            locElementType.setName(locElementType.getName());
            locElementTypeManager.saveLocElementType(locElementType);
          }
        }
      }


    }
    return null;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
