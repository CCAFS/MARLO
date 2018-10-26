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


import org.cgiar.ccafs.marlo.data.dao.RepIndCollaborationTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndCollaborationTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndCollaborationType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndCollaborationTypeManagerImpl implements RepIndCollaborationTypeManager {


  private RepIndCollaborationTypeDAO repIndCollaborationTypeDAO;
  // Managers


  @Inject
  public RepIndCollaborationTypeManagerImpl(RepIndCollaborationTypeDAO repIndCollaborationTypeDAO) {
    this.repIndCollaborationTypeDAO = repIndCollaborationTypeDAO;


  }

  @Override
  public void deleteRepIndCollaborationType(long repIndCollaborationTypeId) {

    repIndCollaborationTypeDAO.deleteRepIndCollaborationType(repIndCollaborationTypeId);
  }

  @Override
  public boolean existRepIndCollaborationType(long repIndCollaborationTypeID) {

    return repIndCollaborationTypeDAO.existRepIndCollaborationType(repIndCollaborationTypeID);
  }

  @Override
  public List<RepIndCollaborationType> findAll() {

    return repIndCollaborationTypeDAO.findAll();

  }

  @Override
  public RepIndCollaborationType getRepIndCollaborationTypeById(long repIndCollaborationTypeID) {

    return repIndCollaborationTypeDAO.find(repIndCollaborationTypeID);
  }

  @Override
  public RepIndCollaborationType saveRepIndCollaborationType(RepIndCollaborationType repIndCollaborationType) {

    return repIndCollaborationTypeDAO.save(repIndCollaborationType);
  }


}
