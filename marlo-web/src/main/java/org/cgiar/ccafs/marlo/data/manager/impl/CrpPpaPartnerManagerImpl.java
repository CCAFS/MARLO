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

import org.cgiar.ccafs.marlo.data.dao.CrpPpaPartnerDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpPpaPartnerManagerImpl implements CrpPpaPartnerManager {

  private CrpPpaPartnerDAO crpPpaPartnerDao;
  private PhaseDAO phaseMySQLDAO;

  @Inject
  public CrpPpaPartnerManagerImpl(CrpPpaPartnerDAO crpPpaPartnerDao, PhaseDAO phaseMySQLDAO) {
    this.crpPpaPartnerDao = crpPpaPartnerDao;
    this.phaseMySQLDAO = phaseMySQLDAO;
  }

  private void addCrpPpaPartnerPhase(Phase next, long crpID, CrpPpaPartner crpPpaPartner) {
    Phase phase = phaseMySQLDAO.find(next.getId());
    List<CrpPpaPartner> outcomes = phase.getCrpPpaPartner().stream()
      .filter(c -> c.isActive()
        && c.getInstitution().getId().longValue() == crpPpaPartner.getInstitution().getId().longValue()
        && c.getCrp().getId().longValue() == crpID)
      .collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && outcomes.isEmpty()) {
      CrpPpaPartner crpPpaPartnerAdd = new CrpPpaPartner();
      crpPpaPartnerAdd.setActive(true);
      crpPpaPartnerAdd.setActiveSince(crpPpaPartner.getActiveSince());
      crpPpaPartnerAdd.setCreatedBy(crpPpaPartner.getCreatedBy());
      crpPpaPartnerAdd.setModificationJustification(crpPpaPartner.getModificationJustification());
      crpPpaPartnerAdd.setModifiedBy(crpPpaPartner.getModifiedBy());
      crpPpaPartnerAdd.setPhase(phase);
      crpPpaPartnerAdd.setCrp(crpPpaPartner.getCrp());
      crpPpaPartnerAdd.setInstitution(crpPpaPartner.getInstitution());
      crpPpaPartnerDao.save(crpPpaPartnerAdd);
    }

    if (phase.getNext() != null) {
      this.addCrpPpaPartnerPhase(phase.getNext(), crpID, crpPpaPartner);
    }


  }

  @Override
  public void deleteCrpPpaPartner(long crpPpaPartnerId) {
    CrpPpaPartner crpPpaPartner = this.getCrpPpaPartnerById(crpPpaPartnerId);
    crpPpaPartner.setActive(false);
    this.saveCrpPpaPartner(crpPpaPartner);

    if (crpPpaPartner.getPhase().getNext() != null) {
      this.deleteCrpPpaPartnerPhase(crpPpaPartner.getPhase().getNext(), crpPpaPartner.getCrp().getId(), crpPpaPartner);
    }
  }

  private void deleteCrpPpaPartnerPhase(Phase next, long crpID, CrpPpaPartner crpPpaPartner) {
    Phase phase = phaseMySQLDAO.find(next.getId());
    List<CrpPpaPartner> partners = phase.getCrpPpaPartner().stream()
      .filter(c -> c.isActive()
        && c.getInstitution().getId().longValue() == crpPpaPartner.getInstitution().getId().longValue()
        && c.getCrp().getId().longValue() == crpID)
      .collect(Collectors.toList());

    for (CrpPpaPartner crpPpaPartnersDB : partners) {
      crpPpaPartnerDao.deleteCrpPpaPartner(crpPpaPartnersDB.getId());
    }
    if (phase.getNext() != null) {
      this.deleteCrpPpaPartnerPhase(phase.getNext(), crpID, crpPpaPartner);
    }
  }

  @Override
  public boolean existCrpPpaPartner(long crpPpaPartnerId) {
    return crpPpaPartnerDao.existCrpPpaPartner(crpPpaPartnerId);
  }

  @Override
  public List<CrpPpaPartner> findAll() {
    return crpPpaPartnerDao.findAll();
  }

  @Override
  public CrpPpaPartner getCrpPpaPartnerById(long crpPpaPartnerId) {
    return crpPpaPartnerDao.find(crpPpaPartnerId);
  }

  @Override
  public CrpPpaPartner saveCrpPpaPartner(CrpPpaPartner crpPpaPartner) {
    CrpPpaPartner resultDao = crpPpaPartnerDao.save(crpPpaPartner);
    if (crpPpaPartner.getPhase().getNext() != null) {
      this.addCrpPpaPartnerPhase(crpPpaPartner.getPhase().getNext(), crpPpaPartner.getCrp().getId(), crpPpaPartner);
    }
    return resultDao;


  }

}
