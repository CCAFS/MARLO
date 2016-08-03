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

  private List<LocElement> countries;

  public CountryLocationLevel() {

  }

  public CountryLocationLevel(String name, List<LocElement> countries) {
    super();
    this.name = name;
    this.countries = countries;
  }

  public List<LocElement> getCountries() {
    return countries;
  }

  public String getName() {
    return name;
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }

  public void setName(String name) {
    this.name = name;
  }


}
