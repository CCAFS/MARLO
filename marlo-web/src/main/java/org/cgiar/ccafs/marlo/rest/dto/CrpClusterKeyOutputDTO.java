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

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrpClusterKeyOutputDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -4927690813395220371L;
  private Double contribution;
  private CrpClusterOfActivityDTO crpClusterOfActivity;
  private String keyOutput;
  private List<CrpClusterKeyOutputOutcomeDTO> keyOutputOutcomes;
  private Set<DeliverableInfoDTO> deliverables = new HashSet<DeliverableInfoDTO>(0);

  public Double getContribution() {
    return contribution;
  }

  public CrpClusterOfActivityDTO getCrpClusterOfActivity() {
    return crpClusterOfActivity;
  }

  public Set<DeliverableInfoDTO> getDeliverables() {
    return deliverables;
  }

  public String getKeyOutput() {
    return keyOutput;
  }

  public List<CrpClusterKeyOutputOutcomeDTO> getKeyOutputOutcomes() {
    return keyOutputOutcomes;
  }

  public void setContribution(Double contribution) {
    this.contribution = contribution;
  }

  public void setCrpClusterOfActivity(CrpClusterOfActivityDTO crpClusterOfActivity) {
    this.crpClusterOfActivity = crpClusterOfActivity;
  }

  public void setDeliverables(Set<DeliverableInfoDTO> deliverables) {
    this.deliverables = deliverables;
  }

  public void setKeyOutput(String keyOutput) {
    this.keyOutput = keyOutput;
  }

  public void setKeyOutputOutcomes(List<CrpClusterKeyOutputOutcomeDTO> keyOutputOutcomes) {
    this.keyOutputOutcomes = keyOutputOutcomes;
  }

}
