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

package org.cgiar.ccafs.marlo.action.summaries.ai.service;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AIReportService extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static String AI_API_URL = "";

	protected APConfig config;

	public AIReportService(APConfig config) {
		super();
		this.config = config;
	}

	public String generateAIReport(String indicator, int year) throws Exception {
		AI_API_URL = config.getSummaryMicroserviceURL();

		URL url = new URL(AI_API_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		// Build JSON
		String jsonInput = String.format("{\"indicator\": \"%s\", \"year\": %d}", indicator, year);

		// Send JSON
		try (OutputStream os = conn.getOutputStream()) {
			byte[] input = jsonInput.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		// Read answer
		int status = conn.getResponseCode();
		StringBuilder response = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line.trim());
			}
		}

		conn.disconnect();

		return response.toString();
	}

	public AIIndicatorReport generateAIReportObject(String indicator, int year) throws Exception {
		AI_API_URL = config.getSummaryMicroserviceURL();

		URL url = new URL(AI_API_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		String jsonInput = String.format("{\"indicator\": \"%s\", \"year\": %d}", indicator, year);

		try (OutputStream os = conn.getOutputStream()) {
			byte[] input = jsonInput.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		int status = conn.getResponseCode();
		System.out.println("[AI Report] HTTP response status: " + status);

		StringBuilder response = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(), "utf-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line.trim());
			}
		}

		conn.disconnect();

		String rawJson = response.toString();
		System.out.println("[AI Report] Raw JSON response:");
		System.out.println(rawJson.length() > 3000 ? rawJson.substring(0, 3000) + "..." : rawJson);

		ObjectMapper mapper = new ObjectMapper();
		try {
			AIIndicatorReport report = mapper.readValue(rawJson, AIIndicatorReport.class);
			System.out.println("[AI Report] JSON parsed successfully.");
			return report;
		} catch (JsonProcessingException e) {
			System.err.println("[AI Report] Error parsing JSON: " + e.getMessage());
			throw e;
		}
	}
}