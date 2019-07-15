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


import org.cgiar.ccafs.marlo.data.dao.GeneralAcronymDAO;
import org.cgiar.ccafs.marlo.data.manager.GeneralAcronymManager;
import org.cgiar.ccafs.marlo.data.model.GeneralAcronym;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class GeneralAcronymManagerImpl implements GeneralAcronymManager {


  private GeneralAcronymDAO generalAcronymDAO;
  // Managers


  @Inject
  public GeneralAcronymManagerImpl(GeneralAcronymDAO generalAcronymDAO) {
    this.generalAcronymDAO = generalAcronymDAO;


  }

  @Override
  public void deleteGeneralAcronym(long generalAcronymId) {

    this.generalAcronymDAO.deleteGeneralAcronym(generalAcronymId);
  }

  @Override
  public boolean existGeneralAcronym(long generalAcronymID) {

    return this.generalAcronymDAO.existGeneralAcronym(generalAcronymID);
  }

  @Override
  public List<GeneralAcronym> findAll() {

    return this.generalAcronymDAO.findAll();

  }

  @Override
  public List<GeneralAcronym> getGeneralAcronymByAcronym(String acronym) {

    return this.generalAcronymDAO.findByAcronym(acronym);
  }

  @Override
  public GeneralAcronym getGeneralAcronymById(long generalAcronymID) {

    return this.generalAcronymDAO.find(generalAcronymID);
  }


  @Override
  public GeneralAcronym saveGeneralAcronym(GeneralAcronym generalAcronym) {

    return this.generalAcronymDAO.save(generalAcronym);
  }


}
