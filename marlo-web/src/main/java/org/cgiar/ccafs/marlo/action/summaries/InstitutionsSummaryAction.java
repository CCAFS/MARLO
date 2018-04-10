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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
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

public class InstitutionsSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 4871536980466987571L;

  private static Logger LOG = LoggerFactory.getLogger(InstitutionsSummaryAction.class);

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  // Parameters
  private long startTime;
  HashMap<InstitutionType, Set<Institution>> institutionsPerType = new HashMap<InstitutionType, Set<Institution>>();
  HashMap<Institution, Set<Project>> projectsPerInstitution = new HashMap<Institution, Set<Project>>();
  Set<LocElement> countries = new HashSet<>();
  Set<Project> projects = new HashSet<>();

  private String partnerType;
  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;

  @Inject
  public InstitutionsSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    if (partnerType.equals("All")) {
      masterReport.getParameterValues().put("i8nSummaryDescription",
        this.getText("summaries.partners.description", new String[] {this.getSelectedCycle()}));
      masterReport.getParameterValues().put("i8nHeader",
        this.getText("summaries.partners.header", new String[] {this.getLoggedCrp().getAcronym()}));
    } else {
      masterReport.getParameterValues().put("i8nSummaryDescription",
        this.getText("summaries.partners.leader.description", new String[] {this.getSelectedCycle()}));
      masterReport.getParameterValues().put("i8nHeader",
        this.getText("summaries.partners.leader.header", new String[] {this.getLoggedCrp().getAcronym()}));
    }

    masterReport.getParameterValues().put("i8nName", this.getText("summaries.partners.name"));
    masterReport.getParameterValues().put("i8nAcronym", this.getText("summaries.partners.acronym"));
    masterReport.getParameterValues().put("i8nWebSite", this.getText("summaries.partners.website"));
    masterReport.getParameterValues().put("i8nType", this.getText("summaries.partners.type"));
    masterReport.getParameterValues().put("i8nCountry", this.getText("summaries.partners.country"));
    masterReport.getParameterValues().put("i8nProjects", this.getText("caseStudy.projects"));
    masterReport.getParameterValues().put("i8nProjectsTitle", this.getText("summaries.partners.projectTitle"));
    return masterReport;
  }

  private void createIntitutionsProjectsList() {
    for (GlobalUnitProject globalUnitProject : this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(p -> p.isActive() && p.getProject() != null && p.getProject().isActive()
        && p.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
        && (p.getProject().getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || p.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())))
      .collect(Collectors.toList())) {

      List<ProjectPartner> projectPartnerList = globalUnitProject.getProject().getProjectPartners().stream()
        .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().equals(this.getSelectedPhase()))
        .collect(Collectors.toList());


      if (!partnerType.equals("All")) {
        List<ProjectPartner> projectPartnerLeaderList = new ArrayList<>();
        for (ProjectPartner projectPartner : projectPartnerList) {
          projectPartner.setPartnerPersons(projectPartner.getProjectPartnerPersons().stream()
            .filter(ppp -> ppp.isActive()).collect(Collectors.toList()));
          if (projectPartner.isLeader()) {
            projectPartnerLeaderList.add(projectPartner);
          }
        }
        projectPartnerList = projectPartnerLeaderList;
      }

      for (ProjectPartner projectPartner : projectPartnerList) {
        if (projectPartner.getInstitution() != null) {
          Project project = projectPartner.getProject();
          Institution institution = projectPartner.getInstitution();
          InstitutionType institutionType = institution.getInstitutionType();
          List<InstitutionLocation> institutionLocation = institution.getInstitutionsLocations().stream()
            .filter(l -> l.isActive() && l.isHeadquater()).collect(Collectors.toList());
          if (institutionLocation != null && institutionLocation.size() > 0) {
            countries.add(institutionLocation.get(0).getLocElement());
          }
          projects.add(project);
          // institutionsPerType
          if (institutionsPerType.containsKey(institutionType)) {
            Set<Institution> institutionSet = institutionsPerType.get(institutionType);
            institutionSet.add(institution);
            institutionsPerType.put(institutionType, institutionSet);
          } else {
            Set<Institution> institutionSet = new HashSet<>();
            institutionSet.add(institution);
            institutionsPerType.put(institutionType, institutionSet);
          }

          // projectsPerInstitution
          if (projectsPerInstitution.containsKey(institution)) {
            Set<Project> projectSet = projectsPerInstitution.get(institution);
            projectSet.add(project);
            projectsPerInstitution.put(institution, projectSet);
          } else {
            Set<Project> projectSet = new HashSet<>();
            projectSet.add(project);
            projectsPerInstitution.put(institution, projectSet);
          }

        }
      }
    }
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    try {
      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/crp/ProjectPartners.prpt"), MasterReport.class);

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
      this.createIntitutionsProjectsList();
      masterReport.getParameterValues().put("total_inst", projectsPerInstitution.size());
      masterReport.getParameterValues().put("total_countries", countries.size());
      masterReport.getParameterValues().put("total_projects", projects.size());
      this.fillSubreport((SubReport) hm.get("details"), "details");
      this.fillSubreport((SubReport) hm.get("summary"), "summary");
      this.fillSubreport((SubReport) hm.get("institution_types"), "institution_types");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating Institutions " + e.getMessage());
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
      case "details":
        model = this.getDetailsTableModel();
        break;
      case "summary":
        model = this.getSummaryTableModel();
        break;
      case "institution_types":
        model = this.getInstitutionTypesTableModel();
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

  private TypedTableModel getDetailsTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"ins_name", "ins_acr", "web_site", "ins_type", "country", "Projects"},
        new Class[] {String.class, String.class, String.class, String.class, String.class, String.class}, 0);

    Map<Institution, Set<Project>> result = projectsPerInstitution.entrySet().stream()
      .sorted((s1, s2) -> s1.getKey().getName().compareTo(s2.getKey().getName())).collect(
        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    for (Institution institution : result.keySet()) {
      String insName = "", insAcr = "", webSite = "", insType = "", country = "", projects = "";
      insName = institution.getName();
      insAcr = institution.getAcronym();
      webSite = institution.getWebsiteLink();
      insType = institution.getInstitutionType().getName();
      List<InstitutionLocation> locations = institution.getInstitutionsLocations().stream()
        .filter(l -> l.isActive() && l.isHeadquater()).collect(Collectors.toList());
      if (locations != null && !locations.isEmpty()) {
        country = locations.get(0).getLocElement().getName();
      }

      Set<Project> projectSet = projectsPerInstitution.get(institution);
      if (projectSet != null && !projectSet.isEmpty()) {
        List<Project> projectList =
          projectSet.stream().sorted((p1, p2) -> p1.getId().compareTo(p2.getId())).collect(Collectors.toList());
        for (Project project : projectList) {
          if (projects.isEmpty()) {
            projects += "P" + project.getId();
          } else {
            projects += ", P" + project.getId();
          }
        }
      }
      model.addRow(new Object[] {insName, insAcr, webSite, insType, country, projects});
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
    if (!partnerType.equals("All")) {
      fileName.append("ProjectPartnersSummary-");
    } else {
      fileName.append("ProjectLeadingInstitutionsSummary-");
    }
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

  private TypedTableModel getInstitutionTypesTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"count_ins", "name_percentage"}, new Class[] {Integer.class, String.class}, 0);
    Map<InstitutionType, Set<Institution>> result = institutionsPerType.entrySet().stream()
      .sorted((s1, s2) -> new Integer(s2.getValue().size()).compareTo(new Integer(s1.getValue().size()))).collect(
        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    int totalInstitutions = projectsPerInstitution.size();
    DecimalFormat percentageFormat = new DecimalFormat("##.##%");

    for (InstitutionType institutionType : result.keySet()) {
      String namePercentage = "";
      Integer countIns = null;
      countIns = institutionsPerType.get(institutionType).size();
      namePercentage = institutionType.getName();
      Double percentajeOfTotal = 0.0;
      if (totalInstitutions > 0) {
        percentajeOfTotal = countIns * 100.0 / totalInstitutions;
      } else {
        percentajeOfTotal = 0.0;
      }

      model.addRow(
        new Object[] {countIns, namePercentage + " - " + percentageFormat.format(round(percentajeOfTotal / 100, 4))});
    }
    return model;
  }

  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"date", "center", "showDescription"},
      new Class[] {String.class, String.class, Boolean.class});
    String center = this.getLoggedCrp().getAcronym();

    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String date = timezone.format(format) + "(GMT" + zone + ")";
    model.addRow(new Object[] {date, center, this.hasSpecificities(APConstants.CRP_REPORTS_DESCRIPTION)});
    return model;
  }

  private TypedTableModel getSummaryTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"ins_name", "acronym", "projects"},
      new Class[] {String.class, String.class, Integer.class}, 0);
    Map<Institution, Set<Project>> result = projectsPerInstitution.entrySet().stream()
      .sorted((i1, i2) -> new Integer(i2.getValue().size()).compareTo(new Integer(i1.getValue().size()))).collect(
        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    int count = 0;
    for (Institution institution : result.keySet()) {
      if (count > 9) {
        break;
      }
      String insName = "", insAcr = "";
      Integer projects = null;
      insName = institution.getName();
      insAcr = institution.getAcronym();
      projects = projectsPerInstitution.get(institution).size();
      model.addRow(new Object[] {insName, insAcr, projects});
      count++;
    }
    return model;
  }


  @Override
  public void prepare() {
    try {
      Map<String, Parameter> parameters = this.getParameters();
      partnerType = StringUtils.trim(parameters.get(APConstants.SUMMARY_PARTNER_TYPE).getMultipleValues()[0]);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.SUMMARY_PARTNER_TYPE
        + " parameter. Parameter will be set as All. Exception: " + e.getMessage());
      partnerType = "All";
    }
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

}