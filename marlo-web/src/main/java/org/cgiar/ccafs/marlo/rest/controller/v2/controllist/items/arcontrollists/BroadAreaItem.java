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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists;

import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExpenditureCategoryManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExpenditureCategory;
import org.cgiar.ccafs.marlo.rest.dto.BroadAreaDTO;
import org.cgiar.ccafs.marlo.rest.mappers.BroadAreaMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class BroadAreaItem<T> {

	private ReportSynthesisExpenditureCategoryManager reportSynthesisExpenditureCategoryManager;
	private BroadAreaMapper broadAreaMapper;

	@Inject
	public BroadAreaItem(ReportSynthesisExpenditureCategoryManager reportSynthesisExpenditureCategoryManager,
			BroadAreaMapper broadAreaMapper) {
		this.reportSynthesisExpenditureCategoryManager = reportSynthesisExpenditureCategoryManager;
		this.broadAreaMapper = broadAreaMapper;
	}

	/**
	 * Find a Broad area Item MARLO id
	 * 
	 * @param id
	 * @return a BroadAreaDTO with the broad area Item
	 */
	public ResponseEntity<BroadAreaDTO> findBroadAreaById(Long id) {
		ReportSynthesisExpenditureCategory reportSynthesisExpenditureCategory = this.reportSynthesisExpenditureCategoryManager
				.getReportSynthesisExpenditureCategoryById(id);

		return Optional.ofNullable(reportSynthesisExpenditureCategory)
				.map(this.broadAreaMapper::reportSynthesisExpenditureCategoryToBroadAreaDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the Broad Areas Items *
	 * 
	 * @return a List of BroadAreaDTO with all the Broad Areas Items.
	 */
	public List<BroadAreaDTO> getAllBroadAreas() {
		if (this.reportSynthesisExpenditureCategoryManager.findAll() != null) {
			List<ReportSynthesisExpenditureCategory> reportSynthesisExpenditureCategories = new ArrayList<>(
					this.reportSynthesisExpenditureCategoryManager.findAll());

			List<BroadAreaDTO> broadAreaDTOs = reportSynthesisExpenditureCategories.stream()
					.map(reportSynthesisExpenditureCategoryEntity -> this.broadAreaMapper
							.reportSynthesisExpenditureCategoryToBroadAreaDTO(reportSynthesisExpenditureCategoryEntity))
					.collect(Collectors.toList());
			return broadAreaDTOs;
		} else {
			return null;
		}
	}

}
