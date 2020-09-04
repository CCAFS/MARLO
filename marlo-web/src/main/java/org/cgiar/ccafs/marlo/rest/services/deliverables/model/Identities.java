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
/**
 * @author Luis Benavides - CIAT/CCAFS
 */
package org.cgiar.ccafs.marlo.rest.services.deliverables.model;

import java.util.List;

public class Identities {

  private String username;
  private List<String> roles;
  private List<String> datasets;

  public List<String> getDatasets() {
    return datasets;
  }

  public List<String> getRoles() {
    return roles;
  }

  public String getUsername() {
    return username;
  }

  public void setDatasets(List<String> datasets) {
    this.datasets = datasets;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public void setUsername(String username) {
    this.username = username;
  }


}
