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
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.ibm.icu.util.Calendar;
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
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

/**
 * ExpectedDeliverablesSummaryAction:
 * 
 * @author avalencia - CCAFS
 * @date Nov 2, 2017
 * @time 9:13:34 AM: Added a new column to masterList called Project Managing Partners
 * @date Nov 23, 2017
 * @time 9:24:44 AM: Add project partner leader and managing responsible
 */
public class ExpectedDeliverablesSummaryAction extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(ExpectedDeliverablesSummaryAction.class);
  // Parameters
  private long startTime;
  private Set<Deliverable> currentPhaseDeliverables = new HashSet<>();
  private String ppa;
  // Store deliverables with year and type HashMap<Deliverable, List<year, type>>
  HashMap<Integer, Set<Deliverable>> deliverablePerYearList = new HashMap<Integer, Set<Deliverable>>();
  HashMap<String, Set<Deliverable>> deliverablePerTypeList = new HashMap<String, Set<Deliverable>>();
  Set<Long> projectsList = new HashSet<Long>();

  // Managers
  private final GenderTypeManager genderTypeManager;
  private final CrpProgramManager crpProgramManager;
  private final CrossCuttingScoringManager crossCuttingScoringManager;
  private final CrpPpaPartnerManager crpPpaPartnerManager;
  private final ResourceManager resourceManager;
  private final DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager;

  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;


  @Inject
  public ExpectedDeliverablesSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    GenderTypeManager genderTypeManager, CrpProgramManager crpProgramManager,
    CrossCuttingScoringManager crossCuttingScoringManager, CrpPpaPartnerManager crpPpaPartnerManager,
    ResourceManager resourceManager, ProjectManager projectManager,
    DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.genderTypeManager = genderTypeManager;
    this.crpProgramManager = crpProgramManager;
    this.crossCuttingScoringManager = crossCuttingScoringManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.resourceManager = resourceManager;
    this.deliverableCrossCuttingMarkerManager = deliverableCrossCuttingMarkerManager;
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
      this.getText("summaries.deliverable.deliverableTitle"));
    masterReport.getParameterValues().put("i8nDeliverableDescription",
      this.getText("summaries.deliverable.deliverableDescription"));
    masterReport.getParameterValues().put("i8nKeyOutput",
      this.getText("project.deliverable.generalInformation.keyOutput"));
    masterReport.getParameterValues().put("i8nExpectedYear", this.getText("summaries.deliverable.expectedYear"));
    masterReport.getParameterValues().put("i8nType", this.getText("deliverable.type"));
    masterReport.getParameterValues().put("i8nSubType", this.getText("deliverable.subtype"));
    masterReport.getParameterValues().put("i8nGender", this.getText("summaries.gender"));
    masterReport.getParameterValues().put("i8nYouth", this.getText("summaries.youth"));
    masterReport.getParameterValues().put("i8nCap", this.getText("summaries.capacityDevelopment"));
    masterReport.getParameterValues().put("i8nStatus", this.getText("project.deliverable.generalInformation.status"));
    masterReport.getParameterValues().put("i8nProjectID", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nCoas", this.getText("deliverable.coas"));
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nRegions", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nIndividual", this.getText("deliverable.individual"));
    masterReport.getParameterValues().put("i8nPartnersResponsible", this.getText("deliverable.managing"));
    masterReport.getParameterValues().put("i8nShared", this.getText("deliverable.shared"));
    masterReport.getParameterValues().put("i8nOpenFundingSourcesID",
      this.getText("summaries.fundingSource.openFundingSource"));
    masterReport.getParameterValues().put("i8nFinishedFundingSourcesID",
      this.getText("summaries.fundingSource.finishedFundingSource"));
    masterReport.getParameterValues().put("i8nFundingWindows", this.getText("deliverable.fundingWindows"));
    masterReport.getParameterValues().put("i8nNewExpectedYear", this.getText("deliverable.newExpectedYear"));
    masterReport.getParameterValues().put("i8nOutcomes", this.getText("impactPathway.menu.hrefOutcomes"));
    masterReport.getParameterValues().put("i8nManagingResponsible", this.getText("deliverable.project.managing"));
    masterReport.getParameterValues().put("i8nProjectLeadPartner", this.getText("summaries.deliverable.leadPartner"));
    masterReport.getParameterValues().put("i8nGeographicScope", this.getText("deliverable.geographicScope"));
    masterReport.getParameterValues().put("i8nCountry", this.getText("deliverable.countries"));
    masterReport.getParameterValues().put("i8nRegion", this.getText("deliverable.region"));


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
        .createDirectly(this.getClass().getResource("/pentaho/crp/ExpectedDeliverables.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();


      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel();
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
      this.fillSubreport((SubReport) hm.get("details"), "details");
      masterReport.getParameterValues().put("total_deliv", currentPhaseDeliverables.size());
      masterReport.getParameterValues().put("total_projects", projectsList.size());
      this.fillSubreport((SubReport) hm.get("summary"), "summary");
      this.fillSubreport((SubReport) hm.get("summaryPerType"), "summaryPerType");
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

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "details":
        model = this.getDeliverablesDetailsTableModel();
        break;
      case "summary":
        model = this.getDeliverablesPerYearTableModel();
        break;
      case "summaryPerType":
        model = this.getDeliverablesPerTypeTableModel();
        break;

    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  private int getCalendarFromDate(Date date) {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      return cal.get(Calendar.YEAR);
    } catch (NullPointerException e) {
      return 0;
    }
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
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverableId", "deliverableTitle", "completionYear", "deliverableType", "deliverableSubType",
        "keyOutput", "delivStatus", "delivNewYear", "projectID", "projectTitle", "projectClusterActivities",
        "flagships", "regions", "individual", "partnersResponsible", "shared", "openFS", "fsWindows", "outcomes",
        "projectLeadPartner", "managingResponsible", "phaseID", "finishedFS", "gender", "youth", "cap",
        "deliverableDescription", "geographicScope", "region", "country"},
      new Class[] {Long.class, String.class, Integer.class, String.class, String.class, String.class, String.class,
        String.class, Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);
    Boolean activePPAFilter = ppa != null && !ppa.isEmpty() && !ppa.equals("All") && !ppa.equals("-1");
    Boolean addDeliverableRow = true;
    Set<Deliverable> phaseDeliverables = new HashSet<>();
    for (GlobalUnitProject globalUnitProject : this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(p -> p.isActive() && p.getProject() != null && p.getProject().isActive()
        && (p.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
          && p.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || p.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null && p.getProject().getProjectInfo()
            .getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
      .collect(Collectors.toList())) {
      for (Deliverable deliverable : globalUnitProject.getProject().getDeliverables().stream().filter(d -> d.isActive()
        && d.getDeliverableInfo(this.getSelectedPhase()) != null && d.getDeliverableInfo().isActive()
        && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == this.getSelectedYear())
          || (d.getDeliverableInfo().getStatus() != null
            && d.getDeliverableInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())
            && d.getDeliverableInfo().getNewExpectedYear() != null
            && d.getDeliverableInfo().getNewExpectedYear() == this.getSelectedYear())
          || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == this.getSelectedYear()
            && d.getDeliverableInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
        .collect(Collectors.toList())) {
        phaseDeliverables.add(deliverable);
      }
    }

    for (Deliverable deliverable : phaseDeliverables.stream().sorted((d1, d2) -> d1.getId().compareTo(d2.getId()))
      .collect(Collectors.toList())) {
      String responsibleName = "";
      String responsibleAcronym = "";
      if (activePPAFilter) {
        addDeliverableRow = false;
      }
      // Store Institution and PartnerPerson
      String individual = "";
      // Store Institution
      String ppaRespondible = "";
      Set<String> ppaResponsibleList = new HashSet<>();
      LinkedHashSet<Institution> institutionsResponsibleList = new LinkedHashSet<>();

      // Get partner responsible

      List<DeliverablePartnership> partnershipsList = deliverable.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());
      // Set responible;
      DeliverablePartnership responsible = null;
      if (partnershipsList.stream()
        .filter(dp -> dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).size() > 0) {
        responsible = partnershipsList.stream()
          .filter(dp -> dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
          .collect(Collectors.toList()).get(0);
      }

      if (responsible != null) {

        if (responsible.getProjectPartner() != null) {
          institutionsResponsibleList.add(responsible.getProjectPartner().getInstitution());
        }
        if (responsible.getProjectPartnerPerson() == null) {

          // get deliverable information when partner responsible does not have a person

          if (responsible.getProjectPartner() != null) {
            if (responsible.getProjectPartner().getInstitution() != null) {
              if (responsible.getProjectPartner().getInstitution().getAcronym() != null
                && !responsible.getProjectPartner().getInstitution().getAcronym().isEmpty()) {
                ppaResponsibleList.add("<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>"
                  + responsible.getProjectPartner().getInstitution().getAcronym() + "</span>");
                responsibleAcronym = responsible.getProjectPartner().getInstitution().getAcronym();
              } else {
                ppaResponsibleList.add("<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>"
                  + responsible.getProjectPartner().getInstitution().getName() + "</span>");
                responsibleName = responsible.getProjectPartner().getInstitution().getName();
              }
            }
          }

          if (responsible.getPartnerDivision() != null && responsible.getPartnerDivision().getAcronym() != null
            && !responsible.getPartnerDivision().getAcronym().isEmpty()) {
          }

        } else if (responsible.getProjectPartnerPerson() != null) {
          individual += "<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>";
          ProjectPartnerPerson responsibleppp = responsible.getProjectPartnerPerson();
          if (responsibleppp.getProjectPartner() != null) {
            if (responsibleppp.getProjectPartner().getInstitution() != null) {
              if (responsibleppp.getProjectPartner().getInstitution().getAcronym() != null
                && !responsibleppp.getProjectPartner().getInstitution().getAcronym().isEmpty()) {
                individual += responsibleppp.getProjectPartner().getInstitution().getAcronym() + " - ";
                ppaRespondible += "<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>"
                  + responsibleppp.getProjectPartner().getInstitution().getAcronym() + "</span>";
                responsibleAcronym = responsibleppp.getProjectPartner().getInstitution().getAcronym();
              } else {
                individual += responsibleppp.getProjectPartner().getInstitution().getName() + " - ";
                ppaRespondible += "<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>"
                  + responsibleppp.getProjectPartner().getInstitution().getName() + "</span>";
                responsibleName = responsibleppp.getProjectPartner().getInstitution().getName();
              }
            }
          }
          if (responsibleppp.getUser() != null) {
            individual += responsibleppp.getUser().getComposedNameWithoutEmail();
          }

          if (responsible.getPartnerDivision() != null && responsible.getPartnerDivision().getAcronym() != null
            && !responsible.getPartnerDivision().getAcronym().isEmpty()) {
            individual += " (" + responsible.getPartnerDivision().getAcronym() + ") ";// All individual resposible
          }

          individual += "</span>";
        }
      }

      // Get partner others
      List<DeliverablePartnership> othersPartnerships = null;
      if (partnershipsList.stream()
        .filter(dp -> dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
        .collect(Collectors.toList()).size() > 0) {
        othersPartnerships = partnershipsList.stream()
          .filter(dp -> dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
          .collect(Collectors.toList());
      }

      if (othersPartnerships != null) {
        individual += ", ";
        for (DeliverablePartnership deliverablePartnership : othersPartnerships) {
          if (deliverablePartnership.getProjectPartner() != null) {
            institutionsResponsibleList.add(deliverablePartnership.getProjectPartner().getInstitution());
          }
          if (deliverablePartnership.getProjectPartnerPerson() != null) {
            if (individual.isEmpty()) {
              individual += "<span style='font-family: Segoe UI;font-size: 10'>";
            }

            ProjectPartnerPerson responsibleppp = deliverablePartnership.getProjectPartnerPerson();
            if (responsibleppp.getProjectPartner() != null) {
              if (responsibleppp.getProjectPartner().getInstitution() != null) {
                if (responsibleppp.getProjectPartner().getInstitution().getAcronym() != null
                  && !responsibleppp.getProjectPartner().getInstitution().getAcronym().isEmpty()) {
                  individual += responsibleppp.getProjectPartner().getInstitution().getAcronym() + " - ";
                  if (!responsibleAcronym.equals(responsibleppp.getProjectPartner().getInstitution().getAcronym())) {
                    ppaResponsibleList.add(responsibleppp.getProjectPartner().getInstitution().getAcronym());
                  }
                } else {
                  individual += responsibleppp.getProjectPartner().getInstitution().getName() + " - ";
                  if (!responsibleAcronym.equals(responsibleppp.getProjectPartner().getInstitution().getName())) {
                    ppaResponsibleList.add(responsibleppp.getProjectPartner().getInstitution().getName());
                  }
                }
              }
            }

            if (responsibleppp.getUser() != null) {
              individual += responsibleppp.getUser().getComposedName();
            }
            if (deliverablePartnership.getPartnerDivision() != null
              && deliverablePartnership.getPartnerDivision().getAcronym() != null
              && !deliverablePartnership.getPartnerDivision().getAcronym().isEmpty()) {
              individual += " (" + deliverablePartnership.getPartnerDivision().getAcronym() + ")";
            }
            individual += "</span>";
          } else {

            // get deliverable information from deliverablePartnership

            if (deliverablePartnership.getProjectPartner() != null) {

              if (deliverablePartnership.getProjectPartner().getInstitution() != null) {
                if (deliverablePartnership.getProjectPartner().getInstitution().getAcronym() != null
                  && !deliverablePartnership.getProjectPartner().getInstitution().getAcronym().isEmpty()) {
                  if (!responsibleAcronym
                    .equals(deliverablePartnership.getProjectPartner().getInstitution().getAcronym())) {
                    ppaResponsibleList.add(deliverablePartnership.getProjectPartner().getInstitution().getAcronym());
                  }
                } else {
                  if (!responsibleAcronym
                    .equals(deliverablePartnership.getProjectPartner().getInstitution().getName())) {
                    ppaResponsibleList.add(deliverablePartnership.getProjectPartner().getInstitution().getName());
                  }
                }
              }

            }

            if (deliverablePartnership.getPartnerDivision() != null
              && deliverablePartnership.getPartnerDivision().getAcronym() != null
              && !deliverablePartnership.getPartnerDivision().getAcronym().isEmpty()) {
            }
          }
        }
      }

      if (individual.isEmpty()) {
        individual = null;
      }
      LinkedHashSet<Institution> managingResponsibleList = new LinkedHashSet<>();
      for (String ppaOher : ppaResponsibleList) {
        if (ppaRespondible.isEmpty()) {
          ppaRespondible += "<span style='font-family: Segoe UI;font-size: 10'>" + ppaOher + "</span>";
        } else {
          ppaRespondible += ", <span style='font-family: Segoe UI;font-size: 10'>" + ppaOher + "</span>";
        }
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
                managingResponsibleList.add(projectPartnerContribution.getProjectPartnerContributor().getInstitution());
              }
            }
          }
        }
      }

      if (ppaRespondible.isEmpty()) {
        ppaRespondible = null;
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
        if (responsible != null && responsible.getProjectPartner() != null) {
          if (responsible.getProjectPartner().getInstitution().equals(managingInstitution)) {
            color = ";color:#ff0000";
          } else {
            List<ProjectPartnerContribution> projectPartnerContributions = responsible.getProjectPartner()
              .getProjectPartnerContributions().stream().filter(pc -> pc.isActive()).collect(Collectors.toList());
            if (projectPartnerContributions != null && !projectPartnerContributions.isEmpty()) {
              for (ProjectPartnerContribution projectPartnerContribution : projectPartnerContributions) {
                if (projectPartnerContribution.getProjectPartner().equals(responsible.getProjectPartner())) {
                  color = ";color:#ff0000";
                }
              }
            }
          }
        }

        if (managingResponsible.isEmpty()) {
          managingResponsible +=
            "<span style='font-family: Segoe UI;font-size: 10" + color + "'>" + institution + "</span>";
        } else {
          managingResponsible +=
            ", <span style='font-family: Segoe UI;font-size: 10" + color + "'>" + institution + "</span>";
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


      if (addDeliverableRow) {
        currentPhaseDeliverables.add(deliverable);
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(this.getSelectedPhase());
        Long phaseID = deliverableInfo.getPhase().getId();

        Long deliverableId = deliverable.getId();
        String deliverableTitle = (deliverableInfo.getTitle() != null && !deliverableInfo.getTitle().isEmpty())
          ? deliverableInfo.getTitle() : null;
        String deliverableDescription =
          (deliverableInfo.getDescription() != null && !deliverableInfo.getDescription().isEmpty())
            ? deliverableInfo.getDescription() : null;
        Integer completionYear = deliverableInfo.getYear();
        String deliverableSubType =
          (deliverableInfo.getDeliverableType() != null && deliverableInfo.getDeliverableType().getName() != null
            && !deliverableInfo.getDeliverableType().getName().isEmpty())
              ? deliverableInfo.getDeliverableType().getName() : null;
        String deliverableType = (deliverableInfo.getDeliverableType() != null
          && deliverableInfo.getDeliverableType().getDeliverableCategory() != null
          && deliverableInfo.getDeliverableType().getDeliverableCategory().getName() != null
          && !deliverableInfo.getDeliverableType().getDeliverableCategory().getName().isEmpty())
            ? deliverableInfo.getDeliverableType().getDeliverableCategory().getName() : null;

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
          if (deliverableCrossCuttingMarkerGender != null
            && deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel() != null) {
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
          if (deliverableCrossCuttingMarkerYouth != null
            && deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel() != null) {
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
          if (deliverableCrossCuttingMarkerCapDev != null
            && deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel() != null) {
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
            if (deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel() != null) {
              gender = deliverableCrossCuttingMarkerGender.getRepIndGenderYouthFocusLevel().getPowbName();
            }
          }
          if (gender.isEmpty()) {
            gender = "0-Not Targeted";
          }

          // Youth
          if (deliverableCrossCuttingMarkerYouth != null
            && deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel() != null) {
            youth = deliverableCrossCuttingMarkerYouth.getRepIndGenderYouthFocusLevel().getPowbName();
          }
          if (youth.isEmpty()) {
            youth = "0-Not Targeted";
          }

          // Cap
          if (deliverableCrossCuttingMarkerCapDev != null
            && deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel() != null) {
            cap = deliverableCrossCuttingMarkerCapDev.getRepIndGenderYouthFocusLevel().getPowbName();
          }
          if (cap.isEmpty()) {
            cap = "0-Not Targeted";
          }
        }

        String keyOutput = "";
        String outcomes = "";

        if (deliverableInfo.getCrpClusterKeyOutput() != null) {
          keyOutput += "• ";

          if (deliverableInfo.getCrpClusterKeyOutput().getCrpClusterOfActivity() != null
            && deliverableInfo.getCrpClusterKeyOutput().getCrpClusterOfActivity().getCrpProgram() != null) {
            keyOutput +=
              deliverableInfo.getCrpClusterKeyOutput().getCrpClusterOfActivity().getCrpProgram().getAcronym() + " - ";
          }
          keyOutput += deliverableInfo.getCrpClusterKeyOutput().getKeyOutput();
          // Get Outcomes Related to the KeyOutput
          for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : deliverableInfo.getCrpClusterKeyOutput()
            .getCrpClusterKeyOutputOutcomes().stream()
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

        String delivStatus = (deliverableInfo.getStatusName(this.getActualPhase()) != null
          && !deliverableInfo.getStatusName(this.getActualPhase()).isEmpty())
            ? deliverableInfo.getStatusName(this.getActualPhase()) : null;
        String delivNewYear = null;
        if (deliverableInfo.getStatus() != null
          && deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
          delivNewYear = deliverableInfo.getNewExpectedYear() != null && deliverableInfo.getNewExpectedYear() != -1
            ? deliverableInfo.getNewExpectedYear().toString() : null;
        } else {
          delivNewYear = "&lt;Not Applicable&gt;";
        }

        Long projectID = null;
        String projectTitle = null;
        String projectLeadPartner = null;

        if (deliverable.getProject() != null) {
          projectID = deliverable.getProject().getId();
          if (deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
            && deliverable.getProject().getProjectInfo().getTitle() != null
            && !deliverable.getProject().getProjectInfo().getTitle().trim().isEmpty()) {
            projectTitle = deliverable.getProject().getProjectInfo().getTitle();
          }
          // Get project leader

          if (deliverable.getProject().getLeader(this.getSelectedPhase()) != null) {
            ProjectPartner leader = deliverable.getProject().getLeader(this.getSelectedPhase());
            if (leader.getInstitution() != null) {
              if (leader.getInstitution().getAcronym() != null
                && !leader.getInstitution().getAcronym().trim().isEmpty()) {
                projectLeadPartner = leader.getInstitution().getAcronym();
              } else {
                projectLeadPartner = leader.getInstitution().getName();
              }
            }
          }
        }
        String projectClusterActivities = "";
        if (deliverable.getProject() != null && deliverable.getProject().getProjectClusterActivities() != null) {
          for (ProjectClusterActivity projectClusterActivity : deliverable.getProject().getProjectClusterActivities()
            .stream().filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase()))
            .collect(Collectors.toList())) {
            if (projectClusterActivities.isEmpty()) {
              projectClusterActivities += projectClusterActivity.getCrpClusterOfActivity().getIdentifier();
            } else {
              projectClusterActivities += ", " + projectClusterActivity.getCrpClusterOfActivity().getIdentifier();
            }
          }
        }
        if (projectClusterActivities.isEmpty()) {
          projectClusterActivities = null;
        }


        String flagships = null;
        // get Flagships related to the project sorted by acronym
        for (ProjectFocus projectFocuses : deliverable.getProject().getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList())) {
          if (flagships == null || flagships.isEmpty()) {
            flagships = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          } else {
            flagships +=
              ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
          }
        }
        String regions = null;
        // If has regions, add the regions to regionsArrayList
        // Get Regions related to the project sorted by acronym
        if (this.hasProgramnsRegions()) {
          for (ProjectFocus projectFocuses : deliverable.getProject().getProjectFocuses().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
            .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
            .collect(Collectors.toList())) {
            if (regions == null || regions.isEmpty()) {
              regions = crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            } else {
              regions +=
                ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            }
          }
          if (deliverable.getProject().getProjecInfoPhase(this.getSelectedPhase()).getNoRegional() != null
            && deliverable.getProject().getProjectInfo().getNoRegional()) {
            if (regions != null && !regions.isEmpty()) {
              LOG.warn("Project is global and has regions selected");
            }
            regions = "Global";
          }
        } else {
          regions = null;
        }
        String openFS = "";
        String finishedFS = "";
        Set<String> fsWindowsSet = new HashSet<String>();

        for (DeliverableFundingSource deliverableFundingSource : deliverable.getDeliverableFundingSources().stream()
          .filter(df -> df.isActive() && df.getPhase() != null && df.getPhase().equals(this.getSelectedPhase())
            && df.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null)
          .collect(Collectors.toList())) {
          FundingSourceInfo fundingSourceInfo = deliverableFundingSource.getFundingSource().getFundingSourceInfo();
          if (fundingSourceInfo.getEndDate() != null) {
            Date endDate = fundingSourceInfo.getEndDate();
            Date extentionDate = fundingSourceInfo.getExtensionDate();
            int endYear = this.getCalendarFromDate(endDate);
            int extentionYear = this.getCalendarFromDate(extentionDate);
            if ((endYear >= this.getSelectedYear()
              && fundingSourceInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
              || (extentionYear >= this.getSelectedYear() && fundingSourceInfo.getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId()))) {
              if (openFS.isEmpty()) {
                openFS += "FS" + deliverableFundingSource.getFundingSource().getId();
              } else {
                openFS += ", FS" + deliverableFundingSource.getFundingSource().getId();
              }
              if (fundingSourceInfo.getBudgetType() != null) {
                fsWindowsSet.add(fundingSourceInfo.getBudgetType().getName());
              }
            } else {
              if (finishedFS.isEmpty()) {
                finishedFS += "FS" + deliverableFundingSource.getFundingSource().getId();
              } else {
                finishedFS += ", FS" + deliverableFundingSource.getFundingSource().getId();
              }
              if (fundingSourceInfo.getBudgetType() != null) {
                fsWindowsSet.add(fundingSourceInfo.getBudgetType().getName());
              }
            }
          }
        }
        if (openFS.isEmpty()) {
          openFS = null;
        }
        if (finishedFS.isEmpty()) {
          finishedFS = null;
        }


        String fsWindows = "";
        for (String fsType : fsWindowsSet.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList())) {
          if (fsWindows.isEmpty()) {
            fsWindows += fsType;
          } else {
            fsWindows += ", " + fsType;
          }
        }
        if (fsWindows.isEmpty()) {
          fsWindows = null;
        }


        /*
         * Geographic Scope
         */
        String geographicScope = "", region = "", country = "";

        // Geographic Scope
        if (deliverableInfo.getGeographicScope() != null) {
          geographicScope = deliverableInfo.getGeographicScope().getName();
          if (deliverableInfo.getGeographicScope().getId().equals(this.getReportingIndGeographicScopeGlobal())) {
            region = "&lt;Not Applicable&gt;";
            country = "&lt;Not Applicable&gt;";
          }
          // Regional
          if (deliverableInfo.getGeographicScope().getId().equals(this.getReportingIndGeographicScopeRegional())) {
            country = "&lt;Not Applicable&gt;";
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
          if (!deliverableInfo.getGeographicScope().getId().equals(this.getReportingIndGeographicScopeGlobal())
            && !deliverableInfo.getGeographicScope().getId().equals(this.getReportingIndGeographicScopeRegional())) {
            region = "&lt;Not Applicable&gt;";
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


        model.addRow(new Object[] {deliverableId, deliverableTitle, completionYear, deliverableType, deliverableSubType,
          keyOutput, delivStatus, delivNewYear, projectID, projectTitle, projectClusterActivities, flagships, regions,
          individual, ppaRespondible, shared, openFS, fsWindows, outcomes, projectLeadPartner, managingResponsible,
          phaseID, finishedFS, gender, youth, cap, deliverableDescription, geographicScope, region, country});

        if (deliverablePerYearList.containsKey(completionYear)) {
          Set<Deliverable> deliverableSet = deliverablePerYearList.get(completionYear);
          deliverableSet.add(deliverable);
          deliverablePerYearList.put(completionYear, deliverableSet);
        } else {
          Set<Deliverable> deliverableSet = new HashSet<>();
          deliverableSet.add(deliverable);
          deliverablePerYearList.put(completionYear, deliverableSet);
        }

        if (deliverableType != null) {
          if (deliverablePerTypeList.containsKey(deliverableType)) {
            Set<Deliverable> deliverableSet = deliverablePerTypeList.get(deliverableType);
            deliverableSet.add(deliverable);
            deliverablePerTypeList.put(deliverableType, deliverableSet);
          } else {
            Set<Deliverable> deliverableSet = new HashSet<>();
            deliverableSet.add(deliverable);
            deliverablePerTypeList.put(deliverableType, deliverableSet);
          }
        }

        if (projectID != null) {
          projectsList.add(projectID);
        }
      }

    }
    return model;

  }

  private TypedTableModel getDeliverablesPerTypeTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"deliverableType", "delivetableTotal"},
      new Class[] {String.class, Integer.class}, 0);
    Integer grandTotalTypes = 0;
    for (String type : deliverablePerTypeList.keySet()) {
      grandTotalTypes += deliverablePerTypeList.get(type).size();
    }
    SortedSet<String> keys = new TreeSet<String>(deliverablePerTypeList.keySet());
    for (String type : keys) {
      Integer totalType = deliverablePerTypeList.get(type).size();
      Float percentageOfTotal = (totalType * 100f) / grandTotalTypes;
      model.addRow(type + " - " + String.format("%.02f", percentageOfTotal) + "%", totalType);
    }
    return model;
  }


  private TypedTableModel getDeliverablesPerYearTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"deliverableYear", "delivetableTotal"},
      new Class[] {String.class, Integer.class}, 0);
    SortedSet<Integer> keys = new TreeSet<Integer>(deliverablePerYearList.keySet());
    for (Integer year : keys) {
      model.addRow(year, deliverablePerYearList.get(year).size());
    }
    return model;
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
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
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

  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible", "showDescription", "cycle"},
        new Class[] {String.class, String.class, String.class, Boolean.class, Boolean.class, String.class});
    String center = this.getLoggedCrp().getAcronym();

    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String date = timezone.format(format) + "(GMT" + zone + ")";
    String year = this.getSelectedYear() + "";
    model.addRow(new Object[] {center, date, year, this.hasProgramnsRegions(),
      this.hasSpecificities(APConstants.CRP_REPORTS_DESCRIPTION), this.getSelectedCycle()});
    return model;
  }


  @Override
  public void prepare() {
    // Get PPA for filtering
    try {
      Map<String, Parameter> parameters = this.getParameters();
      ppa = StringUtils.trim(parameters.get(APConstants.SUMMARY_DELIVERABLE_PPA).getMultipleValues()[0]);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.SUMMARY_DELIVERABLE_PPA
        + " parameter. Parameter will be set as All. Exception: " + e.getMessage());
      ppa = "All";
    }
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

}
