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
import org.cgiar.ccafs.marlo.data.manager.InternalQaCommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.InternalQaCommentableFields;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

public class CommentableFieldsBySectionNameAndParents extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private List<Map<String, String>> sources;
  private List<Project> allProjects;
  private String parentName;
  private String parentId;
  private String sectionName;
  private PhaseManager phaseManager;
  private InternalQaCommentableFieldsManager internalQaCommentableFieldsManager;

  @Inject
  public CommentableFieldsBySectionNameAndParents(APConfig config, PhaseManager phaseManager,
    ProjectManager projectManager, InternalQaCommentableFieldsManager internalQaCommentableFieldsManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.internalQaCommentableFieldsManager = internalQaCommentableFieldsManager;

  }


  @Override
  public String execute() throws Exception {

    sources = new ArrayList<>();


    /**
     * Read only summary objects (not hibernate entities)
     */
    List<InternalQaCommentableFields> summaries;

    summaries = internalQaCommentableFieldsManager.findAll().stream()
      .filter(qa -> qa != null && qa.isActive() && qa.getParentName() != null && qa.getParentName().equals(parentName)
        && qa.getSectionName() != null && qa.getSectionName().equals(sectionName))
      .collect(Collectors.toList());


    for (InternalQaCommentableFields summary : summaries) {

      /*
       * String permission =
       * this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym());
       * boolean hasPermission = this.hasPermissionNoBase(permission);
       * summary.setCanSelect(hasPermission);
       */
      // sources.add(summary.convertToMap());

    }
    return SUCCESS;

  }

  public List<Map<String, String>> getSources() {
    return sources;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    if (parameters.get(APConstants.QUERY_PARAMETER).isDefined()) {
      parentName = StringUtils.trim(parameters.get(APConstants.QUERY_PARAMETER).getMultipleValues()[0]);
    }
    if (parameters.get(APConstants.QUERY_PARAMETER).isDefined()) {
      parentId = StringUtils.trim(StringUtils.trim(parameters.get(APConstants.QUERY_PARAMETER).getMultipleValues()[0]));
    }
    if (parameters.get(APConstants.QUERY_PARAMETER).isDefined()) {
      sectionName =
        StringUtils.trim(StringUtils.trim(parameters.get(APConstants.QUERY_PARAMETER).getMultipleValues()[0]));
    }
  }

  public void setSources(List<Map<String, String>> sources) {
    this.sources = sources;
  }

}
