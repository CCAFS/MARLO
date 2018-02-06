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

  private GlobalUnitDTO globlUnit;

  private boolean active;

  public CrossCuttingDimensionDTO() {

  }


  public String getAssets() {
    return assets;
  }


  public GlobalUnitDTO getGloblUnit() {
    return globlUnit;
  }


  public Long getId() {
    return id;
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


  public void setGloblUnit(GlobalUnitDTO globlUnit) {
    this.globlUnit = globlUnit;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setSummarize(String summarize) {
    this.summarize = summarize;
  }


}
