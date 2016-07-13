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

import org.cgiar.ccafs.marlo.data.dao.SrfSloIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;

import java.util.List;

import com.google.inject.Inject;

public class SrfSloIndicatorMySQLDAO implements SrfSloIndicatorDAO {

  private StandardDAO dao;

  @Inject
  public SrfSloIndicatorMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteSrfSloIndicator(long srfSloIndicatorId) {
    SrfSloIndicator srfSloIndicator = this.find(srfSloIndicatorId);
    srfSloIndicator.setActive(false);
    return this.save(srfSloIndicator) > 0;
  }

  @Override
  public boolean existSrfSloIndicator(long srfSloIndicatorID) {
    SrfSloIndicator srfSloIndicator = this.find(srfSloIndicatorID);
    if (srfSloIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public SrfSloIndicator find(long id) {
    return dao.find(SrfSloIndicator.class, id);

  }

  @Override
  public List<SrfSloIndicator> findAll() {
    String query = "from " + SrfSloIndicator.class.getName() + " where is_active=1";
    List<SrfSloIndicator> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(SrfSloIndicator srfSloIndicator) {
    if (srfSloIndicator.getId() == null) {
      dao.save(srfSloIndicator);
    } else {
      dao.update(srfSloIndicator);
    }
    return srfSloIndicator.getId();
  }


}