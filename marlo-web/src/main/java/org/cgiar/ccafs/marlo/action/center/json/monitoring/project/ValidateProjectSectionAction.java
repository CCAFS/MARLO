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

package org.cgiar.ccafs.marlo.action.center.json.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectCrosscutingThemeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.center.monitoring.project.CenterDeliverableValidator;
import org.cgiar.ccafs.marlo.validation.center.monitoring.project.CenterProjectDescriptionValidator;
import org.cgiar.ccafs.marlo.validation.center.monitoring.project.ProjectPartnerValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidateProjectSectionAction extends BaseAction {


  private static final long serialVersionUID = -4757402842700251641L;


  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ValidateProjectSectionAction.class);

  // Managers
  private ICenterProjectManager projectService;

  private ICenterProjectCrosscutingThemeManager projectCrosscutingThemeService;

  private ICenterSectionStatusManager sectionStatusService;
  // Parameters
  private boolean existProject;
  private boolean validSection;

  private String sectionName;
  private long projectID;
  private Map<String, Object> section;
  // Model
  private CenterSectionStatus sectionStatus;
  // Validator
  private CenterProjectDescriptionValidator descriptionValidator;

  private ProjectPartnerValidator partnerValidator;

  private CenterDeliverableValidator deliverableValidator;

  @Inject
  public ValidateProjectSectionAction(APConfig config, ICenterProjectManager projectService,
    ICenterSectionStatusManager sectionStatusService, CenterProjectDescriptionValidator descriptionValidator,
    ProjectPartnerValidator partnerValidator, CenterDeliverableValidator deliverableValidator,
    ICenterProjectCrosscutingThemeManager projectCrosscutingThemeService) {
    super(config);
    this.projectService = projectService;
    this.projectCrosscutingThemeService = projectCrosscutingThemeService;
    this.sectionStatusService = sectionStatusService;
    this.deliverableValidator = deliverableValidator;
    this.descriptionValidator = descriptionValidator;
    this.partnerValidator = partnerValidator;
  }

  @Override
  public String execute() throws Exception {
    if (existProject && validSection) {
      switch (ProjectSectionsEnum.getValue(sectionName)) {
        case DESCRIPTION:
          this.validateDescription();
          break;
        case PARTNERS:
          this.validatePartners();
          break;
        case DELIVERABLES:
          this.validateDeliverables();
          break;
      }
    }

    CenterProject project = projectService.getCenterProjectById(projectID);
    CenterProgram program = project.getResearchProgram();

    switch (ProjectSectionsEnum.getValue(sectionName)) {
      case DELIVERABLES:
        section = new HashMap<String, Object>();
        section.put("sectionName", sectionName);
        section.put("missingFields", "");

        if (project != null) {
          List<CenterDeliverable> deliverables =
            new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

          if (deliverables != null && !deliverables.isEmpty()) {
            for (CenterDeliverable deliverable : deliverables) {

              sectionStatus = sectionStatusService.getSectionStatusByDeliverable(deliverable.getId(), projectID,
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
          sectionStatusService.getSectionStatusByProject(program.getId(), projectID, sectionName, this.getCenterYear());
        section = new HashMap<String, Object>();
        section.put("sectionName", sectionStatus.getSectionName());
        section.put("missingFields", sectionStatus.getMissingFields());
        break;
    }

    Thread.sleep(500);

    return SUCCESS;
  }

  public long getProjectID() {
    return projectID;
  }

  public Map<String, Object> getSection() {
    return section;
  }

  public String getSectionName() {
    return sectionName;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    // sectionName = StringUtils.trim(((String[]) parameters.get(APConstants.SECTION_NAME))[0]);
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);
    projectID = -1;

    try {
      // projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_ID))[0]));
      projectID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the crp program id = {} ",
        // StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_ID))[0]));
        StringUtils.trim(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]));
    }

    existProject = projectService.existCenterProject(projectID);

    // Validate if the section exists.
    List<String> sections = new ArrayList<>();
    sections.add("projectDescription");
    sections.add("projectPartners");
    sections.add("deliverableList");

    validSection = sections.contains(sectionName);
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setSection(Map<String, Object> section) {
    this.section = section;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }

  public void validateDeliverables() {
    CenterProject project = projectService.getCenterProjectById(projectID);

    if (project != null) {

      CenterProgram program = project.getResearchProgram();

      List<CenterDeliverable> deliverables =
        new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

      if (deliverables != null && !deliverables.isEmpty()) {
        for (CenterDeliverable deliverable : deliverables) {

          deliverable.setDocuments(new ArrayList<>(
            deliverable.getDeliverableDocuments().stream().filter(dd -> dd.isActive()).collect(Collectors.toList())));

          deliverableValidator.validate(this, deliverable, project, program, false);
        }
      }

    }
  }

  public void validateDescription() {

    CenterProject project = projectService.getCenterProjectById(projectID);

    if (project != null) {

      CenterProgram program = project.getResearchProgram();

      CenterProjectCrosscutingTheme crosscutingTheme;
      if (this.isEditable()) {
        crosscutingTheme = projectCrosscutingThemeService.getProjectCrosscutingThemeById(project.getId());
      } else {
        crosscutingTheme = project.getProjectCrosscutingTheme();
      }
      project.setProjectCrosscutingTheme(crosscutingTheme);

      project.setOutputs(
        new ArrayList<>(project.getProjectOutputs().stream().filter(po -> po.isActive()).collect(Collectors.toList())));

      project.setFundingSources(new ArrayList<>(
        project.getProjectFundingSources().stream().filter(fs -> fs.isActive()).collect(Collectors.toList())));

      descriptionValidator.validate(this, project, program, false);


    }

  }

  public void validatePartners() {
    CenterProject project = projectService.getCenterProjectById(projectID);

    if (project != null) {

      CenterProgram program = project.getResearchProgram();

      project.setPartners(new ArrayList<>(
        project.getProjectPartners().stream().filter(pp -> pp.isActive()).collect(Collectors.toList())));

      if (project.getPartners() != null || !project.getPartners().isEmpty()) {
        for (CenterProjectPartner partner : project.getPartners()) {
          partner.setUsers(new ArrayList<>(
            partner.getProjectPartnerPersons().stream().filter(ppp -> ppp.isActive()).collect(Collectors.toList())));
        }
      }

      partnerValidator.validate(this, project, program, false);
    }
  }

}
