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


import org.cgiar.ccafs.marlo.data.dao.PowbIndAssesmentRiskDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbIndAssesmentRiskManager;
import org.cgiar.ccafs.marlo.data.model.PowbIndAssesmentRisk;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PowbIndAssesmentRiskManagerImpl implements PowbIndAssesmentRiskManager {


  private PowbIndAssesmentRiskDAO powbIndAssesmentRiskDAO;
  // Managers


  @Inject
  public PowbIndAssesmentRiskManagerImpl(PowbIndAssesmentRiskDAO powbIndAssesmentRiskDAO) {
    this.powbIndAssesmentRiskDAO = powbIndAssesmentRiskDAO;


  }

  @Override
  public void deletePowbIndAssesmentRisk(long powbIndAssesmentRiskId) {

    powbIndAssesmentRiskDAO.deletePowbIndAssesmentRisk(powbIndAssesmentRiskId);
  }

  @Override
  public boolean existPowbIndAssesmentRisk(long powbIndAssesmentRiskID) {

    return powbIndAssesmentRiskDAO.existPowbIndAssesmentRisk(powbIndAssesmentRiskID);
  }

  @Override
  public List<PowbIndAssesmentRisk> findAll() {

    return powbIndAssesmentRiskDAO.findAll();

  }

  @Override
  public PowbIndAssesmentRisk getPowbIndAssesmentRiskById(long powbIndAssesmentRiskID) {

    return powbIndAssesmentRiskDAO.find(powbIndAssesmentRiskID);
  }

  @Override
  public PowbIndAssesmentRisk savePowbIndAssesmentRisk(PowbIndAssesmentRisk powbIndAssesmentRisk) {

    return powbIndAssesmentRiskDAO.save(powbIndAssesmentRisk);
  }


}
