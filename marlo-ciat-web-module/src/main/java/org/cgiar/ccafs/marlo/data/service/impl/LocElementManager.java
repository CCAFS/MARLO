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


import org.cgiar.ccafs.marlo.data.dao.ILocElementDAO;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.service.ILocElementManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class LocElementManager implements ILocElementManager {


  private ILocElementDAO locElementDAO;

  // Managers


  @Inject
  public LocElementManager(ILocElementDAO locElementDAO) {
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
  public List<LocElement> getLocElementsByUserId(Long userId) {
    return locElementDAO.getLocElementsByUserId(userId);
  }

  @Override
  public long saveLocElement(LocElement locElement) {

    return locElementDAO.save(locElement);
  }


}
