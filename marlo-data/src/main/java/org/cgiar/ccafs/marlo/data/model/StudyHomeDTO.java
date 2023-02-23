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


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class StudyHomeDTO {

  private long studyId;
  private long expectedYear;
  private long projectId;
  private String studyType;
  private String studyTitle;
  private String projectAcronym;

  public StudyHomeDTO() {
  }

  public StudyHomeDTO(long studyId, long expectedYear, long projectId, String studyType, String studyTitle,
    String projectAcronym) {
    super();
    this.studyId = studyId;
    this.expectedYear = expectedYear;
    this.projectId = projectId;
    this.studyType = studyType;
    this.studyTitle = studyTitle;
    this.projectAcronym = projectAcronym;
  }

  public long getExpectedYear() {
    return expectedYear;
  }

  public String getProjectAcronym() {
    return projectAcronym;
  }

  public long getProjectId() {
    return projectId;
  }

  public long getStudyId() {
    return studyId;
  }

  public String getStudyTitle() {
    return studyTitle;
  }

  public String getStudyType() {
    return studyType;
  }

  public void setExpectedYear(long expectedYear) {
    this.expectedYear = expectedYear;
  }

  public void setProjectAcronym(String projectAcronym) {
    this.projectAcronym = projectAcronym;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public void setStudyId(long studyId) {
    this.studyId = studyId;
  }

  public void setStudyTitle(String studyTitle) {
    this.studyTitle = studyTitle;
  }

  public void setStudyType(String studyType) {
    this.studyType = studyType;
  }

}
