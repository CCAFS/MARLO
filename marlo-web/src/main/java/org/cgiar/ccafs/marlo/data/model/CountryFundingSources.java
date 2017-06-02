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
public class CountryFundingSources implements Serializable {


  private static final long serialVersionUID = 884742598405406519L;


  private LocElement locElement;

  private LocElementType locElementType;


  private List<FundingSource> fundingSources;


  private boolean selected;


  public CountryFundingSources() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    CountryFundingSources other = (CountryFundingSources) obj;
    if (locElement == null) {
      if (other.locElement != null) {
        return false;
      } else {
        if (locElementType != null) {
          if (other.locElementType == null) {
            return false;
          } else {
            if (!locElement.getId().equals(other.getLocElement().getId())) {
              return false;
            }
          }

        }
      }
    } else if (!locElement.getId().equals(other.getLocElement().getId())) {
      return false;
    }
    return true;
  }


  public List<FundingSource> getFundingSources() {
    return fundingSources;
  }


  public LocElement getLocElement() {
    return locElement;
  }


  public LocElementType getLocElementType() {
    return locElementType;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((locElement == null) ? 0 : locElement.hashCode());
    return result;
  }

  public boolean isSelected() {
    return selected;
  }


  public void setFundingSources(List<FundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }

  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
  }

  public void setLocElementType(LocElementType locElementType) {
    this.locElementType = locElementType;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  public String toString() {
    try {
      return this.getLocElement().getName();
    } catch (Exception e) {
      return this.getLocElementType().getName();
    }
  }

}
