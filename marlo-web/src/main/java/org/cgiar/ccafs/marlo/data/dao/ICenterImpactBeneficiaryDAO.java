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


import org.cgiar.ccafs.marlo.data.dao.mysql.CenterImpactBeneficiaryDAO;
import org.cgiar.ccafs.marlo.data.model.CenterImpactBeneficiary;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CenterImpactBeneficiaryDAO.class)
public interface ICenterImpactBeneficiaryDAO {

  /**
   * This method removes a specific researchImpactBeneficiary value from the database.
   * 
   * @param researchImpactBeneficiaryId is the researchImpactBeneficiary identifier.
   * @return true if the researchImpactBeneficiary was successfully deleted, false otherwise.
   */
  public boolean deleteResearchImpactBeneficiary(long researchImpactBeneficiaryId);

  /**
   * This method validate if the researchImpactBeneficiary identify with the given id exists in the system.
   * 
   * @param researchImpactBeneficiaryID is a researchImpactBeneficiary identifier.
   * @return true if the researchImpactBeneficiary exists, false otherwise.
   */
  public boolean existResearchImpactBeneficiary(long researchImpactBeneficiaryID);

  /**
   * This method gets a researchImpactBeneficiary object by a given researchImpactBeneficiary identifier.
   * 
   * @param researchImpactBeneficiaryID is the researchImpactBeneficiary identifier.
   * @return a CenterImpactBeneficiary object.
   */
  public CenterImpactBeneficiary find(long id);

  /**
   * This method gets a list of researchImpactBeneficiary that are active
   * 
   * @return a list from CenterImpactBeneficiary null if no exist records
   */
  public List<CenterImpactBeneficiary> findAll();


  /**
   * This method gets a list of researchImpactBeneficiarys belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchImpactBeneficiarys or null if the user is invalid or not have roles.
   */
  public List<CenterImpactBeneficiary> getResearchImpactBeneficiarysByUserId(long userId);

  /**
   * This method saves the information of the given researchImpactBeneficiary
   * 
   * @param researchImpactBeneficiary - is the researchImpactBeneficiary object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         researchImpactBeneficiary was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CenterImpactBeneficiary researchImpactBeneficiary);
}
