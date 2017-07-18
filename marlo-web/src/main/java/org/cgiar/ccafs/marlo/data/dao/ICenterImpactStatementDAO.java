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

import org.cgiar.ccafs.marlo.data.dao.mysql.CenterImpactStatementDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpactStatement;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterImpactStatementDAO.class)
public interface ICenterImpactStatementDAO {

  /**
   * This method removes a specific researchImpactStatement value from the database.
   * 
   * @param researchImpactStatementId is the researchImpactStatement identifier.
   * @return true if the researchImpactStatement was successfully deleted, false otherwise.
   */
  public boolean deleteResearchImpactStatement(long researchImpactStatementId);

  /**
   * This method validate if the researchImpactStatement identify with the given id exists in the system.
   * 
   * @param researchImpactStatementID is a researchImpactStatement identifier.
   * @return true if the researchImpactStatement exists, false otherwise.
   */
  public boolean existResearchImpactStatement(long researchImpactStatementID);

  /**
   * This method gets a researchImpactStatement object by a given researchImpactStatement identifier.
   * 
   * @param researchImpactStatementID is the researchImpactStatement identifier.
   * @return a CenterImpactStatement object.
   */
  public CenterImpactStatement find(long id);

  /**
   * This method gets a list of researchImpactStatement that are active
   * 
   * @return a list from CenterImpactStatement null if no exist records
   */
  public List<CenterImpactStatement> findAll();


  /**
   * This method gets a list of researchImpactStatements belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchImpactStatements or null if the user is invalid or not have roles.
   */
  public List<CenterImpactStatement> getResearchImpactStatementsByUserId(long userId);

  /**
   * This method saves the information of the given researchImpactStatement
   * 
   * @param researchImpactStatement - is the researchImpactStatement object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchImpactStatement
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterImpactStatement researchImpactStatement);

  /**
   * This method saves the information of the given researchImpactStatement
   * 
   * @param researchImpactStatement - is the researchImpactStatement object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchImpactStatement
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterImpactStatement researchImpactStatement, String actionName, List<String> relationsName);
}
