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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
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
  private Crp loggedCrp;
  private int year;
  private String cycle;
  private Boolean showPIEmail;
  private Boolean showIfpriDivision;
  private long startTime;
  // Managers
  private CrpManager crpManager;
  private CrpProgramManager programManager;
  private ProjectManager projectManager;
  private DeliverableFundingSourceManager deliverableFundingSourceManager;
  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;

  @Inject
  public FundingSourcesSummaryAction(APConfig config, CrpManager crpManager, CrpProgramManager programManager,
    ProjectManager projectManager, DeliverableFundingSourceManager deliverableFundingSourceManager) {
    super(config);
    this.crpManager = crpManager;
    this.programManager = programManager;
    this.projectManager = projectManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
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
      String center = loggedCrp.getName();


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

  private TypedTableModel getFundingSourcesProjectsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"fs_title", "fs_id", "finance_code", "lead_partner", "fs_window", "project_id", "total_budget",
        "flagships", "coas", "donor"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, Double.class,
        String.class, String.class, String.class},
      0);

    for (FundingSource fundingSource : loggedCrp.getFundingSources().stream()
      .filter(fs -> fs.isActive() && fs.getBudgetType() != null).collect(Collectors.toList())) {

      String fsTitle = fundingSource.getTitle();
      Long fsId = fundingSource.getId();
      String financeCode = fundingSource.getFinanceCode();
      String donor = null;


      String fsWindow = fundingSource.getBudgetType().getName();
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

        model.addRow(new Object[] {fsTitle, fsId, financeCode, leadPartner, fsWindow, projectId, totalBudget, flagships,
          coas, donor});
      }

    }
    return model;
  }

  private TypedTableModel getFundingSourcesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"fs_title", "fs_id", "finance_code", "lead_partner", "fs_window", "project_id", "total_budget",
        "summary", "start_date", "end_date", "contract", "status", "pi_name", "pi_email", "donor",
        "total_budget_projects", "contract_name", "flagships", "coas", "deliverables"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, Double.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Double.class, String.class, String.class, String.class, String.class},
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


            if (fsIns.getInstitution().getAcronym().equals("IFPRI") && fundingSource.getPartnerDivision() != null
              && fundingSource.getPartnerDivision().getName() != null
              && !fundingSource.getPartnerDivision().getName().trim().isEmpty()) {
              leadPartner += " (" + fundingSource.getPartnerDivision().getName() + ")";
            }
          }
        } else {
          leadPartner += ", \n" + fsIns.getInstitution().getComposedName();
          // Check IFPRI Division
          if (this.showIfpriDivision) {
            if (fsIns.getInstitution().getAcronym().equals("IFPRI")
              && fundingSource.getPartnerDivision().getName() != null
              && !fundingSource.getPartnerDivision().getName().trim().isEmpty()) {
              leadPartner += " (" + fundingSource.getPartnerDivision().getName() + ")";
            }
          }
        }
      }
      String fsWindow = fundingSource.getBudgetType().getName();


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
      model.addRow(new Object[] {fsTitle, fsId, financeCode, leadPartner, fsWindow, projectId, totalBudget, summary,
        starDate, endDate, contract, status, piName, piEmail, donor, totalBudgetProjects, contractName, flagships, coas,
        deliverables});
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

  public Crp getLoggedCrp() {
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


  public int getYear() {
    return year;
  }

  @Override
  public void prepare() {
    // Get loggerCrp
    try {
      loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getCrpById(loggedCrp.getId());
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

    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle);
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setYear(int year) {
    this.year = year;
  }

}
