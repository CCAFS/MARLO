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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.OneCGIARUnitTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARUnitTypeManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUnitType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class OneCGIARUnitTypeManagerImpl implements OneCGIARUnitTypeManager {

  private OneCGIARUnitTypeDAO oneCGIARUnitTypeDAO;

  @Inject
  public OneCGIARUnitTypeManagerImpl(OneCGIARUnitTypeDAO oneCGIARUnitTypeDAO) {
    super();
    this.oneCGIARUnitTypeDAO = oneCGIARUnitTypeDAO;
  }

  @Override
  public void deleteOneCGIARUnitType(Long oneCGIARUnitTypeId) {
    this.oneCGIARUnitTypeDAO.deleteOneCGIARUnitType(oneCGIARUnitTypeId.longValue());
  }

  @Override
  public boolean existOneCGIARUnitType(Long oneCGIARUnitTypeID) {
    return this.oneCGIARUnitTypeDAO.existOneCGIARUnitType(oneCGIARUnitTypeID.longValue());
  }

  @Override
  public List<OneCGIARUnitType> getAll() {
    return oneCGIARUnitTypeDAO.getAll();
  }

  @Override
  public OneCGIARUnitType getUnitTypeByAcronym(String acronym) {
    return this.oneCGIARUnitTypeDAO.getUnitTypeByAcronym(acronym);
  }

  @Override
  public OneCGIARUnitType getUnitTypeById(Long id) {
    return oneCGIARUnitTypeDAO.getUnitTypeById(id.longValue());
  }

  @Override
  public OneCGIARUnitType save(OneCGIARUnitType oneCGIARUnitType) {
    return this.oneCGIARUnitTypeDAO.save(oneCGIARUnitType);
  }

}
