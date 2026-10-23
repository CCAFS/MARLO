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

package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ValueChain;

import java.util.List;


public interface ValueChainDAO {

  /**
   * This method gets a value chain by its identifier.
   *
   * @param id is the value chain identifier.
   * @return a ValueChain object, or null when it does not exist.
   */
  public ValueChain find(long id);

  /**
   * This method gets the active value chains in their display order.
   *
   * @return the active value chains, an empty list when there are none.
   */
  public List<ValueChain> findAll();
}
