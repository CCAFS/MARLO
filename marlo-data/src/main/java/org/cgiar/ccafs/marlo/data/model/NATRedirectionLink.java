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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class NATRedirectionLink extends MarloBaseEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 5158413342847662497L;

  private String indicatorName;
  private Long indicatorId;
  private String redirectionUrl;


  public NATRedirectionLink() {
  }


  public Long getIndicatorId() {
    return indicatorId;
  }

  public String getIndicatorName() {
    return indicatorName;
  }

  public String getRedirectionUrl() {
    return redirectionUrl;
  }

  public void setIndicatorId(Long indicatorId) {
    this.indicatorId = indicatorId;
  }

  public void setIndicatorName(String indicatorName) {
    this.indicatorName = indicatorName;
  }

  public void setRedirectionUrl(String redirectionUrl) {
    this.redirectionUrl = redirectionUrl;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", this.getId()).append("indicatorName", this.getIndicatorName())
      .append("indicatorId", this.getIndicatorId()).append("redirectionUrl", this.getRedirectionUrl()).toString();
  }

}
