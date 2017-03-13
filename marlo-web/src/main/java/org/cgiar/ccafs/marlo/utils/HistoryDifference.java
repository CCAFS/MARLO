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


public class HistoryDifference {


  private String difference;
  private String id;
  private boolean added;

  private String oldValue;
  private int index;


  private String newValue;


  public HistoryDifference(String difference) {
    super();
    this.difference = difference;
  }

  public HistoryDifference(String id, String difference, boolean added, String oldValue, String newValue) {
    super();
    this.id = id;
    this.difference = difference;
    this.added = added;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    HistoryDifference other = (HistoryDifference) obj;
    if (difference == null) {
      if (other.difference != null) {
        return false;
      }
    } else if (!difference.equals(other.difference)) {
      return false;
    }
    return true;
  }

  public String getDifference() {
    return difference;
  }

  public String getId() {
    return id;
  }

  public int getIndex() {
    return index;
  }

  public String getNewValue() {
    return newValue;
  }

  public String getOldValue() {
    return oldValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((difference == null) ? 0 : difference.hashCode());
    return result;
  }

  public boolean isAdded() {
    return added;
  }

  public void setAdded(boolean added) {
    this.added = added;
  }

  public void setDifference(String difference) {
    this.difference = difference;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  @Override
  public String toString() {

    return difference;
  }


}
