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
import org.cgiar.ccafs.marlo.data.manager.CrossCuttingScoringManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLocationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepositoryChannelManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicScope;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

/**
 * DeliverablesReportingExcelSummaryAction
 * 
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 10:30:10 AM: get deliverable dissemination from RepositoryChannel table
 */
public class DeliverablesReportingExcelSummaryAction extends BaseSummariesAction implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(DeliverablesReportingExcelSummaryAction.class);
  private final CrpProgramManager programManager;
  private final DeliverableManager deliverableManager;
  private final GenderTypeManager genderTypeManager;
  private final RepositoryChannelManager repositoryChannelManager;
  private final ResourceManager resourceManager;
  private final CrossCuttingScoringManager crossCuttingScoringManager;
  private final DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager;
  private final DeliverableGeographicScopeManager deliverableGeographicScopeManager;
  private final DeliverableGeographicRegionManager deliverableGeographicRegionManager;
  private DeliverableLocationManager deliverableLocationManager;
  private final CrpPpaPartnerManager crpPpaPartnerManager;
  private final DeliverableInfoManager deliverableInfoManager;
  private String showAllYears;
  private String ppa;


  // XLSX bytes
  private byte[] bytesXLSX;


  // Streams
  InputStream inputStream;

  // Parameters
  private long startTime;

  @Inject
  public DeliverablesReportingExcelSummaryAction(APConfig config, GlobalUnitManager crpManager,
    CrpProgramManager programManager, GenderTypeManager genderTypeManager, DeliverableManager deliverableManager,
    PhaseManager phaseManager, RepositoryChannelManager repositoryChannelManager, ResourceManager resourceManager,
    CrossCuttingScoringManager crossCuttingScoringManager, ProjectManager projectManager,
    DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager,
    DeliverableGeographicScopeManager deliverableGeographicScopeManager,
    DeliverableGeographicRegionManager deliverableGeographicRegionManager,
    DeliverableLocationManager deliverableLocationManager, CrpPpaPartnerManager crpPpaPartnerManager,
    DeliverableInfoManager deliverableInfoManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.genderTypeManager = genderTypeManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.programManager = programManager;
    this.deliverableManager = deliverableManager;
    this.repositoryChannelManager = repositoryChannelManager;
    this.resourceManager = resourceManager;
    this.crossCuttingScoringManager = crossCuttingScoringManager;
    this.deliverableCrossCuttingMarkerManager = deliverableCrossCuttingMarkerManager;
    this.deliverableGeographicScopeManager = deliverableGeographicScopeManager;
    this.deliverableGeographicRegionManager = deliverableGeographicRegionManager;
    this.deliverableLocationManager = deliverableLocationManager;
    this.deliverableInfoManager = deliverableInfoManager;
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
    masterReport.getParameterValues().put("i8nCountry", this.getText("metadata.countries"));
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
    masterReport.getParameterValues().put("i8nGender", this.getText("summaries.gender"));
    masterReport.getParameterValues().put("i8nYouth", this.getText("summaries.youth"));
    masterReport.getParameterValues().put("i8nCap", this.getText("summaries.capacityDevelopment"));
    masterReport.getParameterValues().put("i8nKeyOutput",
      this.getText("project.deliverable.generalInformation.keyOutput"));
    masterReport.getParameterValues().put("i8nOutcomes", this.getText("impactPathway.menu.hrefOutcomes"));
    masterReport.getParameterValues().put("i8nGeographicScope", this.getText("deliverable.geographicScope"));
    masterReport.getParameterValues().put("i8nDeliverableCountry", this.getText("deliverable.countries"));
    masterReport.getParameterValues().put("i8nRegion", this.getText("deliverable.region"));
    masterReport.getParameterValues().put("i8nProjectStatus", this.getText("deliverable.status"));
    masterReport.getParameterValues().put("i8nFundingSource", this.getText("deliverable.fundingSources"));
    masterReport.getParameterValues().put("i8nIsComplete", this.getText("deliverable.isComplete"));
    masterReport.getParameterValues().put("i8nIndividual", this.getText("deliverable.individual"));
    masterReport.getParameterValues().put("i8nPartnersResponsible", this.getText("deliverable.managing"));
    masterReport.getParameterValues().put("i8nManagingResponsible", this.getText("deliverable.project.managing"));
    masterReport.getParameterValues().put("i8nClimate", this.getText("deliverable.climateChange"));
    masterReport.getParameterValues().put("i8nJustification", this.getText("deliverable.justification"));
    masterReport.getParameterValues().put("i8nDeliverableDescription", this.getText("deliverable.description"));
    masterReport.getParameterValues().put("i8nProjectLeadPartner", this.getText("summaries.deliverable.leadPartner"));

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
    masterReport.getParameterValues().put("i8nNewDeliverable",
      this.getText("summaries.board.report.expectedDeliverables.isNewDeliverable"));

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
        .createDirectly(this.getClass().getResource("/pentaho/crp/ReportingDeliverables.prpt"), MasterReport.class);

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
        "keyOutput", "outcomes", "leader", "funding_sources", "deliv_new_year", "deliv_new_year_justification",
        "deliv_dissemination_channel", "deliv_dissemination_url", "deliv_open_access", "deliv_license", "titleMetadata",
        "descriptionMetadata", "dateMetadata", "languageMetadata", "countryMetadata", "keywordsMetadata",
        "citationMetadata", "HandleMetadata", "DOIMetadata", "creator_authors", "data_sharing", "qualityAssurance",
        "dataDictionary", "tools", "F", "A", "I", "R", "disseminated", "restricted_access",
        "deliv_license_modifications", "volume", "issue", "pages", "journal", "journal_indicators", "acknowledge",
        "fl_contrib", "project_ID", "project_title", "flagships", "regions", "others_responsibles", "newExceptedFlag",
        "phaseID", "gender", "youth", "cap", "geographicScope", "region", "country", "status", "isComplete",
        "individual", "ppaResponsible", "managingResponsible", "climate", "justification", "description"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Integer.class, String.class,
        String.class, String.class, String.class, Integer.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, Long.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    if (!deliverableManager.findAll().isEmpty()) {
      List<Deliverable> deliverables = new ArrayList<>();


      /**
       * 08/05 HJ - Fixing this report trying to not use Lambda expression in more of 3 or 4 list because the Hibernate
       * Lazy Java Assist will occupy all the Memory causing JAVA head space Error and crash the App server (Tomcat)
       */

      List<DeliverableInfo> infos = deliverableInfoManager.getDeliverablesInfoByPhase(this.getSelectedPhase());
      if (infos != null && !infos.isEmpty()) {
        for (DeliverableInfo deliverableInfo : infos) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.setDeliverableInfo(deliverableInfo);
          if (showAllYears.equals("true")) {
            deliverables.add(deliverable);
          } else {
            if (deliverableInfo != null && deliverable.getDeliverableInfo().getStatus() != null
              && ((deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
                && (deliverableInfo.getYear() == this.getSelectedYear() || (deliverableInfo.getNewExpectedYear() != null
                  && deliverableInfo.getNewExpectedYear().intValue() >= this.getSelectedYear())))
                || (deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
                  && (deliverableInfo.getYear() == this.getSelectedYear()
                    || (deliverableInfo.getNewExpectedYear() != null
                      && deliverableInfo.getNewExpectedYear().intValue() == this.getSelectedYear())))
                || (deliverableInfo.getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
                  && (deliverableInfo.getYear() == this.getSelectedYear()
                    || (deliverableInfo.getNewExpectedYear() != null
                      && deliverableInfo.getNewExpectedYear().intValue() == this.getSelectedYear())))
                || (deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                  && (deliverableInfo.getYear() == this.getSelectedYear())))
              && (deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
                || deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
                || deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())
                || deliverableInfo.getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))) {
              deliverables.add(deliverable);

            }

          }

        }
      }

      // if (showAllYears.equals("true")) {
      // deliverables = new ArrayList<>(deliverableManager.findAll().stream().filter(d -> d.isActive()
      // && d.getProject() != null && d.getProject().isActive()
      // && d.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
      // && d.getProject().getGlobalUnitProjects().stream().filter(
      // gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
      // .collect(Collectors.toList()).size() > 0
      // && d.getDeliverableInfo(this.getSelectedPhase()) != null).collect(Collectors.toList()));
      // } else {
      // deliverables = new ArrayList<>(deliverableManager.findAll().stream().filter(d -> d.isActive()
      // && d.getProject() != null && d.getProject().isActive()
      // && d.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
      // && d.getProject().getGlobalUnitProjects().stream().filter(
      // gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
      // .collect(Collectors.toList()).size() > 0
      // && d.getDeliverableInfo(this.getSelectedPhase()) != null && d.getDeliverableInfo().getStatus() != null
      // && ((d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Complete.getStatusId())
      // && (d.getDeliverableInfo().getYear() == this.getSelectedYear()
      // || (d.getDeliverableInfo().getNewExpectedYear() != null
      // && d.getDeliverableInfo().getNewExpectedYear().intValue() >= this.getSelectedYear())))
      // || (d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Extended.getStatusId())
      // && (d.getDeliverableInfo().getYear() == this.getSelectedYear()
      // || (d.getDeliverableInfo().getNewExpectedYear() != null
      // && d.getDeliverableInfo().getNewExpectedYear().intValue() == this.getSelectedYear())))
      // || (d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
      // && (d.getDeliverableInfo().getYear() == this.getSelectedYear()
      // || (d.getDeliverableInfo().getNewExpectedYear() != null
      // && d.getDeliverableInfo().getNewExpectedYear().intValue() == this.getSelectedYear())))
      // || (d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
      // && (d.getDeliverableInfo().getYear() == this.getSelectedYear())))
      // && (d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Extended.getStatusId())
      // || d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Complete.getStatusId())
      // || d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
      // || d.getDeliverableInfo().getStatus().intValue() == Integer
      // .parseInt(ProjectStatusEnum.Ongoing.getStatusId())))
      // .collect(Collectors.toList()));
      // }

      // get Reporting deliverables: added on-going as request of Amanda


      HashSet<Deliverable> deliverablesHL = new HashSet<>();
      deliverablesHL.addAll(deliverables);
      deliverables.clear();
      deliverables.addAll(deliverablesHL);
      deliverables.sort((d1, d2) -> {
        if (d1.getProject().getId().compareTo(d2.getProject().getId()) == 0) {
          return d1.getId().compareTo(d2.getId());
        } else {
          return d1.getProject().getId().compareTo(d2.getProject().getId());
        }
      });

      for (Deliverable deliverable : deliverables) {
        Boolean activePPAFilter = ppa != null && !ppa.isEmpty() && !ppa.equals("All") && !ppa.equals("-1");
        Boolean addDeliverableRow = true;
        String delivType = null;
        String title = null;
        String delivSubType = null;
        String delivStatus = deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase());
        Integer delivYear = null;
        String keyOutput = "";
        String outcomes = "";
        String leader = null;
        String othersResponsibles = null;
        String fundingSources = "";
        Boolean showFAIR = false;
        Boolean showPublication = false;
        Boolean showCompilance = false;
        String projectID = null;
        String projectTitle = null;
        String flagships = null, regions = null, status = null, isComplete = "", justification = "", description = "";
        String deliverableNew = "";

        if (deliverable.getProject() != null) {
          projectID = deliverable.getProject().getId().toString();
          if (deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
            && deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()).getTitle() != null) {
            projectTitle = deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()).getTitle();
          }
        }

        // Justification
        if (deliverable.getDeliverableInfo() != null) {
          if (deliverable.getDeliverableInfo().getStatusDescription() != null) {
            justification = deliverable.getDeliverableInfo().getStatusDescription();
          } else {
            justification = "<Not applicable>";
          }
        } else {
          justification = "<Not applicable>";
        }

        // Description
        if (deliverable.getDeliverableInfo() != null) {
          if (deliverable.getDeliverableInfo().getDescription() != null) {
            description = deliverable.getDeliverableInfo().getDescription();
          } else {
            description = "<Not applicable>";
          }
        } else {
          description = "<Not applicable>";
        }

        if (deliverable.getProject() != null
          && deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
          && deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()).getStatusName() != null) {
          status = deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()).getStatusName();
        }

        // New deliverable
        if (this.isDeliverableNew(deliverable.getId())) {
          deliverableNew = "Yes";
        } else {
          deliverableNew = "No";
        }

        Long phaseID = deliverable.getDeliverableInfo().getPhase().getId();
        title = (deliverable.getDeliverableInfo().getTitle() != null
          && !deliverable.getDeliverableInfo().getTitle().isEmpty()) ? deliverable.getDeliverableInfo().getTitle()
            : null;

        if (deliverable.getDeliverableInfo().getDeliverableType() != null) {
          if (deliverable.getDeliverableInfo().getDeliverableType().getName() != null) {
            delivSubType = deliverable.getDeliverableInfo().getDeliverableType().getName();
          }
          if (deliverable.getDeliverableInfo().getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 56
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 57
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 76
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 54
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 81
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 82
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 83
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 55
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 62
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 53
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 60
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 59
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 58
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 77
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 75
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 78
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 72
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 73) {
            showFAIR = true;
          }
          if (deliverable.getDeliverableInfo().getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 74) {
            showCompilance = true;
          }

          if (deliverable.getDeliverableInfo().getDeliverableType().getDeliverableCategory() != null) {
            delivType = deliverable.getDeliverableInfo().getDeliverableType().getDeliverableCategory().getName();
            // FAIR and deliverable publication
            if (deliverable.getDeliverableInfo().getDeliverableType().getDeliverableCategory().getId() == 49) {
              showFAIR = true;
              showPublication = true;
            }
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo().getYear() != -1) {
          delivYear = deliverable.getDeliverableInfo().getYear();
        }

        if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput() != null) {
          keyOutput += "• ";

          if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getCrpClusterOfActivity()
            .getCrpProgram() != null) {
            keyOutput += deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getCrpClusterOfActivity()
              .getCrpProgram().getAcronym() + " - ";
          }
          keyOutput += deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getKeyOutput();
          // Get Outcomes Related to the KeyOutput
          for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : deliverable.getDeliverableInfo()
            .getCrpClusterKeyOutput().getCrpClusterKeyOutputOutcomes().stream()
            .filter(
              ko -> ko.isActive() && ko.getCrpProgramOutcome() != null && ko.getCrpProgramOutcome().getPhase() != null
                && ko.getCrpProgramOutcome().getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
            outcomes += " • ";
            if (crpClusterKeyOutputOutcome.getCrpProgramOutcome().getCrpProgram() != null
              && !crpClusterKeyOutputOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym().isEmpty()) {
              outcomes += crpClusterKeyOutputOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym() + " Outcome: ";
            }
            outcomes += crpClusterKeyOutputOutcome.getCrpProgramOutcome().getDescription() + "\n";
          }
        }
        if (keyOutput.isEmpty()) {
          keyOutput = null;
        }
        if (outcomes.isEmpty()) {
          outcomes = null;
        }

        // Get partner responsible and institution
        // Set responible;
        DeliverableUserPartnership responisble = this.responsiblePartner(deliverable);

        if (responisble != null) {
          if (responisble.getDeliverableUserPartnershipPersons() != null) {
            // DeliverableUserPartnershipPerson responsibleppp = responisble.getDeliverableUserPartnershipPersons()
            // .stream().filter(dp -> dp.isActive()).collect(Collectors.toList()).get(0);

            // Get project leader

            if (deliverable.getProject().getLeader(this.getSelectedPhase()) != null) {
              ProjectPartner leaderProject = deliverable.getProject().getLeader(this.getSelectedPhase());
              if (leaderProject.getInstitution() != null) {
                if (leaderProject.getInstitution().getAcronym() != null
                  && !leaderProject.getInstitution().getAcronym().trim().isEmpty()) {
                  leader = leaderProject.getInstitution().getAcronym();
                } else {
                  leader = leaderProject.getInstitution().getName();
                }
              }
            }
          }
        }

        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive() && d.getPhase() != null && d.getPhase().equals(this.getSelectedPhase())
            && d.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null)
          .collect(Collectors.toList())) {
          String financialCode = "";
          if (dfs.getFundingSource().getFundingSourceInfo().getFinanceCode() != null
            && !dfs.getFundingSource().getFundingSourceInfo().getFinanceCode().isEmpty()) {
            financialCode = " (" + dfs.getFundingSource().getFundingSourceInfo().getFinanceCode() + ")";
          }
          fundingSources += "●  FS" + dfs.getFundingSource().getId() + financialCode + " - "
            + dfs.getFundingSource().getFundingSourceInfo().getTitle() + "\n";
        }
        if (fundingSources.isEmpty()) {
          fundingSources = null;
        }

        // Reporting
        Integer delivNewYear = null;
        String newExceptedFlag = "na";
        String delivNewYearJustification = null;

        if (deliverable.getDeliverableInfo().getStatus() != null) {
          // Extended
          if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())
            && deliverable.getDeliverableInfo().getNewExpectedYear() != null
            && deliverable.getDeliverableInfo().getNewExpectedYear().intValue() != -1) {
            delivNewYear = deliverable.getDeliverableInfo().getNewExpectedYear();
            delivNewYearJustification = deliverable.getDeliverableInfo().getStatusDescription();
            newExceptedFlag = "nd";
          }
          // Complete
          if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
              && deliverable.getDeliverableInfo().getNewExpectedYear() != -1) {
              delivNewYear = deliverable.getDeliverableInfo().getNewExpectedYear();
            }
          }
          // Canceled
          if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
            delivNewYearJustification = deliverable.getDeliverableInfo().getStatusDescription();
          }
        }

        // Is deliverable complete
        if (this.isDeliverableComplete(deliverable.getId(), this.getSelectedPhase().getId()) != null) {
          Boolean completeDeliverable =
            this.isDeliverableComplete(deliverable.getId(), this.getSelectedPhase().getId());
          if (completeDeliverable == true) {
            isComplete = "Yes";
          } else {
            isComplete = "No";
          }
        } else {
          isComplete = "No";
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

        if (deliverable.getDeliverableDisseminations().stream()
          .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList()).size() > 0
          && deliverable.getDeliverableDisseminations().stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList()).get(0) != null) {
          // Get deliverable dissemination
          DeliverableDissemination deliverableDissemination = deliverable.getDeliverableDisseminations().stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList()).get(0);

          if (deliverableDissemination.getAlreadyDisseminated() != null
            && deliverableDissemination.getAlreadyDisseminated() == true) {
            isDisseminated = true;
            disseminated = "Yes";
          }
          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationChannel() != null
              && !deliverableDissemination.getDisseminationChannel().isEmpty()) {
              RepositoryChannel repositoryChannel = repositoryChannelManager
                .getRepositoryChannelByShortName(deliverableDissemination.getDisseminationChannel());
              if (repositoryChannel != null) {
                delivDisseminationChannel = repositoryChannel.getName();
              }
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

            if (deliverableDissemination.getConfidentialUrl() != null
              && !deliverableDissemination.getConfidentialUrl().isEmpty()) {
              if (deliverableDissemination.getConfidentialUrl().contains("REQUIRED")) {
                delivDisseminationUrl = deliverableDissemination.getConfidentialUrl();
              } else {
                delivDisseminationUrl = deliverableDissemination.getConfidentialUrl().replace(" ", "%20");
              }
            }
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
                restrictedAccess = "Restricted Use AgreementOCS - Restricted access (if so, what are these periods?)";
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
          .stream().filter(dm -> dm.isActive() && dm.getMetadataElement() != null && dm.getPhase() != null
            && dm.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {

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
        if (deliverable.getDeliverableUsers() != null) {
          for (DeliverableUser deliverableUser : deliverable.getDeliverableUsers().stream()
            .filter(du -> du.isActive() && du.getPhase() != null && du.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
            creatorAuthors += "\n● ";
            if (deliverableUser != null) {
              if (!deliverableUser.getLastName().isEmpty()) {
                creatorAuthors += deliverableUser.getLastName() + " - ";
              }
              if (!deliverableUser.getFirstName().isEmpty()) {
                creatorAuthors += deliverableUser.getFirstName();
              }
              if (deliverableUser.getElementId() != null) {
                if (!deliverableUser.getElementId().isEmpty()) {
                  creatorAuthors += "<" + deliverableUser.getElementId() + ">";
                }
              }
            }
          }
        }

        if (creatorAuthors.isEmpty()) {
          creatorAuthors = null;
        }

        String dataSharing = "";
        if (isDisseminated && (delivDisseminationChannel != null && !delivDisseminationChannel.equals("Other"))) {


          for (DeliverableDataSharingFile deliverableDataSharingFile : deliverable.getDeliverableDataSharingFiles()
            .stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
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


          if (deliverable.getDeliverableQualityChecks().stream()
            .filter(qc -> qc.isActive() && qc.getPhase() != null && qc.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList()).size() > 0
            && deliverable.getDeliverableQualityChecks().stream()
              .filter(qc -> qc.isActive() && qc.getPhase() != null && qc.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList()).get(0) != null) {
            DeliverableQualityCheck deliverableQualityCheck = deliverable.getDeliverableQualityChecks().stream()
              .filter(qc -> qc.isActive() && qc.getPhase() != null && qc.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList()).get(0);
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
          if (deliverable.getDeliverablePublicationMetadatas().stream()
            .filter(dpm -> dpm.isActive() && dpm.getPhase() != null && dpm.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList()).size() > 0
            && deliverable.getDeliverablePublicationMetadatas().stream()
              .filter(dpm -> dpm.isActive() && dpm.getPhase() != null && dpm.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList()).get(0) != null) {
            DeliverablePublicationMetadata deliverablePublicationMetadata =
              deliverable.getDeliverablePublicationMetadatas().stream()
                .filter(dpm -> dpm.isActive() && dpm.getPhase().equals(this.getSelectedPhase()))
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

            for (DeliverableCrp deliverableCrp : deliverable.getDeliverableCrps().stream()
              .filter(dc -> dc.isActive() && dc.getPhase() != null && dc.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList())) {
              if (deliverableCrp.getCrpProgram() != null) {
                flContrib += "● " + deliverableCrp.getCrpProgram().getCrp().getAcronym().toUpperCase() + " - "
                  + deliverableCrp.getCrpProgram().getAcronym().toUpperCase() + "\n";
              } else {
                if (deliverableCrp.getGlobalUnit() != null) {
                  flContrib += "● " + deliverableCrp.getGlobalUnit().getName().toUpperCase() + "\n";
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
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(this.getCurrentCrp().getId()))
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
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            if (regions == null || regions.isEmpty()) {
              regions = programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            } else {
              regions += ", " + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            }
          }
          if (deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()).getNoRegional() != null
            && deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()).getNoRegional()) {
            if (regions != null && !regions.isEmpty()) {
              LOG.warn("Project is global and has regions selected");
            }
            regions = "Global";
          }
        } else {
          regions = null;
        }

        // Get cross_cutting dimension
        String crossCutting = "";
        String gender = "", youth = "", cap = "", climate = "";
        Boolean isOldCrossCutting = this.getSelectedYear() < 2018;

        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerGender = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 1, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerYouth = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 2, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerCapDev = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 3, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerClimateChange = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 4, this.getSelectedPhase().getId());

        if (isOldCrossCutting) {
          // Gender
          if (deliverableCrossCuttingMarkerGender != null) {
            if (deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getId() != 0
              && deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getId() != 4) {
              gender = deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName();
              String genderLevels = "";
              int countGenderLevel = 0;
              List<DeliverableGenderLevel> deliverableGenderLevels = deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive() && dgl.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
              if (deliverableGenderLevels == null || deliverableGenderLevels.isEmpty()) {
                genderLevels = "";
              } else {
                genderLevels += "Gender level(s): ";
                for (DeliverableGenderLevel dgl : deliverableGenderLevels) {
                  if (dgl.getGenderLevel() != 0.0) {
                    if (countGenderLevel == 0) {
                      genderLevels += genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription();
                    } else {
                      genderLevels += ", " + genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription();
                    }
                    countGenderLevel++;
                  }
                }
              }

              if (!genderLevels.isEmpty()) {
                if (gender.isEmpty()) {
                  gender = genderLevels;
                } else {
                  gender += "\n" + genderLevels;
                }
              }
              if (gender.isEmpty()) {
                gender = "Yes";
              }
            }
          }
          if (gender.isEmpty()) {
            gender = "No";
          }

          // Youth
          if (deliverableCrossCuttingMarkerYouth != null) {
            if (deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getId() != 0
              && deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getId() != 4) {
              youth = deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName();
              if (youth.isEmpty()) {
                youth = "Yes";
              }
            }
          }
          if (youth.isEmpty()) {
            youth = "No";
          }

          // Cap
          if (deliverableCrossCuttingMarkerCapDev != null) {
            if (deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getId() != 0
              && deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getId() != 4) {
              cap = deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName();
              if (cap.isEmpty()) {
                cap = "Yes";
              }
            }
          }
          if (cap.isEmpty()) {
            cap = "No";
          }

          // Climate
          if (deliverableCrossCuttingMarkerClimateChange != null) {
            if (deliverableCrossCuttingMarkerClimateChange.getRepIndGenderYouthFocusLevel().getId() != 0
              && deliverableCrossCuttingMarkerClimateChange.getRepIndGenderYouthFocusLevel().getId() != 4) {
              cap = deliverableCrossCuttingMarkerClimateChange.getRepIndGenderYouthFocusLevel().getPowbName();
              if (cap.isEmpty()) {
                cap = "Yes";
              }
            }
          }
          if (cap.isEmpty()) {
            cap = "No";
          }

        } else {
          // Gender
          if (deliverableCrossCuttingMarkerGender != null) {
            if (deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel() != null) {
              gender = deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName();
            }
          }
          if (gender.isEmpty()) {
            gender = "0-Not Targeted";
          }

          // Youth
          if (deliverableCrossCuttingMarkerYouth != null) {
            if (deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel() != null) {
              youth = deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName();
            }
          }
          if (youth.isEmpty()) {
            youth = "0-Not Targeted";
          }

          // Cap
          if (deliverableCrossCuttingMarkerCapDev != null) {
            if (deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel() != null) {
              cap = deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName();
            }
          }
          if (cap.isEmpty()) {
            cap = "0-Not Targeted";
          }
          // Climate
          if (deliverableCrossCuttingMarkerClimateChange != null) {
            if (deliverableCrossCuttingMarkerClimateChange.getRepIndGenderYouthFocusLevel() != null) {
              climate = deliverableCrossCuttingMarkerClimateChange.getRepIndGenderYouthFocusLevel().getPowbName();
            }
          }
          if (climate.isEmpty()) {
            climate = "0-Not Targeted";
          }
        }

        /*
         * Geographic Scope
         */
        String geographicScope = "", region = "", country = "";

        // Geographic Scope
        try {

          // Setup Geographic Scope
          if (deliverable.getDeliverableGeographicScopes() != null) {
            deliverable.setGeographicScopes(new ArrayList<>(deliverable.getDeliverableGeographicScopes().stream()
              .filter(o -> o.isActive() && o.getPhase().getId() == this.getActualPhase().getId())
              .collect(Collectors.toList())));
          }

          // Deliverable Countries List
          if (deliverable.getDeliverableLocations() == null) {
            deliverable.setCountries(new ArrayList<>());
          } else {
            List<DeliverableLocation> countries = deliverableLocationManager
              .getDeliverableLocationbyPhase(deliverable.getId(), this.getActualPhase().getId());
            deliverable.setCountries(countries);
          }

          // Expected Study Geographic Regions List
          if (deliverable.getDeliverableGeographicRegions() != null
            && !deliverable.getDeliverableGeographicRegions().isEmpty()) {
            deliverable.setDeliverableRegions(new ArrayList<>(deliverableGeographicRegionManager
              .getDeliverableGeographicRegionbyPhase(deliverable.getId(), this.getActualPhase().getId()).stream()
              .filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 1)
              .collect(Collectors.toList())));
          }


          for (DeliverableGeographicScope dgs : deliverable.getGeographicScopes().stream()
            .filter(d -> d.getPhase() != null && d.getPhase().equals(this.getSelectedPhase())
              && d.getDeliverable().getDeliverableInfo(this.getSelectedPhase()) != null)
            .collect(Collectors.toList())) {
            geographicScope += "● " + dgs.getRepIndGeographicScope().getName() + "\n";
          }
          if (geographicScope.isEmpty()) {
            geographicScope = null;
          }

        } catch (Exception e) {

        }

        if (deliverable.getCountries() == null && deliverable.getDeliverableRegions() == null) {
          region = "<Not Applicable>";
          country = "<Not Applicable>";
        } else {
          // Regional
          if (deliverable.getDeliverableRegions() != null) {
            List<DeliverableGeographicRegion> deliverableRegions = deliverable.getDeliverableRegions();
            if (deliverableRegions != null && deliverableRegions.size() > 0) {
              Set<String> regionsSet = new HashSet<>();
              for (DeliverableGeographicRegion deliverableRegion : deliverableRegions) {
                regionsSet.add(deliverableRegion.getLocElement().getName());
              }
              region = String.join(", ", regionsSet);
            }
          } else {
            region = "<Not Defined>";
          }
          // Country
          if (deliverable.getCountries() != null) {
            List<DeliverableLocation> deliverableCountries = deliverable.getCountries();
            if (deliverableCountries != null && deliverableCountries.size() > 0) {
              Set<String> countriesSet = new HashSet<>();
              for (DeliverableLocation deliverableCountry : deliverableCountries) {
                countriesSet.add(deliverableCountry.getLocElement().getName());
              }
              country = String.join(", ", countriesSet);
            }
          } else {
            country = "<Not Defined>";
          }
        }


        String responsibleName = "";
        String responsibleAcronym = "";
        if (activePPAFilter) {
          addDeliverableRow = false;
        }
        // Store Institution and PartnerPerson
        String individual = "";
        // Store Institution
        String ppaResponsible = "";
        Set<String> ppaResponsibleList = new HashSet<>();
        LinkedHashSet<Institution> institutionsResponsibleList = new LinkedHashSet<>();

        // Get partner responsible
        List<DeliverableUserPartnership> partnershipsList = deliverable.getDeliverableUserPartnerships().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
            && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
          .collect(Collectors.toList());


        DeliverableUserPartnership responsible = null;

        // Set responible;
        if (partnershipsList != null && !partnershipsList.isEmpty()) {
          responsible = partnershipsList.get(0);
          if (responsible.getInstitution() != null) {
            institutionsResponsibleList.add(responsible.getInstitution());
          }
          if (responsible.getDeliverableUserPartnershipPersons() == null) {

            // get deliverable information when partner responsible does not have a person
            if (responsible.getInstitution() != null) {
              if (responsible.getInstitution().getAcronym() != null
                && !responsible.getInstitution().getAcronym().isEmpty()) {
                ppaResponsibleList.add("*" + responsible.getInstitution().getAcronym() + " ");
                responsibleAcronym = responsible.getInstitution().getAcronym() + " ";

              } else {
                ppaResponsibleList.add("*" + responsible.getInstitution().getName() + " ");
                responsibleName = responsible.getInstitution().getName() + " ";
              }
            }

          } else if (responsible.getDeliverableUserPartnershipPersons() != null) {
            // individual += "<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>";
            individual += "●  ";
            individual += "*";

            DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
            List<DeliverableUserPartnershipPerson> persons = responsible.getDeliverableUserPartnershipPersons().stream()
              .filter(dp -> dp.isActive()).collect(Collectors.toList());
            if (persons.size() > 0) {
              responsibleppp = persons.get(0);
            }


            // get deliverable information when partner responsible does not have a person
            if (responsible.getInstitution() != null) {
              if (responsible.getInstitution().getAcronym() != null
                && !responsible.getInstitution().getAcronym().isEmpty()) {
                ppaResponsibleList.add("*" + responsible.getInstitution().getAcronym() + " ");
                responsibleAcronym = responsible.getInstitution().getAcronym() + " ";

              } else {
                ppaResponsibleList.add("*" + responsible.getInstitution().getName() + " ");
                responsibleName = responsible.getInstitution().getName() + " ";
              }
            }

            if (responsibleppp.getUser() != null) {
              individual += responsibleppp.getUser().getComposedNameWithoutEmail();
            }
          }

        }


        // Get partner others
        List<DeliverableUserPartnership> othersPartnerships = deliverable.getDeliverableUserPartnerships().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
            && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_OTHER))
          .collect(Collectors.toList());

        if (othersPartnerships != null) {
          individual += "\n ● ";

          for (DeliverableUserPartnership deliverablePartnership : othersPartnerships) {
            if (deliverablePartnership.getInstitution() != null) {
              institutionsResponsibleList.add(deliverablePartnership.getInstitution());
            }
            if (deliverablePartnership.getDeliverableUserPartnershipPersons() != null) {


              List<DeliverableUserPartnershipPerson> responsibleppp =
                deliverablePartnership.getDeliverableUserPartnershipPersons().stream().filter(dp -> dp.isActive())
                  .collect(Collectors.toList());

              if (deliverablePartnership.getInstitution() != null) {
                if (deliverablePartnership.getInstitution().getAcronym() != null
                  && !deliverablePartnership.getInstitution().getAcronym().isEmpty()) {
                  ppaResponsibleList.add("*" + deliverablePartnership.getInstitution().getAcronym() + " ");
                  responsibleAcronym = deliverablePartnership.getInstitution().getAcronym() + " ";

                } else {
                  ppaResponsibleList.add("*" + deliverablePartnership.getInstitution().getName() + " ");
                  responsibleName = deliverablePartnership.getInstitution().getName() + " ";
                }
              }

              for (DeliverableUserPartnershipPerson person : responsibleppp) {
                if (person.getUser() != null) {
                  individual += person.getUser().getComposedName();
                }
              }

              individual += "\n● ";

            } else {

              if (deliverablePartnership.getInstitution() != null) {
                if (deliverablePartnership.getInstitution().getAcronym() != null
                  && !deliverablePartnership.getInstitution().getAcronym().isEmpty()) {
                  ppaResponsibleList.add("*" + deliverablePartnership.getInstitution().getAcronym() + " ");
                  responsibleAcronym = deliverablePartnership.getInstitution().getAcronym() + " ";

                } else {
                  ppaResponsibleList.add("*" + deliverablePartnership.getInstitution().getName() + " ");
                  responsibleName = deliverablePartnership.getInstitution().getName() + " ";
                }
              }

            }
          }
        }

        if (individual.isEmpty()) {
          individual = null;
        } else {
          if ((individual.length() > 4)
            && (individual.substring(individual.length() - 3, individual.length()).contains("● "))) {
            individual = individual.substring(0, individual.length() - 3);
          }
        }
        LinkedHashSet<Institution> managingResponsibleList = new LinkedHashSet<>();
        for (String ppaOher : ppaResponsibleList) {
          if (ppaResponsible.isEmpty()) {
            // ppaResponsible += "<span style='font-family: Segoe UI;font-size: 10'>" + ppaOher + "</span>";
            ppaResponsible += ppaOher + " ";
          } else {
            ppaResponsible += ppaOher + " ";
            // ppaResponsible += ", <span style='font-family: Segoe UI;font-size: 10'>" + ppaOher + "</span>";
          }
          ppaResponsible += ", ";
        }

        for (Institution partnerResponsible : institutionsResponsibleList) {
          // Check if is ppa
          if (partnerResponsible.isPPA(this.getLoggedCrp().getId(), this.getActualPhase())) {
            managingResponsibleList.add(partnerResponsible);
          } else {
            // If is not a ppa, get the crp linked to the partner
            List<ProjectPartner> projectPartners = deliverable
              .getProject().getProjectPartners().stream().filter(pp -> pp.isActive()
                && pp.getInstitution().equals(partnerResponsible) && pp.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList());
            if (projectPartners != null && projectPartners.size() > 0) {
              if (projectPartners.size() > 1) {
                LOG.warn("Two or more partners have the same institution for Project ("
                  + deliverable.getProject().toString() + ") and institution (" + partnerResponsible.toString() + ")");
              }
              ProjectPartner projectPartner = projectPartners.get(0);
              if (projectPartner.getProjectPartnerContributions() != null
                && projectPartner.getProjectPartnerContributions().size() > 0) {
                for (ProjectPartnerContribution projectPartnerContribution : projectPartner
                  .getProjectPartnerContributions().stream().filter(pc -> pc.isActive()).collect(Collectors.toList())) {
                  managingResponsibleList
                    .add(projectPartnerContribution.getProjectPartnerContributor().getInstitution());
                }
              }
            }
          }
        }

        if (ppaResponsible.isEmpty()) {
          ppaResponsible = null;
        } else {
          ppaResponsible = ppaResponsible.replaceAll("  ,", ",");
          ppaResponsible = ppaResponsible.replaceAll(",  ", ",");
          ppaResponsible = ppaResponsible.substring(0, ppaResponsible.length() - 2);
        }

        if (individual != null) {

          if (individual.charAt(0) == ',') {
            individual = individual.substring(1);
          }
          if (individual.charAt(1) == ',') {
            individual = individual.substring(2);
          }
        }


        String managingResponsible = "";
        CrpPpaPartner ppaFilter;
        if (activePPAFilter) {
          ppaFilter = crpPpaPartnerManager.getCrpPpaPartnerById(Long.parseLong(ppa));
        } else {
          ppaFilter = new CrpPpaPartner();
        }

        for (Institution managingInstitution : managingResponsibleList) {
          if (activePPAFilter) {
            if (managingInstitution.getId().equals(ppaFilter.getInstitution().getId())) {
              addDeliverableRow = true;
            }
          }

          String institution = "";
          if (managingInstitution.getAcronym() != null && !managingInstitution.getAcronym().trim().isEmpty()) {
            institution = managingInstitution.getAcronym();
          } else {
            institution = managingInstitution.getName();
          }
          String color = ";color:#000000";

          if (responsible != null && responsible.getInstitution() != null
            && responsible.getInstitution().getId().equals(managingInstitution.getId())) {
            color = ";color:#ff0000";
          } else {
            color = ";color:#ff0000";
            // TODO view in Summary
            // List<ProjectPartnerContribution> projectPartnerContributions = responsible.getProjectPartner()
            // .getProjectPartnerContributions().stream().filter(pc -> pc.isActive()).collect(Collectors.toList());
            // if (projectPartnerContributions != null && !projectPartnerContributions.isEmpty()) {
            // for (ProjectPartnerContribution projectPartnerContribution : projectPartnerContributions) {
            // if (projectPartnerContribution.getProjectPartner().equals(responsible.getProjectPartner())) {
            // color = ";color:#ff0000";
            // }
            // }
            // }
          }


          if (managingResponsible.isEmpty()) {
            /*
             * managingResponsible +=
             * "<span style='font-family: Segoe UI;font-size: 10" + color + "'>" + institution + "</span>";
             */
            managingResponsible += "*" + institution;
          } else {
            /*
             * managingResponsible +=
             * ", <span style='font-family: Segoe UI;font-size: 10" + color + "'>" + institution + "</span>";
             */
            managingResponsible += "*" + institution;
          }
        }
        if (managingResponsible.isEmpty()) {
          managingResponsible = null;
        }
        String shared = null;
        if (managingResponsibleList != null) {
          if (managingResponsibleList.size() == 0) {
            shared = "Not Defined";
          }
          if (managingResponsibleList.size() == 1) {
            shared = "No";
          }
          if (managingResponsibleList.size() > 1) {
            shared = "Yes";
          }
        }

        if (country == null || country == "" || country.isEmpty()) {
          country = "<Not Defined>";
        }


        model.addRow(new Object[] {deliverable.getId(), title, delivType, delivSubType, delivStatus, delivYear,
          keyOutput, outcomes, leader, fundingSources, delivNewYear, delivNewYearJustification,
          delivDisseminationChannel, delivDisseminationUrl, delivOpenAccess, delivLicense, titleMetadata,
          descriptionMetadata, dateMetadata, languageMetadata, countryMetadata, keywordsMetadata, citationMetadata,
          HandleMetadata, DOIMetadata, creatorAuthors, dataSharing, qualityAssurance, dataDictionary, tools, F, A, I, R,
          disseminated, restrictedAccess, delivLicenseModifications, volume, issue, pages, journal, journalIndicator,
          acknowledge, flContrib, projectID, projectTitle, flagships, regions, othersResponsibles, newExceptedFlag,
          phaseID, gender, youth, cap, geographicScope, region, country, status, isComplete, individual, ppaResponsible,
          managingResponsible, climate, justification, description});
      }
    }
    return model;
  }

  private TypedTableModel getDeliverablesPublicationsReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"publication_id", "title", "publication_sub_type", "deliv_year", "leader",
        "deliv_dissemination_channel", "deliv_dissemination_url", "deliv_open_access", "deliv_license", "titleMetadata",
        "descriptionMetadata", "dateMetadata", "languageMetadata", "countryMetadata", "keywordsMetadata",
        "citationMetadata", "HandleMetadata", "DOIMetadata", "creator_authors", "F", "A", "I", "R", "restricted_access",
        "deliv_license_modifications", "volume", "issue", "pages", "journal", "journal_indicators", "acknowledge",
        "fl_contrib", "flagships", "regions", "added_by", "phaseID", "gender", "youth", "cap", "keyOutput", "outcomes",
        "geographicScope", "region", "country", "fundingSources"},
      new Class[] {Long.class, String.class, String.class, Integer.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, Long.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class},
      0);


    if (!this.getLoggedCrp().getDeliverables().stream()
      .filter(d -> d.getIsPublication() != null && d.getIsPublication().booleanValue() && d.isActive()
        && d.getProject() == null && d.getDeliverableInfo(this.getSelectedPhase()) != null)
      .collect(Collectors.toList()).isEmpty()) {
      for (Deliverable deliverable : this.getLoggedCrp().getDeliverables().stream()
        .filter(d -> d.getIsPublication() != null && d.getIsPublication().booleanValue() && d.isActive()
          && d.getProject() == null && d.getDeliverableInfo(this.getSelectedPhase()) != null
          && d.getDeliverableInfo().isActive() && d.getDeliverableInfo().getYear() == this.getSelectedYear())
        .sorted((d1, d2) -> d1.getId().compareTo(d2.getId())).collect(Collectors.toList())) {

        Long publicationId = null;
        Integer delivYear = null;
        String title = null, publicationSubType = null, leader = null, delivDisseminationChannel = null,
          delivDisseminationUrl = null, delivOpenAccess = null, delivLicense = null, titleMetadata = null,
          descriptionMetadata = null, dateMetadata = null, languageMetadata = null, countryMetadata = null,
          keywordsMetadata = null, citationMetadata = null, HandleMetadata = null, DOIMetadata = null,
          creatorAuthors = "", F = null, A = null, I = null, R = null, restrictedAccess = null,
          delivLicenseModifications = null, volume = null, issue = null, pages = null, journal = null,
          journalIndicators = "", acknowledge = null, flContrib = "", flagships = null, regions = null, addedBy = null,
          keyOutput = "", outcomes = "";
        String fundingSources = "";
        publicationId = deliverable.getId();
        title = deliverable.getDeliverableInfo().getTitle();
        Long phaseID = deliverable.getDeliverableInfo().getPhase().getId();

        String delivStatus = deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase());
        Boolean showFAIR = false;
        Boolean showPublication = false;

        if (deliverable.getDeliverableInfo().getDeliverableType() != null) {
          publicationSubType = deliverable.getDeliverableInfo().getDeliverableType().getName();
          if (deliverable.getDeliverableInfo().getDeliverableType().getId() == 51
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 56
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 57
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 76
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 54
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 81
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 82
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 83
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 55
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 62
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 53
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 60
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 59
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 58
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 77
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 75
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 78
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 72
            || deliverable.getDeliverableInfo().getDeliverableType().getId() == 73) {
            showFAIR = true;
          }


          if (deliverable.getDeliverableInfo().getDeliverableType().getDeliverableCategory() != null) {
            // FAIR and deliverable publication
            if (deliverable.getDeliverableInfo().getDeliverableType().getDeliverableCategory().getId() == 49) {
              showFAIR = true;
              showPublication = true;
            }
          }
        }
        if (delivStatus.equals("")) {
          delivStatus = null;
        }
        if (deliverable.getDeliverableInfo().getYear() != 0) {
          delivYear = deliverable.getDeliverableInfo().getYear();
        }


        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive() && d.getPhase() != null && d.getPhase().equals(this.getSelectedPhase())
            && d.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null)
          .collect(Collectors.toList())) {
          String financialCode = "";
          if (dfs.getFundingSource().getFundingSourceInfo().getFinanceCode() != null) {
            financialCode = dfs.getFundingSource().getFundingSourceInfo().getFinanceCode();
          }
          fundingSources += "●  FS" + dfs.getFundingSource().getId() + " (" + financialCode + ") - "
            + dfs.getFundingSource().getFundingSourceInfo().getTitle() + "\n";
        }
        if (fundingSources.isEmpty()) {
          fundingSources = null;
        }

        // Get leaders
        if (!deliverable.getDeliverableLeaders().stream().collect(Collectors.toList()).isEmpty()) {
          for (DeliverableLeader deliverableLeader : deliverable.getDeliverableLeaders().stream()
            .filter(dl -> dl.isActive() && dl.getPhase() != null && dl.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
            if (leader == null || leader.isEmpty()) {
              leader = deliverableLeader.getInstitution().getComposedName();
            } else {
              leader += "\n" + deliverableLeader.getInstitution().getComposedName();
            }
          }
        }

        Boolean isDisseminated = false;
        Boolean showDelivLicenseModifications = false;

        if (deliverable.getDeliverableDisseminations().stream()
          .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList()).size() > 0
          && deliverable.getDeliverableDisseminations().stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList()).get(0) != null) {
          // Get deliverable dissemination
          DeliverableDissemination deliverableDissemination = deliverable.getDeliverableDisseminations().stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList()).get(0);

          if (deliverableDissemination.getAlreadyDisseminated() != null
            && deliverableDissemination.getAlreadyDisseminated() == true) {
            isDisseminated = true;
          }
          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationChannel() != null
              && !deliverableDissemination.getDisseminationChannel().isEmpty()) {
              RepositoryChannel repositoryChannel = repositoryChannelManager
                .getRepositoryChannelByShortName(deliverableDissemination.getDisseminationChannel());
              if (repositoryChannel != null) {
                delivDisseminationChannel = repositoryChannel.getName();
              }
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
                restrictedAccess = "Restricted Use AgreementOCS - Restricted access (if so, what are these periods?)";
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
        }

        for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getDeliverableMetadataElements()
          .stream().filter(dm -> dm.isActive() && dm.getMetadataElement() != null && dm.getPhase() != null
            && dm.getPhase().equals(this.getSelectedPhase()))
          .collect(Collectors.toList())) {

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

        for (DeliverableUser deliverableUser : deliverable.getDeliverableUsers().stream()
          .filter(du -> du.isActive() && du.getPhase() != null && du.getPhase().equals(this.getSelectedPhase()))
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
          if (deliverable.getDeliverablePublicationMetadatas().stream()
            .filter(dpm -> dpm.isActive() && dpm.getPhase() != null && dpm.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList()).size() > 0
            && deliverable.getDeliverablePublicationMetadatas().stream()
              .filter(dpm -> dpm.isActive() && dpm.getPhase() != null && dpm.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList()).get(0) != null) {
            DeliverablePublicationMetadata deliverablePublicationMetadata = deliverable
              .getDeliverablePublicationMetadatas().stream()
              .filter(dpm -> dpm.isActive() && dpm.getPhase() != null && dpm.getPhase().equals(this.getSelectedPhase()))
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

            for (DeliverableCrp deliverableCrp : deliverable.getDeliverableCrps().stream()
              .filter(dc -> dc.isActive() && dc.getPhase() != null && dc.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList())) {
              if (deliverableCrp.getCrpProgram() != null) {
                flContrib += "● " + deliverableCrp.getCrpProgram().getCrp().getAcronym().toUpperCase() + " - "
                  + deliverableCrp.getCrpProgram().getAcronym().toUpperCase() + "\n";
              } else {
                if (deliverableCrp.getGlobalUnit() != null) {
                  flContrib += "● " + deliverableCrp.getGlobalUnit().getName().toUpperCase() + "\n";
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
        if (deliverable.getDeliverablePrograms() != null) {
          for (DeliverableProgram deliverableProgram : deliverable.getDeliverablePrograms().stream()
            .filter(dp -> dp.getCrpProgram() != null && dp.getCrpProgram().isActive()
              && dp.getPhase().equals(this.getSelectedPhase()) && dp.getPhase() != null)
            .collect(Collectors.toList())) {
            Integer programType = deliverableProgram.getCrpProgram().getProgramType();
            if (deliverableProgram.getCrpProgram().getProgramType() > 0) {
              switch (programType) {
                case 1:
                  if (flagships == null || flagships.isEmpty()) {
                    flagships = deliverableProgram.getCrpProgram().getAcronym();
                  } else {
                    flagships += "\n " + deliverableProgram.getCrpProgram().getAcronym();
                  }
                  break;
                case 2:
                  if (regions == null || regions.isEmpty()) {
                    regions = deliverableProgram.getCrpProgram().getAcronym();
                  } else {
                    regions += "\n " + deliverableProgram.getCrpProgram().getAcronym();
                  }
                  break;
                default:
                  break;
              }
            }
          }
        }

        if (deliverable.getCreatedBy() != null) {
          addedBy = deliverable.getCreatedBy().getComposedName();
        }

        // Get cross_cutting dimension
        String crossCutting = "";
        String gender = "", youth = "", cap = "";
        Boolean isOldCrossCutting = this.getSelectedYear() < 2018;
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerGender = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 1, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerYouth = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 2, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerCapDev = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 3, this.getSelectedPhase().getId());
        DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerClimateChange = deliverableCrossCuttingMarkerManager
          .getDeliverableCrossCuttingMarkerId(deliverable.getId(), 4, this.getSelectedPhase().getId());

        if (isOldCrossCutting) {
          // Gender
          if (deliverableCrossCuttingMarkerGender != null) {
            if (deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getId() != 0
              && deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getId() != 4) {
              gender = deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName();
              String genderLevels = "";
              int countGenderLevel = 0;
              List<DeliverableGenderLevel> deliverableGenderLevels = deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive() && dgl.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
              if (deliverableGenderLevels == null || deliverableGenderLevels.isEmpty()) {
                genderLevels = "";
              } else {
                genderLevels += "Gender level(s): ";
                for (DeliverableGenderLevel dgl : deliverableGenderLevels) {
                  if (dgl.getGenderLevel() != 0.0) {
                    if (countGenderLevel == 0) {
                      genderLevels += genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription();
                    } else {
                      genderLevels += ", " + genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription();
                    }
                    countGenderLevel++;
                  }
                }
              }

              if (!genderLevels.isEmpty()) {
                if (gender.isEmpty()) {
                  gender = genderLevels;
                } else {
                  gender += "\n" + genderLevels;
                }
              }
              if (gender.isEmpty()) {
                gender = "Yes";
              }
            }
          }
          if (gender.isEmpty()) {
            gender = "No";
          }

          // Youth
          if (deliverableCrossCuttingMarkerYouth != null) {
            if (deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getId() != 0
              && deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getId() != 4) {
              youth = deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName();
              if (youth.isEmpty()) {
                youth = "Yes";
              }
            }
          }
          if (youth.isEmpty()) {
            youth = "No";
          }

          // Cap
          if (deliverableCrossCuttingMarkerCapDev != null) {
            if (deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getId() != 0
              && deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getId() != 4) {
              cap = deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName();
              if (cap.isEmpty()) {
                cap = "Yes";
              }
            }
          }
          if (cap.isEmpty()) {
            cap = "No";
          }

        } else {
          // Gender
          if (deliverableCrossCuttingMarkerGender != null) {
            if (deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel() != null
              && deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName() != null) {
              gender = deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName();
            }
          }
          if (gender.isEmpty()) {
            gender = "0-Not Targeted";
          }

          // Youth
          if (deliverableCrossCuttingMarkerYouth != null
            && deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel() != null
            && deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName() != null) {
            youth = deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName();
          }
          if (youth.isEmpty()) {
            youth = "0-Not Targeted";
          }

          // Cap
          if (deliverableCrossCuttingMarkerCapDev != null
            && deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel() != null
            && deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName() != null) {
            cap = deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName();
          }
          if (cap.isEmpty()) {
            cap = "0-Not Targeted";
          }
        }

        if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput() != null) {
          keyOutput += "• ";

          if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getCrpClusterOfActivity()
            .getCrpProgram() != null) {
            keyOutput += deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getCrpClusterOfActivity()
              .getCrpProgram().getAcronym() + " - ";
          }
          keyOutput += deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getKeyOutput();
          // Get Outcomes Related to the KeyOutput
          for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : deliverable.getDeliverableInfo()
            .getCrpClusterKeyOutput().getCrpClusterKeyOutputOutcomes().stream()
            .filter(
              ko -> ko.isActive() && ko.getCrpProgramOutcome() != null && ko.getCrpProgramOutcome().getPhase() != null
                && ko.getCrpProgramOutcome().getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
            outcomes += " • ";
            if (crpClusterKeyOutputOutcome.getCrpProgramOutcome().getCrpProgram() != null
              && !crpClusterKeyOutputOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym().isEmpty()) {
              outcomes += crpClusterKeyOutputOutcome.getCrpProgramOutcome().getCrpProgram().getAcronym() + " Outcome: ";
            }
            outcomes += crpClusterKeyOutputOutcome.getCrpProgramOutcome().getDescription() + "\n";
          }
        }
        if (keyOutput.isEmpty()) {
          keyOutput = null;
        }
        if (outcomes.isEmpty()) {
          outcomes = null;
        }

        /*
         * Geographic Scope
         */
        String geographicScope = "", region = "", country = "";

        // Geographic Scope
        if (deliverable.getDeliverableInfo().getGeographicScope() != null) {
          geographicScope = deliverable.getDeliverableInfo().getGeographicScope().getName();
          if (deliverable.getDeliverableInfo().getGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeGlobal())) {
            region = "<Not Applicable>";
            country = "<Not Applicable>";
          }
          // Regional
          if (deliverable.getDeliverableInfo().getGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {
            country = "<Not Applicable>";
            List<DeliverableGeographicRegion> deliverableRegions =
              deliverable.getDeliverableGeographicRegions().stream()
                .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
                .collect(Collectors.toList());
            if (deliverableRegions != null && deliverableRegions.size() > 0) {
              Set<String> regionsSet = new HashSet<>();
              for (DeliverableGeographicRegion deliverableRegion : deliverableRegions) {
                regionsSet.add(deliverableRegion.getLocElement().getName());
              }
              region = String.join(", ", regionsSet);
            }

          }
          // Country
          if (!deliverable.getDeliverableInfo().getGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeGlobal())
            && !deliverable.getDeliverableInfo().getGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {
            region = "<Not Applicable>";
            List<DeliverableLocation> deliverableCountries = deliverable.getDeliverableLocations().stream()
              .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList());
            if (deliverableCountries != null && deliverableCountries.size() > 0) {
              Set<String> countriesSet = new HashSet<>();
              for (DeliverableLocation deliverableCountry : deliverableCountries) {
                countriesSet.add(deliverableCountry.getLocElement().getName());
              }
              country = String.join(", ", countriesSet);
            }
          }
        }
        if (geographicScope.isEmpty()) {
          geographicScope = null;
        }
        if (region.isEmpty()) {
          region = null;
        }
        if (country.isEmpty()) {
          country = null;
        }

        model.addRow(new Object[] {publicationId, title, publicationSubType, delivYear, leader,
          delivDisseminationChannel, delivDisseminationUrl, delivOpenAccess, delivLicense, titleMetadata,
          descriptionMetadata, dateMetadata, languageMetadata, countryMetadata, keywordsMetadata, citationMetadata,
          HandleMetadata, DOIMetadata, creatorAuthors, F, A, I, R, restrictedAccess, delivLicenseModifications, volume,
          issue, pages, journal, journalIndicators, acknowledge, flContrib, flagships, regions, addedBy, phaseID,
          gender, youth, cap, keyOutput, outcomes, geographicScope, region, country, fundingSources});
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
    fileName.append("DeliverablesReportingSummary");
    if (showAllYears.equals("true")) {
      fileName.append("_AllYears");
    }
    fileName.append("-" + this.getLoggedCrp().getAcronym() + "-");
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
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible"},
      new Class[] {String.class, String.class, String.class, Boolean.class});
    model.addRow(new Object[] {center, date, year, this.hasProgramnsRegions()});
    return model;
  }

  public String getShowAllYears() {
    return showAllYears;
  }

  @Override
  public void prepare() throws Exception {
    try {
      Map<String, Parameter> parameters = this.getParameters();
      showAllYears = StringUtils.trim(parameters.get(APConstants.SUMMARY_DELIVERABLE_ALL_YEARS).getMultipleValues()[0]);
      ppa = StringUtils.trim(parameters.get(APConstants.SUMMARY_DELIVERABLE_PPA).getMultipleValues()[0]);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.SUMMARY_DELIVERABLE_ALL_YEARS
        + " parameter. Parameter will be set as false. Exception: " + e.getMessage());
      showAllYears = "false";
      ppa = "All";
    }
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

  private DeliverableUserPartnership responsiblePartner(Deliverable deliverable) {
    try {
      DeliverableUserPartnership partnership = deliverable.getDeliverableUserPartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
          && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
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

  public void setShowAllYears(String showAllYears) {
    this.showAllYears = showAllYears;
  }

}
