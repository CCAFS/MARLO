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

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterSubmissionDAO extends AbstractMarloDAO<CenterSubmission, Long> implements ICenterSubmissionDAO {


  @Inject
  public CenterSubmissionDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSubmission(long submissionId) {
    CenterSubmission submission = this.find(submissionId);
    super.delete(submission);
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
    return super.find(CenterSubmission.class, id);

  }

  @Override
  public List<CenterSubmission> findAll() {
    String query = "from " + CenterSubmission.class.getName();
    List<CenterSubmission> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterSubmission> getSubmissionsByUserId(long userId) {
    String query = "from " + CenterSubmission.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterSubmission save(CenterSubmission submission) {
    if (submission.getId() == null) {
      super.saveEntity(submission);
    } else {
      submission = super.update(submission);
    }
    return submission;
  }


}