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


package org.cgiar.ccafs.marlo.action.publications;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrossCuttingScoringManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGenderLevelManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLocationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndFillingTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPatentStatusManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndTypeActivityManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndTypeParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.RepositoryChannelManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingScoring;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAssetTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GenderType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndFillingType;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndPatentStatus;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeActivity;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeParticipant;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.publications.PublicationValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PublicationAction:
 * 
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 10:30:10 AM: Added repositoryChannel List from Database
 */
public class PublicationAction extends BaseAction {

  private static final long serialVersionUID = -5176367401132626314L;
  private final Logger LOG = LoggerFactory.getLogger(PublicationAction.class);

  // Managers
  private final GlobalUnitManager crpManager;
  private final DeliverableCrpManager deliverableCrpManager;
  private final IpProgramManager ipProgramManager;
  private final DeliverableManager deliverableManager;
  private final DeliverableDisseminationManager deliverableDisseminationManager;
  private final DeliverableGenderLevelManager deliverableGenderLevelManager;
  private final DeliverableMetadataElementManager deliverableMetadataElementManager;
  private final DeliverablePublicationMetadataManager deliverablePublicationMetadataManager;
  private final DeliverableUserManager deliverableUserManager;
  private final DeliverableLeaderManager deliverableLeaderManager;
  private final DeliverableProgramManager deliverableProgramManager;
  private final CrpClusterKeyOutputManager crpClusterKeyOutputManager;
  private final AuditLogManager auditLogManager;
  private final GenderTypeManager genderTypeManager;
  private final DeliverableTypeManager deliverableTypeManager;
  private final RepositoryChannelManager repositoryChannelManager;
  private final DeliverableInfoManager deliverableInfoManager;
  private final InstitutionManager institutionManager;
  private final CrpProgramManager crpProgramManager;
  private final FundingSourceManager fundingSourceManager;
  private final CrossCuttingScoringManager crossCuttingManager;
  private final DeliverableFundingSourceManager deliverableFundingSourceManager;
  private final MetadataElementManager metadataElementManager;
  private final DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;
  private final DeliverableParticipantManager deliverableParticipantManager;
  private final RepIndTypeActivityManager repIndTypeActivityManager;
  private final RepIndTypeParticipantManager repIndTypeParticipantManager;
  private final RepIndGeographicScopeManager repIndGeographicScopeManager;
  private final RepIndRegionManager repIndRegionManager;
  private final LocElementManager locElementManager;
  private final RepIndFillingTypeManager repIndFillingTypeManager;
  private final RepIndPatentStatusManager repIndPatentStatusManager;
  private DeliverableLocationManager deliverableLocationManager;

  // Variables
  private GlobalUnit loggedCrp;
  private long deliverableID;
  private List<GlobalUnit> crps;
  private List<GenderType> genderLevels;
  private List<FundingSource> fundingSources;
  private List<CrpProgram> flagshipsList;
  private List<CrpProgram> regionsList;
  private List<Institution> institutions;
  private Map<String, String> channels;
  private PublicationValidator publicationValidator;
  private Deliverable deliverable;
  private Deliverable deliverableDB;

  private String transaction;


  private HistoryComparator historyComparator;

  private List<DeliverableType> deliverableSubTypes;
  private List<RepositoryChannel> repositoryChannels;
  private List<CrossCuttingScoring> crossCuttingDimensions;
  private Map<Long, String> crossCuttingScoresMap;
  private List<CrpClusterKeyOutput> keyOutputs;
  private List<RepIndTypeActivity> repIndTypeActivities;
  private List<RepIndTypeParticipant> repIndTypeParticipants;
  private List<RepIndGeographicScope> repIndGeographicScopes;
  private List<RepIndRegion> repIndRegions;
  private List<LocElement> countries;
  private List<RepIndFillingType> repIndFillingTypes;
  private List<RepIndPatentStatus> repIndPatentStatuses;
  private Map<String, String> statuses;


  @Inject
  public PublicationAction(APConfig config, GlobalUnitManager crpManager, DeliverableManager deliverableManager,
    GenderTypeManager genderTypeManager, AuditLogManager auditLogManager, DeliverableTypeManager deliverableTypeManager,
    DeliverableDisseminationManager deliverableDisseminationManager, InstitutionManager institutionManager,
    DeliverablePublicationMetadataManager deliverablePublicationMetadataManager,
    DeliverableGenderLevelManager deliverableGenderLevelManager, DeliverableUserManager deliverableUserManager,
    DeliverableCrpManager deliverableCrpManager, CrpPpaPartnerManager crpPpaPartnerManager,
    DeliverableProgramManager deliverableProgramManager, DeliverableLeaderManager deliverableLeaderManager,
    PublicationValidator publicationValidator, HistoryComparator historyComparator,
    DeliverableMetadataElementManager deliverableMetadataElementManager, IpProgramManager ipProgramManager,
    RepositoryChannelManager repositoryChannelManager, CrpProgramManager crpProgramManager,
    FundingSourceManager fundingSourceManager, CrossCuttingScoringManager crossCuttingManager,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager, DeliverableInfoManager deliverableInfoManager,
    DeliverableFundingSourceManager deliverableFundingSourceManager, MetadataElementManager metadataElementManager,
    DeliverableIntellectualAssetManager deliverableIntellectualAssetManager,
    DeliverableParticipantManager deliverableParticipantManager, RepIndTypeActivityManager repIndTypeActivityManager,
    RepIndTypeParticipantManager repIndTypeParticipantManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, RepIndRegionManager repIndRegionManager,
    LocElementManager locElementManager, RepIndFillingTypeManager repIndFillingTypeManager,
    RepIndPatentStatusManager repIndPatentStatusManager, DeliverableLocationManager deliverableLocationManager) {
    super(config);
    this.deliverableDisseminationManager = deliverableDisseminationManager;
    this.historyComparator = historyComparator;
    this.crpManager = crpManager;
    this.publicationValidator = publicationValidator;
    this.deliverableCrpManager = deliverableCrpManager;
    this.deliverableManager = deliverableManager;
    this.genderTypeManager = genderTypeManager;
    this.auditLogManager = auditLogManager;
    this.deliverableGenderLevelManager = deliverableGenderLevelManager;
    this.deliverablePublicationMetadataManager = deliverablePublicationMetadataManager;
    this.deliverableUserManager = deliverableUserManager;
    this.institutionManager = institutionManager;
    this.deliverableProgramManager = deliverableProgramManager;
    this.deliverableLeaderManager = deliverableLeaderManager;
    this.deliverableMetadataElementManager = deliverableMetadataElementManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.ipProgramManager = ipProgramManager;
    this.repositoryChannelManager = repositoryChannelManager;
    this.crpProgramManager = crpProgramManager;
    this.fundingSourceManager = fundingSourceManager;
    this.crossCuttingManager = crossCuttingManager;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.metadataElementManager = metadataElementManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
    this.deliverableParticipantManager = deliverableParticipantManager;
    this.repIndTypeActivityManager = repIndTypeActivityManager;
    this.repIndTypeParticipantManager = repIndTypeParticipantManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.repIndRegionManager = repIndRegionManager;
    this.locElementManager = locElementManager;
    this.repIndFillingTypeManager = repIndFillingTypeManager;
    this.repIndPatentStatusManager = repIndPatentStatusManager;
    this.deliverableLocationManager = deliverableLocationManager;
  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    deliverable.getDeliverableInfo(deliverable.getPhase()).setCrpClusterKeyOutput(null);

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
   * Delete Deliverable Gender Levels if there is no cross cutting gender component.
   * 
   * @param deliverablePrew
   */
  private void deleteDeliverableGenderLevels(Deliverable deliverablePrew) {
    if (!deliverablePrew.getDeliverableInfo(deliverable.getPhase()).getCrossCuttingGender().booleanValue()) {
      Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
      List<DeliverableGenderLevel> deliverableGenderLevels =
        deliverableDB.getDeliverableGenderLevels().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (DeliverableGenderLevel genderLevel : deliverableGenderLevels) {
        deliverableGenderLevelManager.deleteDeliverableGenderLevel(genderLevel.getId());
      }
    }
  }


  private void deleteDeliverableLocations(List<DeliverableLocation> locationsDB) {
    if (locationsDB != null) {
      for (DeliverableLocation deliverableLocation : locationsDB) {
        deliverableLocationManager.deleteDeliverableLocation(deliverableLocation.getId());
      }
    }
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + deliverable.getPhase().getDescription()
      + "_" + deliverable.getPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public Map<String, String> getChannels() {
    return channels;
  }


  public List<LocElement> getCountries() {
    return countries;
  }


  public List<CrossCuttingScoring> getCrossCuttingDimensions() {
    return crossCuttingDimensions;
  }


  public Map<Long, String> getCrossCuttingScoresMap() {
    return crossCuttingScoresMap;
  }


  public List<GlobalUnit> getCrps() {
    return crps;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }


  public Deliverable getDeliverableDB() {
    return deliverableDB;
  }


  public long getDeliverableID() {
    return deliverableID;
  }

  public List<DeliverableType> getDeliverableSubTypes() {
    return deliverableSubTypes;
  }

  public DeliverableTypeManager getDeliverableTypeManager() {
    return deliverableTypeManager;
  }


  public String[] getFlagshipIds() {

    List<DeliverableProgram> projectFocuses = deliverable.getPrograms();

    if (projectFocuses != null) {
      String[] ids = new String[projectFocuses.size()];
      for (int c = 0; c < ids.length; c++) {
        ids[c] = projectFocuses.get(c).getCrpProgram().getId().toString();
      }
      return ids;
    }
    return null;
  }


  public List<CrpProgram> getFlagshipsList() {
    return crpProgramManager.findAll().stream()
      .filter(c -> c.isActive() && c.getCrp().equals(this.loggedCrp)
        && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .sorted((f1, f2) -> f1.getAcronym().compareTo(f2.getAcronym())).collect(Collectors.toList());
  }

  public List<FundingSource> getFundingSources() {
    return fundingSources;
  }

  public List<GenderType> getGenderLevels() {
    return genderLevels;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<CrpClusterKeyOutput> getKeyOutputs() {
    return keyOutputs;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public String[] getRegionsIds() {

    List<DeliverableProgram> projectFocuses = deliverable.getRegions();

    if (projectFocuses != null) {
      String[] ids = new String[projectFocuses.size()];
      for (int c = 0; c < ids.length; c++) {
        ids[c] = projectFocuses.get(c).getCrpProgram().getId().toString();
      }
      return ids;
    }
    return null;
  }


  public List<CrpProgram> getRegionsList() {
    return crpProgramManager.findAll().stream()
      .filter(c -> c.isActive() && c.getCrp().equals(this.loggedCrp)
        && c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
      .sorted((r1, r2) -> r1.getAcronym().compareTo(r2.getAcronym())).collect(Collectors.toList());
  }


  public List<RepIndFillingType> getRepIndFillingTypes() {
    return repIndFillingTypes;
  }


  public List<RepIndGeographicScope> getRepIndGeographicScopes() {
    return repIndGeographicScopes;
  }


  public List<RepIndPatentStatus> getRepIndPatentStatuses() {
    return repIndPatentStatuses;
  }

  public List<RepIndRegion> getRepIndRegions() {
    return repIndRegions;
  }


  public List<RepIndTypeActivity> getRepIndTypeActivities() {
    return repIndTypeActivities;
  }

  public List<RepIndTypeParticipant> getRepIndTypeParticipants() {
    return repIndTypeParticipants;
  }

  public List<RepositoryChannel> getRepositoryChannels() {
    return repositoryChannels;
  }


  public Map<String, String> getStatuses() {
    return statuses;
  }


  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    try {
      deliverableID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_DELIVERABLE_REQUEST_ID)));
    } catch (Exception e) {
      LOG.error("unable to parse deliverableID", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Deliverable history = (Deliverable) auditLogManager.getHistory(transaction);

      if (history != null) {
        deliverable = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      deliverable = deliverableManager.getDeliverableById(deliverableID);
    }

    if (deliverable != null) {


      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        deliverable = (Deliverable) autoSaveReader.readFromJson(jReader);

        // Deliverable Countries List AutoSave
        if (deliverable.getCountriesIdsText() != null) {
          String[] countriesText = deliverable.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
          List<String> countries = new ArrayList<>();
          for (String value : Arrays.asList(countriesText)) {
            countries.add(value.trim());
          }
          deliverable.setCountriesIds(countries);
        }

        deliverable.setPhase(deliverableManager.getDeliverableById(deliverable.getId()).getPhase());

        if (metadataElementManager.findAll() != null) {
          deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
        }
        deliverable.getDeliverableInfo(deliverable.getPhase());
        if (deliverable.getFundingSources() != null) {
          for (DeliverableFundingSource fundingSource : deliverable.getFundingSources()) {
            if (fundingSource != null && fundingSource.getFundingSource() != null) {
              fundingSource
                .setFundingSource(fundingSourceManager.getFundingSourceById(fundingSource.getFundingSource().getId()));
              fundingSource.getFundingSource().getFundingSourceInfo(deliverable.getPhase());
            }
          }
        }

        if (deliverable.getCrps() != null) {
          for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {
            if (deliverableCrp != null) {
              if (deliverableCrp.getCrpProgram() == null || deliverableCrp.getCrpProgram().getId() == null
                || deliverableCrp.getCrpProgram().getId().intValue() == -1) {
                if (deliverableCrp.getGlobalUnit() != null && deliverableCrp.getGlobalUnit().getId() != null
                  && deliverableCrp.getGlobalUnit().getId().intValue() != -1) {
                  deliverableCrp.setGlobalUnit(crpManager.getGlobalUnitById(deliverableCrp.getGlobalUnit().getId()));
                }
              } else {
                deliverableCrp
                  .setCrpProgram(crpProgramManager.getCrpProgramById(deliverableCrp.getCrpProgram().getId()));
              }
            }
          }
        }

        if (deliverable.getLeaders() != null) {
          for (DeliverableLeader deliverableLeader : deliverable.getLeaders()) {
            if (deliverableLeader != null) {
              deliverableLeader
                .setInstitution(institutionManager.getInstitutionById(deliverableLeader.getInstitution().getId()));

            }
          }
        }

        List<DeliverableProgram> programs = new ArrayList<>();
        if (deliverable.getFlagshipValue() != null) {
          for (String programID : deliverable.getFlagshipValue().trim().replace("[", "").replace("]", "").split(",")) {
            try {
              DeliverableProgram deliverableProgram = new DeliverableProgram();
              CrpProgram program = crpProgramManager.getCrpProgramById(Long.parseLong(programID.trim()));
              deliverableProgram.setDeliverable(deliverable);
              deliverableProgram.setCrpProgram(program);
              programs.add(deliverableProgram);
            } catch (Exception e) {
              LOG.error("unable to add deliverableProgram to programs list", e);
              /**
               * Original code swallows the exception and didn't even log it. Now we at least log it,
               * but we need to revisit to see if we should continue processing or re-throw the exception.
               */
            }
          }
        }


        List<DeliverableProgram> regions = new ArrayList<>();
        if (deliverable.getRegionsValue() != null) {
          for (String programID : deliverable.getRegionsValue().trim().replace("[", "").replace("]", "").split(",")) {
            try {
              DeliverableProgram deliverableProgram = new DeliverableProgram();
              CrpProgram program = crpProgramManager.getCrpProgramById(Long.parseLong(programID.trim()));
              deliverableProgram.setDeliverable(deliverable);
              deliverableProgram.setCrpProgram(program);
              regions.add(deliverableProgram);
            } catch (Exception e) {
              LOG.error("unable to add delverable program to regions list", e);
              /**
               * Original code swallows the exception and didn't even log it. Now we at least log it,
               * but we need to revisit to see if we should continue processing or re-throw the exception.
               */
            }
          }
        }
        deliverable.setPrograms(programs);
        deliverable.setRegions(regions);

        String type = deliverable.getDissemination().getType();
        if (type != null) {
          switch (type) {
            case "intellectualProperty":
              deliverable.getDissemination().setIntellectualProperty(true);
              break;
            case "limitedExclusivity":
              deliverable.getDissemination().setLimitedExclusivity(true);
              break;
            case "restrictedUseAgreement":
              deliverable.getDissemination().setRestrictedUseAgreement(true);
              break;
            case "effectiveDateRestriction":
              deliverable.getDissemination().setEffectiveDateRestriction(true);
              break;
            case "notDisseminated":
              deliverable.getDissemination().setNotDisseminated(true);
            default:
              break;
          }
        }
        this.setDraft(true);

      } else {
        deliverable.getDeliverableInfo(deliverable.getPhase());

        // Deliverable Countries List
        if (deliverable.getDeliverableLocations() == null) {
          deliverable.setCountries(new ArrayList<>());
        } else {
          List<DeliverableLocation> countries = deliverableLocationManager
            .getDeliverableLocationbyPhase(deliverable.getId(), deliverable.getPhase().getId());
          deliverable.setCountries(countries);
        }

        if (deliverable.getCountries() != null) {
          for (DeliverableLocation country : deliverable.getCountries()) {
            deliverable.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }

        deliverable.setFundingSources(deliverable.getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(deliverable.getPhase()))
          .collect(Collectors.toList()));
        for (DeliverableFundingSource deliverableFundingSource : deliverable.getFundingSources()) {

          deliverableFundingSource.setFundingSource(
            fundingSourceManager.getFundingSourceById(deliverableFundingSource.getFundingSource().getId()));
          deliverableFundingSource.getFundingSource().setFundingSourceInfo(
            deliverableFundingSource.getFundingSource().getFundingSourceInfo(deliverable.getPhase()));
          if (deliverableFundingSource.getFundingSource().getFundingSourceInfo() == null) {
            deliverableFundingSource.getFundingSource().setFundingSourceInfo(
              deliverableFundingSource.getFundingSource().getFundingSourceInfoLast(deliverable.getPhase()));
          }
        }
        deliverable.setGenderLevels(deliverable.getDeliverableGenderLevels().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList()));


        deliverable.setLeaders(deliverable.getDeliverableLeaders().stream()
          .filter(dl -> dl.isActive() && dl.getPhase().equals(deliverable.getPhase()))
          .sorted((l1, l2) -> l1.getInstitution().getName().compareTo(l2.getInstitution().getName()))
          .collect(Collectors.toList()));
        List<DeliverableProgram> deliverablePrograms = deliverable.getDeliverablePrograms().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList());

        deliverable.setPrograms(deliverablePrograms.stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .sorted((f1, f2) -> f1.getCrpProgram().getAcronym().compareTo(f2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList()));
        deliverable.setRegions(deliverablePrograms.stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .sorted((r1, r2) -> r1.getCrpProgram().getAcronym().compareTo(r2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList()));


        if (deliverable.getDeliverableMetadataElements() != null) {
          deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList())));
        }


        if (deliverable.getDeliverableDisseminations() != null) {
          deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList())));
          if (deliverable.getDisseminations().size() > 0) {
            deliverable.setDissemination(deliverable.getDisseminations().get(0));
          } else {
            deliverable.setDissemination(new DeliverableDissemination());
          }
        }

        if (deliverable.getDeliverablePublicationMetadatas() != null) {
          deliverable.setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList())));
        }
        if (!deliverable.getPublicationMetadatas().isEmpty()) {
          deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
        }

        if (deliverable.getDeliverableDataSharings() != null) {
          deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings().stream()
            .filter(c -> c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList())));
        }

        deliverable.setUsers(deliverable.getDeliverableUsers().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList()));
        deliverable.setCrps(deliverable.getDeliverableCrps().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList()));

        if (deliverable.getDeliverableIntellectualAssets() != null) {
          List<DeliverableIntellectualAsset> intellectualAssets =
            deliverable.getDeliverableIntellectualAssets().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList());

          if (intellectualAssets.size() > 0) {
            deliverable.setIntellectualAsset(intellectualAssets.get(0));
            if (this.transaction != null && !this.transaction.equals("-1")) {
              if (deliverable.getIntellectualAsset().getFillingType() != null
                && deliverable.getIntellectualAsset().getFillingType().getId() != null) {
                deliverable.getIntellectualAsset().setFillingType(repIndFillingTypeManager
                  .getRepIndFillingTypeById(deliverable.getIntellectualAsset().getFillingType().getId()));
              }
              if (deliverable.getIntellectualAsset().getPatentStatus() != null
                && deliverable.getIntellectualAsset().getPatentStatus().getId() != null) {
                deliverable.getIntellectualAsset().setPatentStatus(repIndPatentStatusManager
                  .getRepIndPatentStatusById(deliverable.getIntellectualAsset().getPatentStatus().getId()));
              }
              if (deliverable.getIntellectualAsset().getCountry() != null
                && deliverable.getIntellectualAsset().getCountry().getId() != null) {
                deliverable.getIntellectualAsset().setCountry(
                  locElementManager.getLocElementById(deliverable.getIntellectualAsset().getCountry().getId()));
              }
            }
          } else {
            deliverable.setIntellectualAsset(new DeliverableIntellectualAsset());
          }
        }
        if (deliverable.getDeliverableParticipants() != null) {
          List<DeliverableParticipant> deliverableParticipants = deliverable.getDeliverableParticipants().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList());

          if (deliverableParticipants.size() > 0) {
            deliverable.setDeliverableParticipant(deliverableParticipants.get(0));

            if (this.transaction != null && !this.transaction.equals("-1")) {
              if (deliverable.getDeliverableParticipant().getRepIndTypeActivity() != null
                && deliverable.getDeliverableParticipant().getRepIndTypeActivity().getId() != null) {
                deliverable.getDeliverableParticipant().setRepIndTypeActivity(repIndTypeActivityManager
                  .getRepIndTypeActivityById(deliverable.getDeliverableParticipant().getRepIndTypeActivity().getId()));
              }
              if (deliverable.getDeliverableParticipant().getRepIndTypeParticipant() != null
                && deliverable.getDeliverableParticipant().getRepIndTypeParticipant().getId() != null) {
                deliverable.getDeliverableParticipant()
                  .setRepIndTypeParticipant(repIndTypeParticipantManager.getRepIndTypeParticipantById(
                    deliverable.getDeliverableParticipant().getRepIndTypeParticipant().getId()));
              }
            }
          } else {
            deliverable.setDeliverableParticipant(new DeliverableParticipant());
          }
        }

        deliverable.setFlagshipValue("");
        deliverable.setRegionsValue("");

        for (DeliverableProgram deliverableProgram : deliverable.getPrograms()) {
          if (deliverable.getFlagshipValue().isEmpty()) {
            deliverable.setFlagshipValue(deliverableProgram.getCrpProgram().getId().toString());
          } else {
            deliverable.setFlagshipValue(
              deliverable.getFlagshipValue() + "," + deliverableProgram.getCrpProgram().getId().toString());
          }
        }

        for (DeliverableProgram deliverableProgram : deliverable.getRegions()) {
          if (deliverable.getRegionsValue().isEmpty()) {
            deliverable.setRegionsValue(deliverableProgram.getCrpProgram().getId().toString());
          } else {
            deliverable.setRegionsValue(
              deliverable.getRegionsValue() + "," + deliverableProgram.getCrpProgram().getId().toString());
          }
        }
        this.setDraft(false);
      }

      this.setRepIndGeographicScopes(repIndGeographicScopeManager.findAll().stream()
        .sorted((g1, g2) -> g1.getName().compareTo(g2.getName())).collect(Collectors.toList()));
      this.setRepIndRegions(repIndRegionManager.findAll().stream()
        .sorted((r1, r2) -> r1.getName().compareTo(r2.getName())).collect(Collectors.toList()));
      this.setCountries(locElementManager.findAll().stream()
        .filter(c -> c.isActive() && c.getLocElementType().getId() == 2).collect(Collectors.toList()));

      if (deliverable.getGenderLevels() != null) {
        for (DeliverableGenderLevel deliverableGenderLevel : deliverable.getGenderLevels()) {
          try {
            GenderType type = genderTypeManager.getGenderTypeById(deliverableGenderLevel.getGenderLevel());
            if (type != null) {
              deliverableGenderLevel.setNameGenderLevel(type.getDescription());
              deliverableGenderLevel.setDescriptionGenderLevel(type.getCompleteDescription());
            }
          } catch (Exception e) {
            LOG.error("unable to update DeliverableGenderLevel", e);
          }
        }
      }

      if (metadataElementManager.findAll() != null) {
        deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
      }

      genderLevels = new ArrayList<>();
      List<GenderType> genderTypes = null;
      if (this.hasSpecificities(APConstants.CRP_CUSTOM_GENDER)) {
        genderTypes = genderTypeManager.findAll().stream()
          .filter(
            c -> c.isActive() && c.getCrp() != null && c.getCrp().getId().longValue() == loggedCrp.getId().longValue())
          .collect(Collectors.toList());
      } else {
        genderTypes = genderTypeManager.findAll().stream().filter(c -> c.isActive() && c.getCrp() == null)
          .collect(Collectors.toList());
      }

      for (GenderType projectStatusEnum : genderTypes) {
        genderLevels.add(projectStatusEnum);
      }


      deliverableSubTypes = new ArrayList<>(
        deliverableTypeManager.findAll().stream().filter(dt -> dt.isActive() && dt.getDeliverableCategory() != null
          && dt.getDeliverableCategory().getId().intValue() == 49).collect(Collectors.toList()));
      deliverableSubTypes.add(deliverableTypeManager.getDeliverableTypeById(55));
      deliverableSubTypes.add(deliverableTypeManager.getDeliverableTypeById(56));
      deliverableSubTypes.sort((t1, t2) -> t1.getName().compareTo(t2.getName()));

      crps = new ArrayList<GlobalUnit>();
      for (GlobalUnit crp : crpManager.findAll().stream()
        .filter(c -> c.getId() != this.getLoggedCrp().getId() && c.isActive()).collect(Collectors.toList())) {
        crps.add(crp);
      }
      crps.sort((c1, c2) -> c1.getComposedName().compareTo(c2.getComposedName()));

      this.fundingSources = new ArrayList<>();
      this.fundingSources =
        fundingSourceManager.findAll().stream().filter(fs -> fs.isActive() && fs.getCrp().equals(this.getCurrentCrp())
          && fs.getFundingSourceInfo(deliverable.getPhase()) != null).collect(Collectors.toList());

      fundingSources.sort((f1, f2) -> f1.getId().compareTo(f2.getId()));

      repositoryChannels = repositoryChannelManager.findAll();
      if (repositoryChannels != null && repositoryChannels.size() > 0) {
        repositoryChannels.sort((rc1, rc2) -> rc1.getShortName().compareTo(rc2.getShortName()));
      } else {
        repositoryChannels = new LinkedList<RepositoryChannel>();
      }

      institutions = new ArrayList<>();
      List<Institution> institutionsList =
        institutionManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      institutionsList.sort((i1, i2) -> i1.getComposedName().compareTo(i2.getComposedName()));
      for (Institution institution : institutionsList) {
        institutions.add(institution);
      }

      // Read all the cross cutting scoring from database
      this.crossCuttingDimensions = this.crossCuttingManager.findAll();

      // load the map of cross cutting scores
      this.crossCuttingScoresMap = new HashMap<>();
      for (CrossCuttingScoring score : this.crossCuttingDimensions) {
        this.crossCuttingScoresMap.put(score.getId(), score.getDescription());
      }

      // only show cross cutting number 1 and 2

      List<CrossCuttingScoring> crossCuttingDimensionsTemp = this.crossCuttingDimensions;
      this.crossCuttingDimensions = new ArrayList<>();

      for (CrossCuttingScoring score : crossCuttingDimensionsTemp) {
        if (score.getId() != 0) {
          this.crossCuttingDimensions.add(score);
        }
      }


      this.setKeyOutputs(crpClusterKeyOutputManager.getCrpClusterKeyOutputByGlobalUnitAndPhase(this.loggedCrp.getId(),
        deliverable.getPhase().getId()));
      this.getKeyOutputs().sort((k1, k2) -> k1.getCrpClusterOfActivity().getIdentifier()
        .compareTo(k2.getCrpClusterOfActivity().getIdentifier()));

      this.setRepIndTypeActivities(repIndTypeActivityManager.findAll().stream()
        .sorted((t1, t2) -> t1.getName().compareTo(t2.getName())).collect(Collectors.toList()));
      this.setRepIndTypeParticipants(repIndTypeParticipantManager.findAll().stream()
        .sorted((t1, t2) -> t1.getName().compareTo(t2.getName())).collect(Collectors.toList()));
      this.setRepIndFillingTypes(repIndFillingTypeManager.findAll().stream()
        .sorted((r1, r2) -> r1.getName().compareTo(r2.getName())).collect(Collectors.toList()));
      this.setRepIndPatentStatuses(repIndPatentStatusManager.findAll().stream()
        .sorted((r1, r2) -> r1.getName().compareTo(r2.getName())).collect(Collectors.toList()));
      // Statuses
      statuses = new HashMap<>();
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {
        statuses.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
      }

      deliverableDB = deliverableManager.getDeliverableById(deliverable.getId());

      String params[] = {loggedCrp.getAcronym(), deliverable.getId() + ""};
      this.setBasePermission(this.getText(Permission.PUBLICATION_BASE_INSTITUTION, params));

      if (this.isHttpPost()) {

        if (deliverable.getPublication() != null) {
          deliverable.getPublication().setIsiPublication(null);
          deliverable.getPublication().setCoAuthor(null);
          deliverable.getPublication().setNasr(null);
        }

        deliverable.getDeliverableInfo(deliverable.getPhase()).setDeliverableType(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setCrossCuttingGender(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setCrossCuttingCapacity(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setCrossCuttingNa(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setCrossCuttingYouth(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setIsLocationGlobal(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setLicense(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setCrpClusterKeyOutput(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setRegion(null);
        deliverable.getDeliverableInfo(deliverable.getPhase()).setGeographicScope(null);
        deliverable.setResponsiblePartner(null);

        if (deliverable.getCrps() != null) {
          deliverable.getCrps().clear();
        }
        if (deliverable.getMetadataElements() != null) {
          deliverable.getMetadataElements().clear();
        }

        if (deliverable.getLeaders() != null) {
          deliverable.getLeaders().clear();
        }
        if (deliverable.getPrograms() != null) {
          deliverable.getPrograms().clear();
        }
        deliverable.setFlagshipValue("");
        deliverable.setRegionsValue("");

        if (deliverable.getRegions() != null) {
          deliverable.getRegions().clear();
        }
        if (deliverable.getUsers() != null) {
          deliverable.getUsers().clear();
        }

        if (deliverable.getFundingSources() != null) {
          deliverable.getFundingSources().clear();
        }
        if (deliverable.getGenderLevels() != null) {
          deliverable.getGenderLevels().clear();
        }
        if (deliverable.getDisseminations() != null) {
          deliverable.getDisseminations().clear();
        }
        if (deliverable.getDeliverableParticipant() != null) {
          deliverable.getDeliverableParticipant().setRepIndTypeActivity(null);
          deliverable.getDeliverableParticipant().setRepIndTypeParticipant(null);
        }
        if (deliverable.getIntellectualAsset() != null) {
          deliverable.getIntellectualAsset().setFillingType(null);
          deliverable.getIntellectualAsset().setPatentStatus(null);
        }
        if (deliverable.getCountries() != null) {
          deliverable.getCountries().clear();
        }
      }
    }
  }

  @Override
  public String save() {
    User user = this.getCurrentUser();
    boolean isCreator = user.getId().equals(deliverableDB.getCreatedBy().getId());
    if (isCreator || this.hasPermission("*")) {
      Deliverable deliverablePrew = this.updateDeliverableInfo();
      this.updateDeliverableFS(deliverablePrew);

      this.saveDeliverableGenderLevels(deliverablePrew);
      this.deleteDeliverableGenderLevels(deliverablePrew);
      this.saveDissemination();
      this.saveMetadata();
      this.saveCrps();
      this.savePublicationMetadata();
      this.saveUsers();
      this.saveLeaders();
      this.savePrograms();
      this.saveIntellectualAsset();
      this.saveParticipant();
      // Save Countries list
      this.saveDeliverableCountries();
      deliverableInfoManager.saveDeliverableInfo(deliverablePrew.getDeliverableInfo());

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_DELIVERABLE_INFO);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_FUNDING_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_METADATA_ELEMENT);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_PUBLICATION_METADATA);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_DISEMINATIONS);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_USERS);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_PROGRAMS);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_LEADERS);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_GENDER_LEVELS);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_CRPS);
      relationsName.add(APConstants.PROJECT_DELIVERABLES_INTELLECTUAL_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLES_PARTICIPANT_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_LOCATIONS);
      /**
       * The following is required because we need to update something on the @Deliverable if we want a row
       * created in the auditlog table.
       */
      this.setModificationJustification(deliverablePrew);
      deliverableManager.saveDeliverable(deliverablePrew, this.getActionName(), relationsName, deliverable.getPhase());
      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
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
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void saveCrps() {
    if (deliverable.getCrps() == null) {
      deliverable.setCrps(new ArrayList<>());
    }
    /* Delete */
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableCrp deliverableCrp : deliverableDB.getDeliverableCrps().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList())) {
      if (!deliverable.getCrps().contains(deliverableCrp)) {
        deliverableCrpManager.deleteDeliverableCrp(deliverableCrp.getId());
      }
    }

    /* Save */
    for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {
      if (deliverableCrp.getId() == null || deliverableCrp.getId().intValue() == -1) {
        deliverableCrp.setId(null);
        deliverableCrp.setDeliverable(deliverable);
        deliverableCrp.setPhase(deliverable.getPhase());
        if (deliverableCrp.getGlobalUnit() != null && deliverableCrp.getGlobalUnit().getId() != null
          && deliverableCrp.getGlobalUnit().getId() != -1) {
          deliverableCrp.setCrpProgram(null);
        } else {
          deliverableCrp.setGlobalUnit(null);
        }
        deliverableCrpManager.saveDeliverableCrp(deliverableCrp);
      }
    }
  }

  private void saveDeliverableCountries() {
    if (deliverable.getCountriesIds() != null || !deliverable.getCountriesIds().isEmpty()) {
      List<DeliverableLocation> countries =
        deliverableLocationManager.getDeliverableLocationbyPhase(deliverable.getId(), this.getActualPhase().getId());
      List<DeliverableLocation> countriesSave = new ArrayList<>();
      for (String countryIds : deliverable.getCountriesIds()) {
        DeliverableLocation deliverableLocation = new DeliverableLocation();
        deliverableLocation.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
        deliverableLocation.setDeliverable(deliverable);
        deliverableLocation.setPhase(this.getActualPhase());
        countriesSave.add(deliverableLocation);
        if (!countries.contains(deliverableLocation)) {
          deliverableLocationManager.saveDeliverableLocation(deliverableLocation);
        }
      }
      for (DeliverableLocation deliverableLocation : countries) {
        if (deliverableLocation != null) {
          if (!countriesSave.contains(deliverableLocation)) {
            deliverableLocationManager.deleteDeliverableLocation(deliverableLocation.getId());
          }
        }
      }
    }
  }

  private void saveDeliverableGenderLevels(Deliverable deliverablePrew) {
    if (deliverable.getGenderLevels() != null) {
      if (deliverablePrew.getDeliverableGenderLevels() != null
        && deliverablePrew.getDeliverableGenderLevels().size() > 0) {
        List<DeliverableGenderLevel> fundingSourcesPrew = deliverablePrew.getDeliverableGenderLevels().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList());

        for (DeliverableGenderLevel deliverableFundingSource : fundingSourcesPrew) {
          if (!deliverable.getGenderLevels().contains(deliverableFundingSource)) {
            deliverableGenderLevelManager.deleteDeliverableGenderLevel(deliverableFundingSource.getId());
          }
        }
      }

      for (DeliverableGenderLevel deliverableGenderLevel : deliverable.getGenderLevels()) {
        if (deliverableGenderLevel.getId() == null || deliverableGenderLevel.getId() == -1) {

          deliverableGenderLevel.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
          deliverableGenderLevel.setPhase(deliverable.getPhase());
          deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableGenderLevel);

        } else {
          DeliverableGenderLevel deliverableGenderLevelDB =
            deliverableGenderLevelManager.getDeliverableGenderLevelById(deliverableGenderLevel.getId());
          deliverableGenderLevelDB.setGenderLevel(deliverableGenderLevel.getGenderLevel());
          deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableGenderLevelDB);
        }
      }
    }
  }

  public void saveDissemination() {
    if (deliverable.getDissemination() != null) {

      DeliverableDissemination dissemination = new DeliverableDissemination();
      if (deliverable.getDissemination().getId() != null && deliverable.getDissemination().getId() != -1) {
        dissemination =
          deliverableDisseminationManager.getDeliverableDisseminationById(deliverable.getDissemination().getId());
      } else {
        dissemination = new DeliverableDissemination();
        dissemination.setDeliverable(deliverableManager.getDeliverableById(deliverableID));

      }

      dissemination.setSynced(deliverable.getDissemination().getSynced());

      if (deliverable.getDissemination().getIsOpenAccess() != null) {
        dissemination.setIsOpenAccess(deliverable.getDissemination().getIsOpenAccess());
        if (!deliverable.getDissemination().getIsOpenAccess().booleanValue()) {
          String type = deliverable.getDissemination().getType();
          if (type != null) {
            switch (type) {
              case "intellectualProperty":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(true);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);


                break;
              case "limitedExclusivity":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(true);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);

                break;
              case "restrictedUseAgreement":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(true);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(deliverable.getDissemination().getRestrictedAccessUntil());
                dissemination.setRestrictedEmbargoed(null);

                break;
              case "effectiveDateRestriction":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(true);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(deliverable.getDissemination().getRestrictedEmbargoed());

                break;
              case "notDisseminated":

                dissemination.setNotDisseminated(true);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);

                break;
              default:
                break;
            }
          }
        } else {

          dissemination.setNotDisseminated(false);
          dissemination.setIntellectualProperty(false);
          dissemination.setLimitedExclusivity(false);
          dissemination.setRestrictedUseAgreement(false);
          dissemination.setEffectiveDateRestriction(false);

          dissemination.setRestrictedAccessUntil(null);
          dissemination.setRestrictedEmbargoed(null);
        }
      } else {

        dissemination.setIsOpenAccess(null);
        dissemination.setNotDisseminated(false);
        dissemination.setIntellectualProperty(false);
        dissemination.setLimitedExclusivity(false);
        dissemination.setRestrictedUseAgreement(false);
        dissemination.setEffectiveDateRestriction(false);

        dissemination.setRestrictedAccessUntil(null);
        dissemination.setRestrictedEmbargoed(null);
      }

      if (deliverable.getDissemination().getAlreadyDisseminated() != null) {
        dissemination.setAlreadyDisseminated(deliverable.getDissemination().getAlreadyDisseminated());
        if (deliverable.getDissemination().getAlreadyDisseminated().booleanValue()) {

          dissemination.setDisseminationUrl(deliverable.getDissemination().getDisseminationUrl());
          dissemination.setDisseminationChannel(deliverable.getDissemination().getDisseminationChannel());
        } else {
          dissemination.setDisseminationUrl(null);
          dissemination.setDisseminationChannel(null);
        }
      } else {
        dissemination.setAlreadyDisseminated(null);
        dissemination.setDisseminationUrl(null);
        dissemination.setDisseminationChannel(null);
      }

      dissemination.setPhase(deliverable.getPhase());
      deliverableDisseminationManager.saveDeliverableDissemination(dissemination);

    }
  }


  private void saveIntellectualAsset() {
    if (deliverable.getIntellectualAsset() != null && deliverable.getIntellectualAsset().getHasPatentPvp() != null) {
      DeliverableIntellectualAsset intellectualAsset = new DeliverableIntellectualAsset();

      if (deliverable.getIntellectualAsset().getId() != null && deliverable.getIntellectualAsset().getId() != -1) {
        intellectualAsset = deliverableIntellectualAssetManager
          .getDeliverableIntellectualAssetById(deliverable.getIntellectualAsset().getId());

      } else {
        intellectualAsset = new DeliverableIntellectualAsset();
        intellectualAsset.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
        intellectualAsset.setPhase(deliverable.getPhase());
      }

      intellectualAsset.setHasPatentPvp(deliverable.getIntellectualAsset().getHasPatentPvp());

      if (intellectualAsset.getHasPatentPvp()) {
        intellectualAsset.setAdditionalInformation(deliverable.getIntellectualAsset().getAdditionalInformation());
        intellectualAsset.setApplicant(deliverable.getIntellectualAsset().getApplicant());
        intellectualAsset.setLink(deliverable.getIntellectualAsset().getLink());
        intellectualAsset.setPublicCommunication(deliverable.getIntellectualAsset().getPublicCommunication());
        intellectualAsset.setTitle(deliverable.getIntellectualAsset().getTitle());
        intellectualAsset.setDateFilling(deliverable.getIntellectualAsset().getDateFilling());
        intellectualAsset.setDateRegistration(deliverable.getIntellectualAsset().getDateRegistration());
        intellectualAsset.setDateExpiry(deliverable.getIntellectualAsset().getDateExpiry());
        intellectualAsset.setType(deliverable.getIntellectualAsset().getType());

        if (intellectualAsset.getType() != null) {
          if (DeliverableIntellectualAssetTypeEnum.getValue(intellectualAsset.getType())
            .equals(DeliverableIntellectualAssetTypeEnum.Patent)) {
            if (deliverable.getIntellectualAsset().getFillingType() != null
              && deliverable.getIntellectualAsset().getFillingType().getId() != -1) {
              intellectualAsset.setFillingType(deliverable.getIntellectualAsset().getFillingType());
            } else {
              intellectualAsset.setFillingType(null);
            }
            if (deliverable.getIntellectualAsset().getPatentStatus() != null
              && deliverable.getIntellectualAsset().getPatentStatus().getId() != -1) {
              intellectualAsset.setPatentStatus(deliverable.getIntellectualAsset().getPatentStatus());
            } else {
              intellectualAsset.setPatentStatus(null);
            }
            intellectualAsset.setPatentType(deliverable.getIntellectualAsset().getPatentType());
            intellectualAsset.setVarietyName(null);
            intellectualAsset.setStatus(null);
            intellectualAsset.setCountry(null);
            intellectualAsset.setAppRegNumber(null);
            intellectualAsset.setBreederCrop(null);
          } else if (DeliverableIntellectualAssetTypeEnum.getValue(intellectualAsset.getType())
            .equals(DeliverableIntellectualAssetTypeEnum.PVP)) {
            intellectualAsset.setVarietyName(deliverable.getIntellectualAsset().getVarietyName());
            if (deliverable.getIntellectualAsset().getStatus() != null
              && deliverable.getIntellectualAsset().getStatus() != -1) {
              intellectualAsset.setStatus(deliverable.getIntellectualAsset().getStatus());
            } else {
              intellectualAsset.setStatus(null);
            }
            if (deliverable.getIntellectualAsset().getCountry() != null
              && !deliverable.getIntellectualAsset().getCountry().getIsoAlpha2().equals("-1")) {
              LocElement locElement = locElementManager
                .getLocElementByISOCode(deliverable.getIntellectualAsset().getCountry().getIsoAlpha2());
              if (locElement != null) {
                intellectualAsset.setCountry(locElement);
              } else {
                intellectualAsset.setCountry(null);
              }
            } else {
              intellectualAsset.setCountry(null);
            }
            intellectualAsset.setAppRegNumber(deliverable.getIntellectualAsset().getAppRegNumber());
            intellectualAsset.setBreederCrop(deliverable.getIntellectualAsset().getBreederCrop());
            intellectualAsset.setFillingType(null);
            intellectualAsset.setPatentStatus(null);
            intellectualAsset.setPatentType(null);
          }
        } else {
          intellectualAsset.setFillingType(null);
          intellectualAsset.setPatentStatus(null);
          intellectualAsset.setPatentType(null);
          intellectualAsset.setVarietyName(null);
          intellectualAsset.setStatus(null);
          intellectualAsset.setCountry(null);
          intellectualAsset.setAppRegNumber(null);
          intellectualAsset.setBreederCrop(null);
        }
      } else {
        intellectualAsset.setAdditionalInformation(null);
        intellectualAsset.setApplicant(null);
        intellectualAsset.setLink(null);
        intellectualAsset.setPublicCommunication(null);
        intellectualAsset.setTitle(null);
        intellectualAsset.setType(null);
        intellectualAsset.setFillingType(null);
        intellectualAsset.setPatentStatus(null);
        intellectualAsset.setPatentType(null);
        intellectualAsset.setVarietyName(null);
        intellectualAsset.setStatus(null);
        intellectualAsset.setCountry(null);
        intellectualAsset.setAppRegNumber(null);
        intellectualAsset.setBreederCrop(null);
        intellectualAsset.setDateFilling(null);
        intellectualAsset.setDateRegistration(null);
        intellectualAsset.setDateExpiry(null);
      }

      deliverableIntellectualAssetManager.saveDeliverableIntellectualAsset(intellectualAsset);
    }
  }


  public void saveLeaders() {
    if (deliverable.getLeaders() == null) {

      deliverable.setLeaders(new ArrayList<>());
    }
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableLeader deliverableUser : deliverableDB.getDeliverableLeaders()) {
      if (!deliverable.getLeaders().contains(deliverableUser)) {
        deliverableLeaderManager.deleteDeliverableLeader(deliverableUser.getId());
      }
    }

    for (DeliverableLeader deliverableUser : deliverable.getLeaders()) {

      if (deliverableUser.getId() == null || deliverableUser.getId().intValue() == -1) {
        deliverableUser.setId(null);
        deliverableUser.setDeliverable(deliverable);
        deliverableUser.setPhase(deliverable.getPhase());
        deliverableLeaderManager.saveDeliverableLeader(deliverableUser);
      }
    }
  }


  public void saveMetadata() {
    if (deliverable.getMetadataElements() != null) {
      for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getMetadataElements()) {
        if (deliverableMetadataElement != null && deliverableMetadataElement.getMetadataElement() != null) {
          deliverableMetadataElement.setDeliverable(deliverable);
          deliverableMetadataElement.setPhase(deliverable.getPhase());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElement);
        }
      }
    }
  }


  private void saveParticipant() {
    if (deliverable.getDeliverableParticipant() != null
      && deliverable.getDeliverableParticipant().getHasParticipants() != null) {
      DeliverableParticipant participant = new DeliverableParticipant();
      DeliverableParticipant deliverableParticipantDB = new DeliverableParticipant();
      List<DeliverableParticipant> deliverableParticipants = deliverableParticipantManager
        .findDeliverableParticipantByDeliverableAndPhase(deliverable.getId(), deliverable.getPhase().getId());
      if (deliverableParticipants != null && deliverableParticipants.size() > 0) {
        deliverableParticipantDB = deliverableParticipants.get(0);
        if (deliverableParticipants.size() > 1) {
          LOG.warn("There is more than one deliverableParticipant in database for deliverable: " + deliverable.getId()
            + ", phase: " + deliverable.getPhase().getComposedName());
        }
      }
      if (deliverableParticipantDB != null && deliverableParticipantDB.getId() != null
        && deliverableParticipantDB.getId() != -1) {
        participant = deliverableParticipantManager.getDeliverableParticipantById(deliverableParticipantDB.getId());
      } else {
        participant.setId(null);
        participant.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
        participant.setPhase(deliverable.getPhase());
        participant = deliverableParticipantManager.saveDeliverableParticipant(participant);
      }

      participant.setHasParticipants(deliverable.getDeliverableParticipant().getHasParticipants());

      if (participant.getHasParticipants()) {
        participant.setEventActivityName(deliverable.getDeliverableParticipant().getEventActivityName());
        if (deliverable.getDeliverableParticipant().getRepIndTypeActivity() != null
          && deliverable.getDeliverableParticipant().getRepIndTypeActivity().getId() != -1) {

          participant.setRepIndTypeActivity(deliverable.getDeliverableParticipant().getRepIndTypeActivity());

          if (participant.getRepIndTypeActivity().getId().equals(this.getReportingIndTypeActivityAcademicDegree())) {
            participant.setAcademicDegree(deliverable.getDeliverableParticipant().getAcademicDegree());
          } else {
            participant.setAcademicDegree(null);
          }

        } else {
          participant.setRepIndTypeActivity(null);
          participant.setAcademicDegree(null);
        }
        participant.setParticipants(deliverable.getDeliverableParticipant().getParticipants());
        if (deliverable.getDeliverableParticipant().getEstimateParticipants() != null) {
          participant.setEstimateParticipants(deliverable.getDeliverableParticipant().getEstimateParticipants());
        } else {
          participant.setEstimateParticipants(false);
        }
        participant.setFemales(deliverable.getDeliverableParticipant().getFemales());
        if (deliverable.getDeliverableParticipant().getEstimateFemales() != null) {
          participant.setEstimateFemales(deliverable.getDeliverableParticipant().getEstimateFemales());
        } else {
          participant.setEstimateFemales(false);
        }
        if (deliverable.getDeliverableParticipant().getDontKnowFemale() != null) {
          participant.setDontKnowFemale(deliverable.getDeliverableParticipant().getDontKnowFemale());
        } else {
          participant.setDontKnowFemale(false);
        }
        if (deliverable.getDeliverableParticipant().getRepIndTypeParticipant() != null
          && deliverable.getDeliverableParticipant().getRepIndTypeParticipant().getId() != -1) {
          participant.setRepIndTypeParticipant(deliverable.getDeliverableParticipant().getRepIndTypeParticipant());
        } else {
          participant.setRepIndTypeParticipant(null);
        }

      } else {
        participant.setEventActivityName(null);
        participant.setRepIndTypeActivity(null);
        participant.setAcademicDegree(null);
        participant.setParticipants(null);
        participant.setEstimateParticipants(null);
        participant.setFemales(null);
        participant.setEstimateFemales(null);
        participant.setDontKnowFemale(null);
        participant.setRepIndTypeParticipant(null);
      }

      deliverableParticipantManager.saveDeliverableParticipant(participant);

    }

  }


  public void savePrograms() {

    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    if (deliverable.getFlagshipValue() == null) {
      deliverable.setFlagshipValue("");
    }
    if (deliverable.getRegionsValue() == null) {
      deliverable.setRegionsValue("");
    }
    if (deliverable.getFlagshipValue() != null) {

      for (DeliverableProgram deliverableProgram : deliverableDB.getDeliverablePrograms().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList())) {

        if (!deliverable.getFlagshipValue().contains(deliverableProgram.getCrpProgram().getId().toString())) {
          deliverableProgramManager.deleteDeliverableProgram(deliverableProgram.getId());

        }
      }
      for (String programID : deliverable.getFlagshipValue().trim().split(",")) {
        if (programID.length() > 0) {
          CrpProgram program = crpProgramManager.getCrpProgramById(Long.parseLong(programID.trim()));
          DeliverableProgram deliverableProgram = new DeliverableProgram();
          deliverableProgram.setCrpProgram(program);
          deliverableProgram.setDeliverable(deliverable);
          deliverableProgram.setPhase(deliverable.getPhase());
          if (deliverableDB.getDeliverablePrograms().stream()
            .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == program.getId().longValue())
            .collect(Collectors.toList()).isEmpty()) {

            deliverableProgramManager.saveDeliverableProgram(deliverableProgram);
          }
        }

      }
    }

    if (deliverable.getRegionsValue() != null) {

      for (DeliverableProgram deliverableProgram : deliverableDB.getDeliverablePrograms().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList())) {

        if (!deliverable.getRegionsValue().contains(deliverableProgram.getCrpProgram().getId().toString())) {
          deliverableProgramManager.deleteDeliverableProgram(deliverableProgram.getId());

        }
      }
      for (String programID : deliverable.getRegionsValue().trim().split(",")) {
        if (programID.length() > 0) {
          CrpProgram program = crpProgramManager.getCrpProgramById(Long.parseLong(programID.trim()));
          DeliverableProgram deliverableProgram = new DeliverableProgram();
          deliverableProgram.setCrpProgram(program);
          deliverableProgram.setDeliverable(deliverable);
          deliverableProgram.setPhase(deliverable.getPhase());
          if (deliverableDB.getDeliverablePrograms().stream()
            .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == program.getId().longValue())
            .collect(Collectors.toList()).isEmpty()) {
            deliverableProgramManager.saveDeliverableProgram(deliverableProgram);
          }
        } else {
          Log.debug("No regional programs can be found in String : " + programID);
        }
      }
    }

  }

  public void savePublicationMetadata() {
    if (deliverable.getPublication() != null) {
      deliverable.getPublication().setDeliverable(deliverable);
      if (deliverable.getPublication().getId() != null && deliverable.getPublication().getId().intValue() == -1) {
        deliverable.getPublication().setId(null);
      }
      deliverable.getPublication().setPhase(deliverable.getPhase());
      deliverablePublicationMetadataManager.saveDeliverablePublicationMetadata(deliverable.getPublication());

    }
  }

  public void saveUsers() {
    if (deliverable.getUsers() == null) {

      deliverable.setUsers(new ArrayList<>());
    }
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableUser deliverableUser : deliverableDB.getDeliverableUsers().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(deliverable.getPhase())).collect(Collectors.toList())) {
      if (!deliverable.getUsers().contains(deliverableUser)) {
        deliverableUserManager.deleteDeliverableUser(deliverableUser.getId());
      }
    }

    for (DeliverableUser deliverableUser : deliverable.getUsers()) {

      if (deliverableUser.getId() == null || deliverableUser.getId().intValue() == -1) {
        deliverableUser.setId(null);
        deliverableUser.setPhase(deliverable.getPhase());
        deliverableUser.setDeliverable(deliverable);
        deliverableUserManager.saveDeliverableUser(deliverableUser);
      }
    }
  }

  public void setChannels(Map<String, String> channels) {
    this.channels = channels;
  }


  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }

  public void setCrossCuttingDimensions(List<CrossCuttingScoring> crossCuttingDimensions) {
    this.crossCuttingDimensions = crossCuttingDimensions;
  }


  public void setCrossCuttingScoresMap(Map<Long, String> crossCuttingScoresMap) {
    this.crossCuttingScoresMap = crossCuttingScoresMap;
  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setDeliverableDB(Deliverable deliverableDB) {
    this.deliverableDB = deliverableDB;
  }

  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }


  public void setDeliverableSubTypes(List<DeliverableType> deliverableSubTypes) {
    this.deliverableSubTypes = deliverableSubTypes;
  }

  public void setFundingSources(List<FundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }


  public void setGenderLevels(List<GenderType> genderLevels) {
    this.genderLevels = genderLevels;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setKeyOutputs(List<CrpClusterKeyOutput> keyOutputs) {
    this.keyOutputs = keyOutputs;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setRepIndFillingTypes(List<RepIndFillingType> repIndFillingTypes) {
    this.repIndFillingTypes = repIndFillingTypes;
  }


  public void setRepIndGeographicScopes(List<RepIndGeographicScope> repIndGeographicScopes) {
    this.repIndGeographicScopes = repIndGeographicScopes;
  }

  public void setRepIndPatentStatuses(List<RepIndPatentStatus> repIndPatentStatuses) {
    this.repIndPatentStatuses = repIndPatentStatuses;
  }


  public void setRepIndRegions(List<RepIndRegion> repIndRegions) {
    this.repIndRegions = repIndRegions;
  }


  public void setRepIndTypeActivities(List<RepIndTypeActivity> repIndTypeActivities) {
    this.repIndTypeActivities = repIndTypeActivities;
  }

  public void setRepIndTypeParticipants(List<RepIndTypeParticipant> repIndTypeParticipants) {
    this.repIndTypeParticipants = repIndTypeParticipants;
  }

  public void setRepositoryChannels(List<RepositoryChannel> repositoryChannels) {
    this.repositoryChannels = repositoryChannels;
  }

  public void setStatuses(Map<String, String> statuses) {
    this.statuses = statuses;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  /**
   * This could be merged into a common mapping class.
   * 
   * @param deliverablePrew
   */
  private void updateDeliverableFS(Deliverable deliverablePrew) {
    if (deliverable.getFundingSources() != null) {
      if (deliverablePrew.getDeliverableFundingSources() != null
        && deliverablePrew.getDeliverableFundingSources().size() > 0) {
        List<DeliverableFundingSource> fundingSourcesPrew = deliverablePrew.getDeliverableFundingSources().stream()
          .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(deliverable.getPhase()))
          .collect(Collectors.toList());


        for (DeliverableFundingSource deliverableFundingSource : fundingSourcesPrew) {
          if (!deliverable.getFundingSources().contains(deliverableFundingSource)) {
            deliverableFundingSourceManager.deleteDeliverableFundingSource(deliverableFundingSource.getId());
          }
        }
      }

      for (DeliverableFundingSource deliverableFundingSource : deliverable.getFundingSources()) {
        if (deliverableFundingSource.getId() == null || deliverableFundingSource.getId() == -1) {


          deliverableFundingSource.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
          deliverableFundingSource.setPhase(deliverable.getPhase());
          deliverableFundingSourceManager.saveDeliverableFundingSource(deliverableFundingSource);


        }
      }
    }
  }


  private Deliverable updateDeliverableInfo() {
    // deliverableDb is in a managed state, deliverable is in a detached state.
    Deliverable deliverableBase = deliverableManager.getDeliverableById(deliverableID);
    DeliverableInfo deliverableInfoDb = deliverableBase.getDeliverableInfo(deliverable.getPhase());

    deliverableInfoDb.setTitle(deliverable.getDeliverableInfo(deliverable.getPhase()).getTitle());
    deliverableInfoDb.setDescription(deliverable.getDeliverableInfo().getDescription());

    deliverableInfoDb.setYear(deliverable.getDeliverableInfo().getYear());

    if (deliverable.getDeliverableInfo().getNewExpectedYear() != null) {
      deliverableInfoDb.setNewExpectedYear(deliverable.getDeliverableInfo().getNewExpectedYear());
    }

    if (deliverable.getDeliverableInfo().getCrossCuttingCapacity() == null) {
      deliverableInfoDb.setCrossCuttingCapacity(false);
      deliverableInfoDb.setCrossCuttingScoreCapacity(APConstants.CROSS_CUTTING_NOT_TARGETED);
    } else {
      deliverableInfoDb.setCrossCuttingCapacity(true);
      deliverableInfoDb.setCrossCuttingScoreCapacity(deliverable.getDeliverableInfo().getCrossCuttingScoreCapacity());
    }
    if (deliverable.getDeliverableInfo().getCrossCuttingNa() == null) {
      deliverableInfoDb.setCrossCuttingNa(false);
    } else {
      deliverableInfoDb.setCrossCuttingNa(true);
    }
    if (deliverable.getDeliverableInfo().getCrossCuttingGender() == null) {
      deliverableInfoDb.setCrossCuttingGender(false);
      deliverableInfoDb.setCrossCuttingScoreGender(APConstants.CROSS_CUTTING_NOT_TARGETED);
    } else {
      deliverableInfoDb.setCrossCuttingGender(true);
      deliverableInfoDb.setCrossCuttingScoreGender(deliverable.getDeliverableInfo().getCrossCuttingScoreGender());
    }
    if (deliverable.getDeliverableInfo().getCrossCuttingYouth() == null) {
      deliverableInfoDb.setCrossCuttingYouth(false);
      deliverableInfoDb.setCrossCuttingScoreYouth(APConstants.CROSS_CUTTING_NOT_TARGETED);
    } else {
      deliverableInfoDb.setCrossCuttingYouth(true);
      deliverableInfoDb.setCrossCuttingScoreYouth(deliverable.getDeliverableInfo().getCrossCuttingScoreYouth());
    }

    if (deliverable.getDeliverableInfo().getStatus() != null) {
      deliverableInfoDb.setStatus(deliverable.getDeliverableInfo().getStatus());
    }

    if (deliverable.getDeliverableInfo().getDeliverableType() != null
      && deliverable.getDeliverableInfo().getDeliverableType().getId() != null
      && deliverable.getDeliverableInfo().getDeliverableType().getId().longValue() != -1) {
      DeliverableType deliverableType =
        deliverableTypeManager.getDeliverableTypeById(deliverable.getDeliverableInfo().getDeliverableType().getId());

      deliverableInfoDb.setDeliverableType(deliverableType);
    } else {
      deliverableInfoDb.setDeliverableType(null);
    }

    if (deliverable.getDeliverableInfo().getAdoptedLicense() != null) {
      deliverableInfoDb.setAdoptedLicense(deliverable.getDeliverableInfo().getAdoptedLicense());
      if (deliverable.getDeliverableInfo().getAdoptedLicense().booleanValue()) {
        deliverableInfoDb.setLicense(deliverable.getDeliverableInfo().getLicense());
        if (deliverable.getDeliverableInfo().getLicense() != null) {
          if (deliverable.getDeliverableInfo().getLicense().equals(LicensesTypeEnum.OTHER.getValue())) {
            deliverableInfoDb.setOtherLicense(deliverable.getDeliverableInfo().getOtherLicense());
            deliverableInfoDb.setAllowModifications(deliverable.getDeliverableInfo().getAllowModifications());
          } else {
            deliverableInfoDb.setOtherLicense(null);
            deliverableInfoDb.setAllowModifications(null);
          }
        }

        deliverableInfoDb.setAdoptedLicense(deliverable.getDeliverableInfo().getAdoptedLicense());
      } else {

        deliverableInfoDb.setLicense(null);
        deliverableInfoDb.setOtherLicense(null);
        deliverableInfoDb.setAllowModifications(null);
      }
    } else {
      deliverableInfoDb.setLicense(null);
      deliverableInfoDb.setOtherLicense(null);
      deliverableInfoDb.setAllowModifications(null);
    }

    List<DeliverableLocation> countries =
      deliverableLocationManager.getDeliverableLocationbyPhase(deliverable.getId(), this.getActualPhase().getId());
    // Save Locations
    if (deliverable.getDeliverableInfo().getGeographicScope() != null
      && deliverable.getDeliverableInfo().getGeographicScope().getId() != -1) {
      deliverableInfoDb.setGeographicScope(deliverable.getDeliverableInfo().getGeographicScope());
      RepIndGeographicScope repIndGeographicScope =
        repIndGeographicScopeManager.getRepIndGeographicScopeById(deliverableInfoDb.getGeographicScope().getId());
      // Global
      if (repIndGeographicScope.getId().equals(this.getReportingIndGeographicScopeGlobal())) {
        deliverableInfoDb.setRegion(null);
        this.deleteDeliverableLocations(countries);
      } else
      // Regional
      if (repIndGeographicScope.getId().equals(this.getReportingIndGeographicScopeRegional())) {
        if (deliverable.getDeliverableInfo().getRegion() != null
          && deliverable.getDeliverableInfo().getRegion().getId() != -1) {
          deliverableInfoDb.setRegion(deliverable.getDeliverableInfo().getRegion());
        } else {
          deliverableInfoDb.setRegion(null);
        }
        this.deleteDeliverableLocations(countries);
      } else {
        // Multi-national || National || Sub-national
        deliverableInfoDb.setRegion(null);
      }
    } else {
      deliverableInfoDb.setGeographicScope(null);
      deliverableInfoDb.setRegion(null);
      this.deleteDeliverableLocations(countries);
    }

    deliverableInfoDb.setIsLocationGlobal(deliverable.getDeliverableInfo().getIsLocationGlobal() != null
      ? deliverable.getDeliverableInfo().getIsLocationGlobal() : false);

    // Set CrpClusterKeyOutput to null if has an -1 id
    if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput() != null
      && deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getId() != null
      && deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getId().longValue() == -1) {
      deliverableInfoDb.setCrpClusterKeyOutput(null);
    } else {
      deliverableInfoDb.setCrpClusterKeyOutput(deliverable.getDeliverableInfo().getCrpClusterKeyOutput());
    }

    deliverableInfoDb.setStatusDescription(deliverable.getDeliverableInfo().getStatusDescription());
    deliverableInfoDb.setModificationJustification(this.getJustification());
    deliverableBase.setDeliverableInfo(deliverableInfoDb);
    return deliverableBase;
  }

  @Override
  public void validate() {
    if (save) {
      publicationValidator.validate(this, deliverable, true);
    }
  }
}