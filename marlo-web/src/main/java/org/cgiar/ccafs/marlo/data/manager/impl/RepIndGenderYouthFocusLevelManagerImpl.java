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


import org.cgiar.ccafs.marlo.data.dao.RepIndGenderYouthFocusLevelDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndGenderYouthFocusLevelManagerImpl implements RepIndGenderYouthFocusLevelManager {


  private RepIndGenderYouthFocusLevelDAO repIndGenderYouthFocusLevelDAO;
  // Managers


  @Inject
  public RepIndGenderYouthFocusLevelManagerImpl(RepIndGenderYouthFocusLevelDAO repIndGenderYouthFocusLevelDAO) {
    this.repIndGenderYouthFocusLevelDAO = repIndGenderYouthFocusLevelDAO;


  }

  @Override
  public void deleteRepIndGenderYouthFocusLevel(long repIndGenderYouthFocusLevelId) {

    repIndGenderYouthFocusLevelDAO.deleteRepIndGenderYouthFocusLevel(repIndGenderYouthFocusLevelId);
  }

  @Override
  public boolean existRepIndGenderYouthFocusLevel(long repIndGenderYouthFocusLevelID) {

    return repIndGenderYouthFocusLevelDAO.existRepIndGenderYouthFocusLevel(repIndGenderYouthFocusLevelID);
  }

  @Override
  public List<RepIndGenderYouthFocusLevel> findAll() {

    return repIndGenderYouthFocusLevelDAO.findAll();

  }

  @Override
  public RepIndGenderYouthFocusLevel getRepIndGenderYouthFocusLevelById(long repIndGenderYouthFocusLevelID) {

    return repIndGenderYouthFocusLevelDAO.find(repIndGenderYouthFocusLevelID);
  }

  @Override
  public RepIndGenderYouthFocusLevel saveRepIndGenderYouthFocusLevel(RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel) {

    return repIndGenderYouthFocusLevelDAO.save(repIndGenderYouthFocusLevel);
  }


}
