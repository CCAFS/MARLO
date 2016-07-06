/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
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
  public SectionStatus getSectionStatusByCrpProgam(long crpProgramID, String sectionName) {
    return sectionStatusDAO.getSectionStatusByCrpProgam(crpProgramID, sectionName);
  }

  @Override
  public SectionStatus getSectionStatusById(long sectionStatusID) {

    return sectionStatusDAO.find(sectionStatusID);
  }

  @Override
  public long saveSectionStatus(SectionStatus sectionStatus) {

    return sectionStatusDAO.save(sectionStatus);
  }

}
