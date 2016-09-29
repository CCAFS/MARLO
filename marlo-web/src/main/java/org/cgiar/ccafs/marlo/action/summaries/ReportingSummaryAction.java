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
import org.cgiar.ccafs.marlo.action.json.global.ManageUsersAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PropertiesManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */
public class ReportingSummaryAction extends BaseAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = -624982650510682813L;

  private static Logger LOG = LoggerFactory.getLogger(ManageUsersAction.class);


  private CrpManager crpManager;
  // Front-end
  private long projectID;

  // XLS bytes
  private byte[] bytesPDF;
  // Streams
  InputStream inputStream;

  @Inject
  public ReportingSummaryAction(APConfig config, CrpManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    // (ResourceManager) ServletActionContext.getServletContext().getAttribute(PentahoListener.KEY_NAME);
    manager.registerDefaults();
    try {
      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/project-description.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();

      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());


      // SQLReportDataFactory sdf = (SQLReportDataFactory) masterReport.getDataFactory();
      PropertiesManager managerProperties = new PropertiesManager();
      final DriverConnectionProvider drc = new DriverConnectionProvider();

      String urlMysql = "jdbc:mysql://" + managerProperties.getPropertiesAsString(APConfig.MYSQL_HOST) + ":"
        + managerProperties.getPropertiesAsString(APConfig.MYSQL_PORT) + "/"
        + managerProperties.getPropertiesAsString(APConfig.MYSQL_DATABASE);
      drc.setDriver("com.mysql.jdbc.Driver");
      drc.setUrl(urlMysql);
      drc.setProperty("user", managerProperties.getPropertiesAsString(APConfig.MYSQL_USER));
      drc.setProperty("password", managerProperties.getPropertiesAsString(APConfig.MYSQL_PASSWORD));
      System.out.println("Url  Conexion" + urlMysql);
      Log.info("Url  Conexion" + urlMysql);
      List<CompoundDataFactory> factorys = this.getCompoundDataFactoriesFromMasterAndSubreports(cdf, drc, masterReport);


      for (CompoundDataFactory compoundDataFactory : factorys) {

        int index = 0;
        for (String queryName : compoundDataFactory.getQueryNames()) {
          System.out.println(queryName);
          SQLReportDataFactory sdf = (SQLReportDataFactory) compoundDataFactory.getDataFactoryForQuery(queryName);
          sdf.setConnectionProvider(drc);
          compoundDataFactory.set(index, sdf);
          index++;
        }
      }
      int index = 0;
      for (String queryName : cdf.getQueryNames()) {
        System.out.println(queryName);
        SQLReportDataFactory sdf = (SQLReportDataFactory) cdf.getDataFactoryForQuery(queryName);
        sdf.setConnectionProvider(drc);
        cdf.set(index, cdf);
        index++;
      }
      masterReport.setDataFactory(cdf);
      Number idParam = projectID;
      Number yearParam = Long.valueOf(String.valueOf(this.getCurrentCycleYear()));
      String cycleParam = APConstants.PLANNING;

      masterReport.getParameterValues().put("p_id", idParam);
      masterReport.getParameterValues().put("p_year", yearParam);
      masterReport.getParameterValues().put("p_cycle", cycleParam);


      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {

      LOG.error("Generating PDF" + e.getMessage());
      throw e;
    }
    return SUCCESS;

  }

  private List<CompoundDataFactory> getCompoundDataFactoriesFromMasterAndSubreports(CompoundDataFactory cdf,
    DriverConnectionProvider drc, MasterReport masterReport) {

    List<CompoundDataFactory> CompoundDataFactories = new ArrayList<CompoundDataFactory>();
    CompoundDataFactories.add(cdf); // Master report
    List<SubReport> subReportsInternal = new ArrayList<>();
    List<SubReport> subReports = this.getSubReports(masterReport);
    for (SubReport subReport : subReports) {


      subReportsInternal.addAll(this.getSubReports(subReport));
    }
    subReports.addAll(subReportsInternal);
    for (SubReport subReport : subReports) {

      if (subReport.getDataFactory() instanceof CompoundDataFactory) {
        CompoundDataFactories.add((CompoundDataFactory) subReport.getDataFactory());
      }
    }

    return CompoundDataFactories;
  }

  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
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
    fileName.append("ProjectReport-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");

    return fileName.toString();

  }


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }

  public long getProjectID() {
    return projectID;
  }

  private List<SubReport> getSubReports(MasterReport masterReport) {
    List<SubReport> subReports = new ArrayList<SubReport>();
    this.recurseToFindAllSubReports(masterReport, subReports);
    return subReports;
  }

  private List<SubReport> getSubReports(Section masterReport) {
    List<SubReport> subReports = new ArrayList<SubReport>();
    this.recurseToFindAllSubReports(masterReport, subReports);
    return subReports;
  }

  @Override
  public void prepare() {
    try {
      this
        .setProjectID(Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID))));
    } catch (Exception e) {

    }


  }

  private void recurseToFindAllSubReports(Section section, List<SubReport> subReports) {
    int elementCount = section.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = section.getElement(i);
      if (e instanceof RootLevelBand) {
        SubReport[] subs = ((RootLevelBand) e).getSubReports();
        for (SubReport s : subs) {
          subReports.add(s);
        }
      }
      if (e instanceof Section) {
        this.recurseToFindAllSubReports((Section) e, subReports);
      }
    }
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

}