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

import org.cgiar.ccafs.marlo.data.dao.PowbFinancialExpenditureDAO;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialExpenditure;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbFinancialExpenditureMySQLDAO extends AbstractMarloDAO<PowbFinancialExpenditure, Long> implements PowbFinancialExpenditureDAO {


  @Inject
  public PowbFinancialExpenditureMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbFinancialExpenditure(long powbFinancialExpenditureId) {
    PowbFinancialExpenditure powbFinancialExpenditure = this.find(powbFinancialExpenditureId);
    powbFinancialExpenditure.setActive(false);
    this.save(powbFinancialExpenditure);
  }

  @Override
  public boolean existPowbFinancialExpenditure(long powbFinancialExpenditureID) {
    PowbFinancialExpenditure powbFinancialExpenditure = this.find(powbFinancialExpenditureID);
    if (powbFinancialExpenditure == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbFinancialExpenditure find(long id) {
    return super.find(PowbFinancialExpenditure.class, id);

  }

  @Override
  public List<PowbFinancialExpenditure> findAll() {
    String query = "from " + PowbFinancialExpenditure.class.getName() + " where is_active=1";
    List<PowbFinancialExpenditure> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbFinancialExpenditure save(PowbFinancialExpenditure powbFinancialExpenditure) {
    if (powbFinancialExpenditure.getId() == null) {
      super.saveEntity(powbFinancialExpenditure);
    } else {
      powbFinancialExpenditure = super.update(powbFinancialExpenditure);
    }


    return powbFinancialExpenditure;
  }


}