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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectedBenefits extends MarloBaseEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // Hibernate Mapping
  private ImpactAreaIndicator impactAreaIndicator;
  private Set<ProjectedBenefitsDepthScale> projectedBenefitsDepthScales = new HashSet<ProjectedBenefitsDepthScale>(0);
  private Set<ProjectedBenefitsWeighting> projectedBenefitsWeighting = new HashSet<ProjectedBenefitsWeighting>(0);


  // Services Mapping
  private List<ProjectedBenefitsDepthScale> depthScales;


  private List<ProjectedBenefitsWeighting> weightingList;


  public List<ProjectedBenefitsDepthScale> getDepthScales() {
    return depthScales;
  }


  public ImpactAreaIndicator getImpactAreaIndicator() {
    return impactAreaIndicator;
  }


  public Set<ProjectedBenefitsDepthScale> getProjectedBenefitsDepthScales() {
    return projectedBenefitsDepthScales;
  }


  public Set<ProjectedBenefitsWeighting> getProjectedBenefitsWeighting() {
    return projectedBenefitsWeighting;
  }

  public List<ProjectedBenefitsWeighting> getWeightingList() {
    return weightingList;
  }

  public void setDepthScales(List<ProjectedBenefitsDepthScale> depthScales) {
    this.depthScales = depthScales;
  }

  public void setImpactAreaIndicator(ImpactAreaIndicator impactAreaIndicator) {
    this.impactAreaIndicator = impactAreaIndicator;
  }

  public void setProjectedBenefitsDepthScales(Set<ProjectedBenefitsDepthScale> projectedBenefitsDepthScales) {
    this.projectedBenefitsDepthScales = projectedBenefitsDepthScales;
  }


  public void setProjectedBenefitsWeighting(Set<ProjectedBenefitsWeighting> projectedBenefitsWeighting) {
    this.projectedBenefitsWeighting = projectedBenefitsWeighting;
  }

  public void setWeightingList(List<ProjectedBenefitsWeighting> weightingList) {
    this.weightingList = weightingList;
  }


}
