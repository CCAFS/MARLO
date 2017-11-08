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

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

public class FundingSourcesSummaryAction extends BaseAction implements Summary {


  private static final Logger LOG = LoggerFactory.getLogger(FundingSourcesSummaryAction.class);


  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  // Variables
  private GlobalUnit loggedCrp;
  private int year;

  private String cycle;
  private Boolean showPIEmail;
  private Boolean showIfpriDivision;
  private Boolean showSheet3;
  private Set<Project> fundingSourceProjects = new HashSet<>();
  private Set<Project> allProjects = new HashSet<>();
  private long startTime;
  private Boolean hasW1W2Co;
  // Managers
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;


  private CrpProgramManager programManager;


  private ProjectManager projectManager;
  private DeliverableFundingSourceManager deliverableFundingSourceManager;
  private PhaseManager phaseManager;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public FundingSourcesSummaryAction(APConfig config, GlobalUnitManager crpManager, CrpProgramManager programManager,
    ProjectManager projectManager, DeliverableFundingSourceManager deliverableFundingSourceManager,
    PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.programManager = programManager;
    this.projectManager = projectManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.phaseManager = phaseManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    // Master List
    masterReport.getParameterValues().put("i8nFundingSources", this.getText("menu.fundingSources"));
    masterReport.getParameterValues().put("i8nID", this.getText("fundingSource.fundingSourceID"));
    masterReport.getParameterValues().put("i8nTitle", this.getText("projectCofunded.title.readText"));
    masterReport.getParameterValues().put("i8nSummary", this.getText("projectCofunded.description.readText"));
    masterReport.getParameterValues().put("i8nAgreementStatus", this.getText("projectCofunded.agreementStatus"));
    masterReport.getParameterValues().put("i8nStartDate", this.getText("projectCofunded.startDate"));
    masterReport.getParameterValues().put("i8nEndDate", this.getText("projectCofunded.endDate"));
    masterReport.getParameterValues().put("i8nFinanceCode", this.getText("projectCofunded.financeCode"));
    masterReport.getParameterValues().put("i8nContactName", this.getText("projectCofunded.contactName"));
    masterReport.getParameterValues().put("i8nContactEmail", this.getText("projectCofunded.contactEmail"));
    masterReport.getParameterValues().put("i8nFundingWindow", this.getText("projectCofunded.type"));
    masterReport.getParameterValues().put("i8nDonor", this.getText("projectCofunded.donor"));
    masterReport.getParameterValues().put("i8nBudgetYear",
      this.getText("fundingSource.budget", new String[] {String.valueOf(year)}));
    masterReport.getParameterValues().put("i8nBudgetYearProjects",
      this.getText("fundingSource.budgetYearAllocated", new String[] {String.valueOf(year)}));
    masterReport.getParameterValues().put("i8nDeliverableIDs",
      this.getText("fundingSource.deliverableIDs", new String[] {String.valueOf(year)}));
    masterReport.getParameterValues().put("i8nProjects", this.getText("caseStudy.projects"));
    masterReport.getParameterValues().put("i8nCoas", this.getText("deliverable.coas"));
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nContractProposal", this.getText("fundingSource.contractProposal"));
    masterReport.getParameterValues().put("i8nGlobalDimension", this.getText("fundingSource.globalDimension"));
    masterReport.getParameterValues().put("i8nRegionalDimension", this.getText("fundingSource.regionalDimension"));
    masterReport.getParameterValues().put("i8nSpecificCountries",
      this.getText("projectCofunded.listCountries.readText"));
    masterReport.getParameterValues().put("i8nSheet3Title", this.getText("summaries.fundingSource.sheet3Title"));
    masterReport.getParameterValues().put("i8nSheet3Description",
      this.getText("summaries.fundingSource.sheet3Description", new String[] {String.valueOf(year)}));


    // Funding Sources by Projects
    masterReport.getParameterValues().put("i8nProjectID", this.getText("searchTerms.projectId"));

    return masterReport;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    try {
      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/FundingSourcesSummary.prpt"), MasterReport.class);


      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = loggedCrp.getAcronym();


      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String current_date = timezone.format(format) + "(GMT" + zone + ")";

      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, current_date);
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

      this.fillSubreport((SubReport) hm.get("funding_sources"), "funding_sources");
      this.fillSubreport((SubReport) hm.get("funding_sources_projects"), "funding_sources_projects");
      // Add parameter to show or not the sheet 3

      // Add all projects
      Phase phase = phaseManager.findCycle(cycle, year, loggedCrp.getId().longValue());
      if (phase != null) {
        for (ProjectPhase projectPhase : phase.getProjectPhases()) {
          allProjects.add((projectPhase.getProject()));
        }
      }
      if (allProjects.isEmpty()) {
        allProjects = loggedCrp.getProjects().stream().filter(c -> c.isActive()).collect(Collectors.toSet());
      }
      // delete projects with FS
      for (Project project : fundingSourceProjects) {
        allProjects.remove(project);
      }
      this.setShowSheet3(!allProjects.isEmpty() ? true : false);
      masterReport.getParameterValues().put("showSheet3", this.getShowSheet3());
      if (this.getShowSheet3()) {
        this.fillSubreport((SubReport) hm.get("funding_sources_no_projects"), "funding_sources_no_projects");
      }

      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating FundingSources " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;

  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "funding_sources":
        model = this.getFundingSourcesTableModel();
        break;
      case "funding_sources_projects":
        model = this.getFundingSourcesProjectsTableModel();
        break;
      case "funding_sources_no_projects":
        model = this.getFundingSourcesNoProjectsTableModel();
        break;

    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  /**
   * Get all subreports and store then in a hash map.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm List to populate with subreports found
   * @param itemBand details section in pentaho
   */
  private void getAllSubreports(HashMap<String, Element> hm, ItemBand itemBand) {
    int elementCount = itemBand.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = itemBand.getElement(i);
      // verify if the item is a SubReport
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
          // If report footer is not null check for subreports
          if (((SubReport) e).getReportFooter().getElementCount() != 0) {
            this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
          }
        }
      }
      // If is a band, find the subreport if exist
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  /**
   * Get all subreports in the band.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm
   * @param band
   */
  private void getBandSubreports(HashMap<String, Element> hm, Band band) {
    int elementCount = band.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = band.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        // If report footer is not null check for subreports
        if (((SubReport) e).getReportFooter().getElementCount() != 0) {
          this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
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


  public String getCycle() {
    return cycle;
  }


  /**
   * This method is used to get the file from resources. In this case the Pentaho *.prpt
   * 
   * @param fileName
   * @return File: *.prpt from resources
   */
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
    fileName.append("FundingSourcesSummary-");
    fileName.append(this.year + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");

    return fileName.toString();

  }


  private void getFooterSubreports(HashMap<String, Element> hm, ReportFooter reportFooter) {

    int elementCount = reportFooter.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = reportFooter.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());

        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  public String getFundingSourceFileURL() {
    return config.getDownloadURL() + "/" + this.getFundingSourceUrlPath().replace('\\', '/');
  }

  private TypedTableModel getFundingSourcesNoProjectsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "title", "summary", "start_date", "end_date", "coas", "flagships"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);
    SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM yyyy");
    for (Project project : allProjects.stream().sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
      .collect(Collectors.toList())) {
      String projectId = project.getId().toString();
      String projectTitle =
        project.getTitle() != null && !project.getTitle().trim().isEmpty() ? project.getTitle() : null;
      String projectSummary =
        project.getSummary() != null && !project.getSummary().trim().isEmpty() ? project.getSummary() : null;
      String startDate = project.getStartDate() != null ? dateFormatter.format(project.getStartDate()) : null;
      String endDate = project.getEndDate() != null ? dateFormatter.format(project.getEndDate()) : null;
      // set flagships and coas
      String flagships = null;
      String coas = null;
      List<String> flagshipsList = new ArrayList<String>();
      List<String> coasList = new ArrayList<String>();
      // get Flagships related to the project sorted by acronym
      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
        .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList())) {
        flagshipsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym());
      }
      // get CoAs related to the project sorted by acronym
      if (project.getProjectClusterActivities() != null) {
        for (ProjectClusterActivity projectClusterActivity : project.getProjectClusterActivities().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          coasList.add(projectClusterActivity.getCrpClusterOfActivity().getIdentifier());
        }
      }

      // Remove duplicates
      Set<String> flagshipsHash = new LinkedHashSet<String>(flagshipsList);
      Set<String> coasHash = new LinkedHashSet<String>(coasList);

      // Add flagships
      for (String flagshipString : flagshipsHash.stream().collect(Collectors.toList())) {
        if (flagships == null || flagships.isEmpty()) {
          flagships = flagshipString;
        } else {
          flagships += "\n " + flagshipString;
        }
      }
      // Add coas
      for (String coaString : coasHash.stream().collect(Collectors.toList())) {
        if (coas == null || coas.isEmpty()) {
          coas = coaString;
        } else {
          coas += "\n " + coaString;
        }
      }
      model.addRow(new Object[] {projectId, projectTitle, projectSummary, startDate, endDate, coas, flagships});
    }
    return model;
  }

  private TypedTableModel getFundingSourcesProjectsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"fs_title", "fs_id", "finance_code", "lead_partner", "fs_window", "project_id", "total_budget",
        "flagships", "coas", "donor", "directDonor", "global_dimension", "regional_dimension", "specific_countries"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, Double.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);

    for (FundingSource fundingSource : loggedCrp.getFundingSources().stream()
      .filter(fs -> fs.isActive() && fs.getBudgetType() != null).collect(Collectors.toList())) {

      String fsTitle = fundingSource.getTitle();
      Long fsId = fundingSource.getId();
      String financeCode = fundingSource.getFinanceCode();
      String donor = null;


      String fsWindow = fundingSource.getBudgetType().getName();
      if (hasW1W2Co && fundingSource.getW1w2() != null && fundingSource.getW1w2()) {
        fsWindow = "W1/W2 Co-Financing";
      }
      if (fundingSource.getInstitution() != null) {
        donor = fundingSource.getInstitution().getComposedName();
      }

      for (ProjectBudget projectBudget : fundingSource.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getProject() != null && pb.getProject().isActive()
          && pb.getProject().getStatus() != null
          && pb.getProject().getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
        .collect(Collectors.toList())) {
        String leadPartner = "";
        String projectId = "";
        Double totalBudget = 0.0;
        String flagships = null;
        String coas = null;

        projectId = projectBudget.getProject().getId().toString();
        // add to list of projects
        fundingSourceProjects.add(projectBudget.getProject());
        if (projectId != null && !projectId.isEmpty()) {
          // get Flagships related to the project sorted by acronym
          for (ProjectFocus projectFocuses : projectBudget.getProject().getProjectFocuses().stream()
            .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            if (flagships == null || flagships.isEmpty()) {
              flagships = programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            } else {
              flagships +=
                "\n " + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            }
          }

          // get CoAs related to the project sorted by acronym
          if (projectBudget.getProject().getProjectClusterActivities() != null) {
            for (ProjectClusterActivity projectClusterActivity : projectBudget.getProject()
              .getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
              if (coas == null || coas.isEmpty()) {
                coas = projectClusterActivity.getCrpClusterOfActivity().getIdentifier();
              } else {
                coas += "\n " + projectClusterActivity.getCrpClusterOfActivity().getIdentifier();
              }
            }
          }

        }

        if (projectBudget.getInstitution() != null) {
          leadPartner = projectBudget.getInstitution().getComposedName();
        }

        totalBudget = projectBudget.getAmount();

        String directDonor = "";
        if (fundingSource.getDirectDonor() != null) {
          directDonor = fundingSource.getDirectDonor().getComposedName();
        }

        // Funding sources locations
        String globalDimension = null;
        globalDimension = fundingSource.isGlobal() ? "Yes" : "No";

        String regionalDimension = "";
        // Regions
        for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingSourceLocations().stream().filter(
          fl -> fl.isActive() && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 1)
          .collect(Collectors.toList())) {
          if (regionalDimension.isEmpty()) {
            regionalDimension += fundingSourceLocation.getLocElement().getName();
          } else {
            regionalDimension += ", " + fundingSourceLocation.getLocElement().getName();
          }
        }
        // Scope Regions
        for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingSourceLocations().stream()
          .filter(fl -> fl.isActive() && fl.getLocElementType() != null && fl.getLocElement() == null)
          .collect(Collectors.toList())) {
          if (regionalDimension.isEmpty()) {
            regionalDimension += fundingSourceLocation.getLocElementType().getName();
          } else {
            regionalDimension += ", " + fundingSourceLocation.getLocElementType().getName();
          }
        }

        if (regionalDimension.isEmpty()) {
          regionalDimension = "No";
        }

        String specificCountries = "";
        for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingSourceLocations().stream().filter(
          fl -> fl.isActive() && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 2)
          .collect(Collectors.toList())) {
          if (specificCountries.isEmpty()) {
            specificCountries += fundingSourceLocation.getLocElement().getName();
          } else {
            specificCountries += ", " + fundingSourceLocation.getLocElement().getName();
          }
        }

        if (specificCountries.isEmpty()) {
          specificCountries = null;
        }

        model.addRow(new Object[] {fsTitle, fsId, financeCode, leadPartner, fsWindow, projectId, totalBudget, flagships,
          coas, donor, directDonor, globalDimension, regionalDimension, specificCountries});
      }

    }
    return model;
  }


  private TypedTableModel getFundingSourcesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"fs_title", "fs_id", "finance_code", "lead_partner", "fs_window", "project_id", "total_budget",
        "summary", "start_date", "end_date", "contract", "status", "pi_name", "pi_email", "donor",
        "total_budget_projects", "contract_name", "flagships", "coas", "deliverables", "directDonor",
        "global_dimension", "regional_dimension", "specific_countries"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, Double.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

    for (FundingSource fundingSource : loggedCrp.getFundingSources().stream()
      .filter(fs -> fs.isActive() && fs.getBudgetType() != null).collect(Collectors.toList())) {

      String fsTitle = fundingSource.getTitle();
      Long fsId = fundingSource.getId();
      String financeCode = fundingSource.getFinanceCode();
      String leadPartner = "";
      String summary = fundingSource.getDescription();
      String starDate = "";
      if (fundingSource.getStartDate() != null) {
        starDate = formatter.format(fundingSource.getStartDate());
      }
      String endDate = "";

      if (fundingSource.getEndDate() != null) {
        endDate = formatter.format(fundingSource.getEndDate());
      }

      String contract = "";
      String contractName = "";

      if (fundingSource.getFile() != null) {
        contract = this.getFundingSourceFileURL() + fundingSource.getFile().getFileName();
        contractName = fundingSource.getFile().getFileName();
      }

      String status = "";
      status = fundingSource.getStatusName();

      String piName = "";
      piName = fundingSource.getContactPersonName();

      String piEmail = "";
      // If PIEmail is shown, evaluate the PIEmail else isn't necesary
      if (showPIEmail) {
        piEmail = fundingSource.getContactPersonEmail();
      }
      String donor = "";
      if (fundingSource.getInstitution() != null) {
        donor = fundingSource.getInstitution().getComposedName();
      }


      for (FundingSourceInstitution fsIns : fundingSource.getFundingSourceInstitutions().stream()
        .filter(fsi -> fsi.isActive()).collect(Collectors.toList())) {
        if (leadPartner.isEmpty()) {
          leadPartner = fsIns.getInstitution().getComposedName();
          // Check IFPRI Division
          if (this.showIfpriDivision) {
            if (fsIns.getInstitution().getAcronym() != null && fsIns.getInstitution().getAcronym().equals("IFPRI")
              && fundingSource.getPartnerDivision() != null && fundingSource.getPartnerDivision().getName() != null
              && !fundingSource.getPartnerDivision().getName().trim().isEmpty()) {
              leadPartner += " (" + fundingSource.getPartnerDivision().getName() + ")";
            }
          }
        } else {
          leadPartner += ", \n" + fsIns.getInstitution().getComposedName();
          // Check IFPRI Division
          if (this.showIfpriDivision) {
            if (fsIns.getInstitution().getAcronym() != null && fsIns.getInstitution().getAcronym().equals("IFPRI")
              && fundingSource.getPartnerDivision().getName() != null
              && !fundingSource.getPartnerDivision().getName().trim().isEmpty()) {
              leadPartner += " (" + fundingSource.getPartnerDivision().getName() + ")";
            }
          }
        }
      }
      String fsWindow = fundingSource.getBudgetType().getName();
      if (hasW1W2Co && fundingSource.getW1w2() != null && fundingSource.getW1w2()) {
        fsWindow = "W1/W2 Co-Financing";
      }


      String projectId = "";
      List<String> projectList = new ArrayList<String>();
      for (ProjectBudget projectBudget : fundingSource.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getProject() != null).collect(Collectors.toList())) {
        projectList.add(projectBudget.getProject().getId().toString());
      }

      // Remove duplicates
      Set<String> s = new LinkedHashSet<String>(projectList);
      // set flagships and coas
      String flagships = null;
      String coas = null;
      List<String> flagshipsList = new ArrayList<String>();
      List<String> coasList = new ArrayList<String>();

      for (String projectString : s.stream().collect(Collectors.toList())) {
        Project projectById = this.projectManager.getProjectById(Long.parseLong(projectString));

        // get Flagships related to the project sorted by acronym
        for (ProjectFocus projectFocuses : projectById.getProjectFocuses().stream()
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())) {
          flagshipsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym());
        }

        // get CoAs related to the project sorted by acronym
        if (projectById.getProjectClusterActivities() != null) {
          for (ProjectClusterActivity projectClusterActivity : projectById.getProjectClusterActivities().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {
            coasList.add(projectClusterActivity.getCrpClusterOfActivity().getIdentifier());
          }
        }

        // Add project to field
        if (projectId.isEmpty()) {
          projectId = "P" + projectString;
        } else {
          projectId += ", P" + projectString;
        }
      }
      // Remove duplicates
      Set<String> flagshipsHash = new LinkedHashSet<String>(flagshipsList);
      Set<String> coasHash = new LinkedHashSet<String>(coasList);
      // Add flagships
      for (String flagshipString : flagshipsHash.stream().collect(Collectors.toList())) {
        if (flagships == null || flagships.isEmpty()) {
          flagships = flagshipString;
        } else {
          flagships += "\n " + flagshipString;
        }
      }
      // Add coas
      for (String coaString : coasHash.stream().collect(Collectors.toList())) {
        if (coas == null || coas.isEmpty()) {
          coas = coaString;
        } else {
          coas += "\n " + coaString;
        }
      }

      Double totalBudget = 0.0;
      Double totalBudgetProjects = 0.0;

      for (FundingSourceBudget fundingSourceBudget : fundingSource.getFundingSourceBudgets().stream()
        .filter(fsb -> fsb.isActive() && fsb.getYear() != null && fsb.getYear().intValue() == year)
        .collect(Collectors.toList())) {
        totalBudget += fundingSourceBudget.getBudget();
      }

      for (ProjectBudget projectBudget : fundingSource.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getProject().isActive()
          && pb.getProject().getStatus() != null && pb.getProject().getStatus() == 2)
        .collect(Collectors.toList())) {
        totalBudgetProjects += projectBudget.getAmount();
      }

      // get deliverable funding sources
      String deliverables = "";
      for (DeliverableFundingSource deliverableFundingSource : this.deliverableFundingSourceManager.findAll().stream()
        .filter(df -> df.getFundingSource().getId().longValue() == fundingSource.getId().longValue() && df.isActive()
          && df.getDeliverable() != null && df.getDeliverable().isActive() && df.getDeliverable().getProject() != null
          && df.getDeliverable().getProject().isActive())
        .sorted((df1, df2) -> Long.compare(df1.getDeliverable().getId(), df2.getDeliverable().getId()))
        .collect(Collectors.toList())) {
        if (deliverables.length() == 0) {
          deliverables = "D" + deliverableFundingSource.getDeliverable().getId();
        } else {
          deliverables += ", D" + deliverableFundingSource.getDeliverable().getId();
        }
      }
      if (deliverables.isEmpty()) {
        deliverables = null;
      }

      String directDonor = "";
      if (fundingSource.getDirectDonor() != null) {
        directDonor = fundingSource.getDirectDonor().getComposedName();
      }

      // Funding sources locations
      String globalDimension = null;
      globalDimension = fundingSource.isGlobal() ? "Yes" : "No";

      String regionalDimension = "";
      // Regions
      for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingSourceLocations().stream()
        .filter(
          fl -> fl.isActive() && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 1)
        .collect(Collectors.toList())) {
        if (regionalDimension.isEmpty()) {
          regionalDimension += fundingSourceLocation.getLocElement().getName();
        } else {
          regionalDimension += ", " + fundingSourceLocation.getLocElement().getName();
        }
      }
      // Scope Regions
      for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingSourceLocations().stream()
        .filter(fl -> fl.isActive() && fl.getLocElementType() != null && fl.getLocElement() == null)
        .collect(Collectors.toList())) {
        if (regionalDimension.isEmpty()) {
          regionalDimension += fundingSourceLocation.getLocElementType().getName();
        } else {
          regionalDimension += ", " + fundingSourceLocation.getLocElementType().getName();
        }
      }

      if (regionalDimension.isEmpty()) {
        regionalDimension = "No";
      }

      String specificCountries = "";
      for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingSourceLocations().stream()
        .filter(
          fl -> fl.isActive() && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 2)
        .collect(Collectors.toList())) {
        if (specificCountries.isEmpty()) {
          specificCountries += fundingSourceLocation.getLocElement().getName();
        } else {
          specificCountries += ", " + fundingSourceLocation.getLocElement().getName();
        }
      }

      if (specificCountries.isEmpty()) {
        specificCountries = null;
      }

      model.addRow(new Object[] {fsTitle, fsId, financeCode, leadPartner, fsWindow, projectId, totalBudget, summary,
        starDate, endDate, contract, status, piName, piEmail, donor, totalBudgetProjects, contractName, flagships, coas,
        deliverables, directDonor, globalDimension, regionalDimension, specificCountries});
    }
    return model;
  }


  public String getFundingSourceUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + "fundingSourceFiles" + File.separator;
  }


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  private TypedTableModel getMasterTableModel(String center, String date) {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"center", "date", "managingPPAField", "year", "showPIEmail"},
        new Class[] {String.class, String.class, String.class, Integer.class, Boolean.class});
    model.addRow(new Object[] {center, date, "Managing / PPA Partner", this.year, showPIEmail});
    return model;
  }


  public Boolean getShowSheet3() {
    return showSheet3;
  }

  public int getYear() {
    return year;
  }

  @Override
  public void prepare() {
    // Get loggerCrp
    try {
      loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
    // Get parameters from URL
    // Get year
    try {
      Map<String, Object> parameters = this.getParameters();
      year = Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0])));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.YEAR_REQUEST
        + " parameter. Parameter will be set as CurrentCycleYear. Exception: " + e.getMessage());
      year = this.getCurrentCycleYear();
    }
    // Get cycle
    try {
      Map<String, Object> parameters = this.getParameters();
      cycle = (StringUtils.trim(((String[]) parameters.get(APConstants.CYCLE))[0]));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as CurrentCycle. Exception: "
        + e.getMessage());
      cycle = this.getCurrentCycle();
    }
    // Get PIEmail crp_parameter
    try {
      this.showPIEmail = this.hasSpecificities(this.getText(APConstants.CRP_EMAIL_FUNDING_SOURCE));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CRP_EMAIL_FUNDING_SOURCE
        + " parameter. Parameter will be set false. Exception: " + e.getMessage());
      this.showPIEmail = false;
    }
    // Get IfpriDivision crp_parameter
    try {
      this.showIfpriDivision = this.hasSpecificities(this.getText(APConstants.CRP_DIVISION_FS));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CRP_DIVISION_FS + " parameter. Parameter will be set false. Exception: "
        + e.getMessage());
      this.showIfpriDivision = false;
    }
    hasW1W2Co = this.hasSpecificities(APConstants.CRP_FS_W1W2_COFINANCING);
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle);
  }


  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setShowSheet3(Boolean showSheet3) {
    this.showSheet3 = showSheet3;
  }


  public void setYear(int year) {
    this.year = year;
  }

}
