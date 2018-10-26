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

import com.google.gson.annotations.Expose;

/**
 * RepositoryChannel
 * 
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 8:42:04 AM: Creation of class
 */
public class RepositoryChannel extends MarloSoftDeleteableEntity implements java.io.Serializable {

  private static final long serialVersionUID = -1855854307294714464L;

  @Expose
  private String shortName;

  @Expose
  private String name;

  @Expose
  private String urlExample;


  public RepositoryChannel() {
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
    RepositoryChannel other = (RepositoryChannel) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getName() {
    return name;
  }

  public String getShortName() {
    return shortName;
  }

  public String getUrlExample() {
    return urlExample;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setUrlExample(String urlExample) {
    this.urlExample = urlExample;
  }


  @Override
  public String toString() {
    return "RepositoryChannel [id=" + this.getId() + ", shortName=" + shortName + ", name=" + name + ", urlExample= "
      + urlExample + "]";
  }

}
