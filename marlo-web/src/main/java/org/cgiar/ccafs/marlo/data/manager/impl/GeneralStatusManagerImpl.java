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


import org.cgiar.ccafs.marlo.data.dao.GeneralStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class GeneralStatusManagerImpl implements GeneralStatusManager {


  private GeneralStatusDAO generalStatusDAO;
  // Managers


  @Inject
  public GeneralStatusManagerImpl(GeneralStatusDAO generalStatusDAO) {
    this.generalStatusDAO = generalStatusDAO;


  }

  @Override
  public void deleteGeneralStatus(long generalStatusId) {

    generalStatusDAO.deleteGeneralStatus(generalStatusId);
  }

  @Override
  public boolean existGeneralStatus(long generalStatusID) {

    return generalStatusDAO.existGeneralStatus(generalStatusID);
  }

  @Override
  public List<GeneralStatus> findAll() {

    return generalStatusDAO.findAll();

  }

  @Override
  public GeneralStatus getGeneralStatusById(long generalStatusID) {

    return generalStatusDAO.find(generalStatusID);
  }

  @Override
  public GeneralStatus saveGeneralStatus(GeneralStatus generalStatus) {

    return generalStatusDAO.save(generalStatus);
  }


}
