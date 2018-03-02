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

import org.cgiar.ccafs.marlo.data.dao.PowbManagementRiskDAO;
import org.cgiar.ccafs.marlo.data.model.PowbManagementRisk;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbManagementRiskMySQLDAO extends AbstractMarloDAO<PowbManagementRisk, Long> implements PowbManagementRiskDAO {


  @Inject
  public PowbManagementRiskMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbManagementRisk(long powbManagementRiskId) {
    PowbManagementRisk powbManagementRisk = this.find(powbManagementRiskId);
    powbManagementRisk.setActive(false);
    this.save(powbManagementRisk);
  }

  @Override
  public boolean existPowbManagementRisk(long powbManagementRiskID) {
    PowbManagementRisk powbManagementRisk = this.find(powbManagementRiskID);
    if (powbManagementRisk == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbManagementRisk find(long id) {
    return super.find(PowbManagementRisk.class, id);

  }

  @Override
  public List<PowbManagementRisk> findAll() {
    String query = "from " + PowbManagementRisk.class.getName() + " where is_active=1";
    List<PowbManagementRisk> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbManagementRisk save(PowbManagementRisk powbManagementRisk) {
    if (powbManagementRisk.getId() == null) {
      super.saveEntity(powbManagementRisk);
    } else {
      powbManagementRisk = super.update(powbManagementRisk);
    }


    return powbManagementRisk;
  }


}