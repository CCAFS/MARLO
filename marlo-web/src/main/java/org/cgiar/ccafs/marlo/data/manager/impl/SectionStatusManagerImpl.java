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


import org.cgiar.ccafs.marlo.data.dao.SectionStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SectionStatusManagerImpl implements SectionStatusManager {


  private SectionStatusDAO sectionStatusDAO;
  // Managers


  @Inject
  public SectionStatusManagerImpl(SectionStatusDAO sectionStatusDAO) {
    this.sectionStatusDAO = sectionStatusDAO;


  }

  @Override
  public boolean deleteSectionStatus(long sectionStatusId) {

    return sectionStatusDAO.deleteSectionStatus(sectionStatusId);
  }

  @Override
  public boolean existSectionStatus(long sectionStatusID) {

    return sectionStatusDAO.existSectionStatus(sectionStatusID);
  }

  @Override
  public List<SectionStatus> findAll() {

    return sectionStatusDAO.findAll();

  }

  @Override
  public SectionStatus getSectionStatusByCaseStudy(long caseStudyID, String cycle, int year, String sectionName) {
    return sectionStatusDAO.getSectionStatusByCaseStudy(caseStudyID, cycle, year, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByCrpProgam(long crpProgramID, String sectionName) {
    return sectionStatusDAO.getSectionStatusByCrpProgam(crpProgramID, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByDeliverable(long deliverableID, String cycle, int year, String sectionName) {
    return sectionStatusDAO.getSectionStatusByDeliverable(deliverableID, cycle, year, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusById(long sectionStatusID) {

    return sectionStatusDAO.find(sectionStatusID);
  }

  @Override
  public SectionStatus getSectionStatusByProject(long projectID, String cycle, int year, String sectionName) {
    return sectionStatusDAO.getSectionStatusByProject(projectID, cycle, year, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectCofunded(long projectID, String cycle, int year, String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectCofunded(projectID, cycle, year, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusByProjectOutcome(long projectID, String cycle, int year, String sectionName) {
    return sectionStatusDAO.getSectionStatusByProjectOutcome(projectID, cycle, year, sectionName);
  }

  @Override
  public long saveSectionStatus(SectionStatus sectionStatus) {

    return sectionStatusDAO.save(sectionStatus);
  }

}
