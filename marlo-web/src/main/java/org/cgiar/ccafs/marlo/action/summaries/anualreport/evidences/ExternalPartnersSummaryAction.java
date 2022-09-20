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

package org.cgiar.ccafs.marlo.action.summaries.anualreport.evidences;

import org.cgiar.ccafs.marlo.action.summaries.BaseSummariesAction;
import org.cgiar.ccafs.marlo.action.summaries.Summary;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

/**
 * This action prepare and create the Partners evidences information to fill the part C of the annual report document.
 *
 * @author Hermes Jimenez - CIAT/CCAFS
 */
public class ExternalPartnersSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 7855111013101438102L;
  private static Logger LOG = LoggerFactory.getLogger(ExternalPartnersSummaryAction.class);
  // Managers
  private final ResourceManager resourceManager;


  private final ReportSynthesisKeyPartnershipExternalManager reportSynthesisKeyPartnershipExternalManager;


  // Parameters
  private long startTime;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public ExternalPartnersSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ResourceManager resourceManager, ProjectManager projectManager,
    ReportSynthesisKeyPartnershipExternalManager reportSynthesisKeyPartnershipExternalManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.reportSynthesisKeyPartnershipExternalManager = reportSynthesisKeyPartnershipExternalManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * the order of the parameters is the same order for the getPartnersReportingTableModel() method
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {

    masterReport.getParameterValues().put("i8nColumnA", this.getText("externalPartners.id"));
    masterReport.getParameterValues().put("i8nColumnB", this.getText("externalPartners.name"));
    masterReport.getParameterValues().put("i8nColumnC", this.getText("externalPartners.type"));
    masterReport.getParameterValues().put("i8nColumnD", this.getText("externalPartners.headquarter"));
    masterReport.getParameterValues().put("i8nHeader", this.getText("externalPartners.header"));

    return masterReport;
  }


  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager.createDirectly(
        this.getClass().getResource("/pentaho/crp/AR-Evidences/ExternalPartnersAR2018.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();
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
      LOG.error("Error generating PoliciesEvidenceReporting " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }


  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "details":
        model = this.getPartnersReportingTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

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
    fileName.append("ExternalPartners-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedPhase().getName());
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
    return fileName.toString();
  }

  public String getHighlightsImagesUrl(String projectId) {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath(projectId).replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath(String projectId) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectId + File.separator
      + "hightlightsImage" + File.separator;
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
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "baseUrl"},
      new Class[] {String.class, String.class, String.class, String.class});
    model.addRow(new Object[] {center, date, year, this.getBaseUrl()});
    return model;
  }

  /**
   * Get the information of all external partnerships for the current annual report period, then filter the institutions
   * for each one and include in a List of Institutions
   * 
   * @return a list of all Institutions (Partners) when belongs of an external partnerships of the current annual report
   *         period
   */
  public List<Institution> getPartnersInfo() {

    List<ReportSynthesisKeyPartnershipExternal> externalPartnerships;
    List<Institution> evidencePartners;
    LiaisonInstitution liaisonInstitutionPMU = new LiaisonInstitution();
    if (this.getLoggedCrp().getLiaisonInstitutions() != null
      && !this.getLoggedCrp().getLiaisonInstitutions().isEmpty()) {
      try {
        liaisonInstitutionPMU = this.getLoggedCrp().getLiaisonInstitutions().stream()
          .filter(o -> o != null && o.isActive() && o.getAcronym() != null && o.getAcronym().equals("PMU"))
          .collect(Collectors.toList()).get(0);
      } catch (Exception e) {
        LOG.error("Error generating liaisonInstitutionPMU " + e.getMessage());
      }
    }

    List<LiaisonInstitution> liaisonInstitutions = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // Load Institutions Partnerships evidences
    externalPartnerships = reportSynthesisKeyPartnershipExternalManager.getTable8(liaisonInstitutions,
      liaisonInstitutionPMU, this.getSelectedPhase());
    evidencePartners = new ArrayList<>();
    if (externalPartnerships != null && !externalPartnerships.isEmpty()) {

      for (ReportSynthesisKeyPartnershipExternal externalPartner : externalPartnerships) {
        if (externalPartner.getReportSynthesisKeyPartnershipExternalInstitutions() != null
          && !externalPartner.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {
          for (ReportSynthesisKeyPartnershipExternalInstitution extInstitutions : externalPartner
            .getReportSynthesisKeyPartnershipExternalInstitutions()) {

            if (extInstitutions.getInstitution() != null) {
              extInstitutions.getInstitution().setLocations(new ArrayList<>());
              if (extInstitutions.getInstitution().getInstitutionsLocations() != null
                && !extInstitutions.getInstitution().getInstitutionsLocations().isEmpty()) {
                extInstitutions.getInstitution().getLocations().addAll(extInstitutions.getInstitution()
                  .getInstitutionsLocations().stream().filter(o -> o.isActive()).collect(Collectors.toList()));
              }
            }

            if (evidencePartners.isEmpty()) {
              evidencePartners.add(extInstitutions.getInstitution());
            } else {
              if (!evidencePartners.contains(extInstitutions.getInstitution())) {
                evidencePartners.add(extInstitutions.getInstitution());
              }
            }
          }
        }
      }
    }

    return evidencePartners;

  }


  private TypedTableModel getPartnersReportingTableModel() {

    /*
     * Parameters variables to send to the file
     * paramA - InstitutionID
     * paramB - Institution Name
     * paramC - Institution Type
     * paramD - Institution headquarter Country
     * NOTE : does not mater the order into the implementation (ex: the paramB will be setup first that the paramA)
     */
    TypedTableModel model = new TypedTableModel(new String[] {"paramA", "paramB", "paramC", "paramD"},
      new Class[] {Long.class, String.class, String.class, String.class}, 0);

    // Load the policies information
    List<Institution> institutions = this.getPartnersInfo();

    for (Institution institution : institutions) {
      Long paramA = null;
      String paramB = "", paramC = "", paramD = "";


      // Policy Id
      paramA = institution.getId();

      // Institution Name
      paramB = institution.getComposedName();

      // Institution Type
      if (institution.getInstitutionType() != null) {
        paramC = institution.getInstitutionType().getName();
      } else {
        paramC = "<Not Defined>";
      }

      // Institution headquarter Country
      if (institution.getLocations() != null && !institution.getLocations().isEmpty()) {
        paramD = institution.getLocations().stream().filter(i -> i.isActive() && i.isHeadquater())
          .collect(Collectors.toList()).get(0).getLocElement().getName();
      } else {
        paramD = "<Not Defined>";
      }


      model.addRow(new Object[] {paramA, paramB, paramC, paramD});
    }
    return model;
  }

  @Override
  public void prepare() throws Exception {
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

}
