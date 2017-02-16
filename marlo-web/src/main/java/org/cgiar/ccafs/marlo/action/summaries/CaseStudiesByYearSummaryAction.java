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
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.Crp;
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
import java.util.List;
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
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CaseStudiesByYearSummaryAction extends BaseAction implements Summary {


  private static final long serialVersionUID = 8916546917647383705L;


  private CaseStudyManager caseStudyManager;

  private CrpManager crpManager;

  private int year;


  private Crp loggedCrp;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public CaseStudiesByYearSummaryAction(APConfig config, CaseStudyManager caseStudyManager, CrpManager crpManager) {
    super(config);
    this.caseStudyManager = caseStudyManager;
    this.crpManager = crpManager;
  }

  @Override
  public String execute() throws Exception {

    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    Resource reportResource =
      manager.createDirectly(this.getClass().getResource("/pentaho/CaseStudies.prpt"), MasterReport.class);

    MasterReport masterReport = (MasterReport) reportResource.getResource();
    String center = loggedCrp.getName();


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
    TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(year));
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

    this.fillSubreport((SubReport) hm.get("case_studies"), "case_studies");

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
      case "case_studies":
        model = this.getCaseStudiesTableModel(year);
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


  public byte[] getBytesXLSX() {
    return bytesXLSX;
  }

  private TypedTableModel getCaseStudiesTableModel(int year) {


    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "outcomeStatement", "researchOutputs", "researchPartners", "activities",
        "nonResearchPartneres", "outputUsers", "evidenceOutcome", "outputUsed", "referencesCase",
        "explainIndicatorRelation", "anex", "owner", "indicators", "shared"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);

    Long id = null;

    String title = "", outcomeStatement = "", researchOutputs = "", researchPartners = "", activities = "",
      nonResearchPartneres = "", outputUsers = "", evidenceOutcome = "", outputUsed = "", referencesCase = "",
      explainIndicatorRelation = "", anex = "", owner = "", shared = "", indicators = "";

    if (caseStudyManager.findAll() != null) {

      List<CaseStudy> caseStudies = new ArrayList<>(caseStudyManager.findAll().stream()
        .filter(cs -> cs.isActive() && cs.getYear() == year).collect(Collectors.toList()));

      if (!caseStudies.isEmpty()) {
        for (CaseStudy caseStudy : caseStudies) {

          id = caseStudy.getId();

          title = caseStudy.getTitle();

          outcomeStatement = caseStudy.getOutcomeStatement();

          researchOutputs = caseStudy.getResearchOutputs();

          researchPartners = caseStudy.getResearchPartners();

          activities = caseStudy.getActivities();

          nonResearchPartneres = caseStudy.getNonResearchPartneres();

          outputUsers = caseStudy.getOutputUsers();

          outputUsed = caseStudy.getOutputUsed();

          evidenceOutcome = caseStudy.getEvidenceOutcome();

          referencesCase = caseStudy.getReferencesCase();

          explainIndicatorRelation = caseStudy.getExplainIndicatorRelation();

          List<CaseStudyProject> studyProjects = new ArrayList<>(
            caseStudy.getCaseStudyProjects().stream().filter(csp -> csp.isActive()).collect(Collectors.toList()));

          for (CaseStudyProject caseStudyProject : studyProjects) {
            if (caseStudyProject.isCreated()) {
              shared = String.valueOf(caseStudyProject.getProject().getId());
            }

            owner = "P" + caseStudyProject.getProject().getId();
          }

          List<CaseStudyIndicator> studyIndicators = new ArrayList<>(
            caseStudy.getCaseStudyIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

          StringBuilder indicatorsS = new StringBuilder();


          for (CaseStudyIndicator caseStudyIndicator : studyIndicators) {
            if (caseStudyIndicator.isActive()) {
              indicatorsS.append(caseStudyIndicator.getIpIndicator().getDescription() + "\n");
            }
          }

          indicators = indicatorsS.toString();

          if (caseStudy.getFile() != null) {
            anex = this.getCaseStudyUrl(shared) + caseStudy.getFile().getFileName();
          }


          model.addRow(new Object[] {id, title, outcomeStatement, researchOutputs, researchPartners, activities,
            nonResearchPartneres, outputUsers, evidenceOutcome, outputUsed, referencesCase, explainIndicatorRelation,
            anex, owner, indicators, shared});

        }
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
    return bytesXLSX.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
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
    fileName.append("CaseStudiesSummary_");
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

  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year"},
      new Class[] {String.class, String.class, String.class});
    model.addRow(new Object[] {center, date, year});
    return model;
  }

  public int getYear() {
    return year;
  }

  @Override
  public void prepare() throws Exception {
    try {
      loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    } catch (Exception e) {
    }

    try {
      year = Integer.parseInt(this.getRequest().getParameter("year"));
    } catch (Exception e) {
      year = this.getCurrentCycleYear();
    }
  }

  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setYear(int year) {
    this.year = year;
  }


}
