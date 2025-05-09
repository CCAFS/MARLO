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

import org.cgiar.ccafs.marlo.data.dao.RepIndOptionsDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndOptions;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndOptionsMySQLDAO extends AbstractMarloDAO<RepIndOptions, Long> implements RepIndOptionsDAO {


  @Inject
  public RepIndOptionsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndOptions(long repIndOptionsId) {
    RepIndOptions repIndOptions = this.find(repIndOptionsId);
    this.delete(repIndOptions);
  }

  @Override
  public boolean existRepIndOptions(long repIndOptionsID) {
    RepIndOptions repIndOptions = this.find(repIndOptionsID);
    if (repIndOptions == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndOptions find(long id) {
    return super.find(RepIndOptions.class, id);

  }

  @Override
  public List<RepIndOptions> findAll() {
    String query = "from " + RepIndOptions.class.getName();
    List<RepIndOptions> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndOptions save(RepIndOptions repIndOptions) {
    if (repIndOptions.getId() == null) {
      super.saveEntity(repIndOptions);
    } else {
      repIndOptions = super.update(repIndOptions);
    }


    return repIndOptions;
  }


}