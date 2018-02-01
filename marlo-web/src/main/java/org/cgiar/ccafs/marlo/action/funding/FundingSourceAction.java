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
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
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
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
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


  private FundingSourceBudgetManager fundingSourceBudgetManager;

  private long fundingSourceID;


  private FundingSourceInstitutionManager fundingSourceInstitutionManager;

  private FundingSourceManager fundingSourceManager;
  private FundingSourceInfoManager fundingSourceInfoManager;
  private InstitutionManager institutionManager;
  private List<Institution> institutions;

  private List<Institution> institutionsDonors;


  private LiaisonInstitutionManager liaisonInstitutionManager;

  private List<LiaisonInstitution> liaisonInstitutions;
  private HistoryComparator historyComparator;
  private PartnerDivisionManager partnerDivisionManager;

  private List<PartnerDivision> divisions;

  private GlobalUnit loggedCrp;


  private Map<String, String> status;

  private String transaction;
  private UserManager userManager;
  private FundingSourceValidator validator;

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

  @Inject
  public FundingSourceAction(APConfig config, GlobalUnitManager crpManager, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager, LiaisonInstitutionManager liaisonInstitutionManager,
    AuditLogManager auditLogManager, FundingSourceBudgetManager fundingSourceBudgetManager,
    BudgetTypeManager budgetTypeManager, FundingSourceValidator validator, CrpPpaPartnerManager crpPpaPartnerManager,
    HistoryComparator historyComparator, FileDBManager fileDBManager, UserManager userManager,
    PartnerDivisionManager partnerDivisionManager, FundingSourceInstitutionManager fundingSourceInstitutionManager,
    LocElementManager locElementManager, FundingSourceLocationsManager fundingSourceLocationsManager,
    LocElementTypeManager locElementTypeManager, FundingSourceInfoManager fundingSourceInfoManager,
    /* TODO delete when fix the budget permissions */ RoleManager userRoleManager) {
    super(config);
    this.crpManager = crpManager;
    this.fundingSourceManager = fundingSourceManager;
    this.partnerDivisionManager = partnerDivisionManager;
    this.budgetTypeManager = budgetTypeManager;
    this.institutionManager = institutionManager;
    this.validator = validator;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
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
    // TODO delete when fix the budget permissions
    this.userRoleManager = userRoleManager;
    this.fundingSourceInfoManager = fundingSourceInfoManager;
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


  private Path getAutoSaveFilePath() {

    String composedClassName = fundingSource.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = fundingSource.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";

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

  public List<LocElement> getRegionLists() {
    return regionLists;
  }

  public List<LocElementType> getScopeRegionLists() {
    return scopeRegionLists;
  }

  public Map<String, String> getStatus() {
    return status;
  }


  // methos to download link file
  public String getStudyFileURL(String fsId) {
    return config.getDownloadURL() + "/" + this.getStudyFileUrlPath(fsId).replace('\\', '/');
  }


  public String getStudyFileUrlPath(String fsId) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + fsId + File.separator
      + "fundingSourceFilesResearch" + File.separator;
  }


  public String getTransaction() {
    return transaction;
  }


  public boolean isRegion() {
    return region;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    fundingSourceID =
      Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.FUNDING_SOURCE_REQUEST_ID)));

    // Budget Types list
    budgetTypesList = budgetTypeManager.findAll();

    region = false;

    // Regions List
    regionLists = new ArrayList<>(locElementManager.findAll().stream()
      .filter(le -> le.isActive() && le.getLocElementType() != null && le.getLocElementType().getId() == 1)
      .collect(Collectors.toList()));
    Collections.sort(regionLists, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    // Region Scope List
    scopeRegionLists = new ArrayList<>(locElementTypeManager.findAll().stream()
      .filter(le -> le.isActive() && le.getCrp() != null && le.getCrp().equals(loggedCrp) && le.isScope())
      .collect(Collectors.toList()));

    // Country List
    countryLists = new ArrayList<>(locElementManager.findAll().stream()
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
    // System.out.println(fundingSource.getFundingSourceInfo().getId());
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
              fundingSource.getFundingSourceInfo()
                .setFile(fileDBManager.getFileDBById(fundingSource.getFundingSourceInfo().getFileResearch().getId()));
            }
          }
        }


        if (fundingSource.getInstitutions() != null) {
          for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
            if (fundingSourceInstitution != null) {
              fundingSourceInstitution.setInstitution(
                institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
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

        fundingSource.setProjectBudgetsList(fundingSource.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getProject().isActive() && pb.getPhase() != null
            && pb.getPhase().equals(this.getActualPhase())
            && pb.getProject().getProjecInfoPhase(this.getActualPhase()) != null)
          .collect(Collectors.toList()));

        /*
         * Funding source Locations
         */
        if (fundingSource.getFundingSourceLocations() != null) {

          List<FundingSourceLocation> countries =
            new ArrayList<>(fundingSource.getFundingSourceLocations().stream()
              .filter(fl -> fl.isActive() && fl.getPhase().equals(this.getActualPhase())
                && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 2)
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

          regions = new ArrayList<>(fundingSource.getFundingSourceLocations().stream()
            .filter(fl -> fl.isActive() && fl.getLocElementType() != null && fl.getLocElement() == null
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
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {

        status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
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

      List<CrpPpaPartner> ppaPartners = crpPpaPartnerManager.findAll().stream()
        .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive()
          && c.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList());

      for (CrpPpaPartner crpPpaPartner : ppaPartners) {
        institutions.add(crpPpaPartner.getInstitution());
      }
      if (fundingSource.getFundingSourceInfo() != null) {
        if (fundingSource.getFundingSourceInfo().getBudgetType() != null) {
          // if the funding source is type center funds -- institutions are ppa
          if (fundingSource.getFundingSourceInfo().getBudgetType().getId().longValue() == 4) {
            List<Institution> allInstitutions = null;
            institutionsDonors = new ArrayList<>();
            allInstitutions = institutionManager.findAll();
            for (Institution institutionObject : allInstitutions) {
              // validate if the institutions is PPA
              // if (this.isPPA(institutionObject)) {
              institutionsDonors.add(institutionObject);
              // }

            }

          } else {
            // if the funding source is type w1 -- institutions are cgiar center
            if (fundingSource.getFundingSourceInfo().getBudgetType().getId().longValue() == 1) {
              institutionsDonors = institutionManager.findAll().stream()
                .filter(i -> i.isActive() && i.getInstitutionType().getId().intValue() == 3)
                .collect(Collectors.toList());
            } else {

              // if the funding source is type bilateral -- institutions are not cgiar center
              institutionsDonors =
                institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());
              // institutionsDonors.removeAll(institutions);
            }

          }
        } else {
          // if the funding source don't hava a selected type -- institutions are not cgiar center

          institutionsDonors =
            institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());
        }

        institutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        institutionsDonors.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

        liaisonInstitutions = new ArrayList<>();

        liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions());
        liaisonInstitutions.addAll(
          liaisonInstitutionManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));

      }


    } else {
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
      if (fundingSource.getInstitutions() != null) {
        for (FundingSourceInstitution fundingSourceInstitution : fundingSource.getInstitutions()) {
          fundingSourceInstitution
            .setInstitution(institutionManager.getInstitutionById(fundingSourceInstitution.getInstitution().getId()));
        }
        fundingSource.getFundingSourceInfo().setW1w2(null);
        fundingSource.getInstitutions().clear();
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
      fundingSource.getFundingSourceInfo(this.getActualPhase()).setDirectDonor(null);
      fundingSource.getFundingSourceInfo(this.getActualPhase()).setOriginalDonor(null);
      fundingSource.setBudgets(null);
      fundingSource.getFundingSourceInfo(this.getActualPhase()).setBudgetType(null);
      fundingSource.setFundingRegions(null);
      fundingSource.setFundingCountry(null);

      return;
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      // FundingSource fundingSourceDB = fundingSourceManager.getFundingSourceById(fundingSource.getId());
      // FundingSourceInfo funginsSourceInfoDB =
      // fundingSourceInfoManager.getFundingSourceInfoById(fundingSource.getFundingSourceInfo().getId());
      // fundingSource.getFundingSourceInfo().setCreatedBy(this.getCurrentUser());
      fundingSource.getFundingSourceInfo().setModifiedBy(this.getCurrentUser());
      // fundingSource.getFundingSourceInfo().setActiveSince(new Date());
      fundingSource.getFundingSourceInfo().setModifiedBy(this.getCurrentUser());
      fundingSource.getFundingSourceInfo().setModificationJustification("");

      // if donor has a select option, no option put donor null
      if (fundingSource.getFundingSourceInfo().getDirectDonor() != null
        && fundingSource.getFundingSourceInfo().getDirectDonor().getId() != null
        && fundingSource.getFundingSourceInfo().getDirectDonor().getId().longValue() != -1) {
        fundingSource.getFundingSourceInfo().setDirectDonor(fundingSource.getFundingSourceInfo().getDirectDonor());
      } else {
        fundingSource.getFundingSourceInfo().setDirectDonor(null);
      }
      if (fundingSource.getFundingSourceInfo().getOriginalDonor() != null
        && fundingSource.getFundingSourceInfo().getOriginalDonor().getId() != null
        && fundingSource.getFundingSourceInfo().getOriginalDonor().getId().longValue() != -1) {
        fundingSource.getFundingSourceInfo().setOriginalDonor(fundingSource.getFundingSourceInfo().getOriginalDonor());
      } else {
        fundingSource.getFundingSourceInfo().setOriginalDonor(null);
      }

      fundingSource.getFundingSourceInfo().setTitle(fundingSource.getFundingSourceInfo().getTitle());
      fundingSource.getFundingSourceInfo().setStatus(fundingSource.getFundingSourceInfo().getStatus());
      fundingSource.getFundingSourceInfo().setStartDate(fundingSource.getFundingSourceInfo().getStartDate());
      fundingSource.getFundingSourceInfo().setEndDate(fundingSource.getFundingSourceInfo().getEndDate());
      fundingSource.getFundingSourceInfo().setGlobal(fundingSource.getFundingSourceInfo().isGlobal());
      fundingSource.getFundingSourceInfo().setHasFileResearch(fundingSource.getFundingSourceInfo().isHasFileResearch());

      fundingSource.getFundingSourceInfo().setFinanceCode(fundingSource.getFundingSourceInfo().getFinanceCode());
      fundingSource.getFundingSourceInfo()
        .setContactPersonEmail(fundingSource.getFundingSourceInfo().getContactPersonEmail());
      fundingSource.getFundingSourceInfo()
        .setContactPersonName(fundingSource.getFundingSourceInfo().getContactPersonName());
      fundingSource.getFundingSourceInfo().setBudgetType(fundingSource.getFundingSourceInfo().getBudgetType());

      fundingSource.setBudgets(fundingSource.getBudgets());

      if (fundingSource.getFundingSourceInfo().getPartnerDivision() == null
        || fundingSource.getFundingSourceInfo().getPartnerDivision().getId() == null
        || fundingSource.getFundingSourceInfo().getPartnerDivision().getId().longValue() == -1) {
        fundingSource.getFundingSourceInfo().setPartnerDivision(null);
      } else {
        fundingSource.getFundingSourceInfo()
          .setPartnerDivision(fundingSource.getFundingSourceInfo().getPartnerDivision());
      }

      if (fundingSource.getFundingSourceInfo().getW1w2() == null) {
        fundingSource.getFundingSourceInfo().setW1w2(false);
      } else {
        fundingSource.getFundingSourceInfo().setW1w2(true);
      }
      fundingSource.getFundingSourceInfo().setDescription(fundingSource.getFundingSourceInfo().getDescription());

      if (fundingSource.getFundingSourceInfo().getFile() != null) {
        if (fundingSource.getFundingSourceInfo().getFile().getId() == null) {
          fundingSource.getFundingSourceInfo().setFile(null);
        } else {
          fundingSource.getFundingSourceInfo().setFile(fundingSource.getFundingSourceInfo().getFile());
        }
      }

      // fileResearch validation
      // 20180124 - @jurodca
      if (this.hasSpecificities(APConstants.CRP_HAS_RESEARCH_HUMAN)) {
        if (fundingSource.getFundingSourceInfo().isHasFileResearch()) {
          if (fundingSource.getFundingSourceInfo().getFileResearch() != null) {
            if (fundingSource.getFundingSourceInfo().getFileResearch().getId() == null) {
              fundingSource.getFundingSourceInfo().setFileResearch(null);
            } else {
              fundingSource.getFundingSourceInfo()
                .setFileResearch(fundingSource.getFundingSourceInfo().getFileResearch());
            }
          }
        } else {
          fundingSource.getFundingSourceInfo().setFileResearch(null);
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
            fundingSourceBudget.setActive(true);
            fundingSourceBudget.setCreatedBy(this.getCurrentUser());
            fundingSourceBudget.setModifiedBy(this.getCurrentUser());
            fundingSourceBudget.setModificationJustification("");
            fundingSourceBudget.setFundingSource(fundingSource);
            fundingSourceBudget.setActiveSince(new Date());
            fundingSourceBudget.setPhase(this.getActualPhase());
            fundingSourceBudgetManager.saveFundingSourceBudget(fundingSourceBudget);
          } else {
            FundingSourceBudget fundingSourceBudgetBD =
              fundingSourceBudgetManager.getFundingSourceBudgetById(fundingSourceBudget.getId());
            fundingSourceBudget.setActive(true);
            fundingSourceBudget.setFundingSource(fundingSource);
            fundingSourceBudget.setCreatedBy(fundingSourceBudgetBD.getCreatedBy());
            fundingSourceBudget.setModifiedBy(this.getCurrentUser());
            fundingSourceBudget.setModificationJustification("");
            fundingSourceBudget.setActiveSince(new Date());
            fundingSourceBudget.setPhase(this.getActualPhase());
            fundingSourceBudgetManager.saveFundingSourceBudget(fundingSourceBudget);
          }


        }
      }


      FundingSource fundingSourceDB = fundingSourceManager.getFundingSourceById(fundingSourceID);
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

            fundingSourceInstitution.setId(null);
            fundingSourceInstitution.setFundingSource(fundingSourceDB);
            fundingSourceInstitution.setPhase(this.getActualPhase());
            fundingSourceInstitution =
              fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
            instituionsEdited = true;
          } else {
            fundingSourceInstitutionManager.saveFundingSourceInstitution(
              fundingSourceInstitutionManager.getFundingSourceInstitutionById(fundingSourceInstitution.getId()));

          }

        }
      }
      if (instituionsEdited) {
        this.clearPermissionsCache();
      }


      this.saveLocations(fundingSourceDB);


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.FUNDING_SOURCES_BUDGETS_RELATION);

      relationsName.add(APConstants.FUNDING_SOURCES_LOCATIONS_RELATION);
      relationsName.add(APConstants.FUNDING_SOURCES_INFO);
      relationsName.add(APConstants.FUNDING_SOURCES_LOCATIONS_RELATION);
      relationsName.add(APConstants.FUNDING_SOURCES_LOCATIONS_RELATION);
      relationsName.add(APConstants.FUNDING_SOURCES_INSTITUTIONS_RELATION);
      // fundingSourceDB = fundingSourceManager.getFundingSourceById(fundingSourceID);

      fundingSource.getFundingSourceInfo().setPhase(this.getActualPhase());
      fundingSource.getFundingSourceInfo().setFundingSource(fundingSource);
      fundingSourceInfoManager.saveFundingSourceInfo(fundingSource.getFundingSourceInfo());

      fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);
      fundingSource.setModifiedBy(this.getCurrentUser());
      fundingSource.setActiveSince(new Date());
      fundingSourceManager.saveFundingSource(fundingSource, this.getActionName(), relationsName, this.getActualPhase());

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
    } else {
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

      List<FundingSourceLocation> regions = new ArrayList<>(fundingSourceDB
        .getFundingSourceLocations().stream().filter(fl -> fl.isActive() && fl.getPhase().equals(this.getActualPhase())
          && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 1)
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
          fundingSourceLocationSave.setActive(true);
          fundingSourceLocationSave.setActiveSince(new Date());
          fundingSourceLocationSave.setCreatedBy(this.getCurrentUser());
          fundingSourceLocationSave.setModifiedBy(this.getCurrentUser());
          fundingSourceLocationSave.setModificationJustification("");
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
          fundingSourceLocationsManager.saveFundingSourceLocations(
            fundingSourceLocationsManager.getFundingSourceLocationsById(fundingSourceLocation.getId()));

        }
      }


    }

    if (fundingSource.getFundingCountry() != null) {

      List<FundingSourceLocation> countries = new ArrayList<>(fundingSourceDB
        .getFundingSourceLocations().stream().filter(fl -> fl.isActive() && fl.getPhase().equals(this.getActualPhase())
          && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 2)
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
          fundingSourceLocationSave.setActive(true);
          fundingSourceLocationSave.setActiveSince(new Date());
          fundingSourceLocationSave.setCreatedBy(this.getCurrentUser());
          fundingSourceLocationSave.setModifiedBy(this.getCurrentUser());
          fundingSourceLocationSave.setModificationJustification("");
          fundingSourceLocationSave.setFundingSource(fundingSourceDB);
          fundingSourceLocationSave.setPhase(this.getActualPhase());
          LocElement locElement =
            locElementManager.getLocElementByISOCode(fundingSourceLocation.getLocElement().getIsoAlpha2());

          fundingSourceLocationSave.setLocElement(locElement);

          fundingSourceLocationsManager.saveFundingSourceLocations(fundingSourceLocationSave);
        } else {
          fundingSourceLocationsManager.saveFundingSourceLocations(
            fundingSourceLocationsManager.getFundingSourceLocationsById(fundingSourceLocation.getId()));

        }
      }
    }

    // Check empty regions and countries
    List<FundingSourceLocation> regionsDB = new ArrayList<>(fundingSourceDB.getFundingSourceLocations().stream()
      .filter(
        fl -> fl.isActive() && fl.getLocElementType() == null && fl.getLocElement().getLocElementType().getId() == 1)
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

  @Override
  public void validate() {
    if (save) {
      if (fundingSource.getFundingSourceInfo().getFile().getId() == null
        || fundingSource.getFundingSourceInfo().getFile().getId().longValue() == -1) {
        fundingSource.getFundingSourceInfo().setFile(null);
      }

      if (this.hasSpecificities(APConstants.CRP_HAS_RESEARCH_HUMAN)) {
        if (fundingSource.getFundingSourceInfo().isHasFileResearch()) {
          if (fundingSource.getFundingSourceInfo().getFileResearch() != null) {
            if (fundingSource.getFundingSourceInfo().getFileResearch().getId() == null
              || fundingSource.getFundingSourceInfo().getFileResearch().getId().longValue() == -1) {
              fundingSource.getFundingSourceInfo().setFileResearch(null);
            }
          }
        }
      }


      validator.validate(this, fundingSource, true);
    }
  }

}
