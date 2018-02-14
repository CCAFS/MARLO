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


import org.cgiar.ccafs.marlo.data.dao.PowbFinancialPlannedBudgetDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialPlannedBudgetManager;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbFinancialPlannedBudgetManagerImpl implements PowbFinancialPlannedBudgetManager {


  private PowbFinancialPlannedBudgetDAO powbFinancialPlannedBudgetDAO;
  // Managers


  @Inject
  public PowbFinancialPlannedBudgetManagerImpl(PowbFinancialPlannedBudgetDAO powbFinancialPlannedBudgetDAO) {
    this.powbFinancialPlannedBudgetDAO = powbFinancialPlannedBudgetDAO;


  }

  @Override
  public void deletePowbFinancialPlannedBudget(long powbFinancialPlannedBudgetId) {

    powbFinancialPlannedBudgetDAO.deletePowbFinancialPlannedBudget(powbFinancialPlannedBudgetId);
  }

  @Override
  public boolean existPowbFinancialPlannedBudget(long powbFinancialPlannedBudgetID) {

    return powbFinancialPlannedBudgetDAO.existPowbFinancialPlannedBudget(powbFinancialPlannedBudgetID);
  }

  @Override
  public List<PowbFinancialPlannedBudget> findAll() {

    return powbFinancialPlannedBudgetDAO.findAll();

  }

  @Override
  public PowbFinancialPlannedBudget getPowbFinancialPlannedBudgetById(long powbFinancialPlannedBudgetID) {

    return powbFinancialPlannedBudgetDAO.find(powbFinancialPlannedBudgetID);
  }

  @Override
  public PowbFinancialPlannedBudget savePowbFinancialPlannedBudget(PowbFinancialPlannedBudget powbFinancialPlannedBudget) {

    return powbFinancialPlannedBudgetDAO.save(powbFinancialPlannedBudget);
  }


}
