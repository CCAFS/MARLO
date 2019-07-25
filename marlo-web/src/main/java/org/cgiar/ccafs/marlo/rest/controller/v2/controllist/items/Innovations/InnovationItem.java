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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Innovations;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndContributionOfCrpManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.InnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInnovationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.InnovationMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class InnovationItem<T> {

  // Managers and mappers
  private ProjectInnovationManager projectInnovationManager;
  private InnovationMapper innovationMapper;
  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private RepIndStageInnovationManager repIndStageInnovationManager;
  private InstitutionManager institutionManager;
  private RepIndInnovationTypeManager repIndInnovationTypeManager;
  private RepIndContributionOfCrpManager repIndContributionOfCrpManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private LocElementManager locElementManager;
  private ProjectManager projectManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;
  private ProjectInnovationOrganizationManager projectInnovationOrganizationManager;
  private ProjectInnovationCountryManager projectInnovationCountryManager;
  private ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager;
  private ProjectInnovationRegionManager projectInnovationRegionManager;
  private ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager;

  // Variables
  private List<FieldErrorDTO> fieldErrors;
  private ProjectInnovation projectInnovation;
  private long innovationID;


  @Inject
  public InnovationItem(org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager projectInnovationManager,
    InnovationMapper innovationMapper, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    RepIndStageInnovationManager repIndStageInnovationManager, InstitutionManager institutionManager,
    RepIndInnovationTypeManager repIndInnovationTypeManager,
    RepIndContributionOfCrpManager repIndContributionOfCrpManager,
    RepIndOrganizationTypeManager repIndOrganizationTypeManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, LocElementManager locElementManager,
    ProjectManager projectManager, ProjectInnovationInfoManager projectInnovationInfoManager,
    ProjectInnovationCrpManager projectInnovationCrpManager,
    ProjectInnovationOrganizationManager projectInnovationOrganizationManager,
    ProjectInnovationCountryManager projectInnovationCountryManager,
    ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager,
    ProjectInnovationRegionManager projectInnovationRegionManager,
    ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager) {
    this.projectInnovationManager = projectInnovationManager;
    this.innovationMapper = innovationMapper;
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.repIndStageInnovationManager = repIndStageInnovationManager;
    this.institutionManager = institutionManager;
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
    this.repIndContributionOfCrpManager = repIndContributionOfCrpManager;
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
    this.projectManager = projectManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.projectInnovationCrpManager = projectInnovationCrpManager;
    this.projectInnovationOrganizationManager = projectInnovationOrganizationManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.projectInnovationContributingOrganizationManager = projectInnovationContributingOrganizationManager;
    this.projectInnovationRegionManager = projectInnovationRegionManager;
    this.projectInnovationGeographicScopeManager = projectInnovationGeographicScopeManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.locElementManager = locElementManager;

  }


  /**
   * Create a new Innovation
   * 
   * @param newInnovationDTO all innovation data
   * @param CGIAR entity acronym who is requesting
   * @param year of reporting
   * @param Logged user on system
   * @return innovation id created
   */
  public long createInnovation(NewInnovationDTO newInnovationDTO, String entityAcronym, Integer year, User user) {

    // TODO: Add the save to history
    // TODO: Include all data validations
    // TODO: return an innovationDTO

    this.projectInnovation = new ProjectInnovation();
    ProjectInnovationInfo projectInnovationInfo = new ProjectInnovationInfo();
    this.fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      this.fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == year && c.getName().equalsIgnoreCase("AR")).findFirst().get();

    if (phase == null) {
      this.fieldErrors.add(new FieldErrorDTO("createInnovation", "phase", year + " is an invalid year"));
    }


    RepIndStageInnovation RepIndStageInnovation =
      this.repIndStageInnovationManager.getRepIndStageInnovationById(newInnovationDTO.getStageOfInnovation());
    if (RepIndStageInnovation == null) {
      this.fieldErrors.add(new FieldErrorDTO("createInnovation", "Stage of Innovation",
        newInnovationDTO.getStageOfInnovation() + " is an invalid stage of innovation code"));
    }

    Institution leadInstitution = this.institutionManager.getInstitutionById(newInnovationDTO.getLeadOrganization());
    if (leadInstitution == null) {
      this.fieldErrors.add(new FieldErrorDTO("createInnovation", "Lead institution",
        newInnovationDTO.getLeadOrganization() + " is an invalid institution id"));
    }

    RepIndInnovationType repIndInnovationType =
      this.repIndInnovationTypeManager.getRepIndInnovationTypeById(newInnovationDTO.getInnovationType());
    if (repIndInnovationType == null) {
      this.fieldErrors.add(new FieldErrorDTO("createInnovation", "Innovation Type",
        newInnovationDTO.getInnovationType() + " is an invalid innovation type code"));
    }

    // RepIndContributionOfCrp repIndContributionOfCrp =
    // this.repIndContributionOfCrpManager.getRepIndContributionOfCrpById(newInnovationDTO.getContributionOfCrp());
    // if (repIndContributionOfCrp == null) {
    // this.fieldErrors.add(new FieldErrorDTO("createInnovation", "Contribution Of Crp",
    // newInnovationDTO.getContributionOfCrp() + " is an invalid Contribution of CRP code"));
    // }


    this.projectInnovation.setProject(this.projectManager.getProjectById(57));

    // SAVE innovation info
    this.projectInnovation = this.projectInnovationManager.saveProjectInnovation(this.projectInnovation);
    projectInnovationInfo.setProjectInnovation(this.projectInnovation);
    projectInnovationInfo.setPhase(phase);
    projectInnovationInfo.setYear(year.longValue());
    projectInnovationInfo.setTitle(newInnovationDTO.getTitle());
    projectInnovationInfo.setNarrative(newInnovationDTO.getNarrative());
    projectInnovationInfo.setDescriptionStage(newInnovationDTO.getDescriptionStage());
    projectInnovationInfo.setRepIndStageInnovation(RepIndStageInnovation);
    projectInnovationInfo.setLeadOrganization(leadInstitution);
    projectInnovationInfo.setRepIndInnovationType(repIndInnovationType);
    // projectInnovationInfo.setRepIndContributionOfCrp(repIndContributionOfCrp);
    this.projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);

    this.innovationID = this.projectInnovation.getId();
    // SAVE innovation CRP
    if (this.innovationID > 0) {
      ProjectInnovationCrp projectInnovationCrp = new ProjectInnovationCrp();
      projectInnovationCrp.setGlobalUnit(globalUnitEntity);
      projectInnovationCrp.setPhase(phase);
      projectInnovationCrp.setProjectInnovation(this.projectInnovation);
      this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);
    }

    // save all organization types
    if (newInnovationDTO.getNextUserOrganizationTypes() != null
      && newInnovationDTO.getNextUserOrganizationTypes().size() > 0) {
      for (Long id : newInnovationDTO.getNextUserOrganizationTypes()) {
        ProjectInnovationOrganization projectInnovationOrganization = new ProjectInnovationOrganization();
        RepIndOrganizationType repIndOrganizationType =
          this.repIndOrganizationTypeManager.getRepIndOrganizationTypeById(id);
        if (repIndOrganizationType == null) {
          this.fieldErrors.add(new FieldErrorDTO("createInnovation", "NextUserOrganizationType",
            id + " is an invalid institution Next User Organization Type"));
        } else {
          projectInnovationOrganization.setProjectInnovation(this.projectInnovation);
          projectInnovationOrganization.setRepIndOrganizationType(repIndOrganizationType);
          projectInnovationOrganization.setPhase(phase);
          this.projectInnovationOrganizationManager.saveProjectInnovationOrganization(projectInnovationOrganization);
          // This is to add innovationOrganizationSave to generate correct auditlog.
          this.projectInnovation.getProjectInnovationOrganizations().add(projectInnovationOrganization);
        }
      }
    }

    // save all Next user institutions
    if (newInnovationDTO.getContributingInstitutions() != null
      && newInnovationDTO.getContributingInstitutions().size() > 0) {
      for (Long id : newInnovationDTO.getContributingInstitutions()) {
        Institution addinstitution = this.institutionManager.getInstitutionById(id);
        if (addinstitution == null) {
          this.fieldErrors.add(
            new FieldErrorDTO("createInnovation", "ContributingInstitution", id + " is an invalid institution id"));
        } else {
          ProjectInnovationContributingOrganization newContributingOrganization =
            new ProjectInnovationContributingOrganization();
          newContributingOrganization.setProjectInnovation(this.projectInnovation);
          newContributingOrganization.setPhase(phase);
          newContributingOrganization.setInstitution(addinstitution);
          this.projectInnovationContributingOrganizationManager
            .saveProjectInnovationContributingOrganization(newContributingOrganization);
          // This is to add innovationOrganizationSave to generate correct auditlog.
          this.projectInnovation.getProjectInnovationContributingOrganization().add(newContributingOrganization);
        }
      }
    }

    // save CRPs
    if (newInnovationDTO.getContributingCGIAREntities() != null
      && newInnovationDTO.getContributingCGIAREntities().size() > 0) {
      for (String acronym : newInnovationDTO.getContributingCGIAREntities()) {
        GlobalUnit crp = this.globalUnitManager.findGlobalUnitByAcronym(acronym);

        if (crp == null) {
          this.fieldErrors.add(new FieldErrorDTO("createInnovation", "ContributingCGIAREntities",
            acronym + " is an invalid CGIAR entity acronym"));
        } else {
          ProjectInnovationCrp projectInnovationCrp = new ProjectInnovationCrp();
          projectInnovationCrp.setProjectInnovation(this.projectInnovation);
          projectInnovationCrp.setPhase(phase);
          projectInnovationCrp.setGlobalUnit(crp);
          this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);

          // This is to add innovationOrganizationSave to generate correct auditlog.
          this.projectInnovation.getProjectInnovationCrps().add(projectInnovationCrp);
        }
      }
    }

    // save Geographical Scopes
    if (newInnovationDTO.getGeographicScopes() != null && newInnovationDTO.getGeographicScopes().size() > 0) {
      for (Long id : newInnovationDTO.getGeographicScopes()) {
        RepIndGeographicScope geoScope = this.repIndGeographicScopeManager.getRepIndGeographicScopeById(id);
        if (geoScope == null) {
          this.fieldErrors.add(
            new FieldErrorDTO("createInnovation", "GeographicScopes", id + " is an invalid Geographic Scope code"));
        } else {
          ProjectInnovationGeographicScope geographicScope = new ProjectInnovationGeographicScope();
          geographicScope.setProjectInnovation(this.projectInnovation);
          geographicScope.setPhase(phase);
          geographicScope.setRepIndGeographicScope(geoScope);
          this.projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(geographicScope);

          // This is to add innovationOrganizationSave to generate correct auditlog.
          this.projectInnovation.getProjectInnovationGeographicScopes().add(geographicScope);
        }
      }
    }

    // save Regions
    if (newInnovationDTO.getRegions() != null && newInnovationDTO.getRegions().size() > 0) {
      for (Long id : newInnovationDTO.getRegions()) {
        LocElement region = this.locElementManager.getLocElementByNumericISOCode(id);
        if (region == null) {
          this.fieldErrors.add(new FieldErrorDTO("createInnovation", "Regions", id + " is an invalid Region Code"));

        } else if (region.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
          this.fieldErrors.add(new FieldErrorDTO("createInnovation", "Regions", id + " is not a Region code"));
        } else {
          ProjectInnovationRegion projectInnovationRegion = new ProjectInnovationRegion();
          projectInnovationRegion.setProjectInnovation(this.projectInnovation);
          projectInnovationRegion.setPhase(phase);
          projectInnovationRegion.setLocElement(region);
          this.projectInnovationRegionManager.saveProjectInnovationRegion(projectInnovationRegion);

          // This is to add innovationOrganizationSave to generate correct auditlog.
          this.projectInnovation.getProjectInnovationRegions().add(projectInnovationRegion);
        }
      }
    }

    // save Countries
    if (newInnovationDTO.getCountries() != null && newInnovationDTO.getCountries().size() > 0) {
      for (String iso : newInnovationDTO.getCountries()) {
        LocElement country = this.locElementManager.getLocElementByISOCode(iso);
        if (country == null) {
          this.fieldErrors
            .add(new FieldErrorDTO("createInnovation", "Countries", iso + " is an invalid country ISO Code"));

        } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
          this.fieldErrors.add(new FieldErrorDTO("createInnovation", "Countries", iso + " is not a Country ISO code"));
        } else {
          ProjectInnovationCountry projectInnovationCountry = new ProjectInnovationCountry();
          projectInnovationCountry.setProjectInnovation(this.projectInnovation);
          projectInnovationCountry.setPhase(phase);
          projectInnovationCountry.setLocElement(country);
          this.projectInnovationCountryManager.saveProjectInnovationCountry(projectInnovationCountry);

          // This is to add innovationOrganizationSave to generate correct auditlog.
          this.projectInnovation.getProjectInnovationCountries().add(projectInnovationCountry);
        }
      }
    }

    // Validate all fields
    if (!this.fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        this.fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return this.innovationID;
  }


  /**
   * Find an Innovation by Id and year
   * 
   * @param id
   * @param year
   * @return a InnovationDTO with the innovation Item
   */
  // public ResponseEntity<InnovationDTO> findBudgetTypeById(Long id,String CGIARentityAcronym, Integer repoYear) {
  public ResponseEntity<InnovationDTO> findInnovationById(Long id, String CGIARentityAcronym, Integer repoYear,
    User user) {
    // TODO: Include all security validations

    this.fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    ProjectInnovation innovation = this.projectInnovationManager.getProjectInnovationById(id);
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase("AR")).findFirst().get();

    Set<CrpUser> lstUser = user.getCrpUsers();

    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym))) {
      this.fieldErrors.add(new FieldErrorDTO("findInnovation", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (globalUnitEntity == null) {
      this.fieldErrors.add(new FieldErrorDTO("findInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is not an invalid CGIAR entity acronym"));
    }
    if (phase == null) {
      this.fieldErrors.add(new FieldErrorDTO("findInnovation", "phase", repoYear + " is an invalid year"));
    }

    if (innovation == null || innovation.getProjectInnovationInfo(phase) == null) {
      this.fieldErrors
        .add(new FieldErrorDTO("findInnovation", "InnovationId", id + " is an invalid id of an innovation"));
    }

    innovation.setAllbyPhase(phase);

    // Validate all fields
    if (!this.fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        this.fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(innovation).map(this.innovationMapper::projectInnovationToInnovationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
