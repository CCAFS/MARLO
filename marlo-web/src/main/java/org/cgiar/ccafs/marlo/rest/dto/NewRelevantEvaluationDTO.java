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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class NewRelevantEvaluationDTO {

  @ApiModelProperty(notes = "Name of the evaluation", position = 1)
  private String nameEvaluation;

  @ApiModelProperty(notes = "Recommendation number (from evaluation)", position = 2)
  private String recomendation;

  @ApiModelProperty(notes = "Text of recommendation", position = 3)
  private String response;

  @ApiModelProperty(notes = "Status of response to this recommendation", position = 4)
  private int status;

  @ApiModelProperty(notes = "Concrete actions taken for this recommendation", position = 4)
  private List<NewRelevantEvaluationActionDTO> actionList;


  @ApiModelProperty(notes = "Link to evidence(s)", position = 6)
  private String evidenceLink;


  @ApiModelProperty(notes = "Aditional comments", position = 7)
  private String comments;

  @ApiModelProperty(notes = "Phase year/section", position = 140)
  private PhaseDTO phase;


  public List<NewRelevantEvaluationActionDTO> getActionList() {
    return actionList;
  }


  public String getComments() {
    return comments;
  }


  public String getEvidenceLink() {
    return evidenceLink;
  }


  public String getNameEvaluation() {
    return nameEvaluation;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public String getRecomendation() {
    return recomendation;
  }


  public String getResponse() {
    return response;
  }


  public int getStatus() {
    return status;
  }


  public void setActionList(List<NewRelevantEvaluationActionDTO> actionList) {
    this.actionList = actionList;
  }


  public void setComments(String comments) {
    this.comments = comments;
  }


  public void setEvidenceLink(String evidenceLink) {
    this.evidenceLink = evidenceLink;
  }


  public void setNameEvaluation(String nameEvaluation) {
    this.nameEvaluation = nameEvaluation;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setRecomendation(String recomendation) {
    this.recomendation = recomendation;
  }


  public void setResponse(String response) {
    this.response = response;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
