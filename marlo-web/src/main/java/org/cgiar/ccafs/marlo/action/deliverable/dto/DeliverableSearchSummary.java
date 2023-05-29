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

package org.cgiar.ccafs.marlo.action.deliverable.dto;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliverableSearchSummary {

  private String disseminationURL;
  private String handle;
  private String DOI;
  private Long phaseID;
  private Long deliverableID;
  private String clusterAcronym;
  private String duplicatedField;
  private String title;
  private String subCategory;
  private String responsible;
  private String clusterLeader;
  private String sharedClusters;
  private String year;
  private String status;

  public DeliverableSearchSummary() {

  }

  public Map<String, Object> convertToMap() {
    ObjectMapper oMapper = new ObjectMapper();

    Map<String, Object> map = oMapper.convertValue(this, Map.class);

    return map;
  }

  public String getClusterAcronym() {
    return clusterAcronym;
  }

  public String getClusterLeader() {
    return clusterLeader;
  }

  public Long getDeliverableID() {
    return deliverableID;
  }

  public String getDisseminationURL() {
    return disseminationURL;
  }

  public String getDOI() {
    return DOI;
  }

  public String getDuplicatedField() {
    return duplicatedField;
  }

  public String getHandle() {
    return handle;
  }

  public Long getPhaseID() {
    return phaseID;
  }

  public String getResponsible() {
    return responsible;
  }

  public String getSharedClusters() {
    return sharedClusters;
  }

  public String getStatus() {
    return status;
  }

  public String getSubCategory() {
    return subCategory;
  }

  public String getTitle() {
    return title;
  }

  public String getYear() {
    return year;
  }

  public void setClusterAcronym(String clusterAcronym) {
    this.clusterAcronym = clusterAcronym;
  }

  public void setClusterLeader(String clusterLeader) {
    this.clusterLeader = clusterLeader;
  }

  public void setDeliverableID(Long deliverableID) {
    this.deliverableID = deliverableID;
  }

  public void setDisseminationURL(String disseminationURL) {
    this.disseminationURL = disseminationURL;
  }

  public void setDOI(String DOI) {
    this.DOI = DOI;
  }

  public void setDuplicatedField(String duplicatedField) {
    this.duplicatedField = duplicatedField;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }

  public void setPhaseID(Long phaseID) {
    this.phaseID = phaseID;
  }

  public void setResponsible(String responsible) {
    this.responsible = responsible;
  }

  public void setSharedClusters(String sharedClusters) {
    this.sharedClusters = sharedClusters;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(String year) {
    this.year = year;
  }
}