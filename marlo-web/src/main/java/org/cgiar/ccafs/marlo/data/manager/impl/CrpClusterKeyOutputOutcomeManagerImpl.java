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


import org.cgiar.ccafs.marlo.data.dao.CrpClusterKeyOutputOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CrpClusterKeyOutputOutcomeManagerImpl implements CrpClusterKeyOutputOutcomeManager {


  private CrpClusterKeyOutputOutcomeDAO crpClusterKeyOutputOutcomeDAO;
  // Managers


  @Inject
  public CrpClusterKeyOutputOutcomeManagerImpl(CrpClusterKeyOutputOutcomeDAO crpClusterKeyOutputOutcomeDAO) {
    this.crpClusterKeyOutputOutcomeDAO = crpClusterKeyOutputOutcomeDAO;


  }

  @Override
  public void deleteCrpClusterKeyOutputOutcome(long crpClusterKeyOutputOutcomeId) {

    crpClusterKeyOutputOutcomeDAO.deleteCrpClusterKeyOutputOutcome(crpClusterKeyOutputOutcomeId);
  }

  @Override
  public boolean existCrpClusterKeyOutputOutcome(long crpClusterKeyOutputOutcomeID) {

    return crpClusterKeyOutputOutcomeDAO.existCrpClusterKeyOutputOutcome(crpClusterKeyOutputOutcomeID);
  }

  @Override
  public List<CrpClusterKeyOutputOutcome> findAll() {

    return crpClusterKeyOutputOutcomeDAO.findAll();

  }

  @Override
  public CrpClusterKeyOutputOutcome getCrpClusterKeyOutputOutcomeById(long crpClusterKeyOutputOutcomeID) {

    return crpClusterKeyOutputOutcomeDAO.find(crpClusterKeyOutputOutcomeID);
  }

  @Override
  public CrpClusterKeyOutputOutcome saveCrpClusterKeyOutputOutcome(CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome) {

    return crpClusterKeyOutputOutcomeDAO.save(crpClusterKeyOutputOutcome);
  }


}
