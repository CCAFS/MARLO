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


import org.cgiar.ccafs.marlo.data.dao.ICapdevTargetgroupDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapdevTargetgroupService;
import org.cgiar.ccafs.marlo.data.model.CapdevTargetgroup;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapdevTargetgroupService implements ICapdevTargetgroupService {


  private ICapdevTargetgroupDAO capdevTargetgroupDAO;

  // Managers


  @Inject
  public CapdevTargetgroupService(ICapdevTargetgroupDAO capdevTargetgroupDAO) {
    this.capdevTargetgroupDAO = capdevTargetgroupDAO;


  }

  @Override
  public boolean deleteCapdevTargetgroup(long capdevTargetgroupId) {

    return capdevTargetgroupDAO.deleteCapdevTargetgroup(capdevTargetgroupId);
  }

  @Override
  public boolean existCapdevTargetgroup(long capdevTargetgroupID) {

    return capdevTargetgroupDAO.existCapdevTargetgroup(capdevTargetgroupID);
  }

  @Override
  public List<CapdevTargetgroup> findAll() {

    return capdevTargetgroupDAO.findAll();

  }

  @Override
  public CapdevTargetgroup getCapdevTargetgroupById(long capdevTargetgroupID) {

    return capdevTargetgroupDAO.find(capdevTargetgroupID);
  }

  @Override
  public List<CapdevTargetgroup> getCapdevTargetgroupsByUserId(Long userId) {
    return capdevTargetgroupDAO.getCapdevTargetgroupsByUserId(userId);
  }

  @Override
  public long saveCapdevTargetgroup(CapdevTargetgroup capdevTargetgroup) {

    return capdevTargetgroupDAO.save(capdevTargetgroup);
  }

  @Override
  public long saveCapdevTargetgroup(CapdevTargetgroup capdevTargetgroup, String actionName,
    List<String> relationsName) {
    return capdevTargetgroupDAO.save(capdevTargetgroup, actionName, relationsName);
  }


}
