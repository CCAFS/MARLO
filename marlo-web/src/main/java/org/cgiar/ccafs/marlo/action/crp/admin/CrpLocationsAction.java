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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpLocationsAction extends BaseAction {


  private static final long serialVersionUID = 7866923077836156028L;
  private static final Logger LOG = LoggerFactory.getLogger(CrpLocationsAction.class);


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
        locElementType.setScope(true);
        locElementType = locElementTypeManager.saveLocElementType(locElementType);

        if (locElementType.getLocationElements() != null) {
          for (LocElement locElement : locElementType.getLocationElements()) {
            if (locElement == null) {
              LOG.warn("Skipping null locElement in locationCustomNewData (new) for locElementType={}", locElementType.getName());
              continue;
            }
            if (locElement.getId() == null) {

              if (locElement.getLocElement() == null || locElement.getLocElement().getIsoAlpha2() == null) {
                LOG.warn("Skipping locElement with missing parent ISO code for locElementType={}", locElementType.getName());
                continue;
              }

              LocElement parentElement =
                locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

              locElement.setLocElementType(locElementType);
              locElement.setLocElement(parentElement);
              locElement.setLocGeoposition(null);
              locElement.setCrp(loggedCrp);
              locElement = locElementManager.saveLocElement(locElement);

              locElementType.setHasCoordinates(false);
              locElementTypeManager.saveLocElementType(locElementType);
            }
          }
        }
      } else {

        if (locElementType.getLocationElements() != null) {
          for (LocElement locElement : locElementType.getLocationElements()) {
            if (locElement == null) {
              LOG.warn("Skipping null locElement in locationCustomNewData (existing) for locElementTypeId={}", locElementType.getId());
              continue;
            }
            if (locElement.getId() == null) {

              LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());
              if (locElement.getLocElement() == null || locElement.getLocElement().getIsoAlpha2() == null) {
                LOG.warn("Skipping locElement with missing parent ISO code for locElementTypeId={}", locElementType.getId());
                continue;
              }
              LocElement parentElement =
                locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

              locElement.setLocElementType(elementType);
              locElement.setLocElement(parentElement);
              locElement.setLocGeoposition(null);
              locElement.setCrp(loggedCrp);
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
              if (locElement == null) continue;
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
              if (locElement != null) {
                locElementManager.deleteLocElement(locElement.getId());
              }
            }
          }
          locElementTypeManager.deleteLocElementType(locElementType.getId());
        } else {
          if (locElementType.getLocElements() != null) {
            LocElementType elementType = loggedCrp.getLocationCustomElementTypes().stream()
              .filter(le -> le.equals(locElementType)).collect(Collectors.toList()).get(0);
              
            if (elementType.getLocationElements() != null) {
              for (LocElement locElement : locElementType.getLocElements()) {
                if (locElement != null) {
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
  }


  private void locationNewData() {
    for (LocElementType locElementType : loggedCrp.getLocationElementTypes()) {

      if (locElementType.getId() == null) {

        locElementType.setName(locElementType.getName());
        locElementType.setCrp(loggedCrp);
        locElementType.setScope(false);
        locElementType = locElementTypeManager.saveLocElementType(locElementType);

        if (locElementType.getLocationElements() != null) {
          for (LocElement locElement : locElementType.getLocationElements()) {
            if (locElement.getId() == null) {

              LocGeoposition locGeoposition = new LocGeoposition();
              locGeoposition.setLatitude(locElement.getLocGeoposition().getLatitude());
              locGeoposition.setLongitude(locElement.getLocGeoposition().getLongitude());

              locGeoposition = locGeopositionManager.saveLocGeoposition(locGeoposition);
              LocElement parentElement =
                locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

              locElement.setLocGeoposition(locGeoposition);
              locElement.setLocElementType(locElementType);
              locElement.setLocElement(parentElement);
              locElement.setCrp(loggedCrp);
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

                locGeoposition = locGeopositionManager.saveLocGeoposition(locGeoposition);
                LocElementType elementType = locElementTypeManager.getLocElementTypeById(locElementType.getId());
                LocElement parentElement =
                  locElementManager.getLocElementByISOCode(locElement.getLocElement().getIsoAlpha2());

                locElement.setLocGeoposition(locGeoposition);
                locElement.setLocElementType(elementType);
                locElement.setLocElement(parentElement);
                locElement.setCrp(loggedCrp);
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
    if (!this.hasPermission("*")) {
      return NOT_AUTHORIZED;
    }

    try {
      LOG.info("CrpLocationsAction.save() start for CRP id={} acronym={}",
        loggedCrp != null ? loggedCrp.getId() : null,
        loggedCrp != null ? loggedCrp.getAcronym() : null);

      if (loggedCrp != null) {
        LOG.debug("Before save: locationElementTypes size={} customLevels size={} locationCustomElementTypes size={}",
          loggedCrp.getLocElementTypes() != null ? loggedCrp.getLocElementTypes().size() : 0,
          loggedCrp.getCustomLevels() != null ? loggedCrp.getCustomLevels().size() : 0,
          loggedCrp.getLocationCustomElementTypes() != null ? loggedCrp.getLocationCustomElementTypes().size() : 0);
      }
          // Manual binding for nested location lists (handles cases where client JS didn't set names)
          this.manualBindingLocationElements();

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

      if (loggedCrp != null && loggedCrp.getLocElementTypes() != null) {
        loggedCrp.setLocationElementTypes(new ArrayList<LocElementType>(loggedCrp.getLocElementTypes()));

        for (int i = 0; i < loggedCrp.getLocationElementTypes().size(); i++) {
          loggedCrp.getLocationElementTypes().get(i).setLocationElements(
            new ArrayList<LocElement>(loggedCrp.getLocationElementTypes().get(i).getLocElements()));
        }
      }

      LOG.info("CrpLocationsAction.save() finished successfully for CRP id={}",
        loggedCrp != null ? loggedCrp.getId() : null);

      return SUCCESS;
    } catch (Exception e) {
      LOG.error("Error saving locations for CRP id={}: {}", loggedCrp != null ? loggedCrp.getId() : null, e.getMessage(), e);
      this.addActionError(this.getText("error.saving") + " - " + e.getMessage());
      return INPUT;
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

  /**
   * Manual binding for locationElement nested objects when client-side JS is missing
   * Simplified version - focuses only on binding, deletion is handled by locationCustomPreviousData()
   */
  private void manualBindingLocationElements() {
    if (this.getRequest() == null || loggedCrp == null) {
      return;
    }

    Map<String, String[]> params = this.getRequest().getParameterMap();

    // Ensure top-level lists exist
    if (loggedCrp.getLocationElementTypes() == null) {
      loggedCrp.setLocationElementTypes(new ArrayList<LocElementType>());
    }
    if (loggedCrp.getLocationCustomElementTypes() == null) {
      loggedCrp.setLocationCustomElementTypes(new ArrayList<LocElementType>());
    }
    if (loggedCrp.getCustomLevels() == null) {
      loggedCrp.setCustomLevels(new ArrayList<CustomLevelSelect>());
    }

    for (String key : params.keySet()) {
      try {
        // Handle custom location types binding (Scope/Region Name)
        if (key.startsWith("loggedCrp.locationCustomElementTypes[")) {
          int idx = extractIndex(key);
          int second = -1;
          if (key.contains("locationElements[")) {
            second = extractSecondIndex(key);
          }

          // Top-level custom element type properties (id/name/hasCoordinates)
          if (!key.contains("locationElements[")) {
            while (loggedCrp.getLocationCustomElementTypes().size() <= idx) {
              loggedCrp.getLocationCustomElementTypes().add(new LocElementType());
            }
            LocElementType topType = loggedCrp.getLocationCustomElementTypes().get(idx);
            String[] values = params.get(key);
            if (key.endsWith(".id")) {
              if (values != null && values.length > 0 && !values[0].trim().isEmpty()) {
                try {
                  topType.setId(Long.parseLong(values[0].trim()));
                } catch (NumberFormatException e) {
                  LOG.warn("Invalid custom LocElementType id: {}", values[0]);
                }
              }
            } else if (key.endsWith(".name")) {
              if (values != null && values.length > 0) {
                topType.setName(values[0]);
              }
            } else if (key.endsWith(".hasCoordinates")) {
              if (values != null && values.length > 0) {
                String v = values[0].trim();
                topType.setHasCoordinates("true".equalsIgnoreCase(v) || "on".equalsIgnoreCase(v) || "1".equals(v));
              }
            }
            continue;
          }

          // Handle location elements within custom types
          while (loggedCrp.getLocationCustomElementTypes().size() <= idx) {
            loggedCrp.getLocationCustomElementTypes().add(new LocElementType());
          }
          LocElementType type = loggedCrp.getLocationCustomElementTypes().get(idx);
          if (type.getLocationElements() == null) {
            type.setLocationElements(new ArrayList<LocElement>());
          }

          if (second >= 0) {
            while (type.getLocationElements().size() <= second) {
              LocElement le = new LocElement();
              le.setLocElement(new LocElement());
              type.getLocationElements().add(le);
            }
            LocElement element = type.getLocationElements().get(second);
            
            // Ensure element and its nested objects are not null
            if (element == null) {
              element = new LocElement();
              element.setLocElement(new LocElement());
              type.getLocationElements().set(second, element);
            }
            if (element.getLocElement() == null) {
              element.setLocElement(new LocElement());
            }
            
            String[] values = params.get(key);
            if (key.endsWith(".locElement.isoAlpha2")) {
              if (values != null && values.length > 0) {
                element.getLocElement().setIsoAlpha2(values[0].trim());
              }
            } else if (key.endsWith(".id")) {
              if (values != null && values.length > 0 && !values[0].trim().isEmpty()) {
                try {
                  element.setId(Long.parseLong(values[0].trim()));
                } catch (NumberFormatException e) {
                  LOG.warn("Invalid locElement id: {}", values[0]);
                }
              }
            } else if (key.endsWith(".name")) {
              if (values != null && values.length > 0) {
                element.setName(values[0]);
              }
            }
          }
        }

        // Handle customLevels binding: loggedCrp.customLevels[i].locElementType.id and .check
        if (key.startsWith("loggedCrp.customLevels[")) {
          try {
            int idx = extractIndex(key);
            
            // Use existing customLevels from prepare() if available
            if (loggedCrp.getCustomLevels() != null && idx < loggedCrp.getCustomLevels().size()) {
              CustomLevelSelect cls = loggedCrp.getCustomLevels().get(idx);
              String[] values = params.get(key);
              
              if (key.endsWith(".check")) {
                if (values != null && values.length > 0) {
                  String v = values[0].trim();
                  cls.setCheck("true".equalsIgnoreCase(v) || "on".equalsIgnoreCase(v) || "1".equals(v));
                }
              }
            }
          } catch (Exception e) {
            LOG.error("Error binding customLevels key: {}", key, e);
          }
        }

      } catch (Exception e) {
        LOG.error("Error in manual binding for key: {}", key, e);
      }
    }
  }

  
  private int extractIndex(String key) {
    int startIdx = key.indexOf('[') + 1;
    int endIdx = key.indexOf(']');
    try {
      return Integer.parseInt(key.substring(startIdx, endIdx));
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  private int extractSecondIndex(String key) {
    int firstClose = key.indexOf(']');
    int secondStart = key.indexOf('[', firstClose) + 1;
    int secondEnd = key.indexOf(']', firstClose + 1);
    try {
      return Integer.parseInt(key.substring(secondStart, secondEnd));
    } catch (Exception e) {
      return -1;
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
