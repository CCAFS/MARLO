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


import org.cgiar.ccafs.marlo.data.dao.BudgetTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class BudgetTypeManagerImpl implements BudgetTypeManager {


  private BudgetTypeDAO budgetTypeDAO;
  // Managers


  @Inject
  public BudgetTypeManagerImpl(BudgetTypeDAO budgetTypeDAO) {
    this.budgetTypeDAO = budgetTypeDAO;


  }

  @Override
  public void deleteBudgetType(long budgetTypeId) {

    budgetTypeDAO.deleteBudgetType(budgetTypeId);
  }

  @Override
  public boolean existBudgetType(long budgetTypeID) {

    return budgetTypeDAO.existBudgetType(budgetTypeID);
  }

  @Override
  public List<BudgetType> findAll() {

    return budgetTypeDAO.findAll();

  }

  @Override
  public BudgetType getBudgetTypeById(long budgetTypeID) {

    return budgetTypeDAO.find(budgetTypeID);
  }

  @Override
  public BudgetType saveBudgetType(BudgetType budgetType) {

    return budgetTypeDAO.save(budgetType);
  }


}
