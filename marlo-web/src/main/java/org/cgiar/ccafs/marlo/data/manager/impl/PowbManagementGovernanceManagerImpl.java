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


import org.cgiar.ccafs.marlo.data.dao.PowbManagementGovernanceDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbManagementGovernanceManager;
import org.cgiar.ccafs.marlo.data.model.PowbManagementGovernance;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbManagementGovernanceManagerImpl implements PowbManagementGovernanceManager {


  private PowbManagementGovernanceDAO powbManagementGovernanceDAO;
  // Managers


  @Inject
  public PowbManagementGovernanceManagerImpl(PowbManagementGovernanceDAO powbManagementGovernanceDAO) {
    this.powbManagementGovernanceDAO = powbManagementGovernanceDAO;


  }

  @Override
  public void deletePowbManagementGovernance(long powbManagementGovernanceId) {

    powbManagementGovernanceDAO.deletePowbManagementGovernance(powbManagementGovernanceId);
  }

  @Override
  public boolean existPowbManagementGovernance(long powbManagementGovernanceID) {

    return powbManagementGovernanceDAO.existPowbManagementGovernance(powbManagementGovernanceID);
  }

  @Override
  public List<PowbManagementGovernance> findAll() {

    return powbManagementGovernanceDAO.findAll();

  }

  @Override
  public PowbManagementGovernance getPowbManagementGovernanceById(long powbManagementGovernanceID) {

    return powbManagementGovernanceDAO.find(powbManagementGovernanceID);
  }

  @Override
  public PowbManagementGovernance savePowbManagementGovernance(PowbManagementGovernance powbManagementGovernance) {

    return powbManagementGovernanceDAO.save(powbManagementGovernance);
  }


}
