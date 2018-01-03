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


import org.cgiar.ccafs.marlo.data.dao.PartnerDivisionDAO;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PartnerDivisionManagerImpl implements PartnerDivisionManager {


  private PartnerDivisionDAO partnerDivisionDAO;
  // Managers


  @Inject
  public PartnerDivisionManagerImpl(PartnerDivisionDAO partnerDivisionDAO) {
    this.partnerDivisionDAO = partnerDivisionDAO;


  }

  @Override
  public void deletePartnerDivision(long partnerDivisionId) {

    partnerDivisionDAO.deletePartnerDivision(partnerDivisionId);
  }

  @Override
  public boolean existPartnerDivision(long partnerDivisionID) {

    return partnerDivisionDAO.existPartnerDivision(partnerDivisionID);
  }

  @Override
  public List<PartnerDivision> findAll() {

    return partnerDivisionDAO.findAll();

  }

  @Override
  public PartnerDivision getPartnerDivisionById(long partnerDivisionID) {

    return partnerDivisionDAO.find(partnerDivisionID);
  }

  @Override
  public PartnerDivision savePartnerDivision(PartnerDivision partnerDivision) {

    return partnerDivisionDAO.save(partnerDivision);
  }


}
