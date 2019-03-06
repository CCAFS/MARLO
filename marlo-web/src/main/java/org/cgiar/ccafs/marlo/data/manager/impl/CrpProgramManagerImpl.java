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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class CrpProgramManagerImpl implements CrpProgramManager {

	private CrpProgramDAO crpProgramDAO;
	// Managers

	@Inject
	public CrpProgramManagerImpl(CrpProgramDAO crpProgramDAO) {
		this.crpProgramDAO = crpProgramDAO;

	}

	@Override
	public void deleteCrpProgram(long crpProgramId) {

		this.crpProgramDAO.deleteCrpProgram(crpProgramId);
	}

	@Override
	public boolean existCrpProgram(long crpProgramID) {

		return this.crpProgramDAO.existCrpProgram(crpProgramID);
	}

	@Override
	public List<CrpProgram> findAll() {

		return this.crpProgramDAO.findAll();

	}

	@Override
	public List<CrpProgram> findCrpProgramsByType(long id, int programType) {
		return this.crpProgramDAO.findCrpProgramsByType(id, programType);
	}

	@Override
	public CrpProgram getCrpProgramById(long crpProgramID) {

		return this.crpProgramDAO.find(crpProgramID);
	}

	@Override
	public CrpProgram getCrpProgramBySmoCode(String smoCode) {

		return this.crpProgramDAO.findCrpProgramsBySmoCode(smoCode);
	}

	@Override
	public CrpProgram saveCrpProgram(CrpProgram crpProgram) {

		return this.crpProgramDAO.save(crpProgram);
	}

	@Override
	public CrpProgram saveCrpProgram(CrpProgram crpProgram, String actionName, List<String> relationsName,
			Phase phase) {

		return this.crpProgramDAO.save(crpProgram, actionName, relationsName, phase);
	}

}
