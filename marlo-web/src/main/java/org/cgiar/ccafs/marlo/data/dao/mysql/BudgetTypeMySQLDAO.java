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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.BudgetTypeDAO;
import org.cgiar.ccafs.marlo.data.model.BudgetType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class BudgetTypeMySQLDAO extends AbstractMarloDAO<BudgetType, Long> implements BudgetTypeDAO {


  @Inject
  public BudgetTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteBudgetType(long budgetTypeId) {
    BudgetType budgetType = this.find(budgetTypeId);

    super.delete(budgetType);
  }

  @Override
  public boolean existBudgetType(long budgetTypeID) {
    BudgetType budgetType = this.find(budgetTypeID);
    if (budgetType == null) {
      return false;
    }
    return true;

  }

  @Override
  public BudgetType find(long id) {
    return super.find(BudgetType.class, id);

  }

  @Override
  public List<BudgetType> findAll() {
    String query = "from " + BudgetType.class.getName() + " ";
    List<BudgetType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public BudgetType save(BudgetType budgetType) {
    if (budgetType.getId() == null) {
      super.saveEntity(budgetType);
    } else {
      budgetType = super.update(budgetType);
    }


    return budgetType;
  }


}