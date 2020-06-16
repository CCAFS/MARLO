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

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Luis Benavides - CIAT/CCAFS
 * @author Diego Perez - CIAT/CCAFS
 */


public class ProjectPageDTO {

  @ApiModelProperty(notes = "Project id", position = 1)
  private Long id;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @ApiModelProperty(notes = "Create date", position = 2)
  private Date createDate;

  @ApiModelProperty(notes = "Project information", position = 3)
  private ProjectInfoDTO projectInfo;

  @ApiModelProperty(notes = "Regional Program", position = 4)
  private List<CrpProgramDTO> regions;

  @ApiModelProperty(notes = "CRP Program", position = 5)
  private List<CrpProgramDTO> flagships;

  @ApiModelProperty(notes = "Project Regions", position = 9)
  private List<DefaultFieldDTO> projectRegions;


  @ApiModelProperty(notes = "Project Countries", position = 10)
  private List<DefaultFieldDTO> projectCountries;

  @ApiModelProperty(notes = "Cluster of Activities", position = 11)
  private List<DefaultFieldStringDTO> activities;

  @ApiModelProperty(notes = "Innovations", position = 12)
  private int numberInnovations;

  @ApiModelProperty(notes = "Deliverables", position = 13)
  private int numberDeliverables;

  @ApiModelProperty(notes = "Studies", position = 14)
  private int numberStudies;

  @ApiModelProperty(notes = "Policies", position = 15)
  private int numberPolicies;

  @ApiModelProperty(notes = "Partners", position = 16)
  private int numberPartners;

  @ApiModelProperty(notes = "Contributing Outcomes and Milestones", position = 17)
  private List<ProjectPageOutcomesDTO> outcomesList;


  @ApiModelProperty(notes = "Expected Studies List", position = 18)
  private List<ProjectPageStudiesDTO> studiesList;

  @ApiModelProperty(notes = "Expected Studies List", position = 19)
  private List<ProjectPagePartnersDTO> partnersList;


  @ApiModelProperty(notes = "Deliverables List", position = 20)
  private List<ProjectPageDeliverablesDTO> deliverablesList;


  @ApiModelProperty(notes = "Innovations List", position = 21)
  private List<ProjectPageInnovationsDTO> innovationsList;


  @ApiModelProperty(notes = "Policies List", position = 22)
  private List<ProjectPagePoliciesDTO> policiesList;


  public List<DefaultFieldStringDTO> getActivities() {
    return activities;
  }


  public Date getCreateDate() {
    return createDate;
  }


  public List<ProjectPageDeliverablesDTO> getDeliverablesList() {
    return deliverablesList;
  }


  public List<CrpProgramDTO> getFlagships() {
    return flagships;
  }


  public Long getId() {
    return id;
  }


  public List<ProjectPageInnovationsDTO> getInnovationsList() {
    return innovationsList;
  }


  public int getNumberDeliverables() {
    return numberDeliverables;
  }


  public int getNumberInnovations() {
    return numberInnovations;
  }


  public int getNumberPartners() {
    return numberPartners;
  }


  public int getNumberPolicies() {
    return numberPolicies;
  }


  public int getNumberStudies() {
    return numberStudies;
  }


  public List<ProjectPageOutcomesDTO> getOutcomesList() {
    return outcomesList;
  }


  public List<ProjectPagePartnersDTO> getPartnersList() {
    return partnersList;
  }


  public List<ProjectPagePoliciesDTO> getPoliciesList() {
    return policiesList;
  }


  public List<DefaultFieldDTO> getProjectCountries() {
    return projectCountries;
  }


  public ProjectInfoDTO getProjectInfo() {
    return projectInfo;
  }


  public List<DefaultFieldDTO> getProjectRegions() {
    return projectRegions;
  }

  public List<CrpProgramDTO> getRegions() {
    return regions;
  }

  public List<ProjectPageStudiesDTO> getStudiesList() {
    return studiesList;
  }

  public void setActivities(List<DefaultFieldStringDTO> activities) {
    this.activities = activities;
  }


  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }


  public void setDeliverablesList(List<ProjectPageDeliverablesDTO> deliverablesList) {
    this.deliverablesList = deliverablesList;
  }


  public void setFlagships(List<CrpProgramDTO> flagships) {
    this.flagships = flagships;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInnovationsList(List<ProjectPageInnovationsDTO> innovationsList) {
    this.innovationsList = innovationsList;
  }


  public void setNumberDeliverables(int numberDeliverables) {
    this.numberDeliverables = numberDeliverables;
  }


  public void setNumberInnovations(int numberInnovations) {
    this.numberInnovations = numberInnovations;
  }

  public void setNumberPartners(int numberPartners) {
    this.numberPartners = numberPartners;
  }

  public void setNumberPolicies(int numberPolicies) {
    this.numberPolicies = numberPolicies;
  }

  public void setNumberStudies(int numberStudies) {
    this.numberStudies = numberStudies;
  }

  public void setOutcomesList(List<ProjectPageOutcomesDTO> outcomesList) {
    this.outcomesList = outcomesList;
  }


  public void setPartnersList(List<ProjectPagePartnersDTO> partnersList) {
    this.partnersList = partnersList;
  }

  public void setPoliciesList(List<ProjectPagePoliciesDTO> policiesList) {
    this.policiesList = policiesList;
  }

  public void setProjectCountries(List<DefaultFieldDTO> projectCountries) {
    this.projectCountries = projectCountries;
  }

  public void setProjectInfo(ProjectInfoDTO projectInfo) {
    this.projectInfo = projectInfo;
  }


  public void setProjectRegions(List<DefaultFieldDTO> projectRegions) {
    this.projectRegions = projectRegions;
  }


  public void setRegions(List<CrpProgramDTO> regions) {
    this.regions = regions;
  }

  public void setStudiesList(List<ProjectPageStudiesDTO> studiesList) {
    this.studiesList = studiesList;
  }

}
