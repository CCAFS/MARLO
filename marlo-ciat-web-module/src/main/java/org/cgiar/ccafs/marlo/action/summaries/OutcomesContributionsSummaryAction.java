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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.config.PentahoListener;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.utils.APConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */
public class OutcomesContributionsSummaryAction extends BaseAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;
  private static Logger LOG = LoggerFactory.getLogger(OutcomesContributionsSummaryAction.class);

  // Streams
  InputStream inputStream;
  // PDF bytes
  private byte[] bytesExcel;
  // Services
  private ICenterProgramManager programService;
  // Params
  private CenterProgram researchProgram;
  private long startTime;
  // Store parters budgets HashMap<Outcome, ProjectCount>
  HashMap<CenterOutcome, Integer> allOutcomesProjects = new HashMap<CenterOutcome, Integer>();

  @Inject
  public OutcomesContributionsSummaryAction(APConfig config, ICenterProgramManager programService) {
    super(config);
    this.programService = programService;
  }

  @Override
  public String execute() throws Exception {


    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager =
      (ResourceManager) ServletActionContext.getServletContext().getAttribute(PentahoListener.KEY_NAME);
    // manager.registerDefaults();
    try {

      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/outcomesContributions.prpt"), MasterReport.class);

      // Get main report
      MasterReport masterReport = (MasterReport) reportResource.getResource();

      // Get program from DB
      // project = projectManager.getProjectById(projectID);


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

      // Subreport Description
      this.fillSubreport((SubReport) hm.get("details"), "details");
      // Sort allOutcomesProjects per count
      allOutcomesProjects = this.sortByComparator(allOutcomesProjects);
      this.fillSubreport((SubReport) hm.get("outcomesProjects"), "outcomesProjects");
      this.fillSubreport((SubReport) hm.get("graphic"), "graphic");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesExcel = os.toByteArray();
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
      case "details":
        model = this.getOutcomesDetailsTableModel();
        break;
      case "outcomesProjects":
        model = this.getOutcomesProjectsTableModel();
        break;
      case "graphic":
        model = this.getOutcomesProjectsTableModel();
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

  private File getFile(String fileName) {
    // Get file from resources folder
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    return file;
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("Outcomes_Contributions_Report-");
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
    TypedTableModel model = new TypedTableModel(
      new String[] {"current_date", "imageUrl", "research_program_id", "center", "researchProgramTitle"},
      new Class[] {String.class, String.class, Long.class, String.class, String.class});
    String currentDate = "";
    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    currentDate = timezone.format(format) + this.getTimeZone();

    // Get CIAT imgage URL from repo
    String imageUrl = this.getBaseUrl() + "/images/global/centers/CIAT.png";

    String center = null;
    center = researchProgram.getResearchArea().getResearchCenter().getName();

    String researchProgramTitle = null;
    if (researchProgram.getName() != null && !researchProgram.getName().trim().isEmpty()) {
      researchProgramTitle = researchProgram.getName();
    }

    model.addRow(new Object[] {currentDate, imageUrl, researchProgram.getId(), center, researchProgramTitle});
    return model;
  }

  private TypedTableModel getOutcomesDetailsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"researchTopicTitle", "researchOutcomeID", "researchOutcomeURL", "researchOutcomeTitle",
        "researchImpactTitle", "projectOutputs", "projectDeliverables"},
      new Class[] {String.class, Long.class, String.class, String.class, String.class, String.class, String.class});

    for (CenterTopic researchTopic : researchProgram.getResearchTopics().stream().filter(rt -> rt.isActive())
      .collect(Collectors.toList())) {
      for (CenterOutcome researchOutcome : researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive())
        .collect(Collectors.toList())) {


        String researchTopicTitle = null;
        if (researchTopic.getResearchTopic() != null && !researchTopic.getResearchTopic().trim().isEmpty()) {
          researchTopicTitle = researchTopic.getResearchTopic();
        }

        Long researchOutcomeID = null;
        String researchOutcomeURL = null;
        if (researchOutcome.getId() != null) {
          researchOutcomeID = researchOutcome.getId();
          // TODO: set outcome URL /impactPathway/CIAT/outcomes.do?outcomeID=9&edit=true
          researchOutcomeURL =
            config.getBaseUrl() + "/monitoring/CIAT/monitoringOutcome.do?outcomeID=" + researchOutcomeID;
        }

        String researchOutcomeTitle = null;
        researchOutcomeTitle =
          (researchOutcome.getDescription() != null ? researchOutcome.getDescription() : "title not defined")
            + (researchOutcome.getShortName() != null && !researchOutcome.getShortName().trim().isEmpty()
              ? " (" + researchOutcome.getShortName() + ")" : "");

        String researchImpactTitle = null;
        if (researchOutcome.getResearchImpact() != null && researchOutcome.getResearchImpact().getDescription() != null
          && !researchOutcome.getResearchImpact().getDescription().trim().isEmpty()) {
          researchImpactTitle = researchOutcome.getResearchImpact().getDescription();
        }
        List<CenterProject> projects = new ArrayList<>();
        // CenterProject Outputs
        String projectOutputs = "";
        for (CenterOutput researchOutput : researchOutcome.getResearchOutputs().stream().filter(ro -> ro.isActive())
          .collect(Collectors.toList())) {
          for (CenterProjectOutput projectOutput : researchOutput.getProjectOutputs().stream()
            .filter(po -> po.isActive()).collect(Collectors.toList())) {
            if (projectOutputs.isEmpty()) {
              projectOutputs =
                "P" + projectOutput.getProject().getId() + " - " + projectOutput.getResearchOutput().getComposedName();
            } else {
              projectOutputs += "\nP" + projectOutput.getProject().getId() + " - "
                + projectOutput.getResearchOutput().getComposedName();
            }
            projects.add(projectOutput.getProject());
          }
        }
        if (projectOutputs.trim().isEmpty()) {
          projectOutputs = null;
        }

        // CenterProject Deliverables
        HashSet<CenterProject> hashProjects = new HashSet<>();
        hashProjects.addAll(projects);
        projects = new ArrayList<>(hashProjects);

        String projectDeliverables = "";
        for (CenterProject project : projects) {
          for (CenterDeliverable deliverable : project.getDeliverables().stream().filter(d -> d.isActive())
            .collect(Collectors.toList())) {
            if (projectDeliverables.isEmpty()) {
              projectDeliverables = "P" + project.getId() + " - " + "D" + deliverable.getId() + ": "
                + (deliverable.getName() != null && !deliverable.getName().trim().isEmpty() ? deliverable.getName()
                  : "");
            } else {
              projectDeliverables += "\nP" + project.getId() + " - " + "D" + deliverable.getId() + ": "
                + (deliverable.getName() != null && !deliverable.getName().trim().isEmpty() ? deliverable.getName()
                  : "");
            }
          }
        }
        if (projectDeliverables.trim().isEmpty()) {
          projectDeliverables = null;
        }

        model.addRow(new Object[] {researchTopicTitle, researchOutcomeID, researchOutcomeURL, researchOutcomeTitle,
          researchImpactTitle, projectOutputs, projectDeliverables});

        // Increment outcomes Projects count
        if (allOutcomesProjects.containsKey(researchOutcome)) {
          allOutcomesProjects.put(researchOutcome, allOutcomesProjects.get(researchOutcome) + projects.size());
        } else {
          if (projects.size() > 0) {
            allOutcomesProjects.put(researchOutcome, projects.size());
          }

        }
      }
    }
    return model;
  }

  private TypedTableModel getOutcomesProjectsTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"outcomeID", "outcomeTitle", "projectCount", "researchOutcomeURL"},
        new Class[] {String.class, String.class, String.class, String.class});

    for (CenterOutcome researchOutcome : allOutcomesProjects.keySet()) {
      String researchOutcomeTitle = null;
      researchOutcomeTitle = researchOutcome.getComposedName()
        + (researchOutcome.getShortName() != null && !researchOutcome.getShortName().trim().isEmpty()
          ? " (" + researchOutcome.getShortName() + ")" : "");
      String researchOutcomeURL = null;
      researchOutcomeURL =
        config.getBaseUrl() + "/monitoring/CIAT/monitoringOutcome.do?outcomeID=" + researchOutcome.getId();
      model.addRow(new Object[] {"OC" + researchOutcome.getId(), researchOutcomeTitle,
        allOutcomesProjects.get(researchOutcome), researchOutcomeURL});
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
    this.bytesExcel = bytesPDF;
  }

  public void setResearchProgram(CenterProgram researchProgram) {
    this.researchProgram = researchProgram;
  }

  /**
   * method that sort a map list alphabetical
   * 
   * @param unsortMap - map to sort
   * @return
   */
  private HashMap<CenterOutcome, Integer> sortByComparator(HashMap<CenterOutcome, Integer> unsortMap) {

    // Convert Map to List
    List<HashMap.Entry<CenterOutcome, Integer>> list =
      new LinkedList<HashMap.Entry<CenterOutcome, Integer>>(unsortMap.entrySet());

    // Sort list with comparator, to compare the Map values
    Collections.sort(list, new Comparator<HashMap.Entry<CenterOutcome, Integer>>() {

      @Override
      public int compare(HashMap.Entry<CenterOutcome, Integer> o1, HashMap.Entry<CenterOutcome, Integer> o2) {

        return (o2.getValue().compareTo(o1.getValue()));
      }
    });

    // Convert sorted map back to a Map
    HashMap<CenterOutcome, Integer> sortedMap = new LinkedHashMap<CenterOutcome, Integer>();
    for (Iterator<HashMap.Entry<CenterOutcome, Integer>> it = list.iterator(); it.hasNext();) {
      HashMap.Entry<CenterOutcome, Integer> entry = it.next();
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

}