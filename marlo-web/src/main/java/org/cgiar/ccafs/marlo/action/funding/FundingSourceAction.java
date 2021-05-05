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
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceLocationsManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
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
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.fundingSource.FundingSourceValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FundingSourceAction:
 * 
 * @author AVALENCIA - CCAFS
 * @date Nov 23, 2017
 * @time 11:38:34 AM: Check empty regions and countries
 */
public class FundingSourceAction extends BaseAction {


  private static final long serialVersionUID = -3919022306156272887L;


  private static Logger LOG = LoggerFactory.getLogger(FundingSourceAction.class);

  private AuditLogManager auditLogManager;
  private BudgetTypeManager budgetTypeManager;
  private Map<String, String> budgetTypes;
  private List<BudgetType> budgetTypesList;

  private GlobalUnitManager crpManager;
  private CrpPpaPartnerManager crpPpaPartnerManager;
  private File file;
  private String fileContentType;
  private FileDBManager fileDBManager;
  private String fileFileName;
  private Integer fileID;
  private FundingSource fundingSource;
  private FundingSource fundingSourceShow;
  private FundingSourceBudgetManager fundingSourceBudgetManager;

  private long fundingSourceID;

  private FundingSourceInstitutionManager fundingSourceInstitutionManager;
  private FundingSourceDivisionManager fundingSourceDivisionManager;
  private ProjectBudgetManager projectBudgetManager;

  private ProjectManager projectManager;
  private FundingSourceManager fundingSourceManager;
  private FundingSourceInfoManager fundingSourceInfoManager;
  private InstitutionManager institutionManager;
  private List<Institution> institutions;
  private List<Institution> fundingSourceInstitutions;
  private List<String> projectsMappedIDs;
  private List<Institution> institutionsDonors;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private List<LiaisonInstitution> liaisonInstitutions;
  private HistoryComparator historyComparator;
  private PartnerDivisionManager partnerDivisionManager;
  private List<ProjectBudget> projectBudgetsListOtherCRP;
  private List<PartnerDivision> divisions;
  private GlobalUnit loggedCrp;
  private Map<String, String> status;

  private String transaction;
  private UserManager userManager;
  private FundingSourceValidator validator;
  private Phase crpPhase;

  /*
   * Funding Source Locations
   */
  private FundingSourceLocationsManager fundingSourceLocationsManager;
  private LocElementManager locElementManager;

  private LocElementTypeManager locElementTypeManager;
  private List<LocElement> regionLists;
  private List<LocElementType> scopeRegionLists;
  private List<LocElement> countryLists;
  private boolean region;
  // TODO delete when fix the budget permissions
  private RoleManager userRoleManager;

  // HJ 7/24/2019 Variables for Project Mapping
  private List<Project> userProjects;


  @Inject
  public FundingSourceAction(APConfig config, GlobalUnitManager crpManager, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager, LiaisonInstitutionManager liaisonInstitutionManager,
    AuditLogManager auditLogManager, FundingSourceBudgetManager fundingSourceBudgetManager,
    BudgetTypeManager budgetTypeManager, FundingSourceValidator validator, CrpPpaPartnerManager crpPpaPartnerManager,
    HistoryComparator historyComparator, FileDBManager fileDBManager, UserManager userManager,
    PartnerDivisionManager partnerDivisionManager, FundingSourceInstitutionManager fundingSourceInstitutionManager,
    FundingSourceDivisionManager fundingSourceDivisionManager, LocElementManager locElementManager,
    FundingSourceLocationsManager fundingSourceLocationsManager, LocElementTypeManager locElementTypeManager,
    FundingSourceInfoManager fundingSourceInfoManager, ProjectBudgetManager projectBudgetManager,
    /* TODO delete when fix the budget permissions */ RoleManager userRoleManager, ProjectManager projectManager) {
    super(config);
    this.crpManager = crpManager;
    this.fundingSourceManager = fundingSourceManager;
    this.partnerDivisionManager = partnerDivisionManager;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
    this.budgetTypeManager = budgetTypeManager;
    this.institutionManager = institutionManager;
    this.validator = validator;
    this.fundingSourceDivisionManager = fundingSourceDivisionManager;
    this.userManager = userManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.auditLogManager = auditLogManager;
    this.historyComparator = historyComparator;
    this.fileDBManager = fileDBManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.fundingSourceBudgetManager = fundingSourceBudgetManager;
    this.locElementManager = locElementManager;
    this.fundingSourceLocationsManager = fundingSourceLocationsManager;
    this.locElementTypeManager = locElementTypeManager;
    this.projectManager = projectManager;
    // TODO delete when fix the budget permissions
    this.userRoleManager = userRoleManager;
    this.fundingSourceInfoManager = fundingSourceInfoManager;
    this.projectBudgetManager = projectBudgetManager;
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

  /**
   * Make the validation for CRP Admin, PMU or Finance Manager role
   * to determinate if a Funding source can be duplicated.
   *
   * @return boolean with true o false permission to duplicate FS.
   */
  @Override
  public boolean canDuplicateFunding() {
    boolean canDuplicate = false;
    String roles = this.getRoles();
    if (roles != null && !roles.isEmpty() && (roles.contains("CRP-Admin") || roles.contains("PMU")
      || roles.contains("FM") || roles.contains("SuperAdmin"))) {
      canDuplicate = true;
    } else {
      canDuplicate = false;
    }
    return canDuplicate;
  }

  public boolean canEditFundingSourceBudget() {

    try {
      return this.hasPermissionNoBase(this.generatePermission(Permission.PROJECT_FUNDING_SOURCE_BUDGET_PERMISSION,
        loggedCrp.getAcronym(), fundingSource.getId().toString()));
    } catch (

    Exception e)

    {
      return true;
    }


  }

  public boolean canEditInstitution() {
    User user = userManager.getUser(this.getCurrentUser().getId());
    return user.getUserRoles().stream().filter(c -> c.getRole().getAcronym().equals("CP")).collect(Collectors.toList())
      .isEmpty();


  }

  public boolean canEditType() {
    return fundingSource.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList()).isEmpty();
  }

  /**
   * This method valid if the Funding Source have the information for Map budgets in the projects
   * 
   * @param year
   * @return true is the Funding Source is able to Map Projects
   */
  public boolean canMapProjects(int year) {

    if (!this.isReportingActive()) {
      if (this.fundingSource != null) {

        if (this.fundingSource.getFundingSourceInstitutions() != null) {
          if (this.fundingSource.getFundingSourceInfo().getBudgetType() != null) {
            if (this.fundingSource.getFundingSourceBudgets() != null) {
              List<FundingSourceBudget> fsBudgets = new ArrayList<>(
                this.fundingSource.getFundingSourceBudgets().stream().filter(fb -> fb.isActive() && fb.getYear() == year
                  && fb.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList()));

              if (fsBudgets != null && fsBudgets.size() == 1) {
                FundingSourceBudget fsBudget = fsBudgets.get(0);

                if (fsBudget.getBudget() > 0) {
                  return true;
                }
              }
            }
          }
        }
      }
    }

    return false;
  }


  public String copy() {
    LOG.debug("THE FUNDING SOURCE TO BE COPIED HAS AN ID OF F{}", this.getFundingSourceID());

    FundingSource fundingSourceToBeCopied = fundingSourceManager.getFundingSourceById(fundingSourceID);
    FundingSource fundingSourceCopy = new FundingSource();
    fundingSourceCopy.setCreateDate(new Date());
    fundingSourceCopy.setCrp(this.getCurrentCrp());

    fundingSourceCopy = fundingSourceManager.saveFundingSource(fundingSourceCopy);
    this.fundingSourceID = fundingSourceCopy.getId();

    // funding sources info
    FundingSourceInfo fundingSourceInfoToBeCopied = fundingSourceToBeCopied.getFundingSourceInfo(this.getActualPhase());
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
        FundingSourceBudget fundingSourceBudgetDB = fundingSourceBudgetManager.getFundingSourceBudgetById(fsb.getId());
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

        // replication on save
        fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationSave);
      }
    }

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
    info.getStringPermissions().add(this.generatePermission(Permission.PROJECT_FUNDING_SOURCE_BASE_PERMISSION, params));

    if (fundingSourceID > 0) {
      return SUCCESS;
    } else {
      return ERROR;
    }
  }

  private Path getAutoSaveFilePath() {

    String composedClassName = fundingSource.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = fundingSource.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);

  }

  public FundingSourceBudget getBudget(int year) {

    for (FundingSourceBudget fundingSourceBudget : fundingSource.getBudgets()) {


      if (fundingSourceBudget != null) {
        if (fundingSourceBudget.getYear() != null) {
          if (fundingSourceBudget.getYear().intValue() == year) {
            return fundingSourceBudget;
          }
        }

      }


    }
    FundingSourceBudget fundingSourceBudget = new FundingSourceBudget();
    fundingSourceBudget.setYear(year);
    fundingSourceBudget.setBudget(0.0);
    fundingSource.getBudgets().add(fundingSourceBudget);
    return this.getBudget(year);

  }

  public Map<String, String> getBudgetTypes() {
    return budgetTypes;
  }

  public List<BudgetType> getBudgetTypesList() {
    return budgetTypesList;
  }

  public List<LocElement> getCountryLists() {
    return countryLists;
  }

  public List<PartnerDivision> getDivisions() {
    return divisions;
  }

  public File getFile() {
    return file;
  }


  public String getFileContentType() {
    return fileContentType;
  }

  public String getFileFileName() {
    return fileFileName;
  }


  public Integer getFileID() {
    return fileID;
  }

  public FundingSource getFundingSource() {
    return fundingSource;
  }


  public String getFundingSourceFileURL() {
    return config.getDownloadURL() + "/" + this.getFundingSourceUrlPath().replace('\\', '/');
  }

  public long getFundingSourceID() {
    return fundingSourceID;
  }


  public List<Institution> getFundingSourceInstitutions() {
    return fundingSourceInstitutions;
  }

  public FundingSource getFundingSourceShow() {
    return fundingSourceShow;
  }


  public String getFundingSourceUrlPath() {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + "fundingSourceFiles" + File.separator;
  }

  public int getIndexBugets(int year) {
    int i = 0;
    if (fundingSource.getBudgets() != null) {
      for (FundingSourceBudget fundingSourceBudget : fundingSource.getBudgets()) {
        if (fundingSourceBudget != null) {
          if (fundingSourceBudget.getYear() != null) {
            if (fundingSourceBudget.getYear().intValue() == year) {
              return i;
            }
          }

        }

        i++;
      }
    }

    return -1;

  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<Institution> getInstitutionsDonors() {
    return institutionsDonors;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public void getOtherBudgetContribution() {
    // Get info of projects that this funding source is contributing in others crps

    List<FundingSourceInfo> fundingSourceInfos = new ArrayList<>();
    List<FundingSource> fundingSources = new ArrayList<>();
    List<ProjectBudget> projectBudgets = new ArrayList<>();
    fundingSourceShow = new FundingSource();
    if (fundingSource != null && fundingSource.getFundingSourceInfo() != null
      && fundingSource.getFundingSourceInfo().getFinanceCode() != null) {
      /*
       * fundingSourceInfos = fundingSourceInfoManager.findAll().stream()
       * .filter(fsi -> fsi != null && fsi.isActive() && fsi.getFundingSource() != null
       * && fsi.getFundingSource().getCrp() != null
       * && !(fsi.getFundingSource().getCrp().getId().equals(this.getCurrentCrp().getId()))
       * && fsi.getFinanceCode() != null
       * && fsi.getFinanceCode().equals(fundingSource.getFundingSourceInfo().getFinanceCode())
       * && fsi.getLeadCenter() != null
       * && fsi.getLeadCenter().getId().equals(fundingSource.getFundingSourceInfo().getLeadCenter().getId()))
       * .distinct().collect(Collectors.toList());
       */
      fundingSourceInfos = fundingSourceInfoManager
        .getFundingSourceInfoByFinanceCode(fundingSource.getFundingSourceInfo().getFinanceCode()).stream()
        .filter(fsi -> fsi != null && fsi.isActive() && fsi.getFundingSource() != null
          && fsi.getFundingSource().getCrp() != null
          && !(fsi.getFundingSource().getCrp().getId().equals(this.getCurrentCrp().getId()))
          && fsi.getLeadCenter() != null && fsi.getLeadCenter().getId() != null
          && fundingSource.getFundingSourceInfo().getLeadCenter() != null
          && fsi.getLeadCenter().getId().equals(fundingSource.getFundingSourceInfo().getLeadCenter().getId()))
        .distinct().collect(Collectors.toList());
    }

    if (fundingSourceInfos != null) {
      Long lastID = null;
      for (FundingSourceInfo fundingSourceInfo : fundingSourceInfos) {
        if (fundingSourceInfo.getFundingSource() != null) {
          if (lastID != null) {
            if (lastID != fundingSourceInfo.getFundingSource().getId()) {
              fundingSources.add(fundingSourceInfo.getFundingSource());
            }
          } else {
            fundingSources.add(fundingSourceInfo.getFundingSource());
            lastID = fundingSourceInfo.getFundingSource().getId();
          }
        }
      }
    }

    if (fundingSources != null) {
      for (FundingSource fundingSource : fundingSources) {
        if (fundingSource != null && fundingSource.getProjectBudgets() != null) {
          List<ProjectBudget> tempBudgets = fundingSource.getProjectBudgets().stream()
            .filter(pb -> pb.isActive() && pb.getProject() != null && pb.getProject().isActive()
              && pb.getFundingSource() != null && pb.getFundingSource().getId().equals(this.fundingSource.getId()))
            .collect(Collectors.toList());
          if (tempBudgets != null) {
            List<Long> ids = new ArrayList<>();
            for (ProjectBudget budget : tempBudgets) {
              if (budget != null && budget.getProject() != null && budget.getProject().getId() != null
                && budget.getFundingSource() != null
                && budget.getFundingSource().getId().equals(this.fundingSource.getId())) {
                if (ids != null) {
                  if (!ids.contains(budget.getProject().getId())) {
                    ids.add(budget.getProject().getId());
                    projectBudgets.add(budget);
                  }
                } else {
                  ids.add(budget.getProject().getId());
                  projectBudgets.add(budget);
                }
              }
              crpPhase = this.getCrpPhase(budget.getFundingSource().getCrp().getId(), this.getActualPhase().getYear(),
                this.getActualPhase().getDescription());

              if (crpPhase != null) {
                tempBudgets = tempBudgets.stream()
                  .filter(b -> b.getProject().getProjecInfoPhase(crpPhase) != null
                    && b.getProject().getProjecInfoPhase(crpPhase).getPhase().equals(crpPhase)
                    || (b.getFundingSource().getFundingSourceInfo(crpPhase) != null
                      && b.getFundingSource().getFundingSourceInfo().getPhase().equals(crpPhase))
                      && b.getFundingSource().getId().equals(this.fundingSource.getId()) && b.getPhase() != null
                      && b.getPhase().equals(crpPhase))
                  .collect(Collectors.toList());
              }
            }
            fundingSourceShow.setProjectBudgetsList(tempBudgets);
          }
        }
      }
    }
  }

  // methos to download link file
  public String getPath(String fsId) {
    return config.getDownloadURL() + "/" + this.getStudyFileUrlPath(fsId).replace('\\', '/');
  }

  public void getProjectsMappedIds() {
    projectsMappedIDs = new ArrayList<>();
    for (ProjectBudget projectBudget : fundingSourceShow.getProjectBudgets()) {
      if (projectBudget.getProject() != null
        && !projectsMappedIDs.contains(projectBudget.getProject().getId().toString())) {
        projectsMappedIDs.add(projectBudget.getProject().getId().toString());
      }
    }
  }

  public List<LocElement> getRegionLists() {
    return regionLists;
  }


  public List<LocElementType> getScopeRegionLists() {
    return scopeRegionLists;
  }


  public Map<String, String> getStatus() {
    return status;
  }


  public String getStudyFileUrlPath(String fsId) {
    return config.getFundingSourceFolder(this.getCrpSession()) + File.separator + fundingSourceID + File.separator
      + "fundingSourceFilesResearch" + File.separator;
  }


  public String getTransaction() {
    return transaction;
  }


  public List<Project> getUserProjects() {
    return userProjects;
  }

  public boolean isRegion() {
    return region;
  }

  public void mapFundingSource() {


    FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();;
    // fundingSourceInstitution.setInstitution(institutionManager.getInstitutionById(Integer.parseInt(partner)));
    fundingSourceInstitution.setFundingSource(fundingSource);
    fundingSourceInstitution.setPhase(this.getActualPhase());
    fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);

  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    fundingSourceID =
      Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.FUNDING_SOURCE_REQUEST_ID)));

    // Budget Types list
    budgetTypesList = budgetTypeManager.findAll();
    projectBudgetsListOtherCRP = new ArrayList<>();

    region = false;

    // Regions List
    List<LocElement> locElement = new ArrayList<>();
    locElement = locElementManager.findAll();

    regionLists = new ArrayList<>(locElement.stream()
      .filter(le -> le.isActive() && le.getLocElementType() != null && le.getLocElementType().getId() == 1)
      .collect(Collectors.toList()));
    Collections.sort(regionLists, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    // Region Scope List
    scopeRegionLists = new ArrayList<>(locElementTypeManager.findAll().stream()
      .filter(le -> le.isActive() && le.getCrp() != null && le.getCrp().equals(loggedCrp) && le.isScope())
      .collect(Collectors.toList()));

    // Country List
    countryLists = new ArrayList<>(locElement.stream()
      .filter(le -> le.isActive() && le.getLocElementType() != null && le.getLocElementType().getId() == 2)
      .collect(Collectors.toList()));
    Collections.sort(countryLists, (c1, c2) -> c1.getName().compareTo(c2.getName()));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      FundingSource history = (FundingSource) auditLogManager.getHistory(transaction);

      if (history != null) {
        fundingSource = history;


        Map<String, String> specialList = new HashMap<>();

        this.setDifferences(historyComparator.getDifferences(transaction, specialList, "fundingSource"));

      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);
    }

    fundingSource.setFundingSourceInfo(fundingSource.getFundingSourceInfo(this.getActualPhase()));
    if (fundingSource != null) {

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        fundingSource = (FundingSource) autoSaveReader.readFromJson(jReader);
        reader.close();

        this.setDraft(true);
        FundingSource fundingSourceDB = fundingSourceManager.getFundingSourceById(fundingSourceID);

        fundingSource.setProjectBudgetsList(fundingSourceDB.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getProject().isActive() && pb.getPhase() != null
            && pb.getPhase().equals(this.getActualPhase())
            && pb.getProject().getProjecInfoPhase(this.getActualPhase()) != null)
          .collect(Collectors.toList()));


        if (fundingSource.getFundingSourceInfo().getFile() != null) {
          if (fundingSource.getFundingSourceInfo().getFile().getId() != null) {
            fundingSource.getFundingSourceInfo()
              .setFile(fileDBManager.getFileDBById(fundingSource.getFundingSourceInfo().getFile().getId()));
          } else {
            fundingSource.getFundingSourceInfo().setFile(null);
          }
        }

        // fileResearch validation
        // 20180124 - @jurodca
        if (this.hasSpecificities(APConstants.CRP_HAS_RESEARCH_HUMAN)) {
          if (fundingSource.getFundingSourceInfo().getFileResearch() != null) {
            if (fundingSource.getFundingSourceInfo().getFileResearch().getId() != null) {
              fundingSource.getFundingSourceInfo().setFileResearch(
                fileDBManager.getFileDBById(fundingSource.getFundingSourceInfo().getFileResearch().getId()));
            }
          }
        }


        if (fundingSource.getInstitutions() != null) {
          fundingSourceInstitutions = new ArrayList<>();
          for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
            if (fundingSourceInstitution != null) {
              fundingSourceInstitution.setInstitution(
                institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
            }
          }
        }


        if (fundingSource.getDivisions() != null) {
          for (FundingSourceDivision fundingSourceDivision : fundingSource.getDivisions()) {
            if (fundingSourceDivision != null) {
              fundingSourceDivision.setDivision(
                partnerDivisionManager.getPartnerDivisionById(fundingSourceDivision.getDivision().getId()));
            }
          }
        }

        if (fundingSource.getFundingRegions() != null) {
          region = true;
          for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingRegions()) {
            if (fundingSourceLocation != null) {

              if (!fundingSourceLocation.isScope()) {
                fundingSourceLocation
                  .setLocElement(locElementManager.getLocElementById(fundingSourceLocation.getLocElement().getId()));


              } else {
                LocElementType elementType =
                  locElementTypeManager.getLocElementTypeById(fundingSourceLocation.getLocElement().getId());

                LocElement element = new LocElement();
                element.setId(elementType.getId());
                element.setName(elementType.getName());
                fundingSourceLocation.setLocElement(element);
              }

            }
          }
        }

        if (fundingSource.getFundingCountry() != null) {
          for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingCountry()) {
            if (fundingSourceLocation != null) {
              fundingSourceLocation.setLocElement(
                locElementManager.getLocElementByISOCode(fundingSourceLocation.getLocElement().getIsoAlpha2()));
            }
          }
        }


      } else {
        this.setDraft(false);
        fundingSource.setBudgets(fundingSource.getFundingSourceBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));

        fundingSource.setInstitutions(new ArrayList<>(fundingSource.getFundingSourceInstitutions().stream()
          .filter(pb -> pb.isActive() && pb.getPhase() != null && pb.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList())));

        fundingSourceInstitutions = new ArrayList<>();
        if (fundingSource.getInstitutions() != null) {
          for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
            fundingSourceInstitutions
              .add(institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
          }
        }

        fundingSource.setDivisions(new ArrayList<>(fundingSource.getFundingSourceDivisions().stream()
          .filter(pb -> pb.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList())));

        fundingSource.setProjectBudgetsList(fundingSource.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getProject().isActive() && pb.getPhase() != null
            && pb.getPhase().equals(this.getActualPhase())
            && pb.getProject().getProjecInfoPhase(this.getActualPhase()) != null)
          .collect(Collectors.toList()));

        this.getOtherBudgetContribution();
        // Get info of projects that this funding source is contributing in others crps

        /*
         * List<FundingSourceInfo> fundingSourceInfos = new ArrayList<>();
         * List<FundingSource> fundingSources = new ArrayList<>();
         * List<ProjectBudget> projectBudgets = new ArrayList<>();
         * fundingSourceShow = new FundingSource();
         * if (fundingSource != null && fundingSource.getFundingSourceInfo() != null
         * && fundingSource.getFundingSourceInfo().getFinanceCode() != null
         * && fundingSource.getFundingSourceInfo().getLeadCenter() != null) {
         * fundingSourceInfos = fundingSourceInfoManager.findAll().stream()
         * .filter(fsi -> fsi != null && fsi.isActive() && fsi.getFundingSource() != null
         * && fsi.getFundingSource().getCrp() != null
         * && !(fsi.getFundingSource().getCrp().getId().equals(this.getCurrentCrp().getId()))
         * && fsi.getFinanceCode() != null
         * && fsi.getFinanceCode().equals(fundingSource.getFundingSourceInfo().getFinanceCode())
         * && fsi.getLeadCenter() != null
         * && fsi.getLeadCenter().getId().equals(fundingSource.getFundingSourceInfo().getLeadCenter().getId()))
         * .distinct().collect(Collectors.toList());
         * }
         * if (fundingSourceInfos != null) {
         * Long lastID = null;
         * for (FundingSourceInfo fundingSourceInfo : fundingSourceInfos) {
         * if (fundingSourceInfo.getFundingSource() != null) {
         * if (lastID != null) {
         * if (lastID != fundingSourceInfo.getFundingSource().getId()) {
         * fundingSources.add(fundingSourceInfo.getFundingSource());
         * }
         * } else {
         * fundingSources.add(fundingSourceInfo.getFundingSource());
         * lastID = fundingSourceInfo.getFundingSource().getId();
         * }
         * }
         * }
         * }
         * if (fundingSources != null) {
         * for (FundingSource fundingSource : fundingSources) {
         * if (fundingSource != null && fundingSource.getProjectBudgets() != null) {
         * List<ProjectBudget> tempBudgets = fundingSource.getProjectBudgets().stream()
         * .filter(pb -> pb.isActive() && pb.getProject() != null && pb.getProject().isActive())
         * .collect(Collectors.toList());
         * if (tempBudgets != null) {
         * List<Long> ids = new ArrayList<>();
         * for (ProjectBudget budget : tempBudgets) {
         * if (budget != null && budget.getProject() != null && budget.getProject().getId() != null) {
         * if (ids != null) {
         * if (!ids.contains(budget.getProject().getId())) {
         * ids.add(budget.getProject().getId());
         * projectBudgets.add(budget);
         * System.out
         * .println("entro aqui xx " + this.getCRPPhase(budget.getFundingSource().getCrp().getId(),
         * this.getActualPhase().getYear(), this.getActualPhase().getDescription()));
         * }
         * } else {
         * ids.add(budget.getProject().getId());
         * projectBudgets.add(budget);
         * System.out.println("entro aqui");
         * }
         * }
         * }
         * fundingSourceShow.setProjectBudgetsList(tempBudgets);
         * System.out.println("test " + fundingSourceShow.getProjectBudgetsList().get(0).getProject());
         * }
         * }
         * }
         * }
         */

        if (this.hasSpecificities(APConstants.CRP_HAS_RESEARCH_HUMAN)) {
          if (fundingSource.getFundingSourceInfo().getFileResearch() != null) {
            if (fundingSource.getFundingSourceInfo().getFileResearch().getId() != null) {
              fundingSource.getFundingSourceInfo().setFileResearch(
                fileDBManager.getFileDBById(fundingSource.getFundingSourceInfo().getFileResearch().getId()));
            }
          }
        }

        /**
         * Temporary Performance improvement until we develop a single query to fetch all the fundingSource data for
         * this screen
         */
        @SuppressWarnings("unused")
        List<FundingSourceLocation> findAllByFundingSourceId =
          fundingSourceLocationsManager.findAllByFundingSourceId(fundingSourceID);

        if (fundingSource.getFundingSourceLocations() != null) {

          List<FundingSourceLocation> countries = new ArrayList<>(fundingSource.getFundingSourceLocations().stream()
            .filter(fl -> fl.isActive() && fl.getPhase().equals(this.getActualPhase()) && fl.getLocElementType() == null
              && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId() == 2)
            .collect(Collectors.toList()));

          fundingSource.setFundingCountry(new ArrayList<>(countries));

          List<FundingSourceLocation> regions = new ArrayList<>(fundingSource.getFundingSourceLocations().stream()
            .filter(fl -> fl.isActive() && fl.getLocElement() != null && fl.getPhase().equals(this.getActualPhase())
              && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 1)
            .collect(Collectors.toList()));

          List<FundingSourceLocation> regionsWScope = new ArrayList<>();
          if (regions.size() > 0) {
            region = true;
            for (FundingSourceLocation fundingSourceLocation : regions) {
              fundingSourceLocation.setScope(false);
              regionsWScope.add(fundingSourceLocation);
            }
          }

          regions = new ArrayList<>(fundingSource
            .getFundingSourceLocations().stream().filter(fl -> fl.isActive() && fl.getLocElementType() != null
              && fl.getLocElement() == null && fl.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList()));

          if (regions.size() > 0) {
            region = true;
            for (FundingSourceLocation fundingSourceLocation : regions) {
              fundingSourceLocation.setScope(true);
              LocElement element = new LocElement();
              element.setId(fundingSourceLocation.getLocElementType().getId());
              element.setName(fundingSourceLocation.getLocElementType().getName());
              fundingSourceLocation.setLocElement(element);
              regionsWScope.add(fundingSourceLocation);
            }
          }

          fundingSource.setFundingRegions(new ArrayList<>(regionsWScope));

        }


      }

      status = new HashMap<>();
      // projectStatuses = new HashMap<>();
      List<FundingStatusEnum> list = Arrays.asList(FundingStatusEnum.values());
      for (FundingStatusEnum projectStatusEnum : list) {
        switch (FundingStatusEnum.getValue(Integer.parseInt(projectStatusEnum.getStatusId()))) {
          case Pipeline:
          case Informally:
            if (this.hasSpecificities(APConstants.CRP_STATUS_FUNDING_SOURCES)) {
              status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
            }
            break;
          default:
            status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
            break;
        }
      }


      if (fundingSource.getInstitutions() != null) {
        for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
          if (fundingSourceInstitution != null) {
            fundingSourceInstitution
              .setInstitution(institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
          }
        }
      }
      institutions = new ArrayList<>();

      if (fundingSource.getDivisions() != null) {
        for (FundingSourceDivision fundingSourceDivision : fundingSource.getDivisions()) {
          if (fundingSourceDivision != null) {
            fundingSourceDivision
              .setDivision(partnerDivisionManager.getPartnerDivisionById(fundingSourceDivision.getDivision().getId()));
          }
        }
      }
      divisions = new ArrayList<>();


      List<CrpPpaPartner> ppaPartners = crpPpaPartnerManager.findAll();

      if (ppaPartners != null && !ppaPartners.isEmpty()) {
        ppaPartners = ppaPartners.stream().filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue()
          && c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
        for (CrpPpaPartner crpPpaPartner : ppaPartners) {
          institutions.add(crpPpaPartner.getInstitution());
        }
      }
      List<Institution> allInstitutions = null;
      allInstitutions = institutionManager.findAll();

      if (fundingSource.getFundingSourceInfo() != null) {
        if (fundingSource.getFundingSourceInfo().getBudgetType() != null) {
          // if the funding source is type center funds -- institutions are ppa
          if (fundingSource.getFundingSourceInfo().getBudgetType() != null
            && fundingSource.getFundingSourceInfo().getBudgetType().getId() != null
            && fundingSource.getFundingSourceInfo().getBudgetType().getId().longValue() == 4) {

            institutionsDonors = new ArrayList<>();

            for (Institution institutionObject : allInstitutions) {
              // validate if the institutions is PPA
              // if (this.isPPA(institutionObject)) {
              institutionsDonors.add(institutionObject);
              // }

            }

          } else {
            // if the funding source is type w1 -- institutions are cgiar center
            if (fundingSource.getFundingSourceInfo().getBudgetType() != null
              && fundingSource.getFundingSourceInfo().getBudgetType().getId() != null
              && fundingSource.getFundingSourceInfo().getBudgetType().getId().longValue() == 1) {
              institutionsDonors =
                allInstitutions.stream().filter(i -> i.isActive() && i.getInstitutionType().getId().intValue() == 3)
                  .collect(Collectors.toList());
            } else {

              // if the funding source is type bilateral -- institutions are not cgiar center
              institutionsDonors = allInstitutions.stream().filter(i -> i.isActive()).collect(Collectors.toList());
              // institutionsDonors.removeAll(institutions);
            }

          }
        } else {
          // if the funding source don't hava a selected type -- institutions are not cgiar center

          institutionsDonors = allInstitutions.stream().filter(i -> i.isActive()).collect(Collectors.toList());
        }

        institutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        institutionsDonors.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

        liaisonInstitutions = new ArrayList<>();

        liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions());
        liaisonInstitutions.addAll(
          liaisonInstitutionManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));

      }


    } else

    {
      LOG.debug("No FundingSource found for ID : " + fundingSourceID);
    }

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

    divisions = new ArrayList<>(
      partnerDivisionManager.findAll().stream().filter(pd -> pd.isActive()).collect(Collectors.toList()));
    String params[] = {loggedCrp.getAcronym(), fundingSource.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_FUNDING_SOURCE_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      fundingSource.getFundingSourceInfo().setFile(null);
      fundingSource.getFundingSourceInfo().setFileResearch(null);
      fundingSource.getFundingSourceInfo().setHasFileResearch(null);
      if (fundingSource.getInstitutions() != null) {
        for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
          fundingSourceInstitution
            .setInstitution(institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
        }
        fundingSource.getFundingSourceInfo().setW1w2(null);
        fundingSource.getInstitutions().clear();
      }

      if (fundingSource.getDivisions() != null) {
        for (FundingSourceDivision fundingSourceDivision : fundingSource.getDivisions()) {
          fundingSourceDivision
            .setDivision(partnerDivisionManager.getPartnerDivisionById(fundingSourceDivision.getDivision().getId()));
        }
        fundingSource.getDivisions().clear();
      }

      /**
       * This is a real nasty hack to get around an issue caused by bug #1124. We set the istitution to null and rely on
       * the save() method updating the institution regardless of whether or not the user actually changed this value.
       * If we don't do this hibernate will think that we are modifying the id of a managed entity (i.e. the
       * institution) when a user does actually update the institution. In this scenario hibernate will throw an
       * exception saying that we are trying to modify the id of a managed entity when the validate method gets called
       * (the validate method will perform a query on the funding source which triggers an auto-flush).
       * A better solution would be to have a DTO (assuming we don't want to have our hibernate entities AND their child
       * collections bound to the freemarker templates) and the freemarker template binds to that.
       * The prepare method would map the database values to a FundingSourceDTO (or better call a mapper class to do
       * this) and the save method would map the values from the FundingSourceDTO to the FundingSource hibernate entity.
       */

      /*
       * if (fundingSource.getInstitutions() != null) {
       * for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
       * fundingSourceInstitution
       * .setInstitution(institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
       * }
       * fundingSource.getInstitutions().clear();
       * }
       * if (fundingSource.getFundingRegions() != null) {
       * fundingSource.getFundingRegions().clear();
       * }
       * if (fundingSource.getFundingCountry() != null) {
       * fundingSource.getFundingCountry().clear();
       * }
       */

      fundingSource.getFundingSourceInfo(this.getActualPhase()).setW1w2(null);
      fundingSource.getFundingSourceInfo(this.getActualPhase()).setFile(null);
      fundingSource.getFundingSourceInfo(this.getActualPhase()).setLeadCenter(null);

      fundingSource.getFundingSourceInfo(this.getActualPhase()).setDirectDonor(null);
      fundingSource.getFundingSourceInfo(this.getActualPhase()).setOriginalDonor(null);
      fundingSource.setBudgets(null);
      fundingSource.getFundingSourceInfo(this.getActualPhase()).setBudgetType(null);
      fundingSource.setFundingRegions(null);
      fundingSource.setFundingCountry(null);
      fundingSource.getFundingSourceInfo().setPartnerDivision(null);


      return;
    }
  }

  @Override
  public String save() {
    FundingSource fundingSourceDB = fundingSourceManager.getFundingSourceById(fundingSource.getId());
    if (this.hasPermission("canEdit")) {

      FundingSourceInfo fundingSourceInfoDB =
        fundingSourceInfoManager.getFundingSourceInfoById(fundingSource.getFundingSourceInfo().getId());

      // if donor has a select option, no option put donor null
      if (fundingSource.getFundingSourceInfo().getDirectDonor() != null
        && fundingSource.getFundingSourceInfo().getDirectDonor().getId() != null
        && fundingSource.getFundingSourceInfo().getDirectDonor().getId().longValue() != -1) {
        fundingSourceInfoDB.setDirectDonor(fundingSource.getFundingSourceInfo().getDirectDonor());
      } else {
        fundingSourceInfoDB.setDirectDonor(null);
      }
      if (fundingSource.getFundingSourceInfo().getOriginalDonor() != null
        && fundingSource.getFundingSourceInfo().getOriginalDonor().getId() != null
        && fundingSource.getFundingSourceInfo().getOriginalDonor().getId().longValue() != -1) {
        fundingSourceInfoDB.setOriginalDonor(fundingSource.getFundingSourceInfo().getOriginalDonor());
      } else {
        fundingSourceInfoDB.setOriginalDonor(null);
      }

      if (fundingSource.getFundingSourceInfo().getLeadCenter() != null
        && fundingSource.getFundingSourceInfo().getLeadCenter().getId() != null
        && fundingSource.getFundingSourceInfo().getLeadCenter().getId().longValue() != -1) {
        fundingSourceInfoDB.setLeadCenter(fundingSource.getFundingSourceInfo().getLeadCenter());
      } else {
        fundingSourceInfoDB.setLeadCenter(null);
      }


      fundingSourceInfoDB.setTitle(fundingSource.getFundingSourceInfo().getTitle());
      fundingSourceInfoDB.setStatus(fundingSource.getFundingSourceInfo().getStatus());
      fundingSourceInfoDB.setStartDate(fundingSource.getFundingSourceInfo().getStartDate());
      fundingSourceInfoDB.setEndDate(fundingSource.getFundingSourceInfo().getEndDate());
      fundingSourceInfoDB.setGlobal(fundingSource.getFundingSourceInfo().isGlobal());

      if (fundingSourceInfoDB.getHasFileResearch() == null
        && fundingSource.getFundingSourceInfo().getHasFileResearch() != null) {
        fundingSourceInfoDB.setHasFileResearch(fundingSource.getFundingSourceInfo().getHasFileResearch());
      }

      fundingSourceInfoDB.setGrantAmount(fundingSource.getFundingSourceInfo().getGrantAmount());

      fundingSourceInfoDB.setFinanceCode(fundingSource.getFundingSourceInfo().getFinanceCode().toUpperCase());
      fundingSourceInfoDB.setContactPersonEmail(fundingSource.getFundingSourceInfo().getContactPersonEmail());
      fundingSourceInfoDB.setContactPersonName(fundingSource.getFundingSourceInfo().getContactPersonName());
      if (fundingSource.getFundingSourceInfo().getBudgetType() != null) {
        fundingSourceInfoDB.setBudgetType(fundingSource.getFundingSourceInfo().getBudgetType());
      }
      fundingSourceInfoDB.setSynced(fundingSource.getFundingSourceInfo().getSynced());
      fundingSourceInfoDB.setSyncedDate(fundingSource.getFundingSourceInfo().getSyncedDate());
      fundingSourceInfoDB.setExtensionDate(fundingSource.getFundingSourceInfo().getExtensionDate());

      fundingSourceDB.setBudgets(fundingSource.getBudgets());
      /*
       * if (fundingSource.getFundingSourceInfo().getPartnerDivision() == null
       * || fundingSource.getFundingSourceInfo().getPartnerDivision().getId() == null
       * || fundingSource.getFundingSourceInfo().getPartnerDivision().getId().longValue() == -1) {
       * fundingSourceInfoDB.setPartnerDivision(null);
       * } else {
       * fundingSourceInfoDB.setPartnerDivision(fundingSource.getFundingSourceInfo().getPartnerDivision());
       * }
       */
      if (fundingSource.getFundingSourceInfo().getW1w2() == null) {
        fundingSourceInfoDB.setW1w2(false);
      } else {
        fundingSourceInfoDB.setW1w2(true);
      }
      fundingSourceInfoDB.setDescription(fundingSource.getFundingSourceInfo().getDescription());

      if (fundingSource.getFundingSourceInfo().getFile() != null) {
        if (fundingSource.getFundingSourceInfo().getFile().getId() == null) {
          fundingSource.getFundingSourceInfo().setFile(null);
        } else {
          fundingSourceInfoDB.setFile(fundingSource.getFundingSourceInfo().getFile());
        }
      }

      // fileResearch validation
      // 20180124 - @jurodca
      if (this.hasSpecificities(APConstants.CRP_HAS_RESEARCH_HUMAN)) {
        if (fundingSource.getFundingSourceInfo().getHasFileResearch() != null) {
          if (fundingSource.getFundingSourceInfo().getHasFileResearch().booleanValue()) {

            if (fundingSource.getFundingSourceInfo().getFileResearch() != null) {
              if (fundingSource.getFundingSourceInfo().getFileResearch().getId() == null) {
                fundingSourceInfoDB.setFileResearch(null);
              } else {
                fundingSourceInfoDB.setFileResearch(fundingSource.getFundingSourceInfo().getFileResearch());
              }
            }
          } else {
            fundingSourceInfoDB.setFileResearch(null);
          }
        }
      }

      /*
       * if (file != null) {
       * fundingSourceDB
       * .setFile(this.getFileDB(fundingSourceDB.getFile(), file, fileFileName, this.getFundingSourceFilePath()));
       * FileManager.copyFile(file, this.getFundingSourceFilePath() + fundingSourceDB.getFile().getFileName());
       * }
       * try {
       * if (fundingSourceDB.getFile().getFileName().isEmpty()) {
       * fundingSourceDB.setFile(null);
       * }
       * } catch (Exception e) {
       * }
       */

      if (fundingSource.getBudgets() != null) {

        // TODO find out why the fundingSource budgets are being set to null.
        fundingSource.getBudgets().removeIf(Objects::isNull);

        for (FundingSourceBudget fundingSourceBudget : fundingSource.getBudgets()) {

          if (fundingSourceBudget.getId() == null) {
            fundingSourceBudget.setFundingSource(fundingSourceDB);
            fundingSourceBudget.setPhase(this.getActualPhase());
            fundingSourceBudget = fundingSourceBudgetManager.saveFundingSourceBudget(fundingSourceBudget);
          } else {
            FundingSourceBudget fundingSourceBudgetDB =
              fundingSourceBudgetManager.getFundingSourceBudgetById(fundingSourceBudget.getId());
            fundingSourceBudgetDB.setBudget(fundingSourceBudget.getBudget());
            fundingSourceBudgetDB.setFundingSource(fundingSourceDB);
            fundingSourceBudgetDB.setPhase(this.getActualPhase());
            fundingSourceBudgetDB = fundingSourceBudgetManager.saveFundingSourceBudget(fundingSourceBudgetDB);
          }
        }
      }

      // if remove some institution or add new we call clearPermissionsCache to refresh permissions -CGARCIA
      boolean instituionsEdited = false;
      if (fundingSource.getInstitutions() != null) {


        for (FundingSourceInstitution fundingSourceInstitution : fundingSourceDB.getFundingSourceInstitutions().stream()
          .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
          if (!fundingSource.getInstitutions().contains(fundingSourceInstitution)) {
            fundingSourceInstitutionManager.deleteFundingSourceInstitution(fundingSourceInstitution.getId());
            instituionsEdited = true;
          }
        }
        for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
          if (fundingSourceInstitution.getId() == null || fundingSourceInstitution.getId().longValue() == -1) {

            // fundingSourceInstitution.setId(null);
            fundingSourceInstitution.setFundingSource(fundingSourceDB);
            fundingSourceInstitution.setPhase(this.getActualPhase());
            fundingSourceInstitution =
              fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
            instituionsEdited = true;
          } else {
            FundingSourceInstitution fundingSourceInstitutionDB =
              fundingSourceInstitutionManager.getFundingSourceInstitutionById(fundingSourceInstitution.getId());
            fundingSourceInstitutionDB.setFundingSource(fundingSourceDB);
            fundingSourceInstitutionDB.setPhase(this.getActualPhase());
            fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitutionDB);
          }
        }
      }

      boolean divisionsEdited = false;

      if (fundingSource.getFundingSourceDivisions() != null) {
        for (FundingSourceDivision fundingSourceDivision : fundingSourceDB.getFundingSourceDivisions().stream()
          .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
          if (!fundingSource.getDivisions().contains(fundingSourceDivision)) {
            fundingSourceDivisionManager.deleteFundingSourceDivision(fundingSourceDivision.getId());
            divisionsEdited = true;
          }
        }
        if (fundingSource.getDivisions() != null) {
          for (FundingSourceDivision fundingSourceDivision : fundingSource.getDivisions()) {
            if (fundingSourceDivision.getId() == null || fundingSourceDivision.getId().longValue() == -1) {
              FundingSourceDivision fundingSourceDivisionSave = new FundingSourceDivision();
              fundingSourceDivisionSave.setFundingSource(fundingSourceDB);
              fundingSourceDivisionSave.setPhase(this.getActualPhase());

              PartnerDivision partnerDivision =
                partnerDivisionManager.getPartnerDivisionById(fundingSourceDivision.getDivision().getId());

              fundingSourceDivisionSave.setDivision(partnerDivision);

              fundingSourceDivisionManager.saveFundingSourceDivision(fundingSourceDivisionSave);
              // This is to add innovationCrpSave to generate correct auditlog.
              fundingSource.getFundingSourceDivisions().add(fundingSourceDivisionSave);
              divisionsEdited = true;
            } else {
              FundingSourceDivision fundingSourceDivisionDB =
                fundingSourceDivisionManager.getFundingSourceDivisionById(fundingSourceDivision.getId());
              fundingSourceDivisionDB.setFundingSource(fundingSourceDB);
              fundingSourceDivisionManager.saveFundingSourceDivision(fundingSourceDivisionDB);
            }
          }
        }
      }

      // Check if the funding source type is different to the previous one in DB
      List<ProjectBudget> projectBudgets = fundingSource.getProjectBudgetsList();
      if (projectBudgets != null) {

        projectBudgets = projectBudgets.stream()
          .filter(pb -> pb.isActive() && pb.getProject() != null && pb.getProject().isActive()
            && pb.getFundingSource() != null && pb.getFundingSource().getId().equals(this.fundingSource.getId())
            && pb.getPhase() != null && pb.getPhase().getId().equals(this.getActualPhase().getId())
            && pb.getYear() == this.getActualPhase().getYear())
          .collect(Collectors.toList());
      }

      if (projectBudgets != null && !projectBudgets.isEmpty() && projectBudgets.get(0) != null
        && fundingSource.getFundingSourceInfo().getBudgetType() != null) {
        ProjectBudget projectBudget;
        BudgetType budgetType;
        budgetType = budgetTypeManager.getBudgetTypeById(fundingSource.getFundingSourceInfo().getBudgetType().getId());
        projectBudget = projectBudgets.get(0);
        projectBudget.setBudgetType(budgetType);
        projectBudgetManager.saveProjectBudget(projectBudget);
      }


      if (instituionsEdited) {
        this.clearPermissionsCache();
      }


      this.saveLocations(fundingSourceDB);


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.FUNDING_SOURCES_BUDGETS_RELATION);
      relationsName.add(APConstants.FUNDING_SOURCES_LOCATIONS_RELATION);
      relationsName.add(APConstants.FUNDING_SOURCES_INFO);
      relationsName.add(APConstants.FUNDING_SOURCES_INSTITUTIONS_RELATION);
      relationsName.add(APConstants.FUNDING_SOURCES_DIVISIONS_RELATION);

      // fundingSourceDB = fundingSourceManager.getFundingSourceById(fundingSourceID);

      fundingSourceInfoDB.setPhase(this.getActualPhase());
      fundingSourceInfoDB.setFundingSource(fundingSource);
      if (fundingSource.getFundingSourceInfo().getOriginalDonor() != null
        && (fundingSource.getFundingSourceInfo().getOriginalDonor().getId() == null
          || fundingSource.getFundingSourceInfo().getOriginalDonor().getId() <= 0)) {
        fundingSourceInfoDB.setOriginalDonor(null);
      }
      /**
       * 
       */
      fundingSourceInfoManager.saveFundingSourceInfo(fundingSourceInfoDB);


      /**
       * The following is required because we need to update something on the @FundingSource if we want a row created in
       * the auditlog table.
       */
      this.setModificationJustification(fundingSourceDB);

      fundingSourceDB = fundingSourceManager.saveFundingSource(fundingSourceDB, this.getActionName(), relationsName,
        this.getActualPhase());

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      Collection<String> messages = this.getActionMessages();
      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
        for (String key : keys) {
          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }

      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      return SUCCESS;
    } else

    {
      return NOT_AUTHORIZED;
    }
  }


  /**
   * Funding Source Locations
   * 
   * @param fundingSourceDB
   */
  public void saveLocations(FundingSource fundingSourceDB) {

    if (fundingSource.getFundingRegions() != null) {

      List<FundingSourceLocation> regions = new ArrayList<>(fundingSourceDB.getFundingSourceLocations().stream()
        .filter(fl -> fl.isActive() && fl.getPhase().equals(this.getActualPhase()) && fl.getLocElementType() == null
          && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId() == 1)
        .collect(Collectors.toList()));

      if (regions != null && regions.size() > 0) {

        if (region) {
          for (FundingSourceLocation fundingSourceLocation : regions) {
            if (!fundingSource.getFundingRegions().contains(fundingSourceLocation)) {
              fundingSourceLocationsManager.deleteFundingSourceLocations(fundingSourceLocation.getId());
            }
          }
        } else {
          for (FundingSourceLocation fundingSourceLocation : regions) {
            fundingSourceLocationsManager.deleteFundingSourceLocations(fundingSourceLocation.getId());
          }
        }
      }

      regions = new ArrayList<>(fundingSourceDB.getFundingSourceLocations().stream().filter(fl -> fl.isActive()
        && fl.getPhase().equals(this.getActualPhase()) && fl.getLocElementType() != null && fl.getLocElement() == null)
        .collect(Collectors.toList()));

      if (regions != null && regions.size() > 0) {
        if (region) {
          for (FundingSourceLocation fundingSourceLocation : regions) {
            if (!fundingSource.getFundingRegions().contains(fundingSourceLocation)) {
              fundingSourceLocationsManager.deleteFundingSourceLocations(fundingSourceLocation.getId());
            }
          }
        } else {
          for (FundingSourceLocation fundingSourceLocation : regions) {
            fundingSourceLocationsManager.deleteFundingSourceLocations(fundingSourceLocation.getId());
          }
        }
      }

      for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingRegions()) {


        if (fundingSourceLocation.getId() == null || fundingSourceLocation.getId() == -1) {

          FundingSourceLocation fundingSourceLocationSave = new FundingSourceLocation();
          fundingSourceLocationSave.setFundingSource(fundingSourceDB);
          fundingSourceLocationSave.setPhase(this.getActualPhase());
          fundingSourceLocationSave.setPercentage(fundingSourceLocation.getPercentage());
          if (!fundingSourceLocation.isScope()) {
            LocElement locElement = locElementManager.getLocElementById(fundingSourceLocation.getLocElement().getId());

            fundingSourceLocationSave.setLocElement(locElement);
          } else {
            long elementId = fundingSourceLocation.getLocElement().getId();
            LocElementType elementType = locElementTypeManager.getLocElementTypeById(elementId);

            fundingSourceLocationSave.setLocElementType(elementType);
          }

          fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationSave);
        } else {
          FundingSourceLocation fundingSourceLocationDB =
            fundingSourceLocationsManager.getFundingSourceLocationsById(fundingSourceLocation.getId());
          fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationDB);

        }
      }


    }

    if (fundingSource.getFundingCountry() != null) {

      List<FundingSourceLocation> countries = new ArrayList<>(fundingSourceDB.getFundingSourceLocations().stream()
        .filter(fl -> fl.isActive() && fl.getPhase().equals(this.getActualPhase()) && fl.getLocElementType() == null
          && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId() == 2)
        .collect(Collectors.toList()));

      if (countries != null && countries.size() > 0) {
        for (FundingSourceLocation fundingSourceLocation : countries) {
          if (!fundingSource.getFundingCountry().contains(fundingSourceLocation)) {
            fundingSourceLocationsManager.deleteFundingSourceLocations(fundingSourceLocation.getId());
          }
        }
      }

      for (FundingSourceLocation fundingSourceLocation : fundingSource.getFundingCountry()) {


        if (fundingSourceLocation.getId() == null || fundingSourceLocation.getId() == -1) {

          FundingSourceLocation fundingSourceLocationSave = new FundingSourceLocation();
          fundingSourceLocationSave.setFundingSource(fundingSourceDB);
          fundingSourceLocationSave.setPhase(this.getActualPhase());
          LocElement locElement =
            locElementManager.getLocElementByISOCode(fundingSourceLocation.getLocElement().getIsoAlpha2());

          fundingSourceLocationSave.setLocElement(locElement);

          fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationSave);
        } else {
          // Looks like we don't need to do anything.
          // FundingSourceLocation fundingSourceLocationDB =
          // fundingSourceLocationsManager.getFundingSourceLocationsById(fundingSourceLocation.getId());
          // fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationDB);

        }
      }
    }

    // Check empty regions and countries
    List<FundingSourceLocation> regionsDB = new ArrayList<>(fundingSourceDB
      .getFundingSourceLocations().stream().filter(fl -> fl.isActive() && fl.getLocElementType() == null
        && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId() == 1)
      .collect(Collectors.toList()));

    // If regions were deleted and existed records in DB, delete the regions
    if (fundingSource.getFundingRegions() == null && regionsDB != null && regionsDB.size() > 0) {
      for (FundingSourceLocation fundingSourceLocation : regionsDB) {
        fundingSourceLocationsManager.deleteFundingSourceLocations(fundingSourceLocation.getId());
      }
    }
    List<FundingSourceLocation> countriesDB = new ArrayList<>(fundingSourceDB
      .getFundingSourceLocations().stream().filter(fl -> fl.isActive() && fl.getLocElementType() == null
        && fl.getLocElement() != null && fl.getLocElement().getLocElementType().getId() == 2)
      .collect(Collectors.toList()));
    // If countries were deleted and existed records in DB, delete the countries
    if (fundingSource.getFundingCountry() == null && countriesDB != null && countriesDB.size() > 0) {
      for (FundingSourceLocation fundingSourceLocation : countriesDB) {
        fundingSourceLocationsManager.deleteFundingSourceLocations(fundingSourceLocation.getId());
      }
    }

  }

  public void setBudgetTypes(Map<String, String> budgetTypes) {
    this.budgetTypes = budgetTypes;
  }

  public void setBudgetTypesList(List<BudgetType> budgetTypesList) {
    this.budgetTypesList = budgetTypesList;
  }


  public void setCountryLists(List<LocElement> countryLists) {
    this.countryLists = countryLists;
  }

  public void setDivisions(List<PartnerDivision> divisions) {
    this.divisions = divisions;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }

  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }

  public void setFileID(Integer fileID) {
    this.fileID = fileID;
  }

  public void setFundingSource(FundingSource fundingSource) {
    this.fundingSource = fundingSource;
  }

  public void setFundingSourceID(long fundingSourceID) {
    this.fundingSourceID = fundingSourceID;
  }

  public void setFundingSourceInstitutions(List<Institution> fundingSourceInstitutions) {
    this.fundingSourceInstitutions = fundingSourceInstitutions;
  }

  public void setFundingSourceShow(FundingSource fundingSourceShow) {
    this.fundingSourceShow = fundingSourceShow;
  }


  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }


  public void setInstitutionsDonors(List<Institution> institutionsDonors) {
    this.institutionsDonors = institutionsDonors;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setRegion(boolean region) {
    this.region = region;
  }

  public void setRegionLists(List<LocElement> regionLists) {
    this.regionLists = regionLists;
  }

  public void setScopeRegionLists(List<LocElementType> scopeRegionLists) {
    this.scopeRegionLists = scopeRegionLists;
  }


  public void setStatus(Map<String, String> status) {
    this.status = status;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  public void setUserProjects(List<Project> userProjects) {
    this.userProjects = userProjects;
  }

  @Override
  public void validate() {
    if (save) {
      if (fundingSource.getFundingSourceInfo().getFile() != null
        && fundingSource.getFundingSourceInfo().getFile().getId() == null
        || fundingSource.getFundingSourceInfo().getFile().getId().longValue() == -1) {
        fundingSource.getFundingSourceInfo().setFile(null);
      }

      if (this.hasSpecificities(APConstants.CRP_HAS_RESEARCH_HUMAN)) {
        if (fundingSource.getFundingSourceInfo().getFileResearch() != null
          && fundingSource.getFundingSourceInfo().getFileResearch().getId() == null
          || fundingSource.getFundingSourceInfo().getFileResearch().getId().longValue() == -1) {
          fundingSource.getFundingSourceInfo().setFileResearch(null);
        }
      }

      validator.validate(this, fundingSource, true);

    }
  }

  public boolean w1Permission() {
    return this.hasPermissionNoBase(
      this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()));
  }
}
