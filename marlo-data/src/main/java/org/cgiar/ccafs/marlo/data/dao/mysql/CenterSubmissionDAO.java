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

import org.cgiar.ccafs.marlo.data.dao.ICenterSubmissionDAO;
import org.cgiar.ccafs.marlo.data.model.CenterSubmission;

import java.util.List;

import com.google.inject.Inject;

public class CenterSubmissionDAO implements ICenterSubmissionDAO {

  private StandardDAO dao;

  @Inject
  public CenterSubmissionDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteSubmission(long submissionId) {
    CenterSubmission submission = this.find(submissionId);
    return dao.delete(submission);
  }

  @Override
  public boolean existSubmission(long submissionID) {
    CenterSubmission submission = this.find(submissionID);
    if (submission == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterSubmission find(long id) {
    return dao.find(CenterSubmission.class, id);

  }

  @Override
  public List<CenterSubmission> findAll() {
    String query = "from " + CenterSubmission.class.getName();
    List<CenterSubmission> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterSubmission> getSubmissionsByUserId(long userId) {
    String query = "from " + CenterSubmission.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterSubmission submission) {
    if (submission.getId() == null) {
      dao.save(submission);
    } else {
      dao.update(submission);
    }
    return submission.getId();
  }


}