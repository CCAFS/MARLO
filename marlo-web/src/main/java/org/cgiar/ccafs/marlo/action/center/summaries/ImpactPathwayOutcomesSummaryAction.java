/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.center.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.config.PentahoListener;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.model.CenterImpactObjective;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */
public class ImpactPathwayOutcomesSummaryAction extends BaseAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;
  private static Logger LOG = LoggerFactory.getLogger(ImpactPathwayOutcomesSummaryAction.class);

  // Streams
  InputStream inputStream;
  // PDF bytes
  private byte[] bytesPDF;
  // Services
  private ICenterProgramManager programService;
  // Params
  private CenterProgram researchProgram;
  private long startTime;

  @Inject
  public ImpactPathwayOutcomesSummaryAction(APConfig config, ICenterProgramManager programService) {
    super(config);
    this.programService = programService;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    /**
     * Program Impacts
     */
    masterReport.getParameterValues().put("i8nPiTitle", this.getText("impactPathway.menu.hrefProgramImpacts"));
    masterReport.getParameterValues().put("i8nPiStatement", this.getText("impactPathway.statement") + ":");
    masterReport.getParameterValues().put("i8nPiObjetives", this.getText("programImpact.objectives.readText") + ":");
    masterReport.getParameterValues().put("i8nPiIntendedBeneficiaries",
      this.getText("impactPathway.intendedBeneficiaries") + ":");
    masterReport.getParameterValues().put("i8nPiType", this.getText("impactPathway.type"));
    masterReport.getParameterValues().put("i8nPiFocus", this.getText("impactPathway.focus"));
    masterReport.getParameterValues().put("i8nPiRegion", this.getText("impactPathway.region"));
    masterReport.getParameterValues().put("i8nPiNoData", this.getText("impactPathway.noData"));

    return masterReport;
  }

  @Override
  public String execute() throws Exception {


    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager =
      (ResourceManager) ServletActionContext.getServletContext().getAttribute(PentahoListener.KEY_NAME);
    // manager.registerDefaults();
    try {

      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/impactPathwayOutcomes.prpt"), MasterReport.class);

      // Get main report
      MasterReport masterReport = (MasterReport) reportResource.getResource();

      // Get details band
      ItemBand masteritemBand = masterReport.getItemBand();
      // Create new empty subreport hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the subreports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      System.out.println("Pentaho SubReports: " + hm);

      // Set Main_Query
      String masterQueryName = "main";
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel();
      sdf.addTable(masterQueryName, model);
      masterReport.setDataFactory(cdf);
      // Set i8n for pentaho
      masterReport = this.addi8nParameters(masterReport);

      // Subreport Program Impacts
      this.fillSubreport((SubReport) hm.get("programImpacts"), "programImpacts");


      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating PDF " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;

  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "programImpacts":
        model = this.getProgramImpactTableModel();
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

  public byte[] getBytesPDF() {
    return bytesPDF;
  }

  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
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
    fileName.append("Impact_Pathway_Outcomes_Report-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
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
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }

  /**
   * Get the main information of the report
   * 
   * @return
   */
  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"title", "current_date", "imageUrl", "research_program_id"},
        new Class[] {String.class, String.class, String.class, Long.class});
    String title = "Impact Pathway Full Report";
    String currentDate = "";

    // Get title
    title = researchProgram.getResearchArea().getAcronym() + ", " + researchProgram.getComposedName()
      + ", Organized by Impact";

    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    currentDate = timezone.format(format) + this.getTimeZone();

    // Get CIAT imgage URL from repo
    String imageUrl = this.getBaseUrl() + "/images/global/centers/CIAT.png";


    model.addRow(new Object[] {title, currentDate, imageUrl, researchProgram.getId()});
    return model;
  }

  private TypedTableModel getProgramImpactTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"impactStatement", "impactObjetives", "program_id", "research_impact_id"},
        new Class[] {String.class, String.class, Long.class, Long.class});
    for (CenterImpact researchImpact : researchProgram.getResearchImpacts().stream().filter(rp -> rp.isActive())
      .collect(Collectors.toList())) {
      String impactStatement = "";
      String impactObjetives = "";
      long programId = 0;
      if (researchImpact.getDescription() == null || researchImpact.getDescription().isEmpty()) {
        impactStatement = "&lt;Not Defined&gt;";
      } else {
        impactStatement = researchImpact.getDescription();
      }

      if (researchImpact.getResearchImpactObjectives() != null
        && researchImpact.getResearchImpactObjectives().size() > 0) {
        for (CenterImpactObjective researchImpactObjective : researchImpact.getResearchImpactObjectives().stream()
          .filter(rio -> rio.isActive() && rio.getResearchObjective() != null).collect(Collectors.toList())) {
          impactObjetives += "<br>&#9679    " + researchImpactObjective.getResearchObjective().getObjective();
        }
      } else {
        impactObjetives = "<br>&#9679    &lt;Not Defined&gt;";
      }
      if (impactObjetives.isEmpty()) {
        impactObjetives = null;
      }
      if (researchProgram != null) {
        programId = researchProgram.getId();
      }

      model.addRow(new Object[] {impactStatement, impactObjetives, programId, researchImpact.getId()});
    }
    return model;
  }

  public CenterProgram getResearchProgram() {
    return researchProgram;
  }

  @Override
  public void prepare() {
    long programID = -1;
    try {
      programID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_PROGRAM_ID)));
      researchProgram = programService.getProgramById(programID);
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.CENTER_PROGRAM_ID + " parameter. Exception: " + e.getMessage());
    }
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName());
  }

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }

  public void setResearchProgram(CenterProgram researchProgram) {
    this.researchProgram = researchProgram;
  }

}