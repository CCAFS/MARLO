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

import java.io.Serializable;
import java.util.List;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class LocationsLevels implements Serializable {

  private static final long serialVersionUID = -8762435022172624443L;

  private String name;

  private List<Object> locations;

  private Object modelClass;


  public LocationsLevels() {

  }

  public LocationsLevels(String name, List<Object> locations, Object modelClass) {
    super();
    this.name = name;
    this.locations = locations;
    this.modelClass = modelClass;
  }


  public List<Object> getLocations() {
    return locations;
  }

  public Object getModelClass() {
    return modelClass;
  }

  public String getName() {
    return name;
  }

  public void setLocations(List<Object> locations) {
    this.locations = locations;
  }

  public void setModelClass(Object modelClass) {
    this.modelClass = modelClass;
  }

  public void setName(String name) {
    this.name = name;
  }


}
