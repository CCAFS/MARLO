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


import org.cgiar.ccafs.marlo.data.dao.PowbTocDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbTocManager;
import org.cgiar.ccafs.marlo.data.model.PowbToc;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbTocManagerImpl implements PowbTocManager {


  private PowbTocDAO powbTocDAO;
  // Managers


  @Inject
  public PowbTocManagerImpl(PowbTocDAO powbTocDAO) {
    this.powbTocDAO = powbTocDAO;


  }

  @Override
  public void deletePowbToc(long powbTocId) {

    powbTocDAO.deletePowbToc(powbTocId);
  }

  @Override
  public boolean existPowbToc(long powbTocID) {

    return powbTocDAO.existPowbToc(powbTocID);
  }

  @Override
  public List<PowbToc> findAll() {

    return powbTocDAO.findAll();

  }

  @Override
  public PowbToc getPowbTocById(long powbTocID) {

    return powbTocDAO.find(powbTocID);
  }

  @Override
  public PowbToc savePowbToc(PowbToc powbToc) {

    return powbTocDAO.save(powbToc);
  }


}
