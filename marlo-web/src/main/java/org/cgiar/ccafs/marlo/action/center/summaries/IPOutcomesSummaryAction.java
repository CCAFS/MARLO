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
import org.cgiar.ccafs.marlo.data.manager.ICenterMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
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
import java.util.List;
import java.util.Map;

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
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */
public class IPOutcomesSummaryAction extends BaseAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;
  private static Logger LOG = LoggerFactory.getLogger(IPOutcomesSummaryAction.class);

  // Streams
  InputStream inputStream;
  // PDF bytes
  private byte[] bytesExcel;
  // Services
  private ICenterProgramManager programService;
  private ICenterOutcomeManager outcomeService;
  private ICenterMilestoneManager milestoneService;
  // Params
  private CenterProgram researchProgram;
  private long startTime;

  @Inject
  public IPOutcomesSummaryAction(APConfig config, ICenterProgramManager programService,
    ICenterOutcomeManager outcomeService, ICenterMilestoneManager milestoneService) {
    super(config);
    this.programService = programService;
    this.outcomeService = outcomeService;
    this.milestoneService = milestoneService;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    /**
     * Details
     */
    masterReport.getParameterValues().put("i8nId", this.getText("outcome.id"));
    masterReport.getParameterValues().put("i8nTitle", this.getText("outcome.title"));
    masterReport.getParameterValues().put("i8nImpact", this.getText("outcome.impactStatement"));
    masterReport.getParameterValues().put("i8nTopic", this.getText("outcome.researchTopic"));
    masterReport.getParameterValues().put("i8nTargetUnit", this.getText("outcome.targetUnit"));
    masterReport.getParameterValues().put("i8nTargetValue", this.getText("outcome.targetValue"));
    masterReport.getParameterValues().put("i8nTargetYear", this.getText("outcome.targetYear"));
    masterReport.getParameterValues().put("i8nMilestoneId", this.getText("outcome.milestoneId"));
    masterReport.getParameterValues().put("i8nMilestoneTitle", this.getText("outcome.milestoneTitle"));
    masterReport.getParameterValues().put("i8nMilestoneTargetUnit", this.getText("outcome.milestone.targetUnit"));
    masterReport.getParameterValues().put("i8nMilestoneTargetValue", this.getText("outcome.milestone.targetValue"));
    masterReport.getParameterValues().put("i8nMilestoneTargetYear", this.getText("outcome.milestone.targerYear"));
    masterReport.getParameterValues().put("i8nCount", "Count");


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
        manager.createDirectly(this.getClass().getResource("/pentaho/centerOutcomes.prpt"), MasterReport.class);

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
      this.fillSubreport((SubReport) hm.get("details"), "details");

      this.fillSubreport((SubReport) hm.get("outcomeGraph"), "outcomeGraph");

      this.fillSubreport((SubReport) hm.get("milestoneGraph"), "milestoneGraph");


      ExcelReportUtil.createXLSX(masterReport, os);
      bytesExcel = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating Excel " + e.getMessage());
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
      case "details":
        model = this.getOutcomeTableModel();
        break;
      case "outcomeGraph":
        model = this.getOutcomeTargetUnitModel();
        break;

      case "milestoneGraph":
        model = this.getMilestoneTargetUnitModel();
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

  public byte[] getBytesExcel() {
    return bytesExcel;
  }

  @Override
  public int getContentLength() {
    return bytesExcel.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
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
    fileName.append(researchProgram.getAcronym() + "_Outcomes_report-");
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
      inputStream = new ByteArrayInputStream(bytesExcel);
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
    TypedTableModel model = new TypedTableModel(new String[] {"currentDate", "center", "researchProgram"},
      new Class[] {String.class, String.class, String.class});
    String currentDate = "";

    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    currentDate = timezone.format(format) + this.getTimeZone();

    // Get CIAT imgage URL from repo
    String center = this.getCenterSession();

    model.addRow(new Object[] {currentDate, center, researchProgram.getName()});
    return model;
  }

  private TypedTableModel getMilestoneTargetUnitModel() {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"milestoneTargetUnitName", "milestoneNumber"},
      new Class[] {String.class, Integer.class});

    List<Map<String, Object>> reportOutcome = milestoneService.getCountTargetUnit(researchProgram.getId());

    if (reportOutcome != null) {
      for (Map<String, Object> map : reportOutcome) {


        String milestoneTargetUnitName = map.get("targetUnit").toString();
        Integer milestoneNumber = Integer.parseInt(map.get("count").toString());


        model.addRow(new Object[] {milestoneTargetUnitName, milestoneNumber});
      }
    }
    return model;
  }

  private TypedTableModel getOutcomeTableModel() {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"outcomeId", "outcomeTitle", "impactStatement", "researchTopic", "outcomeTargetUnit",
        "outcomeTargetValue", "outcomeTargetYear", "milestoneId", "milestoneTitle", "milestoneTargetUnit",
        "milestoneTargetValue", "milestoneTargetYear", "outcomeUrl"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class});

    List<Map<String, Object>> reportOutcome = outcomeService.getImpactPathwayOutcomes(researchProgram.getId());

    if (reportOutcome != null) {
      for (Map<String, Object> map : reportOutcome) {


        String outcomeId = map.get("outcomeId").toString();
        String outcomeUrl =
          this.getBaseUrl() + "/centerImpactPathway/" + this.getCenterSession() + "/outcomes.do?outcomeID=" + outcomeId;
        String outcomeTitle = map.get("outcomeDesc").toString();
        String impactStatement = map.get("outcomeDesc").toString();
        String researchTopic = map.get("topic").toString();
        String outcomeTargetUnit = map.get("outcomeTargetUnit").toString();
        String outcomeTargetValue = map.get("outcomeValue").toString();
        String outcomeTargetYear = map.get("outcomeYear").toString();
        String milestoneId = map.get("milestoneId").toString();
        String milestoneTitle = map.get("milestoneDesc").toString();
        String milestoneTargetUnit = map.get("milestoneTargetUnit").toString();
        String milestoneTargetValue = map.get("milestoneValue").toString();
        String milestoneTargetYear = map.get("milestoneYear").toString();


        model.addRow(new Object[] {outcomeId, outcomeTitle, impactStatement, researchTopic, outcomeTargetUnit,
          outcomeTargetValue, outcomeTargetYear, milestoneId, milestoneTitle, milestoneTargetUnit, milestoneTargetValue,
          milestoneTargetYear, outcomeUrl});
      }
    }
    return model;
  }


  private TypedTableModel getOutcomeTargetUnitModel() {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"outcomeTargetUnitName", "outcomeNumber"},
      new Class[] {String.class, Integer.class});

    List<Map<String, Object>> reportOutcome = outcomeService.getCountTargetUnit(researchProgram.getId());

    if (reportOutcome != null) {
      for (Map<String, Object> map : reportOutcome) {


        String outcomeTargetUnitName = map.get("targetUnit").toString();
        Integer outcomeNumber = Integer.parseInt(map.get("count").toString());


        model.addRow(new Object[] {outcomeTargetUnitName, outcomeNumber});
      }
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

  public void setBytesExcel(byte[] bytesExcel) {
    this.bytesExcel = bytesExcel;
  }

  public void setResearchProgram(CenterProgram researchProgram) {
    this.researchProgram = researchProgram;
  }

}