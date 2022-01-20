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

import org.cgiar.ccafs.marlo.data.dao.OneCGIARInvestmentTypeDAO;
import org.cgiar.ccafs.marlo.data.model.OneCGIARInvestmentType;

import java.util.List;

import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class OneCGIARInvestmentTypeMySQLDAO extends AbstractMarloDAO<OneCGIARInvestmentType, Long>
  implements OneCGIARInvestmentTypeDAO {


  public OneCGIARInvestmentTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public List<OneCGIARInvestmentType> getAll() {
    String query = "from " + OneCGIARInvestmentType.class.getName();
    List<OneCGIARInvestmentType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public OneCGIARInvestmentType getOneCGIARInvestmentTypeById(long id) {
    return super.find(OneCGIARInvestmentType.class, id);
  }

}
