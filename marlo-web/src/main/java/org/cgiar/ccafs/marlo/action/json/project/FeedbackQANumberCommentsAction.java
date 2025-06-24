package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

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

public class FeedbackQANumberCommentsAction extends BaseAction {

  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(FeedbackQACommentsAction.class);
  private List<Map<String, Object>> comments;
  private Long parentId;
  private String sectionName;
  private Long phaseId;
  private String fieldDescription;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;
  private FeedbackQACommentManager commentManager;


  @Inject
  public FeedbackQANumberCommentsAction(APConfig config,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager, FeedbackQACommentManager commentManager) {
    super(config);
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.commentManager = commentManager;
  }

  @Override
  public String execute() throws Exception {
    boolean newModel = true;
    int totalComments = 0, answeredComments = 0;
    comments = new ArrayList<>();
    Map<String, Object> fieldsMap;
    Long fieldId = null;
    List<FeedbackQAComment> feedbackComments = null;
    List<FeedbackQACommentableFields> fields = new ArrayList<>();

    // @param = sectionName/parentID/phaseID/fieldDescription
    if (sectionName != null && parentId != null && phaseId != null && fieldDescription != null) {
      try {
        try {
          fields = feedbackQACommentableFieldsManager.findAllByGlobalUnit(this.getCurrentGlobalUnit().getId()).stream()
            .filter(qa -> qa != null && qa.getSectionName() != null && qa.getSectionName().equals(sectionName)
              && qa.getFieldDescription() != null && qa.getFieldDescription().equals(fieldDescription))
            .collect(Collectors.toList());
        } catch (Exception e) {
          logger.error("unable to get list of fields by section and field description", e);
        }
        if (fields != null && !fields.isEmpty()) {
          for (FeedbackQACommentableFields field : fields) {
            fieldId = field.getId();
            long fieldIdLocal = fieldId;

            // Get comments for field
            if (fieldId != null && commentManager.findAll() != null) {
              feedbackComments = new ArrayList<>();
              try {
                feedbackComments
                  .addAll(commentManager.getFeedbackQACommentsByPhaseAndParentId(phaseId, parentId).stream()
                    .filter(c -> c.getField() != null && c.getField().getId() != null
                      && c.getField().getId().equals(fieldIdLocal) && c.getFeedbackStatus() != null
                      && c.getFeedbackStatus().getId() != null
                      // &&
                      // !c.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Draft.getStatusId()))
                      && !c.getFeedbackStatus().getId()
                        .equals(Long.parseLong(FeedbackStatusEnum.Dismissed.getStatusId())))
                    .collect(Collectors.toList()));
              } catch (Exception e) {
                feedbackComments.addAll(commentManager.findAll().stream()
                  .filter(c -> c.getField() != null && c.getField().getId() != null
                    && c.getField().getId().equals(fieldIdLocal) && c.getPhase() != null && c.getPhase().getId() != null
                    && c.getPhase().getId().equals(phaseId) && c.getParentId() == parentId
                    && c.getFeedbackStatus() != null && c.getFeedbackStatus().getId() != null
                    // && !c.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Draft.getStatusId()))
                    && !c.getFeedbackStatus().getId()
                      .equals(Long.parseLong(FeedbackStatusEnum.Dismissed.getStatusId())))
                  .collect(Collectors.toList()));
              }
            }

            // Get comment without reply and approbation
            if (feedbackComments != null) {
              totalComments = feedbackComments.size();

              try {
                if (newModel) {
                  feedbackComments = feedbackComments.stream().filter(f -> f != null
                    && ((f.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Agreed.getStatusId())))
                      || (f.getFeedbackStatus() != null && f.getFeedbackReplies() != null
                        && !f.getFeedbackReplies().isEmpty())))
                    .collect(Collectors.toList());

                } else {
                  feedbackComments = feedbackComments.stream().filter(f -> f != null
                    && ((f.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Agreed.getStatusId())))
                      || (f.getFeedbackStatus() != null && f.getReply() != null)))
                    .collect(Collectors.toList());
                }
                if (feedbackComments != null) {
                  answeredComments = feedbackComments.size();
                }
              } catch (Exception e) {
                logger.error("unable to get list of filters comments", e);
              }
            }
          }
        }
      } catch (Exception e) {
        logger.error("unable to get feedback comments", e);
        fields = new ArrayList<>();
      }
    }

    fieldsMap = new HashMap<String, Object>();
    fieldsMap.put("totalComments", totalComments);
    fieldsMap.put("answeredComments", answeredComments);
    this.comments.add(fieldsMap);


    return SUCCESS;
  }

  public List<Map<String, Object>> getComments() {
    return comments;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    /*
     * if (parameters.get(APConstants.PARENT_REQUEST_NAME).isDefined()) {
     * parentName = StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_NAME).getMultipleValues()[0]);
     * }
     */
    if (parameters.get(APConstants.PARENT_REQUEST_ID).isDefined()) {
      parentId = Long.parseLong(
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PARENT_REQUEST_ID).getMultipleValues()[0])));
    }
    if (parameters.get(APConstants.SECTION_REQUEST_NAME).isDefined()) {
      sectionName =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.SECTION_REQUEST_NAME).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.PHASE_ID).isDefined()) {
      phaseId =
        Long.parseLong(StringUtils.trim(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0])));
    }
    if (parameters.get(APConstants.FIELD_DESCRIPTION).isDefined()) {
      fieldDescription =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.FIELD_DESCRIPTION).getMultipleValues()[0]));
    }

  }

  public void setComments(List<Map<String, Object>> comments) {
    this.comments = comments;
  }

}
