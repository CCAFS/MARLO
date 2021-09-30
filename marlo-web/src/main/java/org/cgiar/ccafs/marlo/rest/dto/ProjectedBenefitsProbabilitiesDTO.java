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


public class ProjectedBenefitsProbabilitiesDTO {

  private Long probabilityID;
  private String probabilityName;
  private String probabilityDescription;

  public String getProbabilityDescription() {
    return probabilityDescription;
  }

  public Long getProbabilityID() {
    return probabilityID;
  }

  public String getProbabilityName() {
    return probabilityName;
  }

  public void setProbabilityDescription(String probabilityDescription) {
    this.probabilityDescription = probabilityDescription;
  }

  public void setProbabilityID(Long probabilityID) {
    this.probabilityID = probabilityID;
  }

  public void setProbabilityName(String probabilityName) {
    this.probabilityName = probabilityName;
  }


}
