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

import io.swagger.annotations.ApiModelProperty;

public class InitiativesDTO {

  @ApiModelProperty(notes = "Initiative ID", position = 1)
  public Long initvStgId;

  public String currentStage;

  public String currentStageName;

  public Long currentStageId;

  public String initiativeName;

  public int initvStageIsActive;

  public String initvStageStatus;

  public Long activeStageId;

  public String activeStageName;

  public String userInitiative;

  public String userInitiativeRole;

  private ConceptDTO concept;


  public Long getActiveStageId() {
    return activeStageId;
  }


  public String getActiveStageName() {
    return activeStageName;
  }

  public ConceptDTO getConcept() {
    return concept;
  }

  public String getCurrentStage() {
    return currentStage;
  }

  public Long getCurrentStageId() {
    return currentStageId;
  }

  public String getCurrentStageName() {
    return currentStageName;
  }

  public String getInitiativeName() {
    return initiativeName;
  }

  public int getInitvStageIsActive() {
    return initvStageIsActive;
  }

  public String getInitvStageStatus() {
    return initvStageStatus;
  }

  public Long getInitvStgId() {
    return initvStgId;
  }

  public String getUserInitiative() {
    return userInitiative;
  }

  public String getUserInitiativeRole() {
    return userInitiativeRole;
  }

  public void setActiveStageId(Long activeStageId) {
    this.activeStageId = activeStageId;
  }

  public void setActiveStageName(String activeStageName) {
    this.activeStageName = activeStageName;
  }

  public void setConcept(ConceptDTO concept) {
    this.concept = concept;
  }

  public void setCurrentStage(String currentStage) {
    this.currentStage = currentStage;
  }

  public void setCurrentStageId(Long currentStageId) {
    this.currentStageId = currentStageId;
  }

  public void setCurrentStageName(String currentStageName) {
    this.currentStageName = currentStageName;
  }

  public void setInitiativeName(String initiativeName) {
    this.initiativeName = initiativeName;
  }

  public void setInitvStageIsActive(int initvStageIsActive) {
    this.initvStageIsActive = initvStageIsActive;
  }

  public void setInitvStageStatus(String initvStageStatus) {
    this.initvStageStatus = initvStageStatus;
  }

  public void setInitvStgId(Long initvStgId) {
    this.initvStgId = initvStgId;
  }

  public void setUserInitiative(String userInitiative) {
    this.userInitiative = userInitiative;
  }

  public void setUserInitiativeRole(String userInitiativeRole) {
    this.userInitiativeRole = userInitiativeRole;
  }
}
