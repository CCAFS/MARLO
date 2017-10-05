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

package org.cgiar.ccafs.marlo.action.synthesis;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpIndicatorReportManager;
import org.cgiar.ccafs.marlo.data.manager.CrpIndicatorTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorReport;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorType;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class CrpIndicatorsAction extends BaseAction {


  private static final long serialVersionUID = -1417643537265271428L;


  private CrpManager crpManager;

  private IpLiaisonInstitutionManager liaisonInstitutionManager;


  private CrpIndicatorReportManager indicatorsReportManager;

  private CrpIndicatorTypeManager crpIndicatorTypeManager;


  // Model for the front-end
  private List<IpLiaisonInstitution> liaisonInstitutions;

  private List<CrpIndicatorReport> indicatorReports;
  private String transaction;
  private AuditLogManager auditLogManager;

  private List<CrpIndicatorType> indicatorsType;
  private UserManager userManager;

  private IpLiaisonInstitution currentLiaisonInstitution;


  private Long liaisonInstitutionID;
  private IpProgramManager ipProgramManager;

  private Long indicatorTypeID;


  private Crp loggedCrp;

  private CrpIndicatorsValidator validator;

  @Inject
  public CrpIndicatorsAction(APConfig config, CrpManager crpManager, CrpIndicatorReportManager indicatorsReportManager,
    IpLiaisonInstitutionManager liaisonInstitutionManager, CrpIndicatorTypeManager crpIndicatorTypeManager,
    CrpIndicatorsValidator validator, AuditLogManager auditLogManager, IpProgramManager ipProgramManager,
    UserManager userManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.indicatorsReportManager = indicatorsReportManager;
    this.validator = validator;
    this.ipProgramManager = ipProgramManager;
    this.auditLogManager = auditLogManager;
    this.crpIndicatorTypeManager = crpIndicatorTypeManager;
    this.userManager = userManager;
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

  private Path getAutoSaveFilePath() {
    String composedClassName = currentLiaisonInstitution.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = currentLiaisonInstitution.getId() + "_" + composedClassName + "_" + loggedCrp.getAcronym()
      + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<CrpIndicatorReport> getCrpIndicatorsByType(long type) {
    List<CrpIndicatorReport> lst = new ArrayList<CrpIndicatorReport>();
    for (CrpIndicatorReport indicatorReport : currentLiaisonInstitution.getIndicatorReports()) {
      long indType = indicatorReport.getCrpIndicator().getCrpIndicatorType().getId();
      if (indType == type) {
        lst.add(indicatorReport);
      }
    }
    return lst;

  }

  public IpLiaisonInstitution getCurrentLiaisonInstitution() {
    return currentLiaisonInstitution;
  }

  public int getIndicatorIndex(long id, long type) {
    int c = 0;
    for (CrpIndicatorReport indicatorReport : currentLiaisonInstitution.getIndicatorReports()) {
      if (indicatorReport.getCrpIndicator().getId() == id
        && indicatorReport.getCrpIndicator().getCrpIndicatorType().getId() == type) {
        return c;
      }
      c++;
    }
    return -1;
  }

  public List<CrpIndicatorReport> getIndicatorReports() {
    return indicatorReports;
  }

  public List<CrpIndicatorType> getIndicatorsType() {
    return indicatorsType;
  }

  public Long getIndicatorTypeID() {
    return indicatorTypeID;
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

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

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


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      IpLiaisonInstitution history = (IpLiaisonInstitution) auditLogManager.getHistory(transaction);

      if (history != null) {
        currentLiaisonInstitution = history;
        liaisonInstitutionID = currentLiaisonInstitution.getId();

        currentLiaisonInstitution.setIndicatorReports((currentLiaisonInstitution.getCrpIndicatorReportses().stream()
          .filter(c -> c.getYear() == this.getCurrentCycleYear()).collect(Collectors.toList())));
        boolean isFP = false;
        if (currentLiaisonInstitution.getIpProgram() != null) {
          IpProgram ipProgram = ipProgramManager.getIpProgramById(currentLiaisonInstitution.getIpProgram().longValue());
          if (ipProgram.isFlagshipProgram()) {
            isFP = true;
          }
        }
        if (!isFP) {
          List<CrpIndicatorReport> crpIndicatorReports = new ArrayList<>();
          crpIndicatorReports.addAll(currentLiaisonInstitution.getIndicatorReports());
          for (CrpIndicatorReport crpIndicatorReport : crpIndicatorReports) {
            switch (crpIndicatorReport.getCrpIndicator().getId().intValue()) {
              case 1:
              case 2:
              case 3:
              case 4:
              case 5:
              case 6:
                currentLiaisonInstitution.getIndicatorReports().remove(crpIndicatorReport);
                break;

              default:
                break;
            }
          }
        }

        currentLiaisonInstitution.getIndicatorReports()
          .sort((p1, p2) -> p1.getCrpIndicator().getId().compareTo(p2.getCrpIndicator().getId()));
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {

      currentLiaisonInstitution = liaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);
    }
    try {
      indicatorTypeID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.INDICATOR_TYPE_REQUEST_ID)));
    } catch (NumberFormatException e) {
      indicatorTypeID = new Long(1);
    }


    if (currentLiaisonInstitution != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
 	      reader.close();
 	

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        currentLiaisonInstitution = (IpLiaisonInstitution) autoSaveReader.readFromJson(jReader);

        liaisonInstitutionID = currentLiaisonInstitution.getId();

        this.setDraft(true);
      
      } else {

        if (transaction == null) {
          indicatorReports =
            indicatorsReportManager.getIndicatorReportsList(liaisonInstitutionID, this.getCurrentCycleYear());

          currentLiaisonInstitution.setIndicatorReports(indicatorReports);
        }

        boolean isFP = false;
        if (currentLiaisonInstitution.getIpProgram() != null) {
          IpProgram ipProgram = ipProgramManager.getIpProgramById(currentLiaisonInstitution.getIpProgram().longValue());
          if (ipProgram.isFlagshipProgram()) {
            isFP = true;
          }
        }
        if (!isFP) {

          List<CrpIndicatorReport> crpIndicatorReports = new ArrayList<>();
          crpIndicatorReports.addAll(currentLiaisonInstitution.getIndicatorReports());
          for (CrpIndicatorReport crpIndicatorReport : crpIndicatorReports) {
            switch (crpIndicatorReport.getCrpIndicator().getId().intValue()) {
              case 1:
              case 2:
              case 3:
              case 4:
              case 5:
              case 6:
                currentLiaisonInstitution.getIndicatorReports().remove(crpIndicatorReport);
                break;

              default:
                break;
            }
          }

        }

        this.setDraft(false);
      }
    }


    // Get the list of liaison institutions.
    liaisonInstitutions = liaisonInstitutionManager.getLiaisonInstitutionsCrpsIndicator();
    indicatorsType = new ArrayList<>(crpIndicatorTypeManager.findAll());
    String params[] = {loggedCrp.getAcronym(), currentLiaisonInstitution.getId() + ""};
    this.setBasePermission(this.getText(Permission.CRP_INDICATORS_BASE_PERMISSION, params));


  }


  @Override
  public String save() {
    IpLiaisonInstitution ipLiaisonInstitution =
      liaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);

    for (CrpIndicatorReport crpIndicatorReport : currentLiaisonInstitution.getIndicatorReports()) {
      crpIndicatorReport.setIpLiaisonInstitution(ipLiaisonInstitution);
      crpIndicatorReport.setLastUpdate(new Date());
      indicatorsReportManager.saveCrpIndicatorReport(crpIndicatorReport);
    }

    currentLiaisonInstitution = liaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);
    currentLiaisonInstitution.setActiveSince(new Date());
    currentLiaisonInstitution.setModifiedBy(this.getCurrentUser());

    List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.IPLIAISON_INDICATORS_REPORT);
    liaisonInstitutionManager.save(currentLiaisonInstitution, this.getActionName(), relationsName);

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
    }


    Collection<String> messages = this.getActionMessages();
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

  }

  public void setCurrentLiaisonInstitution(IpLiaisonInstitution currentLiaisonInstitution) {
    this.currentLiaisonInstitution = currentLiaisonInstitution;
  }

  public void setIndicatorReports(List<CrpIndicatorReport> indicatorReports) {
    this.indicatorReports = indicatorReports;
  }

  public void setIndicatorsType(List<CrpIndicatorType> indicatorsType) {
    this.indicatorsType = indicatorsType;
  }

  public void setIndicatorTypeID(Long indicatorTypeID) {
    this.indicatorTypeID = indicatorTypeID;
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
