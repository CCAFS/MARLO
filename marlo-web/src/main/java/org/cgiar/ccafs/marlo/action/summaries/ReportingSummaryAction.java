/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.config.PentahoListener;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Submission;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */
public class ReportingSummaryAction extends BaseAction implements Summary {

  private static final long serialVersionUID = -624982650510682813L;
  private static Logger LOG = LoggerFactory.getLogger(ReportingSummaryAction.class);

  private CrpManager crpManager;

  // Front-end
  private long projectID;
  private int year;
  private String cycle;


  // XLS bytes
  private byte[] bytesPDF;


  // Streams
  InputStream inputStream;


  // Managers
  private Crp loggedCrp;


  private ProjectManager projectManager;
  private CrpProgramManager programManager;
  private InstitutionManager institutionManager;
  private ProjectBudgetManager projectBudgetManager;
  private LocElementManager locElementManager;
  // Project from DB
  private Project project;

  @Inject
  public ReportingSummaryAction(APConfig config, CrpManager crpManager, ProjectManager projectManager,
    CrpProgramManager programManager, InstitutionManager institutionManager, ProjectBudgetManager projectBudgetManager,
    LocElementManager locElementManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.programManager = programManager;
    this.institutionManager = institutionManager;
    this.projectBudgetManager = projectBudgetManager;
    this.locElementManager = locElementManager;
  }

  @Override
  public String execute() throws Exception {

    // Calculate time to generate report
    long startTime = System.currentTimeMillis();
    // System.out.println("Inicia conteo en: " + (startTime - System.currentTimeMillis()));


    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager = // new ResourceManager();
      (ResourceManager) ServletActionContext.getServletContext().getAttribute(PentahoListener.KEY_NAME);
    // manager.registerDefaults();
    try {
      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/project-description.prpt"), MasterReport.class);

      // Get main report
      MasterReport masterReport = (MasterReport) reportResource.getResource();

      // Get project from DB and general parameters
      project = projectManager.getProjectById(projectID);
      String masterQueryName = "Main_Query";
      int year = 0;
      String cycle = "";
      year = this.getYear();
      if (this.getCycle() != null) {
        cycle = this.getCycle();
      }

      // General list to store parameters of Subreports
      List<Object> args = new LinkedList<>();
      // Verify if the project was found
      if (project == null) {
        // Set Main_Query
        CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
        TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
        TypedTableModel model = this.getNullMasterTableModel(cycle, year);
        sdf.addTable(masterQueryName, model);
        masterReport.setDataFactory(cdf);

      } else {
        // Get details band
        ItemBand masteritemBand = masterReport.getItemBand();
        // Create new empty subreport hash map
        HashMap<String, Element> hm = new HashMap<String, Element>();
        // method to get all the subreports in the prpt and store in the HashMap
        this.getAllSubreports(hm, masteritemBand);
        // Uncomment to see which Subreports are detecting the method getAllSubreports
        // System.out.println("Pentaho SubReports: " + hm);

        // get project leader
        ProjectPartner projectLeader = project.getLeader();
        // get Flagships related to the project sorted by acronym
        List<CrpProgram> flagships = new ArrayList<>();
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())) {
          flagships.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
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


        List<CrpProgram> regions = new ArrayList<>();
        // If has regions, add the regions to regionsArrayList
        // Get Regions related to the project sorted by acronym
        if (hasRegions != false) {
          for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
            .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            regions.add(programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()));
          }
        }

        // Set Main_Query
        CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
        TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
        TypedTableModel model = this.getMasterTableModel(flagships, regions, projectLeader, cycle, year);
        sdf.addTable(masterQueryName, model);
        masterReport.setDataFactory(cdf);

        // Start Setting Subreports

        // Subreport Description
        args.add(projectLeader);
        args.add(cycle);
        args.add(hasRegions);
        this.fillSubreport((SubReport) hm.get("description"), "description", args);
        // Description Flagships
        args.clear();
        args.add(flagships);
        this.fillSubreport((SubReport) hm.get("Flagships"), "description_flagships", args);
        // Description Regions

        if (hasRegions != false) {
          args.clear();
          args.add(regions);
          this.fillSubreport((SubReport) hm.get("Regions"), "description_regions", args);
        }
        // Description CoAs
        args.clear();
        this.fillSubreport((SubReport) hm.get("Description_CoAs"), "description_coas", args);

        // Subreport Partners
        this.fillSubreport((SubReport) hm.get("partners"), "partners_count", args);

        // Subreport Partner Leader
        args.clear();
        args.add(projectLeader);
        this.fillSubreport((SubReport) hm.get("partner_leader"), "institution_leader", args);

        // Subreport Partner Others
        args.clear();
        args.add(projectLeader);
        this.fillSubreport((SubReport) hm.get("partners_others"), "partners_others_ins", args);

        // Note: Contacts for partners are filled by queries inside the prpt
        // Subreport Partner Lessons
        args.clear();
        args.add(cycle);
        this.fillSubreport((SubReport) hm.get("partner_lessons"), "partner_lessons", args);

        // Subreport Locations
        args.clear();
        this.fillSubreport((SubReport) hm.get("locations"), "locations", args);

        // Subreport Outcomes
        args.clear();
        this.fillSubreport((SubReport) hm.get("outcomes"), "outcomes_list", args);

        // Subreport Deliverables
        args.clear();
        args.add(year);
        this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables_list", args);

        // Subreport Activities
        args.clear();
        this.fillSubreport((SubReport) hm.get("activities"), "activities_list", args);

        // Subreport Budgets Summary
        args.clear();
        args.add(year);
        this.fillSubreport((SubReport) hm.get("budgets"), "budget_summary", args);

        // Subreport BudgetsbyPartners
        this.fillSubreport((SubReport) hm.get("budgets_by_partners"), "budgets_by_partners_list", args);

        // Subreport BudgetsbyCoas
        this.fillSubreport((SubReport) hm.get("budgets_by_coas"), "budgets_by_coas_list", args);
      }

      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      long stopTime = System.currentTimeMillis();
      long elapsedTime = stopTime - startTime;
      // System.out.println("Tiempo de ejecución: Error time " + elapsedTime);
      LOG.error("Generating PDF" + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    // System.out.println("Tiempo de ejecución: " + elapsedTime);
    return SUCCESS;

  }

  private void fillSubreport(SubReport subReport, String query, List<Object> args) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "description":
        if (args.get(0) instanceof ProjectPartner) {
          model = this.getDescTableModel((ProjectPartner) args.get(0), (String) args.get(1), (Boolean) args.get(2));
        } else {
          model = this.getDescTableModel(new ProjectPartner(), (String) args.get(1), (Boolean) args.get(2));
        }
        break;
      case "description_flagships":
        model = this.getFLTableModel((List<CrpProgram>) args.get(0));
        break;
      case "description_regions":
        model = this.getRLTableModel((List<CrpProgram>) args.get(0));
        break;
      case "description_coas":
        model = this.getDescCoAsTableModel();
        break;
      case "partners_count":
        model = this.getPartnersTableModel();
        break;
      case "institution_leader":
        if (args.get(0) instanceof ProjectPartner) {
          model = this.getPartnerLeaderTableModel((ProjectPartner) args.get(0));
        } else {
          model = this.getPartnerLeaderTableModel(new ProjectPartner());
        }
        break;
      case "partners_others_ins":
        if (args.get(0) instanceof ProjectPartner) {
          model = this.getPartnersOtherTableModel((ProjectPartner) args.get(0));
        } else {
          model = this.getPartnersOtherTableModel(new ProjectPartner());
        }
        break;
      case "partner_lessons":
        model = this.getPartnersLessonsTableModel((String) args.get(0));
        break;
      case "locations":
        model = this.getLocationsTableModel();
        break;
      case "outcomes_list":
        model = this.getOutcomesTableModel();
        break;
      case "deliverables_list":
        model = this.getDeliverablesTableModel((int) args.get(0));
        break;
      case "activities_list":
        model = this.getActivitiesTableModel();
        break;
      case "budget_summary":
        model = this.getBudgetSummaryTableModel((int) args.get(0));
        break;
      case "budgets_by_partners_list":
        model = this.getBudgetsbyPartnersTableModel((int) args.get(0));
        break;
      case "budgets_by_coas_list":
        model = this.getBudgetsbyCoasTableModel((int) args.get(0));
        break;

    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  private TypedTableModel getActivitiesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"activity_id", "title", "description", "start_date", "end_date", "institution", "activity_leader",
        "status"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

    if (!project.getActivities().isEmpty()) {
      for (Activity activity : project.getActivities().stream().sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(a -> a.isActive() && (a.getActivityStatus() == 2 || a.getActivityStatus() == 4))
        .collect(Collectors.toList())) {
        String institution = null;
        String activity_leader = null;
        String status = null;
        String start_date = null;
        String end_date = null;

        if (activity.getStartDate() != null) {
          start_date = formatter.format(activity.getStartDate());
        }

        if (activity.getEndDate() != null) {
          end_date = formatter.format(activity.getEndDate());
        }

        if (activity.getProjectPartnerPerson() != null) {
          institution = activity.getProjectPartnerPerson().getProjectPartner().getInstitution().getComposedName();
          activity_leader = activity.getProjectPartnerPerson().getUser().getComposedName() + "\n&lt;"
            + activity.getProjectPartnerPerson().getUser().getEmail() + "&gt;";
        }

        status = ProjectStatusEnum.getValue(activity.getActivityStatus().intValue()).getStatus();

        model.addRow(new Object[] {activity.getId(), activity.getTitle(), activity.getDescription(), start_date,
          end_date, institution, activity_leader, status});
      }
    }

    return model;
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


  public ProjectBudgetsCluserActvity getBudgetbyCoa(Long activitiyId, int year, long type) {

    for (ProjectBudgetsCluserActvity pb : project.getProjectBudgetsCluserActvities().stream()
      .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getCrpClusterOfActivity() != null
        && pb.getCrpClusterOfActivity().getId() == activitiyId && type == pb.getBudgetType().getId())
      .collect(Collectors.toList())) {
      return pb;
    }
    return null;
  }

  private TypedTableModel getBudgetsbyCoasTableModel(int year) {
    DecimalFormat df = new DecimalFormat("###,###.00");

    TypedTableModel model = new TypedTableModel(
      new String[] {"description", "year", "w1w2", "w3", "bilateral", "center", "w1w2GenderPer", "w3GenderPer",
        "bilateralGenderPer", "centerGenderPer"},
      new Class[] {String.class, Integer.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);

    List<ProjectClusterActivity> coAs = new ArrayList<>();
    coAs = project.getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    if (coAs.size() == 1) {
      String description = coAs.get(0).getCrpClusterOfActivity().getComposedName();
      String w1w2 = null;
      String w1w2GenderPer = null;
      String w3 = null;
      String w3GenderPer = null;
      String bilateral = null;
      String bilateralGenderPer = null;
      String center = null;
      String centerGenderPer = null;

      // Get types of funding sources
      for (ProjectBudget pb : project.getProjectBudgets().stream()
        .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getBudgetType() != null)
        .collect(Collectors.toList())) {
        if (pb.getBudgetType().getId() == 1) {
          w1w2 = "100";
          w1w2GenderPer = "100";
        }
        if (pb.getBudgetType().getId() == 2) {
          w3 = "100";
          w3GenderPer = "100";
        }
        if (pb.getBudgetType().getId() == 3) {
          bilateral = "100";
          bilateralGenderPer = "100";
        }
        if (pb.getBudgetType().getId() == 4) {
          center = "100";
          centerGenderPer = "100";
        }
      }


      model.addRow(new Object[] {description, year, w1w2, w3, bilateral, center, w1w2GenderPer, w3GenderPer,
        bilateralGenderPer, centerGenderPer});
    } else {

      for (ProjectClusterActivity clusterActivity : coAs) {
        String description = clusterActivity.getCrpClusterOfActivity().getComposedName();
        String w1w2 = null;
        String w1w2GenderPer = null;
        String w3 = null;
        String w3GenderPer = null;
        String bilateral = null;
        String bilateralGenderPer = null;
        String center = null;
        String centerGenderPer = null;


        ProjectBudgetsCluserActvity w1w2pb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), year, 1);
        ProjectBudgetsCluserActvity w3pb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), year, 2);
        ProjectBudgetsCluserActvity bilateralpb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), year, 3);
        ProjectBudgetsCluserActvity centerpb =
          this.getBudgetbyCoa(clusterActivity.getCrpClusterOfActivity().getId(), year, 4);


        if (w1w2pb != null) {
          w1w2 = df.format(w1w2pb.getAmount());
          w1w2GenderPer = df.format(w1w2pb.getGenderPercentage());
        }

        if (w3pb != null) {
          w3 = df.format(w3pb.getAmount());
          w3GenderPer = df.format(w3pb.getGenderPercentage());
        }

        if (bilateralpb != null) {
          bilateral = df.format(bilateralpb.getAmount());
          bilateralGenderPer = df.format(bilateralpb.getGenderPercentage());
        }
        if (centerpb != null) {
          center = df.format(centerpb.getAmount());
          centerGenderPer = df.format(centerpb.getGenderPercentage());
        }


        model.addRow(new Object[] {description, year, w1w2, w3, bilateral, center, w1w2GenderPer, w3GenderPer,
          bilateralGenderPer, centerGenderPer});
      }
    }
    return model;
  }

  private TypedTableModel getBudgetsbyPartnersTableModel(int year) {
    TypedTableModel model = new TypedTableModel(
      new String[] {"year", "institution", "w1w2", "w3", "bilateral", "center", "institution_id", "p_id", "w1w2Gender",
        "w3Gender", "bilateralGender", "centerGender", "w1w2GAmount", "w3GAmount", "bilateralGAmount", "centerGAmount"},
      new Class[] {Integer.class, String.class, String.class, String.class, String.class, String.class, Long.class,
        Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);

    // Get ppaPartners of project
    for (ProjectPartner pp : project.getProjectPartners()) {
      if (this.isPPA(pp.getInstitution())) {
        DecimalFormat myFormatter = new DecimalFormat("###,###.00");

        String w1w2Budget =
          myFormatter.format(Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 1)));
        String w3Budget =
          myFormatter.format(Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 2)));
        String bilateralBudget =
          myFormatter.format(Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 3)));
        String centerBudget =
          myFormatter.format(Double.parseDouble(this.getTotalAmount(pp.getInstitution().getId(), year, 4)));

        String w1w2Gender = myFormatter.format(this.getTotalGenderPer(pp.getInstitution().getId(), year, 1));
        String w3Gender = myFormatter.format(this.getTotalGenderPer(pp.getInstitution().getId(), year, 2));
        String bilateralGender = myFormatter.format(this.getTotalGenderPer(pp.getInstitution().getId(), year, 3));
        String centerGender = myFormatter.format(this.getTotalGenderPer(pp.getInstitution().getId(), year, 4));
        String w1w2GAmount = myFormatter.format(this.getTotalGender(pp.getInstitution().getId(), year, 1));
        String w3GAmount = myFormatter.format(this.getTotalGender(pp.getInstitution().getId(), year, 2));
        String bilateralGAmount = myFormatter.format(this.getTotalGender(pp.getInstitution().getId(), year, 3));
        String centerGAmount = myFormatter.format(this.getTotalGender(pp.getInstitution().getId(), year, 4));


        model.addRow(new Object[] {year, pp.getInstitution().getComposedName(), w1w2Budget, w3Budget, bilateralBudget,
          centerBudget, pp.getInstitution().getId(), projectID, w1w2Gender, w3Gender, bilateralGender, centerGender,
          w1w2GAmount, w3GAmount, bilateralGAmount, centerGAmount});
      }
    }

    return model;
  }

  private TypedTableModel getBudgetSummaryTableModel(int year) {
    TypedTableModel model = new TypedTableModel(new String[] {"year", "w1w2", "w3", "bilateral", "centerfunds"},
      new Class[] {Integer.class, String.class, String.class, String.class, String.class}, 0);
    String w1w2 = null;
    String w3 = null;
    String bilateral = null;
    String centerfunds = null;
    // Decimal format
    DecimalFormat myFormatter = new DecimalFormat("###,###.00");


    w1w2 = myFormatter.format(this.getTotalYear(year, 1));
    w3 = myFormatter.format(this.getTotalYear(year, 2));
    bilateral = myFormatter.format(this.getTotalYear(year, 3));
    centerfunds = myFormatter.format(this.getTotalYear(year, 4));

    model.addRow(new Object[] {year, w1w2, w3, bilateral, centerfunds});

    return model;
  }

  public byte[] getBytesPDF() {
    return bytesPDF;
  }

  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }


  @Override
  public String getContentType() {
    return "application/pdf";
  }


  public String getCycle() {
    return cycle;
  }

  private TypedTableModel getDeliverablesTableModel(int year) {
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverable_id", "title", "deliv_type", "deliv_sub_type", "deliv_status", "deliv_year",
        "key_output", "leader", "institution", "funding_sources", "cross_cutting"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class},
      0);
    if (!project.getDeliverables().isEmpty()) {
      for (Deliverable deliverable : project.getDeliverables().stream()
        .sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId())).filter(c -> c.isActive() && c.getYear() >= year)
        .collect(Collectors.toList())) {
        String deliv_type = null;
        String deliv_sub_type = null;
        String deliv_status = deliverable.getStatusName();
        String deliv_year = null;
        String key_output = "";
        String leader = null;
        String institution = null;
        String funding_sources = "";
        if (deliverable.getDeliverableType() != null) {
          deliv_sub_type = deliverable.getDeliverableType().getName();
          if (deliverable.getDeliverableType().getDeliverableType() != null) {
            deliv_type = deliverable.getDeliverableType().getDeliverableType().getName();
          }
        }
        if (deliv_status.equals("")) {
          deliv_status = null;
        }
        if (deliverable.getYear() != 0) {
          deliv_year = "" + deliverable.getYear();
        }
        if (deliverable.getCrpClusterKeyOutput() != null) {

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

            leader =
              responsibleppp.getUser().getComposedName() + "<br>&lt;" + responsibleppp.getUser().getEmail() + "&gt;";
            if (responsibleppp.getInstitution() != null) {
              institution = responsibleppp.getInstitution().getComposedName();
            }
          }
        }

        // Get funding sources if exist
        for (DeliverableFundingSource dfs : deliverable.getDeliverableFundingSources().stream()
          .filter(d -> d.isActive()).collect(Collectors.toList())) {
          funding_sources += "● " + dfs.getFundingSource().getTitle() + "<br>";
        }
        if (funding_sources.isEmpty()) {
          funding_sources = null;
        }

        // Get cross_cutting dimension
        String cross_cutting = "";
        if (deliverable.getCrossCuttingNa() != null) {
          if (deliverable.getCrossCuttingNa() == true) {
            cross_cutting += "&nbsp;&nbsp;&nbsp;&nbsp; N/A <br>";
          }
        }
        if (deliverable.getCrossCuttingGender() != null) {
          if (deliverable.getCrossCuttingGender() == true) {
            cross_cutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Gender <br>";
          }
        }
        if (deliverable.getCrossCuttingYouth() != null) {
          if (deliverable.getCrossCuttingYouth() == true) {
            cross_cutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Youth <br>";
          }
        }
        if (deliverable.getCrossCuttingCapacity() != null) {
          if (deliverable.getCrossCuttingCapacity() == true) {
            cross_cutting += "&nbsp;&nbsp;&nbsp;&nbsp;● Capacity Development <br>";
          }
        }

        if (deliverable.getCrossCuttingGender() != null) {
          if (deliverable.getCrossCuttingGender() == true) {
            if (deliverable.getDeliverableGenderLevels() == null
              || deliverable.getDeliverableGenderLevels().isEmpty()) {
              cross_cutting += "<br><b>Gender level(s):</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;Not Defined&gt;";
            } else {
              cross_cutting += "<br><b>Gender level(s): </b><br>";
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels()) {
                if (dgl.getGenderLevel() != 0.0) {
                  cross_cutting += "&nbsp;&nbsp;&nbsp;&nbsp;● "
                    + DeliverableGenderTypeEnum.getValue(dgl.getGenderLevel()).getValue() + "<br>";
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

        model.addRow(new Object[] {deliverable.getId(), deliverable.getTitle(), deliv_type, deliv_sub_type,
          deliv_status, deliv_year, key_output, leader, institution, funding_sources, cross_cutting});
      }
    }
    return model;
  }

  private TypedTableModel getDescCoAsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"description"}, new Class[] {String.class}, 0);

    if (project.getProjectClusterActivities() != null) {
      for (ProjectClusterActivity projectClusterActivity : project.getProjectClusterActivities().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        model.addRow(new Object[] {projectClusterActivity.getCrpClusterOfActivity().getComposedName()});
      }
    }
    return model;
  }

  private TypedTableModel getDescTableModel(ProjectPartner projectLeader, String cycle, Boolean hasRegions) {
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "start_date", "end_date", "ml", "ml_contact", "type", "status", "org_leader", "leader",
        "summary", "cycle", "analysis", "cross-cutting", "hasRegions"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class});
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");


    String org_leader = null;
    String ml = null;
    String ml_contact = null;
    String title = project.getTitle();
    String start_date = null;
    String end_date = null;
    if (project.getStartDate() != null) {
      start_date = formatter.format(project.getStartDate());
    }
    if (project.getEndDate() != null) {
      end_date = formatter.format(project.getEndDate());
    }

    if (project.getLiaisonUser() != null) {
      ml = project.getLiaisonUser().getLiaisonInstitution().getAcronym();
      ml_contact =
        project.getLiaisonUser().getComposedName() + "\n&lt;" + project.getLiaisonUser().getUser().getEmail() + "&gt;";
    }
    String type = project.getType();
    String status = ProjectStatusEnum.getValue(project.getStatus().intValue()).getStatus();
    if (projectLeader.getInstitution() != null) {
      org_leader = projectLeader.getInstitution().getComposedName();
      if (projectLeader.getInstitution().getLocElement() != null) {
        org_leader += " - " + projectLeader.getInstitution().getLocElement().getName();
      }
    }
    String leader = null;
    if (project.getLeaderPerson() != null) {
      leader = project.getLeaderPerson().getUser().getComposedName() + "\n&lt;"
        + project.getLeaderPerson().getUser().getEmail() + "&gt;";
    }
    String summary = project.getSummary();
    if (summary != null) {
      if (summary.equals("")) {
        summary = null;
      }
    }
    String analysis = project.getGenderAnalysis();
    if (analysis != null) {
      if (analysis.equals("")) {
        analysis = null;
      }
    }
    String cross_cutting = "";
    if (project.getCrossCuttingNa() != null) {
      if (project.getCrossCuttingNa() == true) {
        cross_cutting += "● N/A <br>";
      }
    }
    if (project.getCrossCuttingGender() != null) {
      if (project.getCrossCuttingGender() == true) {
        cross_cutting += "● Gender <br>";
      }
    }
    if (project.getCrossCuttingYouth() != null) {
      if (project.getCrossCuttingYouth() == true) {
        cross_cutting += "● Youth <br>";
      }
    }
    if (project.getCrossCuttingCapacity() != null) {
      if (project.getCrossCuttingCapacity() == true) {
        cross_cutting += "● Capacity Development <br>";
      }
    }

    if (project.getCrossCuttingGender() != null) {
      if (project.getCrossCuttingGender() == false) {
        if (project.getDimension() == null || project.getDimension().isEmpty()) {
          cross_cutting += "<br><br>" + "<b>Reason for not addressing gender dimension: </b> &lt;Not Defined&gt;";
        } else {
          cross_cutting += "<br><br>" + "<b>Reason for not addressing gender dimension: </b>" + project.getDimension();
        }
      }
    }
    if (cross_cutting.isEmpty()) {
      cross_cutting = null;
    }

    model.addRow(new Object[] {title, start_date, end_date, ml, ml_contact, type, status, org_leader, leader, summary,
      cycle, analysis, cross_cutting, hasRegions});
    return model;
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
    fileName.append("Full_Project_Report-");
    fileName.append(project.getCrp().getName() + "-");
    fileName.append("P" + projectID + "-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();

  }

  private TypedTableModel getFLTableModel(List<CrpProgram> flagships) {

    TypedTableModel model = new TypedTableModel(new String[] {"FL"}, new Class[] {String.class}, 0);
    for (CrpProgram crpProgram : flagships) {
      model.addRow(new Object[] {crpProgram.getComposedName()});
    }
    return model;
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
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }

  private TypedTableModel getLocationsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"level", "lat", "long", "name"},
      new Class[] {String.class, Double.class, Double.class, String.class}, 0);

    if (!project.getProjectLocations().isEmpty()) {
      // Get all selected and show it
      List<LocElement> locElementsAll = locElementManager.findAll();
      for (ProjectLocationElementType projectLocType : project.getProjectLocationElementTypes().stream()
        .filter(plt -> plt.getIsGlobal() && plt.getLocElementType().isActive()).collect(Collectors.toList())) {
        String locTypeName = projectLocType.getLocElementType().getName();

        for (LocElement locElement : locElementsAll.stream()
          .filter(le -> le.isActive() && le.getLocElementType() != null
            && le.getLocElementType().getId() == projectLocType.getLocElementType().getId())
          .collect(Collectors.toList())) {
          Double locLat = null;
          Double locLong = null;
          String locName = null;
          if (locElement != null) {

            if (locElement.getLocGeoposition() != null) {
              locLat = locElement.getLocGeoposition().getLatitude();
              locLong = locElement.getLocGeoposition().getLongitude();
            }
            locName = locElement.getName();
          }

          model.addRow(new Object[] {locTypeName, locLat, locLong, locName});
        }
      }

      for (ProjectLocation pl : project.getProjectLocations().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        LocElement le = pl.getLocElement();
        String locTypeName = null;
        Double locLat = null;
        Double locLong = null;
        String locName = null;
        if (le != null) {
          if (le.getLocElementType() != null) {
            locTypeName = le.getLocElementType().getName();
          }
          if (le.getLocGeoposition() != null) {
            locLat = le.getLocGeoposition().getLatitude();
            locLong = le.getLocGeoposition().getLongitude();
          }
          locName = le.getName();
        }
        model.addRow(new Object[] {locTypeName, locLat, locLong, locName});
      }


    }
    return model;

  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  private TypedTableModel getMasterTableModel(List<CrpProgram> flagships, List<CrpProgram> regions,
    ProjectPartner projectLeader, String cycle, int year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "center", "current_date", "project_submission", "exist", "cycle", "isNew",
        "isAdministrative", "type"},
      new Class[] {String.class, String.class, String.class, String.class, Integer.class, String.class, Boolean.class,
        Boolean.class, String.class});

    // Filling title
    String title = "";

    if (projectLeader != null) {
      if (projectLeader.getInstitution() != null && projectLeader.getInstitution().getAcronym() != ""
        && projectLeader.getInstitution().getAcronym() != null) {
        title += projectLeader.getInstitution().getAcronym() + "-";
      }
    }
    if (project.getAdministrative() == false) {


      if (flagships != null) {
        if (!flagships.isEmpty()) {
          for (CrpProgram crpProgram : flagships) {
            title += crpProgram.getAcronym() + "-";
          }
        }
      }
      if (regions != null) {
        if (!regions.isEmpty()) {
          for (CrpProgram crpProgram : regions) {
            title += crpProgram.getAcronym() + "-";
          }
        }
      }
    }
    title += "P" + Long.toString(projectID);

    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String current_date = timezone.format(format) + "(GMT" + timezone.getOffset() + ")";

    // Filling submission
    List<Submission> submissions = new ArrayList<>();
    for (Submission submission : project.getSubmissions().stream()
      .filter(c -> c.getCycle().equals(cycle) && c.getYear() == year).collect(Collectors.toList())) {
      submissions.add(submission);
    }

    String submission = "";


    if (!submissions.isEmpty()) {
      if (submissions.size() > 1) {
        LOG.error("More than one submission was found, the report will retrieve the first one");
      }
      Submission fisrtSubmission = submissions.get(0);
      String submissionDate = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm").format(fisrtSubmission.getDateTime());

      submission = "Submitted on " + submissionDate + " (" + fisrtSubmission.getCycle() + " cycle "
        + fisrtSubmission.getYear() + ")";
    } else {
      if (!cycle.isEmpty() && year != 0) {
        submission = "Submission for " + cycle + " cycle " + year + ": &lt;pending&gt;";
      } else {
        submission =
          "Submission for " + "&lt;Not Defined&gt;" + " cycle " + "&lt;Not Defined&gt;" + " year" + ": &lt;pending&gt;";
      }
    }

    String centerAcry = "";
    centerAcry = project.getCrp().getName();
    Boolean isAdministrative = false;
    String type = "Research Project";
    if (project.getAdministrative() != null) {
      if (project.getAdministrative() == true) {
        type = "Management Project";
      }
      isAdministrative = project.getAdministrative();
    } else {
      isAdministrative = false;
    }

    Boolean isNew = this.isProjectNew(projectID);


    model.addRow(new Object[] {title, centerAcry, current_date, submission, 1, cycle, isNew, isAdministrative, type});
    return model;
  }

  private TypedTableModel getNullMasterTableModel(String cycle, int year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "center", "current_date", "project_submission", "exist", "isNew", "type"},
      new Class[] {String.class, String.class, String.class, String.class, Integer.class, Boolean.class, String.class});

    // Filling title
    String title = "";
    if (!cycle.isEmpty() && year != 0) {
      title = "P" + Long.toString(projectID) + " (" + cycle + " cycle " + year + ")";
    } else {
      title = "P" + Long.toString(projectID);
    }

    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String current_date = timezone.format(format) + timezone.getZone();

    model.addRow(new Object[] {title, "404", current_date, "", 0, false, ""});
    return model;
  }

  private TypedTableModel getOutcomesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"exp_value", "narrative", "outcome_id", "out_fl", "out_year", "out_value", "out_statement",
        "out_unit", "cross_cutting", "exp_unit"},
      new Class[] {Long.class, String.class, Long.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);

    if (!project.getProjectOutcomes().isEmpty()) {
      for (ProjectOutcome project_outcome : project.getProjectOutcomes().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        String exp_value = null;
        String exp_unit = null;
        String out_fl = null;
        String out_year = null;
        String out_value = null;
        String out_statement = null;
        String out_unit = null;
        String cross_cutting = "";


        if (project_outcome.getCrpProgramOutcome() != null) {
          out_year = "" + project_outcome.getCrpProgramOutcome().getYear();
          out_value = "" + project_outcome.getCrpProgramOutcome().getValue();
          out_statement = project_outcome.getCrpProgramOutcome().getDescription();
          if (project_outcome.getCrpProgramOutcome().getSrfTargetUnit() != null) {
            out_unit = project_outcome.getCrpProgramOutcome().getSrfTargetUnit().getName();
          }
          if (project_outcome.getCrpProgramOutcome().getCrpProgram() != null) {
            out_fl = project_outcome.getCrpProgramOutcome().getCrpProgram().getAcronym();
          }
        }

        exp_value = project_outcome.getExpectedValue() + "";
        if (out_unit == null) {
          if (project_outcome.getExpectedUnit() != null) {
            exp_unit = project_outcome.getExpectedUnit().getName();
          }
        } else {
          exp_unit = out_unit;
        }

        if (project_outcome.getGenderDimenssion() != null && !project_outcome.getGenderDimenssion().isEmpty()) {
          cross_cutting +=
            "<b>Narrative for your expected project contribution to the gender dimensions of this outcome: </b>"
              + project_outcome.getGenderDimenssion() + "<br><br>";
        }
        if (project_outcome.getYouthComponent() != null && !project_outcome.getYouthComponent().isEmpty()) {
          cross_cutting +=
            "<b>Narrative for your expected project contribution to the youth component of this outcome: </b>"
              + project_outcome.getYouthComponent();
        }
        if (cross_cutting.isEmpty()) {
          cross_cutting = null;
        }

        model.addRow(new Object[] {exp_value, project_outcome.getNarrativeTarget(), project_outcome.getId(), out_fl,
          out_year, out_value, out_statement, out_unit, cross_cutting, exp_unit});
      }
    }


    return model;
  }

  private TypedTableModel getPartnerLeaderTableModel(ProjectPartner projectLeader) {
    TypedTableModel model =
      new TypedTableModel(new String[] {"org_leader", "pp_id"}, new Class[] {String.class, Long.class}, 0);
    long pp_id = 0;
    String org_leader = null;

    if (projectLeader.getId() != null && projectLeader.getInstitution() != null) {
      pp_id = projectLeader.getId();
      org_leader = projectLeader.getInstitution().getComposedName();
      model.addRow(new Object[] {org_leader, pp_id});
    } else if (projectLeader.getId() != null && projectLeader.getInstitution() == null) {
      pp_id = projectLeader.getId();
      model.addRow(new Object[] {null, pp_id});
    } else if (projectLeader.getId() == null && projectLeader.getInstitution() != null) {
      org_leader = projectLeader.getInstitution().getComposedName();
      model.addRow(new Object[] {org_leader, null});
    }
    return model;

  }

  private TypedTableModel getPartnersLessonsTableModel(String cycle) {
    TypedTableModel model =
      new TypedTableModel(new String[] {"year", "lesson"}, new Class[] {Integer.class, String.class}, 0);
    if (!cycle.equals("")) {
      for (ProjectComponentLesson pcl : project.getProjectComponentLessons().stream()
        .sorted((p1, p2) -> p1.getYear() - p2.getYear()).filter(c -> c.isActive()
          && c.getComponentName().equals("partners") && c.getCycle().equals(cycle) && c.getYear() == this.getYear())
        .collect(Collectors.toList())) {
        model.addRow(new Object[] {pcl.getYear(), pcl.getLessons()});
      }
    }
    return model;
  }

  private TypedTableModel getPartnersOtherTableModel(ProjectPartner projectLeader) {
    TypedTableModel model = new TypedTableModel(new String[] {"instituttion", "pp_id", "leader_count"},
      new Class[] {String.class, Long.class, Integer.class}, 0);

    int leaderCount = 0;
    if (projectLeader.getId() != null) {
      leaderCount = 1;
      // Get list of partners except project leader
      for (ProjectPartner projectPartner : project.getProjectPartners().stream()
        .filter(c -> c.isActive() && c.getId() != projectLeader.getId()).collect(Collectors.toList())) {
        model.addRow(
          new Object[] {projectPartner.getInstitution().getComposedName(), projectPartner.getId(), leaderCount});
      }
    } else {
      // Get all partners
      for (ProjectPartner projectPartner : project.getProjectPartners().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        model.addRow(
          new Object[] {projectPartner.getInstitution().getComposedName(), projectPartner.getId(), leaderCount});
      }
    }

    return model;
  }


  private TypedTableModel getPartnersTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"count"}, new Class[] {Integer.class}, 0);
    int partnersSize = 0;
    Set<ProjectPartner> projectPartners = project.getProjectPartners();
    if (!projectPartners.isEmpty()) {
      partnersSize = projectPartners.size();
    }
    model.addRow(new Object[] {partnersSize});
    return model;
  }

  public long getProjectID() {
    return projectID;
  }


  private TypedTableModel getRLTableModel(List<CrpProgram> regions) {
    TypedTableModel model = new TypedTableModel(new String[] {"RL"}, new Class[] {String.class}, 0);
    for (CrpProgram crpProgram : regions) {
      model.addRow(new Object[] {crpProgram.getComposedName()});
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
  public String getTotalAmount(long institutionId, int year, long budgetType) {
    return projectBudgetManager.amountByBudgetType(institutionId, year, budgetType, projectID);
  }

  /**
   * Get gender amount per institution, year and budet type
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @return
   */
  public double getTotalGender(long institutionId, int year, long budgetType) {

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
  public double getTotalGenderPer(long institutionId, int year, long budgetType) {

    String totalAmount = this.getTotalAmount(institutionId, year, budgetType);

    double dTotalAmount = Double.parseDouble(totalAmount);

    double totalGender = this.getTotalGender(institutionId, year, budgetType);

    if (dTotalAmount != 0) {
      return (totalGender * 100) / dTotalAmount;
    } else {
      return 0.0;
    }
  }

  /**
   * Get the total budget per year and type
   * 
   * @param year current year in the platform
   * @param type budget type (W1W2/Bilateral/W3/Center funds)
   * @return total budget in the year and type passed as parameters
   */
  public double getTotalYear(int year, long type) {
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
      this
        .setProjectID(Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID))));
      this.setYear(Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.YEAR_REQUEST))));
      this.setCycle(StringUtils.trim(this.getRequest().getParameter(APConstants.CYCLE)));
    } catch (Exception e) {

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

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }


  public void setCycle(String cycle) {
    this.cycle = cycle;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setYear(int year) {
    this.year = year;
  }


}