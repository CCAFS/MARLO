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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLocationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceLocationsManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapDeliverablesDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapFundingSourceBudgetDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapFundingSourcesDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapInnovationsDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapOICRDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapPartnersDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapProjectsDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.CrpGeoLocationMapMapper;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

@Named
public class CrpGeoLocationMapItem<T> {

  private ProjectInnovationCountryManager projectInnovationCountryManager;
  private ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private DeliverableLocationManager deliverableLocationManager;
  private ProjectLocationManager projectLocationManager;
  private FundingSourceLocationsManager fundingSourceLocationsManager;
  private ProjectPartnerLocationManager projectPartnerLocationManager;
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private LocElementManager locElementManager;
  protected APConfig config;
  private ProjectManager projectManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private DeliverableManager deliverableManager;
  private ProjectPartnerManager projectPartnerManager;
  private FundingSourceManager fundingSourceManager;
  private GeneralStatusManager generalStatusManager;
  private DeliverableInfoManager deliverableInfoManager;

  CrpGeoLocationMapMapper crpGeoLocationMapMapper;

  @Inject
  public CrpGeoLocationMapItem(ProjectInnovationCountryManager projectInnovationCountryManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    DeliverableLocationManager deliverableLocationManager, ProjectLocationManager projectLocationManager,
    FundingSourceLocationsManager fundingSourceLocationsManager,
    ProjectPartnerLocationManager projectPartnerLocationManager, GlobalUnitManager globalUnitManager,
    PhaseManager phaseManager, CrpGeoLocationMapMapper crpGeoLocationMapMapper, APConfig config,
    LocElementManager locElementManager, ProjectManager projectManager,
    ProjectInnovationManager projectInnovationManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    DeliverableManager deliverableManager, ProjectPartnerManager projectPartnerManager,
    FundingSourceManager fundingSourceManager, GeneralStatusManager generalStatusManager,
    DeliverableInfoManager deliverableInfoManager) {
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.deliverableLocationManager = deliverableLocationManager;
    this.projectLocationManager = projectLocationManager;
    this.fundingSourceLocationsManager = fundingSourceLocationsManager;
    this.projectPartnerLocationManager = projectPartnerLocationManager;
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.crpGeoLocationMapMapper = crpGeoLocationMapMapper;
    this.config = config;
    this.locElementManager = locElementManager;
    this.projectManager = projectManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.deliverableManager = deliverableManager;
    this.projectPartnerManager = projectPartnerManager;
    this.fundingSourceManager = fundingSourceManager;
    this.generalStatusManager = generalStatusManager;
    this.deliverableInfoManager = deliverableInfoManager;
  }

  public List<CrpGeoLocationMapDTO> getAllCrpGeoLocationMap(String CGIARentityAcronym, int repoYear) {
    List<CrpGeoLocationMapDTO> crpGeoLocationMapList = new ArrayList<CrpGeoLocationMapDTO>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("getAllCrpGeoLocationMap", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("getAllCrpGeoLocationMap", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && p.getYear() == repoYear
        && StringUtils.equalsIgnoreCase(p.getDescription(), APConstants.REPORTING))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("getAllCrpGeoLocationMap", "year", ' ' + repoYear + " is an invalid reporting year"));
    }

    if (fieldErrors.isEmpty()) {
      // for every country lookup for data related
      List<LocElement> countries =
        locElementManager.findAllLocationMap(phase.getId()).stream().collect(Collectors.toList());
      CrpGeoLocationMapDTO geoLocation = null;
      List<Project> projectList = null;
      List<ProjectInnovation> innovationlist = null;
      List<ProjectExpectedStudy> OICRs = null;
      List<Deliverable> deliverableList = null;
      List<ProjectPartner> partners = null;
      List<FundingSource> fundingSources = null;
      // getting all project Locations
      List<ProjectLocation> lprojectLocation = projectLocationManager.findAllByPhase(phase.getId()).stream()
        .filter(c -> c != null && c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toList());
      // getting all partners locations
      List<ProjectPartnerLocation> lpartnerLocation = projectPartnerLocationManager
        .findAllByPhase(phase.getId()).stream().filter(c -> c != null && c.isActive()
          && c.getProjectPartner().getPhase() != null && c.getProjectPartner().getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toList());
      // getting all funding sources locations
      List<FundingSourceLocation> lfundingSourceLocation =
        fundingSourceLocationsManager.findAllByPhase(phase.getId()).stream()
          .filter(
            c -> c != null && c.isActive() && c.getLocElement() != null && c.getPhase().getId().equals(phase.getId()))
          .collect(Collectors.toList());
      // getting all deliverables locations
      List<DeliverableLocation> ldeliverableLocation = deliverableLocationManager.findAllByPhase(phase.getId()).stream()
        .filter(c -> c != null && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
      // getting all innovation locations
      List<ProjectInnovationCountry> linnovationCountry = projectInnovationCountryManager.findAll().stream()
        .filter(c -> c != null && c.isActive() && c.getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toList());
      // gettting all ExpectedStudies
      List<ProjectExpectedStudyCountry> lstudyCountry = projectExpectedStudyCountryManager.findAll().stream()
        .filter(c -> c != null && c.isActive() && c.getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toList());
      // List All Loc_Elements
      for (LocElement locElement : countries) {
        LocElement newLocElement = locElementManager.getLocElementById(locElement.getId());
        geoLocation = new CrpGeoLocationMapDTO();
        geoLocation.setCountry(newLocElement.getName());
        geoLocation.setCountryIsoAlpha2(newLocElement.getIsoAlpha2());
        geoLocation.setCountryIso("" + newLocElement.getIsoNumeric());
        // Project Locations
        projectList = new ArrayList<Project>();
        if (lprojectLocation != null) {
          for (ProjectLocation projectLocation : lprojectLocation.stream()
            .filter(c -> c.getLocElement().getId().equals(locElement.getId())).collect(Collectors.toList())) {
            Project project = projectManager.getProjectById(projectLocation.getProject().getId());
            ProjectInfo info = project.getProjecInfoPhase(phase);
            GeneralStatus status = generalStatusManager.getGeneralStatusById(info.getStatus());
            if (status != null) {
              info.setStatusName(status.getName());
            }
            project.setProjectInfo(info);

            List<CrpProgram> programs = new ArrayList<>();
            for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId())
                && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                && c.getCrpProgram().getCrp().getId().equals(globalUnitEntity.getId()))
              .collect(Collectors.toList())) {
              programs.add(projectFocuses.getCrpProgram());
            }
            project.setFlagships(programs);
            List<CrpProgram> regions = new ArrayList<>();

            for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId())
                && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
                && c.getCrpProgram().getCrp().getId().equals(globalUnitEntity.getId()))
              .collect(Collectors.toList())) {
              regions.add(projectFocuses.getCrpProgram());
            }
            project.setRegions(regions);
            projectList.add(project);
          }
        } else {
          lprojectLocation = new ArrayList<ProjectLocation>();
        }
        List<CrpGeoLocationMapProjectsDTO> crpGeoLocationMapProjectsList = projectList.stream()
          .map(p -> this.crpGeoLocationMapMapper.projectToCrpGeoLocationMapProjectsDTO(p)).collect(Collectors.toList());
        geoLocation.setProjects(crpGeoLocationMapProjectsList);
        // Project Innovations
        innovationlist = new ArrayList<ProjectInnovation>();
        for (ProjectInnovationCountry innovationCountry : linnovationCountry.stream()
          .filter(c -> c.getLocElement().getId().equals(locElement.getId())).collect(Collectors.toList())) {
          ProjectInnovation innovation =
            projectInnovationManager.getProjectInnovationById(innovationCountry.getProjectInnovation().getId());
          innovation.getProjectInnovationInfo(phase);
          Project project = projectManager.getProjectById(innovation.getProject().getId());
          ProjectInfo info = project.getProjecInfoPhase(phase);
          project.setProjectInfo(info);
          innovation.setProject(project);
          if (innovation.getProjectInnovationInfo().getYear() == repoYear) {
            String pdflink = config.getClarisa_summaries_pdf() + "summaries/" + CGIARentityAcronym
              + "/projectInnovationSummary.do?innovationID=" + innovation.getId().longValue() + "&phaseID="
              + phase.getId().longValue();
            innovation.setPdfLink(pdflink);
            innovationlist.add(innovation);
          }
        }
        List<CrpGeoLocationMapInnovationsDTO> crpGeoLocationMapInnovationsList = innovationlist.stream()
          .map(p -> this.crpGeoLocationMapMapper.projectInnovationToCrpGeoLocationMapInnovationsDTO(p))
          .collect(Collectors.toList());
        geoLocation.setInnovations(crpGeoLocationMapInnovationsList);

        // OICRs
        OICRs = new ArrayList<ProjectExpectedStudy>();
        for (ProjectExpectedStudyCountry studyCountry : lstudyCountry.stream()
          .filter(c -> c.getLocElement().getId().equals(locElement.getId())).collect(Collectors.toList())) {
          ProjectExpectedStudy study =
            projectExpectedStudyManager.getProjectExpectedStudyById(studyCountry.getProjectExpectedStudy().getId());
          study.getProjectExpectedStudyInfo(phase);
          if (study.getProjectExpectedStudyInfo().getYear() == repoYear) {
            if (study.getProjectExpectedStudyInfo().getIsPublic() != null
              && study.getProjectExpectedStudyInfo().getIsPublic()) {
              String pdflink =
                config.getClarisa_summaries_pdf() + "projects/" + CGIARentityAcronym + "/studySummary.do?studyID="
                  + study.getId().longValue() + "&cycle=" + phase.getDescription() + "&year=" + phase.getYear();
              study.setPdfLink(pdflink);
            } else {
              study.setPdfLink(null);
            }
            OICRs.add(study);
          }
        }
        List<CrpGeoLocationMapOICRDTO> crpGeoLocationMapOICRsList =
          OICRs.stream().map(p -> this.crpGeoLocationMapMapper.projectExpectedStudyToCrpGeoLocationMapOICRDTO(p))
            .collect(Collectors.toList());
        geoLocation.setExpectedStudies(crpGeoLocationMapOICRsList);

        // Deliverables
        deliverableList = new ArrayList<Deliverable>();
        for (DeliverableLocation deliverableLocation : ldeliverableLocation.stream()
          .filter(c -> c.getLocElement().getId().equals(locElement.getId())).collect(Collectors.toList())) {
          Deliverable deliverable = deliverableManager.getDeliverableById(deliverableLocation.getDeliverable().getId());
          if (deliverable.isActive() && deliverable.getDeliverableInfo() != null) {
            deliverable.getDeliverableInfo(phase);

            if (deliverable.getDeliverableInfo() != null && deliverable.getDeliverableInfo().getYear() == repoYear) {
              deliverable.setDissemination(deliverable.getDeliverableDisseminations().stream()
                .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).findFirst().orElse(null));
              if (deliverable.getProject() != null) {
                Project project = projectManager.getProjectById(deliverable.getProject().getId());
                project.getProjecInfoPhase(phase);
                deliverable.setProject(project);
                deliverableList.add(deliverable);
              }
            }
          }
        }
        List<CrpGeoLocationMapDeliverablesDTO> crpGeoLocationMapDeliverablesDTO = deliverableList.stream()
          .map(p -> this.crpGeoLocationMapMapper.deliverableToCrpGeoLocationMapDeliverablesDTO(p))
          .collect(Collectors.toList());
        geoLocation.setDeliverables(crpGeoLocationMapDeliverablesDTO);

        // Partners
        partners = new ArrayList<ProjectPartner>();
        for (ProjectPartnerLocation partnerLocation : lpartnerLocation.stream()
          .filter(c -> c.getInstitutionLocation().getLocElement().getId().equals(locElement.getId()))
          .collect(Collectors.toList())) {
          ProjectPartner projectPartner =
            projectPartnerManager.getProjectPartnerById(partnerLocation.getProjectPartner().getId());
          partners.add(projectPartner);
        }
        List<CrpGeoLocationMapPartnersDTO> crpGeoLocationMapPartnersDTO =
          partners.stream().map(p -> this.crpGeoLocationMapMapper.projectPartnersToCrpGeoLocationMapPartnersDTO(p))
            .collect(Collectors.toList());
        geoLocation.setPartners(crpGeoLocationMapPartnersDTO);

        // funding sources
        fundingSources = new ArrayList<FundingSource>();
        for (FundingSourceLocation fundingSourceLocation : lfundingSourceLocation.stream()
          .filter(c -> c.isActive() && c.getLocElement().getId().equals(locElement.getId()))
          .collect(Collectors.toList())) {
          FundingSource fundingSource =
            fundingSourceManager.getFundingSourceById(fundingSourceLocation.getFundingSource().getId());
          FundingSourceInfo fundingSourceInfo = fundingSource.getFundingSourceInfo(phase);
          List<ProjectBudget> budgets = fundingSource.getProjectBudgets().stream()
            .filter(c -> c != null && c.getPhase() != null && c.getYear() <= repoYear && c.isActive()
              && c.getPhase().getId().equals(phase.getId()))
            .collect(Collectors.toList());
          Double amount = fundingSource.getFundingSourceBudgets().stream()
            .filter(
              c -> c != null && c.isActive() && c.getPhase().getId().equals(phase.getId()) && c.getYear() == repoYear)
            .mapToDouble(c -> c.getBudget()).findFirst().orElse(0);
          fundingSourceInfo.setGrantAmount(amount);
          fundingSource.setFundingSourceInfo(fundingSourceInfo);
          fundingSource.setProjectBudgetsList(budgets != null
            ? budgets.stream().sorted(Comparator.comparingInt(ProjectBudget::getYear)).collect(Collectors.toList())
            : new ArrayList<ProjectBudget>());
          fundingSources.add(fundingSource);
        }
        List<CrpGeoLocationMapFundingSourcesDTO> crpGeoLocationMapFundingSourcesListDTO = fundingSources.stream()
          .map(p -> this.crpGeoLocationMapMapper.fundingSourceToCrpGeoLocationMapFundingSourcesDTO(p))
          .collect(Collectors.toList());

        List<CrpGeoLocationMapFundingSourcesDTO> crpGeoLocationMapFundingSourcesList2 =
          new ArrayList<CrpGeoLocationMapFundingSourcesDTO>();
        if (crpGeoLocationMapFundingSourcesListDTO != null) {
          for (CrpGeoLocationMapFundingSourcesDTO crpGeoLocationMapFundingSources : crpGeoLocationMapFundingSourcesListDTO) {
            crpGeoLocationMapFundingSources
              .setBudgetList(crpGeoLocationMapFundingSources
                .getBudgetList().stream().collect(
                  Collectors.groupingBy(d -> d.getYear()))
                .entrySet().stream()
                .map(e -> e.getValue().stream()
                  .reduce((f1, f2) -> new CrpGeoLocationMapFundingSourceBudgetDTO(f1.getFundingSourceId(), f1.getYear(),
                    f1.getAmount() + f2.getAmount())))
                .map(f -> f.get()).collect(Collectors.toList()));
            crpGeoLocationMapFundingSourcesList2.add(crpGeoLocationMapFundingSources);
          }
        }
        // realizamos la sumatoria por año

        geoLocation.setFundingSources(crpGeoLocationMapFundingSourcesList2);
        if (geoLocation.getPartners().size() > 0 || geoLocation.getProjects().size() > 0
          || geoLocation.getExpectedStudies().size() > 0 || geoLocation.getInnovations().size() > 0
          || geoLocation.getFundingSources().size() > 0 || geoLocation.getDeliverables().size() > 0) {
          crpGeoLocationMapList.add(geoLocation);
        }
      }
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return crpGeoLocationMapList;
  }


}
