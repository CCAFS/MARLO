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

import org.cgiar.ccafs.marlo.data.dao.PowbManagementGovernanceDAO;
import org.cgiar.ccafs.marlo.data.model.PowbManagementGovernance;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbManagementGovernanceMySQLDAO extends AbstractMarloDAO<PowbManagementGovernance, Long> implements PowbManagementGovernanceDAO {


  @Inject
  public PowbManagementGovernanceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbManagementGovernance(long powbManagementGovernanceId) {
    PowbManagementGovernance powbManagementGovernance = this.find(powbManagementGovernanceId);
    powbManagementGovernance.setActive(false);
    this.save(powbManagementGovernance);
  }

  @Override
  public boolean existPowbManagementGovernance(long powbManagementGovernanceID) {
    PowbManagementGovernance powbManagementGovernance = this.find(powbManagementGovernanceID);
    if (powbManagementGovernance == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbManagementGovernance find(long id) {
    return super.find(PowbManagementGovernance.class, id);

  }

  @Override
  public List<PowbManagementGovernance> findAll() {
    String query = "from " + PowbManagementGovernance.class.getName() + " where is_active=1";
    List<PowbManagementGovernance> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbManagementGovernance save(PowbManagementGovernance powbManagementGovernance) {
    if (powbManagementGovernance.getId() == null) {
      super.saveEntity(powbManagementGovernance);
    } else {
      powbManagementGovernance = super.update(powbManagementGovernance);
    }


    return powbManagementGovernance;
  }


}