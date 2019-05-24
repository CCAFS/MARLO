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
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusTableManager;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GeneralStatusTable;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class GeneralStatusManagerImpl implements GeneralStatusManager {


  private GeneralStatusDAO generalStatusDAO;
  private GeneralStatusTableManager generalStatusTableManager;
  // Managers


  @Inject
  public GeneralStatusManagerImpl(GeneralStatusDAO generalStatusDAO,
    GeneralStatusTableManager generalStatusTableManager) {
    this.generalStatusDAO = generalStatusDAO;
    this.generalStatusTableManager = generalStatusTableManager;


  }

  @Override
  public void deleteGeneralStatus(long generalStatusId) {

    this.generalStatusDAO.deleteGeneralStatus(generalStatusId);
  }

  @Override
  public boolean existGeneralStatus(long generalStatusID) {

    return this.generalStatusDAO.existGeneralStatus(generalStatusID);
  }

  @Override
  public List<GeneralStatus> findAll() {

    return this.generalStatusDAO.findAll();

  }

  // TODO: mralmanzar there are no retrieving the general status on generalStatusTable. CHECK IT
  @Override
  public List<GeneralStatus> findByTable(String tableName) {
    List<GeneralStatusTable> statusTableList;
    List<GeneralStatus> statusList = null;
    statusTableList = this.generalStatusTableManager.findAll();
    statusList = statusTableList.stream().filter(c -> c.getTableName() == tableName)
      .map(result -> result.getGeneralStatus()).collect(Collectors.toList());
    return statusList;
  }

  @Override
  public GeneralStatus getGeneralStatusById(long generalStatusID) {

    return this.generalStatusDAO.find(generalStatusID);
  }

  @Override
  public GeneralStatus saveGeneralStatus(GeneralStatus generalStatus) {

    return this.generalStatusDAO.save(generalStatus);
  }

}
