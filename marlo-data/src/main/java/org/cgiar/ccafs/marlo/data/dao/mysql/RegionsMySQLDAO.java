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

import org.cgiar.ccafs.marlo.data.dao.RegionDAO;
import org.cgiar.ccafs.marlo.data.model.Region;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class RegionsMySQLDAO extends AbstractMarloDAO<Region, Long> implements RegionDAO {

  @Inject
  public RegionsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRegion(long regionId) {
    Region region = this.find(regionId);
    super.delete(region.getId());
  }

  @Override
  public boolean existRegion(long regionId) {
    Region region = this.find(regionId);
    if (region == null) {
      return false;
    }
    return true;
  }

  @Override
  public Region find(long id) {
    return super.find(Region.class, id);
  }

  @Override
  public List<Region> findAll() {
    String query = "from " + Region.class.getName() + " ";
    List<Region> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public Region getRegionByAcronym(String acronym) {
    String query = "select r from Region r where acronym = :acronym";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("acronym", acronym);

    List<Region> results = super.findAll(createQuery);

    Region oneCGIARAccount = (results != null && !results.isEmpty()) ? results.get(0) : null;

    return oneCGIARAccount;
  }

  @Override
  public Region save(Region region) {
    if (region.getId() == null) {
      super.saveEntity(region);
    } else {
      region = super.update(region);
    }
    return region;
  }

}
