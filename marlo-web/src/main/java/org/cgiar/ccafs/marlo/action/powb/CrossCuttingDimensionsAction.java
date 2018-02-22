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


package org.cgiar.ccafs.marlo.action.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.powb.CrossCuttingValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

public class CrossCuttingDimensionsAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -2668150868648923650L;


  private GlobalUnitManager crpManager;

  private AuditLogManager auditLogManager;


  private LiaisonInstitutionManager liaisonInstitutionManager;
  private CrpProgramManager crpProgramManager;
  private PowbSynthesisManager powbSynthesisManager;
  private CrossCuttingValidator validator;
  private PowbCrossCuttingDimensionManager powbCrossCuttingDimensionManager;
  private DeliverableManager deliverableManager;
  private List<LiaisonInstitution> liaisonInstitutions;
  private UserManager userManager;


  private LiaisonInstitution liaisonInstitution;
  private String transaction;
  private Long crossCuttingId;
  private Long liaisonInstitutionID;
  private GlobalUnit loggedCrp;
  private PowbSynthesis powbSynthesis;
  private CrossCuttingDimensionTableDTO tableC;
  private LiaisonInstitution thePMU;
  private List<DeliverableInfo> deliverableList;
  private Long powbSynthesisID;

  @Inject
  public CrossCuttingDimensionsAction(APConfig config, GlobalUnitManager crpManager, AuditLogManager auditLogManager,
    LiaisonInstitutionManager liaisonInstitutionManager, CrossCuttingValidator validator,
    CrpProgramManager crpProgramManager, UserManager userManager, PowbSynthesisManager powbSynthesisManager,
    PowbCrossCuttingDimensionManager powbCrossCuttingDimensionManager, DeliverableManager deliverableManager) {
    super(config);
    this.crpManager = crpManager;
    this.auditLogManager = auditLogManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpProgramManager = crpProgramManager;
    this.userManager = userManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.validator = validator;
    this.powbCrossCuttingDimensionManager = powbCrossCuttingDimensionManager;
    this.deliverableManager = deliverableManager;

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
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public Long getCrossCuttingId() {
    return crossCuttingId;
  }


  public List<DeliverableInfo> getDeliverableList() {
    return deliverableList;
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

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }


  public CrossCuttingDimensionTableDTO getTableC() {
    return tableC;
  }


  public LiaisonInstitution getThePMU() {
    return thePMU;
  }


  public String getTransaction() {
    return transaction;
  }


  public UserManager getUserManager() {
    return userManager;
  }


  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution.getCrpProgram() != null) {
      CrpProgram crpProgram =
        crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
      if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
        isFP = true;
      }
    }
    return isFP;
  }


  @Override
  public boolean isPMU() {
    boolean isFP = false;
    if (liaisonInstitution.getCrpProgram() == null) {
      isFP = true;
    }
    return isFP;

  }


  public boolean isPMU(LiaisonInstitution institution) {
    if (institution.getAcronym().equals("PMU")) {
      return true;
    }

    return false;
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

    for (LiaisonInstitution institution : this.getLoggedCrp().getLiaisonInstitutions()) {
      if (this.isPMU(institution)) {
        thePMU = institution;
        break;
      }
    }


    // If there is a history version being loaded
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      PowbSynthesis history = (PowbSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        powbSynthesis = history;
        powbSynthesisID = powbSynthesis.getId();
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      try {
        powbSynthesisID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)));
        powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

        if (!powbSynthesis.getPhase().equals(phase)) {
          powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), thePMU.getId());
          if (powbSynthesis == null) {
            powbSynthesis = this.createPowbSynthesis(phase.getId(), thePMU.getId());
          }
          powbSynthesisID = powbSynthesis.getId();
        }
      } catch (Exception e) {

        powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), thePMU.getId());
        if (powbSynthesis == null) {
          powbSynthesis = this.createPowbSynthesis(phase.getId(), thePMU.getId());
        }
        powbSynthesisID = powbSynthesis.getId();

      }
    }
    // Get Liaison institution ID Parameter
    try {
      liaisonInstitutionID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (NumberFormatException e) {
      User user = userManager.getUser(this.getCurrentUser().getId());
      if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
        List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
          .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId())
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

            if (!isLeader) {
              liaisonInstitutionID = this.firstFlagship();
            }
          }
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      } else {
        liaisonInstitutionID = this.firstFlagship();
      }
    }

    liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);


    if (powbSynthesis != null) {
      PowbSynthesis powbSynthesisDB = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesisID = powbSynthesisDB.getId();
      // liaisonInstitutionID = powbSynthesisDB.getLiaisonInstitution().getId();
      // liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      Path path = this.getAutoSaveFilePath();

      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        powbSynthesis = (PowbSynthesis) autoSaveReader.readFromJson(jReader);
        powbSynthesisID = powbSynthesis.getId();
        this.setDraft(true);
        reader.close();
      } else {
        this.setDraft(false);
        // Check if CrossCutting relation is null -create it
        if (powbSynthesis.getPowbCrossCuttingDimension() == null) {

          PowbCrossCuttingDimension crossCutting = new PowbCrossCuttingDimension();
          crossCutting.setActive(true);
          crossCutting.setActiveSince(new Date());
          crossCutting.setCreatedBy(this.getCurrentUser());
          crossCutting.setModifiedBy(this.getCurrentUser());
          crossCutting.setModificationJustification("");

          // create one to one relation
          powbSynthesis.setPowbCrossCuttingDimension(crossCutting);
          crossCutting.setPowbSynthesis(powbSynthesis);

          // save the changes
          powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);

        }
      }

    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    // ADD PMU as liasion Institutio too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU")).collect(Collectors.toList()));

    // Get the tableC Information
    this.tableCInfo(phase);


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), powbSynthesis.getId() + ""};
    // this.setBasePermission(this.getText(Permission.POWB_SYNTHESIS_TOC_BASE_PERMISSION, params));

    if (this.isHttpPost()) {

    }


  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      PowbCrossCuttingDimension crossCuttingDB =
        powbSynthesisManager.getPowbSynthesisById(powbSynthesisID).getPowbCrossCuttingDimension();

      crossCuttingDB.setSummarize(powbSynthesis.getPowbCrossCuttingDimension().getSummarize());
      crossCuttingDB.setAssets(powbSynthesis.getPowbCrossCuttingDimension().getAssets());


      crossCuttingDB = powbCrossCuttingDimensionManager.savePowbCrossCuttingDimension(crossCuttingDB);

      List<String> relationsName = new ArrayList<>();
      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesis.setModifiedBy(this.getCurrentUser());
      powbSynthesis.setActiveSince(new Date());

      powbSynthesisManager.save(powbSynthesis, this.getActionName(), relationsName, this.getActualPhase());


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


  public void setCrossCuttingId(Long crossCuttingId) {
    this.crossCuttingId = crossCuttingId;
  }


  public void setDeliverableList(List<DeliverableInfo> deliverableList) {
    this.deliverableList = deliverableList;
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


  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setTableC(CrossCuttingDimensionTableDTO tableC) {
    this.tableC = tableC;
  }


  public void setThePMU(LiaisonInstitution thePMU) {
    this.thePMU = thePMU;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  public void setUserManager(UserManager userManager) {
    this.userManager = userManager;
  }

  /**
   * List all the deliverables of the Crp to make the calculations in the Cross Cutting Socores.
   * 
   * @param pashe - The phase that get the deliverable information.
   */
  public void tableCInfo(Phase pashe) {
    List<Deliverable> deliverables = new ArrayList<>();
    deliverableList = new ArrayList<>();
    int iGenderPrincipal = 0;
    int iGenderSignificant = 0;
    int iYouthPrincipal = 0;
    int iYouthSignificant = 0;
    int iCapDevPrincipal = 0;
    int iCapDevSignificant = 0;
    int iNa = 0;

    if (deliverableManager.findAll() != null) {
      deliverables = deliverableManager.findAll().stream().filter(d -> d.isActive() && d.getPhase().equals(pashe))
        .collect(Collectors.toList());
      for (Deliverable deliverable : deliverables) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(pashe);
        if (deliverableInfo.isActive()) {
          if (deliverableInfo.getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || deliverableInfo.getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            deliverableList.add(deliverableInfo);
            if (deliverableInfo.getCrossCuttingNa() != null && deliverableInfo.getCrossCuttingNa()) {
              iNa++;
            } else {
              // Gender
              if (deliverableInfo.getCrossCuttingGender() != null && deliverableInfo.getCrossCuttingGender()) {
                if (deliverableInfo.getCrossCuttingScoreGender() != null
                  && deliverableInfo.getCrossCuttingScoreGender() == 1) {
                  iGenderSignificant++;
                } else if (deliverableInfo.getCrossCuttingScoreGender() != null
                  && deliverableInfo.getCrossCuttingScoreGender() == 2) {
                  iGenderPrincipal++;
                }
              }

              // Youth
              if (deliverableInfo.getCrossCuttingYouth() != null && deliverableInfo.getCrossCuttingYouth()) {
                if (deliverableInfo.getCrossCuttingScoreYouth() != null
                  && deliverableInfo.getCrossCuttingScoreYouth() == 1) {
                  iYouthSignificant++;
                } else if (deliverableInfo.getCrossCuttingScoreYouth() != null
                  && deliverableInfo.getCrossCuttingScoreYouth() == 2) {
                  iYouthPrincipal++;
                }
              }

              // CapDev
              if (deliverableInfo.getCrossCuttingCapacity() != null && deliverableInfo.getCrossCuttingCapacity()) {
                if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                  && deliverableInfo.getCrossCuttingScoreCapacity() == 1) {
                  iCapDevSignificant++;
                } else if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                  && deliverableInfo.getCrossCuttingScoreCapacity() == 2) {
                  iCapDevPrincipal++;
                }
              }
            }
          }
        }
      }
      tableC = new CrossCuttingDimensionTableDTO();
      int iDeliverableCount = deliverableList.size();

      tableC.setTotal(iDeliverableCount);

      double dGenderPrincipal = (iGenderPrincipal * 100) / iDeliverableCount;
      double dGenderSignificant = (iGenderSignificant * 100) / iDeliverableCount;
      double dYouthPrincipal = (iYouthPrincipal * 100) / iDeliverableCount;
      double dYouthSignificant = (iYouthSignificant * 100) / iDeliverableCount;
      double dCapDevPrincipal = (iCapDevPrincipal * 100) / iDeliverableCount;
      double dCapDevSignificant = (iCapDevSignificant * 100) / iDeliverableCount;
      double dNa = (iNa * 100) / iDeliverableCount;


      // Gender
      tableC.setGenderPrincipal(iGenderPrincipal);
      tableC.setGenderSignificant(iGenderSignificant);
      tableC.setGenderScored(iNa);

      tableC.setPercentageGenderPrincipal(dGenderPrincipal);
      tableC.setPercentageGenderSignificant(dGenderSignificant);
      tableC.setPercentageGenderNotScored(dNa);
      // Youth
      tableC.setYouthPrincipal(iYouthPrincipal);
      tableC.setYouthSignificant(iYouthSignificant);
      tableC.setYouthScored(iNa);

      tableC.setPercentageYouthPrincipal(dYouthPrincipal);
      tableC.setPercentageYouthSignificant(dYouthSignificant);
      tableC.setPercentageYouthNotScored(dNa);
      // CapDev
      tableC.setCapDevPrincipal(iCapDevPrincipal);
      tableC.setCapDevSignificant(iCapDevSignificant);
      tableC.setCapDevScored(iNa);

      tableC.setPercentageCapDevPrincipal(dCapDevPrincipal);
      tableC.setPercentageCapDevSignificant(dCapDevSignificant);
      tableC.setPercentageCapDevNotScored(dNa);


    }

  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, powbSynthesis, true);
    }
  }


}
