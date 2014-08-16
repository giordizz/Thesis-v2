package it.giordizz.Thesis.DataUtils.WATAnnotator;

import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.utils.AnnotationException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *  Copyright 2014 Marco Cornolti
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


public class WATAnnotator {
	private static final int RETRY_N = 2;
	private long lastTime = 0;
	private boolean useContext, useTagger, bogusFilter;
	private final String urlTag;
	private final String urlSpot;
	private final String urlD2W;
	private final String method, relatedness, windowSize, minCommonness,
			minLinkProbability, epsilon, kappa;
	private String sortBy;
	private HashMap<String, HashMap<String, Double>> additionalInfo = new HashMap<>();
	private HashMap<String, List<HashMap<String, Double>>> additionalCandidatesInfo = new HashMap<>();
	private HashMap<String, byte[]> url2jsonCache = new HashMap<>();
	
	class PairComp implements Comparable<PairComp> {
		int id;
		double score;
		
		public PairComp(int concept, float score) {
			id =concept;
			this.score=score;
		}

		@Override
		public int compareTo(PairComp o) {
			// TODO Auto-generated method stub
			return -Double.compare(score, o.score);
		}
		
		
		
		
		
	}
	
	public WATAnnotator(String ip, int port, String method, String sortBy,
			String relatedness, String epsilon, String minLinkProbability,
			boolean useContext, boolean useTagger, boolean bogusFilter) {
		this.urlTag = String.format("http://%s:%d/tag/tag", ip, port);
		this.urlSpot = String.format("http://%s:%d/tag/spot", ip, port);
		this.urlD2W = String.format("http://%s:%d/tag/disambiguate", ip, port);
		this.method = method;
		this.epsilon = epsilon;
		this.windowSize = "";
		this.minCommonness = "";
		this.kappa = "";
		this.useContext = useContext;
		this.useTagger = useTagger;
		this.bogusFilter = bogusFilter;
		this.minLinkProbability = minLinkProbability;
		this.sortBy = sortBy;
		this.relatedness = relatedness;
	}

	public ArrayList<Integer> solve(String text) {
		
		HashSet<ScoredAnnotation> set = solveSa2W(text);
//		HashSet<ScoredAnnotation> set = new HashSet<ScoredAnnotation>();
//		
//		set.add(new ScoredAnnotation(124, 0, 124, 23.5f));
//		set.add(new ScoredAnnotation(1214, 0, 1214, 423.5f));
		TreeSet<PairComp> resT = new TreeSet<PairComp>();
		for (ScoredAnnotation r : set) {
//			System.err.println(r.getScore());
			resT.add(new PairComp(r.getConcept(),r.getScore()));
//			res[r.getPosition()] = r.getConcept();
			
		}
			
		ArrayList<Integer> res = new ArrayList<Integer>(resT.size());
		for (PairComp p : resT) 
			res.add(p.id);
		
		
		return res;
	}
	

	
	private HashSet<ScoredAnnotation> solveSa2W(String text)
			throws AnnotationException {
		// System.out.println(text);
		HashSet<ScoredAnnotation> res = new HashSet<ScoredAnnotation>();
		
		
		JSONObject obj = null;
		try {
			obj = queryJson(text, null, urlTag,
					generateGetParameters(minCommonness, epsilon, kappa),
					RETRY_N);
			lastTime = obj.getJSONObject("time").getInt("total");

		} catch (Exception e) {
			System.out
					.print("Got error while querying WikiSense API with GET parameters: "
							+ generateGetParameters(minCommonness, epsilon,
									kappa) + " with text: " + text);

			throw new AnnotationException(
					"An error occurred while querying WikiSense API. Message: "
							+ e.getMessage());
		}
		try {
			JSONArray jsAnnotations = obj.getJSONArray("annotations");
			for (int i = 0; i < jsAnnotations.length(); i++) {
				JSONObject js_ann = jsAnnotations.getJSONObject(i);
				// System.out.println(js_ann);
				int start = js_ann.getInt("start");
				int end = js_ann.getInt("end");
				int id = js_ann.getInt("id");
				double rho = js_ann.getDouble("rho");
				// System.out.println(text.substring(start, end) + "->" + id +
				// " ("
				// + rho + ")");
				res.add(new ScoredAnnotation(start, end - start, id,
						(float) rho));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new AnnotationException(e.getMessage());
		}
		
		
		return res;
	}

	private String generateGetParameters(String newMinCommonness,
			String newEpsilon, String newKappa) {
		String getParameters = String.format("lang=%s", "en");
		if (!method.equals(""))
			getParameters += String.format("&method=%s", method);
		if (!windowSize.equals(""))
			getParameters += String.format("&windowSize=%s", windowSize);
		if (!newEpsilon.equals(""))
			getParameters += String.format("&epsilon=%s", newEpsilon);
		if (!newMinCommonness.equals(""))
			getParameters += String.format("&minCommonness=%s",
					newMinCommonness);
		if (!newKappa.equals(""))
			getParameters += String.format("&kappa=%s", newKappa);
		if (!minLinkProbability.equals(""))
			getParameters += String.format("&minLinkProbability=%s",
					minLinkProbability);
		if (!relatedness.equals(""))
			getParameters += String.format("&relatedness=%s", relatedness);
		if (!sortBy.equals(""))
			getParameters += String.format("&sortBy=%s", sortBy);
		getParameters += "&bogusFilter=" + this.bogusFilter;
		getParameters += "&useTagger=" + this.useTagger;
		getParameters += "&useContext=" + this.useContext;
		return getParameters;
	}
	
	private JSONObject queryJson(String text, Set<Mention> mentions,
			String url, String getParameters, int retry) throws Exception {

		JSONObject parameters = new JSONObject();
		if (mentions != null) {
			JSONArray mentionsJson = new JSONArray();
			for (Mention m : mentions) {
				JSONObject mentionJson = new JSONObject();
				mentionJson.put("start", m.getPosition());
				mentionJson.put("end", m.getPosition() + m.getLength());
				mentionsJson.put(mentionJson);
			}
			parameters.put("spans", mentionsJson);
		}
		parameters.put("text", text);
		System.out.println(getParameters);
		System.out.println(parameters.toString());

		String resultStr = null;
		try {
			URL wikiSenseApi = new URL(String.format("%s?%s", url,
					getParameters));

			String cacheKey = wikiSenseApi.toExternalForm()
					+ parameters.toString();
			byte[] compressed = url2jsonCache.get(cacheKey);
			if (compressed != null)
				return new JSONObject(decompress(compressed));

			HttpURLConnection slConnection = (HttpURLConnection) wikiSenseApi
					.openConnection();
			slConnection.setReadTimeout(0);
			slConnection.setDoOutput(true);
			slConnection.setDoInput(true);
			slConnection.setRequestMethod("POST");
			slConnection.setRequestProperty("Content-Type", "application/json");
			slConnection.setRequestProperty("Content-Length", ""
					+ parameters.toString().getBytes().length);

			slConnection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					slConnection.getOutputStream());
			wr.write(parameters.toString().getBytes());
			wr.flush();
			wr.close();

			if (slConnection.getResponseCode() != 200) {
				Scanner s = new Scanner(slConnection.getErrorStream())
						.useDelimiter("\\A");
				System.err.printf("Got HTTP error %d. Message is: %s%n",
						slConnection.getResponseCode(), s.next());
				s.close();
			}

			Scanner s = new Scanner(slConnection.getInputStream())
					.useDelimiter("\\A");
			resultStr = s.hasNext() ? s.next() : "";

			JSONObject obj = new JSONObject(resultStr);
			url2jsonCache.put(cacheKey, compress(obj.toString()));
//			increaseFlushCounter();

			return obj;

		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(3000);
				if (retry > 0)
					return queryJson(text, mentions, url, getParameters,
							retry - 1);
				else
					throw e;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				throw new RuntimeException(e1);
			}
		}
	}
	private byte[] compress(String str) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toByteArray();
	}
	
	private String decompress(byte[] compressed) throws IOException {
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(
				compressed));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
		String outStr = "";
		String line;
		while ((line = bf.readLine()) != null)
			outStr += line;
		return outStr;
	}
}