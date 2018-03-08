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


package org.cgiar.ccafs.marlo.data.model.dto;

import java.io.Serializable;

public class CrossCuttingDimensionDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String summarize;

  private String assets;

  private LiaisonInstitutionDTO liaisonInstitution;

  private PhaseDTO phase;

  private boolean active;

  public CrossCuttingDimensionDTO() {

  }


  public String getAssets() {
    return assets;
  }


  public Long getId() {
    return id;
  }


  public LiaisonInstitutionDTO getLiaisonInstitution() {
    return liaisonInstitution;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public String getSummarize() {
    return summarize;
  }


  public boolean isActive() {
    return active;
  }


  public void setActive(boolean active) {
    this.active = active;
  }

  public void setAssets(String assets) {
    this.assets = assets;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setLiaisonInstitution(LiaisonInstitutionDTO liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setSummarize(String summarize) {
    this.summarize = summarize;
  }


}
