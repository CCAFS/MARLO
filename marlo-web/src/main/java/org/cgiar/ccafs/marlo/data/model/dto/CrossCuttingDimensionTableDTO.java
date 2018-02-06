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


public class CrossCuttingDimensionTableDTO implements Serializable {

  private static final long serialVersionUID = -1392933273584642611L;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  private String genderPrincipal;

  private String genderSignificant;

  private String genderScored;

  private String youthPrincipal;

  private String youthSignificant;

  private String youthScored;

  private String capDevPrincipal;

  private String capDevSignificant;

  private String capDevScored;

  private String total;

  public String getCapDevPrincipal() {
    return capDevPrincipal;
  }

  public String getCapDevScored() {
    return capDevScored;
  }

  public String getCapDevSignificant() {
    return capDevSignificant;
  }

  public String getGenderPrincipal() {
    return genderPrincipal;
  }

  public String getGenderScored() {
    return genderScored;
  }

  public String getGenderSignificant() {
    return genderSignificant;
  }

  public String getTotal() {
    return total;
  }

  public String getYouthPrincipal() {
    return youthPrincipal;
  }

  public String getYouthScored() {
    return youthScored;
  }

  public String getYouthSignificant() {
    return youthSignificant;
  }

  public void setCapDevPrincipal(String capDevPrincipal) {
    this.capDevPrincipal = capDevPrincipal;
  }

  public void setCapDevScored(String capDevScored) {
    this.capDevScored = capDevScored;
  }

  public void setCapDevSignificant(String capDevSignificant) {
    this.capDevSignificant = capDevSignificant;
  }

  public void setGenderPrincipal(String genderPrincipal) {
    this.genderPrincipal = genderPrincipal;
  }

  public void setGenderScored(String genderScored) {
    this.genderScored = genderScored;
  }

  public void setGenderSignificant(String genderSignificant) {
    this.genderSignificant = genderSignificant;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public void setYouthPrincipal(String youthPrincipal) {
    this.youthPrincipal = youthPrincipal;
  }

  public void setYouthScored(String youthScored) {
    this.youthScored = youthScored;
  }

  public void setYouthSignificant(String youthSignificant) {
    this.youthSignificant = youthSignificant;
  }


}
