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
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
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

public class FeedbackParentIdAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private final Logger logger = LoggerFactory.getLogger(FeedbackParentIdAction.class);
  private Map<String, Object> parent;
  private String sectionName;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;


  @Inject
  public FeedbackParentIdAction(APConfig config,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager) {
    super(config);
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
  }

  @Override
  public String execute() throws Exception {
    // @param = sectionName

    parent = new HashMap<String, Object>();
    if (sectionName != null) {
      FeedbackQACommentableFields field = new FeedbackQACommentableFields();
      // get existing object from database
      try {

        List<FeedbackQACommentableFields> fields = new ArrayList<FeedbackQACommentableFields>();
        fields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(
            f -> f != null && f.getSectionName() != null && f.getSectionName().equals(sectionName) && f.isActive())
          .collect(Collectors.toList());
        if (fields != null && !fields.isEmpty() && fields.get(0) != null) {
          field = fields.get(0);
        }
      } catch (Exception e) {
        logger.error("unable to get existing Feedback QA commentable object from DB", e);
      }

      if (field.getParentFieldIdentifier() != null) {
        parent.put("parentField", field.getParentFieldIdentifier());
      } else {
        parent.put("parentField", "");
      }
      if (field.getParentFieldDescription() != null) {
        parent.put("parentDescription", field.getParentFieldDescription());
      } else {
        parent.put("parentDescription", "");
      }
    } else {
      parent.put("parentDescription", "");
    }

    return SUCCESS;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    try {
      if (parameters.get(APConstants.SECTION_NAME).isDefined()) {
        sectionName =
          StringUtils.trim(StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]));
      }
    } catch (Exception e) {
      logger.error("unable to get field Description", e);
    }
  }
}
