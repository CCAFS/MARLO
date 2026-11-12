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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.report.MicroserviceReportAction;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The submit flag of the cluster summary (BUG-SUBMIT-EMAIL-001 FN-001, NF-002): the bytes are fetched only when the
 * submit asks for them, and the download keeps queueing the message.
 */
public class ReportingSummarySubmitFlagTest {

  private static final byte[] PDF = {37, 80, 68, 70, 45, 9};

  private static final class SummaryConfig extends APConfig {

    @Override
    public String getMicroserviceApiKey() {
      return "key-1";
    }

    @Override
    public String getMicroserviceBucketname() {
      return "bucket-1";
    }
  }

  /** Records every call the summary makes to the microservice and answers the scripted bytes. */
  private static final class FakeMicroservice extends MicroserviceReportAction {

    final List<String> calls = new ArrayList<>();
    final List<String> fetchedJson = new ArrayList<>();
    final List<String> fetchedName = new ArrayList<>();
    final List<String> queuedJson = new ArrayList<>();
    final List<String> queuedName = new ArrayList<>();

    FakeMicroservice() {
      super(new SummaryConfig(), null);
    }

    @Override
    public byte[] fetchClusterReportBytes(String json, String reportName) {
      this.calls.add("fetch");
      this.fetchedJson.add(json);
      this.fetchedName.add(reportName);
      return PDF.clone();
    }

    @Override
    public void loadData() {
      this.calls.add("loadData");
    }

    @Override
    public void sendClusterReportQueueMessage(String json, String reportName) {
      this.calls.add("queue");
      this.queuedJson.add(json);
      this.queuedName.add(reportName);
    }
  }

  private static ReportingSummaryAction summary(FakeMicroservice microservice) {
    ReportingSummaryAction summary = new ReportingSummaryAction(new SummaryConfig(),
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null,
      microservice, null, null) {

      private static final long serialVersionUID = 1L;

      @Override
      public void loadClusterTemplateData() {
        // The template data is read from the database; the flag does not depend on it.
      }
    };
    Project project = new Project();
    project.setId(77L);
    Phase phase = new Phase();
    phase.setId(501L);
    phase.setYear(2026);
    GlobalUnit unit = new GlobalUnit();
    unit.setId(45L);
    unit.setAcronym("AICCRA");
    summary.setProject(project);
    summary.setSelectedPhase(phase);
    summary.setSelectedCycle("Planning");
    summary.setLoggedCrp(unit);
    return summary;
  }

  private static String fileNameOf(String json) throws Exception {
    Map<?, ?> main = new ObjectMapper().readValue(json, Map.class);
    assertEquals("pdf.generate", main.get("pattern"));
    Map<?, ?> data = (Map<?, ?>) main.get("data");
    assertEquals("bucket-1", data.get("bucketName"));
    return (String) data.get("fileName");
  }

  @Test
  public void generatePdfBytesFetchesTheBytesAndNeverQueuesTheMessage() throws Exception {
    FakeMicroservice microservice = new FakeMicroservice();

    byte[] bytes = summary(microservice).generatePdfBytes();

    assertArrayEquals("the bytes of the fetch are answered", PDF, bytes);
    assertEquals("one fetch and no queue message", java.util.Arrays.asList("loadData", "fetch"), microservice.calls);
    String name = microservice.fetchedName.get(0);
    assertTrue(name, name.startsWith("AICCRA-Cluster-77-Summary-") && name.endsWith(".pdf"));
    assertEquals("the fetch gets the json that names the same report", name,
      fileNameOf(microservice.fetchedJson.get(0)));
  }

  @Test
  public void aPlainGenerationQueuesTheMessageAndNeverFetches() throws Exception {
    FakeMicroservice microservice = new FakeMicroservice();

    summary(microservice).generateAndSendJson();

    assertEquals("one queue message and no fetch", java.util.Arrays.asList("loadData", "queue"), microservice.calls);
    String name = microservice.queuedName.get(0);
    assertTrue(name, name.startsWith("AICCRA-Cluster-77-Summary-") && name.endsWith(".pdf"));
    assertEquals("the queue gets the json that names the same report", name,
      fileNameOf(microservice.queuedJson.get(0)));
  }
}
