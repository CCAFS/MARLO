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

import org.cgiar.ccafs.marlo.data.dao.RepIndRegionDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndRegionMySQLDAO extends AbstractMarloDAO<RepIndRegion, Long> implements RepIndRegionDAO {


  @Inject
  public RepIndRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndRegion(long repIndRegionId) {
    RepIndRegion repIndRegion = this.find(repIndRegionId);
    this.delete(repIndRegion);
  }

  @Override
  public boolean existRepIndRegion(long repIndRegionID) {
    RepIndRegion repIndRegion = this.find(repIndRegionID);
    if (repIndRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndRegion find(long id) {
    return super.find(RepIndRegion.class, id);

  }

  @Override
  public List<RepIndRegion> findAll() {
    String query = "from " + RepIndRegion.class.getName();
    List<RepIndRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndRegion save(RepIndRegion repIndRegion) {
    if (repIndRegion.getId() == null) {
      super.saveEntity(repIndRegion);
    } else {
      repIndRegion = super.update(repIndRegion);
    }


    return repIndRegion;
  }


}