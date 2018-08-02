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


package org.cgiar.ccafs.marlo.action.funding;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceSummary;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FundingSourceListAction extends BaseAction {


  private static final long serialVersionUID = -8858893084495492581L;


  private final Logger logger = LoggerFactory.getLogger(FundingSourceListAction.class);

  private GlobalUnit loggedCrp;

  private List<FundingSourceSummary> myProjects;

  private FundingSourceManager fundingSourceManager;


  private FundingSourceInfoManager fundingSourceInfoManager;

  private FundingSourceInstitutionManager fundingSourceInstitutionManager;

  private GlobalUnitManager crpManager;


  private RoleManager roleManager;
  private LiaisonUserManager liaisonUserManager;
  private InstitutionManager institutionManager;
  private List<FundingSourceSummary> closedProjects;
  private long fundingSourceID;


  private long fundingSourceInfoID;


  private String justification;


  @Inject
  public FundingSourceListAction(APConfig config, RoleManager roleManager, FundingSourceManager fundingSourceManager,
    GlobalUnitManager crpManager, ProjectManager projectManager, LiaisonUserManager liaisonUserManager,
    InstitutionManager institutionManager, FundingSourceInstitutionManager fundingSourceInstitutionManager,
    FundingSourceInfoManager fundingSourceInfoManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.roleManager = roleManager;
    this.liaisonUserManager = liaisonUserManager;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
    this.institutionManager = institutionManager;
    this.fundingSourceInfoManager = fundingSourceInfoManager;
  }

  @Override
  public String add() {
    FundingSource fundingSource = new FundingSource();

    fundingSource.setCrp(loggedCrp);
    fundingSource.setCreateDate(new Date());
    fundingSource = fundingSourceManager.saveFundingSource(fundingSource);

    fundingSourceID = fundingSource.getId();

    Phase phase = this.getActualPhase();
    boolean hasNext = true;
    while (hasNext) {


      FundingSourceInfo fundingSourceInfo = new FundingSourceInfo();
      fundingSourceInfo.setModificationJustification("New expected project bilateral cofunded created");
      fundingSourceInfo.setPhase(phase);
      fundingSourceInfo.setStatus(Integer.parseInt(FundingStatusEnum.Ongoing.getStatusId()));
      fundingSourceInfo.setFundingSource(fundingSourceManager.getFundingSourceById(fundingSourceID));
      fundingSourceInfoID = fundingSourceInfoManager.saveFundingSourceInfo(fundingSourceInfo).getId();


      LiaisonUser user = liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());
      if (user != null) {
        LiaisonInstitution liaisonInstitution = user.getLiaisonInstitution();
        try {
          if (liaisonInstitution != null && liaisonInstitution.getInstitution() != null) {
            Institution institution =
              institutionManager.getInstitutionById(liaisonInstitution.getInstitution().getId());

            FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();
            fundingSourceInstitution.setFundingSource(fundingSource);
            fundingSourceInstitution.setPhase(phase);
            fundingSourceInstitution.setInstitution(institution);
            fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);

          }
        } catch (Exception e) {
          logger.error("unable to save FundingSourceInstitution", e);
          /**
           * Original code swallows the exception and didn't even log it. Now we at least log it,
           * but we need to revisit to see if we should continue processing or re-throw the exception.
           */
        }


      }
      if (phase.getNext() != null) {
        phase = phase.getNext();
      } else {
        hasNext = false;
      }


    }

    this.clearPermissionsCache();
    if (fundingSourceID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }

  public boolean canAddFunding() {
    boolean permission = this.hasPermissionNoBase(
      this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()))

      || this.hasPermissionNoBase(
        this.generatePermission(Permission.PROJECT_FUNDING_W3_BASE_PERMISSION, loggedCrp.getAcronym()))

      || this.hasPermissionNoBase(
        this.generatePermission(Permission.FUNDING_SOURCE_EDIT_PERMISSION, loggedCrp.getAcronym()));

    return permission && !this.isReportingActive();
  }


  @Override
  public String delete() {
    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);
    FundingSourceInfo fundingSourceInfo = fundingSource.getFundingSourceInfo(this.getActualPhase());
    logger.info("Deleting fundingSource with  id: " + fundingSourceID);
    fundingSourceInfo.setModificationJustification(justification);

    fundingSource = fundingSourceManager.saveFundingSource(fundingSource);

    try {
      fundingSourceManager.deleteFundingSource(fundingSource.getId());
      logger.info("Deleted fundingSource with id: " + fundingSource);
      this.addActionMessage(
        "message:" + this.getText("deleting.success", new String[] {this.getText("fundingSource").toLowerCase()}));
    } catch (Exception e) {

      logger.error("Unable to delete fundingSource with id: " + fundingSource + " , with exception " + e);
      this.addActionError(this.getText("deleting.problem", new String[] {this.getText("fundingSource").toLowerCase()}));
    }

    return SUCCESS;
  }

  public List<FundingSourceSummary> getClosedProjects() {
    return closedProjects;
  }

  public Double getFundingSourceBudgetPerPhase(Long fundingSourceId) {
    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceId);
    List<FundingSourceBudget> fundingSourceBudgets =
      fundingSource.getFundingSourceBudgets().stream()
        .filter(fsb -> fsb.isActive() && fsb.getPhase() != null && fsb.getPhase().equals(this.getActualPhase())
          && fsb.getYear() != null && fsb.getYear().equals(this.getActualPhase().getYear()))
        .collect(Collectors.toList());
    if (fundingSourceBudgets != null && fundingSourceBudgets.size() > 0) {
      FundingSourceBudget fundingSourceBudget = fundingSourceBudgets.get(0);
      if (fundingSourceBudget != null) {
        return fundingSourceBudget.getBudget();
      }
    }
    return 0.0;
  }

  public long getFundingSourceID() {
    return fundingSourceID;
  }

  /**
   * Migrated from the BaseAction. Leaving this in here as the call to the fundingSourceValidator
   * may be required in a situation that I am not aware of.
   */
  // private boolean getFundingSourceStatus(FundingSource fundingSource) {
  // fundingSource.setFundingSourceInfo(fundingSource.getFundingSourceInfo(this.getActualPhase()));
  // if (fundingSource.getFundingSourceInfo(this.getActualPhase()) != null) {
  // List<SectionStatus> sectionStatuses = fundingSource.getSectionStatuses().stream()
  // .filter(c -> c.getCycle().equals(this.getActualPhase().getDescription())
  // && c.getYear() == this.getActualPhase().getYear())
  //
  // .collect(Collectors.toList());
  //
  // if (!sectionStatuses.isEmpty()) {
  // SectionStatus sectionStatus = sectionStatuses.get(0);
  // return sectionStatus.getMissingFields().length() == 0
  // && !this.getAutoSaveFilePath(fundingSource.getClass().getSimpleName(),
  // ProjectSectionStatusEnum.FUNDINGSOURCE.getStatus(), fundingSource.getId());
  //
  // } else {
  //
  // fundingSourceValidator.validate(this, fundingSource, false);
  // return this.getFundingSourceStatus(fundingSource);
  // }
  // } else {
  // return false;
  // }
  //
  // }
  //
  // public boolean getFundingSourceStatus(long fundingSourceID) {
  // FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);
  // return this.getFundingSourceStatus(fundingSource);
  // }

  @Override
  public String getJustification() {
    return justification;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<FundingSourceSummary> getMyProjects() {
    return myProjects;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    // if (fundingSourceManager.findAll() != null) {

    Role role = roleManager.getRoleById(Long.parseLong(this.getSession().get(APConstants.CRP_PMU_ROLE).toString()));
    boolean isPMU = !role.getUserRoles().stream()
      .filter(c -> c.getUser().getId().longValue() == this.getCurrentUser().getId().longValue())
      .collect(Collectors.toList()).isEmpty();
    if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin() || isPMU) {

      // TODO confirm with the MARLO team in Cali, that nonPmu users can also view the list of fundingSources.
      this.myProjects = fundingSourceManager.getOngoingFundingSourceSummaries(loggedCrp, this.getActualPhase());


    } else {

      this.myProjects = fundingSourceManager.getOngoingFundingSourceSummaries(loggedCrp, this.getActualPhase());

    }

    this.closedProjects = fundingSourceManager.getClosedFundingSourceSummaries(loggedCrp, this.getActualPhase());

  }

  public void setClosedProjects(List<FundingSourceSummary> closedProjects) {
    this.closedProjects = closedProjects;
  }


  public void setFundingSourceID(long projectID) {
    this.fundingSourceID = projectID;
  }


  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<FundingSourceSummary> myProjects) {
    this.myProjects = myProjects;
  }

}