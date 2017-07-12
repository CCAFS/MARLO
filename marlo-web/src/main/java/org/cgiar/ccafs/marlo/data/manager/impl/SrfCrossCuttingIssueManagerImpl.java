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


import org.cgiar.ccafs.marlo.data.dao.SrfCrossCuttingIssueDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfCrossCuttingIssueManager;
import org.cgiar.ccafs.marlo.data.model.SrfCrossCuttingIssue;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfCrossCuttingIssueManagerImpl implements SrfCrossCuttingIssueManager {


  private SrfCrossCuttingIssueDAO srfCrossCuttingIssueDAO;
  // Managers


  @Inject
  public SrfCrossCuttingIssueManagerImpl(SrfCrossCuttingIssueDAO srfCrossCuttingIssueDAO) {
    this.srfCrossCuttingIssueDAO = srfCrossCuttingIssueDAO;


  }

  @Override
  public boolean deleteSrfCrossCuttingIssue(long srfCrossCuttingIssueId) {

    return srfCrossCuttingIssueDAO.deleteSrfCrossCuttingIssue(srfCrossCuttingIssueId);
  }

  @Override
  public boolean existSrfCrossCuttingIssue(long srfCrossCuttingIssueID) {

    return srfCrossCuttingIssueDAO.existSrfCrossCuttingIssue(srfCrossCuttingIssueID);
  }

  @Override
  public List<SrfCrossCuttingIssue> findAll() {

    return srfCrossCuttingIssueDAO.findAll();

  }

  @Override
  public SrfCrossCuttingIssue getSrfCrossCuttingIssueById(long srfCrossCuttingIssueID) {

    return srfCrossCuttingIssueDAO.find(srfCrossCuttingIssueID);
  }

  @Override
  public long saveSrfCrossCuttingIssue(SrfCrossCuttingIssue srfCrossCuttingIssue) {

    return srfCrossCuttingIssueDAO.save(srfCrossCuttingIssue);
  }


}
