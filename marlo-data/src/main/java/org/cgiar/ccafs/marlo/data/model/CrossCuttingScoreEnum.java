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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public enum CrossCuttingScoreEnum {

  SIGNIFICANT("1", "1-Significant"), PRINCIPAL("2", "2-Principal");


  /**
   * Look for the CrossCuttingScoreEnum with id
   * 
   * @param id the id to search
   * @return Object CrossCuttingScoreEnum if no exist null
   */
  public static CrossCuttingScoreEnum getValue(int id) {
    CrossCuttingScoreEnum[] lst = CrossCuttingScoreEnum.values();
    for (CrossCuttingScoreEnum crossCuttingScoreEnum : lst) {
      if (crossCuttingScoreEnum.getScoreId().equals(String.valueOf(id))) {
        return crossCuttingScoreEnum;
      }
    }
    return null;
  }


  private String score;


  private String scoreId;


  private CrossCuttingScoreEnum(String scoreId, String score) {
    this.scoreId = scoreId;
    this.score = score;
  }

  public String getScore() {
    return score;
  }


  public String getScoreId() {
    return scoreId;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public void setScoreId(String scoreId) {
    this.scoreId = scoreId;
  }


}
