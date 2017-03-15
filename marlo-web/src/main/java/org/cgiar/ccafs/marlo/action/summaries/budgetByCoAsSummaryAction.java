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
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

public class budgetByCoAsSummaryAction extends BaseAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(budgetByCoAsSummaryAction.class);

  // Variables
  private Crp loggedCrp;
  private int year;
  private String cycle;


  // Managers
  private CrpManager crpManager;


  private CrpProgramManager programManager;
  private ProjectBudgetManager projectBudgetManager;
  private InstitutionManager institutionManager;


  // XLSX bytes
  private byte[] bytesXLSX;


  // Streams
  InputStream inputStream;


  @Inject
  public budgetByCoAsSummaryAction(APConfig config, CrpManager crpManager, CrpProgramManager programManager,
    ProjectBudgetManager projectBudgetManager, InstitutionManager institutionManager) {
    super(config);
    this.crpManager = crpManager;
    this.programManager = programManager;
    this.projectBudgetManager = projectBudgetManager;
    this.institutionManager = institutionManager;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    Resource reportResource =
      manager.createDirectly(this.getClass().getResource("/pentaho/budgetByCoAsSummary.prpt"), MasterReport.class);

    MasterReport masterReport = (MasterReport) reportResource.getResource();
    String center = loggedCrp.getName();

    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String current_date = timezone.format(format) + "(GMT" + zone + ")";

    // Verify if the crp has regions avalaible
    List<CrpParameter> hasRegionsList = new ArrayList<>();
    Boolean regionalAvailable = false;
    for (CrpParameter hasRegionsParam : this.loggedCrp.getCrpParameters().stream()
      .filter(cp -> cp.isActive() && cp.getKey().equals(APConstants.CRP_HAS_REGIONS)).collect(Collectors.toList())) {
      hasRegionsList.add(hasRegionsParam);
    }

    if (!hasRegionsList.isEmpty()) {
      if (hasRegionsList.size() > 1) {
        LOG.warn("There is for more than 1 key of type: " + APConstants.CRP_HAS_REGIONS);
      }
      regionalAvailable = Boolean.valueOf(hasRegionsList.get(0).getValue());
    }

    // Set Main_Query
    CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
    String masterQueryName = "main";
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
    TypedTableModel model = this.getMasterTableModel(center, current_date, regionalAvailable);
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

    this.fillSubreport((SubReport) hm.get("projects_coas"), "projects_coas");


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
      case "projects_coas":
        model = this.getProjectsCoAsTableModel();
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

  public ProjectBudgetsCluserActvity getBudgetbyCoa(Long activitiyId, long type, Project project) {

    for (ProjectBudgetsCluserActvity pb : project.getProjectBudgetsCluserActvities().stream()
      .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getCrpClusterOfActivity() != null
        && pb.getCrpClusterOfActivity().getId() == activitiyId && type == pb.getBudgetType().getId())
      .collect(Collectors.toList())) {
      return pb;
    }
    return null;
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
    fileName.append("BudgetByCoAsSummary-");
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

  private TypedTableModel getMasterTableModel(String center, String date, Boolean regionalAvailable) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "regionalAvailable", "budget_gender"},
      new Class[] {String.class, String.class, Boolean.class, Boolean.class});
    Boolean hasGender = false;
    try {
      hasGender = Integer.parseInt(loggedCrp.getCrpParameters().stream()
        .filter(cp -> cp.isActive() && cp.getKey().equals(APConstants.CRP_BUDGET_GENDER)).collect(Collectors.toList())
        .get(0).getValue()) == 1;
    } catch (Exception e) {
      hasGender = false;
    }
    model.addRow(new Object[] {center, date, regionalAvailable, hasGender});
    return model;
  }

  private TypedTableModel getProjectsCoAsTableModel() {
    DecimalFormat df = new DecimalFormat("###,###.00");
    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "title", "project_url", "flagships", "regions", "coa", "total_w1w2", "w1w2_total_per",
        "w1w2_of_total", "gender_w1w2", "w1w2_gender_per", "w1w2_of_gender", "total_w3", "w3_total_per", "w3_of_total",
        "gender_w3", "w3_gender_per", "w3_of_gender", "total_bilateral", "bilateral_total_per", "bilateral_of_total",
        "gender_bilateral", "bilateral_gender_per", "bilateral_of_gender", "total_center", "center_total_per",
        "center_of_total", "gender_center", "center_gender_per", "center_of_gender"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, Double.class,
        Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
        Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
        Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class},
      0);
    // Get amount of total and gender
    Double total_w1w2 = 0.0, total_w3 = 0.0, total_bilateral = 0.0, total_center = 0.0, total_w1w2_gender = 0.0,
      total_w3_gender = 0.0, total_bilateral_gender = 0.0, total_center_gender = 0.0, w1w2_per_total = 0.0,
      w3_per_total = 0.0, bilateral_per_total = 0.0, center_per_total = 0.0, w1w2_per_gender = 0.0, w3_per_gender = 0.0,
      bilateral_per_gender = 0.0, center_per_gender = 0.0;

    for (Project project : loggedCrp.getProjects().stream().filter(p -> p.isActive() && p.getStatus().intValue() == 2)
      .collect(Collectors.toList())) {

      total_w1w2_gender = 0.0;
      total_w3_gender = 0.0;
      total_bilateral_gender = 0.0;
      total_center_gender = 0.0;
      w1w2_per_total = 0.0;
      w3_per_total = 0.0;
      bilateral_per_total = 0.0;
      center_per_total = 0.0;
      w1w2_per_gender = 0.0;
      w3_per_gender = 0.0;
      bilateral_per_gender = 0.0;
      center_per_gender = 0.0;

      // get total budget per year
      total_w1w2 = this.getTotalYear(year, 1, project);
      total_w3 = this.getTotalYear(year, 2, project);
      total_bilateral = this.getTotalYear(year, 3, project);
      total_center = this.getTotalYear(year, 4, project);

      // get total gender per year
      for (ProjectPartner pp : project.getProjectPartners().stream().filter(pp -> pp.isActive())
        .collect(Collectors.toList())) {
        // System.out.println(pp.getInstitution().getComposedName());
        if (this.isPPA(pp.getInstitution())) {
          total_w1w2_gender += this.getTotalGender(pp.getInstitution().getId(), year, 1, project);
          total_w3_gender += this.getTotalGender(pp.getInstitution().getId(), year, 2, project);
          total_bilateral_gender += this.getTotalGender(pp.getInstitution().getId(), year, 3, project);
          total_center_gender += this.getTotalGender(pp.getInstitution().getId(), year, 4, project);
        }
      }

      List<ProjectClusterActivity> coAs = new ArrayList<>();
      coAs = project.getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());

      if (coAs.size() == 1) {
        String project_id = "", title = "", project_url = "", flagships = "", regions = "", coa = "";

        project_id = project.getId().toString();
        project_url = "P" + project.getId().toString();
        title = project.getTitle();
        // get Flagships related to the project sorted by acronym
        List<CrpProgram> flagshipsList = new ArrayList<>();
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())) {
          flagshipsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
        }

        for (CrpProgram crpProgram : flagshipsList) {
          if (flagships.isEmpty()) {
            flagships = crpProgram.getAcronym();
          } else {
            flagships += ", " + crpProgram.getAcronym();
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

        coa = coAs.get(0).getCrpClusterOfActivity().getComposedName();


        if (total_w1w2 != 0.0) {
          w1w2_per_total = 1.0;
        }
        if (total_w3 != 0.0) {
          w3_per_total = 1.0;
        }
        if (total_bilateral != 0.0) {
          bilateral_per_total = 1.0;
        }
        if (total_center != 0.0) {
          center_per_total = 1.0;
        }

        if (total_w1w2_gender != 0.0) {
          w1w2_per_gender = 1.0;
        }
        if (total_w3_gender != 0.0) {
          w3_per_gender = 1.0;
        }
        if (total_bilateral_gender != 0.0) {
          bilateral_per_gender = 1.0;
        }
        if (total_center_gender != 0.0) {
          center_per_gender = 1.0;
        }


        model.addRow(new Object[] {project_id, title, project_url, flagships, regions, coa, total_w1w2, w1w2_per_total,
          total_w1w2, total_w1w2_gender, w1w2_per_gender, total_w1w2_gender, total_w3, w3_per_total, total_w3,
          total_w3_gender, w3_per_gender, total_w3_gender, total_bilateral, bilateral_per_total, total_bilateral,
          total_bilateral_gender, bilateral_per_gender, total_bilateral_gender, total_center, center_per_total,
          total_center, total_center_gender, center_per_gender, total_center_gender});
      } else {

        for (ProjectClusterActivity clusterActivity : coAs) {
          String project_id = "", title = "", project_url = "", flagships = "", regions = "", coa = "";

          Double w1w2 = 0.0;
          Double w3 = 0.0;
          Double bilateral = 0.0;
          Double center = 0.0;
          Double w1w2_gender = 0.0;
          Double w3_gender = 0.0;
          Double bilateral_gender = 0.0;
          Double center_gender = 0.0;
          w1w2_per_total = 0.0;
          w1w2_per_gender = 0.0;
          w3_per_total = 0.0;
          w3_per_gender = 0.0;
          bilateral_per_total = 0.0;
          bilateral_per_gender = 0.0;
          center_per_total = 0.0;
          center_per_gender = 0.0;

          coa = clusterActivity.getCrpClusterOfActivity().getComposedName();
          project_id = project.getId().toString();
          project_url = "P" + project.getId().toString();
          title = project.getTitle();
          // get Flagships related to the project sorted by acronym
          List<CrpProgram> flagshipsList = new ArrayList<>();
          for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
            .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            flagshipsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
          }

          for (CrpProgram crpProgram : flagshipsList) {
            if (flagships.isEmpty()) {
              flagships = crpProgram.getAcronym();
            } else {
              flagships += ", " + crpProgram.getAcronym();
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


          ProjectBudgetsCluserActvity w1w2pb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 1, project);
          ProjectBudgetsCluserActvity w3pb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 2, project);
          ProjectBudgetsCluserActvity bilateralpb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 3, project);
          ProjectBudgetsCluserActvity centerpb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 4, project);


          if (w1w2pb != null) {
            w1w2 = (w1w2pb.getAmount() * total_w1w2) / 100;
            w1w2_per_total = w1w2pb.getAmount() / 100;
            w1w2_gender = (w1w2pb.getGenderPercentage() * total_w1w2_gender) / 100;
            w1w2_per_gender = w1w2pb.getGenderPercentage() / 100;

          }

          if (w3pb != null) {
            w3 = (w3pb.getAmount() * total_w3) / 100;
            w3_per_total = w3pb.getAmount() / 100;
            w3_gender = (w3pb.getGenderPercentage() * total_w3_gender) / 100;
            w3_per_gender = w3pb.getGenderPercentage() / 100;
          }

          if (bilateralpb != null) {
            bilateral = (bilateralpb.getAmount() * total_bilateral) / 100;
            bilateral_per_total = bilateralpb.getAmount() / 100;
            bilateral_gender = (bilateralpb.getGenderPercentage() * total_bilateral_gender) / 100;
            bilateral_per_gender = bilateralpb.getGenderPercentage() / 100;
          }
          if (centerpb != null) {
            center = (centerpb.getAmount() * total_center) / 100;
            center_per_total = centerpb.getAmount() / 100;
            center_gender = (centerpb.getGenderPercentage() * total_center_gender) / 100;
            center_per_gender = centerpb.getGenderPercentage() / 100;
          }


          model.addRow(new Object[] {project_id, title, project_url, flagships, regions, coa, total_w1w2,
            w1w2_per_total, w1w2, total_w1w2_gender, w1w2_per_gender, w1w2_gender, total_w3, w3_per_total, w3,
            total_w3_gender, w3_per_gender, w3_gender, total_bilateral, bilateral_per_total, bilateral,
            total_bilateral_gender, bilateral_per_gender, bilateral_gender, total_center, center_per_total, center,
            total_center_gender, center_per_gender, center_gender});
        }
      }
    }
    return model;
  }

  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType, Project project) {

    List<ProjectBudget> budgets =
      projectBudgetManager.getByParameters(institutionId, year, budgetType, project.getId());

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

  public double getTotalYear(int year, long type, Project project) {
    double total = 0;

    for (ProjectBudget pb : project.getProjectBudgets().stream()
      .filter(
        pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null && pb.getBudgetType().getId() == type)
      .collect(Collectors.toList())) {
      total = total + pb.getAmount();
    }
    return total;
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

}
