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


import org.cgiar.ccafs.marlo.data.dao.RepIndStageInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndStageInnovationManagerImpl implements RepIndStageInnovationManager {


  private RepIndStageInnovationDAO repIndStageInnovationDAO;
  // Managers


  @Inject
  public RepIndStageInnovationManagerImpl(RepIndStageInnovationDAO repIndStageInnovationDAO) {
    this.repIndStageInnovationDAO = repIndStageInnovationDAO;


  }

  @Override
  public void deleteRepIndStageInnovation(long repIndStageInnovationId) {

    repIndStageInnovationDAO.deleteRepIndStageInnovation(repIndStageInnovationId);
  }

  @Override
  public boolean existRepIndStageInnovation(long repIndStageInnovationID) {

    return repIndStageInnovationDAO.existRepIndStageInnovation(repIndStageInnovationID);
  }

  @Override
  public List<RepIndStageInnovation> findAll() {

    return repIndStageInnovationDAO.findAll();

  }

  @Override
  public RepIndStageInnovation getRepIndStageInnovationById(long repIndStageInnovationID) {

    return repIndStageInnovationDAO.find(repIndStageInnovationID);
  }

  @Override
  public RepIndStageInnovation saveRepIndStageInnovation(RepIndStageInnovation repIndStageInnovation) {

    return repIndStageInnovationDAO.save(repIndStageInnovation);
  }


}
