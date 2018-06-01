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

import java.util.List;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ReportSynthesisCrossCuttingAssetDTO implements java.io.Serializable {


  private static final long serialVersionUID = 2863794067938455253L;


  private List<LiaisonInstitution> liaisonInstitutions;


  private DeliverableIntellectualAsset deliverableIntellectualAsset;


  public DeliverableIntellectualAsset getDeliverableIntellectualAsset() {
    return deliverableIntellectualAsset;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public void setDeliverableIntellectualAsset(DeliverableIntellectualAsset deliverableIntellectualAsset) {
    this.deliverableIntellectualAsset = deliverableIntellectualAsset;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

}
