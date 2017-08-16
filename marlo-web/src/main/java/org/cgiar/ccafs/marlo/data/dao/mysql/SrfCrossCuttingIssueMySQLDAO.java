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

import org.cgiar.ccafs.marlo.data.dao.SrfCrossCuttingIssueDAO;
import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class SrfCrossCuttingIssueMySQLDAO extends AbstractMarloDAO<SrfCrossCuttingIssue, Long> implements SrfCrossCuttingIssueDAO {


  @Inject
  public SrfCrossCuttingIssueMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteSrfCrossCuttingIssue(long srfCrossCuttingIssueId) {
    SrfCrossCuttingIssue srfCrossCuttingIssue = this.find(srfCrossCuttingIssueId);
    srfCrossCuttingIssue.setActive(false);
    return this.save(srfCrossCuttingIssue) > 0;
  }

  @Override
  public boolean existSrfCrossCuttingIssue(long srfCrossCuttingIssueID) {
    SrfCrossCuttingIssue srfCrossCuttingIssue = this.find(srfCrossCuttingIssueID);
    if (srfCrossCuttingIssue == null) {
      return false;
    }
    return true;

  }

  @Override
  public SrfCrossCuttingIssue find(long id) {
    return super.find(SrfCrossCuttingIssue.class, id);

  }

  @Override
  public List<SrfCrossCuttingIssue> findAll() {
    String query = "from " + SrfCrossCuttingIssue.class.getName() + " where is_active=1";
    List<SrfCrossCuttingIssue> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(SrfCrossCuttingIssue srfCrossCuttingIssue) {
    if (srfCrossCuttingIssue.getId() == null) {
      super.saveEntity(srfCrossCuttingIssue);
    } else {
      super.update(srfCrossCuttingIssue);
    }
    return srfCrossCuttingIssue.getId();
  }


}