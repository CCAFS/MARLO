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

import org.cgiar.ccafs.marlo.data.dao.PowbIndMilestoneRiskDAO;
import org.cgiar.ccafs.marlo.data.model.PowbIndMilestoneRisk;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbIndMilestoneRiskMySQLDAO extends AbstractMarloDAO<PowbIndMilestoneRisk, Long>
  implements PowbIndMilestoneRiskDAO {


  @Inject
  public PowbIndMilestoneRiskMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbIndMilestoneRisk(long powbIndMilestoneRiskId) {
    PowbIndMilestoneRisk powbIndMilestoneRisk = this.find(powbIndMilestoneRiskId);
    this.delete(powbIndMilestoneRisk);
  }

  @Override
  public boolean existPowbIndMilestoneRisk(long powbIndMilestoneRiskID) {
    PowbIndMilestoneRisk powbIndMilestoneRisk = this.find(powbIndMilestoneRiskID);
    if (powbIndMilestoneRisk == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbIndMilestoneRisk find(long id) {
    return super.find(PowbIndMilestoneRisk.class, id);

  }

  @Override
  public List<PowbIndMilestoneRisk> findAll() {
    String query = "from " + PowbIndMilestoneRisk.class.getName();
    List<PowbIndMilestoneRisk> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbIndMilestoneRisk save(PowbIndMilestoneRisk powbIndMilestoneRisk) {
    if (powbIndMilestoneRisk.getId() == null) {
      super.saveEntity(powbIndMilestoneRisk);
    } else {
      powbIndMilestoneRisk = super.update(powbIndMilestoneRisk);
    }


    return powbIndMilestoneRisk;
  }


}