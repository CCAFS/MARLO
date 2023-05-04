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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.jfree.util.Log;


public class DeliverablesByDisseminationURLHandleDOIAction extends BaseAction {


  private static final long serialVersionUID = 6304226585314276677L;


  List<Map<String, Object>> sources;

  private long phaseID;
  private String disseminationURL;
  private String handle;
  private String DOI;
  // GlobalUnit Manager
  private DeliverableManager deliverableManager;
  private PhaseManager phaseManager;
  private Map<String, Object> deliverableDataMap;
  private Map<String, Object> deliverableMap;

  @Inject
  public DeliverablesByDisseminationURLHandleDOIAction(APConfig config, PhaseManager phaseManager,
    DeliverableManager deliverableManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.deliverableManager = deliverableManager;
  }


  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();

    DeliverableDissemination deliverableDissemination = new DeliverableDissemination();
    Phase phase = phaseManager.getPhaseById(phaseID);

    List<Deliverable> deliverables = null;

    if ((disseminationURL != null || handle != null || DOI != null) && phaseID != 0) {
      deliverables = deliverableManager.getDeliverablesByPhase(phaseID);
    }

    if (deliverables != null && !deliverables.isEmpty() && phase != null) {
      deliverableMap = null;
      for (Deliverable deliverable : deliverables) {
        deliverableMap.put("id", deliverable.getId());

        deliverableDataMap = null;


        // Deliverable dissemination
        try {
          deliverableDissemination = deliverable.getDeliverableDisseminations().stream()
            .filter(ds -> ds.isActive() && ds.getPhase() != null && ds.getPhase().equals(phase))
            .collect(Collectors.toList()).get(0);
        } catch (Exception e) {

        }
        if (deliverableDissemination != null && deliverableDissemination.getDisseminationUrl() != null
          && deliverableDissemination.getDisseminationUrl().equals(disseminationURL)) {
          deliverableDataMap.put("disseminationURL", disseminationURL);
        }

        // Set Metadata Elements
        if (deliverable.getDeliverableMetadataElements() != null) {
          deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
        }


        for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getDeliverableMetadataElements()) {

          // DOI
          if (DOI != null && deliverableMetadataElement.getMetadataElement().getId() == 36L
            && deliverableMetadataElement.getElementValue() != null
            && !deliverableMetadataElement.getElementValue().equals(DOI)) {
            deliverableDataMap.put("DOI", DOI);
          }
          // Handle
          if (handle != null && deliverableMetadataElement.getMetadataElement().getId() == 36L
            && deliverableMetadataElement.getElementValue() != null
            && !deliverableMetadataElement.getElementValue().equals(handle)) {
            deliverableDataMap.put("handle", handle);
          }

        }
        deliverableMap.put("data", deliverableDataMap);
      }
      sources.add(deliverableMap);
    }
    return SUCCESS;
  }


  public List<Map<String, Object>> getSources() {
    return sources;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    try {
      disseminationURL = StringUtils.trim(parameters.get(APConstants.DISSEMINATION_URL).getMultipleValues()[0]);
    } catch (Exception e) {
      disseminationURL = null;
      Log.info(e);
    }
    try {
      handle = StringUtils.trim(parameters.get(APConstants.HANDLE).getMultipleValues()[0]);
    } catch (Exception e) {
      handle = null;
      Log.info(e);
    }
    try {
      DOI = StringUtils.trim(parameters.get(APConstants.DOI).getMultipleValues()[0]);
    } catch (Exception e) {
      DOI = null;
      Log.info(e);
    }
    try {
      phaseID = Long.valueOf(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      phaseID = 0;
      Log.info(e);
    }

  }

  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
