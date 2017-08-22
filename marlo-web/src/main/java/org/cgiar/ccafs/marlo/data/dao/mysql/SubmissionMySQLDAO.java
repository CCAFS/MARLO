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

import org.cgiar.ccafs.marlo.data.dao.SubmissionDAO;
import org.cgiar.ccafs.marlo.data.model.Submission;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class SubmissionMySQLDAO extends AbstractMarloDAO<Submission, Long> implements SubmissionDAO {


  @Inject
  public SubmissionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSubmission(long submissionId) {
    Submission submission = this.find(submissionId);

    super.delete(submission);
  }

  @Override
  public boolean existSubmission(long submissionID) {
    Submission submission = this.find(submissionID);
    if (submission == null) {
      return false;
    }
    return true;

  }

  @Override
  public Submission find(long id) {
    return super.find(Submission.class, id);

  }

  @Override
  public List<Submission> findAll() {
    String query = "from " + Submission.class.getName() + "";
    List<Submission> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return new ArrayList<Submission>();

  }

  @Override
  public Submission save(Submission submission) {
    if (submission.getId() == null) {
      super.saveEntity(submission);
    } else {
      submission = super.update(submission);
    }


    return submission;
  }


}