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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationEvidenceLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndContributionOfCrpManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationEvidenceLink;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewMilestonesDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.OrganizationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectInnovationARDTO;
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
  private CrpMilestoneManager crpMilestoneManager;
  private ProjectInnovationMilestoneManager projectInnovationMilestoneManager;
  private SrfSubIdoManager srfSubIdoManager;
  private ProjectInnovationSubIdoManager projectInnovationSubIdoManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectInnovationEvidenceLinkManager projectInnovationEvidenceLinkManager;
  // changes to be included to Synthesis
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager;


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
    ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager,
    CrpMilestoneManager crpMilestoneManager, ProjectInnovationMilestoneManager projectInnovationMilestoneManager,
    SrfSubIdoManager srfSubIdoManager, ProjectInnovationSubIdoManager projectInnovationSubIdoManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, LiaisonInstitutionManager liaisonInstitutionManager,
    ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager,
    ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ProjectInnovationEvidenceLinkManager projectInnovationEvidenceLinkManager) {
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
    this.crpMilestoneManager = crpMilestoneManager;
    this.projectInnovationMilestoneManager = projectInnovationMilestoneManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.projectInnovationSubIdoManager = projectInnovationSubIdoManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.reportSynthesisFlagshipProgressInnovationManager = reportSynthesisFlagshipProgressInnovationManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.projectInnovationEvidenceLinkManager = projectInnovationEvidenceLinkManager;
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
    // relationship list to validate info
    List<ProjectInnovationOrganization> projectInnovationOrganizationList =
      new ArrayList<ProjectInnovationOrganization>();
    List<ProjectInnovationContributingOrganization> projectInnovationContributingOrganizationList =
      new ArrayList<ProjectInnovationContributingOrganization>();
    List<ProjectInnovationCrp> projectInnovationCrpList = new ArrayList<ProjectInnovationCrp>();
    List<ProjectInnovationGeographicScope> projectInnovationGeographicScopeList =
      new ArrayList<ProjectInnovationGeographicScope>();
    List<ProjectInnovationRegion> projectInnovationRegionList = new ArrayList<ProjectInnovationRegion>();
    List<ProjectInnovationCountry> projectInnovationCountryList = new ArrayList<ProjectInnovationCountry>();
    List<ProjectInnovationMilestone> projectInnovationMilestoneList = new ArrayList<ProjectInnovationMilestone>();
    List<ProjectInnovationSubIdo> projectInnovationSubIdoList = new ArrayList<ProjectInnovationSubIdo>();
    List<ProjectInnovationEvidenceLink> projectInnovationEvidenceLinkList =
      new ArrayList<ProjectInnovationEvidenceLink>();
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
      fieldErrors.add(
        new FieldErrorDTO("createInnovation", "phase", newInnovationDTO.getPhase().getYear() + " is an invalid year"));
    }
    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(
        new FieldErrorDTO("createInnovation", "phase", newInnovationDTO.getPhase().getYear() + " is a closed phase"));
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
    } else {
      if (newInnovationDTO.getInnovationType().getCode() == 1) {
        if (RepIndStageInnovation != null) {
          if (RepIndStageInnovation.getId() == 1 || RepIndStageInnovation.getId() == 2) {
            if (newInnovationDTO.getInnovationNumber() == null
              || (newInnovationDTO.getInnovationNumber() != null && newInnovationDTO.getInnovationNumber() == 0)) {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "InnovationNumber",
                "Number of innovations need to be more than 0"));
            }
          } else {
            newInnovationDTO.setInnovationNumber(new Long(1));
          }
        } else {
          fieldErrors
            .add(new FieldErrorDTO("createInnovation", "Stage of Innovation", "Stage of innovation code is required"));
        }
      }
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

      // projectInnovationInfo.setProjectInnovation(projectInnovation);
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
      projectInnovationInfo.setInnovationNumber((repIndInnovationType.getId() == 1
        && (newInnovationDTO.getDescriptionStage().equals("1") || newInnovationDTO.getDescriptionStage().equals("2")))
          ? newInnovationDTO.getInnovationNumber() : 1);


      // SAVE innovation CRP
      /*
       * if (innovationID > 0) {
       * ProjectInnovationCrp projectInnovationCrp = new ProjectInnovationCrp();
       * projectInnovationCrp.setGlobalUnit(globalUnitEntity);
       * projectInnovationCrp.setPhase(phase);
       * projectInnovationCrp.setProjectInnovation(projectInnovation);
       * this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);
       * }
       */

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
            projectInnovationOrganization.setRepIndOrganizationType(repIndOrganizationType);
            projectInnovationOrganization.setPhase(phase);
            projectInnovationOrganizationList.add(projectInnovationOrganization);
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
            newContributingOrganization.setPhase(phase);
            newContributingOrganization.setInstitution(addinstitution);
            projectInnovationContributingOrganizationList.add(newContributingOrganization);
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
            projectInnovationCrp.setPhase(phase);
            projectInnovationCrp.setGlobalUnit(crp);
            projectInnovationCrpList.add(projectInnovationCrp);
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
            geographicScope.setPhase(phase);
            geographicScope.setRepIndGeographicScope(geoScope);
            projectInnovationGeographicScopeList.add(geographicScope);
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
            projectInnovationRegion.setPhase(phase);
            projectInnovationRegion.setLocElement(region);
            projectInnovationRegionList.add(projectInnovationRegion);
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
            projectInnovationCountry.setPhase(phase);
            projectInnovationCountry.setLocElement(country);
            projectInnovationCountryList.add(projectInnovationCountry);
            // This is to add innovationOrganizationSave to generate
            // correct auditlog.
            projectInnovation.getProjectInnovationCountries().add(projectInnovationCountry);
          }
        }
      }
      // save milestones
      if (newInnovationDTO.getMilestonesCodeList() != null && newInnovationDTO.getMilestonesCodeList().size() > 0) {
        for (NewMilestonesDTO milestones : newInnovationDTO.getMilestonesCodeList()) {
          if (milestones.getMilestone() != null && milestones.getMilestone().length() > 0
            && milestones.getPrimary() != null) {
            // find milestone by composedID and phase
            CrpMilestone crpMilestone =
              crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestone(), phase.getId());
            if (crpMilestone != null) {
              ProjectInnovationMilestone projectInnovationMilestone = new ProjectInnovationMilestone();
              projectInnovationMilestone.setCrpMilestone(crpMilestone);
              projectInnovationMilestone.setPhase(phase);
              projectInnovationMilestone.setPrimary(milestones.getPrimary());
              projectInnovationMilestoneList.add(projectInnovationMilestone);
              // This is to add innovationOrganizationSave to generate
              // correct auditlog.
              projectInnovation.getProjectInnovationMilestones().add(projectInnovationMilestone);
            } else {
              fieldErrors.add(new FieldErrorDTO("createInnovation", "Milestones",
                milestones.getMilestone() + " is an invalid SMO Code"));
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("createInnovation", "Milestones",
              milestones.getMilestone() + " is an invalid SMO Code"));
          }
        }
      }
      // validation for primary milestones must be one of them selected.
      List<ProjectInnovationMilestone> primaryDataMilestones =
        projectInnovationMilestoneList.stream().filter(c -> c.getPrimary() == true).collect(Collectors.toList());
      if (primaryDataMilestones == null || primaryDataMilestones.size() > 1) {
        fieldErrors
          .add(new FieldErrorDTO("createInnovation", "Milestones", "only one milestone can be marked as principal"));
      }


      // save subIDOs
      if (newInnovationDTO.getSrfSubIdoList() != null && newInnovationDTO.getSrfSubIdoList().size() > 0) {
        for (NewSrfSubIdoDTO subIdos : newInnovationDTO.getSrfSubIdoList()) {
          if (subIdos.getSubIdo() != null && subIdos.getSubIdo().length() > 0 && subIdos.getPrimary() != null) {
            // find subido by composedID and phase
            SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subIdos.getSubIdo());
            if (srfSubIdo != null) {
              ProjectInnovationSubIdo projectInnovationSubIdo = new ProjectInnovationSubIdo();
              projectInnovationSubIdo.setSrfSubIdo(srfSubIdo);
              projectInnovationSubIdo.setPhase(phase);
              projectInnovationSubIdo.setPrimary(subIdos.getPrimary());
              projectInnovationSubIdoList.add(projectInnovationSubIdo);
              // correct auditlog.
              projectInnovation.getProjectInnovationSubIdos().add(projectInnovationSubIdo);
            } else {
              fieldErrors
                .add(new FieldErrorDTO("createInnovation", "SubIdo", subIdos.getSubIdo() + " is an invalid SMO Code"));
            }
          } else {
            fieldErrors
              .add(new FieldErrorDTO("createInnovation", "SubIdo", subIdos.getSubIdo() + " is an invalid SMO Code"));
          }
        }
      }
      // save Evidence links
      if (newInnovationDTO.getEvidenceLinkList() != null && newInnovationDTO.getEvidenceLinkList().size() > 0) {
        for (String link : newInnovationDTO.getEvidenceLinkList()) {
          ProjectInnovationEvidenceLink evidenceLink = new ProjectInnovationEvidenceLink();
          evidenceLink.setPhase(phase);
          evidenceLink.setLink(link);
          projectInnovationEvidenceLinkList.add(evidenceLink);
        }
      }

      // validation for primary subIdo must be one of them selected.
      List<ProjectInnovationSubIdo> primaryDataSubIdos =
        projectInnovationSubIdoList.stream().filter(c -> c.getPrimary() == true).collect(Collectors.toList());
      if (primaryDataSubIdos == null || primaryDataSubIdos.size() > 1) {
        fieldErrors.add(new FieldErrorDTO("createInnovation", "SubIdo", "only one subIdo can be marked as principal"));
      }

      if (fieldErrors.isEmpty()) {
        projectInnovation = this.projectInnovationManager.saveProjectInnovation(projectInnovation);
        innovationID = projectInnovation.getId();
        projectInnovationInfo.setProjectInnovation(projectInnovation);
        if (newInnovationDTO.getMilestonesCodeList() != null && newInnovationDTO.getMilestonesCodeList().size() > 0) {
          projectInnovationInfo.setHasMilestones(true);
        }
        this.projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);
        //

        for (ProjectInnovationOrganization projectInnovationOrganization : projectInnovationOrganizationList) {
          projectInnovationOrganization.setProjectInnovation(projectInnovation);
          this.projectInnovationOrganizationManager.saveProjectInnovationOrganization(projectInnovationOrganization);
        }
        for (ProjectInnovationContributingOrganization newContributingOrganization : projectInnovationContributingOrganizationList) {
          newContributingOrganization.setProjectInnovation(projectInnovation);
          this.projectInnovationContributingOrganizationManager
            .saveProjectInnovationContributingOrganization(newContributingOrganization);
        }
        for (ProjectInnovationGeographicScope geographicScope : projectInnovationGeographicScopeList) {
          geographicScope.setProjectInnovation(projectInnovation);
          this.projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(geographicScope);
        }
        for (ProjectInnovationCrp projectInnovationCrp : projectInnovationCrpList) {
          projectInnovationCrp.setProjectInnovation(projectInnovation);
          this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);
        }
        for (ProjectInnovationRegion projectInnovationRegion : projectInnovationRegionList) {
          projectInnovationRegion.setProjectInnovation(projectInnovation);
          this.projectInnovationRegionManager.saveProjectInnovationRegion(projectInnovationRegion);
        }
        for (ProjectInnovationCountry projectInnovationCountry : projectInnovationCountryList) {
          projectInnovationCountry.setProjectInnovation(projectInnovation);
          this.projectInnovationCountryManager.saveProjectInnovationCountry(projectInnovationCountry);
        }
        for (ProjectInnovationMilestone projectInnovationMilestone : projectInnovationMilestoneList) {
          projectInnovationMilestone.setProjectInnovation(projectInnovation);
          projectInnovationMilestoneManager.saveProjectInnovationMilestone(projectInnovationMilestone);
        }
        for (ProjectInnovationSubIdo projectInnovationSubIdo : projectInnovationSubIdoList) {
          projectInnovationSubIdo.setProjectInnovation(projectInnovation);
          projectInnovationSubIdoManager.saveProjectInnovationSubIdo(projectInnovationSubIdo);
        }
        for (ProjectInnovationEvidenceLink projectInnovationEvidenceLink : projectInnovationEvidenceLinkList) {
          projectInnovationEvidenceLink.setProjectInnovation(projectInnovation);
          projectInnovationEvidenceLinkManager.saveProjectInnovationEvidenceLink(projectInnovationEvidenceLink);
        }

        // verify if was included in synthesis PMU
        LiaisonInstitution liaisonInstitution =
          this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
        if (liaisonInstitution != null) {
          boolean existing = true;
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
          if (reportSynthesis != null) {
            ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress =
              reportSynthesis.getReportSynthesisFlagshipProgress();
            if (reportSynthesisFlagshipProgress == null) {
              reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
              reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
              reportSynthesisFlagshipProgress.setCreatedBy(user);
              existing = false;
              reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
                .saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            }

            final Long innovation = innovationID;
            ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation =
              reportSynthesisFlagshipProgress.getReportSynthesisFlagshipProgressInnovations().stream()
                .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovation).findFirst()
                .orElse(null);
            if (reportSynthesisFlagshipProgressInnovation != null && existing) {
              reportSynthesisFlagshipProgressInnovation = reportSynthesisFlagshipProgressInnovationManager
                .getReportSynthesisFlagshipProgressInnovationById(reportSynthesisFlagshipProgressInnovation.getId());
              reportSynthesisFlagshipProgressInnovation.setActive(false);
              reportSynthesisFlagshipProgressInnovation = reportSynthesisFlagshipProgressInnovationManager
                .saveReportSynthesisFlagshipProgressInnovation(reportSynthesisFlagshipProgressInnovation);
            } else {
              reportSynthesisFlagshipProgressInnovation = new ReportSynthesisFlagshipProgressInnovation();
              reportSynthesisFlagshipProgressInnovation.setCreatedBy(user);
              reportSynthesisFlagshipProgressInnovation.setProjectInnovation(projectInnovation);
              reportSynthesisFlagshipProgressInnovation
                .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);

              reportSynthesisFlagshipProgressInnovation = reportSynthesisFlagshipProgressInnovationManager
                .saveReportSynthesisFlagshipProgressInnovation(reportSynthesisFlagshipProgressInnovation);
              reportSynthesisFlagshipProgressInnovation.setActive(false);
              reportSynthesisFlagshipProgressInnovationManager
                .saveReportSynthesisFlagshipProgressInnovation(reportSynthesisFlagshipProgressInnovation);

            }
          }
        }

      }
    }


    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      for (FieldErrorDTO error : fieldErrors) {
        System.out.println(error.getMessage());
      }
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
        innovation.setMilestones(innovation.getProjectInnovationMilestones().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setSubIdos(innovation.getProjectInnovationSubIdos().stream()
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

  public List<ProjectInnovationARDTO> findAllInnovationsByGlobalUnit(String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<ProjectInnovationARDTO> innovationList = new ArrayList<ProjectInnovationARDTO>();
    List<ProjectInnovation> projectInnovationList = new ArrayList<ProjectInnovation>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("allInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && c.getYear() == repoYear
        && c.getName().equalsIgnoreCase(repoPhase))
      .findFirst().orElse(null);

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("allInnovation", "phase", repoYear + " is an invalid year"));
    }


    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    } else {

      List<ProjectInnovationInfo> projectInnovationInfoList = phase.getProjectInnovationInfos().stream()
        .filter(c -> c.getPhase().getId().equals(phase.getId()) && c.getYear().longValue() == repoYear)
        .collect(Collectors.toList());
      for (ProjectInnovationInfo projectInnovationInfo : projectInnovationInfoList) {
        ProjectInnovation innovation =
          this.projectInnovationManager.getProjectInnovationById(projectInnovationInfo.getProjectInnovation().getId());
        if (innovation.isActive() && projectInnovationManager.isInnovationExcluded(innovation.getId(), phase.getId())) {
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
          innovation.setMilestones(innovation.getProjectInnovationMilestones().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
          innovation.setSubIdos(innovation.getProjectInnovationSubIdos().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
          innovation.setInnovationLinks(innovation.getProjectInnovationEvidenceLinks().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
          innovation.setStudies(innovation.getProjectExpectedStudyInnovations().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
          projectInnovationList.add(innovation);
        }
      }
    }
    innovationList = projectInnovationList.stream()
      .map(innovations -> this.innovationMapper.projectInnovationToInnovationARDTO(innovations))
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
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && c.getYear() == repoYear
        && c.getName().equalsIgnoreCase(repoPhase))
      .findFirst().orElse(null);

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
    } else {
      if (innovation == null || innovation.getProjectInnovationInfo(phase) == null) {
        fieldErrors.add(new FieldErrorDTO("findInnovation", "InnovationId", id + " is an invalid id of an innovation"));
      }
    }


    // innovation.setAllbyPhase(phase);
    if (fieldErrors.isEmpty()) {
      if (innovation.getProjectInnovationInfo(phase) != null) {
        if (innovation.getProjectInnovationInfo().getInnovationNumber() == null) {
          innovation.getProjectInnovationInfo().setInnovationNumber(new Long(0));
        }
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
        innovation.setMilestones(innovation.getProjectInnovationMilestones().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setSubIdos(innovation.getProjectInnovationSubIdos().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        innovation.setInnovationLinks(innovation.getProjectInnovationEvidenceLinks().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
        List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovationList =
          new ArrayList<ProjectExpectedStudyInnovation>();
        for (ProjectExpectedStudyInnovation projectExpectedStudyInnovation : innovation
          .getProjectExpectedStudyInnovations().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())) {
          ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager
            .getProjectExpectedStudyById(projectExpectedStudyInnovation.getProjectExpectedStudy().getId());
          ProjectExpectedStudyInfo info = projectExpectedStudy.getProjectExpectedStudyInfo(phase);

          projectExpectedStudy.setProjectExpectedStudyInfo(info);
          projectExpectedStudyInnovation.setProjectExpectedStudy(projectExpectedStudy);

          projectExpectedStudyInnovationList.add(projectExpectedStudyInnovation);
        }
        innovation.setStudies(projectExpectedStudyInnovationList);
      }
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
    // relationship list to validate info
    List<ProjectInnovationOrganization> projectInnovationOrganizationList =
      new ArrayList<ProjectInnovationOrganization>();
    List<ProjectInnovationContributingOrganization> projectInnovationContributingOrganizationList =
      new ArrayList<ProjectInnovationContributingOrganization>();
    List<ProjectInnovationCrp> projectInnovationCrpList = new ArrayList<ProjectInnovationCrp>();
    List<ProjectInnovationGeographicScope> projectInnovationGeographicScopeList =
      new ArrayList<ProjectInnovationGeographicScope>();
    List<ProjectInnovationRegion> projectInnovationRegionList = new ArrayList<ProjectInnovationRegion>();
    List<ProjectInnovationCountry> projectInnovationCountryList = new ArrayList<ProjectInnovationCountry>();
    List<ProjectInnovationMilestone> projectInnovationMilestoneList = new ArrayList<ProjectInnovationMilestone>();
    List<ProjectInnovationSubIdo> projectInnovationSubIdoList = new ArrayList<ProjectInnovationSubIdo>();
    List<ProjectInnovationEvidenceLink> projectInnovationEvidenceLinkList =
      new ArrayList<ProjectInnovationEvidenceLink>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("updateInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == newInnovationDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newInnovationDTO.getPhase().getName()))
      .findFirst().get();

    ProjectInnovation innovation = this.projectInnovationManager.getProjectInnovationById(idInnovation);
    if (innovation == null) {
      fieldErrors
        .add(new FieldErrorDTO("updateInnovation", "Innovation", +idInnovation + " is an invalid innovation Code"));
    } else {
      if (innovation.getProject().getId().longValue() != newInnovationDTO.getProject().getId().longValue()) {
        fieldErrors.add(new FieldErrorDTO("updateInnovation", "Innovation",
          newInnovationDTO.getProject().getId() + " is an invalid project ID"));
      }
    }
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("updateInnovation", "phase",
        new NewInnovationDTO().getPhase().getYear() + " is an invalid year"));
    }
    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("updateInnovation", "phase",
        new NewInnovationDTO().getPhase().getYear() + " is a closed phase"));
    }

    if (fieldErrors.size() == 0) {
      innovationID = innovation.getId();
      // update basic data
      RepIndStageInnovation RepIndStageInnovation = this.repIndStageInnovationManager
        .getRepIndStageInnovationById(newInnovationDTO.getStageOfInnovation().getCode());
      if (RepIndStageInnovation == null) {
        fieldErrors.add(new FieldErrorDTO("updateInnovation", "Stage of Innovation",
          newInnovationDTO.getStageOfInnovation() + " is an invalid stage of innovation code"));
      }

      Institution leadInstitution = null;
      if (newInnovationDTO.getLeadOrganization() != null) {
        leadInstitution = this.institutionManager.getInstitutionById(newInnovationDTO.getLeadOrganization().getCode());
        if (leadInstitution == null) {
          fieldErrors.add(new FieldErrorDTO("updateInnovation", "Lead institution",
            newInnovationDTO.getLeadOrganization() + " is an invalid institution id"));
        }
      }

      RepIndInnovationType repIndInnovationType =
        this.repIndInnovationTypeManager.getRepIndInnovationTypeById(newInnovationDTO.getInnovationType().getCode());
      if (repIndInnovationType == null) {
        fieldErrors.add(new FieldErrorDTO("updateInnovation", "Innovation Type",
          newInnovationDTO.getInnovationType() + " is an invalid innovation type code"));
      } else {
        if (newInnovationDTO.getInnovationType().getCode() == 1) {
          if (RepIndStageInnovation != null) {
            if (RepIndStageInnovation.getId() == 1 || RepIndStageInnovation.getId() == 2) {
              if (newInnovationDTO.getInnovationNumber() == null
                || (newInnovationDTO.getInnovationNumber() != null && newInnovationDTO.getInnovationNumber() == 0)) {
                fieldErrors.add(new FieldErrorDTO("updateInnovation", "InnovationNumber",
                  "Number of innovations need to be more than 0"));
              }
            } else {
              newInnovationDTO.setInnovationNumber(new Long(1));
            }
          } else {
            fieldErrors.add(
              new FieldErrorDTO("updateInnovation", "Stage of Innovation", "Stage of innovation code is required"));
          }
        }
      }

      Project project = this.projectManager.getProjectById(newInnovationDTO.getProject().getId());

      if (fieldErrors.isEmpty()) {
        // CRP/PTF
        if (project == null) {
          fieldErrors.add(new FieldErrorDTO("updateInnovation", "Project id",
            newInnovationDTO.getProject() + " is an invalid project id"));
        }
        innovation.setProject(project);

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
        projectInnovationInfo.setInnovationNumber((repIndInnovationType.getId() == 1
          && (newInnovationDTO.getDescriptionStage().equals("1") || newInnovationDTO.getDescriptionStage().equals("2")))
            ? newInnovationDTO.getInnovationNumber() : 1);


        // let's check Organizations
        List<ProjectInnovationOrganization> projectInnovationOrganizationListDB =
          innovation.getProjectInnovationOrganizations().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
        List<ProjectInnovationOrganization> existingProjectInnovationOrganizationList =
          new ArrayList<ProjectInnovationOrganization>();
        if (newInnovationDTO.getNextUserOrganizationTypes() != null
          && newInnovationDTO.getNextUserOrganizationTypes().size() > 0) {
          for (OrganizationTypeDTO id : newInnovationDTO.getNextUserOrganizationTypes()) {
            RepIndOrganizationType repIndOrganizationType =
              this.repIndOrganizationTypeManager.getRepIndOrganizationTypeById(id.getCode());
            if (repIndOrganizationType == null) {
              fieldErrors.add(new FieldErrorDTO("updateInnovation", "NextUserOrganizationType",
                id + " is an invalid institution Next User Organization Type"));
            } else {
              ProjectInnovationOrganization projectInnovationOrganization =
                this.projectInnovationOrganizationManager.getProjectInnovationOrganizationById(innovation.getId(),
                  repIndOrganizationType.getId(), phase.getId());
              if (projectInnovationOrganization != null) {
                existingProjectInnovationOrganizationList.add(projectInnovationOrganization);
              } else {
                projectInnovationOrganization = new ProjectInnovationOrganization();
                projectInnovationOrganization.setRepIndOrganizationType(repIndOrganizationType);
                projectInnovationOrganization.setPhase(phase);
                projectInnovationOrganizationList.add(projectInnovationOrganization);
                innovation.getProjectInnovationOrganizations().add(projectInnovationOrganization);
              }
            }
          }
        }

        // Check innovation contributing CRP.
        List<ProjectInnovationCrp> projectInnovationCrpListDB = innovation.getProjectInnovationCrps().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
        List<ProjectInnovationCrp> existingProjectInnovationCrpList = new ArrayList<ProjectInnovationCrp>();
        if (newInnovationDTO.getContributingCGIAREntities() != null
          && newInnovationDTO.getContributingCGIAREntities().size() > 0) {
          // search innovationCRPs
          for (CGIAREntityDTO globalUnitCode : newInnovationDTO.getContributingCGIAREntities()) {
            GlobalUnit crp = this.globalUnitManager.findGlobalUnitBySMOCode(globalUnitCode.getCode());
            if (crp == null) {
              fieldErrors.add(new FieldErrorDTO("updateInnovation", "ContributingCGIAREntities",
                globalUnitCode.getCode() + " is an invalid CGIAR entity acronym"));
            } else {
              ProjectInnovationCrp projectInnovationCrp = this.projectInnovationCrpManager
                .getProjectInnovationCrpById(innovation.getId(), crp.getId(), phase.getId());
              if (projectInnovationCrp != null) {
                existingProjectInnovationCrpList.add(projectInnovationCrp);
              } else {
                projectInnovationCrp = new ProjectInnovationCrp();
                projectInnovationCrp.setPhase(phase);
                projectInnovationCrp.setGlobalUnit(crp);
                projectInnovationCrpList.add(projectInnovationCrp);
                innovation.getProjectInnovationCrps().add(projectInnovationCrp);
              }
            }
          }

        }

        // contributing organizations
        List<ProjectInnovationContributingOrganization> projectInnovationContributingOrganizationListDB =
          innovation.getProjectInnovationContributingOrganization().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
        List<ProjectInnovationContributingOrganization> existingProjectInnovationContributingOrganizationList =
          new ArrayList<ProjectInnovationContributingOrganization>();
        if (newInnovationDTO.getContributingInstitutions() != null
          && newInnovationDTO.getContributingInstitutions().size() > 0) {
          for (InstitutionDTO id : newInnovationDTO.getContributingInstitutions()) {
            Institution addinstitution = this.institutionManager.getInstitutionById(id.getCode());
            if (addinstitution == null) {
              fieldErrors.add(new FieldErrorDTO("updateInnovation", "ContributingInstitution",
                id.getCode() + " is an invalid institution id"));
            } else {
              ProjectInnovationContributingOrganization contributingOrganization =
                projectInnovationContributingOrganizationManager.getProjectInnovationContributingOrganizationById(
                  innovation.getId(), addinstitution.getId(), phase.getId());
              if (contributingOrganization != null) {
                existingProjectInnovationContributingOrganizationList.add(contributingOrganization);
              } else {
                contributingOrganization = new ProjectInnovationContributingOrganization();
                contributingOrganization.setPhase(phase);
                contributingOrganization.setInstitution(addinstitution);
                projectInnovationContributingOrganizationList.add(contributingOrganization);
              }
            }
          }

        }

        // check innovation Geographic scope
        List<ProjectInnovationGeographicScope> projectInnovationGeographicScopeListDB =
          innovation.getProjectInnovationGeographicScopes().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
        List<ProjectInnovationGeographicScope> existingProjectInnovationGeographicScopeList =
          new ArrayList<ProjectInnovationGeographicScope>();
        if (newInnovationDTO.getGeographicScopes() != null && newInnovationDTO.getGeographicScopes().size() > 0) {
          // search for new GeographicScopes
          for (GeographicScopeDTO id : newInnovationDTO.getGeographicScopes()) {
            RepIndGeographicScope geoScope =
              this.repIndGeographicScopeManager.getRepIndGeographicScopeById(id.getCode());
            if (geoScope == null) {
              fieldErrors.add(new FieldErrorDTO("updateInnovation", "GeographicScopes",
                id.getCode() + " is an invalid Geographic Scope code"));
            } else {
              ProjectInnovationGeographicScope geographicScope = projectInnovationGeographicScopeManager
                .getProjectInnovationGeographicScope(innovation.getId(), geoScope.getId(), phase.getId());
              if (geographicScope != null) {
                existingProjectInnovationGeographicScopeList.add(geographicScope);
              } else {
                geographicScope = new ProjectInnovationGeographicScope();
                geographicScope.setPhase(phase);
                geographicScope.setRepIndGeographicScope(geoScope);
                projectInnovationGeographicScopeList.add(geographicScope);
                // This is to add innovationGeographicScopeSave to generate
                innovation.getProjectInnovationGeographicScopes().add(geographicScope);
              }
            }
          }
        }

        // check innovations milestones
        List<ProjectInnovationMilestone> projectInnovationMilestoneListDB =
          innovation.getProjectInnovationMilestones().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
        List<ProjectInnovationMilestone> existingProjectInnovationMilestoneList =
          new ArrayList<ProjectInnovationMilestone>();
        if (newInnovationDTO.getMilestonesCodeList() != null && newInnovationDTO.getMilestonesCodeList().size() > 0) {
          for (NewMilestonesDTO milestones : newInnovationDTO.getMilestonesCodeList()) {
            if (milestones.getMilestone() != null && milestones.getMilestone().length() > 0
              && milestones.getPrimary() != null) {
              // find milestone by composedID and phase
              CrpMilestone crpMilestone =
                crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestone(), phase.getId());
              if (crpMilestone != null) {
                ProjectInnovationMilestone projectInnovationMilestone = projectInnovationMilestoneManager
                  .getProjectInnovationMilestoneById(innovation.getId(), crpMilestone.getId(), phase.getId());
                if (projectInnovationMilestone != null) {
                  existingProjectInnovationMilestoneList.add(projectInnovationMilestone);
                } else {
                  projectInnovationMilestone = new ProjectInnovationMilestone();
                  projectInnovationMilestone.setCrpMilestone(crpMilestone);
                  projectInnovationMilestone.setPhase(phase);
                  projectInnovationMilestone.setPrimary(milestones.getPrimary());
                  projectInnovationMilestoneList.add(projectInnovationMilestone);
                  // This is to add innovationOrganizationSave to generate
                  // correct auditlog.
                  innovation.getProjectInnovationMilestones().add(projectInnovationMilestone);
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("updateInnovation", "Milestones",
                  milestones.getMilestone() + " is an invalid SMO Code"));
              }
            }
          }
        }

        // check innovations subidos
        List<ProjectInnovationSubIdo> projectInnovationSubIdoListDB = innovation.getProjectInnovationSubIdos().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
        List<ProjectInnovationSubIdo> existingProjectInnovationSubIdoList = new ArrayList<ProjectInnovationSubIdo>();
        if (newInnovationDTO.getSrfSubIdoList() != null && newInnovationDTO.getSrfSubIdoList().size() > 0) {
          for (NewSrfSubIdoDTO subIdos : newInnovationDTO.getSrfSubIdoList()) {
            if (subIdos.getSubIdo() != null && subIdos.getSubIdo().length() > 0 && subIdos.getPrimary() != null) {
              // find SubIdo by composedID and phase
              SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subIdos.getSubIdo());
              if (srfSubIdo != null) {
                ProjectInnovationSubIdo projectInnovationSubIdo = projectInnovationSubIdoManager
                  .getProjectInnovationSubIdoByPhase(innovation.getId(), srfSubIdo.getId(), phase.getId());
                if (projectInnovationSubIdo != null) {
                  projectInnovationSubIdo.setPrimary(subIdos.getPrimary());
                  projectInnovationSubIdoList.add(projectInnovationSubIdo);
                  existingProjectInnovationSubIdoList.add(projectInnovationSubIdo);
                } else {
                  projectInnovationSubIdo = new ProjectInnovationSubIdo();
                  projectInnovationSubIdo.setSrfSubIdo(srfSubIdo);
                  projectInnovationSubIdo.setPhase(phase);
                  projectInnovationSubIdo.setPrimary(subIdos.getPrimary());
                  projectInnovationSubIdoList.add(projectInnovationSubIdo);
                  // This is to add innovationOrganizationSave to generate
                  // correct auditlog.
                  innovation.getProjectInnovationSubIdos().add(projectInnovationSubIdo);
                }
              } else {
                fieldErrors.add(
                  new FieldErrorDTO("updateInnovation", "SubIdos", subIdos.getSubIdo() + " is an invalid SMO Code"));
              }
            }
          }
        }
        // check innovation regions
        List<ProjectInnovationRegion> projectInnovationRegionListDB = innovation.getProjectInnovationRegions().stream()
          .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
        List<ProjectInnovationRegion> existingProjectInnovationRegionList = new ArrayList<ProjectInnovationRegion>();
        if (newInnovationDTO.getRegions() != null && newInnovationDTO.getRegions().size() > 0) {
          for (RegionDTO id : newInnovationDTO.getRegions()) {
            LocElement region = this.locElementManager.getLocElementByNumericISOCode(id.getUM49Code());
            if (region == null) {
              fieldErrors
                .add(new FieldErrorDTO("createInnovation", "Regions", id.getUM49Code() + " is an invalid Region Code"));
            } else if (region.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
              fieldErrors.add(new FieldErrorDTO("updateInnovation", "Regions", id + " is not a Region code"));
            } else {
              ProjectInnovationRegion projectInnovationRegion = projectInnovationRegionManager
                .getProjectInnovationRegionById(innovation.getId(), region.getId(), phase.getId());
              if (projectInnovationRegion != null) {
                existingProjectInnovationRegionList.add(projectInnovationRegion);
              } else {
                projectInnovationRegion = new ProjectInnovationRegion();
                projectInnovationRegion.setPhase(phase);
                projectInnovationRegion.setLocElement(region);
                projectInnovationRegionList.add(projectInnovationRegion);
                // This is to add innovationRegionSave to generate
                innovation.getProjectInnovationRegions().add(projectInnovationRegion);
              }
            }
          }
        }
        // check innovations countries
        List<ProjectInnovationCountry> projectInnovationCountryListDB =
          this.projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId());
        List<ProjectInnovationCountry> existingprojectInnovationCountryList = new ArrayList<ProjectInnovationCountry>();
        if (newInnovationDTO.getCountries() != null && newInnovationDTO.getCountries().size() > 0) {
          // search for new Countries
          for (CountryDTO iso : newInnovationDTO.getCountries()) {
            LocElement country = this.locElementManager.getLocElementByNumericISOCode(iso.getCode());
            if (country == null) {
              fieldErrors.add(
                new FieldErrorDTO("updateInnovation", "Countries", iso.getCode() + " is an invalid country ISO Code"));
            } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
              fieldErrors.add(new FieldErrorDTO("updateInnovation", "Countries", iso + " is not a Country ISO code"));
            } else {
              ProjectInnovationCountry projectInnovationCountry = projectInnovationCountryManager
                .getInnovationCountrybyPhase(innovation.getId(), country.getId(), phase.getId());
              if (projectInnovationCountry == null) {
                projectInnovationCountry = new ProjectInnovationCountry();
                projectInnovationCountry.setPhase(phase);
                projectInnovationCountry.setLocElement(country);
                projectInnovationCountryList.add(projectInnovationCountry);
                innovation.getProjectInnovationCountries().add(projectInnovationCountry);
              } else {
                existingprojectInnovationCountryList.add(projectInnovationCountry);
              }
            }
          }
        }

        // save Evidence links
        List<ProjectInnovationEvidenceLink> projectInnovationEvidenceLinkListDB =
          this.projectInnovationEvidenceLinkManager.getProjectInnovationEvidenceLinkByPhase(innovation.getId(),
            phase.getId());
        List<ProjectInnovationEvidenceLink> existingprojectInnovationEvidenceLinkList =
          new ArrayList<ProjectInnovationEvidenceLink>();
        if (newInnovationDTO.getEvidenceLinkList() != null && newInnovationDTO.getEvidenceLinkList().size() > 0) {
          for (String link : newInnovationDTO.getEvidenceLinkList()) {
            ProjectInnovationEvidenceLink projectInnovationEvidenceLink = projectInnovationEvidenceLinkManager
              .getProjectInnovationEvidenceLinkByPhase(innovation.getId(), link, phase.getId());
            if (projectInnovationEvidenceLink != null) {
              existingprojectInnovationEvidenceLinkList.add(projectInnovationEvidenceLink);
            } else {
              projectInnovationEvidenceLink = new ProjectInnovationEvidenceLink();
              projectInnovationEvidenceLink.setPhase(phase);
              projectInnovationEvidenceLink.setLink(link);
              projectInnovationEvidenceLinkList.add(projectInnovationEvidenceLink);
            }

          }
        }
        if (fieldErrors.isEmpty()) {
          // SAVE innovation info
          innovation = this.projectInnovationManager.saveProjectInnovation(innovation);
          if (innovation != null) {
            projectInnovationInfo.setHasMilestones(false);
            if (newInnovationDTO.getMilestonesCodeList() != null
              && newInnovationDTO.getMilestonesCodeList().size() > 0) {
              projectInnovationInfo.setHasMilestones(true);
            }
            this.projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);
            for (ProjectInnovationOrganization projectInnovationOrganization : projectInnovationOrganizationList) {
              projectInnovationOrganization.setProjectInnovation(innovation);
              this.projectInnovationOrganizationManager
                .saveProjectInnovationOrganization(projectInnovationOrganization);
            }
            // verify Organization
            for (ProjectInnovationOrganization obj : projectInnovationOrganizationListDB) {
              if (!existingProjectInnovationOrganizationList.contains(obj)) {
                projectInnovationOrganizationManager.deleteProjectInnovationOrganization(obj.getId());
              }
            }
            for (ProjectInnovationCrp projectInnovationCrp : projectInnovationCrpList) {
              projectInnovationCrp.setProjectInnovation(innovation);
              this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);
            }
            // verify innovationCRPs
            for (ProjectInnovationCrp obj : projectInnovationCrpListDB) {
              if (!existingProjectInnovationCrpList.contains(obj)) {
                this.projectInnovationCrpManager.deleteProjectInnovationCrp(obj.getId());
              }
            }
            for (ProjectInnovationContributingOrganization contributingOrganization : projectInnovationContributingOrganizationList) {
              contributingOrganization.setProjectInnovation(innovation);
              this.projectInnovationContributingOrganizationManager
                .saveProjectInnovationContributingOrganization(contributingOrganization);
            }
            // verify existing ProjectInnovationContributingOrganization
            for (ProjectInnovationContributingOrganization obj : projectInnovationContributingOrganizationListDB) {
              if (!existingProjectInnovationContributingOrganizationList.contains(obj)) {
                this.projectInnovationContributingOrganizationManager
                  .deleteProjectInnovationContributingOrganization(obj.getId());
              }
            }
            for (ProjectInnovationGeographicScope geographicScope : projectInnovationGeographicScopeList) {
              geographicScope.setProjectInnovation(innovation);
              this.projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(geographicScope);
            }
            // verify existing ProjectInnovationGeographicScope
            for (ProjectInnovationGeographicScope obj : projectInnovationGeographicScopeListDB) {
              if (!existingProjectInnovationGeographicScopeList.contains(obj)) {
                this.projectInnovationGeographicScopeManager.deleteProjectInnovationGeographicScope(obj.getId());
              }
            }
            for (ProjectInnovationMilestone projectInnovationMilestone : projectInnovationMilestoneList) {
              projectInnovationMilestone.setProjectInnovation(innovation);
              this.projectInnovationMilestoneManager.saveProjectInnovationMilestone(projectInnovationMilestone);
            }
            // verify milestones
            for (ProjectInnovationMilestone obj : projectInnovationMilestoneListDB) {
              if (!existingProjectInnovationMilestoneList.contains(obj)) {
                this.projectInnovationMilestoneManager.deleteProjectInnovationMilestone(obj.getId());
              }
            }

            for (ProjectInnovationRegion projectInnovationRegion : projectInnovationRegionList) {
              projectInnovationRegion.setProjectInnovation(innovation);
              this.projectInnovationRegionManager.saveProjectInnovationRegion(projectInnovationRegion);
            }
            // verify regions
            for (ProjectInnovationRegion obj : projectInnovationRegionListDB) {
              if (!existingProjectInnovationRegionList.contains(obj)) {
                this.projectInnovationRegionManager.deleteProjectInnovationRegion(obj.getId());
              }
            }
            for (ProjectInnovationCountry projectInnovationCountry : projectInnovationCountryList) {
              projectInnovationCountry.setProjectInnovation(innovation);
              this.projectInnovationCountryManager.saveProjectInnovationCountry(projectInnovationCountry);
            }
            // verify existing countries
            for (ProjectInnovationCountry obj : projectInnovationCountryListDB) {
              if (!existingprojectInnovationCountryList.contains(obj)) {
                projectInnovationCountryManager.deleteProjectInnovationCountry(obj.getId());
              }
            }
            for (ProjectInnovationSubIdo projectInnovationSubIdo : projectInnovationSubIdoList) {
              projectInnovationSubIdo.setProjectInnovation(innovation);
              projectInnovationSubIdoManager.saveProjectInnovationSubIdo(projectInnovationSubIdo);
            }
            // verify existing subIdos
            for (ProjectInnovationSubIdo obj : projectInnovationSubIdoListDB) {
              if (!existingProjectInnovationSubIdoList.contains(obj)) {
                projectInnovationSubIdoManager.deleteProjectInnovationSubIdo(obj.getId());
              }
            }

            for (ProjectInnovationEvidenceLink projectInnovationEvidenceLink : projectInnovationEvidenceLinkList) {
              projectInnovationEvidenceLink.setProjectInnovation(innovation);
              projectInnovationEvidenceLinkManager.saveProjectInnovationEvidenceLink(projectInnovationEvidenceLink);
            }
            // verify existing Evidence Links
            for (ProjectInnovationEvidenceLink obj : projectInnovationEvidenceLinkListDB) {
              if (!existingprojectInnovationEvidenceLinkList.contains(obj)) {
                projectInnovationEvidenceLinkManager.deleteProjectInnovationEvidenceLink(obj.getId());
              }
            }
          }
        }
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
