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


import org.cgiar.ccafs.marlo.data.dao.CrpClusterKeyOutputDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CrpClusterKeyOutputManagerImpl implements CrpClusterKeyOutputManager {


  private CrpClusterKeyOutputDAO crpClusterKeyOutputDAO;
  // Managers


  @Inject
  public CrpClusterKeyOutputManagerImpl(CrpClusterKeyOutputDAO crpClusterKeyOutputDAO) {
    this.crpClusterKeyOutputDAO = crpClusterKeyOutputDAO;


  }

  @Override
  public void deleteCrpClusterKeyOutput(long crpClusterKeyOutputId) {

    crpClusterKeyOutputDAO.deleteCrpClusterKeyOutput(crpClusterKeyOutputId);
  }

  @Override
  public boolean existCrpClusterKeyOutput(long crpClusterKeyOutputID) {

    return crpClusterKeyOutputDAO.existCrpClusterKeyOutput(crpClusterKeyOutputID);
  }

  @Override
  public List<CrpClusterKeyOutput> findAll() {

    return crpClusterKeyOutputDAO.findAll();

  }

  @Override
  public CrpClusterKeyOutput getCrpClusterKeyOutputById(long crpClusterKeyOutputID) {

    return crpClusterKeyOutputDAO.find(crpClusterKeyOutputID);
  }

  @Override
  public CrpClusterKeyOutput saveCrpClusterKeyOutput(CrpClusterKeyOutput crpClusterKeyOutput) {

    return crpClusterKeyOutputDAO.save(crpClusterKeyOutput);
  }


}
