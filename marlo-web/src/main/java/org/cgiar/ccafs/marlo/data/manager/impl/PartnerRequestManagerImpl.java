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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.PartnerRequestDAO;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PartnerRequestManagerImpl implements PartnerRequestManager {


  private PartnerRequestDAO partnerRequestDAO;
  // Managers


  @Inject
  public PartnerRequestManagerImpl(PartnerRequestDAO partnerRequestDAO) {
    this.partnerRequestDAO = partnerRequestDAO;


  }

  @Override
  public void deletePartnerRequest(long partnerRequestId) {

    partnerRequestDAO.deletePartnerRequest(partnerRequestId);
  }

  @Override
  public boolean existPartnerRequest(long partnerRequestID) {

    return partnerRequestDAO.existPartnerRequest(partnerRequestID);
  }

  @Override
  public List<PartnerRequest> findAll() {

    return partnerRequestDAO.findAll();

  }

  @Override
  public PartnerRequest getPartnerRequestById(long partnerRequestID) {

    return partnerRequestDAO.find(partnerRequestID);
  }

  @Override
  public PartnerRequest savePartnerRequest(PartnerRequest partnerRequest) {

    return partnerRequestDAO.save(partnerRequest);
  }


}
