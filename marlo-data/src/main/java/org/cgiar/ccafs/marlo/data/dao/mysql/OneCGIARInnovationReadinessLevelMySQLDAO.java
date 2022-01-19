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

import org.cgiar.ccafs.marlo.data.dao.OneCGIARInnovationReadinessLevelDAO;
import org.cgiar.ccafs.marlo.data.model.OneCGIARInnovationReadinessLevel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class OneCGIARInnovationReadinessLevelMySQLDAO extends AbstractMarloDAO<OneCGIARInnovationReadinessLevel, Long>
  implements OneCGIARInnovationReadinessLevelDAO {

  @Inject
  public OneCGIARInnovationReadinessLevelMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public List<OneCGIARInnovationReadinessLevel> getAll() {
    String query = "from " + OneCGIARInnovationReadinessLevel.class.getName();
    List<OneCGIARInnovationReadinessLevel> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public OneCGIARInnovationReadinessLevel getOneCGIARInnovationReadinessLevelById(long id) {
    return super.find(OneCGIARInnovationReadinessLevel.class, id);
  }

}
