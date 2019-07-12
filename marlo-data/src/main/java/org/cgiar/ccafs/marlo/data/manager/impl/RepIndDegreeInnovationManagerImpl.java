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


import org.cgiar.ccafs.marlo.data.dao.RepIndDegreeInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndDegreeInnovationManager;
import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndDegreeInnovationManagerImpl implements RepIndDegreeInnovationManager {


  private RepIndDegreeInnovationDAO repIndDegreeInnovationDAO;
  // Managers


  @Inject
  public RepIndDegreeInnovationManagerImpl(RepIndDegreeInnovationDAO repIndDegreeInnovationDAO) {
    this.repIndDegreeInnovationDAO = repIndDegreeInnovationDAO;


  }

  @Override
  public void deleteRepIndDegreeInnovation(long repIndDegreeInnovationId) {

    repIndDegreeInnovationDAO.deleteRepIndDegreeInnovation(repIndDegreeInnovationId);
  }

  @Override
  public boolean existRepIndDegreeInnovation(long repIndDegreeInnovationID) {

    return repIndDegreeInnovationDAO.existRepIndDegreeInnovation(repIndDegreeInnovationID);
  }

  @Override
  public List<RepIndDegreeInnovation> findAll() {

    return repIndDegreeInnovationDAO.findAll();

  }

  @Override
  public RepIndDegreeInnovation getRepIndDegreeInnovationById(long repIndDegreeInnovationID) {

    return repIndDegreeInnovationDAO.find(repIndDegreeInnovationID);
  }

  @Override
  public RepIndDegreeInnovation saveRepIndDegreeInnovation(RepIndDegreeInnovation repIndDegreeInnovation) {

    return repIndDegreeInnovationDAO.save(repIndDegreeInnovation);
  }


}
