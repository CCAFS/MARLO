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


import org.cgiar.ccafs.marlo.data.dao.PowbManagementRiskDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbManagementRiskManager;
import org.cgiar.ccafs.marlo.data.model.PowbManagementRisk;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbManagementRiskManagerImpl implements PowbManagementRiskManager {


  private PowbManagementRiskDAO powbManagementRiskDAO;
  // Managers


  @Inject
  public PowbManagementRiskManagerImpl(PowbManagementRiskDAO powbManagementRiskDAO) {
    this.powbManagementRiskDAO = powbManagementRiskDAO;


  }

  @Override
  public void deletePowbManagementRisk(long powbManagementRiskId) {

    powbManagementRiskDAO.deletePowbManagementRisk(powbManagementRiskId);
  }

  @Override
  public boolean existPowbManagementRisk(long powbManagementRiskID) {

    return powbManagementRiskDAO.existPowbManagementRisk(powbManagementRiskID);
  }

  @Override
  public List<PowbManagementRisk> findAll() {

    return powbManagementRiskDAO.findAll();

  }

  @Override
  public PowbManagementRisk getPowbManagementRiskById(long powbManagementRiskID) {

    return powbManagementRiskDAO.find(powbManagementRiskID);
  }

  @Override
  public PowbManagementRisk savePowbManagementRisk(PowbManagementRisk powbManagementRisk) {

    return powbManagementRiskDAO.save(powbManagementRisk);
  }


}
