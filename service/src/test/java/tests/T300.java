package tests;

import com.caucho.junit.ConfigurationBaratine;
import com.caucho.junit.RunnerBaratine;
import com.caucho.lucene.LuceneEntry;
import com.caucho.lucene.LuceneIndexImpl;
import com.caucho.lucene.LuceneSessionImpl;
import com.caucho.lucene.LuceneManagerImpl;
import io.baratine.core.Lookup;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * title: tests LuceneSession methods
 */
@RunWith(RunnerBaratine.class)
@ConfigurationBaratine(services = {LuceneIndexImpl.class, LuceneSessionImpl.class},
  logs = {@ConfigurationBaratine.Log(name = "com.caucho", level = "FINER")},
  testTime = 0, pod = "lucene")
public class T300
{
  @Inject @Lookup("session://lucene/session/foo")
  LuceneSessionSync _lucene;

  @Test
  public void testText()
  {
    _lucene.indexText("foo", "foo", "mary had a little lamb");

    LuceneEntry[] result = _lucene.search("foo", "mary", 255);

    Assert.assertEquals(1, result.length);
    Assert.assertEquals("foo", result[0].getExternalId());
  }

  @Test
  public void testDelete()
  {
    _lucene.indexText("foo", "foo", "mary had a little lamb");
    _lucene.delete("foo", "foo");

    LuceneEntry[] result = _lucene.search("foo", "mary", 255);

    Assert.assertEquals(0, result.length);
  }

  @Test
  public void testMap()
  {
    Map<String,Object> map = new HashMap<>();

    map.put("foo", "mary had a little lamb");
    map.put("bar", "mary had two little lamb");
    map.put("zoo", "rose had three little lamb");

    map.put("age", 23);
    map.put("count", 32);

    _lucene.indexMap("foo", "map", map);

    LuceneEntry[] result = _lucene.search("foo", "foo:lamb", 255);
    Assert.assertEquals(1, result.length);

    result = _lucene.search("foo", "bar:two", 255);
    Assert.assertEquals(1, result.length);

    result = _lucene.search("foo", "age:[23 TO 23]", 255);
    Assert.assertEquals(1, result.length);

    result = _lucene.search("foo", "count:32", 255);
    Assert.assertEquals(1, result.length);

    result = _lucene.search("foo", "count:[33 TO 34]", 255);
    Assert.assertEquals(0, result.length);
  }

  @Before
  public final void baseBefore()
  {
    clear();
  }

  final protected void clear()
  {
    _lucene.clear("foo");
  }

  @After
  public final void baseAfter()
  {
    clear();
  }
}
