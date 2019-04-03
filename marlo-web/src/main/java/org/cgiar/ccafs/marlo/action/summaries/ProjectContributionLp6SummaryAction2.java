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
import org.cgiar.ccafs.marlo.data.manager.CrossCuttingScoringManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;
import org.cgiar.ccafs.marlo.utils.APConfig;

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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.ibm.icu.util.Calendar;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectContributionLp6SummaryAction2 extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(ProjectContributionLp6SummaryAction2.class);
  // Parameters
  private long startTime;
  private Set<Deliverable> currentPhaseDeliverables = new HashSet<>();
  private String ppa;
  // Store deliverables with year and type HashMap<Deliverable, List<year, type>>
  HashMap<Integer, Set<Deliverable>> deliverablePerYearList = new HashMap<Integer, Set<Deliverable>>();
  HashMap<String, Set<Deliverable>> deliverablePerTypeList = new HashMap<String, Set<Deliverable>>();
  Set<Long> projectsList = new HashSet<Long>();
  private String showAllYears;


  // Managers
  private final GenderTypeManager genderTypeManager;

  private List<ProjectLp6Contribution> projectLp6Contributions;
  private final CrpProgramManager crpProgramManager;


  private final CrossCuttingScoringManager crossCuttingScoringManager;
  private final ProjectLp6ContributionManager projectLp6ContributionManager;
  private final DeliverableManager deliverableManager;
  private final CrpPpaPartnerManager crpPpaPartnerManager;
  private final ResourceManager resourceManager;
  private final DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager;
  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public ProjectContributionLp6SummaryAction2(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    GenderTypeManager genderTypeManager, CrpProgramManager crpProgramManager,
    ProjectLp6ContributionManager projectLp6ContributionManager, CrossCuttingScoringManager crossCuttingScoringManager,
    CrpPpaPartnerManager crpPpaPartnerManager, ResourceManager resourceManager, ProjectManager projectManager,
    DeliverableManager deliverableManager, DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.genderTypeManager = genderTypeManager;
    this.crpProgramManager = crpProgramManager;
    this.crossCuttingScoringManager = crossCuttingScoringManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.resourceManager = resourceManager;
    this.projectLp6ContributionManager = projectLp6ContributionManager;
    this.deliverableManager = deliverableManager;
    this.deliverableCrossCuttingMarkerManager = deliverableCrossCuttingMarkerManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    /*
     * Deliverables
     */
    masterReport.getParameterValues().put("i8nDeliverableID", this.getText("searchTerms.deliverableId"));
    masterReport.getParameterValues().put("i8nDeliverableTitle",
      this.getText("summaries.deliverable.deliverableTitle"));
    masterReport.getParameterValues().put("i8nDeliverableDescription",
      this.getText("summaries.deliverable.deliverableDescription"));
    masterReport.getParameterValues().put("i8nKeyOutput",
      this.getText("project.deliverable.generalInformation.keyOutput"));
    masterReport.getParameterValues().put("i8nExpectedYear", this.getText("summaries.deliverable.expectedYear"));
    masterReport.getParameterValues().put("i8nType", this.getText("deliverable.type"));
    masterReport.getParameterValues().put("i8nSubType", this.getText("deliverable.subtype"));
    masterReport.getParameterValues().put("i8nGender", this.getText("summaries.gender"));
    masterReport.getParameterValues().put("i8nYouth", this.getText("summaries.youth"));
    masterReport.getParameterValues().put("i8nCap", this.getText("summaries.capacityDevelopment"));
    masterReport.getParameterValues().put("i8nStatus", this.getText("project.deliverable.generalInformation.status"));
    masterReport.getParameterValues().put("i8nProjectID", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nCoas", this.getText("deliverable.coas"));
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nRegions", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nIndividual", this.getText("deliverable.individual"));
    masterReport.getParameterValues().put("i8nPartnersResponsible", this.getText("deliverable.managing"));
    masterReport.getParameterValues().put("i8nShared", this.getText("deliverable.shared"));
    masterReport.getParameterValues().put("i8nOpenFundingSourcesID",
      this.getText("summaries.fundingSource.openFundingSource"));
    masterReport.getParameterValues().put("i8nFinishedFundingSourcesID",
      this.getText("summaries.fundingSource.finishedFundingSource"));
    masterReport.getParameterValues().put("i8nFundingWindows", this.getText("deliverable.fundingWindows"));
    masterReport.getParameterValues().put("i8nNewExpectedYear", this.getText("deliverable.newExpectedYear"));
    masterReport.getParameterValues().put("i8nOutcomes", this.getText("impactPathway.menu.hrefOutcomes"));
    masterReport.getParameterValues().put("i8nManagingResponsible", this.getText("deliverable.project.managing"));
    masterReport.getParameterValues().put("i8nProjectLeadPartner", this.getText("summaries.deliverable.leadPartner"));
    masterReport.getParameterValues().put("i8nGeographicScope", this.getText("deliverable.geographicScope"));
    masterReport.getParameterValues().put("i8nCountry", this.getText("deliverable.countries"));
    masterReport.getParameterValues().put("i8nRegion", this.getText("deliverable.region"));
    masterReport.getParameterValues().put("i8nProjectContribution",
      this.getText("summaries.lp6contribution.projectContribution"));


    masterReport.getParameterValues().put("i8nHeader",
      this.getText("summaries.lp6contribution.header", new String[] {this.getLoggedCrp().getAcronym()}));

    masterReport.getParameterValues().put("i8nSummaryDescription",
      this.getText("summaries.lp6contribution.description"));
    masterReport.getParameterValues().put("i8nProject", this.getText("summaries.lp6contribution.projectName"));
    masterReport.getParameterValues().put("i8nNarrative", this.getText("summaries.lp6contribution.narrative"));
    masterReport.getParameterValues().put("i8nDeliverables", this.getText("summaries.lp6contribution.deliverables"));
    masterReport.getParameterValues().put("i8nGeographicScope",
      this.getText("summaries.lp6contribution.geographicScope"));

    masterReport.getParameterValues().put("i8nWorkingAcross",
      this.getText("summaries.lp6contribution.isWorkingAcrossFlagships"));

    masterReport.getParameterValues().put("i8nWorkingAcrossNarrative",
      this.getText("summaries.lp6contribution.workingAcrossFlagships"));

    masterReport.getParameterValues().put("i8nUndertakingEfforts",
      this.getText("summaries.lp6contribution.isUndertakingEfforts"));

    masterReport.getParameterValues().put("i8nUndertakingEffortsNarrative",
      "Project " + this.getText("summaries.lp6contribution.effortsUndertaking"));

    masterReport.getParameterValues().put("i8nProviding",
      this.getText("summaries.lp6contribution.isProvidingPathways"));
    masterReport.getParameterValues().put("i8nKeyLearnings", this.getText("summaries.lp6contribution.keyLearnings"));
    masterReport.getParameterValues().put("i8nTop3", this.getText("summaries.lp6contribution.top3Partnerts"));
    masterReport.getParameterValues().put("i8nUndertakingCSA",
      this.getText("summaries.lp6contribution.isUndertakingEffortsCSA"));
    masterReport.getParameterValues().put("i8nUndertakingCSAN",
      this.getText("summaries.lp6contribution.undertakingEffortsCSA"));
    masterReport.getParameterValues().put("i8nUndertaking",
      this.getText("summaries.lp6contribution.isUndertakingInitiative"));
    masterReport.getParameterValues().put("i8nUndertakingN",
      this.getText("summaries.lp6contribution.undertakingInitiative"));


    return masterReport;
  }

  public String convertBoolean(Boolean value) {
    String result = null;
    if (value == true) {
      result = "Yes";
    } else if (value == false) {
      result = "No";
    }
    return result;
  }

  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();

    try {
      Resource reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/ProjectLp6Contribution2.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();

      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel();
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
      this.fillSubreport((SubReport) hm.get("details"), "details");
      masterReport.getParameterValues().put("total_deliv", currentPhaseDeliverables.size());
      masterReport.getParameterValues().put("total_projects", projectsList.size());
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ExpectedDeliverables " + e.getMessage());
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
      case "details":
        model = this.getDeliverablesDetailsTableModel();
        break;
      case "summary":
        model = this.getDeliverablesPerYearTableModel();
        break;
      case "summaryPerType":
        model = this.getDeliverablesPerTypeTableModel();
        break;

    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  private int getCalendarFromDate(Date date) {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      return cal.get(Calendar.YEAR);
    } catch (NullPointerException e) {
      return 0;
    }
  }

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  private TypedTableModel getDeliverablesDetailsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"project", "narrative", "deliverables", "geographicScope", "workingAcross",
        "workingAcrossNarrative", "undertakingEfforts", "undertakingEffortsNarrative", "providing", "keyLearnings",
        "top3", "undertakingCSA", "undertakingCSAN", "undertaking", "undertakingN", "id"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);

    String deliverableId = null, deliverableTitle = null, completionYear = null, deliverableType = null,
      deliverableSubType = null, keyOutput = null, delivStatus = null, delivNewYear = null, projectID = null,
      projectTitle = null, ppaRespondible = null, projectClusterActivities = null, flagships = null, regions = null,
      individual = null, partnersResponsible = null, shared = null, openFS = null, fsWindows = null, outcomes = null,
      projectLeadPartner = null, managingResponsible = null, phaseID = null, finishedFS = null, gender = null,
      youth = null, cap = null, deliverableDescription = null, geographicScope2 = null, region = null, country = null;


    for (ProjectLp6Contribution projectLp6Contribution : projectLp6Contributions) {
      String projectId = "", narrativeLp6 = "", deliverables = "", geographicScope = "", workingAcross = "",
        workingAcrossNarrative = "", undertakingEfforts = "", undertakingEffortsNarrative = "", providing = "",
        keyOutputs = "", top3 = "", undertakingCSA = "", undertakingCSANarrative = "", initiativeRelated = "",
        initiativeRelatedNarrative = "", id = "";

      id = projectLp6Contribution.getProject() != null && projectLp6Contribution.getProject().getId() != null
        ? projectLp6Contribution.getProject().getId() + "" : null;
      /*
       * projectId = "=HYPERLINK(\"" + this.getBaseUrl() + "/projects/" + this.getCurrentCrp().getAcronym() + "/"
       * + "contributionsLP6.do?projectID=" + projectLp6Contribution.getProject().getId() + "&edit=true&phaseID="
       * + this.getSelectedPhase().getId() + "\",\"" + projectLp6Contribution.getProject().getId() + "\")";
       */

      projectId =
        this.getBaseUrl() + "/projects/" + this.getCurrentCrp().getAcronym() + "/" + "contributionsLP6.do?projectID="
          + projectLp6Contribution.getProject().getId() + "&edit=true&phaseID=" + this.getSelectedPhase().getId();


      narrativeLp6 =
        projectLp6Contribution.getNarrative() != null && !projectLp6Contribution.getNarrative().trim().isEmpty()
          ? projectLp6Contribution.getNarrative() : null;

      /*
       * List<ProjectLp6ContributionDeliverable> deliverableList = new ArrayList<ProjectLp6ContributionDeliverable>();
       * if (deliverableManager.findAll() != null) {
       * deliverableList = deliverableLp6Manager.findAll().stream()
       * .filter(d -> d.getProjectLp6Contribution().getId().equals(projectLp6Contribution.getId())
       * && d.getPhase().getId().equals(this.getSelectedPhase().getId()))
       * .collect(Collectors.toList());
       * }
       * for (ProjectLp6ContributionDeliverable deliverable : deliverableList) {
       * if (deliverable.getId() != null) {
       * deliverables += deliverable.getId() + ", ";
       * }
       * }
       */

      /***/

      if (projectLp6Contribution.getDeliverables() == null) {
        projectLp6Contribution.setDeliverables(new ArrayList<>());
      }
      List<ProjectLp6ContributionDeliverable> deliverableList =
        projectLp6Contribution.getProjectLp6ContributionDeliverable().stream()
          .filter(ld -> ld.isActive() && ld.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
      for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : deliverableList) {
        if (projectLp6ContributionDeliverable.getDeliverable() != null
          && projectLp6ContributionDeliverable.getDeliverable().getId() != null) {
          projectLp6ContributionDeliverable.setDeliverable(
            deliverableManager.getDeliverableById(projectLp6ContributionDeliverable.getDeliverable().getId()));
        }
      }

      for (ProjectLp6ContributionDeliverable deliverable : deliverableList) {
        if (deliverable.getId() != null) {
          deliverables += deliverable.getId() + ", ";
        }
      }

      try {
        if (deliverables.contains(",")) {
          deliverables = deliverables.substring(0, deliverables.length() - 2);
        }
      } catch (Exception e) {

      }
      /****/
      if (deliverables != null && deliverables.isEmpty()) {
        deliverables = null;
      }
      geographicScope = projectLp6Contribution.getGeographicScopeNarrative() != null
        && !projectLp6Contribution.getGeographicScopeNarrative().trim().isEmpty()
          ? projectLp6Contribution.getGeographicScopeNarrative() : null;

      workingAcross = projectLp6Contribution.isWorkingAcrossFlagships() != null
        ? this.convertBoolean(projectLp6Contribution.isWorkingAcrossFlagships()) + "" : null;

      workingAcrossNarrative = projectLp6Contribution.getWorkingAcrossFlagshipsNarrative() != null
        && !projectLp6Contribution.getWorkingAcrossFlagshipsNarrative().isEmpty()
          ? projectLp6Contribution.getWorkingAcrossFlagshipsNarrative() : null;

      if (workingAcross != null && workingAcross.equals("Yes") && workingAcrossNarrative == null) {
        workingAcrossNarrative = "<Not Specified>";
      }

      undertakingEfforts = projectLp6Contribution.isUndertakingEffortsLeading() != null
        ? this.convertBoolean(projectLp6Contribution.isUndertakingEffortsLeading()) + "" : null;

      undertakingEffortsNarrative = projectLp6Contribution.getUndertakingEffortsLeadingNarrative() != null
        && !projectLp6Contribution.getUndertakingEffortsLeadingNarrative().isEmpty()
          ? projectLp6Contribution.getUndertakingEffortsLeadingNarrative() + "" : null;

      if (undertakingEfforts != null && undertakingEfforts.equals("Yes") && undertakingEffortsNarrative == null) {
        undertakingEffortsNarrative = "<Not Specified>";
      }

      providing = projectLp6Contribution.isProvidingPathways() != null
        ? this.convertBoolean(projectLp6Contribution.isProvidingPathways()) + "" : null;

      keyOutputs = projectLp6Contribution.getProvidingPathwaysNarrative() != null
        && !projectLp6Contribution.getProvidingPathwaysNarrative().isEmpty()
          ? projectLp6Contribution.getProvidingPathwaysNarrative() + "" : null;

      if (providing != null && providing.equals("Yes") && keyOutputs == null) {
        keyOutputs = "<Not Specified>";
      }

      top3 = projectLp6Contribution.getTopThreePartnershipsNarrative() != null
        && !projectLp6Contribution.getTopThreePartnershipsNarrative().isEmpty()
          ? projectLp6Contribution.getTopThreePartnershipsNarrative() + "" : null;

      undertakingCSA = projectLp6Contribution.isUndertakingEffortsCsa() != null
        ? this.convertBoolean(projectLp6Contribution.isUndertakingEffortsCsa()) + "" : null;

      undertakingCSANarrative = projectLp6Contribution.getUndertakingEffortsCsaNarrative() != null
        && !projectLp6Contribution.getUndertakingEffortsCsaNarrative().isEmpty()
          ? projectLp6Contribution.getUndertakingEffortsCsaNarrative() + "" : null;

      if (undertakingCSA != null && undertakingCSA.equals("Yes") && undertakingCSANarrative == null) {
        undertakingCSANarrative = "<Not Specified>";
      }

      initiativeRelated = projectLp6Contribution.isInitiativeRelated() != null
        ? this.convertBoolean(projectLp6Contribution.isUndertakingEffortsCsa()) + "" : null;

      initiativeRelatedNarrative = projectLp6Contribution.getInitiativeRelatedNarrative() != null
        && !projectLp6Contribution.getInitiativeRelatedNarrative().isEmpty()
          ? projectLp6Contribution.getInitiativeRelatedNarrative() + "" : null;

      if (initiativeRelated != null && initiativeRelated.equals("Yes") && initiativeRelatedNarrative == null) {
        initiativeRelatedNarrative = "<Not Specified>";
      }

      model.addRow(new Object[] {projectId, narrativeLp6, deliverables, geographicScope, workingAcross,
        workingAcrossNarrative, undertakingEfforts, undertakingEffortsNarrative, providing, keyOutputs, top3,
        undertakingCSA, undertakingCSANarrative, initiativeRelated, initiativeRelatedNarrative, id});
    }
    return model;

  }

  private TypedTableModel getDeliverablesPerTypeTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"deliverableType", "delivetableTotal"},
      new Class[] {String.class, Integer.class}, 0);
    Integer grandTotalTypes = 0;
    for (String type : deliverablePerTypeList.keySet()) {
      grandTotalTypes += deliverablePerTypeList.get(type).size();
    }
    SortedSet<String> keys = new TreeSet<String>(deliverablePerTypeList.keySet());
    for (String type : keys) {
      Integer totalType = deliverablePerTypeList.get(type).size();
      Float percentageOfTotal = (totalType * 100f) / grandTotalTypes;
      model.addRow(type + " - " + String.format("%.02f", percentageOfTotal) + "%", totalType);
    }
    return model;
  }


  private TypedTableModel getDeliverablesPerYearTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"deliverableYear", "delivetableTotal"},
      new Class[] {String.class, Integer.class}, 0);
    SortedSet<Integer> keys = new TreeSet<Integer>(deliverablePerYearList.keySet());
    for (Integer year : keys) {
      model.addRow(year, deliverablePerYearList.get(year).size());
    }
    return model;
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

    fileName.append("ProjectContributionToLP6-");

    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedCycle() + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
    return fileName.toString();
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }


  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible", "showDescription", "cycle"},
        new Class[] {String.class, String.class, String.class, Boolean.class, Boolean.class, String.class});
    String center = this.getLoggedCrp().getAcronym();

    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String date = timezone.format(format) + "(GMT" + zone + ")";
    String year = this.getSelectedYear() + "";
    model.addRow(new Object[] {center, date, year, this.hasProgramnsRegions(),
      this.hasSpecificities(APConstants.CRP_REPORTS_DESCRIPTION), this.getSelectedCycle()});
    return model;
  }

  public String getShowAllYears() {
    return showAllYears;
  }

  @Override
  public void prepare() {
    // Get PPA for filtering

    projectLp6Contributions = projectLp6ContributionManager.findAll();

    if (projectLp6Contributions == null) {
      projectLp6Contributions = new ArrayList<>();
    }


    if (projectLp6Contributions != null) {
      projectLp6Contributions = projectLp6Contributions.stream()
        .filter(
          c -> c.isActive() && c.isContribution() == true && c.getPhase().getId().equals(this.getActualPhase().getId()))
        .collect(Collectors.toList());
    }


    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

  public void setShowAllYears(String showAllYears) {
    this.showAllYears = showAllYears;
  }


}
