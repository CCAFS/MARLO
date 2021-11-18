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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.GlobalTargetDAO;
import org.cgiar.ccafs.marlo.data.model.GlobalTargets;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class GlobalTargetMySQLDAO extends AbstractMarloDAO<GlobalTargets, Long> implements GlobalTargetDAO {

  @Inject
  public GlobalTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<GlobalTargets> getAll() {
    String query = "from " + GlobalTargets.class.getName();
    List<GlobalTargets> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public GlobalTargets getGlobalTargetById(long id) {
    return super.find(GlobalTargets.class, id);
  }

}
