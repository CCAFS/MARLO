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

package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.utils.APConfig;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class Test404 extends BaseAction {

  private static final long serialVersionUID = 1511557973574400249L;

  @Inject
  public Test404(APConfig config) {
    super(config);
  }


  @Override
  public String execute() throws Exception {
    System.out.println("Ejecuta");
    return INPUT;
  }

  @Override
  public void prepare() throws Exception {
    // TODO Auto-generated method stub
    super.prepare();
  }

}
