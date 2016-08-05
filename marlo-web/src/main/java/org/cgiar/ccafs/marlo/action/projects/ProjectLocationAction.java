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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationType;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.CountryLocationLevel;
import org.cgiar.ccafs.marlo.utils.LocationLevel;

import java.util.ArrayList;
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

  private List<CountryLocationLevel> locationsData;


  private CrpManager crpManager;

  private ProjectManager projectManager;


  private LocElementTypeManager locElementTypeManager;

  private ProjectLocationManager projectLocationManager;


  private LocElementManager locElementManager;

  private CrpProgramManager crpProgramManager;


  private long projectID;

  @Inject
  public ProjectLocationAction(APConfig config, CrpManager crpManager, ProjectManager projectManager,
    LocElementTypeManager locElementTypeManager, CrpProgramManager crpProgramManager,
    LocElementManager locElementManager, ProjectLocationManager projectLocationManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.locElementTypeManager = locElementTypeManager;
    this.crpProgramManager = crpProgramManager;
    this.locElementManager = locElementManager;
    this.projectLocationManager = projectLocationManager;
  }

  public List<CountryLocationLevel> getLocationsData() {
    return locationsData;
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


  /**
  * 
  */
  public void locationLevels() {

    locationsLevels = new ArrayList<>();

    List<CountryLocationLevel> countryLocationLevels = new ArrayList<>();
    LocElementType regionsElementType = locElementTypeManager.getLocElementTypeById(1);

    for (LocElement element : regionsElementType.getLocElements().stream().filter(le -> le.isActive())
      .collect(Collectors.toList())) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(element.getId());
      countryLocationLevel.setName(element.getName());
      countryLocationLevel.setAllElements(new ArrayList<LocElement>(locElementManager
        .findLocElementByParent(element.getId()).stream().filter(le -> le.isActive()).collect(Collectors.toList())));
      countryLocationLevel.setList(true);
      countryLocationLevel.setModelClass(ProjectLocationType.PROJECT_LOCATION_ELEMENT_TYPE.getValue());
      countryLocationLevels.add(countryLocationLevel);
    }
    locationsLevels.add(new LocationLevel("Regions", countryLocationLevels));

    countryLocationLevels = new ArrayList<>();
    List<CrpProgram> regionPrograms = crpProgramManager
      .findAll().stream().filter(cp -> cp.isActive()
        && (cp.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) && cp.getCrp().equals(loggedCrp))
      .collect(Collectors.toList());

    for (CrpProgram crpProgram : regionPrograms) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(crpProgram.getId());
      countryLocationLevel.setName(crpProgram.getName());
      countryLocationLevel.setModelClass(ProjectLocationType.PROJECT_LOCATION_PROGRAM_TYPE.getValue());
      countryLocationLevel.setAllElements(new ArrayList<LocElement>());
      for (CrpProgramCountry programCountry : crpProgram.getCrpProgramCountries()) {
        countryLocationLevel.getAllElements().add(programCountry.getLocElement());
      }
      countryLocationLevel.setList(true);
      countryLocationLevels.add(countryLocationLevel);
    }

    locationsLevels
      .add(new LocationLevel(loggedCrp.getAcronym().toUpperCase() + " Custom Regions", countryLocationLevels));

    countryLocationLevels = new ArrayList<>();
    List<LocElementType> customElementTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() != null && let.getCrp().equals(loggedCrp) && let.getId() != 1)
      .collect(Collectors.toList());

    for (LocElementType locElementType : customElementTypes) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(locElementType.getId());
      countryLocationLevel.setName(locElementType.getName());
      countryLocationLevel.setModelClass(ProjectLocationType.PROJECT_LOCATION_ELEMENT_TYPE.getValue());
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
      countryLocationLevel.setModelClass(ProjectLocationType.PROJECT_LOCATION_ELEMENT_TYPE.getValue());
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
    // Get current CRP
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    project = projectManager.getProjectById(projectID);

    locationsData = new ArrayList<>();

    this.locationLevels();

    List<Map<String, Object>> parentLocations = new ArrayList<>();

    project.setLocations(new ArrayList<ProjectLocation>(
      project.getProjectLocations().stream().filter(p -> p.isActive()).collect(Collectors.toList())));


    if (!project.getLocations().stream().filter(pl -> pl.getCrpProgram() != null && pl.getRegionLocElement() == null)
      .collect(Collectors.toList()).isEmpty()) {
      parentLocations.addAll(projectLocationManager.getParentLocations(project.getId(), "crp_program_region_id"));
    }
    if (!project.getLocations().stream().filter(pl -> pl.getRegionLocElement() != null && pl.getCrpProgram() == null)
      .collect(Collectors.toList()).isEmpty()) {
      parentLocations.addAll(projectLocationManager.getParentLocations(project.getId(), "region_loc_element"));
    }
    if (!project.getLocations().stream().filter(pl -> pl.getCrpProgram() == null && pl.getRegionLocElement() == null)
      .collect(Collectors.toList()).isEmpty()) {
      Map<String, Object> locationParent;

      for (ProjectLocation location : project.getLocations().stream()
        .filter(pl -> pl.getCrpProgram() == null && pl.getRegionLocElement() == null).collect(Collectors.toList())) {
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
      if (map.containsKey("crp_program_region_id")) {
        CrpProgram program =
          crpProgramManager.getCrpProgramById(Long.parseLong(map.get("crp_program_region_id").toString()));
        countryLocationLevel = new CountryLocationLevel();
        countryLocationLevel.setId(program.getId());
        countryLocationLevel.setName(program.getName());
        countryLocationLevel.setList(true);
        countryLocationLevel.setModelClass(ProjectLocationType.PROJECT_LOCATION_PROGRAM_TYPE.getValue());
        countryLocationLevel.setLocElements(new ArrayList<LocElement>());
        countryLocationLevel.setAllElements(new ArrayList<LocElement>());
        for (CrpProgramCountry programCountry : program.getCrpProgramCountries()) {
          countryLocationLevel.getAllElements().add(programCountry.getLocElement());
        }
        for (ProjectLocation projectLocation : project.getLocations().stream()
          .filter(l -> l.isActive() && l.getCrpProgram() != null && l.getCrpProgram().equals(program))
          .collect(Collectors.toList())) {
          countryLocationLevel.getLocElements().add(projectLocation.getLocElement());
        }
        locationsData.add(countryLocationLevel);
      }


      if (map.containsKey("region_loc_element")) {
        Long.parseLong(map.get("region_loc_element").toString());
        LocElement elementType =
          locElementManager.getLocElementById(Long.parseLong(map.get("region_loc_element").toString()));
        countryLocationLevel = new CountryLocationLevel();
        countryLocationLevel.setId(elementType.getId());
        countryLocationLevel.setName(elementType.getName());
        countryLocationLevel.setLocElements(new ArrayList<LocElement>());
        countryLocationLevel.setList(true);
        countryLocationLevel.setModelClass(ProjectLocationType.PROJECT_LOCATION_ELEMENT_TYPE.getValue());
        countryLocationLevel
          .setAllElements(new ArrayList<LocElement>(locElementManager.findLocElementByParent(elementType.getId())
            .stream().filter(le -> le.isActive()).collect(Collectors.toList())));

        for (ProjectLocation projectLocation : project.getLocations().stream()
          .filter(l -> l.isActive() && l.getRegionLocElement() != null && l.getRegionLocElement().equals(elementType))
          .collect(Collectors.toList())) {
          countryLocationLevel.getLocElements().add(projectLocation.getLocElement());
        }
        locationsData.add(countryLocationLevel);
      }

      if (!map.containsKey("crp_program_region_id") && !map.containsKey("region_loc_element")) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
          countryLocationLevel = new CountryLocationLevel();
          countryLocationLevel.setId(Long.parseLong(entry.getValue().toString()));
          countryLocationLevel.setName(entry.getKey());
          countryLocationLevel.setLocElements(new ArrayList<LocElement>());
          countryLocationLevel.setModelClass(ProjectLocationType.PROJECT_LOCATION_ELEMENT_TYPE.getValue());

          countryLocationLevel.setAllElements(new ArrayList<LocElement>(
            locElementTypeManager.getLocElementTypeById(Long.parseLong(entry.getValue().toString())).getLocElements()));

          for (ProjectLocation projectLocation : project.getLocations().stream()
            .filter(l -> l.isActive() && l.getCrpProgram() == null && l.getRegionLocElement() == null)
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

          locationsData.add(countryLocationLevel);
        }
      }
    }
  }


  public void setLocationsData(List<CountryLocationLevel> locationsData) {
    this.locationsData = locationsData;
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

}
