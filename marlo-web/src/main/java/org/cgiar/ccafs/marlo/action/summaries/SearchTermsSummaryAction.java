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
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

public class SearchTermsSummaryAction extends BaseAction implements Summary {

  private static Logger LOG = LoggerFactory.getLogger(SearchTermsSummaryAction.class);
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // Variables
  private Crp loggedCrp;
  private long startTime;
  private String cycle;
  private int year;
  // Keys to be searched
  List<String> keys = new ArrayList<String>();

  // Managers
  private CrpManager crpManager;
  private CrpProgramManager programManager;

  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;

  @Inject
  public SearchTermsSummaryAction(APConfig config, CrpManager crpManager, CrpProgramManager programManager) {
    super(config);
    this.crpManager = crpManager;
    this.programManager = programManager;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    try {
      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/search_terms.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = loggedCrp.getName();
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String currentDate = timezone.format(format) + "(GMT" + zone + ")";
      String parameters = this.getRequest().getParameter("keys");
      if (parameters != null) {
        if (parameters.isEmpty()) {
          // Empty keys
        } else {
          keys = Arrays.asList(parameters.split("~/"));
        }
      }
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, currentDate);
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
      this.fillSubreport((SubReport) hm.get("projects_details"), "project");
      this.fillSubreport((SubReport) hm.get("projects_activities"), "activities");
      this.fillSubreport((SubReport) hm.get("projects_deliverables"), "deliverables");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating PDF " + e.getMessage());
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
      case "project":
        model = this.getProjectsTableModel();
        break;
      case "activities":
        model = this.getActivitiesTableModel();
        break;
      case "deliverables":
        model = this.getDeliverablesTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  private TypedTableModel getActivitiesTableModel() {

    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "title", "act_id", "act_title", "act_desc", "start_date", "end_date", "lead_ins",
        "leader", "project_url"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);
    if (!keys.isEmpty()) {
      // Pattern case insensitive
      String patternString = "(?i)\\b(" + StringUtils.join(keys, "|") + ")\\b";
      Pattern pattern = Pattern.compile(patternString);
      // date format for star and end dates
      SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM yyyy");
      // Search projects with activities
      List<Project> projects = new ArrayList<>();
      if (!cycle.equals(APConstants.REPORTING)) {
        projects = loggedCrp.getProjects().stream()
          .filter(p -> p.isActive() && p.getStatus() != null
            && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            && p.getActivities().size() > 0)
          .collect(Collectors.toList());
      } else {
        projects =
          loggedCrp.getProjects().stream().filter(p -> p.isActive() && p.getStatus() != null && p.getReporting() != null
            && p.getReporting().booleanValue() && p.getActivities().size() > 0).collect(Collectors.toList());
      }
      for (Project project : projects) {
        // Get active activities
        for (Activity activity : project.getActivities().stream().filter(a -> a.isActive())
          .collect(Collectors.toList())) {
          String actTit = activity.getTitle();
          String actDesc = activity.getDescription();
          String startDate = null;
          String endDate = null;
          String projectTitle = project.getTitle();
          String insLeader = "";
          String leader = "";
          // Search keys in activity title
          // count and store occurrences
          Set<String> matchesTitle = new HashSet<>();
          Set<String> matchesDescription = new HashSet<>();
          if (activity.getTitle() != null) {
            actTit = "<font size=2 face='Segoe UI' color='#000000'>" + activity.getTitle() + "</font>";
            // Find keys in title
            Matcher matcher = pattern.matcher(actTit);
            // while are occurrences
            while (matcher.find()) {
              // add elements to matches
              matchesTitle.add(matcher.group(1));
            }
            for (String match : matchesTitle) {
              actTit = actTit.replaceAll("\\b" + match + "\\b",
                "<font size=2 face='Segoe UI' color='#FF0000'><b>$0</b></font>");
            }
          } else {
            actTit = "<font size=2 face='Segoe UI' color='#000000'></font>";
          }
          if (activity.getDescription() != null) {
            actDesc = "<font size=2 face='Segoe UI' color='#000000'>" + activity.getDescription() + "</font>";
            // Hash set list of matches, avoiding duplicates
            // Find keys in description
            Matcher matcher = pattern.matcher(actDesc);
            // while are occurrences
            while (matcher.find()) {
              // add elements to matches
              matchesDescription.add(matcher.group(1));
            }
            for (String match : matchesDescription) {
              actDesc = actDesc.replaceAll("\\b" + match + "\\b",
                "<font size=2 face='Segoe UI' color='#FF0000'><b>$0</b></font>");
            }
          } else {
            actDesc = "<font size=2 face='Segoe UI' color='#000000'></font>";
          }
          // If matches is found
          if ((matchesDescription.size() + matchesTitle.size()) > 0) {
            // set dates
            if (activity.getStartDate() != null) {
              startDate = "<font size=2 face='Segoe UI' color='#000000'>"
                + dateFormatter.format(activity.getStartDate()) + "</font>";
            } else {
              startDate = "<font size=2 face='Segoe UI' color='#000000'></font>";
            }
            if (activity.getEndDate() != null) {
              endDate = "<font size=2 face='Segoe UI' color='#000000'>" + dateFormatter.format(activity.getEndDate())
                + "</font>";
            } else {
              endDate = "<font size=2 face='Segoe UI' color='#000000'></font>";
            }
            if (project.getTitle() != null) {
              projectTitle = "<font size=2 face='Segoe UI' color='#000000'>" + project.getTitle() + "</font>";
            } else {
              projectTitle = "<font size=2 face='Segoe UI' color='#000000'></font>";
            }
            String projectId =
              "<font size=2 face='Segoe UI' color='#0000ff'>P" + project.getId().toString() + "</font>";
            String projectU = project.getId().toString();
            String actId = "<font size=2 face='Segoe UI' color='#0000ff'>A" + activity.getId().toString() + "</font>";
            // Set leader
            if (activity.getProjectPartnerPerson() != null) {
              leader = "<font size=2 face='Segoe UI' color='#000000'>"
                + activity.getProjectPartnerPerson().getUser().getComposedName() + "\n&lt;"
                + activity.getProjectPartnerPerson().getUser().getEmail() + "&gt;</font>";
            }
            if (leader.isEmpty()) {
              leader = "<font size=2 face='Segoe UI' color='#000000'></font>";
            }
            // Set leader institution
            if (activity.getProjectPartnerPerson() != null) {
              if (activity.getProjectPartnerPerson().getProjectPartner() != null) {
                if (activity.getProjectPartnerPerson().getProjectPartner().getInstitution() != null) {
                  insLeader = "<font size=2 face='Segoe UI' color='#000000'>";
                  insLeader +=
                    activity.getProjectPartnerPerson().getProjectPartner().getInstitution().getComposedName();
                  if (activity.getProjectPartnerPerson().getProjectPartner().getInstitution().getLocElement() != null) {
                    insLeader += " - " + activity.getProjectPartnerPerson().getProjectPartner().getInstitution()
                      .getLocElement().getName();
                  }
                }
              }
            }
            if (insLeader.isEmpty()) {
              insLeader = "<font size=2 face='Segoe UI' color='#000000'></font>";
            } else {
              insLeader += "</font>";
            }
            model.addRow(new Object[] {projectId, projectTitle, actId, actTit, actDesc, startDate, endDate,
              insLeader, leader, projectU});
          }
        }
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

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  private TypedTableModel getDeliverablesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "title", "dev_id", "dev_title", "dev_type", "dev_sub_type", "lead_ins", "leader",
        "project_url", "dev_url"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class},
      0);
    if (!keys.isEmpty()) {
      List<Project> projects = new ArrayList<>();
      if (!cycle.equals(APConstants.REPORTING)) {
        projects = loggedCrp.getProjects().stream()
          .filter(p -> p.isActive() && p.getStatus() != null
            && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            && p.getDeliverables().size() > 0)
          .collect(Collectors.toList());
      } else {
        projects =
          loggedCrp.getProjects().stream().filter(p -> p.isActive() && p.getStatus() != null && p.getReporting() != null
            && p.getReporting().booleanValue() && p.getDeliverables().size() > 0).collect(Collectors.toList());
      }
      for (Project project : projects) {
        for (Deliverable deliverable : project.getDeliverables().stream().filter(d -> d.isActive())
          .collect(Collectors.toList())) {
          String devTitle = "";
          // Pattern case insensitive
          String patternString = "(?i)\\b(" + StringUtils.join(keys, "|") + ")\\b";
          Pattern pattern = Pattern.compile(patternString);
          // Search keys in deliverable title
          // count and store occurrences
          Set<String> matchesDelivTitle = new HashSet<>();
          if (deliverable.getTitle() != null) {
            devTitle = "<font size=2 face='Segoe UI' color='#000000'>" + deliverable.getTitle() + "</font>";
            // Find keys in title
            Matcher matcher = pattern.matcher(devTitle);
            // while are occurrences
            while (matcher.find()) {
              // add elements to matches
              matchesDelivTitle.add(matcher.group(1));
            }
            for (String match : matchesDelivTitle) {
              devTitle = devTitle.replaceAll("\\b" + match + "\\b",
                "<font size=2 face='Segoe UI' color='#FF0000'><b>$0</b></font>");
            }
          } else {
            devTitle = "<font size=2 face='Segoe UI' color='#000000'></font>";
          }
          if (matchesDelivTitle.size() > 0) {
            String projectId =
              "<font size=2 face='Segoe UI' color='#0000ff'>P" + project.getId().toString() + "</font>";
            String projectUrl = project.getId().toString();
            String title = project.getTitle();
            String devId =
              "<font size=2 face='Segoe UI' color='#0000ff'>D" + deliverable.getId().toString() + "</font>";
            String devUrl = deliverable.getId().toString();
            String devType = "<font size=2 face='Segoe UI' color='#000000'></font>";
            String devSubType = "<font size=2 face='Segoe UI' color='#000000'></font>";
            String leadIns = "<font size=2 face='Segoe UI' color='#000000'></font>";
            String leader = "<font size=2 face='Segoe UI' color='#000000'></font>";
            if (project.getTitle() != null) {
              title = "<font size=2 face='Segoe UI' color='#000000'>" + project.getTitle() + "</font>";
            } else {
              title = "<font size=2 face='Segoe UI' color='#000000'></font>";
            }
            if (deliverable.getDeliverableType() != null) {
              if (deliverable.getDeliverableType().getDeliverableType() != null) {
                devType = "<font size=2 face='Segoe UI' color='#000000'>"
                  + deliverable.getDeliverableType().getDeliverableType().getName() + "</font>";
                devSubType = "<font size=2 face='Segoe UI' color='#000000'>"
                  + deliverable.getDeliverableType().getName() + "</font>";
              } else {
                devType = "<font size=2 face='Segoe UI' color='#000000'>D" + deliverable.getDeliverableType().getName()
                  + "</font>";
              }
            }
            // Get partner responsible and institution
            // Set responible;
            DeliverablePartnership responisble = this.responsiblePartner(deliverable);
            if (responisble != null) {
              if (responisble.getProjectPartnerPerson() != null) {
                ProjectPartnerPerson responsibleppp = responisble.getProjectPartnerPerson();
                leader = "<font size=2 face='Segoe UI' color='#000000'>" + responsibleppp.getUser().getComposedName()
                  + "\n&lt;" + responsibleppp.getUser().getEmail() + "&gt;</font>";
                if (responsibleppp.getInstitution() != null) {
                  leadIns = "<font size=2 face='Segoe UI' color='#000000'>"
                    + responsibleppp.getInstitution().getComposedName() + "</font>";
                }
              }
            }
            model.addRow(new Object[] {projectId, title, devId, devTitle, devType, devSubType, leadIns, leader,
              projectUrl, devUrl});
          }
        }
      }
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
    fileName.append("SearchTermsSummary-");
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

  private TypedTableModel getMasterTableModel(String center, String date) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "keys", "regionalAvailable"},
      new Class[] {String.class, String.class, String.class, Boolean.class});
    String keysString = "";
    int countKeys = 0;
    for (String key : keys) {
      if (countKeys == 0) {
        keysString += "\"" + key + "\"";
        countKeys++;
      } else {
        keysString += ", \"" + key + "\"";
        countKeys++;
      }
    }
    model.addRow(new Object[] {center, date, keysString, this.hasProgramnsRegions()});
    return model;
  }

  private TypedTableModel getProjectsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"project_id", "title", "summary", "start_date", "end_date", "flagships", "regions", "lead_ins",
        "leader", "w1w2_budget", "w3_budget", "bilateral_budget", "center_budget", "project_url"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, Double.class, Double.class, Double.class, Double.class, String.class},
      0);
    if (!keys.isEmpty()) {
      // Pattern case insensitive
      String patternString = "(?i)\\b(" + StringUtils.join(keys, "|") + ")\\b";
      Pattern pattern = Pattern.compile(patternString);
      // date format for star and end dates
      SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM yyyy");
      // Decimal format for budgets
      List<Project> projects = new ArrayList<>();
      if (!cycle.equals(APConstants.REPORTING)) {
        projects = loggedCrp.getProjects().stream()
          .filter(p -> p.isActive() && p.getStatus() != null
            && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
          .collect(Collectors.toList());
      } else {
        projects = loggedCrp.getProjects().stream()
          .filter(
            p -> p.isActive() && p.getStatus() != null && p.getReporting() != null && p.getReporting().booleanValue())
          .collect(Collectors.toList());
      }
      for (Project project : projects) {
        String title = project.getTitle();
        String summary = project.getSummary();
        String startDate = null;
        String endDate = null;
        String flagships = "";
        String regions = "";
        String insLeader = "";
        String leader = "";
        Double w1w2 = null;
        Double w3 = null;
        Double bilateral = null;
        Double center = null;
        // count and store occurrences
        Set<String> matchesTitle = new HashSet<>();
        Set<String> matchesSummary = new HashSet<>();
        if (project.getTitle() != null) {
          title = "<font size=2 face='Segoe UI' color='#000000'>" + project.getTitle() + "</font>";
          // Hash set list of matches, avoiding duplicates
          // Find keys in title
          Matcher matcher = pattern.matcher(title);
          // while are occurrences
          while (matcher.find()) {
            // add elements to matches
            matchesTitle.add(matcher.group(1));
          }
          for (String match : matchesTitle) {
            title =
              title.replaceAll("\\b" + match + "\\b", "<font size=2 face='Segoe UI' color='#FF0000'><b>$0</b></font>");
          }
        } else {
          title = "<font size=2 face='Segoe UI' color='#000000'></font>";
        }
        if (project.getSummary() != null) {
          summary = "<font size=2 face='Segoe UI' color='#000000'>" + project.getSummary() + "</font>";
          // Hash set list of matches, avoiding duplicates
          // Find keys in title
          Matcher matcher = pattern.matcher(summary);
          // while are occurrences
          while (matcher.find()) {
            // add elements to matches
            matchesSummary.add(matcher.group(1));
          }
          for (String match : matchesSummary) {
            summary = summary.replaceAll("\\b" + match + "\\b",
              "<font size=2 face='Segoe UI' color='#FF0000'><b>$0</b></font>");
          }
        } else {
          summary = "<font size=2 face='Segoe UI' color='#000000'></font>";
        }
        if ((matchesSummary.size() + matchesTitle.size()) > 0) {
          // set dates
          if (project.getStartDate() != null) {
            startDate = "<font size=2 face='Segoe UI' color='#000000'>" + dateFormatter.format(project.getStartDate())
              + "</font>";
          } else {
            startDate = "<font size=2 face='Segoe UI' color='#000000'></font>";
          }
          if (project.getEndDate() != null) {
            endDate =
              "<font size=2 face='Segoe UI' color='#000000'>" + dateFormatter.format(project.getEndDate()) + "</font>";
          } else {
            endDate = "<font size=2 face='Segoe UI' color='#000000'></font>";
          }
          // get Flagships related to the project sorted by acronym
          int countFlagships = 0;
          for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
            .sorted((o1, o2) -> o1.getCrpProgram().getAcronym().compareTo(o2.getCrpProgram().getAcronym()))
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            if (countFlagships == 0) {
              flagships += "<font size=2 face='Segoe UI' color='#000000'>"
                + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
              countFlagships++;
            } else {
              flagships += ", " + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
              countFlagships++;
            }
          }
          if (flagships.isEmpty()) {
            flagships = "<font size=2 face='Segoe UI' color='#000000'></font>";
          } else {
            flagships += "</font>";
          }
          // If has regions, add the regions to regionsArrayList
          if (project.getNoRegional() != null && project.getNoRegional()) {
            regions = "<font size=2 face='Segoe UI' color='#000000'>Global";
          } else {
            // Get Regions related to the project sorted by acronym
            int countRegions = 0;
            for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
              .sorted((c1, c2) -> c1.getCrpProgram().getAcronym().compareTo(c2.getCrpProgram().getAcronym()))
              .filter(
                c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
              .collect(Collectors.toList())) {
              if (countRegions == 0) {
                regions += "<font size=2 face='Segoe UI' color='#000000'>"
                  + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
                countRegions++;
              } else {
                regions += ", " + programManager.getCrpProgramById(projectFocuses.getCrpProgram().getId()).getAcronym();
                countRegions++;
              }
            }
          }
          if (regions.isEmpty()) {
            regions = "<font size=2 face='Segoe UI' color='#000000'></font>";
          } else {
            regions += "</font>";
          }
          // Set leader institution
          ProjectPartner projectLeader = project.getLeader();
          if (projectLeader != null) {
            if (projectLeader.getInstitution() != null) {
              insLeader = "<font size=2 face='Segoe UI' color='#000000'>";
              insLeader += projectLeader.getInstitution().getComposedName();
              if (projectLeader.getInstitution().getLocElement() != null) {
                insLeader += " - " + projectLeader.getInstitution().getLocElement().getName();
              }
            }
          }
          if (insLeader.isEmpty()) {
            insLeader = "<font size=2 face='Segoe UI' color='#000000'></font>";
          } else {
            insLeader += "</font>";
          }
          // Set leader
          if (project.getLeaderPerson() != null) {
            leader = "<font size=2 face='Segoe UI' color='#000000'>";
            leader =
              "<font size=2 face='Segoe UI' color='#000000'>" + project.getLeaderPerson().getUser().getComposedName()
                + "\n&lt;" + project.getLeaderPerson().getUser().getEmail() + "&gt;</font>";
          }
          if (leader.isEmpty()) {
            leader = "<font size=2 face='Segoe UI' color='#000000'></font>";
          }
          // Set budgets
          int year = this.getCurrentCycleYear();
          if (this.getTotalYear(year, 1, project) != 0.0) {
            w1w2 = this.getTotalYear(year, 1, project);
          }
          if (this.getTotalYear(year, 2, project) != 0.0) {
            w3 = this.getTotalYear(year, 2, project);
          }
          if (this.getTotalYear(year, 3, project) != 0.0) {
            bilateral = this.getTotalYear(year, 3, project);
          }
          if (this.getTotalYear(year, 4, project) != 0.0) {
            center = this.getTotalYear(year, 4, project);
          }
          String projectId = "<font size=2 face='Segoe UI' color='#0000ff'>P" + project.getId().toString() + "</font>";
          String projectUrl = project.getId().toString();
          model.addRow(new Object[] {projectId, title, summary, startDate, endDate, flagships, regions, insLeader,
            leader, w1w2, w3, bilateral, center, projectUrl});
        }
      }
    }
    return model;
  }

  /**
   * Get the total budget per year and type
   * 
   * @param year current year in the platform
   * @param type budget type (W1W2/Bilateral/W3/Center funds)
   * @return total budget in the year and type passed as parameters
   */
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
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Cycle: " + cycle);
  }

  private DeliverablePartnership responsiblePartner(Deliverable deliverable) {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
    } catch (Exception e) {
      LOG.warn("Error getting DeliverablePartnership. Exception: " + e.getMessage());
      return null;
    }
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
