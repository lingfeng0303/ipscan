package net.azib.ipscan.util;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Version;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

public class GoogleAnalytics {
	public void report(String screen) {
		report("screenview", screen);
	}

	public void report(String type, String content) {
		try {
			Config config = Config.getConfig();
			URL url = new URL("https://www.google-analytics.com/collect");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setRequestProperty("User-Agent", "Java/" + System.getProperty("java.version") + "-" + System.getProperty("os.name") + "-" + System.getProperty("os.version") + "-" + System.getProperty("os.arch"));
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			String contentParam = "exception".equals(type) ? "exd" : "cd";
			String payload = "v=1&t=" + type + "&tid=" + Version.GA_ID + "&cid=" + config.getUUID() + "&an=ipscan&av=" + Version.getVersion() + "&" +
							 contentParam + "=" + URLEncoder.encode(content, "UTF-8") + "&ul=" + config.getLocale();
			os.write(payload.getBytes());
			os.close();
			conn.getContent();
			conn.disconnect();
		}
		catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(WARNING, "Failed to report", e);
		}
	}

	public void report(Throwable e) {
		report("exception", e.toString());
	}

	public void asyncReport(final String screen) {
		new Thread() {
			@Override public void run() {
				report(screen);
			}
		}.start();
	}
}
