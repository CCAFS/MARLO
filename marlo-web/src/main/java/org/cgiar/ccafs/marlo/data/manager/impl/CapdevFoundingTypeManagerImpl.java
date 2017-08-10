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


import org.cgiar.ccafs.marlo.data.dao.CapdevFoundingTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.CapdevFoundingTypeManager;
import org.cgiar.ccafs.marlo.data.model.CapdevFoundingType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CapdevFoundingTypeManagerImpl implements CapdevFoundingTypeManager {


  private CapdevFoundingTypeDAO capdevFoundingTypeDAO;
  // Managers


  @Inject
  public CapdevFoundingTypeManagerImpl(CapdevFoundingTypeDAO capdevFoundingTypeDAO) {
    this.capdevFoundingTypeDAO = capdevFoundingTypeDAO;


  }

  @Override
  public boolean deleteCapdevFoundingType(long capdevFoundingTypeId) {

    return capdevFoundingTypeDAO.deleteCapdevFoundingType(capdevFoundingTypeId);
  }

  @Override
  public boolean existCapdevFoundingType(long capdevFoundingTypeID) {

    return capdevFoundingTypeDAO.existCapdevFoundingType(capdevFoundingTypeID);
  }

  @Override
  public List<CapdevFoundingType> findAll() {

    return capdevFoundingTypeDAO.findAll();

  }

  @Override
  public CapdevFoundingType getCapdevFoundingTypeById(long capdevFoundingTypeID) {

    return capdevFoundingTypeDAO.find(capdevFoundingTypeID);
  }

  @Override
  public long saveCapdevFoundingType(CapdevFoundingType capdevFoundingType) {

    return capdevFoundingTypeDAO.save(capdevFoundingType);
  }


}
