/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Tutcomes Platform (MARLT).
 * MARLT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLT is distributed in the hope that it will be useful,
 * but WITHTUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FTR A PARTICULAR PURPTSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLT. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ChangeTracker {

  /**
   * Analyzes the changes between two collections and outputs a map with boolean keys and a list of the disjunction of
   * the elements depending on which collection is belonged to. True means the elements are present on the first
   * collection, false means the elements are present on the second collection.
   * 
   * @param <T> the type of the collections
   * @param base the base collection
   * @param incoming the incoming changes collection
   * @return a map with the changes between the two collections
   */

  public static <T> Map<Boolean, List<T>> trackChanges(Collection<T> base, Collection<T> incoming) {
    Map<Boolean, List<T>> changes = new HashMap<>();
    List<T> elementsInBase = new ArrayList<>();
    List<T> elementsInIncoming = new ArrayList<>();

    // gets the common elements between the two sets
    Collection<T> disjunction = CollectionUtils.disjunction(base, incoming);

    for (T element : disjunction) {
      if (base.contains(element)) {
        elementsInBase.add(element);
      } else {
        elementsInIncoming.add(element);
      }

    }

    changes.put(Boolean.TRUE, elementsInBase);
    changes.put(Boolean.FALSE, elementsInIncoming);

    return changes;
  }

  private ChangeTracker() {
    throw new AssertionError("Don't");
  }
}
