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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterSubmissionDAO;
import org.cgiar.ccafs.marlo.data.model.CenterSubmission;
import org.cgiar.ccafs.marlo.data.service.ICenterSubmissionService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterSubmissionService implements ICenterSubmissionService {


  private ICenterSubmissionDAO submissionDAO;

  // Managers


  @Inject
  public CenterSubmissionService(ICenterSubmissionDAO submissionDAO) {
    this.submissionDAO = submissionDAO;


  }

  @Override
  public boolean deleteSubmission(long submissionId) {

    return submissionDAO.deleteSubmission(submissionId);
  }

  @Override
  public boolean existSubmission(long submissionID) {

    return submissionDAO.existSubmission(submissionID);
  }

  @Override
  public List<CenterSubmission> findAll() {

    return submissionDAO.findAll();

  }

  @Override
  public CenterSubmission getSubmissionById(long submissionID) {

    return submissionDAO.find(submissionID);
  }

  @Override
  public List<CenterSubmission> getSubmissionsByUserId(Long userId) {
    return submissionDAO.getSubmissionsByUserId(userId);
  }

  @Override
  public long saveSubmission(CenterSubmission submission) {

    return submissionDAO.save(submission);
  }


}
