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
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

/**
 * ExpectedDeliverablesSummaryAction:
 * 
 * @author avalencia - CCAFS
 * @date Nov 2, 2017
 * @time 9:13:34 AM: Added a new column to masterList called Project Managing Partners
 */
public class ExpectedDeliverablesSummaryAction extends BaseAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(ExpectedDeliverablesSummaryAction.class);
  // Parameters
  private Crp loggedCrp;
  private long startTime;
  private int year;

  // Managers
  private final CrpManager crpManager;
  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public ExpectedDeliverablesSummaryAction(APConfig config, CrpManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }


  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    /*
     * Deliverables
     */
    masterReport.getParameterValues().put("i8nDeliverableID", this.getText("searchTerms.deliverableId"));
    masterReport.getParameterValues().put("i8nDeliverableTitle",
      this.getText("project.deliverable.generalInformation.title"));
    masterReport.getParameterValues().put("i8nKeyOutput",
      this.getText("project.deliverable.generalInformation.keyOutput"));
    masterReport.getParameterValues().put("i8nExpectedYear",
      this.getText("project.deliverable.generalInformation.year"));
    masterReport.getParameterValues().put("i8nType", this.getText("deliverable.type"));
    masterReport.getParameterValues().put("i8nSubType", this.getText("deliverable.subtype"));
    masterReport.getParameterValues().put("i8nCrossCutting", this.getText("project.crossCuttingDimensions.readText"));
    masterReport.getParameterValues().put("i8nGenderLevels", this.getText("deliverable.genderLevels.readText"));
    masterReport.getParameterValues().put("i8nStatus", this.getText("project.deliverable.generalInformation.status"));
    masterReport.getParameterValues().put("i8nProjectID", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nCoas", this.getText("deliverable.coas"));
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nRegions", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nIndividual", this.getText("deliverable.individual"));
    masterReport.getParameterValues().put("i8nManaging", this.getText("deliverable.managing"));
    masterReport.getParameterValues().put("i8nShared", this.getText("deliverable.shared"));
    masterReport.getParameterValues().put("i8nFundingSourcesID", this.getText("deliverable.fundingSourcesID"));
    masterReport.getParameterValues().put("i8nFundingWindows", this.getText("deliverable.fundingWindows"));
    masterReport.getParameterValues().put("i8nNewExpectedYear", this.getText("deliverable.newExpectedYear"));
    masterReport.getParameterValues().put("i8nOutcomes", this.getText("impactPathway.menu.hrefOutcomes"));
    masterReport.getParameterValues().put("i8nProjectManaging", this.getText("deliverable.project.managing"));

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
        manager.createDirectly(this.getClass().getResource("/pentaho/deliverables_20171102.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();

      Number idParam = loggedCrp.getId();


      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String currentDate = timezone.format(format) + "(GMT" + zone + ")";

      masterReport.getParameterValues().put("crp_id", idParam);
      masterReport.getParameterValues().put("year", year);
      masterReport.getParameterValues().put("date", currentDate);
      masterReport.getParameterValues().put("regionalAvalaible", this.hasProgramnsRegions());
      masterReport.getParameterValues().put("showDescription",
        this.hasSpecificities(APConstants.CRP_REPORTS_DESCRIPTION));
      masterReport.getParameterValues().put("hasOutcomeIndicator",
        this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR));
      // Set i8n for pentaho
      masterReport = this.addi8nParameters(masterReport);
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ExpectedDeliverables " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;

  }

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
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
    fileName.append("ExpectedDeliverablesSummary-");
    fileName.append(this.year + "_");
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

  public Crp getLoggedCrp() {
    return loggedCrp;
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
      // Map<String, Object> parameters = this.getParameters();
      Map<String, Parameter> parameters = this.getParameters();
      // year = Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0])));
      year = Integer.parseInt((StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0])));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.YEAR_REQUEST
        + " parameter. Parameter will be set as CurrentCycleYear. Exception: " + e.getMessage());
      year = this.getCurrentCycleYear();
    }
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.loggedCrp.getAcronym());
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setYear(int year) {
    this.year = year;
  }


}
