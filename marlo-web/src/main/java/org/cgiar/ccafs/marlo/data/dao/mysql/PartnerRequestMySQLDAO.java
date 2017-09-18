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

import com.google.inject.Inject;

public class PartnerRequestMySQLDAO implements PartnerRequestDAO {

  private StandardDAO dao;

  @Inject
  public PartnerRequestMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deletePartnerRequest(long partnerRequestId) {
    PartnerRequest partnerRequest = this.find(partnerRequestId);
    partnerRequest.setActive(false);
    return this.save(partnerRequest) > 0;
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
    return dao.find(PartnerRequest.class, id);

  }

  @Override
  public List<PartnerRequest> findAll() {
    String query = "from " + PartnerRequest.class.getName() + " where is_active=1";
    List<PartnerRequest> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(PartnerRequest partnerRequest) {
    if (partnerRequest.getId() == null) {
      dao.save(partnerRequest);
    } else {
      dao.update(partnerRequest);
    }


    return partnerRequest.getId();
  }


}