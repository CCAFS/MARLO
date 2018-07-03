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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HTMLParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class StudiesSummaryAction extends BaseSummariesAction implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(StudiesSummaryAction.class);

  // Managers
  private final ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private final ResourceManager resourceManager;
  private final HTMLParser HTMLParser;

  // PDF bytes
  private byte[] bytesPDF;
  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;


  // Parameters
  private long startTime;
  private String studyType;
  private String selectedFormat;

  @Inject
  public StudiesSummaryAction(APConfig config, CaseStudyManager caseStudyManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager, ResourceManager resourceManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager, HTMLParser HTMLParser,
    ProjectManager projectManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.HTMLParser = HTMLParser;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nStudies", this.getText("menu.studies"));
    masterReport.getParameterValues().put("i8nStudiesRNoData", this.getText("summaries.study.noData"));
    masterReport.getParameterValues().put("i8nStudiesRCaseStudy", this.getText("summaries.study"));
    masterReport.getParameterValues().put("i8nStudiesRTitle", this.getText("summaries.study.title"));
    masterReport.getParameterValues().put("i8nStudiesRYear", this.getText("summaries.study.year"));
    masterReport.getParameterValues().put("i8nStudiesRStatus", this.getText("study.status"));
    masterReport.getParameterValues().put("i8nStudiesRType", this.getText("study.type"));
    masterReport.getParameterValues().put("i8nStudiesROutcomeImpactStatement",
      this.getText("summaries.study.outcomeStatement"));
    masterReport.getParameterValues().put("i8nStudiesRIsContributionText",
      this.getText("summaries.study.reportingIndicatorThree"));
    masterReport.getParameterValues().put("i8nStudiesRpolicyInvestimentType",
      this.getText("study.reportingIndicatorThree.policyType"));
    masterReport.getParameterValues().put("i8nStudiesRPolicyAmount",
      this.getText("study.reportingIndicatorThree.amount"));
    masterReport.getParameterValues().put("i8nStudiesROrganizationType",
      this.getText("study.reportingIndicatorThree.organizationType"));
    masterReport.getParameterValues().put("i8nStudiesRStageProcess",
      this.getText("study.reportingIndicatorThree.stage"));
    masterReport.getParameterValues().put("i8nStudiesRStageStudy", this.getText("summaries.study.maturityChange"));
    masterReport.getParameterValues().put("i8nStudiesRStrategicResults",
      this.getText("summaries.study.stratgicResultsLink"));
    masterReport.getParameterValues().put("i8nStudiesRSubIdos",
      this.getText("summaries.study.stratgicResultsLink.subIDOs"));
    masterReport.getParameterValues().put("i8nStudiesRSRFTargets",
      this.getText("summaries.study.stratgicResultsLink.srfTargets"));
    masterReport.getParameterValues().put("i8nStudiesRTopLevelCommentst",
      this.getText("summaries.study.stratgicResultsLink.comments"));
    masterReport.getParameterValues().put("i8nStudiesRGeographicScope", this.getText("study.geographicScope"));
    masterReport.getParameterValues().put("i8nStudiesRRegion", this.getText("study.region"));
    masterReport.getParameterValues().put("i8nStudiesRContries", this.getText("involveParticipants.countries"));
    masterReport.getParameterValues().put("i8nStudiesRScopeComments",
      this.getText("study.geographicScopeComments.readText"));
    masterReport.getParameterValues().put("i8nStudiesRKeyContributors",
      this.getText("summaries.study.keyContributors"));
    masterReport.getParameterValues().put("i8nStudiesRCrps", this.getText("study.keyContributors.crps"));
    masterReport.getParameterValues().put("i8nStudiesRFlagships", this.getText("study.keyContributors.flagships"));
    masterReport.getParameterValues().put("i8nStudiesRRegions", this.getText("study.keyContributors.regions"));
    masterReport.getParameterValues().put("i8nStudiesRInstitutions",
      this.getText("study.keyContributors.externalPartners"));
    masterReport.getParameterValues().put("i8nStudiesRElaborationOutcomeImpactStatement",
      this.getText("summaries.study.elaborationStatement"));
    masterReport.getParameterValues().put("i8nStudiesRReferenceText", this.getText("summaries.study.referencesCited"));
    masterReport.getParameterValues().put("i8nStudiesRReferencesFile",
      this.getText("study.referencesCitedAttach.readText"));
    masterReport.getParameterValues().put("i8nStudiesRQuantification", this.getText("summaries.study.quantification"));
    masterReport.getParameterValues().put("i8nStudiesRGenderDevelopment",
      this.getText("summaries.study.crossCuttingRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRGenderRelevance", this.getText("study.genderRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRYouthRelevance", this.getText("study.youthRelevance"));
    masterReport.getParameterValues().put("i8nStudiesRCapacityRelevance", this.getText("study.capDevRelevance"));
    masterReport.getParameterValues().put("i8nStudiesROtherCrossCuttingDimensions",
      this.getText("summaries.study.otherCrossCutting"));
    masterReport.getParameterValues().put("i8nStudiesRComunicationsMaterial",
      this.getText("summaries.study.communicationMaterials"));
    masterReport.getParameterValues().put("i8nStudiesRComunicationsFile",
      this.getText("study.communicationMaterialsAttach.readText"));
    masterReport.getParameterValues().put("i8nStudiesRContacts", this.getText("summaries.study.contacts"));
    masterReport.getParameterValues().put("i8nCaseStudiesRStudyProjects",
      this.getText("summaries.study.studyProjects"));
    return masterReport;
  }


  @Override
  public String execute() throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource;
      if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
        reportResource = resourceManager
          .createDirectly(this.getClass().getResource("/pentaho/crp/CaseStudiesExcel.prpt"), MasterReport.class);
      } else {
        reportResource = resourceManager.createDirectly(this.getClass().getResource("/pentaho/crp/StudiesPDF.prpt"),
          MasterReport.class);
      }
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();

      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String date = timezone.format(format) + "(GMT" + zone + ")";

      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(this.getSelectedYear()));
      sdf.addTable(masterQueryName, model);
      masterReport.setDataFactory(cdf);
      // Set i8n for pentaho
      masterReport = this.addi8nParameters(masterReport);
      // Get details band
      ItemBand masteritemBand = masterReport.getItemBand();
      // Create new empty subreport hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the subreports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      // System.out.println("Pentaho SubReports: " + hm);

      this.fillSubreport((SubReport) hm.get("case_studies"), "case_studies");

      if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
        ExcelReportUtil.createXLSX(masterReport, os);
        bytesXLSX = os.toByteArray();
      } else {
        PdfReportUtil.createPDF(masterReport, os);
        bytesPDF = os.toByteArray();
      }
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating CaseStudy" + this.getSelectedFormat() + ": " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }


  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "case_studies":
        model = this.getCaseStudiesTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  public byte[] getBytesPDF() {
    return bytesPDF;
  }

  public byte[] getBytesXLSX() {
    return bytesXLSX;
  }

  private TypedTableModel getCaseStudiesTableModel() {

    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "year", "policyAmount", "title", "status", "type", "outcomeImpactStatement",
        "isContributionText", "policyInvestimentType", "organizationType", "stageProcess", "stageStudy", "srfTargets",
        "subIdos", "topLevelComments", "geographicScope", "region", "countries", "scopeComments", "crps", "flagships",
        "regions", "institutions", "elaborationOutcomeImpactStatement", "referenceText", "referencesFile",
        "quantification", "genderRelevance", "youthRelevance", "capacityRelevance", "otherCrossCuttingDimensions",
        "comunicationsMaterial", "comunicationsFile", "contacts", "studyProjects", "isContribution",
        "isBudgetInvestment", "isStage1", "isRegional", "isNational", "hasreferencesFile", "hasCommunicationFile",
        "isOutcomeCaseStudy"},
      new Class[] {Long.class, Integer.class, Double.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, Boolean.class, Boolean.class, Boolean.class,
        Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class},
      0);
    List<ProjectExpectedStudyInfo> projectExpectedStudyInfos = new ArrayList<>();
    if (studyType.equals("all")) {
      projectExpectedStudyInfos =
        projectExpectedStudyInfoManager.getProjectExpectedStudyInfoByPhase(this.getSelectedPhase()).stream()
          .filter(si -> si.getProjectExpectedStudy().getYear() != null
            && si.getProjectExpectedStudy().getYear().intValue() == this.getSelectedYear())
          .collect(Collectors.toList());
    }
    if (studyType.equals("outcome_case_study")) {
      projectExpectedStudyInfos =
        projectExpectedStudyInfoManager.getProjectExpectedStudyInfoByPhase(this.getSelectedPhase()).stream()
          .filter(si -> si.getProjectExpectedStudy().getYear() != null
            && si.getProjectExpectedStudy().getYear().intValue() == this.getSelectedYear() && si.getStudyType() != null
            && si.getStudyType().getId() == 1)
          .collect(Collectors.toList());
    }
    if (studyType.equals("others")) {
      projectExpectedStudyInfos =
        projectExpectedStudyInfoManager.getProjectExpectedStudyInfoByPhase(this.getSelectedPhase()).stream()
          .filter(si -> si.getProjectExpectedStudy().getYear() != null
            && si.getProjectExpectedStudy().getYear().intValue() == this.getSelectedYear() && si.getStudyType() != null
            && si.getStudyType().getId() != 1)
          .collect(Collectors.toList());
    }
    if (projectExpectedStudyInfos != null && !projectExpectedStudyInfos.isEmpty()) {
      for (ProjectExpectedStudyInfo projectExpectedStudyInfo : projectExpectedStudyInfos) {
        Integer year = null;
        Double policyAmount = null;
        String title = null, status = null, type = null, outcomeImpactStatement = null, isContributionText = null,
          policyInvestimentType = null, organizationType = null, stageProcess = null, stageStudy = null,
          srfTargets = null, subIdos = null, topLevelComments = null, geographicScope = null, region = null,
          countries = null, scopeComments = null, crps = null, flagships = null, regions = null, institutions = null,
          elaborationOutcomeImpactStatement = null, referenceText = null, referencesFile = null, quantification = null,
          genderRelevance = null, youthRelevance = null, capacityRelevance = null, otherCrossCuttingDimensions = null,
          comunicationsMaterial = null, comunicationsFile = null, contacts = null, studyProjects = null;

        Boolean isContribution = false, isBudgetInvestment = false, isStage1 = false, isRegional = false,
          isNational = false, hasreferencesFile = false, hasCommunicationFile = false, isOutcomeCaseStudy = false;

        Long id = null;
        // Id
        id = projectExpectedStudyInfo.getProjectExpectedStudy().getId();
        // Year
        if (projectExpectedStudyInfo.getProjectExpectedStudy().getYear() != null) {
          year = projectExpectedStudyInfo.getProjectExpectedStudy().getYear();
        }
        // Title
        if (projectExpectedStudyInfo.getTitle() != null && !projectExpectedStudyInfo.getTitle().trim().isEmpty()) {
          title = projectExpectedStudyInfo.getTitle();
        }
        // Status
        if (projectExpectedStudyInfo.getStatus() != null) {
          status = ProjectStatusEnum.getValue(projectExpectedStudyInfo.getStatus()).getStatus();
        }
        // Type
        if (projectExpectedStudyInfo.getStudyType() != null) {
          type = projectExpectedStudyInfo.getStudyType().getName();
          if (projectExpectedStudyInfo.getStudyType().getId().intValue() == 1) {
            isOutcomeCaseStudy = true;
          }
        }
        // outcomeImpactStatement
        if (projectExpectedStudyInfo.getOutcomeImpactStatement() != null
          && !projectExpectedStudyInfo.getOutcomeImpactStatement().trim().isEmpty()) {
          outcomeImpactStatement = projectExpectedStudyInfo.getOutcomeImpactStatement();
        }
        // isContribution
        if (projectExpectedStudyInfo.getIsContribution() != null) {
          isContribution = projectExpectedStudyInfo.getIsContribution();
          isContributionText = projectExpectedStudyInfo.getIsContribution() ? "Yes" : "No";
          if (isContribution) {
            // Policy Investment and Amount
            if (projectExpectedStudyInfo.getRepIndPolicyInvestimentType() != null) {
              policyInvestimentType = projectExpectedStudyInfo.getRepIndPolicyInvestimentType().getName();
              if (projectExpectedStudyInfo.getRepIndPolicyInvestimentType().getId().equals(3l)) {
                isBudgetInvestment = true;
                if (projectExpectedStudyInfo.getPolicyAmount() != null) {
                  policyAmount = projectExpectedStudyInfo.getPolicyAmount();
                }
              }
            }
            // organizationType
            if (projectExpectedStudyInfo.getRepIndOrganizationType() != null) {
              organizationType = projectExpectedStudyInfo.getRepIndOrganizationType().getName();
            }
            // stageProcess and stageStudy
            if (projectExpectedStudyInfo.getRepIndStageProcess() != null) {
              stageProcess = projectExpectedStudyInfo.getRepIndStageProcess().getName();
              if (projectExpectedStudyInfo.getRepIndStageProcess().getId().equals(1l)) {
                isStage1 = true;
              }
            }
          }
        }
        if (projectExpectedStudyInfo.getRepIndStageStudy() != null) {
          stageStudy = projectExpectedStudyInfo.getRepIndStageStudy().getName();
        }

        // SubIdos
        List<ProjectExpectedStudySubIdo> subIdosList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudySubIdos().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> subIdoSet = new HashSet<>();
        if (subIdosList != null && subIdosList.size() > 0) {
          for (ProjectExpectedStudySubIdo studySrfTarget : subIdosList) {
            subIdoSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + studySrfTarget.getSrfSubIdo().getDescription());
          }
          subIdos = String.join("", subIdoSet);
        }

        // SRF Targets
        List<ProjectExpectedStudySrfTarget> studySrfTargets =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudySrfTargets().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> srfTargetSet = new HashSet<>();
        if (studySrfTargets != null && studySrfTargets.size() > 0) {
          for (ProjectExpectedStudySrfTarget studySrfTarget : studySrfTargets) {
            srfTargetSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp;● " + studySrfTarget.getSrfSloIndicator().getTitle());
          }
          srfTargets = String.join("", srfTargetSet);
        }

        // Comments
        if (projectExpectedStudyInfo.getTopLevelComments() != null
          && !projectExpectedStudyInfo.getTopLevelComments().trim().isEmpty()) {
          topLevelComments = HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getTopLevelComments());
        }

        // Geographic Scope
        if (projectExpectedStudyInfo.getRepIndGeographicScope() != null) {
          geographicScope = projectExpectedStudyInfo.getRepIndGeographicScope().getName();
          // Regional
          if (projectExpectedStudyInfo.getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {
            isRegional = true;
            if (projectExpectedStudyInfo.getRepIndRegion() != null) {
              region = projectExpectedStudyInfo.getRepIndRegion().getName();
            }
          }
          // Country
          if (!projectExpectedStudyInfo.getRepIndGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeGlobal())
            && !projectExpectedStudyInfo.getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
            isNational = true;
            List<ProjectExpectedStudyCountry> studyCountries =
              projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyCountries().stream()
                .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
            if (studyCountries != null && studyCountries.size() > 0) {
              Set<String> countriesSet = new HashSet<>();
              for (ProjectExpectedStudyCountry projectExpectedStudyCountry : studyCountries) {
                countriesSet
                  .add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + projectExpectedStudyCountry.getLocElement().getName());
              }
              countries = String.join("", countriesSet);
            }
          }
        }

        // Geographic Scope comment
        if (projectExpectedStudyInfo.getScopeComments() != null
          && !projectExpectedStudyInfo.getScopeComments().trim().isEmpty()) {
          scopeComments = HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getScopeComments());
        }

        // Key Contributions
        // CRPs/Platforms
        List<ProjectExpectedStudyCrp> studyCrpsList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyCrps().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> crpsSet = new HashSet<>();
        if (studyCrpsList != null && studyCrpsList.size() > 0) {
          for (ProjectExpectedStudyCrp studyCrp : studyCrpsList) {
            crpsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyCrp.getGlobalUnit().getComposedName());
          }
          crps = String.join("", crpsSet);
        }
        // Crp Programs
        List<ProjectExpectedStudyFlagship> studyProgramsList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyFlagships().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        // Flagships
        List<ProjectExpectedStudyFlagship> studyFlagshipList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        Set<String> flaghipsSet = new HashSet<>();
        if (studyFlagshipList != null && studyFlagshipList.size() > 0) {
          for (ProjectExpectedStudyFlagship studyFlagship : studyFlagshipList) {
            flaghipsSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          flagships = String.join("", flaghipsSet);
        }
        // Regional Programs
        List<ProjectExpectedStudyFlagship> studyRegionsList = studyProgramsList.stream()
          .filter(f -> f.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        Set<String> regionSet = new HashSet<>();
        if (studyRegionsList != null && studyRegionsList.size() > 0) {
          for (ProjectExpectedStudyFlagship studyFlagship : studyRegionsList) {
            regionSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyFlagship.getCrpProgram().getComposedName());
          }
          regions = String.join("", regionSet);
        }
        // External Partners
        List<ProjectExpectedStudyInstitution> studyInstitutionList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getProjectExpectedStudyInstitutions().stream()
            .filter(s -> s.isActive() && s.getPhase() != null && s.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList());
        Set<String> institutionSet = new HashSet<>();
        if (studyInstitutionList != null && studyInstitutionList.size() > 0) {
          for (ProjectExpectedStudyInstitution studyinstitution : studyInstitutionList) {
            institutionSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● " + studyinstitution.getInstitution().getComposedName());
          }
          institutions = String.join("", institutionSet);
        }

        // Elaboration of Outcome/Impact Statement
        if (projectExpectedStudyInfo.getElaborationOutcomeImpactStatement() != null
          && !projectExpectedStudyInfo.getElaborationOutcomeImpactStatement().trim().isEmpty()) {
          elaborationOutcomeImpactStatement =
            HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getElaborationOutcomeImpactStatement());
        }

        // References cited
        if (projectExpectedStudyInfo.getReferencesText() != null
          && !projectExpectedStudyInfo.getReferencesText().trim().isEmpty()) {
          referenceText = HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getReferencesText());
        }
        // Atached material
        if (projectExpectedStudyInfo.getReferencesFile() != null) {
          hasreferencesFile = true;
          referencesFile = projectExpectedStudyInfo.getReferencesFile().getFileName();
        }

        // Quantification
        if (projectExpectedStudyInfo.getQuantification() != null
          && !projectExpectedStudyInfo.getQuantification().trim().isEmpty()) {
          quantification = HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getQuantification());
        }

        // Gender, Youth, and Capacity Development
        // Gender
        if (projectExpectedStudyInfo.getGenderLevel() != null) {
          genderRelevance = projectExpectedStudyInfo.getGenderLevel().getName();

          if (!(isContribution && isStage1) && !projectExpectedStudyInfo.getGenderLevel().getId().equals(1l)
            && projectExpectedStudyInfo.getDescribeGender() != null
            && !projectExpectedStudyInfo.getDescribeGender().isEmpty()) {
            genderRelevance += "<br>" + this.getText("study.achievementsGenderRelevance.readText") + ": "
              + HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeGender());
          }
        }
        // Youth
        if (projectExpectedStudyInfo.getYouthLevel() != null) {
          youthRelevance = projectExpectedStudyInfo.getYouthLevel().getName();
          if (!(isContribution && isStage1) && !projectExpectedStudyInfo.getYouthLevel().getId().equals(1l)
            && projectExpectedStudyInfo.getDescribeYouth() != null
            && !projectExpectedStudyInfo.getDescribeYouth().isEmpty()) {
            youthRelevance += "<br>" + this.getText("study.achievementsYouthRelevance.readText") + ": "
              + HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeYouth());
          }
        }
        // Capacity Development
        if (projectExpectedStudyInfo.getCapdevLevel() != null) {
          capacityRelevance = projectExpectedStudyInfo.getCapdevLevel().getName();
          if (!(isContribution && isStage1) && !projectExpectedStudyInfo.getCapdevLevel().getId().equals(1l)
            && projectExpectedStudyInfo.getDescribeCapdev() != null
            && !projectExpectedStudyInfo.getDescribeCapdev().isEmpty()) {
            capacityRelevance += "<br>" + this.getText("study.achievementsCapDevRelevance.readText") + ": "
              + HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getDescribeCapdev());
          }
        }

        // Other cross-cutting dimensions
        if (projectExpectedStudyInfo.getOtherCrossCuttingDimensions() != null
          && !projectExpectedStudyInfo.getOtherCrossCuttingDimensions().trim().isEmpty()) {
          otherCrossCuttingDimensions =
            HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getOtherCrossCuttingDimensions());
        }

        // Communications materials
        if (projectExpectedStudyInfo.getComunicationsMaterial() != null
          && !projectExpectedStudyInfo.getComunicationsMaterial().trim().isEmpty()) {
          comunicationsMaterial = HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getComunicationsMaterial());
        }
        // Atached material
        if (projectExpectedStudyInfo.getOutcomeFile() != null) {
          hasCommunicationFile = true;
          comunicationsFile = projectExpectedStudyInfo.getOutcomeFile().getFileName();
        }

        // Contact person
        if (projectExpectedStudyInfo.getContacts() != null
          && !projectExpectedStudyInfo.getContacts().trim().isEmpty()) {
          contacts = HTMLParser.plainTextToHtml(projectExpectedStudyInfo.getContacts());
        }

        // Projects
        List<ExpectedStudyProject> studyProjectList =
          projectExpectedStudyInfo.getProjectExpectedStudy().getExpectedStudyProjects().stream()
            .filter(e -> e.isActive() && e.getPhase() != null && e.getPhase().equals(this.getSelectedPhase()))
            .sorted((sp1, sp2) -> sp2.getProject().getId().compareTo(sp1.getProject().getId()))
            .collect(Collectors.toList());
        Set<String> studyProjectSet = new HashSet<>();
        if (projectExpectedStudyInfo.getProjectExpectedStudy().getProject() != null) {
          studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● P"
            + projectExpectedStudyInfo.getProjectExpectedStudy().getProject().getId());
        }
        if (studyProjectList != null && studyProjectList.size() > 0) {
          for (ExpectedStudyProject studyProject : studyProjectList) {
            studyProjectSet.add("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● P" + studyProject.getProject().getId());
          }
        }
        if (studyProjectSet != null && !studyProjectSet.isEmpty()) {
          studyProjects = String.join("", studyProjectSet);
        }

        model.addRow(new Object[] {id, year, policyAmount, title, status, type, outcomeImpactStatement,
          isContributionText, policyInvestimentType, organizationType, stageProcess, stageStudy, srfTargets, subIdos,
          topLevelComments, geographicScope, region, countries, scopeComments, crps, flagships, regions, institutions,
          elaborationOutcomeImpactStatement, referenceText, referencesFile, quantification, genderRelevance,
          youthRelevance, capacityRelevance, otherCrossCuttingDimensions, comunicationsMaterial, comunicationsFile,
          contacts, studyProjects, isContribution, isBudgetInvestment, isStage1, isRegional, isNational,
          hasreferencesFile, hasCommunicationFile, isOutcomeCaseStudy});
      }
    }

    return model;

  }

  public String getCaseStudyUrl(String project) {
    return config.getDownloadURL() + "/" + this.getCaseStudyUrlPath(project).replace('\\', '/');
  }

  public String getCaseStudyUrlPath(String project) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project + File.separator + "caseStudy"
      + File.separator;
  }

  @Override
  public int getContentLength() {
    if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
      return bytesXLSX.length;
    } else {
      return bytesPDF.length;
    }
  }


  @Override
  public String getContentType() {
    if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
      return "application/xlsx";
    } else {
      return "application/pdf";
    }

  }

  @SuppressWarnings("unused")
  private File getFile(String fileName) {
    // Get file from resources folder
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    return file;
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("OutcomesCaseStudiesSummary-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
      fileName.append(".xlsx");
    } else {
      fileName.append(".pdf");
    }
    return fileName.toString();
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
        inputStream = new ByteArrayInputStream(bytesXLSX);
      } else {
        inputStream = new ByteArrayInputStream(bytesPDF);
      }
    }
    return inputStream;
  }

  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "isReporting", "cycle"},
      new Class[] {String.class, String.class, String.class, Boolean.class, String.class});
    model.addRow(new Object[] {center, date, year, this.getSelectedPhase().isReporting(), this.getSelectedCycle()});
    return model;
  }

  public String getSelectedFormat() {
    return selectedFormat;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    this.setGeneralParameters();
    this.setSelectedFormat((StringUtils.trim(parameters.get(APConstants.SUMMARY_FORMAT).getMultipleValues()[0])));
    // Study type
    try {
      studyType = StringUtils.trim(parameters.get(APConstants.SUMMARY_STUDY_TYPE).getMultipleValues()[0]);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.SUMMARY_STUDY_TYPE
        + " parameter. Parameter will be set as All. Exception: " + e.getMessage());
      studyType = "All";
    }
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }


  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public void setSelectedFormat(String selectedFormat) {
    this.selectedFormat = selectedFormat;
  }

}
