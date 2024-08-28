/*
 * This file is part of Managing Agricultural Research for Learning&*Outcomes Platform(MARLO).
 ** MARLO is free software:you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation,either version 3 of the License,or*at your option)any later version.
 ** MARLO is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See
 * the
 ** GNU General Public License for more details.
 ** You should have received a copy of the GNU General Public License
 ** along with MARLO.If not,see<http:// www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PrimaryAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.manager.PrimaryAllianceStrategicOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.RelatedAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.manager.RelatedAllianceLeverSdgContributionManager;
import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceLever;
import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceStrategicOutcome;
import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLever;
import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLeverSdgContribution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AllianceLeversManagementAction extends BaseAction {

  private static Logger logger = LoggerFactory.getLogger(AllianceLeversManagementAction.class);
  private static final long serialVersionUID = -793652591843623397L;
  private List<PrimaryAllianceLever> primaryLevers;
  private List<PrimaryAllianceStrategicOutcome> primaryStrategicOutcomeLevers;
  private List<RelatedAllianceLever> relatedLevers;
  private List<RelatedAllianceLeverSdgContribution> relatedLeverSdgContributions;
  private PrimaryAllianceLeverManager primaryAllianceLeverManager;
  private PrimaryAllianceStrategicOutcomeManager primaryAllianceStrategicOutcomeManager;
  private RelatedAllianceLeverManager relatedAllianceLeverManager;
  private RelatedAllianceLeverSdgContributionManager relatedAllianceLeverSdgContributionManager;

  @Inject
  public AllianceLeversManagementAction(APConfig config, PrimaryAllianceLeverManager primaryAllianceLeverManager,
    PrimaryAllianceStrategicOutcomeManager primaryAllianceStrategicOutcomeManager,
    RelatedAllianceLeverManager relatedAllianceLeverManager,
    RelatedAllianceLeverSdgContributionManager relatedAllianceLeverSdgContributionManager) {
    super(config);
    this.primaryAllianceLeverManager = primaryAllianceLeverManager;
    this.primaryAllianceStrategicOutcomeManager = primaryAllianceStrategicOutcomeManager;
    this.relatedAllianceLeverManager = relatedAllianceLeverManager;
    this.relatedAllianceLeverSdgContributionManager = relatedAllianceLeverSdgContributionManager;
  }

  /**
   * Fill related levers SDG Contributions
   */
  public void fillSdgContributions() {
    if (relatedLevers != null && !relatedLevers.isEmpty()) {
      try {
        List<RelatedAllianceLeverSdgContribution> relatedSubLevers =
          relatedAllianceLeverSdgContributionManager.findAll();

        if (relatedSubLevers != null && !relatedSubLevers.isEmpty()) {
          relatedLevers.forEach(action -> {
            List<RelatedAllianceLeverSdgContribution> relatedSubLeversAdd = relatedSubLevers.stream()
              .filter(subLever -> action != null && action.getId() != null && subLever != null
                && subLever.getRelatedAllianceLever() != null && subLever.getRelatedAllianceLever().getId() != null
                && subLever.getRelatedAllianceLever().getId().equals(action.getId()))
              .collect(Collectors.toList());

            if (!relatedSubLeversAdd.isEmpty()) {
              action.setRelatedLeverSdgContributions(relatedSubLeversAdd);
            }
          });
        }
      } catch (Exception e) {
        logger.info(e + "no sub actions added yet");
      }
    }
  }

  /**
   * Fill Strategic outcomes levers with sub actions list
   */
  public void fillStrategicOutcomesLevers() {
    if (primaryLevers != null && !primaryLevers.isEmpty()) {
      try {
        List<PrimaryAllianceStrategicOutcome> primarySubLevers = primaryAllianceStrategicOutcomeManager.findAll();

        if (primarySubLevers != null && !primarySubLevers.isEmpty()) {
          primaryLevers.forEach(action -> {
            List<PrimaryAllianceStrategicOutcome> primarySubLeversAdd = primarySubLevers.stream()
              .filter(subLever -> action != null && action.getId() != null && subLever != null
                && subLever.getPrimaryAllianceLever() != null && subLever.getPrimaryAllianceLever().getId() != null
                && subLever.getPrimaryAllianceLever().getId().equals(action.getId()))
              .collect(Collectors.toList());

            if (!primarySubLeversAdd.isEmpty()) {
              action.setPrimaryStrategicOutcomes(primarySubLeversAdd);
            }
          });
        }
      } catch (Exception e) {
        logger.info(e + "no sub actions added yet");
      }
    }
  }

  public List<PrimaryAllianceLever> getPrimaryLevers() {
    return primaryLevers;
  }

  public List<PrimaryAllianceStrategicOutcome> getPrimaryStrategicOutcomeLevers() {
    return primaryStrategicOutcomeLevers;
  }

  public List<RelatedAllianceLever> getRelatedLevers() {
    return relatedLevers;
  }

  public List<RelatedAllianceLeverSdgContribution> getRelatedLeverSdgContributions() {
    return relatedLeverSdgContributions;
  }

  @Override
  public void prepare() throws Exception {
    primaryLevers = new ArrayList<>();
    primaryLevers = primaryAllianceLeverManager.findAll();
    this.fillStrategicOutcomesLevers();
    this.fillSdgContributions();
    if (this.isHttpPost()) {
      if (primaryLevers != null) {
        primaryLevers.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.canAcessCrpAdmin()) {
      this.savePrimaryLevers();
      this.saveRelatedLevers();

      if (this.getUrl() == null || this.getUrl().isEmpty()) {

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

  public void savePrimaryLevers() {
    List<PrimaryAllianceStrategicOutcome> toDelete = new ArrayList<>();
    List<PrimaryAllianceLever> primaryLeversDB = null;
    primaryLeversDB = primaryAllianceLeverManager.findAll();

    try {
      List<PrimaryAllianceStrategicOutcome> DB = null;
      DB = primaryAllianceStrategicOutcomeManager.findAll();
      if (this.primaryLevers != null && !this.primaryLevers.isEmpty()) {
        for (PrimaryAllianceLever action : this.primaryLevers) {

          // Remove Sub actions
          if (action.getPrimaryStrategicOutcomes() != null && !action.getPrimaryStrategicOutcomes().isEmpty()) {
            // Obtener los IDs de las subacciones en action.getPrimaryStrategicOutcomes()
            Set<Long> strategicOutcomeIdsInFrontend = action.getPrimaryStrategicOutcomes().stream()
              .filter(Objects::nonNull).map(PrimaryAllianceStrategicOutcome::getId).collect(Collectors.toSet());

            // Filter DB to retain only the subactions present in
            // action.getPrimaryStrategicOutcomes()
            DB = DB.stream()
              .filter(strategicOutcomeDB -> strategicOutcomeDB.getPrimaryAllianceLever() != null
                && strategicOutcomeDB.getPrimaryAllianceLever().getId() != null
                && strategicOutcomeDB.getPrimaryAllianceLever().getId().equals(action.getId())
                && !strategicOutcomeIdsInFrontend.contains(strategicOutcomeDB.getId()))
              .collect(Collectors.toList());

            // Delete subactions from DB that are not in action.getPrimaryStrategicOutcomes()
            DB.forEach(strategicOutcomeDB -> {
              primaryAllianceStrategicOutcomeManager.deletePrimaryAllianceStrategicOutcome(strategicOutcomeDB.getId());
              action.getPrimaryStrategicOutcomes().removeIf(sa -> sa.getId().equals(strategicOutcomeDB.getId()));
              toDelete.add(strategicOutcomeDB);
            });

          } else {
            DB.forEach(strategicOutcomeDB -> {
              primaryAllianceStrategicOutcomeManager.deletePrimaryAllianceStrategicOutcome(strategicOutcomeDB.getId());
              toDelete.add(strategicOutcomeDB);
            });
          }
        }
      }
    } catch (Exception e) {
      logger.info(e + " error deleting sub action");
    }

    if (this.primaryLevers != null && !this.primaryLevers.isEmpty()) {

      if (primaryLeversDB != null) {
        try {
          List<PrimaryAllianceStrategicOutcome> strategicOutcomes = new ArrayList<>();

          for (PrimaryAllianceLever actionDB : primaryLeversDB) {
            if (this.primaryLevers != null && !this.primaryLevers.isEmpty()) {
              Set<Long> actionIdsInFrontend = this.primaryLevers.stream().filter(Objects::nonNull)
                .map(PrimaryAllianceLever::getId).collect(Collectors.toSet());
              if (!actionIdsInFrontend.contains(actionDB.getId())) {

                // Validate previous sub actions in DB for each priority actions

                try {
                  strategicOutcomes = primaryAllianceStrategicOutcomeManager.findAll().stream()
                    .filter(strategicOutcomeDB -> strategicOutcomeDB.getPrimaryAllianceLever() != null
                      && strategicOutcomeDB.getPrimaryAllianceLever().getId() != null
                      && strategicOutcomeDB.getPrimaryAllianceLever().getId().equals(actionDB.getId()))
                    .collect(Collectors.toList());;


                  if (strategicOutcomes != null && !strategicOutcomes.isEmpty()) {

                    // delete sub actions
                    for (PrimaryAllianceStrategicOutcome strategicOutcomeDelete : strategicOutcomes) {
                      if (strategicOutcomeDelete != null && strategicOutcomeDelete.getId() != null) {
                        primaryAllianceStrategicOutcomeManager
                          .deletePrimaryAllianceStrategicOutcome(strategicOutcomeDelete.getId());
                      }
                    }
                  }
                } catch (Exception e) {
                  logger.error("error deleting sub actions", e);
                }
                primaryAllianceLeverManager.deletePrimaryAllianceLever(actionDB.getId());


              }
            }
          }
        } catch (Exception e) {
          logger.info(e + " error deleting actions");
        }
      }

      for (PrimaryAllianceLever action : this.primaryLevers) {
        PrimaryAllianceLever actionSave = new PrimaryAllianceLever();
        if (action.getId() != null) {
          actionSave = primaryAllianceLeverManager.getPrimaryAllianceLeverById(action.getId());
        }

        if (toDelete != null && !toDelete.isEmpty()) {

          for (PrimaryAllianceStrategicOutcome strategicOutcometoDelete : toDelete) {
            if (actionSave.getPrimaryStrategicOutcomes().contains(strategicOutcometoDelete)) {
              actionSave.getPrimaryStrategicOutcomes().remove(strategicOutcometoDelete);
            }
          }
        }

        if (action.getName() != null) {
          actionSave.setName(action.getName());
        }
        if (action.getDescription() != null) {
          actionSave.setDescription(action.getDescription());
        }
        List<PrimaryAllianceStrategicOutcome> strategicOutcomesTemp = new ArrayList<>();
        if (action.getPrimaryStrategicOutcomes() != null && !action.getPrimaryStrategicOutcomes().isEmpty()) {
          strategicOutcomesTemp = action.getPrimaryStrategicOutcomes();
        }

        action = primaryAllianceLeverManager.savePrimaryAllianceLever(actionSave);
        action.setPrimaryStrategicOutcomes(strategicOutcomesTemp);

        // Save sub-actions
        if (action.getPrimaryStrategicOutcomes() != null && !action.getPrimaryStrategicOutcomes().isEmpty()) {
          for (PrimaryAllianceStrategicOutcome strategicOutcome : action.getPrimaryStrategicOutcomes()) {
            PrimaryAllianceStrategicOutcome strategicOutcomeSave = new PrimaryAllianceStrategicOutcome();

            if (strategicOutcome.getId() != null) {
              strategicOutcomeSave.setId(strategicOutcome.getId());
            }
            if (strategicOutcome.getName() != null) {
              strategicOutcomeSave.setName(strategicOutcome.getName());
            }
            if (strategicOutcome.getDescription() != null) {
              strategicOutcomeSave.setDescription(strategicOutcome.getDescription());
            }
            if (action.getId() != null) {
              strategicOutcomeSave.setPrimaryAllianceLever(action);
            }
            try {
              primaryAllianceStrategicOutcomeManager.savePrimaryAllianceStrategicOutcome(strategicOutcomeSave);
            } catch (Exception e) {
              logger.info(e + " error saving sub action");
            }
          }
        }
      }
    } else {
      // Delete all priority actions DB
      try {
        List<PrimaryAllianceStrategicOutcome> strategicOutcomes = new ArrayList<>();

        if (primaryLeversDB != null && !primaryLeversDB.isEmpty()) {
          for (PrimaryAllianceLever primaryLever : primaryLeversDB) {

            strategicOutcomes = primaryAllianceStrategicOutcomeManager.findAll().stream()
              .filter(strategicOutcomeDB -> strategicOutcomeDB.getPrimaryAllianceLever() != null
                && strategicOutcomeDB.getPrimaryAllianceLever().getId() != null
                && strategicOutcomeDB.getPrimaryAllianceLever().getId().equals(primaryLever.getId()))
              .collect(Collectors.toList());;


            if (strategicOutcomes != null && !strategicOutcomes.isEmpty()) {

              // delete sub actions
              for (PrimaryAllianceStrategicOutcome strategicOutcomeDelete : strategicOutcomes) {
                if (strategicOutcomeDelete != null && strategicOutcomeDelete.getId() != null) {
                  primaryAllianceStrategicOutcomeManager
                    .deletePrimaryAllianceStrategicOutcome(strategicOutcomeDelete.getId());
                }
              }
            }

            primaryAllianceLeverManager.deletePrimaryAllianceLever(primaryLever.getId());
          }
        }
      } catch (Exception e) {
        logger.error("error deleting priority actions: " + e);
      }
    }
  }

  public void saveRelatedLevers() {
    List<RelatedAllianceLeverSdgContribution> toDelete = new ArrayList<>();
    List<RelatedAllianceLever> relatedLeversDB = null;
    relatedLeversDB = relatedAllianceLeverManager.findAll();

    try {
      List<RelatedAllianceLeverSdgContribution> DB = null;
      DB = relatedAllianceLeverSdgContributionManager.findAll();
      if (this.relatedLevers != null && !this.relatedLevers.isEmpty()) {
        for (RelatedAllianceLever action : this.relatedLevers) {

          // Remove Sub actions
          if (action.getRelatedLeverSdgContributions() != null && !action.getRelatedLeverSdgContributions().isEmpty()) {
            // Obtener los IDs de las subacciones en action.getRelatedLeverSdgContributions()
            Set<Long> sdgContributionsIdsInFrontend = action.getRelatedLeverSdgContributions().stream()
              .filter(Objects::nonNull).map(RelatedAllianceLeverSdgContribution::getId).collect(Collectors.toSet());

            // Filter DB to retain only the subactions present in
            // action.getRelatedLeverSdgContributions()
            DB = DB.stream()
              .filter(sdgContributionDB -> sdgContributionDB.getRelatedAllianceLever() != null
                && sdgContributionDB.getRelatedAllianceLever().getId() != null
                && sdgContributionDB.getRelatedAllianceLever().getId().equals(action.getId())
                && !sdgContributionsIdsInFrontend.contains(sdgContributionDB.getId()))
              .collect(Collectors.toList());

            // Delete subactions from DB that are not in action.getRelatedLeverSdgContributions()
            DB.forEach(sdgContributionDB -> {
              relatedAllianceLeverSdgContributionManager
                .deleteRelatedAllianceLeverSdgContribution(sdgContributionDB.getId());
              action.getRelatedLeverSdgContributions().removeIf(sa -> sa.getId().equals(sdgContributionDB.getId()));
              toDelete.add(sdgContributionDB);
            });

          } else {
            DB.forEach(sdgContributionDB -> {
              relatedAllianceLeverSdgContributionManager
                .deleteRelatedAllianceLeverSdgContribution(sdgContributionDB.getId());
              toDelete.add(sdgContributionDB);
            });
          }
        }
      }
    } catch (Exception e) {
      logger.info(e + " error deleting sub action");
    }

    if (this.relatedLevers != null && !this.relatedLevers.isEmpty()) {

      if (relatedLeversDB != null) {
        try {
          List<RelatedAllianceLeverSdgContribution> sdgContributions = new ArrayList<>();

          for (RelatedAllianceLever actionDB : relatedLeversDB) {
            if (this.relatedLevers != null && !this.relatedLevers.isEmpty()) {
              Set<Long> actionIdsInFrontend = this.relatedLevers.stream().filter(Objects::nonNull)
                .map(RelatedAllianceLever::getId).collect(Collectors.toSet());
              if (!actionIdsInFrontend.contains(actionDB.getId())) {

                // Validate previous sub actions in DB for each priority actions

                try {
                  sdgContributions = relatedAllianceLeverSdgContributionManager.findAll().stream()
                    .filter(sdgContributionDB -> sdgContributionDB.getRelatedAllianceLever() != null
                      && sdgContributionDB.getRelatedAllianceLever().getId() != null
                      && sdgContributionDB.getRelatedAllianceLever().getId().equals(actionDB.getId()))
                    .collect(Collectors.toList());;


                  if (sdgContributions != null && !sdgContributions.isEmpty()) {

                    // delete sub actions
                    for (RelatedAllianceLeverSdgContribution sdgContributionDelete : sdgContributions) {
                      if (sdgContributionDelete != null && sdgContributionDelete.getId() != null) {
                        relatedAllianceLeverSdgContributionManager
                          .deleteRelatedAllianceLeverSdgContribution(sdgContributionDelete.getId());
                      }
                    }
                  }
                } catch (Exception e) {
                  logger.error("error deleting sub actions", e);
                }
                relatedAllianceLeverManager.deleteRelatedAllianceLever(actionDB.getId());


              }
            }
          }
        } catch (Exception e) {
          logger.info(e + " error deleting actions");
        }
      }

      for (RelatedAllianceLever action : this.relatedLevers) {
        RelatedAllianceLever actionSave = new RelatedAllianceLever();
        if (action.getId() != null) {
          actionSave = relatedAllianceLeverManager.getRelatedAllianceLeverById(action.getId());
        }

        if (toDelete != null && !toDelete.isEmpty()) {

          for (RelatedAllianceLeverSdgContribution strategicOutcometoDelete : toDelete) {
            if (actionSave.getRelatedLeverSdgContributions().contains(strategicOutcometoDelete)) {
              actionSave.getRelatedLeverSdgContributions().remove(strategicOutcometoDelete);
            }
          }
        }

        if (action.getName() != null) {
          actionSave.setName(action.getName());
        }
        if (action.getDescription() != null) {
          actionSave.setDescription(action.getDescription());
        }
        List<RelatedAllianceLeverSdgContribution> sdgContributionsTemp = new ArrayList<>();
        if (action.getRelatedLeverSdgContributions() != null && !action.getRelatedLeverSdgContributions().isEmpty()) {
          sdgContributionsTemp = action.getRelatedLeverSdgContributions();
        }

        action = relatedAllianceLeverManager.saveRelatedAllianceLever(actionSave);
        action.setRelatedLeverSdgContributions(sdgContributionsTemp);

        // Save sub-actions
        if (action.getRelatedLeverSdgContributions() != null && !action.getRelatedLeverSdgContributions().isEmpty()) {
          for (RelatedAllianceLeverSdgContribution strategicOutcome : action.getRelatedLeverSdgContributions()) {
            RelatedAllianceLeverSdgContribution strategicOutcomeSave = new RelatedAllianceLeverSdgContribution();

            if (strategicOutcome.getId() != null) {
              strategicOutcomeSave.setId(strategicOutcome.getId());
            }
            if (strategicOutcome.getName() != null) {
              strategicOutcomeSave.setName(strategicOutcome.getName());
            }
            if (strategicOutcome.getDescription() != null) {
              strategicOutcomeSave.setDescription(strategicOutcome.getDescription());
            }
            if (action.getId() != null) {
              strategicOutcomeSave.setRelatedAllianceLever(action);
            }
            try {
              relatedAllianceLeverSdgContributionManager.saveRelatedAllianceLeverSdgContribution(strategicOutcomeSave);
            } catch (Exception e) {
              logger.info(e + " error saving sub action");
            }
          }
        }
      }
    } else {
      // Delete all priority actions DB
      try {
        List<RelatedAllianceLeverSdgContribution> sdgContributions = new ArrayList<>();

        if (relatedLeversDB != null && !relatedLeversDB.isEmpty()) {
          for (RelatedAllianceLever relatedLever : relatedLeversDB) {

            sdgContributions = relatedAllianceLeverSdgContributionManager.findAll().stream()
              .filter(sdgContributionDB -> sdgContributionDB.getRelatedAllianceLever() != null
                && sdgContributionDB.getRelatedAllianceLever().getId() != null
                && sdgContributionDB.getRelatedAllianceLever().getId().equals(relatedLever.getId()))
              .collect(Collectors.toList());;


            if (sdgContributions != null && !sdgContributions.isEmpty()) {

              // delete sub actions
              for (RelatedAllianceLeverSdgContribution sdgContributionDelete : sdgContributions) {
                if (sdgContributionDelete != null && sdgContributionDelete.getId() != null) {
                  relatedAllianceLeverSdgContributionManager
                    .deleteRelatedAllianceLeverSdgContribution(sdgContributionDelete.getId());
                }
              }
            }

            relatedAllianceLeverManager.deleteRelatedAllianceLever(relatedLever.getId());
          }
        }
      } catch (Exception e) {
        logger.error("error deleting priority actions: " + e);
      }
    }
  }

  public void setPrimaryLevers(List<PrimaryAllianceLever> primaryLevers) {
    this.primaryLevers = primaryLevers;
  }

  public void setPrimaryStrategicOutcomeLevers(List<PrimaryAllianceStrategicOutcome> primaryStrategicOutcomeLevers) {
    this.primaryStrategicOutcomeLevers = primaryStrategicOutcomeLevers;
  }

  public void setRelatedLevers(List<RelatedAllianceLever> relatedLevers) {
    this.relatedLevers = relatedLevers;
  }

  public void setRelatedLeverSdgContributions(List<RelatedAllianceLeverSdgContribution> relatedLeverSdgContributions) {
    this.relatedLeverSdgContributions = relatedLeverSdgContributions;
  }

  @Override
  public void validate() {
    try {
      if (save) {
      }
    } catch (Exception e) {
      Log.error("validating error " + e);
    }
  }
}