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

import org.cgiar.ccafs.marlo.data.dao.ValueChainDAO;
import org.cgiar.ccafs.marlo.data.model.ValueChain;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ValueChainMySQLDAO extends AbstractMarloDAO<ValueChain, Long> implements ValueChainDAO {

  @Inject
  public ValueChainMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public ValueChain find(long id) {
    return super.find(ValueChain.class, id);
  }

  @Override
  public List<ValueChain> findAll() {
    String query = "from " + ValueChain.class.getName() + " where is_active=1 order by sort_order, id";
    return super.findAll(query);
  }
}
