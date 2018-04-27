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
import org.cgiar.ccafs.marlo.data.manager.DeliverableGenderLevelManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.RepositoryChannelManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingScoring;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GenderType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.publications.PublicationValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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


  /**
   * 
   */
  private static final long serialVersionUID = -5176367401132626314L;

  private final Logger LOG = LoggerFactory.getLogger(PublicationAction.class);


  private GlobalUnit loggedCrp;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  private long deliverableID;

  private DeliverableCrpManager deliverableCrpManager;
  private Map<String, String> crps;
  private List<GenderType> genderLevels;
  private IpProgramManager ipProgramManager;
  private Map<String, String> programs;
  private List<FundingSource> fundingSources;

  private List<CrpProgram> flagshipsList;


  private List<CrpProgram> regionsList;

  private Map<String, String> regions;
  private Map<String, String> institutions;
  private Map<String, String> channels;
  private DeliverableManager deliverableManager;
  private PublicationValidator publicationValidator;
  private Deliverable deliverable;
  private DeliverableDisseminationManager deliverableDisseminationManager;
  private DeliverableGenderLevelManager deliverableGenderLevelManager;
  private DeliverableMetadataElementManager deliverableMetadataElementManager;
  private DeliverablePublicationMetadataManager deliverablePublicationMetadataManager;
  private DeliverableUserManager deliverableUserManager;

  private DeliverableLeaderManager deliverableLeaderManager;

  private DeliverableProgramManager deliverableProgramManager;
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;


  private String transaction;


  private AuditLogManager auditLogManager;

  private DeliverableQualityCheckManager deliverableQualityCheckManager;


  private MetadataElementManager metadataElementManager;

  private HistoryComparator historyComparator;
  private UserManager userManager;
  private InstitutionManager institutionManager;
  private List<DeliverableType> deliverableSubTypes;
  private GenderTypeManager genderTypeManager;
  private DeliverableTypeManager deliverableTypeManager;
  private RepositoryChannelManager repositoryChannelManager;
  private List<RepositoryChannel> repositoryChannels;

  private CrpProgramManager crpProgramManager;
  private FundingSourceManager fundingSourceManager;
  private CrossCuttingScoringManager crossCuttingManager;

  private List<CrossCuttingScoring> crossCuttingDimensions;

  private Map<Long, String> crossCuttingScoresMap;

  private List<CrpClusterKeyOutput> keyOutputs;

  @Inject
  public PublicationAction(APConfig config, GlobalUnitManager crpManager, DeliverableManager deliverableManager,
    GenderTypeManager genderTypeManager, DeliverableQualityCheckManager deliverableQualityCheckManager,
    AuditLogManager auditLogManager, DeliverableTypeManager deliverableTypeManager,
    MetadataElementManager metadataElementManager, UserManager userManager,
    DeliverableDisseminationManager deliverableDisseminationManager, InstitutionManager institutionManager,
    DeliverablePublicationMetadataManager deliverablePublicationMetadataManager,
    DeliverableGenderLevelManager deliverableGenderLevelManager, DeliverableUserManager deliverableUserManager,
    DeliverableCrpManager deliverableCrpManager, CrpPpaPartnerManager crpPpaPartnerManager,
    DeliverableProgramManager deliverableProgramManager, DeliverableLeaderManager deliverableLeaderManager,
    PublicationValidator publicationValidator, HistoryComparator historyComparator,
    DeliverableMetadataElementManager deliverableMetadataElementManager, IpProgramManager ipProgramManager,
    RepositoryChannelManager repositoryChannelManager, CrpProgramManager crpProgramManager,
    FundingSourceManager fundingSourceManager, CrossCuttingScoringManager crossCuttingManager,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager) {

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
    this.deliverableQualityCheckManager = deliverableQualityCheckManager;
    this.deliverablePublicationMetadataManager = deliverablePublicationMetadataManager;
    this.deliverableUserManager = deliverableUserManager;
    this.institutionManager = institutionManager;
    this.deliverableProgramManager = deliverableProgramManager;
    this.deliverableLeaderManager = deliverableLeaderManager;
    this.deliverableMetadataElementManager = deliverableMetadataElementManager;
    this.metadataElementManager = metadataElementManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.ipProgramManager = ipProgramManager;
    this.userManager = userManager;
    this.repositoryChannelManager = repositoryChannelManager;
    this.crpProgramManager = crpProgramManager;
    this.fundingSourceManager = fundingSourceManager;
    this.crossCuttingManager = crossCuttingManager;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;
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
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public Map<String, String> getChannels() {
    return channels;
  }

  public List<CrossCuttingScoring> getCrossCuttingDimensions() {
    return crossCuttingDimensions;
  }

  public Map<Long, String> getCrossCuttingScoresMap() {
    return crossCuttingScoresMap;
  }

  public Map<String, String> getCrps() {
    return crps;
  }


  public Deliverable getDeliverable() {
    return deliverable;
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
      .filter(
        c -> c.getCrp().equals(this.loggedCrp) && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
  }


  public List<FundingSource> getFundingSources() {
    return fundingSources;
  }


  public List<GenderType> getGenderLevels() {
    return genderLevels;
  }

  public Map<String, String> getInstitutions() {
    return institutions;
  }


  public List<CrpClusterKeyOutput> getKeyOutputs() {
    return keyOutputs;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public Map<String, String> getPrograms() {
    return programs;
  }


  public Map<String, String> getRegions() {
    return regions;
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
      .filter(
        c -> c.getCrp().equals(this.loggedCrp) && c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
  }


  public List<RepositoryChannel> getRepositoryChannels() {
    return repositoryChannels;
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
        Map<String, String> specialList = new HashMap<>();
        this.setDifferences(historyComparator.getDifferences(transaction, specialList, "deliverable"));
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      deliverable = deliverableManager.getDeliverableById(deliverableID);
    }

    if (deliverable != null) {

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && !this.isHttpPost()) {

        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        deliverable = (Deliverable) autoSaveReader.readFromJson(jReader);
        if (metadataElementManager.findAll() != null) {
          deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
        }
        deliverable.getDeliverableInfo(this.getActualPhase());
        if (deliverable.getFundingSources() != null) {
          for (DeliverableFundingSource fundingSource : deliverable.getFundingSources()) {
            if (fundingSource != null && fundingSource.getFundingSource() != null) {
              fundingSource
                .setFundingSource(fundingSourceManager.getFundingSourceById(fundingSource.getFundingSource().getId()));
              fundingSource.getFundingSource().getFundingSourceInfo(this.getActualPhase());
            }
          }
        }

        if (deliverable.getCrps() != null) {
          for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {
            if (deliverableCrp != null) {
              if (deliverableCrp.getCrpProgram() == null || deliverableCrp.getCrpProgram().getId() == null
                || deliverableCrp.getCrpProgram().getId().intValue() == -1) {
                deliverableCrp.setGlobalUnit(crpManager.getGlobalUnitById(deliverableCrp.getGlobalUnit().getId()));
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

        this.setDraft(true);
      } else {
        deliverable.getDeliverableInfo(this.getActualPhase());

        deliverable.setFundingSources(deliverable.getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList()));
        for (DeliverableFundingSource deliverableFundingSource : deliverable.getFundingSources()) {

          deliverableFundingSource.setFundingSource(
            fundingSourceManager.getFundingSourceById(deliverableFundingSource.getFundingSource().getId()));
          deliverableFundingSource.getFundingSource().setFundingSourceInfo(
            deliverableFundingSource.getFundingSource().getFundingSourceInfo(this.getActualPhase()));
          if (deliverableFundingSource.getFundingSource().getFundingSourceInfo() == null) {
            deliverableFundingSource.getFundingSource().setFundingSourceInfo(
              deliverableFundingSource.getFundingSource().getFundingSourceInfoLast(this.getActualPhase()));
          }
        }
        deliverable.setGenderLevels(deliverable.getDeliverableGenderLevels().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));


        deliverable.setLeaders(
          deliverable.getDeliverableLeaders().stream().filter(dl -> dl.getPhase().equals(this.getActualPhase()))
            .sorted((l1, l2) -> l1.getInstitution().getName().compareTo(l2.getInstitution().getName()))
            .collect(Collectors.toList()));
        List<DeliverableProgram> deliverablePrograms = deliverable.getDeliverablePrograms().stream()
          .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

        deliverable.setPrograms(deliverablePrograms.stream()
          .filter(c -> c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .sorted((f1, f2) -> f1.getCrpProgram().getAcronym().compareTo(f2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList()));
        deliverable.setRegions(deliverablePrograms.stream()
          .filter(c -> c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .sorted((r1, r2) -> r1.getCrpProgram().getAcronym().compareTo(r2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList()));


        if (deliverable.getDeliverableMetadataElements() != null) {
          deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
        }


        if (deliverable.getDeliverableDisseminations() != null) {
          deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          if (deliverable.getDisseminations().size() > 0) {
            deliverable.setDissemination(deliverable.getDisseminations().get(0));
          } else {
            deliverable.setDissemination(new DeliverableDissemination());
          }
        }


        if (deliverable.getDeliverableDataSharingFiles() != null) {
          deliverable.setDataSharingFiles(new ArrayList<>(deliverable.getDeliverableDataSharingFiles().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
        }

        if (deliverable.getDeliverablePublicationMetadatas() != null) {
          deliverable.setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
        }
        if (!deliverable.getPublicationMetadatas().isEmpty()) {
          deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
        }

        if (deliverable.getDeliverableDataSharings() != null) {
          deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
        }

        deliverable.setUsers(deliverable.getDeliverableUsers().stream()
          .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
        deliverable.setCrps(deliverable.getDeliverableCrps().stream()
          .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
        deliverable.setFiles(new ArrayList<>());
        for (DeliverableDataSharingFile dataSharingFile : deliverable.getDataSharingFiles()) {

          DeliverableFile deFile = new DeliverableFile();
          switch (dataSharingFile.getTypeId().toString()) {
            case APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED:
              deFile.setHosted(APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED_STR);
              deFile.setName(dataSharingFile.getFile().getFileName());
              break;

            case APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED:
              deFile.setHosted(APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED_STR);
              deFile.setName(dataSharingFile.getExternalFile());
              break;
          }
          deFile.setId(dataSharingFile.getId());
          deFile.setSize(0);
          deliverable.getFiles().add(deFile);
        }
        if (deliverable.getFiles() != null) {
          deliverable.getFiles().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
        }

        if (deliverable.getDeliverableIntellectualAssets() != null) {
          List<DeliverableIntellectualAsset> intellectualAssets = deliverable.getDeliverableIntellectualAssets()
            .stream().filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

          if (intellectualAssets.size() > 0) {
            deliverable.setIntellectualAsset(intellectualAssets.get(0));
          } else {
            deliverable.setIntellectualAsset(new DeliverableIntellectualAsset());
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
          .filter(c -> c.getCrp() != null && c.getCrp().getId().longValue() == loggedCrp.getId().longValue())
          .collect(Collectors.toList());
      } else {
        genderTypes = genderTypeManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList());
      }

      for (GenderType projectStatusEnum : genderTypes) {
        genderLevels.add(projectStatusEnum);
      }


      deliverableSubTypes = new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.getDeliverableCategory() != null && dt.getDeliverableCategory().getId().intValue() == 49)
        .collect(Collectors.toList()));
      deliverableSubTypes.add(deliverableTypeManager.getDeliverableTypeById(55));
      deliverableSubTypes.add(deliverableTypeManager.getDeliverableTypeById(56));

      crps = new HashMap<>();
      for (GlobalUnit crp : crpManager.findAll().stream()
        .filter(c -> c.getId() != this.getLoggedCrp().getId() && c.isActive()).collect(Collectors.toList())) {
        crps.put(crp.getId().toString(), crp.getName());
      }

      programs = new HashMap<>();
      for (IpProgram program : ipProgramManager.findAll().stream().filter(c -> c.getIpProgramType().getId() == 4)
        .collect(Collectors.toList())) {
        programs.put(program.getId().toString(), program.getAcronym());
      }
      regions = new HashMap<>();
      for (IpProgram program : ipProgramManager.findAll().stream().filter(c -> c.getIpProgramType().getId() == 5)
        .collect(Collectors.toList())) {
        regions.put(program.getId().toString(), program.getAcronym());
      }


      this.fundingSources = fundingSourceManager.findAll().stream()
        .filter(
          fs -> fs.getCrp().equals(this.getCurrentCrp()) && fs.getFundingSourceInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList());

      fundingSources.sort((f1, f2) -> f1.getId().compareTo(f2.getId()));


      repositoryChannels = repositoryChannelManager.findAll();
      if (repositoryChannels != null && repositoryChannels.size() > 0) {
        repositoryChannels.sort((rc1, rc2) -> rc1.getShortName().compareTo(rc2.getShortName()));
      } else {
        repositoryChannels = new LinkedList<RepositoryChannel>();
      }

      institutions = new HashMap<>();

      for (Institution institution : institutionManager.findAll().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        institutions.put(institution.getId().toString(), institution.getComposedName());
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
        this.getActualPhase().getId()));
      this.getKeyOutputs().sort((k1, k2) -> k1.getCrpClusterOfActivity().getIdentifier()
        .compareTo(k2.getCrpClusterOfActivity().getIdentifier()));

      if (this.isHttpPost()) {

        if (deliverable.getPublication() != null) {
          deliverable.getPublication().setIsiPublication(null);
          deliverable.getPublication().setCoAuthor(null);
          deliverable.getPublication().setNasr(null);
        }

        deliverable.getDeliverableInfo(this.getActualPhase()).setDeliverableType(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingGender(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingCapacity(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingNa(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingYouth(null);
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
        if (deliverable.getRegions() != null) {
          deliverable.getRegions().clear();
        }
        if (deliverable.getUsers() != null) {
          deliverable.getUsers().clear();
        }

        deliverable.getDeliverableInfo(this.getActualPhase()).setCrpClusterKeyOutput(null);

        if (deliverable.getFundingSources() != null) {
          deliverable.getFundingSources().clear();
        }
        if (deliverable.getGenderLevels() != null) {
          deliverable.getGenderLevels().clear();
        }
        if (deliverable.getDisseminations() != null) {
          deliverable.getDisseminations().clear();
        }

      }
    }
  }

  @Override
  public String save() {
    Deliverable deliverablePrew = deliverableManager.getDeliverableById(deliverableID);
    DeliverableInfo deliverableInfoPrew = deliverablePrew.getDeliverableInfo(this.getActualPhase());
    deliverableInfoPrew.setTitle(deliverable.getDeliverableInfo().getTitle());


    if (deliverable.getDeliverableInfo().getAdoptedLicense() != null) {
      deliverableInfoPrew.setAdoptedLicense(deliverable.getDeliverableInfo().getAdoptedLicense());
      if (deliverable.getDeliverableInfo().getAdoptedLicense().booleanValue()) {
        deliverableInfoPrew.setLicense(deliverable.getDeliverableInfo().getLicense());
        if (deliverable.getDeliverableInfo().getLicense() != null) {
          if (deliverable.getDeliverableInfo().getLicense().equals(LicensesTypeEnum.OTHER.getValue())) {
            deliverableInfoPrew.setOtherLicense(deliverable.getDeliverableInfo().getOtherLicense());
            deliverableInfoPrew.setAllowModifications(deliverable.getDeliverableInfo().getAllowModifications());
          } else {
            deliverableInfoPrew.setOtherLicense(null);
            deliverableInfoPrew.setAllowModifications(null);
          }
        }
        deliverableInfoPrew.setAdoptedLicense(deliverable.getDeliverableInfo().getAdoptedLicense());
      } else {

        deliverableInfoPrew.setLicense(null);
        deliverableInfoPrew.setOtherLicense(null);
        deliverableInfoPrew.setAllowModifications(null);
      }
    } else {
      deliverableInfoPrew.setLicense(null);
      deliverableInfoPrew.setOtherLicense(null);
      deliverableInfoPrew.setAllowModifications(null);
    }
    deliverableInfoPrew.setDeliverableType(deliverable.getDeliverableInfo().getDeliverableType());

    if (deliverablePrew.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId().intValue() == -1) {
      deliverableInfoPrew.setDeliverableType(null);
    }
    if (deliverable.getDeliverableInfo().getCrossCuttingCapacity() == null) {
      deliverableInfoPrew.setCrossCuttingCapacity(false);
    } else {
      deliverableInfoPrew.setCrossCuttingCapacity(true);
    }
    if (deliverable.getDeliverableInfo().getCrossCuttingNa() == null) {
      deliverableInfoPrew.setCrossCuttingNa(false);
    } else {
      deliverableInfoPrew.setCrossCuttingNa(true);
    }
    if (deliverable.getDeliverableInfo().getCrossCuttingGender() == null) {
      deliverableInfoPrew.setCrossCuttingGender(false);
    } else {
      deliverableInfoPrew.setCrossCuttingGender(true);
    }
    if (deliverable.getDeliverableInfo().getCrossCuttingYouth() == null) {
      deliverableInfoPrew.setCrossCuttingYouth(false);
    } else {
      deliverableInfoPrew.setCrossCuttingYouth(true);
    }
    deliverableManager.saveDeliverable(deliverablePrew);
    List<String> relationsName = new ArrayList<>();


    if (deliverable.getGenderLevels() != null) {
      if (deliverablePrew.getDeliverableGenderLevels() != null
        && deliverablePrew.getDeliverableGenderLevels().size() > 0) {
        List<DeliverableGenderLevel> fundingSourcesPrew = deliverablePrew.getDeliverableGenderLevels().stream()
          .filter(dp -> dp.isActive()).collect(Collectors.toList());


        for (DeliverableGenderLevel deliverableFundingSource : fundingSourcesPrew) {
          if (!deliverable.getGenderLevels().contains(deliverableFundingSource)) {
            deliverableGenderLevelManager.deleteDeliverableGenderLevel(deliverableFundingSource.getId());
          }
        }
      }

      for (DeliverableGenderLevel deliverableFundingSource : deliverable.getGenderLevels()) {
        if (deliverableFundingSource.getId() == null || deliverableFundingSource.getId() == -1) {


          deliverableFundingSource.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
          deliverableFundingSource.setActive(true);
          deliverableFundingSource.setCreatedBy(this.getCurrentUser());
          deliverableFundingSource.setModifiedBy(this.getCurrentUser());
          deliverableFundingSource.setModificationJustification("");
          deliverableFundingSource.setActiveSince(new Date());

          deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableFundingSource);


        } else {
          DeliverableGenderLevel deliverableGenderLevelDB =
            deliverableGenderLevelManager.getDeliverableGenderLevelById(deliverableFundingSource.getId());
          deliverableGenderLevelDB.setModifiedBy(this.getCurrentUser());
          deliverableGenderLevelDB.setGenderLevel(deliverableFundingSource.getGenderLevel());
          deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableGenderLevelDB);


        }
      }
    }

    if (!deliverablePrew.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender().booleanValue()) {
      Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
      for (DeliverableGenderLevel genderLevel : deliverableDB.getDeliverableGenderLevels().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        deliverableGenderLevelManager.deleteDeliverableGenderLevel(genderLevel.getId());
      }
    }

    this.saveDissemination();
    this.saveMetadata();

    this.savePublicationMetadata();
    this.saveUsers();
    this.saveCrps();
    this.saveLeaders();
    this.savePrograms();
    relationsName.add(APConstants.PROJECT_DELIVERABLE_METADATA_ELEMENT);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_PUBLICATION_METADATA);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_DISEMINATIONS);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_USERS);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_PROGRAMS);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_LEADERS);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_GENDER_LEVELS);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_CRPS);

    deliverable = deliverableManager.getDeliverableById(deliverableID);
    deliverable.setActiveSince(new Date());
    // deliverable.setModifiedBy(this.getCurrentUser());
    // deliverable.setModificationJustification(this.getJustification());

    deliverableManager.saveDeliverable(deliverable, this.getActionName(), relationsName, this.getActualPhase());
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
  }

  public void saveCrps() {
    if (deliverable.getCrps() == null) {
      deliverable.setCrps(new ArrayList<>());
    }
    /* Delete */
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableCrp deliverableCrp : deliverableDB.getDeliverableCrps().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
      if (!deliverable.getCrps().contains(deliverableCrp)) {
        deliverableCrpManager.deleteDeliverableCrp(deliverableCrp.getId());
      }
    }

    /* Save */
    for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {
      if (deliverableCrp.getId() == null || deliverableCrp.getId().intValue() == -1) {
        deliverableCrp.setId(null);
        deliverableCrp.setDeliverable(deliverable);
        deliverableCrp.setPhase(this.getActualPhase());
        deliverableCrpManager.saveDeliverableCrp(deliverableCrp);
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

                dissemination.setIntellectualProperty(true);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);


                break;
              case "limitedExclusivity":

                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(true);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);

                break;
              case "restrictedUseAgreement":

                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(true);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(deliverable.getDissemination().getRestrictedAccessUntil());
                dissemination.setRestrictedEmbargoed(null);

                break;
              case "effectiveDateRestriction":

                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(true);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(deliverable.getDissemination().getRestrictedEmbargoed());

                break;

              default:
                break;
            }
          }
        } else {


          dissemination.setIntellectualProperty(false);
          dissemination.setLimitedExclusivity(false);
          dissemination.setRestrictedUseAgreement(false);
          dissemination.setEffectiveDateRestriction(false);

          dissemination.setRestrictedAccessUntil(null);
          dissemination.setRestrictedEmbargoed(null);
        }
      } else {

        dissemination.setIsOpenAccess(null);

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


      deliverableDisseminationManager.saveDeliverableDissemination(dissemination);

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
        deliverableLeaderManager.saveDeliverableLeader(deliverableUser);
      }
    }
  }

  public void saveMetadata() {
    if (deliverable.getMetadataElements() != null) {

      for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getMetadataElements()) {

        if (deliverableMetadataElement != null && deliverableMetadataElement.getMetadataElement() != null) {

          deliverableMetadataElement.setDeliverable(deliverable);
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElement);

        }
      }
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
          if (deliverableDB.getDeliverablePrograms().stream()
            .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == program.getId().longValue())
            .collect(Collectors.toList()).isEmpty()) {

            deliverableProgramManager.saveDeliverableProgram(deliverableProgram);
          }
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
      deliverablePublicationMetadataManager.saveDeliverablePublicationMetadata(deliverable.getPublication());

    }
  }


  public void saveUsers() {
    if (deliverable.getUsers() == null) {

      deliverable.setUsers(new ArrayList<>());
    }
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableUser deliverableUser : deliverableDB.getDeliverableUsers()) {
      if (!deliverable.getUsers().contains(deliverableUser)) {
        deliverableUserManager.deleteDeliverableUser(deliverableUser.getId());
      }
    }

    for (DeliverableUser deliverableUser : deliverable.getUsers()) {

      if (deliverableUser.getId() == null || deliverableUser.getId().intValue() == -1) {
        deliverableUser.setId(null);
        deliverableUser.setDeliverable(deliverable);
        deliverableUserManager.saveDeliverableUser(deliverableUser);
      }
    }
  }


  public void setChannels(Map<String, String> channels) {
    this.channels = channels;
  }


  public void setCrossCuttingDimensions(List<CrossCuttingScoring> crossCuttingDimensions) {
    this.crossCuttingDimensions = crossCuttingDimensions;
  }


  public void setCrossCuttingScoresMap(Map<Long, String> crossCuttingScoresMap) {
    this.crossCuttingScoresMap = crossCuttingScoresMap;
  }

  public void setCrps(Map<String, String> crps) {
    this.crps = crps;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }


  public void setDeliverableSubTypes(List<DeliverableType> deliverableSubTypes) {
    this.deliverableSubTypes = deliverableSubTypes;
  }

  public void setDeliverableTypeManager(DeliverableTypeManager deliverableTypeManager) {
    this.deliverableTypeManager = deliverableTypeManager;
  }

  public void setFundingSources(List<FundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }

  public void setGenderLevels(List<GenderType> genderLevels) {
    this.genderLevels = genderLevels;
  }

  public void setInstitutions(Map<String, String> institutions) {
    this.institutions = institutions;
  }


  public void setKeyOutputs(List<CrpClusterKeyOutput> keyOutputs) {
    this.keyOutputs = keyOutputs;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPrograms(Map<String, String> programs) {
    this.programs = programs;
  }

  public void setRegions(Map<String, String> regions) {
    this.regions = regions;
  }

  public void setRepositoryChannels(List<RepositoryChannel> repositoryChannels) {
    this.repositoryChannels = repositoryChannels;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      publicationValidator.validate(this, deliverable, true);
    }
  }
}