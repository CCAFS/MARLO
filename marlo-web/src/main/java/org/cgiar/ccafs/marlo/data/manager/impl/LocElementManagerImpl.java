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


import org.cgiar.ccafs.marlo.data.dao.LocElementDAO;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class LocElementManagerImpl implements LocElementManager {


  private LocElementDAO locElementDAO;
  // Managers


  @Inject
  public LocElementManagerImpl(LocElementDAO locElementDAO) {
    this.locElementDAO = locElementDAO;


  }

  @Override
  public boolean deleteLocElement(long locElementId) {

    return locElementDAO.deleteLocElement(locElementId);
  }

  @Override
  public boolean existLocElement(long locElementID) {

    return locElementDAO.existLocElement(locElementID);
  }

  @Override
  public List<LocElement> findAll() {

    return locElementDAO.findAll();

  }

  @Override
  public LocElement getLocElementById(long locElementID) {

    return locElementDAO.find(locElementID);
  }

  @Override
  public LocElement getLocElementByISOCode(String ISOCode) {

    return locElementDAO.findISOCode(ISOCode);
  }

  @Override
  public long saveLocElement(LocElement locElement) {

    return locElementDAO.save(locElement);
  }


}
