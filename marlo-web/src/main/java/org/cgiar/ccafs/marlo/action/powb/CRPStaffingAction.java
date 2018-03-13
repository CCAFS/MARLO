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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingCategoriesManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisCrpStaffingCategoryManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffing;
import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffingCategories;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisCrpStaffingCategory;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.powb.CrpStaffingValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
public class CRPStaffingAction extends BaseAction {

  private static final long serialVersionUID = 8792953923111769705L;
  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbSynthesisManager powbSynthesisManager;
  private CrpProgramManager crpProgramManager;
  private AuditLogManager auditLogManager;
  private PowbCrpStaffingManager powbCrpStaffingManager;
  private PowbSynthesisCrpStaffingCategoryManager powbSynthesisCrpStaffingCategoryManager;
  private PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager;
  // Model for the front-end
  private PowbSynthesis powbSynthesis;
  private Long powbSynthesisID;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<PowbCrpStaffingCategories> powbCrpStaffingCategories;
  private String transaction;
  private LiaisonInstitution liaisonInstitution;
  private Long liaisonInstitutionID;
  private GlobalUnit loggedCrp;
  private CrpStaffingValidator validator;

  @Inject
  public CRPStaffingAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, AuditLogManager auditLogManager,
    CrpProgramManager crpProgramManager, PowbSynthesisManager powbSynthesisManager, CrpStaffingValidator validator,
    PowbCrpStaffingManager powbCrpStaffingManager,
    PowbSynthesisCrpStaffingCategoryManager powbSynthesisCrpStaffingCategoryManager,
    PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpProgramManager = crpProgramManager;
    this.auditLogManager = auditLogManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.validator = validator;
    this.powbCrpStaffingManager = powbCrpStaffingManager;
    this.powbSynthesisCrpStaffingCategoryManager = powbSynthesisCrpStaffingCategoryManager;
    this.powbCrpStaffingCategoriesManager = powbCrpStaffingCategoriesManager;
  }

  @Override
  public String cancel() {
    return SUCCESS;
  }

  private void createEmptyCrpStaffing() {
    if (powbSynthesis.getCrpStaffing() == null && this.isPMU()) {
      PowbCrpStaffing newPowbCrpStaffing = new PowbCrpStaffing();
      newPowbCrpStaffing.setActive(true);
      newPowbCrpStaffing.setCreatedBy(this.getCurrentUser());
      newPowbCrpStaffing.setModifiedBy(this.getCurrentUser());
      newPowbCrpStaffing.setActiveSince(new Date());
      newPowbCrpStaffing.setStaffingIssues("");
      powbSynthesis.setCrpStaffing(newPowbCrpStaffing);
      newPowbCrpStaffing.setPowbSynthesis(powbSynthesis);
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    }
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
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<LiaisonInstitution> getFlagships() {
    List<LiaisonInstitution> flagshipsList = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    if (flagshipsList != null) {
      flagshipsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
      return flagshipsList;
    } else {
      return new ArrayList<>();
    }
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

  // Method to download link file
  public String getPath(Long liaisonInstitutionID) {
    return config.getDownloadURL() + "/" + this.getPowbSourceFolder(liaisonInstitutionID).replace('\\', '/');
  }

  public List<PowbCrpStaffingCategories> getPowbCrpStaffingCategories() {
    return powbCrpStaffingCategories;
  }


  // Method to get the download folder
  private String getPowbSourceFolder(Long liaisonInstitutionID) {
    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    return APConstants.POWB_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(liaisonInstitution.getAcronym()).concat(File.separator).concat(this.getActionName().replace("/", "_"))
      .concat(File.separator);
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public Long getPowbSynthesisID() {
    return powbSynthesisID;
  }

  public PowbSynthesisCrpStaffingCategory getSynthesisCrpStaffingCategory(Long crpStaffingcategory) {
    if (crpStaffingcategory != null) {
      List<PowbSynthesisCrpStaffingCategory> PowbSynthesisCrpStaffingCategory =
        powbSynthesis.getPowbSynthesisCrpStaffingCategoryList().stream()
          .filter(c -> c.getPowbCrpStaffingCategory().getId().equals(crpStaffingcategory)).collect(Collectors.toList());
      if (PowbSynthesisCrpStaffingCategory != null && !PowbSynthesisCrpStaffingCategory.isEmpty()) {
        return PowbSynthesisCrpStaffingCategory.get(0);
      } else {
        return null;
      }
    } else {
      return null;
    }
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
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      this.setPowbSynthesisIdHistory();
    } else {
      this.setPowbSynthesisParameters();
    }
    // Validate draft version
    if (powbSynthesis != null) {


      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        this.readJsonAndLoadPowbSynthesis(path);
      } else {
        this.setDraft(false);
        this.createEmptyCrpStaffing();
        powbSynthesis.setPowbSynthesisCrpStaffingCategoryList(powbSynthesis.getPowbSynthesisCrpStaffingCategory()
          .stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      }
    }
    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU") && c.isActive())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    powbCrpStaffingCategories = new ArrayList<>();
    powbCrpStaffingCategories =
      powbCrpStaffingCategoriesManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    if (this.isFlagship()) {
      PowbSynthesis powbSynthesisDB =
        powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());
      powbSynthesisID = powbSynthesisDB.getId();
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), powbSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.POWB_SYNTHESIS_CRPSTAFFING_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (powbSynthesis.getPowbSynthesisCrpStaffingCategoryList() != null) {
        powbSynthesis.getPowbSynthesisCrpStaffingCategoryList().clear();
      }
    }
  }

  private void readJsonAndLoadPowbSynthesis(Path path) throws IOException {
    BufferedReader reader = null;
    reader = new BufferedReader(new FileReader(path.toFile()));
    Gson gson = new GsonBuilder().create();
    JsonObject jReader = gson.fromJson(reader, JsonObject.class);
    AutoSaveReader autoSaveReader = new AutoSaveReader();
    // We read the JSON serialized by the front-end and cast it to the object
    powbSynthesis = (PowbSynthesis) autoSaveReader.readFromJson(jReader);
    powbSynthesisID = powbSynthesis.getId();
    this.setDraft(true);
    reader.close();
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      // CrpStaffing: staffing issues
      this.saveUpdateCrpStaffing();
      // CrpStaffing: Categories
      if (powbSynthesis.getPowbSynthesisCrpStaffingCategoryList() != null
        && !powbSynthesis.getPowbSynthesisCrpStaffingCategoryList().isEmpty()) {
        for (PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory : powbSynthesis
          .getPowbSynthesisCrpStaffingCategoryList()) {
          if (powbSynthesisCrpStaffingCategory.getId() == null) {
            this.saveNewCategory(powbSynthesisCrpStaffingCategory);
          } else {
            this.saveUpdateCategory(powbSynthesisCrpStaffingCategory);
          }
        }
      }

      List<String> relationsName = new ArrayList<>();
      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesis.setActiveSince(new Date());
      powbSynthesis.setModifiedBy(this.getCurrentUser());
      relationsName.add(APConstants.SYNTHESIS_CRP_STAFFING_CATEGORIES_RELATION);
      powbSynthesisManager.save(powbSynthesis, this.getActionName(), relationsName, this.getActualPhase());
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }
      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
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

  private void saveNewCategory(PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory) {
    PowbSynthesisCrpStaffingCategory newPowbCrpStaffingCategories = new PowbSynthesisCrpStaffingCategory();
    newPowbCrpStaffingCategories.setActive(true);
    newPowbCrpStaffingCategories.setCreatedBy(this.getCurrentUser());
    newPowbCrpStaffingCategories.setModifiedBy(this.getCurrentUser());
    newPowbCrpStaffingCategories.setActiveSince(new Date());
    newPowbCrpStaffingCategories.setPowbSynthesis(powbSynthesis);
    newPowbCrpStaffingCategories
      .setPowbCrpStaffingCategory(powbSynthesisCrpStaffingCategory.getPowbCrpStaffingCategory());
    if (powbSynthesisCrpStaffingCategory.getFemale() != null) {
      newPowbCrpStaffingCategories.setFemale(powbSynthesisCrpStaffingCategory.getFemale());
    } else {
      newPowbCrpStaffingCategories.setFemale(0.0);
    }
    if (powbSynthesisCrpStaffingCategory.getMale() != null) {
      newPowbCrpStaffingCategories.setMale(powbSynthesisCrpStaffingCategory.getMale());
    } else {
      newPowbCrpStaffingCategories.setMale(0.0);
    }
    if (powbSynthesisCrpStaffingCategory.getMaleNoCgiar() != null) {
      newPowbCrpStaffingCategories.setMaleNoCgiar(powbSynthesisCrpStaffingCategory.getMaleNoCgiar());
    } else {
      newPowbCrpStaffingCategories.setMaleNoCgiar(0.0);
    }
    if (powbSynthesisCrpStaffingCategory.getFemaleNoCgiar() != null) {
      newPowbCrpStaffingCategories.setFemaleNoCgiar(powbSynthesisCrpStaffingCategory.getFemaleNoCgiar());
    } else {
      newPowbCrpStaffingCategories.setFemaleNoCgiar(0.0);
    }
    newPowbCrpStaffingCategories =
      powbSynthesisCrpStaffingCategoryManager.savePowbSynthesisCrpStaffingCategory(newPowbCrpStaffingCategories);
  }


  private void saveUpdateCategory(PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory) {
    PowbSynthesisCrpStaffingCategory powbCrpStaffingCategories = powbSynthesisCrpStaffingCategoryManager
      .getPowbSynthesisCrpStaffingCategoryById(powbSynthesisCrpStaffingCategory.getId());
    powbCrpStaffingCategories.setActive(true);
    powbCrpStaffingCategories.setModifiedBy(this.getCurrentUser());
    powbCrpStaffingCategories.setActiveSince(new Date());
    if (powbSynthesisCrpStaffingCategory.getFemale() != null) {
      powbCrpStaffingCategories.setFemale(powbSynthesisCrpStaffingCategory.getFemale());
    } else {
      powbCrpStaffingCategories.setFemale(0.0);
    }
    if (powbSynthesisCrpStaffingCategory.getMale() != null) {
      powbCrpStaffingCategories.setMale(powbSynthesisCrpStaffingCategory.getMale());
    } else {
      powbCrpStaffingCategories.setMale(0.0);
    }
    if (powbSynthesisCrpStaffingCategory.getMaleNoCgiar() != null) {
      powbCrpStaffingCategories.setMaleNoCgiar(powbSynthesisCrpStaffingCategory.getMaleNoCgiar());
    } else {
      powbCrpStaffingCategories.setMaleNoCgiar(0.0);
    }
    if (powbSynthesisCrpStaffingCategory.getFemaleNoCgiar() != null) {
      powbCrpStaffingCategories.setFemaleNoCgiar(powbSynthesisCrpStaffingCategory.getFemaleNoCgiar());
    } else {
      powbCrpStaffingCategories.setFemaleNoCgiar(0.0);
    }
    powbCrpStaffingCategories =
      powbSynthesisCrpStaffingCategoryManager.savePowbSynthesisCrpStaffingCategory(powbCrpStaffingCategories);
  }

  private void saveUpdateCrpStaffing() {
    PowbCrpStaffing PowbCrpStaffingDB = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID).getCrpStaffing();
    if (PowbCrpStaffingDB.getId() == null) {
      PowbCrpStaffingDB.setId(powbSynthesisID);
    }
    PowbCrpStaffingDB.setActiveSince(new Date());
    PowbCrpStaffingDB.setModifiedBy(this.getCurrentUser());
    PowbCrpStaffingDB.setStaffingIssues(powbSynthesis.getCrpStaffing().getStaffingIssues());
    PowbCrpStaffingDB = powbCrpStaffingManager.savePowbCrpStaffing(PowbCrpStaffingDB);
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
      List<LiaisonInstitution> pmuList = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU") && c.isActive())
        .collect(Collectors.toList());
      if (pmuList != null && !pmuList.isEmpty()) {
        liaisonInstitutionID = pmuList.get(0).getId();
      }
    }
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPowbCrpStaffingCategories(List<PowbCrpStaffingCategories> powbCrpStaffingCategories) {
    this.powbCrpStaffingCategories = powbCrpStaffingCategories;
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
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    } else {
      this.transaction = null;
      this.setTransaction("-1");
    }
  }

  private void setPowbSynthesisIdParameter() {
    List<LiaisonInstitution> pmuList = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.getAcronym().equals("PMU") && c.isActive())
      .collect(Collectors.toList());
    if (pmuList != null && !pmuList.isEmpty()) {
      Long liaisonInstitutionID = pmuList.get(0).getId();
      PowbSynthesis powbSynthesis =
        powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
      if (powbSynthesis != null) {
        powbSynthesisID = powbSynthesis.getId();
      } else {
        powbSynthesis = this.createPowbSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
        powbSynthesisID = powbSynthesis.getId();
      }
    }
  }

  private void setPowbSynthesisParameters() {
    this.setLiaisonInstitutionIdParameter();
    this.setPowbSynthesisIdParameter();
    liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, powbSynthesis, true);
    }
  }

}
