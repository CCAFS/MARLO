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


import org.cgiar.ccafs.marlo.data.dao.ICapdevPartnersDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapdevPartnersService;
import org.cgiar.ccafs.marlo.data.model.CapdevPartners;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapdevPartnersService implements ICapdevPartnersService {


  private ICapdevPartnersDAO capdevPartnersDAO;

  // Managers


  @Inject
  public CapdevPartnersService(ICapdevPartnersDAO capdevPartnersDAO) {
    this.capdevPartnersDAO = capdevPartnersDAO;


  }

  @Override
  public boolean deleteCapdevPartners(long capdevPartnersId) {

    return capdevPartnersDAO.deleteCapdevPartners(capdevPartnersId);
  }

  @Override
  public boolean existCapdevPartners(long capdevPartnersID) {

    return capdevPartnersDAO.existCapdevPartners(capdevPartnersID);
  }

  @Override
  public List<CapdevPartners> findAll() {

    return capdevPartnersDAO.findAll();

  }

  @Override
  public CapdevPartners getCapdevPartnersById(long capdevPartnersID) {

    return capdevPartnersDAO.find(capdevPartnersID);
  }

  @Override
  public List<CapdevPartners> getCapdevPartnerssByUserId(Long userId) {
    return capdevPartnersDAO.getCapdevPartnerssByUserId(userId);
  }

  @Override
  public long saveCapdevPartners(CapdevPartners capdevPartners) {

    return capdevPartnersDAO.save(capdevPartners);
  }

  @Override
  public long saveCapdevPartners(CapdevPartners capdevPartners, String actionName, List<String> relationsName) {
    return capdevPartnersDAO.save(capdevPartners, actionName, relationsName);
  }


}
