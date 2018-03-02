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

import org.cgiar.ccafs.marlo.data.dao.PowbCrpStaffingDAO;
import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffing;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbCrpStaffingMySQLDAO extends AbstractMarloDAO<PowbCrpStaffing, Long> implements PowbCrpStaffingDAO {


  @Inject
  public PowbCrpStaffingMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbCrpStaffing(long powbCrpStaffingId) {
    PowbCrpStaffing powbCrpStaffing = this.find(powbCrpStaffingId);
    powbCrpStaffing.setActive(false);
    this.save(powbCrpStaffing);
  }

  @Override
  public boolean existPowbCrpStaffing(long powbCrpStaffingID) {
    PowbCrpStaffing powbCrpStaffing = this.find(powbCrpStaffingID);
    if (powbCrpStaffing == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbCrpStaffing find(long id) {
    return super.find(PowbCrpStaffing.class, id);

  }

  @Override
  public List<PowbCrpStaffing> findAll() {
    String query = "from " + PowbCrpStaffing.class.getName() + " where is_active=1";
    List<PowbCrpStaffing> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbCrpStaffing save(PowbCrpStaffing powbCrpStaffing) {
    if (powbCrpStaffing.getId() == null) {
      super.saveEntity(powbCrpStaffing);
    } else {
      powbCrpStaffing = super.update(powbCrpStaffing);
    }


    return powbCrpStaffing;
  }


}