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

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class EndOfInitiativeOutcomeDTO {

  private Long initiativeId;
  private String initiativeOfficialCode;
  private String initiativeName;
  private String initiativeStageName;
  private List<InitiativeOutcomeDTO> initiativeOutcomes;

  public Long getInitiativeId() {
    return initiativeId;
  }

  public String getInitiativeName() {
    return initiativeName;
  }

  public String getInitiativeOfficialCode() {
    return initiativeOfficialCode;
  }

  public List<InitiativeOutcomeDTO> getInitiativeOutcomes() {
    return initiativeOutcomes;
  }

  public String getInitiativeStageName() {
    return initiativeStageName;
  }

  public void setInitiativeId(Long initiativeId) {
    this.initiativeId = initiativeId;
  }

  public void setInitiativeName(String initiativeName) {
    this.initiativeName = initiativeName;
  }

  public void setInitiativeOfficialCode(String initiativeOfficialCode) {
    this.initiativeOfficialCode = initiativeOfficialCode;
  }

  public void setInitiativeOutcomes(List<InitiativeOutcomeDTO> initiativeOutcomes) {
    this.initiativeOutcomes = initiativeOutcomes;
  }

  public void setInitiativeStageName(String initiativeStageName) {
    this.initiativeStageName = initiativeStageName;
  }
}
