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

package org.cgiar.ccafs.marlo.data.dto;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class InnovationHomeDTO {

  private long innovationId;
  private long expectedYear;
  private long projectId;
  private String innovationType;
  private String innovationTitle;
  private String innovationStage;

  public InnovationHomeDTO() {
  }

  public InnovationHomeDTO(long innovationId, long expectedYear, long projectId, String innovationType,
    String innovationTitle, String innovationStage) {
    super();
    this.innovationId = innovationId;
    this.expectedYear = expectedYear;
    this.projectId = projectId;
    this.innovationType = innovationType;
    this.innovationTitle = innovationTitle;
    this.innovationStage = innovationStage;
  }

  public long getExpectedYear() {
    return expectedYear;
  }

  public long getInnovationId() {
    return innovationId;
  }

  public String getInnovationStage() {
    return innovationStage;
  }

  public String getInnovationTitle() {
    return innovationTitle;
  }

  public String getInnovationType() {
    return innovationType;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setExpectedYear(long expectedYear) {
    this.expectedYear = expectedYear;
  }

  public void setInnovationId(long innovationId) {
    this.innovationId = innovationId;
  }

  public void setInnovationStage(String innovationStage) {
    this.innovationStage = innovationStage;
  }

  public void setInnovationTitle(String innovationTitle) {
    this.innovationTitle = innovationTitle;
  }

  public void setInnovationType(String innovationType) {
    this.innovationType = innovationType;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

}