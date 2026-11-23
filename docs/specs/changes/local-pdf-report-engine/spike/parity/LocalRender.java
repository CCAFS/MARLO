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
