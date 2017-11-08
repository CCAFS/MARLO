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


import org.cgiar.ccafs.marlo.data.dao.CrpOutcomeSubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpOutcomeSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpOutcomeSubIdoManagerImpl implements CrpOutcomeSubIdoManager {


  private CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO;
  // Managers


  @Inject
  public CrpOutcomeSubIdoManagerImpl(CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO) {
    this.crpOutcomeSubIdoDAO = crpOutcomeSubIdoDAO;


  }

  @Override
  public void deleteCrpOutcomeSubIdo(long crpOutcomeSubIdoId) {

    crpOutcomeSubIdoDAO.deleteCrpOutcomeSubIdo(crpOutcomeSubIdoId);
  }

  @Override
  public boolean existCrpOutcomeSubIdo(long crpOutcomeSubIdoID) {

    return crpOutcomeSubIdoDAO.existCrpOutcomeSubIdo(crpOutcomeSubIdoID);
  }

  @Override
  public List<CrpOutcomeSubIdo> findAll() {

    return crpOutcomeSubIdoDAO.findAll();

  }

  @Override
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoById(long crpOutcomeSubIdoID) {

    return crpOutcomeSubIdoDAO.find(crpOutcomeSubIdoID);
  }

  @Override
  public CrpOutcomeSubIdo saveCrpOutcomeSubIdo(CrpOutcomeSubIdo crpOutcomeSubIdo) {

    return crpOutcomeSubIdoDAO.save(crpOutcomeSubIdo);
  }


}
