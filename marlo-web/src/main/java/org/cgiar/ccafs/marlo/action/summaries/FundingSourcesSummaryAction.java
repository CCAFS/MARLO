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
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
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
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
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

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

public class FundingSourcesSummaryAction extends BaseAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // Variables
  private Crp loggedCrp;
  private int year;
  private String cycle;
  // Managers
  private CrpManager crpManager;
  private CrpProgramManager programManager;

  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;

  @Inject
  public FundingSourcesSummaryAction(APConfig config, CrpManager crpManager, CrpProgramManager programManager) {
    super(config);
    this.crpManager = crpManager;
    this.programManager = programManager;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    Resource reportResource =
      manager.createDirectly(this.getClass().getResource("/pentaho/FundingSourcesSummary.prpt"), MasterReport.class);

    MasterReport masterReport = (MasterReport) reportResource.getResource();
    String center = loggedCrp.getName();
    // Get parameters from URL

    // Get cycle
    if (this.getRequest().getParameter("cycle") != null) {
      cycle = this.getRequest().getParameter("cycle");
    } else {
      cycle = this.getCurrentCycle();
    }

    // Get year
    try {
      year = Integer.parseInt(this.getRequest().getParameter("year"));
    } catch (Exception e) {
      if (cycle.equals("Planning")) {
        year = this.getPlanningYear();
      } else {
        year = this.getReportingYear();
      }
    }
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

  private File getFile(String fileName) {
    // Get file from resources folder
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    return file;

  }


  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("Funding_Sources_");
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
      new String[] {"fs_title", "fs_id", "finance_code", "lead_partner", "fs_window", "project_id", "total_budget"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, Double.class}, 0);

    for (FundingSource fundingSource : loggedCrp.getFundingSources().stream()
      .filter(fs -> fs.isActive() && fs.getBudgetType() != null).collect(Collectors.toList())) {

      String fs_title = fundingSource.getTitle();
      Long fs_id = fundingSource.getId();
      String finance_code = fundingSource.getFinanceCode();


      String fs_window = fundingSource.getBudgetType().getName();


      for (ProjectBudget projectBudget : fundingSource.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getProject() != null).collect(Collectors.toList())) {
        String lead_partner = "";
        String project_id = "";
        Double total_budget = 0.0;

        project_id = projectBudget.getProject().getId().toString();
        if (projectBudget.getInstitution() != null) {
          lead_partner = projectBudget.getInstitution().getComposedName();
        }

        total_budget = projectBudget.getAmount();

        model.addRow(new Object[] {fs_title, fs_id, finance_code, lead_partner, fs_window, project_id, total_budget});
      }

    }
    return model;
  }

  private TypedTableModel getFundingSourcesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"fs_title", "fs_id", "finance_code", "lead_partner", "fs_window", "project_id", "total_budget",
        "summary", "start_date", "end_date", "contract", "status", "pi_name", "pi_email", "donor",
        "total_budget_projects", "contract_name"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, Double.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Double.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

    for (FundingSource fundingSource : loggedCrp.getFundingSources().stream()
      .filter(fs -> fs.isActive() && fs.getBudgetType() != null).collect(Collectors.toList())) {

      String fs_title = fundingSource.getTitle();
      Long fs_id = fundingSource.getId();
      String finance_code = fundingSource.getFinanceCode();
      String lead_partner = "";
      String summary = fundingSource.getDescription();
      String start_date = "";
      if (fundingSource.getStartDate() != null) {
        start_date = formatter.format(fundingSource.getStartDate());
      }
      String end_date = "";

      if (fundingSource.getEndDate() != null) {
        end_date = formatter.format(fundingSource.getEndDate());
      }

      String contract = "";
      String contract_name = "";

      if (fundingSource.getFile() != null) {
        contract = this.getFundingSourceFileURL() + fundingSource.getFile().getFileName();
        contract_name = fundingSource.getFile().getFileName();
      }

      String status = "";
      status = fundingSource.getStatusName();

      String pi_name = "";
      pi_name = fundingSource.getContactPersonName();

      String pi_email = "";
      pi_email = fundingSource.getContactPersonEmail();

      String donor = "";
      if (fundingSource.getInstitution() != null) {
        donor = fundingSource.getInstitution().getComposedName();
      }


      for (FundingSourceInstitution fs_ins : fundingSource.getFundingSourceInstitutions()) {
        if (lead_partner.isEmpty()) {
          lead_partner = fs_ins.getInstitution().getComposedName();
        } else {
          lead_partner += ", \n" + fs_ins.getInstitution().getComposedName();
        }

      }
      String fs_window = fundingSource.getBudgetType().getName();


      String project_id = "";
      List<String> projectList = new ArrayList<String>();
      for (ProjectBudget projectBudget : fundingSource.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getProject() != null).collect(Collectors.toList())) {
        projectList.add(projectBudget.getProject().getId().toString());
      }

      // Remove duplicates
      Set<String> s = new LinkedHashSet<String>(projectList);

      for (String projectString : s.stream().collect(Collectors.toList())) {
        if (project_id.isEmpty()) {
          project_id = "P" + projectString;
        } else {
          project_id += ", P" + projectString;
        }
      }


      Double total_budget = 0.0;
      Double total_budget_projects = 0.0;

      for (FundingSourceBudget fundingSourceBudget : fundingSource.getFundingSourceBudgets().stream()
        .filter(fsb -> fsb.isActive() && fsb.getYear() != null && fsb.getYear().intValue() == year)
        .collect(Collectors.toList())) {
        total_budget += fundingSourceBudget.getBudget();
      }

      for (ProjectBudget projectBudget : fundingSource.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getProject().isActive()
          && pb.getProject().getStatus() != null && pb.getProject().getStatus() == 2)
        .collect(Collectors.toList())) {
        total_budget_projects += projectBudget.getAmount();
      }

      model
        .addRow(new Object[] {fs_title, fs_id, finance_code, lead_partner, fs_window, project_id, total_budget, summary,
          start_date, end_date, contract, status, pi_name, pi_email, donor, total_budget_projects, contract_name});
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
      new TypedTableModel(new String[] {"center", "date"}, new Class[] {String.class, String.class});
    model.addRow(new Object[] {center, date});
    return model;
  }


  public int getYear() {
    return year;
  }

  @Override
  public void prepare() {
    try {
      loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    } catch (Exception e) {
    }
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
