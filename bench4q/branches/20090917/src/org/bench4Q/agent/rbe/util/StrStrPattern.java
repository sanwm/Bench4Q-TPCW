package org.bench4Q.agent.rbe.util;

public class StrStrPattern extends StringPattern {
	protected String p; // The string to match.

	public StrStrPattern(String s) {
		p = s;
	}

	public int length() {
		return (p.length());
	}

	// Search from index start to end, inclusive.
	public int find(String s, int start, int end) {
		int i = s.indexOf(p, start);
		if (i == -1) {
			return (-1);
		}
		// FIXME: This is slower than needed, when the
		// string is much longer than end.
		else if (i >= (end + p.length())) {
			return (-1);
		} else {
			this.start = i;
			this.end = this.start + p.length() - 1;
			return (i);
		}
	}

	// See if pattern matches exactly characters pos to end, inclusive.
	public boolean matchWithin(String s, int pos, int end) {
		if ((end - pos + 1) < p.length())
			return (false);

		if (s.startsWith(p, pos)) {
			this.start = pos;
			this.end = pos + p.length() - 1;
			return (true);
		} else {
			return (false);
		}
	}

	public String toString() {
		return p;
	}

	// Minimum and maximum lengths.
	protected int minLength() {
		return (p.length());
	}

	protected int maxLength() {
		return (p.length());
	}

}
