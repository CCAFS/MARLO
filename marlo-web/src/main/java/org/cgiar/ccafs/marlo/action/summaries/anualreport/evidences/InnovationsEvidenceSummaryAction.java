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

package org.cgiar.ccafs.marlo.action.summaries.anualreport.evidences;

import org.cgiar.ccafs.marlo.action.summaries.BaseSummariesAction;
import org.cgiar.ccafs.marlo.action.summaries.Summary;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.anualreport.evidences.ARInnovationsEvidence;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
 * This action prepare and create the Project Innovations evidences to fill the part C of the annual report document.
 * == NOTE : this report works only for annual report 2018 and later phases ==
 * 
 * @author Hermes Jimenez - CIAT/CCAFS
 */
public class InnovationsEvidenceSummaryAction extends BaseSummariesAction implements Summary {


  private static final long serialVersionUID = -2543743013961311798L;
  private static Logger LOG = LoggerFactory.getLogger(InnovationsEvidenceSummaryAction.class);
  // Managers
  private final ResourceManager resourceManager;

  private final ReportSynthesisManager reportSynthesisManager;
  private final ProjectInnovationManager projectInnovationManager;


  // Parameters
  private long startTime;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public InnovationsEvidenceSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ResourceManager resourceManager, ProjectManager projectManager, ReportSynthesisManager reportSynthesisManager,
    ProjectInnovationManager projectInnovationManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.projectInnovationManager = projectInnovationManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * the order of the parameters is the same order for the getInnovationEvidenceReportingTableModel() method
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {

    masterReport.getParameterValues().put("i8nColumnA", this.getText("projectInnovations.table.id"));
    masterReport.getParameterValues().put("i8nColumnB", this.getText("projectInnovations.table.year"));
    masterReport.getParameterValues().put("i8nColumnC", this.getText("projectInnovations.table.title"));
    masterReport.getParameterValues().put("i8nColumnD", this.getText("projectInnovations.narrative.readText"));
    masterReport.getParameterValues().put("i8nColumnE", this.getText("projectInnovations.stage"));
    masterReport.getParameterValues().put("i8nColumnF", this.getText("projectInnovations.innovationType"));
    masterReport.getParameterValues().put("i8nColumnG", this.getText("projectInnovations.contributionOfCrp"));
    masterReport.getParameterValues().put("i8nColumnH", this.getText("projectInnovations.table.nextUsers"));
    masterReport.getParameterValues().put("i8nColumnI", this.getText("projectInnovations.geographicScope"));
    masterReport.getParameterValues().put("i8nColumnJ", this.getText("projectInnovations.region"));
    masterReport.getParameterValues().put("i8nColumnK", this.getText("projectInnovations.countries"));
    masterReport.getParameterValues().put("i8nColumnL", this.getText("projectInnovations.stageDescription"));
    masterReport.getParameterValues().put("i8nColumnM", this.getText("projectInnovations.table.clearLead"));
    masterReport.getParameterValues().put("i8nColumnN", this.getText("projectInnovations.table.leadOrganization"));
    masterReport.getParameterValues().put("i8nColumnO", this.getText("projectInnovations.table.organizations"));
    masterReport.getParameterValues().put("i8nColumnP", this.getText("projectInnovations.table.OICR"));
    masterReport.getParameterValues().put("i8nColumnQ", this.getText("projectInnovations.table.deliverable"));
    masterReport.getParameterValues().put("i8nColumnR", this.getText("projectInnovations.contributing"));
    masterReport.getParameterValues().put("i8nColumnS", this.getText("projectInnovations.table.modification"));
    masterReport.getParameterValues().put("i8nColumnT", this.getText("projectInnovations.table.include"));
    masterReport.getParameterValues().put("i8nHeader", this.getText("projectInnovations.table.header"));

    return masterReport;
  }


  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager.createDirectly(
        this.getClass().getResource("/pentaho/crp/AR-Evidences/InnovationsAR2018.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String date = timezone.format(format) + "(GMT" + zone + ")";
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(this.getSelectedYear()));
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
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating InnovationsEvidenceReporting " + e.getMessage());
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
        model = this.getInnovationEvidenceReportingTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  public byte[] getBytesXLSX() {
    return bytesXLSX;
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
    fileName.append("Innovations-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedPhase().getName());
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
    return fileName.toString();
  }

  public String getHighlightsImagesUrl(String projectId) {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath(projectId).replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath(String projectId) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectId + File.separator
      + "hightlightsImage" + File.separator;
  }

  private TypedTableModel getInnovationEvidenceReportingTableModel() {
    /*
     * Parameters variables to send to the file
     * paramA - innovationID
     * paramB - year
     * paramC - name
     * paramD - description
     * paramE - stageInnovation
     * paramF - innovationType
     * paramG - crpContribution
     * paramH - nextUsers
     * paramI - geographicScope
     * paramJ - regions
     * paramK - countries
     * paramL - descriptionStage
     * paramM - clearLead
     * paramN - leadOrganization
     * paramO - organizations
     * paramP - OICR - study
     * paramQ - deliverable
     * paramR - crps/plts
     * paramS - General last modification date
     * paramT - includeAR
     * innovationURL
     * studyURL
     * NOTE : does not mater the order into the implementation (ex: the paramO will be setup first that the paramA)
     */
    TypedTableModel model = new TypedTableModel(
      new String[] {"paramA", "paramB", "paramC", "paramD", "paramE", "paramF", "paramG", "paramH", "paramI", "paramJ",
        "paramK", "paramL", "paramM", "paramN", "paramO", "paramP", "paramQ", "paramR", "paramS", "paramT",
        "innovationURL", "studyURL"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);

    // Load the Innovations information
    List<ARInnovationsEvidence> innovationsEvidences = this.getInnovationsInfo();

    for (ARInnovationsEvidence innovationEvidences : innovationsEvidences) {
      Long paramA = null, paramB = null;
      String paramC = "", paramD = "", paramE = "", paramF = "", paramG = "", paramH = "", paramI = "", paramJ = "",
        paramK = "", paramL = "", paramM = "", paramN = "", paramO = "", paramP = "", paramQ = "", paramR = "",
        paramS = "", paramT = "", innovationURL = "", studyURL = "";

      // Condition to know if the project innovation have information in the selected phase
      if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase()) != null) {

        // Innovation Id
        paramA = innovationEvidences.getProjectInnovation().getId();
        // Year
        paramB = innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase()).getYear();
        // Title
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getTitle() != null
          && !innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase()).getTitle()
            .isEmpty()) {
          paramC =
            innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase()).getTitle();
        } else {
          paramC = "<Not Defined>";
        }

        // Description
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getNarrative() != null
          && !innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getNarrative().isEmpty()) {
          paramD =
            innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase()).getNarrative();
        } else {
          paramD = "<Not Defined>";
        }

        // Stage of Innovation
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getRepIndStageInnovation() != null) {
          paramE = innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getRepIndStageInnovation().getName();
        } else {
          paramE = "<Not Defined>";
        }

        // Innovation Type
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getRepIndInnovationType() != null) {
          paramF = innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getRepIndInnovationType().getName();
        } else {
          paramF = "<Not Defined>";
        }

        // Next Users
        if (innovationEvidences.getProjectInnovation().getProjectInnovationOrganizations() != null) {
          List<ProjectInnovationOrganization> organizations =
            new ArrayList<>(innovationEvidences.getProjectInnovation().getProjectInnovationOrganizations().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (organizations != null && !organizations.isEmpty()) {
            for (ProjectInnovationOrganization organization : organizations) {
              paramH += "● " + organization.getRepIndOrganizationType().getName() + "\n";
            }
          } else {
            paramH = "<Not Defined>";
          }
        } else {
          paramH = "<Not Defined>";
        }

        // Geographic scopes, regions and countries
        boolean haveRegions = false;
        boolean haveCountries = false;

        if (innovationEvidences.getProjectInnovation().getProjectInnovationGeographicScopes() != null) {

          List<ProjectInnovationGeographicScope> geoScopes =
            new ArrayList<>(innovationEvidences.getProjectInnovation().getProjectInnovationGeographicScopes().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (geoScopes != null && !geoScopes.isEmpty()) {
            // Ask if the selection of the geographic scope needs add a Region or a Country
            for (ProjectInnovationGeographicScope projectInnovationGeographicScope : geoScopes) {

              if (projectInnovationGeographicScope.getRepIndGeographicScope().getId() == 2) {
                haveRegions = true;
              }
              if (projectInnovationGeographicScope.getRepIndGeographicScope().getId() != 1
                && projectInnovationGeographicScope.getRepIndGeographicScope().getId() != 2) {
                haveCountries = true;
              }
              paramI += "● " + projectInnovationGeographicScope.getRepIndGeographicScope().getName() + "\n";
            }
          } else {
            paramI = "<Not Defined>";
          }
        } else {
          paramI = "<Not Defined>";
        }

        if (haveRegions) {
          // Load Regions
          if (innovationEvidences.getProjectInnovation().getProjectInnovationRegions() != null) {
            List<ProjectInnovationRegion> regions =
              new ArrayList<>(innovationEvidences.getProjectInnovation().getProjectInnovationRegions().stream()
                .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
                .collect(Collectors.toList()));
            if (regions != null && !regions.isEmpty()) {
              for (ProjectInnovationRegion region : regions) {
                paramJ += "● " + region.getLocElement().getName() + "\n";
              }
            } else {
              paramJ = "<Not Defined>";
            }
          } else {
            paramJ = "<Not Defined>";
          }
        } else {
          paramJ = "<Not Applicable>";
        }

        if (haveCountries) {
          // Load Countries
          if (innovationEvidences.getProjectInnovation().getProjectInnovationCountries() != null) {
            List<ProjectInnovationCountry> countries =
              new ArrayList<>(innovationEvidences.getProjectInnovation().getProjectInnovationCountries().stream()
                .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
                .collect(Collectors.toList()));
            if (countries != null && !countries.isEmpty()) {
              for (ProjectInnovationCountry country : countries) {
                paramK += "● " + country.getLocElement().getName() + "\n";
              }
            } else {
              paramK = "<Not Defined>";
            }
          } else {
            paramK = "<Not Defined>";
          }
        } else {
          paramK = "<Not Applicable>";
        }

        // Description Stage
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getDescriptionStage() != null
          && !innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getDescriptionStage().isEmpty()) {
          paramL = innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getDescriptionStage();
        } else {
          paramL = "<Not Defined>";
        }


        // Clear lead
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getClearLead() != null) {
          if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getClearLead()) {
            paramM = "No";

          } else {
            paramM = "Yes";
          }
        } else {
          paramM = "Yes";
        }

        // Lead organization
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getLeadOrganization() != null) {
          paramN = innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getLeadOrganization().getComposedName();
        } else {
          paramN = "<Not Defined>";
        }

        // Organizations
        if (innovationEvidences.getProjectInnovation().getProjectInnovationContributingOrganization() != null) {
          List<ProjectInnovationContributingOrganization> organizations =
            new ArrayList<>(innovationEvidences.getProjectInnovation().getProjectInnovationContributingOrganization()
              .stream().filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (organizations != null && !organizations.isEmpty()) {
            for (ProjectInnovationContributingOrganization organization : organizations) {
              paramO += "● " + organization.getInstitution().getComposedName() + "\n";
            }
          } else {
            paramO = "<Not Defined>";
          }
        } else {
          paramO = "<Not Defined>";
        }

        // OICR
        if (innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
          .getProjectExpectedStudy() != null) {

          paramP = innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
            .getProjectExpectedStudy().getComposedName();


          // Generate the innovation - study url of MARLO
          studyURL = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/study.do?expectedID="
            + innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
              .getProjectExpectedStudy().getId().toString()
            + "&phaseID=" + this.getSelectedPhase().getId().toString() + "&projectID="
            + innovationEvidences.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase())
              .getProjectExpectedStudy().getProject().getId().toString();

        } else {
          paramP = "<Not Defined>";
        }

        // Deliverables
        if (innovationEvidences.getProjectInnovation().getProjectInnovationDeliverables() != null) {
          List<ProjectInnovationDeliverable> deliverables =
            new ArrayList<>(innovationEvidences.getProjectInnovation().getProjectInnovationDeliverables().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (deliverables != null && !deliverables.isEmpty()) {
            for (ProjectInnovationDeliverable deliverable : deliverables) {
              paramQ += "● " + "(D" + deliverable.getDeliverable().getId() + ") "
                + deliverable.getDeliverable().getDeliverableInfo(this.getSelectedPhase()).getTitle() + "\n";
            }
          } else {
            paramQ = "<Not Defined>";
          }
        } else {
          paramQ = "<Not Defined>";
        }

        // CRPs / PLTs
        if (innovationEvidences.getProjectInnovation().getProjectInnovationCrps() != null) {
          List<ProjectInnovationCrp> crps =
            new ArrayList<>(innovationEvidences.getProjectInnovation().getProjectInnovationCrps().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (crps != null && !crps.isEmpty()) {
            for (ProjectInnovationCrp crp : crps) {
              paramR += "● " + crp.getGlobalUnit().getComposedName() + "\n";
            }
          } else {
            paramR = "<Not Defined>";
          }
        } else {
          paramR = "<Not Defined>";
        }

        // Is included in the AR
        if (innovationEvidences.getInclude() == null) {
          paramT = "<Not Applicable>";
        } else {
          if (innovationEvidences.getInclude()) {
            paramT = "Yes";
          } else {
            paramT = "No";
          }
        }

        paramS = innovationEvidences.getProjectInnovation().getActiveSince().toLocaleString();

        // Generate the innovation url of MARLO
        innovationURL = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/innovation.do?innovationID="
          + innovationEvidences.getProjectInnovation().getId().toString() + "&phaseID="
          + this.getSelectedPhase().getId().toString() + "&projectID="
          + innovationEvidences.getProjectInnovation().getProject().getId().toString();


      }

      model.addRow(new Object[] {paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK,
        paramL, paramM, paramN, paramO, paramP, paramQ, paramR, paramS, paramT, innovationURL, studyURL});
    }
    return model;
  }


  /**
   * Get the information of all project innovations adding if these project innovations include in the annual report
   * 
   * @return a list of all project innovations with the indicator if this is included in the Annual Report
   */
  public List<ARInnovationsEvidence> getInnovationsInfo() {

    List<ARInnovationsEvidence> innovationsPMU = new ArrayList<ARInnovationsEvidence>();
    LinkedHashSet<ProjectInnovation> AllInnovations = new LinkedHashSet<>();

    LiaisonInstitution liaisonInstitutionPMU = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(o -> o.isActive() && o.getAcronym() != null && o.getAcronym().equals("PMU")).collect(Collectors.toList())
      .get(0);

    List<LiaisonInstitution> liaisonInstitutions = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    ReportSynthesis reportSynthesisPMU =
      reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), liaisonInstitutionPMU.getId());


    if (reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null) {

      AllInnovations = new LinkedHashSet<>(
        projectInnovationManager.getProjectInnovationsList(liaisonInstitutionPMU, this.getSelectedPhase()));


      if (reportSynthesisPMU.getReportSynthesisFlagshipProgress()
        .getReportSynthesisFlagshipProgressInnovations() != null
        && !reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations()
          .isEmpty()) {
        for (ReportSynthesisFlagshipProgressInnovation flagshipProgressInnovation : reportSynthesisPMU
          .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressInnovations().stream()
          .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          ARInnovationsEvidence innovationsEvidence = new ARInnovationsEvidence();
          innovationsEvidence.setProjectInnovation(flagshipProgressInnovation.getProjectInnovation());
          innovationsEvidence.setInclude(false);
          innovationsPMU.add(innovationsEvidence);
          AllInnovations.remove(flagshipProgressInnovation.getProjectInnovation());
        }
      }


      for (ProjectInnovation projectInnovation : AllInnovations) {
        ARInnovationsEvidence innovationsEvidence = new ARInnovationsEvidence();
        innovationsEvidence.setProjectInnovation(projectInnovation);
        innovationsEvidence.setInclude(true);
        innovationsPMU.add(innovationsEvidence);
      }
    }


    /*
     * Update 04/23/2019
     * Add the Project Innovations that no belongs in the AR Synthesis.
     */
    for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {

      List<ProjectInnovation> notSynthesisInnovations =
        projectInnovationManager.getProjectInnovationsNoSynthesisList(liaisonInstitution, this.getSelectedPhase());

      for (ProjectInnovation notSynthesisInnovation : notSynthesisInnovations) {
        ARInnovationsEvidence innovationsEvidence = new ARInnovationsEvidence();
        innovationsEvidence.setProjectInnovation(notSynthesisInnovation);
        innovationsEvidence.setInclude(null);
        innovationsPMU.add(innovationsEvidence);
      }

    }


    // sorted the list by ID
    if (innovationsPMU != null && !innovationsPMU.isEmpty()) {
      innovationsPMU.sort((p1, p2) -> p1.getProjectInnovation().getId().compareTo(p2.getProjectInnovation().getId()));
    }

    return innovationsPMU;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }


  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "baseUrl"},
      new Class[] {String.class, String.class, String.class, String.class});
    model.addRow(new Object[] {center, date, year, this.getBaseUrl()});
    return model;
  }

  @Override
  public void prepare() throws Exception {
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

}
