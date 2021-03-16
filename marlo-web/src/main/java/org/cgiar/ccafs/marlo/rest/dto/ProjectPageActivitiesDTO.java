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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

public class ProjectPageActivitiesDTO {

  @ApiModelProperty(notes = "Activity ID", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Activity Title", position = 2)
  private String title;

  @ApiModelProperty(notes = "Activity Description", position = 3)
  private String description;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @ApiModelProperty(notes = "Activity Start Date", position = 4)
  private Date startDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @ApiModelProperty(notes = "Activity End Date", position = 5)
  private Date endDate;

  @ApiModelProperty(notes = "Activity Status", position = 6)
  private String status;


  public String getDescription() {
    return description;
  }


  public Date getEndDate() {
    return endDate;
  }


  public Long getId() {
    return id;
  }


  public Date getStartDate() {
    return startDate;
  }


  public String getStatus() {
    return status;
  }


  public String getTitle() {
    return title;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


  public void setStatus(String status) {
    this.status = status;
  }


  public void setTitle(String title) {
    this.title = title;
  }

}
