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

public class PolicyHomeDTO {

  private long policyId;
  private long expectedYear;
  private long projectId;
  private String policyType;
  private String policyTitle;
  private String policyLevel;

  public PolicyHomeDTO() {
  }

  public PolicyHomeDTO(long policyId, long expectedYear, long projectId, String policyType, String policyTitle,
    String policyLevel) {
    super();
    this.policyId = policyId;
    this.expectedYear = expectedYear;
    this.projectId = projectId;
    this.policyType = policyType;
    this.policyTitle = policyTitle;
    this.policyLevel = policyLevel;
  }

  public long getExpectedYear() {
    return expectedYear;
  }

  public long getPolicyId() {
    return policyId;
  }

  public String getPolicyLevel() {
    return policyLevel;
  }

  public String getPolicyTitle() {
    return policyTitle;
  }

  public String getPolicyType() {
    return policyType;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setExpectedYear(long expectedYear) {
    this.expectedYear = expectedYear;
  }

  public void setPolicyId(long policyId) {
    this.policyId = policyId;
  }

  public void setPolicyLevel(String policyLevel) {
    this.policyLevel = policyLevel;
  }

  public void setPolicyTitle(String policyTitle) {
    this.policyTitle = policyTitle;
  }

  public void setPolicyType(String policyType) {
    this.policyType = policyType;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

}