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


import org.cgiar.ccafs.marlo.data.dao.CrpPandrDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpPandrManager;
import org.cgiar.ccafs.marlo.data.model.CrpPandr;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpPandrManagerImpl implements CrpPandrManager {


  private CrpPandrDAO crpPandrDAO;
  // Managers


  @Inject
  public CrpPandrManagerImpl(CrpPandrDAO crpPandrDAO) {
    this.crpPandrDAO = crpPandrDAO;


  }

  @Override
  public boolean deleteCrpPandr(long crpPandrId) {

    return crpPandrDAO.deleteCrpPandr(crpPandrId);
  }

  @Override
  public boolean existCrpPandr(long crpPandrID) {

    return crpPandrDAO.existCrpPandr(crpPandrID);
  }

  @Override
  public List<CrpPandr> findAll() {

    return crpPandrDAO.findAll();

  }

  @Override
  public CrpPandr getCrpPandrById(long crpPandrID) {

    return crpPandrDAO.find(crpPandrID);
  }

  @Override
  public long saveCrpPandr(CrpPandr crpPandr) {

    return crpPandrDAO.save(crpPandr);
  }


}
