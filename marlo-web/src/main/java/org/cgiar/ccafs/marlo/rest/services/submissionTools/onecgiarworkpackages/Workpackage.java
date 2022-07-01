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

package org.cgiar.ccafs.marlo.rest.services.submissionTools.onecgiarworkpackages;

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

  private Long wp_id;

  private String name;

  private Long initiative_id;

  private String init_official_code;

  private Long stage_id;

  private String results;

  private String pathway_content;

  private String acronym;


  private Long wp_official_code;


  private List<OneCGIARWorkpackageScienceGroup> scienceGroupList;


  private List<OneCGIARWorkpackageSdg> sdgList;

  // notmapped

  private List<OneCGIARWorkpackageImpactArea> impactAreaList;

  public String getAcronym() {
    return acronym;
  }


  public List<OneCGIARWorkpackageImpactArea> getImpactAreaList() {
    return impactAreaList;
  }


  public String getInit_official_code() {
    return init_official_code;
  }

  public Long getInitiative_id() {
    return initiative_id;
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

  public Long getStage_id() {
    return stage_id;
  }


  public Long getWp_id() {
    return wp_id;
  }

  public Long getWp_official_code() {
    return wp_official_code;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setImpactAreaList(List<OneCGIARWorkpackageImpactArea> impactAreaList) {
    this.impactAreaList = impactAreaList;
  }

  public void setInit_official_code(String init_official_code) {
    this.init_official_code = init_official_code;
  }

  public void setInitiative_id(Long initiative_id) {
    this.initiative_id = initiative_id;
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


  public void setStage_id(Long stage_id) {
    this.stage_id = stage_id;
  }


  public void setWp_id(Long wp_id) {
    this.wp_id = wp_id;
  }


  public void setWp_official_code(Long wp_official_code) {
    this.wp_official_code = wp_official_code;
  }
}
