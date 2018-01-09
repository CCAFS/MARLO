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


import org.cgiar.ccafs.marlo.data.dao.SrfSloIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIdoManager;
import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class SrfSloIdoManagerImpl implements SrfSloIdoManager {


  private SrfSloIdoDAO srfSloIdoDAO;
  // Managers


  @Inject
  public SrfSloIdoManagerImpl(SrfSloIdoDAO srfSloIdoDAO) {
    this.srfSloIdoDAO = srfSloIdoDAO;


  }

  @Override
  public void deleteSrfSloIdo(long srfSloIdoId) {

    srfSloIdoDAO.deleteSrfSloIdo(srfSloIdoId);
  }

  @Override
  public boolean existSrfSloIdo(long srfSloIdoID) {

    return srfSloIdoDAO.existSrfSloIdo(srfSloIdoID);
  }

  @Override
  public List<SrfSloIdo> findAll() {

    return srfSloIdoDAO.findAll();

  }

  @Override
  public SrfSloIdo getSrfSloIdoById(long srfSloIdoID) {

    return srfSloIdoDAO.find(srfSloIdoID);
  }

  @Override
  public SrfSloIdo saveSrfSloIdo(SrfSloIdo srfSloIdo) {

    return srfSloIdoDAO.save(srfSloIdo);
  }


}
