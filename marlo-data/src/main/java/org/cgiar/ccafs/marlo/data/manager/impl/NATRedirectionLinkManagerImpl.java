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


import org.cgiar.ccafs.marlo.data.dao.NATRedirectionLinkDAO;
import org.cgiar.ccafs.marlo.data.manager.NATRedirectionLinkManager;
import org.cgiar.ccafs.marlo.data.model.NATRedirectionLink;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class NATRedirectionLinkManagerImpl implements NATRedirectionLinkManager {


  private NATRedirectionLinkDAO NATRedirectionLinkDAO;
  // Managers


  @Inject
  public NATRedirectionLinkManagerImpl(NATRedirectionLinkDAO NATRedirectionLinkDAO) {
    this.NATRedirectionLinkDAO = NATRedirectionLinkDAO;


  }

  @Override
  public void deleteNATRedirectionLink(long NATRedirectionLinkId) {

    NATRedirectionLinkDAO.deleteNATRedirectionLink(NATRedirectionLinkId);
  }

  @Override
  public boolean existNATRedirectionLink(long NATRedirectionLinkID) {

    return NATRedirectionLinkDAO.existNATRedirectionLink(NATRedirectionLinkID);
  }

  @Override
  public List<NATRedirectionLink> findAll() {

    return NATRedirectionLinkDAO.findAll();

  }

  @Override
  public NATRedirectionLink getNATRedirectionLinkById(long NATRedirectionLinkID) {

    return NATRedirectionLinkDAO.find(NATRedirectionLinkID);
  }

  @Override
  public NATRedirectionLink saveNATRedirectionLink(NATRedirectionLink NATRedirectionLink) {

    return NATRedirectionLinkDAO.save(NATRedirectionLink);
  }


}
