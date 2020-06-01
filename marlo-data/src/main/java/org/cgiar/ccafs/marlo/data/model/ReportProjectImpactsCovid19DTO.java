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

package org.cgiar.ccafs.marlo.data.model;

import java.util.Map;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class ReportProjectImpactsCovid19DTO implements java.io.Serializable {

  private static final long serialVersionUID = 8299349521582729646L;

  private String projectId;
  private String title;
  private String projectSummary;
  private String projectLeader;
  private String managementLiasion;
  private Map<Integer, String> answer;
  private String projectUrl;
  private String phaseId;
  private String projectLeaderEmail;
  private String managementLiasionAcronym;


  public Map<Integer, String> getAnswer() {
    return answer;
  }


  public String getManagementLiasion() {
    return managementLiasion;
  }


  public String getManagementLiasionAcronym() {
    return managementLiasionAcronym;
  }


  public String getPhaseId() {
    return phaseId;
  }


  public String getProjectId() {
    return projectId;
  }


  public String getProjectLeader() {
    return projectLeader;
  }

  public String getProjectLeaderEmail() {
    return projectLeaderEmail;
  }

  public String getProjectSummary() {
    return projectSummary;
  }

  public String getProjectUrl() {
    return projectUrl;
  }

  public String getTitle() {
    return title;
  }

  public void setAnswer(Map<Integer, String> answer) {
    this.answer = answer;
  }

  public void setManagementLiasion(String managementLiasion) {
    this.managementLiasion = managementLiasion;
  }

  public void setManagementLiasionAcronym(String managementLiasionAcronym) {
    this.managementLiasionAcronym = managementLiasionAcronym;
  }

  public void setPhaseId(String phaseId) {
    this.phaseId = phaseId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public void setProjectLeader(String projectLeader) {
    this.projectLeader = projectLeader;
  }

  public void setProjectLeaderEmail(String projectLeaderEmail) {
    this.projectLeaderEmail = projectLeaderEmail;
  }

  public void setProjectSummary(String projectSummary) {
    this.projectSummary = projectSummary;
  }


  public void setProjectUrl(String projectUrl) {
    this.projectUrl = projectUrl;
  }


  public void setTitle(String title) {
    this.title = title;
  }


}
