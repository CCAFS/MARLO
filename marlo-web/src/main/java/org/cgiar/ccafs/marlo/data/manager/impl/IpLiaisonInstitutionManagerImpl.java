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


import org.cgiar.ccafs.marlo.data.dao.IpLiaisonInstitutionDAO;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class IpLiaisonInstitutionManagerImpl implements IpLiaisonInstitutionManager {


  private IpLiaisonInstitutionDAO ipLiaisonInstitutionDAO;
  // Managers


  @Inject
  public IpLiaisonInstitutionManagerImpl(IpLiaisonInstitutionDAO ipLiaisonInstitutionDAO) {
    this.ipLiaisonInstitutionDAO = ipLiaisonInstitutionDAO;


  }

  @Override
  public void deleteIpLiaisonInstitution(long ipLiaisonInstitutionId) {

    ipLiaisonInstitutionDAO.deleteIpLiaisonInstitution(ipLiaisonInstitutionId);
  }

  @Override
  public boolean existIpLiaisonInstitution(long ipLiaisonInstitutionID) {

    return ipLiaisonInstitutionDAO.existIpLiaisonInstitution(ipLiaisonInstitutionID);
  }

  @Override
  public List<IpLiaisonInstitution> findAll() {

    return ipLiaisonInstitutionDAO.findAll();

  }

  @Override
  public IpLiaisonInstitution findByIpProgram(long ipProgramID) {
    return ipLiaisonInstitutionDAO.findByIpProgram(ipProgramID);
  }

  @Override
  public IpLiaisonInstitution getIpLiaisonInstitutionById(long ipLiaisonInstitutionID) {

    return ipLiaisonInstitutionDAO.find(ipLiaisonInstitutionID);
  }

  @Override
  public List<IpLiaisonInstitution> getLiaisonInstitutionsCrpsIndicator() {
    return ipLiaisonInstitutionDAO.getLiaisonInstitutionsCrpsIndicator();
  }

  @Override
  public List<IpLiaisonInstitution> getLiaisonInstitutionSynthesisByMog() {
    List<IpLiaisonInstitution> liaisonInstitutions = new ArrayList<>();

    List<Map<String, Object>> view = ipLiaisonInstitutionDAO.getLiaisonInstitutionSynthesisByMog();

    if (view != null) {
      for (Map<String, Object> map : view) {
        liaisonInstitutions.add(this.getIpLiaisonInstitutionById(Long.parseLong(map.get("id").toString())));
      }
    }

    return liaisonInstitutions;
  }

  @Override
  public IpLiaisonInstitution save(IpLiaisonInstitution ipLiaisonInstitution, String section, List<String> relationsName) {
    return ipLiaisonInstitutionDAO.save(ipLiaisonInstitution, section, relationsName);
  }

  @Override
  public IpLiaisonInstitution saveIpLiaisonInstitution(IpLiaisonInstitution ipLiaisonInstitution) {

    return ipLiaisonInstitutionDAO.save(ipLiaisonInstitution);
  }


}
