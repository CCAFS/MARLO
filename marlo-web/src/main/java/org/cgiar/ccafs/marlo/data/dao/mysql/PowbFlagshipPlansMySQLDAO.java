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

import org.cgiar.ccafs.marlo.data.dao.PowbFlagshipPlansDAO;
import org.cgiar.ccafs.marlo.data.model.PowbFlagshipPlans;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbFlagshipPlansMySQLDAO extends AbstractMarloDAO<PowbFlagshipPlans, Long> implements PowbFlagshipPlansDAO {


  @Inject
  public PowbFlagshipPlansMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbFlagshipPlans(long powbFlagshipPlansId) {
    PowbFlagshipPlans powbFlagshipPlans = this.find(powbFlagshipPlansId);
    powbFlagshipPlans.setActive(false);
    this.save(powbFlagshipPlans);
  }

  @Override
  public boolean existPowbFlagshipPlans(long powbFlagshipPlansID) {
    PowbFlagshipPlans powbFlagshipPlans = this.find(powbFlagshipPlansID);
    if (powbFlagshipPlans == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbFlagshipPlans find(long id) {
    return super.find(PowbFlagshipPlans.class, id);

  }

  @Override
  public List<PowbFlagshipPlans> findAll() {
    String query = "from " + PowbFlagshipPlans.class.getName() + " where is_active=1";
    List<PowbFlagshipPlans> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbFlagshipPlans save(PowbFlagshipPlans powbFlagshipPlans) {
    if (powbFlagshipPlans.getId() == null) {
      super.saveEntity(powbFlagshipPlans);
    } else {
      powbFlagshipPlans = super.update(powbFlagshipPlans);
    }


    return powbFlagshipPlans;
  }


}