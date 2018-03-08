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
public class PowbTocListDTO implements Serializable {


  private static final long serialVersionUID = -3990241163829321362L;


  private LiaisonInstitution liaisonInstitution;


  private String overall;


  public PowbTocListDTO() {

  }

  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }

  public String getOverall() {
    return overall;
  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setOverall(String overall) {
    this.overall = overall;
  }

}
