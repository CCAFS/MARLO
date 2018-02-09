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

import org.cgiar.ccafs.marlo.data.dao.PowbCrpStaffingCategoriesDAO;
import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffingCategories;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbCrpStaffingCategoriesMySQLDAO extends AbstractMarloDAO<PowbCrpStaffingCategories, Long> implements PowbCrpStaffingCategoriesDAO {


  @Inject
  public PowbCrpStaffingCategoriesMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbCrpStaffingCategories(long powbCrpStaffingCategoriesId) {
    PowbCrpStaffingCategories powbCrpStaffingCategories = this.find(powbCrpStaffingCategoriesId);
    powbCrpStaffingCategories.setActive(false);
    this.save(powbCrpStaffingCategories);
  }

  @Override
  public boolean existPowbCrpStaffingCategories(long powbCrpStaffingCategoriesID) {
    PowbCrpStaffingCategories powbCrpStaffingCategories = this.find(powbCrpStaffingCategoriesID);
    if (powbCrpStaffingCategories == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbCrpStaffingCategories find(long id) {
    return super.find(PowbCrpStaffingCategories.class, id);

  }

  @Override
  public List<PowbCrpStaffingCategories> findAll() {
    String query = "from " + PowbCrpStaffingCategories.class.getName() + " where is_active=1";
    List<PowbCrpStaffingCategories> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbCrpStaffingCategories save(PowbCrpStaffingCategories powbCrpStaffingCategories) {
    if (powbCrpStaffingCategories.getId() == null) {
      super.saveEntity(powbCrpStaffingCategories);
    } else {
      powbCrpStaffingCategories = super.update(powbCrpStaffingCategories);
    }


    return powbCrpStaffingCategories;
  }


}