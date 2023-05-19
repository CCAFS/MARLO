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
import org.cgiar.ccafs.marlo.action.deliverable.dto.DeliverableSearchSummary;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
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
  private long deliverableID;
  private String disseminationURL;
  private String handle;
  private String DOI;
  private DeliverableManager deliverableManager;
  private PhaseManager phaseManager;
  private ProjectDeliverableSharedManager projectDeliverableSharedManager;

  @Inject
  public DeliverablesByDisseminationURLHandleDOIAction(APConfig config, PhaseManager phaseManager,
    DeliverableManager deliverableManager, ProjectDeliverableSharedManager projectDeliverableSharedManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.deliverableManager = deliverableManager;
    this.projectDeliverableSharedManager = projectDeliverableSharedManager;
  }

  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();

    Phase phase = phaseManager.getPhaseById(phaseID);
    List<Deliverable> deliverables = null;

    if ((disseminationURL != null || handle != null || DOI != null) && phaseID != 0 && deliverableID != 0) {
      deliverables = deliverableManager.getDeliverablesByPhase(phaseID);
    }

    if (deliverables != null && !deliverables.isEmpty() && phase != null) {
      deliverables = deliverables.stream().filter(d -> d != null && d.isActive() && d.getId() != null
        && d.getId() != deliverableID && d.getDeliverableInfo(phase).isActive()).collect(Collectors.toList());
      if (deliverables != null && !deliverables.isEmpty()) {

        for (Deliverable deliverable : deliverables) {
          if (deliverable != null && deliverable.getId() != null) {
            deliverable = deliverableManager.getDeliverableById(deliverable.getId());
          }
          DeliverableDissemination deliverableDissemination = new DeliverableDissemination();
          boolean isDOIDuplicated = false;
          boolean isDisseminationURLDuplicated = false;
          boolean isHandleDuplicated = false;
          String deliverableDOI = null;
          String deliverableHandle = null;
          String deliverableDisseminationURL = null;

          // Deliverable dissemination
          try {
            deliverableDissemination = deliverable.getDissemination(phase);
          } catch (Exception e) {
            Log.info(e);
          }
          // Dissemination URL
          if (disseminationURL != null && !disseminationURL.isEmpty() && deliverableDissemination != null
            && deliverableDissemination.getDisseminationUrl() != null
            && !deliverableDissemination.getDisseminationUrl().isEmpty()
            && deliverableDissemination.getDisseminationUrl().equals(disseminationURL)) {
            isDisseminationURLDuplicated = true;
          }

          // Set Metadata Elements
          if (deliverable.getDeliverableMetadataElements() != null) {
            deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }

          List<DeliverableMetadataElement> deliverableMetadataElements;
          deliverableMetadataElements = deliverable.getMetadataElements(phase);

          if (deliverableMetadataElements != null) {

            try {
              deliverableDOI = deliverableMetadataElements.stream()
                .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
                  && me.getMetadataElement().getId().longValue() == 36L && !StringUtils.isBlank(me.getElementValue()))
                .findFirst().orElse(null).getElementValue();
            } catch (Exception e) {
              Log.info(e);
            }

            try {
              deliverableHandle = deliverableMetadataElements.stream()
                .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
                  && me.getMetadataElement().getId().longValue() == 35L && !StringUtils.isBlank(me.getElementValue()))
                .findFirst().orElse(null).getElementValue();
            } catch (Exception e) {
              Log.info(e);
            }

            try {
              if (deliverableDOI != null && !deliverableDOI.isEmpty() && DOI != null && !DOI.isEmpty()
                && deliverableDOI.equals(DOI)) {
                isDOIDuplicated = true;
              }
            } catch (Exception e) {
              Log.info(e);
            }

            try {
              if (deliverableHandle != null && !deliverableHandle.isEmpty() && handle != null && !handle.isEmpty()
                && deliverableHandle.equals(handle)) {
                isHandleDuplicated = true;
              }
            } catch (Exception e) {
              Log.info(e);
            }
          }

          DeliverableSearchSummary deliverableDTO = new DeliverableSearchSummary();
          // fill object list
          if (isDOIDuplicated || isHandleDuplicated || isDisseminationURLDuplicated) {

            if (deliverableDissemination != null && deliverableDissemination.getDisseminationUrl() != null) {
              deliverableDisseminationURL = deliverableDissemination.getDisseminationUrl();
            }

            if (deliverableDOI != null) {
              deliverableDTO.setDOI(deliverableDOI);
            } else {
              deliverableDTO.setDOI("");
            }

            if (deliverableHandle != null) {
              deliverableDTO.setHandle(deliverableHandle);
            } else {
              deliverableDTO.setHandle("");
            }

            if (deliverableDisseminationURL != null) {
              deliverableDTO.setDisseminationURL(deliverableDisseminationURL);
            } else {
              deliverableDTO.setDisseminationURL("");
            }

            deliverableDTO.setDeliverableID(deliverable.getId());

            if (deliverable.getProject() != null && deliverable.getProject().getAcronym() != null) {
              deliverableDTO.setClusterAcronym(deliverable.getProject().getAcronym());
            } else {
              deliverableDTO.setClusterAcronym("");
            }

            // deliverable responsible
            String leader = null;
            List<DeliverableUserPartnership> deliverablePartnershipResponsibles = deliverable
              .getDeliverableUserPartnerships().stream()
              .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
              .collect(Collectors.toList());
            if (deliverablePartnershipResponsibles != null && !deliverablePartnershipResponsibles.isEmpty()) {
              if (deliverablePartnershipResponsibles.size() > 1) {
                Log.warn("There are more than 1 deliverable responsibles for D" + deliverable.getId() + " "
                  + phase.toString());
              }
              DeliverableUserPartnership responisble = deliverablePartnershipResponsibles.get(0);

              if (responisble != null) {
                if (responisble.getDeliverableUserPartnershipPersons() != null) {

                  DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
                  List<DeliverableUserPartnershipPerson> persons = responisble.getDeliverableUserPartnershipPersons()
                    .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
                  if (!persons.isEmpty()) {
                    responsibleppp = persons.get(0);
                  }

                  if (responsibleppp != null && responsibleppp.getUser() != null
                    && responsibleppp.getUser().getComposedName() != null) {
                    leader = responsibleppp.getUser().getComposedName();
                  }
                }
              }
            }

            // Set deliverable responsible
            if (leader != null) {
              deliverableDTO.setResponsible(leader);
            }

            // Set cluster leader
            String projectLeaderName = null;
            if (deliverable.getProject() != null) {
              ProjectPartnerPerson projectLeader = deliverable.getProject().getLeaderPersonDB(phase);
              if (projectLeader != null) {
                projectLeaderName = projectLeader.getComposedName();
              }
            }
            deliverableDTO.setClusterLeader(projectLeaderName);

            // Set subCategory
            if (deliverable.getDeliverableInfo(phase).getDeliverableType() != null
              && deliverable.getDeliverableInfo(phase).getDeliverableType().getName() != null) {
              deliverableDTO.setSubCategory(deliverable.getDeliverableInfo(phase).getDeliverableType().getName());
            }

            // Set title
            if (deliverable.getDeliverableInfo(phase).getTitle() != null) {
              deliverableDTO.setTitle(deliverable.getDeliverableInfo(phase).getTitle());
            }

            // Set duplicated field info
            deliverableDTO.setDuplicatedField("");
            if (isDOIDuplicated) {
              deliverableDTO.setDuplicatedField("DOI | ");
            }
            if (isHandleDuplicated) {
              deliverableDTO.setDuplicatedField(deliverableDTO.getDuplicatedField().concat("Handle | "));
            }
            if (isDisseminationURLDuplicated) {
              deliverableDTO.setDuplicatedField(deliverableDTO.getDuplicatedField().concat("Dissemination URL "));
            }

            deliverableDTO.setPhaseID(phaseID);


            // Shared deliverables
            if (deliverable.getSharedWithProjects() == null) {
              deliverable.setSharedWithProjects(
                "" + deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
            } else {
              if (deliverable != null && deliverable.getSharedWithProjects() != null
                && deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null
                && !deliverable.getSharedWithProjects()
                  .contains(deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym())) {
                deliverable.setSharedWithProjects(deliverable.getSharedWithProjects() + "; "
                  + deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
              }
            }

            if (deliverable.getSharedWithProjects() != null) {
              deliverableDTO.setSharedClusters(deliverable.getSharedWithProjects());
            }

            sources.add(deliverableDTO.convertToMap());
          }
        } // End deliverables for
      }
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
    try {
      deliverableID = Long
        .valueOf(StringUtils.trim(parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      deliverableID = 0;
      Log.info(e);
    }
  }

  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
