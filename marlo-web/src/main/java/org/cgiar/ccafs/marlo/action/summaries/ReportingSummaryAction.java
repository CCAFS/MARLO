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
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyIndicator;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.ChannelEnum;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.OtherContribution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectCrpContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;
import org.cgiar.ccafs.marlo.data.model.ProjectOtherContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomePandr;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
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

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }


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
  private CrpManager crpManager;
  private IpElementManager ipElementManager;
  private HashMap<Long, String> targetUnitList;
  private SrfTargetUnitManager srfTargetUnitManager;


  // Project from DB
  private Project project;


  @Inject
  public ReportingSummaryAction(APConfig config, CrpManager crpManager, ProjectManager projectManager,
    CrpProgramManager programManager, InstitutionManager institutionManager, ProjectBudgetManager projectBudgetManager,
    LocElementManager locElementManager, IpElementManager ipElementManager, SrfTargetUnitManager srfTargetUnitManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.programManager = programManager;
    this.institutionManager = institutionManager;
    this.projectBudgetManager = projectBudgetManager;
    this.locElementManager = locElementManager;
    this.ipElementManager = ipElementManager;
    this.srfTargetUnitManager = srfTargetUnitManager;
  }


  public String calculateAcumulativeTarget(int yearCalculate, IpProjectIndicator id) {

    int acumulative = 0;

    try {
      for (IpProjectIndicator indicators : project.getProjectIndicators()) {
        if (indicators != null) {
          if (id.getIpIndicator().getIpIndicator() != null) {
            if (indicators.getYear() <= yearCalculate && indicators.getIpIndicator().getIpIndicator().getId()
              .longValue() == id.getIpIndicator().getIpIndicator().getId().longValue()) {
              if (indicators.getTarget() == null) {
                indicators.setTarget("0");
              }
              if (indicators.getTarget() != null) {
                if (!indicators.getTarget().equals("")) {
                  try {
                    acumulative = acumulative + Integer.parseInt(indicators.getTarget());
                  } catch (NumberFormatException e) {
                    return "Cannot be Calculated";
                  }
                }
              }
            }
          } else {
            if (indicators.getYear() <= yearCalculate && indicators.getIpIndicator() != null
              && indicators.getIpIndicator().getId() != null
              && indicators.getIpIndicator().getId().longValue() == id.getIpIndicator().getId().longValue()) {
              if (indicators.getTarget() == null) {
                indicators.setTarget("0");
              }
              if (indicators.getTarget() != null) {

                if (!indicators.getTarget().equals("")) {
                  try {
                    acumulative = acumulative + Integer.parseInt(indicators.getTarget());
                  } catch (NumberFormatException e) {
                    return "Cannot be Calculated";
                  }
                }
              }
            }
          }
        }


      }
    } catch (Exception e) {
      return "Cannot be Calculated";
    }
    return String.valueOf(acumulative);
  }

  public boolean containsOutput(long outputID, long outcomeID) {

    if (project.getMogs() != null) {
      for (IpElement output : project.getMogs()) {

        IpElement outputDB = ipElementManager.getIpElementById(output.getId());
        if (outputDB != null && outputDB.getId().longValue() == outputID) {


          return true;

        }
      }
    }
    return false;
  }


  @Override
  public String execute() throws Exception {

    // Calculate time to generate report
    // long startTime = System.currentTimeMillis();
    // System.out.println("Inicia conteo en: " + (startTime - System.currentTimeMillis()));


    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager = // new ResourceManager();
      (ResourceManager) ServletActionContext.getServletContext().getAttribute(PentahoListener.KEY_NAME);
    // manager.registerDefaults();
    try {
      // Get project from DB
      project = projectManager.getProjectById(projectID);
      String masterQueryName = "Main_Query";
      // Get parameters from URL
      // Get year
      try {
        year = Integer.parseInt(this.getRequest().getParameter("year"));
      } catch (Exception e) {
        year = this.getCurrentCycleYear();
      }

      // Get cycle
      try {
        cycle = this.getRequest().getParameter("cycle");
      } catch (Exception e) {
        cycle = this.getCurrentCycle();
      }
      Resource reportResource;
      if (cycle.equals("Planning")) {
        reportResource = manager.createDirectly(
          this.getClass().getResource("/pentaho/project-description(Planning).prpt"), MasterReport.class);
      } else {
        reportResource = manager.createDirectly(
          this.getClass().getResource("/pentaho/project-description(Reporting).prpt"), MasterReport.class);
      }

      // Get main report
      MasterReport masterReport = (MasterReport) reportResource.getResource();


      // General list to store parameters of Subreports
      List<Object> args = new LinkedList<>();
      // Verify if the project was found
      if (project != null) {
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

        List<CrpProgram> regions = new ArrayList<>();
        // If has regions, add the regions to regionsArrayList
        // Get Regions related to the project sorted by acronym
        if (this.hasProgramnsRegions() != false) {
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
        TypedTableModel model = this.getMasterTableModel(flagships, regions, projectLeader);
        sdf.addTable(masterQueryName, model);
        masterReport.setDataFactory(cdf);

        // Start Setting Planning Subreports

        // Subreport Description
        args.add(projectLeader);
        args.add(this.hasProgramnsRegions());
        this.fillSubreport((SubReport) hm.get("description"), "description", args);
        // Description Flagships
        args.clear();
        args.add(flagships);
        this.fillSubreport((SubReport) hm.get("Flagships"), "description_flagships", args);
        // Description Regions

        args.clear();
        args.add(regions);
        this.fillSubreport((SubReport) hm.get("Regions"), "description_regions", args);

        if (cycle.equals("Planning")) {
          // Description CoAs
          args.clear();
          this.fillSubreport((SubReport) hm.get("Description_CoAs"), "description_coas", args);
        }
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
        this.fillSubreport((SubReport) hm.get("partner_lessons"), "partner_lessons", args);

        // Subreport Locations
        args.clear();
        this.fillSubreport((SubReport) hm.get("locations"), "locations", args);
        if (cycle.equals("Planning")) {
          // Subreport Outcomes
          args.clear();
          this.fillSubreport((SubReport) hm.get("outcomes"), "outcomes_list", args);
        } else {
          // Subreport Project Outcomes
          args.clear();
          this.fillSubreport((SubReport) hm.get("project_outcomes"), "project_outcomes", args);
          this.fillSubreport((SubReport) hm.get("other_outcomes"), "other_outcomes", args);
          this.fillSubreport((SubReport) hm.get("ccafs_outcomes"), "ccafs_outcomes", args);
          this.fillSubreport((SubReport) hm.get("other_contributions"), "other_contributions", args);
          this.fillSubreport((SubReport) hm.get("other_contributions_detail"), "other_contributions_detail", args);
          this.fillSubreport((SubReport) hm.get("other_contributions_crps"), "other_contributions_crps", args);
          this.fillSubreport((SubReport) hm.get("case_studies"), "case_studies", args);
          this.fillSubreport((SubReport) hm.get("overview_by_mogs"), "overview_by_mogs", args);
        }
        // Subreport Deliverables
        if (cycle.equals("Planning")) {
          // Subreport Outcomes
          args.clear();
          this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables_list", args);
        } else {
          args.clear();
          this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables_list_reporting", args);
          this.fillSubreport((SubReport) hm.get("project_highlight"), "project_highlight", args);
        }


        // Subreport Activities
        args.clear();
        if (cycle.equals("Planning")) {

          this.fillSubreport((SubReport) hm.get("activities"), "activities_list", args);
        } else {
          this.fillSubreport((SubReport) hm.get("activities_reporting_list"), "activities_reporting_list", args);
        }
        if (cycle.equals("Planning")) {
          // Subreport Budgets Summary
          args.clear();
          this.fillSubreport((SubReport) hm.get("budgets"), "budget_summary", args);

          // Subreport BudgetsbyPartners
          this.fillSubreport((SubReport) hm.get("budgets_by_partners"), "budgets_by_partners_list", args);

          // Subreport BudgetsbyCoas
          this.fillSubreport((SubReport) hm.get("budgets_by_coas"), "budgets_by_coas_list", args);
        } else {
          // Subreport Leverages for reporting
          this.fillSubreport((SubReport) hm.get("leverages"), "leverages", args);

        }

      }

      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      // long stopTime = System.currentTimeMillis();
      // long elapsedTime = stopTime - startTime;
      // System.out.println("Tiempo de ejecución: Error time " + elapsedTime);
      LOG.error("Generating PDF" + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    // long stopTime = System.currentTimeMillis();
    // long elapsedTime = stopTime - startTime;
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
          model = this.getDescTableModel((ProjectPartner) args.get(0), (Boolean) args.get(1));
        } else {
          model = this.getDescTableModel(new ProjectPartner(), (Boolean) args.get(1));
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
        model = this.getPartnersLessonsTableModel();
        break;
      case "locations":
        model = this.getLocationsTableModel();
        break;
      case "outcomes_list":
        model = this.getOutcomesTableModel();
        break;
      case "deliverables_list":
        model = this.getDeliverablesTableModel();
        break;
      case "activities_list":
        model = this.getActivitiesTableModel();
        break;
      case "activities_reporting_list":
        model = this.getActivitiesReportingTableModel();
        break;
      case "budget_summary":
        model = this.getBudgetSummaryTableModel();
        break;
      case "budgets_by_partners_list":
        model = this.getBudgetsbyPartnersTableModel();
        break;
      case "budgets_by_coas_list":
        model = this.getBudgetsbyCoasTableModel();
        break;
      // Especific for reporting
      case "project_outcomes":
        model = this.getProjectOutcomesTableModel();
        break;
      case "other_outcomes":
        model = this.getProjectOtherOutcomesTableModel();
        break;
      case "ccafs_outcomes":
        model = this.getccafsOutcomesTableModel();
        break;
      case "other_contributions":
        model = this.getOtherContributionsTableModel();
        break;
      case "other_contributions_detail":
        model = this.getOtherContributionsDetailTableModel();
        break;
      case "other_contributions_crps":
        model = this.getOtherContributionsCrpsTableModel();
        break;
      case "case_studies":
        model = this.getCaseStudiesTableModel();
        break;
      case "overview_by_mogs":
        model = this.getOverviewByMogsTableModel();
        break;
      case "deliverables_list_reporting":
        model = this.getDeliverablesReportingTableModel();
        break;
      case "project_highlight":
        model = this.getProjectHighlightReportingTableModel();
        break;
      case "leverages":
        model = this.getLeveragesTableModel();
        break;


    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  private TypedTableModel getActivitiesReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"activity_id", "title", "description", "start_date", "end_date", "institution", "activity_leader",
        "status", "overall"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");


    if (!project.getActivities().isEmpty()) {
      for (Activity activity : project.getActivities().stream().sorted((d1, d2) -> Long.compare(d1.getId(), d2.getId()))
        .filter(a -> a.isActive() && a.getActivityStatus() != null
          && (a.getActivityStatus() == 2 || a.getActivityStatus() == 4 || a.getActivityStatus() == 3)
          && a.getStartDate() != null && a.getEndDate() != null)
        .collect(Collectors.toList())) {
        // Filter by date
        Calendar cal = Calendar.getInstance();
        cal.setTime(activity.getStartDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(activity.getEndDate());
        if (cal.get(Calendar.YEAR) >= year || cal2.get(Calendar.YEAR) >= year) {


          String institution = null;
          String activity_leader = null;
          String status = null;
          String start_date = null;
          String end_date = null;
          String overall = null;

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

          // Reporting
          if (activity.getActivityProgress() != null && !activity.getActivityProgress().isEmpty()) {
            overall = activity.getActivityProgress();
          }


          model.addRow(new Object[] {activity.getId(), activity.getTitle(), activity.getDescription(), start_date,
            end_date, institution, activity_leader, status, overall});
        }
      }
    }

    return model;
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


  private TypedTableModel getBudgetsbyCoasTableModel() {
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


  private TypedTableModel getBudgetsbyPartnersTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"year", "institution", "w1w2", "w3", "bilateral", "center", "institution_id", "p_id", "w1w2Gender",
        "w3Gender", "bilateralGender", "centerGender", "w1w2GAmount", "w3GAmount", "bilateralGAmount", "centerGAmount"},
      new Class[] {Integer.class, String.class, String.class, String.class, String.class, String.class, Long.class,
        Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);

    // Get ppaPartners of project
    for (ProjectPartner pp : project.getProjectPartners().stream().filter(pp -> pp.isActive())
      .collect(Collectors.toList())) {
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


  private TypedTableModel getBudgetSummaryTableModel() {
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

  private TypedTableModel getCaseStudiesTableModel() {
    // Code Author: Hermes Jimenez
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "outcomeStatement", "researchOutputs", "researchPartners", "activities",
        "nonResearchPartneres", "outputUsers", "evidenceOutcome", "outputUsed", "referencesCase",
        "explainIndicatorRelation", "anex", "owner", "indicators", "shared", "year"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);

    Long id = null;

    String title = "", outcomeStatement = "", researchOutputs = "", researchPartners = "", activities = "",
      nonResearchPartneres = "", outputUsers = "", evidenceOutcome = "", outputUsed = "", referencesCase = "",
      explainIndicatorRelation = "", anex = "", owner = "", shared = "", indicators = "", year = "";

    for (CaseStudyProject caseStudyProject : project
      .getCaseStudyProjects().stream().filter(csp -> csp.isActive() && csp.getCaseStudy() != null
        && csp.getCaseStudy().getYear() != null && csp.getCaseStudy().getYear() >= this.year)
      .collect(Collectors.toList())) {
      CaseStudy caseStudy = caseStudyProject.getCaseStudy();
      // System.out.println(this.year);
      // System.out.println(caseStudy.getYear());

      id = caseStudy.getId();

      year = String.valueOf(caseStudy.getYear());

      title = caseStudy.getTitle().trim().isEmpty() ? null : caseStudy.getTitle();

      outcomeStatement = caseStudy.getOutcomeStatement().trim().isEmpty() ? null : caseStudy.getOutcomeStatement();

      researchOutputs = caseStudy.getResearchOutputs().trim().isEmpty() ? null : caseStudy.getResearchOutputs();

      researchPartners = caseStudy.getResearchPartners().trim().isEmpty() ? null : caseStudy.getResearchPartners();

      activities = caseStudy.getActivities().trim().isEmpty() ? null : caseStudy.getActivities();

      nonResearchPartneres =
        caseStudy.getNonResearchPartneres().trim().isEmpty() ? null : caseStudy.getNonResearchPartneres();

      outputUsers = caseStudy.getOutputUsers().trim().isEmpty() ? null : caseStudy.getOutputUsers();

      outputUsed = caseStudy.getOutputUsed().trim().isEmpty() ? null : caseStudy.getOutputUsed();

      evidenceOutcome = caseStudy.getEvidenceOutcome().trim().isEmpty() ? null : caseStudy.getEvidenceOutcome();

      referencesCase = caseStudy.getReferencesCase().trim().isEmpty() ? null : caseStudy.getReferencesCase();

      explainIndicatorRelation =
        caseStudy.getExplainIndicatorRelation().trim().isEmpty() ? null : caseStudy.getExplainIndicatorRelation();

      List<CaseStudyProject> studyProjects = new ArrayList<>(
        caseStudy.getCaseStudyProjects().stream().filter(csp -> csp.isActive()).collect(Collectors.toList()));

      for (CaseStudyProject caseStudyProjectList : studyProjects) {
        if (caseStudyProjectList.isCreated()) {
          shared = String.valueOf(caseStudyProjectList.getProject().getId());
        }

        owner = "P" + caseStudyProjectList.getProject().getId();
      }

      List<CaseStudyIndicator> studyIndicators = new ArrayList<>(
        caseStudy.getCaseStudyIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      StringBuilder indicatorsS = new StringBuilder();


      for (CaseStudyIndicator caseStudyIndicator : studyIndicators) {
        if (caseStudyIndicator.isActive()) {
          indicatorsS.append("● " + caseStudyIndicator.getIpIndicator().getDescription() + "<br>");
        }
      }

      indicators = indicatorsS.toString();

      if (caseStudy.getFile() != null) {
        anex = (this.getCaseStudyUrl(shared) + caseStudy.getFile().getFileName()).replace(" ", "%20");
      }


      model.addRow(new Object[] {id, title, outcomeStatement, researchOutputs, researchPartners, activities,
        nonResearchPartneres, outputUsers, evidenceOutcome, outputUsed, referencesCase, explainIndicatorRelation, anex,
        owner.trim(), indicators.trim(), shared.trim(), year});

    }

    return model;
  }

  public String getCaseStudyUrl(String project) {
    return config.getDownloadURL() + "/" + this.getCaseStudyUrlPath(project).replace('\\', '/');
  }

  public String getCaseStudyUrlPath(String project) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project + File.separator + "caseStudy"
      + File.separator;
  }


  private TypedTableModel getccafsOutcomesTableModel() {

    TypedTableModel model = new TypedTableModel(
      new String[] {"program_outcome", "program_outcome_description", "indicator", "indicator_description",
        "ipProjectIndicatoryear", "target_value", "target_cumulative", "target_achieved", "target_narrative",
        "target_achieved_narrative", "achieved_annual_gender", "annual_gender", "is_current", "showOutcome",
        "showIndicator", "showOutputs", "outputs"},
      new Class[] {String.class, String.class, String.class, String.class, Integer.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, Boolean.class, Boolean.class,
        Boolean.class, Boolean.class, String.class},
      0);

    project.setProjectIndicators(
      project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

    // Get list of outcomes
    Set<IpElement> outcomesList = new HashSet<>();
    for (IpProjectIndicator ipProjectIndicator : project.getIpProjectIndicators().stream().filter(i -> i.isActive())
      .collect(Collectors.toList())) {
      IpElement ipElement = ipElementManager.getIpElementById(ipProjectIndicator.getOutcomeId());
      outcomesList.add(ipElement);
    }

    for (IpElement outcome : outcomesList) {
      // System.out.println(outcome.getId());
      Boolean showOutcome = true;

      // Get list of indicators
      Set<IpIndicator> indicatorsList = new HashSet<>();
      for (IpProjectIndicator ipProjectIndicator : project.getIpProjectIndicators().stream()
        .filter(i -> i.isActive() && i.getOutcomeId() == outcome.getId().intValue() && (i.getYear() == year
          || i.getYear() == year - 1 || i.getYear() == year + 1 || i.getYear() == APConstants.MID_OUTCOME_YEAR))
        .collect(Collectors.toList())) {
        IpIndicator ipIndicator = ipProjectIndicator.getIpIndicator();
        // System.out.println("Current " + ipIndicator.getId());
        // System.out.println("Final " + this.getFinalIndicator(ipIndicator).getId());
        indicatorsList.add(this.getFinalIndicator(ipIndicator));
      }

      int ultimoIndicator = indicatorsList.size();
      int ultimoIndicatorCount = 0;
      Boolean showOutputs = false;
      int indicator_number = 0;
      for (IpIndicator ipIndicator : indicatorsList) {
        indicator_number++;
        ultimoIndicatorCount++;
        Boolean showIndicator = true;
        int ultimoProjectIndicator = (int) project.getIpProjectIndicators().stream()
          .filter(i -> i.isActive() && i.getOutcomeId() == outcome.getId().intValue()
            && i.getIpIndicator() == ipIndicator && (i.getYear() == year || i.getYear() == year - 1
              || i.getYear() == year + 1 || i.getYear() == APConstants.MID_OUTCOME_YEAR))
          .count();
        int ultimoProjectIndicatorCount = 0;
        for (IpProjectIndicator ipProjectIndicator : project.getIpProjectIndicators().stream()
          .filter(i -> i.isActive() && i.getOutcomeId() == outcome.getId().intValue()
            && i.getIpIndicator() == ipIndicator && (i.getYear() == year || i.getYear() == year - 1
              || i.getYear() == year + 1 || i.getYear() == APConstants.MID_OUTCOME_YEAR))
          .collect(Collectors.toList())) {
          ultimoProjectIndicatorCount++;

          String program_outcome = null, program_outcome_description = null, indicator = null,
            indicator_description = null, target_value = null, target_cumulative = null, target_narrative = null,
            target_achieved_narrative = null, achieved_annual_gender = null, annual_gender = null, outputs = "";
          Integer ipProjectIndicatoryear = null;
          Double target_achieved = 0.0;
          Boolean is_current = false;

          if (outcome.getIpProgram() != null && !outcome.getIpProgram().getAcronym().isEmpty()) {
            program_outcome = outcome.getIpProgram().getAcronym() + " Outcome " + APConstants.MID_OUTCOME_YEAR;
          }

          if (outcome.getDescription() != null && !outcome.getDescription().isEmpty()) {
            program_outcome_description = outcome.getDescription();
          }

          if (ipProjectIndicator.getIpIndicator() != null) {
            indicator_description = this.getFinalIndicator(ipProjectIndicator.getIpIndicator()).getDescription();
            indicator = "Indicator #" + indicator_number;
          }

          ipProjectIndicatoryear = ipProjectIndicator.getYear();

          if (ipProjectIndicator.getTarget() != null && !ipProjectIndicator.getTarget().isEmpty()) {
            target_value = ipProjectIndicator.getTarget();
          }

          target_cumulative = this.calculateAcumulativeTarget(ipProjectIndicator.getYear(), ipProjectIndicator);
          if (ipProjectIndicator.getArchived() != null) {
            target_achieved = ipProjectIndicator.getArchived();
          }
          if (ipProjectIndicator.getDescription() != null && !ipProjectIndicator.getDescription().isEmpty()) {
            target_narrative = ipProjectIndicator.getDescription();
          }
          if (ipProjectIndicator.getNarrativeTargets() != null && !ipProjectIndicator.getNarrativeTargets().isEmpty()) {
            target_achieved_narrative = ipProjectIndicator.getNarrativeTargets();
          }
          if (ipProjectIndicator.getNarrativeGender() != null && !ipProjectIndicator.getNarrativeGender().isEmpty()) {
            achieved_annual_gender = ipProjectIndicator.getNarrativeGender();
          }
          if (ipProjectIndicator.getGender() != null && !ipProjectIndicator.getGender().isEmpty()) {
            annual_gender = ipProjectIndicator.getGender();
          }
          if (ipProjectIndicator.getYear() == year) {
            is_current = true;
          }

          if (ultimoProjectIndicatorCount == ultimoProjectIndicator && ultimoIndicatorCount == ultimoIndicator) {
            showOutputs = true;
            List<IpElement> outputsList = this.getMidOutcomeOutputs(outcome.getId());

            for (IpElement ipElement : outputsList) {
              outputs += "● " + ipElement.getIpProgram().getAcronym() + ": " + ipElement.getDescription() + "<br><br>";
            }

          }
          if (outputs.isEmpty()) {
            outputs = null;
          }

          model.addRow(new Object[] {program_outcome, program_outcome_description, indicator, indicator_description,
            ipProjectIndicatoryear, target_value, target_cumulative, target_achieved, target_narrative,
            target_achieved_narrative, achieved_annual_gender, annual_gender, is_current, showOutcome, showIndicator,
            showOutputs, outputs});
          showOutcome = false;
          showIndicator = false;
        }
      }
    }

    return model;
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


  private String getDeliverableDataSharingFilePath() {
    String upload = config.getDownloadURL();
    return upload + "/" + this.getDeliverableDataSharingFileRelativePath().replace('\\', '/');
  }

  private String getDeliverableDataSharingFileRelativePath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectID + File.separator
      + "deliverableDataSharing" + File.separator;
  }

  private TypedTableModel getDeliverablesReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"deliverable_id", "title", "deliv_type", "deliv_sub_type", "deliv_status", "deliv_year",
        "key_output", "leader", "institution", "funding_sources", "cross_cutting", "deliv_new_year",
        "deliv_new_year_justification", "deliv_dissemination_channel", "deliv_dissemination_url", "deliv_open_access",
        "deliv_license", "titleMetadata", "descriptionMetadata", "dateMetadata", "languageMetadata", "countryMetadata",
        "keywordsMetadata", "citationMetadata", "HandleMetadata", "DOIMetadata", "creator_authors", "data_sharing",
        "qualityAssurance", "dataDictionary", "tools", "showFAIR", "F", "A", "I", "R", "isDisseminated", "disseminated",
        "restricted_access", "isRestricted", "restricted_date", "isLastTwoRestricted", "deliv_license_modifications",
        "show_deliv_license_modifications", "volume", "issue", "pages", "journal", "journal_indicators", "acknowledge",
        "fl_contrib", "show_publication", "showCompilance"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        Boolean.class, String.class, String.class, String.class, String.class, Boolean.class, String.class,
        String.class, Boolean.class, String.class, Boolean.class, String.class, Boolean.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class,
        Boolean.class},
      0);
    if (!project.getDeliverables().isEmpty()) {
      // get Reporting deliverables
      List<Deliverable> deliverables = new ArrayList<>(project.getDeliverables().stream()
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


      for (Deliverable deliverable : deliverables) {
        String deliv_type = null;
        String deliv_sub_type = null;
        String deliv_status = deliverable.getStatusName();
        String deliv_year = null;
        String key_output = "";
        String leader = null;
        String institution = null;
        String funding_sources = "";
        Boolean showFAIR = false;
        Boolean show_publication = false;
        Boolean showCompilance = false;

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
          deliv_year = "" + deliverable.getYear();
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
            cross_cutting += "&nbsp;&nbsp;&nbsp;&nbsp;● N/A <br>";
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
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
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

        // Reporting
        Integer deliv_new_year = null;
        String deliv_new_year_justification = null;

        if (deliverable.getStatus() != null) {
          // Extended
          if (deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            deliv_new_year = deliverable.getNewExpectedYear();
            deliv_new_year_justification = deliverable.getStatusDescription();
          }
          // Complete
          if (deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            deliv_new_year = deliverable.getNewExpectedYear();
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
        String restricted_date = null;
        Boolean isRestricted = false;
        Boolean isLastTwoRestricted = false;
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

          if (deliverableDissemination.getDisseminationChannel() != null
            && !deliverableDissemination.getDisseminationChannel().isEmpty()) {
            if (ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()) != null) {
              deliv_dissemination_channel =
                ChannelEnum.getValue(deliverableDissemination.getDisseminationChannel()).getDesc();
            }

            // deliv_dissemination_channel = deliverableDissemination.getDisseminationChannel();
          }

          if (deliverableDissemination.getDisseminationUrl() != null
            && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
            deliv_dissemination_url = deliverableDissemination.getDisseminationUrl().replace(" ", "%20");
          }

          if (deliverableDissemination.getIsOpenAccess() != null) {
            if (deliverableDissemination.getIsOpenAccess() == true) {
              deliv_open_access = "Yes";
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
                isLastTwoRestricted = true;
                if (deliverableDissemination.getRestrictedAccessUntil() != null) {
                  restricted_date =
                    "<b>Restricted access until: </b>" + deliverableDissemination.getRestrictedAccessUntil();
                } else {
                  restricted_date = "<b>Restricted access until: </b>&lt;Not Defined&gt;";
                }
              }

              if (deliverableDissemination.getEffectiveDateRestriction() != null
                && deliverableDissemination.getEffectiveDateRestriction() == true) {
                restricted_access = "Effective Date Restriction - embargoed periods (if so, what are these periods?)";
                isLastTwoRestricted = true;
                if (deliverableDissemination.getRestrictedEmbargoed() != null) {
                  restricted_date =
                    "<b>Restricted embargoed date: </b>" + deliverableDissemination.getRestrictedEmbargoed();
                } else {
                  restricted_date = "<b>Restricted embargoed date: </b>&lt;Not Defined&gt;";
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
          creator_authors += "<br>● ";
          if (!deliverableUser.getLastName().isEmpty()) {
            creator_authors += deliverableUser.getLastName() + " - ";
          }
          if (!deliverableUser.getFirstName().isEmpty()) {
            creator_authors += deliverableUser.getFirstName();
          }
          if (!deliverableUser.getElementId().isEmpty()) {
            creator_authors += "&lt;" + deliverableUser.getElementId() + "&gt;";
          }
        }

        if (creator_authors.isEmpty()) {
          creator_authors = null;
        }

        String data_sharing = "";
        for (DeliverableDataSharingFile deliverableDataSharingFile : deliverable.getDeliverableDataSharingFiles()
          .stream().filter(ds -> ds.isActive()).collect(Collectors.toList())) {
          if (deliverableDataSharingFile.getExternalFile() != null
            && !deliverableDataSharingFile.getExternalFile().isEmpty()) {
            data_sharing += deliverableDataSharingFile.getExternalFile().replace(" ", "%20") + "<br>";
          }

          if (deliverableDataSharingFile.getFile() != null && deliverableDataSharingFile.getFile().isActive()) {
            data_sharing +=
              (this.getDeliverableDataSharingFilePath() + deliverableDataSharingFile.getFile().getFileName())
                .replace(" ", "%20") + "<br>";

          }
        }
        if (data_sharing.isEmpty()) {
          data_sharing = null;
        }


        String qualityAssurance = "";
        String dataDictionary = "";
        String tools = "";

        if (deliverable.getDeliverableQualityChecks().stream().filter(qc -> qc.isActive()).collect(Collectors.toList())
          .size() > 0
          && deliverable.getDeliverableQualityChecks().stream().filter(qc -> qc.isActive()).collect(Collectors.toList())
            .get(0) != null) {
          DeliverableQualityCheck deliverableQualityCheck = deliverable.getDeliverableQualityChecks().stream()
            .filter(qc -> qc.isActive()).collect(Collectors.toList()).get(0);
          // QualityAssurance
          if (deliverableQualityCheck.getQualityAssurance() != null) {
            if (deliverableQualityCheck.getQualityAssurance().getId() == 2) {
              if (deliverableQualityCheck.getFileAssurance() != null
                && deliverableQualityCheck.getFileAssurance().isActive()) {
                qualityAssurance += "<br>● File: <font size=2 face='Segoe UI' color='blue'>"
                  + (this.getDeliverableUrl("Assurance", deliverable)
                    + deliverableQualityCheck.getFileAssurance().getFileName()).replace(" ", "%20")
                  + "</font>";
              }
              if (deliverableQualityCheck.getLinkAssurance() != null
                && !deliverableQualityCheck.getLinkAssurance().isEmpty()) {
                qualityAssurance += "<br>● Link: <font size=2 face='Segoe UI' color='blue'>"
                  + deliverableQualityCheck.getLinkAssurance().replace(" ", "%20") + "</font>";
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
                dataDictionary += "<br>● File: <font size=2 face='Segoe UI' color='blue'>"
                  + (this.getDeliverableUrl("Dictionary", deliverable)
                    + deliverableQualityCheck.getFileDictionary().getFileName()).replace(" ", "%20")
                  + "</font>";
              }
              if (deliverableQualityCheck.getLinkDictionary() != null
                && !deliverableQualityCheck.getLinkDictionary().isEmpty()) {
                dataDictionary += "<br>● Link: <font size=2 face='Segoe UI' color='blue'>"
                  + deliverableQualityCheck.getLinkDictionary().replace(" ", "%20") + "</font>";
              }
            } else {
              dataDictionary = "● " + deliverableQualityCheck.getDataDictionary().getName();
            }
          }

          // Tools
          if (deliverableQualityCheck.getDataTools() != null) {
            if (deliverableQualityCheck.getDataTools().getId() == 2) {
              if (deliverableQualityCheck.getFileTools() != null && deliverableQualityCheck.getFileTools().isActive()) {
                tools += "<br>● File: <font size=2 face='Segoe UI' color='blue'>"
                  + (this.getDeliverableUrl("Tools", deliverable)
                    + deliverableQualityCheck.getFileTools().getFileName()).replace(" ", "%20")
                  + "</font>";
              }
              if (deliverableQualityCheck.getLinkTools() != null && !deliverableQualityCheck.getLinkTools().isEmpty()) {
                tools += "<br>● Link: <font size=2 face='Segoe UI' color='blue'>"
                  + deliverableQualityCheck.getLinkTools().replace(" ", "%20") + "</font>";
              }
            } else {
              tools = "● " + deliverableQualityCheck.getDataTools().getName();
            }
          }
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


        // FAIR
        String F = "";
        if (this.isF(deliverable.getId()) == null) {
          F = "#a3a3a3";
        } else {
          if (this.isF(deliverable.getId()) == true) {
            F = "#008000";
          } else {
            F = "#ca1010";
          }
        }

        String A = "";

        if (this.isA(deliverable.getId()) == null) {
          A += "#a3a3a3";
        } else {
          if (this.isA(deliverable.getId()) == true) {
            A += "#008000";
          } else {
            A += "#ca1010";
          }
        }

        String I = "";

        if (this.isI(deliverable.getId()) == null) {
          I += "#a3a3a3";
        } else {
          if (this.isI(deliverable.getId()) == true) {
            I += "#008000";
          } else {
            I += "#ca1010";
          }
        }

        String R = "";
        if (this.isR(deliverable.getId()) == null) {
          R += "#a3a3a3";
        } else {
          if (this.isR(deliverable.getId()) == true) {
            R += "#008000";
          } else {
            R += "#ca1010";
          }
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


        if (deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
          .collect(Collectors.toList()).size() > 0
          && deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
            .collect(Collectors.toList()).get(0) != null) {
          DeliverablePublicationMetadata deliverablePublicationMetadata =
            deliverable.getDeliverablePublicationMetadatas().stream().filter(dpm -> dpm.isActive())
              .collect(Collectors.toList()).get(0);
          volume = deliverablePublicationMetadata.getVolume();
          issue = deliverablePublicationMetadata.getIssue();
          pages = deliverablePublicationMetadata.getPages();
          journal = deliverablePublicationMetadata.getJournal();
          if (deliverablePublicationMetadata.getIsiPublication() != null
            && deliverablePublicationMetadata.getIsiPublication() == true) {
            journal_indicators += "● This journal article is an ISI publication <br>";
          }
          if (deliverablePublicationMetadata.getNasr() != null && deliverablePublicationMetadata.getNasr() == true) {
            journal_indicators +=
              "● This article have a co-author from a developing country National Agricultural Research System (NARS)<br>";
          }
          if (deliverablePublicationMetadata.getCoAuthor() != null
            && deliverablePublicationMetadata.getCoAuthor() == true) {
            journal_indicators +=
              "● This article have a co-author based in an Earth System Science-related academic department";
          }
          if (journal_indicators.isEmpty()) {
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
                + deliverableCrp.getIpProgram().getAcronym().toUpperCase() + "<br>";
            } else {
              if (deliverableCrp.getCrpPandr() != null) {
                fl_contrib += "● " + deliverableCrp.getCrpPandr().getName().toUpperCase() + "<br>";
              }
            }
          }
        }

        model.addRow(new Object[] {deliverable.getId(), deliverable.getTitle(), deliv_type, deliv_sub_type,
          deliv_status, deliv_year, key_output, leader, institution, funding_sources, cross_cutting, deliv_new_year,
          deliv_new_year_justification, deliv_dissemination_channel, deliv_dissemination_url, deliv_open_access,
          deliv_license, titleMetadata, descriptionMetadata, dateMetadata, languageMetadata, countryMetadata,
          keywordsMetadata, citationMetadata, HandleMetadata, DOIMetadata, creator_authors, data_sharing,
          qualityAssurance, dataDictionary, tools, showFAIR, F, A, I, R, isDisseminated, disseminated,
          restricted_access, isRestricted, restricted_date, isLastTwoRestricted, deliv_license_modifications,
          show_deliv_license_modifications, volume, issue, pages, journal, journal_indicators, acknowledge, fl_contrib,
          show_publication, showCompilance});
      }
    }
    return model;
  }

  private TypedTableModel getDeliverablesTableModel() {
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
            cross_cutting += "&nbsp;&nbsp;&nbsp;&nbsp;● N/A <br>";
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
              for (DeliverableGenderLevel dgl : deliverable.getDeliverableGenderLevels().stream()
                .filter(dgl -> dgl.isActive()).collect(Collectors.toList())) {
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

  public String getDeliverableUrl(String fileType, Deliverable deliverable) {
    return config.getDownloadURL() + "/" + this.getDeliverableUrlPath(fileType, deliverable).replace('\\', '/');
  }

  public String getDeliverableUrlPath(String fileType, Deliverable deliverable) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + deliverable.getId() + File.separator
      + "deliverable" + File.separator + fileType + File.separator;
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

  private TypedTableModel getDescTableModel(ProjectPartner projectLeader, Boolean hasRegions) {
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
    // Get type from funding sources
    String type = "";
    List<String> typeList = new ArrayList<String>();
    for (ProjectBudget projectBudget : project.getProjectBudgets().stream()
      .filter(pb -> pb.isActive() && pb.getYear() == year && pb.getFundingSource() != null)
      .collect(Collectors.toList())) {
      typeList.add(projectBudget.getFundingSource().getBudgetType().getName());
    }
    // Remove duplicates
    Set<String> s = new LinkedHashSet<String>(typeList);

    for (String typeString : s.stream().collect(Collectors.toList())) {
      if (type.isEmpty()) {
        type = typeString;
      } else {
        type += ", " + typeString;
      }
    }

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
    fileName.append("FullProjectReportSummary-");
    fileName.append(project.getCrp().getName() + "-");
    fileName.append("P" + projectID + "-");
    fileName.append(this.year + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();

  }


  public IpIndicator getFinalIndicator(IpIndicator ipIndicator) {
    IpIndicator newIpIndicator = ipIndicator;
    if (newIpIndicator.getIpIndicator() != null) {
      return this.getFinalIndicator(newIpIndicator.getIpIndicator());
    } else {
      return newIpIndicator;
    }

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

  public String getHighlightsImagesUrl() {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath().replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "hightlightsImage" + File.separator;
  }

  public String getHighlightsImagesUrlPath(long projectID) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectID + File.separator
      + "hightlightsImage" + File.separator;
  }

  private String getHightlightImagePath(long projectID) {
    return config.getUploadsBaseFolder() + File.separator + this.getHighlightsImagesUrlPath(projectID) + File.separator;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }

  private TypedTableModel getLeveragesTableModel() {
    // Decimal format
    DecimalFormat myFormatter = new DecimalFormat("###,###.00");
    TypedTableModel model =
      new TypedTableModel(new String[] {"id", "title", "partner_name", "leverage_year", "flagship", "budget"},
        new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class}, 0);
    for (ProjectLeverage projectLeverage : project.getProjectLeverages().stream()
      .filter(pl -> pl.isActive() && pl.getYear() == this.year).collect(Collectors.toList())) {
      String title = null, partner_name = null, leverage_year = null, flagship = null, budget = null;

      if (projectLeverage.getTitle() != null && !projectLeverage.getTitle().isEmpty()) {
        title = projectLeverage.getTitle();
      }

      if (projectLeverage.getInstitution() != null && !projectLeverage.getInstitution().getComposedName().isEmpty()) {
        partner_name = projectLeverage.getInstitution().getComposedName();
      }

      if (projectLeverage.getYear() != null) {
        leverage_year = projectLeverage.getYear() + "";
      }

      if (projectLeverage.getCrpProgram() != null && !projectLeverage.getCrpProgram().getComposedName().isEmpty()) {
        flagship = projectLeverage.getCrpProgram().getComposedName();
      }

      if (projectLeverage.getBudget() != null) {
        budget = myFormatter.format(projectLeverage.getBudget());
      }

      model.addRow(new Object[] {projectLeverage.getId(), title, partner_name, leverage_year, flagship, budget});
    }
    return model;
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
    ProjectPartner projectLeader) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "center", "current_date", "project_submission", "cycle", "isNew", "isAdministrative",
        "type", "isGlobal", "isPhaseOne", "budget_gender", "hasTargetUnit"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, Boolean.class, Boolean.class,
        String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class});

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
      if (project.getNoRegional() != null && project.getNoRegional()) {
        title += "Global" + "-";
      } else {
        if (regions != null && !regions.isEmpty()) {
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
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String current_date = timezone.format(format) + "(GMT" + zone + ")";

    // Filling submission
    List<Submission> submissions = new ArrayList<>();
    for (Submission submission : project.getSubmissions().stream()
      .filter(c -> c.getCycle().equals(cycle) && c.getYear() == year && c.getUnSubmitUser() == null)
      .collect(Collectors.toList())) {
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
        if (cycle.equals("Reporting")) {
          submission = "Submission for " + cycle + " cycle " + year + ": &lt;not submited&gt;";
        } else {
          submission = "Submission for " + cycle + " cycle " + year + ": &lt;pending&gt;";
        }

      } else {
        submission = "Submission for " + "&lt;Not Defined&gt;" + " cycle " + "&lt;Not Defined&gt;" + " year"
          + ": &lt;Not Defined&gt;";
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

    Boolean hasGender = false;
    try {
      hasGender = Integer.parseInt(project.getCrp().getCrpParameters().stream()
        .filter(cp -> cp.isActive() && cp.getKey().equals(APConstants.CRP_BUDGET_GENDER)).collect(Collectors.toList())
        .get(0).getValue()) == 1;
    } catch (Exception e) {
      hasGender = false;
    }

    Boolean hasTargetUnit = false;
    if (targetUnitList.size() > 0) {
      hasTargetUnit = true;
    }

    model.addRow(new Object[] {title, centerAcry, current_date, submission, cycle, isNew, isAdministrative, type,
      project.isLocationGlobal(), this.isPhaseOne(), hasGender, hasTargetUnit});
    return model;
  }


  public List<IpElement> getMidOutcomeOutputs(long midOutcomeID) {
    List<IpProjectContribution> ipProjectContributions =
      project.getIpProjectContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setMogs(new ArrayList<>());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getMogs().add(ipProjectContribution.getIpElementByMogId());
    }
    List<IpElement> outputs = new ArrayList<>();
    IpElement midOutcome = ipElementManager.getIpElementById(midOutcomeID);

    if (this.isRegionalOutcome(midOutcome)) {
      List<IpElement> mogs = new ArrayList<>();

      List<IpElement> translatedOf =
        ipElementManager.getIPElementsRelated(midOutcome.getId().intValue(), APConstants.ELEMENT_RELATION_TRANSLATION);

      for (IpElement fsOutcome : translatedOf) {
        mogs.addAll(ipElementManager.getIPElementsByParent(fsOutcome, APConstants.ELEMENT_RELATION_CONTRIBUTION));
        for (IpElement mog : mogs) {
          if (!outputs.contains(mog)) {
            outputs.add(mog);
          }
        }
      }
    } else {
      outputs = ipElementManager.getIPElementsByParent(midOutcome, APConstants.ELEMENT_RELATION_CONTRIBUTION);

    }

    List<IpElement> elements = new ArrayList<>();
    elements.addAll(outputs);
    for (IpElement ipElement : elements) {
      if (!this.containsOutput(ipElement.getId(), midOutcomeID)) {
        outputs.remove(ipElement);
      }
    }
    return outputs;

  }

  private TypedTableModel getOtherContributionsCrpsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"crp_name", "collaboration_description"},
      new Class[] {String.class, String.class}, 0);


    for (ProjectCrpContribution projectCrpContribution : project.getProjectCrpContributions().stream()
      .filter(pcc -> pcc.isActive()).collect(Collectors.toList())) {
      String crp_name = null, collaboration_description = null;

      if (projectCrpContribution.getCrp() != null && !projectCrpContribution.getCrp().getName().isEmpty()) {
        crp_name = projectCrpContribution.getCrp().getName();
      }
      if (projectCrpContribution.getCollaborationNature() != null) {
        if (!projectCrpContribution.getCollaborationNature().isEmpty()) {
          collaboration_description = projectCrpContribution.getCollaborationNature();
        }
      }

      model.addRow(new Object[] {crp_name, collaboration_description});
    }

    return model;
  }

  private TypedTableModel getOtherContributionsDetailTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"region", "indicator", "contribution_description", "target_contribution", "otherContributionyear"},
      new Class[] {String.class, String.class, String.class, Integer.class, Integer.class}, 0);


    for (OtherContribution otherContribution : project.getOtherContributions().stream().filter(oc -> oc.isActive())
      .collect(Collectors.toList())) {
      String region = null, indicator = null, contribution_description = null;
      Integer target_contribution = null;
      int otherContributionyear = 0;
      if (otherContribution.getIpProgram() != null) {
        if (otherContribution.getIpProgram().getAcronym() != null) {
          if (!otherContribution.getIpProgram().getAcronym().isEmpty()) {
            region = otherContribution.getIpProgram().getAcronym();
          }
        }
      }
      if (otherContribution.getIpIndicator() != null
        && !otherContribution.getIpIndicator().getComposedName().isEmpty()) {
        indicator = otherContribution.getIpIndicator().getComposedName();
      }

      if (otherContribution.getDescription() != null) {
        if (!otherContribution.getDescription().isEmpty()) {
          contribution_description = otherContribution.getDescription();
        }
      }

      if (otherContribution.getTarget() != null) {
        target_contribution = otherContribution.getTarget();
      }

      otherContributionyear = this.getYear();

      model
        .addRow(new Object[] {region, indicator, contribution_description, target_contribution, otherContributionyear});

    }


    return model;
  }

  private TypedTableModel getOtherContributionsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"contribution"}, new Class[] {String.class}, 0);
    String contribution = null;

    for (ProjectOtherContribution projectOtherContribution : project.getProjectOtherContributions().stream()
      .filter(poc -> poc.isActive()).collect(Collectors.toList())) {

      if (projectOtherContribution.getContribution() != null && !projectOtherContribution.getContribution().isEmpty()) {
        contribution = projectOtherContribution.getContribution();

      }
    }
    model.addRow(new Object[] {contribution});
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

  private TypedTableModel getOverviewByMogsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"ipProgram", "ipElement", "bullet_points", "summary", "plan", "gender", "output_year",
        "changeYearTab", "isMidOutcome"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, Integer.class,
        Boolean.class, Boolean.class},
      0);
    int control_year = 0;
    Boolean changeYearTab = true;
    Boolean isMidOutcome = false;
    for (IpProjectContributionOverview ipProjectContributionOverview : project.getIpProjectContributionOverviews()
      .stream().sorted((co1, co2) -> co2.getYear() - co1.getYear()).filter(co -> co.isActive())
      .collect(Collectors.toList())) {
      // System.out.println(ipProjectContributionOverview.getYear());
      if (ipProjectContributionOverview.getYear() == APConstants.MID_OUTCOME_YEAR) {
        isMidOutcome = true;
      } else {
        isMidOutcome = false;
      }
      if (control_year == 0) {
        control_year = ipProjectContributionOverview.getYear();
      } else {
        if (control_year == ipProjectContributionOverview.getYear()) {
          changeYearTab = false;
        } else {
          changeYearTab = true;
          control_year = ipProjectContributionOverview.getYear();
        }

      }

      String ipProgram = null;
      String ipElement = null;
      String bullet_points = null;
      String summary = null;
      String plan = null;
      String gender = null;
      int output_year = 0;

      if (ipProjectContributionOverview.getIpElement() != null) {
        if (!ipProjectContributionOverview.getIpElement().getDescription().isEmpty()) {
          ipElement = ipProjectContributionOverview.getIpElement().getDescription();
        }
        if (ipProjectContributionOverview.getIpElement().getIpProgram() != null) {
          if (!ipProjectContributionOverview.getIpElement().getIpProgram().getAcronym().isEmpty()) {
            ipProgram = ipProjectContributionOverview.getIpElement().getIpProgram().getAcronym();
          }

        }
      }
      if (ipProjectContributionOverview.getAnualContribution() != null
        && !ipProjectContributionOverview.getAnualContribution().isEmpty()) {
        bullet_points = ipProjectContributionOverview.getAnualContribution();
      }

      if (ipProjectContributionOverview.getBriefSummary() != null
        && !ipProjectContributionOverview.getBriefSummary().isEmpty()) {
        summary = ipProjectContributionOverview.getBriefSummary();
      }
      if (ipProjectContributionOverview.getGenderContribution() != null
        && !ipProjectContributionOverview.getGenderContribution().isEmpty()) {
        plan = ipProjectContributionOverview.getGenderContribution();
      }
      if (ipProjectContributionOverview.getSummaryGender() != null
        && !ipProjectContributionOverview.getSummaryGender().isEmpty()) {
        gender = ipProjectContributionOverview.getSummaryGender();
      }

      output_year = ipProjectContributionOverview.getYear();

      model.addRow(new Object[] {ipProgram, ipElement, bullet_points, summary, plan, gender, output_year, changeYearTab,
        isMidOutcome});

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

  private TypedTableModel getPartnersLessonsTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"year", "lesson"}, new Class[] {Integer.class, String.class}, 0);
    if (!cycle.equals("")) {
      for (ProjectComponentLesson pcl : project.getProjectComponentLessons().stream()
        .sorted((p1, p2) -> p1.getYear() - p2.getYear()).filter(c -> c.isActive()
          && c.getComponentName().equals("partners") && c.getCycle().equals(cycle) && c.getYear() == this.getYear())
        .collect(Collectors.toList())) {
        String lessons = null;
        if (pcl.getLessons() != null && !pcl.getLessons().isEmpty()) {
          lessons = pcl.getLessons();
        }
        model.addRow(new Object[] {pcl.getYear(), lessons});
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
    TypedTableModel model =
      new TypedTableModel(new String[] {"count", "overall"}, new Class[] {Integer.class, String.class}, 0);
    int partnersSize = 0;
    List<ProjectPartner> projectPartners =
      project.getProjectPartners().stream().filter(pp -> pp.isActive()).collect(Collectors.toList());
    if (!projectPartners.isEmpty()) {
      partnersSize = projectPartners.size();
    }


    String overall = "";

    if (cycle.equals("Reporting")) {

      // Get project partners overall
      for (ProjectPartner projectPartner : project.getProjectPartners().stream().filter(pp -> pp.isActive())
        .collect(Collectors.toList())) {
        for (ProjectPartnerOverall projectPartnerOverall : projectPartner.getProjectPartnerOveralls().stream()
          .filter(ppo -> ppo.getYear() == year).collect(Collectors.toList())) {
          if (!projectPartnerOverall.getOverall().isEmpty()) {
            if (!projectPartnerOverall.getOverall().equals("null")) {
              overall = projectPartnerOverall.getOverall();
            }
          }
        }
      }

      if (overall.isEmpty()) {
        overall = "&lt;Not Defined&gt;";
      }
    }

    model.addRow(new Object[] {partnersSize, overall});

    return model;
  }


  private TypedTableModel getProjectHighlightReportingTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "author", "subject", "publisher", "year_reported", "highlights_types",
        "highlights_is_global", "start_date", "end_date", "keywords", "countries", "image", "highlight_desc",
        "introduction", "results", "partners", "links", "width", "heigth"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, Integer.class, Integer.class},
      0);

    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");


    for (ProjectHighlight projectHighlight : project.getProjectHighligths().stream()
      .filter(ph -> ph.isActive() && ph.getYear() != null && ph.getYear() >= this.getYear())
      .collect(Collectors.toList())) {
      String title = null, author = null, subject = null, publisher = null, highlights_types = "",
        highlights_is_global = null, start_date = null, end_date = null, keywords = null, countries = "", image = "",
        highlight_desc = null, introduction = null, results = null, partners = null, links = null;
      Long year_reported = null;
      int width = 0;
      int heigth = 0;

      if (projectHighlight.getTitle() != null && !projectHighlight.getTitle().isEmpty()) {
        title = projectHighlight.getTitle();
      }
      if (projectHighlight.getAuthor() != null && !projectHighlight.getAuthor().isEmpty()) {
        author = projectHighlight.getAuthor();
      }
      if (projectHighlight.getSubject() != null && !projectHighlight.getSubject().isEmpty()) {
        subject = projectHighlight.getSubject();
      }
      if (projectHighlight.getPublisher() != null && !projectHighlight.getPublisher().isEmpty()) {
        publisher = projectHighlight.getPublisher();
      }
      if (projectHighlight.getYear() != null) {
        year_reported = projectHighlight.getYear();
      }

      for (ProjectHighlightType projectHighlightType : projectHighlight.getProjectHighligthsTypes().stream()
        .filter(pht -> pht.isActive()).collect(Collectors.toList())) {
        if (ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "") != null) {
          highlights_types +=
            "<br>● " + ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "").getDescription();
        }
      }
      if (highlights_types.isEmpty()) {
        highlights_types = null;
      }
      if (projectHighlight.isGlobal() == true) {
        highlights_is_global = "Yes";
      } else {
        highlights_is_global = "No";
      }

      if (projectHighlight.getStartDate() != null) {
        start_date = formatter.format(projectHighlight.getStartDate());
      }

      if (projectHighlight.getEndDate() != null) {
        end_date = formatter.format(projectHighlight.getEndDate());
      }
      if (projectHighlight.getKeywords() != null && !projectHighlight.getKeywords().isEmpty()) {
        keywords = projectHighlight.getKeywords();
      }

      int countriesFlag = 0;
      for (ProjectHighlightCountry projectHighlightCountry : projectHighlight.getProjectHighligthCountries().stream()
        .filter(phc -> phc.isActive()).collect(Collectors.toList())) {

        if (projectHighlightCountry.getLocElement() != null) {
          if (countriesFlag == 0) {
            countries += projectHighlightCountry.getLocElement().getName();
            countriesFlag++;
          } else {
            countries += ", " + projectHighlightCountry.getLocElement().getName();
            countriesFlag++;
          }
        }
      }

      if (countries.isEmpty()) {
        countries = null;
      }

      if (projectHighlight.getFile() != null) {
        double pageWidth = 612 * 0.4;
        double pageHeigth = 792 * 0.4;
        double imageWidth = 0;
        double imageHeigth = 0;
        image =
          this.getHightlightImagePath(projectHighlight.getProject().getId()) + projectHighlight.getFile().getFileName();

        Image imageFile = null;

        LOG.info("image.getURL.replace " + image);
        File url;
        try {
          url = new File(image);
        } catch (Exception e) {
          e.printStackTrace();
          url = null;
        }
        if (url != null && url.exists()) {
          // System.out.println("Project: " + projectHighlight.getProject().getId() + " PH: " +
          // projectHighlight.getId());
          try {
            imageFile = Image.getInstance(FileManager.readURL(url));
            // System.out.println("W: " + imageFile.getWidth() + " \nH: " + imageFile.getHeight());
            if (imageFile.getWidth() >= imageFile.getHeight()) {
              imageWidth = pageWidth;
              imageHeigth = imageFile.getHeight() * (((pageWidth * 100) / imageFile.getWidth()) / 100);
            } else {
              imageHeigth = pageHeigth;
              imageWidth = imageFile.getWidth() * (((pageHeigth * 100) / imageFile.getHeight()) / 100);
            }
            // System.out.println("New W: " + imageWidth + " \nH: " + imageHeigth);
            width = (int) imageWidth;
            heigth = (int) imageHeigth;
            // If successful, process the message
          } catch (BadElementException e) {
            // System.out.println("Unable to retrieve Image!!");
            image = "";
            e.printStackTrace();
          } catch (MalformedURLException e) {
            // System.out.println("Unable to retrieve Image!!");
            image = "";
            e.printStackTrace();
          } catch (IOException e) {
            // System.out.println("Unable to retrieve Image!!");
            image = "";
            e.printStackTrace();
          }
        }
      }

      if (projectHighlight.getDescription() != null && !projectHighlight.getDescription().isEmpty()) {
        highlight_desc = projectHighlight.getDescription();
      }

      if (projectHighlight.getObjectives() != null && !projectHighlight.getObjectives().isEmpty()) {
        introduction = projectHighlight.getObjectives();
      }
      if (projectHighlight.getResults() != null && !projectHighlight.getResults().isEmpty()) {
        results = projectHighlight.getResults();
      }
      if (projectHighlight.getPartners() != null && !projectHighlight.getPartners().isEmpty()) {
        partners = projectHighlight.getPartners();
      }
      if (projectHighlight.getLinks() != null && !projectHighlight.getLinks().isEmpty()) {
        links = projectHighlight.getLinks();
      }

      model.addRow(new Object[] {projectHighlight.getId(), title, author, subject, publisher, year_reported,
        highlights_types, highlights_is_global, start_date, end_date, keywords, countries, image, highlight_desc,
        introduction, results, partners, links, width, heigth});
    }

    return model;
  }

  public long getProjectID() {
    return projectID;
  }

  private TypedTableModel getProjectOtherOutcomesTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"out_statement", "year"}, new Class[] {String.class, Integer.class}, 0);
    String out_statement = "";
    int outcomeYear = 0;
    for (ProjectOutcomePandr projectOutcomePandr : project.getProjectOutcomesPandr().stream()
      .sorted((p1, p2) -> p1.getYear() - p2.getYear())
      .filter(pop -> pop.isActive() && pop.getYear() != 2019 && pop.getYear() != year).collect(Collectors.toList())) {
      out_statement = projectOutcomePandr.getStatement();
      outcomeYear = projectOutcomePandr.getYear();
      model.addRow(new Object[] {out_statement, outcomeYear});
    }


    return model;
  }

  private TypedTableModel getProjectOutcomesTableModel() {

    TypedTableModel model = new TypedTableModel(
      new String[] {"out_statement", "out_statement_current", "out_progress_current", "communication_current",
        "current_year", "lessons", "file"},
      new Class[] {String.class, String.class, String.class, String.class, Integer.class, String.class, String.class},
      0);
    String out_statement = null;
    String out_statement_current = null;
    String communication_current = null;
    String out_progress_current = null;
    String file = null;
    String lessons = null;

    for (ProjectOutcomePandr projectOutcomePandr : project.getProjectOutcomesPandr().stream()
      .filter(pop -> pop.isActive() && (pop.getYear() == APConstants.MID_OUTCOME_YEAR || pop.getYear() == year))
      .collect(Collectors.toList())) {


      if (projectOutcomePandr.getYear() == APConstants.MID_OUTCOME_YEAR) {
        if (projectOutcomePandr.getStatement() != null) {
          if (!projectOutcomePandr.getStatement().isEmpty()) {
            out_statement = projectOutcomePandr.getStatement();
          }
        }
      }

      if (projectOutcomePandr.getYear() == year) {
        if (projectOutcomePandr.getStatement() != null) {
          if (!projectOutcomePandr.getStatement().isEmpty()) {
            out_statement_current = projectOutcomePandr.getStatement();
          }
        }
        if (projectOutcomePandr.getComunication() != null) {
          if (!projectOutcomePandr.getComunication().isEmpty()) {
            communication_current = projectOutcomePandr.getComunication();
          }
        }
        if (projectOutcomePandr.getAnualProgress() != null) {
          if (!projectOutcomePandr.getAnualProgress().isEmpty()) {
            out_progress_current = projectOutcomePandr.getAnualProgress();
          }
        }

        if (projectOutcomePandr.getFile() != null) {
          file = (this.getProjectOutcomeUrl() + projectOutcomePandr.getFile().getFileName()).replace(" ", "%20");
        }

      }
    }

    if (!cycle.equals("")) {
      for (ProjectComponentLesson pcl : project.getProjectComponentLessons().stream()
        .sorted((p1, p2) -> p1.getYear() - p2.getYear()).filter(pcl -> pcl.isActive()
          && pcl.getComponentName().equals("outcomesPandR") && pcl.getCycle().equals(cycle) && pcl.getYear() == year)
        .collect(Collectors.toList())) {
        if (pcl.getLessons() != null) {
          if (!pcl.getLessons().isEmpty()) {
            lessons = pcl.getLessons();
          }
        }
      }
    }

    model.addRow(new Object[] {out_statement, out_statement_current, out_progress_current, communication_current, year,
      lessons, file});
    return model;

  }


  public String getProjectOutcomeUrl() {
    return config.getDownloadURL() + "/" + this.getProjectOutcomeUrlPath().replace('\\', '/');
  }

  public String getProjectOutcomeUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project.getId() + File.separator
      + "projectOutcome" + File.separator;
  }

  private TypedTableModel getRLTableModel(List<CrpProgram> regions) {
    TypedTableModel model = new TypedTableModel(new String[] {"RL"}, new Class[] {String.class}, 0);
    String global = "";
    if (project.getNoRegional() != null && project.getNoRegional()) {
      global = "Global";
      model.addRow(new Object[] {global});
    } else {
      for (CrpProgram crpProgram : regions) {
        model.addRow(new Object[] {crpProgram.getComposedName()});
      }
    }
    return model;
  }


  public HashMap<Long, String> getTargetUnitList() {
    return targetUnitList;
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

  public boolean isRegionalOutcome(IpElement outcome) {

    List<IpElement> translatedOf =
      ipElementManager.getIPElementsRelated(outcome.getId().intValue(), APConstants.ELEMENT_RELATION_TRANSLATION);
    return !translatedOf.isEmpty();
  }

  @Override
  public void prepare() {
    try {
      loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getCrpById(loggedCrp.getId());
      this
        .setProjectID(Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID))));
      this.setCrpSession(loggedCrp.getAcronym());

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
    // Fill target unit list
    targetUnitList = new HashMap<>();
    if (srfTargetUnitManager.findAll() != null) {

      List<SrfTargetUnit> targetUnits = new ArrayList<>();

      List<CrpTargetUnit> crpTargetUnits = new ArrayList<>(
        loggedCrp.getCrpTargetUnits().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));

      for (CrpTargetUnit crpTargetUnit : crpTargetUnits) {
        targetUnits.add(crpTargetUnit.getSrfTargetUnit());
      }

      Collections.sort(targetUnits,
        (tu1, tu2) -> tu1.getName().toLowerCase().trim().compareTo(tu2.getName().toLowerCase().trim()));

      for (SrfTargetUnit srfTargetUnit : targetUnits) {
        targetUnitList.put(srfTargetUnit.getId(), srfTargetUnit.getName());
      }
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


  public void setTargetUnitList(HashMap<Long, String> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }

  public void setYear(int year) {
    this.year = year;
  }


}