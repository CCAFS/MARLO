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

import org.cgiar.ccafs.marlo.data.dao.ClusterTypeDAO;
import org.cgiar.ccafs.marlo.data.model.ClusterType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ClusterTypeMySQLDAO extends AbstractMarloDAO<ClusterType, Long> implements ClusterTypeDAO {


  @Inject
  public ClusterTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteClusterType(long clusterTypeId) {
    ClusterType clusterType = this.find(clusterTypeId);
    this.delete(clusterType);
  }

  @Override
  public boolean existClusterType(long clusterTypeID) {
    ClusterType clusterType = this.find(clusterTypeID);
    if (clusterType == null) {
      return false;
    }
    return true;

  }

  @Override
  public ClusterType find(long id) {
    return super.find(ClusterType.class, id);

  }

  @Override
  public List<ClusterType> findAll() {
    String query = "from " + ClusterType.class.getName();
    List<ClusterType> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public ClusterType save(ClusterType clusterType) {
    if (clusterType.getId() == null) {
      super.saveEntity(clusterType);
    } else {
      clusterType = super.update(clusterType);
    }
    return clusterType;
  }


}