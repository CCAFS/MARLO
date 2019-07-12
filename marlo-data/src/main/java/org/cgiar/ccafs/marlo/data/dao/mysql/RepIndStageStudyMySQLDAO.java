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

import org.cgiar.ccafs.marlo.data.dao.RepIndStageStudyDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndStageStudyMySQLDAO extends AbstractMarloDAO<RepIndStageStudy, Long> implements RepIndStageStudyDAO {


  @Inject
  public RepIndStageStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndStageStudy(long repIndStageStudyId) {
    RepIndStageStudy repIndStageStudy = this.find(repIndStageStudyId);
    this.delete(repIndStageStudy);
  }

  @Override
  public boolean existRepIndStageStudy(long repIndStageStudyID) {
    RepIndStageStudy repIndStageStudy = this.find(repIndStageStudyID);
    if (repIndStageStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndStageStudy find(long id) {
    return super.find(RepIndStageStudy.class, id);

  }

  @Override
  public List<RepIndStageStudy> findAll() {
    String query = "from " + RepIndStageStudy.class.getName();
    List<RepIndStageStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndStageStudy save(RepIndStageStudy repIndStageStudy) {
    if (repIndStageStudy.getId() == null) {
      super.saveEntity(repIndStageStudy);
    } else {
      repIndStageStudy = super.update(repIndStageStudy);
    }


    return repIndStageStudy;
  }


}