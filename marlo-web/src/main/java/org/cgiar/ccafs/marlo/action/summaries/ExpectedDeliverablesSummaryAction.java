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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
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
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
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

  // Managers
  private GlobalUnitManager crpManager;
  private GenderTypeManager genderTypeManager;
  private CrpProgramManager crpProgramManager;
  private DeliverableManager deliverableManager;
  // XLS bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;
  // Store deliverables with year and type HashMap<Deliverable, List<year, type>>
  HashMap<Integer, Set<Deliverable>> deliverablePerYearList = new HashMap<Integer, Set<Deliverable>>();
  HashMap<String, Set<Deliverable>> deliverablePerTypeList = new HashMap<String, Set<Deliverable>>();
  Set<Long> projectsList = new HashSet<Long>();
  Set<Long> deliverablesList = new HashSet<Long>();

  @Inject
  public ExpectedDeliverablesSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    GenderTypeManager genderTypeManager, CrpProgramManager crpProgramManager, DeliverableManager deliverableManager) {
    super(config, crpManager, phaseManager);
    this.genderTypeManager = genderTypeManager;
    this.crpProgramManager = crpProgramManager;
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
    masterReport.getParameterValues().put("i8nPartnersResponsible", this.getText("deliverable.managing"));
    masterReport.getParameterValues().put("i8nShared", this.getText("deliverable.shared"));
    masterReport.getParameterValues().put("i8nFundingSourcesID", this.getText("deliverable.fundingSourcesID"));
    masterReport.getParameterValues().put("i8nFundingWindows", this.getText("deliverable.fundingWindows"));
    masterReport.getParameterValues().put("i8nNewExpectedYear", this.getText("deliverable.newExpectedYear"));
    masterReport.getParameterValues().put("i8nOutcomes", this.getText("impactPathway.menu.hrefOutcomes"));
    masterReport.getParameterValues().put("i8nManagingResponsible", this.getText("deliverable.project.managing"));
    masterReport.getParameterValues().put("i8nProjectLeadPartner", this.getText("summaries.deliverable.leadPartner"));

    return masterReport;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    try {
      Resource reportResource = manager
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
      // System.out.println("Pentaho SubReports: " + hm);
      this.fillSubreport((SubReport) hm.get("details"), "details");
      masterReport.getParameterValues().put("total_deliv", deliverablesList.size());
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
        "crossCutting", "genderLevels", "keyOutput", "delivStatus", "delivNewYear", "projectID", "projectTitle",
        "projectClusterActivities", "flagships", "regions", "individual", "partnersResponsible", "shared", "FS",
        "fsWindows", "outcomes", "projectLeadPartner", "managingResponsible"},
      new Class[] {Long.class, String.class, Integer.class, String.class, String.class, String.class, String.class,
        String.class, String.class, Integer.class, Long.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);

    for (Deliverable deliverable : deliverableManager.findAll().stream()
      .filter(d -> d.isActive() && d.getProject() != null && d.getProject().isActive()
        && d.getProject().getGlobalUnitProjects().stream()
          .filter(gup -> gup.isActive() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
          .collect(Collectors.toList()).size() > 0
        && d.getDeliverableInfo(this.getSelectedPhase()) != null
        && d.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
        && d.getProject().getProjectInfo().getStatus().toString().equals(ProjectStatusEnum.Ongoing.getStatusId())
        && ((d.getDeliverableInfo().getNewExpectedYear() != null
          && d.getDeliverableInfo().getNewExpectedYear() >= this.getSelectedYear())
          || d.getDeliverableInfo().getYear() >= this.getSelectedYear()))
      .sorted((d1, d2) -> d1.getId().compareTo(d2.getId())).collect(Collectors.toList())) {
      DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(this.getSelectedPhase());

      Long deliverableId = deliverable.getId();
      String deliverableTitle = (deliverableInfo.getTitle() != null && !deliverableInfo.getTitle().isEmpty())
        ? deliverableInfo.getTitle() : null;
      Integer completionYear = deliverableInfo.getYear();
      String deliverableSubType =
        (deliverableInfo.getDeliverableType() != null && deliverableInfo.getDeliverableType().getName() != null
          && !deliverableInfo.getDeliverableType().getName().isEmpty()) ? deliverableInfo.getDeliverableType().getName()
            : null;
      String deliverableType = (deliverableInfo.getDeliverableType() != null
        && deliverableInfo.getDeliverableType().getDeliverableType() != null
        && deliverableInfo.getDeliverableType().getDeliverableType().getName() != null
        && !deliverableInfo.getDeliverableType().getDeliverableType().getName().isEmpty())
          ? deliverableInfo.getDeliverableType().getDeliverableType().getName() : null;

      // Get cross_cutting dimension
      String crossCutting = "";
      if (deliverableInfo.getCrossCuttingNa() != null) {
        if (deliverableInfo.getCrossCuttingNa() == true) {
          crossCutting += "N/A";
        }
      }
      if (deliverableInfo.getCrossCuttingGender() != null) {
        if (deliverableInfo.getCrossCuttingGender() == true) {
          if (crossCutting.isEmpty()) {
            crossCutting += "Gender";
          } else {
            crossCutting += ", Gender";
          }
        }
      }
      if (deliverableInfo.getCrossCuttingYouth() != null) {
        if (deliverableInfo.getCrossCuttingYouth() == true) {
          if (crossCutting.isEmpty()) {
            crossCutting += "Youth";
          } else {
            crossCutting += ", Youth";
          }
        }
      }
      if (deliverableInfo.getCrossCuttingCapacity() != null) {
        if (deliverableInfo.getCrossCuttingCapacity() == true) {
          if (crossCutting.isEmpty()) {
            crossCutting += "Capacity Development";
          } else {
            crossCutting += ", Capacity Development";
          }

        }
      }

      if (crossCutting.isEmpty()) {
        crossCutting = null;
      }
      String genderLevels = "";

      int countGender = 0;
      if (deliverableInfo.getCrossCuttingGender() != null) {
        if (deliverableInfo.getCrossCuttingGender() == true) {
          if (deliverable.getDeliverableGenderLevels() == null || deliverable.getDeliverableGenderLevels().isEmpty()) {
            genderLevels += "Gender level(s): &lt;Not Defined&gt;";
          } else {
            genderLevels += "Gender level(s): ";
            for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
              .filter(dgl -> dgl.isActive() && dgl.getPhase().equals(this.getSelectedPhase()))
              .collect(Collectors.toList())) {
              if (dgl.getGenderLevel() != 0.0) {
                if (countGender == 0) {
                  genderLevels += genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription();
                } else {
                  genderLevels += ", " + genderTypeManager.getGenderTypeById(dgl.getGenderLevel()).getDescription();
                }
                countGender++;
              }
            }
          }
        }
      }
      if (genderLevels.isEmpty()) {
        genderLevels = null;
      }
      String keyOutput = "";
      String outcomes = "";

      if (deliverableInfo.getCrpClusterKeyOutput() != null) {
        keyOutput += "• ";

        if (deliverableInfo.getCrpClusterKeyOutput().getCrpClusterOfActivity().getCrpProgram() != null) {
          keyOutput +=
            deliverableInfo.getCrpClusterKeyOutput().getCrpClusterOfActivity().getCrpProgram().getAcronym() + " - ";
        }
        keyOutput += deliverableInfo.getCrpClusterKeyOutput().getKeyOutput();
        // Get Outcomes Related to the KeyOutput
        for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : deliverableInfo.getCrpClusterKeyOutput()
          .getCrpClusterKeyOutputOutcomes().stream().filter(ko -> ko.isActive() && ko.getCrpProgramOutcome() != null)
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

      String delivStatus = (deliverableInfo.getStatusName(this.getActualPhase()) != null
        && !deliverableInfo.getStatusName(this.getActualPhase()).isEmpty())
          ? deliverableInfo.getStatusName(this.getActualPhase()) : null;
      Integer delivNewYear = deliverableInfo.getNewExpectedYear() != null && deliverableInfo.getNewExpectedYear() != -1
        ? deliverableInfo.getNewExpectedYear() : null;
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
          flagships += ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
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
            regions += ", " + crpProgramManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
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
      // Store Institution and PartnerPerson
      String individual = "";
      // Store Institution
      String ppaRespondible = "";
      Set<String> ppaResponsibleList = new HashSet<>();
      Set<Institution> institutionsResponsibleList = new HashSet<>();

      int managingCount = 0;
      // Get partner responsible

      List<DeliverablePartnership> partnershipsList = deliverable.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());
      // Set responible;
      DeliverablePartnership responisble = null;
      if (partnershipsList.stream()
        .filter(dp -> dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).size() > 0) {
        responisble = partnershipsList.stream()
          .filter(dp -> dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
          .collect(Collectors.toList()).get(0);
      }

      if (responisble != null) {
        if (responisble.getProjectPartnerPerson() != null) {
          individual += "<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>";
          ProjectPartnerPerson responsibleppp = responisble.getProjectPartnerPerson();
          if (responsibleppp.getProjectPartner() != null) {
            if (responsibleppp.getProjectPartner().getInstitution() != null) {
              institutionsResponsibleList.add(responsibleppp.getProjectPartner().getInstitution());
              if (responsibleppp.getProjectPartner().getInstitution().getAcronym() != null
                && !responsibleppp.getProjectPartner().getInstitution().getAcronym().isEmpty()) {
                individual += responsibleppp.getProjectPartner().getInstitution().getAcronym() + " - ";
                ppaRespondible += "<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>"
                  + responsibleppp.getProjectPartner().getInstitution().getAcronym() + "</span>";
              } else {
                individual += responsibleppp.getProjectPartner().getInstitution().getName() + " - ";
                ppaRespondible += "<span style='font-family: Segoe UI;color:#ff0000;font-size: 10'>"
                  + responsibleppp.getProjectPartner().getInstitution().getName() + "</span>";
              }
            }
          }
          if (responsibleppp.getUser() != null) {
            individual += responsibleppp.getUser().getComposedName();
          }
          managingCount++;
          if (responisble.getPartnerDivision() != null && responisble.getPartnerDivision().getAcronym() != null
            && !responisble.getPartnerDivision().getAcronym().isEmpty()) {
            individual += " (" + responisble.getPartnerDivision().getAcronym() + ")";
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
        for (DeliverablePartnership deliverablePartnership : othersPartnerships) {
          if (deliverablePartnership.getProjectPartnerPerson() != null) {
            individual += ", <span style='font-family: Segoe UI;font-size: 10'>";
            ProjectPartnerPerson responsibleppp = deliverablePartnership.getProjectPartnerPerson();
            if (responsibleppp.getProjectPartner() != null) {
              if (responsibleppp.getProjectPartner().getInstitution() != null) {
                institutionsResponsibleList.add(responsibleppp.getProjectPartner().getInstitution());
                if (responsibleppp.getProjectPartner().getInstitution().getAcronym() != null
                  && !responsibleppp.getProjectPartner().getInstitution().getAcronym().isEmpty()) {
                  individual += responsibleppp.getProjectPartner().getInstitution().getAcronym() + " - ";
                  ppaResponsibleList.add(responsibleppp.getProjectPartner().getInstitution().getAcronym());
                } else {
                  individual += responsibleppp.getProjectPartner().getInstitution().getName() + " - ";
                  ppaResponsibleList.add(responsibleppp.getProjectPartner().getInstitution().getName());
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
          }
        }
      }

      if (individual.isEmpty()) {
        individual = null;
      }
      Set<Institution> managingResponsibleList = new HashSet<>();
      for (String ppaOher : ppaResponsibleList) {
        managingCount++;
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
      String shared = null;
      if (managingCount == 0) {
        shared = "Not Defined";
      }
      if (managingCount == 1) {
        shared = "No";
      }
      if (managingCount > 1) {
        shared = "Yes";
      }

      String managingResponsible = "";
      for (Institution managingInstitution : managingResponsibleList) {
        String institution = "";
        if (managingInstitution.getAcronym() != null && !managingInstitution.getAcronym().trim().isEmpty()) {
          institution = managingInstitution.getAcronym();
        } else {
          institution = managingInstitution.getName();
        }
        if (managingResponsible.isEmpty()) {
          managingResponsible += "<span style='font-family: Segoe UI;font-size: 10'>" + institution + "</span>";
        } else {
          managingResponsible += ", <span style='font-family: Segoe UI;font-size: 10'>" + institution + "</span>";
        }
      }
      if (managingResponsible.isEmpty()) {
        managingResponsible = null;
      }


      String FS = "";
      Set<String> fsWindowsSet = new HashSet<String>();

      for (DeliverableFundingSource deliverableFundingSource : deliverable.getDeliverableFundingSources().stream()
        .filter(df -> df.isActive() && df.getPhase() != null && df.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList())) {
        if (FS.isEmpty()) {
          FS += "FS" + deliverableFundingSource.getFundingSource().getId();
        } else {
          FS += ", FS" + deliverableFundingSource.getFundingSource().getId();
        }

        if (deliverableFundingSource.getFundingSource() != null
          && deliverableFundingSource.getFundingSource().getFundingSourceInfo(this.getSelectedPhase()) != null
          && deliverableFundingSource.getFundingSource().getFundingSourceInfo().getBudgetType() != null) {
          fsWindowsSet
            .add(deliverableFundingSource.getFundingSource().getFundingSourceInfo().getBudgetType().getName());
        }
      }
      if (FS.isEmpty()) {
        FS = null;
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

      model.addRow(new Object[] {deliverableId, deliverableTitle, completionYear, deliverableType, deliverableSubType,
        crossCutting, genderLevels, keyOutput, delivStatus, delivNewYear, projectID, projectTitle,
        projectClusterActivities, flagships, regions, individual, ppaRespondible, shared, FS, fsWindows, outcomes,
        projectLeadPartner, managingResponsible});

      if (completionYear != null) {
        if (deliverablePerYearList.containsKey(completionYear)) {
          Set<Deliverable> deliverableSet = deliverablePerYearList.get(completionYear);
          deliverableSet.add(deliverable);
          deliverablePerYearList.put(completionYear, deliverableSet);
        } else {
          Set<Deliverable> deliverableSet = new HashSet<>();
          deliverableSet.add(deliverable);
          deliverablePerYearList.put(completionYear, deliverableSet);
        }
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
      if (deliverableId != null) {
        deliverablesList.add(deliverableId);
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
      new TypedTableModel(new String[] {"center", "date", "year", "regionalAvalaible", "showDescription"},
        new Class[] {String.class, String.class, String.class, Boolean.class, Boolean.class});
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
      this.hasSpecificities(APConstants.CRP_REPORTS_DESCRIPTION)});
    return model;
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
