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


import org.cgiar.ccafs.marlo.data.dao.RepIndInnovationNatureDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationNatureManager;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationNature;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndInnovationNatureManagerImpl implements RepIndInnovationNatureManager {


  private RepIndInnovationNatureDAO repIndInnovationNatureDAO;
  // Managers


  @Inject
  public RepIndInnovationNatureManagerImpl(RepIndInnovationNatureDAO repIndInnovationNatureDAO) {
    this.repIndInnovationNatureDAO = repIndInnovationNatureDAO;


  }

  @Override
  public void deleteRepIndInnovationNature(long repIndInnovationNatureId) {

    repIndInnovationNatureDAO.deleteRepIndInnovationNature(repIndInnovationNatureId);
  }

  @Override
  public boolean existRepIndInnovationNature(long repIndInnovationNatureID) {

    return repIndInnovationNatureDAO.existRepIndInnovationNature(repIndInnovationNatureID);
  }

  @Override
  public List<RepIndInnovationNature> findAll() {

    return repIndInnovationNatureDAO.findAll();

  }

  @Override
  public RepIndInnovationNature getRepIndInnovationNatureById(long repIndInnovationNatureID) {

    return repIndInnovationNatureDAO.find(repIndInnovationNatureID);
  }

  @Override
  public RepIndInnovationNature saveRepIndInnovationNature(RepIndInnovationNature repIndInnovationNature) {

    return repIndInnovationNatureDAO.save(repIndInnovationNature);
  }


}
