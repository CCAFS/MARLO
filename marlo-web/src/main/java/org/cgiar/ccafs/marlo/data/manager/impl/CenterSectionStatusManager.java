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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterSectionStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterSectionStatusManager implements ICenterSectionStatusManager {


  private ICenterSectionStatusDAO sectionStatusDAO;

  // Managers


  @Inject
  public CenterSectionStatusManager(ICenterSectionStatusDAO sectionStatusDAO) {
    this.sectionStatusDAO = sectionStatusDAO;


  }

  @Override
  public void deleteSectionStatus(long sectionStatusId) {

    sectionStatusDAO.deleteSectionStatus(sectionStatusId);
  }

  @Override
  public List<String> distinctSectionStatus(long programID) {
    List<String> status = new ArrayList<String>();

    List<Map<String, Object>> data = sectionStatusDAO.distinctSectionStatus(programID);
    if (data != null) {
      for (Map<String, Object> map : data) {
        status.add(map.get("section_name").toString());
      }
    }

    return status;
  }

  @Override
  public List<String> distinctSectionStatusProject(long projectID) {
    List<String> status = new ArrayList<String>();

    List<Map<String, Object>> data = sectionStatusDAO.distinctSectionStatusProject(projectID);
    if (data != null) {
      for (Map<String, Object> map : data) {
        status.add(map.get("section_name").toString());
      }
    }

    return status;
  }

  @Override
  public boolean existSectionStatus(long sectionStatusID) {

    return sectionStatusDAO.existSectionStatus(sectionStatusID);
  }

  @Override
  public List<CenterSectionStatus> findAll() {

    return sectionStatusDAO.findAll();

  }

  @Override
  public List<CenterSectionStatus> getSectionStatus(long programId, String sectionName) {
    return sectionStatusDAO.getSectionStatus(programId, sectionName);
  }

  @Override
  public CenterSectionStatus getSectionStatusByDeliverable(long deliverableId, long projectId, String sectionName,
    int year) {
    return sectionStatusDAO.getSectionStatusByDeliverable(deliverableId, projectId, sectionName, year);
  }

  @Override
  public CenterSectionStatus getSectionStatusById(long sectionStatusID) {

    return sectionStatusDAO.find(sectionStatusID);
  }

  @Override
  public CenterSectionStatus getSectionStatusByOutcome(long programId, long outcomeId, String sectionName, int year) {
    return sectionStatusDAO.getSectionStatusByOutcome(programId, outcomeId, sectionName, year);
  }

  @Override
  public CenterSectionStatus getSectionStatusByOutput(long programId, long outputId, String sectionName, int year) {
    return sectionStatusDAO.getSectionStatusByOutput(programId, outputId, sectionName, year);
  }

  @Override
  public CenterSectionStatus getSectionStatusByProgram(long programId, String sectionName, int year) {
    return sectionStatusDAO.getSectionStatusByProgram(programId, sectionName, year);
  }

  @Override
  public CenterSectionStatus getSectionStatusByProject(long programId, long projectId, String sectionName, int year) {
    return sectionStatusDAO.getSectionStatusByProject(programId, projectId, sectionName, year);
  }

  @Override
  public List<CenterSectionStatus> getSectionStatussByUserId(Long userId) {
    return sectionStatusDAO.getSectionStatussByUserId(userId);
  }

  @Override
  public CenterSectionStatus saveSectionStatus(CenterSectionStatus sectionStatus) {

    return sectionStatusDAO.save(sectionStatus);
  }


}
