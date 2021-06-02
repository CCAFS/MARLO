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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ClusterTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ClusterTypeManager;
import org.cgiar.ccafs.marlo.data.model.ClusterType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ClusterTypeManagerImpl implements ClusterTypeManager {


  private ClusterTypeDAO clusterTypeDAO;
  // Managers


  @Inject
  public ClusterTypeManagerImpl(ClusterTypeDAO clusterTypeDAO) {
    this.clusterTypeDAO = clusterTypeDAO;


  }

  @Override
  public void deleteClusterType(long clusterTypeId) {

    clusterTypeDAO.deleteClusterType(clusterTypeId);
  }

  @Override
  public boolean existClusterType(long clusterTypeID) {

    return clusterTypeDAO.existClusterType(clusterTypeID);
  }

  @Override
  public List<ClusterType> findAll() {

    return clusterTypeDAO.findAll();

  }

  @Override
  public ClusterType getClusterTypeById(long clusterTypeID) {

    return clusterTypeDAO.find(clusterTypeID);
  }

  @Override
  public ClusterType saveClusterType(ClusterType clusterType) {

    return clusterTypeDAO.save(clusterType);
  }


}
