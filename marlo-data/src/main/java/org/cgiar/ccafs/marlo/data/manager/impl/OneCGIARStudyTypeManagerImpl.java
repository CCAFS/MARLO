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

import org.cgiar.ccafs.marlo.data.dao.OneCGIARStudyTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARStudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARStudyType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class OneCGIARStudyTypeManagerImpl implements OneCGIARStudyTypeManager {

  private OneCGIARStudyTypeDAO oneCGIARStudyTypeDAO;

  @Inject
  public OneCGIARStudyTypeManagerImpl(OneCGIARStudyTypeDAO oneCGIARStudyTypeDAO) {
    super();
    this.oneCGIARStudyTypeDAO = oneCGIARStudyTypeDAO;
  }

  @Override
  public void deleteOneCGIARStudyType(long oneCGIARStudyTypeId) {
    this.oneCGIARStudyTypeDAO.deleteOneCGIARStudyType(oneCGIARStudyTypeId);
  }

  @Override
  public boolean existOneCGIARStudyType(long oneCGIARStudyTypeID) {
    return this.oneCGIARStudyTypeDAO.existOneCGIARStudyType(oneCGIARStudyTypeID);
  }

  @Override
  public List<OneCGIARStudyType> getAll() {
    return this.oneCGIARStudyTypeDAO.getAll();
  }

  @Override
  public OneCGIARStudyType getStudyTypeById(long id) {
    return this.oneCGIARStudyTypeDAO.getStudyTypeById(id);
  }

  @Override
  public OneCGIARStudyType save(OneCGIARStudyType oneCGIARStudyType) {
    return this.oneCGIARStudyTypeDAO.save(oneCGIARStudyType);
  }

}
