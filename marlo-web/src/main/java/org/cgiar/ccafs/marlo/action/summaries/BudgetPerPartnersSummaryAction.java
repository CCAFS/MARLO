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
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
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
  // Variables
  private Crp loggedCrp;
  private int year;
  private String cycle;
  // Store total projects
  Integer totalProjects = 0;
  // Store parters budgets HashMap<Institution, List<w1w2,w3bilateralcenter>>
  HashMap<Institution, List<Double>> totalPartners = new HashMap<Institution, List<Double>>();


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
    ProjectBudgetManager projectBudgetManager, CrpProgramManager programManager,
    InstitutionManager institutionManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectBudgetManager = projectBudgetManager;
    this.programManager = programManager;
    this.institutionManager = institutionManager;
  }


  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

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

    // Get details band
    ItemBand masteritemBand = masterReport.getItemBand();
    // Create new empty sub-report hash map
    HashMap<String, Element> hm = new HashMap<String, Element>();
    // method to get all the sub-reports in the prpt and store in the HashMap
    this.getAllSubreports(hm, masteritemBand);
    // Uncomment to see which Sub-reports are detecting the method getAllSubreports
    System.out.println("Pentaho SubReports: " + hm);

    this.fillSubreport((SubReport) hm.get("budgetperpartner_details"), "budgetperpartner_details");
    // Sort partnersList by key
    totalPartners = this.sortByComparator(totalPartners);
    this.fillSubreport((SubReport) hm.get("summaryByPPA"), "summaryByPPA");
    this.fillSubreport((SubReport) hm.get("partners_budgets"), "partners_budgets");

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
      case "budgetperpartner_details":
        model = this.getBudgetPerPartnersTableModel();
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
        "genderBilateral", "budgetCenter", "genderPeCenter", "genderCenter"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, Double.class,
        Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
        Double.class, Double.class, Double.class},
      0);

    for (Project project : this.loggedCrp.getProjects().stream()
      .filter(p -> p.isActive() && p.getStatus() != null
        && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
      .collect(Collectors.toList())) {
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
            budgetCenter = null, genderPeCenter = null, genderCenter = null;

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

          List<CrpParameter> hasRegionsList = new ArrayList<>();
          Boolean hasRegions = false;
          for (CrpParameter hasRegionsParam : project.getCrp().getCrpParameters().stream()
            .filter(cp -> cp.isActive() && cp.getKey().equals(APConstants.CRP_HAS_REGIONS))
            .collect(Collectors.toList())) {
            hasRegionsList.add(hasRegionsParam);
          }

          if (!hasRegionsList.isEmpty()) {
            if (hasRegionsList.size() > 1) {
              LOG.warn("There is for more than 1 key of type: " + APConstants.CRP_HAS_REGIONS);
            }
            hasRegions = Boolean.valueOf(hasRegionsList.get(0).getValue());
          }

          List<CrpProgram> regionsList = new ArrayList<>();
          // If has regions, add the regions to regionsArrayList
          // Get Regions related to the project sorted by acronym
          if (hasRegions != false) {
            for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
              .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
              .filter(
                c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
              .collect(Collectors.toList())) {
              regionsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
            }
          }

          for (CrpProgram crpProgram : regionsList) {
            if (regions.isEmpty()) {
              regions = crpProgram.getAcronym();
            } else {
              regions += ", " + crpProgram.getAcronym();
            }
          }

          if (regions.isEmpty()) {
            regions = null;
          }
          if (coas.isEmpty()) {
            regions = null;
          }
          if (flagships.isEmpty()) {
            regions = null;
          }

          budgetW1W2 = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 1, projectId));
          budgetW3 = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 2, projectId));
          budgetBilateral = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 3, projectId));
          budgetCenter = Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 4, projectId));

          genderPeW1W2 = this.getTotalGenderPer(pp.getInstitution().getId(), year, 1, projectId) / 100;
          genderPeW3 = this.getTotalGenderPer(pp.getInstitution().getId(), year, 2, projectId) / 100;
          genderPeBilateral = this.getTotalGenderPer(pp.getInstitution().getId(), year, 3, projectId) / 100;
          genderPeCenter = this.getTotalGenderPer(pp.getInstitution().getId(), year, 4, projectId) / 100;

          genderW1W2 = this.getTotalGender(pp.getInstitution().getId(), year, 1, projectId);
          genderW3 = this.getTotalGender(pp.getInstitution().getId(), year, 2, projectId);
          genderBilateral = this.getTotalGender(pp.getInstitution().getId(), year, 3, projectId);
          genderCenter = this.getTotalGender(pp.getInstitution().getId(), year, 4, projectId);

          List<Double> list = new ArrayList<Double>();
          // Add institution w1w2 budget
          Double totalBudgetW1W2 = totalPartners.containsKey(institution) ? totalPartners.get(institution).get(0) : 0;
          totalBudgetW1W2 += budgetW1W2;
          // Add institution w3bilateralcenter budget
          Double totalBudgetBilateralW3Center =
            totalPartners.containsKey(institution) ? totalPartners.get(institution).get(1) : 0;
          totalBudgetBilateralW3Center += budgetW3 + budgetBilateral + budgetCenter;
          list.add(totalBudgetW1W2);
          list.add(totalBudgetBilateralW3Center);

          totalPartners.put(institution, list);

          model.addRow(new Object[] {projectId, projectTitle, ppaPartner, flagships, coas, regions, budgetW1W2,
            genderPeW1W2, genderW1W2, budgetW3, genderPeW3, genderW3, budgetBilateral, genderPeBilateral,
            genderBilateral, budgetCenter, genderPeCenter, genderCenter});
        }
      }
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
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "crp_id", "regionalAvalaible"},
      new Class[] {String.class, String.class, Integer.class, Long.class, Boolean.class});

    String center = loggedCrp.getName();
    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String date = timezone.format(format) + "(GMT" + zone + ")";
    // Verify if the crp has regions avalaible
    List<CrpParameter> hasRegionsList = new ArrayList<>();
    Boolean hasRegions = false;
    for (CrpParameter hasRegionsParam : this.loggedCrp.getCrpParameters().stream()
      .filter(cp -> cp.isActive() && cp.getKey().equals(APConstants.CRP_HAS_REGIONS)).collect(Collectors.toList())) {
      hasRegionsList.add(hasRegionsParam);
    }

    if (!hasRegionsList.isEmpty()) {
      if (hasRegionsList.size() > 1) {
        LOG.warn("There is for more than 1 key of type: " + APConstants.CRP_HAS_REGIONS);
      }
      hasRegions = Boolean.valueOf(hasRegionsList.get(0).getValue());
    }

    model.addRow(new Object[] {center, date, this.getYear(), loggedCrp.getId(), hasRegions});
    return model;
  }

  private TypedTableModel getPartnersBudgetsSummaryTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"partner", "budget"}, new Class[] {String.class, Double.class}, 0);
    for (Institution institution : totalPartners.keySet()) {
      Double w1w2budget = totalPartners.get(institution).get(0);
      Double w3bilateralcenterbudget = totalPartners.get(institution).get(1);
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
    for (Institution institution : totalPartners.keySet()) {
      Double w1w2budget = totalPartners.get(institution).get(0);
      Double w3bilateralcenterbudget = totalPartners.get(institution).get(1);
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
  public String getTotalAmount(long institutionId, int year, long budgetType, Long projectId) {
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectId);
  }


  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, long projectID) {

    List<ProjectBudget> budgets = projectBudgetManager.getByParameters(institutionId, year, budgetType, projectID);

    double totalGender = 0;
    if (budgets != null) {
      for (ProjectBudget projectBudget : budgets) {
        double amount = projectBudget.getAmount() != null ? projectBudget.getAmount() : 0;
        double gender = projectBudget.getGenderPercentage() != null ? projectBudget.getGenderPercentage() : 0;

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
  public double getTotalGenderPer(long institutionId, int year, long budgetType, long projectId) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType, projectId);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType, projectId);

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
    // Get cycle
    try {
      Map<String, Object> parameters = this.getParameters();
      cycle = (StringUtils.trim(((String[]) parameters.get(APConstants.CYCLE))[0]));
    } catch (Exception e) {
      cycle = this.getCurrentCycle();
    }
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
}
