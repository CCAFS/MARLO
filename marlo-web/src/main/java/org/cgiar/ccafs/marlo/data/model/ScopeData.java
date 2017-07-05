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

import java.io.Serializable;
import java.util.List;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ScopeData implements Serializable {


  private static final long serialVersionUID = -2962967650419813034L;

  Long id;

  LocElementType scopeRegion;

  List<LocElement> scopeElements;

  int size;

  public Long getId() {
    return id;
  }

  public List<LocElement> getScopeElements() {
    return scopeElements;
  }

  public LocElementType getScopeRegion() {
    return scopeRegion;
  }

  public int getSize() {
    return size;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setScopeElements(List<LocElement> scopeElements) {
    this.scopeElements = scopeElements;
  }

  public void setScopeRegion(LocElementType scopeRegion) {
    this.scopeRegion = scopeRegion;
  }

  public void setSize(int size) {
    this.size = size;
  }


}
