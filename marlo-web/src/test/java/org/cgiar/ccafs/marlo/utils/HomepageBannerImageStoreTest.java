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

import org.cgiar.ccafs.marlo.utils.HomepageBannerImageStore.StoreOutcome;
import org.cgiar.ccafs.marlo.utils.HomepageBannerImageStore.StoreStatus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The store owns every rule that decides what reaches the filesystem: the format whitelist, the size cap and the
 * derivation of the stored path. Those are the security-relevant parts of the homepage banner feature
 * (ENH-HOMEPAGE-BANNER-001 SEC-002..SEC-004), so they are tested here rather than through an HTTP request.
 */
public class HomepageBannerImageStoreTest {

  private static final String ACRONYM = "AICCRA";

  @Rule
  public TemporaryFolder uploads = new TemporaryFolder();

  @Rule
  public TemporaryFolder incoming = new TemporaryFolder();

  private HomepageBannerImageStore store;

  private File bannersFolder() {
    return new File(uploads.getRoot(), "homepageBanners");
  }

  @Before
  public void setUp() {
    store = new HomepageBannerImageStore(uploads.getRoot().getAbsolutePath());
  }

  private File rasterFile(String name, String format) throws Exception {
    File file = incoming.newFile(name);
    ImageIO.write(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), format, file);
    return file;
  }

  private File svgFile(String name) throws Exception {
    File file = incoming.newFile(name);
    String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 4 4\"><rect width=\"4\" height=\"4\"/></svg>";
    Files.write(file.toPath(), svg.getBytes(StandardCharsets.UTF_8));
    return file;
  }

  @Test
  public void storesPngUnderAnAcronymDerivedName() throws Exception {
    StoreOutcome outcome = store.store(ACRONYM, rasterFile("whatever.png", "png"));

    assertEquals(StoreStatus.STORED, outcome.getStatus());
    assertEquals("AICCRA.png", outcome.getFileName());
    assertTrue(new File(bannersFolder(), "AICCRA.png").isFile());
  }

  @Test
  public void storesJpegWithAJpgExtension() throws Exception {
    StoreOutcome outcome = store.store(ACRONYM, rasterFile("photo.jpeg", "jpg"));

    assertEquals(StoreStatus.STORED, outcome.getStatus());
    assertEquals("AICCRA.jpg", outcome.getFileName());
    assertTrue(new File(bannersFolder(), "AICCRA.jpg").isFile());
  }

  @Test
  public void storesSvgByteForByte() throws Exception {
    File source = svgFile("map.svg");

    StoreOutcome outcome = store.store(ACRONYM, source);

    assertEquals(StoreStatus.STORED, outcome.getStatus());
    assertEquals("AICCRA.svg", outcome.getFileName());
    File stored = new File(bannersFolder(), "AICCRA.svg");
    assertArrayEqualsBytes(Files.readAllBytes(source.toPath()), Files.readAllBytes(stored.toPath()));
  }

  private void assertArrayEqualsBytes(byte[] expected, byte[] actual) {
    assertEquals("stored byte count", expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
      assertEquals("byte at " + i, expected[i], actual[i]);
    }
  }

  @Test
  public void rejectsATextFileRenamedAsPng() throws Exception {
    File fake = incoming.newFile("evil.png");
    Files.write(fake.toPath(), "this is not an image".getBytes(StandardCharsets.UTF_8));

    StoreOutcome outcome = store.store(ACRONYM, fake);

    assertEquals(StoreStatus.INVALID_FORMAT, outcome.getStatus());
    assertNull(outcome.getFileName());
    assertFalse(bannersFolder().exists());
  }

  @Test
  public void rejectsAFileOverTwoMegabytesBeforeLookingAtItsFormat() throws Exception {
    File big = incoming.newFile("big.png");
    Files.write(big.toPath(), new byte[(int) (HomepageBannerImageStore.MAX_IMAGE_BYTES + 1)]);

    StoreOutcome outcome = store.store(ACRONYM, big);

    assertEquals(StoreStatus.TOO_LARGE, outcome.getStatus());
    assertFalse(bannersFolder().exists());
  }

  @Test
  public void keepsTheStoredFileInsideTheBannersFolderWhenTheAcronymLooksLikeAPath() throws Exception {
    StoreOutcome outcome = store.store("../../etc/passwd", rasterFile("ok.png", "png"));

    assertEquals(StoreStatus.STORED, outcome.getStatus());
    File stored = new File(bannersFolder(), outcome.getFileName());
    assertTrue(stored.isFile());
    assertEquals(bannersFolder().getCanonicalPath(), stored.getCanonicalFile().getParentFile().getPath());
  }

  @Test
  public void replacingAPngWithAnSvgDeletesThePng() throws Exception {
    store.store(ACRONYM, rasterFile("first.png", "png"));
    assertTrue(new File(bannersFolder(), "AICCRA.png").isFile());

    StoreOutcome outcome = store.store(ACRONYM, svgFile("second.svg"));

    assertEquals(StoreStatus.STORED, outcome.getStatus());
    assertTrue(new File(bannersFolder(), "AICCRA.svg").isFile());
    assertFalse("the superseded PNG must not linger", new File(bannersFolder(), "AICCRA.png").exists());
  }

  @Test
  public void reportsWhenTheUploadsFolderIsNotConfigured() throws Exception {
    HomepageBannerImageStore unconfigured = new HomepageBannerImageStore("   ");

    StoreOutcome outcome = unconfigured.store(ACRONYM, rasterFile("ok.png", "png"));

    assertEquals(StoreStatus.UPLOADS_NOT_CONFIGURED, outcome.getStatus());
  }

  @Test
  public void reportsWhenTheBannersFolderCannotBeCreated() throws Exception {
    File notADirectory = uploads.newFile("uploadsBaseIsAFile");
    HomepageBannerImageStore broken = new HomepageBannerImageStore(notADirectory.getAbsolutePath());

    StoreOutcome outcome = broken.store(ACRONYM, rasterFile("ok.png", "png"));

    assertEquals(StoreStatus.UPLOADS_NOT_WRITABLE, outcome.getStatus());
  }

  @Test
  public void deleteRemovesTheStoredFile() throws Exception {
    StoreOutcome outcome = store.store(ACRONYM, rasterFile("ok.png", "png"));

    assertTrue(store.delete(ACRONYM, outcome.getFileName()));

    assertFalse(new File(bannersFolder(), "AICCRA.png").exists());
  }

  @Test
  public void resolveReturnsNullWhenTheNamedFileIsNotOnDisk() throws Exception {
    assertNull(store.resolve(ACRONYM, "AICCRA.png"));
  }

  @Test
  public void resolveReturnsTheStoredFile() throws Exception {
    StoreOutcome outcome = store.store(ACRONYM, rasterFile("ok.png", "png"));

    File resolved = store.resolve(ACRONYM, outcome.getFileName());

    assertEquals(new File(bannersFolder(), "AICCRA.png").getCanonicalPath(), resolved.getCanonicalPath());
  }

  @Test
  public void contentTypeFollowsTheStoredExtension() {
    assertEquals("image/png", HomepageBannerImageStore.contentTypeFor("AICCRA.png"));
    assertEquals("image/jpeg", HomepageBannerImageStore.contentTypeFor("AICCRA.jpg"));
    assertEquals("image/svg+xml", HomepageBannerImageStore.contentTypeFor("AICCRA.svg"));
  }
}
