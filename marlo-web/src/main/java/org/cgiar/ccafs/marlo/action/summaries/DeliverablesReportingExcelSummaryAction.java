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

import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.model.ChannelEnum;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
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
import java.util.HashSet;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class DeliverablesReportingExcelSummaryAction extends BaseSummariesAction implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(DeliverablesReportingExcelSummaryAction.class);
  private CrpProgramManager programManager;
  private DeliverableManager deliverableManager;
  private GenderTypeManager genderTypeManager;

  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;
  // Parameters
  private long startTime;

  @Inject
  public DeliverablesReportingExcelSummaryAction(APConfig config, CrpManager crpManager,
    ProjectHighligthManager projectHighLightManager, CrpProgramManager programManager,
    GenderTypeManager genderTypeManager, DeliverableManager deliverableManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
    this.genderTypeManager = genderTypeManager;
    this.programManager = programManager;
    this.deliverableManager = deliverableManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    /*
     * Reporting
     * Deliverables
     */
    masterReport.getParameterValues().put("i8nProjectID", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nProjectFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nProjectRegions", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nDeliverableId", this.getText("searchTerms.deliverableId"));
    masterReport.getParameterValues().put("i8nDeliverableTitle",
      this.getText("project.deliverable.generalInformation.title"));
    masterReport.getParameterValues().put("i8nMainInfo",
      this.getText("project.deliverable.generalInformation.titleTab"));
    masterReport.getParameterValues().put("i8nType", this.getText("project.deliverable.generalInformation.type"));
    masterReport.getParameterValues().put("i8nSubType", this.getText("project.deliverable.generalInformation.subType"));
    masterReport.getParameterValues().put("i8nStatus", this.getText("project.deliverable.generalInformation.status"));
    masterReport.getParameterValues().put("i8nYearExpectedCompletion",
      this.getText("project.deliverable.generalInformation.year"));
    masterReport.getParameterValues().put("i8nNewExpectedYear", this.getText("deliverable.newExpectedYear"));
    masterReport.getParameterValues().put("i8nResponsible", this.getText("leadPartner.responsible"));
    masterReport.getParameterValues().put("i8nCrossCuttingDimensions",
      this.getText("deliverable.crossCuttingDimensions.readText"));
    masterReport.getParameterValues().put("i8nAlreadyDisseminatedQuestion",
      this.getText("project.deliverable.dissemination.alreadyDisseminatedQuestion"));
    masterReport.getParameterValues().put("i8nDisseminationChanel",
      this.getText("project.deliverable.dissemination.v.DisseminationChanel"));
    masterReport.getParameterValues().put("i8nDiseminationTitle", this.getText("deliverable.diseminationTitle"));
    masterReport.getParameterValues().put("i8nDisseminationUrl",
      this.getText("project.deliverable.dissemination.disseminationUrl"));
    masterReport.getParameterValues().put("i8nIsOpenAccess",
      this.getText("project.deliverable.dissemination.v.isOpenAccess"));
    masterReport.getParameterValues().put("i8nOpenAccessRestriction",
      this.getText("project.deliverable.dissemination.v.openAccessRestriction"));
    masterReport.getParameterValues().put("i8nALicense", this.getText("project.deliverable.v.ALicense"));
    masterReport.getParameterValues().put("i8nMetadataSubtitle",
      this.getText("project.deliverable.dissemination.metadataSubtitle"));
    masterReport.getParameterValues().put("i8nPublicationAllowModifications",
      this.getText("publication.publicationAllowModifications"));
    masterReport.getParameterValues().put("i8nMetadataTitle", this.getText("metadata.title"));
    masterReport.getParameterValues().put("i8nMetadataDescription", this.getText("metadata.description.readText"));
    masterReport.getParameterValues().put("i8nMetadataDate", this.getText("metadata.date"));
    masterReport.getParameterValues().put("i8nLanguage", this.getText("metadata.language"));
    masterReport.getParameterValues().put("i8nCountry", this.getText("metadata.country"));
    masterReport.getParameterValues().put("i8nKeywords", this.getText("metadata.keywords.help"));
    masterReport.getParameterValues().put("i8nCitation", this.getText("metadata.citation.readText"));
    masterReport.getParameterValues().put("i8nHandle", this.getText("metadata.handle"));
    masterReport.getParameterValues().put("i8nDoi", this.getText("metadata.doi"));
    masterReport.getParameterValues().put("i8nPublicationTitle",
      this.getText("project.deliverable.dissemination.publicationTitle"));
    masterReport.getParameterValues().put("i8nCreator", this.getText("metadata.creator"));
    masterReport.getParameterValues().put("i8nVolume", this.getText("project.deliverable.dissemination.volume"));
    masterReport.getParameterValues().put("i8nIssue", this.getText("project.deliverable.dissemination.issue"));
    masterReport.getParameterValues().put("i8nPages", this.getText("project.deliverable.dissemination.pages"));
    masterReport.getParameterValues().put("i8nJournalName",
      this.getText("project.deliverable.dissemination.journalName"));
    masterReport.getParameterValues().put("i8nIndicatorsJournal",
      this.getText("project.deliverable.dissemination.indicatorsJournal"));
    masterReport.getParameterValues().put("i8nPublicationAcknowledge",
      this.getText("deliverable.publicationAcknowledge"));
    masterReport.getParameterValues().put("i8nPublicationFLContribution",
      this.getText("deliverable.publicationFLContribution"));
    masterReport.getParameterValues().put("i8nQualityCheckTitle", this.getText("deliverable.qualityCheckTitle"));
    masterReport.getParameterValues().put("i8nFindable", this.getText("project.deliverable.quality.FLabel"));
    masterReport.getParameterValues().put("i8nAccessible", this.getText("project.deliverable.quality.ALabel"));
    masterReport.getParameterValues().put("i8nInteroperable", this.getText("project.deliverable.quality.ILabel"));
    masterReport.getParameterValues().put("i8nReusable", this.getText("project.deliverable.quality.RLabel"));
    masterReport.getParameterValues().put("i8nQualityCheckAssurance",
      this.getText("deliverable.qualityCheckAssurance"));
    masterReport.getParameterValues().put("i8nQualityCheckDataDictionary",
      this.getText("deliverable.qualityCheckDataDictionary"));
    masterReport.getParameterValues().put("i8nQualityCheckQuestion3",
      this.getText("project.deliverable.quality.question3"));
    masterReport.getParameterValues().put("i8nDataSharingTitle", this.getText("projectDeliverable.dataSharingTitle"));
    masterReport.getParameterValues().put("i8nDeliverableFiles",
      this.getText("projectDeliverable.dataSharing.deliverableFiles"));

    /*
     * Reporting
     * Publications
     */
    masterReport.getParameterValues().put("i8nPublicationMainTitle", this.getText("publication.publicationMainTitle"));
    masterReport.getParameterValues().put("i8nPublicationID", this.getText("publication.publicationId"));
    masterReport.getParameterValues().put("i8nPublicationTitle", this.getText("publication.title"));
    masterReport.getParameterValues().put("i8nPublicationAddedBy", this.getText("publicationsList.column.addedBy"));
    masterReport.getParameterValues().put("i8nPublicationSubType", this.getText("publication.subType"));
    masterReport.getParameterValues().put("i8nPublicationYear", this.getText("publication.year"));
    masterReport.getParameterValues().put("i8nPublicationLeadPartners", this.getText("publication.leadPartners"));
    masterReport.getParameterValues().put("i8nPublicationDissemination",
      this.getText("publication.publicationDissemination"));
    masterReport.getParameterValues().put("i8nPublicationCrossCuttingDimensions",
      this.getText("project.crossCuttingDimensions.readText"));
    masterReport.getParameterValues().put("i8nPublicationDisseminationChannel",
      this.getText("publication.dissemination.disseminationChannel"));
    masterReport.getParameterValues().put("i8nPublicationDisseminationUrl",
      this.getText("project.deliverable.dissemination.disseminationUrl"));
    masterReport.getParameterValues().put("i8nPublicationAllowPublication",
      this.getText("project.deliverable.publication.v.allowPublication"));
    masterReport.getParameterValues().put("i8nPublicationQualityCheck",
      this.getText("project.deliverable.v.qualityCheck"));

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
        manager.createDirectly(this.getClass().getResource("/pentaho/deliverablesReporting.prpt"), MasterReport.class);

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

      this.fillSubreport((SubReport) hm.get("deliverables_reporting_data"), "deliverables_reporting_data");
      this.fillSubreport((SubReport) hm.get("deliverables_reporting_publications"),
        "deliverables_reporting_publications");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating DeliverablesReportingExcel " + e.getMessage());
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
      case "deliverables_reporting_data":
        model = this.getDeliverablesDataReportingTableModel();
        break;
      case "deliverables_reporting_publications":
        model = this.getDeliverablesPublicationsReportingTableModel();
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

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }


  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  private String getDeliverableDataSharingFilePath(String projectID) {
    String upload = config.getDownloadURL();
    return upload + "/" + this.getDeliverableDataSharingFileRelativePath(projectID).replace('\\', '/');
  }

  private String getDeliverableDataSharingFileRelativePath(String projectID) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectID + File.separator
      + "deliverableDataSharing" + File.separator;
  }

  private TypedTableModel getDeliverablesDataReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverable_id", "title", "deliv_type", "deliv_sub_type", "deliv_status", "deliv_year",
        "key_output", "leader", "funding_sources", "cross_cutting", "deliv_new_year", "deliv_new_year_justification",
        "deliv_dissemination_channel", "deliv_dissemination_url", "deliv_open_access", "deliv_license", "titleMetadata",
        "descriptionMetadata", "dateMetadata", "languageMetadata", "countryMetadata", "keywordsMetadata",
        "citationMetadata", "HandleMetadata", "DOIMetadata", "creator_authors", "data_sharing", "qualityAssurance",
        "dataDictionary", "tools", "F", "A", "I", "R", "disseminated", "restricted_access",
        "deliv_license_modifications", "volume", "issue", "pages", "journal", "journal_indicators", "acknowledge",
        "fl_contrib", "project_ID", "project_title", "flagships", "regions", "others_responsibles", "newExceptedFlag"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Integer.class, String.class,
        String.class, String.class, String.class, Integer.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);
    if (!deliverableManager.findAll().isEmpty()) {

      // get Reporting deliverables
      List<Deliverable> deliverables = new ArrayList<>(deliverableManager.findAll().stream().filter(d -> d.isActive()
        && d.getProject() != null && d.getProject().isActive()
        && d.getProject().getProjecInfoPhase(this.getActualPhase()).getReporting() != null
        && d.getProject().getProjecInfoPhase(this.getActualPhase()).getReporting() && d.getProject().getCrp() != null
        && d.getProject().getCrp().getId().equals(this.getLoggedCrp().getId())
        && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
        && ((d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId())
          && (d.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getSelectedYear()
            || (d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null && d
              .getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() >= this.getSelectedYear())))
          || (d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())
            && (d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
              && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this.getSelectedYear()))
          || (d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
            && (d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getSelectedYear()
              || (d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null && d
                .getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this.getSelectedYear()))))
        && (d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())
          || d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
          || d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())))
        .collect(Collectors.toList()));

      deliverables
        .sort((p1, p2) -> p1.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getSelectedYear())
          .compareTo(p2.getDeliverableInfo(this.getActualPhase()).isRequieriedReporting(this.getSelectedYear())));

      HashSet<Deliverable> deliverablesHL = new HashSet<>();
      deliverablesHL.addAll(deliverables);
      deliverables.clear();
      deliverables.addAll(deliverablesHL);
      int i = 0;
      for (Deliverable deliverable : deliverables) {
        i++;
        // System.out.println(deliverable.getId());
        // System.out.println("#" + i);
        String delivType = null;
        String delivSubType = null;
        String delivStatus = deliverable.getDeliverableInfo(this.getActualPhase()).getStatusName();
        Integer delivYear = null;
        String keyOutput = "";
        String leader = null;
        String othersResponsibles = null;
        String fundingSources = "";
        Boolean showFAIR = false;
        Boolean showPublication = false;
        Boolean showCompilance = false;
        String projectID = null;
        String projectTitle = null;
        String flagships = null, regions = null;

        if (deliverable.getProject() != null) {
          projectID = deliverable.getProject().getId().toString();
          if (deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getTitle() != null
            && !deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getTitle().trim().isEmpty()) {
            projectTitle = deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getTitle();
          }
        }


        if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType() != null) {
          delivSubType = deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getName();
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 56
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 57
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 76
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 54
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 81
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 82
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 83
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 55
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 62
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 53
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 60
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 59
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 58
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 77
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 75
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 78
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 72
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 73) {
            showFAIR = true;
          }
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 74) {
            showCompilance = true;
          }

          if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getDeliverableType() != null) {
            delivType =
              deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getDeliverableType().getName();
            // FAIR and deliverable publication
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getDeliverableType()
              .getId() == 49) {
              showFAIR = true;
              showPublication = true;
            }
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() != 0) {
          delivYear = deliverable.getDeliverableInfo(this.getActualPhase()).getYear();
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput() != null) {
          keyOutput += "● ";

          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput().getCrpClusterOfActivity()
            .getCrpProgram() != null) {
            keyOutput += deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput()
              .getCrpClusterOfActivity().getCrpProgram().getAcronym() + " - ";
          }
          keyOutput += deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput().getKeyOutput();
        }


        // Get partner responsible and institution
        // Set responible;
        DeliverablePartnership responisble = this.responsiblePartner(deliverable);

        if (responisble != null) {
          if (responisble.getProjectPartnerPerson() != null) {
            ProjectPartnerPerson responsibleppp = responisble.getProjectPartnerPerson();

            leader = responsibleppp.getUser().getComposedName();
            if (responsibleppp.getProjectPartner() != null) {
              if (responsibleppp.getProjectPartner().getInstitution() != null) {
                leader += " - " + responsibleppp.getProjectPartner().getInstitution().getAcronym();
                if (responisble.getPartnerDivision() != null) {
                  leader += " (" + responisble.getPartnerDivision().getComposedName() + ")";
                }
              }
            }
          }
        }

        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive()).collect(Collectors.toList())) {
          fundingSources += "● " + dfs.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()).getTitle() + "\n";
        }
        if (fundingSources.isEmpty()) {
          fundingSources = null;
        }

        // Get cross_cutting dimension
        String crossCutting = "";
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingNa() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingNa() == true) {
            crossCutting += "● N/A \n";
          }
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() == true) {
            crossCutting += "● Gender \n";
          }
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingYouth() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingYouth() == true) {
            crossCutting += "● Youth \n";
          }
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingCapacity() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingCapacity() == true) {
            crossCutting += "● Capacity Development \n";
          }
        }

        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() == true) {
            if (deliverable.getDeliverableGenderLevels() == null
              || deliverable.getDeliverableGenderLevels().isEmpty()) {
              crossCutting += "\nGender level(s):\n<Not Defined>";
            } else {
              crossCutting += "\nGender level(s): \n";
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
                if (dgl.getGenderLevel() != 0.0) {
                  crossCutting +=
                    "● " + genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription() + "\n";
                }
              }
            }
          }
        }
        if (crossCutting.isEmpty()) {
          crossCutting = null;
        }

        if (keyOutput.isEmpty()) {
          keyOutput = null;
        }

        // Reporting
        Integer delivNewYear = null;
        String newExceptedFlag = "na";
        String delivNewYearJustification = null;

        if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null) {
          // Extended
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            delivNewYear = deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear();
            delivNewYearJustification = deliverable.getDeliverableInfo(this.getActualPhase()).getStatusDescription();
            newExceptedFlag = "nd";
          }
          // Complete
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            delivNewYear = deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear();
            delivNewYearJustification = "<Not applicable>";
            newExceptedFlag = "nd";
          }
          // Canceled
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
            delivNewYearJustification = deliverable.getDeliverableInfo(this.getActualPhase()).getStatusDescription();
          }
        }


        String delivDisseminationChannel = null;
        String delivDisseminationUrl = null;
        String delivOpenAccess = null;
        String delivLicense = null;
        String delivLicenseModifications = null;
        Boolean isDisseminated = false;
        String disseminated = "No";
        String restrictedAccess = null;
        Boolean showDelivLicenseModifications = false;

        if (deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).size() > 0
          && deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).get(0) != null) {
          // Get deliverable dissemination
          DeliverableDissemination deliverableDissemination =
            deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).get(0);

          if (deliverableDissemination.getAlreadyDisseminated() != null
            && deliverableDissemination.getAlreadyDisseminated() == true) {
            isDisseminated = true;
            disseminated = "Yes";
          }
          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationChannel() != null
              && !deliverableDissemination.getDisseminationChannel().isEmpty()) {
              if (ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()) != null) {
                delivDisseminationChannel =
                  ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()).getDesc();
              }
              // deliv_dissemination_channel = deliverableDissemination.getDisseminationChannel();
            }
          } else {
            delivDisseminationChannel = "<Not applicable>";
          }

          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationUrl() != null
              && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
              delivDisseminationUrl = deliverableDissemination.getDisseminationUrl().replace(" ", "%20");
            }
          } else {
            delivDisseminationUrl = "<Not applicable>";
          }

          if (deliverableDissemination.getIsOpenAccess() != null) {
            if (deliverableDissemination.getIsOpenAccess() == true) {
              delivOpenAccess = "Yes";
              restrictedAccess = "<Not applicable>";
            } else {
              // get the open access
              delivOpenAccess = "No";
              if (deliverableDissemination.getIntellectualProperty() != null
                && deliverableDissemination.getIntellectualProperty() == true) {
                restrictedAccess = "Intellectual Property Rights (confidential information)";
              }

              if (deliverableDissemination.getLimitedExclusivity() != null
                && deliverableDissemination.getLimitedExclusivity() == true) {
                restrictedAccess = "Limited Exclusivity Agreements";
              }

              if (deliverableDissemination.getNotDisseminated() != null
                && deliverableDissemination.getNotDisseminated() == true) {
                restrictedAccess = "Not Disseminated";
              }

              if (deliverableDissemination.getRestrictedUseAgreement() != null
                && deliverableDissemination.getRestrictedUseAgreement() == true) {
                restrictedAccess = "Restricted Use Agreement - Restricted access (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedAccessUntil() != null) {
                  restrictedAccess +=
                    "\nRestricted access until: " + deliverableDissemination.getRestrictedAccessUntil();
                } else {
                  restrictedAccess += "\nRestricted access until: <Not Defined>";
                }
              }

              if (deliverableDissemination.getEffectiveDateRestriction() != null
                && deliverableDissemination.getEffectiveDateRestriction() == true) {
                restrictedAccess = "Effective Date Restriction - embargoed periods (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedEmbargoed() != null) {
                  restrictedAccess +=
                    "\nRestricted embargoed date: " + deliverableDissemination.getRestrictedEmbargoed();
                } else {
                  restrictedAccess += "\nRestricted embargoed date: <Not Defined>";
                }
              }
            }
          }

          if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() != null) {
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() == true) {
              delivLicense = deliverable.getDeliverableInfo(this.getActualPhase()).getLicense();
              if (delivLicense.equals("OTHER")) {
                delivLicense = deliverable.getDeliverableInfo(this.getActualPhase()).getOtherLicense();
                showDelivLicenseModifications = true;
                if (deliverable.getDeliverableInfo(this.getActualPhase()).getAllowModifications() != null
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getAllowModifications() == true) {
                  delivLicenseModifications = "Yes";
                } else {
                  delivLicenseModifications = "No";
                }
              } else {
                if (!showDelivLicenseModifications) {
                  delivLicenseModifications = "<Not applicable>";
                }
              }
            } else {
              delivLicense = "No";
            }
          }
        }

        if (delivLicense != null && delivLicense.isEmpty()) {
          delivLicense = null;
        }

        String titleMetadata = null;
        String descriptionMetadata = null;
        String dateMetadata = null;
        String languageMetadata = null;
        String countryMetadata = null;
        String keywordsMetadata = null;
        String citationMetadata = null;
        String HandleMetadata = null;
        String DOIMetadata = null;

        for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getDeliverableMetadataElements()
          .stream().filter(dm -> dm.isActive() && dm.getMetadataElement() != null).collect(Collectors.toList())) {

          if (deliverableMetadataElement.getMetadataElement().getId() == 1) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              titleMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 8) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              descriptionMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 17) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              dateMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 24) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              languageMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 28) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              countryMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 37) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              keywordsMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 22) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              citationMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 35) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              HandleMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 36) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              DOIMetadata = deliverableMetadataElement.getElementValue();
            }
          }
        }

        String creatorAuthors = "";
        for (DeliverableUser deliverableUser : deliverable.getDeliverableUsers().stream().filter(du -> du.isActive())
          .collect(Collectors.toList())) {
          creatorAuthors += "\n● ";
          if (!deliverableUser.getLastName().isEmpty()) {
            creatorAuthors += deliverableUser.getLastName() + " - ";
          }
          if (!deliverableUser.getFirstName().isEmpty()) {
            creatorAuthors += deliverableUser.getFirstName();
          }
          if (!deliverableUser.getElementId().isEmpty()) {
            creatorAuthors += "<" + deliverableUser.getElementId() + ">";
          }
        }

        if (creatorAuthors.isEmpty()) {
          creatorAuthors = null;
        }

        String dataSharing = "";
        if (isDisseminated && (delivDisseminationChannel != null && !delivDisseminationChannel.equals("Other"))) {


          for (DeliverableDataSharingFile deliverableDataSharingFile : deliverable.getDeliverableDataSharingFiles()
            .stream().filter(ds -> ds.isActive()).collect(Collectors.toList())) {
            if (deliverableDataSharingFile.getExternalFile() != null
              && !deliverableDataSharingFile.getExternalFile().isEmpty()) {
              dataSharing += deliverableDataSharingFile.getExternalFile().replace(" ", "%20") + "\n";
            }

            if (deliverableDataSharingFile.getFile() != null && deliverableDataSharingFile.getFile().isActive()) {
              dataSharing +=
                (this.getDeliverableDataSharingFilePath(projectID) + deliverableDataSharingFile.getFile().getFileName())
                  .replace(" ", "%20") + "\n";

            }
          }
          if (dataSharing.isEmpty()) {
            dataSharing = null;
          }
        } else {
          dataSharing = "<Not applicable>";
        }


        String qualityAssurance = "";
        String dataDictionary = "";
        String tools = "";
        if (showCompilance) {


          if (deliverable.getDeliverableQualityChecks().stream().filter(qc -> qc.isActive())
            .collect(Collectors.toList()).size() > 0
            && deliverable.getDeliverableQualityChecks().stream().filter(qc -> qc.isActive())
              .collect(Collectors.toList()).get(0) != null) {
            DeliverableQualityCheck deliverableQualityCheck = deliverable.getDeliverableQualityChecks().stream()
              .filter(qc -> qc.isActive()).collect(Collectors.toList()).get(0);
            // QualityAssurance
            if (deliverableQualityCheck.getQualityAssurance() != null) {
              if (deliverableQualityCheck.getQualityAssurance().getId() == 2) {
                if (deliverableQualityCheck.getFileAssurance() != null
                  && deliverableQualityCheck.getFileAssurance().isActive()) {
                  qualityAssurance += "\n● File: " + (this.getDeliverableUrl("Assurance", deliverable)
                    + deliverableQualityCheck.getFileAssurance().getFileName()).replace(" ", "%20") + "";
                }
                if (deliverableQualityCheck.getLinkAssurance() != null
                  && !deliverableQualityCheck.getLinkAssurance().isEmpty()) {
                  qualityAssurance +=
                    "\n● Link: " + deliverableQualityCheck.getLinkAssurance().replace(" ", "%20") + "";
                }
              } else {
                qualityAssurance = "● " + deliverableQualityCheck.getQualityAssurance().getName();
              }
            }


            // Data dictionary
            if (deliverableQualityCheck.getDataDictionary() != null) {
              if (deliverableQualityCheck.getDataDictionary().getId() == 2) {
                if (deliverableQualityCheck.getFileDictionary() != null
                  && deliverableQualityCheck.getFileDictionary().isActive()) {
                  dataDictionary += "\n● File: " + (this.getDeliverableUrl("Dictionary", deliverable)
                    + deliverableQualityCheck.getFileDictionary().getFileName()).replace(" ", "%20") + "";
                }
                if (deliverableQualityCheck.getLinkDictionary() != null
                  && !deliverableQualityCheck.getLinkDictionary().isEmpty()) {
                  dataDictionary += "\n● Link: " + deliverableQualityCheck.getLinkDictionary().replace(" ", "%20") + "";
                }
              } else {
                dataDictionary = "● " + deliverableQualityCheck.getDataDictionary().getName();
              }
            }

            // Tools
            if (deliverableQualityCheck.getDataTools() != null) {
              if (deliverableQualityCheck.getDataTools().getId() == 2) {
                if (deliverableQualityCheck.getFileTools() != null
                  && deliverableQualityCheck.getFileTools().isActive()) {
                  tools += "\n● File: " + (this.getDeliverableUrl("Tools", deliverable)
                    + deliverableQualityCheck.getFileTools().getFileName()).replace(" ", "%20") + "";
                }
                if (deliverableQualityCheck.getLinkTools() != null
                  && !deliverableQualityCheck.getLinkTools().isEmpty()) {
                  tools += "\n● Link: " + deliverableQualityCheck.getLinkTools().replace(" ", "%20") + "";
                }
              } else {
                tools = "● " + deliverableQualityCheck.getDataTools().getName();
              }
            }
          }
        } else {
          tools = "<Not applicable>";
          dataDictionary = "<Not applicable>";
          qualityAssurance = "<Not applicable>";
        }

        if (qualityAssurance.isEmpty()) {
          qualityAssurance = null;
        }
        if (dataDictionary.isEmpty()) {
          dataDictionary = null;
        }
        if (tools.isEmpty()) {
          tools = null;
        }
        String F = "";
        String A = "";
        String I = "";
        String R = "";
        if (showFAIR) {
          // FAIR

          if (this.isF(deliverable.getId()) == null) {
            F = "X";
          } else {
            if (this.isF(deliverable.getId()) == true) {
              F = "F";
            } else {
              F = "X";
            }
          }


          if (this.isA(deliverable.getId()) == null) {
            A += "X";
          } else {
            if (this.isA(deliverable.getId()) == true) {
              A += "A";
            } else {
              A += "X";
            }
          }


          try {
            if (this.isI(deliverable.getId()) == null) {
              I += "X";
            } else {
              if (this.isI(deliverable.getId()) == true) {
                I += "I";
              } else {
                I += "X";
              }
            }
          } catch (Exception e) {
            LOG.warn("Error getting isI(Deliverable). Exception: " + e.getMessage());
            I += "X";
          }


          if (this.isR(deliverable.getId()) == null) {
            R += "X";
          } else {
            if (this.isR(deliverable.getId()) == true) {
              R += "R";
            } else {
              R += "X";
            }
          }
        } else {
          F = "<Not applicable>";
          A = "<Not applicable>";
          I = "<Not applicable>";
          R = "<Not applicable>";
        }


        String volume = null;
        String issue = null;
        String pages = null;
        String journal = null;
        String journalIndicator = "";
        String acknowledge = null;
        String flContrib = "";
        // Publication metadata
        // Verify if the deliverable is of type Articles and Books

        if (showPublication) {
          if (deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
            .collect(Collectors.toList()).size() > 0
            && deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
              .collect(Collectors.toList()).get(0) != null) {
            DeliverablePublicationMetadata deliverablePublicationMetadata =
              deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
                .collect(Collectors.toList()).get(0);
            if (deliverablePublicationMetadata.getVolume() != null
              && !deliverablePublicationMetadata.getVolume().trim().isEmpty()) {
              volume = deliverablePublicationMetadata.getVolume();
            }
            if (deliverablePublicationMetadata.getIssue() != null
              && !deliverablePublicationMetadata.getIssue().trim().isEmpty()) {
              issue = deliverablePublicationMetadata.getIssue();
            }
            if (deliverablePublicationMetadata.getPages() != null
              && !deliverablePublicationMetadata.getPages().trim().isEmpty()) {
              pages = deliverablePublicationMetadata.getPages();
            }
            if (deliverablePublicationMetadata.getJournal() != null
              && !deliverablePublicationMetadata.getJournal().trim().isEmpty()) {
              journal = deliverablePublicationMetadata.getJournal();
            }

            if (deliverablePublicationMetadata.getIsiPublication() != null
              && deliverablePublicationMetadata.getIsiPublication() == true) {
              journalIndicator += "● This journal article is an ISI publication \n";
            }
            if (deliverablePublicationMetadata.getNasr() != null && deliverablePublicationMetadata.getNasr() == true) {
              journalIndicator +=
                "● This article have a co-author from a developing country National Agricultural Research System (NARS)\n";
            }
            if (deliverablePublicationMetadata.getCoAuthor() != null
              && deliverablePublicationMetadata.getCoAuthor() == true) {
              journalIndicator +=
                "● This article have a co-author based in an Earth System Science-related academic department";
            }
            if (journalIndicator.trim().isEmpty()) {
              journalIndicator = null;
            }

            if (deliverablePublicationMetadata.getPublicationAcknowledge() != null
              && deliverablePublicationMetadata.getPublicationAcknowledge() == true) {
              acknowledge = "Yes";
            } else {
              acknowledge = "No";
            }

            for (DeliverableCrp deliverableCrp : deliverable.getDeliverableCrps().stream().filter(dc -> dc.isActive())
              .collect(Collectors.toList())) {
              if (deliverableCrp.getCrpPandr() != null && deliverableCrp.getIpProgram() != null) {
                flContrib += "● " + deliverableCrp.getCrpPandr().getAcronym().toUpperCase() + " - "
                  + deliverableCrp.getIpProgram().getAcronym().toUpperCase() + "\n";
              } else {
                if (deliverableCrp.getCrpPandr() != null) {
                  flContrib += "● " + deliverableCrp.getCrpPandr().getName().toUpperCase() + "\n";
                }
              }
            }
          }
        } else {
          volume = "<Not applicable>";
          issue = "<Not applicable>";
          pages = "<Not applicable>";
          journal = "<Not applicable>";
          journalIndicator = "<Not applicable>";
          acknowledge = "<Not applicable>";
          flContrib = "<Not applicable>";
        }

        if (flContrib.trim().isEmpty()) {
          flContrib = null;
        }
        if (journalIndicator != null) {
          if (journalIndicator.trim().isEmpty()) {
            journalIndicator = null;
          }
        }

        // get Flagships related to the project sorted by acronym
        for (ProjectFocus projectFocuses : deliverable.getProject().getProjectFocuses().stream()
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())) {
          if (flagships == null || flagships.isEmpty()) {
            flagships = programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          } else {
            flagships += "\n " + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          }
        }
        // If has regions, add the regions to regionsArrayList
        // Get Regions related to the project sorted by acronym
        if (this.hasProgramnsRegions()) {
          for (ProjectFocus projectFocuses : deliverable.getProject().getProjectFocuses().stream()
            .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            if (regions == null || regions.isEmpty()) {
              regions = programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            } else {
              regions += ", " + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            }
          }
          if (deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getNoRegional() != null
            && deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getNoRegional()) {
            if (regions != null && !regions.isEmpty()) {
              LOG.warn("Project is global and has regions selected");
            }
            regions = "Global";
          }
        } else {
          regions = null;
        }


        model.addRow(new Object[] {deliverable.getId(),
          deliverable.getDeliverableInfo(this.getActualPhase()).getTitle().trim().isEmpty() ? null
            : deliverable.getDeliverableInfo(this.getActualPhase()).getTitle(),
          delivType, delivSubType, delivStatus, delivYear, keyOutput, leader, fundingSources, crossCutting,
          delivNewYear, delivNewYearJustification, delivDisseminationChannel, delivDisseminationUrl, delivOpenAccess,
          delivLicense, titleMetadata, descriptionMetadata, dateMetadata, languageMetadata, countryMetadata,
          keywordsMetadata, citationMetadata, HandleMetadata, DOIMetadata, creatorAuthors, dataSharing,
          qualityAssurance, dataDictionary, tools, F, A, I, R, disseminated, restrictedAccess,
          delivLicenseModifications, volume, issue, pages, journal, journalIndicator, acknowledge, flContrib, projectID,
          projectTitle, flagships, regions, othersResponsibles, newExceptedFlag});
      }
    }
    return model;
  }

  private TypedTableModel getDeliverablesPublicationsReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"publication_id", "title", "publication_sub_type", "deliv_year", "leader", "cross_cutting",
        "deliv_dissemination_channel", "deliv_dissemination_url", "deliv_open_access", "deliv_license", "titleMetadata",
        "descriptionMetadata", "dateMetadata", "languageMetadata", "countryMetadata", "keywordsMetadata",
        "citationMetadata", "HandleMetadata", "DOIMetadata", "creator_authors", "F", "A", "I", "R", "restricted_access",
        "deliv_license_modifications", "volume", "issue", "pages", "journal", "journal_indicators", "acknowledge",
        "fl_contrib", "flagships", "regions", "added_by"},
      new Class[] {Long.class, String.class, String.class, Integer.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class},
      0);


    if (!this
      .getLoggedCrp().getDeliverables().stream().filter(d -> d.getIsPublication() != null
        && d.getIsPublication().booleanValue() && d.isActive() && d.getProject() == null)
      .collect(Collectors.toList()).isEmpty()) {
      for (Deliverable deliverable : this
        .getLoggedCrp().getDeliverables().stream().filter(d -> d.getIsPublication() != null
          && d.getIsPublication().booleanValue() && d.isActive() && d.getProject() == null)
        .collect(Collectors.toList())) {
        // System.out.println(deliverable.getId());
        // System.out.println("#" + i);
        Long publicationId = null;
        Integer delivYear = null;
        String title = null, publicationSubType = null, leader = null, crossCutting = "",
          delivDisseminationChannel = null, delivDisseminationUrl = null, delivOpenAccess = null, delivLicense = null,
          titleMetadata = null, descriptionMetadata = null, dateMetadata = null, languageMetadata = null,
          countryMetadata = null, keywordsMetadata = null, citationMetadata = null, HandleMetadata = null,
          DOIMetadata = null, creatorAuthors = "", F = null, A = null, I = null, R = null, restrictedAccess = null,
          delivLicenseModifications = null, volume = null, issue = null, pages = null, journal = null,
          journalIndicators = "", acknowledge = null, flContrib = "", flagships = null, regions = null, addedBy = null;
        publicationId = deliverable.getId();
        title = deliverable.getDeliverableInfo(this.getActualPhase()).getTitle();

        String delivStatus = deliverable.getDeliverableInfo(this.getActualPhase()).getStatusName();
        Boolean showFAIR = false;
        Boolean showPublication = false;

        if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType() != null) {
          publicationSubType = deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getName();
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 56
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 57
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 76
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 54
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 81
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 82
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 83
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 55
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 62
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 53
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 60
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 59
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 58
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 77
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 75
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 78
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 72
            || deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() == 73) {
            showFAIR = true;
          }


          if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getDeliverableType() != null) {
            // FAIR and deliverable publication
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getDeliverableType()
              .getId() == 49) {
              showFAIR = true;
              showPublication = true;
            }
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() != 0) {
          delivYear = deliverable.getDeliverableInfo(this.getActualPhase()).getYear();
        }


        // Get leaders
        if (!deliverable.getDeliverableLeaders().stream().collect(Collectors.toList()).isEmpty()) {
          for (DeliverableLeader deliverableLeader : deliverable.getDeliverableLeaders().stream()
            .collect(Collectors.toList())) {
            if (leader == null || leader.isEmpty()) {
              leader = deliverableLeader.getInstitution().getComposedName();
            } else {
              leader += "\n" + deliverableLeader.getInstitution().getComposedName();
            }
          }
        }


        // Get cross_cutting dimension
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingNa() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingNa() == true) {
            crossCutting += "● N/A \n";
          }
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() == true) {
            crossCutting += "● Gender \n";
          }
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingYouth() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingYouth() == true) {
            crossCutting += "● Youth \n";
          }
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingCapacity() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingCapacity() == true) {
            crossCutting += "● Capacity Development \n";
          }
        }

        if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() == true) {
            if (deliverable.getDeliverableGenderLevels() == null
              || deliverable.getDeliverableGenderLevels().isEmpty()) {
              crossCutting += "\nGender level(s):\n<Not Defined>";
            } else {
              crossCutting += "\nGender level(s): \n";
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
                if (dgl.getGenderLevel() != 0.0) {
                  crossCutting +=
                    "● " + genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription() + "\n";
                }
              }
            }
          }
        }
        if (crossCutting.isEmpty()) {
          crossCutting = null;
        }

        Boolean isDisseminated = false;
        Boolean showDelivLicenseModifications = false;

        if (deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).size() > 0
          && deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).get(0) != null) {
          // Get deliverable dissemination
          DeliverableDissemination deliverableDissemination =
            deliverable.getDeliverableDisseminations().stream().collect(Collectors.toList()).get(0);

          if (deliverableDissemination.getAlreadyDisseminated() != null
            && deliverableDissemination.getAlreadyDisseminated() == true) {
            isDisseminated = true;
          }
          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationChannel() != null
              && !deliverableDissemination.getDisseminationChannel().isEmpty()) {
              if (ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()) != null) {
                delivDisseminationChannel =
                  ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()).getDesc();
              }
              // deliv_dissemination_channel = deliverableDissemination.getDisseminationChannel();
            }
          } else {
            delivDisseminationChannel = "<Not applicable>";
          }

          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationUrl() != null
              && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
              delivDisseminationUrl = deliverableDissemination.getDisseminationUrl().replace(" ", "%20");
            }
          } else {
            delivDisseminationUrl = "<Not applicable>";
          }

          if (deliverableDissemination.getIsOpenAccess() != null) {
            if (deliverableDissemination.getIsOpenAccess() == true) {
              delivOpenAccess = "Yes";
              restrictedAccess = "<Not applicable>";
            } else {
              // get the open access
              delivOpenAccess = "No";
              if (deliverableDissemination.getIntellectualProperty() != null
                && deliverableDissemination.getIntellectualProperty() == true) {
                restrictedAccess = "Intellectual Property Rights (confidential information)";
              }

              if (deliverableDissemination.getLimitedExclusivity() != null
                && deliverableDissemination.getLimitedExclusivity() == true) {
                restrictedAccess = "Limited Exclusivity Agreements";
              }

              if (deliverableDissemination.getNotDisseminated() != null
                && deliverableDissemination.getNotDisseminated() == true) {
                restrictedAccess = "Not Disseminated";
              }

              if (deliverableDissemination.getRestrictedUseAgreement() != null
                && deliverableDissemination.getRestrictedUseAgreement() == true) {
                restrictedAccess = "Restricted Use Agreement - Restricted access (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedAccessUntil() != null) {
                  restrictedAccess +=
                    "\nRestricted access until: " + deliverableDissemination.getRestrictedAccessUntil();
                } else {
                  restrictedAccess += "\nRestricted access until: <Not Defined>";
                }
              }

              if (deliverableDissemination.getEffectiveDateRestriction() != null
                && deliverableDissemination.getEffectiveDateRestriction() == true) {
                restrictedAccess = "Effective Date Restriction - embargoed periods (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedEmbargoed() != null) {
                  restrictedAccess +=
                    "\nRestricted embargoed date: " + deliverableDissemination.getRestrictedEmbargoed();
                } else {
                  restrictedAccess += "\nRestricted embargoed date: <Not Defined>";
                }
              }
            }
          }

          if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() != null) {
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() == true) {
              if (deliverable.getDeliverableInfo(this.getActualPhase()).getLicense() != null
                && !deliverable.getDeliverableInfo(this.getActualPhase()).getLicense().isEmpty()) {
                delivLicense = deliverable.getDeliverableInfo(this.getActualPhase()).getLicense();
              } else {
                delivLicense = "No";
              }
              if (delivLicense.equals("OTHER")) {
                delivLicense = deliverable.getDeliverableInfo(this.getActualPhase()).getOtherLicense();
                showDelivLicenseModifications = true;
                if (deliverable.getDeliverableInfo(this.getActualPhase()).getAllowModifications() != null
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getAllowModifications() == true) {
                  delivLicenseModifications = "Yes";
                } else {
                  delivLicenseModifications = "No";
                }
              } else {
                if (!showDelivLicenseModifications) {
                  delivLicenseModifications = "<Not applicable>";
                }
              }
            } else {
              delivLicense = "No";
            }
          }
        }

        if (delivLicense != null && delivLicense.isEmpty()) {
          delivLicense = null;
        }

        for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getDeliverableMetadataElements()
          .stream().filter(dm -> dm.isActive() && dm.getMetadataElement() != null).collect(Collectors.toList())) {

          if (deliverableMetadataElement.getMetadataElement().getId() == 1) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              titleMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 8) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              descriptionMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 17) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              dateMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 24) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              languageMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 28) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              countryMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 37) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              keywordsMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 22) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              citationMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 35) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              HandleMetadata = deliverableMetadataElement.getElementValue();
            }
          }

          if (deliverableMetadataElement.getMetadataElement().getId() == 36) {
            if (deliverableMetadataElement.getElementValue() != null
              && !deliverableMetadataElement.getElementValue().isEmpty()) {
              DOIMetadata = deliverableMetadataElement.getElementValue();
            }
          }
        }

        for (DeliverableUser deliverableUser : deliverable.getDeliverableUsers().stream().filter(du -> du.isActive())
          .collect(Collectors.toList())) {
          creatorAuthors += "\n● ";
          if (!deliverableUser.getLastName().isEmpty()) {
            creatorAuthors += deliverableUser.getLastName() + " - ";
          }
          if (!deliverableUser.getFirstName().isEmpty()) {
            creatorAuthors += deliverableUser.getFirstName();
          }
          if (!deliverableUser.getElementId().isEmpty()) {
            creatorAuthors += "<" + deliverableUser.getElementId() + ">";
          }
        }

        if (creatorAuthors.isEmpty()) {
          creatorAuthors = null;
        }


        if (showFAIR) {
          // FAIR

          if (this.isF(deliverable.getId()) == null) {
            F = "X";
          } else {
            if (this.isF(deliverable.getId()) == true) {
              F = "F";
            } else {
              F = "X";
            }
          }


          if (this.isA(deliverable.getId()) == null) {
            A = "X";
          } else {
            if (this.isA(deliverable.getId()) == true) {
              A = "A";
            } else {
              A = "X";
            }
          }


          try {
            if (this.isI(deliverable.getId()) == null) {
              I = "X";
            } else {
              if (this.isI(deliverable.getId()) == true) {
                I = "I";
              } else {
                I = "X";
              }
            }
          } catch (Exception e) {
            LOG.warn("Error getting isI(Deliverable). Exception: " + e.getMessage());
            I = "X";
          }


          if (this.isR(deliverable.getId()) == null) {
            R = "X";
          } else {
            if (this.isR(deliverable.getId()) == true) {
              R = "R";
            } else {
              R = "X";
            }
          }
        } else {
          F = "<Not applicable>";
          A = "<Not applicable>";
          I = "<Not applicable>";
          R = "<Not applicable>";
        }


        // Publication metadata
        // Verify if the deliverable is of type Articles and Books

        if (showPublication) {
          if (deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
            .collect(Collectors.toList()).size() > 0
            && deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
              .collect(Collectors.toList()).get(0) != null) {
            DeliverablePublicationMetadata deliverablePublicationMetadata =
              deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
                .collect(Collectors.toList()).get(0);
            if (deliverablePublicationMetadata.getVolume() != null
              && !deliverablePublicationMetadata.getVolume().trim().isEmpty()) {
              volume = deliverablePublicationMetadata.getVolume();
            }
            if (deliverablePublicationMetadata.getIssue() != null
              && !deliverablePublicationMetadata.getIssue().trim().isEmpty()) {
              issue = deliverablePublicationMetadata.getIssue();
            }
            if (deliverablePublicationMetadata.getPages() != null
              && !deliverablePublicationMetadata.getPages().trim().isEmpty()) {
              pages = deliverablePublicationMetadata.getPages();
            }
            if (deliverablePublicationMetadata.getJournal() != null
              && !deliverablePublicationMetadata.getJournal().trim().isEmpty()) {
              journal = deliverablePublicationMetadata.getJournal();
            }

            if (deliverablePublicationMetadata.getIsiPublication() != null
              && deliverablePublicationMetadata.getIsiPublication() == true) {
              journalIndicators += "● This journal article is an ISI publication \n";
            }
            if (deliverablePublicationMetadata.getNasr() != null && deliverablePublicationMetadata.getNasr() == true) {
              journalIndicators +=
                "● This article have a co-author from a developing country National Agricultural Research System (NARS)\n";
            }
            if (deliverablePublicationMetadata.getCoAuthor() != null
              && deliverablePublicationMetadata.getCoAuthor() == true) {
              journalIndicators +=
                "● This article have a co-author based in an Earth System Science-related academic department";
            }
            if (journalIndicators.trim().isEmpty()) {
              journalIndicators = null;
            }

            if (deliverablePublicationMetadata.getPublicationAcknowledge() != null
              && deliverablePublicationMetadata.getPublicationAcknowledge() == true) {
              acknowledge = "Yes";
            } else {
              acknowledge = "No";
            }

            for (DeliverableCrp deliverableCrp : deliverable.getDeliverableCrps().stream().filter(dc -> dc.isActive())
              .collect(Collectors.toList())) {
              if (deliverableCrp.getCrpPandr() != null && deliverableCrp.getIpProgram() != null) {
                flContrib += "● " + deliverableCrp.getCrpPandr().getAcronym().toUpperCase() + " - "
                  + deliverableCrp.getIpProgram().getAcronym().toUpperCase() + "\n";
              } else {
                if (deliverableCrp.getCrpPandr() != null) {
                  flContrib += "● " + deliverableCrp.getCrpPandr().getName().toUpperCase() + "\n";
                }
              }
            }
          }
        } else {
          volume = "<Not applicable>";
          issue = "<Not applicable>";
          pages = "<Not applicable>";
          journal = "<Not applicable>";
          journalIndicators = "<Not applicable>";
          acknowledge = "<Not applicable>";
          flContrib = "<Not applicable>";
        }

        if (flContrib.trim().isEmpty()) {
          flContrib = null;
        }
        if (journalIndicators != null) {
          if (journalIndicators.trim().isEmpty()) {
            journalIndicators = null;
          }
        }

        // get Flagships related to the project sorted by acronym
        for (DeliverableProgram deliverableProgram : deliverable.getDeliverablePrograms().stream()
          .filter(dp -> dp.getIpProgram() != null && dp.getIpProgram().isActive()).collect(Collectors.toList())) {
          if (deliverableProgram.getIpProgram().getIpProgramType() != null) {
            Integer programType = deliverableProgram.getIpProgram().getIpProgramType().getId().intValue();
            switch (programType) {
              case 4:
                if (flagships == null || flagships.isEmpty()) {
                  flagships = deliverableProgram.getIpProgram().getAcronym();
                } else {
                  flagships += "\n " + deliverableProgram.getIpProgram().getAcronym();
                }
                break;
              case 5:
                if (regions == null || regions.isEmpty()) {
                  regions = deliverableProgram.getIpProgram().getAcronym();
                } else {
                  regions += "\n " + deliverableProgram.getIpProgram().getAcronym();
                }
                break;
              default:
                break;
            }
          }
        }

        if (deliverable.getCreatedBy() != null) {
          addedBy = deliverable.getCreatedBy().getComposedName();
        }

        model.addRow(new Object[] {publicationId, title, publicationSubType, delivYear, leader, crossCutting,
          delivDisseminationChannel, delivDisseminationUrl, delivOpenAccess, delivLicense, titleMetadata,
          descriptionMetadata, dateMetadata, languageMetadata, countryMetadata, keywordsMetadata, citationMetadata,
          HandleMetadata, DOIMetadata, creatorAuthors, F, A, I, R, restrictedAccess, delivLicenseModifications, volume,
          issue, pages, journal, journalIndicators, acknowledge, flContrib, flagships, regions, addedBy});
      }
    }
    return model;
  }

  public String getDeliverableUrl(String fileType, Deliverable deliverable) {
    return config.getDownloadURL() + "/" + this.getDeliverableUrlPath(fileType, deliverable).replace('\\', '/');
  }

  public String getDeliverableUrlPath(String fileType, Deliverable deliverable) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + deliverable.getId() + File.separator
      + "deliverable" + File.separator + fileType + File.separator;
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
    fileName.append("DeliverablesReportingSummary-");
    fileName.append(this.getSelectedYear() + "_");
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
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible"},
      new Class[] {String.class, String.class, String.class, Boolean.class});
    model.addRow(new Object[] {center, date, year, this.hasProgramnsRegions()});
    return model;
  }

  @Override
  public void prepare() throws Exception {
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

  private DeliverablePartnership responsiblePartner(Deliverable deliverable) {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
    } catch (Exception e) {
      LOG.warn("Error getting DeliverablePartnership. Exception: " + e.getMessage());
      return null;
    }

  }

  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

}
