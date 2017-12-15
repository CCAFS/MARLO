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

public class LocElementDTO {

  private Long id;

  private String isoAlpha2;

  private Long isoNumeric;

  private LocElementTypeDTO locElementType;

  private LocGeopositionDTO locGeoposition;

  private String name;

  private Boolean isSiteIntegration;


  public Long getId() {
    return id;
  }


  public String getIsoAlpha2() {
    return isoAlpha2;
  }


  public Long getIsoNumeric() {
    return isoNumeric;
  }


  public Boolean getIsSiteIntegration() {
    return isSiteIntegration;
  }


  public LocElementTypeDTO getLocElementType() {
    return locElementType;
  }


  public LocGeopositionDTO getLocGeoposition() {
    return locGeoposition;
  }


  public String getName() {
    return name;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setIsoAlpha2(String isoAlpha2) {
    this.isoAlpha2 = isoAlpha2;
  }


  public void setIsoNumeric(Long isoNumeric) {
    this.isoNumeric = isoNumeric;
  }


  public void setIsSiteIntegration(Boolean isSiteIntegration) {
    this.isSiteIntegration = isSiteIntegration;
  }


  public void setLocElementType(LocElementTypeDTO locElementType) {
    this.locElementType = locElementType;
  }


  public void setLocGeoposition(LocGeopositionDTO locGeoposition) {
    this.locGeoposition = locGeoposition;
  }


  public void setName(String name) {
    this.name = name;
  }

}
