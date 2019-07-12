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


import org.cgiar.ccafs.marlo.data.dao.RepIndTrainingTermDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndTrainingTermManager;
import org.cgiar.ccafs.marlo.data.model.RepIndTrainingTerm;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndTrainingTermManagerImpl implements RepIndTrainingTermManager {


  private RepIndTrainingTermDAO repIndTrainingTermDAO;
  // Managers


  @Inject
  public RepIndTrainingTermManagerImpl(RepIndTrainingTermDAO repIndTrainingTermDAO) {
    this.repIndTrainingTermDAO = repIndTrainingTermDAO;


  }

  @Override
  public void deleteRepIndTrainingTerm(long repIndTrainingTermId) {

    repIndTrainingTermDAO.deleteRepIndTrainingTerm(repIndTrainingTermId);
  }

  @Override
  public boolean existRepIndTrainingTerm(long repIndTrainingTermID) {

    return repIndTrainingTermDAO.existRepIndTrainingTerm(repIndTrainingTermID);
  }

  @Override
  public List<RepIndTrainingTerm> findAll() {

    return repIndTrainingTermDAO.findAll();

  }

  @Override
  public RepIndTrainingTerm getRepIndTrainingTermById(long repIndTrainingTermID) {

    return repIndTrainingTermDAO.find(repIndTrainingTermID);
  }

  @Override
  public RepIndTrainingTerm saveRepIndTrainingTerm(RepIndTrainingTerm repIndTrainingTerm) {

    return repIndTrainingTermDAO.save(repIndTrainingTerm);
  }


}
