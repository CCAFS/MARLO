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

import org.cgiar.ccafs.marlo.data.dao.PowbSynthesisCrpStaffingCategoryDAO;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisCrpStaffingCategory;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbSynthesisCrpStaffingCategoryMySQLDAO extends AbstractMarloDAO<PowbSynthesisCrpStaffingCategory, Long> implements PowbSynthesisCrpStaffingCategoryDAO {


  @Inject
  public PowbSynthesisCrpStaffingCategoryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbSynthesisCrpStaffingCategory(long powbSynthesisCrpStaffingCategoryId) {
    PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory = this.find(powbSynthesisCrpStaffingCategoryId);
    powbSynthesisCrpStaffingCategory.setActive(false);
    this.save(powbSynthesisCrpStaffingCategory);
  }

  @Override
  public boolean existPowbSynthesisCrpStaffingCategory(long powbSynthesisCrpStaffingCategoryID) {
    PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory = this.find(powbSynthesisCrpStaffingCategoryID);
    if (powbSynthesisCrpStaffingCategory == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbSynthesisCrpStaffingCategory find(long id) {
    return super.find(PowbSynthesisCrpStaffingCategory.class, id);

  }

  @Override
  public List<PowbSynthesisCrpStaffingCategory> findAll() {
    String query = "from " + PowbSynthesisCrpStaffingCategory.class.getName() + " where is_active=1";
    List<PowbSynthesisCrpStaffingCategory> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbSynthesisCrpStaffingCategory save(PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory) {
    if (powbSynthesisCrpStaffingCategory.getId() == null) {
      super.saveEntity(powbSynthesisCrpStaffingCategory);
    } else {
      powbSynthesisCrpStaffingCategory = super.update(powbSynthesisCrpStaffingCategory);
    }


    return powbSynthesisCrpStaffingCategory;
  }


}