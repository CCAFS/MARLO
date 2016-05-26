/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.SrfIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfIdoManagerImpl implements SrfIdoManager {


  private SrfIdoDAO srfIdoDAO;
  // Managers


  @Inject
  public SrfIdoManagerImpl(SrfIdoDAO srfIdoDAO) {
    this.srfIdoDAO = srfIdoDAO;


  }

  @Override
  public boolean deleteSrfIdo(long srfIdoId) {

    return srfIdoDAO.deleteSrfIdo(srfIdoId);
  }

  @Override
  public boolean existSrfIdo(long srfIdoID) {

    return srfIdoDAO.existSrfIdo(srfIdoID);
  }

  @Override
  public List<SrfIdo> findAll() {

    return srfIdoDAO.findAll();

  }

  @Override
  public SrfIdo getSrfIdoById(long srfIdoID) {

    return srfIdoDAO.find(srfIdoID);
  }

  @Override
  public long saveSrfIdo(SrfIdo srfIdo) {

    return srfIdoDAO.save(srfIdo);
  }


}
