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
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
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

public class DeliverablesParticipantsSummaryAction extends BaseSummariesAction implements Summary {

  private static Logger LOG = LoggerFactory.getLogger(DeliverablesParticipantsSummaryAction.class);

  private static final long serialVersionUID = 7635064842971227743L;
  // Managers
  private final ResourceManager resourceManager;
  private final DeliverableManager deliverableManager;

  // Parameters
  private byte[] bytesXLSX;
  private String showAllYears;
  private long startTime;
  private InputStream inputStream;

  public DeliverablesParticipantsSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager, ResourceManager resourceManager, DeliverableManager deliverableManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.deliverableManager = deliverableManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nColumnA", this.getText("searchTerms.deliverableId"));
    masterReport.getParameterValues().put("i8nColumnB", this.getText("project.deliverable.generalInformation.title"));
    masterReport.getParameterValues().put("i8nColumnC",
      this.getText("project.deliverable.generalInformation.description"));
    masterReport.getParameterValues().put("i8nColumnD", this.getText("project.deliverable.generalInformation.type"));
    masterReport.getParameterValues().put("i8nColumnE", this.getText("project.deliverable.generalInformation.subType"));
    masterReport.getParameterValues().put("i8nColumnF", this.getText("project.deliverable.generalInformation.status"));
    masterReport.getParameterValues().put("i8nColumnG", this.getText("project.deliverable.generalInformation.year"));
    masterReport.getParameterValues().put("i8nColumnH", this.getText("deliverable.newExpectedYear"));
    masterReport.getParameterValues().put("i8nColumnI", this.getText("involveParticipants.title"));
    masterReport.getParameterValues().put("i8nColumnJ", this.getText("involveParticipants.typeActivity"));
    masterReport.getParameterValues().put("i8nColumnK", this.getText("involveParticipants.academicDegree"));
    masterReport.getParameterValues().put("i8nColumnL", this.getText("involveParticipants.participants"));
    masterReport.getParameterValues().put("i8nColumnM", this.getText("involveParticipants.estimate.participant"));
    masterReport.getParameterValues().put("i8nColumnN", this.getText("involveParticipants.females"));
    masterReport.getParameterValues().put("i8nColumnO", this.getText("involveParticipants.estimate.female"));
    masterReport.getParameterValues().put("i8nColumnP", this.getText("involveParticipants.participantsType"));
    masterReport.getParameterValues().put("i8nColumnQ", this.getText("involveParticipants.trainingPeriod"));
    masterReport.getParameterValues().put("i8nHeader", this.getText("summaries.deliverable.participants"));
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
        .createDirectly(this.getClass().getResource("/pentaho/crp/DeliverablesParticipants.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();

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
      TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(this.getSelectedYear()));
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

      this.fillSubreport((SubReport) hm.get("details"), "details");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating DeliverablesParticipants " + e.getMessage());
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
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  /**
   * @return the bytesXLSX
   */
  public byte[] getBytesXLSX() {
    return bytesXLSX;
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
    /*
     * Parameters variables to send to the file
     * paramA - DeliverableID
     * paramB - Title
     * paramC - Description
     * paramD - Category
     * paramE - Sub Category
     * paramF - Status
     * paramG - Year
     * paramH - Expected Year (Possible <Not applicable>)
     * paramI - Event/Activity name
     * paramJ - Type of Activity
     * paramK - Academic Degree (Possible <Not applicable>)
     * paramL - Total number of participants
     * paramM - Total estimate
     * paramN - Number of females (Possible <Not applicable>)
     * paramO - Female Estimate (Possible <Not applicable>)
     * paramP - Type of Participant(s)
     * paramQ - Training period of time
     * deliverableURL
     * NOTE : does not mater the order into the implementation (ex: the paramO will be setup first that the paramA)
     */
    TypedTableModel model = new TypedTableModel(
      new String[] {"paramA", "paramB", "paramC", "paramD", "paramE", "paramF", "paramG", "paramH", "paramI", "paramJ",
        "paramK", "paramL", "paramM", "paramN", "paramO", "paramP", "paramQ", "deliverableURL"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);


    List<Deliverable> deliverables = new ArrayList<Deliverable>();
    if (showAllYears.equals("true")) {
      deliverables = deliverableManager.getDeliverablesByParameters(this.getSelectedPhase(), false, true);
    } else {
      deliverables = deliverableManager.getDeliverablesByParameters(this.getSelectedPhase(), true, true);
    }

    if (deliverables != null && !deliverables.isEmpty()) {
      for (Deliverable deliverable : deliverables) {
        String paramA = null, paramB = null, paramC = null, paramD = null, paramE = null, paramF = null, paramG = null,
          paramH = null, paramI = null, paramJ = null, paramK = null, paramL = null, paramM = null, paramN = null,
          paramO = null, paramP = null, paramQ = null, deliverableURL = null;

        // paramA - DeliverableID
        paramA = "D" + deliverable.getId();

        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(this.getSelectedPhase());
        if (deliverableInfo != null) {
          // paramB - Title
          if (deliverableInfo.getTitle() != null && !deliverableInfo.getTitle().isEmpty()) {
            paramB = deliverableInfo.getTitle();
          }
          // paramC - Description
          if (deliverableInfo.getDescription() != null && !deliverableInfo.getDescription().isEmpty()) {
            paramC = deliverableInfo.getDescription();
          }
          // paramD - Category
          if (deliverable.getDeliverableInfo().getDeliverableType() != null) {

            if (deliverable.getDeliverableInfo().getDeliverableType().getDeliverableCategory() != null) {
              paramD = deliverable.getDeliverableInfo().getDeliverableType().getDeliverableCategory().getName();
            }
            // paramE - Sub Category
            if (deliverable.getDeliverableInfo().getDeliverableType().getName() != null) {
              paramE = deliverable.getDeliverableInfo().getDeliverableType().getName();
            }

          }
          // paramF - Status
          if (deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase()) != null
            && !deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase()).isEmpty()) {
            paramF = deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase());

            // paramH - Expected Year (Possible <Not applicable>)
            // Ongoing
            if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
              paramH = "<Not Applicable>";
            } else {
              // Extended
              if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())
                && deliverable.getDeliverableInfo().getNewExpectedYear() != null
                && deliverable.getDeliverableInfo().getNewExpectedYear().intValue() != -1) {
                paramH = deliverable.getDeliverableInfo().getNewExpectedYear() + "";
              }
              // Complete
              if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
                if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                  && deliverable.getDeliverableInfo().getNewExpectedYear() != -1) {
                  paramH = deliverable.getDeliverableInfo().getNewExpectedYear() + "";
                }
              }
            }

          }
          // paramG - Year
          if (deliverable.getDeliverableInfo().getYear() != -1) {
            paramG = deliverable.getDeliverableInfo().getYear() + "";
          }

          // Deliverable Participant
          DeliverableParticipant deliverableParticipant = deliverable.getDeliverableParticipants().stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase())
              && ds.getHasParticipants() != null && ds.getHasParticipants())
            .collect(Collectors.toList()).get(0);
          // paramI - Event/Activity name
          if (deliverableParticipant.getEventActivityName() != null
            && !deliverableParticipant.getEventActivityName().isEmpty()) {
            paramI = deliverableParticipant.getEventActivityName();
          }

          if (deliverableParticipant.getRepIndTypeActivity() != null
            && deliverableParticipant.getRepIndTypeActivity().getName() != null
            && !deliverableParticipant.getRepIndTypeActivity().getName().isEmpty()) {

            // paramJ - Type of Activity
            paramJ = deliverableParticipant.getRepIndTypeActivity().getName();

            if (deliverableParticipant.getRepIndTypeActivity().getId()
              .equals(this.getReportingIndTypeActivityAcademicDegree())) {
              // paramK - Academic Degree
              if (deliverableParticipant.getAcademicDegree() != null
                && !deliverableParticipant.getAcademicDegree().isEmpty()) {
                paramK = deliverableParticipant.getAcademicDegree();
              }
            } else {
              paramK = "<Not Applicable>";
            }

            if (deliverableParticipant.getRepIndTypeActivity().getIsFormal()) {
              // paramQ - Training period of time
              if (deliverableParticipant.getRepIndTrainingTerm() != null) {
                paramQ = deliverableParticipant.getRepIndTrainingTerm().getName();
              }
            } else {
              paramQ = "<Not Applicable>";
            }
          }


          // paramL - Total number of participants
          if (deliverableParticipant.getParticipants() != null) {
            paramL = deliverableParticipant.getParticipants() + "";
          }
          // paramM - Total estimate
          if (deliverableParticipant.getEstimateParticipants() != null) {
            if (deliverableParticipant.getEstimateParticipants()) {
              paramM = "Yes";
            } else {
              paramM = "No";
            }
          }

          if (deliverableParticipant.getDontKnowFemale() != null && deliverableParticipant.getDontKnowFemale()) {
            // paramN - Number of females (Possible <Not applicable>)
            paramN = "<Not Applicable>";
            // paramO - Female Estimate (Possible <Not applicable>)
            paramO = "<Not Applicable>";
          } else {
            // paramN - Number of females (Possible <Not applicable>)
            if (deliverableParticipant.getFemales() != null) {
              paramN = deliverableParticipant.getFemales() + "";
            }
            // paramO - Female Estimate (Possible <Not applicable>)
            if (deliverableParticipant.getEstimateFemales() != null) {
              if (deliverableParticipant.getEstimateFemales()) {
                paramO = "Yes";
              } else {
                paramO = "No";
              }
            }
          }
          // paramP - Type of Participant(s)
          if (deliverableParticipant.getRepIndTypeParticipant() != null) {
            paramP = deliverableParticipant.getRepIndTypeParticipant().getName();
          }

          // Generate the deliverable url of MARLO
          // Publication
          if (deliverable.getIsPublication() != null && deliverable.getIsPublication()
            && deliverable.getProject() == null) {
            deliverableURL = this.getBaseUrl() + "/publications/" + this.getSelectedPhase().getCrp().getAcronym()
              + "/publication.do?deliverableID=" + deliverable.getId() + "&phaseID=" + this.getSelectedPhase().getId();
          } else {
            // Project deliverable
            deliverableURL = this.getBaseUrl() + "/projects/" + this.getSelectedPhase().getCrp().getAcronym()
              + "/deliverable.do?deliverableID=" + deliverable.getId() + "&phaseID=" + this.getSelectedPhase().getId();
          }


          model.addRow(new Object[] {paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ,
            paramK, paramL, paramM, paramN, paramO, paramP, paramQ, deliverableURL});
        }
      }
    }


    return model;
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("DeliverablesParticipantsSummary");
    if (showAllYears.equals("true")) {
      fileName.append("_AllYears");
    }
    fileName.append("-" + this.getLoggedCrp().getAcronym() + "-");
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


  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible"},
      new Class[] {String.class, String.class, String.class, Boolean.class});
    model.addRow(new Object[] {center, date, year, this.hasProgramnsRegions()});
    return model;
  }


  /**
   * @return the showAllYears
   */
  public String getShowAllYears() {
    return showAllYears;
  }

  @Override
  public void prepare() throws Exception {
    try {
      Map<String, Parameter> parameters = this.getParameters();
      showAllYears = StringUtils.trim(parameters.get(APConstants.SUMMARY_DELIVERABLE_ALL_YEARS).getMultipleValues()[0]);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.SUMMARY_DELIVERABLE_ALL_YEARS
        + " parameter. Parameter will be set as false. Exception: " + e.getMessage());
      showAllYears = "false";
    }
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

  /**
   * @param bytesXLSX the bytesXLSX to set
   */
  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }


  /**
   * @param inputStream the inputStream to set
   */
  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }


  /**
   * @param showAllYears the showAllYears to set
   */
  public void setShowAllYears(String showAllYears) {
    this.showAllYears = showAllYears;
  }
}
