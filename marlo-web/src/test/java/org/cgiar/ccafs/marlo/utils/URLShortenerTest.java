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

import static org.junit.Assert.assertNotEquals;

public class URLShortenerTest {

  @Test
  public void testDetectAndShortenLinksEndsText() throws Exception {
    URLShortener urlShortener = new URLShortener();
    String url =
      "url test: https://marlo.cgiar.org/projects/CCAFS/studySummary.do?studyID=2074&cycle=Reporting&year=2017";
    String shortURL = urlShortener.detectAndShortenLinks(url);
    assertNotEquals(url, shortURL);
  }

  @Test
  public void testGetShortUrlServiceBugUseReplace() throws Exception {
    URLShortener urlShortener = new URLShortener();
    String url =
      "https://cgiar.sharepoint.com/:f:/r/sites/CCAFS/CRP%207%20Management/Reviewing%20and%20Reporting/Annual%20Reporting/TL%20and%20RPL%20Technical%20Reporting/2019/Internal%20Evidences/P266/Evidence%203247?csf=1&e=0xuOXI10";
    String shortURL = urlShortener.getShortUrlService(url);
    assertNotEquals(url, shortURL);
  }

  @Test
  public void testGetShortUrlServiceLessThat93Characters() throws Exception {
    URLShortener urlShortener = new URLShortener();
    String url = "https://marlo.cgiar.org/projects/CCAFS/studySummary.do?studyID=2074&cycle=Reporting&year=2017";
    String shortURL = urlShortener.getShortUrlService(url);
    assertNotEquals(url, shortURL);
  }

}
