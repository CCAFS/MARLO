/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.LocElementTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.LocElementType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class LocElementTypeManagerImpl implements LocElementTypeManager {


  private LocElementTypeDAO locElementTypeDAO;
  // Managers


  @Inject
  public LocElementTypeManagerImpl(LocElementTypeDAO locElementTypeDAO) {
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
  public long saveLocElementType(LocElementType locElementType) {

    return locElementTypeDAO.save(locElementType);
  }


}
