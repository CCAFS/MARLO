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


import org.cgiar.ccafs.marlo.data.dao.SrfSloDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfSloManager;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfSloManagerImpl implements SrfSloManager {


  private SrfSloDAO srfSloDAO;
  // Managers


  @Inject
  public SrfSloManagerImpl(SrfSloDAO srfSloDAO) {
    this.srfSloDAO = srfSloDAO;


  }

  @Override
  public boolean deleteSrfSlo(long srfSloId) {

    return srfSloDAO.deleteSrfSlo(srfSloId);
  }

  @Override
  public boolean existSrfSlo(long srfSloID) {

    return srfSloDAO.existSrfSlo(srfSloID);
  }

  @Override
  public List<SrfSlo> findAll() {

    return srfSloDAO.findAll();

  }

  @Override
  public SrfSlo getSrfSloById(long srfSloID) {

    return srfSloDAO.find(srfSloID);
  }

  @Override
  public long saveSrfSlo(SrfSlo srfSlo) {

    return srfSloDAO.save(srfSlo);
  }


}
