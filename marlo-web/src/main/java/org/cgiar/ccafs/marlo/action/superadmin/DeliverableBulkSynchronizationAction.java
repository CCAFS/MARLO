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
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationsNotMappedManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAltmetricInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.ExternalSourceAuthorManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.doi.DOIService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
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
  private DeliverableMetadataElementManager deliverableMetadataElementManager;
  private DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager;
  private DeliverableAffiliationManager deliverableAffiliationManager;
  private DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager;
  private ExternalSourceAuthorManager externalSourceAuthorManager;
  private InstitutionManager institutionManager;
  private DeliverableManager deliverableManager;
  private PhaseManager phaseManager;
  private DeliverableAltmetricInfoManager deliverableAltmetricInfoManager;

  // Action
  private DeliverableMetadataByWOS deliverableMetadataByWOS;

  // Variables
  private Deliverable deliverable;
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase phase;

  @Inject
  public DeliverableBulkSynchronizationAction(APConfig config, DeliverableManager deliverableManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager, GlobalUnitManager globalUnitManager,
    PhaseManager phaseManager, DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager,
    DeliverableAffiliationManager deliverableAffiliationManager,
    DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager,
    ExternalSourceAuthorManager externalSourceAuthorManager, InstitutionManager institutionManager,
    DeliverableAltmetricInfoManager deliverableAltmetricInfoManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.deliverableMetadataElementManager = deliverableMetadataElementManager;
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.deliverableMetadataExternalSourcesManager = deliverableMetadataExternalSourcesManager;
    this.deliverableAffiliationManager = deliverableAffiliationManager;
    this.deliverableAffiliationsNotMappedManager = deliverableAffiliationsNotMappedManager;
    this.externalSourceAuthorManager = externalSourceAuthorManager;
    this.institutionManager = institutionManager;
    this.deliverableAltmetricInfoManager = deliverableAltmetricInfoManager;
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
    Deliverable deliverable = this.deliverableManager.getDeliverableById(deliverableId);

    if (deliverable != null) {
      // DeliverableMetadataElements, element_id = 36 -> DOI
      List<DeliverableMetadataElement> metadataElements = deliverable.getMetadataElements(phase);
      if (metadataElements != null) {
        DeliverableMetadataElement doiMetadataElement = metadataElements.stream()
          .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
            && me.getMetadataElement().getId().longValue() == 36L && !StringUtils.isBlank(me.getElementValue()))
          .findFirst().orElse(null);
        if (doiMetadataElement != null) {
          link = DOIService.tryGetDoiName(StringUtils.stripToEmpty(doiMetadataElement.getElementValue()));
        } else {
          // search by handle
          doiMetadataElement = metadataElements.stream()
            .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
              && me.getMetadataElement().getId().longValue() == 35L && !StringUtils.isBlank(me.getElementValue()))
            .findFirst().orElse(null);
          if (doiMetadataElement != null) {
            link = doiMetadataElement.getElementValue();
          }
        }
      }

      if (StringUtils.isBlank(link)) {
        // DeliverableDissemination, dissemination URL (look for doi), articleURL (look for doi)
        DeliverableDissemination dissemination = deliverable.getDissemination(phase);
        if (link != null && !link.isEmpty() && link.contains("handle")) {
          return link;
        }

        link = DOIService.tryGetDoiName(StringUtils.stripToEmpty(dissemination.getDisseminationUrl()));
        if (StringUtils.isBlank(link)) {
          link = DOIService.tryGetDoiName(StringUtils.stripToEmpty(dissemination.getArticleUrl()));
        }
      }
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
    this.deliverableMetadataByWOS = new DeliverableMetadataByWOS(config, deliverableAffiliationManager,
      deliverableMetadataExternalSourcesManager, deliverableAffiliationsNotMappedManager, externalSourceAuthorManager,
      deliverableManager, institutionManager, phaseManager, deliverableAltmetricInfoManager);
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (entityByPhaseList != null && !entityByPhaseList.isEmpty()) {
        LOG.debug("Start synchronization in phase: " + selectedPhaseID);
        phase = phaseManager.getPhaseById(selectedPhaseID);

        for (String id : entityByPhaseList.trim().split(",")) {
          Long deliverableId = Long.valueOf(StringUtils.stripToNull(id));
          String link = this.getLink(deliverableId);

          if (link != null) {
            LOG.debug("Synchronizing deliverable : " + id);
            try {
              this.deliverableMetadataByWOS.saveInfo(selectedPhaseID, deliverableId, link);
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
