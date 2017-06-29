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


import org.cgiar.ccafs.marlo.data.dao.CrpClusterActivityLeaderDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterActivityLeaderManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpClusterActivityLeaderManagerImpl implements CrpClusterActivityLeaderManager {


  private CrpClusterActivityLeaderDAO crpClusterActivityLeaderDAO;
  // Managers


  @Inject
  public CrpClusterActivityLeaderManagerImpl(CrpClusterActivityLeaderDAO crpClusterActivityLeaderDAO) {
    this.crpClusterActivityLeaderDAO = crpClusterActivityLeaderDAO;


  }

  @Override
  public boolean deleteCrpClusterActivityLeader(long crpClusterActivityLeaderId) {

    return crpClusterActivityLeaderDAO.deleteCrpClusterActivityLeader(crpClusterActivityLeaderId);
  }

  @Override
  public boolean existCrpClusterActivityLeader(long crpClusterActivityLeaderID) {

    return crpClusterActivityLeaderDAO.existCrpClusterActivityLeader(crpClusterActivityLeaderID);
  }

  @Override
  public List<CrpClusterActivityLeader> findAll() {

    return crpClusterActivityLeaderDAO.findAll();

  }

  @Override
  public CrpClusterActivityLeader getCrpClusterActivityLeaderById(long crpClusterActivityLeaderID) {

    return crpClusterActivityLeaderDAO.find(crpClusterActivityLeaderID);
  }

  @Override
  public long saveCrpClusterActivityLeader(CrpClusterActivityLeader crpClusterActivityLeader) {

    return crpClusterActivityLeaderDAO.save(crpClusterActivityLeader);
  }


}
