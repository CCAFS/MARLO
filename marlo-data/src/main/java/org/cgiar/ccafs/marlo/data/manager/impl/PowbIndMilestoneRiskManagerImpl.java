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


import org.cgiar.ccafs.marlo.data.dao.PowbIndMilestoneRiskDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbIndMilestoneRiskManager;
import org.cgiar.ccafs.marlo.data.model.PowbIndMilestoneRisk;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PowbIndMilestoneRiskManagerImpl implements PowbIndMilestoneRiskManager {


  private PowbIndMilestoneRiskDAO powbIndMilestoneRiskDAO;
  // Managers


  @Inject
  public PowbIndMilestoneRiskManagerImpl(PowbIndMilestoneRiskDAO powbIndMilestoneRiskDAO) {
    this.powbIndMilestoneRiskDAO = powbIndMilestoneRiskDAO;


  }

  @Override
  public void deletePowbIndMilestoneRisk(long powbIndMilestoneRiskId) {

    powbIndMilestoneRiskDAO.deletePowbIndMilestoneRisk(powbIndMilestoneRiskId);
  }

  @Override
  public boolean existPowbIndMilestoneRisk(long powbIndMilestoneRiskID) {

    return powbIndMilestoneRiskDAO.existPowbIndMilestoneRisk(powbIndMilestoneRiskID);
  }

  @Override
  public List<PowbIndMilestoneRisk> findAll() {

    return powbIndMilestoneRiskDAO.findAll();

  }

  @Override
  public PowbIndMilestoneRisk getPowbIndMilestoneRiskById(long powbIndMilestoneRiskID) {

    return powbIndMilestoneRiskDAO.find(powbIndMilestoneRiskID);
  }

  @Override
  public PowbIndMilestoneRisk savePowbIndMilestoneRisk(PowbIndMilestoneRisk powbIndMilestoneRisk) {

    return powbIndMilestoneRiskDAO.save(powbIndMilestoneRisk);
  }


}
