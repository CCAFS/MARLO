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

package org.cgiar.ccafs.marlo.action.annualReport;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndCollaborationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndCollaborationType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.CrossCgiarPartnershipValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrossCgiarPartnershipAction extends BaseAction {


  private static final long serialVersionUID = 6149547440883752671L;


  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;

  private AuditLogManager auditLogManager;


  private CrpProgramManager crpProgramManager;


  private RepIndCollaborationTypeManager repIndCollaborationTypeManager;


  private UserManager userManager;


  private ReportSynthesisCrossCgiarManager reportSynthesisCrossCgiarManager;


  private ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager;


  private CrossCgiarPartnershipValidator validator;


  private PhaseManager phaseManager;


  // Variables
  private String transaction;


  private ReportSynthesis reportSynthesis;


  private Long liaisonInstitutionID;


  private Long synthesisID;


  private LiaisonInstitution liaisonInstitution;


  private GlobalUnit loggedCrp;


  private List<LiaisonInstitution> liaisonInstitutions;


  private List<GlobalUnit> globalUnitList;


  private List<RepIndCollaborationType> collaborationList;
  private Map<Integer, String> statuses;
  private List<ReportSynthesisCrossCgiarCollaboration> flagshipCollaborations;
  private String pmuText;

  @Inject
  public CrossCgiarPartnershipAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, CrpProgramManager crpProgramManager,
    RepIndCollaborationTypeManager repIndCollaborationTypeManager, UserManager userManager,
    ReportSynthesisCrossCgiarManager reportSynthesisCrossCgiarManager,
    ReportSynthesisCrossCgiarCollaborationManager reportSynthesisCrossCgiarCollaborationManager,
    CrossCgiarPartnershipValidator validator, PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.crpProgramManager = crpProgramManager;
    this.repIndCollaborationTypeManager = repIndCollaborationTypeManager;
    this.userManager = userManager;
    this.reportSynthesisCrossCgiarManager = reportSynthesisCrossCgiarManager;
    this.reportSynthesisCrossCgiarCollaborationManager = reportSynthesisCrossCgiarCollaborationManager;
    this.validator = validator;
    this.phaseManager = phaseManager;
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
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_"
      + this.getActualPhase().getDescription() + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<RepIndCollaborationType> getCollaborationList() {
    return collaborationList;
  }

  public List<ReportSynthesisCrossCgiarCollaboration> getFlagshipCollaborations() {
    return flagshipCollaborations;
  }

  public List<GlobalUnit> getGlobalUnitList() {
    return globalUnitList;
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

  public String getPmuText() {
    return pmuText;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public Map<Integer, String> getStatuses() {
    return statuses;
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
                  if (institution.getAcronym().equals("PMU")) {
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
        if (reportSynthesis.getReportSynthesisCrossCgiar() == null) {
          ReportSynthesisCrossCgiar crossCgiar = new ReportSynthesisCrossCgiar();
          // create one to one relation
          reportSynthesis.setReportSynthesisCrossCgiar(crossCgiar);
          crossCgiar.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }

        if (this.isFlagship()) {
          // CGIAR collaborations Information
          if (reportSynthesis.getReportSynthesisCrossCgiar().getReportSynthesisCrossCgiarCollaborations() != null) {
            reportSynthesis.getReportSynthesisCrossCgiar()
              .setCollaborations(new ArrayList<>(
                reportSynthesis.getReportSynthesisCrossCgiar().getReportSynthesisCrossCgiarCollaborations().stream()
                  .filter(st -> st.isActive()).collect(Collectors.toList())));
          }

          LiaisonInstitution pmuInstitution = loggedCrp.getLiaisonInstitutions().stream()
            .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU")).collect(Collectors.toList()).get(0);
          ReportSynthesis reportSynthesisPMU =
            reportSynthesisManager.findSynthesis(phase.getId(), pmuInstitution.getId());
          if (reportSynthesisPMU != null) {
            if (reportSynthesisPMU.getReportSynthesisCrossCgiar() != null) {
              pmuText = reportSynthesisPMU.getReportSynthesisCrossCgiar().getHighlights();

            }
          }
        }
      }
    }

    // Getting The list
    statuses = new HashMap<>();
    List<ProjectStatusEnum> listStatus = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum globalStatusEnum : listStatus) {
      statuses.put(Integer.parseInt(globalStatusEnum.getStatusId()), globalStatusEnum.getStatus());
    }
    collaborationList = new ArrayList<>(repIndCollaborationTypeManager.findAll().stream().collect(Collectors.toList()));
    globalUnitList = crpManager.findAll().stream()
      .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
      .collect(Collectors.toList());

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    if (this.isPMU()) {
      flagshipCollaborations =
        reportSynthesisCrossCgiarCollaborationManager.getFlagshipCollaborations(liaisonInstitutions, phase.getId());
    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_CROSS_CGIAR_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations() != null) {
        reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisCrossCgiar crossCgiarDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisCrossCgiar();
      if (this.isFlagship()) {
        this.saveCollaborations(crossCgiarDB);
      } else {
        crossCgiarDB.setHighlights(reportSynthesis.getReportSynthesisCrossCgiar().getHighlights());
      }

      crossCgiarDB = reportSynthesisCrossCgiarManager.saveReportSynthesisCrossCgiar(crossCgiarDB);


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
   * Save Collaborations Information
   * 
   * @param crossCgiarDB
   */
  public void saveCollaborations(ReportSynthesisCrossCgiar crossCgiarDB) {

    // Search and deleted form Information
    if (crossCgiarDB.getReportSynthesisCrossCgiarCollaborations() != null
      && crossCgiarDB.getReportSynthesisCrossCgiarCollaborations().size() > 0) {

      List<ReportSynthesisCrossCgiarCollaboration> collaborationPrev =
        new ArrayList<>(crossCgiarDB.getReportSynthesisCrossCgiarCollaborations().stream().filter(nu -> nu.isActive())
          .collect(Collectors.toList()));

      for (ReportSynthesisCrossCgiarCollaboration collaboration : collaborationPrev) {
        if (!reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations().contains(collaboration)) {
          reportSynthesisCrossCgiarCollaborationManager
            .deleteReportSynthesisCrossCgiarCollaboration(collaboration.getId());
        }
      }
    }

    // Save form Information
    if (reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations() != null) {
      for (ReportSynthesisCrossCgiarCollaboration collaboration : reportSynthesis.getReportSynthesisCrossCgiar()
        .getCollaborations()) {
        if (collaboration.getId() == null) {
          ReportSynthesisCrossCgiarCollaboration collaborationSave = new ReportSynthesisCrossCgiarCollaboration();

          collaborationSave.setReportSynthesisCrossCgiar(crossCgiarDB);
          if (collaboration.getGlobalUnit() != null && collaboration.getGlobalUnit().getId() != -1) {
            GlobalUnit globalUnit = crpManager.getGlobalUnitById(collaboration.getGlobalUnit().getId());
            collaborationSave.setGlobalUnit(globalUnit);
          }

          if (collaboration.getRepIndCollaborationType() != null
            && collaboration.getRepIndCollaborationType().getId() != -1) {
            RepIndCollaborationType repIndCollaborationType = repIndCollaborationTypeManager
              .getRepIndCollaborationTypeById(collaboration.getRepIndCollaborationType().getId());
            collaborationSave.setRepIndCollaborationType(repIndCollaborationType);
          }

          collaborationSave.setStatus(collaboration.getStatus());
          collaborationSave.setDescription(collaboration.getDescription());
          collaborationSave.setFlagship(collaboration.getFlagship());

          reportSynthesisCrossCgiarCollaborationManager.saveReportSynthesisCrossCgiarCollaboration(collaborationSave);
        } else {

          ReportSynthesisCrossCgiarCollaboration collaborationPrev = reportSynthesisCrossCgiarCollaborationManager
            .getReportSynthesisCrossCgiarCollaborationById(collaboration.getId());


          collaborationPrev.setDescription(collaboration.getDescription());


          collaborationPrev.setFlagship(collaboration.getFlagship());

          if (collaboration.getGlobalUnit() != null && collaboration.getGlobalUnit().getId() != -1) {
            GlobalUnit globalUnit = crpManager.getGlobalUnitById(collaboration.getGlobalUnit().getId());
            collaborationPrev.setGlobalUnit(globalUnit);
          }

          if (collaboration.getRepIndCollaborationType() != null
            && collaboration.getRepIndCollaborationType().getId() != -1) {
            RepIndCollaborationType repIndCollaborationType = repIndCollaborationTypeManager
              .getRepIndCollaborationTypeById(collaboration.getRepIndCollaborationType().getId());
            collaborationPrev.setRepIndCollaborationType(repIndCollaborationType);
          }

          collaborationPrev.setStatus(collaboration.getStatus());


          reportSynthesisCrossCgiarCollaborationManager.saveReportSynthesisCrossCgiarCollaboration(collaborationPrev);

        }
      }
    }


  }

  public void setCollaborationList(List<RepIndCollaborationType> collaborationList) {
    this.collaborationList = collaborationList;
  }

  public void setFlagshipCollaborations(List<ReportSynthesisCrossCgiarCollaboration> flagshipCollaborations) {
    this.flagshipCollaborations = flagshipCollaborations;
  }

  public void setGlobalUnitList(List<GlobalUnit> globalUnitList) {
    this.globalUnitList = globalUnitList;
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

  public void setPmuText(String pmuText) {
    this.pmuText = pmuText;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setStatuses(Map<Integer, String> statuses) {
    this.statuses = statuses;
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
