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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLocationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipPersonManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DeliverablesReplicationAction:
 * 
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class DeliverablesReplicationAction extends BaseAction {

  private static final long serialVersionUID = 6392973543544674655L;

  private static Logger logger = LoggerFactory.getLogger(DeliverablesReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private DeliverableManager deliverableManager;
  private PhaseManager phaseManager;
  private DeliverableFundingSourceManager deliverableFundingSourceManager;
  private DeliverableUserPartnershipManager deliverableUserPartnershipManager;
  private DeliverableQualityCheckManager deliverableQualityCheckManager;
  private DeliverableDisseminationManager deliverableDisseminationManager;
  private DeliverableMetadataElementManager deliverableMetadataElementManager;
  private DeliverableCrpManager deliverableCrpManager;
  private DeliverablePublicationMetadataManager deliverablePublicationMetadataManager;
  private DeliverableUserManager deliverableUserManager;
  private DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;
  private DeliverableParticipantManager deliverableParticipantManager;
  private DeliverableInfoManager deliverableInfoManager;
  private DeliverableLocationManager deliverableLocationManager;
  private DeliverableGeographicRegionManager deliverableGeographicRegionManager;
  private DeliverableUserPartnershipPersonManager deliverableUserPartnershipPersonManager;
  private ProjectPartnerManager projectPartnerManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase phase;
  private Deliverable deliverable;


  @Inject
  public DeliverablesReplicationAction(APConfig config, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    DeliverableFundingSourceManager deliverableFundingSourceManager, DeliverableManager deliverableManager,
    DeliverableUserPartnershipManager deliverableUserPartnershipManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager,
    DeliverableDisseminationManager deliverableDisseminationManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager, DeliverableCrpManager deliverableCrpManager,
    DeliverablePublicationMetadataManager deliverablePublicationMetadataManager,
    DeliverableUserManager deliverableUserManager,
    DeliverableIntellectualAssetManager deliverableIntellectualAssetManager,
    DeliverableParticipantManager deliverableParticipantManager, DeliverableInfoManager deliverableInfoManager,
    DeliverableLocationManager deliverableLocationManager,
    DeliverableGeographicRegionManager deliverableGeographicRegionManager,
    DeliverableUserPartnershipPersonManager deliverableUserPartnershipPersonManager,
    ProjectPartnerManager projectPartnerManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.globalUnitManager = globalUnitManager;
    this.deliverableManager = deliverableManager;
    this.deliverableUserPartnershipManager = deliverableUserPartnershipManager;
    this.deliverableQualityCheckManager = deliverableQualityCheckManager;
    this.deliverableDisseminationManager = deliverableDisseminationManager;
    this.deliverableMetadataElementManager = deliverableMetadataElementManager;
    this.deliverableCrpManager = deliverableCrpManager;
    this.deliverablePublicationMetadataManager = deliverablePublicationMetadataManager;
    this.deliverableUserManager = deliverableUserManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
    this.deliverableParticipantManager = deliverableParticipantManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableLocationManager = deliverableLocationManager;
    this.deliverableGeographicRegionManager = deliverableGeographicRegionManager;
    this.deliverableUserPartnershipPersonManager = deliverableUserPartnershipPersonManager;
    this.projectPartnerManager = projectPartnerManager;
  }


  private Path getAutoSaveFilePath() {

    // get the class simple name
    String composedClassName = deliverable.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + phase.getName() + "_" + phase.getYear()
      + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);

  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }

  private DeliverableUserPartnership getDeliverablePartnershipResponsibleDB(Deliverable deliverableDB) {
    DeliverableUserPartnership partnershipResponsible = null;
    List<DeliverableUserPartnership> deliverablePartnerships = deliverableDB.getDeliverableUserPartnerships().stream()
      .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(phase)
        && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
      .collect(Collectors.toList());
    if (deliverablePartnerships != null && deliverablePartnerships.size() > 0) {
      try {
        partnershipResponsible = deliverablePartnerships.get(0);
      } catch (Exception e) {
        // NEVER EVER JUST SWALLOW UNCHECKED EXCEPTIONS! Logging this now.
        logger.error("unable to filter DeliverablePartnership list", e);
        // This is strange, shouldn't we re-throw the exception, in fact we shouldn't even catch it in the first place!
        partnershipResponsible = null;
      }

    }
    return partnershipResponsible;
  }


  public String getEntityByPhaseList() {
    return entityByPhaseList;
  }


  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  /**
   * HJ 08/01/2019
   * 
   * @param institutionId
   * @return
   */
  public List<User> getUserList(Long institutionId, Long projectID) {

    List<User> users = new ArrayList<>();

    List<ProjectPartner> partnersTmp = projectPartnerManager.findAll().stream()
      .filter(pp -> pp.isActive() && pp.getProject().getId().equals(projectID)
        && pp.getPhase().getId().equals(this.getSelectedPhaseID()) && pp.getInstitution().getId().equals(institutionId))
      .collect(Collectors.toList());

    if (partnersTmp != null && !partnersTmp.isEmpty()) {
      ProjectPartner projectPartner = partnersTmp.get(0);
      List<ProjectPartnerPerson> partnerPersons = new ArrayList<>(
        projectPartner.getProjectPartnerPersons().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()));
      for (ProjectPartnerPerson projectPartnerPerson : partnerPersons) {

        users.add(projectPartnerPerson.getUser());
      }
    }

    return users;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    crps = globalUnitManager.findAll().stream().filter(c -> c.isMarlo() && c.isActive()).collect(Collectors.toList());
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (entityByPhaseList != null && !entityByPhaseList.isEmpty()) {
        logger.debug("Start replication for phase: " + selectedPhaseID);
        phase = phaseManager.getPhaseById(selectedPhaseID);

        for (String id : entityByPhaseList.trim().split(",")) {
          logger.debug("Replicating deliverable: " + id);

          deliverable = deliverableManager.getDeliverableById(new Long(id.trim()));
          if (deliverable.getDeliverableInfo(phase) != null) {

            // Load relations for auditlog
            List<String> relationsName = new ArrayList<>();
            relationsName.add(APConstants.PROJECT_DELIVERABLE_PARTNERSHIPS_RELATION);
            relationsName.add(APConstants.PROJECT_DELIVERABLE_INFO);
            relationsName.add(APConstants.PROJECT_DELIVERABLE_FUNDING_RELATION);
            relationsName.add(APConstants.PROJECT_DELIVERABLE_GENDER_LEVELS);
            relationsName.add(APConstants.PROJECT_DELIVERABLE_LOCATIONS);
            if (phase.isReporting() || phase.getUpkeep()) {
              relationsName.add(APConstants.PROJECT_DELIVERABLE_QUALITY_CHECK);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_METADATA_ELEMENT);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_DATA_SHARING_FILES);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_PUBLICATION_METADATA);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_DISEMINATIONS);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_CRPS);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_USERS);
              if (this.hasSpecificities(this.crpDeliverableIntellectualAsset())) {
                relationsName.add(APConstants.PROJECT_DELIVERABLES_INTELLECTUAL_RELATION);
              }
              relationsName.add(APConstants.PROJECT_DELIVERABLES_PARTICIPANT_RELATION);
            }

            // Save Deliverable Funding sources
            List<DeliverableFundingSource> deliverableFundingSources = deliverable.getDeliverableFundingSources()
              .stream().filter(df -> df.isActive() && df.getPhase() != null && df.getPhase().equals(phase))
              .collect(Collectors.toList());

            if (deliverableFundingSources != null && !deliverableFundingSources.isEmpty()) {
              for (DeliverableFundingSource deliverableFundingSource : deliverableFundingSources) {
                deliverableFundingSourceManager.saveDeliverableFundingSource(deliverableFundingSource);
              }
            }

            // Save DelivetablePartnerships
            // Responsible
            DeliverableUserPartnership partnershipResponsibleDB =
              this.getDeliverablePartnershipResponsibleDB(deliverable);
            if (partnershipResponsibleDB != null) {

              if (partnershipResponsibleDB.getDeliverableUserPartnershipPersons() != null) {


                List<DeliverableUserPartnershipPerson> trueList = new ArrayList<>();

                List<DeliverableUserPartnershipPerson> list =
                  new ArrayList<>(partnershipResponsibleDB.getDeliverableUserPartnershipPersons().stream()
                    .filter(p -> p.isActive()).collect(Collectors.toList()));

                for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : list) {

                  if (trueList.isEmpty()) {
                    trueList.add(deliverableUserPartnershipPerson);
                  } else {
                    boolean addUser = true;
                    for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson2 : trueList) {


                      if (deliverableUserPartnershipPerson2.getUser().getId()
                        .equals(deliverableUserPartnershipPerson.getUser().getId())) {
                        addUser = false;
                        deliverableUserPartnershipPersonManager
                          .deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPerson.getId());
                        break;
                      }
                    }
                    if (addUser) {
                      trueList.add(deliverableUserPartnershipPerson);
                    }
                  }
                }

                List<DeliverableUserPartnershipPerson> personList = new ArrayList<>();
                if (partnershipResponsibleDB.getInstitution() != null
                  && partnershipResponsibleDB.getDeliverable().getProject() != null) {
                  List<User> dUsers = this.getUserList(partnershipResponsibleDB.getInstitution().getId(),
                    partnershipResponsibleDB.getDeliverable().getProject().getId());

                  for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson2 : trueList) {
                    boolean addUser = false;
                    for (User user : dUsers) {

                      if (user.getId().equals(deliverableUserPartnershipPerson2.getUser().getId())) {
                        addUser = true;
                        break;
                      }

                    }

                    if (addUser) {
                      personList.add(deliverableUserPartnershipPerson2);
                    } else {
                      deliverableUserPartnershipPersonManager
                        .deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPerson2.getId());
                    }

                  }
                }

                partnershipResponsibleDB.setPartnershipPersons(trueList);
              }

              deliverableUserPartnershipManager.saveDeliverableUserPartnership(partnershipResponsibleDB);
            }
            // Others
            List<DeliverableUserPartnership> deliverablePartnershipOthers =
              deliverable.getDeliverableUserPartnerships().stream()
                .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                  && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_OTHER))
                .collect(Collectors.toList());
            if (deliverablePartnershipOthers != null && deliverablePartnershipOthers.size() > 0) {
              for (DeliverableUserPartnership deliverablePartnershipOther : deliverablePartnershipOthers) {
                if (deliverablePartnershipOther.getDeliverableUserPartnershipPersons() != null) {

                  if (deliverablePartnershipOther.getDeliverableUserPartnershipPersons() != null) {

                    List<DeliverableUserPartnershipPerson> trueList = new ArrayList<>();

                    List<DeliverableUserPartnershipPerson> list =
                      new ArrayList<>(deliverablePartnershipOther.getDeliverableUserPartnershipPersons().stream()
                        .filter(p -> p.isActive()).collect(Collectors.toList()));

                    for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : list) {

                      if (trueList.isEmpty()) {
                        trueList.add(deliverableUserPartnershipPerson);
                      } else {
                        boolean addUser = true;
                        for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson2 : trueList) {

                          if (deliverableUserPartnershipPerson2.getUser().getId()
                            .equals(deliverableUserPartnershipPerson.getUser().getId())) {
                            addUser = false;
                            deliverableUserPartnershipPersonManager
                              .deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPerson.getId());
                            break;
                          }
                        }
                        if (addUser) {
                          trueList.add(deliverableUserPartnershipPerson);
                        }
                      }
                    }

                    List<DeliverableUserPartnershipPerson> personList = new ArrayList<>();
                    if (deliverablePartnershipOther.getInstitution() != null
                      && deliverablePartnershipOther.getDeliverable().getProject() != null) {
                      List<User> dUsers = this.getUserList(deliverablePartnershipOther.getInstitution().getId(),
                        deliverablePartnershipOther.getDeliverable().getProject().getId());

                      for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson2 : trueList) {
                        boolean addUser = false;
                        for (User user : dUsers) {

                          if (user.getId().equals(deliverableUserPartnershipPerson2.getUser().getId())) {
                            addUser = true;
                            break;
                          }

                        }

                        if (addUser) {
                          personList.add(deliverableUserPartnershipPerson2);
                        } else {
                          deliverableUserPartnershipPersonManager
                            .deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPerson2.getId());
                        }

                      }
                    }


                    deliverablePartnershipOther.setPartnershipPersons(trueList);
                  }

                  deliverableUserPartnershipManager.saveDeliverableUserPartnership(deliverablePartnershipOther);
                }
              }
            }

            // Save Countries list
            List<DeliverableLocation> countries =
              deliverableLocationManager.getDeliverableLocationbyPhase(deliverable.getId(), phase.getId());
            if (countries != null && countries.size() > 0) {
              for (DeliverableLocation deliverableLocation : countries) {
                deliverableLocationManager.saveDeliverableLocation(deliverableLocation);
              }
            }

            // Save Regions list
            List<DeliverableGeographicRegion> regions = deliverableGeographicRegionManager
              .getDeliverableGeographicRegionbyPhase(deliverable.getId(), phase.getId());
            if (regions != null && regions.size() > 0) {
              for (DeliverableGeographicRegion deliverableRegion : regions) {
                deliverableGeographicRegionManager.saveDeliverableGeographicRegion(deliverableRegion);
              }
            }

            // Reporting and upkeep
            if (phase.isReporting() || phase.getUpkeep()) {

              // Deliverable Quality check
              DeliverableQualityCheck deliverableQualityCheck = deliverableQualityCheckManager
                .getDeliverableQualityCheckByDeliverable(deliverable.getId(), phase.getId());
              if (deliverableQualityCheck != null) {
                deliverableQualityCheckManager.saveDeliverableQualityCheck(deliverableQualityCheck);
              }

              // Deliverable Dissemination
              List<DeliverableDissemination> deliverableDisseminations = deliverable.getDeliverableDisseminations()
                .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
              if (deliverableDisseminations != null && !deliverableDisseminations.isEmpty()) {
                if (deliverableDisseminations.size() > 1) {
                  logger.warn("There is more than 1 dissemination for deliverable: " + deliverable.getId()
                    + " and phase: " + phase.getId());
                }
                deliverableDisseminationManager.saveDeliverableDissemination(deliverableDisseminations.get(0));
              }

              // Deliverable Metadata elements
              List<DeliverableMetadataElement> deliverableMetadataElements =
                deliverable.getDeliverableMetadataElements().stream()
                  .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
              if (deliverableMetadataElements != null && !deliverableMetadataElements.isEmpty()) {
                for (DeliverableMetadataElement deliverableMetadataElement : deliverableMetadataElements) {
                  deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElement);
                }
              }

              // Deliverable Crps
              List<DeliverableCrp> deliverableCrps = deliverable.getDeliverableCrps().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
              if (deliverableCrps != null && !deliverableCrps.isEmpty()) {
                for (DeliverableCrp deliverableCrp : deliverableCrps) {
                  deliverableCrpManager.saveDeliverableCrp(deliverableCrp);
                }
              }

              // Deliverable Publication Metadata
              List<DeliverablePublicationMetadata> deliverablePublicationMetadata =
                deliverable.getDeliverablePublicationMetadatas().stream()
                  .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
              if (deliverablePublicationMetadata != null && !deliverablePublicationMetadata.isEmpty()) {
                if (deliverablePublicationMetadata.size() > 1) {
                  logger.warn("There is more than 1 publication metadata for deliverable: " + deliverable.getId()
                    + " and phase: " + phase.getId());
                }
                deliverablePublicationMetadataManager
                  .saveDeliverablePublicationMetadata(deliverablePublicationMetadata.get(0));
              }

              // Deliverable Users
              List<DeliverableUser> deliverableUsers = deliverable.getDeliverableUsers().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
              if (deliverableUsers != null && !deliverableUsers.isEmpty()) {
                for (DeliverableUser deliverableUser : deliverableUsers) {
                  deliverableUserManager.saveDeliverableUser(deliverableUser);
                }
              }

              // Deliverable Intellectual asset
              if (this.hasSpecificities(this.crpDeliverableIntellectualAsset())) {
                List<DeliverableIntellectualAsset> intellectualAssets = deliverable.getDeliverableIntellectualAssets()
                  .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
                if (intellectualAssets != null && !intellectualAssets.isEmpty()) {
                  if (intellectualAssets.size() > 1) {
                    logger.warn("There is more than 1 intellectual assets for deliverable: " + deliverable.getId()
                      + " and phase: " + phase.getId());
                  }
                  deliverableIntellectualAssetManager.saveDeliverableIntellectualAsset(intellectualAssets.get(0));
                }
              }


              // Deliverable Participant
              List<DeliverableParticipant> deliverableParticipants = deliverable.getDeliverableParticipants().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
              if (deliverableParticipants != null && !deliverableParticipants.isEmpty()) {
                if (deliverableParticipants.size() > 1) {
                  logger.warn("There is more than 1 deliverable participant for deliverable: " + deliverable.getId()
                    + " and phase: " + phase.getId());
                }
                deliverableParticipantManager.saveDeliverableParticipant(deliverableParticipants.get(0));
              }

            }

            // Deliverable info
            deliverable.getDeliverableInfo(phase).setModificationJustification(this.getJustification());
            deliverableInfoManager.saveDeliverableInfo(deliverable.getDeliverableInfo(phase));

            // Deliverable
            this.setModificationJustification(deliverable);
            deliverableManager.saveDeliverable(deliverable, this.getActionName(), relationsName, phase);

            // Delete autosave
            Path path = this.getAutoSaveFilePath();

            if (path.toFile().exists()) {
              path.toFile().delete();
            }


          }

        }

        logger.debug("Finished replication succesfully");
      } else {
        logger.debug("No deliverable selected");
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }

  public void setEntityByPhaseList(String entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }


  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }


}