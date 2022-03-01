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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ImpactAreasDAO;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ImpactAreasMySQLDAO extends AbstractMarloDAO<ImpactArea, Long> implements ImpactAreasDAO {

  @Inject
  public ImpactAreasMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteImpactArea(long impactAreaId) {
    /*
     * ImpactArea impactArea = this.getImpactAreaById(impactAreaId);
     * impactArea.setActive(false);
     * this.save(impactArea);
     */
    ImpactArea impactArea = this.getImpactAreaById(impactAreaId);
    this.delete(impactArea);
  }

  @Override
  public boolean existImpactArea(long impactAreaID) {
    ImpactArea impactArea = this.getImpactAreaById(impactAreaID);
    if (impactArea == null) {
      return false;
    }
    return true;

  }

  @Override
  public List<ImpactArea> getAll() {
    String query = "from " + ImpactArea.class.getName();
    List<ImpactArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ImpactArea getImpactAreaByFinancialCode(String financialCode) {
    String query = "select ia from ImpactArea ia where financialCode = :financialCode";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("financialCode", financialCode);

    List<ImpactArea> results = super.findAll(createQuery);

    ImpactArea impactArea = (results != null && !results.isEmpty()) ? results.get(0) : null;

    return impactArea;
  }

  @Override
  public ImpactArea getImpactAreaById(long id) {
    return super.find(ImpactArea.class, id);
  }

  @Override
  public ImpactArea save(ImpactArea impactArea) {
    if (impactArea.getId() == null) {
      super.saveEntity(impactArea);
    } else {
      impactArea = super.update(impactArea);
    }
    return impactArea;
  }
}
