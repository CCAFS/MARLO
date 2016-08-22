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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocGeopositionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.CountryLocationLevel;
import org.cgiar.ccafs.marlo.utils.LocationLevel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocationAction extends BaseAction {


  private static final long serialVersionUID = -3215013554941621274L;


  private Crp loggedCrp;

  private Project project;


  private List<LocationLevel> locationsLevels;

  // private List<CountryLocationLevel> locationsData;


  private CrpManager crpManager;

  private ProjectManager projectManager;


  private LocElementTypeManager locElementTypeManager;

  private ProjectLocationManager projectLocationManager;


  private LocElementManager locElementManager;

  private LocGeopositionManager locGeopositionManager;

  private String transaction;

  private long projectID;

  private AuditLogManager auditLogManager;

  @Inject
  public ProjectLocationAction(APConfig config, CrpManager crpManager, ProjectManager projectManager,
    LocElementTypeManager locElementTypeManager, LocElementManager locElementManager,
    ProjectLocationManager projectLocationManager, LocGeopositionManager locGeopositionManager,
    AuditLogManager auditLogManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.locElementTypeManager = locElementTypeManager;
    this.locElementManager = locElementManager;
    this.projectLocationManager = projectLocationManager;
    this.locGeopositionManager = locGeopositionManager;
    this.auditLogManager = auditLogManager;
  }

  public List<LocationLevel> getLocationsLevels() {
    return locationsLevels;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<CountryLocationLevel> getProjectLocationsData() {

    List<Map<String, Object>> parentLocations = new ArrayList<>();
    List<CountryLocationLevel> locationLevels = new ArrayList<>();

    project.setLocations(new ArrayList<ProjectLocation>(
      project.getProjectLocations().stream().filter(p -> p.isActive()).collect(Collectors.toList())));

    if (!project.getLocations().isEmpty()) {
      Map<String, Object> locationParent;

      for (ProjectLocation location : project.getLocations()) {
        locationParent = new HashMap<String, Object>();
        if (!parentLocations.isEmpty()) {
          locationParent.put(location.getLocElement().getLocElementType().getName(),
            location.getLocElement().getLocElementType().getId());
          if (!parentLocations.contains(locationParent)) {
            parentLocations.add(locationParent);
          }
        } else {
          locationParent.put(location.getLocElement().getLocElementType().getName(),
            location.getLocElement().getLocElementType().getId());
          parentLocations.add(locationParent);
        }

      }

    }

    CountryLocationLevel countryLocationLevel;

    for (Map<String, Object> map : parentLocations) {

      for (Map.Entry<String, Object> entry : map.entrySet()) {
        countryLocationLevel = new CountryLocationLevel();
        countryLocationLevel.setId(Long.parseLong(entry.getValue().toString()));
        countryLocationLevel.setName(entry.getKey());
        countryLocationLevel.setLocElements(new ArrayList<LocElement>());

        countryLocationLevel.setAllElements(new ArrayList<LocElement>(
          locElementTypeManager.getLocElementTypeById(Long.parseLong(entry.getValue().toString())).getLocElements()));

        for (ProjectLocation projectLocation : project.getLocations().stream().filter(l -> l.isActive())
          .collect(Collectors.toList())) {
          if (projectLocation.getLocElement().getLocElementType().getId() == Long
            .parseLong(entry.getValue().toString())) {
            countryLocationLevel.getLocElements().add(projectLocation.getLocElement());
          }
        }

        if (Long.parseLong(entry.getValue().toString()) != 2) {
          countryLocationLevel.setList(false);
        } else {
          countryLocationLevel.setList(true);
        }

        locationLevels.add(countryLocationLevel);
      }

    }

    return locationLevels;
  }

  public String getTransaction() {
    return transaction;
  }


  /**
  * 
  */
  public void locationLevels() {

    locationsLevels = new ArrayList<>();
    List<CountryLocationLevel> countryLocationLevels = new ArrayList<>();
    countryLocationLevels = new ArrayList<>();
    List<LocElementType> customElementTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() != null && let.getCrp().equals(loggedCrp) && let.getId() != 1)
      .collect(Collectors.toList());

    for (LocElementType locElementType : customElementTypes) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(locElementType.getId());
      countryLocationLevel.setName(locElementType.getName());
      countryLocationLevel.setAllElements(new ArrayList<LocElement>(locElementType.getLocElements()));
      countryLocationLevel.setList(true);
      countryLocationLevels.add(countryLocationLevel);
    }

    locationsLevels
      .add(new LocationLevel(loggedCrp.getAcronym().toUpperCase() + " Custom Locations", countryLocationLevels));

    countryLocationLevels = new ArrayList<>();
    List<LocElementType> elementTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() == null && let.getId() != 1).collect(Collectors.toList());

    for (LocElementType locElementType : elementTypes) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(locElementType.getId());
      countryLocationLevel.setName(locElementType.getName());
      countryLocationLevel.setAllElements(new ArrayList<LocElement>(locElementType.getLocElements()));
      if (locElementType.getId() != 2) {
        countryLocationLevel.setList(false);
      } else {
        countryLocationLevel.setList(true);
      }

      countryLocationLevels.add(countryLocationLevel);
    }

    locationsLevels.add(new LocationLevel("Other Locations", countryLocationLevels));

  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      project = projectManager.getProjectById(projectID);
    }

    this.locationLevels();
    // locationsData = this.getProjectLocationsData();
    project.setLocationsData(this.getProjectLocationsData());


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_LOCATION_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (project.getLocationsData() != null) {
      }
    }

  }

  public void projectLocationNewData() {
    for (CountryLocationLevel locationData : project.getLocationsData()) {
      if (locationData.getId() == null) {
        if (locationData.getLocElements() != null && !locationData.getLocElements().isEmpty()) {
          for (LocElement locElement : locationData.getLocElements()) {
            if (locElement.getId() != null) {
              LocElement element = locElementManager.getLocElementById(locElement.getId());

              ProjectLocation existProjectLocation =
                projectLocationManager.getProjectLocationByProjectAndLocElement(project.getId(), locElement.getId());

              if (existProjectLocation == null) {
                ProjectLocation projectLocation = new ProjectLocation();
                projectLocation.setProject(project);
                projectLocation.setLocElement(element);
                projectLocation.setActive(true);
                projectLocation.setActiveSince(new Date());
                projectLocation.setCreatedBy(this.getCurrentUser());
                projectLocation.setModificationJustification("");
                projectLocation.setModifiedBy(this.getCurrentUser());

                projectLocationManager.saveProjectLocation(projectLocation);

              } else {

                if (!existProjectLocation.isActive()) {
                  existProjectLocation.setActive(true);
                  existProjectLocation.setActiveSince(new Date());
                  existProjectLocation.setCreatedBy(this.getCurrentUser());
                  existProjectLocation.setModificationJustification("");
                  existProjectLocation.setModifiedBy(this.getCurrentUser());
                  projectLocationManager.saveProjectLocation(existProjectLocation);
                }
              }
            } else {
              this.saveGeoProjectLocation(locElement, locationData.getId());
            }
          }
        }
      } else {

        for (LocElement locElement : locationData.getLocElements()) {
          if (locElement.getId() != null) {
            LocElement element = locElementManager.getLocElementById(locElement.getId());
            if (element.getLocGeoposition() != null) {
              if ((element.getLocGeoposition().getLatitude() != locElement.getLocGeoposition().getLatitude())
                || (element.getLocGeoposition().getLongitude() != locElement.getLocGeoposition().getLongitude())) {
                element.getLocGeoposition().setLongitude(locElement.getLocGeoposition().getLongitude());
                element.getLocGeoposition().setLatitude(locElement.getLocGeoposition().getLatitude());

                locGeopositionManager.saveLocGeoposition(element.getLocGeoposition());
              }
            } else {
              ProjectLocation existProjectLocation =
                projectLocationManager.getProjectLocationByProjectAndLocElement(project.getId(), locElement.getId());
              if (existProjectLocation == null) {


                ProjectLocation projectLocation = new ProjectLocation();
                projectLocation.setProject(project);
                projectLocation.setLocElement(element);
                projectLocation.setActive(true);
                projectLocation.setActiveSince(new Date());
                projectLocation.setCreatedBy(this.getCurrentUser());
                projectLocation.setModificationJustification("");
                projectLocation.setModifiedBy(this.getCurrentUser());

                projectLocationManager.saveProjectLocation(projectLocation);
              } else {

                if (!existProjectLocation.isActive()) {
                  existProjectLocation.setActive(true);
                  existProjectLocation.setActiveSince(new Date());
                  existProjectLocation.setCreatedBy(this.getCurrentUser());
                  existProjectLocation.setModificationJustification("");
                  existProjectLocation.setModifiedBy(this.getCurrentUser());
                  projectLocationManager.saveProjectLocation(existProjectLocation);
                }
              }
            }
          } else {
            this.saveGeoProjectLocation(locElement, locationData.getId());
          }
        }
      }
    }
  }

  public void projectLocationPreviousData() {
    List<CountryLocationLevel> locationsDataPrew = this.getProjectLocationsData();

    for (CountryLocationLevel countryLocationLevel : locationsDataPrew) {
      if (!project.getLocationsData().contains(countryLocationLevel)) {
        for (LocElement locElement : countryLocationLevel.getLocElements()) {
          ProjectLocation projectLocation = project.getProjectLocations().stream()
            .filter(pl -> pl.isActive() && pl.getLocElement().getId() == locElement.getId())
            .collect(Collectors.toList()).get(0);
          projectLocationManager.deleteProjectLocation(projectLocation.getId());
        }
      } else {
        for (CountryLocationLevel locationData : project.getLocationsData()) {
          if (locationData.equals(countryLocationLevel)) {
            List<LocElement> locElements = countryLocationLevel.getLocElements();
            for (LocElement element : locElements) {
              if (!locationData.getLocElements().contains(element)) {
                ProjectLocation projectLocation = project.getProjectLocations().stream()
                  .filter(pl -> pl.isActive() && pl.getLocElement().getId() == element.getId())
                  .collect(Collectors.toList()).get(0);
                projectLocationManager.deleteProjectLocation(projectLocation.getId());
              }
            }
          }
        }
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification("");
      project.setActiveSince(projectDB.getActiveSince());

      this.projectLocationPreviousData();

      this.projectLocationNewData();

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_LOCATIONS_RELATION);
      project.setActiveSince(new Date());
      projectManager.saveProject(project, this.getActionName(), relationsName);

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      return SUCCESS;
    }
    return SUCCESS;
  }

  public void saveGeoProjectLocation(LocElement locElement, Long elementTypeId) {
    LocElement parentElement = locElementManager.getLocElementByISOCode(locElement.getIsoAlpha2());
    LocElementType typeLement = locElementTypeManager.getLocElementTypeById(elementTypeId);

    LocGeoposition geoposition = new LocGeoposition();
    geoposition.setActive(true);
    geoposition.setActiveSince(new Date());
    geoposition.setCreatedBy(this.getCurrentUser());
    geoposition.setModifiedBy(this.getCurrentUser());
    geoposition.setModificationJustification("");
    geoposition.setLatitude(locElement.getLocGeoposition().getLatitude());
    geoposition.setLongitude(locElement.getLocGeoposition().getLongitude());

    LocGeoposition geoParent =
      locGeopositionManager.getLocGeopositionById(locGeopositionManager.saveLocGeoposition(geoposition));

    LocElement element = new LocElement();
    element.setActive(true);
    element.setActiveSince(new Date());
    element.setCreatedBy(this.getCurrentUser());
    element.setModifiedBy(this.getCurrentUser());
    element.setModificationJustification("");
    element.setCrp(loggedCrp);
    element.setLocElement(parentElement);
    element.setLocElementType(typeLement);
    element.setName(locElement.getName());
    element.setLocGeoposition(geoParent);
    element.setIsSiteIntegration(false);

    LocElement newLocElement = locElementManager.getLocElementById(locElementManager.saveLocElement(element));

    ProjectLocation projectLocation = new ProjectLocation();
    projectLocation.setProject(project);
    projectLocation.setLocElement(newLocElement);
    projectLocation.setActive(true);
    projectLocation.setActiveSince(new Date());
    projectLocation.setCreatedBy(this.getCurrentUser());
    projectLocation.setModificationJustification("");
    projectLocation.setModifiedBy(this.getCurrentUser());

    projectLocationManager.saveProjectLocation(projectLocation);
  }

  public void setLocationsLevels(List<LocationLevel> locationsLevels) {
    this.locationsLevels = locationsLevels;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

}
