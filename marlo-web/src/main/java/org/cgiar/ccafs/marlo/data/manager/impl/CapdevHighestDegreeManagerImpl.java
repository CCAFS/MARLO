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


import org.cgiar.ccafs.marlo.data.dao.CapdevHighestDegreeDAO;
import org.cgiar.ccafs.marlo.data.manager.CapdevHighestDegreeManager;
import org.cgiar.ccafs.marlo.data.model.CapdevHighestDegree;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian Garcia
 */
@Named
public class CapdevHighestDegreeManagerImpl implements CapdevHighestDegreeManager {


  private CapdevHighestDegreeDAO capdevHighestDegreeDAO;
  // Managers


  @Inject
  public CapdevHighestDegreeManagerImpl(CapdevHighestDegreeDAO capdevHighestDegreeDAO) {
    this.capdevHighestDegreeDAO = capdevHighestDegreeDAO;


  }

  @Override
  public void deleteCapdevHighestDegree(long capdevHighestDegreeId) {

    capdevHighestDegreeDAO.deleteCapdevHighestDegree(capdevHighestDegreeId);
  }

  @Override
  public boolean existCapdevHighestDegree(long capdevHighestDegreeID) {

    return capdevHighestDegreeDAO.existCapdevHighestDegree(capdevHighestDegreeID);
  }

  @Override
  public List<CapdevHighestDegree> findAll() {

    return capdevHighestDegreeDAO.findAll();

  }

  @Override
  public CapdevHighestDegree getCapdevHighestDegreeById(long capdevHighestDegreeID) {

    return capdevHighestDegreeDAO.find(capdevHighestDegreeID);
  }

  @Override
  public CapdevHighestDegree saveCapdevHighestDegree(CapdevHighestDegree capdevHighestDegree) {

    return capdevHighestDegreeDAO.save(capdevHighestDegree);
  }


}
