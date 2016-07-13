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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.dao.mysql.SubmissionMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.Submission;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(SubmissionMySQLDAO.class)
public interface SubmissionDAO {

  /**
   * This method removes a specific submission value from the database.
   * 
   * @param submissionId is the submission identifier.
   * @return true if the submission was successfully deleted, false otherwise.
   */
  public boolean deleteSubmission(long submissionId);

  /**
   * This method validate if the submission identify with the given id exists in the system.
   * 
   * @param submissionID is a submission identifier.
   * @return true if the submission exists, false otherwise.
   */
  public boolean existSubmission(long submissionID);

  /**
   * This method gets a submission object by a given submission identifier.
   * 
   * @param submissionID is the submission identifier.
   * @return a Submission object.
   */
  public Submission find(long id);

  /**
   * This method gets a list of submission that are active
   * 
   * @return a list from Submission null if no exist records
   */
  public List<Submission> findAll();


  /**
   * This method saves the information of the given submission
   * 
   * @param submission - is the submission object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the submission was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(Submission submission);
}
