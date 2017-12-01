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

package org.cgiar.ccafs.marlo.rest.response;

import java.io.Serializable;

public class CrpDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String name;

  private String acronym;

  private Integer category;

  private boolean active;

  private boolean marlo;

  private boolean hasRegions;

  private String modificationJustification;

  public CrpDTO() {

  }

  public String getAcronym() {
    return acronym;
  }


  public Integer getCategory() {
    return category;
  }


  public Long getId() {
    return id;
  }


  public String getModificationJustification() {
    return modificationJustification;
  }


  public String getName() {
    return name;
  }


  public boolean isActive() {
    return active;
  }


  public boolean isHasRegions() {
    return hasRegions;
  }


  public boolean isMarlo() {
    return marlo;
  }


  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setActive(boolean active) {
    this.active = active;
  }


  public void setCategory(Integer category) {
    this.category = category;
  }


  public void setHasRegions(boolean hasRegions) {
    this.hasRegions = hasRegions;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setMarlo(boolean marlo) {
    this.marlo = marlo;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setName(String name) {
    this.name = name;
  }

}
