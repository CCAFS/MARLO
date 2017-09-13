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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.google.inject.Inject;
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

public class ExpectedDeliverablesSummaryAction extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(ExpectedDeliverablesSummaryAction.class);
  // Parameters
  private long startTime;

  // Managers

  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public ExpectedDeliverablesSummaryAction(APConfig config, CrpManager crpManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
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
        manager.createDirectly(this.getClass().getResource("/pentaho/deliverables.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();

      Number idParam = this.getLoggedCrp().getId();


      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String currentDate = timezone.format(format) + "(GMT" + zone + ")";

      masterReport.getParameterValues().put("crp_id", idParam);
      masterReport.getParameterValues().put("year", this.getSelectedYear());
      masterReport.getParameterValues().put("date", currentDate);
      masterReport.getParameterValues().put("regionalAvalaible", this.hasProgramnsRegions());
      masterReport.getParameterValues().put("showDescription",
        this.hasSpecificities(APConstants.CRP_REPORTS_DESCRIPTION));
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
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
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


  @Override
  public void prepare() {
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

}
