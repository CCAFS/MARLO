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


import org.cgiar.ccafs.marlo.data.dao.PowbEvidenceDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbEvidenceManager;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbEvidenceManagerImpl implements PowbEvidenceManager {


  private PowbEvidenceDAO powbEvidenceDAO;
  // Managers


  @Inject
  public PowbEvidenceManagerImpl(PowbEvidenceDAO powbEvidenceDAO) {
    this.powbEvidenceDAO = powbEvidenceDAO;


  }

  @Override
  public void deletePowbEvidence(long powbEvidenceId) {

    powbEvidenceDAO.deletePowbEvidence(powbEvidenceId);
  }

  @Override
  public boolean existPowbEvidence(long powbEvidenceID) {

    return powbEvidenceDAO.existPowbEvidence(powbEvidenceID);
  }

  @Override
  public List<PowbEvidence> findAll() {

    return powbEvidenceDAO.findAll();

  }

  @Override
  public PowbEvidence getPowbEvidenceById(long powbEvidenceID) {

    return powbEvidenceDAO.find(powbEvidenceID);
  }

  @Override
  public PowbEvidence savePowbEvidence(PowbEvidence powbEvidence) {

    return powbEvidenceDAO.save(powbEvidence);
  }


}
