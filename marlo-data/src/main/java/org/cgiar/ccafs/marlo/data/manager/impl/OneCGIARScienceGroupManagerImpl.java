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

import org.cgiar.ccafs.marlo.data.dao.OneCGIARScienceGroupDAO;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARScienceGroupManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARScienceGroup;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class OneCGIARScienceGroupManagerImpl implements OneCGIARScienceGroupManager {

  private OneCGIARScienceGroupDAO oneCGIARScienceGroupDAO;

  @Inject
  public OneCGIARScienceGroupManagerImpl(OneCGIARScienceGroupDAO oneCGIARScienceGroupDAO) {
    super();
    this.oneCGIARScienceGroupDAO = oneCGIARScienceGroupDAO;
  }

  @Override
  public void deleteOneCGIARScienceGroup(Long oneCGIARScienceGroupId) {
    this.oneCGIARScienceGroupDAO.deleteOneCGIARScienceGroup(oneCGIARScienceGroupId.longValue());
  }

  @Override
  public boolean existOneCGIARScienceGroup(Long oneCGIARScienceGroupID) {
    return this.oneCGIARScienceGroupDAO.existOneCGIARScienceGroup(oneCGIARScienceGroupID.longValue());
  }

  @Override
  public List<OneCGIARScienceGroup> getAll() {
    return oneCGIARScienceGroupDAO.getAll();
  }

  @Override
  public OneCGIARScienceGroup getScienceGroupByFinanceCode(String financeCode) {
    return this.oneCGIARScienceGroupDAO.getScienceGroupByFinanceCode(financeCode);
  }

  @Override
  public OneCGIARScienceGroup getScienceGroupById(Long id) {
    return oneCGIARScienceGroupDAO.getScienceGroupById(id.longValue());
  }

  @Override
  public OneCGIARScienceGroup save(OneCGIARScienceGroup oneCGIARScienceGroup) {
    return this.oneCGIARScienceGroupDAO.save(oneCGIARScienceGroup);
  }

}
