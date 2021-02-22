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
import org.cgiar.ccafs.marlo.action.json.project.DeliverableMetadataByWOS;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DeliverableBulkSynchronizationAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -329901282034569795L;

  private static final Logger LOG = LoggerFactory.getLogger(DeliverableBulkSynchronizationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private DeliverableManager deliverableManager;

  // Action
  private DeliverableMetadataByWOS deliverableMetadataByWOS;

  // Variables
  private Deliverable deliverable;
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase phase;

  @Inject
  public DeliverableBulkSynchronizationAction(APConfig config, DeliverableMetadataByWOS deliverableMetadataByWOS,
    DeliverableManager deliverableManager) {
    super(config);
    this.deliverableMetadataByWOS = deliverableMetadataByWOS;
    this.deliverableManager = deliverableManager;
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


  public String getEntityByPhaseList() {
    return entityByPhaseList;
  }


  private String getLink(Long deliverableId) {
    String link = null;
    Deliverable deliverable = this.deliverableManager.getDeliverableById(selectedPhaseID);
    if (deliverable != null) {
      /*
       * DeliverableMetadataElements, element_id = 36 -> DOI
       * DeliverableDissemination, dissemination URL (look for doi), articleURL (look for doi)
       */
    }

    return link;
  }

  public long getSelectedPhaseID() {
    return selectedPhaseID;
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
        LOG.debug("Start synchronization in phase: " + selectedPhaseID);
        phase = phaseManager.getPhaseById(selectedPhaseID);

        for (String id : entityByPhaseList.trim().split(",")) {
          Long deliverableId = Long.valueOf(id);
          String link = this.getLink(deliverableId);

          if (link != null) {
            LOG.debug("Synchronizing deliverable : " + id);
            try {
              this.deliverableMetadataByWOS.saveInfo(phase, deliverableId, id);
            } catch (IOException ioe) {
              ioe.printStackTrace();
            }
          } else {
            LOG.debug("There was a problem on deliverable " + id + " sync");
          }
        }

        LOG.debug("Synchronization finished successfully");
      } else {
        LOG.debug("No deliverables selected");
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
