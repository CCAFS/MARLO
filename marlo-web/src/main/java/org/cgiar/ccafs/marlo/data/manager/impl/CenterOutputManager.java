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


import org.cgiar.ccafs.marlo.data.dao.ICenterOutputDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterOutputManager implements ICenterOutputManager {


  private ICenterOutputDAO researchOutputDAO;

  // Managers


  @Inject
  public CenterOutputManager(ICenterOutputDAO researchOutputDAO) {
    this.researchOutputDAO = researchOutputDAO;


  }

  @Override
  public void deleteResearchOutput(long researchOutputId) {

    researchOutputDAO.deleteResearchOutput(researchOutputId);
  }

  @Override
  public boolean existResearchOutput(long researchOutputID) {

    return researchOutputDAO.existResearchOutput(researchOutputID);
  }

  @Override
  public List<CenterOutput> findAll() {

    return researchOutputDAO.findAll();

  }

  @Override
  public CenterOutput getResearchOutputById(long researchOutputID) {

    return researchOutputDAO.find(researchOutputID);
  }

  @Override
  public List<CenterOutput> getResearchOutputsByUserId(Long userId) {
    return researchOutputDAO.getResearchOutputsByUserId(userId);
  }

  @Override
  public CenterOutput saveResearchOutput(CenterOutput researchOutput) {

    return researchOutputDAO.save(researchOutput);
  }

  @Override
  public CenterOutput saveResearchOutput(CenterOutput output, String actionName, List<String> relationsName) {
    return researchOutputDAO.save(output, actionName, relationsName);
  }


}
