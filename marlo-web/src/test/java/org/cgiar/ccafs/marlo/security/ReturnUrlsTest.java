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

package org.cgiar.ccafs.marlo.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * CHG-OTP-LOGIN-001-T08, FN-011 and FN-019: the same-origin return URL guard extracted from
 * {@code CognitoLoginAction}. Every expectation is a literal; none is derived with the code under test.
 */
public class ReturnUrlsTest {

  private static final String BASE = "https://marlo.example.org";

  @Test
  public void sameOriginDeepLinkIsKept() {
    assertEquals("https://marlo.example.org/projects/list.do",
      ReturnUrls.sameOriginOrNull("https://marlo.example.org/projects/list.do", BASE));
  }

  @Test
  public void candidateIsTrimmed() {
    assertEquals("https://marlo.example.org/projects/list.do",
      ReturnUrls.sameOriginOrNull("  https://marlo.example.org/projects/list.do ", BASE));
  }

  @Test
  public void baseUrlWithTrailingSlashStillMatches() {
    assertEquals("https://marlo.example.org/a.do",
      ReturnUrls.sameOriginOrNull("https://marlo.example.org/a.do", BASE + "/"));
  }

  @Test
  public void exactBaseUrlIsKept() {
    assertEquals(BASE, ReturnUrls.sameOriginOrNull(BASE, BASE));
  }

  @Test
  public void crossOriginIsDropped() {
    assertNull(ReturnUrls.sameOriginOrNull("https://evil.example.com/a.do", BASE));
  }

  @Test
  public void lookalikeHostIsDropped() {
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org.evil.com/a.do", BASE));
  }

  @Test
  public void nullAndBlankCandidatesAreDropped() {
    assertNull(ReturnUrls.sameOriginOrNull(null, BASE));
    assertNull(ReturnUrls.sameOriginOrNull("", BASE));
    assertNull(ReturnUrls.sameOriginOrNull("   ", BASE));
  }

  @Test
  public void nullOrBlankBaseUrlFailsClosed() {
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/a.do", null));
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/a.do", ""));
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/a.do", "  "));
  }

  @Test
  public void malformedSameOriginUrlIsDropped() {
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/a b.do", BASE));
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/%zz.do", BASE));
  }

  @Test
  public void authenticationEndpointsAreDropped() {
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/cognitoCallback.do?code=x&state=y", BASE));
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/cognitoLogin.do", BASE));
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/validateUser.do", BASE));
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/x/../CognitoCallback.do", BASE));
    assertNull(ReturnUrls.sameOriginOrNull("https://marlo.example.org/cognitoCallback.do;jsessionid=1", BASE));
  }

  @Test
  public void authenticationEndpointNameInsideAnotherPathIsKept() {
    assertEquals("https://marlo.example.org/notCognitoLogin.do",
      ReturnUrls.sameOriginOrNull("https://marlo.example.org/notCognitoLogin.do", BASE));
    assertEquals("https://marlo.example.org/a.do?next=cognitoCallback.do",
      ReturnUrls.sameOriginOrNull("https://marlo.example.org/a.do?next=cognitoCallback.do", BASE));
  }
}
