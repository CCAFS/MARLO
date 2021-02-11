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
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceLocationsManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceDivision;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.struts2.dispatcher.Parameter;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FundingSourceListAction extends BaseAction {


  private static final long serialVersionUID = -8858893084495492581L;


  private final Logger logger = LoggerFactory.getLogger(FundingSourceListAction.class);

  private GlobalUnit loggedCrp;

  private List<FundingSource> myProjects;
  private String institutionsIDs;
  private String institutionsIDsFilter;
  private List<String> institutionsIDsList;
  private List<String> partnertsIDList;
  private FundingSourceManager fundingSourceManager;

  private List<Institution> filteredInstitutions;
  private List<FundingSourceInstitution> ins;
  private List<FundingSourceInstitution> institutionFSFiltered;
  private Role cpRole;
  private FundingSourceInfoManager fundingSourceInfoManager;
  private FundingSourceBudgetManager fundingSourceBudgetManager;
  private RoleManager roleManager;
  private FundingSourceInstitutionManager fundingSourceInstitutionManager;
  private GlobalUnitManager crpManager;
  private LiaisonUserManager liaisonUserManager;
  private InstitutionManager institutionManager;
  private CrpPpaPartnerManager crpPpaPartnerManager;
  private GlobalUnitManager globalUnitManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private BudgetTypeManager budgetTypeManager;
  private FundingSourceDivisionManager fundingSourceDivisionManager;
  private FundingSourceLocationsManager fundingSourceLocationsManager;
  private PartnerDivisionManager partnerDivisionManager;
  private LocElementManager locElementManager;
  private LocElementTypeManager locElementTypeManager;

  private Map<String, String> budgetTypes;
  private List<FundingSource> closedProjects;
  private List<FundingSourceInstitution> fundingSourceInstitutions;
  private List<Institution> managingInstitutionsList;
  private List<LiaisonUser> contactsPoint;
  private List<Long> usersContactPoint;
  private Map<String, String> agreementStatus;
  private Map<String, String> agreementStatusModal;
  private UserRoleManager userRoleManager;
  private long fundingSourceID;
  private long fundingSourceInfoID;
  private String justification;
  private String financeCode;
  private Long centerID;
  private String agreementStatusValue;
  private String institutionLead;
  private String partnerIDs;
  private Long budgetTypeID;
  private boolean checkAllInstitutions;
  private FundingSource fundingSourceTemp;


  @Inject
  public FundingSourceListAction(APConfig config, FundingSourceManager fundingSourceManager,
    GlobalUnitManager crpManager, ProjectManager projectManager, LiaisonUserManager liaisonUserManager,
    InstitutionManager institutionManager, LiaisonInstitutionManager liaisonInstitutionManager,
    FundingSourceInstitutionManager fundingSourceInstitutionManager, FundingSourceInfoManager fundingSourceInfoManager,
    RoleManager roleManager, UserRoleManager userRoleManager, CrpPpaPartnerManager crpPpaPartnerManager,
    GlobalUnitManager globalUnitManager, BudgetTypeManager budgetTypeManager,
    FundingSourceBudgetManager fundingSourceBudgetManager, FundingSourceDivisionManager fundingSourceDivisionManager,
    FundingSourceLocationsManager fundingSourceLocationsManager, PartnerDivisionManager partnerDivisionManager,
    LocElementManager locElementManager, LocElementTypeManager locElementTypeManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.liaisonUserManager = liaisonUserManager;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
    this.institutionManager = institutionManager;
    this.fundingSourceInfoManager = fundingSourceInfoManager;
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.globalUnitManager = globalUnitManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.budgetTypeManager = budgetTypeManager;
    this.fundingSourceBudgetManager = fundingSourceBudgetManager;
    this.fundingSourceDivisionManager = fundingSourceDivisionManager;
    this.fundingSourceLocationsManager = fundingSourceLocationsManager;
    this.partnerDivisionManager = partnerDivisionManager;
    this.locElementManager = locElementManager;
    this.locElementTypeManager = locElementTypeManager;
  }


  @Override
  public String add() {
    FundingSource fundingSource = new FundingSource();
    Map<String, Parameter> parameters = this.getParameters();
    try {
      financeCode = StringUtils.trim(parameters.get(APConstants.FINANCE_CODE).getMultipleValues()[0]);
      agreementStatusValue = StringUtils.trim(parameters.get(APConstants.AGREEMENT_STATUS).getMultipleValues()[0]);
      institutionLead = StringUtils.trim(parameters.get(APConstants.INSTITUTION_LEAD).getMultipleValues()[0]);
      // centerID = Long.parseLong(parameters.get(APConstants.CRP_ID).getMultipleValues()[0]);
      partnerIDs = StringUtils.trim(parameters.get(APConstants.PARTNERS_ID).getMultipleValues()[0]);
      budgetTypeID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.BUDGET_TYPE_REQUEST_ID).getMultipleValues()[0]));

    } catch (Exception e) {
      Log.error(e);
    }

    if (partnerIDs != null) {
      this.getPartnertsIDs();
    }

    if (financeCode != null && institutionLead != null) {

      FundingSource fundingSourceSearch = new FundingSource();
      List<FundingSource> fundingSourceSearchTemp = new ArrayList<FundingSource>();
      fundingSourceSearchTemp = null;
      /*
       * fundingSourceSearchTemp = fundingSourceManager.findAll().stream()
       * .filter(f -> f.getFundingSourceInfo(this.getActualPhase()) != null
       * && f.getFundingSourceInfo(this.getActualPhase()).getFinanceCode() != null
       * && f.getFundingSourceInfo(this.getActualPhase()).getFinanceCode().equals(financeCode))
       * .collect(Collectors.toList());
       */
      // if finance code does not exist
      fundingSource.setCrp(loggedCrp);
      fundingSource.setCreateDate(new Date());
      fundingSource = fundingSourceManager.saveFundingSource(fundingSource);

      fundingSourceID = fundingSource.getId();

      Phase phase = this.getActualPhase();
      boolean hasNext = true;
      Institution leadInstitution = new Institution();
      leadInstitution = institutionManager.getInstitutionById(Integer.parseInt(institutionLead));
      while (hasNext) {

        FundingSourceInfo fundingSourceInfo = new FundingSourceInfo();
        fundingSourceInfo.setModificationJustification("New expected project bilateral cofunded created");
        fundingSourceInfo.setPhase(phase);
        fundingSourceInfo.setFinanceCode(financeCode);
        fundingSourceInfo.setLeadCenter(leadInstitution);
        fundingSourceInfo.setStatus(Integer.parseInt(agreementStatusValue));
        fundingSourceInfo.setFundingSource(fundingSourceManager.getFundingSourceById(fundingSourceID));
        fundingSourceInfoID = fundingSourceInfoManager.saveFundingSourceInfo(fundingSourceInfo).getId();
        if (budgetTypeID != null) {
          BudgetType budgetType;
          budgetType = budgetTypeManager.getBudgetTypeById(budgetTypeID);
          if (budgetType != null) {
            fundingSourceInfo.setBudgetType(budgetType);
          }
        }

        if (phase.getNext() != null) {
          phase = phase.getNext();
        } else {
          hasNext = false;
        }
      }

      /*
       * if (partnertsIDList != null) {
       * for (String partner : partnertsIDList) {
       * if (partner != null && !partner.isEmpty() && partner != "") {
       * FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();;
       * fundingSourceInstitution.setInstitution(institutionManager.getInstitutionById(Integer.parseInt(partner)));
       * fundingSourceInstitution.setFundingSource(fundingSource);
       * fundingSourceInstitution.setPhase(this.getActualPhase());
       * fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
       * }
       * }
       * }
       */

      if (fundingSourceTemp.getInstitutions() != null) {
        for (FundingSourceInstitution partner : fundingSourceTemp.getInstitutions()) {
          if (partner.getId() == null || partner.getId().longValue() == -1 && partner.getInstitution().getId() == null
            || partner.getInstitution().getId().longValue() == -1) {
            FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();
            fundingSourceInstitution
              .setInstitution(institutionManager.getInstitutionById(partner.getInstitution().getId()));
            fundingSourceInstitution.setFundingSource(fundingSource);
            fundingSourceInstitution.setPhase(this.getActualPhase());
            fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
          }
        }
      }
      /*
       * LiaisonUser user = liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(),
       * loggedCrp.getId());
       * if (user != null) {
       * LiaisonInstitution liaisonInstitution = user.getLiaisonInstitution();
       * try {
       * if (liaisonInstitution != null && liaisonInstitution.getInstitution() != null) {
       * Institution institution =
       * institutionManager.getInstitutionById(liaisonInstitution.getInstitution().getId());
       * FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();
       * fundingSourceInstitution.setFundingSource(fundingSource);
       * fundingSourceInstitution.setPhase(this.getActualPhase());
       * fundingSourceInstitution.setInstitution(institution);
       * fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
       * }
       * } catch (Exception e) {
       * logger.error("unable to save FundingSourceInstitution", e);
       * }
       * }
       */
      // this.clearPermissionsCache();
      // HJ : add the permission String
      AuthorizationInfo info = ((APCustomRealm) this.securityContext.getRealm())
        .getAuthorizationInfo(this.securityContext.getSubject().getPrincipals());

      String params[] = {loggedCrp.getAcronym(), fundingSource.getId() + ""};
      info.getStringPermissions()
        .add(this.generatePermission(Permission.PROJECT_FUNDING_SOURCE_BASE_PERMISSION, params));

      if (fundingSourceID > 0) {
        return SUCCESS;
      }


    } else {
      return ERROR;
    }
    return INPUT;
  }


  public void assignLeadCenter() {
    for (FundingSource fundingSource : fundingSourceManager.findAll().stream().filter(fs -> fs.isActive())
      .collect(Collectors.toList())) {
      if (fundingSource.getInstitutions() != null && fundingSource.getFundingSourceInfo() != null
        && fundingSource.getFundingSourceInfo().getLeadCenter() == null) {

        // add lead center when funding source has just one institution
        if (fundingSource.getInstitutions().size() == 1 && fundingSource.getInstitutions().get(0) != null
          && fundingSource.getInstitutions().get(0).getInstitution() != null) {

          fundingSource.getFundingSourceInfo().setLeadCenter(fundingSource.getInstitutions().get(0).getInstitution());
          fundingSourceInfoManager.saveFundingSourceInfo(fundingSource.getFundingSourceInfo());
        } else {

          // add lead center when the finance code is a ciat code
          if (fundingSource.getFundingSourceInfo().getFinanceCode() != null
            && fundingSource.getFundingSourceInfo().getFinanceCode().length() == 4) {
            List<Institution> institutions = new ArrayList<>();
            for (FundingSourceInstitution fundingSourceIntitution : fundingSource.getInstitutions()) {
              institutions.add(fundingSourceIntitution.getInstitution());
            }
            Institution ciatInstitution = new Institution();
            ciatInstitution = institutionManager.getInstitutionById(46);
            if (institutions != null && ciatInstitution != null && institutions.contains(ciatInstitution)) {
              fundingSource.getFundingSourceInfo().setLeadCenter(ciatInstitution);
              fundingSourceInfoManager.saveFundingSourceInfo(fundingSource.getFundingSourceInfo());
            }
          }
        }
      }
    }
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


  public void convertListToString(List<String> list) {
    if (list != null && !list.isEmpty()) {
      for (String element : list) {
        if (institutionsIDsFilter == null || institutionsIDsFilter.isEmpty()) {
          institutionsIDsFilter = element;
        } else {
          institutionsIDsFilter += ", " + element;
        }
      }
    }
  }


  /**
   * Creates a copy of the selected funding source
   */
  public String copy() {
    // ONLY SuperAdmin and CRP-Admins can copy a funding source from this section
    if (this.canEditCrpAdmin() || this.canAccessSuperAdmin()) {
      logger.debug("THE FUNDING SOURCE TO BE COPIED HAS AN ID OF F{}", this.getFundingSourceID());

      FundingSource fundingSourceToBeCopied = fundingSourceManager.getFundingSourceById(fundingSourceID);
      FundingSource fundingSourceCopy = new FundingSource();
      fundingSourceCopy.setCreateDate(new Date());
      fundingSourceCopy.setCrp(this.getCurrentCrp());

      fundingSourceCopy = fundingSourceManager.saveFundingSource(fundingSourceCopy);
      this.fundingSourceID = fundingSourceCopy.getId();

      // funding sources info
      FundingSourceInfo fundingSourceInfoToBeCopied =
        fundingSourceToBeCopied.getFundingSourceInfo(this.getActualPhase());
      fundingSourceInfoToBeCopied =
        fundingSourceInfoManager.getFundingSourceInfoById(fundingSourceInfoToBeCopied.getId());
      FundingSourceInfo fundingSourceInfoCopy = new FundingSourceInfo();
      fundingSourceInfoCopy.copyFields(fundingSourceInfoToBeCopied);

      fundingSourceInfoCopy.setFinanceCode("");
      fundingSourceInfoCopy.setFundingSource(fundingSourceCopy);
      fundingSourceInfoCopy.setPhase(this.getActualPhase());
      fundingSourceInfoCopy = fundingSourceInfoManager.saveFundingSourceInfo(fundingSourceInfoCopy);

      Phase nextPhase = this.getActualPhase().getNext();
      while (nextPhase != null) {
        // replication
        FundingSourceInfo replicated = new FundingSourceInfo();
        replicated.copyFields(fundingSourceInfoCopy);
        nextPhase = nextPhase.getNext();
      }

      // funding source budgets
      if (fundingSourceToBeCopied.getFundingSourceBudgets() != null) {
        Set<FundingSourceBudget> fsbs = fundingSourceToBeCopied.getFundingSourceBudgets();
        // ???
        fsbs.removeIf(Objects::isNull);
        fsbs.removeIf(fsb -> !fsb.getPhase().equals(this.getActualPhase()));
        for (FundingSourceBudget fsb : fsbs) {
          FundingSourceBudget newFsb = new FundingSourceBudget();
          FundingSourceBudget fundingSourceBudgetDB =
            fundingSourceBudgetManager.getFundingSourceBudgetById(fsb.getId());
          newFsb.copyFields(fundingSourceBudgetDB);
          newFsb.setFundingSource(fundingSourceCopy);
          newFsb.setPhase(this.getActualPhase());
          // replication on save
          fundingSourceBudgetManager.saveFundingSourceBudget(newFsb);
        }
      }

      // funding source divisions
      if (fundingSourceToBeCopied.getFundingSourceDivisions() != null) {
        Set<FundingSourceDivision> fsds = fundingSourceToBeCopied.getFundingSourceDivisions();
        // ???
        fsds.removeIf(Objects::isNull);
        fsds.removeIf(fsd -> !fsd.getPhase().equals(this.getActualPhase()));
        for (FundingSourceDivision fundingSourceDivision : fsds) {
          if (fundingSourceDivision.getId() == null || fundingSourceDivision.getId().longValue() == -1) {
            FundingSourceDivision fundingSourceDivisionSave = new FundingSourceDivision();
            fundingSourceDivisionSave.setFundingSource(fundingSourceCopy);
            fundingSourceDivisionSave.setPhase(this.getActualPhase());
            PartnerDivision partnerDivision =
              partnerDivisionManager.getPartnerDivisionById(fundingSourceDivision.getDivision().getId());
            fundingSourceDivisionSave.setDivision(partnerDivision);
            // replication on save
            fundingSourceDivisionManager.saveFundingSourceDivision(fundingSourceDivisionSave);
          }
        }
      }

      // funding source location
      if (fundingSourceToBeCopied.getFundingSourceLocations() != null) {
        Set<FundingSourceLocation> fsls = fundingSourceToBeCopied.getFundingSourceLocations();
        // ???
        fsls.removeIf(Objects::isNull);
        fsls.removeIf(fsl -> !fsl.getPhase().equals(this.getActualPhase()));
        for (FundingSourceLocation fundingSourceLocation : fsls) {
          FundingSourceLocation fundingSourceLocationSave = new FundingSourceLocation();
          fundingSourceLocationSave.setFundingSource(fundingSourceCopy);
          fundingSourceLocationSave.setPhase(this.getActualPhase());

          if (fundingSourceLocation.getLocElement().getLocElementType() != null) {
            LocElement locElement = locElementManager.getLocElementById(fundingSourceLocation.getLocElement().getId());
            if (fundingSourceLocation.getLocElement().getLocElementType().getId().equals(1L)) {
              // region
              fundingSourceLocationSave.setPercentage(fundingSourceLocation.getPercentage());
              if (!fundingSourceLocation.isScope()) {
                fundingSourceLocationSave.setLocElement(locElement);
              } else {
                LocElementType elementType =
                  locElementTypeManager.getLocElementTypeById(fundingSourceLocation.getLocElement().getId());
                fundingSourceLocationSave.setLocElementType(elementType);
              }
            } else if (fundingSourceLocation.getLocElement().getLocElementType().getId().equals(2L)) {
              // country
              fundingSourceLocationSave.setLocElement(locElement);
            }
          }

          // fundingSourceLocationSave.setLocElement(fundingSourceLocation.getLocElement());
          // replication on save
          fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationSave);
        }
      }

      // funding source location (countries)
      // if (fundingSourceToBeCopied.getFundingCountry() != null) {
      // List<FundingSourceLocation> fscs = fundingSourceToBeCopied.getFundingCountry();
      // for (FundingSourceLocation fundingSourceLocation : fscs) {
      // FundingSourceLocation fundingSourceLocationSave = new FundingSourceLocation();
      // fundingSourceLocationSave.setFundingSource(fundingSourceCopy);
      // fundingSourceLocationSave.setPhase(this.getActualPhase());
      // fundingSourceLocationSave.setLocElement(fundingSourceLocation.getLocElement());
      // // replication on save
      // fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationSave);
      // }
      // }

      // funding source location (regions)
      // if (fundingSourceToBeCopied.getFundingRegions() != null) {
      // List<FundingSourceLocation> fsrs = fundingSourceToBeCopied.getFundingRegions();
      // for (FundingSourceLocation fundingSourceLocation : fsrs) {
      // FundingSourceLocation fundingSourceLocationSave = new FundingSourceLocation();
      // fundingSourceLocationSave.setFundingSource(fundingSourceCopy);
      // fundingSourceLocationSave.setPhase(this.getActualPhase());
      // fundingSourceLocationSave.setPercentage(fundingSourceLocation.getPercentage());
      // if (!fundingSourceLocation.isScope()) {
      // fundingSourceLocationSave.setLocElement(fundingSourceLocation.getLocElement());
      // } else {
      // fundingSourceLocationSave.setLocElementType(fundingSourceLocation.getLocElementType());
      // }
      //
      // fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationSave);
      // }
      // }

      // funding source institutions
      if (fundingSourceToBeCopied.getFundingSourceInstitutions() != null) {
        Set<FundingSourceInstitution> fsis = fundingSourceToBeCopied.getFundingSourceInstitutions();
        // ???
        fsis.removeIf(Objects::isNull);
        fsis.removeIf(fsi -> !fsi.getPhase().equals(this.getActualPhase()));
        for (FundingSourceInstitution partner : fsis) {
          if (partner.getId() != null && partner.getId().longValue() != -1 && partner.getInstitution().getId() != null
            && partner.getInstitution().getId().longValue() != -1) {
            FundingSourceInstitution newFsi = new FundingSourceInstitution();
            newFsi.setInstitution(institutionManager.getInstitutionById(partner.getInstitution().getId()));
            newFsi.setFundingSource(fundingSourceCopy);
            newFsi.setPhase(this.getActualPhase());
            // replication on save
            fundingSourceInstitutionManager.saveFundingSourceInstitution(newFsi);
          }
        }
      }

      AuthorizationInfo info = ((APCustomRealm) this.securityContext.getRealm())
        .getAuthorizationInfo(this.securityContext.getSubject().getPrincipals());

      String params[] = {loggedCrp.getAcronym(), fundingSourceCopy.getId() + ""};
      info.getStringPermissions()
        .add(this.generatePermission(Permission.PROJECT_FUNDING_SOURCE_BASE_PERMISSION, params));

      if (fundingSourceID > 0) {
        return SUCCESS;
      } else {
        return ERROR;
      }
    }

    return INPUT;
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


  /**
   * Add cpRole as a flag to avoid contact points
   * 
   * @param crpPpaPartner
   */
  private void fillContactPoints(CrpPpaPartner crpPpaPartner) {

    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
      .getLiasonInstitutionByInstitutionId(crpPpaPartner.getInstitution().getId(), loggedCrp.getId());
    if (cpRole != null && liaisonInstitution != null && liaisonInstitution.isActive()) {
      crpPpaPartner.setContactPoints(liaisonInstitution.getLiaisonUsers().stream()
        .filter(lu -> lu.isActive() && lu.getUser() != null && lu.getUser().isActive() && lu.getCrp() != null
          && lu.getCrp().equals(loggedCrp))
        .sorted((lu1, lu2) -> lu1.getUser().getLastName().compareTo(lu2.getUser().getLastName()))
        .collect(Collectors.toList()));
    } else {
      crpPpaPartner.setContactPoints(new ArrayList<LiaisonUser>());
    }
  }

  public void fillInstitutionsList() {
    // Fill the institutions list search each institution with the institutions id list
    filteredInstitutions = new ArrayList<>();

    if (institutionsIDsList != null) {
      if (institutionsIDsList.size() == 1 && institutionsIDsList.get(0).equals("0")) {
        // If the institution id received is equals to "0", its mean that all the institutions have to be check mark
        checkAllInstitutions = true;
      } else {
        for (String institution : institutionsIDsList) {

          Long id = null;
          if (institution != null && !institution.equals("0")) {
            id = Long.parseLong(institution);
          }
          if (id != 0) {
            filteredInstitutions.add(institutionManager.getInstitutionById(id));
          }
        }
      }
    }
  }


  public Map<String, String> getAgreementStatus() {
    return agreementStatus;
  }

  public Map<String, String> getAgreementStatusModal() {
    return agreementStatusModal;
  }

  public Map<String, String> getBudgetTypes() {
    return budgetTypes;
  }

  public List<FundingSource> getClosedProjects() {
    return closedProjects;
  }

  public void getCrpContactPoint() {
    contactsPoint = new ArrayList<>();
    usersContactPoint = new ArrayList<>();

    // Check if the CRP has Contact Point and ContactPointRole, if not cpRole will be null (it will be used as a flag)
    if (this.hasSpecificities(APConstants.CRP_HAS_CP)
      && roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE))) != null) {
      cpRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_CP_ROLE)));
    }

    if (loggedCrp.getCrpPpaPartners() != null) {
      loggedCrp.setCrpInstitutionsPartners(new ArrayList<CrpPpaPartner>(loggedCrp.getCrpPpaPartners().stream()
        .filter(ppa -> ppa.isActive() && ppa.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
      loggedCrp.getCrpInstitutionsPartners()
        .sort((p1, p2) -> p1.getInstitution().getName().compareTo(p2.getInstitution().getName()));
      // Fill Managing/PPA Partners with contact persons
      if (cpRole != null) {
        Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);
        for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners()) {
          this.fillContactPoints(crpPpaPartner);
          crpPpaPartners.add(crpPpaPartner);
        }
        loggedCrp.setCrpPpaPartners(crpPpaPartners);
      }
    }


    for (CrpPpaPartner partner : loggedCrp.getCrpInstitutionsPartners()) {
      if (partner.getContactPoints() != null) {
        for (LiaisonUser contactPoint : partner.getContactPoints()) {
          if (contactPoint != null) {
            contactsPoint.add(contactPoint);
            if (contactPoint.getUser() != null) {
              usersContactPoint.add(contactPoint.getUser().getId());
            }
          }
        }
      }
    }

  }

  public List<Institution> getFilteredInstitutions() {
    return filteredInstitutions;
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
    // Get all the institutions for each funding source
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

  public FundingSource getFundingSourceTemp() {
    return fundingSourceTemp;
  }

  public List<FundingSourceInstitution> getIns() {
    return ins;
  }


  public List<FundingSourceInstitution> getInstitutionFSFiltered() {
    return institutionFSFiltered;
  }

  public void getInstitutionsIds() {
    // Separate institutions ids from institutions apConstans filters into arrayList received from front end
    int lastI = 0;
    if (institutionsIDs.contains(",")) {
      for (int i = 0; i < institutionsIDs.length(); i++) {
        if ((institutionsIDs.charAt(i) == ',')) {
          institutionsIDsList.add(institutionsIDs.substring(lastI, i).trim());
          if ((i + 1) <= institutionsIDs.length()) {
            lastI = i + 1;
          }
        }

        if (i == institutionsIDs.length() - 1) {
          institutionsIDsList.add(institutionsIDs.substring(lastI, i + 1).trim());
        }
      }
    } else {
      institutionsIDsList.add(institutionsIDs);
    }
  }

  public String getInstitutionsIDsFilter() {
    return institutionsIDsFilter;
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

  public List<Institution> getManagingInstitutionsList() {
    return managingInstitutionsList;
  }

  public List<FundingSource> getMyProjects() {
    return myProjects;
  }


  public void getPartnertsIDs() {
    // Separate institutions partner Managing ids from institutions apConstans filters into arrayList
    int lastI = 0;
    if (partnerIDs.contains(",")) {
      for (int i = 0; i < partnerIDs.length(); i++) {
        if ((partnerIDs.charAt(i) == ',')) {
          partnertsIDList.add(partnerIDs.substring(lastI, i).trim());
          if ((i + 1) <= partnerIDs.length()) {
            lastI = i + 1;
          }
        }

        if (i == partnerIDs.length() - 1) {
          partnertsIDList.add(partnerIDs.substring(lastI, i + 1).trim());
        }
      }
    } else {
      partnertsIDList.add(partnerIDs);

    }
  }


  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    checkAllInstitutions = false;
    institutionsIDsList = new ArrayList<String>();
    partnertsIDList = new ArrayList<String>();

    try {
      Map<String, Parameter> parameters = this.getParameters();
      institutionsIDs = StringUtils.trim(parameters.get(APConstants.INSTITUTIONS_ID).getMultipleValues()[0]);
    } catch (Exception e) {
      institutionsIDs = "0";
      checkAllInstitutions = true;
    }

    if (institutionsIDs.equals("0")) {
      checkAllInstitutions = true;
    }

    // Status list
    Set<Integer> statusTypes = new HashSet<>();
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Ongoing.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Extended.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Pipeline.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Informally.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Complete.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Cancelled.getStatusId()));


    // Budget Types list
    budgetTypes = new HashMap<>();
    for (BudgetType budgetType : budgetTypeManager.findAll()) {
      if (budgetType.getId().intValue() == 1) {
        if (this.hasPermissionNoBase(
          this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()))) {
          budgetTypes.put(budgetType.getId().toString(), budgetType.getName());
        }
      } else {
        budgetTypes.put(budgetType.getId().toString(), budgetType.getName());
      }
    }


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

    // Load Managing Institutions List
    managingInstitutionsList = new ArrayList<>();
    List<CrpPpaPartner> ppaPartners = crpPpaPartnerManager.findAll().stream()
      .filter(p -> p != null && p.getPhase() != null && p.getPhase().getId().equals(this.getActualPhase().getId()))
      .collect(Collectors.toList());
    if (ppaPartners != null && !ppaPartners.isEmpty()) {
      ppaPartners.stream().distinct()
        .filter(c -> c != null && c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive()
          && c.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList());


      for (CrpPpaPartner crpPpaPartner : ppaPartners) {
        managingInstitutionsList.add(crpPpaPartner.getInstitution());
      }
    }

    // Load Agreement Status
    agreementStatus = new HashMap<>();
    List<FundingStatusEnum> list = Arrays.asList(FundingStatusEnum.values());
    for (FundingStatusEnum projectStatusEnum : list) {
      switch (FundingStatusEnum.getValue(Integer.parseInt(projectStatusEnum.getStatusId()))) {
        case Pipeline:
        case Informally:
          if (this.hasSpecificities(APConstants.CRP_STATUS_FUNDING_SOURCES)) {
            agreementStatus.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
          }
          break;
        default:
          agreementStatus.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
          break;
      }
    }
    // add new status list for new funding source modal
    agreementStatusModal = new HashMap<>(agreementStatus);
    agreementStatusModal.remove(FundingStatusEnum.Complete.getStatusId());
    agreementStatusModal.remove(FundingStatusEnum.Extended.getStatusId());
    agreementStatusModal.remove(FundingStatusEnum.Cancelled.getStatusId());

    this.getCrpContactPoint();
    this.getFundingSourceInstitutionsList();
    // this.assignLeadCenter();
    if (institutionsIDs != null && !institutionsIDs.isEmpty() && !institutionsIDs.equals("0")) {
      // if (!institutionsIDs.equals("0")) {

      // Get each institution id captured from url string into an ArrayList
      this.getInstitutionsIds();

      // Remove institutions not checked in frontend of 'myProjects' list, using ids in institutionsIDsList.
      this.removeInstitutions();
      // }
      this.fillInstitutionsList();

      // return string with the institutions in the apconstant variable separated with ','
      this.convertListToString(institutionsIDsList);
    } else {
      if (contactsPoint != null && usersContactPoint != null) {
        // If the logged user is a contact point, its definen the default checked institutions
        institutionFSFiltered = new ArrayList<>();
        this.removeInstitutionsContactPointRole();
      }
    }
    this.setChecksToList();
  }


  public void removeInstitutions() {
    List<FundingSource> tempList = new ArrayList<>();
    int contains = 0;

    /*
     * On-Going funding sources
     */
    if (myProjects != null && institutionsIDsList != null) {
      tempList.addAll(myProjects);

      for (FundingSource fundingSource : myProjects) {

        if (fundingSource.getInstitutions() != null && !fundingSource.getInstitutions().isEmpty()
          && fundingSource.getInstitutions().size() != 0) {
          // if the list of funding source institutions has elements, check the ID

          contains = 0;
          int countInstitutions = 0;
          for (FundingSourceInstitution institution : fundingSource.getInstitutions()) {
            countInstitutions++;

            if (institutionsIDsList.contains(String.valueOf((institution.getInstitution().getId())))) {
              contains += 1;
            }

            if (contains == 0 && countInstitutions == fundingSource.getInstitutions().size()) {
              // remove funding source without expected Id institution

              try {
                tempList.remove(fundingSource);
              } catch (Exception e) {

              }
            }
            // end institutions for
          }
        } else {
          // remove funding source without institutions
          tempList.remove(fundingSource);
        }
      }
    }
    if (myProjects != null) {
      myProjects.removeAll(myProjects);
    }
    if (tempList != null) {
      myProjects.addAll(tempList);
    }

    /*
     * Archieved funding sources
     */
    if (closedProjects != null && institutionsIDsList != null) {
      tempList.addAll(closedProjects);

      for (FundingSource fundingSource : closedProjects) {

        if (fundingSource.getInstitutions() != null && !fundingSource.getInstitutions().isEmpty()
          && fundingSource.getInstitutions().size() != 0) {
          // if the list of funding source institutions has elements, check the ID

          contains = 0;
          int countInstitutions = 0;
          for (FundingSourceInstitution institution : fundingSource.getInstitutions()) {
            countInstitutions++;

            if (institutionsIDsList.contains(String.valueOf((institution.getInstitution().getId())))) {
              contains += 1;
            }

            if (contains == 0 && countInstitutions == fundingSource.getInstitutions().size()) {
              // remove funding source without expected Id institution

              try {
                tempList.remove(fundingSource);
              } catch (Exception e) {

              }
            }
            // end institutions for
          }
        } else {
          // remove funding source without institutions
          tempList.remove(fundingSource);
        }
      }
    }
    if (closedProjects != null) {
      closedProjects.removeAll(closedProjects);
    }
    if (tempList != null) {
      closedProjects.addAll(tempList);
    }
  }


  public void removeInstitutionsContactPointRole() {
    // Get institution for contact point
    List<FundingSource> tempList = new ArrayList<>();

    /*
     * On-Going funding sources
     */
    if (myProjects != null) {
      tempList.addAll(myProjects);

      for (FundingSource fundingSource : myProjects) {

        if (fundingSource.getInstitutions() != null && !fundingSource.getInstitutions().isEmpty()
          && fundingSource.getInstitutions().size() != 0) {
          // if the list of funding source institutions has elements, check the ID
          if (usersContactPoint.contains(this.getCurrentUser().getId())) {

            for (CrpPpaPartner partner : loggedCrp.getCrpInstitutionsPartners()) {
              if (partner.getContactPoints() != null) {
                for (LiaisonUser lsUser : partner.getContactPoints()) {
                  if (lsUser.getUser().getId().equals(this.getCurrentUser().getId())) {
                    if (fundingSource.getInstitutions() != null) {
                      int contains = 0;
                      for (FundingSourceInstitution institutionFS : fundingSource.getInstitutions()) {
                        if (institutionFS.getInstitution().getId().equals(partner.getInstitution().getId())) {
                          contains++;
                          institutionFSFiltered.add(institutionFS);
                        }
                      }
                      if (contains == 0) {
                        tempList.remove(fundingSource);
                        institutionsIDsFilter = String.valueOf(partner.getInstitution().getId());
                      }
                    }
                  }
                }
              }
            }
          }
          // end institutions for

        } else {
          // remove funding source without institutions
          tempList.remove(fundingSource);
        }
      }
    }

    if (myProjects != null) {
      myProjects.removeAll(myProjects);
    }

    if (tempList != null) {
      myProjects.addAll(tempList);
    }

    /*
     * Archieved funding sources
     */
    if (closedProjects != null) {
      tempList.addAll(closedProjects);

      for (FundingSource fundingSource : closedProjects) {

        if (fundingSource.getInstitutions() != null && !fundingSource.getInstitutions().isEmpty()
          && fundingSource.getInstitutions().size() != 0) {
          // if the list of funding source institutions has elements, check the ID
          if (usersContactPoint.contains(this.getCurrentUser().getId())) {

            for (CrpPpaPartner partner : loggedCrp.getCrpInstitutionsPartners()) {
              if (partner.getContactPoints() != null) {
                for (LiaisonUser lsUser : partner.getContactPoints()) {
                  if (lsUser.getUser().getId().equals(this.getCurrentUser().getId())) {
                    if (fundingSource.getInstitutions() != null) {
                      int contains = 0;
                      for (FundingSourceInstitution institutionFS : fundingSource.getInstitutions()) {
                        if (institutionFS.getInstitution().getId().equals(partner.getInstitution().getId())) {
                          contains++;
                        }
                      }
                      if (contains == 0) {
                        tempList.remove(fundingSource);
                        institutionsIDsFilter = String.valueOf(partner.getInstitution().getId());
                      }
                    }
                  }
                }
              }
            }
          }
          // end institutions for

        } else {
          // remove funding source without institutions
          tempList.remove(fundingSource);
        }
      }
    }
    /*
     * if (closedProjects != null) {
     * closedProjects.removeAll(closedProjects);
     * }
     * if (tempList != null) {
     * closedProjects.addAll(tempList);
     * }
     */
    if (institutionsIDsFilter != null) {
      institutionsIDsFilter = "";
    }
  }


  public void setAgreementStatusModal(Map<String, String> agreementStatusModal) {
    this.agreementStatusModal = agreementStatusModal;
  }


  private void setChecksToList() {
    // check if the institution is selected in the front component, if yes, then set as 'yes' boolean property value of
    // funding institution
    if (fundingSourceInstitutions != null) {
      if (institutionFSFiltered != null) {
        for (FundingSourceInstitution fundingInstitution : fundingSourceInstitutions) {
          if (institutionFSFiltered.contains(fundingInstitution)) {
            fundingInstitution.setIsChecked(true);
          } else {
            fundingInstitution.setIsChecked(false);
          }
        }
      } else {
        for (FundingSourceInstitution fundingInstitution : fundingSourceInstitutions) {
          // If all institutions is selected in front end
          if (checkAllInstitutions == true) {
            fundingInstitution.setIsChecked(true);
          } else {
            // Check institutions with check mark for add to the list
            if (filteredInstitutions != null && filteredInstitutions.contains(fundingInstitution.getInstitution())) {
              fundingInstitution.setIsChecked(true);
            } else {
              fundingInstitution.setIsChecked(false);
            }
          }
        }
      }
    }
  }

  public void setClosedProjects(List<FundingSource> closedProjects) {
    this.closedProjects = closedProjects;
  }

  public void setFilteredInstitutions(List<Institution> filteredInstitutions) {
    this.filteredInstitutions = filteredInstitutions;
  }

  public void setFundingSourceID(long projectID) {
    this.fundingSourceID = projectID;
  }


  public void setFundingSourceInstitutions(List<FundingSourceInstitution> fundingSourceInstitutions) {
    this.fundingSourceInstitutions = fundingSourceInstitutions;
  }

  public void setFundingSourceTemp(FundingSource fundingSourceTemp) {
    this.fundingSourceTemp = fundingSourceTemp;
  }


  public void setIns(List<FundingSourceInstitution> ins) {
    this.ins = ins;
  }

  public void setInstitutionFSFiltered(List<FundingSourceInstitution> institutionFSFiltered) {
    this.institutionFSFiltered = institutionFSFiltered;
  }

  public void setInstitutionsIDsFilter(String institutionsIDsFilter) {
    this.institutionsIDsFilter = institutionsIDsFilter;
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