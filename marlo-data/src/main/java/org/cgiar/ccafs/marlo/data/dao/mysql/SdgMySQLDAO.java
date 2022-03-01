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

import org.cgiar.ccafs.marlo.data.dao.SdgDAO;
import org.cgiar.ccafs.marlo.data.model.Sdg;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class SdgMySQLDAO extends AbstractMarloDAO<Sdg, Long> implements SdgDAO {

  @Inject
  public SdgMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSdg(long sdgId) {
    /*
     * Sdg sdg = this.getSdgById(sdgId);
     * sdg.setActive(false);
     * this.save(sdg);
     */
    Sdg sdg = this.getSdgById(sdgId);
    this.delete(sdg);
  }

  @Override
  public boolean existSdg(long sdgID) {
    Sdg sdg = this.getSdgById(sdgID);
    if (sdg == null) {
      return false;
    }
    return true;

  }

  @Override
  public List<Sdg> getAll() {
    String query = "from " + Sdg.class.getName();
    List<Sdg> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public Sdg getSdgByFinancialCode(String financialCode) {
    String query = "select sdg from Sdg sdg where financialCode = :financialCode";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("financialCode", financialCode);

    List<Sdg> results = super.findAll(createQuery);

    Sdg sdg = (results != null && !results.isEmpty()) ? results.get(0) : null;

    return sdg;
  }

  @Override
  public Sdg getSdgById(long id) {
    return super.find(Sdg.class, id);
  }

  @Override
  public Sdg getSdgBySmoCode(String smoCode) {
    String query = "select sdg from Sdg sdg where smoCode = :smoCode";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("smoCode", smoCode);

    List<Sdg> results = super.findAll(createQuery);

    Sdg sdg = (results != null && !results.isEmpty()) ? results.get(0) : null;

    return sdg;
  }

  @Override
  public Sdg save(Sdg sdg) {
    if (sdg.getId() == null) {
      super.saveEntity(sdg);
    } else {
      sdg = super.update(sdg);
    }
    return sdg;
  }

}