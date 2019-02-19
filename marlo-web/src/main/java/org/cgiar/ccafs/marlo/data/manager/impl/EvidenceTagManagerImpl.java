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


import org.cgiar.ccafs.marlo.data.dao.EvidenceTagDAO;
import org.cgiar.ccafs.marlo.data.manager.EvidenceTagManager;
import org.cgiar.ccafs.marlo.data.model.EvidenceTag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class EvidenceTagManagerImpl implements EvidenceTagManager {


  private EvidenceTagDAO evidenceTagDAO;
  // Managers


  @Inject
  public EvidenceTagManagerImpl(EvidenceTagDAO evidenceTagDAO) {
    this.evidenceTagDAO = evidenceTagDAO;


  }

  @Override
  public void deleteEvidenceTag(long evidenceTagId) {

    evidenceTagDAO.deleteEvidenceTag(evidenceTagId);
  }

  @Override
  public boolean existEvidenceTag(long evidenceTagID) {

    return evidenceTagDAO.existEvidenceTag(evidenceTagID);
  }

  @Override
  public List<EvidenceTag> findAll() {

    return evidenceTagDAO.findAll();

  }

  @Override
  public EvidenceTag getEvidenceTagById(long evidenceTagID) {

    return evidenceTagDAO.find(evidenceTagID);
  }

  @Override
  public EvidenceTag saveEvidenceTag(EvidenceTag evidenceTag) {

    return evidenceTagDAO.save(evidenceTag);
  }


}
