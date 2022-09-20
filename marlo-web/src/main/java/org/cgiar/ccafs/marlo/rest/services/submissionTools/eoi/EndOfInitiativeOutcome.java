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

package org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi;

import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class EndOfInitiativeOutcome {

  private Long initiative_id;
  private String initiative_official_code;
  private String initiative_name;
  private String stage_name;
  private List<InitiativeOutcome> eoi_o;


  public List<InitiativeOutcome> getEoi_o() {
    return eoi_o;
  }

  public Long getInitiative_id() {
    return initiative_id;
  }

  public String getInitiative_name() {
    return initiative_name;
  }

  public String getInitiative_official_code() {
    return initiative_official_code;
  }

  public String getStage_name() {
    return stage_name;
  }

  public void setEoi_o(List<InitiativeOutcome> eoi_o) {
    this.eoi_o = eoi_o;
  }

  public void setInitiative_id(Long initiative_id) {
    this.initiative_id = initiative_id;
  }

  public void setInitiative_name(String initiative_name) {
    this.initiative_name = initiative_name;
  }

  public void setInitiative_official_code(String initiative_official_code) {
    this.initiative_official_code = initiative_official_code;
  }

  public void setStage_name(String stage_name) {
    this.stage_name = stage_name;
  }
}
