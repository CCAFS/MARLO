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

package org.cgiar.ccafs.marlo.data.model;

public class HomeIndicatorsGraphByTotalDTO {
	private String indicatorName;
	private long indicatorTotal;
	private String indicatorColor;

	public HomeIndicatorsGraphByTotalDTO() {

	}

	public HomeIndicatorsGraphByTotalDTO(String indicatorName, long indicatorTotal, String indicatorColor) {
		super();
		this.indicatorName = indicatorName;
		this.indicatorTotal = indicatorTotal;
		this.indicatorColor = indicatorColor;
	}

	public String getIndicatorColor() {
		return indicatorColor;
	}

	public String getIndicatorName() {
		return indicatorName;
	}

	public long getIndicatorTotal() {
		return indicatorTotal;
	}

	public void setIndicatorColor(String indicatorColor) {
		this.indicatorColor = indicatorColor;
	}

	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}

	public void setIndicatorTotal(long indicatorTotal) {
		this.indicatorTotal = indicatorTotal;
	}
}
