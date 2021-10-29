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

import org.cgiar.ccafs.marlo.data.dao.OneCGIARUnitDAO;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARUnitManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUnit;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class OneCGIARUnitManagerImpl implements OneCGIARUnitManager {

  private OneCGIARUnitDAO oneCGIARUnitDAO;

  @Inject
  public OneCGIARUnitManagerImpl(OneCGIARUnitDAO oneCGIARUnitDAO) {
    super();
    this.oneCGIARUnitDAO = oneCGIARUnitDAO;
  }

  @Override
  public List<OneCGIARUnit> getAll() {
    return this.oneCGIARUnitDAO.getAll();
  }

  @Override
  public OneCGIARUnit getUnitByFinancialCode(String financialCode) {
    return this.oneCGIARUnitDAO.getUnitByFinancialCode(financialCode);
  }

  @Override
  public OneCGIARUnit getUnitById(Long id) {
    return this.oneCGIARUnitDAO.getUnitById(id.longValue());
  }

  @Override
  public List<OneCGIARUnit> getUnitsByParent(Long parentId) {
    return this.oneCGIARUnitDAO.getUnitsByParent(parentId.longValue());
  }

  @Override
  public List<OneCGIARUnit> getUnitsByScienceGroup(Long scienceGroupId) {
    return this.oneCGIARUnitDAO.getUnitsByScienceGroup(scienceGroupId.longValue());
  }

}
