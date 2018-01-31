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
import org.cgiar.ccafs.marlo.data.manager.CrpLocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocGeopositionManager;
import org.cgiar.ccafs.marlo.data.model.CrpLocElementType;
import org.cgiar.ccafs.marlo.data.model.CustomLevelSelect;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
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

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpLocationsAction extends BaseAction {


  private static final long serialVersionUID = 7866923077836156028L;


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;


  private LocElementManager locElementManager;
  private LocElementTypeManager locElementTypeManager;
  private LocGeopositionManager locGeopositionManager;
  private LocElementManager locElementManger;
  private CrpLocElementTypeManager crpLocElementTypeManager;
  // Variables
  private GlobalUnit loggedCrp;
  private List<LocElement> countriesList;

  private List<LocElement> regions;
  private List<LocElementType> defaultLocationTypes;

  @Inject
  public CrpLocationsAction(APConfig config, GlobalUnitManager crpManager, LocElementManager locElementManager,
    LocElementTypeManager locElementTypeManager, LocGeopositionManager locGeopositionManager,
    CrpLocElementTypeManager crpLocElementTypeManager, LocElementManager locElementManger) {
    super(config);
    this.crpManager = crpManager;
    this.locElementManager = locElementManager;
    this.locElementTypeManager = locElementTypeManager;
    this.locGeopositionManager = locGeopositionManager;
    this.crpLocElementTypeManager = crpLocElementTypeManager;
    this.locElementManger = locElementManger;
  }

  public boolean canBeSelected(Long id) {
    boolean returnValue = true;
    switch (id.intValue()) {
      case 1:
        returnValue = false;
        break;

      case 2:
        returnValue = false;
        break;

    }

    return returnValue;

  }


  public List<LocElement> getCountriesList() {
    return countriesList;
  }

  public List<LocElementType> getDefaultLocationTypes() {
    return defaultLocationTypes;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<LocElement> getRegions() {
    return regions;
  }


  private void locationCustomNewData() {
    for (LocElementType locElementType : loggedCrp.getLocationCustomElementTypes()) {

      if (locElementType.getId() == null) {

        locElementType.setName(locElementType.getName());
        locElementType.setCrp(loggedCrp);
        locElementType.setActive(true);
        locElementType.setModifiedBy(this.getCurrentUser());
        locElementType.setCreatedBy(this.getCurrentUser());
        locElementType.setActiveSince(new Date());
        locElementType.setModificationJustification("");
        locElementType.setScope(true);
        locElementType = locElementTypeManager.saveLocElementType(locElementType);

        if (locElementType.getLocationElements() != null) {
          for (LocElement locElement : locElementType.getLocationElements()) {
            if (locElement.getId() == null) {

              LocElement parentElement =
                locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

              locElement.setLocElementType(locElementType);
              locElement.setLocElement(parentElement);
              locElement.setLocGeoposition(null);
              locElement.setCrp(loggedCrp);
              locElement.setActive(true);
              locElement.setModifiedBy(this.getCurrentUser());
              locElement.setCreatedBy(this.getCurrentUser());
              locElement.setActiveSince(new Date());
              locElement.setModificationJustification("");
              locElement = locElementManager.saveLocElement(locElement);

              locElementType.setHasCoordinates(false);
              locElementTypeManager.saveLocElementType(locElementType);
            }
          }
        }
      } else {

        if (locElementType.getLocationElements() != null) {
          for (LocElement locElement : locElementType.getLocationElements()) {
            if (locElement.getId() == null) {


              LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());
              LocElement parentElement =
                locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

              locElement.setLocElementType(elementType);
              locElement.setLocElement(parentElement);
              locElement.setLocGeoposition(null);
              locElement.setCrp(loggedCrp);
              locElement.setActive(true);
              locElement.setModifiedBy(this.getCurrentUser());
              locElement.setCreatedBy(this.getCurrentUser());
              locElement.setActiveSince(new Date());
              locElement.setModificationJustification("");
              locElementManager.saveLocElement(locElement);

              elementType.setHasCoordinates(false);
              elementType.setName(locElementType.getName());
              locElementTypeManager.saveLocElementType(elementType);
            }
          }
        } else {
          LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());

          if (elementType.getLocElements() != null) {
            for (LocElement locElement : locElementType.getLocElements()) {
              locElementManager.deleteLocElement(locElement.getId());
            }
          }
          elementType.setHasCoordinates(false);
          elementType.setName(locElementType.getName());
          locElementTypeManager.saveLocElementType(elementType);
        }
      }
    }
  }


  private void locationCustomPreviousData() {
    List<LocElementType> locElementTypesPrew = new ArrayList<LocElementType>(loggedCrp.getLocElementTypes().stream()
      .filter(let -> let.isActive() && let.isScope()).collect(Collectors.toList()));

    if (locElementTypesPrew != null) {
      for (LocElementType locElementType : locElementTypesPrew) {
        if (!loggedCrp.getLocationCustomElementTypes().contains(locElementType)) {
          if (locElementType.getLocElements() != null) {
            for (LocElement locElement : locElementType.getLocElements()) {
              locElementManager.deleteLocElement(locElement.getId());
            }
          }
          locElementTypeManager.deleteLocElementType(locElementType.getId());
        } else {
          if (locElementType.getLocElements() != null) {

            LocElementType elementType = loggedCrp.getLocationCustomElementTypes().stream()
              .filter(le -> le.equals(locElementType)).collect(Collectors.toList()).get(0);
            if (elementType.getLocationElements() != null) {
              for (LocElement locElement : locElementType.getLocElements()) {
                if (!elementType.getLocationElements().contains(locElement)) {
                  locElementManager.deleteLocElement(locElement.getId());
                }
              }
            }
          }
        }
      }
    }
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
        locElementType.setScope(false);
        locElementType = locElementTypeManager.saveLocElementType(locElementType);

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

              locGeoposition = locGeopositionManager.saveLocGeoposition(locGeoposition);
              LocElement parentElement =
                locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

              locElement.setLocGeoposition(locGeoposition);
              locElement.setLocElementType(locElementType);
              locElement.setLocElement(parentElement);
              locElement.setCrp(loggedCrp);
              locElement.setActive(true);
              locElement.setModifiedBy(this.getCurrentUser());
              locElement.setCreatedBy(this.getCurrentUser());
              locElement.setActiveSince(new Date());
              locElement.setModificationJustification("");
              locElementManager.saveLocElement(locElement);

              locElementType.setHasCoordinates(true);
              locElementType.setName(locElementType.getName());
              locElementTypeManager.saveLocElementType(locElementType);
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
          elementType.setName(locElementType.getName());
          locElementTypeManager.saveLocElementType(elementType);
        } else {
          LocElementType elementTypeDB = locElementTypeManager.getLocElementTypeById(locElementType.getId());
          elementTypeDB.setHasCoordinates(true);
          elementTypeDB.setName(locElementType.getName());
          locElementTypeManager.saveLocElementType(elementTypeDB);
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

                locGeoposition = locGeopositionManager.saveLocGeoposition(locGeoposition);
                LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());
                LocElement parentElement =
                  locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

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
                elementType.setName(locElementType.getName());
                locElementTypeManager.saveLocElementType(elementType);
              } else {
                LocElement elementDB = locElementManager.getLocElementById(locElement.getId());
                elementDB.setName(locElement.getName());
                locElementManager.saveLocElement(elementDB);

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
            elementType.setName(locElementType.getName());
            locElementTypeManager.saveLocElementType(elementType);
          }
        }
      }
    }
  }

  private void locationPreviousData() {
    List<LocElementType> locElementTypesPrew = new ArrayList<LocElementType>(loggedCrp.getLocElementTypes().stream()
      .filter(let -> let.isActive() && !let.isScope()).collect(Collectors.toList()));

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

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    String params[] = {loggedCrp.getAcronym()};
    Collections.sort(defaultLocationTypes, (tu1, tu2) -> tu1.getName().compareTo(tu2.getName()));

    loggedCrp.setCustomLevels(new ArrayList<>());
    for (LocElementType locElementType : defaultLocationTypes) {

      CustomLevelSelect select = new CustomLevelSelect();
      select.setLocElementType(locElementType);

      CrpLocElementType crpLocElementType =
        crpLocElementTypeManager.getByLocElementTypeAndCrpId(loggedCrp.getId(), locElementType.getId());

      if (crpLocElementType != null) {
        boolean check = crpLocElementType.isActive();
        select.setCheck(check);

      } else {
        select.setCheck(false);
      }

      loggedCrp.getCustomLevels().add(select);

    }

    // Countries list
    List<LocElement> locs =
      locElementManger.findAll().stream().filter(c -> c.getLocElementType().getId() == 2).collect(Collectors.toList());
    Collections.sort(locs, (l1, l2) -> l1.getName().compareTo(l2.getName()));
    countriesList = locs;

    regions =
      locElementManger.findAll().stream().filter(c -> c.getLocElementType().getId() == 1).collect(Collectors.toList());
    Collections.sort(locs, (l1, l2) -> l1.getName().compareTo(l2.getName()));

    if (loggedCrp.getLocElementTypes() != null) {

      // Location Level
      loggedCrp.setLocationElementTypes(new ArrayList<LocElementType>(loggedCrp.getLocElementTypes().stream()
        .filter(let -> let.isActive() && !let.isScope()).collect(Collectors.toList())));


      Collections.sort(loggedCrp.getLocationElementTypes(), (le1, le2) -> le1.getName().compareTo(le2.getName()));

      for (int i = 0; i < loggedCrp.getLocationElementTypes().size(); i++) {
        loggedCrp.getLocationElementTypes().get(i)
          .setLocationElements(new ArrayList<LocElement>(loggedCrp.getLocationElementTypes().get(i).getLocElements()
            .stream().filter(le -> le.isActive()).collect(Collectors.toList())));

        Collections.sort(loggedCrp.getLocationElementTypes().get(i).getLocationElements(),
          (le1, le2) -> le1.getName().compareTo(le2.getName()));
      }


      // Custom Location Scope
      loggedCrp.setLocationCustomElementTypes(new ArrayList<LocElementType>(loggedCrp.getLocElementTypes().stream()
        .filter(let -> let.isActive() && let.isScope()).collect(Collectors.toList())));


      Collections.sort(loggedCrp.getLocationCustomElementTypes(), (le1, le2) -> le1.getName().compareTo(le2.getName()));

      for (int i = 0; i < loggedCrp.getLocationCustomElementTypes().size(); i++) {
        loggedCrp.getLocationCustomElementTypes().get(i)
          .setLocationElements(new ArrayList<LocElement>(loggedCrp.getLocationCustomElementTypes().get(i)
            .getLocElements().stream().filter(le -> le.isActive()).collect(Collectors.toList())));

        Collections.sort(loggedCrp.getLocationCustomElementTypes().get(i).getLocationElements(),
          (le1, le2) -> le1.getName().compareTo(le2.getName()));
      }

    }

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      loggedCrp.getLocationElementTypes().clear();

      loggedCrp.getLocationCustomElementTypes().clear();


      if (defaultLocationTypes != null) {

        defaultLocationTypes.clear();
      }

      if (loggedCrp.getCustomLevels() != null) {
        loggedCrp.getCustomLevels().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      this.locationPreviousData();
      this.locationNewData();

      this.locationCustomPreviousData();
      this.locationCustomNewData();
      this.saveCustomLocations();

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


  public void saveCustomLocations() {
    if (loggedCrp.getCustomLevels() == null) {
      loggedCrp.setCustomLevels(new ArrayList<>());
    }
    for (CustomLevelSelect customLevelSelect : loggedCrp.getCustomLevels()) {

      CrpLocElementType crpLocElementType = crpLocElementTypeManager.getByLocElementTypeAndCrpId(loggedCrp.getId(),
        customLevelSelect.getLocElementType().getId());

      if (crpLocElementType != null) {
        if (customLevelSelect.getCheck() == null) {
          customLevelSelect.setCheck(false);
        }
        if (!customLevelSelect.getCheck()) {
          crpLocElementTypeManager.deleteCrpLocElementType(crpLocElementType.getId());
        }


      } else {

        if (customLevelSelect.getCheck() != null && customLevelSelect.getCheck()) {

          CrpLocElementType locElementType = new CrpLocElementType();
          locElementType.setCrp(loggedCrp);
          locElementType.setLocElementType(
            locElementTypeManager.getLocElementTypeById(customLevelSelect.getLocElementType().getId()));
          crpLocElementTypeManager.saveCrpLocElementType(locElementType);

        }

      }

    }
  }

  public void setCountriesList(List<LocElement> countriesList) {
    this.countriesList = countriesList;
  }

  public void setDefaultLocationTypes(List<LocElementType> defaultLocationTypes) {
    this.defaultLocationTypes = defaultLocationTypes;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setRegions(List<LocElement> regions) {
    this.regions = regions;
  }

}
