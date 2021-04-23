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

package org.cgiar.ccafs.marlo.rest.services.deliverables.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class MetadataGardianModel implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 9000696391982171809L;

  private String findability;
  private String accessibility;
  private String interoperability;
  private String reusability;
  private String title;


  public MetadataGardianModel() {
  }

  public String getAccessibility() {
    return accessibility;
  }

  public String getFindability() {
    return findability;
  }

  public String getInteroperability() {
    return interoperability;
  }

  public String getReusability() {
    return reusability;
  }

  public String getTitle() {
    return title;
  }

  public void setAccessibility(String accessibility) {
    this.accessibility = accessibility;
  }

  public void setFindability(String findability) {
    this.findability = findability;
  }

  public void setInteroperability(String interoperability) {
    this.interoperability = interoperability;
  }

  public void setReusability(String reusability) {
    this.reusability = reusability;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}