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

package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.data.model.LocElement;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CountryLocationLevel implements Serializable {


  private static final long serialVersionUID = 5540168118630349975L;


  private String name;

  private List<LocElement> locElements;


  private List<LocElement> allElements;

  private Long id;

  private boolean isList;


  public CountryLocationLevel() {

  }

  public CountryLocationLevel(String name, List<LocElement> countries) {
    super();
    this.name = name;
    this.locElements = countries;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    CountryLocationLevel other = (CountryLocationLevel) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }

  public List<LocElement> getAllElements() {
    return allElements;
  }

  public Long getId() {
    return id;
  }

  public List<LocElement> getLocElements() {
    return locElements;
  }

  public String getName() {
    return name;
  }


  public boolean isList() {
    return isList;
  }


  public void setAllElements(List<LocElement> allElements) {
    this.allElements = allElements;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setList(boolean isList) {
    this.isList = isList;
  }

  public void setLocElements(List<LocElement> locElements) {
    this.locElements = locElements;
  }

  public void setName(String name) {
    this.name = name;
  }


}
