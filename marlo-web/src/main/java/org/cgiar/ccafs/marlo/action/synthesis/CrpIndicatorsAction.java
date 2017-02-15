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
import org.cgiar.ccafs.marlo.data.manager.CrpIndicatorReportManager;
import org.cgiar.ccafs.marlo.data.manager.CrpIndicatorTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorReport;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorType;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.sythesis.CrpIndicatorsValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
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


  private List<CrpIndicatorType> indicatorsType;

  private IpLiaisonInstitution currentLiaisonInstitution;


  private Long liaisonInstitutionID;


  private Long indicatorTypeID;


  private Crp loggedCrp;

  private CrpIndicatorsValidator validator;

  @Inject
  public CrpIndicatorsAction(APConfig config, CrpManager crpManager, CrpIndicatorReportManager indicatorsReportManager,
    IpLiaisonInstitutionManager liaisonInstitutionManager, CrpIndicatorTypeManager crpIndicatorTypeManager,
    CrpIndicatorsValidator validator) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.indicatorsReportManager = indicatorsReportManager;
    this.validator = validator;
    this.crpIndicatorTypeManager = crpIndicatorTypeManager;
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
    for (CrpIndicatorReport indicatorReport : indicatorReports) {
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

      if (this.getCurrentUser().getIpLiaisonUsers() != null || !this.getCurrentUser().getIpLiaisonUsers().isEmpty()) {

        List<IpLiaisonUser> liaisonUsers = new ArrayList<>(this.getCurrentUser().getIpLiaisonUsers());

        if (!liaisonUsers.isEmpty()) {
          LiaisonUser liaisonUser = new LiaisonUser();
          liaisonUser = new ArrayList<>(this.getCurrentUser().getLiasonsUsers()).get(0);
          liaisonInstitutionID = liaisonUser.getLiaisonInstitution().getId();
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

    currentLiaisonInstitution = liaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);

    try {
      indicatorTypeID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.INDICATOR_TYPE_REQUEST_ID)));
    } catch (NumberFormatException e) {
      indicatorTypeID = new Long(1);
    }


    indicatorReports =
      indicatorsReportManager.getIndicatorReportsList(liaisonInstitutionID, this.getCurrentCycleYear());

    currentLiaisonInstitution.setIndicatorReports(indicatorReports);

    // Get the list of liaison institutions.
    liaisonInstitutions = liaisonInstitutionManager.getLiaisonInstitutionsCrpsIndicator();
    indicatorsType = new ArrayList<>(crpIndicatorTypeManager.findAll());

  }


  @Override
  public String save() {
    IpLiaisonInstitution ipLiaisonInstitution =
      liaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);

    for (CrpIndicatorReport crpIndicatorReport : currentLiaisonInstitution.getIndicatorReports()) {
      crpIndicatorReport.setIpLiaisonInstitution(ipLiaisonInstitution);
      indicatorsReportManager.saveCrpIndicatorReport(crpIndicatorReport);
    }

    currentLiaisonInstitution = liaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);
    currentLiaisonInstitution.setActiveSince(new Date());
    currentLiaisonInstitution.setModifiedBy(this.getCurrentUser());

    List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.IPLIAISON_INDICATORS_REPORT);
    liaisonInstitutionManager.save(ipLiaisonInstitution, this.getActionName(), relationsName);

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

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, currentLiaisonInstitution.getIndicatorReports(), currentLiaisonInstitution, true);
    }
  }
}
