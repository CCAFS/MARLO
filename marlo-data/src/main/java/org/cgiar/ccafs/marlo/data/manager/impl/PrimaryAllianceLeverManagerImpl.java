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


import org.cgiar.ccafs.marlo.data.dao.PrimaryAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.PrimaryAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PrimaryAllianceLeverManagerImpl implements PrimaryAllianceLeverManager {


  private PrimaryAllianceLeverDAO primaryAllianceLeverDAO;
  // Managers


  @Inject
  public PrimaryAllianceLeverManagerImpl(PrimaryAllianceLeverDAO primaryAllianceLeverDAO) {
    this.primaryAllianceLeverDAO = primaryAllianceLeverDAO;


  }

  @Override
  public void deletePrimaryAllianceLever(long primaryAllianceLeverId) {

    primaryAllianceLeverDAO.deletePrimaryAllianceLever(primaryAllianceLeverId);
  }

  @Override
  public boolean existPrimaryAllianceLever(long primaryAllianceLeverID) {

    return primaryAllianceLeverDAO.existPrimaryAllianceLever(primaryAllianceLeverID);
  }

  @Override
  public List<PrimaryAllianceLever> findAll() {

    return primaryAllianceLeverDAO.findAll();

  }

  @Override
  public List<PrimaryAllianceLever> findAllByPhase(long phaseId) {

    return primaryAllianceLeverDAO.findAllByPhase(phaseId);

  }

  @Override
  public PrimaryAllianceLever getPrimaryAllianceLeverById(long primaryAllianceLeverID) {

    return primaryAllianceLeverDAO.find(primaryAllianceLeverID);
  }

  @Override
  public PrimaryAllianceLever savePrimaryAllianceLever(PrimaryAllianceLever primaryAllianceLever) {

    return primaryAllianceLeverDAO.save(primaryAllianceLever);
  }


}
