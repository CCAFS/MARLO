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

package org.cgiar.ccafs.marlo.action.annualReport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPartnershipMainAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationPmuManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalMainAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipPmuManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.PartnershipsSynthesis;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationCrp;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationPmu;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipPmu;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.PartnershipValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class PartnershipsAction extends BaseAction {


  private static final long serialVersionUID = 575869881576979848L;

  private static Logger LOG = LoggerFactory.getLogger(PartnershipsAction.class);

  // Managers
  private SectionStatusManager sectionStatusManager;
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager;
  private ReportSynthesisKeyPartnershipExternalManager reportSynthesisKeyPartnershipExternalManager;
  private ReportSynthesisKeyPartnershipExternalMainAreaManager reportSynthesisKeyPartnershipExternalMainAreaManager;
  private ReportSynthesisKeyPartnershipExternalInstitutionManager reportSynthesisKeyPartnershipExternalInstitutionManager;
  private RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager;
  private ReportSynthesisKeyPartnershipCollaborationManager reportSynthesisKeyPartnershipCollaborationManager;
  private ReportSynthesisKeyPartnershipCollaborationCrpManager reportSynthesisKeyPartnershipCollaborationCrpManager;
  private ReportSynthesisKeyPartnershipPmuManager reportSynthesisKeyPartnershipPmuManager;
  private ReportSynthesisKeyPartnershipCollaborationPmuManager reportSynthesisKeyPartnershipCollaborationPmuManager;
  private InstitutionManager institutionManager;
  private FileDBManager fileDBManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private PartnershipValidator validator;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectPartnerPartnership> partnerShipList;

  // Values for AR part C Evidence Information
  private List<Institution> evidencePartners;
  private List<ReportSynthesisKeyPartnershipExternal> externalPartnerships;

  //
  private List<ReportSynthesisKeyPartnershipExternal> flagshipExternalPartnerships;


  private List<ReportSynthesisKeyPartnershipCollaboration> flagshipExternalCollaborations;

  private List<ReportSynthesisExternalPartnershipDTO> flagshipPlannedList;
  private List<RepIndPartnershipMainArea> mainAreasSel;
  private List<Institution> partners;
  private List<ProjectComponentLesson> projectKeyPartnerships;

  private List<PartnershipsSynthesis> projectPartners;
  private List<GlobalUnit> globalUnits;
  private int indexTab;
  private List<String> listOfFlagships;

  @Inject
  public PartnershipsAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    PartnershipValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    PhaseManager phaseManager, ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager,
    ReportSynthesisKeyPartnershipExternalManager reportSynthesisKeyPartnershipExternalManager,
    ReportSynthesisKeyPartnershipExternalMainAreaManager reportSynthesisKeyPartnershipExternalMainAreaManager,
    ReportSynthesisKeyPartnershipExternalInstitutionManager reportSynthesisKeyPartnershipExternalInstitutionManager,
    RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager, InstitutionManager institutionManager,
    FileDBManager fileDBManager,
    ReportSynthesisKeyPartnershipCollaborationManager reportSynthesisKeyPartnershipCollaborationManager,
    ReportSynthesisKeyPartnershipCollaborationCrpManager reportSynthesisKeyPartnershipCollaborationCrpManager,
    ReportSynthesisKeyPartnershipPmuManager reportSynthesisKeyPartnershipPmuManager,
    ReportSynthesisKeyPartnershipCollaborationPmuManager reportSynthesisKeyPartnershipCollaborationPmuManager,
    SectionStatusManager sectionStatusManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.validator = validator;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.reportSynthesisKeyPartnershipManager = reportSynthesisKeyPartnershipManager;
    this.reportSynthesisKeyPartnershipExternalManager = reportSynthesisKeyPartnershipExternalManager;
    this.reportSynthesisKeyPartnershipExternalMainAreaManager = reportSynthesisKeyPartnershipExternalMainAreaManager;
    this.reportSynthesisKeyPartnershipExternalInstitutionManager =
      reportSynthesisKeyPartnershipExternalInstitutionManager;
    this.repIndPartnershipMainAreaManager = repIndPartnershipMainAreaManager;
    this.institutionManager = institutionManager;
    this.fileDBManager = fileDBManager;
    this.reportSynthesisKeyPartnershipCollaborationManager = reportSynthesisKeyPartnershipCollaborationManager;
    this.reportSynthesisKeyPartnershipCollaborationCrpManager = reportSynthesisKeyPartnershipCollaborationCrpManager;
    this.reportSynthesisKeyPartnershipPmuManager = reportSynthesisKeyPartnershipPmuManager;
    this.reportSynthesisKeyPartnershipCollaborationPmuManager = reportSynthesisKeyPartnershipCollaborationPmuManager;
    this.sectionStatusManager = sectionStatusManager;
  }

  public Long firstFlagship() {
    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
  }

  public void flagshipExternalCollaborations(List<LiaisonInstitution> flagshipliaisonInstitutions) {

    flagshipExternalCollaborations = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : flagshipliaisonInstitutions) {


      ReportSynthesis reportSynthesisFP =
        reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisKeyPartnership() != null) {
          if (reportSynthesisFP.getReportSynthesisKeyPartnership()
            .getReportSynthesisKeyPartnershipCollaborations() != null
            && !reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations()
              .isEmpty()) {


            List<ReportSynthesisKeyPartnershipCollaboration> collaborations = new ArrayList<>(
              reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations()
                .stream().filter(c -> c.isActive()).collect(Collectors.toList()));

            for (ReportSynthesisKeyPartnershipCollaboration collaboration : collaborations) {

              if (collaboration.getReportSynthesisKeyPartnershipCollaborationCrps() != null
                && !collaboration.getReportSynthesisKeyPartnershipCollaborationCrps().isEmpty()) {
                collaboration.setCrps(new ArrayList<>(collaboration.getReportSynthesisKeyPartnershipCollaborationCrps()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              flagshipExternalCollaborations.add(collaboration);
            }
          }
        }
      }

    }
  }

  public void flagshipExternalPartnerships(List<LiaisonInstitution> flagshipliaisonInstitutions) {

    flagshipExternalPartnerships = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : flagshipliaisonInstitutions) {


      ReportSynthesis reportSynthesisFP =
        reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisKeyPartnership() != null) {
          if (reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals() != null
            && !reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals()
              .isEmpty()) {


            List<ReportSynthesisKeyPartnershipExternal> externals = new ArrayList<>(
              reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));

            for (ReportSynthesisKeyPartnershipExternal external : externals) {

              if (external.getReportSynthesisKeyPartnershipExternalInstitutions() != null
                && !external.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {
                external.setInstitutions(new ArrayList<>(external.getReportSynthesisKeyPartnershipExternalInstitutions()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              if (external.getReportSynthesisKeyPartnershipExternalMainAreas() != null
                && !external.getReportSynthesisKeyPartnershipExternalMainAreas().isEmpty()) {
                external.setMainAreas(new ArrayList<>(external.getReportSynthesisKeyPartnershipExternalMainAreas()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              flagshipExternalPartnerships.add(external);

            }


          }
        }
      }

    }


  }

  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    config.getAutoSaveFolder();
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public GlobalUnitManager getCrpManager() {
    return crpManager;
  }

  public List<Institution> getEvidencePartners() {
    return evidencePartners;
  }

  public List<ReportSynthesisKeyPartnershipExternal> getExternalPartnerships() {
    return externalPartnerships;
  }

  public List<ReportSynthesisKeyPartnershipCollaboration> getFlagshipExternalCollaborations() {
    return flagshipExternalCollaborations;
  }

  public List<ReportSynthesisKeyPartnershipExternal> getFlagshipExternalPartnerships() {
    return flagshipExternalPartnerships;
  }


  public List<ReportSynthesisExternalPartnershipDTO> getFlagshipPlannedList() {
    return flagshipPlannedList;
  }


  public void getFlagshipsWithMissingFields() {
    String flagshipsIncomplete = "";
    listOfFlagships = new ArrayList<>();
    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      "Reporting", this.getActualPhase().getYear(), false, "externalPartnerships");

    if (sectionStatus != null && sectionStatus.getMissingFields() != null && !sectionStatus.getMissingFields().isEmpty()
      && sectionStatus.getMissingFields().length() != 0 && sectionStatus.getSynthesisFlagships() != null
      && !sectionStatus.getSynthesisFlagships().isEmpty()
      && sectionStatus.getMissingFields().contains("synthesis.AR2019Table8/9")) {
      flagshipsIncomplete = sectionStatus.getSynthesisFlagships();
    }


    if (flagshipsIncomplete != null && !flagshipsIncomplete.isEmpty()) {
      String textToSeparate = flagshipsIncomplete;
      String separator = ";";
      String[] arrayText = textToSeparate.split(separator);
      for (String element : arrayText) {
        listOfFlagships.add(element);
      }
    }

    /*
     * List<String> arraylist = new ArrayList<>();
     * String textToSeparate = "Go,PHP,JavaScript,Python";
     * String separator = ";";
     * String[] arrayText = textToSeparate.split(separator);
     * for (String element : arrayText) {
     * arraylist.add(element);
     * }
     */

  }


  public List<GlobalUnit> getGlobalUnits() {
    return globalUnits;
  }


  public int getIndexTab() {
    return indexTab;
  }


  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }


  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }


  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }


  public List<String> getListOfFlagships() {
    return listOfFlagships;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<RepIndPartnershipMainArea> getMainAreasSel() {
    return mainAreasSel;
  }

  private String getParnetshipSourceFolder() {
    return APConstants.PARTNERSHIP_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(File.separator).concat(this.getCrpSession() + "_")
      .concat(ReportSynthesis2018SectionStatusEnum.EXTERNAL_PARTNERSHIPS.getStatus()).concat(File.separator);
  }

  public List<Institution> getPartners() {
    return partners;
  }


  public List<ProjectPartnerPartnership> getPartnerShipList() {
    return partnerShipList;
  }


  public List<ProjectComponentLesson> getProjectKeyPartnerships() {
    return projectKeyPartnerships;
  }


  public List<PartnershipsSynthesis> getProjectPartners() {
    return projectPartners;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public Long getSynthesisID() {
    return synthesisID;
  }


  public String getTransaction() {
    return transaction;
  }

  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }


  @Override
  public boolean isPMU() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;

  }

  @Override
  public void prepare() throws Exception {
    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase = this.getActualPhase();

    // If there is a history version being loaded
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ReportSynthesis history = (ReportSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        reportSynthesis = history;
        synthesisID = reportSynthesis.getId();
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      // Get Liaison institution ID Parameter
      try {
        liaisonInstitutionID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
      } catch (NumberFormatException e) {
        User user = userManager.getUser(this.getCurrentUser().getId());
        if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
          List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
            .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
              && lu.getLiaisonInstitution().getCrp().getId().equals(loggedCrp.getId())
              && lu.getLiaisonInstitution().getInstitution() == null)
            .collect(Collectors.toList()));
          if (!liaisonUsers.isEmpty()) {
            boolean isLeader = false;
            for (LiaisonUser liaisonUser : liaisonUsers) {
              LiaisonInstitution institution = liaisonUser.getLiaisonInstitution();
              if (institution.isActive()) {
                if (institution.getCrpProgram() != null) {
                  if (institution.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                } else {
                  if (institution.getAcronym() != null && institution.getAcronym().equals("PMU")) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                }
              }
            }
            if (!isLeader) {
              liaisonInstitutionID = this.firstFlagship();
            }
          } else {
            liaisonInstitutionID = this.firstFlagship();
          }
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      }

      try {
        synthesisID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)));
        reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

        if (!reportSynthesis.getPhase().equals(phase)) {
          reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
          if (reportSynthesis == null) {
            reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
          }
          synthesisID = reportSynthesis.getId();
        }
      } catch (Exception e) {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
        if (reportSynthesis == null) {
          reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
        }
        synthesisID = reportSynthesis.getId();

      }
    }


    if (reportSynthesis != null) {

      ReportSynthesis reportSynthesisDB = reportSynthesisManager.getReportSynthesisById(reportSynthesis.getId());
      synthesisID = reportSynthesisDB.getId();
      liaisonInstitutionID = reportSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
      this.getFlagshipsWithMissingFields();

      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        reportSynthesis = (ReportSynthesis) autoSaveReader.readFromJson(jReader);
        synthesisID = reportSynthesis.getId();
        this.setDraft(true);
      } else {

        this.setDraft(false);
        // Check if relation is null -create it
        if (reportSynthesis.getReportSynthesisKeyPartnership() == null) {
          ReportSynthesisKeyPartnership keyPartnership = new ReportSynthesisKeyPartnership();
          // create one to one relation
          reportSynthesis.setReportSynthesisKeyPartnership(keyPartnership);
          keyPartnership.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        if (this.isFlagship()) {

          // Key External Partnership List
          reportSynthesis.getReportSynthesisKeyPartnership().setPartnerships(new ArrayList<>());

          if (reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals() != null
            && !reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals()
              .isEmpty()) {
            for (ReportSynthesisKeyPartnershipExternal keyPartnershipExternal : reportSynthesis
              .getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream()
              .filter(ro -> ro.isActive()).collect(Collectors.toList())) {

              // Setup Main Areas And Institutions
              keyPartnershipExternal.setMainAreas(new ArrayList<>());
              keyPartnershipExternal.setInstitutions(new ArrayList<>());

              if (keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions() != null
                && !keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {

                for (ReportSynthesisKeyPartnershipExternalInstitution institution : keyPartnershipExternal
                  .getReportSynthesisKeyPartnershipExternalInstitutions().stream().filter(ro -> ro.isActive())
                  .collect(Collectors.toList())) {
                  keyPartnershipExternal.getInstitutions().add(institution);
                }

              }


              if (keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas() != null
                && !keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas().isEmpty()) {

                for (ReportSynthesisKeyPartnershipExternalMainArea mainArea : keyPartnershipExternal
                  .getReportSynthesisKeyPartnershipExternalMainAreas().stream().filter(ro -> ro.isActive())
                  .collect(Collectors.toList())) {
                  keyPartnershipExternal.getMainAreas().add(mainArea);
                }

              }

              // Load File
              if (keyPartnershipExternal.getFile() != null) {
                if (keyPartnershipExternal.getFile().getId() != null) {
                  keyPartnershipExternal.setFile(fileDBManager.getFileDBById(keyPartnershipExternal.getFile().getId()));
                }
              }


              reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships().add(keyPartnershipExternal);
            }

            reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships()
              .sort(Comparator.comparing(ReportSynthesisKeyPartnershipExternal::getId));

          }


        } else {

          // Load Pmu External Partnerships
          reportSynthesis.getReportSynthesisKeyPartnership().setSelectedExternalPartnerships(new ArrayList<>());
          if (reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipPmus() != null
            && !reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipPmus().isEmpty()) {
            for (ReportSynthesisKeyPartnershipPmu plannedPmu : reportSynthesis.getReportSynthesisKeyPartnership()
              .getReportSynthesisKeyPartnershipPmus().stream().filter(ro -> ro.isActive())
              .collect(Collectors.toList())) {
              reportSynthesis.getReportSynthesisKeyPartnership().getSelectedExternalPartnerships()
                .add(plannedPmu.getReportSynthesisKeyPartnershipExternal());
            }
          }

          // Load Pmu Collaborations
          reportSynthesis.getReportSynthesisKeyPartnership().setSelectedCollaborations(new ArrayList<>());
          if (reportSynthesis.getReportSynthesisKeyPartnership()
            .getReportSynthesisKeyPartnershipCollaborationPmus() != null
            && !reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborationPmus()
              .isEmpty()) {
            for (ReportSynthesisKeyPartnershipCollaborationPmu plannedPmu : reportSynthesis
              .getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborationPmus().stream()
              .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
              reportSynthesis.getReportSynthesisKeyPartnership().getSelectedCollaborations()
                .add(plannedPmu.getReportSynthesisKeyPartnershipCollaboration());
            }
          }


        }
        // Load CGIAR collaborations
        reportSynthesis.getReportSynthesisKeyPartnership().setCollaborations(new ArrayList<>());

        if (reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations() != null
          && !reportSynthesis.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations()
            .isEmpty()) {

          for (ReportSynthesisKeyPartnershipCollaboration keyPartnershipCollaboration : reportSynthesis
            .getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {

            keyPartnershipCollaboration.setCrps(new ArrayList<>());

            if (keyPartnershipCollaboration.getReportSynthesisKeyPartnershipCollaborationCrps() != null
              && !keyPartnershipCollaboration.getReportSynthesisKeyPartnershipCollaborationCrps().isEmpty()) {

              for (ReportSynthesisKeyPartnershipCollaborationCrp crp : keyPartnershipCollaboration
                .getReportSynthesisKeyPartnershipCollaborationCrps().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                keyPartnershipCollaboration.getCrps().add(crp);
              }
            }


            reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations().add(keyPartnershipCollaboration);
          }

          reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations()
            .sort(Comparator.comparing(ReportSynthesisKeyPartnershipCollaboration::getId));
        }

      }


      if (this.isFlagship()) {
        // Charge Main Areas and Partners Selection List
        mainAreasSel = repIndPartnershipMainAreaManager.findAll();
        partners = institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());
      }

      // load Crps-Platforms
      globalUnits =
        crpManager
          .findAll().stream().filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1
            || gu.getGlobalUnitType().getId() == 3 || gu.getGlobalUnitType().getId() == 4))
          .collect(Collectors.toList());

    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {
      this.flagshipExternalPartnerships(liaisonInstitutions);
      this.flagshipExternalCollaborations(liaisonInstitutions);

      // Load Institutions Partnerships evidences
      externalPartnerships =
        reportSynthesisKeyPartnershipExternalManager.getTable8(liaisonInstitutions, liaisonInstitution, phase);
      evidencePartners = new ArrayList<>();
      if (externalPartnerships != null && !externalPartnerships.isEmpty()) {

        for (ReportSynthesisKeyPartnershipExternal externalPartner : externalPartnerships) {
          if (externalPartner.getReportSynthesisKeyPartnershipExternalInstitutions() != null
            && !externalPartner.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {
            for (ReportSynthesisKeyPartnershipExternalInstitution extInstitutions : externalPartner
              .getReportSynthesisKeyPartnershipExternalInstitutions()) {

              if (extInstitutions.getInstitution() != null) {
                extInstitutions.getInstitution().setLocations(new ArrayList<>());
                if (extInstitutions.getInstitution().getInstitutionsLocations() != null
                  && !extInstitutions.getInstitution().getInstitutionsLocations().isEmpty()) {
                  extInstitutions.getInstitution().getLocations().addAll(extInstitutions.getInstitution()
                    .getInstitutionsLocations().stream().filter(o -> o.isActive()).collect(Collectors.toList()));
                }
              }

              if (evidencePartners.isEmpty()) {
                evidencePartners.add(extInstitutions.getInstitution());
              } else {
                if (!evidencePartners.contains(extInstitutions.getInstitution())) {
                  evidencePartners.add(extInstitutions.getInstitution());
                }
              }
            }
          }
        }
      }

    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_EXTERNAL_PARTNERSHIP_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships() != null) {
        reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships().clear();
      }
      if (reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations() != null) {
        reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations().clear();
      }
      if (reportSynthesis.getReportSynthesisKeyPartnership().getPlannedExternalPartnerships() != null) {
        reportSynthesis.getReportSynthesisKeyPartnership().getPlannedExternalPartnerships().clear();
      }
      if (reportSynthesis.getReportSynthesisKeyPartnership().getPlannedCollaborations() != null) {
        reportSynthesis.getReportSynthesisKeyPartnership().getPlannedCollaborations().clear();
      }
    }


    try {
      indexTab = Integer.parseInt(this.getSession().get("indexTab").toString());
      this.getSession().remove("indexTab");
    } catch (Exception e) {
      indexTab = 1;
    }
  }

  /*
   * Load The Project Key Partnership of each Flagship/Module
   */
  public List<PartnershipsSynthesis> projectPartnerships(boolean isCgiar) {

    List<PartnershipsSynthesis> partnershipsSynthesis = new ArrayList<>();

    Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());

    if (projectFocusManager.findAll() != null) {

      try {

        List<ProjectFocus> projectFocus = new ArrayList<>();
        if (this.isPMU()) {
          // All project focus for PMU
          projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
            .filter(pf -> pf.isActive() && pf.getPhase() != null && pf.getPhase().getId().equals(phase.getId()))
            .collect(Collectors.toList()));
        } else {
          projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
            .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
              && pf.getPhase() != null && pf.getPhase().getId().equals(phase.getId()))
            .collect(Collectors.toList()));
        }

        // Get project list (removing repeated records)
        Set<Project> projects = new HashSet<Project>();

        for (ProjectFocus focus : projectFocus) {
          Project project = projectManager.getProjectById(focus.getProject().getId());
          projects.add(project);
        }

        for (Project project : projects) {
          if (project.getProjectPartners() != null) {

            PartnershipsSynthesis partnershipsSynt = new PartnershipsSynthesis();
            partnershipsSynt.setProject(project);
            partnershipsSynt.setPartners(new ArrayList<>());
            List<ProjectPartner> projectPartnersList = project.getProjectPartners().stream()
              .filter(co -> co.isActive() && co.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());

            if (projectPartnersList != null && !projectPartnersList.isEmpty()) {
              if (isCgiar) {
                partnershipsSynt.getPartners().addAll(
                  projectPartnersList.stream().filter(c -> c.getInstitution().getInstitutionType().getId().equals(3L))
                    .collect(Collectors.toList()));
              } else {
                partnershipsSynt.getPartners()
                  .addAll(projectPartnersList.stream()
                    .filter(c -> !c.getInstitution().getInstitutionType().getId().equals(3L))
                    .collect(Collectors.toList()));
              }


            }
            partnershipsSynthesis.add(partnershipsSynt);

          }
        }

      } catch (Exception e) {
        e.printStackTrace();
        LOG.error("Error getting partnerships list: " + e.getMessage());
      }


    }
    if (partnershipsSynthesis != null && !partnershipsSynthesis.isEmpty()) {
      partnershipsSynthesis = partnershipsSynthesis.stream()
        .sorted((p1, p2) -> p1.getProject().getId().compareTo(p2.getProject().getId())).collect(Collectors.toList());
    }
    return partnershipsSynthesis;
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      this.getSession().put("indexTab", indexTab);

      ReportSynthesisKeyPartnership keyPartnershipDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisKeyPartnership();

      if (this.isFlagship()) {
        this.saveKeyExternalPartnership(keyPartnershipDB);
      }

      this.saveKeyExternalCollaboration(keyPartnershipDB);

      if (this.isPMU()) {

        keyPartnershipDB.setSummary(reportSynthesis.getReportSynthesisKeyPartnership().getSummary());
        keyPartnershipDB.setSummaryCgiar(reportSynthesis.getReportSynthesisKeyPartnership().getSummaryCgiar());

        if (reportSynthesis.getReportSynthesisKeyPartnership().getPlannedExternalPartnerships() == null) {
          reportSynthesis.getReportSynthesisKeyPartnership().setPlannedExternalPartnerships(new ArrayList<>());
        }

        if (reportSynthesis.getReportSynthesisKeyPartnership().getPlannedCollaborations() == null) {
          reportSynthesis.getReportSynthesisKeyPartnership().setPlannedCollaborations(new ArrayList<>());
        }

        this.saveExternalPmu(keyPartnershipDB);
        this.saveCollaborationPmu(keyPartnershipDB);
      }


      keyPartnershipDB = reportSynthesisKeyPartnershipManager.saveReportSynthesisKeyPartnership(keyPartnershipDB);


      List<String> relationsName = new ArrayList<>();
      reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

      /**
       * The following is required because we need to update something on the @ReportSynthesis if we want a row created
       * in the auditlog table.
       */
      this.setModificationJustification(reportSynthesis);

      reportSynthesisManager.save(reportSynthesis, this.getActionName(), relationsName, this.getActualPhase());


      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      this.getActionMessages();
      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
        for (String key : keys) {
          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }

      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }


      return SUCCESS;
    } else {


      return NOT_AUTHORIZED;
    }
  }

  public void saveCollaborationPmu(ReportSynthesisKeyPartnership partnershipDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> externalIds = new ArrayList<>();

    for (ReportSynthesisKeyPartnershipCollaboration ext : flagshipExternalCollaborations) {
      externalIds.add(ext.getId());
    }

    if (reportSynthesis.getReportSynthesisKeyPartnership().getPlannedCollaborationsValue() != null
      && reportSynthesis.getReportSynthesisKeyPartnership().getPlannedCollaborationsValue().length() > 0) {
      List<Long> exList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisKeyPartnership().getPlannedCollaborationsValue().trim()
        .split(",")) {
        exList.add(Long.parseLong(string.trim()));
      }


      for (Long externalId : externalIds) {
        int index = exList.indexOf(externalId);
        if (index < 0) {
          selectedPs.add(externalId);
        }


      }

      for (ReportSynthesisKeyPartnershipCollaborationPmu reportExternal : partnershipDB
        .getReportSynthesisKeyPartnershipCollaborationPmus().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(reportExternal.getReportSynthesisKeyPartnershipCollaboration().getId())) {
          reportSynthesisKeyPartnershipCollaborationPmuManager
            .deleteReportSynthesisKeyPartnershipCollaborationPmu(reportExternal.getId());
        }
      }

      for (Long externalId : selectedPs) {
        ReportSynthesisKeyPartnershipCollaboration collaboration = reportSynthesisKeyPartnershipCollaborationManager
          .getReportSynthesisKeyPartnershipCollaborationById(externalId);

        ReportSynthesisKeyPartnershipCollaborationPmu collaborationNew =
          new ReportSynthesisKeyPartnershipCollaborationPmu();

        collaborationNew.setReportSynthesisKeyPartnershipCollaboration(collaboration);
        collaborationNew.setReportSynthesisKeyPartnership(partnershipDB);

        List<ReportSynthesisKeyPartnershipCollaborationPmu> externalPmus =
          partnershipDB.getReportSynthesisKeyPartnershipCollaborationPmus().stream().filter(rio -> rio.isActive())
            .collect(Collectors.toList());


        if (!externalPmus.contains(collaborationNew)) {
          collaborationNew = reportSynthesisKeyPartnershipCollaborationPmuManager
            .saveReportSynthesisKeyPartnershipCollaborationPmu(collaborationNew);
        }
      }
    } else {

      for (Long externalId : selectedPs) {
        ReportSynthesisKeyPartnershipCollaboration collaboration = reportSynthesisKeyPartnershipCollaborationManager
          .getReportSynthesisKeyPartnershipCollaborationById(externalId);

        ReportSynthesisKeyPartnershipCollaborationPmu collaborationNew =
          new ReportSynthesisKeyPartnershipCollaborationPmu();

        collaborationNew.setReportSynthesisKeyPartnershipCollaboration(collaboration);
        collaborationNew.setReportSynthesisKeyPartnership(partnershipDB);

        List<ReportSynthesisKeyPartnershipCollaborationPmu> externalPmus =
          partnershipDB.getReportSynthesisKeyPartnershipCollaborationPmus().stream().filter(rio -> rio.isActive())
            .collect(Collectors.toList());


        if (!externalPmus.contains(collaborationNew)) {
          collaborationNew = reportSynthesisKeyPartnershipCollaborationPmuManager
            .saveReportSynthesisKeyPartnershipCollaborationPmu(collaborationNew);
        }
      }

    }
  }

  public void saveExternalPmu(ReportSynthesisKeyPartnership partnershipDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> externalIds = new ArrayList<>();

    for (ReportSynthesisKeyPartnershipExternal ext : flagshipExternalPartnerships) {
      externalIds.add(ext.getId());
    }

    if (reportSynthesis.getReportSynthesisKeyPartnership().getPlannedExternalPartnershipsValue() != null
      && reportSynthesis.getReportSynthesisKeyPartnership().getPlannedExternalPartnershipsValue().length() > 0) {
      List<Long> exList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisKeyPartnership().getPlannedExternalPartnershipsValue()
        .trim().split(",")) {
        exList.add(Long.parseLong(string.trim()));
      }


      for (Long externalId : externalIds) {
        int index = exList.indexOf(externalId);
        if (index < 0) {
          selectedPs.add(externalId);
        }


      }

      for (ReportSynthesisKeyPartnershipPmu reportExternal : partnershipDB.getReportSynthesisKeyPartnershipPmus()
        .stream().filter(rio -> rio.isActive()).collect(Collectors.toList())) {
        if (!selectedPs.contains(reportExternal.getReportSynthesisKeyPartnershipExternal().getId())) {
          reportSynthesisKeyPartnershipPmuManager.deleteReportSynthesisKeyPartnershipPmu(reportExternal.getId());
        }
      }

      for (Long externalId : selectedPs) {
        ReportSynthesisKeyPartnershipExternal keyPartnershipExternal =
          reportSynthesisKeyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(externalId);

        ReportSynthesisKeyPartnershipPmu externalNew = new ReportSynthesisKeyPartnershipPmu();

        externalNew.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
        externalNew.setReportSynthesisKeyPartnership(partnershipDB);

        List<ReportSynthesisKeyPartnershipPmu> externalPmus = partnershipDB.getReportSynthesisKeyPartnershipPmus()
          .stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!externalPmus.contains(externalNew)) {
          externalNew = reportSynthesisKeyPartnershipPmuManager.saveReportSynthesisKeyPartnershipPmu(externalNew);
        }
      }
    } else {

      for (Long externalId : externalIds) {
        ReportSynthesisKeyPartnershipExternal keyPartnershipExternal =
          reportSynthesisKeyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(externalId);

        ReportSynthesisKeyPartnershipPmu externalNew = new ReportSynthesisKeyPartnershipPmu();
        externalNew.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
        externalNew.setReportSynthesisKeyPartnership(partnershipDB);

        List<ReportSynthesisKeyPartnershipPmu> externalPmus = partnershipDB.getReportSynthesisKeyPartnershipPmus()
          .stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!externalPmus.contains(externalNew)) {
          externalNew = reportSynthesisKeyPartnershipPmuManager.saveReportSynthesisKeyPartnershipPmu(externalNew);
        }
      }

    }
  }

  /**
   * Save Key External Collaborations Information
   * 
   * @param crpProgressDB
   */
  public void saveKeyExternalCollaboration(ReportSynthesisKeyPartnership keyPartnershipDB) {


    // Search and deleted form Information
    if (keyPartnershipDB.getReportSynthesisKeyPartnershipCollaborations() != null
      && keyPartnershipDB.getReportSynthesisKeyPartnershipCollaborations().size() > 0) {

      List<ReportSynthesisKeyPartnershipCollaboration> collaborationPrev =
        new ArrayList<>(keyPartnershipDB.getReportSynthesisKeyPartnershipCollaborations().stream()
          .filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisKeyPartnershipCollaboration collaboration : collaborationPrev) {
        if (reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations() != null
          && !reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations().contains(collaboration)) {

          // Delete Crp to the deleteable Collaboration
          if (collaboration.getReportSynthesisKeyPartnershipCollaborationCrps() != null
            && !collaboration.getReportSynthesisKeyPartnershipCollaborationCrps().isEmpty()) {

            for (ReportSynthesisKeyPartnershipCollaborationCrp crp : collaboration
              .getReportSynthesisKeyPartnershipCollaborationCrps().stream().filter(ro -> ro.isActive())
              .collect(Collectors.toList())) {
              reportSynthesisKeyPartnershipCollaborationCrpManager
                .deleteReportSynthesisKeyPartnershipCollaborationCrp(crp.getId());
            }

          }

          // Delete Collaboration
          reportSynthesisKeyPartnershipCollaborationManager
            .deleteReportSynthesisKeyPartnershipCollaboration(collaboration.getId());


        }
      }
    }

    // Save form Information
    if (reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations() != null) {
      for (ReportSynthesisKeyPartnershipCollaboration collaboration : reportSynthesis.getReportSynthesisKeyPartnership()
        .getCollaborations()) {
        if (collaboration.getId() == null) {

          ReportSynthesisKeyPartnershipCollaboration collaborationSave =
            new ReportSynthesisKeyPartnershipCollaboration();

          collaborationSave.setReportSynthesisKeyPartnership(keyPartnershipDB);
          collaborationSave.setDescription(collaboration.getDescription());
          collaborationSave.setValueAdded(collaboration.getValueAdded());


          collaborationSave = reportSynthesisKeyPartnershipCollaborationManager
            .saveReportSynthesisKeyPartnershipCollaboration(collaborationSave);

          this.saveKeyExternalPartnershipCrp(collaborationSave, collaboration);


        } else {

          ReportSynthesisKeyPartnershipCollaboration collaborationSave =
            reportSynthesisKeyPartnershipCollaborationManager
              .getReportSynthesisKeyPartnershipCollaborationById(collaboration.getId());

          this.saveKeyExternalPartnershipCrp(collaborationSave, collaboration);

          collaborationSave.setReportSynthesisKeyPartnership(keyPartnershipDB);
          collaborationSave.setDescription(collaboration.getDescription());
          collaborationSave.setValueAdded(collaboration.getValueAdded());


          collaborationSave = reportSynthesisKeyPartnershipCollaborationManager
            .saveReportSynthesisKeyPartnershipCollaboration(collaborationSave);

        }
      }
    }
  }

  /**
   * Save Key External Partnership Information
   * 
   * @param crpProgressDB
   */
  public void saveKeyExternalPartnership(ReportSynthesisKeyPartnership keyPartnershipDB) {


    // Search and deleted form Information
    if (keyPartnershipDB.getReportSynthesisKeyPartnershipExternals() != null
      && keyPartnershipDB.getReportSynthesisKeyPartnershipExternals().size() > 0) {

      List<ReportSynthesisKeyPartnershipExternal> externalPrev = new ArrayList<>(keyPartnershipDB
        .getReportSynthesisKeyPartnershipExternals().stream().filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisKeyPartnershipExternal external : externalPrev) {
        if (!reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships().contains(external)) {

          // Delete MainAreas And Institution to the deleteable Key External Partnership
          if (external.getReportSynthesisKeyPartnershipExternalInstitutions() != null
            && !external.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {

            for (ReportSynthesisKeyPartnershipExternalInstitution institution : external
              .getReportSynthesisKeyPartnershipExternalInstitutions().stream().filter(ro -> ro.isActive())
              .collect(Collectors.toList())) {
              reportSynthesisKeyPartnershipExternalInstitutionManager
                .deleteReportSynthesisKeyPartnershipExternalInstitution(institution.getId());
            }

          }

          if (external.getReportSynthesisKeyPartnershipExternalMainAreas() != null
            && !external.getReportSynthesisKeyPartnershipExternalMainAreas().isEmpty()) {

            for (ReportSynthesisKeyPartnershipExternalMainArea mainArea : external
              .getReportSynthesisKeyPartnershipExternalMainAreas().stream().filter(ro -> ro.isActive())
              .collect(Collectors.toList())) {
              reportSynthesisKeyPartnershipExternalMainAreaManager
                .deleteReportSynthesisKeyPartnershipExternalMainArea(mainArea.getId());
            }

          }

          List<ReportSynthesisKeyPartnershipPmu> externalPmus =
            reportSynthesisKeyPartnershipPmuManager.findByExternalId(external.getId());

          if (externalPmus != null && !externalPmus.isEmpty()) {

            for (ReportSynthesisKeyPartnershipPmu externalPmu : externalPmus) {
              reportSynthesisKeyPartnershipPmuManager.hardDeleteReportSynthesisKeyPartnershipPmu(externalPmu.getId());
            }

          }


          // Delete Key External partnership
          reportSynthesisKeyPartnershipExternalManager.deleteReportSynthesisKeyPartnershipExternal(external.getId());


        }
      }
    }

    // Save form Information
    if (reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships() != null) {
      for (ReportSynthesisKeyPartnershipExternal external : reportSynthesis.getReportSynthesisKeyPartnership()
        .getPartnerships()) {
        if (external.getId() == null) {

          ReportSynthesisKeyPartnershipExternal externalSave = new ReportSynthesisKeyPartnershipExternal();

          externalSave.setReportSynthesisKeyPartnership(keyPartnershipDB);
          externalSave.setDescription(external.getDescription());

          // Save File
          if (external.getFile() != null) {
            if (external.getFile().getId() == null) {
              externalSave.setFile(null);
            } else {
              externalSave.setFile(external.getFile());
            }
          }

          externalSave =
            reportSynthesisKeyPartnershipExternalManager.saveReportSynthesisKeyPartnershipExternal(externalSave);

          this.saveKeyExternalPartnershipMainAreas(externalSave, external);
          this.saveKeyExternalPartnershipInstitutions(externalSave, external);


        } else {

          ReportSynthesisKeyPartnershipExternal externalSave =
            reportSynthesisKeyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(external.getId());

          this.saveKeyExternalPartnershipMainAreas(externalSave, external);
          this.saveKeyExternalPartnershipInstitutions(externalSave, external);

          externalSave.setDescription(external.getDescription());

          // Save File
          if (external.getFile() != null) {
            if (external.getFile().getId() == null) {
              externalSave.setFile(null);
            } else {
              externalSave.setFile(external.getFile());
            }
          }


          externalSave =
            reportSynthesisKeyPartnershipExternalManager.saveReportSynthesisKeyPartnershipExternal(externalSave);

        }
      }
    }
  }

  /**
   * Save Key External Partnership Crps Information
   */
  public void saveKeyExternalPartnershipCrp(ReportSynthesisKeyPartnershipCollaboration collaborationDB,
    ReportSynthesisKeyPartnershipCollaboration collaboration) {


    // Search and deleted form Information
    if (collaborationDB.getReportSynthesisKeyPartnershipCollaborationCrps() != null
      && collaborationDB.getReportSynthesisKeyPartnershipCollaborationCrps().size() > 0) {

      List<ReportSynthesisKeyPartnershipCollaborationCrp> crpPrev =
        new ArrayList<>(collaborationDB.getReportSynthesisKeyPartnershipCollaborationCrps().stream()
          .filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisKeyPartnershipCollaborationCrp crp : crpPrev) {
        if (!collaboration.getCrps().contains(crp)) {
          reportSynthesisKeyPartnershipCollaborationCrpManager
            .deleteReportSynthesisKeyPartnershipCollaborationCrp(crp.getId());
        }
      }
    }

    // Save form Information
    if (collaboration.getCrps() != null) {
      for (ReportSynthesisKeyPartnershipCollaborationCrp crp : collaboration.getCrps()) {
        if (crp.getId() == null) {

          ReportSynthesisKeyPartnershipCollaborationCrp crpSave = new ReportSynthesisKeyPartnershipCollaborationCrp();

          crpSave.setReportSynthesisKeyPartnershipCollaboration(collaborationDB);

          GlobalUnit globalUnit = crpManager.getGlobalUnitById(crp.getGlobalUnit().getId());

          crpSave.setGlobalUnit(globalUnit);


          reportSynthesisKeyPartnershipCollaborationCrpManager
            .saveReportSynthesisKeyPartnershipCollaborationCrp(crpSave);


        }
      }
    }
  }


  /**
   * Save Key External Partnership Institutions Information
   */
  public void saveKeyExternalPartnershipInstitutions(ReportSynthesisKeyPartnershipExternal externalDB,
    ReportSynthesisKeyPartnershipExternal external) {


    // Search and deleted form Information
    if (externalDB.getReportSynthesisKeyPartnershipExternalInstitutions() != null
      && externalDB.getReportSynthesisKeyPartnershipExternalInstitutions().size() > 0) {

      List<ReportSynthesisKeyPartnershipExternalInstitution> institutionPrev =
        new ArrayList<>(externalDB.getReportSynthesisKeyPartnershipExternalInstitutions().stream()
          .filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisKeyPartnershipExternalInstitution institution : institutionPrev) {
        if (external.getInstitutions() == null || !external.getInstitutions().contains(institution)) {
          reportSynthesisKeyPartnershipExternalInstitutionManager
            .deleteReportSynthesisKeyPartnershipExternalInstitution(institution.getId());
        }
      }
    }

    // Save form Information
    if (external.getInstitutions() != null) {
      for (ReportSynthesisKeyPartnershipExternalInstitution institution : external.getInstitutions()) {
        if (institution.getId() == null) {

          ReportSynthesisKeyPartnershipExternalInstitution institutionSave =
            new ReportSynthesisKeyPartnershipExternalInstitution();

          institutionSave.setReportSynthesisKeyPartnershipExternal(externalDB);

          Institution partner = institutionManager.getInstitutionById(institution.getInstitution().getId());

          institutionSave.setInstitution(partner);

          reportSynthesisKeyPartnershipExternalInstitutionManager
            .saveReportSynthesisKeyPartnershipExternalInstitution(institutionSave);

        }
      }
    }
  }

  /**
   * Save Key External Partnership Main Areas Information
   */
  public void saveKeyExternalPartnershipMainAreas(ReportSynthesisKeyPartnershipExternal externalDB,
    ReportSynthesisKeyPartnershipExternal external) {


    // Search and deleted form Information
    if (externalDB.getReportSynthesisKeyPartnershipExternalMainAreas() != null
      && externalDB.getReportSynthesisKeyPartnershipExternalMainAreas().size() > 0) {

      List<ReportSynthesisKeyPartnershipExternalMainArea> areaPrev =
        new ArrayList<>(externalDB.getReportSynthesisKeyPartnershipExternalMainAreas().stream()
          .filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisKeyPartnershipExternalMainArea area : areaPrev) {
        if (external.getMainAreas() == null || !external.getMainAreas().contains(area)) {
          reportSynthesisKeyPartnershipExternalMainAreaManager
            .deleteReportSynthesisKeyPartnershipExternalMainArea(area.getId());
        }
      }
    }

    // Save form Information
    /*
     * if (external.getMainAreas() != null) {
     * for (ReportSynthesisKeyPartnershipExternalMainArea area : external.getMainAreas()) {
     * if (area.getId() == null) {
     * ReportSynthesisKeyPartnershipExternalMainArea areaSave = new ReportSynthesisKeyPartnershipExternalMainArea();
     * areaSave.setReportSynthesisKeyPartnershipExternal(externalDB);
     * RepIndPartnershipMainArea mainArea =
     * repIndPartnershipMainAreaManager.getRepIndPartnershipMainAreaById(area.getPartnerArea().getId());
     * areaSave.setPartnerArea(mainArea);
     * reportSynthesisKeyPartnershipExternalMainAreaManager
     * .saveReportSynthesisKeyPartnershipExternalMainArea(areaSave);
     * }
     * }
     */

    boolean hasOther = false;
    if (external.getMainAreas() != null) {
      for (ReportSynthesisKeyPartnershipExternalMainArea area : external.getMainAreas()) {
        RepIndPartnershipMainArea mainArea =
          repIndPartnershipMainAreaManager.getRepIndPartnershipMainAreaById(area.getPartnerArea().getId());
        if (area.getId() == null) {
          ReportSynthesisKeyPartnershipExternalMainArea areaSave = new ReportSynthesisKeyPartnershipExternalMainArea();

          areaSave.setReportSynthesisKeyPartnershipExternal(externalDB);
          areaSave.setPartnerArea(mainArea);

          if (mainArea != null && mainArea.getId() != null && mainArea.getId().equals(APConstants.OTHER_MAIN_AREA)) {
            hasOther = true;
          }

          reportSynthesisKeyPartnershipExternalMainAreaManager
            .saveReportSynthesisKeyPartnershipExternalMainArea(areaSave);
        } else {
          if (mainArea != null && mainArea.getId() != null && mainArea.getId().equals(APConstants.OTHER_MAIN_AREA)) {
            hasOther = true;
          }
        }
      }
    }

    if (hasOther) {
      if (external.getOther() != null) {
        externalDB.setOther(external.getOther());
      } else {
        externalDB.setOther(null);
      }
    } else {
      externalDB.setOther(null);
    }
    // }
  }


  public void setCrpManager(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  public void setEvidencePartners(List<Institution> evidencePartners) {
    this.evidencePartners = evidencePartners;
  }

  public void setExternalPartnerships(List<ReportSynthesisKeyPartnershipExternal> externalPartnerships) {
    this.externalPartnerships = externalPartnerships;
  }

  public void
    setFlagshipExternalCollaborations(List<ReportSynthesisKeyPartnershipCollaboration> flagshipExternalCollaborations) {
    this.flagshipExternalCollaborations = flagshipExternalCollaborations;
  }

  public void
    setFlagshipExternalPartnerships(List<ReportSynthesisKeyPartnershipExternal> flagshipExternalPartnerships) {
    this.flagshipExternalPartnerships = flagshipExternalPartnerships;
  }

  public void setFlagshipPlannedList(List<ReportSynthesisExternalPartnershipDTO> flagshipPlannedList) {
    this.flagshipPlannedList = flagshipPlannedList;
  }

  public void setGlobalUnits(List<GlobalUnit> globalUnits) {
    this.globalUnits = globalUnits;
  }

  public void setIndexTab(int indexTab) {
    this.indexTab = indexTab;
  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setListOfFlagships(List<String> listOfFlagships) {
    this.listOfFlagships = listOfFlagships;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMainAreasSel(List<RepIndPartnershipMainArea> mainAreasSel) {
    this.mainAreasSel = mainAreasSel;
  }

  public void setPartners(List<Institution> partners) {
    this.partners = partners;
  }

  public void setPartnerShipList(List<ProjectPartnerPartnership> partnerShipList) {
    this.partnerShipList = partnerShipList;
  }

  public void setProjectKeyPartnerships(List<ProjectComponentLesson> projectKeyPartnerships) {
    this.projectKeyPartnerships = projectKeyPartnerships;
  }

  public void setProjectPartners(List<PartnershipsSynthesis> projectPartners) {
    this.projectPartners = projectPartners;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, reportSynthesis, true);
    }
  }
}