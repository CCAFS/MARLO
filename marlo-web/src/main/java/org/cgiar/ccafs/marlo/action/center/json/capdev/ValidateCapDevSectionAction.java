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

package org.cgiar.ccafs.marlo.action.center.json.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.action.center.json.monitoring.project.ValidateProjectSectionAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CapDevSectionEnum;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapdevDiscipline;
import org.cgiar.ccafs.marlo.data.model.CapdevLocations;
import org.cgiar.ccafs.marlo.data.model.CapdevOutputs;
import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;
import org.cgiar.ccafs.marlo.data.model.CapdevPartners;
import org.cgiar.ccafs.marlo.data.model.CapdevTargetgroup;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableType;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;
import org.cgiar.ccafs.marlo.data.model.Participant;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapDevDescriptionValidator;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapacityDevelopmentValidator;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapdevSupportingDocsValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateCapDevSectionAction extends BaseAction {


  private static final long serialVersionUID = -4345065728423046366L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ValidateProjectSectionAction.class);

  // Managers
  private ICapacityDevelopmentService capdevService;
  private ICenterDeliverableManager centerDeliverableService;
  private ICenterDeliverableTypeManager centerDeliverableTypeService;
  private ICenterSectionStatusManager sectionStatusService;

  // Parameters
  private boolean existCapdev;
  private boolean validSection;

  private String sectionName;
  private long capdevID;
  private long deliverableID;
  private Map<String, Object> section;
  // Model
  private CenterSectionStatus sectionStatus;

  // Validator
  private CapacityDevelopmentValidator interventionValidator;
  private CapDevDescriptionValidator descriptionValidator;
  private CapdevSupportingDocsValidator supportingDocsValidtor;


  @Inject
  public ValidateCapDevSectionAction(APConfig config, ICapacityDevelopmentService capdevService,
    ICenterDeliverableManager centerDeliverableService, ICenterDeliverableTypeManager centerDeliverableTypeService,
    ICenterSectionStatusManager sectionStatusService, CapacityDevelopmentValidator interventionValidator,
    CapDevDescriptionValidator descriptionValidator, CapdevSupportingDocsValidator supportingDocsValidtor) {
    super(config);
    this.capdevService = capdevService;
    this.centerDeliverableService = centerDeliverableService;
    this.centerDeliverableTypeService = centerDeliverableTypeService;
    this.sectionStatusService = sectionStatusService;
    this.interventionValidator = interventionValidator;
    this.descriptionValidator = descriptionValidator;
    this.supportingDocsValidtor = supportingDocsValidtor;
  }

  @Override
  public String execute() throws Exception {
    if (existCapdev && validSection) {
      switch (CapDevSectionEnum.getValue(sectionName)) {
        case INTERVENTION:
          this.validateIntervention();
          break;
        case DESCRIPTION:
          this.validateDescription();
          break;
        case SUPPORTINGDOCS:
          this.validateSupportingDocs();
          break;
      }
    }

    CapacityDevelopment capdev = capdevService.getCapacityDevelopmentById(capdevID);


    switch (CapDevSectionEnum.getValue(sectionName)) {
      case SUPPORTINGDOCS:
        section = new HashMap<String, Object>();
        section.put("sectionName", sectionName);
        section.put("missingFields", "");

        if (capdev != null) {
          List<CenterDeliverable> deliverables =
            new ArrayList<>(capdev.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

          if ((deliverables != null) && !deliverables.isEmpty()) {
            for (CenterDeliverable deliverable : deliverables) {

              sectionStatus = sectionStatusService.getSectionStatusByDeliverable(deliverable.getId(), capdevID,
                sectionName, this.getCenterYear());

              if (sectionStatus == null) {
                sectionStatus = new CenterSectionStatus();
                sectionStatus.setMissingFields("No section");
              }
              if (sectionStatus.getMissingFields().length() > 0) {
                section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
              }
            }
          } else {
            sectionStatus = new CenterSectionStatus();
            sectionStatus.setMissingFields("No deliverables");
            section.put("missingFields", section.get("missingFields") + "-" + sectionStatus.getMissingFields());
          }
        }
        break;
      default:
        sectionStatus =
          sectionStatusService.getSectionStatusByCapdev(capdev.getId(), sectionName, this.getCenterYear());

        section = new HashMap<String, Object>();
        section.put("sectionName", sectionStatus.getSectionName());
        section.put("missingFields", sectionStatus.getMissingFields());

        break;
    }

    Thread.sleep(500);

    return SUCCESS;
  }

  public long getCapdevID() {
    return capdevID;
  }


  public long getDeliverableID() {
    return deliverableID;
  }

  public Map<String, Object> getSection() {
    return section;
  }


  public String getSectionName() {
    return sectionName;
  }


  @Override
  public void prepare() throws Exception {

    Map<String, Parameter> parameters = this.getParameters();
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);
    capdevID = -1;
    deliverableID = -1;

    try {
      capdevID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.CAPDEV_ID).getMultipleValues()[0]));
      deliverableID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_DELIVERABLE_ID)));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the capdev id = {} ",
        StringUtils.trim(parameters.get(APConstants.CAPDEV_ID).getMultipleValues()[0]));
    }

    existCapdev = capdevService.existCapacityDevelopment(capdevID);

    // Validate if the section exists.
    List<String> sections = new ArrayList<>();
    sections.add("detailCapdev");
    sections.add("descriptionCapdev");
    sections.add("supportingDocs");

    validSection = sections.contains(sectionName);
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }


  public void setSection(Map<String, Object> section) {
    this.section = section;
  }


  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }

  public void validateDescription() {
    CapacityDevelopment capdev = capdevService.getCapacityDevelopmentById(capdevID);

    if (capdev != null) {

      if (capdev.getCapdevDiscipline() != null) {
        List<CapdevDiscipline> capdevDisciplines = new ArrayList<CapdevDiscipline>(
          capdev.getCapdevDiscipline().stream().filter(d -> d.isActive()).collect(Collectors.toList()));
        capdev.setCapdevDisciplineList(capdevDisciplines);
      }

      if (capdev.getCapdevTargetgroup() != null) {
        List<CapdevTargetgroup> capdevTargetGroups = new ArrayList<CapdevTargetgroup>(
          capdev.getCapdevTargetgroup().stream().filter(t -> t.isActive()).collect(Collectors.toList()));
        capdev.setCapdevTargetGroupList(capdevTargetGroups);
      }

      if (capdev.getCapdevPartners() != null) {
        List<CapdevPartners> capdevPartners = new ArrayList<CapdevPartners>(
          capdev.getCapdevPartners().stream().filter(p -> p.isActive()).collect(Collectors.toList()));
        capdev.setCapdevPartnersList(capdevPartners);
      }

      if (capdev.getCapdevOutputs() != null) {
        List<CapdevOutputs> capdevOuputs = new ArrayList<CapdevOutputs>(
          capdev.getCapdevOutputs().stream().filter(o -> o.isActive()).collect(Collectors.toList()));
        capdev.setCapdevOutputsList(capdevOuputs);
      }
    }
    descriptionValidator.validate(this, capdev);
  }


  public void validateIntervention() {
    CapacityDevelopment capdev = capdevService.getCapacityDevelopmentById(capdevID);

    if (capdev != null) {

      List<CapdevParticipant> participants =
        new ArrayList<>(capdev.getCapdevParticipant().stream().filter(p -> p.isActive()).collect(Collectors.toList()));
      Collections.sort(participants, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

      if (capdev.getCategory() == 1) {
        if (!participants.isEmpty()) {
          capdev.setParticipant(participants.get(0).getParticipant());
        }

      }

      if (!capdev.getCapdevLocations().isEmpty()) {
        List<CapdevLocations> regions = new ArrayList<>(capdev.getCapdevLocations().stream()
          .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 1))
          .collect(Collectors.toList()));
        Collections.sort(regions, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
        capdev.setCapDevRegions(regions);

        List<CapdevLocations> countries = new ArrayList<>(capdev.getCapdevLocations().stream()
          .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 2))
          .collect(Collectors.toList()));
        Collections.sort(countries, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
        capdev.setCapDevCountries(countries);

      }
      Participant participant = new Participant();
      participant = capdev.getParticipant();

      interventionValidator.validate(this, capdev, participant, null, null);


    }
  }


  public void validateSupportingDocs() {


    CapacityDevelopment capdev = capdevService.getCapacityDevelopmentById(capdevID);

    List<CenterDeliverable> centerDeliverables =
      new ArrayList<>(capdev.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

    for (CenterDeliverable deliverable : centerDeliverables) {

      if (deliverable != null) {
        if (deliverable.getDeliverableType() != null) {
          Long deliverableTypeParentId = deliverable.getDeliverableType().getDeliverableType().getId();

          List<CenterDeliverableType> deliverablesSubtypesList =
            new ArrayList<>(centerDeliverableTypeService.findAll().stream()
              .filter(
                dt -> (dt.getDeliverableType() != null) && (dt.getDeliverableType().getId() == deliverableTypeParentId))
              .collect(Collectors.toList()));

          Collections.sort(deliverablesSubtypesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));

        }

        if (deliverable.getDeliverableDocuments() != null) {

          List<CenterDeliverableDocument> documents =
            deliverable.getDeliverableDocuments().stream().filter(d -> d.isActive()).collect(Collectors.toList());
          Collections.sort(documents, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
          deliverable.setDocuments(documents);
        }
      }

      supportingDocsValidtor.validate(this, deliverable);

    }

  }

}
