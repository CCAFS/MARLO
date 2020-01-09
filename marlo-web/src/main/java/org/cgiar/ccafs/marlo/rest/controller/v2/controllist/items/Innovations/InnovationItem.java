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
import org.cgiar.ccafs.marlo.data.model.Project;
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
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.OrganizationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
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
  // private List<FieldErrorDTO> fieldErrors;
  // private ProjectInnovation projectInnovation;
  // private long innovationID;

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
  public Long createInnovation(NewInnovationDTO newInnovationDTO, String entityAcronym, User user) {

    // TODO: Add the save to history
    // TODO: Include all data validations
    // TODO: return an innovationDTO
    Long innovationID = null;
    ProjectInnovation projectInnovation = new ProjectInnovation();
    ProjectInnovationInfo projectInnovationInfo = new ProjectInnovationInfo();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newInnovationDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newInnovationDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "phase",
        new NewInnovationDTO().getPhase().getYear() + " is an invalid year"));
    }

    RepIndStageInnovation RepIndStageInnovation =
      this.repIndStageInnovationManager.getRepIndStageInnovationById(newInnovationDTO.getStageOfInnovation().getCode());
    if (RepIndStageInnovation == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "Stage of Innovation",
        newInnovationDTO.getStageOfInnovation() + " is an invalid stage of innovation code"));
    }

    Institution leadInstitution = null;
    if (newInnovationDTO.getLeadOrganization() != null) {
      leadInstitution = this.institutionManager.getInstitutionById(newInnovationDTO.getLeadOrganization().getCode());
      if (leadInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createInnovation", "Lead institution",
          newInnovationDTO.getLeadOrganization() + " is an invalid institution id"));
      }
    }


    RepIndInnovationType repIndInnovationType =
      this.repIndInnovationTypeManager.getRepIndInnovationTypeById(newInnovationDTO.getInnovationType().getCode());
    if (repIndInnovationType == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "Innovation Type",
        newInnovationDTO.getInnovationType() + " is an invalid innovation type code"));
    }
    if (newInnovationDTO.getProject() == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "Project ID", "Innovation need an invalid project id"));
    } else {
      Project project = this.projectManager.getProjectById(newInnovationDTO.getProject().getId());

      // TODO: Include the validation that the project should be on same
      // CRP/PTF
      if (project == null) {
        fieldErrors.add(new FieldErrorDTO("createInnovation", "Project id",
          newInnovationDTO.getProject() + " is an invalid project id"));
      }

      projectInnovation.setProject(project);
      // SAVE innovation info
      projectInnovation = this.projectInnovationManager.saveProjectInnovation(projectInnovation);
      projectInnovationInfo.setProjectInnovation(projectInnovation);
      projectInnovationInfo.setPhase(phase);
      projectInnovationInfo.setYear((long) newInnovationDTO.getPhase().getYear());
      projectInnovationInfo.setTitle(newInnovationDTO.getTitle());
      projectInnovationInfo.setNarrative(newInnovationDTO.getNarrative());
      projectInnovationInfo.setDescriptionStage(newInnovationDTO.getDescriptionStage());
      projectInnovationInfo.setRepIndStageInnovation(RepIndStageInnovation);
      projectInnovationInfo.setLeadOrganization(leadInstitution);
      projectInnovationInfo.setOtherInnovationType(newInnovationDTO.getOtherInnovationType());
      projectInnovationInfo.setRepIndInnovationType(repIndInnovationType);
      projectInnovationInfo.setEvidenceLink(newInnovationDTO.getEvidenceLink());
      projectInnovationInfo.setClearLead(newInnovationDTO.getEquitativeEffort());
      this.projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);

      innovationID = projectInnovation.getId();
      // SAVE innovation CRP
      if (innovationID > 0) {
        ProjectInnovationCrp projectInnovationCrp = new ProjectInnovationCrp();
        projectInnovationCrp.setGlobalUnit(globalUnitEntity);
        projectInnovationCrp.setPhase(phase);
        projectInnovationCrp.setProjectInnovation(projectInnovation);
        this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);
      }

      // save all organization types
      if (newInnovationDTO.getNextUserOrganizationTypes() != null
        && newInnovationDTO.getNextUserOrganizationTypes().size() > 0) {
        for (OrganizationTypeDTO id : newInnovationDTO.getNextUserOrganizationTypes()) {
          ProjectInnovationOrganization projectInnovationOrganization = new ProjectInnovationOrganization();
          RepIndOrganizationType repIndOrganizationType =
            this.repIndOrganizationTypeManager.getRepIndOrganizationTypeById(id.getCode());
          if (repIndOrganizationType == null) {
            fieldErrors.add(new FieldErrorDTO("createInnovation", "NextUserOrganizationType",
              id + " is an invalid institution Next User Organization Type"));
          } else {
            projectInnovationOrganization.setProjectInnovation(projectInnovation);
            projectInnovationOrganization.setRepIndOrganizationType(repIndOrganizationType);
            projectInnovationOrganization.setPhase(phase);
            this.projectInnovationOrganizationManager.saveProjectInnovationOrganization(projectInnovationOrganization);
            // This is to add innovationOrganizationSave to generate
            // correct auditlog.
            projectInnovation.getProjectInnovationOrganizations().add(projectInnovationOrganization);
          }
        }
      }

      // save all Next user institutions
      if (newInnovationDTO.getContributingInstitutions() != null
        && newInnovationDTO.getContributingInstitutions().size() > 0) {
        for (InstitutionDTO id : newInnovationDTO.getContributingInstitutions()) {
          Institution addinstitution = this.institutionManager.getInstitutionById(id.getCode());
          if (addinstitution == null) {
            fieldErrors.add(
              new FieldErrorDTO("createInnovation", "ContributingInstitution", id + " is an invalid institution id"));
          } else {
            ProjectInnovationContributingOrganization newContributingOrganization =
              new ProjectInnovationContributingOrganization();
            newContributingOrganization.setProjectInnovation(projectInnovation);
            newContributingOrganization.setPhase(phase);
            newContributingOrganization.setInstitution(addinstitution);
            this.projectInnovationContributingOrganizationManager
              .saveProjectInnovationContributingOrganization(newContributingOrganization);
            // This is to add innovationOrganizationSave to generate
            // correct auditlog.
            projectInnovation.getProjectInnovationContributingOrganization().add(newContributingOrganization);
          }
        }
      }

      // save CRPs
      if (newInnovationDTO.getContributingCGIAREntities() != null
        && newInnovationDTO.getContributingCGIAREntities().size() > 0) {
        for (CGIAREntityDTO globalUnitCode : newInnovationDTO.getContributingCGIAREntities()) {
          GlobalUnit crp = this.globalUnitManager.findGlobalUnitBySMOCode(globalUnitCode.getCode());

          if (crp == null) {
            fieldErrors.add(new FieldErrorDTO("createInnovation", "ContributingCGIAREntities",
              globalUnitCode + " is an invalid CGIAR entity acronym"));
          } else {
            ProjectInnovationCrp projectInnovationCrp = new ProjectInnovationCrp();
            projectInnovationCrp.setProjectInnovation(projectInnovation);
            projectInnovationCrp.setPhase(phase);
            projectInnovationCrp.setGlobalUnit(crp);
            this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);

            // This is to add innovationOrganizationSave to generate
            // correct auditlog.
            projectInnovation.getProjectInnovationCrps().add(projectInnovationCrp);
          }
        }
      }

      // save Geographical Scopes
      if (newInnovationDTO.getGeographicScopes() != null && newInnovationDTO.getGeographicScopes().size() > 0) {
        for (GeographicScopeDTO id : newInnovationDTO.getGeographicScopes()) {
          RepIndGeographicScope geoScope = this.repIndGeographicScopeManager.getRepIndGeographicScopeById(id.getCode());
          if (geoScope == null) {
            fieldErrors.add(
              new FieldErrorDTO("createInnovation", "GeographicScopes", id + " is an invalid Geographic Scope code"));
          } else {
            ProjectInnovationGeographicScope geographicScope = new ProjectInnovationGeographicScope();
            geographicScope.setProjectInnovation(projectInnovation);
            geographicScope.setPhase(phase);
            geographicScope.setRepIndGeographicScope(geoScope);
            this.projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(geographicScope);

            // This is to add innovationOrganizationSave to generate
            // correct auditlog.
            projectInnovation.getProjectInnovationGeographicScopes().add(geographicScope);
          }
        }
      }

      // save Regions
      if (newInnovationDTO.getRegions() != null && newInnovationDTO.getRegions().size() > 0) {
        for (RegionDTO id : newInnovationDTO.getRegions()) {
          LocElement region = this.locElementManager.getLocElementByNumericISOCode(id.getUM49Code());
          if (region == null) {
            fieldErrors
              .add(new FieldErrorDTO("createInnovation", "Regions", id.getUM49Code() + " is an invalid Region Code"));

          } else if (region.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
            fieldErrors.add(new FieldErrorDTO("createInnovation", "Regions", id + " is not a Region code"));
          } else {
            ProjectInnovationRegion projectInnovationRegion = new ProjectInnovationRegion();
            projectInnovationRegion.setProjectInnovation(projectInnovation);
            projectInnovationRegion.setPhase(phase);
            projectInnovationRegion.setLocElement(region);
            this.projectInnovationRegionManager.saveProjectInnovationRegion(projectInnovationRegion);

            // This is to add innovationOrganizationSave to generate
            // correct auditlog.
            projectInnovation.getProjectInnovationRegions().add(projectInnovationRegion);
          }
        }
      }

      // save Countries
      if (newInnovationDTO.getCountries() != null && newInnovationDTO.getCountries().size() > 0) {
        for (CountryDTO iso : newInnovationDTO.getCountries()) {
          LocElement country = this.locElementManager.getLocElementByNumericISOCode(iso.getCode());
          if (country == null) {
            fieldErrors
              .add(new FieldErrorDTO("createInnovation", "Countries", iso + " is an invalid country ISO Code"));

          } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
            fieldErrors.add(new FieldErrorDTO("createInnovation", "Countries", iso + " is not a Country ISO code"));
          } else {
            ProjectInnovationCountry projectInnovationCountry = new ProjectInnovationCountry();
            projectInnovationCountry.setProjectInnovation(projectInnovation);
            projectInnovationCountry.setPhase(phase);
            projectInnovationCountry.setLocElement(country);
            this.projectInnovationCountryManager.saveProjectInnovationCountry(projectInnovationCountry);

            // This is to add innovationOrganizationSave to generate
            // correct auditlog.
            projectInnovation.getProjectInnovationCountries().add(projectInnovationCountry);
          }
        }
      }
    }


    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return innovationID;
  }

  /**
   * Delete an Innovation by Id,Phase and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a InnovationDTO with the innovation Item
   */
  public ResponseEntity<InnovationDTO> deleteInnovationById(Long id, String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }
    ProjectInnovation innovation = this.projectInnovationManager.getProjectInnovationById(id);
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "phase",
        new NewInnovationDTO().getPhase().getYear() + " is an invalid year"));
    }

    if (innovation != null) {
      // delete
      if (innovation.getProjectInnovationInfo(phase) != null) {
        innovation.setCountries(
          this.projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId()));
        innovation.setRegions(innovation.getProjectInnovationRegions().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setGeographicScopes(innovation.getProjectInnovationGeographicScopes().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setContributingOrganizations(innovation.getProjectInnovationContributingOrganization().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setCrps(innovation.getProjectInnovationCrps().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setOrganizations(innovation.getProjectInnovationOrganizations().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
      }
      projectInnovationManager.deleteProjectInnovation(id);

    } else {
      fieldErrors.add(new FieldErrorDTO("deleteInnovation", "Innovation", id + " is an invalid innovation Code"));

    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(innovation).map(this.innovationMapper::projectInnovationToInnovationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

  }

  public List<InnovationDTO> findAllInnovationsByGlobalUnit(String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<InnovationDTO> innovationList = new ArrayList<InnovationDTO>();
    List<ProjectInnovation> projectInnovationList = new ArrayList<ProjectInnovation>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "phase",
        new NewInnovationDTO().getPhase().getYear() + " is an invalid year"));
    }


    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    } else {

      List<ProjectInnovationInfo> projectInnovationInfoList = phase.getProjectInnovationInfos().stream()
        .filter(c -> c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
      for (ProjectInnovationInfo projectInnovationInfo : projectInnovationInfoList) {
        ProjectInnovation innovation =
          this.projectInnovationManager.getProjectInnovationById(projectInnovationInfo.getProjectInnovation().getId());
        innovation.setProjectInnovationInfo(projectInnovationInfo);
        innovation.setCountries(
          this.projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId()));
        innovation.setRegions(innovation.getProjectInnovationRegions().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setGeographicScopes(innovation.getProjectInnovationGeographicScopes().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setContributingOrganizations(innovation.getProjectInnovationContributingOrganization().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setCrps(innovation.getProjectInnovationCrps().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setOrganizations(innovation.getProjectInnovationOrganizations().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        projectInnovationList.add(innovation);
      }

    }
    innovationList = projectInnovationList.stream()
      .map(innovations -> this.innovationMapper.projectInnovationToInnovationDTO(innovations))
      .collect(Collectors.toList());
    return innovationList;

  }

  /**
   * Find an Innovation by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a InnovationDTO with the innovation Item
   */

  public ResponseEntity<InnovationDTO> findInnovationById(Long id, String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    // TODO: Include all security validations

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    ProjectInnovation innovation = this.projectInnovationManager.getProjectInnovationById(id);
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    Set<CrpUser> lstUser = user.getCrpUsers();

    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("findInnovation", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is not an invalid CGIAR entity acronym"));
    }
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findInnovation", "phase", repoYear + " is an invalid year"));
    }

    if (innovation == null || innovation.getProjectInnovationInfo(phase) == null) {
      fieldErrors.add(new FieldErrorDTO("findInnovation", "InnovationId", id + " is an invalid id of an innovation"));
    }


    // innovation.setAllbyPhase(phase);
    if (innovation.getProjectInnovationInfo(phase) != null) {
      innovation.setCountries(
        this.projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId()));
      innovation.setRegions(innovation.getProjectInnovationRegions().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
      innovation.setGeographicScopes(innovation.getProjectInnovationGeographicScopes().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
      innovation.setContributingOrganizations(innovation.getProjectInnovationContributingOrganization().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
      innovation.setCrps(innovation.getProjectInnovationCrps().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
      innovation.setOrganizations(innovation.getProjectInnovationOrganizations().stream()
        .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(innovation).map(this.innovationMapper::projectInnovationToInnovationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Update an Innovation by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a InnovationDTO with the innovation Item
   */

  public Long putInnovationById(Long idInnovation, NewInnovationDTO newInnovationDTO, String CGIARentityAcronym,
    User user) {
    Long innovationID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == newInnovationDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newInnovationDTO.getPhase().getName()))
      .findFirst().get();
    ProjectInnovation innovation = this.projectInnovationManager.getProjectInnovationById(idInnovation);
    if (fieldErrors.size() == 0) {
      if (innovation != null) {
        innovationID = innovation.getId();

        // update basic data
        RepIndStageInnovation RepIndStageInnovation = this.repIndStageInnovationManager
          .getRepIndStageInnovationById(newInnovationDTO.getStageOfInnovation().getCode());
        if (RepIndStageInnovation == null) {
          fieldErrors.add(new FieldErrorDTO("createInnovation", "Stage of Innovation",
            newInnovationDTO.getStageOfInnovation() + " is an invalid stage of innovation code"));
        }

        Institution leadInstitution =
          this.institutionManager.getInstitutionById(newInnovationDTO.getLeadOrganization().getCode());
        if (leadInstitution == null) {
          fieldErrors.add(new FieldErrorDTO("createInnovation", "Lead institution",
            newInnovationDTO.getLeadOrganization() + " is an invalid institution id"));
        }

        RepIndInnovationType repIndInnovationType =
          this.repIndInnovationTypeManager.getRepIndInnovationTypeById(newInnovationDTO.getInnovationType().getCode());
        if (repIndInnovationType == null) {
          fieldErrors.add(new FieldErrorDTO("createInnovation", "Innovation Type",
            newInnovationDTO.getInnovationType() + " is an invalid innovation type code"));
        }

        Project project = this.projectManager.getProjectById(newInnovationDTO.getProject().getId());

        // CRP/PTF
        if (project == null) {
          fieldErrors.add(new FieldErrorDTO("createInnovation", "Project id",
            newInnovationDTO.getProject() + " is an invalid project id"));
        }
        innovation.setProject(project);
        // SAVE innovation info
        innovation = this.projectInnovationManager.saveProjectInnovation(innovation);
        // update data info
        ProjectInnovationInfo projectInnovationInfo = this.projectInnovationInfoManager
          .getProjectInnovationInfoById(innovation.getProjectInnovationInfo(phase).getId());
        projectInnovationInfo.setProjectInnovation(innovation);
        projectInnovationInfo.setPhase(phase);
        projectInnovationInfo.setYear((long) newInnovationDTO.getPhase().getYear());
        projectInnovationInfo.setTitle(newInnovationDTO.getTitle());
        projectInnovationInfo.setNarrative(newInnovationDTO.getNarrative());
        projectInnovationInfo.setDescriptionStage(newInnovationDTO.getDescriptionStage());
        projectInnovationInfo.setRepIndStageInnovation(RepIndStageInnovation);
        projectInnovationInfo.setLeadOrganization(leadInstitution);
        projectInnovationInfo.setOtherInnovationType(newInnovationDTO.getOtherInnovationType());
        projectInnovationInfo.setRepIndInnovationType(repIndInnovationType);
        projectInnovationInfo.setEvidenceLink(newInnovationDTO.getEvidenceLink());
        projectInnovationInfo.setClearLead(newInnovationDTO.getEquitativeEffort());
        this.projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);

        // let's check Organizations
        if (newInnovationDTO.getNextUserOrganizationTypes() != null
          && newInnovationDTO.getNextUserOrganizationTypes().size() > 0) {

          List<ProjectInnovationOrganization> projectInnovationOrganizationList =
            innovation.getProjectInnovationOrganizations().stream()
              .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());

          List<ProjectInnovationOrganization> existingProjectInnovationOrganizationList =
            new ArrayList<ProjectInnovationOrganization>();
          for (OrganizationTypeDTO id : newInnovationDTO.getNextUserOrganizationTypes()) {
            RepIndOrganizationType repIndOrganizationType =
              this.repIndOrganizationTypeManager.getRepIndOrganizationTypeById(id.getCode());
            if (repIndOrganizationType == null) {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "NextUserOrganizationType",
                id + " is an invalid institution Next User Organization Type"));
            } else {
              ProjectInnovationOrganization projectInnovationOrganization =
                this.projectInnovationOrganizationManager.getProjectInnovationOrganizationById(innovation.getId(),
                  repIndOrganizationType.getId(), phase.getId());

              if (projectInnovationOrganization != null) {

                existingProjectInnovationOrganizationList.add(projectInnovationOrganization);
              } else {
                projectInnovationOrganization = new ProjectInnovationOrganization();
                projectInnovationOrganization.setProjectInnovation(innovation);
                projectInnovationOrganization.setRepIndOrganizationType(repIndOrganizationType);
                projectInnovationOrganization.setPhase(phase);
                this.projectInnovationOrganizationManager
                  .saveProjectInnovationOrganization(projectInnovationOrganization);
                innovation.getProjectInnovationOrganizations().add(projectInnovationOrganization);
              }
            }
          }
          // verify Organization
          for (ProjectInnovationOrganization obj : projectInnovationOrganizationList) {
            if (!existingProjectInnovationOrganizationList.contains(obj)) {
              projectInnovationOrganizationManager.deleteProjectInnovationOrganization(obj.getId());
            }
          }
        }

        // Check innovation contributing CRP
        if (newInnovationDTO.getContributingCGIAREntities() != null
          && newInnovationDTO.getContributingCGIAREntities().size() > 0) {
          List<ProjectInnovationCrp> projectInnovationCrpList = innovation.getProjectInnovationCrps().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());

          List<ProjectInnovationCrp> existingProjectInnovationCrpList = new ArrayList<ProjectInnovationCrp>();
          // search innovationCRPs
          for (CGIAREntityDTO globalUnitCode : newInnovationDTO.getContributingCGIAREntities()) {
            GlobalUnit crp = this.globalUnitManager.findGlobalUnitBySMOCode(globalUnitCode.getCode());
            if (crp == null) {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "ContributingCGIAREntities",
                globalUnitCode.getCode() + " is an invalid CGIAR entity acronym"));
            } else {
              ProjectInnovationCrp projectInnovationCrp = this.projectInnovationCrpManager
                .getProjectInnovationCrpById(innovation.getId(), crp.getId(), phase.getId());
              if (projectInnovationCrp != null) {
                existingProjectInnovationCrpList.add(projectInnovationCrp);
              } else {
                projectInnovationCrp = new ProjectInnovationCrp();
                projectInnovationCrp.setProjectInnovation(innovation);
                projectInnovationCrp.setPhase(phase);
                projectInnovationCrp.setGlobalUnit(crp);
                this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);
                innovation.getProjectInnovationCrps().add(projectInnovationCrp);
              }
            }
          }
          // verify innovationCRPs
          for (ProjectInnovationCrp obj : projectInnovationCrpList) {
            if (!existingProjectInnovationCrpList.contains(obj)) {
              this.projectInnovationCrpManager.deleteProjectInnovationCrp(obj.getId());
            }
          }
        }

        // contributing organizations
        if (newInnovationDTO.getContributingInstitutions() != null
          && newInnovationDTO.getContributingInstitutions().size() > 0) {
          List<ProjectInnovationContributingOrganization> projectInnovationContributingOrganizationList =
            innovation.getProjectInnovationContributingOrganization().stream()
              .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
          List<ProjectInnovationContributingOrganization> existingProjectInnovationContributingOrganizationList =
            new ArrayList<ProjectInnovationContributingOrganization>();
          for (InstitutionDTO id : newInnovationDTO.getContributingInstitutions()) {
            Institution addinstitution = this.institutionManager.getInstitutionById(id.getCode());
            if (addinstitution == null) {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "ContributingInstitution",
                id.getCode() + " is an invalid institution id"));
            } else {
              ProjectInnovationContributingOrganization contributingOrganization =
                projectInnovationContributingOrganizationManager.getProjectInnovationContributingOrganizationById(
                  innovation.getId(), addinstitution.getId(), phase.getId());

              if (contributingOrganization != null) {
                existingProjectInnovationContributingOrganizationList.add(contributingOrganization);
              } else {
                contributingOrganization = new ProjectInnovationContributingOrganization();
                contributingOrganization.setProjectInnovation(innovation);
                contributingOrganization.setPhase(phase);
                contributingOrganization.setInstitution(addinstitution);
                this.projectInnovationContributingOrganizationManager
                  .saveProjectInnovationContributingOrganization(contributingOrganization);
              }
            }
          }
          // verify existing ProjectInnovationContributingOrganization
          for (ProjectInnovationContributingOrganization obj : projectInnovationContributingOrganizationList) {
            if (!existingProjectInnovationContributingOrganizationList.contains(obj)) {
              this.projectInnovationContributingOrganizationManager
                .deleteProjectInnovationContributingOrganization(obj.getId());
            }
          }
        }

        // check innovation Geographic scope
        if (newInnovationDTO.getGeographicScopes() != null && newInnovationDTO.getGeographicScopes().size() > 0) {
          List<ProjectInnovationGeographicScope> projectInnovationGeographicScopeList =
            innovation.getProjectInnovationGeographicScopes().stream()
              .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());

          List<ProjectInnovationGeographicScope> existingProjectInnovationGeographicScopeList =
            new ArrayList<ProjectInnovationGeographicScope>();
          // search for new GeographicScopes
          for (GeographicScopeDTO id : newInnovationDTO.getGeographicScopes()) {
            RepIndGeographicScope geoScope =
              this.repIndGeographicScopeManager.getRepIndGeographicScopeById(id.getCode());
            if (geoScope == null) {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "GeographicScopes",
                id.getCode() + " is an invalid Geographic Scope code"));
            } else {
              ProjectInnovationGeographicScope geographicScope = projectInnovationGeographicScopeManager
                .getProjectInnovationGeographicScope(innovation.getId(), geoScope.getId(), phase.getId());
              if (geographicScope != null) {
                existingProjectInnovationGeographicScopeList.add(geographicScope);
              } else {
                geographicScope = new ProjectInnovationGeographicScope();
                geographicScope.setProjectInnovation(innovation);
                geographicScope.setPhase(phase);
                geographicScope.setRepIndGeographicScope(geoScope);
                this.projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(geographicScope);
                // This is to add innovationGeographicScopeSave to generate
                innovation.getProjectInnovationGeographicScopes().add(geographicScope);
              }
            }
          }
          // verify existing ProjectInnovationGeographicScope
          for (ProjectInnovationGeographicScope obj : projectInnovationGeographicScopeList) {
            if (!existingProjectInnovationGeographicScopeList.contains(obj)) {
              this.projectInnovationGeographicScopeManager.deleteProjectInnovationGeographicScope(obj.getId());
            }
          }
        }

        // check innovation regions
        if (newInnovationDTO.getRegions() != null && newInnovationDTO.getRegions().size() > 0) {
          List<ProjectInnovationRegion> projectInnovationRegionList = innovation.getProjectInnovationRegions().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
          List<ProjectInnovationRegion> existingProjectInnovationRegionList = new ArrayList<ProjectInnovationRegion>();
          for (RegionDTO id : newInnovationDTO.getRegions()) {
            LocElement region = this.locElementManager.getLocElementByNumericISOCode(id.getUM49Code());
            if (region == null) {
              fieldErrors
                .add(new FieldErrorDTO("createInnovation", "Regions", id.getUM49Code() + " is an invalid Region Code"));

            } else if (region.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "Regions", id + " is not a Region code"));
            } else {

              ProjectInnovationRegion projectInnovationRegion = projectInnovationRegionManager
                .getProjectInnovationRegionById(innovation.getId(), region.getId(), phase.getId());
              if (projectInnovationRegion != null) {

                existingProjectInnovationRegionList.add(projectInnovationRegion);
              } else {
                projectInnovationRegion = new ProjectInnovationRegion();
                projectInnovationRegion.setProjectInnovation(innovation);
                projectInnovationRegion.setPhase(phase);
                projectInnovationRegion.setLocElement(region);
                this.projectInnovationRegionManager.saveProjectInnovationRegion(projectInnovationRegion);
                // This is to add innovationRegionSave to generate
                innovation.getProjectInnovationRegions().add(projectInnovationRegion);
              }
            }
          }
          // verify regions
          for (ProjectInnovationRegion obj : projectInnovationRegionList) {

            if (!existingProjectInnovationRegionList.contains(obj)) {
              this.projectInnovationRegionManager.deleteProjectInnovationRegion(obj.getId());
            }
          }
        }
        // check innovations countries
        if (newInnovationDTO.getCountries() != null && newInnovationDTO.getCountries().size() > 0) {
          List<ProjectInnovationCountry> projectInnovationCountryList =
            this.projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId());
          List<ProjectInnovationCountry> existingprojectInnovationCountryList =
            new ArrayList<ProjectInnovationCountry>();
          // search for new Countries
          for (CountryDTO iso : newInnovationDTO.getCountries()) {
            LocElement country = this.locElementManager.getLocElementByNumericISOCode(iso.getCode());
            if (country == null) {
              fieldErrors.add(
                new FieldErrorDTO("createInnovation", "Countries", iso.getCode() + " is an invalid country ISO Code"));

            } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "Countries", iso + " is not a Country ISO code"));
            } else {
              ProjectInnovationCountry projectInnovationCountry = projectInnovationCountryManager
                .getInnovationCountrybyPhase(innovation.getId(), country.getId(), phase.getId());
              if (projectInnovationCountry == null) {
                projectInnovationCountry = new ProjectInnovationCountry();
                projectInnovationCountry.setProjectInnovation(innovation);
                projectInnovationCountry.setPhase(phase);
                projectInnovationCountry.setLocElement(country);
                this.projectInnovationCountryManager.saveProjectInnovationCountry(projectInnovationCountry);
                innovation.getProjectInnovationCountries().add(projectInnovationCountry);
              } else {
                existingprojectInnovationCountryList.add(projectInnovationCountry);
              }
            }
          }
          // verify existing countries that is not in new DTOlist of countries
          for (ProjectInnovationCountry obj : projectInnovationCountryList) {
            if (!existingprojectInnovationCountryList.contains(obj)) {
              projectInnovationCountryManager.deleteProjectInnovationCountry(obj.getId());
            }
          }
        }
      } else {
        fieldErrors
          .add(new FieldErrorDTO("UpdateInnovation", "Innovation", +idInnovation + " is an invalid innovation Code"));
      }
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return innovationID;
  }

}
