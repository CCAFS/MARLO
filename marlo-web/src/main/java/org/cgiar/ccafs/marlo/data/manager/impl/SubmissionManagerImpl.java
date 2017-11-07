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


import org.cgiar.ccafs.marlo.data.dao.SubmissionDAO;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.Submission;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SubmissionManagerImpl implements SubmissionManager {


  private SubmissionDAO submissionDAO;
  // Managers


  @Inject
  public SubmissionManagerImpl(SubmissionDAO submissionDAO) {
    this.submissionDAO = submissionDAO;


  }

  @Override
  public void deleteSubmission(long submissionId) {

    submissionDAO.deleteSubmission(submissionId);
  }

  @Override
  public boolean existSubmission(long submissionID) {

    return submissionDAO.existSubmission(submissionID);
  }

  @Override
  public List<Submission> findAll() {

    return submissionDAO.findAll();

  }

  @Override
  public Submission getSubmissionById(long submissionID) {

    return submissionDAO.find(submissionID);
  }

  @Override
  public Submission saveSubmission(Submission submission) {

    return submissionDAO.save(submission);
  }


}
