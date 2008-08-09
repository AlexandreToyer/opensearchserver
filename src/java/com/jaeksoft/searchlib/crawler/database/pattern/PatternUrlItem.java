/**   
 * License Agreement for Jaeksoft SearchLib Community
 *
 * Copyright (C) 2008 Emmanuel Keller / Jaeksoft
 * 
 * http://www.jaeksoft.com
 * 
 * This file is part of Jaeksoft SearchLib Community.
 *
 * Jaeksoft SearchLib Community is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Jaeksoft SearchLib Community is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jaeksoft SearchLib Community. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.crawler.database.pattern;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.faces.event.ValueChangeEvent;

public class PatternUrlItem {

	public enum Status {
		UNDEFINED("Undefined"), INJECTED("Injected"), ALREADY(
				"Already injected"), ERROR("Unknown Error");

		private String name;

		private Status(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private Status status;

	protected String sPattern;

	private Pattern pattern;

	private PatternUrlList patternList;

	public PatternUrlItem() {
		status = Status.UNDEFINED;
		sPattern = null;
		pattern = null;
		patternList = null;
	}

	public PatternUrlItem(URL url) {
		this();
		setPattern(url.toExternalForm());
	}

	public PatternUrlItem(String sPattern) {
		this();
		setPattern(sPattern);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status v) {
		status = v;
	}

	public void setSelected(boolean v) {
		if (v)
			patternList.addSelection(this);
		else
			patternList.removeSelection(this);
	}

	public boolean isSelected() {
		return patternList.isSelected(this);
	}

	void toggleSelected(ValueChangeEvent event) {
		setSelected(!isSelected());
	}

	public boolean match(String sUrl) {
		if (pattern == null)
			return sUrl.equals(sPattern);
		return pattern.matcher(sUrl).matches();
	}

	public void setPattern(String s) {
		sPattern = s.trim();
		if (!sPattern.contains("*"))
			return;
		pattern = Pattern.compile(sPattern.replace("*", ".*"));
	}

	public URL extractUrl(boolean removeWildcard) throws MalformedURLException {
		return new URL(removeWildcard ? sPattern.replace("*", "") : sPattern);
	}

	public String getPattern() {
		return sPattern;
	}

	public void setPatternUrlList(PatternUrlList patternList) {
		this.patternList = patternList;
	}

}
