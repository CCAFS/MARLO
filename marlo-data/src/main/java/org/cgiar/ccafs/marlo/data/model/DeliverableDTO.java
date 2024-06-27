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


public class DeliverableDTO {

  Long id;

  private String title;


  public Long getId() {
    return id;
  }


  public String getTitle() {
    return title;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  @Override
  public String toString() {
    return "DeliverableDTO [id=" + id + ", title=" + title + "]";
  }


}
