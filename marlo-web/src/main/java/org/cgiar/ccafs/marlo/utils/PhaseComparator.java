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

package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Comparator;

import org.apache.commons.collections4.comparators.ComparatorChain;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class PhaseComparator implements Comparator<Phase> {

  private static class Holder {

    public static PhaseComparator instance = new PhaseComparator();
  }

  public static PhaseComparator getInstance() {
    return Holder.instance;
  }

  private ComparatorChain<Phase> chain;

  private PhaseComparator() {
    this.chain = new ComparatorChain<>();
    this.chain.addComparator(Comparator.comparingInt(Phase::getYear));
    this.chain.addComparator(Comparator
      .nullsLast((p1, p2) -> PhaseNames.getByName(p1.getName()).compareTo(PhaseNames.getByName(p2.getName()))));
  }

  @Override
  public int compare(Phase o1, Phase o2) {
    return this.chain.compare(o1, o2);
  }

}
