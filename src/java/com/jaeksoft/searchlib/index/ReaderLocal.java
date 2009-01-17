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

package com.jaeksoft.searchlib.index;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.FieldCache.StringIndex;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

import com.jaeksoft.searchlib.cache.DocumentCache;
import com.jaeksoft.searchlib.cache.FilterCache;
import com.jaeksoft.searchlib.cache.SearchCache;
import com.jaeksoft.searchlib.filter.FilterHits;
import com.jaeksoft.searchlib.filter.FilterList;
import com.jaeksoft.searchlib.function.expression.SyntaxError;
import com.jaeksoft.searchlib.request.Request;
import com.jaeksoft.searchlib.result.DocumentCacheItem;
import com.jaeksoft.searchlib.result.DocumentRequestItem;
import com.jaeksoft.searchlib.result.DocumentResult;
import com.jaeksoft.searchlib.result.ResultSearch;
import com.jaeksoft.searchlib.sort.SortList;
import com.jaeksoft.searchlib.util.FileUtils;

public class ReaderLocal extends NameFilter implements ReaderInterface {

	private IndexDirectory indexDirectory;
	private IndexSearcher indexSearcher;
	private IndexReader indexReader;

	private SearchCache searchCache;
	private FilterCache filterCache;
	private DocumentCache documentCache;

	private final ReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	private File rootDir;
	private File dataDir;

	private ReaderLocal(String name, File rootDir, File dataDir)
			throws IOException {
		super(name);
		init(rootDir, dataDir);
	}

	private void init(File rootDir, File dataDir) throws IOException {
		w.lock();
		try {
			this.rootDir = rootDir;
			this.dataDir = dataDir;
			this.indexDirectory = new IndexDirectory(getName(), dataDir);
			this.indexSearcher = new IndexSearcher(indexDirectory
					.getDirectory());
			this.indexReader = indexSearcher.getIndexReader();
		} finally {
			w.unlock();
		}
	}

	private void init(ReaderLocal r) {
		w.lock();
		try {
			this.rootDir = r.rootDir;
			this.dataDir = r.dataDir;
			this.indexDirectory = r.indexDirectory;
			this.indexSearcher = r.indexSearcher;
			this.indexReader = r.indexReader;
		} finally {
			w.unlock();
		}
	}

	private void initCache(int searchCache, int filterCache, int documentCache) {
		w.lock();
		try {
			this.searchCache = new SearchCache(searchCache);
			this.filterCache = new FilterCache(filterCache);
			this.documentCache = new DocumentCache(documentCache);
		} finally {
			w.unlock();
		}
	}

	private void resetCache() {
		w.lock();
		try {
			searchCache.clear();
			filterCache.clear();
			documentCache.clear();
		} finally {
			w.unlock();
		}
	}

	public long getVersion() {
		r.lock();
		try {
			return indexReader.getVersion();
		} finally {
			r.unlock();
		}
	}

	public TermDocs getTermDocs(Term term) throws IOException {
		r.lock();
		try {
			return indexReader.termDocs(term);
		} finally {
			r.unlock();
		}
	}

	public int getDocFreq(Term term) throws IOException {
		r.lock();
		try {
			return indexSearcher.docFreq(term);
		} finally {
			r.unlock();
		}
	}

	public void close() {
		w.lock();
		try {
			if (indexReader != null) {
				indexReader.close();
				indexReader = null;
			}
			if (indexSearcher != null) {
				indexSearcher.close();
				indexSearcher = null;
			}
			if (indexDirectory != null) {
				indexDirectory.close();
				indexDirectory = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			w.unlock();
		}
	}

	public int maxDoc() throws IOException {
		r.lock();
		try {
			return indexSearcher.maxDoc();
		} finally {
			r.unlock();
		}
	}

	public int numDocs() {
		r.lock();
		try {
			return indexReader.numDocs();
		} finally {
			r.unlock();
		}
	}

	public TopDocs search(Query query, Filter filter, Sort sort, int nTop)
			throws IOException {
		r.lock();
		try {
			if (sort == null) {
				if (filter == null)
					return indexSearcher.search(query, nTop);
				else
					return indexSearcher.search(query, filter, nTop);
			} else {
				return indexSearcher.search(query, filter, nTop, sort);
			}
		} finally {
			r.unlock();
		}
	}

	public ResultSearch search(Request request) throws IOException,
			ParseException, SyntaxError {
		ResultSearch result = new ResultSearch(this, request);
		if (request.isWithDocument())
			result.documents();
		return result;
	}

	public void search(Query query, Filter filter, HitCollector collector)
			throws IOException {
		r.lock();
		try {
			if (filter == null)
				indexSearcher.search(query, collector);
			else
				indexSearcher.search(query, filter, collector);
		} finally {
			r.unlock();
		}
	}

	protected void deleteDocument(int docNum) throws StaleReaderException,
			CorruptIndexException, LockObtainFailedException, IOException {
		indexReader.deleteDocument(docNum);
	}

	public Document document(int docId, FieldSelector selector)
			throws CorruptIndexException, IOException {
		r.lock();
		try {
			return indexReader.document(docId, selector);
		} catch (IllegalArgumentException e) {
			throw e;
		} finally {
			r.unlock();
		}
	}

	public StringIndex getStringIndex(String fieldName) throws IOException {
		r.lock();
		try {
			return FieldCache.DEFAULT.getStringIndex(indexReader, fieldName);
		} finally {
			r.unlock();
		}
	}

	public void xmlInfo(PrintWriter writer, HashSet<String> classDetail) {
		r.lock();
		try {
			writer.println("<index name=\"" + indexDirectory.getName()
					+ "\" path=\"" + indexDirectory.getDirectory() + "\"/>");
		} finally {
			r.unlock();
		}
	}

	private static ReaderLocal getMostRecent(String name, File rootDir,
			boolean bDeleteOld) {
		ReaderLocal reader = null;
		for (File f : rootDir.listFiles()) {
			try {
				if (f.getName().startsWith("."))
					continue;
				ReaderLocal r = new ReaderLocal(name, rootDir, f);
				if (reader == null)
					reader = r;
				else {
					ReaderLocal deleteReader = null;
					if (r.getVersion() > reader.getVersion()) {
						reader.close();
						if (bDeleteOld)
							deleteReader = reader;
						reader = r;
					} else
						deleteReader = r;
					if (deleteReader != null)
						deleteReader.indexDirectory.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (bDeleteOld)
					IndexDirectory.deleteDir(f);
			}
		}
		return reader;
	}

	public static ReaderLocal fromConfig(File homeDir, IndexConfig indexConfig,
			boolean createIfNotExists) throws IOException {
		if (indexConfig.getName() == null || indexConfig.getPath() == null)
			return null;

		File rootDir = new File(FileUtils.locatePath(homeDir, indexConfig
				.getPath()));
		if (!rootDir.exists() && createIfNotExists)
			rootDir.mkdirs();
		ReaderLocal reader = ReaderLocal.getMostRecent(indexConfig.getName(),
				rootDir, false);

		if (reader == null) {
			if (!createIfNotExists)
				return null;
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			File dataDir = new File(rootDir, df.format(new Date()));
			WriterLocal.create(dataDir);
			reader = new ReaderLocal(indexConfig.getName(), rootDir, dataDir);
		}

		reader.initCache(indexConfig.getSearchCache(), indexConfig
				.getFilterCache(), indexConfig.getDocumentCache());
		return reader;
	}

	public Directory getDirectory() {
		r.lock();
		try {
			return indexDirectory.getDirectory();
		} finally {
			r.unlock();
		}
	}

	private void reload() throws IOException {
		w.lock();
		try {
			close();
			init(rootDir, dataDir);
			resetCache();
		} finally {
			w.unlock();
		}
	}

	public void reload(String indexName, boolean deleteOld) throws IOException {
		if (!acceptName(indexName))
			return;
		ReaderLocal newReader = ReaderLocal.getMostRecent(getName(), rootDir,
				deleteOld);
		if (newReader.getVersion() <= getVersion())
			return;
		w.lock();
		try {
			close();
			init(newReader);
			resetCache();
		} finally {
			w.unlock();
		}
	}

	public DocSetHits searchDocSet(Request request) throws IOException,
			ParseException, SyntaxError {
		boolean isDelete = request.isDelete();
		boolean isFacet = request.isFacet();
		if (isDelete)
			w.lock();
		else
			r.lock();
		try {
			StringBuffer cacheDshKey = new StringBuffer(request
					.getQueryParsed());
			FilterHits filter = null;
			FilterList filterList = request.getFilterList();
			if (filterList.size() > 0) {
				String cacheFilterKey = FilterHits.toCacheKey(filterList);
				filter = filterCache.getAndPromote(cacheFilterKey);
				if (filter == null) {
					filter = new FilterHits(this, filterList);
					filterCache.put(cacheFilterKey, filter);
				}
				cacheDshKey.append('|');
				cacheDshKey.append(cacheFilterKey);
			}
			SortList sortList = request.getSortList();
			Sort sort = sortList.getLuceneSort();
			if (sort != null) {
				cacheDshKey.append("_");
				cacheDshKey.append(sortList.getSortKey());
			}
			if (isFacet)
				cacheDshKey.append("|facet");
			String cacheDshKeyStr = cacheDshKey.toString();
			DocSetHits dsh = null;
			if (!isDelete)
				dsh = searchCache.getAndPromote(cacheDshKeyStr);
			if (dsh == null) {
				dsh = new DocSetHits(this, request.getQuery(), filter, sort,
						isDelete, isFacet);
				if (!isDelete)
					searchCache.put(cacheDshKeyStr, dsh);
				else if (dsh.getDocNumFound() > 0)
					reload();
			}
			return dsh;
		} finally {
			if (isDelete)
				w.unlock();
			else
				r.unlock();
		}
	}

	private DocumentRequestItem document(int docId, Request request)
			throws CorruptIndexException, IOException, ParseException {
		r.lock();
		try {
			DocumentCacheItem doc = null;
			String key = DocumentCache.getKey(request.getName(), getName(),
					docId);
			if (key != null) {
				doc = documentCache.getAndPromote(key);
				if (doc != null)
					new DocumentRequestItem(request, doc);
			}

			Document document = document(docId, request.getDocumentFieldList());
			doc = new DocumentCacheItem(key, request, document);
			if (key != null)
				documentCache.put(key, doc);
			return new DocumentRequestItem(request, doc);
		} finally {
			r.unlock();
		}
	}

	public DocumentResult documents(Request request)
			throws CorruptIndexException, IOException, ParseException {
		r.lock();
		try {
			List<Integer> docIds = request.getDocIds();
			if (docIds == null)
				return null;
			DocumentResult documentResult = new DocumentResult();
			for (int docId : docIds)
				documentResult.add(document(docId, request));
			return documentResult;
		} finally {
			r.unlock();
		}
	}

	public boolean sameIndex(ReaderInterface reader) {
		r.lock();
		try {
			if (reader == this)
				return true;
			if (reader == null)
				return true;
			return reader.sameIndex(this);
		} finally {
			r.unlock();
		}
	}

	public IndexStatistics getStatistics() {
		r.lock();
		try {
			return new IndexStatistics(indexReader);
		} finally {
			r.unlock();
		}
	}

}
