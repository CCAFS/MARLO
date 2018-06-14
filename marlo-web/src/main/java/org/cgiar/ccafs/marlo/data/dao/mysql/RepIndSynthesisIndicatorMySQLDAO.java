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

import org.cgiar.ccafs.marlo.data.dao.RepIndSynthesisIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndSynthesisIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndSynthesisIndicatorMySQLDAO extends AbstractMarloDAO<RepIndSynthesisIndicator, Long>
  implements RepIndSynthesisIndicatorDAO {


  @Inject
  public RepIndSynthesisIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndSynthesisIndicator(long repIndSynthesisIndicatorId) {
    RepIndSynthesisIndicator repIndSynthesisIndicator = this.find(repIndSynthesisIndicatorId);
    this.delete(repIndSynthesisIndicator);
  }

  @Override
  public boolean existRepIndSynthesisIndicator(long repIndSynthesisIndicatorID) {
    RepIndSynthesisIndicator repIndSynthesisIndicator = this.find(repIndSynthesisIndicatorID);
    if (repIndSynthesisIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndSynthesisIndicator find(long id) {
    return super.find(RepIndSynthesisIndicator.class, id);

  }

  @Override
  public List<RepIndSynthesisIndicator> findAll() {
    String query = "from " + RepIndSynthesisIndicator.class.getName();
    List<RepIndSynthesisIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndSynthesisIndicator save(RepIndSynthesisIndicator repIndSynthesisIndicator) {
    if (repIndSynthesisIndicator.getId() == null) {
      super.saveEntity(repIndSynthesisIndicator);
    } else {
      repIndSynthesisIndicator = super.update(repIndSynthesisIndicator);
    }


    return repIndSynthesisIndicator;
  }


}