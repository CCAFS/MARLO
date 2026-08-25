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
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FeedbackManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private static final Logger LOG = LoggerFactory.getLogger(FeedbackManagementAction.class);

  private List<FeedbackQACommentableFields> feedbackFields;
  private List<String> projectSections;

  /** Comment usage per commentable field id, each entry describing one project/phase and its comment count. */
  private Map<Long, List<Map<String, Object>>> fieldUsage;

  /** Total comment count per commentable field id. Drives both the relations button and the delete rule. */
  private Map<Long, Long> fieldUsageTotals;

  private final FeedbackQACommentableFieldsManager fieldsManager;
  private final FeedbackQACommentManager commentManager;
  private final ProjectManager projectManager;
  private final PhaseManager phaseManager;


  @Inject
  public FeedbackManagementAction(APConfig config, FeedbackQACommentableFieldsManager fieldsManager,
    FeedbackQACommentManager commentManager, ProjectManager projectManager, PhaseManager phaseManager) {
    super(config);
    this.fieldsManager = fieldsManager;
    this.commentManager = commentManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
  }

  /**
   * Whether a commentable field can be deleted. A field with comments cannot: `feedback_qa_comments.field_id` is
   * declared `ON DELETE RESTRICT`, so the database rejects it regardless of the comment status. The rule therefore
   * blocks on any comment, and is scoped to the current global unit by the usage query itself.
   *
   * @param fieldId the commentable field identifier
   * @return true when the field has no comments in the current global unit
   */
  public boolean canDeleteFeedbackField(Long fieldId) {
    return this.getFieldUsageTotal(fieldId) == 0L;
  }

  /**
   * The project/phase breakdown of the comments left on one commentable field.
   *
   * @param fieldId the commentable field identifier
   * @return the usage rows, never null
   */
  public List<Map<String, Object>> getFieldUsage(Long fieldId) {
    if (fieldId == null || fieldUsage == null) {
      return new ArrayList<>();
    }
    return fieldUsage.getOrDefault(fieldId, new ArrayList<>());
  }

  /**
   * How many comments were left on one commentable field, within the current global unit.
   *
   * @param fieldId the commentable field identifier
   * @return the comment count, zero when the field is unused
   */
  public long getFieldUsageTotal(Long fieldId) {
    if (fieldId == null || fieldUsageTotals == null) {
      return 0L;
    }
    return fieldUsageTotals.getOrDefault(fieldId, 0L);
  }

  public List<FeedbackQACommentableFields> getFeedbackFields() {
    return feedbackFields;
  }

  public List<String> getProjectSections() {
    return projectSections;
  }

  @Override
  public void prepare() throws Exception {
    ProjectSectionsEnum[] projectSectionsArray = ProjectSectionsEnum.values();
    projectSections = new ArrayList<>();
    if (projectSectionsArray != null && (projectSectionsArray.length > 0)) {
      for (ProjectSectionsEnum section : projectSectionsArray) {
        if (section != null && section.getStatus() != null) {
          projectSections.add(section.getStatus());
        }
      }
    }

    this.loadFieldUsage();

    if (!this.isHttpPost()) {
      // For GET: Load feedback fields from DB
      feedbackFields = fieldsManager.findAllByGlobalUnit(this.getCurrentGlobalUnit().getId());
    } else {
      // For POST: Will bind feedback fields manually from request in save()
      feedbackFields = new ArrayList<>();
    }
  }

  /**
   * Loads, in a single aggregate query, how many comments each commentable field of the current global unit has and
   * which project/phase they belong to. Runs on POST too, because save() needs the totals to refuse deleting a
   * field that is in use.
   */
  private void loadFieldUsage() {
    fieldUsage = new LinkedHashMap<>();
    fieldUsageTotals = new HashMap<>();

    if (this.getCurrentGlobalUnit() == null || this.getCurrentGlobalUnit().getId() == null) {
      LOG.warn("Current global unit is null, feedback field usage cannot be loaded");
      return;
    }

    List<Map<String, Object>> rows;
    try {
      rows = commentManager.getUsageByCommentableFieldAndGlobalUnit(this.getCurrentGlobalUnit().getId());
    } catch (Exception e) {
      LOG.error("unable to load the feedback field usage of global unit {}", this.getCurrentGlobalUnit().getId(), e);
      return;
    }

    if (rows == null) {
      return;
    }

    Map<Long, String> projectLabels = new HashMap<>();
    Map<Long, String> phaseLabels = new HashMap<>();

    for (Map<String, Object> row : rows) {
      Long fieldId = this.toLong(row.get("field_id"));
      if (fieldId == null) {
        continue;
      }

      Long projectId = this.toLong(row.get("project_id"));
      Long phaseId = this.toLong(row.get("phase_id"));
      long total = this.toLong(row.get("total")) != null ? this.toLong(row.get("total")) : 0L;

      Map<String, Object> usage = new HashMap<>();
      usage.put("projectId", projectId);
      usage.put("projectLabel", this.resolveProjectLabel(projectId, projectLabels));
      usage.put("phaseLabel", this.resolvePhaseLabel(phaseId, phaseLabels));
      usage.put("total", total);
      usage.put("link", this.toLink(row.get("link")));

      fieldUsage.computeIfAbsent(fieldId, k -> new ArrayList<>()).add(usage);
      fieldUsageTotals.merge(fieldId, total, Long::sum);
    }
  }

  private String resolvePhaseLabel(Long phaseId, Map<Long, String> cache) {
    if (phaseId == null) {
      return this.getText("feedbackManagement.usage.unknownPhase");
    }
    return cache.computeIfAbsent(phaseId, id -> {
      try {
        Phase phase = phaseManager.getPhaseById(id);
        if (phase != null) {
          return phase.getDescription() + " " + phase.getYear();
        }
      } catch (Exception e) {
        LOG.error("unable to resolve the phase {}", id, e);
      }
      return this.getText("feedbackManagement.usage.unknownPhase");
    });
  }

  private String resolveProjectLabel(Long projectId, Map<Long, String> cache) {
    if (projectId == null) {
      return this.getText("feedbackManagement.usage.unknownProject");
    }
    return cache.computeIfAbsent(projectId, id -> {
      try {
        Project project = projectManager.getProjectById(id);
        if (project != null) {

          /*
           * Clusters are identified by their acronym ("Senegal", "Theme 2", "WA"), which is what an administrator
           * recognises. Only when it is missing do we fall back to the id plus the phase title.
           */
          if (project.getAcronym() != null && !project.getAcronym().trim().isEmpty()) {
            return project.getAcronym().trim();
          }

          ProjectInfo info = project.getProjecInfoPhase(this.getActualPhase());
          String title = info != null && info.getTitle() != null ? info.getTitle() : "";
          return ("C" + id + (title.isEmpty() ? "" : " - " + title)).trim();
        }
      } catch (Exception e) {
        LOG.error("unable to resolve the project {}", id, e);
      }
      return "C" + id;
    });
  }

  /**
   * Returns the stored comment link as it is held in the database.
   * <p>
   * `feedback_qa_comments.link` already holds the absolute URL that SaveFeedbackCommentsAction wrote when the
   * comment was created, so it is offered verbatim; only blank values are discarded.
   *
   * @param storedLink the link as read from the database
   * @return the stored link, or null when there is nothing usable to link to
   */
  private String toLink(Object storedLink) {
    if (storedLink == null) {
      return null;
    }

    String link = storedLink.toString().trim();
    return link.isEmpty() ? null : link;
  }

  private Long toLong(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    try {
      return Long.valueOf(value.toString());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Manually bind feedback fields from HTTP request parameters.
   * This is necessary because Struts 6 cannot automatically populate lists when items are deleted.
   */
  private void bindFeedbackFieldsFromRequest() {
    feedbackFields = new ArrayList<>();
    int index = 0;
    boolean hasMore = true;

    while (hasMore) {
      String idParam = this.getRequest().getParameter("feedbackFields[" + index + "].id");
      String fieldNameParam = this.getRequest().getParameter("feedbackFields[" + index + "].fieldName");
      String fieldDescriptionParam = this.getRequest().getParameter("feedbackFields[" + index + "].fieldDescription");
      String sectionNameParam = this.getRequest().getParameter("feedbackFields[" + index + "].sectionName");
      String sectionDescriptionParam = this.getRequest().getParameter("feedbackFields[" + index + "].sectionDescription");
      String parentFieldIdentifierParam = this.getRequest().getParameter("feedbackFields[" + index + "].parentFieldIdentifier");
      String parentFieldDescriptionParam = this.getRequest().getParameter("feedbackFields[" + index + "].parentFieldDescription");

      boolean hasAnyContent = (idParam != null && !idParam.trim().isEmpty()) ||
        (fieldNameParam != null && !fieldNameParam.trim().isEmpty()) ||
        (fieldDescriptionParam != null && !fieldDescriptionParam.trim().isEmpty()) ||
        (sectionNameParam != null && !sectionNameParam.trim().isEmpty()) ||
        (sectionDescriptionParam != null && !sectionDescriptionParam.trim().isEmpty()) ||
        (parentFieldIdentifierParam != null && !parentFieldIdentifierParam.trim().isEmpty()) ||
        (parentFieldDescriptionParam != null && !parentFieldDescriptionParam.trim().isEmpty());

      if (hasAnyContent) {
        try {
          FeedbackQACommentableFields field = new FeedbackQACommentableFields();

          // ID (can be null for new elements)
          if (idParam != null && !idParam.trim().isEmpty()) {
            try {
              Long id = Long.parseLong(idParam);
              field.setId(id);
            } catch (NumberFormatException e) {
              // Silent fail
            }
          }

          // Field Name
          field.setFieldName(fieldNameParam);

          // Field Description
          field.setFieldDescription(fieldDescriptionParam);

          // Section Name
          field.setSectionName(sectionNameParam);

          // Section Description
          field.setSectionDescription(sectionDescriptionParam);

          // Parent Field Identifier
          field.setParentFieldIdentifier(parentFieldIdentifierParam);

          // Parent Field Description
          field.setParentFieldDescription(parentFieldDescriptionParam);

          feedbackFields.add(field);
          index++;
        } catch (Exception e) {
          hasMore = false;
        }
      } else {
        hasMore = false;
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      // Manually bind feedback fields from request parameters
      bindFeedbackFieldsFromRequest();

      if (feedbackFields != null && !feedbackFields.isEmpty()) {

        List<Long> inputIds = feedbackFields.stream().map(FeedbackQACommentableFields::getId).filter(Objects::nonNull)
          .collect(Collectors.toList());
        List<FeedbackQACommentableFields> existingFieldsBeforeSave =
          fieldsManager.findAllByGlobalUnit(this.getCurrentGlobalUnit().getId());

        // FIRST: Save/update all feedback fields from the form
        for (FeedbackQACommentableFields fields : feedbackFields) {

          // New Activity
          FeedbackQACommentableFields fieldSave = new FeedbackQACommentableFields();

          if (fields.getId() != null) {
            fieldSave = fieldsManager.getInternalQaCommentableFieldsById(fields.getId());
          }
          if (fields.getFieldName() != null) {
            fieldSave.setFieldName(fields.getFieldName());
          }
          if (fields.getFieldDescription() != null) {
            fieldSave.setFieldDescription(fields.getFieldDescription());
          }
          if (fields.getSectionName() != null) {
            fieldSave.setSectionName(fields.getSectionName());
          }
          if (fields.getSectionDescription() != null) {
            fieldSave.setSectionDescription(fields.getSectionDescription());
          }
          if (fields.getParentFieldIdentifier() != null) {
            fieldSave.setParentFieldIdentifier(fields.getParentFieldIdentifier());
          }
          if (fields.getParentFieldDescription() != null) {
            fieldSave.setParentFieldDescription(fields.getParentFieldDescription());
          }

          if (fields.getGlobalUnit() != null) {
            fieldSave.setGlobalUnit(fields.getGlobalUnit());
          } else {
            fieldSave.setGlobalUnit(this.getCurrentGlobalUnit());
          }

          fieldsManager.saveInternalQaCommentableFields(fieldSave);

        }

        // THEN: Delete feedback fields not present in the form
        if (existingFieldsBeforeSave != null && !existingFieldsBeforeSave.isEmpty()) {
          for (FeedbackQACommentableFields activityDB : existingFieldsBeforeSave) {
            if (activityDB.getId() != null && !inputIds.contains(activityDB.getId())) {

              /*
               * A field that already carries comments cannot be removed: feedback_qa_comments.field_id is
               * ON DELETE RESTRICT, so the database would reject it and abort the whole save, losing the edits
               * made to the other rows. Report it instead and keep the field.
               */
              long usage = this.getFieldUsageTotal(activityDB.getId());
              if (usage > 0L) {
                String label = activityDB.getFieldName() != null ? activityDB.getFieldName()
                  : String.valueOf(activityDB.getId());
                this.addActionMessage(
                  this.getText("feedbackManagement.delete.inUse", new String[] {label, String.valueOf(usage)}));
                LOG.warn("Refused to delete commentable field {} because it has {} comments", activityDB.getId(),
                  usage);
                continue;
              }

              try {
                fieldsManager.deleteInternalQaCommentableFields(activityDB.getId());
              } catch (Exception e) {
                LOG.error("unable to delete the commentable field {}", activityDB.getId(), e);
                this.addActionMessage(this.getText("feedbackManagement.delete.failed",
                  new String[] {String.valueOf(activityDB.getId())}));
              }
            }
          }
        }
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        if (this.getInvalidFields() != null && !this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          // this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        this.addActionMessage("message:" + this.getText("saving.saved"));
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

  public void setFeedbackFields(List<FeedbackQACommentableFields> feedbackFields) {
    // Note: This setter is kept for JSP/FTL access but is not used for Struts binding
    // We bind feedback fields manually in save() using bindFeedbackFieldsFromRequest()
    this.feedbackFields = feedbackFields;
  }

  public void setProjectSections(List<String> projectSections) {
    this.projectSections = projectSections;
  }

  @Override
  public void validate() {
    if (save) {
    }
  }
}