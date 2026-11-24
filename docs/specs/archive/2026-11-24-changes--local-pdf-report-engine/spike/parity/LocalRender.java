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

import java.nio.file.Files;
import java.nio.file.Path;

import org.cgiar.ccafs.marlo.utils.pdf.LocalPdfReportService;

/**
 * Local render of one MARLO report payload through the built LocalPdfReportService.
 * Usage: java -cp marlo-web/target/classes:<test classpath> LocalRender.java payload.json out.pdf
 */
public class LocalRender {

  public static void main(String[] args) throws Exception {
    String payload = Files.readString(Path.of(args[0]));
    LocalPdfReportService service = new LocalPdfReportService();
    try {
      long t0 = System.currentTimeMillis();
      byte[] pdf = service.render(payload, LocalPdfReportService.DOWNLOAD_DEADLINE_MS);
      Files.write(Path.of(args[1]), pdf);
      System.out.println("local ok " + pdf.length + " bytes " + (System.currentTimeMillis() - t0) + " ms");
    } finally {
      service.shutdown();
    }
  }
}
