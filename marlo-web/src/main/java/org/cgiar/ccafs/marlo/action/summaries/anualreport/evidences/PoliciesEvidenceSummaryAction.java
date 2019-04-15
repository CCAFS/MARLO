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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.anualreport.evidences.ARPoliciesEvidence;
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
 * This action prepare and create the Project Policies evidences to fill the part C of the annual report document.
 * == NOTE : this report works only works for annual report 2018 and later phases ==
 * 
 * @author Hermes Jimenez - CIAT/CCAFS
 */
public class PoliciesEvidenceSummaryAction extends BaseSummariesAction implements Summary {


  private static final long serialVersionUID = -2543743013961311798L;
  private static Logger LOG = LoggerFactory.getLogger(PoliciesEvidenceSummaryAction.class);
  // Managers
  private final ResourceManager resourceManager;

  private final ReportSynthesisManager reportSynthesisManager;
  private final ProjectPolicyManager projectPolicyManager;


  // Parameters
  private long startTime;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public PoliciesEvidenceSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ResourceManager resourceManager, ProjectManager projectManager, ReportSynthesisManager reportSynthesisManager,
    ProjectPolicyManager projectPolicyManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.projectPolicyManager = projectPolicyManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * the order of the parameters is the same order for the getPolicyEvidenceReportingTableModel() method
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {

    masterReport.getParameterValues().put("i8nColumn1", this.getText("policy.id.table"));
    masterReport.getParameterValues().put("i8nColumn2", this.getText("policy.year"));
    masterReport.getParameterValues().put("i8nColumn3", this.getText("policy.title"));
    masterReport.getParameterValues().put("i8nColumn4", this.getText("policy.policyType"));
    masterReport.getParameterValues().put("i8nColumn5", this.getText("policy.amount"));
    masterReport.getParameterValues().put("i8nColumn6", this.getText("policy.organizationType"));
    masterReport.getParameterValues().put("i8nColumn7", this.getText("policy.maturityLevel"));
    masterReport.getParameterValues().put("i8nColumn8", this.getText("policy.policyOwners"));
    masterReport.getParameterValues().put("i8nColumn9", this.getText("policy.otherOwner.readMode"));
    masterReport.getParameterValues().put("i8nColumn10", this.getText("policy.evidence.table"));
    masterReport.getParameterValues().put("i8nColumn11", this.getText("policy.narrative"));
    masterReport.getParameterValues().put("i8nColumn12", this.getText("policy.innovations.table"));
    masterReport.getParameterValues().put("i8nColumn13", this.getText("policy.contributingCrpsPtfs"));
    masterReport.getParameterValues().put("i8nColumn14", this.getText("policy.subidos.table"));
    masterReport.getParameterValues().put("i8nColumn15", this.getText("policy.gender.table"));
    masterReport.getParameterValues().put("i8nColumn16", this.getText("policy.youth.table"));
    masterReport.getParameterValues().put("i8nColumn17", this.getText("policy.capdev.table"));
    masterReport.getParameterValues().put("i8nColumn18", this.getText("policy.climatechange.table"));
    masterReport.getParameterValues().put("i8nColumn19", this.getText("policy.geographicScope"));
    masterReport.getParameterValues().put("i8nColumn20", this.getText("policy.regions"));
    masterReport.getParameterValues().put("i8nColumn21", this.getText("policy.countries"));
    masterReport.getParameterValues().put("i8nColumn22", this.getText("policy.include.table"));
    masterReport.getParameterValues().put("i8nHeader", this.getText("policy.header.table"));

    return masterReport;
  }


  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/PoliciesAR2018.prpt"), MasterReport.class);
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
      LOG.error("Error generating PoliciesEvidenceReporting " + e.getMessage());
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
        model = this.getPolicyEvidenceReportingTableModel();
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
    fileName.append("PoliciesEvidence-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
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


  /**
   * Get the information of all project policies adding if these project polices include in the annual report
   * 
   * @return a list of all project policies with the indicator if this is included in the Annual Report
   */
  public List<ARPoliciesEvidence> getPoliciesInfo() {

    List<ARPoliciesEvidence> policiesPMU = new ArrayList<ARPoliciesEvidence>();
    LinkedHashSet<ProjectPolicy> AllPolicies = new LinkedHashSet<>();

    LiaisonInstitution liaisonInstitutionPMU = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(o -> o.isActive() && o.getAcronym().equals("PMU")).collect(Collectors.toList()).get(0);

    ReportSynthesis reportSynthesisPMU =
      reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), liaisonInstitutionPMU.getId());

    if (reportSynthesisPMU.getReportSynthesisFlagshipProgress() != null) {

      AllPolicies = new LinkedHashSet<>(
        projectPolicyManager.getProjectPoliciesList(liaisonInstitutionPMU, this.getSelectedPhase()));


      if (reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies() != null
        && !reportSynthesisPMU.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies()
          .isEmpty()) {
        for (ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicy : reportSynthesisPMU
          .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressPolicies().stream()
          .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          ARPoliciesEvidence policiesEvidence = new ARPoliciesEvidence();
          policiesEvidence.setProjectPolicy(flagshipProgressPolicy.getProjectPolicy());
          policiesEvidence.setInclude(false);
          policiesPMU.add(policiesEvidence);
          AllPolicies.remove(flagshipProgressPolicy.getProjectPolicy());
        }
      }


      for (ProjectPolicy projectPolicy : AllPolicies) {
        ARPoliciesEvidence policiesEvidence = new ARPoliciesEvidence();
        policiesEvidence.setProjectPolicy(projectPolicy);
        policiesEvidence.setInclude(true);
        policiesPMU.add(policiesEvidence);
      }
    }

    // sorted the list by ID
    if (policiesPMU != null && !policiesPMU.isEmpty()) {
      policiesPMU.sort((p1, p2) -> p1.getProjectPolicy().getId().compareTo(p2.getProjectPolicy().getId()));
    }

    return policiesPMU;
  }

  private TypedTableModel getPolicyEvidenceReportingTableModel() {

    /*
     * Parameters variables to send to the file
     * param1 - policyID
     * param2 - year
     * param3 - title
     * param4 - investmentType
     * param5 - amount
     * param6 - organizationType
     * param7 - levelMatuirity
     * param8 - policyType
     * param9 - other
     * param10 - evidences
     * param11 - narrative
     * param12 - innovations
     * param13 - crpPtfs
     * param14 - subIdos
     * param15 - gender
     * param16 - youth
     * param17 - capDev
     * param18 - climateChange
     * param19 - geographicScope
     * param20 - regions
     * param21 - countries
     * param22 - includeAR
     * policyURL
     * NOTE : does not mater the order into the implementation (ex: the param22 will be setup first that the param1)
     */
    TypedTableModel model = new TypedTableModel(
      new String[] {"param1", "param2", "param3", "param4", "param5", "param6", "param7", "param8", "param9", "param10",
        "param11", "param12", "param13", "param14", "param15", "param16", "param17", "param18", "param19", "param20",
        "param21", "param22", "policyURL"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);

    // Load the policies information
    List<ARPoliciesEvidence> policyEvidences = this.getPoliciesInfo();

    for (ARPoliciesEvidence policyEvidence : policyEvidences) {

      String param1 = null, param2 = null, param3 = null, param4 = null, param5 = null, param6 = null, param7 = null,
        param8 = null, param9 = null, param10 = null, param11 = null, param12 = null, param13 = null, param14 = null,
        param15 = null, param16 = null, param17 = null, param18 = null, param19 = null, param20 = null, param21 = null,
        param22 = null, policyURL = null;

      // Condition to know if the project policy have information in the selected phase
      if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()) != null) {

        // Policy Id
        param1 = policyEvidence.getProjectPolicy().getId().toString();

        // Year
        param2 = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getYear().toString();

        // Title
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getTitle() != null
          && !policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getTitle().isEmpty()) {
          param3 = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getTitle();
        } else {
          param3 = "&lt;Not Defined&gt;";
        }

        // Policy / Investment Type and amount (If Investment type Id = 3)
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
          .getRepIndPolicyInvestimentType() != null) {
          param4 = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
            .getRepIndPolicyInvestimentType().getName();
          // If Investment type Id = 3, check the amount value
          if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
            .getRepIndPolicyInvestimentType().getId() == 3) {
            // amount
            if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getAmount() != null
              && policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getAmount() != 0) {
              param5 =
                policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getAmount().toString();
            } else {
              param5 = "&lt;Not Defined&gt;";
            }
          }
        } else {
          param4 = "&lt;Not Defined&gt;";
          param5 = "&lt;Not Defined&gt;";
        }

        // Organization type
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
          .getRepIndOrganizationType() != null) {
          param6 = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
            .getRepIndOrganizationType().getName();
        } else {
          param6 = "&lt;Not Defined&gt;";
        }

        // Level of Maturity
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
          .getRepIndStageProcess() != null) {
          param7 = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
            .getRepIndStageProcess().getName();
        } else {
          param7 = "&lt;Not Defined&gt;";
        }

        // Whose policy is and Other (If policy type == 4)
        if (policyEvidence.getProjectPolicy().getProjectPolicyOwners() != null) {
          List<ProjectPolicyOwner> owners = new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyOwners()
            .stream().filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList()));
          if (owners != null && !owners.isEmpty()) {
            boolean bOther = false;
            for (ProjectPolicyOwner owner : owners) {
              param8 += "● " + owner.getRepIndPolicyType().getName() + "<br>";
              // Check if has Other value
              if (owner.getRepIndPolicyType().getId() == 4) {
                bOther = true;
              }
            }

            if (bOther) {
              // Other
              if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getOther() != null
                && !policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getOther()
                  .isEmpty()) {
                param9 = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getOther();
              } else {
                param9 = "&lt;Not Defined&gt;";
              }
            } else {
              param9 = "&lt;Not applicable&gt;";
            }

          } else {
            param8 = "&lt;Not Defined&gt;";
            param9 = "&lt;Not Defined&gt;";
          }
        } else {
          param8 = "&lt;Not Defined&gt;";
          param9 = "&lt;Not Defined&gt;";
        }

        // Evidences
        if (policyEvidence.getProjectPolicy().getProjectExpectedStudyPolicies() != null) {
          List<ProjectExpectedStudyPolicy> evidences =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectExpectedStudyPolicies().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (evidences != null && !evidences.isEmpty()) {
            for (ProjectExpectedStudyPolicy evidence : evidences) {
              param10 += "● " + evidence.getProjectExpectedStudy().getComposedName() + "<br>";
            }
          }
        } else {
          param10 = "&lt;Not Defined&gt;";
        }


        // Narrative of evidence
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
          .getNarrativeEvidence() != null
          && !policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getNarrativeEvidence()
            .isEmpty()) {
          param11 =
            policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getNarrativeEvidence();
        } else {
          param11 = "&lt;Not Defined&gt;";
        }


        // Innovations
        if (policyEvidence.getProjectPolicy().getProjectPolicyInnovations() != null) {
          List<ProjectPolicyInnovation> innovations =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyInnovations().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (innovations != null && !innovations.isEmpty()) {
            for (ProjectPolicyInnovation innovation : innovations) {
              param12 += "● " + innovation.getProjectInnovation().getComposedName() + "<br>";
            }
          }
        } else {
          param12 = "&lt;Not Defined&gt;";
        }

        // Crp and Platforms
        if (policyEvidence.getProjectPolicy().getProjectPolicyCrps() != null) {
          List<ProjectPolicyCrp> crps = new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyCrps()
            .stream().filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList()));
          if (crps != null && !crps.isEmpty()) {
            for (ProjectPolicyCrp crp : crps) {
              param13 += "● " + crp.getGlobalUnit().getAcronym() + "<br>";
            }
          }
        } else {
          param13 = "&lt;Not Defined&gt;";
        }

        // Sub-IDOs
        if (policyEvidence.getProjectPolicy().getProjectPolicySubIdos() != null) {
          List<ProjectPolicySubIdo> subIdos =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicySubIdos().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (subIdos != null && !subIdos.isEmpty()) {
            for (ProjectPolicySubIdo subIdo : subIdos) {
              param14 += "● " + subIdo.getSrfSubIdo().getDescription() + "<br>";
            }
          }
        } else {
          param14 = "&lt;Not Defined&gt;";
        }

        // CGIAR Cross-cutting Markers
        if (policyEvidence.getProjectPolicy().getProjectPolicyCrossCuttingMarkers() != null) {

          List<ProjectPolicyCrossCuttingMarker> markers =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyCrossCuttingMarkers().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));

          if (markers != null && !markers.isEmpty()) {
            for (ProjectPolicyCrossCuttingMarker marker : markers) {
              // Gender
              if (marker.getCgiarCrossCuttingMarker().getId() == 1) {
                if (marker.getRepIndGenderYouthFocusLevel() != null) {
                  param15 = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  param15 = "&lt;Not Defined&gt;";
                }
              }
              // Youth
              if (marker.getCgiarCrossCuttingMarker().getId() == 2) {
                if (marker.getRepIndGenderYouthFocusLevel() != null) {
                  param16 = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  param16 = "&lt;Not Defined&gt;";
                }
              }
              // CapDev
              if (marker.getCgiarCrossCuttingMarker().getId() == 3) {
                if (marker.getRepIndGenderYouthFocusLevel() != null) {
                  param17 = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  param17 = "&lt;Not Defined&gt;";
                }
              }
              // Climate Change
              if (marker.getCgiarCrossCuttingMarker().getId() == 4) {
                if (marker.getRepIndGenderYouthFocusLevel() != null) {
                  param18 = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  param18 = "&lt;Not Defined&gt;";
                }
              }
            }
          }

        } else {
          param15 = "&lt;Not Defined&gt;";
          param16 = "&lt;Not Defined&gt;";
          param17 = "&lt;Not Defined&gt;";
          param18 = "&lt;Not Defined&gt;";
        }

        // Geographic scopes, regions and countries
        boolean haveRegions = false;
        boolean haveCountries = false;

        if (policyEvidence.getProjectPolicy().getProjectPolicyGeographicScopes() != null) {

          List<ProjectPolicyGeographicScope> geoScopes =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyGeographicScopes().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (geoScopes != null && !geoScopes.isEmpty()) {
            // Ask if the selection of the geographic scope needs add a Region or a Country
            for (ProjectPolicyGeographicScope projectPolicyGeographicScope : geoScopes) {

              if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() == 2) {
                haveRegions = true;
              }
              if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 1
                && projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 2) {
                haveCountries = true;
              }
              param19 += "● " + projectPolicyGeographicScope.getRepIndGeographicScope().getName() + "<br>";
            }
          }
        } else {
          param19 = "&lt;Not Defined&gt;";
        }

        if (haveRegions) {
          // Load Regions
          if (policyEvidence.getProjectPolicy().getProjectPolicyRegions() != null) {
            List<ProjectPolicyRegion> regions =
              new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyRegions().stream()
                .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
                .collect(Collectors.toList()));
            if (regions != null && !regions.isEmpty()) {
              for (ProjectPolicyRegion region : regions) {
                param20 += "● " + region.getLocElement().getName() + "<br>";
              }
            }
          } else {
            param20 = "&lt;Not Defined&gt;";
          }
        } else {
          param20 = "&lt;Not applicable&gt;";
        }

        if (haveCountries) {
          // Load Countries
          if (policyEvidence.getProjectPolicy().getProjectPolicyCountries() != null) {
            List<ProjectPolicyCountry> countries =
              new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyCountries().stream()
                .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
                .collect(Collectors.toList()));
            if (countries != null && !countries.isEmpty()) {
              for (ProjectPolicyCountry country : countries) {
                param21 += "● " + country.getLocElement().getName() + "<br>";
              }
            }
          } else {
            param21 = "&lt;Not Defined&gt;";
          }
        } else {
          param21 = "&lt;Not applicable&gt;";
        }

        // Is included in the AR
        if (policyEvidence.isInclude()) {
          param22 = "Yes";
        } else {
          param22 = "No";
        }


      }

      model.addRow(
        new Object[] {param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12,
          param13, param14, param15, param16, param17, param18, param19, param20, param21, param22, policyURL});
    }
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
