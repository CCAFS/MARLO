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


import org.cgiar.ccafs.marlo.data.dao.ICapdevDisciplineDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapdevDisciplineService;
import org.cgiar.ccafs.marlo.data.model.CapdevDiscipline;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian Garcia
 */
@Named
public class CapdevDisciplineService implements ICapdevDisciplineService {


  private ICapdevDisciplineDAO capdevDisciplineDAO;

  // Managers


  @Inject
  public CapdevDisciplineService(ICapdevDisciplineDAO capdevDisciplineDAO) {
    this.capdevDisciplineDAO = capdevDisciplineDAO;


  }

  @Override
  public void deleteCapdevDiscipline(long capdevDisciplineId) {

    capdevDisciplineDAO.deleteCapdevDiscipline(capdevDisciplineId);
  }

  @Override
  public boolean existCapdevDiscipline(long capdevDisciplineID) {

    return capdevDisciplineDAO.existCapdevDiscipline(capdevDisciplineID);
  }

  @Override
  public List<CapdevDiscipline> findAll() {

    return capdevDisciplineDAO.findAll();

  }

  @Override
  public CapdevDiscipline getCapdevDisciplineById(long capdevDisciplineID) {

    return capdevDisciplineDAO.find(capdevDisciplineID);
  }

  @Override
  public List<CapdevDiscipline> getCapdevDisciplinesByUserId(Long userId) {
    return capdevDisciplineDAO.getCapdevDisciplinesByUserId(userId);
  }

  @Override
  public CapdevDiscipline saveCapdevDiscipline(CapdevDiscipline capdevDiscipline) {

    return capdevDisciplineDAO.save(capdevDiscipline);
  }

  @Override
  public CapdevDiscipline saveCapdevDiscipline(CapdevDiscipline capdevDiscipline, String actionName,
    List<String> relationsName) {
    return capdevDisciplineDAO.save(capdevDiscipline, actionName, relationsName);
  }


}
