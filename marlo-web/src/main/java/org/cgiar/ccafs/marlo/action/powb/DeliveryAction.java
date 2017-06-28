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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.sythesis.CrpIndicatorsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sebastian Amariles Garcia - CIAT/CCAFS
 */
public class DeliveryAction extends BaseAction {


  private static final long serialVersionUID = -1417643537265271428L;

  // Managers
  private CrpManager crpManager;
  private IpLiaisonInstitutionManager liaisonInstitutionManager;

  // Model for the front-end
  private List<IpLiaisonInstitution> liaisonInstitutions;
  private String transaction;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private IpLiaisonInstitution currentLiaisonInstitution;
  private Long liaisonInstitutionID;
  private IpProgramManager ipProgramManager;
  private Crp loggedCrp;
  private CrpIndicatorsValidator validator;

  @Inject
  public DeliveryAction(APConfig config, CrpManager crpManager, IpLiaisonInstitutionManager liaisonInstitutionManager,
    CrpIndicatorsValidator validator, AuditLogManager auditLogManager, IpProgramManager ipProgramManager,
    UserManager userManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.validator = validator;
    this.ipProgramManager = ipProgramManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
  }

  @Override
  public String cancel() {
    return SUCCESS;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = currentLiaisonInstitution.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = currentLiaisonInstitution.getId() + "_" + composedClassName + "_" + loggedCrp.getAcronym()
      + "_powb_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public IpLiaisonInstitution getCurrentLiaisonInstitution() {
    return currentLiaisonInstitution;
  }

  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }

  public List<IpLiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public String getTransaction() {
    return transaction;
  }

  public boolean isFlagship() {
    boolean isFP = false;
    if (currentLiaisonInstitution.getIpProgram() != null) {
      IpProgram ipProgram = ipProgramManager.getIpProgramById(currentLiaisonInstitution.getIpProgram().longValue());
      if (ipProgram.isFlagshipProgram()) {
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
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    // Get Liaison institution ID Parameter
    try {
      liaisonInstitutionID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (NumberFormatException e) {
      User user = userManager.getUser(this.getCurrentUser().getId());
      if (user.getIpLiaisonUsers() != null || !user.getIpLiaisonUsers().isEmpty()) {

        List<IpLiaisonUser> liaisonUsers = new ArrayList<>(user.getIpLiaisonUsers());

        if (!liaisonUsers.isEmpty()) {
          IpLiaisonUser liaisonUser = new IpLiaisonUser();
          liaisonUser = liaisonUsers.get(0);
          liaisonInstitutionID = liaisonUser.getIpLiaisonInstitution().getId();
          if (liaisonInstitutionID == 1) {
            liaisonInstitutionID = new Long(2);
          }
        } else {
          liaisonInstitutionID = new Long(2);
        }

      } else {
        liaisonInstitutionID = new Long(2);
      }
    }

    // If the URL doesn't has a liaisoninstitutionID
    if (currentLiaisonInstitution != null) {
      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        currentLiaisonInstitution = (IpLiaisonInstitution) autoSaveReader.readFromJson(jReader);
        liaisonInstitutionID = currentLiaisonInstitution.getId();
        this.setDraft(true);
        reader.close();
      } else {
        if (transaction == null) {
          // Do something
        }
        this.setDraft(false);
      }
    }

    // If there is a history version being loaded
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      IpLiaisonInstitution history = (IpLiaisonInstitution) auditLogManager.getHistory(transaction);
      if (history != null) {
        currentLiaisonInstitution = history;
        liaisonInstitutionID = currentLiaisonInstitution.getId();
        currentLiaisonInstitution.setIndicatorReports((currentLiaisonInstitution.getCrpIndicatorReportses().stream()
          .filter(c -> c.getYear() == this.getCurrentCycleYear()).collect(Collectors.toList())));
        currentLiaisonInstitution.getIndicatorReports()
          .sort((p1, p2) -> p1.getCrpIndicator().getId().compareTo(p2.getCrpIndicator().getId()));
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      currentLiaisonInstitution = liaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);
    }

    // Get the list of liaison institutions.
    // TODO: List only Flagships and the PMU
    liaisonInstitutions = liaisonInstitutionManager.getLiaisonInstitutionSynthesisByMog();
    String params[] = {loggedCrp.getAcronym(), currentLiaisonInstitution.getId() + ""};
    this.setBasePermission(this.getText(Permission.CRP_INDICATORS_BASE_PERMISSION, params));

  }

  @Override
  public String save() {
    return SUCCESS;
  }

  public void setCurrentLiaisonInstitution(IpLiaisonInstitution currentLiaisonInstitution) {
    this.currentLiaisonInstitution = currentLiaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<IpLiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, currentLiaisonInstitution.getIndicatorReports(), currentLiaisonInstitution, true);
    }
  }

}
