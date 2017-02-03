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
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class PublicationAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -5176367401132626314L;
  private Crp loggedCrp;
  private CrpManager crpManager;
  private long deliverableID;

  private DeliverableManager deliverableManager;

  private Deliverable publication;
  private DeliverableDisseminationManager deliverableDisseminationManager;
  private DeliverableMetadataElementManager deliverableMetadataElementManager;
  private DeliverablePublicationMetadataManager deliverablePublicationMetadataManager;
  private DeliverableUserManager deliverableUserManager;

  private String transaction;


  private AuditLogManager auditLogManager;

  private DeliverableQualityCheckManager deliverableQualityCheckManager;
  private MetadataElementManager metadataElementManager;

  private List<DeliverableType> deliverableSubTypes;
  private DeliverableTypeManager deliverableTypeManager;

  @Inject
  public PublicationAction(APConfig config, CrpManager crpManager, DeliverableManager deliverableManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager, AuditLogManager auditLogManager,
    DeliverableTypeManager deliverableTypeManager, MetadataElementManager metadataElementManager,
    DeliverableDisseminationManager deliverableDisseminationManager,
    DeliverablePublicationMetadataManager deliverablePublicationMetadataManager,
    DeliverableUserManager deliverableUserManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager) {

    super(config);
    this.deliverableDisseminationManager = deliverableDisseminationManager;
    this.crpManager = crpManager;
    this.deliverableManager = deliverableManager;
    this.auditLogManager = auditLogManager;
    this.deliverableQualityCheckManager = deliverableQualityCheckManager;
    this.deliverablePublicationMetadataManager = deliverablePublicationMetadataManager;
    this.deliverableUserManager = deliverableUserManager;
    this.deliverableMetadataElementManager = deliverableMetadataElementManager;
    this.metadataElementManager = metadataElementManager;
    this.deliverableTypeManager = deliverableTypeManager;
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
    String composedClassName = publication.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = publication.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public Deliverable getPublication() {
    return publication;
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

    }


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Deliverable history = (Deliverable) auditLogManager.getHistory(transaction);

      if (history != null) {
        publication = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }
    }

    else {
      publication = deliverableManager.getDeliverableById(deliverableID);
    }

    if (publication != null) {

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        publication = (Deliverable) autoSaveReader.readFromJson(jReader);
        this.setDraft(true);
      } else {


        /**
         * 
         */
        publication.setGenderLevels(
          publication.getDeliverableGenderLevels().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        DeliverableQualityCheck deliverableQualityCheck =
          deliverableQualityCheckManager.getDeliverableQualityCheckByDeliverable(publication.getId());
        publication.setQualityCheck(deliverableQualityCheck);

        if (publication.getDeliverableMetadataElements() != null) {
          publication.setMetadataElements(new ArrayList<>(publication.getDeliverableMetadataElements()));
        }

        if (publication.getDeliverableDisseminations() != null) {
          publication.setDisseminations(new ArrayList<>(publication.getDeliverableDisseminations()));
          if (publication.getDeliverableDisseminations().size() > 0) {
            publication.setDissemination(publication.getDisseminations().get(0));
          } else {
            publication.setDissemination(new DeliverableDissemination());
          }

        }

        if (publication.getDeliverableDataSharingFiles() != null) {
          publication.setDataSharingFiles(new ArrayList<>(publication.getDeliverableDataSharingFiles()));
        }

        if (publication.getDeliverablePublicationMetadatas() != null) {
          publication.setPublicationMetadatas(new ArrayList<>(publication.getDeliverablePublicationMetadatas()));
        }
        if (!publication.getPublicationMetadatas().isEmpty()) {
          publication.setPublication(publication.getPublicationMetadatas().get(0));
        }

        if (publication.getDeliverableDataSharings() != null) {
          publication.setDataSharing(new ArrayList<>(publication.getDeliverableDataSharings()));

        }
        publication.setUsers(publication.getDeliverableUsers().stream().collect(Collectors.toList()));

        this.setDraft(false);
      }


    }
    if (metadataElementManager.findAll() != null) {
      publication.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
    }


    deliverableSubTypes = new ArrayList<>(deliverableTypeManager.findAll().stream()
      .filter(dt -> dt.getDeliverableType() != null && dt.getDeliverableType().getId().intValue() == 49)
      .collect(Collectors.toList()));

    if (this.isHttpPost()) {
      publication.setCrossCuttingGender(null);
      publication.setCrossCuttingCapacity(null);
      publication.setCrossCuttingNa(null);
      publication.setCrossCuttingYouth(null);

      if (publication.getUsers() != null) {
        publication.getUsers().clear();
      }

      if (publication.getMetadataElements() != null) {
        publication.getMetadataElements().clear();
      }
    }
  }


  @Override
  public String save() {
    Deliverable deliverablePrew = deliverableManager.getDeliverableById(deliverableID);

    deliverablePrew.setTitle(publication.getTitle());


    if (publication.getCrossCuttingCapacity() == null) {
      deliverablePrew.setCrossCuttingCapacity(false);
    } else {
      deliverablePrew.setCrossCuttingCapacity(true);
    }
    if (publication.getCrossCuttingNa() == null) {
      deliverablePrew.setCrossCuttingNa(false);
    } else {
      deliverablePrew.setCrossCuttingNa(true);
    }
    if (publication.getCrossCuttingGender() == null) {
      deliverablePrew.setCrossCuttingGender(false);
    } else {
      deliverablePrew.setCrossCuttingGender(true);
    }
    if (publication.getCrossCuttingYouth() == null) {
      deliverablePrew.setCrossCuttingYouth(false);
    } else {
      deliverablePrew.setCrossCuttingYouth(true);
    }
    deliverableManager.saveDeliverable(deliverablePrew);
    List<String> relationsName = new ArrayList<>();


    this.saveDissemination();
    this.saveMetadata();

    this.savePublicationMetadata();
    this.saveUsers();
    relationsName.add(APConstants.PROJECT_DELIVERABLE_METADATA_ELEMENT);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_PUBLICATION_METADATA);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_DISEMINATIONS);
    relationsName.add(APConstants.PROJECT_DELIVERABLE_USERS);


    publication = deliverableManager.getDeliverableById(deliverableID);
    publication.setActiveSince(new Date());
    publication.setModifiedBy(this.getCurrentUser());
    publication.setModificationJustification(this.getJustification());

    deliverableManager.saveDeliverable(publication, this.getActionName(), relationsName);
    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
    }

    this.setInvalidFields(new HashMap<>());
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


  public void saveDissemination() {
    if (publication.getDissemination() != null) {

      DeliverableDissemination dissemination = new DeliverableDissemination();
      if (publication.getDissemination().getId() != null && publication.getDissemination().getId() != -1) {
        dissemination =
          deliverableDisseminationManager.getDeliverableDisseminationById(publication.getDissemination().getId());
      } else {
        dissemination = new DeliverableDissemination();
        dissemination.setDeliverable(deliverableManager.getDeliverableById(deliverableID));

      }


      if (publication.getDissemination().getIsOpenAccess() != null) {
        dissemination.setIsOpenAccess(publication.getDissemination().getIsOpenAccess());
        if (!publication.getDissemination().getIsOpenAccess().booleanValue()) {
          String type = publication.getDissemination().getType();
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

                dissemination.setRestrictedAccessUntil(publication.getDissemination().getRestrictedAccessUntil());
                dissemination.setRestrictedEmbargoed(null);

                break;
              case "effectiveDateRestriction":

                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(true);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(publication.getDissemination().getRestrictedEmbargoed());

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

      if (publication.getDissemination().getAlreadyDisseminated() != null) {
        dissemination.setAlreadyDisseminated(publication.getDissemination().getAlreadyDisseminated());
        if (publication.getDissemination().getAlreadyDisseminated().booleanValue()) {

          dissemination.setDisseminationUrl(publication.getDissemination().getDisseminationUrl());
          dissemination.setDisseminationChannel(publication.getDissemination().getDisseminationChannel());
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

  public void saveMetadata() {
    if (publication.getMetadataElements() != null) {

      for (DeliverableMetadataElement deliverableMetadataElement : publication.getMetadataElements()) {

        if (deliverableMetadataElement != null && deliverableMetadataElement.getMetadataElement() != null) {

          deliverableMetadataElement.setDeliverable(publication);
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElement);

        }
      }
    }

  }


  public void savePublicationMetadata() {
    if (publication.getPublication() != null) {
      publication.getPublication().setDeliverable(publication);
      if (publication.getPublication().getId() != null && publication.getPublication().getId().intValue() == -1) {
        publication.getPublication().setId(null);
      }
      deliverablePublicationMetadataManager.saveDeliverablePublicationMetadata(publication.getPublication());

    }
  }

  public void saveUsers() {
    if (publication.getUsers() == null) {

      publication.setUsers(new ArrayList<>());
    }
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableUser deliverableUser : deliverableDB.getDeliverableUsers()) {
      if (!publication.getUsers().contains(deliverableUser)) {
        deliverableUserManager.deleteDeliverableUser(deliverableUser.getId());
      }
    }

    for (DeliverableUser deliverableUser : publication.getUsers()) {

      if (deliverableUser.getId() == null || deliverableUser.getId().intValue() == -1) {
        deliverableUser.setId(null);
        deliverableUser.setDeliverable(publication);
        deliverableUserManager.saveDeliverableUser(deliverableUser);
      }
    }
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

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPublication(Deliverable deliverable) {
    this.publication = deliverable;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }
}
