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

import org.cgiar.ccafs.marlo.data.dao.PowbIndAssesmentRiskDAO;
import org.cgiar.ccafs.marlo.data.model.PowbIndAssesmentRisk;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbIndAssesmentRiskMySQLDAO extends AbstractMarloDAO<PowbIndAssesmentRisk, Long>
  implements PowbIndAssesmentRiskDAO {


  @Inject
  public PowbIndAssesmentRiskMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbIndAssesmentRisk(long powbIndAssesmentRiskId) {
    PowbIndAssesmentRisk powbIndAssesmentRisk = this.find(powbIndAssesmentRiskId);
    this.delete(powbIndAssesmentRisk);
  }

  @Override
  public boolean existPowbIndAssesmentRisk(long powbIndAssesmentRiskID) {
    PowbIndAssesmentRisk powbIndAssesmentRisk = this.find(powbIndAssesmentRiskID);
    if (powbIndAssesmentRisk == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbIndAssesmentRisk find(long id) {
    return super.find(PowbIndAssesmentRisk.class, id);

  }

  @Override
  public List<PowbIndAssesmentRisk> findAll() {
    String query = "from " + PowbIndAssesmentRisk.class.getName();
    List<PowbIndAssesmentRisk> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbIndAssesmentRisk save(PowbIndAssesmentRisk powbIndAssesmentRisk) {
    if (powbIndAssesmentRisk.getId() == null) {
      super.saveEntity(powbIndAssesmentRisk);
    } else {
      powbIndAssesmentRisk = super.update(powbIndAssesmentRisk);
    }


    return powbIndAssesmentRisk;
  }


}