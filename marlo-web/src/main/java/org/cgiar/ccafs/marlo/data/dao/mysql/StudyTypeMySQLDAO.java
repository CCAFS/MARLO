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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.StudyTypeDAO;
import org.cgiar.ccafs.marlo.data.model.StudyType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class StudyTypeMySQLDAO extends AbstractMarloDAO<StudyType, Long> implements StudyTypeDAO {


  @Inject
  public StudyTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteStudyType(long studyTypeId) {
    StudyType studyType = this.find(studyTypeId);
    this.delete(studyType);
  }

  @Override
  public boolean existStudyType(long studyTypeID) {
    StudyType studyType = this.find(studyTypeID);
    if (studyType == null) {
      return false;
    }
    return true;

  }

  @Override
  public StudyType find(long id) {
    return super.find(StudyType.class, id);

  }

  @Override
  public List<StudyType> findAll() {
    String query = "from " + StudyType.class.getName();
    List<StudyType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public StudyType save(StudyType studyType) {
    if (studyType.getId() == null) {
      super.saveEntity(studyType);
    } else {
      studyType = super.update(studyType);
    }


    return studyType;
  }


}