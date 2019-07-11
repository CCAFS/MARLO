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
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
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
  private Long cpCrpID;
  private FundingSourceInfoManager fundingSourceInfoManager;
  private RoleManager roleManager;
  private FundingSourceInstitutionManager fundingSourceInstitutionManager;
  private GlobalUnitManager crpManager;
  private LiaisonUserManager liaisonUserManager;
  private InstitutionManager institutionManager;
  private CrpPpaPartnerManager crpPpaPartnerManager;
  private GlobalUnitManager globalUnitManager;

  private List<FundingSource> closedProjects;
  private List<FundingSourceInstitution> fundingSourceInstitutions;
  private List<Institution> managingInstitutionsList;
  private Map<String, String> agreementStatus;
  private UserRoleManager userRoleManager;
  private long fundingSourceID;
  private long fundingSourceInfoID;
  private String justification;
  private String financeCode;
  private Long centerID;
  private String agreementStatusValue;
  private String institutionLead;
  private String partnerIDs;


  @Inject
  public FundingSourceListAction(APConfig config, FundingSourceManager fundingSourceManager,
    GlobalUnitManager crpManager, ProjectManager projectManager, LiaisonUserManager liaisonUserManager,
    InstitutionManager institutionManager, FundingSourceInstitutionManager fundingSourceInstitutionManager,
    FundingSourceInfoManager fundingSourceInfoManager, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpPpaPartnerManager crpPpaPartnerManager, GlobalUnitManager globalUnitManager) {
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
    } catch (Exception e) {
      Log.error(e);
    }

    if (partnerIDs != null) {
      this.getPartnertsIDs();
    }

    if (financeCode != null) {

      FundingSource fundingSourceSearch = new FundingSource();
      List<FundingSource> fundingSourceSearchTemp = new ArrayList<FundingSource>();
      fundingSourceSearchTemp = null;
      fundingSourceSearchTemp = fundingSourceManager.findAll().stream()
        .filter(f -> f.getFundingSourceInfo(this.getActualPhase()) != null
          && f.getFundingSourceInfo(this.getActualPhase()).getFinanceCode() != null
          && f.getFundingSourceInfo(this.getActualPhase()).getFinanceCode().equals(financeCode))
        .collect(Collectors.toList());

      if (fundingSourceSearchTemp == null || fundingSourceSearchTemp.isEmpty()) {
        // if finance code does not exist
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
          fundingSourceInfo.setFinanceCode(financeCode);
          fundingSourceInfo.setStatus(Integer.parseInt(agreementStatusValue));
          fundingSourceInfo.setFundingSource(fundingSourceManager.getFundingSourceById(fundingSourceID));
          fundingSourceInfoID = fundingSourceInfoManager.saveFundingSourceInfo(fundingSourceInfo).getId();


          if (phase.getNext() != null) {
            phase = phase.getNext();
          } else {
            hasNext = false;
          }
        }

        if (partnertsIDList != null) {
          for (String partner : partnertsIDList) {
            FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();;
            fundingSourceInstitution.setInstitution(institutionManager.getInstitutionById(Integer.parseInt(partner)));
            fundingSourceInstitution.setFundingSource(fundingSource);
            fundingSourceInstitution.setPhase(this.getActualPhase());
            fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
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
        // if finance code already exist
        if (fundingSourceSearchTemp.get(0) != null) {
          fundingSourceSearch = fundingSourceSearchTemp.get(0);
        }

      }
    } else {
      return ERROR;
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

  public Map<String, String> getAgreementStatus() {
    return agreementStatus;
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


  public void getInstitutionsIds() {
    // Separate institutions ids from institutions apConstans filters into arrayList
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

    institutionsIDsList = new ArrayList<String>();
    partnertsIDList = new ArrayList<String>();

    try {
      Map<String, Parameter> parameters = this.getParameters();
      institutionsIDs = StringUtils.trim(parameters.get(APConstants.INSTITUTIONS_ID).getMultipleValues()[0]);

    } catch (Exception e) {
      Log.error(e + "error getting institutionsID parameter");
    }
    Set<Integer> statusTypes = new HashSet<>();
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Ongoing.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Extended.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Pipeline.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Informally.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Complete.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Cancelled.getStatusId()));

    List<FundingSource> allFundingSources =
      fundingSourceManager.getGlobalUnitFundingSourcesByPhaseAndTypes(loggedCrp, this.getActualPhase(), statusTypes);

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
      .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive()
        && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList());
    for (CrpPpaPartner crpPpaPartner : ppaPartners) {
      managingInstitutionsList.add(crpPpaPartner.getInstitution());
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

    this.getCrpContactPoint();
    this.getFundingSourceInstitutionsList();
    if (institutionsIDs != null && !institutionsIDs.equals("0") && !institutionsIDs.isEmpty()) {
      this.getInstitutionsIds();
      this.removeInstitutions();
      // return string with the institutions in the apconstant variable separated with ','
      this.convertListToString(institutionsIDsList);
    } else {

      if (cpCrpID != null && cpCrpID != -1 && cpCrpID != 0) {
        this.removeInstitutionsContactPointRole();
      }
    }

  }

  public void removeInstitutions() {
    List<FundingSource> tempList = new ArrayList<>();
    int contains = 0;

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
    myProjects.removeAll(myProjects);
    myProjects.addAll(tempList);
  }

  public void removeInstitutionsContactPointRole() {
    // Get institution for contact point
    List<FundingSource> tempList = new ArrayList<>();
    int contains = 0;
    List<String> instIDs = new ArrayList<>();

    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit =
      globalUnitManager.findAll().stream().filter(f -> f.getId().equals(cpCrpID)).collect(Collectors.toList()).get(0);

    if (globalUnit != null && globalUnit.getInstitution() != null) {
      instIDs.add(String.valueOf(globalUnit.getInstitution().getId()));
    }


    if (myProjects != null && instIDs != null) {
      tempList.addAll(myProjects);

      for (FundingSource fundingSource : myProjects) {

        if (fundingSource.getInstitutions() != null && !fundingSource.getInstitutions().isEmpty()
          && fundingSource.getInstitutions().size() != 0) {
          // if the list of funding source institutions has elements, check the ID

          contains = 0;
          int countInstitutions = 0;
          for (FundingSourceInstitution institution : fundingSource.getInstitutions()) {
            countInstitutions++;

            if (instIDs.contains(String.valueOf((institution.getInstitution().getId())))) {
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
    myProjects.removeAll(myProjects);
    myProjects.addAll(tempList);
    institutionsIDsFilter = String.valueOf(globalUnit.getInstitution().getId());
  }

  public void setClosedProjects(List<FundingSource> closedProjects) {
    this.closedProjects = closedProjects;
  }

  public void setCpCrpID(Long cpCrpID) {
    this.cpCrpID = cpCrpID;
  }


  public void setFundingSourceID(long projectID) {
    this.fundingSourceID = projectID;
  }

  public void setFundingSourceInstitutions(List<FundingSourceInstitution> fundingSourceInstitutions) {
    this.fundingSourceInstitutions = fundingSourceInstitutions;
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