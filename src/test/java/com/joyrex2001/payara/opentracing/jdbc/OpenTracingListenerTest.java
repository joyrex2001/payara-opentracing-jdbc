package com.joyrex2001.payara.opentracing.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracerTestUtil;

import org.glassfish.api.jdbc.SQLTraceRecord;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OpenTracingListenerTest {

    private static final MockTracer mockTracer = new MockTracer();

    @BeforeClass
    public static void init() {
      GlobalTracerTestUtil.setGlobalTracerUnconditionally(mockTracer);
    }
  
    @Before
    public void before() {
      mockTracer.reset();
    }
  
    @Test
    public void testGlobalTracer() {
        OpenTracingListener listener = new OpenTracingListener();
        assertNotNull(listener.getTracer());
    }
  
    @Test
    public void testExplicitTracer() {
        OpenTracingListener listener = new OpenTracingListener();
        assertNull(listener.getSpan());

        SQLTraceRecord record = new SQLTraceRecord();
        record.setMethodName("getMetaData");
        record.setParams(null);
        listener.sqlTrace(record);
        assertNull(listener.getSpan());

        String[] params = new String[1];
        params[0] = "select wisdom from world";
        record.setMethodName("prepareStatement");
        record.setParams(params);
        listener.sqlTrace(record);
        assertNotNull(listener.getSpan());

        record.setMethodName("execute");
        record.setParams(null);
        listener.sqlTrace(record);
        assertNotNull(listener.getSpan());

        record.setMethodName("close");
        record.setParams(null);
        listener.sqlTrace(record);
        assertNotNull(listener.getSpan());

        List<MockSpan> spans = mockTracer.finishedSpans();
        assertEquals(1, spans.size());
    }

}