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

import org.cgiar.ccafs.marlo.data.dao.AgreementDAO;
import org.cgiar.ccafs.marlo.data.manager.AgreementManager;
import org.cgiar.ccafs.marlo.data.model.Agreement;

import javax.inject.Named;
import javax.inject.Inject;


@Named
public class AgreementManagerImpl implements AgreementManager {

  private AgreementDAO agreementDAO;


  @Inject
  public AgreementManagerImpl(AgreementDAO agreementDAO) {
    this.agreementDAO = agreementDAO;
  }

  @Override
  public Agreement find(String id) {
    return this.agreementDAO.find(id);
  }

  @Override
  public Agreement save(Agreement agreement) {
    return this.agreementDAO.save(agreement);
  }

  @Override
  public Agreement update(Agreement agreement) {
    return this.agreementDAO.update(agreement);
  }

}
