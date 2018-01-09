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
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocGeopositionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CountryFundingSources;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpLocElementType;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ScopeData;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.CountryLocationLevel;
import org.cgiar.ccafs.marlo.utils.LocationLevel;
import org.cgiar.ccafs.marlo.validation.projects.ProjectLocationValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocationAction extends BaseAction {


  private static final long serialVersionUID = -3215013554941621274L;

  private final AuditLogManager auditLogManager;


  private final CrpManager crpManager;


  private final FundingSourceManager fundingSourceManager;
  private List<LocationLevel> locationsLevels;


  private final ProjectLocationValidator locationValidator;


  private final LocElementManager locElementManager;

  private final LocElementTypeManager locElementTypeManager;

  private final LocGeopositionManager locGeopositionManager;


  private Crp loggedCrp;

  private Project project;


  private long projectID;

  private final ProjectLocationElementTypeManager projectLocationElementTypeManager;

  private final ProjectLocationManager projectLocationManager;

  private final ProjectManager projectManager;

  private boolean region;

  private List<LocElement> regionLists;

  private List<ScopeData> scopeData;
  private List<LocElementType> scopeRegionLists;

  private List<LocElementType> scopeRegions;

  private String transaction;


  @Inject
  public ProjectLocationAction(APConfig config, CrpManager crpManager, ProjectManager projectManager,
    LocElementTypeManager locElementTypeManager, LocElementManager locElementManager,
    ProjectLocationManager projectLocationManager, LocGeopositionManager locGeopositionManager,
    AuditLogManager auditLogManager, ProjectLocationValidator locationValidator,
    ProjectLocationElementTypeManager projectLocationElementTypeManager, FundingSourceManager fundingSourceManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.locElementTypeManager = locElementTypeManager;
    this.locElementManager = locElementManager;
    this.projectLocationManager = projectLocationManager;
    this.locGeopositionManager = locGeopositionManager;
    this.auditLogManager = auditLogManager;
    this.locationValidator = locationValidator;
    this.projectLocationElementTypeManager = projectLocationElementTypeManager;
    this.fundingSourceManager = fundingSourceManager;
  }


  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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
    List<ProjectLocationElementType> locationsElementType = new ArrayList<>(
      project.getProjectLocationElementTypes().stream().filter(pl -> pl.getIsGlobal()).collect(Collectors.toList()));

    project.setLocations(new ArrayList<ProjectLocation>(project
      .getProjectLocations().stream().filter(p -> p.isActive() && p.getLocElementType() == null
        && p.getLocElement() != null && p.getLocElement().getLocElementType().getId().longValue() != 1)
      .collect(Collectors.toList())));
    Map<String, Object> locationParent;
    if (!project.getLocations().isEmpty()) {

      if (locationsElementType != null) {
        for (ProjectLocationElementType projectLocationElementType : locationsElementType) {
          boolean existElementType = false;
          for (ProjectLocation location : project.getLocations()) {
            if (projectLocationElementType.getLocElementType().getId() == location.getLocElement().getLocElementType()
              .getId()) {
              existElementType = true;
            }
          }
          if (!existElementType) {
            locationParent = new HashMap<String, Object>();
            if (!parentLocations.isEmpty()) {
              locationParent.put(projectLocationElementType.getLocElementType().getName(),
                projectLocationElementType.getLocElementType().getId());
              if (!parentLocations.contains(locationParent)) {
                parentLocations.add(locationParent);
              }
            } else {
              locationParent.put(projectLocationElementType.getLocElementType().getName(),
                projectLocationElementType.getLocElementType().getId());
              parentLocations.add(locationParent);
            }
          }
        }
      }

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

    } else {
      if (!locationsElementType.isEmpty()) {
        for (ProjectLocationElementType projectLocationElementType : locationsElementType) {
          locationParent = new HashMap<String, Object>();
          if (!parentLocations.isEmpty()) {
            locationParent.put(projectLocationElementType.getLocElementType().getName(),
              projectLocationElementType.getLocElementType().getId());
            if (!parentLocations.contains(locationParent)) {
              parentLocations.add(locationParent);
            }
          } else {
            locationParent.put(projectLocationElementType.getLocElementType().getName(),
              projectLocationElementType.getLocElementType().getId());
            parentLocations.add(locationParent);
          }
        }
      }
    }

    CountryLocationLevel countryLocationLevel;
    ProjectLocationElementType locationElementType = null;
    for (Map<String, Object> map : parentLocations) {

      for (Map.Entry<String, Object> entry : map.entrySet()) {
        countryLocationLevel = new CountryLocationLevel();
        countryLocationLevel.setId(Long.parseLong(entry.getValue().toString()));
        countryLocationLevel.setName(entry.getKey());
        countryLocationLevel.setLocElements(new ArrayList<LocElement>());

        LocElementType elementType =
          locElementTypeManager.getLocElementTypeById(Long.parseLong(entry.getValue().toString()));

        countryLocationLevel.setAllElements(new ArrayList<LocElement>(elementType.getLocElements()));

        for (ProjectLocation projectLocation : project.getLocations().stream().filter(l -> l.isActive())
          .collect(Collectors.toList())) {
          if (projectLocation.getLocElement().getLocElementType().getId() == Long
            .parseLong(entry.getValue().toString())) {
            countryLocationLevel.getLocElements().add(projectLocation.getLocElement());
          }
        }

        if (elementType.getId() == 2 || elementType.getCrp() != null) {

          locationElementType =
            projectLocationElementTypeManager.getByProjectAndElementType(projectID, elementType.getId());

          countryLocationLevel.setList(true);

        } else {
          countryLocationLevel.setList(false);

        }

        locationLevels.add(countryLocationLevel);
      }


    }

    return locationLevels;
  }


  public List<LocElement> getRegionLists() {
    return regionLists;
  }

  public List<ScopeData> getScopeData() {
    return scopeData;
  }

  public List<LocElementType> getScopeRegionLists() {
    return scopeRegionLists;
  }

  public List<LocElementType> getScopeRegions() {
    return scopeRegions;
  }


  public String getTransaction() {
    return transaction;
  }

  public boolean isRegion() {
    return region;
  }

  public void listScopeRegions() {

    List<LocElementType> scopeRegionsPrew = locElementTypeManager.findAll().stream()
      .filter(et -> et.isActive() && et.isScope() && et.getCrp().getId() == loggedCrp.getId())
      .collect(Collectors.toList());

    scopeRegions = new ArrayList<>();


    if (project.getLocationsData() != null) {
      for (CountryLocationLevel locationData : project.getLocationsData()) {
        if (locationData.getLocElements() != null) {
          for (LocElement locElement : locationData.getLocElements()) {
            if (locElement.getId() != null && locElement.getId() != -1) {

              LocElement elementReview = locElementManager.getLocElementById(locElement.getId());

              while (true) {
                long elementReviewType = elementReview.getLocElementType().getId();

                if (elementReviewType == 2) {

                  for (LocElementType locElementType : scopeRegionsPrew) {

                    List<LocElement> scopeElements = new ArrayList<>(locElementType.getLocElements().stream()
                      .filter(lc -> lc.isActive()).collect(Collectors.toList()));

                    for (LocElement scopeElement : scopeElements) {
                      LocElement scopeParentElement = scopeElement.getLocElement();
                      if (scopeParentElement.equals(elementReview)) {
                        if (scopeRegions.isEmpty()) {
                          scopeRegions.add(locElementType);
                        } else {
                          if (!scopeRegions.contains(locElementType)) {
                            scopeRegions.add(locElementType);
                          }
                        }
                      }
                    }


                  }

                  break;

                } else {
                  if (elementReview.getLocElement() != null) {
                    elementReview = locElementManager.getLocElementById(elementReview.getLocElement().getId());
                  } else {
                    break;
                  }
                }
              }

            }
          }
        }
      }
    }
  }

  /**
   * 
   */
  public void locationLevels() {

    locationsLevels = new ArrayList<>();
    List<CountryLocationLevel> countryLocationLevels = new ArrayList<>();
    countryLocationLevels = new ArrayList<>();
    List<LocElementType> customElementTypes =
      locElementTypeManager.findAll().stream().filter(let -> let.isActive() && let.getCrp() != null
        && let.getCrp().equals(loggedCrp) && let.getId() != 1 && !let.isScope()).collect(Collectors.toList());

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
    List<LocElementType> elementTypes = new ArrayList<>();
    Crp crpBD = crpManager.getCrpById(this.getCrpID());
    for (CrpLocElementType locElementType : crpBD.getCrpLocElementTypes().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      elementTypes.add(locElementType.getLocElementType());
    }

    Collections.sort(elementTypes, (tu1, tu2) -> tu1.getName().compareTo(tu2.getName()));
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

  public boolean locElementSelected(long locElementID) {

    if (project.getLocationsData() != null) {

      if (locElementManager.getLocElementById(locElementID).getLocElementType().getId().longValue() == 1) {

        if (project.getProjectRegions() != null) {
          List<ProjectLocation> locElements = project.getProjectRegions().stream()
            .filter(c -> c.getLocElement() != null && c.getLocElement().getId().longValue() == locElementID)
            .collect(Collectors.toList());

          return !locElements.isEmpty();
        } else {
          return false;
        }


      } else {
        List<CountryLocationLevel> locElements = project.getLocationsData().stream()
          .filter(c -> c.getLocElements() != null
            && c.getLocElements().contains(locElementManager.getLocElementById(locElementID)))
          .collect(Collectors.toList());
        return !locElements.isEmpty();

      }
    } else {
      Project projectDB = projectManager.getProjectById(projectID);
      List<ProjectLocation> locElements = projectDB.getProjectLocations().stream()
        .filter(c -> c.isActive() && c.getLocElement() != null && c.getLocElement().getId().longValue() == locElementID)
        .collect(Collectors.toList());

      return !locElements.isEmpty();
    }


  }

  public boolean locElementTypeSelected(long locElementID) {

    if (project.getProjectRegions() != null) {
      List<ProjectLocation> locElements = project.getProjectRegions().stream()
        .filter(c -> c.getLocElementType() != null && c.getLocElementType().getId().longValue() == locElementID)
        .collect(Collectors.toList());

      return !locElements.isEmpty();
    } else {
      Project projectDB = projectManager.getProjectById(projectID);
      List<ProjectLocation> locElements = projectDB.getProjectLocations().stream().filter(
        c -> c.isActive() && c.getLocElementType() != null && c.getLocElementType().getId().longValue() == locElementID)
        .collect(Collectors.toList());

      return !locElements.isEmpty();
    }


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
    if (project != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectEditLeader(projectDb.isProjectEditLeader());
        project.setProjectLocations(projectDb.getProjectLocations());
        project.setAdministrative(projectDb.getAdministrative());
        if (project.getLocationsData() != null) {
          for (CountryLocationLevel level : project.getLocationsData()) {
            LocElementType elementType = locElementTypeManager.getLocElementTypeById(level.getId());
            if (elementType.getId() == 2 || elementType.getCrp() != null) {
              level.setAllElements(
                elementType.getLocElements().stream().filter(le -> le.isActive()).collect(Collectors.toList()));
            }
          }

        }
        if (project.getProjectRegions() != null) {
          for (ProjectLocation projectLocation : project.getProjectRegions()) {
            if (projectLocation.getLocElement() != null && projectLocation.getLocElement().getId() != null) {
              projectLocation
                .setLocElement(locElementManager.getLocElementById(projectLocation.getLocElement().getId()));
              projectLocation.setLocElementType(null);
            } else {
              projectLocation.setLocElementType(
                locElementTypeManager.getLocElementTypeById(projectLocation.getLocElementType().getId()));
              projectLocation.setLocElement(null);
            }
          }
        }

        List<CountryFundingSources> reCountryFundingSources = new ArrayList<>();
        List<CountryFundingSources> coCountryFundingSources = new ArrayList<>();
        if (project.getRegionFS() != null) {
          for (CountryFundingSources co : project.getRegionFS()) {
            if (co.getLocElement() != null) {
              co.setLocElement(locElementManager.getLocElementById(co.getLocElement().getId()));
              List<FundingSource> sources = fundingSourceManager.searchFundingSourcesByLocElement(projectID,
                co.getLocElement().getId(), this.getCurrentCycleYear(), loggedCrp.getId());
              co.setFundingSources(sources);
            } else {
              co.setLocElementType(locElementTypeManager.getLocElementTypeById(co.getLocElementType().getId()));
              List<FundingSource> sources = fundingSourceManager.searchFundingSourcesByLocElementType(projectID,
                co.getLocElementType().getId(), this.getCurrentCycleYear(), loggedCrp.getId());
              co.setFundingSources(sources);
            }
            if (!co.isSelected()) {
              if (co.getLocElement() != null) {
                co.setSelected(this.locElementSelected(co.getLocElement().getId()));
              } else {
                co.setSelected(this.locElementTypeSelected(co.getLocElementType().getId()));
              }
            }
            if (!co.getFundingSources().stream()
              .filter(c -> c.isActive() && c.getProjectBudgets().stream()
                .filter(
                  fp -> fp.isActive() && fp.getProject().isActive() && fp.getProject().getId().longValue() == projectID)
                .collect(Collectors.toList()).size() > 0)
              .collect(Collectors.toList()).isEmpty()) {
              reCountryFundingSources.add(co);
            }

          }
        }
        project.setRegionFS(reCountryFundingSources);
        if (project.getCountryFS() != null) {
          for (CountryFundingSources co : project.getCountryFS()) {
            if (co.getLocElement() != null) {
              co.setLocElement(locElementManager.getLocElementById(co.getLocElement().getId()));

              List<FundingSource> sources = fundingSourceManager.searchFundingSourcesByLocElement(projectID,
                co.getLocElement().getId(), this.getCurrentCycleYear(), loggedCrp.getId());
              co.setFundingSources(new ArrayList<>(sources));

            } else {
              co.setLocElementType(locElementTypeManager.getLocElementTypeById(co.getLocElementType().getId()));
            }
            if (!co.isSelected()) {
              if (co.getLocElement() != null) {
                co.setSelected(this.locElementSelected(co.getLocElement().getId()));
              } else {
                co.setSelected(this.locElementTypeSelected(co.getLocElementType().getId()));
              }
            }
            if (!co.getFundingSources().stream()
              .filter(c -> c.isActive() && c.getProjectBudgets().stream()
                .filter(
                  fp -> fp.isActive() && fp.getProject().isActive() && fp.getProject().getId().longValue() == projectID)
                .collect(Collectors.toList()).size() > 0)
              .collect(Collectors.toList()).isEmpty()) {
              coCountryFundingSources.add(co);
            }
          }
          project.setCountryFS(coCountryFundingSources);
        }

        this.prepareFundingList();
        this.setDraft(true);
      } else {
        this.setDraft(false);


        this.prepareFundingList();


        for (CountryFundingSources locElement : project.getCountryFS()) {
          locElement.setSelected(this.locElementSelected(locElement.getLocElement().getId()));
        }
        for (CountryFundingSources locElement : project.getRegionFS()) {
          if (locElement.getLocElement() != null) {
            locElement.setSelected(this.locElementSelected(locElement.getLocElement().getId()));
          } else {
            locElement.setSelected(this.locElementTypeSelected(locElement.getLocElementType().getId()));
          }

        }
        project.setLocationsData(this.getProjectLocationsData());
        project.setProjectRegions(new ArrayList<ProjectLocation>(project
          .getProjectLocations().stream().filter(p -> p.isActive() && p.getLocElementType() == null
            && p.getLocElement() != null && p.getLocElement().getLocElementType().getId().longValue() == 1)
          .collect(Collectors.toList())));
        project.getProjectRegions()
          .addAll(project.getProjectLocations().stream()
            .filter(p -> p.isActive() && p.getLocElementType() != null && p.getLocElement() == null)
            .collect(Collectors.toList()));

      }
    }


    this.listScopeRegions();


    Collection<LocElement> fsLocs = new ArrayList<>();

    if (project.getCountryFS() == null) {
      project.setCountryFS(new ArrayList<>());
    }
    if (project.getRegionFS() == null) {
      project.setRegionFS(new ArrayList<>());
    }

    for (CountryFundingSources locElement : project.getCountryFS()) {
      fsLocs.add(locElement.getLocElement());
    }


    if (project.getLocationsData() == null) {
      project.setLocationsData(new ArrayList<>());

      // Fix Ull Collection when autosave gets the suggeste country - 10/13/2017
      for (CountryLocationLevel countryLocationLevel : project.getLocationsData()) {

        Collection<LocElement> similar = new HashSet<LocElement>(countryLocationLevel.getLocElements());
        Collection<LocElement> different = new HashSet<LocElement>();
        different.addAll(countryLocationLevel.getLocElements());
        different.addAll(fsLocs);
        similar.retainAll(fsLocs);
        different.removeAll(similar);

        countryLocationLevel.getLocElements().removeAll(similar);


      }
    }


    Collection<LocElement> fsLocsRegions = new ArrayList<>();
    for (CountryFundingSources locElement : project.getRegionFS()) {
      if (locElement.getLocElement() != null) {
        fsLocsRegions.add(locElement.getLocElement());
      }

    }

    if (project.getProjectRegions() != null) {
      for (ProjectLocation projectLocation : project.getProjectRegions().stream().filter(c -> c.getLocElement() != null)
        .collect(Collectors.toList())) {

        if (fsLocsRegions.contains(projectLocation.getLocElement())) {
          project.getProjectRegions().remove(projectLocation);
        }

      }
    }

    Collection<LocElementType> fsLocsCustomRegions = new ArrayList<>();
    for (CountryFundingSources locElement : project.getRegionFS()) {
      if (locElement.getLocElementType() != null) {
        fsLocsCustomRegions.add(locElement.getLocElementType());

      }

    }

    if (project.getProjectRegions() != null) {
      for (ProjectLocation projectLocation : project.getProjectRegions().stream()
        .filter(c -> c.getLocElementType() != null).collect(Collectors.toList())) {

        if (fsLocsCustomRegions.contains(projectLocation.getLocElementType())) {
          project.getProjectRegions().remove(projectLocation);
        }

      }
    }

    regionLists = new ArrayList<>(locElementManager.findAll().stream()
      .filter(le -> le.isActive() && le.getLocElementType() != null && le.getLocElementType().getId() == 1)
      .collect(Collectors.toList()));
    Collections.sort(regionLists, (r1, r2) -> r1.getName().compareTo(r2.getName()));
    scopeRegionLists = new ArrayList<>(locElementTypeManager.findAll().stream()
      .filter(le -> le.isActive() && le.getCrp() != null && le.getCrp().equals(loggedCrp) && le.isScope())
      .collect(Collectors.toList()));
    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_LOCATION_BASE_PERMISSION, params));

    if (!project.getLocationsData().stream().filter(c -> c.getId().longValue() != 2).collect(Collectors.toList())
      .isEmpty()) {
      region = true;
    } else {
      region = false;
    }
    if (this.isHttpPost()) {
      if (project.getLocationsData() != null) {
        project.getLocationsData().clear();
      }

      project.setLocationGlobal(false);
      if (project.getCountryFS() != null) {
        project.getCountryFS().clear();
      }
      if (project.getRegionFS() != null) {
        project.getRegionFS().clear();
      }
    }

  }


  public void prepareFundingList() {

    Project projectDB = projectManager.getProjectById(project.getId());


    List<ProjectBudget> projectBudgets = new ArrayList<>(projectDB.getProjectBudgets().stream()
      .filter(pb -> pb.isActive() && pb.getYear() == this.getCurrentCycleYear() && pb.getProject().isActive())
      .collect(Collectors.toList()));

    List<FundingSource> fundingSources = new ArrayList<>();
    for (ProjectBudget projectBudget : projectBudgets) {

      fundingSources.add(projectBudget.getFundingSource());

    }

    HashSet<FundingSource> fuHashSet = new HashSet<>();
    fuHashSet.addAll(fundingSources);

    fundingSources = new ArrayList<>(fuHashSet);

    List<LocElement> locElements = new ArrayList<>();
    List<LocElementType> locElementTypes = new ArrayList<>();
    if (project.getLocationRegional() == null) {
      project.setLocationRegional(false);
    }

    boolean calculateYesOrNo = !this.hasSpecificities(APConstants.CRP_OTHER_LOCATIONS);
    if (calculateYesOrNo) {
      project.setLocationGlobal(false);
      project.setLocationRegional(false);
    }
    for (FundingSource fundingSource : fundingSources) {
      if (calculateYesOrNo) {
        project.setLocationGlobal(project.isLocationGlobal() || fundingSource.isGlobal());

      }
      // get the funding source info from db
      fundingSource = fundingSourceManager.getFundingSourceById(fundingSource.getId());
      // Filter The Fundign Source Location Acroding
      List<FundingSourceLocation> fundingSourceLocations = new ArrayList<>(
        fundingSource.getFundingSourceLocations().stream().filter(fs -> fs.isActive()).collect(Collectors.toList()));

      for (FundingSourceLocation fundingSourceLocation : fundingSourceLocations) {
        if (fundingSourceLocation.getLocElementType() == null) {
          locElements.add(fundingSourceLocation.getLocElement());
          if (fundingSourceLocation.getLocElement().getLocElementType().getId() != 2) {
            if (calculateYesOrNo) {
              project.setLocationRegional(project.getLocationRegional() || true);
            }

          }

        } else {
          locElementTypes.add(fundingSourceLocation.getLocElementType());
          if (calculateYesOrNo) {
            project.setLocationRegional(project.getLocationRegional() || true);
          }
        }
      }


    }


    if (project.getCountryFS() == null) {
      project.setCountryFS(new ArrayList<>());
    }

    if (project.getRegionFS() == null) {
      project.setRegionFS(new ArrayList<>());
    }


    HashSet<LocElement> hashElements = new HashSet<>();
    hashElements.addAll(locElements);
    locElements = new ArrayList<>(hashElements);

    for (LocElement locElement : hashElements) {
      CountryFundingSources countryFundingSources = new CountryFundingSources();
      countryFundingSources.setLocElement(locElement);

      List<FundingSource> sources = fundingSourceManager.searchFundingSourcesByLocElement(projectID, locElement.getId(),
        this.getCurrentCycleYear(), loggedCrp.getId());
      countryFundingSources.setFundingSources(new ArrayList<>(sources));
      if (locElement.getLocElementType().getId().longValue() == 2) {
        if (!project.getCountryFS().contains(countryFundingSources)) {
          project.getCountryFS().add(countryFundingSources);
        }

      } else {
        if (!project.getRegionFS().contains(countryFundingSources)) {
          project.getRegionFS().add(countryFundingSources);
        }
      }


    }

    HashSet<LocElementType> hashElementTypes = new HashSet<>();
    hashElementTypes.addAll(locElementTypes);
    locElementTypes = new ArrayList<>(hashElementTypes);

    for (LocElementType locElementType : hashElementTypes) {
      CountryFundingSources countryFundingSources = new CountryFundingSources();
      countryFundingSources.setLocElementType(locElementType);
      List<FundingSource> sources = fundingSourceManager.searchFundingSourcesByLocElementType(projectID,
        locElementType.getId(), this.getCurrentCycleYear(), loggedCrp.getId());
      countryFundingSources.setFundingSources(new ArrayList<>(sources));
      if (!project.getRegionFS().contains(countryFundingSources)) {
        project.getRegionFS().add(countryFundingSources);
      }

    }
    Collections.sort(project.getCountryFS(),
      (tu1, tu2) -> tu1.getLocElement().getName().compareTo(tu2.getLocElement().getName()));


  }


  public void projectLocationNewData() {

    List<CountryLocationLevel> locationsDataPrew = this.getProjectLocationsData();

    if (project.getLocationsData() != null) {
      for (CountryLocationLevel locationData : project.getLocationsData()) {
        if (!locationsDataPrew.contains(locationData)) {


          if (locationData.getLocElements() != null && !locationData.getLocElements().isEmpty()) {
            for (LocElement locElement : locationData.getLocElements()) {
              if (locElement.getId() != null && locElement.getId() != -1) {

                LocElement element = locElementManager.getLocElementById(locElement.getId());

                if (!element.getName().equals(locElement.getName())) {
                  element.setName(locElement.getName());
                  locElementManager.saveLocElement(element);
                }

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

            ProjectLocationElementType projectLocationElementType =
              projectLocationElementTypeManager.getByProjectAndElementType(project.getId(), locationData.getId());

            if (projectLocationElementType == null) {
              ProjectLocationElementType newProjectLocationElementType = new ProjectLocationElementType();

              LocElementType locElementType = locElementTypeManager.getLocElementTypeById(locationData.getId());

              newProjectLocationElementType.setLocElementType(locElementType);

              Project project = projectManager.getProjectById(this.project.getId());

              newProjectLocationElementType.setProject(project);

              newProjectLocationElementType.setIsGlobal(false);

              projectLocationElementTypeManager.saveProjectLocationElementType(newProjectLocationElementType);

            } else {
              projectLocationElementType.setIsGlobal(false);

              projectLocationElementTypeManager.saveProjectLocationElementType(projectLocationElementType);
            }

          }
        } else {

          if (locationData.getLocElements() != null) {
            for (LocElement locElement : locationData.getLocElements()) {
              if (locElement.getId() != null && locElement.getId() != -1) {

                LocElement element = locElementManager.getLocElementById(locElement.getId());

                if (!element.getName().equals(locElement.getName())) {
                  element.setName(locElement.getName());
                  locElementManager.saveLocElement(element);
                }

                if (element.getLocGeoposition() != null && element.getLocElementType().getCrp() == null) {
                  if ((element.getLocGeoposition().getLatitude() != locElement.getLocGeoposition().getLatitude())
                    || (element.getLocGeoposition().getLongitude() != locElement.getLocGeoposition().getLongitude())) {
                    element.getLocGeoposition().setLongitude(locElement.getLocGeoposition().getLongitude());
                    element.getLocGeoposition().setLatitude(locElement.getLocGeoposition().getLatitude());

                    locGeopositionManager.saveLocGeoposition(element.getLocGeoposition());
                  }
                } else {
                  ProjectLocation existProjectLocation = projectLocationManager
                    .getProjectLocationByProjectAndLocElement(project.getId(), locElement.getId());
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
                  }
                }
              } else {
                this.saveGeoProjectLocation(locElement, locationData.getId());
              }
            }
          }


        }
      }
    }


    Project projectDB = projectManager.getProjectById(projectID);
    List<LocElement> regionsCustomSaved = new ArrayList<>();

    for (ProjectLocation projectLocation : projectDB.getProjectLocations().stream().filter(
      c -> c.isActive() && c.getLocElement() != null && c.getLocElement().getLocElementType().getId().longValue() == 2)
      .collect(Collectors.toList())) {

      regionsCustomSaved.add(projectLocation.getLocElement());

    }


    if (project.getCountryFS() == null) {
      project.setCountryFS(new ArrayList<>());
    }

    for (CountryFundingSources countryFundingSources : project.getCountryFS()) {

      ProjectLocation projectLocationSave = new ProjectLocation();
      projectLocationSave.setActive(true);
      projectLocationSave.setActiveSince(new Date());
      projectLocationSave.setCreatedBy(this.getCurrentUser());
      projectLocationSave.setModifiedBy(this.getCurrentUser());
      projectLocationSave.setModificationJustification("");
      projectLocationSave.setProject(project);

      if (!regionsCustomSaved.contains(countryFundingSources.getLocElement()) && countryFundingSources.isSelected()) {

        projectLocationSave.setLocElement(countryFundingSources.getLocElement());
        projectLocationManager.saveProjectLocation(projectLocationSave);
      }

    }


  }

  public void projectLocationPreviousData() {
    List<CountryLocationLevel> locationsDataPrew = this.getProjectLocationsData();

    for (CountryLocationLevel countryLocationLevel : locationsDataPrew) {
      if (!project.getLocationsData().contains(countryLocationLevel)) {
        for (LocElement locElement : countryLocationLevel.getLocElements()) {
          ProjectLocation projectLocation = project.getProjectLocations().stream()
            .filter(
              pl -> pl.isActive() && pl.getLocElement() != null && pl.getLocElement().getId() == locElement.getId())
            .collect(Collectors.toList()).get(0);

          if (locElementManager.getLocElementById(projectLocation.getLocElement().getId()).getLocElementType().getId()
            .longValue() == 2) {
            if (project.getCountryFS().stream()
              .filter(c -> c.getLocElement() != null
                && c.getLocElement().getId().longValue() == projectLocation.getLocElement().getId().longValue()
                && c.isSelected())
              .collect(Collectors.toList()).isEmpty()) {
              projectLocationManager.deleteProjectLocation(projectLocation.getId());
            }
          } else {
            projectLocationManager.deleteProjectLocation(projectLocation.getId());
          }


        }

        ProjectLocationElementType projectLocationElementType =
          projectLocationElementTypeManager.getByProjectAndElementType(project.getId(), countryLocationLevel.getId());

        if (projectLocationElementType != null) {
          projectLocationElementTypeManager.deleteProjectLocationElementType(projectLocationElementType.getId());
        }
      } else {
        for (CountryLocationLevel locationData : project.getLocationsData()) {
          if (locationData.equals(countryLocationLevel)) {
            List<LocElement> locElements = countryLocationLevel.getLocElements();
            for (LocElement element : locElements) {
              if (locationData.getLocElements() != null) {
                if (!locationData.getLocElements().contains(element)) {
                  ProjectLocation projectLocation = project.getProjectLocations().stream().filter(
                    pl -> pl.isActive() && pl.getLocElement() != null && pl.getLocElement().getId() == element.getId())
                    .collect(Collectors.toList()).get(0);
                  if (locElementManager.getLocElementById(projectLocation.getLocElement().getId()).getLocElementType()
                    .getId().longValue() == 2) {
                    if (project.getCountryFS().stream()
                      .filter(c -> c.getLocElement() != null
                        && c.getLocElement().getId().longValue() == projectLocation.getLocElement().getId().longValue()
                        && c.isSelected())
                      .collect(Collectors.toList()).isEmpty()) {
                      projectLocationManager.deleteProjectLocation(projectLocation.getId());
                    }
                  } else {
                    projectLocationManager.deleteProjectLocation(projectLocation.getId());
                  }
                }
              } else {
                ProjectLocation projectLocation = project.getProjectLocations().stream()
                  .filter(
                    pl -> pl.isActive() && pl.getLocElement() != null && pl.getLocElement().getId() == element.getId())
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

      boolean isProjectGlobal = project.isLocationGlobal();
      boolean isProjectRegional = project.getLocationRegional();
      this.projectLocationPreviousData();

      this.projectLocationNewData();
      this.saveRegions();
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_LOCATIONS_RELATION);
      project = projectManager.getProjectById(projectID);
      project.setActiveSince(new Date());
      project.setModificationJustification(this.getJustification());
      project.setModifiedBy(this.getCurrentUser());
      project.setLocationGlobal(isProjectGlobal);
      project.setLocationRegional(isProjectRegional);
      projectManager.saveProject(project, this.getActionName(), relationsName);
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }
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

    locGeopositionManager.saveLocGeoposition(geoposition);

    if (geoposition.getId() != null) {
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
      element.setLocGeoposition(geoposition);
      element.setIsSiteIntegration(false);

      element = locElementManager.saveLocElement(element);

      ProjectLocation projectLocation = new ProjectLocation();
      projectLocation.setProject(project);
      projectLocation.setLocElement(element);
      projectLocation.setActive(true);
      projectLocation.setActiveSince(new Date());
      projectLocation.setCreatedBy(this.getCurrentUser());
      projectLocation.setModificationJustification("");
      projectLocation.setModifiedBy(this.getCurrentUser());

      projectLocationManager.saveProjectLocation(projectLocation);
    }

  }

  public void saveRegions() {

    Project projectDB = projectManager.getProjectById(projectID);

    if (project.getProjectRegions() == null) {
      project.setProjectRegions(new ArrayList<>());
    }


    List<ProjectLocation> regions = new ArrayList<>(projectDB.getProjectLocations().stream()
      .filter(fl -> fl.isActive() && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId() == 1)
      .collect(Collectors.toList()));
    regions.addAll(projectDB.getProjectLocations().stream()
      .filter(fl -> fl.isActive() && fl.getLocElement() == null && fl.getLocElementType() != null)
      .collect(Collectors.toList()));
    if (regions != null && regions.size() > 0) {

      if (project.getLocationRegional()) {
        for (ProjectLocation projectLocation : regions) {

          if (projectLocation != null) {
            if (!project.getProjectRegions().contains(projectLocation)) {
              if (projectLocation.getLocElementType() != null && projectLocation.getLocElementType().getId() != null) {
                if (project.getRegionFS().stream()
                  .filter(c -> c.getLocElementType() != null && c.getLocElementType().getId()
                    .longValue() == projectLocation.getLocElementType().getId().longValue() && c.isSelected())
                  .collect(Collectors.toList()).isEmpty()) {
                  projectLocationManager.deleteProjectLocation(projectLocation.getId());
                }
              }

            }
          }


        }
      } else {
        for (ProjectLocation projectLocation : regions) {
          projectLocationManager.deleteProjectLocation(projectLocation.getId());
        }
      }
    }

    regions = new ArrayList<>(projectDB
      .getProjectLocations().stream().filter(fl -> fl.isActive() && fl.getLocElementType() == null
        && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId().longValue() == 1)
      .collect(Collectors.toList()));

    if (regions != null && regions.size() > 0) {

      if (project.getLocationRegional()) {
        for (ProjectLocation projectLocation : regions) {
          if (projectLocation != null) {
            if (!project.getProjectRegions().contains(projectLocation)) {
              if (projectLocation.getLocElement() != null && projectLocation.getLocElement().getId() != null) {
                if (project.getRegionFS().stream()
                  .filter(c -> c.getLocElement() != null
                    && c.getLocElement().getId().longValue() == projectLocation.getLocElement().getId().longValue()
                    && c.isSelected())
                  .collect(Collectors.toList()).isEmpty()) {
                  projectLocationManager.deleteProjectLocation(projectLocation.getId());
                }
              }

            }
          }

        }
      } else {
        for (ProjectLocation projectLocation : regions) {
          projectLocationManager.deleteProjectLocation(projectLocation.getId());
        }
      }
    }

    for (ProjectLocation projectLocation : project.getProjectRegions()) {


      if (projectLocation.getId() == null || projectLocation.getId() == -1) {

        ProjectLocation projectLocationSave = new ProjectLocation();
        projectLocationSave.setActive(true);
        projectLocationSave.setActiveSince(new Date());
        projectLocationSave.setCreatedBy(this.getCurrentUser());
        projectLocationSave.setModifiedBy(this.getCurrentUser());
        projectLocationSave.setModificationJustification("");
        projectLocationSave.setProject(project);

        if (!projectLocation.isScope()) {
          LocElement locElement = locElementManager.getLocElementById(projectLocation.getLocElement().getId());

          projectLocationSave.setLocElement(locElement);
        } else {
          long elementId = projectLocation.getLocElement().getId();
          LocElementType elementType = locElementTypeManager.getLocElementTypeById(elementId);

          projectLocationSave.setLocElementType(elementType);
        }

        projectLocationManager.saveProjectLocation(projectLocationSave);
      }
    }

    projectDB = projectManager.getProjectById(projectID);
    regions = new ArrayList<>(projectDB.getProjectLocations().stream()
      .filter(fl -> fl.isActive() && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId() == 1)
      .collect(Collectors.toList()));
    regions.addAll(projectDB.getProjectLocations().stream()
      .filter(fl -> fl.isActive() && fl.getLocElement() == null && fl.getLocElementType() != null)
      .collect(Collectors.toList()));

    List<LocElement> regionsSaved = new ArrayList<>();
    List<LocElementType> regionsCustomSaved = new ArrayList<>();

    for (ProjectLocation projectLocation : regions) {
      if (projectLocation.getLocElement() == null) {
        regionsCustomSaved.add(projectLocation.getLocElementType());
      } else {
        regionsSaved.add(projectLocation.getLocElement());
      }
    }

    if (project.getRegionFS() == null) {
      project.setRegionFS(new ArrayList<>());
    }

    for (CountryFundingSources countryFundingSources : project.getRegionFS()) {

      ProjectLocation projectLocationSave = new ProjectLocation();
      projectLocationSave.setActive(true);
      projectLocationSave.setActiveSince(new Date());
      projectLocationSave.setCreatedBy(this.getCurrentUser());
      projectLocationSave.setModifiedBy(this.getCurrentUser());
      projectLocationSave.setModificationJustification("");
      projectLocationSave.setProject(project);

      if (countryFundingSources.getLocElement() == null) {
        if (!regionsCustomSaved.contains(countryFundingSources.getLocElementType())
          && countryFundingSources.isSelected()) {

          projectLocationSave.setLocElementType(countryFundingSources.getLocElementType());
          projectLocationManager.saveProjectLocation(projectLocationSave);
        }


      } else {
        if (!regionsSaved.contains(countryFundingSources.getLocElement()) && countryFundingSources.isSelected()) {

          projectLocationSave.setLocElement(countryFundingSources.getLocElement());
          projectLocationManager.saveProjectLocation(projectLocationSave);
        }
      }
    }
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

  public void setRegion(boolean region) {
    this.region = region;
  }


  public void setRegionLists(List<LocElement> regionLists) {
    this.regionLists = regionLists;
  }

  public void setScopeData(List<ScopeData> scopeData) {
    this.scopeData = scopeData;
  }

  public void setScopeRegionLists(List<LocElementType> scopeRegionLists) {
    this.scopeRegionLists = scopeRegionLists;
  }

  public void setScopeRegions(List<LocElementType> scopeRegions) {
    this.scopeRegions = scopeRegions;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      locationValidator.validate(this, project, true);
    }
  }
}
