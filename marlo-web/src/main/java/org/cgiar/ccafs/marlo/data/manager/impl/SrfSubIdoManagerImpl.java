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


import org.cgiar.ccafs.marlo.data.dao.SrfSubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfSubIdoManagerImpl implements SrfSubIdoManager {


  private SrfSubIdoDAO srfSubIdoDAO;
  // Managers


  @Inject
  public SrfSubIdoManagerImpl(SrfSubIdoDAO srfSubIdoDAO) {
    this.srfSubIdoDAO = srfSubIdoDAO;


  }

  @Override
  public void deleteSrfSubIdo(long srfSubIdoId) {

    srfSubIdoDAO.deleteSrfSubIdo(srfSubIdoId);
  }

  @Override
  public boolean existSrfSubIdo(long srfSubIdoID) {

    return srfSubIdoDAO.existSrfSubIdo(srfSubIdoID);
  }

  @Override
  public List<SrfSubIdo> findAll() {

    return srfSubIdoDAO.findAll();

  }

  @Override
  public SrfSubIdo getSrfSubIdoById(long srfSubIdoID) {

    return srfSubIdoDAO.find(srfSubIdoID);
  }

  @Override
  public SrfSubIdo saveSrfSubIdo(SrfSubIdo srfSubIdo) {

    return srfSubIdoDAO.save(srfSubIdo);
  }


}
