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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocGeopositionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpLocationsAction extends BaseAction {


  private static final long serialVersionUID = 7866923077836156028L;

  private CrpManager crpManager;
  private LocElementManager locElementManager;
  private LocElementTypeManager locElementTypeManager;
  private LocGeopositionManager locGeopositionManager;
  private Crp loggedCrp;

  private List<LocElementType> defaultLocationTypes;


  @Inject
  public CrpLocationsAction(APConfig config, CrpManager crpManager, LocElementManager locElementManager,
    LocElementTypeManager locElementTypeManager, LocGeopositionManager locGeopositionManager) {
    super(config);
    this.crpManager = crpManager;
    this.locElementManager = locElementManager;
    this.locElementTypeManager = locElementTypeManager;
    this.locGeopositionManager = locGeopositionManager;
  }


  public List<LocElementType> getDefaultLocationTypes() {
    return defaultLocationTypes;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  private void locationNewData() {
    for (LocElementType locElementType : loggedCrp.getLocationElementTypes()) {

      if (locElementType.getId() == null) {

        locElementType.setName(locElementType.getName());
        locElementType.setCrp(loggedCrp);
        locElementType.setActive(true);
        locElementType.setModifiedBy(this.getCurrentUser());
        locElementType.setCreatedBy(this.getCurrentUser());
        locElementType.setActiveSince(new Date());
        locElementType.setModificationJustification("");
        Long newLocElementTypeId = locElementTypeManager.saveLocElementType(locElementType);

        if (locElementType.getLocationElements() != null) {
          for (LocElement locElement : locElementType.getLocationElements()) {
            if (locElement.getId() == null) {

              LocGeoposition locGeoposition = new LocGeoposition();
              locGeoposition.setLatitude(locElement.getLocGeoposition().getLatitude());
              locGeoposition.setLongitude(locElement.getLocGeoposition().getLongitude());
              locGeoposition.setActive(true);
              locGeoposition.setModifiedBy(this.getCurrentUser());
              locGeoposition.setCreatedBy(this.getCurrentUser());
              locGeoposition.setActiveSince(new Date());
              locGeoposition.setModificationJustification("");

              Long newLocGeopositionId = locGeopositionManager.saveLocGeoposition(locGeoposition);
              LocElementType elementType = locElementTypeManager.getLocElementTypeById(newLocElementTypeId);
              LocElement parentElement =
                locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

              locGeoposition = locGeopositionManager.getLocGeopositionById(newLocGeopositionId);

              locElement.setLocGeoposition(locGeoposition);
              locElement.setLocElementType(elementType);
              locElement.setLocElement(parentElement);
              locElement.setCrp(loggedCrp);
              locElement.setActive(true);
              locElement.setModifiedBy(this.getCurrentUser());
              locElement.setCreatedBy(this.getCurrentUser());
              locElement.setActiveSince(new Date());
              locElement.setModificationJustification("");
              locElementManager.saveLocElement(locElement);

              elementType.setHasCoordinates(true);
              locElementTypeManager.saveLocElementType(elementType);
            }
          }
        }
      } else {
        if (!locElementType.getHasCoordinates()) {
          LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());
          if (elementType.getLocElements() != null) {
            for (LocElement locElement : elementType.getLocElements()) {
              locGeopositionManager.deleteLocGeoposition(locElement.getLocGeoposition().getId());
              locElementManager.deleteLocElement(locElement.getId());
            }
          }
          elementType.setHasCoordinates(false);
          locElementTypeManager.saveLocElementType(elementType);
        } else {
          if (locElementType.getLocationElements() != null) {
            for (LocElement locElement : locElementType.getLocationElements()) {
              if (locElement.getId() == null) {

                LocGeoposition locGeoposition = new LocGeoposition();
                locGeoposition.setLatitude(locElement.getLocGeoposition().getLatitude());
                locGeoposition.setLongitude(locElement.getLocGeoposition().getLongitude());
                locGeoposition.setActive(true);
                locGeoposition.setModifiedBy(this.getCurrentUser());
                locGeoposition.setCreatedBy(this.getCurrentUser());
                locGeoposition.setActiveSince(new Date());
                locGeoposition.setModificationJustification("");

                Long newLocGeopositionId = locGeopositionManager.saveLocGeoposition(locGeoposition);
                LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());
                LocElement parentElement =
                  locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

                locGeoposition = locGeopositionManager.getLocGeopositionById(newLocGeopositionId);

                locElement.setLocGeoposition(locGeoposition);
                locElement.setLocElementType(elementType);
                locElement.setLocElement(parentElement);
                locElement.setCrp(loggedCrp);
                locElement.setActive(true);
                locElement.setModifiedBy(this.getCurrentUser());
                locElement.setCreatedBy(this.getCurrentUser());
                locElement.setActiveSince(new Date());
                locElement.setModificationJustification("");
                locElementManager.saveLocElement(locElement);

                elementType.setHasCoordinates(true);
                locElementTypeManager.saveLocElementType(elementType);
              }
            }
          } else {
            LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());

            if (elementType.getLocElements() != null) {
              for (LocElement locElement : locElementType.getLocElements()) {
                locGeopositionManager.deleteLocGeoposition(locElement.getLocGeoposition().getId());
                locElementManager.deleteLocElement(locElement.getId());
              }
            }
            elementType.setHasCoordinates(false);
            locElementTypeManager.saveLocElementType(elementType);
          }
        }
      }
    }
  }

  private void locationPreviousData() {
    List<LocElementType> locElementTypesPrew = new ArrayList<LocElementType>(loggedCrp.getLocElementTypes());

    if (locElementTypesPrew != null) {
      for (LocElementType locElementType : locElementTypesPrew) {
        if (!loggedCrp.getLocationElementTypes().contains(locElementType)) {
          if (locElementType.getLocElements() != null) {
            for (LocElement locElement : locElementType.getLocElements()) {
              locGeopositionManager.deleteLocGeoposition(locElement.getLocGeoposition().getId());
              locElementManager.deleteLocElement(locElement.getId());
            }
          }
          locElementTypeManager.deleteLocElementType(locElementType.getId());
        } else {
          if (locElementType.getLocElements() != null) {

            LocElementType elementType = loggedCrp.getLocationElementTypes().stream()
              .filter(le -> le.equals(locElementType)).collect(Collectors.toList()).get(0);
            if (elementType.getLocationElements() != null) {
              for (LocElement locElement : locElementType.getLocElements()) {
                if (!elementType.getLocationElements().contains(locElement)) {
                  locGeopositionManager.deleteLocGeoposition(locElement.getLocGeoposition().getId());
                  locElementManager.deleteLocElement(locElement.getId());
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();

    defaultLocationTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() == null).collect(Collectors.toList());

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    String params[] = {loggedCrp.getAcronym()};

    if (loggedCrp.getLocElementTypes() != null) {
      loggedCrp.setLocationElementTypes(new ArrayList<LocElementType>(
        loggedCrp.getLocElementTypes().stream().filter(let -> let.isActive()).collect(Collectors.toList())));


      Collections.sort(loggedCrp.getLocationElementTypes(), (le1, le2) -> le1.getName().compareTo(le2.getName()));

      for (int i = 0; i < loggedCrp.getLocationElementTypes().size(); i++) {
        loggedCrp.getLocationElementTypes().get(i)
          .setLocationElements(new ArrayList<LocElement>(loggedCrp.getLocationElementTypes().get(i).getLocElements()
            .stream().filter(le -> le.isActive()).collect(Collectors.toList())));

        Collections.sort(loggedCrp.getLocationElementTypes().get(i).getLocationElements(),
          (le1, le2) -> le1.getName().compareTo(le2.getName()));
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

      this.locationPreviousData();
      this.locationNewData();

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      messages = this.getActionMessages();

      if (loggedCrp.getLocElementTypes() != null) {
        loggedCrp.setLocationElementTypes(new ArrayList<LocElementType>(loggedCrp.getLocElementTypes()));

        for (int i = 0; i < loggedCrp.getLocationElementTypes().size(); i++) {
          loggedCrp.getLocationElementTypes().get(i).setLocationElements(
            new ArrayList<LocElement>(loggedCrp.getLocationElementTypes().get(i).getLocElements()));
        }
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setDefaultLocationTypes(List<LocElementType> defaultLocationTypes) {
    this.defaultLocationTypes = defaultLocationTypes;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
