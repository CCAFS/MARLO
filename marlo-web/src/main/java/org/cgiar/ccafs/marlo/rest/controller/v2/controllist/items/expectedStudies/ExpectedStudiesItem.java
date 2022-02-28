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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.EvidenceTagManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyReferenceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewMilestonesDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudiesARDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.QuantificationDTO;
import org.cgiar.ccafs.marlo.rest.dto.ReferenceCitedDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectExpectedStudyMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ExpectedStudiesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;

  private RepIndStageStudyManager repIndStageStudyManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private SrfSubIdoManager srfSubIdoManager;
  private CrpProgramManager crpProgramManager;
  private LocElementManager locElementManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectPolicyManager projectPolicyManager;
  private InstitutionManager institutionManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  private EvidenceTagManager evidenceTagManager;
  private GeneralStatusManager generalStatusManager;
  private ProjectManager projectManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private StudyTypeManager studyTypeManager;
  private CrpMilestoneManager crpMilestoneManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;
  private ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager;
  private ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager;
  private ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager;
  private ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager;
  private ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager;
  private ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager;
  private ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager;
  private ProjectExpectedStudyMapper projectExpectedStudyMapper;
  private GlobalUnitProjectManager globalUnitProjectManager;
  private ProjectExpectedStudyReferenceManager projectExpectedStudyReferenceManager;
  // changes to be included to Synthesis
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager;


  @Inject
  public ExpectedStudiesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    RepIndStageStudyManager repIndStageStudyManager, RepIndGeographicScopeManager repIndGeographicScopeManager,
    SrfSloIndicatorManager srfSloIndicatorManager, SrfSloIndicatorTargetManager srfSloIndicatorTargetManager,
    SrfSubIdoManager srfSubIdoManager, CrpProgramManager crpProgramManager, LocElementManager locElementManager,
    ProjectInnovationManager projectInnovationManager, ProjectPolicyManager projectPolicyManager,
    InstitutionManager institutionManager, CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager, EvidenceTagManager evidenceTagManager,
    GeneralStatusManager generalStatusManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager,
    ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager,
    ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager,
    ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager,
    ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager,
    ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager,
    ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager,
    StudyTypeManager studyTypeManager, ProjectManager projectManager, CrpMilestoneManager crpMilestoneManager,
    ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager,
    ProjectExpectedStudyMapper projectExpectedStudyMapper, GlobalUnitProjectManager globalUnitProjectManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressStudyManager reportSynthesisFlagshipProgressStudyManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ProjectExpectedStudyReferenceManager projectExpectedStudyReferenceManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.repIndStageStudyManager = repIndStageStudyManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.crpProgramManager = crpProgramManager;
    this.locElementManager = locElementManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyManager = projectPolicyManager;
    this.institutionManager = institutionManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    this.evidenceTagManager = evidenceTagManager;
    this.generalStatusManager = generalStatusManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.projectExpectedStudyGeographicScopeManager = projectExpectedStudyGeographicScopeManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.projectExpectedStudyRegionManager = projectExpectedStudyRegionManager;
    this.projectExpectedStudySrfTargetManager = projectExpectedStudySrfTargetManager;
    this.projectExpectedStudyFlagshipManager = projectExpectedStudyFlagshipManager;
    this.projectExpectedStudyInstitutionManager = projectExpectedStudyInstitutionManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectExpectedStudyCrpManager = projectExpectedStudyCrpManager;
    this.projectExpectedStudyQuantificationManager = projectExpectedStudyQuantificationManager;
    this.projectExpectedStudyLinkManager = projectExpectedStudyLinkManager;
    this.projectExpectedStudyMilestoneManager = projectExpectedStudyMilestoneManager;
    this.studyTypeManager = studyTypeManager;
    this.projectManager = projectManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.projectExpectedStudySubIdoManager = projectExpectedStudySubIdoManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.projectExpectedStudyMapper = projectExpectedStudyMapper;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.reportSynthesisFlagshipProgressStudyManager = reportSynthesisFlagshipProgressStudyManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.projectExpectedStudyReferenceManager = projectExpectedStudyReferenceManager;
  }

  private int countWords(String string) {
    int wordCount = 0;
    string = StringUtils.stripToEmpty(string);
    if (!string.isEmpty()) {
      String[] words = StringUtils.split(string);
      wordCount = words.length;
    }

    return wordCount;
  }

  public Long createExpectedStudy(NewProjectExpectedStudyDTO newProjectExpectedStudy, String entityAcronym, User user) {
    Long projectExpectedStudyID = null;
    Phase phase = null;
    Long id = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "GlobalUnitEntity",
        entityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "GlobalUnitEntity",
          "The Global Unit with acronym " + entityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), entityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newProjectExpectedStudy.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "PhaseEntity", "Phase must not be null"));
    } else {
      if (newProjectExpectedStudy.getPhase().getName() == null
        || newProjectExpectedStudy.getPhase().getName().trim().isEmpty()
        || newProjectExpectedStudy.getPhase().getYear() == null
        // DANGER! Magic number ahead
        || newProjectExpectedStudy.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
            && p.getYear() == newProjectExpectedStudy.getPhase().getYear()
            && p.getName().equalsIgnoreCase(newProjectExpectedStudy.getPhase().getName()))
          .findFirst().orElse(null);
        if (phase == null) {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "phase", newProjectExpectedStudy.getPhase().getName()
            + ' ' + newProjectExpectedStudy.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    Project project = null;
    if (newProjectExpectedStudy.getProject() != null) {
      project = projectManager.getProjectById(newProjectExpectedStudy.getProject());
      if (project == null) {
        fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "Project",
          newProjectExpectedStudy.getProject() + " is an invalid project ID"));
      }

    } else {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "Project", "Please insert a Project ID"));
    }

    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "phase", newProjectExpectedStudy.getPhase().getName()
        + ' ' + newProjectExpectedStudy.getPhase().getYear() + " is an closed phase"));
    }

    if (fieldErrors.isEmpty()) {
      ProjectExpectedStudy projectExpectedStudy = new ProjectExpectedStudy();
      List<RepIndGeographicScope> geographicScopeList = new ArrayList<RepIndGeographicScope>();
      List<SrfSloIndicator> srfSloIndicatorList = new ArrayList<SrfSloIndicator>();
      List<GlobalUnit> crpContributing = new ArrayList<GlobalUnit>();
      List<CrpProgram> flagshipList = new ArrayList<>();
      List<LocElement> countriesList = new ArrayList<>();
      List<LocElement> regionsList = new ArrayList<>();
      List<ProjectExpectedStudySubIdo> srfSubIdoList = new ArrayList<>();
      List<ProjectInnovation> projectInnovationList = new ArrayList<ProjectInnovation>();
      List<ProjectPolicy> projectPolicyList = new ArrayList<ProjectPolicy>();
      List<Institution> institutionList = new ArrayList<Institution>();
      List<String> linkList = new ArrayList<String>();
      List<ProjectExpectedStudyMilestone> milestoneList = new ArrayList<ProjectExpectedStudyMilestone>();
      List<ProjectExpectedStudyQuantification> ExpectedStudyQuantificationList =
        new ArrayList<ProjectExpectedStudyQuantification>();
      List<ProjectExpectedStudyReference> referenceList = new ArrayList<ProjectExpectedStudyReference>();
      int hasPrimary = 0;
      int wordCount = -1;

      if (newProjectExpectedStudy.getProjectExpectedStudyInfo() != null) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();

        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getTitle() != null
          && !newProjectExpectedStudy.getProjectExpectedStudyInfo().getTitle().trim().isEmpty()) {
          projectExpectedStudyInfo.setTitle(newProjectExpectedStudy.getProjectExpectedStudyInfo().getTitle());
        } else {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "Title", "Please insert a valid title"));
        }

        // shortOutcome
        wordCount = this.countWords(newProjectExpectedStudy.getProjectExpectedStudyInfo().getOutcomeImpactStatement());
        if (wordCount > 80) {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "outcomeImpactStatement",
            "Short outcome/impact statement excedes the maximum number of words (80 words)"));
        } else {
          projectExpectedStudyInfo.setOutcomeImpactStatement(
            newProjectExpectedStudy.getProjectExpectedStudyInfo().getOutcomeImpactStatement());
        }

        // communicationsMaterial
        wordCount = this.countWords(newProjectExpectedStudy.getProjectExpectedStudyInfo().getComunicationsMaterial());
        if (wordCount > 400) {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "comunicationsMaterial",
            "Outcome story for communications use excedes the maximum number of words (400 words)"));
        } else {
          projectExpectedStudyInfo
            .setComunicationsMaterial(newProjectExpectedStudy.getProjectExpectedStudyInfo().getComunicationsMaterial());
        }

        // CGIAR innovations
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getCgiarInnovation() != null
          && !newProjectExpectedStudy.getProjectExpectedStudyInfo().getCgiarInnovation().isEmpty()) {
          projectExpectedStudyInfo
            .setCgiarInnovation(newProjectExpectedStudy.getProjectExpectedStudyInfo().getCgiarInnovation());
        }
        // elaborationOutcomeImpactStatement
        wordCount =
          this.countWords(newProjectExpectedStudy.getProjectExpectedStudyInfo().getElaborationOutcomeImpactStatement());
        if (wordCount > 400) {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "elaborationOutcomeImpactStatement",
            "Elaboration of Outcome/Impact Statement excedes the maximum number of words (400 words)"));
        } else {
          projectExpectedStudyInfo.setElaborationOutcomeImpactStatement(
            newProjectExpectedStudy.getProjectExpectedStudyInfo().getElaborationOutcomeImpactStatement());
        }

        // referencesText
        projectExpectedStudyInfo
          .setReferencesText(newProjectExpectedStudy.getProjectExpectedStudyInfo().getReferencesText());


        // DANGER! Magic number ahead
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getYear() > 1900) {
          projectExpectedStudyInfo.setYear(newProjectExpectedStudy.getProjectExpectedStudyInfo().getYear());
        } else {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "Year", "Please insert a valid year"));
        }

        projectExpectedStudyInfo.setPhase(phase);
        projectExpectedStudyInfo.setContacts(newProjectExpectedStudy.getProjectExpectedStudyInfo().getContacts());

        StudyType studyType = null;
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null) {
          studyType =
            studyTypeManager.getStudyTypeById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType());
          if (studyType != null) {
            // DANGER! Magic number ahead
            if (studyType.getId() != 1) {
              fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Study Type",
                "Study type with id " + newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType()
                  + " does not correspond with an Outcome Impact Case Report"));
            } else {
              projectExpectedStudyInfo.setStudyType(studyType);
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Study Type",
              newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() + " is an invalid study code"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Study", "study identifier can not be null nor empty"));
        }

        // null check not necessary because status is a long (primitive type)
        GeneralStatus generalStatus =
          generalStatusManager.getGeneralStatusById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getStatus());
        if (generalStatus != null) {
          projectExpectedStudyInfo.setStatus(generalStatus);

        } else {
          fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Status",
            newProjectExpectedStudy.getProjectExpectedStudyInfo().getStatus() + " is an invalid status code"));
        }


        /*
         * EvidenceTag evidenceTag = null;
         * if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getTag() != null) {
         * evidenceTag =
         * evidenceTagManager.getEvidenceTagById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getTag());
         * if (evidenceTag != null) {
         * projectExpectedStudyInfo.setEvidenceTag(evidenceTag);
         * } else {
         * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Evidence Tag",
         * newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange()
         * + " is an invalid Evidence Tag code"));
         * }
         * } else {
         * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "EvidenceTag",
         * "evidence tag identifier can not be null nor empty"));
         * }
         */

        RepIndStageStudy repIndStageStudy = null;
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange() != null) {
          repIndStageStudy = repIndStageStudyManager
            .getRepIndStageStudyById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange());
          if (repIndStageStudy != null) {
            projectExpectedStudyInfo.setRepIndStageStudy(repIndStageStudy);
          } else {
            fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "MaturityOfChange",
              newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange()
                + " is an invalid Level of maturity of change reported code"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "MaturityOfChange",
            "maturity of change identifier can not be null nor empty"));
        }

        if (fieldErrors.size() == 0) {
          projectExpectedStudy.setPhase(phase.getId());
          projectExpectedStudy.setProjectExpectedStudyInfo(projectExpectedStudyInfo);

          // geographic
          if (newProjectExpectedStudy.getGeographicScopes() != null
            && newProjectExpectedStudy.getGeographicScopes().size() > 0) {
            for (String geographicscope : newProjectExpectedStudy.getGeographicScopes()) {
              if (geographicscope != null && !geographicscope.trim().isEmpty()) {
                id = this.tryParseLong(geographicscope.trim(), fieldErrors, "CreateExpectedStudy", "GeographicScope");
                if (id != null) {
                  RepIndGeographicScope repIndGeographicScope =
                    repIndGeographicScopeManager.getRepIndGeographicScopeById(id);
                  if (repIndGeographicScope != null) {
                    geographicScopeList.add(repIndGeographicScope);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "GeographicScope",
                      id + " is an invalid geographicScope identifier"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "GeographicScope",
                  "geographicScope identifier can not be null nor empty"));
              }
            }
          }
          projectExpectedStudyInfo.setScopeComments(newProjectExpectedStudy.getGeographicScopeComment());

          /*
           * else {
           * fieldErrors.add(
           * new FieldErrorDTO("CreateExpectedStudy", "GeographicScope", "Please enter the Geographic Scope(s)."));
           * }
           */

          // Slo Target
          if (newProjectExpectedStudy.getSrfSloTargetList() != null
            && newProjectExpectedStudy.getSrfSloTargetList().size() > 0) {
            for (String sloTarget : newProjectExpectedStudy.getSrfSloTargetList()) {
              if (sloTarget != null && !sloTarget.trim().isEmpty()) {
                SrfSloIndicatorTarget srfSloIndicatorTarget =
                  srfSloIndicatorTargetManager.findbyTargetIndicatorCode(sloTarget);
                if (srfSloIndicatorTarget != null) {
                  SrfSloIndicator srfSloIndicator =
                    srfSloIndicatorManager.getSrfSloIndicatorById(srfSloIndicatorTarget.getSrfSloIndicator().getId());
                  srfSloIndicatorList.add(srfSloIndicator);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SrfSloIndicatorTarget ",
                    sloTarget + " is an invalid SLOIndicatorTarget identifier"));
                }

              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SrfSloIndicatorTarget",
                  "sloTarget identifier can not be null nor empty"));
              }
            }
          }

          // crps
          if (newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO() != null
            && newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO().size() > 0) {
            for (String crps : newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO()) {
              if (crps != null && !crps.trim().isEmpty()) {
                GlobalUnit globalUnit = globalUnitManager.findGlobalUnitBySMOCode(crps.trim());
                if (globalUnit != null) {
                  crpContributing.add(globalUnit);
                } else {
                  fieldErrors.add(
                    new FieldErrorDTO("CreateExpectedStudy", "GlobalUnit ", crps + " is an invalid CRP identifier"));
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "GlobalUnit",
                  "A GlobalUnit code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "GlobalUnit", "Please enter the CRP(s)."));
             * }
             */

          // flagships
          if (newProjectExpectedStudy.getFlagshipsList() != null
            && newProjectExpectedStudy.getFlagshipsList().size() > 0) {
            for (String flagship : newProjectExpectedStudy.getFlagshipsList()) {
              if (flagship != null && !flagship.trim().isEmpty()) {
                id = this.tryParseLong(flagship.trim(), fieldErrors, "CreateExpectedStudy", "CrpProgram");
                if (id != null) {
                  CrpProgram crpProgram = crpProgramManager.getCrpProgramBySmoCode(flagship.trim());
                  if (crpProgram != null) {
                    flagshipList.add(crpProgram);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Flagship/Module",
                      flagship + " is an invalid Flagship/Module code"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "CrpProgram",
                  "A CrpProgram code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "CrpProgram", "Please enter the Flagship(s)."));
             * }
             */

          // countries
          if (newProjectExpectedStudy.getCountries() != null && newProjectExpectedStudy.getCountries().size() > 0) {
            for (String countries : newProjectExpectedStudy.getCountries()) {
              if (countries != null && !countries.trim().isEmpty()) {
                id = this.tryParseLong(countries.trim(), fieldErrors, "CreateExpectedStudy", "Country");
                if (id != null) {
                  LocElement country = this.locElementManager.getLocElementByNumericISOCode(id);
                  if (country == null) {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Countries",
                      countries + " is an invalid country ISO Code"));

                  } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
                    fieldErrors.add(
                      new FieldErrorDTO("CreateExpectedStudy", "Countries", countries + " is not a Country ISO code"));
                  } else {
                    countriesList.add(country);
                  }
                }
              } else {
                fieldErrors.add(
                  new FieldErrorDTO("CreateExpectedStudy", "Countries", "A Country code can not be null nor empty."));
              }
            }
            // verification for single or multiple countries
            if (countriesList.size() == 1) {
              geographicScopeList
                .removeIf(g -> g.getId() != null && (g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_MULTINATIONAL
                  || g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_NATIONAL));
              // should not cause exception
              geographicScopeList.add(repIndGeographicScopeManager
                .getRepIndGeographicScopeById(APConstants.REP_IND_GEOGRAPHIC_SCOPE_NATIONAL));
            } else if (countriesList.size() > 1) {
              geographicScopeList
                .removeIf(g -> g.getId() != null && (g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_NATIONAL
                  || g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_MULTINATIONAL));
              // should not cause exception
              geographicScopeList.add(repIndGeographicScopeManager
                .getRepIndGeographicScopeById(APConstants.REP_IND_GEOGRAPHIC_SCOPE_MULTINATIONAL));
            }

          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Countries", "Please enter the Country(ies)."));
             * }
             */

          // regions
          if (newProjectExpectedStudy.getRegions() != null && newProjectExpectedStudy.getRegions().size() > 0) {
            for (String region : newProjectExpectedStudy.getRegions()) {
              if (region != null && !region.trim().isEmpty()) {
                id = this.tryParseLong(region.trim(), fieldErrors, "CreateExpectedStudy", "Region");
                if (id != null) {
                  LocElement country = this.locElementManager.getLocElementByNumericISOCode(id);
                  if (country == null) {
                    fieldErrors.add(
                      new FieldErrorDTO("CreateExpectedStudy", "Regions", region + " is an invalid region UM49 Code"));

                  } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
                    fieldErrors
                      .add(new FieldErrorDTO("CreateExpectedStudy", "Regions", region + " is not a Region UM49 code"));
                  } else {
                    regionsList.add(country);
                  }
                }
              } else {
                fieldErrors
                  .add(new FieldErrorDTO("CreateExpectedStudy", "Regions", "A Region code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Regions", "Please enter the Region(s)."));
             * }
             */

          // subidos
          if (newProjectExpectedStudy.getSrfSubIdoList() != null
            && newProjectExpectedStudy.getSrfSubIdoList().size() > 0) {
            if (newProjectExpectedStudy.getSrfSubIdoList().size() <= 3) {
              hasPrimary = 0;
              for (NewSrfSubIdoDTO subido : newProjectExpectedStudy.getSrfSubIdoList()) {
                if (subido != null && subido.getSubIdo() != null && !subido.getSubIdo().trim().isEmpty()) {
                  SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subido.getSubIdo().trim());
                  if (srfSubIdo == null || !srfSubIdo.isActive()) {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO",
                      subido.getSubIdo() + " is an invalid subIDO Code"));
                  } else {
                    ProjectExpectedStudySubIdo obj = new ProjectExpectedStudySubIdo();
                    obj.setSrfSubIdo(srfSubIdo);
                    obj.setPrimary(subido.getPrimary() != null && subido.getPrimary());
                    hasPrimary = hasPrimary + (obj.getPrimary() == true ? 1 : 0);
                    srfSubIdoList.add(obj);
                  }
                } else {
                  fieldErrors.add(
                    new FieldErrorDTO("CreateExpectedStudy", "SubIDO", "A Sub IDO code can not be null nor empty."));
                }
              }
              if (hasPrimary == 0) {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO",
                  "There should be at least one Sub-IDO marked as primary"));
              }
              if (hasPrimary > 1) {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO",
                  "There can not be more than one Sub-IDO marked as primary"));
              }
            } else {
              fieldErrors
                .add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO", "There can not be more than three SubIDO(s)."));
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO", "Please enter the SubIDO(s)."));
             * }
             */

          // innovation link
          if (newProjectExpectedStudy.getInnovationCodeList() != null
            && newProjectExpectedStudy.getInnovationCodeList().size() > 0) {
            for (String innovation : newProjectExpectedStudy.getInnovationCodeList()) {
              if (innovation != null && !innovation.trim().isEmpty()) {
                id = this.tryParseLong(innovation.trim(), fieldErrors, "CreateExpectedStudy", "Innovation");
                if (id != null) {
                  ProjectInnovation projectInnovation = projectInnovationManager.getProjectInnovationById(id);
                  if (projectInnovation != null) {
                    projectInnovationList.add(projectInnovation);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Innovation",
                      innovation + " is an invalid innovation identifier"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Innovation",
                  "An Innovation code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Innovation",
             * "Please enter the Innovation(s)."));
             * }
             */

          // policies link
          if (newProjectExpectedStudy.getPoliciesCodeList() != null
            && newProjectExpectedStudy.getPoliciesCodeList().size() > 0) {
            for (String policy : newProjectExpectedStudy.getPoliciesCodeList()) {
              if (policy != null && !policy.trim().isEmpty()) {
                id = this.tryParseLong(policy.trim(), fieldErrors, "CreateExpectedStudy", "Policy");
                if (id != null) {
                  ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(id);
                  if (projectPolicy != null) {
                    projectPolicyList.add(projectPolicy);
                  } else {
                    fieldErrors.add(
                      new FieldErrorDTO("CreateExpectedStudy", "Policy", policy + " is an invalid Policy identifier"));
                  }
                }
              } else {
                fieldErrors
                  .add(new FieldErrorDTO("CreateExpectedStudy", "Policy", "A Policy code can not be null nor empty."));
              }
            }
          }

          // institutions
          if (newProjectExpectedStudy.getInstitutionsList() != null
            && newProjectExpectedStudy.getInstitutionsList().size() > 0) {
            for (String institution : newProjectExpectedStudy.getInstitutionsList()) {
              if (institution != null && !institution.trim().isEmpty()) {
                id = this.tryParseLong(institution.trim(), fieldErrors, "CreateExpectedStudy", "Institution");
                if (id != null) {
                  Institution institutionDB = institutionManager.getInstitutionById(id);
                  if (institutionDB != null) {
                    institutionList.add(institutionDB);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Institution",
                      institution + " is an invalid Institution identifier"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Institution",
                  "An Institution code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors
             * .add(new FieldErrorDTO("CreateExpectedStudy", "Institution", "Please enter the Institution(s)."));
             * }
             */

          // milestones
          if (newProjectExpectedStudy.getMilestonesCodeList() != null
            && newProjectExpectedStudy.getMilestonesCodeList().size() > 0) {
            hasPrimary = 0;
            for (NewMilestonesDTO milestones : newProjectExpectedStudy.getMilestonesCodeList()) {
              if (milestones != null && milestones.getMilestone() != null
                && !milestones.getMilestone().trim().isEmpty()) {
                CrpMilestone crpMilestone =
                  crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestone(), phase.getId());
                if (crpMilestone != null && crpMilestone.isActive()) {
                  ProjectExpectedStudyMilestone obj = new ProjectExpectedStudyMilestone();
                  obj.setCrpMilestone(crpMilestone);
                  obj.setPrimary(milestones.getPrimary() != null && milestones.getPrimary());
                  if (obj.getPrimary()) {
                    hasPrimary += 1;
                  }
                  milestoneList.add(obj);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Milestone",
                    milestones.getMilestone() + " is an invalid Milestone identifier"));
                }

              } else {
                fieldErrors.add(
                  new FieldErrorDTO("CreateExpectedStudy", "Milestone", "A Milestone code can not be null nor empty."));
              }
            }

            if (hasPrimary > 1) {
              fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Milestone",
                "There can not be more than one milestone marked as primary"));
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Milestone", "Please enter the Milestone(s)."));
             * }
             */

          // Crosscutting markers
          if (newProjectExpectedStudy.getCrossCuttingMarkers() != null
            && newProjectExpectedStudy.getCrossCuttingMarkers().size() > 0) {
            for (NewCrosscuttingMarkersDTO crosscuttingmark : newProjectExpectedStudy.getCrossCuttingMarkers()) {
              if (crosscuttingmark != null && crosscuttingmark.getCrossCuttingmarker() != null
                && crosscuttingmark.getCrossCuttingmarkerScore() != null
                && !crosscuttingmark.getCrossCuttingmarker().trim().isEmpty()
                && !crosscuttingmark.getCrossCuttingmarkerScore().trim().isEmpty()) {
                id = this.tryParseLong(crosscuttingmark.getCrossCuttingmarker().trim(), fieldErrors,
                  "CreateExpectedStudy", "Crosscuttingmarker");
                if (id != null) {
                  CgiarCrossCuttingMarker cgiarCrossCuttingMarker =
                    cgiarCrossCuttingMarkerManager.getCgiarCrossCuttingMarkerById(id);
                  if (cgiarCrossCuttingMarker == null) {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Crosscuttingmarker",
                      cgiarCrossCuttingMarker + " is an invalid Crosscuttingmarker Code"));
                  } else {
                    id = this.tryParseLong(crosscuttingmark.getCrossCuttingmarkerScore().trim(), fieldErrors,
                      "CreateExpectedStudy", "CrosscuttingmarkerScore");
                    if (id != null) {
                      RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                        repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(id);
                      if (repIndGenderYouthFocusLevel == null) {
                        fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "CrosscuttingmarkerScore",
                          cgiarCrossCuttingMarker + " is an invalid GenderYouthFocusLevel Code"));
                      } else {
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_GENDER) {
                          projectExpectedStudyInfo.setGenderLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeGender(crosscuttingmark.getDescription());
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_YOUTH) {
                          projectExpectedStudyInfo.setYouthLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeYouth(crosscuttingmark.getDescription());
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_CAPDEV) {
                          projectExpectedStudyInfo.setCapdevLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeCapdev(crosscuttingmark.getDescription());
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_CLIMATECHANGE) {
                          projectExpectedStudyInfo.setClimateChangeLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeClimateChange(crosscuttingmark.getDescription());
                        }
                      }
                    }
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "CrosscuttingMarker",
                  "A CrosscuttingMarker or CrosscuttingmarkerScore is invalid."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Crosscuttingmarker",
             * "Please enter the CrosscuttingMarker(s)."));
             * }
             */

          // links
          if (newProjectExpectedStudy.getLinks() != null && !newProjectExpectedStudy.getLinks().isEmpty()) {
            for (String link : newProjectExpectedStudy.getLinks()) {
              if (link != null && !link.trim().isEmpty()) {
                linkList.add(link);
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Link", "A Link can not be null nor empty."));
              }
            }
          }

          // quantifications
          if (newProjectExpectedStudy.getQuantificationList() != null
            && !newProjectExpectedStudy.getQuantificationList().isEmpty()) {
            for (QuantificationDTO quantification : newProjectExpectedStudy.getQuantificationList()) {
              if (quantification.getTargetUnit() != null && quantification.getTargetUnit().length() > 0
                && quantification.getNumber() > 0 && (quantification.getQuantificationType().equalsIgnoreCase("A")
                  || quantification.getQuantificationType().equalsIgnoreCase("B"))) {
                ProjectExpectedStudyQuantification projectExpectedStudyQuantification =
                  new ProjectExpectedStudyQuantification();
                projectExpectedStudyQuantification.setPhase(phase);
                projectExpectedStudyQuantification.setTargetUnit(quantification.getTargetUnit());
                projectExpectedStudyQuantification.setTypeQuantification(quantification.getQuantificationType());
                projectExpectedStudyQuantification.setNumber(quantification.getNumber());
                projectExpectedStudyQuantification.setComments(quantification.getComments());
                ExpectedStudyQuantificationList.add(projectExpectedStudyQuantification);
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Quantification",
                  "You have to fill all fields for quantification"));
              }
            }
          }
          // reference Cited list
          if (newProjectExpectedStudy.getReferenceCitedList() != null
            && !newProjectExpectedStudy.getReferenceCitedList().isEmpty()) {
            for (ReferenceCitedDTO referenceCited : newProjectExpectedStudy.getReferenceCitedList()) {
              ProjectExpectedStudyReference projectExpectedStudyReference = new ProjectExpectedStudyReference();
              projectExpectedStudyReference.setPhase(phase);
              projectExpectedStudyReference.setReference(referenceCited.getReference());
              projectExpectedStudyReference.setLink(referenceCited.getUrl());
              referenceList.add(projectExpectedStudyReference);
            }
          }

          // all validated
          if (fieldErrors.size() == 0 && project != null) {
            projectExpectedStudy.setPhase(phase.getId());
            projectExpectedStudy.setYear(phase.getYear());
            projectExpectedStudy.setProject(project);
            ProjectExpectedStudy projectExpectedStudyDB =
              projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudy);
            if (projectExpectedStudyDB != null) {
              projectExpectedStudyID = projectExpectedStudyDB.getId();
              projectExpectedStudyInfo.setProjectExpectedStudy(projectExpectedStudyDB);
              if (projectPolicyList.size() > 0) {
                projectExpectedStudyInfo.setIsContribution(true);
              }
              if (newProjectExpectedStudy.getMilestonesCodeList() != null
                && newProjectExpectedStudy.getMilestonesCodeList().size() > 0) {
                projectExpectedStudyInfo.setHasMilestones(true);
              }


              if (srfSloIndicatorList.size() > 0) {
                projectExpectedStudyInfo.setIsSrfTarget("targetsOptionYes");
              }
              if (projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(projectExpectedStudyInfo) != null) {
                // save geographicscopes
                for (RepIndGeographicScope repIndGeographicScope : geographicScopeList) {
                  ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope =
                    new ProjectExpectedStudyGeographicScope();
                  projectExpectedStudyGeographicScope.setPhase(phase);
                  projectExpectedStudyGeographicScope.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyGeographicScope.setRepIndGeographicScope(repIndGeographicScope);
                  projectExpectedStudyGeographicScopeManager
                    .saveProjectExpectedStudyGeographicScope(projectExpectedStudyGeographicScope);
                }

                // countries
                for (LocElement country : countriesList) {
                  ProjectExpectedStudyCountry projectExpectedStudyCountry = new ProjectExpectedStudyCountry();
                  projectExpectedStudyCountry.setLocElement(country);
                  projectExpectedStudyCountry.setPhase(phase);
                  projectExpectedStudyCountry.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(projectExpectedStudyCountry);
                }

                // regions
                for (LocElement country : regionsList) {
                  ProjectExpectedStudyRegion projectExpectedStudyRegion = new ProjectExpectedStudyRegion();
                  projectExpectedStudyRegion.setLocElement(country);
                  projectExpectedStudyRegion.setPhase(phase);
                  projectExpectedStudyRegion.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyRegionManager.saveProjectExpectedStudyRegion(projectExpectedStudyRegion);
                }

                // SLO targets
                for (SrfSloIndicator srfSloIndicator : srfSloIndicatorList.stream().distinct()
                  .collect(Collectors.toList())) {
                  ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget = new ProjectExpectedStudySrfTarget();
                  projectExpectedStudySrfTarget.setSrfSloIndicator(srfSloIndicator);
                  projectExpectedStudySrfTarget.setPhase(phase);
                  projectExpectedStudySrfTarget.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudySrfTargetManager.saveProjectExpectedStudySrfTarget(projectExpectedStudySrfTarget);
                }

                // SudIDOs
                for (ProjectExpectedStudySubIdo srfSubIdo : srfSubIdoList) {
                  ProjectExpectedStudySubIdo projectExpectedStudySubIdo = new ProjectExpectedStudySubIdo();
                  projectExpectedStudySubIdo.setPhase(phase);
                  projectExpectedStudySubIdo.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudySubIdo.setSrfSubIdo(srfSubIdo.getSrfSubIdo());
                  projectExpectedStudySubIdo.setPrimary(srfSubIdo.getPrimary());
                  // to do Set as a primary if is necessary
                  projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(projectExpectedStudySubIdo);
                }

                // flagships
                for (CrpProgram crpProgram : flagshipList) {
                  ProjectExpectedStudyFlagship projectExpectedStudyFlagship = new ProjectExpectedStudyFlagship();
                  projectExpectedStudyFlagship.setCrpProgram(crpProgram);
                  projectExpectedStudyFlagship.setPhase(phase);
                  projectExpectedStudyFlagship.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(projectExpectedStudyFlagship);
                }

                // Links
                for (String link : linkList) {
                  ProjectExpectedStudyLink projectExpectedStudyLink = new ProjectExpectedStudyLink();
                  projectExpectedStudyLink.setPhase(phase);
                  projectExpectedStudyLink.setLink(link);
                  projectExpectedStudyLink.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(projectExpectedStudyLink);
                }

                // institutions
                for (Institution institution : institutionList) {
                  ProjectExpectedStudyInstitution projectExpectedStudyInstitution =
                    new ProjectExpectedStudyInstitution();
                  projectExpectedStudyInstitution.setInstitution(institution);
                  projectExpectedStudyInstitution.setPhase(phase);
                  projectExpectedStudyInstitution.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyInstitutionManager
                    .saveProjectExpectedStudyInstitution(projectExpectedStudyInstitution);
                }

                // policies
                for (ProjectPolicy projectPolicy : projectPolicyList) {
                  ProjectExpectedStudyPolicy projectExpectedStudyPolicy = new ProjectExpectedStudyPolicy();
                  projectExpectedStudyPolicy.setProjectPolicy(projectPolicy);
                  projectExpectedStudyPolicy.setPhase(phase);
                  projectExpectedStudyPolicy.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(projectExpectedStudyPolicy);
                }

                // innovations
                for (ProjectInnovation projectInnovation : projectInnovationList) {
                  ProjectExpectedStudyInnovation projectExpectedStudyInnovation = new ProjectExpectedStudyInnovation();
                  projectExpectedStudyInnovation.setPhase(phase);
                  projectExpectedStudyInnovation.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyInnovation.setProjectInnovation(projectInnovation);
                  projectExpectedStudyInnovationManager
                    .saveProjectExpectedStudyInnovation(projectExpectedStudyInnovation);
                }

                // milestones
                for (ProjectExpectedStudyMilestone projectExpectedStudyMilestones : milestoneList) {
                  ProjectExpectedStudyMilestone expectedStudyMilestone = new ProjectExpectedStudyMilestone();
                  expectedStudyMilestone.setCrpMilestone(projectExpectedStudyMilestones.getCrpMilestone());
                  expectedStudyMilestone.setPhase(phase);
                  expectedStudyMilestone.setPrimary(projectExpectedStudyMilestones.getPrimary());
                  expectedStudyMilestone.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(expectedStudyMilestone);
                }

                // crps
                for (GlobalUnit globalUnit : crpContributing) {
                  ProjectExpectedStudyCrp projectExpectedStudyCrp = new ProjectExpectedStudyCrp();
                  projectExpectedStudyCrp.setGlobalUnit(globalUnit);
                  projectExpectedStudyCrp.setPhase(phase);
                  projectExpectedStudyCrp.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(projectExpectedStudyCrp);
                }

                // quantification
                for (ProjectExpectedStudyQuantification projectExpectedStudyQuantification : ExpectedStudyQuantificationList) {
                  ProjectExpectedStudyQuantification projectExpectedStudyQuantificationDB =
                    new ProjectExpectedStudyQuantification();
                  projectExpectedStudyQuantificationDB.setNumber(projectExpectedStudyQuantification.getNumber());
                  projectExpectedStudyQuantificationDB.setPhase(phase);
                  projectExpectedStudyQuantificationDB.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyQuantificationDB
                    .setTargetUnit(projectExpectedStudyQuantification.getTargetUnit());
                  projectExpectedStudyQuantificationDB
                    .setTypeQuantification(projectExpectedStudyQuantification.getTypeQuantification());
                  projectExpectedStudyQuantificationDB.setComments(projectExpectedStudyQuantification.getComments());
                  projectExpectedStudyQuantificationManager
                    .saveProjectExpectedStudyQuantification(projectExpectedStudyQuantificationDB);
                }

                // references cited
                for (ProjectExpectedStudyReference projectExpectedStudyReference : referenceList) {
                  ProjectExpectedStudyReference projectExpectedStudyReferenceDB = new ProjectExpectedStudyReference();
                  projectExpectedStudyReferenceDB.setLink(projectExpectedStudyReference.getLink());
                  projectExpectedStudyReferenceDB.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyReferenceDB.setPhase(phase);
                  projectExpectedStudyReferenceDB.setReference(projectExpectedStudyReference.getReference());
                  projectExpectedStudyReferenceManager
                    .saveProjectExpectedStudyReference(projectExpectedStudyReferenceDB);
                }


                // verify if was included in synthesis PMU
                LiaisonInstitution liaisonInstitution = this.liaisonInstitutionManager
                  .findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
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
                    final Long study = projectExpectedStudyID;
                    ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy =
                      reportSynthesisFlagshipProgress.getReportSynthesisFlagshipProgressStudies().stream()
                        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == study)
                        .findFirst().orElse(null);
                    if (reportSynthesisFlagshipProgressStudy != null && existing) {
                      reportSynthesisFlagshipProgressStudy = reportSynthesisFlagshipProgressStudyManager
                        .getReportSynthesisFlagshipProgressStudyById(reportSynthesisFlagshipProgressStudy.getId());
                      reportSynthesisFlagshipProgressStudy.setActive(false);
                      reportSynthesisFlagshipProgressStudy = reportSynthesisFlagshipProgressStudyManager
                        .saveReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudy);
                    } else {
                      reportSynthesisFlagshipProgressStudy = new ReportSynthesisFlagshipProgressStudy();
                      reportSynthesisFlagshipProgressStudy.setCreatedBy(user);
                      reportSynthesisFlagshipProgressStudy.setProjectExpectedStudy(projectExpectedStudy);
                      reportSynthesisFlagshipProgressStudy
                        .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
                      reportSynthesisFlagshipProgressStudy = reportSynthesisFlagshipProgressStudyManager
                        .saveReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudy);
                      reportSynthesisFlagshipProgressStudy.setActive(false);
                      reportSynthesisFlagshipProgressStudyManager
                        .saveReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudy);
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "ProjectExpectedStudyInfoEntity",
          "Please enter a Project Expected Study Info"));
      }
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      for (FieldErrorDTO errors : fieldErrors) {
        System.out.println("FieldErrorDTO " + errors.getMessage());
      }

      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return projectExpectedStudyID;
  }

  public ResponseEntity<ProjectExpectedStudyDTO> deleteExpectedStudyById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteExpectedStudy", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteExpectedStudy", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), CGIARentityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteExpectedStudy", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().orElse(null);
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("deleteExpectedStudy", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    ProjectExpectedStudy projectExpectedStudy = this.projectExpectedStudyManager.getProjectExpectedStudyById(id);

    if (projectExpectedStudy != null) {
      ProjectExpectedStudyInfo projectExpectedStudyInfo = projectExpectedStudy.getProjectExpectedStudyInfo(phase);
      if (projectExpectedStudyInfo != null) {
        // Flagship
        List<ProjectExpectedStudyFlagship> projectExpectedStudyFlagshipList =
          projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setFlagships(projectExpectedStudyFlagshipList);
        // SubIDOs
        List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdoList =
          projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setSubIdos(projectExpectedStudySubIdoList);
        if (projectExpectedStudyInfo.getIsSrfTarget() != null
          && projectExpectedStudyInfo.getIsSrfTarget().equals("Yes")) {
          // SrfSlo
          List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
            projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
          projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
        } else {
          List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
            new ArrayList<ProjectExpectedStudySrfTarget>();
          projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
        }

        // GeographicScope
        List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopeList =
          projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setGeographicScopes(projectExpectedStudyGeographicScopeList);
        // Institutions
        List<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutionList =
          projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setInstitutions(projectExpectedStudyInstitutionList);
        // CRPs contribution
        List<ProjectExpectedStudyCrp> projectExpectedStudyCrpList = projectExpectedStudy.getProjectExpectedStudyCrps()
          .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setCrps(projectExpectedStudyCrpList);
        // Regions
        List<ProjectExpectedStudyRegion> projectExpectedStudyRegionList = projectExpectedStudyRegionManager
          .findAll().stream().filter(c -> c.isActive()
            && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
          .collect(Collectors.toList());
        projectExpectedStudy.setStudyRegions(projectExpectedStudyRegionList);
        // Countries
        List<ProjectExpectedStudyCountry> projectExpectedStudyCountryList = projectExpectedStudyCountryManager
          .findAll().stream().filter(c -> c.isActive()
            && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
          .collect(Collectors.toList());
        projectExpectedStudy.setCountries(projectExpectedStudyCountryList);
        // Quantification
        List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantificationList =
          projectExpectedStudyQuantificationManager
            .findAll().stream().filter(c -> c.isActive()
              && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
            .collect(Collectors.toList());
        projectExpectedStudy.setQuantifications(projectExpectedStudyQuantificationList);
        // Links
        List<ProjectExpectedStudyLink> projectExpectedStudyLinkList =
          projectExpectedStudy.getProjectExpectedStudyLinks().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setLinks(projectExpectedStudyLinkList);

        // innovations
        List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovationList =
          projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setInnovations(projectExpectedStudyInnovationList);
        if (projectExpectedStudyInfo.getIsContribution() != null && projectExpectedStudyInfo.getIsContribution()) {
          // Policies
          List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList =
            projectExpectedStudy.getProjectExpectedStudyPolicies().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
          projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
        } else {
          List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList = new ArrayList<ProjectExpectedStudyPolicy>();
          projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
        }

        List<ProjectExpectedStudyMilestone> projectExpectedStudyMilestoneList = projectExpectedStudy
          .getProjectExpectedStudyMilestones().stream().filter(c -> c != null
            && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
          .collect(Collectors.toList());
        projectExpectedStudy.setMilestones(projectExpectedStudyMilestoneList);

        projectExpectedStudyManager.deleteProjectExpectedStudy(projectExpectedStudy.getId());
      } // ?
    } else {
      fieldErrors.add(new FieldErrorDTO("deleteExpectedStudy", "ProjectExpectedStudyEntity",
        id + " is an invalid Project Expected Study Code"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(projectExpectedStudy)
      .map(this.projectExpectedStudyMapper::projectExpectedStudyToProjectExpectedStudyDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<ProjectExpectedStudiesARDTO> findAllExpectedStudyByGlobalUnit(String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {

    List<ProjectExpectedStudiesARDTO> projectExpectedStudyList = new ArrayList<ProjectExpectedStudiesARDTO>();
    List<ProjectExpectedStudy> expectedStudyList = new ArrayList<ProjectExpectedStudy>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && c.getYear() == repoYear
        && c.getName().equalsIgnoreCase(repoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("findExpectedStudy", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }
    if (fieldErrors.isEmpty()) {
      List<ProjectExpectedStudyInfo> projectExpectedStudyInfoList = phase.getProjectExpectedStudyInfos().stream()
        .filter(c -> c.getStudyType() != null && c.getStudyType().getId() == 1 && c.getYear() == phase.getYear())
        .collect(Collectors.toList());

      for (ProjectExpectedStudyInfo projectExpectedStudyInfo : projectExpectedStudyInfoList) {
        ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager
          .getProjectExpectedStudyById(projectExpectedStudyInfo.getProjectExpectedStudy().getId());
        if (projectExpectedStudy.isActive()
          && projectExpectedStudyManager.isStudyExcluded(projectExpectedStudy.getId(), phase.getId(), new Long(1))) {
          projectExpectedStudy = this.getExpectedStudyInfo(projectExpectedStudy, phase);
          expectedStudyList.add(projectExpectedStudy);
        }
      }
    }
    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    projectExpectedStudyList = expectedStudyList.stream()
      .map(oicr -> this.projectExpectedStudyMapper.projectExpectedStudyToProjectExpectedStudyARDTO(oicr))
      .collect(Collectors.toList());

    return projectExpectedStudyList;
  }

  public ResponseEntity<ProjectExpectedStudyDTO> findExpectedStudyById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && c.getYear() == repoYear
        && c.getName().equalsIgnoreCase(repoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("findExpectedStudy", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(id.longValue());

    if (projectExpectedStudy != null && fieldErrors.isEmpty()) {
      ProjectExpectedStudyInfo projectExpectedStudyInfo = projectExpectedStudy.getProjectExpectedStudyInfo(phase);
      // Flagship
      List<ProjectExpectedStudyFlagship> projectExpectedStudyFlagshipList =
        projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setFlagships(projectExpectedStudyFlagshipList);
      // SubIDOs
      List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdoList =
        projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setSubIdos(projectExpectedStudySubIdoList);
      if (projectExpectedStudyInfo.getIsSrfTarget() != null
        && projectExpectedStudyInfo.getIsSrfTarget().equals("targetsOptionYes")) {
        // SrfSlo
        List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
          projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
      } else {
        List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
          new ArrayList<ProjectExpectedStudySrfTarget>();
        projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
      }

      // GeographicScope
      List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopeList =
        projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setGeographicScopes(projectExpectedStudyGeographicScopeList);
      // Institutions
      Comparator<ProjectExpectedStudyInstitution> compareByNameInstitution = (ProjectExpectedStudyInstitution o1,
        ProjectExpectedStudyInstitution o2) -> o1.getInstitution().getName().compareTo(o2.getInstitution().getName());
      List<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutionList =
        projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      Collections.sort(projectExpectedStudyInstitutionList, compareByNameInstitution);
      projectExpectedStudy.setInstitutions(projectExpectedStudyInstitutionList);
      // CRPs contribution
      List<ProjectExpectedStudyCrp> projectExpectedStudyCrpList = projectExpectedStudy.getProjectExpectedStudyCrps()
        .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setCrps(projectExpectedStudyCrpList);
      // Regions
      List<ProjectExpectedStudyRegion> projectExpectedStudyRegionList = projectExpectedStudyRegionManager
        .findAll().stream().filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
      projectExpectedStudy.setStudyRegions(projectExpectedStudyRegionList);
      // Countries
      List<ProjectExpectedStudyCountry> projectExpectedStudyCountryList = projectExpectedStudyCountryManager
        .findAll().stream().filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
      projectExpectedStudy.setCountries(projectExpectedStudyCountryList);
      // Quantification
      List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantificationList =
        projectExpectedStudyQuantificationManager
          .findAll().stream().filter(c -> c.isActive()
            && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
          .collect(Collectors.toList());
      projectExpectedStudy.setQuantifications(projectExpectedStudyQuantificationList);
      // Links
      List<ProjectExpectedStudyLink> projectExpectedStudyLinkList = projectExpectedStudy.getProjectExpectedStudyLinks()
        .stream().filter(c -> c.isActive() && c.getPhase().equals(phase))
        .sorted(Comparator.comparing(ProjectExpectedStudyLink::getLink)).collect(Collectors.toList());
      projectExpectedStudy.setLinks(projectExpectedStudyLinkList);

      // innovations
      List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovationList =
        projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setInnovations(projectExpectedStudyInnovationList);
      if (projectExpectedStudyInfo.getIsContribution() != null && projectExpectedStudyInfo.getIsContribution()) {
        // Policies
        List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList =
          projectExpectedStudy.getProjectExpectedStudyPolicies().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
      } else {
        List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList = new ArrayList<ProjectExpectedStudyPolicy>();
        projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
      }

      List<ProjectExpectedStudyMilestone> projectExpectedStudyMilestoneList = projectExpectedStudy
        .getProjectExpectedStudyMilestones().stream().filter(c -> c != null
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
      projectExpectedStudy.setMilestones(projectExpectedStudyMilestoneList);

      List<ProjectExpectedStudyReference> projectExpectedStudyReferenceList = projectExpectedStudy
        .getProjectExpectedStudyReferences().stream().filter(c -> c != null
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
      projectExpectedStudy.setReferences(projectExpectedStudyReferenceList);
    } else {
      fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "ProjectExpectedStudyEntity",
        id + " is an invalid Project Expected Study Code"));
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(projectExpectedStudy)
      .map(this.projectExpectedStudyMapper::projectExpectedStudyToProjectExpectedStudyDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  private ProjectExpectedStudy getExpectedStudyInfo(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
    ProjectExpectedStudyInfo projectExpectedStudyInfo = projectExpectedStudy.getProjectExpectedStudyInfo(phase);
    // Flagship
    List<ProjectExpectedStudyFlagship> projectExpectedStudyFlagshipList =
      projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
    projectExpectedStudy.setFlagships(projectExpectedStudyFlagshipList);
    // SubIDOs
    List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdoList =
      projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
    projectExpectedStudy.setSubIdos(projectExpectedStudySubIdoList);
    if (projectExpectedStudyInfo.getIsSrfTarget() != null
      && projectExpectedStudyInfo.getIsSrfTarget().equals("targetsOptionYes")) {
      // SrfSlo
      List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
        projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
    } else {
      List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
        new ArrayList<ProjectExpectedStudySrfTarget>();
      projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
    }

    // GeographicScope
    List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopeList =
      projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
    projectExpectedStudy.setGeographicScopes(projectExpectedStudyGeographicScopeList);
    // Institutions
    Comparator<ProjectExpectedStudyInstitution> compareByNameInstitution = (ProjectExpectedStudyInstitution o1,
      ProjectExpectedStudyInstitution o2) -> o1.getInstitution().getName().compareTo(o2.getInstitution().getName());
    List<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutionList =
      projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
    Collections.sort(projectExpectedStudyInstitutionList, compareByNameInstitution);
    projectExpectedStudy.setInstitutions(projectExpectedStudyInstitutionList);
    // CRPs contribution
    List<ProjectExpectedStudyCrp> projectExpectedStudyCrpList = projectExpectedStudy.getProjectExpectedStudyCrps()
      .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
    projectExpectedStudy.setCrps(projectExpectedStudyCrpList);
    // Regions
    List<ProjectExpectedStudyRegion> projectExpectedStudyRegionList = projectExpectedStudyRegionManager
      .findAll().stream().filter(c -> c.isActive()
        && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
      .collect(Collectors.toList());
    projectExpectedStudy.setStudyRegions(projectExpectedStudyRegionList);
    // Countries
    List<ProjectExpectedStudyCountry> projectExpectedStudyCountryList = projectExpectedStudyCountryManager
      .findAll().stream().filter(c -> c.isActive()
        && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
      .collect(Collectors.toList());
    projectExpectedStudy.setCountries(projectExpectedStudyCountryList);
    // Quantification
    List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantificationList =
      projectExpectedStudyQuantificationManager
        .findAll().stream().filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
    projectExpectedStudy.setQuantifications(projectExpectedStudyQuantificationList);
    // Links
    List<ProjectExpectedStudyLink> projectExpectedStudyLinkList = projectExpectedStudy.getProjectExpectedStudyLinks()
      .stream().filter(c -> c.isActive() && c.getPhase().equals(phase))
      .sorted(Comparator.comparing(ProjectExpectedStudyLink::getLink)).collect(Collectors.toList());
    projectExpectedStudy.setLinks(projectExpectedStudyLinkList);

    // innovations
    List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovationList =
      projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
    projectExpectedStudy.setInnovations(projectExpectedStudyInnovationList);
    if (projectExpectedStudyInfo.getIsContribution() != null && projectExpectedStudyInfo.getIsContribution()) {
      // Policies
      List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList =
        projectExpectedStudy.getProjectExpectedStudyPolicies().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
    } else {
      List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList = new ArrayList<ProjectExpectedStudyPolicy>();
      projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
    }

    List<ProjectExpectedStudyMilestone> projectExpectedStudyMilestoneList = projectExpectedStudy
      .getProjectExpectedStudyMilestones().stream().filter(c -> c != null
        && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
      .collect(Collectors.toList());
    projectExpectedStudy.setMilestones(projectExpectedStudyMilestoneList);
    // references
    List<ProjectExpectedStudyReference> projectExpectedStudyReferenceList = projectExpectedStudy
      .getProjectExpectedStudyReferences().stream().filter(c -> c != null
        && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
      .collect(Collectors.toList());
    projectExpectedStudy.setReferences(projectExpectedStudyReferenceList);
    return projectExpectedStudy;
  }

  public Long putExpectedStudyById(Long idExpectedStudy, NewProjectExpectedStudyDTO newProjectExpectedStudy,
    String CGIARentityAcronym, User user) {
    Long expectedStudyID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Phase phase = null;
    Long id = null;

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newProjectExpectedStudy.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "PhaseEntity", "Phase must not be null"));
    } else {
      if (newProjectExpectedStudy.getPhase().getName() == null
        || newProjectExpectedStudy.getPhase().getName().trim().isEmpty()
        || newProjectExpectedStudy.getPhase().getYear() == null
        // DANGER! Magic number ahead
        || newProjectExpectedStudy.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
            && p.getYear() == newProjectExpectedStudy.getPhase().getYear()
            && p.getName().equalsIgnoreCase(newProjectExpectedStudy.getPhase().getName()))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "phase", newProjectExpectedStudy.getPhase().getName()
            + ' ' + newProjectExpectedStudy.getPhase().getYear() + " is an invalid phase"));
        }

      }

    }

    Project project = null;
    if (newProjectExpectedStudy.getProject() != null) {
      project = projectManager.getProjectById(newProjectExpectedStudy.getProject());
      if (project == null) {
        fieldErrors.add(
          new FieldErrorDTO("putExpectedStudy", "Project", newProjectExpectedStudy.getProject() + " is an project ID"));
      } else {
        GlobalUnitProject crpProject = globalUnitProjectManager
          .findByProjectAndGlobalUnitId(newProjectExpectedStudy.getProject(), globalUnitEntity.getId());
        if (crpProject == null) {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Project",
            newProjectExpectedStudy.getProject() + " is an invalid project ID"));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Project", "A projectID can not be null"));
    }

    ProjectExpectedStudy projectExpectedStudy =
      this.projectExpectedStudyManager.getProjectExpectedStudyById(idExpectedStudy);
    if (projectExpectedStudy == null) {
      fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "ProjectExpectedStudy",
        idExpectedStudy + " is an invalid ProjectExpectedStudy code"));
    } else {
      if (projectExpectedStudy.getProject().getId().longValue() != newProjectExpectedStudy.getProject()) {
        fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Project",
          newProjectExpectedStudy.getProject() + " is an invalid project ID"));
      }
    }
    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "phase", newProjectExpectedStudy.getPhase().getName() + ' '
        + newProjectExpectedStudy.getPhase().getYear() + " is an closed phase"));
    }

    if (fieldErrors.size() == 0) {
      expectedStudyID = projectExpectedStudy.getId();

      List<RepIndGeographicScope> geographicScopeList = new ArrayList<RepIndGeographicScope>();
      List<SrfSloIndicator> srfSloIndicatorList = new ArrayList<SrfSloIndicator>();
      List<GlobalUnit> crpContributing = new ArrayList<GlobalUnit>();
      List<CrpProgram> flagshipList = new ArrayList<>();
      List<LocElement> countriesList = new ArrayList<>();
      List<LocElement> regionsList = new ArrayList<>();
      List<ProjectExpectedStudySubIdo> srfSubIdoList = new ArrayList<>();
      List<ProjectInnovation> projectInnovationList = new ArrayList<ProjectInnovation>();
      List<ProjectPolicy> projectPolicyList = new ArrayList<ProjectPolicy>();
      List<Institution> institutionList = new ArrayList<Institution>();
      List<String> linkList = new ArrayList<String>();
      List<ProjectExpectedStudyMilestone> milestoneList = new ArrayList<ProjectExpectedStudyMilestone>();
      List<ProjectExpectedStudyQuantification> expectedStudyQuantificationList =
        new ArrayList<ProjectExpectedStudyQuantification>();
      List<ProjectExpectedStudyReference> referenceList = new ArrayList<ProjectExpectedStudyReference>();
      int hasPrimary = 0;
      int wordCount = -1;

      if (newProjectExpectedStudy.getProjectExpectedStudyInfo() != null) {
        // update expected Study info
        ProjectExpectedStudyInfo projectExpectedStudyInfo = projectExpectedStudy.getProjectExpectedStudyInfo(phase);
        if (projectExpectedStudyInfo == null) {
          projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
        }

        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getTitle() != null
          && !newProjectExpectedStudy.getProjectExpectedStudyInfo().getTitle().trim().isEmpty()) {
          projectExpectedStudyInfo.setTitle(newProjectExpectedStudy.getProjectExpectedStudyInfo().getTitle());
        } else {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Title", "Please insert a valid title"));
        }

        // shortOutcome
        wordCount = this.countWords(newProjectExpectedStudy.getProjectExpectedStudyInfo().getOutcomeImpactStatement());
        if (wordCount > 80) {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "outcomeImpactStatement",
            "Short outcome/impact statement excedes the maximum number of words (80 words)"));
        } else {
          projectExpectedStudyInfo.setOutcomeImpactStatement(
            newProjectExpectedStudy.getProjectExpectedStudyInfo().getOutcomeImpactStatement());
        }

        // communicationsMaterial
        wordCount = this.countWords(newProjectExpectedStudy.getProjectExpectedStudyInfo().getComunicationsMaterial());
        if (wordCount > 400) {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "comunicationsMaterial",
            "Outcome story for communications use excedes the maximum number of words (400 words)"));
        } else {
          projectExpectedStudyInfo
            .setComunicationsMaterial(newProjectExpectedStudy.getProjectExpectedStudyInfo().getComunicationsMaterial());
        }

        // cgiar Innovations
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getCgiarInnovation() != null
          && !newProjectExpectedStudy.getProjectExpectedStudyInfo().getCgiarInnovation().isEmpty()) {
          projectExpectedStudyInfo
            .setCgiarInnovation(newProjectExpectedStudy.getProjectExpectedStudyInfo().getCgiarInnovation());
        }

        // elaborationOutcomeImpactStatement
        wordCount =
          this.countWords(newProjectExpectedStudy.getProjectExpectedStudyInfo().getElaborationOutcomeImpactStatement());
        if (wordCount > 400) {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudy", "elaborationOutcomeImpactStatement",
            "Elaboration of Outcome/Impact Statement excedes the maximum number of words (400 words)"));
        } else {
          projectExpectedStudyInfo.setElaborationOutcomeImpactStatement(
            newProjectExpectedStudy.getProjectExpectedStudyInfo().getElaborationOutcomeImpactStatement());
        }

        // referencesText
        projectExpectedStudyInfo
          .setReferencesText(newProjectExpectedStudy.getProjectExpectedStudyInfo().getReferencesText());

        // DANGER! Magic number ahead
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getYear() > 1900) {
          projectExpectedStudyInfo.setYear(newProjectExpectedStudy.getProjectExpectedStudyInfo().getYear());
        } else {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Year", "Please insert a valid year"));
        }
        projectExpectedStudyInfo.setPhase(phase);
        projectExpectedStudyInfo.setContacts(newProjectExpectedStudy.getProjectExpectedStudyInfo().getContacts());

        StudyType studyType = null;
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null) {
          studyType =
            studyTypeManager.getStudyTypeById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType());
          if (studyType != null) {
            // DANGER! Magic number ahead
            if (studyType.getId() != 1) {
              fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Study Type",
                "Study type with id " + newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType()
                  + " does not correspond with an Outcome Impact Case Report"));
            } else {
              projectExpectedStudyInfo.setStudyType(studyType);
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Study Type",
              newProjectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() + " is an invalid study code"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Study", "study identifier can not be null nor empty"));
        }

        // null check not necessary because status is a long (primitive type)
        GeneralStatus generalStatus =
          generalStatusManager.getGeneralStatusById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getStatus());
        if (generalStatus != null) {
          projectExpectedStudyInfo.setStatus(generalStatus);
        } else {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Status",
            newProjectExpectedStudy.getProjectExpectedStudyInfo().getStatus() + " is an invalid status code"));
        }

        /*
         * EvidenceTag evidenceTag = null;
         * if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getTag() != null) {
         * evidenceTag =
         * evidenceTagManager.getEvidenceTagById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getTag());
         * if (evidenceTag != null) {
         * projectExpectedStudyInfo.setEvidenceTag(evidenceTag);
         * } else {
         * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Evidence Tag",
         * newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange()
         * + " is an invalid Evidence Tag code"));
         * }
         * } else {
         * fieldErrors.add(
         * new FieldErrorDTO("putExpectedStudy", "EvidenceTag", "evidence tag identifier can not be null nor empty"));
         * }
         */

        RepIndStageStudy repIndStageStudy = null;
        if (newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange() != null) {
          repIndStageStudy = repIndStageStudyManager
            .getRepIndStageStudyById(newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange());
          if (repIndStageStudy != null) {
            projectExpectedStudyInfo.setRepIndStageStudy(repIndStageStudy);
          } else {
            fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "MaturityOfChange",
              newProjectExpectedStudy.getProjectExpectedStudyInfo().getMaturityOfChange()
                + " is an invalid Level of maturity of change reported code"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "MaturityOfChange",
            "maturity of change identifier can not be null nor empty"));
        }

        if (fieldErrors.size() == 0) {
          projectExpectedStudy.setPhase(phase.getId());
          projectExpectedStudy.setProjectExpectedStudyInfo(projectExpectedStudyInfo);

          // update many to many relationships
          // geographic
          if (newProjectExpectedStudy.getGeographicScopes() != null
            && newProjectExpectedStudy.getGeographicScopes().size() > 0) {
            for (String geographicscope : newProjectExpectedStudy.getGeographicScopes()) {
              if (geographicscope != null && !geographicscope.trim().isEmpty()) {
                id = this.tryParseLong(geographicscope.trim(), fieldErrors, "putExpectedStudy", "GeographicScope");
                if (id != null) {
                  RepIndGeographicScope repIndGeographicScope =
                    repIndGeographicScopeManager.getRepIndGeographicScopeById(id);
                  if (repIndGeographicScope != null) {
                    geographicScopeList.add(repIndGeographicScope);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "GeographicScope",
                      id + " is an invalid geographicScope identifier"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "GeographicScope",
                  "geographicScope identifier can not be null nor empty"));
              }
            }
          }
          projectExpectedStudyInfo.setScopeComments(newProjectExpectedStudy.getGeographicScopeComment());
          /*
           * else {
           * fieldErrors
           * .add(new FieldErrorDTO("putExpectedStudy", "GeographicScope", "Please enter the Geographic Scope(s)."));
           * }
           */

          // Slo Target
          if (newProjectExpectedStudy.getSrfSloTargetList() != null
            && newProjectExpectedStudy.getSrfSloTargetList().size() > 0) {
            for (String sloTarget : newProjectExpectedStudy.getSrfSloTargetList()) {
              if (sloTarget != null && !sloTarget.trim().isEmpty()) {
                SrfSloIndicatorTarget srfSloIndicatorTarget =
                  srfSloIndicatorTargetManager.findbyTargetIndicatorCode(sloTarget);
                if (srfSloIndicatorTarget != null) {
                  SrfSloIndicator srfSloIndicator =
                    srfSloIndicatorManager.getSrfSloIndicatorById(srfSloIndicatorTarget.getSrfSloIndicator().getId());
                  srfSloIndicatorList.add(srfSloIndicator);
                } else {
                  fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "SrfSloIndicatorTarget ",
                    sloTarget + " is an invalid SLOIndicatorTarget identifier"));
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "SrfSloIndicatorTarget",
                  "sloTarget identifier can not be null nor empty"));
              }
            }
          }

          // crps
          if (newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO() != null
            && newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO().size() > 0) {
            for (String crps : newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO()) {
              if (crps != null && !crps.trim().isEmpty()) {
                GlobalUnit globalUnit = globalUnitManager.findGlobalUnitBySMOCode(crps.trim());
                if (globalUnit != null) {
                  crpContributing.add(globalUnit);
                } else {
                  fieldErrors
                    .add(new FieldErrorDTO("putExpectedStudy", "GlobalUnit ", crps + " is an invalid CRP identifier"));
                }
              } else {
                fieldErrors.add(
                  new FieldErrorDTO("putExpectedStudy", "GlobalUnit", "A GlobalUnit code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "GlobalUnit", "Please enter the CRP(s)."));
             * }
             */

          // flagship
          if (newProjectExpectedStudy.getFlagshipsList() != null
            && newProjectExpectedStudy.getFlagshipsList().size() > 0) {
            for (String flagship : newProjectExpectedStudy.getFlagshipsList()) {
              if (flagship != null && !flagship.trim().isEmpty()) {
                id = this.tryParseLong(flagship.trim(), fieldErrors, "putExpectedStudy", "CrpProgram");
                if (id != null) {
                  CrpProgram crpProgram = crpProgramManager.getCrpProgramBySmoCode(flagship.trim());
                  if (crpProgram != null) {
                    flagshipList.add(crpProgram);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Flagship/Module",
                      flagship + " is an invalid Flagship/Module code"));
                  }
                }
              } else {
                fieldErrors.add(
                  new FieldErrorDTO("putExpectedStudy", "CrpProgram", "A CrpProgram code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "CrpProgram", "Please enter the Flagship(s)."));
             * }
             */

          // countries
          if (newProjectExpectedStudy.getCountries() != null && newProjectExpectedStudy.getCountries().size() > 0) {
            for (String countries : newProjectExpectedStudy.getCountries()) {
              if (countries != null && !countries.trim().isEmpty()) {
                id = this.tryParseLong(countries.trim(), fieldErrors, "putExpectedStudy", "Country");
                if (id != null) {
                  LocElement country = this.locElementManager.getLocElementByNumericISOCode(id);
                  if (country == null) {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Countries",
                      countries + " is an invalid country ISO Code"));

                  } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
                    fieldErrors.add(
                      new FieldErrorDTO("putExpectedStudy", "Countries", countries + " is not a Country ISO code"));
                  } else {
                    countriesList.add(country);
                  }
                }
              } else {
                fieldErrors
                  .add(new FieldErrorDTO("putExpectedStudy", "Countries", "A Country code can not be null nor empty."));
              }
            }
            // verification for single or multiple countries
            if (countriesList.size() == 1) {
              geographicScopeList
                .removeIf(g -> g.getId() != null && (g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_MULTINATIONAL
                  || g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_NATIONAL));
              // should not cause exception
              geographicScopeList.add(repIndGeographicScopeManager
                .getRepIndGeographicScopeById(APConstants.REP_IND_GEOGRAPHIC_SCOPE_NATIONAL));
            } else if (countriesList.size() > 1) {
              geographicScopeList
                .removeIf(g -> g.getId() != null && (g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_NATIONAL
                  || g.getId() == APConstants.REP_IND_GEOGRAPHIC_SCOPE_MULTINATIONAL));
              // should not cause exception
              geographicScopeList.add(repIndGeographicScopeManager
                .getRepIndGeographicScopeById(APConstants.REP_IND_GEOGRAPHIC_SCOPE_MULTINATIONAL));
            }

          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Countries", "Please enter the Country(ies)."));
             * }
             */

          // regions
          if (newProjectExpectedStudy.getRegions() != null && newProjectExpectedStudy.getRegions().size() > 0) {
            for (String region : newProjectExpectedStudy.getRegions()) {
              if (region != null && !region.trim().isEmpty()) {
                id = this.tryParseLong(region.trim(), fieldErrors, "putExpectedStudy", "Region");
                if (id != null) {
                  LocElement country = this.locElementManager.getLocElementByNumericISOCode(id);
                  if (country == null) {
                    fieldErrors.add(
                      new FieldErrorDTO("putExpectedStudy", "Regions", region + " is an invalid region UM49 Code"));

                  } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
                    fieldErrors
                      .add(new FieldErrorDTO("putExpectedStudy", "Regions", region + " is not a Region UM49 code"));
                  } else {
                    regionsList.add(country);
                  }
                }
              } else {
                fieldErrors
                  .add(new FieldErrorDTO("putExpectedStudy", "Regions", "A Region code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Regions", "Please enter the Region(s)."));
             * }
             */

          // subidos
          if (newProjectExpectedStudy.getSrfSubIdoList() != null
            && newProjectExpectedStudy.getSrfSubIdoList().size() > 0) {
            if (newProjectExpectedStudy.getSrfSubIdoList().size() <= 3) {
              hasPrimary = 0;
              for (NewSrfSubIdoDTO subido : newProjectExpectedStudy.getSrfSubIdoList()) {
                if (subido != null && subido.getSubIdo() != null && !subido.getSubIdo().trim().isEmpty()) {
                  SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subido.getSubIdo().trim());
                  if (srfSubIdo == null || !srfSubIdo.isActive()) {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "SubIDO",
                      subido.getSubIdo() + " is an invalid subIDO Code"));
                  } else {
                    ProjectExpectedStudySubIdo obj = new ProjectExpectedStudySubIdo();
                    obj.setSrfSubIdo(srfSubIdo);
                    obj.setPrimary(subido.getPrimary() != null && subido.getPrimary());
                    hasPrimary = hasPrimary + (obj.getPrimary() == true ? 1 : 0);
                    srfSubIdoList.add(obj);
                  }
                } else {
                  fieldErrors
                    .add(new FieldErrorDTO("putExpectedStudy", "SubIDO", "A Sub IDO code can not be null nor empty."));
                }
              }
              if (hasPrimary == 0) {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "SubIDO",
                  "There should be at least one Sub-IDO marked as primary"));
              }
              if (hasPrimary > 1) {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "SubIDO",
                  "There can not be more than one Sub-IDO marked as primary"));
              }
            } else {
              fieldErrors
                .add(new FieldErrorDTO("putExpectedStudy", "SubIDO", "There can not be more than three SubIDO(s)."));
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO", "Please enter the SubIDO(s)."));
             * }
             */

          // innovation link
          if (newProjectExpectedStudy.getInnovationCodeList() != null
            && newProjectExpectedStudy.getInnovationCodeList().size() > 0) {
            for (String innovation : newProjectExpectedStudy.getInnovationCodeList()) {
              if (innovation != null && !innovation.trim().isEmpty()) {
                id = this.tryParseLong(innovation.trim(), fieldErrors, "putExpectedStudy", "Innovation");
                if (id != null) {
                  ProjectInnovation projectInnovation = projectInnovationManager.getProjectInnovationById(id);
                  if (projectInnovation != null) {
                    projectInnovationList.add(projectInnovation);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Innovation",
                      innovation + " is an invalid innovation identifier"));
                  }
                }
              } else {
                fieldErrors.add(
                  new FieldErrorDTO("putExpectedStudy", "Innovation", "An Innovation code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Innovation", "Please enter the Innovation(s)."));
             * }
             */

          // policies link
          if (newProjectExpectedStudy.getPoliciesCodeList() != null
            && newProjectExpectedStudy.getPoliciesCodeList().size() > 0) {
            for (String policy : newProjectExpectedStudy.getPoliciesCodeList()) {
              if (policy != null && !policy.trim().isEmpty()) {
                id = this.tryParseLong(policy.trim(), fieldErrors, "putExpectedStudy", "Policy");
                if (id != null) {
                  ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(id);
                  if (projectPolicy != null) {
                    projectPolicyList.add(projectPolicy);
                  } else {
                    fieldErrors.add(
                      new FieldErrorDTO("putExpectedStudy", "Policy", policy + " is an invalid Policy identifier"));
                  }
                }
              } else {
                fieldErrors
                  .add(new FieldErrorDTO("putExpectedStudy", "Policy", "A Policy code can not be null nor empty."));
              }
            }
          }

          // institutions
          if (newProjectExpectedStudy.getInstitutionsList() != null
            && newProjectExpectedStudy.getInstitutionsList().size() > 0) {
            for (String institution : newProjectExpectedStudy.getInstitutionsList()) {
              if (institution != null && !institution.trim().isEmpty()) {
                id = this.tryParseLong(institution.trim(), fieldErrors, "putExpectedStudy", "Institution");
                if (id != null) {
                  Institution institutionDB = institutionManager.getInstitutionById(id);
                  if (institutionDB != null) {
                    institutionList.add(institutionDB);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Institution",
                      institution + " is an invalid Institution identifier"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Institution",
                  "An Institution code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Institution",
             * "Please enter the Institution(s)."));
             * }
             */

          // milestones
          if (newProjectExpectedStudy.getMilestonesCodeList() != null
            && newProjectExpectedStudy.getMilestonesCodeList().size() > 0) {
            hasPrimary = 0;
            for (NewMilestonesDTO milestones : newProjectExpectedStudy.getMilestonesCodeList()) {
              if (milestones != null && milestones.getMilestone() != null
                && !milestones.getMilestone().trim().isEmpty()) {
                CrpMilestone crpMilestone =
                  crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestone(), phase.getId());
                if (crpMilestone != null && crpMilestone.isActive()) {
                  ProjectExpectedStudyMilestone obj = new ProjectExpectedStudyMilestone();
                  obj.setCrpMilestone(crpMilestone);
                  obj.setPrimary(milestones.getPrimary() != null && milestones.getPrimary());
                  if (obj.getPrimary()) {
                    hasPrimary += 1;
                  }
                  milestoneList.add(obj);
                } else {
                  fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Milestone",
                    milestones.getMilestone() + " is an invalid Milestone identifier"));
                }

              } else {
                fieldErrors.add(
                  new FieldErrorDTO("putExpectedStudy", "Milestone", "A Milestone code can not be null nor empty."));
              }
            }

            if (hasPrimary > 1) {
              fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Milestone",
                "There can not be more than one milestone marked as primary"));
            }
          } /*
             * else {
             * fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Milestone", "Please enter the Milestone(s)."));
             * }
             */

          // Crosscutting markers
          if (newProjectExpectedStudy.getCrossCuttingMarkers() != null
            && newProjectExpectedStudy.getCrossCuttingMarkers().size() > 0) {
            for (NewCrosscuttingMarkersDTO crosscuttingmark : newProjectExpectedStudy.getCrossCuttingMarkers()) {
              if (crosscuttingmark != null && crosscuttingmark.getCrossCuttingmarker() != null
                && crosscuttingmark.getCrossCuttingmarkerScore() != null
                && !crosscuttingmark.getCrossCuttingmarker().trim().isEmpty()
                && !crosscuttingmark.getCrossCuttingmarkerScore().trim().isEmpty()) {
                id = this.tryParseLong(crosscuttingmark.getCrossCuttingmarker().trim(), fieldErrors, "putExpectedStudy",
                  "Crosscuttingmarker");
                if (id != null) {
                  CgiarCrossCuttingMarker cgiarCrossCuttingMarker =
                    cgiarCrossCuttingMarkerManager.getCgiarCrossCuttingMarkerById(id);
                  if (cgiarCrossCuttingMarker == null) {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Crosscuttingmarker",
                      cgiarCrossCuttingMarker + " is an invalid Crosscuttingmarker Code"));
                  } else {
                    id = this.tryParseLong(crosscuttingmark.getCrossCuttingmarkerScore().trim(), fieldErrors,
                      "putExpectedStudy", "CrosscuttingmarkerScore");
                    if (id != null) {
                      RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                        repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(id);
                      if (repIndGenderYouthFocusLevel == null) {
                        fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "CrosscuttingmarkerScore",
                          cgiarCrossCuttingMarker + " is an invalid GenderYouthFocusLevel Code"));
                      } else {
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_GENDER) {
                          projectExpectedStudyInfo.setGenderLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeGender(crosscuttingmark.getDescription());;

                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_YOUTH) {
                          projectExpectedStudyInfo.setYouthLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeYouth(crosscuttingmark.getDescription());
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_CAPDEV) {
                          projectExpectedStudyInfo.setCapdevLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeCapdev(crosscuttingmark.getDescription());
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_CLIMATECHANGE) {
                          projectExpectedStudyInfo.setClimateChangeLevel(repIndGenderYouthFocusLevel);
                          projectExpectedStudyInfo.setDescribeClimateChange(crosscuttingmark.getDescription());
                        }
                      }
                    }
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "CrosscuttingMarker",
                  "A CrosscuttingMarker/CrosscuttingmarkerScore code can not be null nor empty."));
              }
            }
          } /*
             * else {
             * fieldErrors.add(
             * new FieldErrorDTO("putExpectedStudy", "Crosscuttingmarker", "Please enter the CrosscuttingMarker(s)."));
             * }
             */

          // links
          if (newProjectExpectedStudy.getLinks() != null && !newProjectExpectedStudy.getLinks().isEmpty()) {
            for (String link : newProjectExpectedStudy.getLinks()) {
              if (link != null && !link.trim().isEmpty()) {
                linkList.add(link);
              } else {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Link", "A Link can not be null nor empty."));
              }
            }
          }

          // quantifications
          if (newProjectExpectedStudy.getQuantificationList() != null
            && !newProjectExpectedStudy.getQuantificationList().isEmpty()) {
            for (QuantificationDTO quantification : newProjectExpectedStudy.getQuantificationList()) {
              if (quantification.getTargetUnit() != null && quantification.getTargetUnit().trim().length() > 0
                && quantification.getNumber() >= 0 && quantification.getQuantificationType() != null
                && (quantification.getQuantificationType().equalsIgnoreCase("A")
                  || quantification.getQuantificationType().equalsIgnoreCase("B"))) {
                ProjectExpectedStudyQuantification projectExpectedStudyQuantification =
                  new ProjectExpectedStudyQuantification();
                projectExpectedStudyQuantification.setPhase(phase);
                projectExpectedStudyQuantification.setTargetUnit(quantification.getTargetUnit());
                projectExpectedStudyQuantification.setTypeQuantification(quantification.getQuantificationType());
                projectExpectedStudyQuantification.setNumber(quantification.getNumber());
                projectExpectedStudyQuantification.setComments(quantification.getComments());
                expectedStudyQuantificationList.add(projectExpectedStudyQuantification);
              } else {
                fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Quantification",
                  "You have to fill all fields for quantification"));
              }
            }
          }
          // reference cited
          // reference Cited list
          if (newProjectExpectedStudy.getReferenceCitedList() != null
            && !newProjectExpectedStudy.getReferenceCitedList().isEmpty()) {
            for (ReferenceCitedDTO referenceCited : newProjectExpectedStudy.getReferenceCitedList()) {
              ProjectExpectedStudyReference projectExpectedStudyReference = new ProjectExpectedStudyReference();
              projectExpectedStudyReference.setPhase(phase);
              projectExpectedStudyReference.setReference(referenceCited.getReference());
              projectExpectedStudyReference.setLink(referenceCited.getUrl());
              referenceList.add(projectExpectedStudyReference);
            }
          }

        }

        if (fieldErrors.size() == 0 && project != null) {
          projectExpectedStudy.setPhase(phase.getId());
          projectExpectedStudy.setYear(phase.getYear());
          projectExpectedStudy.setProject(project);
          ProjectExpectedStudy projectExpectedStudyDB =
            projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudy);
          if (projectExpectedStudyDB != null) {
            expectedStudyID = projectExpectedStudyDB.getId();
            projectExpectedStudyInfo.setProjectExpectedStudy(projectExpectedStudyDB);
            if (projectPolicyList.size() > 0) {
              projectExpectedStudyInfo.setIsContribution(true);
            }
            if (srfSloIndicatorList.size() > 0) {
              projectExpectedStudyInfo.setIsSrfTarget("targetsOptionYes");
            }
            if (newProjectExpectedStudy.getMilestonesCodeList() != null
              && newProjectExpectedStudy.getMilestonesCodeList().size() > 0) {
              projectExpectedStudyInfo.setHasMilestones(true);
            }
            if (projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(projectExpectedStudyInfo) != null) {
              // update flashsip/module
              // getting actual flagships
              Long phaseId = phase.getId();
              List<ProjectExpectedStudyFlagship> projectExpectedStudyFlagshipList =
                projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing flagship
              List<ProjectExpectedStudyFlagship> existingProjectExpectedStudyFlagshipList =
                new ArrayList<ProjectExpectedStudyFlagship>();
              // flagships
              for (CrpProgram crpProgram : flagshipList) {
                ProjectExpectedStudyFlagship projectExpectedStudyFlagship = projectExpectedStudyFlagshipManager
                  .findProjectExpectedStudyFlagshipbyPhase(expectedStudyID, crpProgram.getId(), phase.getId());
                if (projectExpectedStudyFlagship != null) {
                  existingProjectExpectedStudyFlagshipList.add(projectExpectedStudyFlagship);
                } else {
                  projectExpectedStudyFlagship = new ProjectExpectedStudyFlagship();
                  projectExpectedStudyFlagship.setCrpProgram(crpProgram);
                  projectExpectedStudyFlagship.setPhase(phase);
                  projectExpectedStudyFlagship.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(projectExpectedStudyFlagship);
                }
              }
              // delete not existing
              for (ProjectExpectedStudyFlagship obj : projectExpectedStudyFlagshipList) {
                if (!existingProjectExpectedStudyFlagshipList.contains(obj)) {
                  projectExpectedStudyFlagshipManager.deleteProjectExpectedStudyFlagship(obj.getId());
                }
              }

              // update geographicscope
              // getting actual geographicscope
              List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopeList =
                projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing geographicscope
              List<ProjectExpectedStudyGeographicScope> existingProjectExpectedStudyGeographicScopeList =
                new ArrayList<ProjectExpectedStudyGeographicScope>();
              // save geographicscopes
              for (RepIndGeographicScope repIndGeographicScope : geographicScopeList) {
                ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope =
                  projectExpectedStudyGeographicScopeManager.getProjectExpectedStudyGeographicScopeByPhase(
                    expectedStudyID, repIndGeographicScope.getId(), phase.getId());
                if (projectExpectedStudyGeographicScope != null) {
                  existingProjectExpectedStudyGeographicScopeList.add(projectExpectedStudyGeographicScope);
                } else {
                  projectExpectedStudyGeographicScope = new ProjectExpectedStudyGeographicScope();
                  projectExpectedStudyGeographicScope.setPhase(phase);
                  projectExpectedStudyGeographicScope.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyGeographicScope.setRepIndGeographicScope(repIndGeographicScope);
                  projectExpectedStudyGeographicScopeManager
                    .saveProjectExpectedStudyGeographicScope(projectExpectedStudyGeographicScope);
                }
              }
              // delete not existing
              for (ProjectExpectedStudyGeographicScope obj : projectExpectedStudyGeographicScopeList) {
                if (!existingProjectExpectedStudyGeographicScopeList.contains(obj)) {
                  projectExpectedStudyGeographicScopeManager.deleteProjectExpectedStudyGeographicScope(obj.getId());
                }
              }

              // countries
              // getting actual countries
              List<ProjectExpectedStudyCountry> projectExpectedStudyCountryList =
                projectExpectedStudyCountryManager.findAll().stream()
                  .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId())
                    && c.getPhase().getId() == phaseId)
                  .collect(Collectors.toList());
              // create existing countries
              List<ProjectExpectedStudyCountry> existingProjectExpectedStudyCountryList =
                new ArrayList<ProjectExpectedStudyCountry>();
              // save countries
              for (LocElement country : countriesList) {
                ProjectExpectedStudyCountry projectExpectedStudyCountry = projectExpectedStudyCountryManager
                  .getProjectExpectedStudyCountryByPhase(expectedStudyID, country.getId(), phase.getId());
                if (projectExpectedStudyCountry != null) {
                  existingProjectExpectedStudyCountryList.add(projectExpectedStudyCountry);
                } else {
                  projectExpectedStudyCountry = new ProjectExpectedStudyCountry();
                  projectExpectedStudyCountry.setLocElement(country);
                  projectExpectedStudyCountry.setPhase(phase);
                  projectExpectedStudyCountry.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(projectExpectedStudyCountry);
                }
              }
              // delete not existing
              for (ProjectExpectedStudyCountry obj : projectExpectedStudyCountryList) {
                if (!existingProjectExpectedStudyCountryList.contains(obj)) {
                  projectExpectedStudyCountryManager.deleteProjectExpectedStudyCountry(obj.getId());
                }
              }

              // regions
              // getting actual regions
              List<ProjectExpectedStudyRegion> projectExpectedStudyRegionList =
                projectExpectedStudyRegionManager.findAll().stream()
                  .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId())
                    && c.getPhase().getId() == phaseId)
                  .collect(Collectors.toList());
              // create existing regions
              List<ProjectExpectedStudyRegion> existingProjectExpectedStudyRegionList =
                new ArrayList<ProjectExpectedStudyRegion>();
              // save regions
              for (LocElement region : regionsList) {
                ProjectExpectedStudyRegion projectExpectedStudyRegion = projectExpectedStudyRegionManager
                  .getProjectExpectedStudyRegionByPhase(expectedStudyID, region.getId(), phase.getId());
                if (projectExpectedStudyRegion != null) {
                  existingProjectExpectedStudyRegionList.add(projectExpectedStudyRegion);
                } else {
                  projectExpectedStudyRegion = new ProjectExpectedStudyRegion();
                  projectExpectedStudyRegion.setLocElement(region);
                  projectExpectedStudyRegion.setPhase(phase);
                  projectExpectedStudyRegion.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyRegionManager.saveProjectExpectedStudyRegion(projectExpectedStudyRegion);
                }
              }
              // delete not existing regions
              for (ProjectExpectedStudyRegion obj : projectExpectedStudyRegionList) {
                if (!existingProjectExpectedStudyRegionList.contains(obj)) {
                  projectExpectedStudyRegionManager.deleteProjectExpectedStudyRegion(obj.getId());
                }
              }

              // SLO targets
              // getting actual SLO targets
              List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
                projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing SLO targets
              List<ProjectExpectedStudySrfTarget> existingProjectExpectedStudySrfTargetList =
                new ArrayList<ProjectExpectedStudySrfTarget>();
              // save SLO targets
              for (SrfSloIndicator srfSloIndicator : srfSloIndicatorList.stream().distinct()
                .collect(Collectors.toList())) {
                ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget = projectExpectedStudySrfTargetManager
                  .getProjectExpectedStudySrfTargetByPhase(expectedStudyID, srfSloIndicator.getId(), phase.getId());
                if (projectExpectedStudySrfTarget != null) {
                  existingProjectExpectedStudySrfTargetList.add(projectExpectedStudySrfTarget);
                } else {
                  projectExpectedStudySrfTarget = new ProjectExpectedStudySrfTarget();
                  projectExpectedStudySrfTarget.setSrfSloIndicator(srfSloIndicator);
                  projectExpectedStudySrfTarget.setPhase(phase);
                  projectExpectedStudySrfTarget.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudySrfTargetManager.saveProjectExpectedStudySrfTarget(projectExpectedStudySrfTarget);
                }
              }
              // delete not existing SLO targets
              for (ProjectExpectedStudySrfTarget obj : projectExpectedStudySrfTargetList) {
                if (!existingProjectExpectedStudySrfTargetList.contains(obj)) {
                  projectExpectedStudySrfTargetManager.deleteProjectExpectedStudySrfTarget(obj.getId());
                }
              }

              // SudIDOs
              // getting actual SudIDOs
              List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdoList =
                projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing SudIDOs
              List<ProjectExpectedStudySubIdo> existingProjectExpectedStudySubIdoList =
                new ArrayList<ProjectExpectedStudySubIdo>();
              // save SudIDOs
              for (ProjectExpectedStudySubIdo srfSubIdo : srfSubIdoList) {
                ProjectExpectedStudySubIdo projectExpectedStudySubIdo =
                  projectExpectedStudySubIdoManager.getProjectExpectedStudySubIdoByPhase(expectedStudyID,
                    srfSubIdo.getSrfSubIdo().getId(), phase.getId());
                if (projectExpectedStudySubIdo != null) {
                  existingProjectExpectedStudySubIdoList.add(projectExpectedStudySubIdo);
                } else {
                  projectExpectedStudySubIdo = new ProjectExpectedStudySubIdo();
                  projectExpectedStudySubIdo.setSrfSubIdo(srfSubIdo.getSrfSubIdo());
                  projectExpectedStudySubIdo.setPhase(phase);
                  projectExpectedStudySubIdo.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudySubIdo.setPrimary(srfSubIdo.getPrimary());
                  projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(projectExpectedStudySubIdo);
                }
              }
              // delete not existing SudIDOs
              for (ProjectExpectedStudySubIdo obj : projectExpectedStudySubIdoList) {
                if (!existingProjectExpectedStudySubIdoList.contains(obj)) {
                  projectExpectedStudySubIdoManager.deleteProjectExpectedStudySubIdo(obj.getId());
                }
              }

              // links
              // getting actual links
              List<ProjectExpectedStudyLink> projectExpectedStudyLinkList =
                projectExpectedStudy.getProjectExpectedStudyLinks().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing links
              List<ProjectExpectedStudyLink> existingProjectExpectedStudyLinkList =
                new ArrayList<ProjectExpectedStudyLink>();
              // save links
              for (String link : linkList) {
                ProjectExpectedStudyLink projectExpectedStudyLink = projectExpectedStudyLinkManager
                  .getProjectExpectedStudyLinkByPhase(expectedStudyID, link, phase.getId());
                if (projectExpectedStudyLink != null) {
                  existingProjectExpectedStudyLinkList.add(projectExpectedStudyLink);
                } else {
                  projectExpectedStudyLink = new ProjectExpectedStudyLink();
                  projectExpectedStudyLink.setLink(link);
                  projectExpectedStudyLink.setPhase(phase);
                  projectExpectedStudyLink.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(projectExpectedStudyLink);
                }
              }
              // delete not existing links
              for (ProjectExpectedStudyLink obj : projectExpectedStudyLinkList) {
                if (!existingProjectExpectedStudyLinkList.contains(obj)) {
                  projectExpectedStudyLinkManager.deleteProjectExpectedStudyLink(obj.getId());
                }
              }

              // institutions
              // getting actual institutions
              List<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutionList =
                projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing institutions
              List<ProjectExpectedStudyInstitution> existingProjectExpectedStudyInstitutionList =
                new ArrayList<ProjectExpectedStudyInstitution>();
              // save institutions
              for (Institution institution : institutionList) {
                ProjectExpectedStudyInstitution projectExpectedStudyInstitution = projectExpectedStudyInstitutionManager
                  .getProjectExpectedStudyInstitutionByPhase(expectedStudyID, institution.getId(), phase.getId());
                if (projectExpectedStudyInstitution != null) {
                  existingProjectExpectedStudyInstitutionList.add(projectExpectedStudyInstitution);
                } else {
                  projectExpectedStudyInstitution = new ProjectExpectedStudyInstitution();
                  projectExpectedStudyInstitution.setInstitution(institution);
                  projectExpectedStudyInstitution.setPhase(phase);
                  projectExpectedStudyInstitution.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyInstitutionManager
                    .saveProjectExpectedStudyInstitution(projectExpectedStudyInstitution);
                }
              }
              // delete not existing institutions
              for (ProjectExpectedStudyInstitution obj : projectExpectedStudyInstitutionList) {
                if (!existingProjectExpectedStudyInstitutionList.contains(obj)) {
                  projectExpectedStudyInstitutionManager.deleteProjectExpectedStudyInstitution(obj.getId());
                }
              }

              // milestones
              // getting actual milestones
              List<ProjectExpectedStudyMilestone> projectExpectedStudyMilestoneList =
                projectExpectedStudy.getProjectExpectedStudyMilestones().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing links
              List<ProjectExpectedStudyMilestone> existingProjectExpectedStudyMilestoneList =
                new ArrayList<ProjectExpectedStudyMilestone>();
              // save institutions
              for (ProjectExpectedStudyMilestone expectedStudyMilestones : milestoneList) {
                ProjectExpectedStudyMilestone projectExpectedStudyMilestone =
                  projectExpectedStudyMilestoneManager.getProjectExpectedStudyMilestoneByPhase(expectedStudyID,
                    expectedStudyMilestones.getCrpMilestone().getId(), phase.getId());
                if (projectExpectedStudyMilestone != null) {
                  projectExpectedStudyMilestoneList.add(projectExpectedStudyMilestone);
                } else {
                  projectExpectedStudyMilestone = new ProjectExpectedStudyMilestone();
                  projectExpectedStudyMilestone.setPrimary(expectedStudyMilestones.getPrimary());
                  projectExpectedStudyMilestone.setCrpMilestone(expectedStudyMilestones.getCrpMilestone());
                  projectExpectedStudyMilestone.setPhase(phase);
                  projectExpectedStudyMilestone.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(projectExpectedStudyMilestone);
                }
              }
              // delete not existing institutions
              for (ProjectExpectedStudyMilestone obj : projectExpectedStudyMilestoneList) {
                if (!existingProjectExpectedStudyMilestoneList.contains(obj)) {
                  projectExpectedStudyMilestoneManager.deleteProjectExpectedStudyMilestone(obj.getId());
                }
              }

              // contributing crps
              // getting actual crps
              List<ProjectExpectedStudyCrp> projectExpectedStudyCrpList =
                projectExpectedStudy.getProjectExpectedStudyCrps().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing crps
              List<ProjectExpectedStudyCrp> existingProjectExpectedStudyCrpList =
                new ArrayList<ProjectExpectedStudyCrp>();
              // save crps
              for (GlobalUnit globalUnit : crpContributing) {
                ProjectExpectedStudyCrp projectExpectedStudyCrp = projectExpectedStudyCrpManager
                  .getProjectExpectedStudyCrpByPhase(expectedStudyID, globalUnit.getId(), phase.getId());
                if (projectExpectedStudyCrp != null) {
                  existingProjectExpectedStudyCrpList.add(projectExpectedStudyCrp);
                } else {
                  projectExpectedStudyCrp = new ProjectExpectedStudyCrp();
                  projectExpectedStudyCrp.setGlobalUnit(globalUnit);
                  projectExpectedStudyCrp.setPhase(phase);
                  projectExpectedStudyCrp.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(projectExpectedStudyCrp);
                }
              }
              // delete not existing crps
              for (ProjectExpectedStudyCrp obj : projectExpectedStudyCrpList) {
                if (!existingProjectExpectedStudyCrpList.contains(obj)) {
                  projectExpectedStudyCrpManager.deleteProjectExpectedStudyCrp(obj.getId());
                }
              }

              // quantifications
              // getting actual quantifications
              List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantificationList =
                projectExpectedStudyQuantificationManager.findAll().stream()
                  .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId())
                    && c.getPhase().getId() == phaseId)
                  .collect(Collectors.toList());
              // create existing quantifications
              List<ProjectExpectedStudyQuantification> existingProjectExpectedStudyQuantificationList =
                new ArrayList<ProjectExpectedStudyQuantification>();
              // save quantifications
              for (ProjectExpectedStudyQuantification expectedStudyQuantification : expectedStudyQuantificationList) {
                ProjectExpectedStudyQuantification projectExpectedStudyQuantification =
                  projectExpectedStudyQuantificationManager.getProjectExpectedStudyQuantificationByPhase(
                    expectedStudyID, expectedStudyQuantification.getTypeQuantification(),
                    expectedStudyQuantification.getNumber(), expectedStudyQuantification.getTargetUnit(),
                    phase.getId());
                if (projectExpectedStudyQuantification != null) {
                  existingProjectExpectedStudyQuantificationList.add(projectExpectedStudyQuantification);
                } else {
                  projectExpectedStudyQuantification = new ProjectExpectedStudyQuantification();
                  projectExpectedStudyQuantification.setNumber(expectedStudyQuantification.getNumber());
                  projectExpectedStudyQuantification.setTargetUnit(expectedStudyQuantification.getTargetUnit());
                  projectExpectedStudyQuantification
                    .setTypeQuantification(expectedStudyQuantification.getTypeQuantification());
                  projectExpectedStudyQuantification.setComments(expectedStudyQuantification.getComments());
                  projectExpectedStudyQuantification.setPhase(phase);
                  projectExpectedStudyQuantification.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyQuantificationManager
                    .saveProjectExpectedStudyQuantification(projectExpectedStudyQuantification);
                }
              }
              // delete not existing quantifications
              for (ProjectExpectedStudyQuantification obj : projectExpectedStudyQuantificationList) {
                if (!existingProjectExpectedStudyQuantificationList.contains(obj)) {
                  projectExpectedStudyQuantificationManager.deleteProjectExpectedStudyQuantification(obj.getId());
                }
              }

              // innovations
              // getting actual innovations
              List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovationList =
                projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing innovations
              List<ProjectExpectedStudyInnovation> existingProjectExpectedStudyInnovationList =
                new ArrayList<ProjectExpectedStudyInnovation>();
              // save innovations
              for (ProjectInnovation projectInnovation : projectInnovationList) {
                ProjectExpectedStudyInnovation projectExpectedStudyInnovation = projectExpectedStudyInnovationManager
                  .getProjectExpectedStudyInnovationByPhase(expectedStudyID, projectInnovation.getId(), phase.getId());
                if (projectExpectedStudyInnovation != null) {
                  existingProjectExpectedStudyInnovationList.add(projectExpectedStudyInnovation);
                } else {
                  projectExpectedStudyInnovation = new ProjectExpectedStudyInnovation();
                  projectExpectedStudyInnovation.setProjectInnovation(projectInnovation);
                  projectExpectedStudyInnovation.setPhase(phase);
                  projectExpectedStudyInnovation.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyInnovationManager
                    .saveProjectExpectedStudyInnovation(projectExpectedStudyInnovation);
                }
              }
              // delete not existing innovations
              for (ProjectExpectedStudyInnovation obj : projectExpectedStudyInnovationList) {
                if (!existingProjectExpectedStudyInnovationList.contains(obj)) {
                  projectExpectedStudyInnovationManager.deleteProjectExpectedStudyInnovation(obj.getId());
                }
              }

              // policies
              // getting actual policies
              List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList =
                projectExpectedStudy.getProjectExpectedStudyPolicies().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing policies
              List<ProjectExpectedStudyPolicy> existingProjectExpectedStudyPolicyList =
                new ArrayList<ProjectExpectedStudyPolicy>();
              // save policies
              for (ProjectPolicy projectPolicy : projectPolicyList) {
                ProjectExpectedStudyPolicy projectExpectedStudyPolicy = projectExpectedStudyPolicyManager
                  .getProjectExpectedStudyPolicyByPhase(expectedStudyID, projectPolicy.getId(), phase.getId());
                if (projectExpectedStudyPolicy != null) {
                  existingProjectExpectedStudyPolicyList.add(projectExpectedStudyPolicy);
                } else {
                  projectExpectedStudyPolicy = new ProjectExpectedStudyPolicy();
                  projectExpectedStudyPolicy.setProjectPolicy(projectPolicy);
                  projectExpectedStudyPolicy.setPhase(phase);
                  projectExpectedStudyPolicy.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(projectExpectedStudyPolicy);
                }
              }
              // delete not existing policies
              for (ProjectExpectedStudyPolicy obj : projectExpectedStudyPolicyList) {
                if (!existingProjectExpectedStudyPolicyList.contains(obj)) {
                  projectExpectedStudyPolicyManager.deleteProjectExpectedStudyPolicy(obj.getId());
                }
              }

              // Refence Cited
              // getting actual references
              List<ProjectExpectedStudyReference> projectExpectedStudyReferenceList =
                projectExpectedStudy.getProjectExpectedStudyReferences().stream()
                  .filter(c -> c.isActive() && c.getPhase().getId() == phaseId).collect(Collectors.toList());
              // create existing references
              List<ProjectExpectedStudyReference> existingProjectExpectedStudyReferenceList =
                new ArrayList<ProjectExpectedStudyReference>();
              // save reference
              for (ProjectExpectedStudyReference expectedStudyReference : referenceList) {
                ProjectExpectedStudyReference projectExpectedStudyReference =
                  projectExpectedStudyReferenceManager.getProjectExpectedStudyReferenceByPhase(expectedStudyID,
                    expectedStudyReference.getLink(), phase.getId());
                if (projectExpectedStudyReference != null) {
                  existingProjectExpectedStudyReferenceList.add(projectExpectedStudyReference);
                } else {
                  projectExpectedStudyReference = new ProjectExpectedStudyReference();
                  projectExpectedStudyReference.setLink(expectedStudyReference.getLink());
                  projectExpectedStudyReference.setReference(expectedStudyReference.getReference());
                  projectExpectedStudyReference.setPhase(phase);
                  projectExpectedStudyReference.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyReferenceManager.saveProjectExpectedStudyReference(projectExpectedStudyReference);
                }
              }
              // delete not existing reference
              for (ProjectExpectedStudyReference obj : projectExpectedStudyReferenceList) {
                if (!existingProjectExpectedStudyReferenceList.contains(obj)) {
                  projectExpectedStudyReferenceManager.deleteProjectExpectedStudyReference(obj.getId());
                }
              }


              // verify if was included in synthesis PMU
              LiaisonInstitution liaisonInstitution = this.liaisonInstitutionManager
                .findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
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
                  final Long study = expectedStudyID;
                  ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy =
                    reportSynthesisFlagshipProgress.getReportSynthesisFlagshipProgressStudies().stream()
                      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == study).findFirst()
                      .orElse(null);
                  if (reportSynthesisFlagshipProgressStudy != null && existing) {
                    reportSynthesisFlagshipProgressStudy = reportSynthesisFlagshipProgressStudyManager
                      .getReportSynthesisFlagshipProgressStudyById(reportSynthesisFlagshipProgressStudy.getId());
                    reportSynthesisFlagshipProgressStudy.setActive(false);
                    reportSynthesisFlagshipProgressStudy = reportSynthesisFlagshipProgressStudyManager
                      .saveReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudy);
                  } else {
                    reportSynthesisFlagshipProgressStudy = new ReportSynthesisFlagshipProgressStudy();
                    reportSynthesisFlagshipProgressStudy.setCreatedBy(user);
                    reportSynthesisFlagshipProgressStudy.setProjectExpectedStudy(projectExpectedStudy);
                    reportSynthesisFlagshipProgressStudy
                      .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
                    reportSynthesisFlagshipProgressStudy = reportSynthesisFlagshipProgressStudyManager
                      .saveReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudy);
                    reportSynthesisFlagshipProgressStudy.setActive(false);
                    reportSynthesisFlagshipProgressStudyManager
                      .saveReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudy);
                  }
                }
              }
            }
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "ProjectExpectedStudyInfoEntity",
          "Please enter a Project Expected Study Info"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return expectedStudyID;
  }

  public Long tryParseLong(String value, List<FieldErrorDTO> fieldErrors, String httpMethod, String field) {
    Long result = null;
    try {
      result = Long.parseLong(value);
    } catch (NumberFormatException nfe) {
      fieldErrors
        .add(new FieldErrorDTO(httpMethod, field, value + " is an invalid " + field + " numeric identification code"));
    }
    return result;
  }
}