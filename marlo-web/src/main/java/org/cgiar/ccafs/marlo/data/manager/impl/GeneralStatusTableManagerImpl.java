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


import org.cgiar.ccafs.marlo.data.dao.GeneralStatusTableDAO;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusTableManager;
import org.cgiar.ccafs.marlo.data.model.GeneralStatusTable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class GeneralStatusTableManagerImpl implements GeneralStatusTableManager {


  private GeneralStatusTableDAO generalStatusTableDAO;
  // Managers


  @Inject
  public GeneralStatusTableManagerImpl(GeneralStatusTableDAO generalStatusTableDAO) {
    this.generalStatusTableDAO = generalStatusTableDAO;


  }

  @Override
  public void deleteGeneralStatusTable(long generalStatusTableId) {

    this.generalStatusTableDAO.deleteGeneralStatusTable(generalStatusTableId);
  }

  @Override
  public boolean existGeneralStatusTable(long generalStatusTableID) {

    return this.generalStatusTableDAO.existGeneralStatusTable(generalStatusTableID);
  }

  @Override
  public List<GeneralStatusTable> findAll() {

    return this.generalStatusTableDAO.findAll();

  }

  @Override
  public GeneralStatusTable getGeneralStatusTableById(long generalStatusTableID) {

    return this.generalStatusTableDAO.find(generalStatusTableID);
  }

  @Override
  public GeneralStatusTable saveGeneralStatusTable(GeneralStatusTable generalStatusTable) {

    return this.generalStatusTableDAO.save(generalStatusTable);
  }


}
