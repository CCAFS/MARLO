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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ISrfCrossCuttingIssueDAO;
import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;

import java.util.List;

import com.google.inject.Inject;

public class SrfCrossCuttingIssueDAO implements ISrfCrossCuttingIssueDAO {

  private StandardDAO dao;

  @Inject
  public SrfCrossCuttingIssueDAO(StandardDAO dao) {
    this.dao = dao;
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
    return dao.find(SrfCrossCuttingIssue.class, id);

  }

  @Override
  public List<SrfCrossCuttingIssue> findAll() {
    String query = "from " + SrfCrossCuttingIssue.class.getName();
    List<SrfCrossCuttingIssue> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<SrfCrossCuttingIssue> getSrfCrossCuttingIssuesByUserId(long userId) {
    String query = "from " + SrfCrossCuttingIssue.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(SrfCrossCuttingIssue srfCrossCuttingIssue) {
    if (srfCrossCuttingIssue.getId() == null) {
      dao.save(srfCrossCuttingIssue);
    } else {
      dao.update(srfCrossCuttingIssue);
    }
    return srfCrossCuttingIssue.getId();
  }

  @Override
  public long save(SrfCrossCuttingIssue srfCrossCuttingIssue, String actionName, List<String> relationsName) {
    if (srfCrossCuttingIssue.getId() == null) {
      dao.save(srfCrossCuttingIssue, actionName, relationsName);
    } else {
      dao.update(srfCrossCuttingIssue, actionName, relationsName);
    }
    return srfCrossCuttingIssue.getId();
  }


}