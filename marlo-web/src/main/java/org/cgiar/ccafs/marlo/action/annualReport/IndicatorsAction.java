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
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipResearchPhase;
import org.cgiar.ccafs.marlo.data.model.RepIndSynthesisIndicator;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicator;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicatorGeneral;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisInnovationsByStageDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPartnershipsByGeographicScopeDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPartnershipsByPhaseDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisPartnershipsByRepIndOrganizationTypeDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisStudiesByOrganizationTypeDTO;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.IndicatorsValidator;

import java.io.BufferedReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class IndicatorsAction extends BaseAction {

  private static final long serialVersionUID = -8306463804965610803L;

  private static Logger LOG = LoggerFactory.getLogger(IndicatorsAction.class);

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager;
  private ReportSynthesisIndicatorManager reportSynthesisIndicatorManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private RepIndStageInnovationManager repIndStageInnovationManager;
  private ProjectPartnerPartnershipManager projectPartnerPartnershipManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager;
  private DeliverableParticipantManager deliverableParticipantManager;
  private DeliverableInfoManager deliverableInfoManager;
  private DeliverableTypeManager deliverableTypeManager;
  // Variables
  private String transaction;
  private IndicatorsValidator indicatorsValidator;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private ReportSynthesis reportSynthesisPMU;
  private Boolean isInfluence;
  private List<ReportSynthesisStudiesByOrganizationTypeDTO> organizationTypeByStudiesDTOs;
  private List<ReportSynthesisInnovationsByStageDTO> innovationsByStageDTO;
  private List<ProjectExpectedStudy> projectExpectedStudies;
  private List<ProjectInnovationInfo> projectInnovationInfos;
  private List<ProjectPartnerPartnership> projectPartnerPartnerships;
  private List<ReportSynthesisPartnershipsByRepIndOrganizationTypeDTO> partnershipsByRepIndOrganizationTypeDTOs;
  private List<ReportSynthesisPartnershipsByGeographicScopeDTO> partnershipsByGeographicScopeDTO;
  private List<ReportSynthesisPartnershipsByPhaseDTO> partnershipsByPhaseDTO;
  private List<DeliverableParticipant> deliverableParticipants;
  private Double totalParticipants = new Double(0);
  private Double percentageFemales = new Double(0);
  private Double totalParticipantFormalTraining = new Double(0);
  private List<DeliverableInfo> deliverableInfos;
  private Integer totalOpenAccess = 0;
  private Integer totalLimited = 0;
  private Integer totalIsis = 0;
  private Integer totalNoIsis = 0;


  @Inject
  public IndicatorsAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    IndicatorsValidator indicatorsValidator, RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager,
    ReportSynthesisIndicatorManager reportSynthesisIndicatorManager,
    RepIndOrganizationTypeManager repIndOrganizationTypeManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, ProjectInnovationInfoManager projectInnovationInfoManager,
    RepIndStageInnovationManager repIndStageInnovationManager,
    ProjectPartnerPartnershipManager projectPartnerPartnershipManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager,
    RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager,
    DeliverableParticipantManager deliverableParticipantManager, DeliverableInfoManager deliverableInfoManager,
    DeliverableTypeManager deliverableTypeManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.indicatorsValidator = indicatorsValidator;
    this.repIndSynthesisIndicatorManager = repIndSynthesisIndicatorManager;
    this.reportSynthesisIndicatorManager = reportSynthesisIndicatorManager;
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.repIndStageInnovationManager = repIndStageInnovationManager;
    this.projectPartnerPartnershipManager = projectPartnerPartnershipManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.repIndPhaseResearchPartnershipManager = repIndPhaseResearchPartnershipManager;
    this.deliverableParticipantManager = deliverableParticipantManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableTypeManager = deliverableTypeManager;
  }


  @Override
  public String cancel() {
    Path path = this.getAutoSaveFilePath();
    if (path.toFile().exists()) {
      boolean fileDeleted = path.toFile().delete();
    }
    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();
    return SUCCESS;
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
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<DeliverableInfo> getDeliverableInfos() {
    return deliverableInfos;
  }


  public List<DeliverableParticipant> getDeliverableParticipants() {
    return deliverableParticipants;
  }


  public List<ReportSynthesisInnovationsByStageDTO> getInnovationsByStageDTO() {
    return innovationsByStageDTO;
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


  public List<ReportSynthesisStudiesByOrganizationTypeDTO> getOrganizationTypeByStudiesDTOs() {
    return organizationTypeByStudiesDTOs;
  }

  public List<ReportSynthesisPartnershipsByGeographicScopeDTO> getPartnershipsByGeographicScopeDTO() {
    return partnershipsByGeographicScopeDTO;
  }


  public List<ReportSynthesisPartnershipsByPhaseDTO> getPartnershipsByPhaseDTO() {
    return partnershipsByPhaseDTO;
  }


  public List<ReportSynthesisPartnershipsByRepIndOrganizationTypeDTO> getPartnershipsByRepIndOrganizationTypeDTOs() {
    return partnershipsByRepIndOrganizationTypeDTOs;
  }


  public Double getPercentageFemales() {
    return percentageFemales;
  }


  public List<ProjectExpectedStudy> getProjectExpectedStudies() {
    return projectExpectedStudies;
  }


  public List<ProjectInnovationInfo> getProjectInnovationInfos() {
    return projectInnovationInfos;
  }


  public List<ProjectPartnerPartnership> getProjectPartnerPartnerships() {
    return projectPartnerPartnerships;
  }


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public Long getSynthesisID() {
    return synthesisID;
  }


  public Integer getTotalIsis() {
    return totalIsis;
  }


  public Integer getTotalLimited() {
    return totalLimited;
  }


  public Integer getTotalNoIsis() {
    return totalNoIsis;
  }


  public Integer getTotalOpenAccess() {
    return totalOpenAccess;
  }

  public Double getTotalParticipantFormalTraining() {
    return totalParticipantFormalTraining;
  }

  public Double getTotalParticipants() {
    return totalParticipants;
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
  public String next() {
    String result = this.save();
    if (result.equals(BaseAction.SUCCESS)) {
      return BaseAction.NEXT;
    } else {
      return result;
    }
  }


  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase = this.getActualPhase();

    // Verify if is Influence or Control section
    String[] actionParts = this.getActionName().split("/");
    if (actionParts.length > 0) {
      String action = actionParts[1];
      if (action.equals(ReportSynthesisSectionStatusEnum.INFLUENCE.getStatus())) {
        isInfluence = true;
      } else if (action.equals(ReportSynthesisSectionStatusEnum.CONTROL.getStatus())) {
        isInfluence = false;
      }
    }


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
      // reportSynthesisPMU: Used to calculate FLagships values
      LiaisonInstitution pmuInstitution = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.getAcronym() != null && c.getAcronym().equals("PMU"))
        .collect(Collectors.toList()).get(0);
      reportSynthesisPMU = reportSynthesisManager.findSynthesis(phase.getId(), pmuInstitution.getId());

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
        if (reportSynthesis.getReportSynthesisIndicatorGeneral() == null) {
          ReportSynthesisIndicatorGeneral indicatorGeneral = new ReportSynthesisIndicatorGeneral();
          // create one to one relation
          reportSynthesis.setReportSynthesisIndicatorGeneral(indicatorGeneral);
          indicatorGeneral.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }
        if (this.isPMU()) {
          List<ReportSynthesisIndicator> reportSynthesisIndicators = new ArrayList<>();
          if (isInfluence) {
            reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesis,
              APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_INFLUENCE);
          } else {
            reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesis,
              APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL);
          }

          if (reportSynthesisIndicators != null && !reportSynthesisIndicators.isEmpty()) {
            reportSynthesis.getReportSynthesisIndicatorGeneral()
              .setSynthesisIndicators(new ArrayList<>(reportSynthesisIndicators));
          } else {
            reportSynthesis.getReportSynthesisIndicatorGeneral().setSynthesisIndicators(new ArrayList<>());
            List<RepIndSynthesisIndicator> repIndSynthesisIndicator = new ArrayList<>();
            if (isInfluence) {
              repIndSynthesisIndicator = repIndSynthesisIndicatorManager.findAll().stream()
                .filter(i -> i.isMarlo() && i.getType().equals(APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_INFLUENCE))
                .sorted((i1, i2) -> i1.getIndicator().compareTo(i2.getIndicator())).collect(Collectors.toList());
            } else {
              repIndSynthesisIndicator = repIndSynthesisIndicatorManager.findAll().stream()
                .filter(i -> i.isMarlo() && i.getType().equals(APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL))
                .sorted((i1, i2) -> i1.getIndicator().compareTo(i2.getIndicator())).collect(Collectors.toList());
            }

            for (RepIndSynthesisIndicator synthesisIndicator : repIndSynthesisIndicator) {
              ReportSynthesisIndicator reportSynthesisIndicator = new ReportSynthesisIndicator();
              reportSynthesisIndicator.setRepIndSynthesisIndicator(synthesisIndicator);
              reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators()
                .add(reportSynthesisIndicator);
            }

          }
        }
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Informative table to Flagships
    if (this.isFlagship()) {
      if (reportSynthesisPMU != null) {
        if (reportSynthesisPMU.getReportSynthesisIndicatorGeneral() != null) {
          List<ReportSynthesisIndicator> reportSynthesisIndicators = new ArrayList<>();

          if (isInfluence) {
            reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesisPMU,
              APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_INFLUENCE);
          } else {
            reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesisPMU,
              APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL);
          }

          if (reportSynthesisIndicators != null && !reportSynthesisIndicators.isEmpty()) {
            reportSynthesis.getReportSynthesisIndicatorGeneral().setSynthesisIndicators(reportSynthesisIndicators);
          }
        }
      }
    }
    // Informative Tables/Charts
    if (isInfluence) {
      // Chart: Studies by organization type
      organizationTypeByStudiesDTOs = repIndOrganizationTypeManager.getOrganizationTypesByStudies(phase);
      // Table: Outcomes/Impacts involved in policy/investments
      projectExpectedStudies = projectExpectedStudyManager.getStudiesByPhase(phase);
      for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {
        projectExpectedStudy.getProjectExpectedStudyInfo(phase);
      }
    } else {
      // Table: Table D-2: List of CRP Innovations in 2017
      projectInnovationInfos = projectInnovationInfoManager.getProjectInnovationInfoByPhase(phase);
      // Innovations by stage
      innovationsByStageDTO = repIndStageInnovationManager.getInnovationsByStageDTO(phase);

      // Table G: Projects Key Partnerships
      projectPartnerPartnerships = projectPartnerPartnershipManager.getProjectPartnerPartnershipByPhase(phase);
      if (projectPartnerPartnerships != null && !projectPartnerPartnerships.isEmpty()) {
        for (ProjectPartnerPartnership projectPartnerPartnership : projectPartnerPartnerships) {
          List<ProjectPartnerPartnershipResearchPhase> projectPartnerPartnershipResearchPhases =
            projectPartnerPartnership.getProjectPartnerPartnershipResearchPhases().stream().filter(pr -> pr.isActive())
              .sorted((p1, p2) -> p1.getRepIndPhaseResearchPartnership().getName()
                .compareTo(p2.getRepIndPhaseResearchPartnership().getName()))
              .collect(Collectors.toList());
          projectPartnerPartnership.setPartnershipResearchPhases(projectPartnerPartnershipResearchPhases);
        }
      }
      // Chart: Partnerships by Partner type
      partnershipsByRepIndOrganizationTypeDTOs =
        repIndOrganizationTypeManager.getPartnershipsByRepIndOrganizationTypeDTO(projectPartnerPartnerships);
      // Chart: Partnerships by Geographic Scope
      partnershipsByGeographicScopeDTO =
        repIndGeographicScopeManager.getPartnershipsByGeographicScopeDTO(projectPartnerPartnerships);
      // Chart: Partnerships by Phase
      partnershipsByPhaseDTO =
        repIndPhaseResearchPartnershipManager.getPartnershipsByPhaseDTO(projectPartnerPartnerships);

      // Deliverables Participants
      deliverableParticipants = deliverableParticipantManager.getDeliverableParticipantByPhase(phase);
      Double totalFemales = new Double(0);
      if (deliverableParticipants != null && !deliverableParticipants.isEmpty()) {
        for (DeliverableParticipant deliverableParticipant : deliverableParticipants) {
          // Total Participants
          if (deliverableParticipant.getParticipants() != null) {
            totalParticipants += deliverableParticipant.getParticipants();
          }
          if (deliverableParticipant.getFemales() != null) {
            totalFemales += deliverableParticipant.getFemales();
          }

          // Total Formal Training
          if (deliverableParticipant.getRepIndTypeActivity() != null && deliverableParticipant.getRepIndTypeActivity()
            .getName().contains(APConstants.REP_IND_SYNTHESIS_TYPE_ACTIVITY_FORMAL_TRAINING)) {
            totalParticipantFormalTraining += deliverableParticipant.getParticipants();
          }
        }
      }
      // Percentage female
      percentageFemales = Math.round(((totalFemales * 100) / totalParticipants) * 100) / 100.0;

      // Deliverables of Journal Articles type
      DeliverableType deliverableType = deliverableTypeManager.getDeliverableTypeById(63);
      deliverableInfos = deliverableInfoManager.getDeliverablesInfoByType(phase, deliverableType);

      if (deliverableInfos != null && !deliverableInfos.isEmpty()) {
        for (DeliverableInfo deliverableInfo : deliverableInfos) {
          // Load Disseminations
          List<DeliverableDissemination> deliverableDisseminations =
            deliverableInfo.getDeliverable().getDeliverableDisseminations().stream()
              .filter(dd -> dd.isActive() && dd.getPhase() != null && dd.getPhase().equals(phase))
              .collect(Collectors.toList());
          if (deliverableDisseminations != null && !deliverableDisseminations.isEmpty()) {
            deliverableInfo.getDeliverable().setDissemination(deliverableDisseminations.get(0));
            if (deliverableInfo.getDeliverable().getDissemination().getIsOpenAccess() != null) {
              // Journal Articles by Open Access
              if (deliverableInfo.getDeliverable().getDissemination().getIsOpenAccess()) {
                totalOpenAccess++;
              } else {
                totalLimited++;
              }
            } else {
              totalLimited++;
            }
          } else {
            totalLimited++;
          }

          // Load Publications
          List<DeliverablePublicationMetadata> deliverablePublicationMetadatas =
            deliverableInfo.getDeliverable().getDeliverablePublicationMetadatas().stream()
              .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(phase))
              .collect(Collectors.toList());
          if (deliverablePublicationMetadatas != null && !deliverablePublicationMetadatas.isEmpty()) {
            deliverableInfo.getDeliverable().setPublication(deliverablePublicationMetadatas.get(0));
            // Journal Articles by ISI status
            if (deliverableInfo.getDeliverable().getPublication().getIsiPublication() != null) {
              if (deliverableInfo.getDeliverable().getPublication().getIsiPublication()) {
                totalIsis++;
              } else {
                totalNoIsis++;
              }
            } else {
              totalNoIsis++;
            }
          } else {
            totalNoIsis++;
          }

          // Load Partnerships
          List<DeliverablePartnership> deliverablePartnerships =
            deliverableInfo.getDeliverable().getDeliverablePartnerships().stream()
              .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(phase)
                && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
              .collect(Collectors.toList());
          if (deliverablePartnerships != null && !deliverablePartnerships.isEmpty()) {
            deliverableInfo.getDeliverable().setResponsiblePartner(deliverablePartnerships.get(0));
          }
        }
      }
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    if (isInfluence) {
      this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_INFLUENCE_BASE_PERMISSION, params));
    } else {
      this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_CONTROL_BASE_PERMISSION, params));
    }

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators() != null) {
        reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators().clear();
      }
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisIndicatorGeneral indicatorGeneralDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisIndicatorGeneral();

      if (this.isPMU()) {
        this.saveSynthesisIndicators(indicatorGeneralDB);
      }

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
   * Save Synthesis Indicators
   * 
   * @param IndicatorGeneralDB
   */
  public void saveSynthesisIndicators(ReportSynthesisIndicatorGeneral IndicatorGeneralDB) {

    // Save form Information
    if (reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators() != null) {
      for (ReportSynthesisIndicator synthesisIndicator : reportSynthesis.getReportSynthesisIndicatorGeneral()
        .getSynthesisIndicators()) {
        if (synthesisIndicator.getId() == null) {

          ReportSynthesisIndicator synthesisIndicatorSave = new ReportSynthesisIndicator();

          synthesisIndicatorSave.setReportSynthesisIndicatorGeneral(IndicatorGeneralDB);

          RepIndSynthesisIndicator repIndSynthesisIndicator = repIndSynthesisIndicatorManager
            .getRepIndSynthesisIndicatorById(synthesisIndicator.getRepIndSynthesisIndicator().getId());

          synthesisIndicatorSave.setRepIndSynthesisIndicator(repIndSynthesisIndicator);

          synthesisIndicatorSave.setData(synthesisIndicator.getData());
          synthesisIndicatorSave.setComment(synthesisIndicator.getComment());
          reportSynthesisIndicatorManager.saveReportSynthesisIndicator(synthesisIndicatorSave);

        } else {
          boolean hasChanges = false;
          ReportSynthesisIndicator synthesisIndicatorPrev =
            reportSynthesisIndicatorManager.getReportSynthesisIndicatorById(synthesisIndicator.getId());

          if (synthesisIndicatorPrev.getData() != synthesisIndicator.getData()) {
            hasChanges = true;
            synthesisIndicatorPrev.setData(synthesisIndicator.getData());
          }

          if (synthesisIndicatorPrev.getComment() != synthesisIndicator.getComment()) {
            hasChanges = true;
            synthesisIndicatorPrev.setComment(synthesisIndicator.getComment());
          }


          if (hasChanges) {
            reportSynthesisIndicatorManager.saveReportSynthesisIndicator(synthesisIndicatorPrev);
          }

        }
      }
    }

  }

  public void setDeliverableInfos(List<DeliverableInfo> deliverableInfos) {
    this.deliverableInfos = deliverableInfos;
  }

  public void setDeliverableParticipants(List<DeliverableParticipant> deliverableParticipants) {
    this.deliverableParticipants = deliverableParticipants;
  }

  public void setInnovationsByStageDTO(List<ReportSynthesisInnovationsByStageDTO> innovationsByStageDTO) {
    this.innovationsByStageDTO = innovationsByStageDTO;
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


  public void
    setOrganizationTypeByStudiesDTOs(List<ReportSynthesisStudiesByOrganizationTypeDTO> organizationTypeByStudiesDTOs) {
    this.organizationTypeByStudiesDTOs = organizationTypeByStudiesDTOs;
  }

  public void setPartnershipsByGeographicScopeDTO(
    List<ReportSynthesisPartnershipsByGeographicScopeDTO> partnershipsByGeographicScopeDTO) {
    this.partnershipsByGeographicScopeDTO = partnershipsByGeographicScopeDTO;
  }

  public void setPartnershipsByPhaseDTO(List<ReportSynthesisPartnershipsByPhaseDTO> partnershipsByPhaseDTO) {
    this.partnershipsByPhaseDTO = partnershipsByPhaseDTO;
  }

  public void setPartnershipsByRepIndOrganizationTypeDTOs(
    List<ReportSynthesisPartnershipsByRepIndOrganizationTypeDTO> partnershipsByRepIndOrganizationTypeDTOs) {
    this.partnershipsByRepIndOrganizationTypeDTOs = partnershipsByRepIndOrganizationTypeDTOs;
  }

  public void setPercentageFemales(Double percentageFemales) {
    this.percentageFemales = percentageFemales;
  }

  public void setProjectExpectedStudies(List<ProjectExpectedStudy> projectExpectedStudies) {
    this.projectExpectedStudies = projectExpectedStudies;
  }

  public void setProjectInnovationInfos(List<ProjectInnovationInfo> projectInnovationInfos) {
    this.projectInnovationInfos = projectInnovationInfos;
  }

  public void setProjectPartnerPartnerships(List<ProjectPartnerPartnership> projectPartnerPartnerships) {
    this.projectPartnerPartnerships = projectPartnerPartnerships;
  }


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }


  public void setTotalIsis(Integer totalIsis) {
    this.totalIsis = totalIsis;
  }


  public void setTotalLimited(Integer totalLimited) {
    this.totalLimited = totalLimited;
  }


  public void setTotalNoIsis(Integer totalNoIsis) {
    this.totalNoIsis = totalNoIsis;
  }


  public void setTotalOpenAccess(Integer totalOpenAccess) {
    this.totalOpenAccess = totalOpenAccess;
  }


  public void setTotalParticipantFormalTraining(Double totalParticipantFormalTraining) {
    this.totalParticipantFormalTraining = totalParticipantFormalTraining;
  }

  public void setTotalParticipants(Double totalParticipants) {
    this.totalParticipants = totalParticipants;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      indicatorsValidator.validate(this, reportSynthesis, true, isInfluence);
    }
  }

}
