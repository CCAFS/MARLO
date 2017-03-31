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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.model.ChannelEnum;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderTypeEnum;
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
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
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
public class DeliverablesReportingExcelSummaryAction extends BaseAction implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(DeliverablesReportingExcelSummaryAction.class);
  private CrpManager crpManager;
  private CrpProgramManager programManager;
  private DeliverableManager deliverableManager;

  private Crp loggedCrp;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  private int year;

  @Inject
  public DeliverablesReportingExcelSummaryAction(APConfig config, CrpManager crpManager,
    ProjectHighligthManager projectHighLightManager, CrpProgramManager programManager,
    DeliverableManager deliverableManager) {
    super(config);
    this.crpManager = crpManager;
    this.programManager = programManager;
    this.deliverableManager = deliverableManager;
  }

  @Override
  public String execute() throws Exception {

    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    Resource reportResource =
      manager.createDirectly(this.getClass().getResource("/pentaho/deliverablesReporting.prpt"), MasterReport.class);

    MasterReport masterReport = (MasterReport) reportResource.getResource();
    String center = loggedCrp.getName();


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
    TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(year));
    sdf.addTable(masterQueryName, model);
    masterReport.setDataFactory(cdf);

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
      List<Deliverable> deliverables = new ArrayList<>(deliverableManager.findAll().stream()
        .filter(d -> d.isActive() && d.getProject() != null && d.getProject().isActive()
          && d.getProject().getReporting() != null && d.getProject().getReporting() && d.getProject().getCrp() != null
          && d.getProject().getCrp().getId().equals(this.loggedCrp.getId()) && d.getStatus() != null
          && ((d.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
            && (d.getYear() >= this.year
              || (d.getNewExpectedYear() != null && d.getNewExpectedYear().intValue() >= this.year)))
            || (d.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
              && (d.getNewExpectedYear() != null && d.getNewExpectedYear().intValue() == this.year))
            || (d.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())
              && (d.getYear() == this.year
                || (d.getNewExpectedYear() != null && d.getNewExpectedYear().intValue() == this.year))))
          && (d.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
            || d.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
            || d.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())))
        .collect(Collectors.toList()));

      deliverables.sort((p1, p2) -> p1.isRequieriedReporting(year).compareTo(p2.isRequieriedReporting(year)));

      HashSet<Deliverable> deliverablesHL = new HashSet<>();
      deliverablesHL.addAll(deliverables);
      deliverables.clear();
      deliverables.addAll(deliverablesHL);
      int i = 0;
      for (Deliverable deliverable : deliverables) {
        i++;
        // System.out.println(deliverable.getId());
        // System.out.println("#" + i);
        String deliv_type = null;
        String deliv_sub_type = null;
        String deliv_status = deliverable.getStatusName();
        Integer deliv_year = null;
        String key_output = "";
        String leader = null;
        String others_responsibles = null;
        String funding_sources = "";
        Boolean showFAIR = false;
        Boolean show_publication = false;
        Boolean showCompilance = false;
        String project_ID = null;
        String project_title = null;
        String flagships = null, regions = null;
        String others_reponsibles = null;

        if (deliverable.getProject() != null) {
          project_ID = deliverable.getProject().getId().toString();
          if (deliverable.getProject().getTitle() != null && !deliverable.getProject().getTitle().trim().isEmpty()) {
            project_title = deliverable.getProject().getTitle();
          }
        }


        if (deliverable.getDeliverableType() != null) {
          deliv_sub_type = deliverable.getDeliverableType().getName();
          if (deliverable.getDeliverableType().getId() == 51 || deliverable.getDeliverableType().getId() == 56
            || deliverable.getDeliverableType().getId() == 57 || deliverable.getDeliverableType().getId() == 76
            || deliverable.getDeliverableType().getId() == 54 || deliverable.getDeliverableType().getId() == 81
            || deliverable.getDeliverableType().getId() == 82 || deliverable.getDeliverableType().getId() == 83
            || deliverable.getDeliverableType().getId() == 55 || deliverable.getDeliverableType().getId() == 62
            || deliverable.getDeliverableType().getId() == 53 || deliverable.getDeliverableType().getId() == 60
            || deliverable.getDeliverableType().getId() == 59 || deliverable.getDeliverableType().getId() == 58
            || deliverable.getDeliverableType().getId() == 77 || deliverable.getDeliverableType().getId() == 75
            || deliverable.getDeliverableType().getId() == 78 || deliverable.getDeliverableType().getId() == 72
            || deliverable.getDeliverableType().getId() == 73) {
            showFAIR = true;
          }
          if (deliverable.getDeliverableType().getId() == 51 || deliverable.getDeliverableType().getId() == 74) {
            showCompilance = true;
          }

          if (deliverable.getDeliverableType().getDeliverableType() != null) {
            deliv_type = deliverable.getDeliverableType().getDeliverableType().getName();
            // FAIR and deliverable publication
            if (deliverable.getDeliverableType().getDeliverableType().getId() == 49) {
              showFAIR = true;
              show_publication = true;
            }
          }
        }
        if (deliv_status.equals("")) {
          deliv_status = null;
        }
        if (deliverable.getYear() != 0) {
          deliv_year = deliverable.getYear();
        }
        if (deliverable.getCrpClusterKeyOutput() != null) {
          key_output += "● ";

          if (deliverable.getCrpClusterKeyOutput().getCrpClusterOfActivity().getCrpProgram() != null) {
            key_output +=
              deliverable.getCrpClusterKeyOutput().getCrpClusterOfActivity().getCrpProgram().getAcronym() + " - ";
          }
          key_output += deliverable.getCrpClusterKeyOutput().getKeyOutput();
        }


        // Get partner responsible and institution
        // Set responible;
        DeliverablePartnership responisble = this.responsiblePartner(deliverable);

        if (responisble != null) {
          if (responisble.getProjectPartnerPerson() != null) {
            ProjectPartnerPerson responsibleppp = responisble.getProjectPartnerPerson();

            leader = responsibleppp.getUser().getComposedName();
            if (responsibleppp.getInstitution() != null && responsibleppp.getInstitution().getAcronym() != null) {
              leader += " - " + responsibleppp.getInstitution().getAcronym();
            }
          }
        }

        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive()).collect(Collectors.toList())) {
          funding_sources += "● " + dfs.getFundingSource().getTitle() + "\n";
        }
        if (funding_sources.isEmpty()) {
          funding_sources = null;
        }

        // Get cross_cutting dimension
        String cross_cutting = "";
        if (deliverable.getCrossCuttingNa() != null) {
          if (deliverable.getCrossCuttingNa() == true) {
            cross_cutting += "● N/A \n";
          }
        }
        if (deliverable.getCrossCuttingGender() != null) {
          if (deliverable.getCrossCuttingGender() == true) {
            cross_cutting += "● Gender \n";
          }
        }
        if (deliverable.getCrossCuttingYouth() != null) {
          if (deliverable.getCrossCuttingYouth() == true) {
            cross_cutting += "● Youth \n";
          }
        }
        if (deliverable.getCrossCuttingCapacity() != null) {
          if (deliverable.getCrossCuttingCapacity() == true) {
            cross_cutting += "● Capacity Development \n";
          }
        }

        if (deliverable.getCrossCuttingGender() != null) {
          if (deliverable.getCrossCuttingGender() == true) {
            if (deliverable.getDeliverableGenderLevels() == null
              || deliverable.getDeliverableGenderLevels().isEmpty()) {
              cross_cutting += "\nGender level(s):\n<Not Defined>";
            } else {
              cross_cutting += "\nGender level(s): \n";
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
                if (dgl.getGenderLevel() != 0.0) {
                  cross_cutting += "● " + DeliverableGenderTypeEnum.getValue(dgl.getGenderLevel()).getValue() + "\n";
                }
              }
            }
          }
        }
        if (cross_cutting.isEmpty()) {
          cross_cutting = null;
        }

        if (key_output.isEmpty()) {
          key_output = null;
        }

        // Reporting
        Integer deliv_new_year = null;
        String newExceptedFlag = "na";
        String deliv_new_year_justification = null;

        if (deliverable.getStatus() != null) {
          // Extended
          if (deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            deliv_new_year = deliverable.getNewExpectedYear();
            deliv_new_year_justification = deliverable.getStatusDescription();
            newExceptedFlag = "nd";
          }
          // Complete
          if (deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            deliv_new_year = deliverable.getNewExpectedYear();
            deliv_new_year_justification = "<Not applicable>";
            newExceptedFlag = "nd";
          }
          // Canceled
          if (deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
            deliv_new_year_justification = deliverable.getStatusDescription();
          }
        }


        String deliv_dissemination_channel = null;
        String deliv_dissemination_url = null;
        String deliv_open_access = null;
        String deliv_license = null;
        String deliv_license_modifications = null;
        Boolean isDisseminated = false;
        String disseminated = "No";
        String restricted_access = null;
        Boolean isRestricted = false;
        Boolean show_deliv_license_modifications = false;

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
                deliv_dissemination_channel =
                  ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()).getDesc();
              }
              // deliv_dissemination_channel = deliverableDissemination.getDisseminationChannel();
            }
          } else {
            deliv_dissemination_channel = "<Not applicable>";
          }

          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationUrl() != null
              && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
              deliv_dissemination_url = deliverableDissemination.getDisseminationUrl().replace(" ", "%20");
            }
          } else {
            deliv_dissemination_url = "<Not applicable>";
          }

          if (deliverableDissemination.getIsOpenAccess() != null) {
            if (deliverableDissemination.getIsOpenAccess() == true) {
              deliv_open_access = "Yes";
              restricted_access = "<Not applicable>";
            } else {
              // get the open access
              deliv_open_access = "No";
              isRestricted = true;
              if (deliverableDissemination.getIntellectualProperty() != null
                && deliverableDissemination.getIntellectualProperty() == true) {
                restricted_access = "Intellectual Property Rights (confidential information)";
              }

              if (deliverableDissemination.getLimitedExclusivity() != null
                && deliverableDissemination.getLimitedExclusivity() == true) {
                restricted_access = "Limited Exclusivity Agreements";
              }

              if (deliverableDissemination.getNotDisseminated() != null
                && deliverableDissemination.getNotDisseminated() == true) {
                restricted_access = "Not Disseminated";
              }

              if (deliverableDissemination.getRestrictedUseAgreement() != null
                && deliverableDissemination.getRestrictedUseAgreement() == true) {
                restricted_access = "Restricted Use Agreement - Restricted access (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedAccessUntil() != null) {
                  restricted_access +=
                    "\nRestricted access until: " + deliverableDissemination.getRestrictedAccessUntil();
                } else {
                  restricted_access += "\nRestricted access until: <Not Defined>";
                }
              }

              if (deliverableDissemination.getEffectiveDateRestriction() != null
                && deliverableDissemination.getEffectiveDateRestriction() == true) {
                restricted_access = "Effective Date Restriction - embargoed periods (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedEmbargoed() != null) {
                  restricted_access +=
                    "\nRestricted embargoed date: " + deliverableDissemination.getRestrictedEmbargoed();
                } else {
                  restricted_access += "\nRestricted embargoed date: <Not Defined>";
                }
              }
            }
          }

          if (deliverable.getAdoptedLicense() != null) {
            if (deliverable.getAdoptedLicense() == true) {
              deliv_license = deliverable.getLicense();
              if (deliv_license.equals("OTHER")) {
                deliv_license = deliverable.getOtherLicense();
                show_deliv_license_modifications = true;
                if (deliverable.getAllowModifications() != null && deliverable.getAllowModifications() == true) {
                  deliv_license_modifications = "Yes";
                } else {
                  deliv_license_modifications = "No";
                }
              } else {
                if (!show_deliv_license_modifications) {
                  deliv_license_modifications = "<Not applicable>";
                }
              }
            } else {
              deliv_license = "No";
            }
          }
        }

        if (deliv_license != null && deliv_license.isEmpty()) {
          deliv_license = null;
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

        String creator_authors = "";
        for (DeliverableUser deliverableUser : deliverable.getDeliverableUsers().stream().filter(du -> du.isActive())
          .collect(Collectors.toList())) {
          creator_authors += "\n● ";
          if (!deliverableUser.getLastName().isEmpty()) {
            creator_authors += deliverableUser.getLastName() + " - ";
          }
          if (!deliverableUser.getFirstName().isEmpty()) {
            creator_authors += deliverableUser.getFirstName();
          }
          if (!deliverableUser.getElementId().isEmpty()) {
            creator_authors += "<" + deliverableUser.getElementId() + ">";
          }
        }

        if (creator_authors.isEmpty()) {
          creator_authors = null;
        }

        String data_sharing = "";
        if (isDisseminated && (deliv_dissemination_channel != null && !deliv_dissemination_channel.equals("Other"))) {


          for (DeliverableDataSharingFile deliverableDataSharingFile : deliverable.getDeliverableDataSharingFiles()
            .stream().filter(ds -> ds.isActive()).collect(Collectors.toList())) {
            if (deliverableDataSharingFile.getExternalFile() != null
              && !deliverableDataSharingFile.getExternalFile().isEmpty()) {
              data_sharing += deliverableDataSharingFile.getExternalFile().replace(" ", "%20") + "\n";
            }

            if (deliverableDataSharingFile.getFile() != null && deliverableDataSharingFile.getFile().isActive()) {
              data_sharing += (this.getDeliverableDataSharingFilePath(project_ID)
                + deliverableDataSharingFile.getFile().getFileName()).replace(" ", "%20") + "\n";

            }
          }
          if (data_sharing.isEmpty()) {
            data_sharing = null;
          }
        } else {
          data_sharing = "<Not applicable>";
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
        String journal_indicators = "";
        String acknowledge = null;
        String fl_contrib = "";
        // Publication metadata
        // Verify if the deliverable is of type Articles and Books

        if (show_publication) {
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
              journal_indicators += "● This journal article is an ISI publication \n";
            }
            if (deliverablePublicationMetadata.getNasr() != null && deliverablePublicationMetadata.getNasr() == true) {
              journal_indicators +=
                "● This article have a co-author from a developing country National Agricultural Research System (NARS)\n";
            }
            if (deliverablePublicationMetadata.getCoAuthor() != null
              && deliverablePublicationMetadata.getCoAuthor() == true) {
              journal_indicators +=
                "● This article have a co-author based in an Earth System Science-related academic department";
            }
            if (journal_indicators.trim().isEmpty()) {
              journal_indicators = null;
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
                fl_contrib += "● " + deliverableCrp.getCrpPandr().getAcronym().toUpperCase() + " - "
                  + deliverableCrp.getIpProgram().getAcronym().toUpperCase() + "\n";
              } else {
                if (deliverableCrp.getCrpPandr() != null) {
                  fl_contrib += "● " + deliverableCrp.getCrpPandr().getName().toUpperCase() + "\n";
                }
              }
            }
          }
        } else {
          volume = "<Not applicable>";
          issue = "<Not applicable>";
          pages = "<Not applicable>";
          journal = "<Not applicable>";
          journal_indicators = "<Not applicable>";
          acknowledge = "<Not applicable>";
          fl_contrib = "<Not applicable>";
        }

        if (fl_contrib.trim().isEmpty()) {
          fl_contrib = null;
        }
        if (journal_indicators != null) {
          if (journal_indicators.trim().isEmpty()) {
            journal_indicators = null;
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
          if (deliverable.getProject().getNoRegional() != null && deliverable.getProject().getNoRegional()) {
            if (regions != null && !regions.isEmpty()) {
              LOG.warn("Project is global and has regions selected");
            }
            regions = "Global";
          }
        } else {
          regions = null;
        }


        model.addRow(
          new Object[] {deliverable.getId(), deliverable.getTitle().trim().isEmpty() ? null : deliverable.getTitle(),
            deliv_type, deliv_sub_type, deliv_status, deliv_year, key_output, leader, funding_sources, cross_cutting,
            deliv_new_year, deliv_new_year_justification, deliv_dissemination_channel, deliv_dissemination_url,
            deliv_open_access, deliv_license, titleMetadata, descriptionMetadata, dateMetadata, languageMetadata,
            countryMetadata, keywordsMetadata, citationMetadata, HandleMetadata, DOIMetadata, creator_authors,
            data_sharing, qualityAssurance, dataDictionary, tools, F, A, I, R, disseminated, restricted_access,
            deliv_license_modifications, volume, issue, pages, journal, journal_indicators, acknowledge, fl_contrib,
            project_ID, project_title, flagships, regions, others_responsibles, newExceptedFlag});
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


    if (!loggedCrp.getDeliverables().stream().filter(d -> d.getIsPublication() != null
      && d.getIsPublication().booleanValue() && d.isActive() && d.getProject() == null).collect(Collectors.toList())
      .isEmpty()) {
      for (Deliverable deliverable : loggedCrp.getDeliverables().stream().filter(d -> d.getIsPublication() != null
        && d.getIsPublication().booleanValue() && d.isActive() && d.getProject() == null)
        .collect(Collectors.toList())) {
        // System.out.println(deliverable.getId());
        // System.out.println("#" + i);
        Long publication_id = null;
        Integer deliv_year = null;
        String title = null, publication_sub_type = null, leader = null, cross_cutting = "",
          deliv_dissemination_channel = null, deliv_dissemination_url = null, deliv_open_access = null,
          deliv_license = null, titleMetadata = null, descriptionMetadata = null, dateMetadata = null,
          languageMetadata = null, countryMetadata = null, keywordsMetadata = null, citationMetadata = null,
          HandleMetadata = null, DOIMetadata = null, creator_authors = "", F = null, A = null, I = null, R = null,
          restricted_access = null, deliv_license_modifications = null, volume = null, issue = null, pages = null,
          journal = null, journal_indicators = "", acknowledge = null, fl_contrib = "", flagships = null,
          regions = null, added_by = null;
        publication_id = deliverable.getId();
        title = deliverable.getTitle();

        String deliv_status = deliverable.getStatusName();
        Boolean showFAIR = false;
        Boolean show_publication = false;

        if (deliverable.getDeliverableType() != null) {
          publication_sub_type = deliverable.getDeliverableType().getName();
          if (deliverable.getDeliverableType().getId() == 51 || deliverable.getDeliverableType().getId() == 56
            || deliverable.getDeliverableType().getId() == 57 || deliverable.getDeliverableType().getId() == 76
            || deliverable.getDeliverableType().getId() == 54 || deliverable.getDeliverableType().getId() == 81
            || deliverable.getDeliverableType().getId() == 82 || deliverable.getDeliverableType().getId() == 83
            || deliverable.getDeliverableType().getId() == 55 || deliverable.getDeliverableType().getId() == 62
            || deliverable.getDeliverableType().getId() == 53 || deliverable.getDeliverableType().getId() == 60
            || deliverable.getDeliverableType().getId() == 59 || deliverable.getDeliverableType().getId() == 58
            || deliverable.getDeliverableType().getId() == 77 || deliverable.getDeliverableType().getId() == 75
            || deliverable.getDeliverableType().getId() == 78 || deliverable.getDeliverableType().getId() == 72
            || deliverable.getDeliverableType().getId() == 73) {
            showFAIR = true;
          }


          if (deliverable.getDeliverableType().getDeliverableType() != null) {
            // FAIR and deliverable publication
            if (deliverable.getDeliverableType().getDeliverableType().getId() == 49) {
              showFAIR = true;
              show_publication = true;
            }
          }
        }
        if (deliv_status.equals("")) {
          deliv_status = null;
        }
        if (deliverable.getYear() != 0) {
          deliv_year = deliverable.getYear();
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
        if (deliverable.getCrossCuttingNa() != null) {
          if (deliverable.getCrossCuttingNa() == true) {
            cross_cutting += "● N/A \n";
          }
        }
        if (deliverable.getCrossCuttingGender() != null) {
          if (deliverable.getCrossCuttingGender() == true) {
            cross_cutting += "● Gender \n";
          }
        }
        if (deliverable.getCrossCuttingYouth() != null) {
          if (deliverable.getCrossCuttingYouth() == true) {
            cross_cutting += "● Youth \n";
          }
        }
        if (deliverable.getCrossCuttingCapacity() != null) {
          if (deliverable.getCrossCuttingCapacity() == true) {
            cross_cutting += "● Capacity Development \n";
          }
        }

        if (deliverable.getCrossCuttingGender() != null) {
          if (deliverable.getCrossCuttingGender() == true) {
            if (deliverable.getDeliverableGenderLevels() == null
              || deliverable.getDeliverableGenderLevels().isEmpty()) {
              cross_cutting += "\nGender level(s):\n<Not Defined>";
            } else {
              cross_cutting += "\nGender level(s): \n";
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
                if (dgl.getGenderLevel() != 0.0) {
                  cross_cutting += "● " + DeliverableGenderTypeEnum.getValue(dgl.getGenderLevel()).getValue() + "\n";
                }
              }
            }
          }
        }
        if (cross_cutting.isEmpty()) {
          cross_cutting = null;
        }

        Boolean isDisseminated = false;
        Boolean show_deliv_license_modifications = false;

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
                deliv_dissemination_channel =
                  ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()).getDesc();
              }
              // deliv_dissemination_channel = deliverableDissemination.getDisseminationChannel();
            }
          } else {
            deliv_dissemination_channel = "<Not applicable>";
          }

          if (isDisseminated) {
            if (deliverableDissemination.getDisseminationUrl() != null
              && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
              deliv_dissemination_url = deliverableDissemination.getDisseminationUrl().replace(" ", "%20");
            }
          } else {
            deliv_dissemination_url = "<Not applicable>";
          }

          if (deliverableDissemination.getIsOpenAccess() != null) {
            if (deliverableDissemination.getIsOpenAccess() == true) {
              deliv_open_access = "Yes";
              restricted_access = "<Not applicable>";
            } else {
              // get the open access
              deliv_open_access = "No";
              if (deliverableDissemination.getIntellectualProperty() != null
                && deliverableDissemination.getIntellectualProperty() == true) {
                restricted_access = "Intellectual Property Rights (confidential information)";
              }

              if (deliverableDissemination.getLimitedExclusivity() != null
                && deliverableDissemination.getLimitedExclusivity() == true) {
                restricted_access = "Limited Exclusivity Agreements";
              }

              if (deliverableDissemination.getNotDisseminated() != null
                && deliverableDissemination.getNotDisseminated() == true) {
                restricted_access = "Not Disseminated";
              }

              if (deliverableDissemination.getRestrictedUseAgreement() != null
                && deliverableDissemination.getRestrictedUseAgreement() == true) {
                restricted_access = "Restricted Use Agreement - Restricted access (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedAccessUntil() != null) {
                  restricted_access +=
                    "\nRestricted access until: " + deliverableDissemination.getRestrictedAccessUntil();
                } else {
                  restricted_access += "\nRestricted access until: <Not Defined>";
                }
              }

              if (deliverableDissemination.getEffectiveDateRestriction() != null
                && deliverableDissemination.getEffectiveDateRestriction() == true) {
                restricted_access = "Effective Date Restriction - embargoed periods (if so, what are these periods?)";
                if (deliverableDissemination.getRestrictedEmbargoed() != null) {
                  restricted_access +=
                    "\nRestricted embargoed date: " + deliverableDissemination.getRestrictedEmbargoed();
                } else {
                  restricted_access += "\nRestricted embargoed date: <Not Defined>";
                }
              }
            }
          }

          if (deliverable.getAdoptedLicense() != null) {
            if (deliverable.getAdoptedLicense() == true) {
              if (deliverable.getLicense() != null && !deliverable.getLicense().isEmpty()) {
                deliv_license = deliverable.getLicense();
              } else {
                deliv_license = "No";
              }
              if (deliv_license.equals("OTHER")) {
                deliv_license = deliverable.getOtherLicense();
                show_deliv_license_modifications = true;
                if (deliverable.getAllowModifications() != null && deliverable.getAllowModifications() == true) {
                  deliv_license_modifications = "Yes";
                } else {
                  deliv_license_modifications = "No";
                }
              } else {
                if (!show_deliv_license_modifications) {
                  deliv_license_modifications = "<Not applicable>";
                }
              }
            } else {
              deliv_license = "No";
            }
          }
        }

        if (deliv_license != null && deliv_license.isEmpty()) {
          deliv_license = null;
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
          creator_authors += "\n● ";
          if (!deliverableUser.getLastName().isEmpty()) {
            creator_authors += deliverableUser.getLastName() + " - ";
          }
          if (!deliverableUser.getFirstName().isEmpty()) {
            creator_authors += deliverableUser.getFirstName();
          }
          if (!deliverableUser.getElementId().isEmpty()) {
            creator_authors += "<" + deliverableUser.getElementId() + ">";
          }
        }

        if (creator_authors.isEmpty()) {
          creator_authors = null;
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

        if (show_publication) {
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
              journal_indicators += "● This journal article is an ISI publication \n";
            }
            if (deliverablePublicationMetadata.getNasr() != null && deliverablePublicationMetadata.getNasr() == true) {
              journal_indicators +=
                "● This article have a co-author from a developing country National Agricultural Research System (NARS)\n";
            }
            if (deliverablePublicationMetadata.getCoAuthor() != null
              && deliverablePublicationMetadata.getCoAuthor() == true) {
              journal_indicators +=
                "● This article have a co-author based in an Earth System Science-related academic department";
            }
            if (journal_indicators.trim().isEmpty()) {
              journal_indicators = null;
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
                fl_contrib += "● " + deliverableCrp.getCrpPandr().getAcronym().toUpperCase() + " - "
                  + deliverableCrp.getIpProgram().getAcronym().toUpperCase() + "\n";
              } else {
                if (deliverableCrp.getCrpPandr() != null) {
                  fl_contrib += "● " + deliverableCrp.getCrpPandr().getName().toUpperCase() + "\n";
                }
              }
            }
          }
        } else {
          volume = "<Not applicable>";
          issue = "<Not applicable>";
          pages = "<Not applicable>";
          journal = "<Not applicable>";
          journal_indicators = "<Not applicable>";
          acknowledge = "<Not applicable>";
          fl_contrib = "<Not applicable>";
        }

        if (fl_contrib.trim().isEmpty()) {
          fl_contrib = null;
        }
        if (journal_indicators != null) {
          if (journal_indicators.trim().isEmpty()) {
            journal_indicators = null;
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
          added_by = deliverable.getCreatedBy().getComposedName();
        }

        model.addRow(new Object[] {publication_id, title, publication_sub_type, deliv_year, leader, cross_cutting,
          deliv_dissemination_channel, deliv_dissemination_url, deliv_open_access, deliv_license, titleMetadata,
          descriptionMetadata, dateMetadata, languageMetadata, countryMetadata, keywordsMetadata, citationMetadata,
          HandleMetadata, DOIMetadata, creator_authors, F, A, I, R, restricted_access, deliv_license_modifications,
          volume, issue, pages, journal, journal_indicators, acknowledge, fl_contrib, flagships, regions, added_by});
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
    fileName.append(this.year + "_");
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

  public String getHighlightsImagesUrl(String project_id) {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath(project_id).replace('\\', '/');
  }


  public String getHighlightsImagesUrlPath(String project_id) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project_id + File.separator
      + "hightlightsImage" + File.separator;
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

  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible"},
      new Class[] {String.class, String.class, String.class, Boolean.class});
    // Verify if the crp has regions avalaible
    List<CrpParameter> hasRegionsList = new ArrayList<>();

    model.addRow(new Object[] {center, date, year, this.hasProgramnsRegions()});
    return model;
  }

  @Override
  public void prepare() throws Exception {
    try {
      loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    } catch (Exception e) {
    }

    // Get parameters from URL
    // Get year
    try {
      Map<String, Object> parameters = this.getParameters();
      year = Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0])));
    } catch (Exception e) {
      year = this.getCurrentCycleYear();
    }
  }

  private DeliverablePartnership responsiblePartner(Deliverable deliverable) {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
    } catch (Exception e) {
      return null;
    }

  }

  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }
}
