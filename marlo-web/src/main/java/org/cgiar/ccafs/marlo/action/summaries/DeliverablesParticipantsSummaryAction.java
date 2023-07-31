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
import org.cgiar.ccafs.marlo.data.manager.DeliverableClusterParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLocationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableClusterParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicScope;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  private final DeliverableGeographicRegionManager deliverableGeographicRegionManager;
  private final DeliverableLocationManager deliverableLocationManager;
  private final DeliverableClusterParticipantManager deliverableClusterParticipantManager;

  // Parameters
  private byte[] bytesXLSX;
  private String showAllYears;
  private long startTime;
  private InputStream inputStream;

  public DeliverablesParticipantsSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager, ResourceManager resourceManager, DeliverableManager deliverableManager,
    DeliverableGeographicRegionManager deliverableGeographicRegionManager,
    DeliverableLocationManager deliverableLocationManager,
    DeliverableClusterParticipantManager deliverableClusterParticipantManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.deliverableManager = deliverableManager;
    this.deliverableGeographicRegionManager = deliverableGeographicRegionManager;
    this.deliverableLocationManager = deliverableLocationManager;
    this.deliverableClusterParticipantManager = deliverableClusterParticipantManager;
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
    masterReport.getParameterValues().put("i8nColumnI", this.getText("deliverable.geographicScope"));
    masterReport.getParameterValues().put("i8nColumnJ", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nColumnK", this.getText("deliverable.countries"));
    masterReport.getParameterValues().put("i8nColumnL", this.getText("involveParticipants.title"));
    masterReport.getParameterValues().put("i8nColumnM", this.getText("involveParticipants.typeActivity"));
    masterReport.getParameterValues().put("i8nColumnN", this.getText("involveParticipants.academicDegree"));
    masterReport.getParameterValues().put("i8nColumnO", this.getText("involveParticipants.participants"));
    masterReport.getParameterValues().put("i8nColumnP", this.getText("involveParticipants.estimate.participant"));
    masterReport.getParameterValues().put("i8nColumnQ", this.getText("involveParticipants.females"));
    masterReport.getParameterValues().put("i8nColumnR", this.getText("involveParticipants.estimate.female"));
    masterReport.getParameterValues().put("i8nColumnS", this.getText("involveParticipants.participantsType"));
    masterReport.getParameterValues().put("i8nColumnT", this.getText("involveParticipants.trainingPeriod"));
    masterReport.getParameterValues().put("i8nColumnU", this.getText("involveParticipants.performanceIndicator"));

    // Clusters participant information
    masterReport.getParameterValues().put("i8nColumnV", "Clusters");
    masterReport.getParameterValues().put("i8nColumnW",
      this.getText("annualReport2018.ccDimensions.table7.numberTrainnees"));
    masterReport.getParameterValues().put("i8nColumnX", this.getText("involveParticipants.females"));
    masterReport.getParameterValues().put("i8nColumnY", this.getText("involveParticipants.african"));
    masterReport.getParameterValues().put("i8nColumnZ", this.getText("involveParticipants.youth"));
    masterReport.getParameterValues().put("i8nColumnAA", "Clusters contribution to this activity");

    masterReport.getParameterValues().put("i8nHeader",
      this.getLoggedCrp().getAcronym() + " " + this.getSelectedPhase().getName() + " " + this.getSelectedYear() + " "
        + this.getText("summaries.deliverable.participants"));

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
     * paramI - Geographic Scopes
     * paramJ - Regions
     * paramK - Countries
     * paramL - Event/Activity name
     * paramM - Type of Activity
     * paramN - Academic Degree (Possible <Not applicable>)
     * paramO - Total number of participants
     * paramP - Total estimate
     * paramQ - Number of females (Possible <Not applicable>)
     * paramR - Female Estimate (Possible <Not applicable>)
     * paramS - Type of Participant(s)
     * paramT - Training period of time
     * paramU - Performance Indicator
     * paramV - Clusters shared
     * paramV - Participants shared
     * paramX - Females shared
     * paramY - Africans shared
     * paramZ - Youth shared
     * deliverableURL
     * NOTE : does not mater the order into the implementation (ex: the paramO will be setup first that the paramA)
     */
    TypedTableModel model = new TypedTableModel(
      new String[] {"paramA", "paramB", "paramC", "paramD", "paramE", "paramF", "paramG", "paramH", "paramI", "paramJ",
        "paramK", "paramL", "paramM", "paramN", "paramO", "paramP", "paramQ", "paramR", "paramS", "paramT",
        "deliverableURL", "paramU", "indicatorURL", "africansNumber", "africansEstimate", "youthNumber",
        "youthEstimate", "eventFocus", "likelyOutcomes", "clusters", "numberTraineesShared", "numberFemaleShared",
        "numberAfricanShared", "numberYouthShared"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);

    List<Deliverable> deliverables = new ArrayList<Deliverable>();
    if (showAllYears.equals("true")) {
      deliverables = deliverableManager.getDeliverablesByParameters(this.getSelectedPhase(), false, true, null);
    } else {
      deliverables = deliverableManager.getDeliverablesByParameters(this.getSelectedPhase(), true, true, null);
    }

    if (deliverables != null && !deliverables.isEmpty()) {
      for (Deliverable deliverable : deliverables) {
        String paramA = null, paramB = null, paramC = null, paramD = null, paramE = null, paramF = null, paramG = null,
          paramH = null, paramI = null, paramJ = null, paramK = null, paramL = null, paramM = null, paramN = null,
          paramO = null, paramP = null, paramQ = null, paramR = null, paramS = null, paramT = null, paramU = null,
          deliverableURL = null, indicatorURL = null, africansNumber = null, africansEstimate = null,
          youthNumber = null, youthEstimate = null, eventFocus = null, likelyOutcomes = null, clustersShared = null,
          traineesShared = null, femalesShared = null, africansShared = null, youthShared = null;

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

          boolean haveRegions = false;
          boolean haveCountries = false;

          // paramI - Geographic Scopes
          if (deliverable.getDeliverableGeographicScopes() != null) {
            List<DeliverableGeographicScope> deliverableGeographicScopes = new ArrayList<>(deliverable
              .getDeliverableGeographicScopes().stream()
              .filter(dg -> dg.isActive() && dg.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
            Set<String> geographicScopeSet = new HashSet<>();
            if (deliverableGeographicScopes != null) {
              for (DeliverableGeographicScope dgs : deliverableGeographicScopes) {
                geographicScopeSet.add(dgs.getRepIndGeographicScope().getName());

                if (dgs.getRepIndGeographicScope().getId() == 2) {
                  haveRegions = true;
                }

                if (dgs.getRepIndGeographicScope().getId() != 1 && dgs.getRepIndGeographicScope().getId() != 2) {
                  haveCountries = true;
                }
              }
            }
            paramI = String.join(", ", geographicScopeSet);
          }

          // paramJ - Regions
          if (haveRegions) {
            List<DeliverableGeographicRegion> deliverableGeographicRegions =
              new ArrayList<>(deliverableGeographicRegionManager
                .getDeliverableGeographicRegionbyPhase(deliverable.getId(), this.getActualPhase().getId()).stream()
                .filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 1)
                .collect(Collectors.toList()));
            Set<String> geographicRegionSet = new HashSet<>();
            if (deliverableGeographicRegions != null) {
              for (DeliverableGeographicRegion dgr : deliverableGeographicRegions) {
                geographicRegionSet.add(dgr.getLocElement().getName());
              }
              paramJ = String.join(", ", geographicRegionSet);
            }
          } else {
            paramJ = "<Not Applicable>";
          }


          // paramK - Countries
          if (haveCountries) {

            List<DeliverableLocation> deliverableGeographicCountries = new ArrayList<>(deliverableLocationManager
              .getDeliverableLocationbyPhase(deliverable.getId(), this.getActualPhase().getId()));
            Set<String> geographicCountrySet = new HashSet<>();
            if (deliverableGeographicCountries != null) {
              for (DeliverableLocation dl : deliverableGeographicCountries) {
                geographicCountrySet.add(dl.getLocElement().getName());
              }
              paramK = String.join(", ", geographicCountrySet);
            }
          } else {
            paramK = "<Not Applicable>";
          }


          // Deliverable Participant
          DeliverableParticipant deliverableParticipant = deliverable.getDeliverableParticipants().stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(this.getSelectedPhase())
              && ds.getHasParticipants() != null && ds.getHasParticipants())
            .collect(Collectors.toList()).get(0);
          // paramL - Event/Activity name
          if (deliverableParticipant.getEventActivityName() != null
            && !deliverableParticipant.getEventActivityName().isEmpty()) {
            paramL = deliverableParticipant.getEventActivityName();
          }

          if (deliverableParticipant.getRepIndTypeActivity() != null
            && deliverableParticipant.getRepIndTypeActivity().getName() != null
            && !deliverableParticipant.getRepIndTypeActivity().getName().isEmpty()) {

            // paramM - Type of Activity
            paramM = deliverableParticipant.getRepIndTypeActivity().getName();

            if (deliverableParticipant.getRepIndTypeActivity().getId()
              .equals(this.getReportingIndTypeActivityAcademicDegree())) {
              // paramN - Academic Degree
              if (deliverableParticipant.getAcademicDegree() != null
                && !deliverableParticipant.getAcademicDegree().isEmpty()) {
                paramN = deliverableParticipant.getAcademicDegree();
              }
            } else {
              paramN = "<Not Applicable>";
            }

            if (deliverableParticipant.getRepIndTypeActivity().getIsFormal()) {
              // paramT - Training period of time
              if (deliverableParticipant.getRepIndTrainingTerm() != null) {
                paramT = deliverableParticipant.getRepIndTrainingTerm().getName();
              }
            } else {
              paramT = "<Not Applicable>";
            }
          }


          // paramO - Total number of participants
          if (deliverableParticipant.getParticipants() != null) {
            paramO = deliverableParticipant.getParticipants() + "";
          }
          // paramP - Total estimate
          if (deliverableParticipant.getEstimateParticipants() != null) {
            if (deliverableParticipant.getEstimateParticipants()) {
              paramP = "Yes";
            } else {
              paramP = "No";
            }
          }
          /*
           * if (deliverableParticipant.getDontKnowFemale() != null && deliverableParticipant.getDontKnowFemale()) {
           * // paramQ - Number of females (Possible <Not applicable>)
           * paramQ = "<Not Applicable>";
           * // paramR - Female Estimate (Possible <Not applicable>)
           * paramR = "<Not Applicable>";
           * } else {
           */
          // paramQ - Number of females (Possible <Not applicable>)
          if (deliverableParticipant.getFemales() != null) {
            paramQ = deliverableParticipant.getFemales() + "";
          }
          // paramR - Female Estimate (Possible <Not applicable>)
          if (deliverableParticipant.getEstimateFemales() != null) {
            if (deliverableParticipant.getEstimateFemales()) {
              paramR = "Yes";
            } else {
              paramR = "No";
            }
          }
          // Number of africans (Possible <Not applicable>)
          if (deliverableParticipant.getAfrican() != null) {
            africansNumber = deliverableParticipant.getAfrican() + "";
          }
          // africans Estimate (Possible <Not applicable>)
          if (deliverableParticipant.getEstimateAfrican() != null) {
            if (deliverableParticipant.getEstimateAfrican()) {
              africansEstimate = "Yes";
            } else {
              africansEstimate = "No";
            }
          }
          // Number of youth (Possible <Not applicable>)
          if (deliverableParticipant.getYouth() != null) {
            youthNumber = deliverableParticipant.getYouth() + "";
          }
          // youth Estimate (Possible <Not applicable>)
          if (deliverableParticipant.getEstimateYouth() != null) {
            if (deliverableParticipant.getEstimateYouth()) {
              youthEstimate = "Yes";
            } else {
              youthEstimate = "No";
            }
          }

          // Event Focus
          if (deliverableParticipant.getFocus() != null) {
            eventFocus = deliverableParticipant.getFocus();
          }

          // Likely Outcomes
          if (deliverableParticipant.getLikelyOutcomes() != null) {
            likelyOutcomes = deliverableParticipant.getLikelyOutcomes();
          }
          // }
          // paramS - Type of Participant(s)
          if (deliverableParticipant.getRepIndTypeParticipant() != null) {
            paramS = deliverableParticipant.getRepIndTypeParticipant().getName();
          }

          // paramU - Performance Indicator
          if (deliverable.getDeliverableCrpOutcomes() != null) {
            deliverable.setCrpOutcomes(new ArrayList<>(deliverable.getDeliverableCrpOutcomes().stream()
              .filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList())));
          }

          try {
            if (deliverable != null && deliverable.getCrpOutcomes() != null) {
              for (DeliverableCrpOutcome deliverableOutcomes : deliverable.getCrpOutcomes()) {
                if (deliverableOutcomes != null && deliverableOutcomes.getCrpProgramOutcome() != null
                  && deliverableOutcomes.getCrpProgramOutcome().getAcronym() != null) {
                  if (paramU == null) {
                    paramU = deliverableOutcomes.getCrpProgramOutcome().getAcronym();
                  } else {
                    paramU += "; " + deliverableOutcomes.getCrpProgramOutcome().getAcronym();
                  }
                }
              }

              /*
               * if (deliverable.getProjectOutcomes().get(0).getProjectOutcome().getId() != null) {
               * indicatorURL = this.getBaseUrl() + "/clusters/" + this.getSelectedPhase().getCrp().getAcronym()
               * + "/contributionCrp.do?projectOutcomeID="
               * + deliverable.getProjectOutcomes().get(0).getProjectOutcome().getId() + "&phaseID="
               * + this.getSelectedPhase().getId();
               * }
               */
            }
          } catch (Exception e) {
            LOG.warn("Error getting deliverables project outcomes" + e.getMessage());
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

          // Deliverables shared clusters
          List<DeliverableClusterParticipant> participantList = new ArrayList<>();
          participantList = deliverableClusterParticipantManager.getDeliverableClusterParticipantByDeliverableAndPhase(
            deliverable.getId(), this.getSelectedPhase().getId());

          if (participantList != null && !participantList.isEmpty()) {
            for (DeliverableClusterParticipant participant : participantList) {
              String clusterName = "";
              if (participant != null && participant.getProject() != null) {
                if (participant.getProject().getAcronym() != null && !participant.getProject().getAcronym().isEmpty()) {
                  clusterName = participant.getProject().getAcronym();
                } else {
                  clusterName = participant.getProject().getId() + "";
                }

                if (participant.getAfrican() != null) {
                  if (africansShared == null) {
                    africansShared = clusterName + ": " + participant.getAfrican() + "";
                  } else {
                    africansShared += "; " + clusterName + ": " + participant.getAfrican() + "";
                  }
                }

                if (participant.getFemales() != null) {
                  if (femalesShared == null) {
                    femalesShared = clusterName + ": " + participant.getFemales() + "";
                  } else {
                    femalesShared += "; " + clusterName + ": " + participant.getFemales() + "";
                  }
                }

                if (participant.getYouth() != null) {
                  if (youthShared == null) {
                    youthShared = clusterName + ": " + participant.getYouth() + "";
                  } else {
                    youthShared += "; " + clusterName + ": " + participant.getYouth() + "";
                  }
                }

                if (participant.getParticipants() != null) {
                  if (traineesShared == null) {
                    traineesShared = clusterName + ": " + participant.getParticipants() + "";
                  } else {
                    traineesShared += "; " + clusterName + ": " + participant.getParticipants() + "";
                  }
                }

                if (clustersShared == null) {
                  clustersShared = clusterName + "";
                } else {
                  clustersShared.concat("; " + clusterName);
                }
              }

            }
          }

          model.addRow(new Object[] {paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ,
            paramK, paramL, paramM, paramN, paramO, paramP, paramQ, paramR, paramS, paramT, deliverableURL, paramU,
            indicatorURL, africansNumber, africansEstimate, youthNumber, youthEstimate, eventFocus, likelyOutcomes,
            clustersShared, traineesShared, femalesShared, africansShared, youthShared});
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
