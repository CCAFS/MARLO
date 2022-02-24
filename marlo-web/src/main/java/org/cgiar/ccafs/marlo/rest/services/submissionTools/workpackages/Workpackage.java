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

package org.cgiar.ccafs.marlo.rest.services.submissionTools.workpackages;

import org.cgiar.ccafs.marlo.data.model.OneCGIARWorkpackageImpactArea;
import org.cgiar.ccafs.marlo.data.model.OneCGIARWorkpackageScienceGroup;
import org.cgiar.ccafs.marlo.data.model.OneCGIARWorkpackageSdg;

import java.io.Serializable;
import java.util.List;

public class Workpackage implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long id;

  private String name;

  private Long initiativeId;

  private Long stageId;

  private String results;

  private String pathway_content;

  private String acronym;

  // notmapped

  private List<OneCGIARWorkpackageScienceGroup> scienceGroupList;

  private List<OneCGIARWorkpackageSdg> sdgList;

  private List<OneCGIARWorkpackageImpactArea> impactAreaList;


  public String getAcronym() {
    return acronym;
  }


  public Long getId() {
    return id;
  }


  public List<OneCGIARWorkpackageImpactArea> getImpactAreaList() {
    return impactAreaList;
  }


  public Long getInitiativeId() {
    return initiativeId;
  }


  public String getName() {
    return name;
  }

  public String getPathway_content() {
    return pathway_content;
  }


  public String getResults() {
    return results;
  }

  public List<OneCGIARWorkpackageScienceGroup> getScienceGroupList() {
    return scienceGroupList;
  }

  public List<OneCGIARWorkpackageSdg> getSdgList() {
    return sdgList;
  }


  public Long getStageId() {
    return stageId;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setImpactAreaList(List<OneCGIARWorkpackageImpactArea> impactAreaList) {
    this.impactAreaList = impactAreaList;
  }


  public void setInitiativeId(Long initiativeId) {
    this.initiativeId = initiativeId;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setPathway_content(String pathway_content) {
    this.pathway_content = pathway_content;
  }


  public void setResults(String results) {
    this.results = results;
  }


  public void setScienceGroupList(List<OneCGIARWorkpackageScienceGroup> scienceGroupList) {
    this.scienceGroupList = scienceGroupList;
  }


  public void setSdgList(List<OneCGIARWorkpackageSdg> sdgList) {
    this.sdgList = sdgList;
  }


  public void setStageId(Long stageId) {
    this.stageId = stageId;
  }
}
