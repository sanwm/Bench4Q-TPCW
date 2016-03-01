package org.bench4q.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Util {

	// public final String SESSION_ID="JIGSAW_SESSION_ID";
	// public static final String SESSION_ID="JServSessionIdroot";
	// public static final String SESSION_ID = "$sessionid$";

	public static final String SESSION_PRIORITY_KEY = "bench4q_session_priority";

	protected static final Integer DEFAULT_PRIORITY = 5;

	public static final Integer PRIORITY_LEVELS = 10;

	protected static final String DIFFSERV_SESSION_PRIORITY_KEY = "diffserv_session_priority";

	// This must be equal to the number of items in the ITEM table
	public static final int NUM_ITEMS = 1000;

	public static int getRandomI_ID() {
		Random rand = new Random();
		Double temp = new Double(Math.floor(rand.nextFloat() * NUM_ITEMS));
		return (temp.intValue() % NUM_ITEMS) + 1;
	}

	public static int getRandom(int i) { // Returns integer 1, 2, 3 ... i
		return ((int) (java.lang.Math.random() * i) + 1);
	}

	// Not very random function. If called in swift sucession, it will
	// return the same string because the system time used to seed the
	// random number generator won't change.
	public static String getRandomString(int min, int max) {
		String newstring = new String();
		Random rand = new Random();
		int i;
		final char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
				'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@', '#', '$', '%', '^', '&', '*',
				'(', ')', '_', '-', '=', '+', '{', '}', '[', ']', '|', ':', ';', ',', '.', '?', '/', '~', ' ' }; // 79
		// characters
		int strlen = (int) Math.floor(rand.nextDouble() * (max - min + 1));
		strlen += min;
		for (i = 0; i < strlen; i++) {
			char c = chars[(int) Math.floor(rand.nextDouble() * 79)];
			newstring = newstring.concat(String.valueOf(c));
		}
		return newstring;
	}

	// Defined in TPC-W Spec Clause 4.6.2.8
	private static final String[] digS = { "BA", "OG", "AL", "RI", "RE", "SE", "AT", "UL", "IN", "NG" };

	public static String DigSyl(int d, int n) {
		String s = "";

		if (n == 0)
			return (DigSyl(d));
		for (; n > 0; n--) {
			int c = d % 10;
			s = digS[c] + s;
			d = d / 10;
		}

		return (s);
	}

	public static String DigSyl(int d) {
		String s = "";

		for (; d != 0; d = d / 10) {
			int c = d % 10;
			s = digS[c] + s;
		}

		return (s);
	}

	public static void determinePriorityLevel(HttpServletRequest req) {
		// by xiaowei zhou, determine session-based differentiated service
		// priority level, 20101116
		HttpSession session = req.getSession(false);
		String strSessionPriorityLevel = req.getParameter(Util.SESSION_PRIORITY_KEY);
		Integer igrSessionPri = null;
		if (strSessionPriorityLevel != null && !strSessionPriorityLevel.equals("")) {
			try {
				igrSessionPri = Integer.valueOf(strSessionPriorityLevel);
			} catch (NumberFormatException e) {
				// ignore, use default
			}
			if (igrSessionPri != null) {
				if (igrSessionPri < 1 || igrSessionPri > Util.PRIORITY_LEVELS) {
					igrSessionPri = Util.DEFAULT_PRIORITY;
				}
				if (session != null) {
					session.setAttribute(Util.DIFFSERV_SESSION_PRIORITY_KEY, igrSessionPri);
				}
			}
		}
	}

	public static String appendSessionId(HttpServletRequest req) {
		// by xiaowei zhou, change "$sessionid$" to "jsessionid=", 2010.11.4
		String sessionIdStrToAppend = req.getRequestedSessionId();
		if (sessionIdStrToAppend != null) {
			sessionIdStrToAppend = ";jsessionid=" + sessionIdStrToAppend;
		} else {
			sessionIdStrToAppend = "";
		}
		return sessionIdStrToAppend;
	}

	public static String buildImageUrl(UUID uuid, Class<?> clazz, String imageName) {
		try {
			return "image?uuid=" + URLEncoder.encode(uuid.toString(), "utf-8") + "&className="
					+ URLEncoder.encode(clazz.getName(), "utf-8") + "&imageName=" + imageName;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
