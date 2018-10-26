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


import org.cgiar.ccafs.marlo.data.dao.RepIndStageStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndStageStudyManagerImpl implements RepIndStageStudyManager {


  private RepIndStageStudyDAO repIndStageStudyDAO;
  // Managers


  @Inject
  public RepIndStageStudyManagerImpl(RepIndStageStudyDAO repIndStageStudyDAO) {
    this.repIndStageStudyDAO = repIndStageStudyDAO;


  }

  @Override
  public void deleteRepIndStageStudy(long repIndStageStudyId) {

    repIndStageStudyDAO.deleteRepIndStageStudy(repIndStageStudyId);
  }

  @Override
  public boolean existRepIndStageStudy(long repIndStageStudyID) {

    return repIndStageStudyDAO.existRepIndStageStudy(repIndStageStudyID);
  }

  @Override
  public List<RepIndStageStudy> findAll() {

    return repIndStageStudyDAO.findAll();

  }

  @Override
  public RepIndStageStudy getRepIndStageStudyById(long repIndStageStudyID) {

    return repIndStageStudyDAO.find(repIndStageStudyID);
  }

  @Override
  public RepIndStageStudy saveRepIndStageStudy(RepIndStageStudy repIndStageStudy) {

    return repIndStageStudyDAO.save(repIndStageStudy);
  }


}
