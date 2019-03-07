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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalMainAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class PartnershipsAction extends BaseAction {


  private static final long serialVersionUID = 575869881576979848L;


  // Managers
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
  private InstitutionManager institutionManager;
  private FileDBManager fileDBManager;

  private PartnershipValidator validator;

  private ProjectFocusManager projectFocusManager;

  private ProjectManager projectManager;

  private PhaseManager phaseManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectPartnerPartnership> partnerShipList;
  private List<ReportSynthesisExternalPartnership> flagshipExternalPartnerships;
  private List<ReportSynthesisExternalPartnershipDTO> flagshipPlannedList;

  private List<RepIndPartnershipMainArea> mainAreas;
  private List<Institution> partners;

  private List<ProjectComponentLesson> projectKeyPartnerships;


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
    FileDBManager fileDBManager) {
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


  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    String fl = config.getAutoSaveFolder();
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public GlobalUnitManager getCrpManager() {
    return crpManager;
  }


  public List<ReportSynthesisExternalPartnership> getFlagshipExternalPartnerships() {
    return flagshipExternalPartnerships;
  }


  public List<ReportSynthesisExternalPartnershipDTO> getFlagshipPlannedList() {
    return flagshipPlannedList;
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

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<RepIndPartnershipMainArea> getMainAreas() {
    return mainAreas;
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
              && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId()
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

      // Fill Flagship Expected Studies
      // TODO
      // if (this.isFlagship()) {
      // this.partnerShipList(phase.getId(), liaisonInstitution);
      // }


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

          }

          // Charge Main Areas and Partners Selection List
          mainAreas = repIndPartnershipMainAreaManager.findAll();
          partners = institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());

          // Load Project Flagship Partnerships
          this.projectPartnerships(phase.getId(), liaisonInstitution);

        }
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {
      // // Table G
      // flagshipPlannedList = reportSynthesisExternalPartnershipManager.getPlannedPartnershipList(liaisonInstitutions,
      // phase.getId(), loggedCrp, this.liaisonInstitution);
      //
      // // Flagship External Partnership Synthesis Progress
      // flagshipExternalPartnerships =
      // reportSynthesisExternalPartnershipManager.getFlagshipCExternalPartnership(liaisonInstitutions, phase.getId());
    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_EXTERNAL_PARTNERSHIP_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisExternalPartnership().getPartnerPartnerships() != null) {
        reportSynthesis.getReportSynthesisExternalPartnership().getPartnerPartnerships().clear();
      }
    }
  }

  /*
   * Load The Project Key Partnership of each Flagship/Module
   */
  public void projectPartnerships(long phaseID, LiaisonInstitution liaisonInstitution) {

    this.projectKeyPartnerships = new ArrayList<>();

    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectFocusManager.findAll() != null) {

      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
          && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {

        Project project = projectManager.getProjectById(focus.getProject().getId());

        if (project.getProjectComponentLessons() != null) {

          List<ProjectComponentLesson> projectKeyPartnershipsList = project.getProjectComponentLessons().stream()
            .filter(co -> co.isActive() && co.getPhase().getId().equals(phase.getId())
              && co.getYear() == this.getCurrentCycleYear()
              && co.getComponentName().equals(ProjectSectionStatusEnum.PARTNERS.getStatus()))
            .collect(Collectors.toList());

          if (projectKeyPartnershipsList != null && !projectKeyPartnershipsList.isEmpty()) {

            for (ProjectComponentLesson projectKeyPartnership : projectKeyPartnershipsList) {
              this.projectKeyPartnerships.add(projectKeyPartnership);
            }

          }
        }
      }
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      ReportSynthesisKeyPartnership keyPartnershipDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisKeyPartnership();

      if (this.isFlagship()) {

        // if (reportSynthesis.getReportSynthesisExternalPartnership().getPartnerPartnerships() == null) {
        // reportSynthesis.getReportSynthesisExternalPartnership().setPartnerPartnerships(new ArrayList<>());
        // }
        //
        // this.saveExternalPartnership(externalPartnershipDB);


      }

      if (this.isPMU()) {
        keyPartnershipDB.setSummary(reportSynthesis.getReportSynthesisKeyPartnership().getSummary());
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

      Collection<String> messages = this.getActionMessages();
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

          reportSynthesisKeyPartnershipExternalManager.saveReportSynthesisKeyPartnershipExternal(externalSave);


        } else {

          // boolean hasChanges = false;
          // ReportSynthesisSrfProgressTarget srfTargetPrev =
          // reportSynthesisSrfProgressTargetManager.getReportSynthesisSrfProgressTargetById(srfTarget.getId());
          //
          // if (!srfTargetPrev.getBirefSummary().equals(srfTarget.getBirefSummary())) {
          // hasChanges = true;
          // srfTargetPrev.setBirefSummary(srfTarget.getBirefSummary());
          // }
          //
          // if (!srfTargetPrev.getAdditionalContribution().equals(srfTarget.getAdditionalContribution())) {
          // hasChanges = true;
          // srfTargetPrev.setAdditionalContribution(srfTarget.getAdditionalContribution());
          // }
          //
          // if (hasChanges) {
          // reportSynthesisSrfProgressTargetManager.saveReportSynthesisSrfProgressTarget(srfTargetPrev);
          // }
        }
      }
    }


  }

  public void setCrpManager(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }


  public void setFlagshipExternalPartnerships(List<ReportSynthesisExternalPartnership> flagshipExternalPartnerships) {
    this.flagshipExternalPartnerships = flagshipExternalPartnerships;
  }

  public void setFlagshipPlannedList(List<ReportSynthesisExternalPartnershipDTO> flagshipPlannedList) {
    this.flagshipPlannedList = flagshipPlannedList;
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

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMainAreas(List<RepIndPartnershipMainArea> mainAreas) {
    this.mainAreas = mainAreas;
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


      if (reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships() != null) {
        for (ReportSynthesisKeyPartnershipExternal external : reportSynthesis.getReportSynthesisKeyPartnership()
          .getPartnerships()) {

          if (external.getFile() != null && external.getFile().getId() == null
            || external.getFile().getId().longValue() == -1) {
            external.setFile(null);
          }
        }
      }


      validator.validate(this, reportSynthesis, true);
    }
  }
}
