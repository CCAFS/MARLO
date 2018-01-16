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


import org.cgiar.ccafs.marlo.data.dao.CrpIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.CrpIndicator;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CrpIndicatorManagerImpl implements CrpIndicatorManager {


  private CrpIndicatorDAO crpIndicatorDAO;
  // Managers


  @Inject
  public CrpIndicatorManagerImpl(CrpIndicatorDAO crpIndicatorDAO) {
    this.crpIndicatorDAO = crpIndicatorDAO;


  }

  @Override
  public void deleteCrpIndicator(long crpIndicatorId) {

    crpIndicatorDAO.deleteCrpIndicator(crpIndicatorId);
  }

  @Override
  public boolean existCrpIndicator(long crpIndicatorID) {

    return crpIndicatorDAO.existCrpIndicator(crpIndicatorID);
  }

  @Override
  public List<CrpIndicator> findAll() {

    return crpIndicatorDAO.findAll();

  }

  @Override
  public CrpIndicator getCrpIndicatorById(long crpIndicatorID) {

    return crpIndicatorDAO.find(crpIndicatorID);
  }

  @Override
  public CrpIndicator saveCrpIndicator(CrpIndicator crpIndicator) {

    return crpIndicatorDAO.save(crpIndicator);
  }


}
