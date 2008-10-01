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

package com.jaeksoft.searchlib.remote;

import java.io.IOException;
import java.net.URL;

public class UrlReadObject extends UrlRead {

	private StreamReadObject sro;

	public UrlReadObject(URL url) throws IOException {
		super(url.toExternalForm());
		sro = new StreamReadObject(getMethod.getResponseBodyAsStream());
	}

	public Object read() throws IOException, ClassNotFoundException {
		return sro.read();
	}

	public void close() {
		if (sro != null) {
			sro.close();
			sro = null;
		}
		super.close();
	}
}
