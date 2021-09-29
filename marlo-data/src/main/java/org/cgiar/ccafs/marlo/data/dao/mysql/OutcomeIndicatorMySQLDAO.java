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

import org.cgiar.ccafs.marlo.data.dao.OutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.OutcomeIndicator;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;


public class OutcomeIndicatorMySQLDAO extends AbstractMarloDAO<OutcomeIndicator, Long> implements OutcomeIndicatorDAO {

  @Inject
  public OutcomeIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public List<OutcomeIndicator> getAll() {
    String query = "from " + OutcomeIndicator.class.getName();
    List<OutcomeIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public OutcomeIndicator getOutcomeIndicatorById(long id) {
    return super.find(OutcomeIndicator.class, id);
  }

}
