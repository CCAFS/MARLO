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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.pentaho.reporting.engine.classic.core.ReportHeader;
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

public class BudgetPerPartnersSummaryAction extends BaseAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Logger LOG = LoggerFactory.getLogger(BudgetPerPartnersSummaryAction.class);
  // Parameters
  private Crp loggedCrp;
  private int year;
  private String cycle;
  private Boolean hasW1W2Co;
  private long startTime;

  private PhaseManager phaseManager;

  // Store total projects
  Integer totalProjects = 0;
  // Store parters budgets HashMap<Institution, List<w1w2,w3bilateralcenter>>
  HashMap<Institution, List<Double>> allPartnersBudgets = new HashMap<Institution, List<Double>>();
  // Store projects budgets HashMap<Project, List<totalw1w2, totalw3bilateralcenter, totalw1w2Gender, totalw3Gender>>
  HashMap<Project, List<Double>> allProjectsBudgets = new HashMap<Project, List<Double>>();
  private CrpManager crpManager;


  private ProjectBudgetManager projectBudgetManager;
  private CrpProgramManager programManager;
  private InstitutionManager institutionManager;
  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;


  @Inject
  public BudgetPerPartnersSummaryAction(APConfig config, CrpManager crpManager,
    ProjectBudgetManager projectBudgetManager, CrpProgramManager programManager, InstitutionManager institutionManager,
    PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectBudgetManager = projectBudgetManager;
    this.programManager = programManager;
    this.institutionManager = institutionManager;
    this.phaseManager = phaseManager;
  }


  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {

    masterReport.getParameterValues().put("i8nProjectID", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nProjectTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nPpaPartnersTitle", this.getText("ppaPartners.title"));
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nCoas", this.getText("deliverable.coas"));
    masterReport.getParameterValues().put("i8nRegions", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nTotalW1W2", this.getText("budgetPartner.w1w2"));
    masterReport.getParameterValues().put("i8nPercentajeW1W2",
      this.getText("budgetPartner.w1w2Percentage") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nGenderW1W2",
      this.getText("budgetPartner.gender") + " " + this.getText("projectsList.W1W2projectBudget"));
    masterReport.getParameterValues().put("i8nTotalW1W2Co",
      this.getText("searchTerms.totalBudget") + this.getText("budget.w1w2cofinancing"));
    masterReport.getParameterValues().put("i8nPercentajeW1W2Co",
      this.getText("budget.w1w2cofinancing") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nGenderW1W2Co",
      this.getText("budgetPartner.gender") + " " + this.getText("budget.w1w2cofinancing"));
    masterReport.getParameterValues().put("i8nGrandTotalW1W2", this.getText("budgetPartner.totalW1W2"));
    masterReport.getParameterValues().put("i8nTotalW3",
      this.getText("searchTerms.totalBudget") + this.getText("projectsList.W3projectBudget"));
    masterReport.getParameterValues().put("i8nPercentajeW3",
      this.getText("projectsList.W3projectBudget") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nGenderW3",
      this.getText("budgetPartner.gender") + " " + this.getText("projectsList.W3projectBudget"));
    masterReport.getParameterValues().put("i8nTotalBilateral",
      this.getText("searchTerms.totalBudget") + this.getText("projectsList.BILATERALprojectBudget"));
    masterReport.getParameterValues().put("i8nPercentajeBilateral",
      this.getText("projectsList.BILATERALprojectBudget") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nGenderBilateral",
      this.getText("budgetPartner.gender") + " " + this.getText("projectsList.BILATERALprojectBudget"));
    masterReport.getParameterValues().put("i8nTotalCenter",
      this.getText("searchTerms.totalBudget") + this.getText("budget.centerFunds"));
    masterReport.getParameterValues().put("i8nPercentajeCenter",
      this.getText("budget.centerFunds") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nGenderCenter",
      this.getText("budgetPartner.gender") + " " + this.getText("budget.centerFunds"));

    masterReport.getParameterValues().put("i8nGrandTotalW3BilateralCenter",
      this.getText("budgetPartner.grandTotalW3BilateralCenter"));
    masterReport.getParameterValues().put("i8nGrandTotalGenderW3BilateralCenter",
      this.getText("budgetPartner.grandTotalGenderW3BilateralCenter"));
    masterReport.getParameterValues().put("i8nGrandTotalGenderFundingSoucres",
      this.getText("budgetPartner.grandTotalGenderFundingSoucres"));
    masterReport.getParameterValues().put("i8nGrandTotalBudget", this.getText("budgetPartner.grandTotalBudget"));
    masterReport.getParameterValues().put("i8nSharePercentaje", this.getText("budgetPartner.sharePercentaje"));
    masterReport.getParameterValues().put("i8nFundingSourcesTotal", this.getText("budgetPartner.fundingSourcesTotal"));
    masterReport.getParameterValues().put("i8nW3BilateralCenterW1W2ratio",
      this.getText("budgetPartner.W3BilateralCenterW1W2ratio"));
    masterReport.getParameterValues().put("i8nGenderPercentage", this.getText("budget.genderPercentage"));
    masterReport.getParameterValues().put("i8nW1W2", this.getText("projectsList.W1W2projectBudget"));
    masterReport.getParameterValues().put("i8nW3BilateralCenter", this.getText("budgetPartner.W3BilateralCenter"));
    masterReport.getParameterValues().put("i8nAllFundingSources", this.getText("budgetPartner.allFundingSources"));
    masterReport.getParameterValues().put("i8nGrandTotalGenderW1W2",
      this.getText("budgetPartner.grandTotalGenderW1W2"));
    masterReport.getParameterValues().put("i8nGrandTotal", this.getText("budgetPartner.grandTotal"));

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
        manager.createDirectly(this.getClass().getResource("/pentaho/budgetperpartner.prpt"), MasterReport.class);

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
      // Create new empty sub-report hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the sub-reports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // Uncomment to see which Sub-reports are detecting the method getAllSubreports
      // System.out.println("Pentaho SubReports: " + hm);

      this.fillSubreport((SubReport) hm.get("budgetperpartner_details"), "budgetperpartner_details");
      // Sort projectList by ProjectId
      allProjectsBudgets = this.sortProjectByComparator(allProjectsBudgets);
      this.fillSubreport((SubReport) hm.get("summaryByProject"), "summaryByProject");
      // Sort partnersList by institution acronym or name
      allPartnersBudgets = this.sortByComparator(allPartnersBudgets);
      this.fillSubreport((SubReport) hm.get("summaryByPPA"), "summaryByPPA");
      this.fillSubreport((SubReport) hm.get("partners_budgets"), "partners_budgets");

      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating BudgetByPartners " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "budgetperpartner_details":
        model = this.getBudgetPerPartnersTableModel();
        break;
      case "summaryByProject":
        model = this.getBudgetPerProjectsTableModel();
        break;
      case "summaryByPPA":
        model = this.getPPASummaryTableModel();
        break;
      case "partners_budgets":
        model = this.getPartnersBudgetsSummaryTableModel();
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
          // If report footer is not null check for subreports
          if (((SubReport) e).getReportHeader().getElementCount() != 0) {
            this.getHeaderSubreports(hm, ((SubReport) e).getReportHeader());
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
        // If report footer is not null check for subreports
        if (((SubReport) e).getReportHeader().getElementCount() != 0) {
          this.getHeaderSubreports(hm, ((SubReport) e).getReportHeader());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  private TypedTableModel getBudgetPerPartnersTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"projectId", "projectTitle", "ppaPartner", "flagships", "coas", "regions", "budgetW1W2",
        "genderPeW1W2", "genderW1W2", "budgetW3", "genderPeW3", "genderW3", "budgetBilateral", "genderPeBilateral",
        "genderBilateral", "budgetCenter", "genderPeCenter", "genderCenter", "budgetW1W2Co", "genderPeW1W2Co",
        "genderW1W2Co"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, Double.class,
        Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
        Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
      0);

    List<Project> projects = new ArrayList<>();
    Phase phase = phaseManager.findCycle(APConstants.PLANNING, year, loggedCrp.getId().longValue());
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      projects.add((projectPhase.getProject()));
    }
    for (Project project : projects) {
      // Get PPA institutions with budgets
      List<Institution> institutionsList = new ArrayList<>();

      for (ProjectBudget projectBudget : project.getProjectBudgets().stream().filter(pb -> pb.isActive()
        && pb.getYear() == this.getYear() && pb.getInstitution() != null && pb.getInstitution().isActive())
        .collect(Collectors.toList())) {
        if (this.isPPA(projectBudget.getInstitution())) {
          institutionsList.add(projectBudget.getInstitution());
        }
      }
      // remove duplicates
      Set<Institution> institutions = new LinkedHashSet<Institution>(institutionsList);
      if (institutions.size() > 0) {
        totalProjects++;
      }
      for (Institution institution : institutions) {
        for (ProjectPartner pp : project.getProjectPartners().stream()
          .filter(pp -> pp.isActive() && pp.getInstitution().getId().equals(institution.getId()))
          .collect(Collectors.toList())) {
          String projectTitle = null, ppaPartner = null, flagships = "", coas = "", regions = "";
          Long projectId = null;
          Double budgetW1W2 = null, genderPeW1W2 = null, genderW1W2 = null, budgetW3 = null, genderPeW3 = null,
            genderW3 = null, budgetBilateral = null, genderPeBilateral = null, genderBilateral = null,
            budgetCenter = null, genderPeCenter = null, genderCenter = null, budgetW1W2Co = null, genderPeW1W2Co = null,
            genderW1W2Co = null;

          projectId = project.getId();
          projectTitle = project.getTitle();
          ppaPartner = pp.getComposedName();

          // get Flagships related to the project sorted by acronym
          for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
            .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            if (flagships == null || flagships.isEmpty()) {
              flagships = programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            } else {
              flagships +=
                "\n " + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
            }
          }
          // get CoAs related to the project sorted by acronym
          if (project.getProjectClusterActivities() != null) {
            for (ProjectClusterActivity projectClusterActivity : project.getProjectClusterActivities().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList())) {
              if (coas == null || coas.isEmpty()) {
                coas = projectClusterActivity.getCrpClusterOfActivity().getIdentifier();
              } else {
                coas += ", " + projectClusterActivity.getCrpClusterOfActivity().getIdentifier();
              }
            }
          }

          if (this.hasProgramnsRegions()) {
            List<CrpProgram> regionsList = new ArrayList<>();
            // If has regions, add the regions to regionsArrayList
            // Get Regions related to the project sorted by acronym
            if (this.hasProgramnsRegions() != false) {
              for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
                .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
                .filter(c -> c.isActive()
                  && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
                .collect(Collectors.toList())) {
                regionsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
              }
            }

            if (project.getNoRegional() != null && project.getNoRegional()) {
              regions = "Global";
              if (regionsList.size() > 0) {
                LOG.warn("Project is global and has regions selected");
              }
            } else {
              for (CrpProgram crpProgram : regionsList) {
                if (regions.isEmpty()) {
                  regions = crpProgram.getAcronym();
                } else {
                  regions += ", " + crpProgram.getAcronym();
                }
              }
            }
          }

          if (regions.isEmpty()) {
            regions = null;
          }
          if (coas.isEmpty()) {
            coas = null;
          }
          if (flagships.isEmpty()) {
            flagships = null;
          }

          if (hasW1W2Co) {
            budgetW1W2 = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 1, projectId, 3));
            budgetW1W2Co = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 1, projectId, 2));

            genderPeW1W2 = this.getTotalGenderPer(pp.getInstitution().getId(), year, 1, projectId, 3) / 100;
            genderPeW1W2Co = this.getTotalGenderPer(pp.getInstitution().getId(), year, 1, projectId, 2) / 100;

            genderW1W2 = this.getTotalGender(pp.getInstitution().getId(), year, 1, projectId, 3);
            genderW1W2Co = this.getTotalGender(pp.getInstitution().getId(), year, 1, projectId, 2);
          } else {
            budgetW1W2 = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 1, projectId, 1));
            genderPeW1W2 = this.getTotalGenderPer(pp.getInstitution().getId(), year, 1, projectId, 1) / 100;
            genderW1W2 = this.getTotalGender(pp.getInstitution().getId(), year, 1, projectId, 1);
            budgetW1W2Co = 0.0;
            genderPeW1W2Co = 0.0;
            genderW1W2Co = 0.0;
          }


          budgetW3 = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 2, projectId, 1));
          budgetBilateral = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 3, projectId, 1));
          budgetCenter = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 4, projectId, 1));


          genderPeW3 = this.getTotalGenderPer(pp.getInstitution().getId(), year, 2, projectId, 1) / 100;
          genderPeBilateral = this.getTotalGenderPer(pp.getInstitution().getId(), year, 3, projectId, 1) / 100;
          genderPeCenter = this.getTotalGenderPer(pp.getInstitution().getId(), year, 4, projectId, 1) / 100;


          genderW3 = this.getTotalGender(pp.getInstitution().getId(), year, 2, projectId, 1);
          genderBilateral = this.getTotalGender(pp.getInstitution().getId(), year, 3, projectId, 1);
          genderCenter = this.getTotalGender(pp.getInstitution().getId(), year, 4, projectId, 1);

          // Fill institutions and their budgets
          List<Double> budgetList = new ArrayList<Double>();
          // Add institution w1w2 budget
          Double totalBudgetW1W2 =
            allPartnersBudgets.containsKey(institution) ? allPartnersBudgets.get(institution).get(0) : 0.0;
          totalBudgetW1W2 += budgetW1W2 + budgetW1W2Co;
          // Add institution w3bilateralcenter budget
          Double totalBudgetBilateralW3Center =
            allPartnersBudgets.containsKey(institution) ? allPartnersBudgets.get(institution).get(1) : 0.0;
          totalBudgetBilateralW3Center += budgetW3 + budgetBilateral + budgetCenter;
          budgetList.add(totalBudgetW1W2);
          budgetList.add(totalBudgetBilateralW3Center);
          allPartnersBudgets.put(institution, budgetList);
          // End institutions fill

          // Fill projects with their budgets
          List<Double> projectBudgetList = new ArrayList<Double>();
          // Add project w1w2 budget
          Double totalProjectBudgetW1W2 =
            allProjectsBudgets.containsKey(project) ? allProjectsBudgets.get(project).get(0) : 0.0;
          totalProjectBudgetW1W2 += budgetW1W2 + budgetW1W2Co;
          projectBudgetList.add(totalProjectBudgetW1W2);
          // Add project w3bilateralcenter budget
          Double totalProjectBudgetBilateralW3Center =
            allProjectsBudgets.containsKey(project) ? allProjectsBudgets.get(project).get(1) : 0.0;
          totalProjectBudgetBilateralW3Center += budgetW3 + budgetBilateral + budgetCenter;
          projectBudgetList.add(totalProjectBudgetBilateralW3Center);
          // Add projects w1w2 gender
          Double totalProjectGenderW1W2 =
            allProjectsBudgets.containsKey(project) ? allProjectsBudgets.get(project).get(2) : 0.0;
          totalProjectGenderW1W2 += genderW1W2 + genderW1W2Co;
          projectBudgetList.add(totalProjectGenderW1W2);
          // Add projects w3bilateralcenter gender
          Double totalProjectGenderW3BilateralCenter =
            allProjectsBudgets.containsKey(project) ? allProjectsBudgets.get(project).get(3) : 0.0;
          totalProjectGenderW3BilateralCenter += genderW3 + genderBilateral + genderCenter;
          projectBudgetList.add(totalProjectGenderW3BilateralCenter);

          allProjectsBudgets.put(project, projectBudgetList);
          // End projects fill

          model.addRow(new Object[] {projectId, projectTitle, ppaPartner, flagships, coas, regions, budgetW1W2,
            genderPeW1W2, genderW1W2, budgetW3, genderPeW3, genderW3, budgetBilateral, genderPeBilateral,
            genderBilateral, budgetCenter, genderPeCenter, genderCenter, budgetW1W2Co, genderPeW1W2Co, genderW1W2Co});
        }
      }
    }
    return model;
  }


  private TypedTableModel getBudgetPerProjectsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"projectID", "totalw1w2", "totalw3bilateralcenter", "totalw1w2Gender", "totalw3Gender"},
      new Class[] {Long.class, Double.class, Double.class, Double.class, Double.class}, 0);
    for (Project project : allProjectsBudgets.keySet()) {
      String projectID = "P" + project.getId();
      Double totalw1w2 = allProjectsBudgets.get(project).get(0);
      Double totalw3bilateralcenter = allProjectsBudgets.get(project).get(1);
      Double totalw1w2Gender = allProjectsBudgets.get(project).get(2);
      Double totalw3Gender = allProjectsBudgets.get(project).get(3);

      model.addRow(new Object[] {projectID, totalw1w2, totalw3bilateralcenter, totalw1w2Gender, totalw3Gender});
    }
    return model;
  }


  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }


  @Override
  public String getContentType() {
    return "application/xlsx";
  }


  public String getCycle() {
    return cycle;
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
    fileName.append("BudgetPerPartnersSummary-");
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

  private void getHeaderSubreports(HashMap<String, Element> hm, ReportHeader reportHeader) {

    int elementCount = reportHeader.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = reportHeader.getElement(i);
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

  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"center", "date", "year", "crp_id", "regionalAvalaible", "hasW1W2Co"},
        new Class[] {String.class, String.class, Integer.class, Long.class, Boolean.class, Boolean.class});

    String center = loggedCrp.getName();
    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String date = timezone.format(format) + "(GMT" + zone + ")";
    model.addRow(new Object[] {center, date, this.getYear(), loggedCrp.getId(), this.hasProgramnsRegions(), hasW1W2Co});
    return model;
  }

  private TypedTableModel getPartnersBudgetsSummaryTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"partner", "budget"}, new Class[] {String.class, Double.class}, 0);
    for (Institution institution : allPartnersBudgets.keySet()) {
      Double w1w2budget = allPartnersBudgets.get(institution).get(0);
      Double w3bilateralcenterbudget = allPartnersBudgets.get(institution).get(1);
      String partner = institution.getAcronym();

      if (partner == null || partner.isEmpty()) {
        partner = institution.getName();
      }
      model.addRow(new Object[] {partner, w1w2budget + w3bilateralcenterbudget});
    }
    return model;
  }

  private TypedTableModel getPPASummaryTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"partner", "totalw1w2", "totalw3bilateralcenter", "totalAll", "ratio"},
        new Class[] {String.class, Double.class, Double.class, Double.class, Double.class}, 0);
    for (Institution institution : allPartnersBudgets.keySet()) {
      Double w1w2budget = allPartnersBudgets.get(institution).get(0);
      Double w3bilateralcenterbudget = allPartnersBudgets.get(institution).get(1);
      String partner = institution.getAcronym();

      if (partner == null || partner.isEmpty()) {
        partner = institution.getName();
      }
      Double ratio = 0.0;
      if (w1w2budget != 0.0 && w3bilateralcenterbudget != 0) {
        ratio = w3bilateralcenterbudget / w1w2budget;
      }
      model.addRow(
        new Object[] {partner, w1w2budget, w3bilateralcenterbudget, w1w2budget + w3bilateralcenterbudget, ratio});
    }
    return model;
  }


  /**
   * Get total amount per institution year and type
   * 
   * @param institutionId
   * @param year current platform year
   * @param budgetType
   * @return String with the total amount.
   */
  public String getTotalAmount(long institutionId, int year, long budgetType, Long projectId, Integer coFinancing) {
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectId, coFinancing);
  }


  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, long projectID, Integer coFinancing) {

    List<ProjectBudget> budgets =
      projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID, coFinancing);

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0.0;
        double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0.0;

        totalGender = totalGender + (amount * (gender / 100));
      }
    }

    return totalGender;
  }


  /**
   * Get total gender percentaje per institution, year and type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGenderPer(long institutionId, int year, long budgetType, long projectId, Integer coFinancing) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType, projectId, coFinancing);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType, projectId, coFinancing);

    if (dTotalAmount != 0) {
      return (totalGender * 100) / dTotalAmount;
    } else {
      return 0.0;
    }
  }


  public int getYear() {
    return year;
  }

  /**
   * Verify if an institution isPPA or not
   * 
   * @param institution
   * @return boolean with true if is ppa and false if not
   */
  @Override
  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return true;
        }
      }
    }
    return false;
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
      Map<String, Object> parameters = this.getParameters();
      year = Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0])));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.YEAR_REQUEST
        + " parameter. Parameter will be set as CurrentCycleYear. Exception: " + e.getMessage());
      year = this.getCurrentCycleYear();
    }
    // Get cycle
    try {
      Map<String, Object> parameters = this.getParameters();
      cycle = (StringUtils.trim(((String[]) parameters.get(APConstants.CYCLE))[0]));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as CurrentCycle. Exception: "
        + e.getMessage());
      cycle = this.getCurrentCycle();
    }
    hasW1W2Co = this.hasSpecificities(APConstants.CRP_FS_W1W2_COFINANCING);
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle);
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setYear(int year) {
    this.year = year;
  }


  /**
   * method that sort a map list alphabetical
   * 
   * @param unsortMap - map to sort
   * @return
   */
  private HashMap<Institution, List<Double>> sortByComparator(HashMap<Institution, List<Double>> unsortMap) {

    // Convert Map to List
    List<HashMap.Entry<Institution, List<Double>>> list =
      new LinkedList<HashMap.Entry<Institution, List<Double>>>(unsortMap.entrySet());

    // Sort list with comparator, to compare the Map values
    Collections.sort(list, new Comparator<HashMap.Entry<Institution, List<Double>>>() {

      @Override
      public int compare(HashMap.Entry<Institution, List<Double>> o1, HashMap.Entry<Institution, List<Double>> o2) {

        String s1 = o1.getKey().getAcronym() != null && !o1.getKey().getAcronym().isEmpty() ? o1.getKey().getAcronym()
          : o1.getKey().getName();
        String s2 = o2.getKey().getAcronym() != null && !o2.getKey().getAcronym().isEmpty() ? o2.getKey().getAcronym()
          : o2.getKey().getName();

        return (s1.trim()).compareTo(s2.trim());
      }
    });

    // Convert sorted map back to a Map
    HashMap<Institution, List<Double>> sortedMap = new LinkedHashMap<Institution, List<Double>>();
    for (Iterator<HashMap.Entry<Institution, List<Double>>> it = list.iterator(); it.hasNext();) {
      HashMap.Entry<Institution, List<Double>> entry = it.next();
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  /**
   * method that sort a map list alphabetical
   * 
   * @param unsortMap - map to sort
   * @return
   */
  private HashMap<Project, List<Double>> sortProjectByComparator(HashMap<Project, List<Double>> unsortMap) {

    // Convert Map to List
    List<HashMap.Entry<Project, List<Double>>> list =
      new LinkedList<HashMap.Entry<Project, List<Double>>>(unsortMap.entrySet());

    // Sort list with comparator, to compare the Map values
    Collections.sort(list, new Comparator<HashMap.Entry<Project, List<Double>>>() {

      @Override
      public int compare(HashMap.Entry<Project, List<Double>> o1, HashMap.Entry<Project, List<Double>> o2) {

        return (o1.getKey().getId().compareTo(o2.getKey().getId()));
      }
    });

    // Convert sorted map back to a Map
    HashMap<Project, List<Double>> sortedMap = new LinkedHashMap<Project, List<Double>>();
    for (Iterator<HashMap.Entry<Project, List<Double>>> it = list.iterator(); it.hasNext();) {
      HashMap.Entry<Project, List<Double>> entry = it.next();
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }
}
