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


import org.cgiar.ccafs.marlo.data.dao.PowbEvidencePlannedStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbEvidencePlannedStudyManager;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbEvidencePlannedStudyManagerImpl implements PowbEvidencePlannedStudyManager {


  private PowbEvidencePlannedStudyDAO powbEvidencePlannedStudyDAO;
  // Managers


  @Inject
  public PowbEvidencePlannedStudyManagerImpl(PowbEvidencePlannedStudyDAO powbEvidencePlannedStudyDAO) {
    this.powbEvidencePlannedStudyDAO = powbEvidencePlannedStudyDAO;


  }

  @Override
  public void deletePowbEvidencePlannedStudy(long powbEvidencePlannedStudyId) {

    powbEvidencePlannedStudyDAO.deletePowbEvidencePlannedStudy(powbEvidencePlannedStudyId);
  }

  @Override
  public boolean existPowbEvidencePlannedStudy(long powbEvidencePlannedStudyID) {

    return powbEvidencePlannedStudyDAO.existPowbEvidencePlannedStudy(powbEvidencePlannedStudyID);
  }

  @Override
  public List<PowbEvidencePlannedStudy> findAll() {

    return powbEvidencePlannedStudyDAO.findAll();

  }

  @Override
  public PowbEvidencePlannedStudy getPowbEvidencePlannedStudyById(long powbEvidencePlannedStudyID) {

    return powbEvidencePlannedStudyDAO.find(powbEvidencePlannedStudyID);
  }

  @Override
  public PowbEvidencePlannedStudy savePowbEvidencePlannedStudy(PowbEvidencePlannedStudy powbEvidencePlannedStudy) {

    return powbEvidencePlannedStudyDAO.save(powbEvidencePlannedStudy);
  }


}
