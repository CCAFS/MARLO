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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * A request that gets no answer at all (connection refused, DNS failure, timeout) used to end in a
 * NullPointerException on the missing response, which hid the real failure in the logs of every caller (CGSpace,
 * Dataverse, IFPRI, ILRI, WOS metadata). Port 1 on the loopback address refuses the connection without any network.
 */
public class RestConnectionUtilNoResponseTest {

  private static final String UNREACHABLE = "http://127.0.0.1:1/metadata";

  @Test
  public void aDoiRequestWithNoResponseFailsWithTheRequestNotANullPointer() {
    IllegalStateException thrown =
      assertThrows(IllegalStateException.class, () -> new RestConnectionUtil().getJsonRestClientFromDOI(UNREACHABLE));
    assertEquals("The request to " + UNREACHABLE + " got no response", thrown.getMessage());
  }

  @Test
  public void aJsonRequestWithNoResponseFailsWithTheRequestNotANullPointer() {
    IllegalStateException thrown =
      assertThrows(IllegalStateException.class, () -> new RestConnectionUtil().getJsonRestClient(UNREACHABLE));
    assertEquals("The request to " + UNREACHABLE + " got no response", thrown.getMessage());
  }

  @Test
  public void anXmlRequestWithNoResponseFailsWithTheRequestNotANullPointer() {
    IllegalStateException thrown =
      assertThrows(IllegalStateException.class, () -> new RestConnectionUtil().getXmlRestClient(UNREACHABLE));
    assertEquals("The request to " + UNREACHABLE + " got no response", thrown.getMessage());
  }
}
