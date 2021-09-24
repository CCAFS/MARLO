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

package org.cgiar.ccafs.marlo.data.dto;

import java.math.BigDecimal;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ImpactPathwaysClusterDTO {

  private String flagshipAcronym;
  private String clusterIdentifier;
  private String clusterTitle;
  private String clusterLeaders;
  private String keyOutputStatement;
  private BigDecimal keyOutputContribution;
  private String outcomes;

  public ImpactPathwaysClusterDTO() {
  }

  public ImpactPathwaysClusterDTO(String flagshipAcronym, String clusterIdentifier, String clusterTitle,
    String clusterLeaders, String keyOutputStatement, BigDecimal keyOutputContribution, String outcomes) {
    super();
    this.flagshipAcronym = flagshipAcronym;
    this.clusterIdentifier = clusterIdentifier;
    this.clusterTitle = clusterTitle;
    this.clusterLeaders = clusterLeaders;
    this.keyOutputStatement = keyOutputStatement;
    this.outcomes = outcomes;
    this.keyOutputContribution = keyOutputContribution;
  }


  public String getClusterIdentifier() {
    return clusterIdentifier;
  }

  public String getClusterLeaders() {
    return clusterLeaders;
  }

  public String getClusterTitle() {
    return clusterTitle;
  }

  public String getFlagshipAcronym() {
    return flagshipAcronym;
  }

  public BigDecimal getKeyOutputContribution() {
    return keyOutputContribution;
  }

  public String getKeyOutputStatement() {
    return keyOutputStatement;
  }

  public String getOutcomes() {
    return outcomes;
  }

  public void setClusterIdentifier(String clusterIdentifier) {
    this.clusterIdentifier = clusterIdentifier;
  }

  public void setClusterLeaders(String clusterLeaders) {
    this.clusterLeaders = clusterLeaders;
  }

  public void setClusterTitle(String clusterTitle) {
    this.clusterTitle = clusterTitle;
  }

  public void setFlagshipAcronym(String flagshipAcronym) {
    this.flagshipAcronym = flagshipAcronym;
  }

  public void setKeyOutputContribution(BigDecimal keyOutputContribution) {
    this.keyOutputContribution = keyOutputContribution;
  }

  public void setKeyOutputStatement(String keyOutputStatement) {
    this.keyOutputStatement = keyOutputStatement;
  }

  public void setOutcomes(String outcomes) {
    this.outcomes = outcomes;
  }

}
