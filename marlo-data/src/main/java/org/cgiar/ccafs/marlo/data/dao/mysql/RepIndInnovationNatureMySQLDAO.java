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

import org.cgiar.ccafs.marlo.data.dao.RepIndInnovationNatureDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationNature;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndInnovationNatureMySQLDAO extends AbstractMarloDAO<RepIndInnovationNature, Long> implements RepIndInnovationNatureDAO {


  @Inject
  public RepIndInnovationNatureMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndInnovationNature(long repIndInnovationNatureId) {
    RepIndInnovationNature repIndInnovationNature = this.find(repIndInnovationNatureId);
    repIndInnovationNature.setActive(false);
    this.update(repIndInnovationNature);
  }

  @Override
  public boolean existRepIndInnovationNature(long repIndInnovationNatureID) {
    RepIndInnovationNature repIndInnovationNature = this.find(repIndInnovationNatureID);
    if (repIndInnovationNature == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndInnovationNature find(long id) {
    return super.find(RepIndInnovationNature.class, id);

  }

  @Override
  public List<RepIndInnovationNature> findAll() {
    String query = "from " + RepIndInnovationNature.class.getName() + " where is_active=1";
    List<RepIndInnovationNature> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndInnovationNature save(RepIndInnovationNature repIndInnovationNature) {
    if (repIndInnovationNature.getId() == null) {
      super.saveEntity(repIndInnovationNature);
    } else {
      repIndInnovationNature = super.update(repIndInnovationNature);
    }


    return repIndInnovationNature;
  }


}