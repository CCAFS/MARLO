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


import org.cgiar.ccafs.marlo.data.dao.ICapdevOutputsDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapdevOutputsService;
import org.cgiar.ccafs.marlo.data.model.CapdevOutputs;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian Garcia
 */
@Named
public class CapdevOutputsService implements ICapdevOutputsService {


  private ICapdevOutputsDAO capdevOutputsDAO;

  // Managers


  @Inject
  public CapdevOutputsService(ICapdevOutputsDAO capdevOutputsDAO) {
    this.capdevOutputsDAO = capdevOutputsDAO;


  }

  @Override
  public void deleteCapdevOutputs(long capdevOutputsId) {

    capdevOutputsDAO.deleteCapdevOutputs(capdevOutputsId);
  }

  @Override
  public boolean existCapdevOutputs(long capdevOutputsID) {

    return capdevOutputsDAO.existCapdevOutputs(capdevOutputsID);
  }

  @Override
  public List<CapdevOutputs> findAll() {

    return capdevOutputsDAO.findAll();

  }

  @Override
  public CapdevOutputs getCapdevOutputsById(long capdevOutputsID) {

    return capdevOutputsDAO.find(capdevOutputsID);
  }

  @Override
  public List<CapdevOutputs> getCapdevOutputssByUserId(Long userId) {
    return capdevOutputsDAO.getCapdevOutputssByUserId(userId);
  }

  @Override
  public CapdevOutputs saveCapdevOutputs(CapdevOutputs capdevOutputs) {

    return capdevOutputsDAO.save(capdevOutputs);
  }

  @Override
  public CapdevOutputs saveCapdevOutputs(CapdevOutputs capdevOutputs, String actionName, List<String> relationsName) {
    return capdevOutputsDAO.save(capdevOutputs, actionName, relationsName);
  }


}
