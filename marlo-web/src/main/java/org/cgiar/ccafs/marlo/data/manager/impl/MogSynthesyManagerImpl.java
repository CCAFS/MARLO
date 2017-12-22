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


import org.cgiar.ccafs.marlo.data.dao.MogSynthesyDAO;
import org.cgiar.ccafs.marlo.data.manager.MogSynthesyManager;
import org.cgiar.ccafs.marlo.data.model.MogSynthesy;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class MogSynthesyManagerImpl implements MogSynthesyManager {


  private MogSynthesyDAO mogSynthesyDAO;
  // Managers


  @Inject
  public MogSynthesyManagerImpl(MogSynthesyDAO mogSynthesyDAO) {
    this.mogSynthesyDAO = mogSynthesyDAO;


  }

  @Override
  public void deleteMogSynthesy(long mogSynthesyId) {

    mogSynthesyDAO.deleteMogSynthesy(mogSynthesyId);
  }

  @Override
  public boolean existMogSynthesy(long mogSynthesyID) {

    return mogSynthesyDAO.existMogSynthesy(mogSynthesyID);
  }

  @Override
  public List<MogSynthesy> findAll() {

    return mogSynthesyDAO.findAll();

  }


  @Override
  public List<MogSynthesy> getMogSynthesis(long programId) {
    return mogSynthesyDAO.findMogSynthesis(programId);
  }

  @Override
  public List<MogSynthesy> getMogSynthesisRegions(long midoutcome, int year) {
    return mogSynthesyDAO.findMogSynthesisRegion(midoutcome, year);
  }

  @Override
  public MogSynthesy getMogSynthesyById(long mogSynthesyID) {

    return mogSynthesyDAO.find(mogSynthesyID);
  }

  @Override
  public MogSynthesy saveMogSynthesy(MogSynthesy mogSynthesy) {

    return mogSynthesyDAO.save(mogSynthesy);
  }


}
