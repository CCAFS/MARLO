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

import org.cgiar.ccafs.marlo.data.dao.LocElementRegionDAO;
import org.cgiar.ccafs.marlo.data.model.LocElementRegion;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class LocElementRegionMySQLDAO extends AbstractMarloDAO<LocElementRegion, Long> implements LocElementRegionDAO {

  @Inject
  public LocElementRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteLocElementRegion(long locElementRegionId) {
    LocElementRegion locElementRegion = this.find(locElementRegionId);
    super.delete(locElementRegion.getId());

  }

  @Override
  public boolean existLocElementRegion(long locElementRegionId) {
    LocElementRegion locElementRegion = this.find(locElementRegionId);
    if (locElementRegion == null) {
      return false;
    }
    return true;
  }

  @Override
  public LocElementRegion find(long id) {
    return super.find(LocElementRegion.class, id);
  }

  @Override
  public List<LocElementRegion> findAll() {
    String query = "from " + LocElementRegion.class.getName() + " ";
    List<LocElementRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public LocElementRegion save(LocElementRegion locElementRegion) {
    if (locElementRegion.getId() == null) {
      super.saveEntity(locElementRegion);
    } else {
      locElementRegion = super.update(locElementRegion);
    }
    return locElementRegion;
  }

}
