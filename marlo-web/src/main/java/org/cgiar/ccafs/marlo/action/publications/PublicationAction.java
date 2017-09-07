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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPandrManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
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
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ChannelEnum;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpPandr;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.GenderType;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicationAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -5176367401132626314L;

  private final Logger LOG = LoggerFactory.getLogger(PublicationAction.class);

  private Crp loggedCrp;
  private CrpManager crpManager;
  private long deliverableID;
  private Map<String, String> channels;
  private DeliverableCrpManager deliverableCrpManager;
  private Map<String, String> crps;
  private List<GenderType> genderLevels;

  private CrpPandrManager crpPandrManager;
  private IpProgramManager ipProgramManager;
  private Map<String, String> programs;
  private Map<String, String> regions;
  private Map<String, String> institutions;
  private CrpPpaPartnerManager crpPpaPartnerManager;
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

  @Inject
  public PublicationAction(APConfig config, CrpManager crpManager, DeliverableManager deliverableManager,
    GenderTypeManager genderTypeManager, DeliverableQualityCheckManager deliverableQualityCheckManager,
    AuditLogManager auditLogManager, DeliverableTypeManager deliverableTypeManager,
    MetadataElementManager metadataElementManager, UserManager userManager,
    DeliverableDisseminationManager deliverableDisseminationManager, InstitutionManager institutionManager,
    DeliverablePublicationMetadataManager deliverablePublicationMetadataManager,
    DeliverableGenderLevelManager deliverableGenderLevelManager, DeliverableUserManager deliverableUserManager,
    CrpPandrManager crpPandrManager, DeliverableCrpManager deliverableCrpManager,
    CrpPpaPartnerManager crpPpaPartnerManager, DeliverableProgramManager deliverableProgramManager,
    DeliverableLeaderManager deliverableLeaderManager, PublicationValidator publicationValidator,
    HistoryComparator historyComparator, DeliverableMetadataElementManager deliverableMetadataElementManager,
    IpProgramManager ipProgramManager) {

    super(config);
    this.deliverableDisseminationManager = deliverableDisseminationManager;
    this.historyComparator = historyComparator;
    this.crpManager = crpManager;
    this.publicationValidator = publicationValidator;
    this.crpPandrManager = crpPandrManager;
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
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.userManager = userManager;
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
        ids[c] = projectFocuses.get(c).getIpProgram().getId().toString();
      }
      return ids;
    }
    return null;
  }


  public List<GenderType> getGenderLevels() {
    return genderLevels;
  }

  public Map<String, String> getInstitutions() {
    return institutions;
  }

  public Crp getLoggedCrp() {
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
        ids[c] = projectFocuses.get(c).getIpProgram().getId().toString();
      }
      return ids;
    }
    return null;
  }

  public String getTransaction() {
    return transaction;
  }


  @Override
  public void prepare() throws Exception {


    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
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
        deliverable.setModifiedBy(userManager.getUser(deliverable.getModifiedBy().getId()));
        Map<String, String> specialList = new HashMap<>();

        this.setDifferences(historyComparator.getDifferences(transaction, specialList, "deliverable"));
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }
    }

    else {
      deliverable = deliverableManager.getDeliverableById(deliverableID);
    }

    if (deliverable != null) {

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        deliverable = (Deliverable) autoSaveReader.readFromJson(jReader);
        this.setDraft(true);
        reader.close();
        if (deliverable.getCrps() != null) {
          for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {
            if (deliverableCrp != null) {

              if (deliverableCrp.getIpProgram() == null || deliverableCrp.getIpProgram().getId() == null
                || deliverableCrp.getIpProgram().getId().intValue() == -1) {
                deliverableCrp.setCrpPandr(crpPandrManager.getCrpPandrById(deliverableCrp.getCrpPandr().getId()));

              } else {
                deliverableCrp.setIpProgram(ipProgramManager.getIpProgramById(deliverableCrp.getIpProgram().getId()));
                deliverableCrp.setCrpPandr(crpPandrManager.getCrpPandrById(3));
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
              IpProgram program = ipProgramManager.getIpProgramById(Long.parseLong(programID.trim()));
              deliverableProgram.setDeliverable(deliverable);
              deliverableProgram.setIpProgram(program);
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
              IpProgram program = ipProgramManager.getIpProgramById(Long.parseLong(programID.trim()));
              deliverableProgram.setDeliverable(deliverable);
              deliverableProgram.setIpProgram(program);
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

      } else {


        /**
         * 
         */
        deliverable.setGenderLevels(
          deliverable.getDeliverableGenderLevels().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        DeliverableQualityCheck deliverableQualityCheck =
          deliverableQualityCheckManager.getDeliverableQualityCheckByDeliverable(deliverable.getId());
        deliverable.setQualityCheck(deliverableQualityCheck);

        if (deliverable.getDeliverableMetadataElements() != null) {
          deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements()));
        }

        if (deliverable.getDeliverableDisseminations() != null) {
          deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations()));
          if (deliverable.getDeliverableDisseminations().size() > 0) {
            deliverable.setDissemination(deliverable.getDisseminations().get(0));
          } else {
            deliverable.setDissemination(new DeliverableDissemination());
          }

        }

        if (deliverable.getDeliverableDataSharingFiles() != null) {
          deliverable.setDataSharingFiles(new ArrayList<>(deliverable.getDeliverableDataSharingFiles()));
        }

        if (deliverable.getDeliverablePublicationMetadatas() != null) {
          deliverable.setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas()));
        }
        if (!deliverable.getPublicationMetadatas().isEmpty()) {
          deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
        }

        if (deliverable.getDeliverableDataSharings() != null) {
          deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings()));

        }
        deliverable.setCrps(deliverable.getDeliverableCrps().stream().collect(Collectors.toList()));
        deliverable.setUsers(deliverable.getDeliverableUsers().stream().collect(Collectors.toList()));
        deliverable.setLeaders(deliverable.getDeliverableLeaders().stream().collect(Collectors.toList()));
        deliverable.setPrograms(deliverable.getDeliverablePrograms().stream()
          .filter(c -> c.getIpProgram().getIpProgramType().getId().intValue() == 4).collect(Collectors.toList()));
        deliverable.setRegions(deliverable.getDeliverablePrograms().stream()
          .filter(c -> c.getIpProgram().getIpProgramType().getId().intValue() == 5).collect(Collectors.toList()));
        deliverable.setFlagshipValue("");
        deliverable.setRegionsValue("");

        for (DeliverableProgram deliverableProgram : deliverable.getPrograms()) {
          if (deliverable.getFlagshipValue().isEmpty()) {
            deliverable.setFlagshipValue(deliverableProgram.getIpProgram().getId().toString());
          } else {
            deliverable.setFlagshipValue(
              deliverable.getFlagshipValue() + "," + deliverableProgram.getIpProgram().getId().toString());
          }
        }

        for (DeliverableProgram deliverableProgram : deliverable.getRegions()) {
          if (deliverable.getRegionsValue().isEmpty()) {
            deliverable.setRegionsValue(deliverableProgram.getIpProgram().getId().toString());
          } else {
            deliverable.setRegionsValue(
              deliverable.getRegionsValue() + "," + deliverableProgram.getIpProgram().getId().toString());
          }
        }
        deliverable.setGenderLevels(
          deliverable.getDeliverableGenderLevels().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


        LOG.debug("Deliverable.getGenderLevels size = " + deliverable.getGenderLevels().size());
        this.setDraft(false);
      }

      if (metadataElementManager.findAll() != null) {
        deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
      }
    }


    deliverableSubTypes = new ArrayList<>(deliverableTypeManager.findAll().stream()
      .filter(dt -> dt.getDeliverableType() != null && dt.getDeliverableType().getId().intValue() == 49)
      .collect(Collectors.toList()));
    deliverableSubTypes.add(deliverableTypeManager.getDeliverableTypeById(55));
    deliverableSubTypes.add(deliverableTypeManager.getDeliverableTypeById(56));


    if (this.isHttpPost()) {


      if (deliverable.getPublication() != null) {
        deliverable.getPublication().setIsiPublication(null);
        deliverable.getPublication().setCoAuthor(null);
        deliverable.getPublication().setNasr(null);

      }

      deliverable.setCrossCuttingGender(null);
      deliverable.setCrossCuttingCapacity(null);
      deliverable.setCrossCuttingNa(null);
      deliverable.setCrossCuttingYouth(null);

      if (deliverable.getCrps() != null) {
        deliverable.getCrps().clear();
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

      if (deliverable.getMetadataElements() != null) {
        deliverable.getMetadataElements().clear();
      }


      if (deliverable.getOtherPartners() != null) {
        deliverable.getOtherPartners().clear();
      }
      if (deliverable.getFundingSources() != null) {
        deliverable.getFundingSources().clear();
      }
      if (deliverable.getGenderLevels() != null) {
        deliverable.getGenderLevels().clear();
      }

      if (deliverable.getQualityCheck() != null) {
        deliverable.getQualityCheck().setFileAssurance(null);
        deliverable.getQualityCheck().setFileDictionary(null);
        deliverable.getQualityCheck().setFileTools(null);
      }

    }

    channels = new HashMap<>();


    for (ChannelEnum channel : ChannelEnum.values()) {
      channels.put(channel.getId(), channel.getDesc());
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
    crps = new HashMap<>();
    for (CrpPandr crp : crpPandrManager.findAll().stream().filter(c -> c.getId() != 3 && c.isActive())
      .collect(Collectors.toList())) {
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
    institutions = new HashMap<>();

    for (Institution institution : institutionManager.findAll().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      institutions.put(institution.getId().toString(), institution.getComposedName());

    }

  }


  @Override
  public String save() {
    Deliverable deliverablePrew = deliverableManager.getDeliverableById(deliverableID);

    deliverablePrew.setTitle(deliverable.getTitle());


    if (deliverable.getAdoptedLicense() != null) {
      deliverablePrew.setAdoptedLicense(deliverable.getAdoptedLicense());
      if (deliverable.getAdoptedLicense().booleanValue()) {
        deliverablePrew.setLicense(deliverable.getLicense());
        if (deliverable.getLicense() != null) {
          if (deliverable.getLicense().equals(LicensesTypeEnum.OTHER.getValue())) {
            deliverablePrew.setOtherLicense(deliverable.getOtherLicense());
            deliverablePrew.setAllowModifications(deliverable.getAllowModifications());
          } else {
            deliverablePrew.setOtherLicense(null);
            deliverablePrew.setAllowModifications(null);
          }
        }
        deliverablePrew.setAdoptedLicense(deliverable.getAdoptedLicense());
      } else {

        deliverablePrew.setLicense(null);
        deliverablePrew.setOtherLicense(null);
        deliverablePrew.setAllowModifications(null);
      }
    } else {
      deliverablePrew.setLicense(null);
      deliverablePrew.setOtherLicense(null);
      deliverablePrew.setAllowModifications(null);
    }
    deliverablePrew.setDeliverableType(deliverable.getDeliverableType());

    if (deliverablePrew.getDeliverableType().getId().intValue() == -1) {
      deliverablePrew.setDeliverableType(null);
    }
    if (deliverable.getCrossCuttingCapacity() == null) {
      deliverablePrew.setCrossCuttingCapacity(false);
    } else {
      deliverablePrew.setCrossCuttingCapacity(true);
    }
    if (deliverable.getCrossCuttingNa() == null) {
      deliverablePrew.setCrossCuttingNa(false);
    } else {
      deliverablePrew.setCrossCuttingNa(true);
    }
    if (deliverable.getCrossCuttingGender() == null) {
      deliverablePrew.setCrossCuttingGender(false);
    } else {
      deliverablePrew.setCrossCuttingGender(true);
    }
    if (deliverable.getCrossCuttingYouth() == null) {
      deliverablePrew.setCrossCuttingYouth(false);
    } else {
      deliverablePrew.setCrossCuttingYouth(true);
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

    if (!deliverablePrew.getCrossCuttingGender().booleanValue()) {
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
    deliverable.setModifiedBy(this.getCurrentUser());
    deliverable.setModificationJustification(this.getJustification());

    deliverableManager.saveDeliverable(deliverable, this.getActionName(), relationsName);
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
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableCrp deliverableCrp : deliverableDB.getDeliverableCrps()) {
      if (!deliverable.getCrps().contains(deliverableCrp)) {
        deliverableCrpManager.deleteDeliverableCrp(deliverableCrp.getId());
      }
    }

    for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {

      if (deliverableCrp.getId() == null || deliverableCrp.getId().intValue() == -1) {
        deliverableCrp.setId(null);
        deliverableCrp.setDeliverable(deliverable);

        if (deliverableCrp.getCrpPandr() != null) {
          if (deliverableCrp.getCrpPandr().getId() == null) {
            deliverableCrp.setCrpPandr(null);
          } else {
            if (deliverableCrp.getCrpPandr().getId().intValue() == -1) {
              deliverableCrp.setCrpPandr(null);
            }
          }
        }

        if (deliverableCrp.getCrpPandr() == null) {
          deliverableCrp.setCrpPandr(crpPandrManager.getCrpPandrById(new Long(3)));
        } else {
          deliverableCrp.setIpProgram(null);
        }

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
        .filter(c -> c.isActive() && c.getIpProgram().getIpProgramType().getId().intValue() == 4)
        .collect(Collectors.toList())) {

        if (!deliverable.getFlagshipValue().contains(deliverableProgram.getIpProgram().getId().toString())) {
          deliverableProgramManager.deleteDeliverableProgram(deliverableProgram.getId());

        }
      }
      for (String programID : deliverable.getFlagshipValue().trim().split(",")) {
        if (programID.length() > 0) {
          IpProgram program = ipProgramManager.getIpProgramById(Long.parseLong(programID.trim()));
          DeliverableProgram deliverableProgram = new DeliverableProgram();
          deliverableProgram.setIpProgram(program);
          deliverableProgram.setDeliverable(deliverable);
          if (deliverableDB.getDeliverablePrograms().stream()
            .filter(c -> c.isActive() && c.getIpProgram().getId().longValue() == program.getId().longValue())
            .collect(Collectors.toList()).isEmpty()) {

            deliverableProgramManager.saveDeliverableProgram(deliverableProgram);
          }
        }

      }
    }

    if (deliverable.getRegionsValue() != null) {

      for (DeliverableProgram deliverableProgram : deliverableDB.getDeliverablePrograms().stream()
        .filter(c -> c.isActive() && c.getIpProgram().getIpProgramType().getId().intValue() == 5)
        .collect(Collectors.toList())) {

        if (!deliverable.getRegionsValue().contains(deliverableProgram.getIpProgram().getId().toString())) {
          deliverableProgramManager.deleteDeliverableProgram(deliverableProgram.getId());

        }
      }
      for (String programID : deliverable.getRegionsValue().trim().split(",")) {
        if (programID.length() > 0) {
          IpProgram program = ipProgramManager.getIpProgramById(Long.parseLong(programID.trim()));
          DeliverableProgram deliverableProgram = new DeliverableProgram();
          deliverableProgram.setIpProgram(program);
          deliverableProgram.setDeliverable(deliverable);
          if (deliverableDB.getDeliverablePrograms().stream()
            .filter(c -> c.isActive() && c.getIpProgram().getId().longValue() == program.getId().longValue())
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


  public void setGenderLevels(List<GenderType> genderLevels) {
    this.genderLevels = genderLevels;
  }


  public void setInstitutions(Map<String, String> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPrograms(Map<String, String> programs) {
    this.programs = programs;
  }

  public void setRegions(Map<String, String> regions) {
    this.regions = regions;
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