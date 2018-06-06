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


import org.cgiar.ccafs.marlo.data.dao.RepIndInnovationTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndInnovationTypeManagerImpl implements RepIndInnovationTypeManager {


  private RepIndInnovationTypeDAO repIndInnovationTypeDAO;
  // Managers


  @Inject
  public RepIndInnovationTypeManagerImpl(RepIndInnovationTypeDAO repIndInnovationTypeDAO) {
    this.repIndInnovationTypeDAO = repIndInnovationTypeDAO;


  }

  @Override
  public void deleteRepIndInnovationType(long repIndInnovationTypeId) {

    repIndInnovationTypeDAO.deleteRepIndInnovationType(repIndInnovationTypeId);
  }

  @Override
  public boolean existRepIndInnovationType(long repIndInnovationTypeID) {

    return repIndInnovationTypeDAO.existRepIndInnovationType(repIndInnovationTypeID);
  }

  @Override
  public List<RepIndInnovationType> findAll() {

    return repIndInnovationTypeDAO.findAll();

  }

  @Override
  public RepIndInnovationType getRepIndInnovationTypeById(long repIndInnovationTypeID) {

    return repIndInnovationTypeDAO.find(repIndInnovationTypeID);
  }

  @Override
  public RepIndInnovationType saveRepIndInnovationType(RepIndInnovationType repIndInnovationType) {

    return repIndInnovationTypeDAO.save(repIndInnovationType);
  }


}
