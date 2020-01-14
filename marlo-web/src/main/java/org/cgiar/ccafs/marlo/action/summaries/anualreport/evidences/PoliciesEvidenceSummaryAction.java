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
import org.cgiar.ccafs.marlo.data.model.ProgramType;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
 * == NOTE : this report works only for annual report 2018 and later phases ==
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

    masterReport.getParameterValues().put("i8nColumnA", this.getText("policy.id.table"));
    masterReport.getParameterValues().put("i8nColumnB", this.getText("policy.year"));
    masterReport.getParameterValues().put("i8nColumnC", this.getText("policy.title"));
    masterReport.getParameterValues().put("i8nColumnD", this.getText("policy.policyType"));
    masterReport.getParameterValues().put("i8nColumnE", this.getText("policy.amount"));
    masterReport.getParameterValues().put("i8nColumnF", this.getText("policy.organizationType"));
    masterReport.getParameterValues().put("i8nColumnG", this.getText("policy.maturityLevel"));
    masterReport.getParameterValues().put("i8nColumnH", this.getText("policy.policyOwners"));
    masterReport.getParameterValues().put("i8nColumnI", this.getText("policy.otherOwner.readMode"));
    masterReport.getParameterValues().put("i8nColumnJ", this.getText("policy.evidence.table"));
    masterReport.getParameterValues().put("i8nColumnK", this.getText("policy.narrative"));
    masterReport.getParameterValues().put("i8nColumnL", this.getText("policy.innovations.table"));
    masterReport.getParameterValues().put("i8nColumnM", this.getText("policy.contributingCrpsPtfs"));
    masterReport.getParameterValues().put("i8nColumnN", this.getText("policy.subidos.table"));
    masterReport.getParameterValues().put("i8nColumnO", this.getText("policy.gender.table"));
    masterReport.getParameterValues().put("i8nColumnP", this.getText("policy.youth.table"));
    masterReport.getParameterValues().put("i8nColumnQ", this.getText("policy.capdev.table"));
    masterReport.getParameterValues().put("i8nColumnR", this.getText("policy.climatechange.table"));
    masterReport.getParameterValues().put("i8nColumnS", this.getText("policy.geographicScope"));
    masterReport.getParameterValues().put("i8nColumnT", this.getText("policy.regions"));
    masterReport.getParameterValues().put("i8nColumnU", this.getText("policy.countries"));
    masterReport.getParameterValues().put("i8nColumnV", this.getText("policy.include.modification"));
    masterReport.getParameterValues().put("i8nColumnW", this.getText("policy.include.table"));
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
      Resource reportResource = resourceManager.createDirectly(
        this.getClass().getResource("/pentaho/crp/AR-Evidences/PoliciesAR2018.prpt"), MasterReport.class);
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
    fileName.append("Policies-");
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

    /*
     * Update 04/23/2019
     * Add the Project Policies that no belongs in the AR Synthesis.
     */
    for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {
      List<ProjectPolicy> notSynthesisPolicies =
        projectPolicyManager.getProjectPoliciesNoSynthesisList(liaisonInstitution, this.getSelectedPhase());
      for (ProjectPolicy notSynthesisPolicy : notSynthesisPolicies) {
        ARPoliciesEvidence policiesEvidence = new ARPoliciesEvidence();
        policiesEvidence.setProjectPolicy(notSynthesisPolicy);
        policiesEvidence.setInclude(null);
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
     * paramA - policyID
     * paramB - year
     * paramC - title
     * paramD - investmentType
     * paramE - amount
     * paramF - organizationType
     * paramG - levelMatuirity
     * paramH - policyType
     * paramI - other
     * paramJ - evidences
     * paramK - narrative
     * paramL - innovations
     * paramM - crpPtfs
     * paramN - subIdos
     * paramO - gender
     * paramP - youth
     * paramQ - capDev
     * paramR - climateChange
     * paramS - geographicScope
     * paramT - regions
     * paramU - countries
     * paramV - General last modification date
     * paramW - includeAR
     * policyURL
     * NOTE : does not mater the order into the implementation (ex: the paramV will be setup first that the paramA)
     */
    TypedTableModel model = new TypedTableModel(
      new String[] {"paramA", "paramB", "paramC", "paramD", "paramE", "paramF", "paramG", "paramH", "paramI", "paramJ",
        "paramK", "paramL", "paramM", "paramN", "paramO", "paramP", "paramQ", "paramR", "paramS", "paramT", "paramU",
        "paramV", "paramW", "policyURL"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);

    // Load the policies information
    List<ARPoliciesEvidence> policyEvidences = this.getPoliciesInfo();

    for (ARPoliciesEvidence policyEvidence : policyEvidences) {
      Long paramA = null, paramB = null;
      String paramC = "", paramD = "", paramE = "", paramF = "", paramG = "", paramH = "", paramI = "", paramJ = "",
        paramK = "", paramL = "", paramM = "", paramN = "", paramO = "", paramP = "", paramQ = "", paramR = "",
        paramS = "", paramT = "", paramU = "", paramV = "", paramW = "", policyURL = "";

      // Condition to know if the project policy have information in the selected phase
      if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()) != null) {

        // Policy Id
        paramA = policyEvidence.getProjectPolicy().getId();
        // Year
        paramB = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getYear();
        // Title
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getTitle() != null
          && !policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getTitle().isEmpty()) {
          paramC = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getTitle();
        } else {
          paramC = "<Not Defined>";
        }
        // Policy / Investment Type and amount (If Investment type Id = 3)
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
          .getRepIndPolicyInvestimentType() != null) {
          paramD = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
            .getRepIndPolicyInvestimentType().getName();
          // If Investment type Id = 3, check the amount value
          if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
            .getRepIndPolicyInvestimentType().getId() == 3) {
            // amount
            if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getAmount() != null
              && policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getAmount() != 0) {

              Locale.setDefault(Locale.US);
              DecimalFormat num = new DecimalFormat("#,###.00");
              paramE =
                num.format(policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getAmount());

            } else {
              paramE = "<Not Defined>";
            }
          } else {
            paramE = "<Not Applicable>";
          }
        } else {
          paramD = "<Not Defined>";
          paramE = "<Not Defined>";
        }

        // Organization type


        // Level of Maturity
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
          .getRepIndStageProcess() != null) {
          paramG = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
            .getRepIndStageProcess().getName();
        } else {
          paramG = "<Not Defined>";
        }

        // Whose policy is and Other (If policy type == 4)
        if (policyEvidence.getProjectPolicy().getProjectPolicyOwners() != null) {
          List<ProjectPolicyOwner> owners = new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyOwners()
            .stream().filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList()));
          if (owners != null && !owners.isEmpty()) {
            boolean bOther = false;
            for (ProjectPolicyOwner owner : owners) {
              paramH += "● " + owner.getRepIndPolicyType().getName() + "\n";
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
                paramI = policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getOther();
              } else {
                paramI = "<Not Defined>";
              }
            } else {
              paramI = "<Not Applicable>";
            }

          } else {
            paramH = "<Not Defined>";
            paramI = "<Not Defined>";
          }
        } else {
          paramH = "<Not Defined>";
          paramI = "<Not Defined>";
        }

        // Evidences
        if (policyEvidence.getProjectPolicy().getProjectExpectedStudyPolicies() != null) {
          List<ProjectExpectedStudyPolicy> evidences =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectExpectedStudyPolicies().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (evidences != null && !evidences.isEmpty()) {
            for (ProjectExpectedStudyPolicy evidence : evidences) {
              if (evidence.getProjectExpectedStudy() != null
                && evidence.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()) != null) {
                paramJ += "● " + evidence.getProjectExpectedStudy().getComposedName() + "\n";
              }
            }
          } else {
            paramJ = "<Not Defined>";
          }
        } else {
          paramJ = "<Not Defined>";
        }


        // Narrative of evidence
        if (policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase())
          .getNarrativeEvidence() != null
          && !policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getNarrativeEvidence()
            .isEmpty()) {
          paramK =
            policyEvidence.getProjectPolicy().getProjectPolicyInfo(this.getSelectedPhase()).getNarrativeEvidence();
        } else {
          paramK = "<Not Defined>";
        }


        // Innovations
        if (policyEvidence.getProjectPolicy().getProjectPolicyInnovations() != null) {
          List<ProjectPolicyInnovation> innovations =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyInnovations().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (innovations != null && !innovations.isEmpty()) {
            for (ProjectPolicyInnovation innovation : innovations) {
              if (innovation.getProjectInnovation() != null
                && innovation.getProjectInnovation().getProjectInnovationInfo(this.getSelectedPhase()) != null) {
                paramL += "● " + innovation.getProjectInnovation().getComposedName() + "\n";
              }
            }
          } else {
            paramL = "<Not Defined>";
          }
        } else {
          paramL = "<Not Defined>";
        }

        // Crp and Platforms
        if (policyEvidence.getProjectPolicy().getProjectPolicyCrps() != null) {
          List<ProjectPolicyCrp> crps = new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicyCrps()
            .stream().filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
            .collect(Collectors.toList()));
          if (crps != null && !crps.isEmpty()) {
            for (ProjectPolicyCrp crp : crps) {
              paramM += "● " + crp.getGlobalUnit().getAcronym() + "\n";
            }
          }
        } else {
          paramM = "<Not Defined>";
        }

        // Sub-IDOs
        if (policyEvidence.getProjectPolicy().getProjectPolicySubIdos() != null) {
          List<ProjectPolicySubIdo> subIdos =
            new ArrayList<>(policyEvidence.getProjectPolicy().getProjectPolicySubIdos().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getSelectedPhase().getId()))
              .collect(Collectors.toList()));
          if (subIdos != null && !subIdos.isEmpty()) {
            for (ProjectPolicySubIdo subIdo : subIdos) {
              paramN += "● " + subIdo.getSrfSubIdo().getDescription() + "\n";
            }
          }
        } else {
          paramN = "<Not Defined>";
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
                  paramO = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  paramO = "<Not Defined>";
                }
              }
              // Youth
              if (marker.getCgiarCrossCuttingMarker().getId() == 2) {
                if (marker.getRepIndGenderYouthFocusLevel() != null) {
                  paramP = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  paramP = "<Not Defined>";
                }
              }
              // CapDev
              if (marker.getCgiarCrossCuttingMarker().getId() == 3) {
                if (marker.getRepIndGenderYouthFocusLevel() != null) {
                  paramQ = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  paramQ = "<Not Defined>";
                }
              }
              // Climate Change
              if (marker.getCgiarCrossCuttingMarker().getId() == 4) {
                if (marker.getRepIndGenderYouthFocusLevel() != null) {
                  paramR = marker.getRepIndGenderYouthFocusLevel().getName();
                } else {
                  paramR = "<Not Defined>";
                }
              }
            }
          }

        } else {
          paramO = "<Not Defined>";
          paramP = "<Not Defined>";
          paramQ = "<Not Defined>";
          paramR = "<Not Defined>";
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
              paramS += "● " + projectPolicyGeographicScope.getRepIndGeographicScope().getName() + "\n";
            }
          }
        } else {
          paramS = "<Not Defined>";
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
                paramT += "● " + region.getLocElement().getName() + "\n";
              }
            }
          } else {
            paramT = "<Not Defined>";
          }
        } else {
          paramT = "<Not Applicable>";
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
                paramU += "● " + country.getLocElement().getName() + "\n";
              }
            }
          } else {
            paramU = "<Not Defined>";
          }
        } else {
          paramU = "<Not Applicable>";
        }

        // Is included in the AR
        if (policyEvidence.getInclude() == null) {
          paramW = "<Not Applicable>";
        } else {
          if (policyEvidence.getInclude()) {
            paramW = "Yes";
          } else {
            paramW = "No";
          }
        }

        paramV = policyEvidence.getProjectPolicy().getActiveSince().toLocaleString();

        // Generate the policy url of MARLO
        policyURL = this.getBaseUrl() + "/projects/" + this.getCrpSession() + "/policy.do?policyID="
          + policyEvidence.getProjectPolicy().getId().toString() + "&phaseID="
          + this.getSelectedPhase().getId().toString();

      }

      model.addRow(new Object[] {paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK,
        paramL, paramM, paramN, paramO, paramP, paramQ, paramR, paramS, paramT, paramU, paramV, paramW, policyURL});
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
