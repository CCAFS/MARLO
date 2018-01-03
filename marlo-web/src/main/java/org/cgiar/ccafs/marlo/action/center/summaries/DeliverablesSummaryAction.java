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
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
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
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
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
public class DeliverablesSummaryAction extends BaseAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;
  private static Logger LOG = LoggerFactory.getLogger(DeliverablesSummaryAction.class);

  // Streams
  InputStream inputStream;
  // PDF bytes
  private byte[] bytesExcel;
  // Services
  private ICenterProgramManager programService;
  // Params
  private CenterProgram researchProgram;
  private long startTime;
  private HashSet<Long> centerDeliverablesSet = new HashSet<Long>();
  private HashSet<Long> centerProjectsSet = new HashSet<Long>();
  private HashMap<String, Integer> deliverablesCategory = new HashMap<String, Integer>();
  private int deliverablesCategoryTotal = 0;

  @Inject
  public DeliverablesSummaryAction(APConfig config, ICenterProgramManager programService) {
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
     * Details
     */
    masterReport.getParameterValues().put("i8nId", this.getText("deliverable.deliverableId"));
    masterReport.getParameterValues().put("i8nTitle", this.getText("deliverable.title"));
    masterReport.getParameterValues().put("i8nType", this.getText("deliverable.type"));
    masterReport.getParameterValues().put("i8nSubType", this.getText("deliverable.subType"));
    masterReport.getParameterValues().put("i8nStartDate", this.getText("deliverable.startDate"));
    masterReport.getParameterValues().put("i8nEndDate", this.getText("deliverable.endDate"));
    masterReport.getParameterValues().put("i8nCrossCutting",
      this.getText("deliverable.crossCuttingDimensions.readText"));
    masterReport.getParameterValues().put("i8nDeliverableOutputs", this.getText("deliverable.outputs.readText"));
    masterReport.getParameterValues().put("i8nSupportingDocuments", this.getText("deliverable.supportingDocuments"));
    masterReport.getParameterValues().put("i8nProjectID", this.getText("project.projectId"));
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("projectDescription.name"));

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
        manager.createDirectly(this.getClass().getResource("/pentaho/centerDeliverables.prpt"), MasterReport.class);

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
      this.fillSubreport((SubReport) hm.get("summary"), "summary");

      masterReport.getParameterValues().put("count_deliverables", centerDeliverablesSet.size());
      masterReport.getParameterValues().put("count_projects", centerProjectsSet.size());

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
        model = this.getDeliverablesTableModel();
        break;
      case "summary":
        model = this.getDeliverablesSummaryTableModel();
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

  private String getCrossCuttingTheme(CenterDeliverable centerDeliverable) {
    String crossCutting = "";
    if (centerDeliverable.getDeliverableCrosscutingTheme() != null) {
      if (centerDeliverable.getDeliverableCrosscutingTheme().getClimateChange() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getClimateChange()) {
          crossCutting += "●    " + this.getText("deliverable.climateChange") + "\n";
        }
      }

      if (centerDeliverable.getDeliverableCrosscutingTheme().getGender() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getGender()) {
          crossCutting += "●    " + this.getText("deliverable.gender") + "\n";
        }
      }

      if (centerDeliverable.getDeliverableCrosscutingTheme().getYouth() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getYouth()) {
          crossCutting += "●    " + this.getText("deliverable.youth") + "\n";
        }
      }

      if (centerDeliverable.getDeliverableCrosscutingTheme().getCapacityDevelopment() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getCapacityDevelopment()) {
          crossCutting += "●    " + this.getText("deliverable.capacityDevelopment") + "\n";
        }
      }

      if (centerDeliverable.getDeliverableCrosscutingTheme().getBigData() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getBigData()) {
          crossCutting += "●    " + this.getText("deliverable.bigData") + "\n";
        }
      }

      if (centerDeliverable.getDeliverableCrosscutingTheme().getImpactAssessment() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getImpactAssessment()) {
          crossCutting += "●    " + this.getText("deliverable.impactAssessment") + "\n";
        }
      }

      if (centerDeliverable.getDeliverableCrosscutingTheme().getPoliciesInstitutions() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getPoliciesInstitutions()) {
          crossCutting += "●    " + this.getText("deliverable.policiesInstitutions") + "\n";
        }
      }

      if (centerDeliverable.getDeliverableCrosscutingTheme().getNa() != null) {
        if (centerDeliverable.getDeliverableCrosscutingTheme().getNa()) {
          crossCutting += "●    " + this.getText("deliverable.na") + "\n";
        }
      }
    }
    return crossCutting;
  }

  private TypedTableModel getDeliverablesSummaryTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"namePercentage", "countCategory"}, new Class[] {String.class, Integer.class});

    for (String deliverableType : deliverablesCategory.keySet()) {
      model.addRow(new Object[] {deliverableType + " - "
        + ((deliverablesCategory.get(deliverableType) * 100) / deliverablesCategoryTotal) + " %",
        deliverablesCategory.get(deliverableType)});
    }
    return model;
  }

  private TypedTableModel getDeliverablesTableModel() {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverableId", "title", "type", "subType", "startDate", "endDate", "crossCutting",
        "deliverableOutputs", "supportingDocuments", "projectId", "projectTitle"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class});

    for (CenterProject centerProject : researchProgram.getProjects().stream().filter(p -> p.isActive())
      .collect(Collectors.toList())) {
      for (CenterDeliverable centerDeliverable : centerProject.getDeliverables().stream().filter(d -> d.isActive())
        .collect(Collectors.toList())) {
        String deliverableId = centerDeliverable.getId().toString();
        String title = null;
        if (centerDeliverable.getName() != null && !centerDeliverable.getName().trim().isEmpty()) {
          title = centerDeliverable.getName();
        }
        String type = null;
        String subType = null;
        if (centerDeliverable.getDeliverableType() != null
          && centerDeliverable.getDeliverableType().getName() != null) {
          type = centerDeliverable.getDeliverableType().getName();

          if (centerDeliverable.getDeliverableType().getDeliverableType() != null
            && centerDeliverable.getDeliverableType().getDeliverableType().getName() != null) {
            subType = centerDeliverable.getDeliverableType().getDeliverableType().getName();
          }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
        String startDate = null;
        if (centerDeliverable.getStartDate() != null) {
          startDate = formatter.format(centerDeliverable.getStartDate());
        }

        String endDate = null;
        if (centerDeliverable.getEndDate() != null) {
          endDate = formatter.format(centerDeliverable.getEndDate());
        }

        String crossCutting = this.getCrossCuttingTheme(centerDeliverable);

        if (crossCutting.trim().isEmpty()) {
          crossCutting = null;
        }

        String deliverableOutputs = "";
        for (CenterDeliverableOutput centerDeliverableOutput : centerDeliverable.getDeliverableOutputs().stream()
          .filter(devo -> devo.isActive()).collect(Collectors.toList())) {
          deliverableOutputs += "●    " + centerDeliverableOutput.getResearchOutput().getComposedName() + "\n";
        }

        if (deliverableOutputs.trim().isEmpty()) {
          deliverableOutputs = null;
        }

        String supportingDocuments = "";
        for (CenterDeliverableDocument centerDeliverableDocument : centerDeliverable.getDeliverableDocuments().stream()
          .filter(dd -> dd.isActive()).collect(Collectors.toList())) {
          supportingDocuments += "●    " + centerDeliverableDocument.getLink() + "\n";
        }
        if (supportingDocuments.trim().isEmpty()) {
          supportingDocuments = null;
        }

        String projectId = null;
        if (centerDeliverable.getProject() != null && centerDeliverable.getProject().getId() != null) {
          projectId = centerDeliverable.getProject().getId().toString();
        }

        String projectTitle = null;
        if (centerDeliverable.getProject() != null && centerDeliverable.getProject().getName() != null
          && !centerDeliverable.getProject().getName().trim().isEmpty()) {
          projectTitle = centerDeliverable.getProject().getName();
        }
        centerDeliverablesSet.add(centerDeliverable.getId());
        centerProjectsSet.add(centerProject.getId());
        if (type != null && !type.isEmpty()) {
          Integer deliverablesCategoryCount =
            deliverablesCategory.containsKey(type) ? deliverablesCategory.get(type) : 0;
          deliverablesCategory.put(type, deliverablesCategoryCount + 1);
          deliverablesCategoryTotal++;
        }

        model.addRow(new Object[] {deliverableId, title, type, subType, startDate, endDate, crossCutting,
          deliverableOutputs, supportingDocuments, projectId, projectTitle});
      }
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
    fileName.append("Deliverables_report-");
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
    String center = this.getCenterSession();

    model.addRow(new Object[] {currentDate, center, researchProgram.getName()});
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