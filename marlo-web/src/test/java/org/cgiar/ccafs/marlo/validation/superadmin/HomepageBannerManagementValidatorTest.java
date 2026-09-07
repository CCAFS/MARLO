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

package org.cgiar.ccafs.marlo.validation.superadmin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The validator's rules, tested without a Struts context. Wiring those rules into action errors needs a live
 * BaseAction and is covered by the manual QA pass; what is worth pinning here is the rules themselves — above all
 * that an entirely empty banner is legal, since that is how an administrator hides the banner
 * (ENH-HOMEPAGE-BANNER-001 FN-004).
 */
public class HomepageBannerManagementValidatorTest {

  @Rule
  public TemporaryFolder incoming = new TemporaryFolder();

  private HomepageBannerManagementValidator validator;

  @Before
  public void setUp() {
    validator = new HomepageBannerManagementValidator();
  }

  @Test
  public void anEmptyBannerIsValidBecauseThatIsHowTheBannerIsHidden() {
    assertFalse(validator.isTitleTooLong(null));
    assertFalse(validator.isTitleTooLong(""));
    assertFalse(validator.isTitleTooLong("   "));
  }

  @Test
  public void aTitleOfExactlyFiveHundredCharactersIsAccepted() {
    assertFalse(validator.isTitleTooLong(StringUtils.repeat('a', 500)));
  }

  @Test
  public void aTitleOfFiveHundredAndOneCharactersIsRejected() {
    assertTrue(validator.isTitleTooLong(StringUtils.repeat('a', 501)));
  }

  @Test
  public void surroundingWhitespaceDoesNotPushATitleOverTheLimit() {
    assertFalse(validator.isTitleTooLong("  " + StringUtils.repeat('a', 500) + "  "));
  }

  @Test
  public void noUploadedFileIsValid() {
    assertFalse(validator.isImageFormatInvalid(null));
    assertFalse(validator.isImageTooLarge(null));
  }

  @Test
  public void aPngUploadIsAcceptedAndATextFileIsNot() throws Exception {
    File png = incoming.newFile("real.png");
    ImageIO.write(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "png", png);
    File text = incoming.newFile("fake.png");
    Files.write(text.toPath(), "not an image".getBytes(StandardCharsets.UTF_8));

    assertFalse(validator.isImageFormatInvalid(png));
    assertTrue(validator.isImageFormatInvalid(text));
  }

  @Test
  public void anOversizedUploadIsRejectedOnSizeAlone() throws Exception {
    File big = incoming.newFile("big.png");
    Files.write(big.toPath(), new byte[2 * 1024 * 1024 + 1]);

    assertTrue(validator.isImageTooLarge(big));
  }
}
