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

import org.cgiar.ccafs.marlo.data.dao.PartnerDivisionDAO;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;

import java.util.List;

import com.google.inject.Inject;

public class PartnerDivisionMySQLDAO implements PartnerDivisionDAO {

  private StandardDAO dao;

  @Inject
  public PartnerDivisionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deletePartnerDivision(long partnerDivisionId) {
    PartnerDivision partnerDivision = this.find(partnerDivisionId);
    partnerDivision.setActive(false);
    return this.save(partnerDivision) > 0;
  }

  @Override
  public boolean existPartnerDivision(long partnerDivisionID) {
    PartnerDivision partnerDivision = this.find(partnerDivisionID);
    if (partnerDivision == null) {
      return false;
    }
    return true;

  }

  @Override
  public PartnerDivision find(long id) {
    return dao.find(PartnerDivision.class, id);

  }

  @Override
  public List<PartnerDivision> findAll() {
    String query = "from " + PartnerDivision.class.getName() + " where is_active=1";
    List<PartnerDivision> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(PartnerDivision partnerDivision) {
    if (partnerDivision.getId() == null) {
      dao.save(partnerDivision);
    } else {
      dao.update(partnerDivision);
    }


    return partnerDivision.getId();
  }


}