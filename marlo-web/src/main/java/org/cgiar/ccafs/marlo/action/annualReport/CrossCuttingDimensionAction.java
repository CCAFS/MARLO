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
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionAssetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingAssetDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionAsset;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingInnovationDTO;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.CCDimensionValidator;

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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrossCuttingDimensionAction extends BaseAction {


  private static final long serialVersionUID = -6437914260247069386L;


  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;


  private AuditLogManager auditLogManager;


  private CrpProgramManager crpProgramManager;

  private UserManager userManager;


  private DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;


  private ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager;


  private CCDimensionValidator validator;

  private ProjectFocusManager projectFocusManager;


  private ProjectManager projectManager;

  private ProjectInnovationManager projectInnovationManager;


  private ReportSynthesisCrossCuttingDimensionAssetManager reportSynthesisCrossCuttingDimensionAssetManager;

  private ReportSynthesisCrossCuttingDimensionInnovationManager reportSynthesisCrossCuttingDimensionInnovationManager;


  private PhaseManager phaseManager;

  // Variables
  private String transaction;


  private ReportSynthesis reportSynthesis;


  private Long liaisonInstitutionID;


  private Long synthesisID;

  private LiaisonInstitution liaisonInstitution;


  private GlobalUnit loggedCrp;


  private List<LiaisonInstitution> liaisonInstitutions;


  private List<ProjectInnovation> innovationsList;


  private List<DeliverableIntellectualAsset> assetsList;

  private List<DeliverableInfo> deliverableList;


  private CrossCuttingDimensionTableDTO tableC;


  private List<ReportSynthesisCrossCuttingDimension> flagshipCCDimensions;


  private List<ReportSynthesisCrossCuttingAssetDTO> flagshipPlannedAssets;


  private List<ReportSynthesisCrossCuttingInnovationDTO> flagshipPlannedInnovations;


  @Inject
  public CrossCuttingDimensionAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager,
    DeliverableIntellectualAssetManager deliverableIntellectualAssetManager,
    ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager,
    CCDimensionValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ProjectInnovationManager projectInnovationManager,
    ReportSynthesisCrossCuttingDimensionAssetManager reportSynthesisCrossCuttingDimensionAssetManager,
    ReportSynthesisCrossCuttingDimensionInnovationManager reportSynthesisCrossCuttingDimensionInnovationManager,
    PhaseManager phaseManager, CrpProgramManager crpProgramManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
    this.reportSynthesisCrossCuttingDimensionManager = reportSynthesisCrossCuttingDimensionManager;
    this.validator = validator;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.projectInnovationManager = projectInnovationManager;
    this.reportSynthesisCrossCuttingDimensionAssetManager = reportSynthesisCrossCuttingDimensionAssetManager;
    this.reportSynthesisCrossCuttingDimensionInnovationManager = reportSynthesisCrossCuttingDimensionInnovationManager;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
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


  /**
   * load the flagship tables information
   * 
   * @param phaseID
   * @param liaisonInstitution
   */
  private void flagshipSelectTables(long phaseID, LiaisonInstitution liaisonInstitution) {

    List<Deliverable> deliverables = new ArrayList<>();
    innovationsList = new ArrayList<>();
    assetsList = new ArrayList<>();
    deliverableList = new ArrayList<>();

    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectFocusManager.findAll() != null) {

      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
          && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());
        List<ProjectInnovation> innovations = new ArrayList<>(project.getProjectInnovations().stream()
          .filter(in -> in.isActive() && in.getProjectInnovationInfo(phase) != null).collect(Collectors.toList()));
        for (ProjectInnovation projectInnovation : innovations) {
          if (projectInnovation.getProjectInnovationInfo(phase).getYear() == this.getCurrentCycleYear()) {
            innovationsList.add(projectInnovation);
          }
        }

        deliverables.addAll(this.getProjectDeliverables(project, phase));

      }


      // setup project innovations
      for (ProjectInnovation projectInnovation : innovationsList) {
        if (projectInnovation.getProjectInnovationCrps() != null
          && !projectInnovation.getProjectInnovationCrps().isEmpty()) {
          projectInnovation.setCrps(new ArrayList<>(projectInnovation.getProjectInnovationCrps().stream()
            .filter(s -> s.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

      }

      for (Deliverable deliverable : deliverables) {


        // Setup deliverable intellectual assets
        if (deliverable.getDeliverableIntellectualAssets() != null
          && !deliverable.getDeliverableIntellectualAssets().isEmpty()
          && deliverable.getDeliverableIntellectualAssets().size() > 0) {
          List<DeliverableIntellectualAsset> list = deliverable.getDeliverableIntellectualAssets().stream()
            .filter(i -> i.isActive() && i.getHasPatentPvp() != null && i.getHasPatentPvp())
            .collect(Collectors.toList());
          if (list != null && !list.isEmpty()) {
            DeliverableIntellectualAsset deliverableIntellectualAsset = list.get(0);
            if (deliverableIntellectualAsset != null) {
              assetsList.add(deliverableIntellectualAsset);
            }
          }

        }

      }


    }


  }


  public List<DeliverableIntellectualAsset> getAssetsList() {
    return assetsList;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_"
      + this.getActualPhase().getDescription() + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<DeliverableInfo> getDeliverableList() {
    return deliverableList;
  }


  public List<ReportSynthesisCrossCuttingDimension> getFlagshipCCDimensions() {
    return flagshipCCDimensions;
  }

  public List<ReportSynthesisCrossCuttingAssetDTO> getFlagshipPlannedAssets() {
    return flagshipPlannedAssets;
  }

  public List<ReportSynthesisCrossCuttingInnovationDTO> getFlagshipPlannedInnovations() {
    return flagshipPlannedInnovations;
  }

  public List<ProjectInnovation> getInnovationsList() {
    return innovationsList;
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

  /**
   * Select the Project Reporting Deliverables
   * 
   * @param project
   * @param phase
   * @return
   */
  public List<Deliverable> getProjectDeliverables(Project project, Phase phase) {
    List<Deliverable> deliverables = new ArrayList<>();


    deliverables = project.getDeliverables().stream()
      .filter(d -> d.isActive() && d.getProject() != null && d.getProject().isActive()
        && d.getProject().getGlobalUnitProjects().stream()
          .filter(gup -> gup.isActive() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
          .collect(Collectors.toList()).size() > 0
        && d.getDeliverableInfo(phase) != null && d.getDeliverableInfo(phase).getStatus() != null
        && ((d.getDeliverableInfo(phase).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId())
          && (d.getDeliverableInfo(phase).getYear() >= this.getCurrentCycleYear()
            || (d.getDeliverableInfo(phase).getNewExpectedYear() != null
              && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() >= this.getCurrentCycleYear())))
          || (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())
            && (d.getDeliverableInfo(phase).getNewExpectedYear() != null
              && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() == this.getCurrentCycleYear()))
          || (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())
            && (d.getDeliverableInfo(phase).getYear() == this.getCurrentCycleYear()
              || (d.getDeliverableInfo(phase).getNewExpectedYear() != null
                && d.getDeliverableInfo(phase).getNewExpectedYear().intValue() == this.getCurrentCycleYear()))))
        && (d.getDeliverableInfo(phase).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())
          || d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
          || d.getDeliverableInfo(phase).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())))
      .collect(Collectors.toList());


    return deliverables;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public Long getSynthesisID() {
    return synthesisID;
  }


  public CrossCuttingDimensionTableDTO getTableC() {
    return tableC;
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

      // Fill Flagship Innovations and Assets
      if (this.isFlagship()) {
        this.flagshipSelectTables(phase.getId(), liaisonInstitution);
      }


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
        if (reportSynthesis.getReportSynthesisCrossCuttingDimension() == null) {
          ReportSynthesisCrossCuttingDimension cuttingDimension = new ReportSynthesisCrossCuttingDimension();
          // create one to one relation
          reportSynthesis.setReportSynthesisCrossCuttingDimension(cuttingDimension);
          cuttingDimension.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        if (this.isFlagship()) {

          // Project Innovations
          reportSynthesis.getReportSynthesisCrossCuttingDimension().setInnovations(new ArrayList<>());
          if (reportSynthesis.getReportSynthesisCrossCuttingDimension()
            .getReportSynthesisCrossCuttingDimensionInnovations() != null
            && !reportSynthesis.getReportSynthesisCrossCuttingDimension()
              .getReportSynthesisCrossCuttingDimensionInnovations().isEmpty()) {
            for (ReportSynthesisCrossCuttingDimensionInnovation dimensionInnovation : reportSynthesis
              .getReportSynthesisCrossCuttingDimension().getReportSynthesisCrossCuttingDimensionInnovations().stream()
              .filter(i -> i.isActive()).collect(Collectors.toList())) {
              reportSynthesis.getReportSynthesisCrossCuttingDimension().getInnovations()
                .add(dimensionInnovation.getProjectInnovation());
            }
          }

          // Deliverable Intellectual Assets
          reportSynthesis.getReportSynthesisCrossCuttingDimension().setAssets(new ArrayList<>());
          if (reportSynthesis.getReportSynthesisCrossCuttingDimension()
            .getReportSynthesisCrossCuttingDimensionAssets() != null
            && !reportSynthesis.getReportSynthesisCrossCuttingDimension()
              .getReportSynthesisCrossCuttingDimensionAssets().isEmpty()) {
            for (ReportSynthesisCrossCuttingDimensionAsset dimensionAsset : reportSynthesis
              .getReportSynthesisCrossCuttingDimension().getReportSynthesisCrossCuttingDimensionAssets().stream()
              .filter(a -> a.isActive()).collect(Collectors.toList())) {
              reportSynthesis.getReportSynthesisCrossCuttingDimension().getAssets()
                .add(dimensionAsset.getDeliverableIntellectualAsset());
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


    if (this.isPMU()) {

      // Flagship Cross Cutting Dimensions Synthesis progress
      flagshipCCDimensions =
        reportSynthesisCrossCuttingDimensionManager.getFlagshipCCDimensions(liaisonInstitutions, phase.getId());

      // Table D-2 Lof of Crp Innovations
      flagshipPlannedInnovations = reportSynthesisCrossCuttingDimensionManager
        .getPlannedInnovationList(liaisonInstitutions, phase.getId(), loggedCrp, this.liaisonInstitution);

      // Table E Intellectual Assets
      flagshipPlannedAssets = reportSynthesisCrossCuttingDimensionManager.getPlannedAssetsList(liaisonInstitutions,
        phase.getId(), loggedCrp, this.liaisonInstitution);


    }

    // Setup Table C
    tableC = reportSynthesisCrossCuttingDimensionManager.getTableC(phase, loggedCrp);
    deliverableList = tableC.getDeliverableList();


    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_CROSS_CUTTING_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisCrossCuttingDimension().getPlannedAssets() != null) {
        reportSynthesis.getReportSynthesisCrossCuttingDimension().getPlannedAssets().clear();
      }

      if (reportSynthesis.getReportSynthesisCrossCuttingDimension().getPlannedInnovations() != null) {
        reportSynthesis.getReportSynthesisCrossCuttingDimension().getPlannedInnovations().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      ReportSynthesisCrossCuttingDimension crossCuttingDimensionDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisCrossCuttingDimension();

      if (this.isFlagship()) {

        if (reportSynthesis.getReportSynthesisCrossCuttingDimension().getPlannedInnovations() == null) {
          reportSynthesis.getReportSynthesisCrossCuttingDimension().setPlannedInnovations(new ArrayList<>());
        }

        if (reportSynthesis.getReportSynthesisCrossCuttingDimension().getPlannedAssets() == null) {
          reportSynthesis.getReportSynthesisCrossCuttingDimension().setPlannedAssets(new ArrayList<>());
        }

        this.saveInnovations(crossCuttingDimensionDB);
        this.saveAssets(crossCuttingDimensionDB);

      }

      crossCuttingDimensionDB
        .setGenderDescription(reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderDescription());
      crossCuttingDimensionDB
        .setGenderLessons(reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderLessons());
      crossCuttingDimensionDB
        .setYouthDescription(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthDescription());
      crossCuttingDimensionDB
        .setYouthLessons(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthLessons());
      crossCuttingDimensionDB
        .setOtherAspects(reportSynthesis.getReportSynthesisCrossCuttingDimension().getOtherAspects());
      crossCuttingDimensionDB.setCapDev(reportSynthesis.getReportSynthesisCrossCuttingDimension().getCapDev());

      if (this.isPMU()) {
        crossCuttingDimensionDB
          .setIntellectualAssets(reportSynthesis.getReportSynthesisCrossCuttingDimension().getIntellectualAssets());
        crossCuttingDimensionDB.setOpenData(reportSynthesis.getReportSynthesisCrossCuttingDimension().getOpenData());
      }


      crossCuttingDimensionDB =
        reportSynthesisCrossCuttingDimensionManager.saveReportSynthesisCrossCuttingDimension(crossCuttingDimensionDB);


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

  public void saveAssets(ReportSynthesisCrossCuttingDimension crossCuttingDimensionDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> studiesIds = new ArrayList<>();

    for (DeliverableIntellectualAsset std : assetsList) {
      studiesIds.add(std.getId());
    }

    if (reportSynthesis.getReportSynthesisCrossCuttingDimension().getAssetsValue() != null
      && reportSynthesis.getReportSynthesisCrossCuttingDimension().getAssetsValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisCrossCuttingDimension().getAssetsValue().trim()
        .split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }


      for (Long studyId : studiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }


      }

      for (ReportSynthesisCrossCuttingDimensionAsset reportAsset : crossCuttingDimensionDB
        .getReportSynthesisCrossCuttingDimensionAssets().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(reportAsset.getDeliverableIntellectualAsset().getId())) {
          reportSynthesisCrossCuttingDimensionAssetManager
            .deleteReportSynthesisCrossCuttingDimensionAsset(reportAsset.getId());
        }
      }

      for (Long studyId : studiesIds) {
        DeliverableIntellectualAsset asset =
          deliverableIntellectualAssetManager.getDeliverableIntellectualAssetById(studyId);

        ReportSynthesisCrossCuttingDimensionAsset assetNew = new ReportSynthesisCrossCuttingDimensionAsset();
        assetNew = new ReportSynthesisCrossCuttingDimensionAsset();
        assetNew.setDeliverableIntellectualAsset(asset);
        assetNew.setReportSynthesisCrossCuttingDimension(crossCuttingDimensionDB);

        List<ReportSynthesisCrossCuttingDimensionAsset> crpPlannedStudies =
          crossCuttingDimensionDB.getReportSynthesisCrossCuttingDimensionAssets().stream().filter(rio -> rio.isActive())
            .collect(Collectors.toList());


        if (!crpPlannedStudies.contains(assetNew)) {
          assetNew =
            reportSynthesisCrossCuttingDimensionAssetManager.saveReportSynthesisCrossCuttingDimensionAsset(assetNew);
        }
      }
    } else {

      for (Long studyId : studiesIds) {
        DeliverableIntellectualAsset asset =
          deliverableIntellectualAssetManager.getDeliverableIntellectualAssetById(studyId);

        ReportSynthesisCrossCuttingDimensionAsset assetNew = new ReportSynthesisCrossCuttingDimensionAsset();
        assetNew = new ReportSynthesisCrossCuttingDimensionAsset();
        assetNew.setDeliverableIntellectualAsset(asset);
        assetNew.setReportSynthesisCrossCuttingDimension(crossCuttingDimensionDB);

        List<ReportSynthesisCrossCuttingDimensionAsset> crpPlannedStudies =
          crossCuttingDimensionDB.getReportSynthesisCrossCuttingDimensionAssets().stream().filter(rio -> rio.isActive())
            .collect(Collectors.toList());


        if (!crpPlannedStudies.contains(assetNew)) {
          assetNew =
            reportSynthesisCrossCuttingDimensionAssetManager.saveReportSynthesisCrossCuttingDimensionAsset(assetNew);
        }
      }

    }

  }

  public void saveInnovations(ReportSynthesisCrossCuttingDimension crossCuttingDimensionDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> studiesIds = new ArrayList<>();

    for (ProjectInnovation std : innovationsList) {
      studiesIds.add(std.getId());
    }

    if (reportSynthesis.getReportSynthesisCrossCuttingDimension().getInnovationsValue() != null
      && reportSynthesis.getReportSynthesisCrossCuttingDimension().getInnovationsValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisCrossCuttingDimension().getInnovationsValue().trim()
        .split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }


      for (Long studyId : studiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }


      }

      for (ReportSynthesisCrossCuttingDimensionInnovation reportInnovation : crossCuttingDimensionDB
        .getReportSynthesisCrossCuttingDimensionInnovations().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(reportInnovation.getProjectInnovation().getId())) {
          reportSynthesisCrossCuttingDimensionInnovationManager
            .deleteReportSynthesisCrossCuttingDimensionInnovation(reportInnovation.getId());
        }
      }

      for (Long studyId : studiesIds) {
        ProjectInnovation innovation = projectInnovationManager.getProjectInnovationById(studyId);

        ReportSynthesisCrossCuttingDimensionInnovation innovationNew =
          new ReportSynthesisCrossCuttingDimensionInnovation();
        innovationNew = new ReportSynthesisCrossCuttingDimensionInnovation();
        innovationNew.setProjectInnovation(innovation);
        innovationNew.setReportSynthesisCrossCuttingDimension(crossCuttingDimensionDB);

        List<ReportSynthesisCrossCuttingDimensionInnovation> crpPlannedStudies =
          crossCuttingDimensionDB.getReportSynthesisCrossCuttingDimensionInnovations().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!crpPlannedStudies.contains(innovationNew)) {
          innovationNew = reportSynthesisCrossCuttingDimensionInnovationManager
            .saveReportSynthesisCrossCuttingDimensionInnovation(innovationNew);
        }
      }
    } else {

      for (Long studyId : studiesIds) {
        ProjectInnovation innovation = projectInnovationManager.getProjectInnovationById(studyId);

        ReportSynthesisCrossCuttingDimensionInnovation innovationNew =
          new ReportSynthesisCrossCuttingDimensionInnovation();
        innovationNew = new ReportSynthesisCrossCuttingDimensionInnovation();
        innovationNew.setProjectInnovation(innovation);
        innovationNew.setReportSynthesisCrossCuttingDimension(crossCuttingDimensionDB);

        List<ReportSynthesisCrossCuttingDimensionInnovation> crpPlannedStudies =
          crossCuttingDimensionDB.getReportSynthesisCrossCuttingDimensionInnovations().stream()
            .filter(rio -> rio.isActive()).collect(Collectors.toList());

        if (!crpPlannedStudies.contains(innovationNew)) {
          innovationNew = reportSynthesisCrossCuttingDimensionInnovationManager
            .saveReportSynthesisCrossCuttingDimensionInnovation(innovationNew);
        }
      }

    }

  }

  public void setAssetsList(List<DeliverableIntellectualAsset> assetsList) {
    this.assetsList = assetsList;
  }

  public void setDeliverableList(List<DeliverableInfo> deliverableList) {
    this.deliverableList = deliverableList;
  }

  public void setFlagshipCCDimensions(List<ReportSynthesisCrossCuttingDimension> flagshipCCDimensions) {
    this.flagshipCCDimensions = flagshipCCDimensions;
  }

  public void setFlagshipPlannedAssets(List<ReportSynthesisCrossCuttingAssetDTO> flagshipPlannedAssets) {
    this.flagshipPlannedAssets = flagshipPlannedAssets;
  }


  public void setFlagshipPlannedInnovations(List<ReportSynthesisCrossCuttingInnovationDTO> flagshipPlannedInnovations) {
    this.flagshipPlannedInnovations = flagshipPlannedInnovations;
  }


  public void setInnovationsList(List<ProjectInnovation> innovationsList) {
    this.innovationsList = innovationsList;
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

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTableC(CrossCuttingDimensionTableDTO tableC) {
    this.tableC = tableC;
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
