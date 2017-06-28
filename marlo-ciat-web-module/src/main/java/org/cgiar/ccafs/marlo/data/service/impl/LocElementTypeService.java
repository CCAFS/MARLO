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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ILocElementTypeDAO;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.service.ILocElementTypeService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class LocElementTypeService implements ILocElementTypeService {


  private ILocElementTypeDAO locElementTypeDAO;

  // Managers


  @Inject
  public LocElementTypeService(ILocElementTypeDAO locElementTypeDAO) {
    this.locElementTypeDAO = locElementTypeDAO;


  }

  @Override
  public boolean deleteLocElementType(long locElementTypeId) {

    return locElementTypeDAO.deleteLocElementType(locElementTypeId);
  }

  @Override
  public boolean existLocElementType(long locElementTypeID) {

    return locElementTypeDAO.existLocElementType(locElementTypeID);
  }

  @Override
  public List<LocElementType> findAll() {

    return locElementTypeDAO.findAll();

  }

  @Override
  public LocElementType getLocElementTypeById(long locElementTypeID) {

    return locElementTypeDAO.find(locElementTypeID);
  }

  @Override
  public List<LocElementType> getLocElementTypesByUserId(Long userId) {
    return locElementTypeDAO.getLocElementTypesByUserId(userId);
  }

  @Override
  public long saveLocElementType(LocElementType locElementType) {

    return locElementTypeDAO.save(locElementType);
  }


}
