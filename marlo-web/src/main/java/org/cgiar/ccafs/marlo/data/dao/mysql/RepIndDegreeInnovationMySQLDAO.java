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

import org.cgiar.ccafs.marlo.data.dao.RepIndDegreeInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndDegreeInnovationMySQLDAO extends AbstractMarloDAO<RepIndDegreeInnovation, Long>
  implements RepIndDegreeInnovationDAO {


  @Inject
  public RepIndDegreeInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndDegreeInnovation(long repIndDegreeInnovationId) {
    RepIndDegreeInnovation repIndDegreeInnovation = this.find(repIndDegreeInnovationId);
    this.delete(repIndDegreeInnovation);
  }

  @Override
  public boolean existRepIndDegreeInnovation(long repIndDegreeInnovationID) {
    RepIndDegreeInnovation repIndDegreeInnovation = this.find(repIndDegreeInnovationID);
    if (repIndDegreeInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndDegreeInnovation find(long id) {
    return super.find(RepIndDegreeInnovation.class, id);

  }

  @Override
  public List<RepIndDegreeInnovation> findAll() {
    String query = "from " + RepIndDegreeInnovation.class.getName();
    List<RepIndDegreeInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndDegreeInnovation save(RepIndDegreeInnovation repIndDegreeInnovation) {
    if (repIndDegreeInnovation.getId() == null) {
      super.saveEntity(repIndDegreeInnovation);
    } else {
      repIndDegreeInnovation = super.update(repIndDegreeInnovation);
    }


    return repIndDegreeInnovation;
  }


}