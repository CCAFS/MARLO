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

import org.cgiar.ccafs.marlo.data.dao.RepIndTrainingTermDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndTrainingTerm;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndTrainingTermMySQLDAO extends AbstractMarloDAO<RepIndTrainingTerm, Long>
  implements RepIndTrainingTermDAO {


  @Inject
  public RepIndTrainingTermMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndTrainingTerm(long repIndTrainingTermId) {
    RepIndTrainingTerm repIndTrainingTerm = this.find(repIndTrainingTermId);
    this.delete(repIndTrainingTerm);
  }

  @Override
  public boolean existRepIndTrainingTerm(long repIndTrainingTermID) {
    RepIndTrainingTerm repIndTrainingTerm = this.find(repIndTrainingTermID);
    if (repIndTrainingTerm == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndTrainingTerm find(long id) {
    return super.find(RepIndTrainingTerm.class, id);

  }

  @Override
  public List<RepIndTrainingTerm> findAll() {
    String query = "from " + RepIndTrainingTerm.class.getName();
    List<RepIndTrainingTerm> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndTrainingTerm save(RepIndTrainingTerm repIndTrainingTerm) {
    if (repIndTrainingTerm.getId() == null) {
      super.saveEntity(repIndTrainingTerm);
    } else {
      repIndTrainingTerm = super.update(repIndTrainingTerm);
    }


    return repIndTrainingTerm;
  }


}