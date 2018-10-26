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


import org.cgiar.ccafs.marlo.data.dao.RepIndPatentStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndPatentStatusManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPatentStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndPatentStatusManagerImpl implements RepIndPatentStatusManager {


  private RepIndPatentStatusDAO repIndPatentStatusDAO;
  // Managers


  @Inject
  public RepIndPatentStatusManagerImpl(RepIndPatentStatusDAO repIndPatentStatusDAO) {
    this.repIndPatentStatusDAO = repIndPatentStatusDAO;


  }

  @Override
  public void deleteRepIndPatentStatus(long repIndPatentStatusId) {

    repIndPatentStatusDAO.deleteRepIndPatentStatus(repIndPatentStatusId);
  }

  @Override
  public boolean existRepIndPatentStatus(long repIndPatentStatusID) {

    return repIndPatentStatusDAO.existRepIndPatentStatus(repIndPatentStatusID);
  }

  @Override
  public List<RepIndPatentStatus> findAll() {

    return repIndPatentStatusDAO.findAll();

  }

  @Override
  public RepIndPatentStatus getRepIndPatentStatusById(long repIndPatentStatusID) {

    return repIndPatentStatusDAO.find(repIndPatentStatusID);
  }

  @Override
  public RepIndPatentStatus saveRepIndPatentStatus(RepIndPatentStatus repIndPatentStatus) {

    return repIndPatentStatusDAO.save(repIndPatentStatus);
  }


}
