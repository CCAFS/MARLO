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

import org.cgiar.ccafs.marlo.data.dao.LocElementDAO;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class LocElementManagerImpl implements LocElementManager {

	private LocElementDAO locElementDAO;
	// Managers

	@Inject
	public LocElementManagerImpl(LocElementDAO locElementDAO) {
		this.locElementDAO = locElementDAO;

	}

	@Override
	public void deleteLocElement(long locElementId) {

		this.locElementDAO.deleteLocElement(locElementId);
	}

	@Override
	public boolean existLocElement(long locElementID) {

		return this.locElementDAO.existLocElement(locElementID);
	}

	@Override
	public List<LocElement> findAll() {

		return this.locElementDAO.findAll();

	}

	@Override
	public List<LocElement> findLocElementByParent(long parentId) {

		return this.locElementDAO.findLocElementByParent(parentId);
	}

	@Override
	public LocElement getLocElementById(long locElementID) {

		return this.locElementDAO.find(locElementID);
	}

	@Override
	public LocElement getLocElementByISOCode(String ISOCode) {

		return this.locElementDAO.findISOCode(ISOCode);
	}

	@Override
	public LocElement getLocElementByNumericISOCode(Long ISOCode) {

		return this.locElementDAO.findNumericISOCode(ISOCode);
	}

	@Override
	public LocElement saveLocElement(LocElement locElement) {

		return this.locElementDAO.save(locElement);
	}

}
