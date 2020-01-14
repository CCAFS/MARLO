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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pentaho.reporting.engine.classic.core.Band;
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

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

public class ProjectsFundingSourcesSummaryAction extends BaseSummariesAction implements Summary {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectsFundingSourcesSummaryAction.class);
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // Variables
  private Boolean showPIEmail;
  private Boolean showIfpriDivision;
  private Boolean showSheet3;
  private Boolean hasResearchHuman;
  private Set<Project> fundingSourceProjectsWithBudgets = new HashSet<>();
  private Set<FundingSource> currentCycleFundingSources = new HashSet<>();
  private List<Project> allProjects = new ArrayList<>();
  private long startTime;
  private Boolean hasW1W2Co;
  // Managers
  private final CrpProgramManager programManager;
  private final ResourceManager resourceManager;
  private final ProjectBudgetManager projectBudgetManager;
  private final CrpProgramManager crpProgramManager;
  private final LocElementManager locElementManager;


  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;

  @Inject
  public ProjectsFundingSourcesSummaryAction(APConfig config, GlobalUnitManager crpManager,
    CrpProgramManager programManager, ProjectManager projectManager, PhaseManager phaseManager,
    ResourceManager resourceManager, ProjectBudgetManager projectBudgetManager, CrpProgramManager crpProgramManager,
    LocElementManager locElementManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.programManager = programManager;
    this.resourceManager = resourceManager;
    this.projectBudgetManager = projectBudgetManager;
    this.crpProgramManager = crpProgramManager;
    this.locElementManager = locElementManager;
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
    masterReport.getParameterValues().put("i8nExtentionDate", this.getText("projectCofunded.extensionDate"));
    masterReport.getParameterValues().put("i8nFinanceCode", this.getText("projectCofunded.financeCode"));
    masterReport.getParameterValues().put("i8nContactName", this.getText("projectCofunded.contactName"));
    masterReport.getParameterValues().put("i8nContactEmail", this.getText("projectCofunded.contactEmail"));
    masterReport.getParameterValues().put("i8nFundingWindow", this.getText("projectCofunded.type"));
    masterReport.getParameterValues().put("i8nDonor", this.getText("projectsList.originalDonor"));
    masterReport.getParameterValues().put("i8nDirectDonor", this.getText("projectsList.projectDonor"));
    masterReport.getParameterValues().put("i8nBudgetYear",
      this.getText("fundingSource.budget", new String[] {String.valueOf(this.getSelectedYear())}));
    masterReport.getParameterValues().put("i8nBudgetYearProjects",
      this.getText("fundingSource.budgetYearAllocated", new String[] {String.valueOf(this.getSelectedYear())}));
    masterReport.getParameterValues().put("i8nDeliverableIDs",
      this.getText("fundingSource.deliverableIDs", new String[] {String.valueOf(this.getSelectedYear())}));
    masterReport.getParameterValues().put("i8nProjects", this.getText("caseStudy.projects"));
    masterReport.getParameterValues().put("i8nCoas", this.getText("deliverable.coas"));
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nContractProposal", this.getText("fundingSource.contractProposal"));
    masterReport.getParameterValues().put("i8nGlobalDimension", this.getText("fundingSource.globalDimension"));
    masterReport.getParameterValues().put("i8nRegionalDimension", this.getText("fundingSource.regionalDimension"));
    masterReport.getParameterValues().put("i8nSpecificCountries",
      this.getText("projectCofunded.listCountries.readText"));


    // Funding Sources by Projects
    masterReport.getParameterValues().put("i8nProjectID", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nFundingSourcesProjectsNote",
      this.getText("summaries.fundingSource.sheet2Description"));


    // Funding Sources no Projects
    masterReport.getParameterValues().put("i8nSheet3Title", this.getText("summaries.fundingSource.sheet3Title"));
    masterReport.getParameterValues().put("i8nSheet3Description",
      this.getText("summaries.fundingSource.sheet3Description", new String[] {String.valueOf(this.getSelectedYear())}));

    masterReport.getParameterValues().put("i8nResearchHumanSubjects",
      this.getText("fundingSource.doesResearchHumanSubjects"));

    return masterReport;
  }


  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();

    try {
      Resource reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/ProjectsFundingSources.prpt"), MasterReport.class);


      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();


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

      // get funding sources to the current cycle
      currentCycleFundingSources = this.getActiveFundingSourcesOnPhase();


      this.fillSubreport((SubReport) hm.get("funding_sources"), "funding_sources");

      // Add all projects
      // Status of projects
      String[] statuses = {ProjectStatusEnum.Ongoing.getStatusId(), ProjectStatusEnum.Extended.getStatusId(),
        ProjectStatusEnum.Extended.getStatusId()};
      allProjects = this.getActiveProjectsByPhase(this.getSelectedPhase(), this.getSelectedYear(), statuses);

      // delete projects with FS
      for (Project project : fundingSourceProjectsWithBudgets) {
        allProjects.remove(project);
      }
      this.setShowSheet3(!allProjects.isEmpty() ? true : false);
      masterReport.getParameterValues().put("showSheet3", this.getShowSheet3());

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
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
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
  @Override
  public void getAllSubreports(HashMap<String, Element> hm, ItemBand itemBand) {
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
  public void getBandSubreports(HashMap<String, Element> hm, Band band) {
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
    fileName.append("ProjectsFundingSourcesSummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");

    return fileName.toString();

  }

  public String getfundingSourceFilesResearchURL(Long fundingSourceID) {
    return config.getDownloadURL() + "/" + this.getFundingSourceResearchUrlPath(fundingSourceID).replace('\\', '/');
  }

  public String getFundingSourceFileURL() {
    return config.getDownloadURL() + "/" + this.getFundingSourceUrlPath().replace('\\', '/');
  }

  public String getFundingSourceResearchUrlPath(Long fundingSourceID) {
    return config.getFundingSourceFolder(this.getCrpSession()) + File.separator + fundingSourceID + File.separator
      + "fundingSourceFilesResearch" + File.separator;
  }

  private TypedTableModel getFundingSourcesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"fs_title", "fs_id", "finance_code", "lead_partner", "fs_window", "project_id", "total_budget",
        "summary", "start_date", "end_date", "contract", "status", "pi_name", "pi_email", "donor",
        "total_budget_projects", "contract_name", "flagships", "coas", "deliverables", "directDonor",
        "global_dimension", "regional_dimension", "specific_countries", "extention_date", "phaseID",
        "researchHumanSubjects", "hasresearchHumanSubjectsFile", "researchHumanSubjectsURL"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, Double.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Double.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, Long.class, String.class, Boolean.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

    // Funding sources information
    for (FundingSource fundingSource : currentCycleFundingSources) {

      String fsTitle = fundingSource.getFundingSourceInfo().getTitle();
      Long fsId = fundingSource.getId();
      String financeCode = fundingSource.getFundingSourceInfo().getFinanceCode();
      Long phaseID = fundingSource.getFundingSourceInfo().getPhase().getId();
      String leadPartner = "";
      String summary = fundingSource.getFundingSourceInfo().getDescription();
      String starDate = "";
      if (fundingSource.getFundingSourceInfo().getStartDate() != null) {
        starDate = formatter.format(fundingSource.getFundingSourceInfo().getStartDate());
      }
      String endDate = "";

      if (fundingSource.getFundingSourceInfo().getEndDate() != null) {
        endDate = formatter.format(fundingSource.getFundingSourceInfo().getEndDate());
      }
      String extentionDate = "";

      if (fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
        .parseInt(FundingStatusEnum.Extended.getStatusId())) {
        if (fundingSource.getFundingSourceInfo().getExtensionDate() != null) {
          extentionDate = formatter.format(fundingSource.getFundingSourceInfo().getExtensionDate());
        } else {
          extentionDate = "<Not Defined>";
        }
      } else {
        extentionDate = "<Not Applicable>";
      }


      String contract = "";
      String contractName = "";

      if (fundingSource.getFundingSourceInfo().getFile() != null) {
        contract = this.getFundingSourceFileURL() + fundingSource.getFundingSourceInfo().getFile().getFileName();
        contractName = fundingSource.getFundingSourceInfo().getFile().getFileName();
      }
      String status = "";
      status = fundingSource.getFundingSourceInfo().getStatusName();

      String piName = "";
      piName = fundingSource.getFundingSourceInfo().getContactPersonName();

      String piEmail = "";
      // If PIEmail is shown, evaluate the PIEmail else isn't necesary
      if (showPIEmail) {
        piEmail = fundingSource.getFundingSourceInfo().getContactPersonEmail();
      }
      String originalDonor = null;
      if (fundingSource.getFundingSourceInfo().getOriginalDonor() != null) {
        originalDonor = fundingSource.getFundingSourceInfo().getOriginalDonor().getComposedName();
      }

      String directDonor = null;
      if (fundingSource.getFundingSourceInfo().getDirectDonor() != null) {
        directDonor = fundingSource.getFundingSourceInfo().getDirectDonor().getComposedName();
      }

      for (FundingSourceInstitution fsIns : fundingSource.getFundingSourceInstitutions().stream()
        .filter(fsi -> fsi.isActive() && fsi.getPhase() != null && fsi.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        if (leadPartner.isEmpty()) {
          leadPartner = fsIns.getInstitution().getComposedName();
          // Check IFPRI Division
          if (this.showIfpriDivision) {
            if (fsIns.getInstitution().getAcronym() != null && fsIns.getInstitution().getAcronym().equals("IFPRI")
              && fundingSource.getFundingSourceInfo().getPartnerDivision() != null
              && fundingSource.getFundingSourceInfo().getPartnerDivision().getName() != null
              && !fundingSource.getFundingSourceInfo().getPartnerDivision().getName().trim().isEmpty()) {
              leadPartner += " (" + fundingSource.getFundingSourceInfo().getPartnerDivision().getName() + ")";
            }
          }
        } else {
          leadPartner += ", \n" + fsIns.getInstitution().getComposedName();
          // Check IFPRI Division
          if (this.showIfpriDivision) {
            if (fsIns.getInstitution().getAcronym() != null && fsIns.getInstitution().getAcronym().equals("IFPRI")
              && fundingSource.getFundingSourceInfo().getPartnerDivision().getName() != null
              && !fundingSource.getFundingSourceInfo().getPartnerDivision().getName().trim().isEmpty()) {
              leadPartner += " (" + fundingSource.getFundingSourceInfo().getPartnerDivision().getName() + ")";
            }
          }
        }
      }
      String fsWindow = "";
      if (fundingSource.getFundingSourceInfo().getBudgetType() != null) {
        fsWindow = fundingSource.getFundingSourceInfo().getBudgetType().getName();
      }

      if (hasW1W2Co && fundingSource.getFundingSourceInfo().getW1w2() != null
        && fundingSource.getFundingSourceInfo().getW1w2()) {
        fsWindow = "W1/W2 Co-Financing";
      }


      String projectId = "";
      List<String> projectList = new ArrayList<String>();
      for (ProjectBudget projectBudget : fundingSource
        .getProjectBudgets().stream().filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear()
          && pb.getProject() != null && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
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
        Project projectById = projectManager.getProjectById(Long.parseLong(projectString));
        Boolean isAdministrative = true;
        int countAdministrative = 0;
        if (countAdministrative == 0) {
          if (projectById.getProjecInfoPhase(this.getSelectedPhase()).getAdministrative() != null) {
            isAdministrative = projectById.getProjecInfoPhase(this.getSelectedPhase()).getAdministrative();
          } else {
            isAdministrative = false;
          }
          if (isAdministrative) {
            countAdministrative++;
            flagshipsList.add("Cross cutting");
            coasList.add("Cross cutting");
          }
        }


        // get Flagships related to the project sorted by acronym
        for (ProjectFocus projectFocuses : projectById.getProjectFocuses().stream()
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgram().getCrp().getId().equals(this.getCurrentCrp().getId()))
          .collect(Collectors.toList())) {
          flagshipsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym());
        }

        // get CoAs related to the project sorted by acronym
        if (projectById.getProjectClusterActivities() != null) {
          for (ProjectClusterActivity projectClusterActivity : projectById.getProjectClusterActivities().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
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
        .filter(fsb -> fsb.isActive() && fsb.getYear() != null && fsb.getYear().intValue() == this.getSelectedYear()
          && fsb.getPhase() != null && fsb.getPhase().equals(this.getSelectedPhase()) && fsb.getBudget() != null)
        .collect(Collectors.toList())) {
        totalBudget += fundingSourceBudget.getBudget();
      }

      for (ProjectBudget projectBudget : fundingSource.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear() && pb.getProject().isActive()
          && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase())
          && pb.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null && pb.getPhase() != null
          && pb.getProject().getProjectInfo().getStatus() != null
          && (pb.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || pb.getProject().getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())))
        .collect(Collectors.toList())) {
        totalBudgetProjects += projectBudget.getAmount();
      }

      // get deliverable funding sources
      String deliverables = "";
      for (DeliverableFundingSource deliverableFundingSource : fundingSource.getDeliverableFundingSources().stream()
        .filter(df -> df.isActive() && df.getPhase() != null && df.getPhase().equals(this.getSelectedPhase())
          && df.getDeliverable() != null && df.getDeliverable().isActive() && df.getDeliverable().getProject() != null
          && df.getDeliverable().getProject().isActive()
          && df.getDeliverable().getDeliverableInfo(this.getSelectedPhase()) != null && (
          // If upkeep phase is selected
          (df.getDeliverable().getDeliverableInfo().getStatus() != null
            && this.getSelectedPhase().getName().equals("UpKeep")
            && ((df.getDeliverable().getDeliverableInfo().getYear() == this.getSelectedYear())
              || (df.getDeliverable().getDeliverableInfo().getNewExpectedYear() != null
                && df.getDeliverable().getDeliverableInfo().getNewExpectedYear() == this.getSelectedYear())))
            ||

            (df.getDeliverable().getDeliverableInfo().getStatus() == null
              && df.getDeliverable().getDeliverableInfo().getYear() == this.getSelectedYear())
            || (df.getDeliverable().getDeliverableInfo().getStatus() != null
              && df.getDeliverable().getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())
              && df.getDeliverable().getDeliverableInfo().getNewExpectedYear() != null
              && df.getDeliverable().getDeliverableInfo().getNewExpectedYear() == this.getSelectedYear())
            || (df.getDeliverable().getDeliverableInfo().getStatus() != null
              && df.getDeliverable().getDeliverableInfo().getYear() == this.getSelectedYear()
              && df.getDeliverable().getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
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

      // Funding sources locations
      String globalDimension = null;
      globalDimension = fundingSource.getFundingSourceInfo().isGlobal() ? "Yes" : "No";

      String regionalDimension = "";
      // Regions
      for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingSourceLocations().stream()
        .filter(fl -> fl.isActive() && fl.getLocElement() != null && fl.getLocElementType() == null
          && fl.getLocElement().getLocElementType().getId() == 1 && fl.getPhase() != null
          && fl.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        if (regionalDimension.isEmpty()) {
          regionalDimension += fundingSourceLocation.getLocElement().getName();
        } else {
          regionalDimension += ", " + fundingSourceLocation.getLocElement().getName();
        }
      }
      // Scope Regions
      for (FundingSourceLocation fundingSourceLocation : fundingSource
        .getFundingSourceLocations().stream().filter(fl -> fl.isActive() && fl.getLocElementType() != null
          && fl.getLocElement() == null && fl.getPhase() != null && fl.getPhase().equals(this.getSelectedPhase()))
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
        .filter(fl -> fl.isActive() && fl.getLocElement() != null && fl.getLocElement().getLocElementType() != null
          && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 2
          && fl.getPhase() != null && fl.getPhase().equals(this.getSelectedPhase()))
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

      String researchHumanSubjects = null;
      String researchHumanSubjectsURL = null;
      Boolean hasFileResearch = fundingSource.getFundingSourceInfo().getHasFileResearch();
      Boolean hasresearchHumanSubjectsFile = false;
      if (hasFileResearch != null) {
        if (hasFileResearch) {
          researchHumanSubjects = "Yes";
          if (fundingSource.getFundingSourceInfo().getFileResearch() != null) {
            hasresearchHumanSubjectsFile = true;
            researchHumanSubjects = fundingSource.getFundingSourceInfo().getFileResearch().getFileName();
            researchHumanSubjectsURL = this.getfundingSourceFilesResearchURL(fsId)
              + fundingSource.getFundingSourceInfo().getFileResearch().getFileName();
          } else {
            researchHumanSubjects = "Yes, missing approval letter";
          }
        } else {
          researchHumanSubjects = "No";
        }
      }

      model.addRow(new Object[] {fsTitle, fsId, financeCode, leadPartner, fsWindow, projectId, totalBudget, summary,
        starDate, endDate, contract, status, piName, piEmail, originalDonor, totalBudgetProjects, contractName,
        flagships, coas, deliverables, directDonor, globalDimension, regionalDimension, specificCountries,
        extentionDate, phaseID, researchHumanSubjects, hasresearchHumanSubjectsFile, researchHumanSubjectsURL});
    }

    // Projects Information

    // Status of projects
    String[] statuses = null;

    // Get projects with the status defined
    List<Project> activeProjects = this.getActiveProjectsByPhase(this.getSelectedPhase(), 0, statuses);
    System.out.println("active projects " + activeProjects.size());
    for (Project project : activeProjects) {
      Long projectId = project.getId();
      String projectTitle = project.getProjectInfo().getTitle();
      String managementLiaison = null;
      String crossCutting = "";
      String projectSummary = "";
      String locations = "";
      String startDate = "";
      String endDate = "";

      if (project.getProjectInfo().getSummary() != null && !project.getProjectInfo().getSummary().isEmpty()) {
        projectSummary = project.getProjectInfo().getSummary();
      }

      if (project.getProjectInfo().getStartDate() != null) {
        startDate = formatter.format(project.getProjectInfo().getStartDate());
      }
      if (project.getProjectInfo().getEndDate() != null) {
        endDate = formatter.format(project.getProjectInfo().getEndDate());
      }
      if (project.getProjectInfo().getLiaisonInstitution() != null) {
        managementLiaison = project.getProjectInfo().getLiaisonInstitution().getComposedName();

        managementLiaison = managementLiaison.replaceAll("<", "&lt;");
        managementLiaison = managementLiaison.replaceAll(">", "&gt;");
      }

      // Get type from funding sources
      String type = "";
      List<String> typeList = new ArrayList<String>();
      for (ProjectBudget projectBudget : project
        .getProjectBudgets().stream().filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear()
          && pb.getFundingSource() != null && pb.getPhase() != null && pb.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        if (projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null
          && projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType() != null
          && projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType()
            .getName() != null) {
          typeList.add(
            projectBudget.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getBudgetType().getName());
        }
      }
      // Remove duplicates
      Set<String> s = new LinkedHashSet<String>(typeList);
      for (String typeString : s.stream().collect(Collectors.toList())) {
        if (type.isEmpty()) {
          type = typeString;
        } else {
          type += ", " + typeString;
        }
      }

      String flagships = null;
      // get Flagships related to the project sorted by acronym
      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
          && c.getCrpProgram().getCrp().isCenterType() == false)
        .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
        .collect(Collectors.toList())) {
        if (flagships == null || flagships.isEmpty()) {
          flagships = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
        } else {
          flagships += ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
        }
      }
      String regions = null;
      // If has regions, add the regions to regionsArrayList
      // Get Regions related to the project sorted by acronym
      if (this.hasProgramnsRegions()) {
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList())) {
          if (regions == null || regions.isEmpty()) {
            regions = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          } else {
            regions += ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          }
        }
        if (project.getProjecInfoPhase(this.getSelectedPhase()).getNoRegional() != null
          && project.getProjectInfo().getNoRegional()) {
          if (regions != null && !regions.isEmpty()) {
            LOG.warn("Project is global and has regions selected");
          }
          regions = "Global";
        }
      } else {
        regions = null;
      }

      String institutionLeader = null;
      String projectLeaderName = null;

      ProjectPartnerPerson projectLeader = project.getLeaderPersonDB(this.getSelectedPhase());
      if (projectLeader != null) {
        institutionLeader = projectLeader.getProjectPartner().getInstitution().getComposedName();
        projectLeaderName = projectLeader.getComposedName();
        projectLeaderName = projectLeaderName.replaceAll("<", "&lt;");
        projectLeaderName = projectLeaderName.replaceAll(">", "&gt;");
      }

      Set<Activity> activitiesSet = new HashSet();
      for (Activity activity : project
        .getActivities().stream().sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId())).filter(a -> a.isActive()
          && (a.getActivityStatus() == 2) && a.getPhase() != null && a.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        activitiesSet.add(activity);
      }
      Integer activitiesOnGoing = activitiesSet.size();
      Set<Deliverable> deliverablesSet = new HashSet();
      for (Deliverable deliverable : project.getDeliverables().stream()
        .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(
          d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null && d.getDeliverableInfo().isActive()
            && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == 2017)
              || (d.getDeliverableInfo().getStatus() != null
                && d.getDeliverableInfo().getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                && d.getDeliverableInfo().getNewExpectedYear() != null
                && d.getDeliverableInfo().getNewExpectedYear() == 2017)
              || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == 2017
                && d.getDeliverableInfo().getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
        .collect(Collectors.toList())) {
        deliverablesSet.add(deliverable);
      }
      Integer expectedDeliverables = deliverablesSet.size();

      Set<ProjectOutcome> projectOutcomeSet = new HashSet();
      for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        projectOutcomeSet.add(projectOutcome);
      }
      Integer outcomes = projectOutcomeSet.size();

      Set<ProjectExpectedStudy> projectExpectedStudySet = new HashSet();
      for (ProjectExpectedStudy projectExpectedStudy : project.getProjectExpectedStudies().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase() == this.getActualPhase().getId())
        .collect(Collectors.toList())) {
        projectExpectedStudySet.add(projectExpectedStudy);
      }
      for (ExpectedStudyProject expectedStudyProject : project.getExpectedStudyProjects().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getPhase() == this.getActualPhase().getId())
        .collect(Collectors.toList())) {
        projectExpectedStudySet.add(expectedStudyProject.getProjectExpectedStudy());
      }
      Integer expectedStudies = projectExpectedStudySet.size();

      String status = "";
      project.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
      if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
          status = ProjectStatusEnum.Ongoing.getStatus();
        }

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
          status = ProjectStatusEnum.Complete.getStatus();
        }

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
          status = ProjectStatusEnum.Cancelled.getStatus();
        }

        if (project.getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
          status = ProjectStatusEnum.Extended.getStatus();
        }
      }

      // Cross cutting Dimensions

      if (project.getProjectInfo().getCrossCuttingCapacity() != null
        && project.getProjectInfo().getCrossCuttingCapacity() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", Capacity Development";
        } else {
          crossCutting += " Capacity Development";
        }
      }
      if (project.getProjectInfo().getCrossCuttingClimate() != null
        && project.getProjectInfo().getCrossCuttingClimate() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", Climate Change";
        } else {
          crossCutting += " Climate Change";
        }
      }
      if (project.getProjectInfo().getCrossCuttingNa() != null
        && project.getProjectInfo().getCrossCuttingNa() == true) {
        if (crossCutting.length() > 0) {
          crossCutting += ", N/A";
        } else {
          crossCutting += " N/A";
        }
      }

      // Project Location:
      if (!project.getProjectLocations().isEmpty()) {
        // Get all selected and show it
        List<LocElement> locElementsAll = locElementManager.findAll();
        String lastLocTypeName = "";
        for (ProjectLocationElementType projectLocType : project.getProjectLocationElementTypes().stream()
          .filter(plt -> plt.getIsGlobal() && plt.getLocElementType().isActive()).collect(Collectors.toList())) {
          String locTypeName = projectLocType.getLocElementType().getName();
          for (LocElement locElement : locElementsAll.stream()
            .filter(le -> le.isActive() && le.getLocElementType() != null
              && le.getLocElementType().getId() == projectLocType.getLocElementType().getId())
            .collect(Collectors.toList())) {

            String locName = null;
            if (locElement != null) {

              locName = locElement.getName();
            }

            if (locations.isEmpty() || locations == null) {
              locations += locTypeName + ": " + locName;
            } else {
              if (locTypeName.equals(lastLocTypeName)) {
                locations += ", " + locName;
              } else {
                locations += ", " + locTypeName + ": " + locName;
              }
            }
            lastLocTypeName = locTypeName;
          }
        }
        for (ProjectLocation pl : project.getProjectLocations().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {
          LocElement le = pl.getLocElement();
          String locTypeName = null;

          String locName = null;
          if (le != null) {
            if (le.getLocElementType() != null) {
              locTypeName = le.getLocElementType().getName();
            }
            locName = le.getName();
          }

          if (locations.isEmpty() || locations == null) {
            locations += locTypeName + ": " + locName;
          } else {
            if (locTypeName.equals(lastLocTypeName)) {
              locations += ", " + locName;
            } else {
              locations += ", " + locTypeName + ": " + locName;
            }
          }
          lastLocTypeName = locTypeName;
        }
      }


      // Get ppaPartners of project
      // get w1w2 co
      boolean hasW1W2Co;
      try {
        hasW1W2Co = this.hasSpecificities(APConstants.CRP_FS_W1W2_COFINANCING);
      } catch (Exception e) {
        LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as false. Exception: "
          + e.getMessage());
        hasW1W2Co = false;
      }

      String bilateralGender = null;
      String w1w2Budget = null;
      String w1w2Gender = null;
      String bilateralBudget = null;
      boolean check = true;
      for (ProjectPartner pp : project.getProjectPartners().stream()
        .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        if (this.isPPA(pp.getInstitution())) {
          DecimalFormat myFormatter = new DecimalFormat("###,###");

          String w1w2CoBudget = null;

          String w1w2CoGender = null;
          String w1w2GAmount = null;
          String w1w2CoGAmount = null;
          // Partner Total
          String partnerTotal = null;
          double partnerTotald = 0.0;

          if (hasW1W2Co) {
            w1w2Budget = myFormatter.format(Double.parseDouble(
              this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3)));
            w1w2CoBudget = myFormatter.format(Double.parseDouble(
              this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2)));

            w1w2Gender = myFormatter.format(
              this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3));
            w1w2CoGender = myFormatter.format(
              this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2));

            w1w2GAmount = myFormatter
              .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3));
            w1w2CoGAmount = myFormatter
              .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2));

            // increment partner total
            partnerTotald += Double.parseDouble(
              this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 3));
            partnerTotald += Double.parseDouble(
              this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 2));
          } else {
            w1w2Budget = myFormatter.format(Double.parseDouble(
              this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1)));
            w1w2Gender = myFormatter.format(
              this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1));
            w1w2GAmount = myFormatter
              .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1));
            w1w2CoBudget = myFormatter.format(0.0);
            w1w2CoGender = myFormatter.format(0.0);
            w1w2CoGAmount = myFormatter.format(0.0);

            // increment partner total
            partnerTotald += Double.parseDouble(
              this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 1, project.getId(), 1));
          }

          String w3Budget = myFormatter.format(Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1)));
          bilateralBudget = myFormatter.format(Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1)));
          String centerBudget = myFormatter.format(Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1)));


          String w3Gender = myFormatter
            .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1));
          bilateralGender = myFormatter
            .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1));
          String centerGender = myFormatter
            .format(this.getTotalGenderPer(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1));


          String w3GAmount = myFormatter
            .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1));
          String bilateralGAmount = myFormatter
            .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1));
          String centerGAmount = myFormatter
            .format(this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1));

          // increment partner total
          partnerTotald += Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 2, project.getId(), 1));
          partnerTotald += Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 3, project.getId(), 1));
          partnerTotald += Double.parseDouble(
            this.getTotalAmount(pp.getInstitution().getId(), this.getSelectedYear(), 4, project.getId(), 1));

          // set partner total
          partnerTotal = "$" + myFormatter.format(partnerTotald);
          // System.out.println("holi wiw2buget " + w1w2Budget + " bilateralBudget" + bilateralBudget + "
          // w1w2CoBudget");
        }

        check = false;
      }
      if (locations.isEmpty() || locations == null || locations.length() == 0) {
        locations = "<Not defined>";
      }

      if (type.isEmpty() || type == null || type.length() == 0) {
        type = "<Not defined>";
      }
      model.addRow(new Object[] {projectTitle, projectId, projectSummary, managementLiaison, type, flagships, regions,
        institutionLeader, startDate, endDate, projectLeaderName, activitiesOnGoing, expectedDeliverables, outcomes,
        expectedStudies, this.getSelectedPhase().getId(), crossCutting, flagships, "", w1w2Budget, bilateralBudget,
        locations, "", "", "", "", "", "", ""});
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

  private TypedTableModel getMasterTableModel(String center, String date) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"center", "date", "managingPPAField", "year", "showPIEmail", "cycle", "hasResearchHuman"},
      new Class[] {String.class, String.class, String.class, Integer.class, Boolean.class, String.class,
        Boolean.class});
    model.addRow(new Object[] {center, date, "Managing / PPA Partner", this.getSelectedYear(), showPIEmail,
      this.getSelectedCycle(), hasResearchHuman});
    return model;
  }

  public Boolean getShowSheet3() {
    return showSheet3;
  }

  /**
   * Get total amount per institution year and type
   * 
   * @param institutionId
   * @param year current platform year
   * @param budgetType
   * @return String with the total amount.
   */
  public String getTotalAmount(long institutionId, int year, long budgetType, Long projectId, Integer coFinancing) {
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectId, coFinancing,
      this.getSelectedPhase().getId());
  }

  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, long projectID, Integer coFinancing) {

    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID,
      coFinancing, this.getSelectedPhase().getId());

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget.getPhase().equals(this.getSelectedPhase())) {
          double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0.0;
          double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0.0;

          totalGender = totalGender + (amount * (gender / 100));
        }
      }
    }

    return totalGender;
  }

  /**
   * Get total gender percentaje per institution, year and type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGenderPer(long institutionId, int year, long budgetType, long projectId, Integer coFinancing) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType, projectId, coFinancing);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType, projectId, coFinancing);

    if (dTotalAmount != 0) {
      return (totalGender * 100) / dTotalAmount;
    } else {
      return 0.0;
    }
  }


  @Override
  public void prepare() {
    this.setGeneralParameters();
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

    // Get ResearchHuman crp_parameter
    try {
      this.hasResearchHuman = this.hasSpecificities(this.getText(APConstants.CRP_HAS_RESEARCH_HUMAN));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CRP_HAS_RESEARCH_HUMAN
        + " parameter. Parameter will be set false. Exception: " + e.getMessage());
      this.hasResearchHuman = false;
    }

    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  public void setShowSheet3(Boolean showSheet3) {
    this.showSheet3 = showSheet3;

  }

}
