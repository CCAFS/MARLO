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

import org.cgiar.ccafs.marlo.data.dao.GlobalUnitDAO;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class GlobalUnitManagerImpl implements GlobalUnitManager {

	private GlobalUnitDAO globalUnitDAO;

	@Inject
	public GlobalUnitManagerImpl(GlobalUnitDAO globalUnitDAO) {
		this.globalUnitDAO = globalUnitDAO;

	}

	@Override
	public List<GlobalUnit> crpUsers(String emai) {
		return this.globalUnitDAO.crpUsers(emai);
	}

	@Override
	public void deleteGlobalUnit(long globalUnitId) {

		this.globalUnitDAO.deleteGlobalUnit(globalUnitId);
	}

	@Override
	public boolean existGlobalUnit(long globalUnitID) {

		return this.globalUnitDAO.existGlobalUnit(globalUnitID);
	}

	@Override
	public List<GlobalUnit> findAll() {

		return this.globalUnitDAO.findAll();

	}

	@Override
	public GlobalUnit findGlobalUnitByAcronym(String acronym) {

		return this.globalUnitDAO.findGlobalUnitByAcronym(acronym);
	}

	@Override
	public GlobalUnit findGlobalUnitBySMOCode(String smoCode) {

		return this.globalUnitDAO.findGlobalUnitBySMOCode(smoCode);
	}

	@Override
	public GlobalUnit getGlobalUnitById(long globalUnitID) {

		return this.globalUnitDAO.find(globalUnitID);
	}

	@Override
	public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {

		return this.globalUnitDAO.save(globalUnit);
	}

}
