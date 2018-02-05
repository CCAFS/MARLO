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
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.sythesis.CrpIndicatorsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class FlagshipPlansAction extends BaseAction {


  private static final long serialVersionUID = -1417643537265271428L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbSynthesisManager powbSynthesisManager;
  private FileDBManager fileDBManager;

  // Model for the front-end
  private PowbSynthesis powbSynthesis;
  private Long powbSynthesisID;

  private List<LiaisonInstitution> liaisonInstitutions;


  private String transaction;

  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private LiaisonInstitution liaisonInstitution;
  private Long liaisonInstitutionID;
  private CrpProgramManager crpProgramManager;
  private GlobalUnit loggedCrp;
  private CrpIndicatorsValidator validator;

  @Inject
  public FlagshipPlansAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, CrpIndicatorsValidator validator,
    AuditLogManager auditLogManager, CrpProgramManager crpProgramManager, UserManager userManager,
    PowbSynthesisManager powbSynthesisManager, FileDBManager fileDBManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.fileDBManager = fileDBManager;

  }

  @Override
  public String cancel() {
    return SUCCESS;
  }

  private void checkFlagshipPlansFile() {
    if (powbSynthesis.getPowbFlagshipPlans() != null
      && powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null) {
      if (powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId() != null) {
        powbSynthesis.getPowbFlagshipPlans().setFlagshipProgramFile(
          fileDBManager.getFileDBById(powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId()));
      } else {
        powbSynthesis.getPowbFlagshipPlans().setFlagshipProgramFile(null);
      }
    }
  }

  private void checkHistoryVersion() {
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      this.setPowbSynthesisIdHistory();
    } else {
      this.setLiaisonInstitutionIdParameter();
      this.setPowbSynthesisIdParameter();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
      if (powbSynthesisID != null) {
        powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      }
    }
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
    String composedClassName = liaisonInstitution.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = liaisonInstitution.getId() + "_" + composedClassName + "_" + loggedCrp.getAcronym() + "_powb_"
      + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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

  public Long getPowbSynthesisID() {
    return powbSynthesisID;
  }


  public String getTransaction() {
    return transaction;
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
    // Check history version
    this.checkHistoryVersion();
    // Validate draft version
    if (powbSynthesis != null) {
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        this.readJsonAndLoadPowbSynthesis(path);
      } else {
        this.setDraft(false);
      }
    }
    this.checkFlagshipPlansFile();
    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU")).collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
  }

  private void readJsonAndLoadPowbSynthesis(Path path) throws IOException {
    BufferedReader reader = null;
    reader = new BufferedReader(new FileReader(path.toFile()));
    Gson gson = new GsonBuilder().create();
    JsonObject jReader = gson.fromJson(reader, JsonObject.class);
    reader.close();
    AutoSaveReader autoSaveReader = new AutoSaveReader();
    // We read the JSON serialized by the front-end and cast it to the object
    powbSynthesis = (PowbSynthesis) autoSaveReader.readFromJson(jReader);
    powbSynthesisID = powbSynthesis.getId();
  }

  @Override
  public String save() {
    return SUCCESS;
  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  /**
   * Get Liaison institution ID Parameter
   */
  private void setLiaisonInstitutionIdParameter() {
    try {
      liaisonInstitutionID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (NumberFormatException e) {
      // If there is no liaisonInstitution parameter
      User user = userManager.getUser(this.getCurrentUser().getId());
      if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
        List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
          .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId())
          .collect(Collectors.toList()));
        if (!liaisonUsers.isEmpty()) {
          LiaisonUser liaisonUser = new LiaisonUser();
          liaisonUser = liaisonUsers.get(0);
          liaisonInstitutionID = liaisonUser.getLiaisonInstitution().getId();
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      } else {
        liaisonInstitutionID = this.firstFlagship();
      }
    }
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

  public void setPowbSynthesisID(Long powbSynthesisID) {
    this.powbSynthesisID = powbSynthesisID;
  }

  private void setPowbSynthesisIdHistory() {
    transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
    PowbSynthesis history = (PowbSynthesis) auditLogManager.getHistory(transaction);
    if (history != null) {
      powbSynthesis = history;
      powbSynthesisID = powbSynthesis.getId();
      liaisonInstitutionID = powbSynthesis.getLiaisonInstitution().getId();
    } else {
      this.transaction = null;
      this.setTransaction("-1");
    }
  }

  private void setPowbSynthesisIdParameter() {
    try {
      powbSynthesisID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)));
    } catch (NumberFormatException e) {
      PowbSynthesis powbSynthesis =
        powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
      if (powbSynthesis != null) {
        powbSynthesisID = powbSynthesis.getId();
      }
    }
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {

    }
  }

}
