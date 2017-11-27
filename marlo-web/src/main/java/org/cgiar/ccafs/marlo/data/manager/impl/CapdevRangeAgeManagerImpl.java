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


import org.cgiar.ccafs.marlo.data.dao.CapdevRangeAgeDAO;
import org.cgiar.ccafs.marlo.data.manager.CapdevRangeAgeManager;
import org.cgiar.ccafs.marlo.data.model.CapdevRangeAge;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapdevRangeAgeManagerImpl implements CapdevRangeAgeManager {


  private CapdevRangeAgeDAO capdevRangeAgeDAO;
  // Managers


  @Inject
  public CapdevRangeAgeManagerImpl(CapdevRangeAgeDAO capdevRangeAgeDAO) {
    this.capdevRangeAgeDAO = capdevRangeAgeDAO;


  }

  @Override
  public void deleteCapdevRangeAge(long capdevRangeAgeId) {

    capdevRangeAgeDAO.deleteCapdevRangeAge(capdevRangeAgeId);
  }

  @Override
  public boolean existCapdevRangeAge(long capdevRangeAgeID) {

    return capdevRangeAgeDAO.existCapdevRangeAge(capdevRangeAgeID);
  }

  @Override
  public List<CapdevRangeAge> findAll() {

    return capdevRangeAgeDAO.findAll();

  }

  @Override
  public CapdevRangeAge getCapdevRangeAgeById(long capdevRangeAgeID) {

    return capdevRangeAgeDAO.find(capdevRangeAgeID);
  }

  @Override
  public CapdevRangeAge saveCapdevRangeAge(CapdevRangeAge capdevRangeAge) {

    return capdevRangeAgeDAO.save(capdevRangeAge);
  }


}
