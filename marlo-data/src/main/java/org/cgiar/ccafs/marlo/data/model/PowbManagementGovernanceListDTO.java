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

import java.io.Serializable;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class PowbManagementGovernanceListDTO implements Serializable {

  private static final long serialVersionUID = -2177574215343193880L;


  private LiaisonInstitution liaisonInstitution;


  private String description;


  public PowbManagementGovernanceListDTO() {

  }


  public String getDescription() {
    return description;
  }


  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }


}
