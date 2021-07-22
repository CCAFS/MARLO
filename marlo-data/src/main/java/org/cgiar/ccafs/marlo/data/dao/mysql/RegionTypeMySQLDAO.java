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

import org.cgiar.ccafs.marlo.data.dao.RegionTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RegionType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RegionTypeMySQLDAO extends AbstractMarloDAO<RegionType, Long> implements RegionTypeDAO {

  @Inject
  public RegionTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRegionType(long regionTypeId) {
    RegionType regionType = this.find(regionTypeId);
    super.delete(regionType.getId());

  }

  @Override
  public boolean existRegionType(long regionTypeId) {
    RegionType regionType = this.find(regionTypeId);
    if (regionType == null) {
      return false;
    }
    return true;
  }

  @Override
  public RegionType find(long id) {
    return super.find(RegionType.class, id);
  }

  @Override
  public List<RegionType> findAll() {
    String query = "from " + RegionType.class.getName() + " ";
    List<RegionType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public RegionType save(RegionType regionType) {
    if (regionType.getId() == null) {
      super.saveEntity(regionType);
    } else {
      regionType = super.update(regionType);
    }


    return regionType;
  }

}
