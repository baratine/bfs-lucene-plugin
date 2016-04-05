package com.caucho.lucene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import io.baratine.service.Api;
import io.baratine.service.Result;
import io.baratine.service.Service;
import io.baratine.stream.ResultStreamBuilder;

@Service("service")
@Api(LuceneFacade.class)
public class LuceneFacadeImpl implements LuceneFacade
{
  private static final Logger log
    = Logger.getLogger(LuceneFacadeImpl.class.getName());

  @Inject
  @Service("/lucene-writer")
  private LuceneWriter _indexWriter;

  /*@Inject
  @Service("/lucene-writer")
  private ServiceRef _indexWriterRef;
*/
  @Inject
  @Service("/lucene-reader")
  private LuceneReader _indexReader;

  private LuceneWriter getLuceneWriter(String id)
  {
    //return _indexWriterRef.as(LuceneWriter.class);
    return _indexWriter;
    //return _indexWriterRef.node(id.hashCode()).as(LuceneWriter.class);
  }

  @Override
  public void indexFile(String collection, String path, Result<Boolean> result)
    throws LuceneException
  {
    checkCollection(collection);
    checkId(path);

    getLuceneWriter(path).indexFile(collection, path, result);
  }

  @Override
  public void indexText(String collection,
                        String id,
                        String text,
                        Result<Boolean> result) throws LuceneException
  {
    checkCollection(collection);
    checkId(id);
    checkText(text);

    getLuceneWriter(id).indexText(collection, id, text, result);
  }

  @Override
  public void indexMap(String collection,
                       String id,
                       Map<String,Object> map,
                       Result<Boolean> result) throws LuceneException
  {
    checkCollection(collection);
    checkId(id);
    checkMap(map);

    getLuceneWriter(id).indexMap(collection, id, map, result);
  }

  @Override
  public void search(String collection,
                     String query,
                     int limit,
                     Result<List<LuceneEntry>> result)
    throws LuceneException
  {
    checkCollection(collection);
    checkQuery(query);

    _indexReader.search(collection, query, result.of(x -> Arrays.asList(x)));
  }

  private void checkCollection(String collection)
  {
    if (collection == null || collection.isEmpty()) {
      throw new LuceneException("collection should have a value");
    }
  }

  private void checkId(String id)
  {
    if (id == null || id.isEmpty()) {
      throw new LuceneException("id|path should have a value");
    }
  }

  private void checkText(String text)
  {
    if (text == null) {
      throw new LuceneException("text should not be null");
    }
  }

  private void checkQuery(String query)
  {
    if (query == null || query.isEmpty()) {
      throw new LuceneException("query should have a value");
    }
  }

  private void checkMap(Map<String,Object> map)
  {
    if (map == null) {
      throw new LuceneException("map should not be null");
    }
  }

  @Override
  public void delete(String collection, String id, Result<Boolean> result)
    throws LuceneException
  {
    getLuceneWriter(id).delete(collection, id, result);
  }

  @Override
  public void clear(String collection, Result<Void> result)
    throws LuceneException
  {
    ResultStreamBuilder<Void> builder = _indexWriter.clear(collection);

    builder.collect(ArrayList<Void>::new, (a, b) -> {
    }, (a, b) -> {
    })
           .result(result.of((c -> null)));

  }
}
