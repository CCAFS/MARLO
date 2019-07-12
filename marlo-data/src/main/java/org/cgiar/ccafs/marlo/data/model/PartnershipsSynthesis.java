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


public class PartnershipsSynthesis implements Serializable {


  private static final long serialVersionUID = 5784584698071980334L;

  private Project project;
  private List<ProjectPartner> partners;

  public List<ProjectPartner> getPartners() {
    return partners;
  }

  public Project getProject() {
    return project;
  }

  public void setPartners(List<ProjectPartner> partners) {
    this.partners = partners;
  }

  public void setProject(Project project) {
    this.project = project;
  }

}
