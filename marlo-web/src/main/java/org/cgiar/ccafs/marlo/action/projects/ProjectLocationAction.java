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
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.CountryLocationLevel;
import org.cgiar.ccafs.marlo.utils.LocationLevel;

import java.util.ArrayList;
import java.util.List;
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

  private List<LocationLevel> locationsData;


  private CrpManager crpManager;

  private ProjectManager projectManager;

  private LocElementTypeManager locElementTypeManager;

  private ProjectLocationManager projectLocationManager;

  private LocElementManager locElementManager;

  private CrpProgramManager crpProgramManager;

  private Long projectID;


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
  }

  public List<LocationLevel> getLocationsData() {
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


  public Long getProjectID() {
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
      countryLocationLevel.setLocElements(new ArrayList<LocElement>(locElementManager
        .findLocElementByParent(element.getId()).stream().filter(le -> le.isActive()).collect(Collectors.toList())));
      countryLocationLevel.setList(true);
      countryLocationLevels.add(countryLocationLevel);
    }
    locationsLevels.add(new LocationLevel("Regions", countryLocationLevels, regionsElementType.getClass()));

    countryLocationLevels = new ArrayList<>();
    List<CrpProgram> regionPrograms = crpProgramManager
      .findAll().stream().filter(cp -> cp.isActive()
        && (cp.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) && cp.getCrp().equals(loggedCrp))
      .collect(Collectors.toList());

    for (CrpProgram crpProgram : regionPrograms) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(crpProgram.getId());
      countryLocationLevel.setName(crpProgram.getName());
      countryLocationLevel.setLocElements(new ArrayList<LocElement>());
      for (CrpProgramCountry programCountry : crpProgram.getCrpProgramCountries()) {
        countryLocationLevel.getLocElements().add(programCountry.getLocElement());
      }
      countryLocationLevel.setList(true);
      countryLocationLevels.add(countryLocationLevel);
    }


    locationsLevels.add(new LocationLevel(loggedCrp.getAcronym() + " Custom Regions", countryLocationLevels,
      regionPrograms.get(0).getClass()));

    countryLocationLevels = new ArrayList<>();
    List<LocElementType> customElementTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() != null && let.getCrp().equals(loggedCrp) && let.getId() != 1)
      .collect(Collectors.toList());

    for (LocElementType locElementType : customElementTypes) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(locElementType.getId());
      countryLocationLevel.setName(locElementType.getName());
      countryLocationLevel.setLocElements(new ArrayList<LocElement>(locElementType.getLocElements()));
      countryLocationLevel.setList(true);
      countryLocationLevels.add(countryLocationLevel);
    }


    locationsLevels.add(new LocationLevel(loggedCrp.getAcronym() + " Custom Locations", countryLocationLevels,
      customElementTypes.get(0).getClass()));

    countryLocationLevels = new ArrayList<>();
    List<LocElementType> elementTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() == null && let.getId() != 1).collect(Collectors.toList());

    for (LocElementType locElementType : elementTypes) {
      CountryLocationLevel countryLocationLevel = new CountryLocationLevel();
      countryLocationLevel.setId(locElementType.getId());
      countryLocationLevel.setName(locElementType.getName());
      countryLocationLevel.setLocElements(new ArrayList<LocElement>(locElementType.getLocElements()));
      if (locElementType.getId() != 2) {
        countryLocationLevel.setList(false);
      } else {
        countryLocationLevel.setList(true);
      }

      countryLocationLevels.add(countryLocationLevel);
    }

    locationsLevels.add(new LocationLevel("Other Locations", countryLocationLevels, elementTypes.get(0).getClass()));

  }

  @Override
  public void prepare() throws Exception {
    // Get current CRP
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    project = projectManager.getProjectById(projectID);

    this.locationLevels();

    project.setLocations(new ArrayList<ProjectLocation>(
      project.getProjectLocations().stream().filter(p -> p.isActive()).collect(Collectors.toList())));


  }

  public void setLocationsData(List<LocationLevel> locationsData) {
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

  public void setProjectID(Long projectID) {
    this.projectID = projectID;
  }
}
