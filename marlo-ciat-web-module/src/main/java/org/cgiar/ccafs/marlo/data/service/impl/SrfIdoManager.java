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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ISrfIdoDAO;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.service.ISrfIdoManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfIdoManager implements ISrfIdoManager {


  private ISrfIdoDAO srfIdoDAO;

  // Managers


  @Inject
  public SrfIdoManager(ISrfIdoDAO srfIdoDAO) {
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
  public List<SrfIdo> getSrfIdosByUserId(Long userId) {
    return srfIdoDAO.getSrfIdosByUserId(userId);
  }

  @Override
  public long saveSrfIdo(SrfIdo srfIdo) {

    return srfIdoDAO.save(srfIdo);
  }

  @Override
  public long saveSrfIdo(SrfIdo srfIdo, String actionName, List<String> relationsName) {
    return srfIdoDAO.save(srfIdo, actionName, relationsName);
  }


}
