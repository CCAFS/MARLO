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


import org.cgiar.ccafs.marlo.data.dao.StudyTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.StudyType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class StudyTypeManagerImpl implements StudyTypeManager {


  private StudyTypeDAO studyTypeDAO;
  // Managers


  @Inject
  public StudyTypeManagerImpl(StudyTypeDAO studyTypeDAO) {
    this.studyTypeDAO = studyTypeDAO;


  }

  @Override
  public void deleteStudyType(long studyTypeId) {

    studyTypeDAO.deleteStudyType(studyTypeId);
  }

  @Override
  public boolean existStudyType(long studyTypeID) {

    return studyTypeDAO.existStudyType(studyTypeID);
  }

  @Override
  public List<StudyType> findAll() {

    return studyTypeDAO.findAll();

  }

  @Override
  public StudyType getStudyTypeById(long studyTypeID) {

    return studyTypeDAO.find(studyTypeID);
  }

  @Override
  public StudyType saveStudyType(StudyType studyType) {

    return studyTypeDAO.save(studyType);
  }


}
