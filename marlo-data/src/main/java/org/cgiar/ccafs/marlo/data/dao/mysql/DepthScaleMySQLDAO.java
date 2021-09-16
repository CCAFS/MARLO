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

import org.cgiar.ccafs.marlo.data.dao.DepthScaleDAO;
import org.cgiar.ccafs.marlo.data.model.DepthScales;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DepthScaleMySQLDAO extends AbstractMarloDAO<DepthScales, Long> implements DepthScaleDAO {

  @Inject
  public DepthScaleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public DepthScales find(long id) {
    return super.find(DepthScales.class, id);
  }

  @Override
  public List<DepthScales> findAll() {
    String query = "from " + DepthScales.class.getName();
    List<DepthScales> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

}
