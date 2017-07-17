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


import org.cgiar.ccafs.marlo.data.dao.ICapdevLocationsDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapdevLocationsService;
import org.cgiar.ccafs.marlo.data.model.CapdevLocations;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapdevLocationsService implements ICapdevLocationsService {


  private ICapdevLocationsDAO capdevLocationsDAO;

  // Managers


  @Inject
  public CapdevLocationsService(ICapdevLocationsDAO capdevLocationsDAO) {
    this.capdevLocationsDAO = capdevLocationsDAO;


  }

  @Override
  public boolean deleteCapdevLocations(long capdevLocationsId) {

    return capdevLocationsDAO.deleteCapdevLocations(capdevLocationsId);
  }

  @Override
  public boolean existCapdevLocations(long capdevLocationsID) {

    return capdevLocationsDAO.existCapdevLocations(capdevLocationsID);
  }

  @Override
  public List<CapdevLocations> findAll() {

    return capdevLocationsDAO.findAll();

  }

  @Override
  public CapdevLocations getCapdevLocationsById(long capdevLocationsID) {

    return capdevLocationsDAO.find(capdevLocationsID);
  }

  @Override
  public List<CapdevLocations> getCapdevLocationssByUserId(Long userId) {
    return capdevLocationsDAO.getCapdevLocationssByUserId(userId);
  }

  @Override
  public long saveCapdevLocations(CapdevLocations capdevLocations) {

    return capdevLocationsDAO.save(capdevLocations);
  }

  @Override
  public long saveCapdevLocations(CapdevLocations capdevLocations, String actionName, List<String> relationsName) {
    return capdevLocationsDAO.save(capdevLocations, actionName, relationsName);
  }


}
