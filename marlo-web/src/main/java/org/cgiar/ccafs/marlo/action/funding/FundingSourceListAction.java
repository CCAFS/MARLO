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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
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
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.shiro.authz.AuthorizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FundingSourceListAction extends BaseAction {


  private static final long serialVersionUID = -8858893084495492581L;


  private final Logger logger = LoggerFactory.getLogger(FundingSourceListAction.class);

  private GlobalUnit loggedCrp;

  private List<FundingSource> myProjects;

  private FundingSourceManager fundingSourceManager;
  private Long cpCrpID;
  private FundingSourceInfoManager fundingSourceInfoManager;
  private RoleManager roleManager;
  private FundingSourceInstitutionManager fundingSourceInstitutionManager;
  private GlobalUnitManager crpManager;
  private LiaisonUserManager liaisonUserManager;
  private InstitutionManager institutionManager;
  private List<FundingSource> closedProjects;
  private List<FundingSourceInstitution> fundingSourceInstitutions;
  private UserRoleManager userRoleManager;
  private long fundingSourceID;
  private long fundingSourceInfoID;
  private String justification;


  @Inject
  public FundingSourceListAction(APConfig config, FundingSourceManager fundingSourceManager,
    GlobalUnitManager crpManager, ProjectManager projectManager, LiaisonUserManager liaisonUserManager,
    InstitutionManager institutionManager, FundingSourceInstitutionManager fundingSourceInstitutionManager,
    FundingSourceInfoManager fundingSourceInfoManager, RoleManager roleManager, UserRoleManager userRoleManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.liaisonUserManager = liaisonUserManager;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
    this.institutionManager = institutionManager;
    this.fundingSourceInfoManager = fundingSourceInfoManager;
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
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


      if (phase.getNext() != null) {
        phase = phase.getNext();
      } else {
        hasNext = false;
      }


    }


    LiaisonUser user = liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());
    if (user != null) {
      LiaisonInstitution liaisonInstitution = user.getLiaisonInstitution();
      try {
        if (liaisonInstitution != null && liaisonInstitution.getInstitution() != null) {
          Institution institution = institutionManager.getInstitutionById(liaisonInstitution.getInstitution().getId());

          FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();
          fundingSourceInstitution.setFundingSource(fundingSource);
          fundingSourceInstitution.setPhase(this.getActualPhase());
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

    // this.clearPermissionsCache();
    // HJ : add the permission String
    AuthorizationInfo info = ((APCustomRealm) this.securityContext.getRealm())
      .getAuthorizationInfo(this.securityContext.getSubject().getPrincipals());


    String params[] = {loggedCrp.getAcronym(), fundingSource.getId() + ""};
    info.getStringPermissions().add(this.generatePermission(Permission.PROJECT_FUNDING_SOURCE_BASE_PERMISSION, params));

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

  public List<FundingSource> getClosedProjects() {
    return closedProjects;
  }

  public Long getCpCrpID() {
    return cpCrpID;
  }

  public void getCrpContactPoint() {

    // Check if the CRP has Contact Point and ContactPointRole, if not cpRole will be null (it will be used as a flag)

    Role cpRol = null;
    if (this.hasSpecificities(APConstants.CRP_HAS_CP)
      && roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE))) != null) {
      cpRol = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE)));
    }

    List<Role> roles = new ArrayList<>();
    roles = this.getRolesList();

    if (roles.contains(cpRol)) {
      cpCrpID = cpRol.getCrp().getId();
    } else {
      cpCrpID = (long) 0;
    }
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


  public List<FundingSourceInstitution> getFundingSourceInstitutions() {
    return fundingSourceInstitutions;
  }

  // public void getBudgetAmount(long fundingsourceID){
  //
  // FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingsourceID);
  //
  // List<FundingSourceBudget> fundingSourceBudgets = new
  // ArrayList<>(fundingSource.getFundingSourceBudgets().stream().filter(fb -> fb.isActive() &&
  // fb.getPhase().getId().equals(this)));
  //
  //
  // }


  public void getFundingSourceInstitutionsList() {
    List<String> institutionsName = new ArrayList<String>();
    if (myProjects != null) {
      for (FundingSource fundingSource : myProjects) {
        if (fundingSource.getInstitutions() != null) {
          for (FundingSourceInstitution institution : fundingSource.getInstitutions()) {

            // funding source institutions cycle
            if (fundingSourceInstitutions != null) {
              // if the list of funding source institutions has elements, check the acronym and/or the name of
              // institution
              if (institution.getInstitution().getAcronym() != null) {
                if (!institutionsName.contains(institution.getInstitution().getAcronym())) {
                  institutionsName.add(institution.getInstitution().getAcronym());
                  fundingSourceInstitutions.add(institution);
                }
              } else if (institution.getInstitution().getName() != null) {
                if (!institutionsName.contains(institution.getInstitution().getName())) {
                  institutionsName.add(institution.getInstitution().getName());
                  fundingSourceInstitutions.add(institution);
                }
              }
            } else {
              // if the list of the funding source institutions to send to front is empty
              fundingSourceInstitutions.add(institution);
            }
          }
        }
      }
    }
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

  public List<FundingSource> getMyProjects() {
    return myProjects;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    Set<Integer> statusTypes = new HashSet<>();
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Ongoing.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Extended.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Pipeline.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Informally.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Complete.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Cancelled.getStatusId()));


    List<FundingSource> allFundingSources =
      fundingSourceManager.getGlobalUnitFundingSourcesByPhaseAndTypes(loggedCrp, this.getActualPhase(), statusTypes);

    /*
     * List<FundingSource> allFundingSources = fundingSourceManager
     * .getGlobalUnitFundingSourcesByPhaseAndTypesWithoutInstitutions(loggedCrp, this.getActualPhase(), statusTypes);
     */

    this.myProjects = new ArrayList<>();
    this.closedProjects = new ArrayList<>();
    this.fundingSourceInstitutions = new ArrayList<>();

    if (allFundingSources != null && allFundingSources.size() > 0) {

      for (FundingSource fundingSource : allFundingSources) {

        fundingSource.getFundingSourceInfo(this.getActualPhase());
        fundingSource.setInstitutions(fundingSource.getFundingSourceInstitutions().stream()
          .filter(fsi -> fsi.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
        if (fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
          .parseInt(FundingStatusEnum.Ongoing.getStatusId())
          || fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
            .parseInt(FundingStatusEnum.Extended.getStatusId())
          || fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
            .parseInt(FundingStatusEnum.Pipeline.getStatusId())
          || fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
            .parseInt(FundingStatusEnum.Informally.getStatusId())) {
          myProjects.add(fundingSource);
        }

        if (fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
          .parseInt(FundingStatusEnum.Cancelled.getStatusId())
          || fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
            .parseInt(FundingStatusEnum.Complete.getStatusId())) {
          closedProjects.add(fundingSource);
        }
      }
    }

    this.getCrpContactPoint();
    this.getFundingSourceInstitutionsList();
  }

  public void setClosedProjects(List<FundingSource> closedProjects) {
    this.closedProjects = closedProjects;
  }


  public void setCpCrpID(Long cpRoleID) {
    this.cpCrpID = cpRoleID;
  }

  public void setFundingSourceID(long projectID) {
    this.fundingSourceID = projectID;
  }


  public void setFundingSourceInstitutions(List<FundingSourceInstitution> fundingSourceInstitutions) {
    this.fundingSourceInstitutions = fundingSourceInstitutions;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<FundingSource> myProjects) {
    this.myProjects = myProjects;
  }


}