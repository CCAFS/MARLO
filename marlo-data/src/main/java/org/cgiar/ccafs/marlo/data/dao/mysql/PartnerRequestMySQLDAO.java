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

import org.cgiar.ccafs.marlo.data.dao.PartnerRequestDAO;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@Named
public class PartnerRequestMySQLDAO extends AbstractMarloDAO<PartnerRequest, Long> implements PartnerRequestDAO {

  @Inject
  public PartnerRequestMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePartnerRequest(long partnerRequestId) {
    PartnerRequest partnerRequest = this.find(partnerRequestId);
    partnerRequest.setActive(false);
    this.save(partnerRequest);
  }

  @Override
  public boolean existPartnerRequest(long partnerRequestID) {
    PartnerRequest partnerRequest = this.find(partnerRequestID);
    if (partnerRequest == null) {
      return false;
    }
    return true;

  }

  @Override
  public PartnerRequest find(long id) {
    return super.find(PartnerRequest.class, id);

  }

  @Override
  public List<PartnerRequest> findAll() {
    String query = "from " + PartnerRequest.class.getName() + " where is_active=1";
    List<PartnerRequest> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PartnerRequest findForUpdate(long id) {
    Session session = this.getSessionFactory().getCurrentSession();
    PartnerRequest partnerRequest = session.get(PartnerRequest.class, id);
    if (partnerRequest == null) {
      return null;
    }
    // The session may already hold a stale copy (OpenSessionInView): re-read it under the row lock.
    session.refresh(partnerRequest, LockMode.PESSIMISTIC_WRITE);
    return partnerRequest;
  }

  @Override
  public PartnerRequest save(PartnerRequest partnerRequest) {
    if (partnerRequest.getId() == null) {
      partnerRequest = super.saveEntity(partnerRequest);
    } else {
      partnerRequest = super.update(partnerRequest);
    }


    return partnerRequest;
  }


}