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


import org.cgiar.ccafs.marlo.data.dao.CrpProgramLeaderDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpProgramLeaderManagerImpl implements CrpProgramLeaderManager {


  private CrpProgramLeaderDAO crpProgramLeaderDAO;
  // Managers


  @Inject
  public CrpProgramLeaderManagerImpl(CrpProgramLeaderDAO crpProgramLeaderDAO) {
    this.crpProgramLeaderDAO = crpProgramLeaderDAO;


  }

  @Override
  public void deleteCrpProgramLeader(long crpProgramLeaderId) {

    crpProgramLeaderDAO.deleteCrpProgramLeader(crpProgramLeaderId);
  }

  @Override
  public boolean existCrpProgramLeader(long crpProgramLeaderID) {

    return crpProgramLeaderDAO.existCrpProgramLeader(crpProgramLeaderID);
  }

  @Override
  public List<CrpProgramLeader> findAll() {

    return crpProgramLeaderDAO.findAll();

  }

  @Override
  public CrpProgramLeader getCrpProgramLeaderById(long crpProgramLeaderID) {

    return crpProgramLeaderDAO.find(crpProgramLeaderID);
  }

  @Override
  public CrpProgramLeader saveCrpProgramLeader(CrpProgramLeader crpProgramLeader) {

    return crpProgramLeaderDAO.save(crpProgramLeader);
  }


}
