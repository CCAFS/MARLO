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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
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

public class budgetByCoAsSummaryAction extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(budgetByCoAsSummaryAction.class);

  // Parameters
  private long startTime;

  // Managers
  private CrpProgramManager programManager;
  private ProjectBudgetManager projectBudgetManager;
  private InstitutionManager institutionManager;

  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;


  @Inject
  public budgetByCoAsSummaryAction(APConfig config, CrpManager crpManager, CrpProgramManager programManager,
    ProjectBudgetManager projectBudgetManager, InstitutionManager institutionManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
    this.programManager = programManager;
    this.projectBudgetManager = projectBudgetManager;
    this.institutionManager = institutionManager;
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
    masterReport.getParameterValues().put("i8nFlagships", this.getText("project.Flagships"));
    masterReport.getParameterValues().put("i8nRegions", this.getText("project.Regions"));
    masterReport.getParameterValues().put("i8nClusterOfActivity", this.getText("global.sClusterOfActivities"));
    masterReport.getParameterValues().put("i8nTotalW1W2", this.getText("budgetPartner.totalW1W2"));
    masterReport.getParameterValues().put("i8nPercentajeW1W2",
      this.getText("projectsList.W1W2projectBudget") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nW1W2OfTotal", this.getText("budgetCoa.w1w2OfTotal"));
    masterReport.getParameterValues().put("i8nGenderW1W2",
      this.getText("budgetPartner.gender") + " " + this.getText("projectsList.W1W2projectBudget"));
    masterReport.getParameterValues().put("i8nPercentajeGenderW1W2",
      this.getText("projectsList.W1W2projectBudget") + " " + this.getText("budgetCoa.percentajeGender"));
    masterReport.getParameterValues().put("i8nW1W2OfGender", this.getText("budgetCoa.w1w2OfGender"));
    masterReport.getParameterValues().put("i8nTotalW3", this.getText("budgetCoa.totalW3"));
    masterReport.getParameterValues().put("i8nPercentajeW3",
      this.getText("projectsList.W3projectBudget") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nW3OfTotal", this.getText("budgetCoa.w3OfTotal"));
    masterReport.getParameterValues().put("i8nGenderW3",
      this.getText("budgetPartner.gender") + " " + this.getText("projectsList.W3projectBudget"));
    masterReport.getParameterValues().put("i8nPercentajeGenderW3",
      this.getText("projectsList.W3projectBudget") + " " + this.getText("budgetCoa.percentajeGender"));
    masterReport.getParameterValues().put("i8nW3OfGender", this.getText("budgetCoa.w3OfGender"));
    masterReport.getParameterValues().put("i8nTotalBilateral", this.getText("budgetCoa.totalBilateral"));
    masterReport.getParameterValues().put("i8nPercentajeBilateral",
      this.getText("projectsList.BILATERALprojectBudget") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nBilateralOfTotal", this.getText("budgetCoa.bilateralOfTotal"));
    masterReport.getParameterValues().put("i8nGenderBilateral",
      this.getText("budgetPartner.gender") + " " + this.getText("projectsList.BILATERALprojectBudget"));
    masterReport.getParameterValues().put("i8nPercentajeGenderBilateral",
      this.getText("projectsList.BILATERALprojectBudget") + " " + this.getText("budgetCoa.percentajeGender"));
    masterReport.getParameterValues().put("i8nBilateralOfGender", this.getText("budgetCoa.bilateralOfGender"));
    masterReport.getParameterValues().put("i8nTotalCenter", this.getText("budgetCoa.totalCenter"));
    masterReport.getParameterValues().put("i8nPercentajeCenter",
      this.getText("budget.centerFunds") + " " + this.getText("budgetPartner.percentaje"));
    masterReport.getParameterValues().put("i8nCenterOfTotal", this.getText("budgetCoa.centerOfTotal"));
    masterReport.getParameterValues().put("i8nGenderCenter",
      this.getText("budgetPartner.gender") + " " + this.getText("budget.centerFunds"));
    masterReport.getParameterValues().put("i8nPercentajeGenderCenter",
      this.getText("budget.centerFunds") + " " + this.getText("budgetCoa.percentajeGender"));
    masterReport.getParameterValues().put("i8nCenterOfGender", this.getText("budgetCoa.centerOfGender"));
    masterReport.getParameterValues().put("i8nCoasTitle", this.getText("projectBudgetByCoAs.title"));

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
        manager.createDirectly(this.getClass().getResource("/pentaho/budgetByCoAsSummary.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();

      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String currentDate = timezone.format(format) + "(GMT" + zone + ")";

      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, currentDate);
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

      this.fillSubreport((SubReport) hm.get("projects_coas"), "projects_coas");


      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating BudgetByCoAs " + e.getMessage());
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
      case "projects_coas":
        model = this.getProjectsCoAsTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }


  public ProjectBudgetsCluserActvity getBudgetbyCoa(Long activitiyId, long type, Project project) {

    for (ProjectBudgetsCluserActvity pb : project.getProjectBudgetsCluserActvities().stream()
      .filter(pb -> pb.isActive() && pb.getYear() == this.getSelectedYear() && pb.getCrpClusterOfActivity() != null
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
    fileName.append("BudgetByCoAsSummary-");
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


  private TypedTableModel getMasterTableModel(String center, String date) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "regionalAvailable", "budget_gender"},
      new Class[] {String.class, String.class, Boolean.class, Boolean.class});
    Boolean hasGender = false;
    try {
      hasGender = this.hasSpecificities(APConstants.CRP_BUDGET_GENDER);

    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CRP_BUDGET_GENDER + " parameter. Parameter was set null. Exception: "
        + e.getMessage());
      hasGender = false;
    }
    model.addRow(new Object[] {center, date, this.hasProgramnsRegions(), hasGender});
    return model;
  }

  private TypedTableModel getProjectsCoAsTableModel() {
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
    Double totalW1w = 0.0, totalW3 = 0.0, totalBilateral = 0.0, totalCenter = 0.0, totalW1w2Gender = 0.0,
      totalW3Gender = 0.0, totalBilateralGender = 0.0, totalCenterGender = 0.0, w1w2PerTotal = 0.0, w3PerTotal = 0.0,
      bilateralPerTotal = 0.0, centerPerTotal = 0.0, w1w2PerGender = 0.0, w3PerGender = 0.0, bilateralPerGender = 0.0,
      centerPerGender = 0.0;

    List<Project> projects = new ArrayList<>();
    for (ProjectPhase projectPhase : this.getSelectedPhase().getProjectPhases()) {
      projects.add((projectPhase.getProject()));
    }
    for (Project project : projects) {

      totalW1w2Gender = 0.0;
      totalW3Gender = 0.0;
      totalBilateralGender = 0.0;
      totalCenterGender = 0.0;
      w1w2PerTotal = 0.0;
      w3PerTotal = 0.0;
      bilateralPerTotal = 0.0;
      centerPerTotal = 0.0;
      w1w2PerGender = 0.0;
      w3PerGender = 0.0;
      bilateralPerGender = 0.0;
      centerPerGender = 0.0;

      // get total budget per year
      totalW1w = this.getTotalYear(this.getSelectedYear(), 1, project);
      totalW3 = this.getTotalYear(this.getSelectedYear(), 2, project);
      totalBilateral = this.getTotalYear(this.getSelectedYear(), 3, project);
      totalCenter = this.getTotalYear(this.getSelectedYear(), 4, project);

      // get total gender per year
      for (ProjectPartner pp : project.getProjectPartners().stream().filter(pp -> pp.isActive())
        .collect(Collectors.toList())) {
        // System.out.println(pp.getInstitution().getComposedName());
        if (this.isPPA(pp.getInstitution())) {
          totalW1w2Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 1, project, 1);
          totalW3Gender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 2, project, 1);
          totalBilateralGender +=
            this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 3, project, 1);
          totalCenterGender += this.getTotalGender(pp.getInstitution().getId(), this.getSelectedYear(), 4, project, 1);
        }
      }

      List<ProjectClusterActivity> coAs = new ArrayList<>();
      coAs = project.getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());

      if (coAs.size() == 1) {
        String projectId = "", title = "", projectUrl = "", flagships = "", regions = "", coa = "";

        projectId = project.getId().toString();
        projectUrl = "P" + project.getId().toString();
        title = project.getProjecInfoPhase(this.getActualPhase()).getTitle();
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

        if (this.hasProgramnsRegions()) {
          List<CrpProgram> regionsList = new ArrayList<>();
          // If has regions, add the regions to regionsArrayList
          // Get Regions related to the project sorted by acronym
          if (this.hasProgramnsRegions() != false) {
            for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
              .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
              .filter(
                c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
              .collect(Collectors.toList())) {
              regionsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
            }
          }
          if (project.getProjecInfoPhase(this.getActualPhase()).getNoRegional() != null
            && project.getProjecInfoPhase(this.getActualPhase()).getNoRegional()) {
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
        coa = coAs.get(0).getCrpClusterOfActivity().getComposedName();
        if (totalW1w != 0.0) {
          w1w2PerTotal = 1.0;
        }
        if (totalW3 != 0.0) {
          w3PerTotal = 1.0;
        }
        if (totalBilateral != 0.0) {
          bilateralPerTotal = 1.0;
        }
        if (totalCenter != 0.0) {
          centerPerTotal = 1.0;
        }

        if (totalW1w2Gender != 0.0) {
          w1w2PerGender = 1.0;
        }
        if (totalW3Gender != 0.0) {
          w3PerGender = 1.0;
        }
        if (totalBilateralGender != 0.0) {
          bilateralPerGender = 1.0;
        }
        if (totalCenterGender != 0.0) {
          centerPerGender = 1.0;
        }


        model.addRow(new Object[] {projectId, title, projectUrl, flagships, regions, coa, totalW1w, w1w2PerTotal,
          totalW1w, totalW1w2Gender, w1w2PerGender, totalW1w2Gender, totalW3, w3PerTotal, totalW3, totalW3Gender,
          w3PerGender, totalW3Gender, totalBilateral, bilateralPerTotal, totalBilateral, totalBilateralGender,
          bilateralPerGender, totalBilateralGender, totalCenter, centerPerTotal, totalCenter, totalCenterGender,
          centerPerGender, totalCenterGender});
      } else {

        for (ProjectClusterActivity clusterActivity : coAs) {
          String projectId = "", title = "", projectUrl = "", flagships = "", regions = "", coa = "";

          Double w1w2 = 0.0;
          Double w3 = 0.0;
          Double bilateral = 0.0;
          Double center = 0.0;
          Double w1w2Gender = 0.0;
          Double w3Gender = 0.0;
          Double bilateralGender = 0.0;
          Double centerGender = 0.0;
          w1w2PerTotal = 0.0;
          w1w2PerGender = 0.0;
          w3PerTotal = 0.0;
          w3PerGender = 0.0;
          bilateralPerTotal = 0.0;
          bilateralPerGender = 0.0;
          centerPerTotal = 0.0;
          centerPerGender = 0.0;

          coa = clusterActivity.getCrpClusterOfActivity().getComposedName();
          projectId = project.getId().toString();
          projectUrl = "P" + project.getId().toString();
          title = project.getProjecInfoPhase(this.getActualPhase()).getTitle();
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


          List<CrpProgram> regionsList = new ArrayList<>();
          // If has regions, add the regions to regionsArrayList
          // Get Regions related to the project sorted by acronym
          if (this.hasProgramnsRegions() != false) {
            for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
              .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
              .filter(
                c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
              .collect(Collectors.toList())) {
              regionsList.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
            }
          }
          if (project.getProjecInfoPhase(this.getActualPhase()).getNoRegional() != null
            && project.getProjecInfoPhase(this.getActualPhase()).getNoRegional()) {
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


          ProjectBudgetsCluserActvity w1w2pb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 1, project);
          ProjectBudgetsCluserActvity w3pb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 2, project);
          ProjectBudgetsCluserActvity bilateralpb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 3, project);
          ProjectBudgetsCluserActvity centerpb =
            this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), 4, project);


          if (w1w2pb != null) {
            w1w2 = (w1w2pb.getAmount() * totalW1w) / 100;
            w1w2PerTotal = w1w2pb.getAmount() / 100;
            w1w2Gender = (w1w2pb.getGenderPercentage() * totalW1w2Gender) / 100;
            w1w2PerGender = w1w2pb.getGenderPercentage() / 100;

          }

          if (w3pb != null) {
            w3 = (w3pb.getAmount() * totalW3) / 100;
            w3PerTotal = w3pb.getAmount() / 100;
            w3Gender = (w3pb.getGenderPercentage() * totalW3Gender) / 100;
            w3PerGender = w3pb.getGenderPercentage() / 100;
          }

          if (bilateralpb != null) {
            bilateral = (bilateralpb.getAmount() * totalBilateral) / 100;
            bilateralPerTotal = bilateralpb.getAmount() / 100;
            bilateralGender = (bilateralpb.getGenderPercentage() * totalBilateralGender) / 100;
            bilateralPerGender = bilateralpb.getGenderPercentage() / 100;
          }
          if (centerpb != null) {
            center = (centerpb.getAmount() * totalCenter) / 100;
            centerPerTotal = centerpb.getAmount() / 100;
            centerGender = (centerpb.getGenderPercentage() * totalCenterGender) / 100;
            centerPerGender = centerpb.getGenderPercentage() / 100;
          }


          model.addRow(new Object[] {projectId, title, projectUrl, flagships, regions, coa, totalW1w, w1w2PerTotal,
            w1w2, totalW1w2Gender, w1w2PerGender, w1w2Gender, totalW3, w3PerTotal, w3, totalW3Gender, w3PerGender,
            w3Gender, totalBilateral, bilateralPerTotal, bilateral, totalBilateralGender, bilateralPerGender,
            bilateralGender, totalCenter, centerPerTotal, center, totalCenterGender, centerPerGender, centerGender});
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
  public double getTotalGender(long institutionId, int year, long budgetType, Project project, Integer coFinancing) {

    List<ProjectBudget> budgets =
      projectBudgetManager.getByParameters(institutionId, year, budgetType, project.getId(), coFinancing);

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
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

}
