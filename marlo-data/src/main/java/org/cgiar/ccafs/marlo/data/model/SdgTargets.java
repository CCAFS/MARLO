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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.model;


public class SdgTargets implements java.io.Serializable {

  private static final long serialVersionUID = 6128814462106164029L;
  private Long id;
  private String target_code;
  private String target;
  private Sdg sdg;

  public SdgTargets() {
    super();
  }


  public SdgTargets(long id, String target_code, String target, Sdg sdg) {
    super();
    this.id = id;
    this.target_code = target_code;
    this.target = target;
    this.sdg = sdg;
  }


  public Long getId() {
    return id;
  }


  public Sdg getSdg() {
    return sdg;
  }


  public String getTarget() {
    return target;
  }


  public String getTarget_code() {
    return target_code;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setSdg(Sdg sdg) {
    this.sdg = sdg;
  }


  public void setTarget(String target) {
    this.target = target;
  }


  public void setTarget_code(String target_code) {
    this.target_code = target_code;
  }


}
