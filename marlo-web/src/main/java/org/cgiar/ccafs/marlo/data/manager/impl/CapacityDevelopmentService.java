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


import org.cgiar.ccafs.marlo.data.dao.ICapacityDevelopmentDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapacityDevelopmentService implements ICapacityDevelopmentService {


  private ICapacityDevelopmentDAO capacityDevelopmentDAO;

  // Managers


  @Inject
  public CapacityDevelopmentService(ICapacityDevelopmentDAO capacityDevelopmentDAO) {
    this.capacityDevelopmentDAO = capacityDevelopmentDAO;


  }

  @Override
  public boolean deleteCapacityDevelopment(long capacityDevelopmentId) {

    return capacityDevelopmentDAO.deleteCapacityDevelopment(capacityDevelopmentId);
  }

  @Override
  public boolean existCapacityDevelopment(long capacityDevelopmentID) {

    return capacityDevelopmentDAO.existCapacityDevelopment(capacityDevelopmentID);
  }

  @Override
  public List<CapacityDevelopment> findAll() {

    return capacityDevelopmentDAO.findAll();

  }

  @Override
  public CapacityDevelopment getCapacityDevelopmentById(long capacityDevelopmentID) {

    return capacityDevelopmentDAO.find(capacityDevelopmentID);
  }

  @Override
  public List<CapacityDevelopment> getCapacityDevelopmentsByUserId(Long userId) {
    return capacityDevelopmentDAO.getCapacityDevelopmentsByUserId(userId);
  }

  @Override
  public long saveCapacityDevelopment(CapacityDevelopment capacityDevelopment) {

    return capacityDevelopmentDAO.save(capacityDevelopment);
  }

  @Override
  public long saveCapacityDevelopment(CapacityDevelopment capacityDevelopment, String actionName,
    List<String> relationsName) {
    return capacityDevelopmentDAO.save(capacityDevelopment, actionName, relationsName);
  }


}
