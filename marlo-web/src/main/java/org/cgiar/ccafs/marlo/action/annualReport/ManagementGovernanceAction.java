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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisGovernanceManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.annualreport.GovernanceValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ManagementGovernanceAction extends BaseAction {


  private static final long serialVersionUID = 1087968306297693209L;


  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportbSynthesisManager;

  private AuditLogManager auditLogManager;


  private UserManager userManager;


  private CrpProgramManager crpProgramManager;


  private ReportSynthesisGovernanceManager reportSynthesisGovernanceManager;


  private GovernanceValidator validator;


  private String pmuText;


  // Variables
  private String transaction;


  private ReportSynthesis reportSynthesis;


  private Long liaisonInstitutionID;

  private Long synthesisID;


  private LiaisonInstitution liaisonInstitution;


  private GlobalUnit loggedCrp;


  private List<LiaisonInstitution> liaisonInstitutions;


  @Inject
  public ManagementGovernanceAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportbSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    ReportSynthesisGovernanceManager reportSynthesisGovernanceManager, GovernanceValidator validator) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportbSynthesisManager = reportbSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisGovernanceManager = reportSynthesisGovernanceManager;
    this.validator = validator;
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
        reportSynthesis = reportbSynthesisManager.getReportSynthesisById(synthesisID);

        if (!powbSynthesis.getPhase().equals(phase)) {
          powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
          if (powbSynthesis == null) {
            powbSynthesis = this.createPowbSynthesis(phase.getId(), liaisonInstitutionID);
          }
          powbSynthesisID = powbSynthesis.getId();
        }
      } catch (Exception e) {

        powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
        if (powbSynthesis == null) {
          powbSynthesis = this.createPowbSynthesis(phase.getId(), liaisonInstitutionID);
        }
        powbSynthesisID = powbSynthesis.getId();

      }
    }
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


  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

}
