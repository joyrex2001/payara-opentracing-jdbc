package com.joyrex2001.payara.opentracing.jdbc;

import org.glassfish.api.jdbc.SQLTraceListener;
import org.glassfish.api.jdbc.SQLTraceRecord;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

public class OpenTracingListener implements SQLTraceListener {

    private Tracer tracer;

    private static final String GET_CLIENT_INFO = "getClientInfo";
    private static final String SET_CLIENT_INFO = "setClientInfo";
    private static final String PREPARE_STATEMENT = "prepareStatement";
    private static final String CLOSE_STATEMENT = "close";

    ThreadLocal<Span> currentSpan = new ThreadLocal<>();

    @Override
    public void sqlTrace(SQLTraceRecord record) {
        switch(record.getMethodName()) {
            case GET_CLIENT_INFO:
                break;
            case SET_CLIENT_INFO:
                break;
            case PREPARE_STATEMENT:
                this.prepare(record);
                break;
            case CLOSE_STATEMENT:
                this.close();
                break;
            default:
                this.log(record);
                break;
        }
    }

    private void prepare(SQLTraceRecord record) {
        Span span = this.newSpan(record.getClassName());
        Tags.DB_STATEMENT.set(span, record.getParams()[0].toString());
    }

    private void log(SQLTraceRecord record) {
        Span span = this.getSpan();
        if (span != null) {
            span.log(record.toString());
        }
    }

    private void close() {
        this.getSpan().finish();
        currentSpan.set(null);
    }

    private Span newSpan(String operationName) {
        Span span = this.getTracer().buildSpan(operationName).start();
        currentSpan.set(span);
        return span;
    }

    Span getSpan() {
        return currentSpan.get();
    }

    Tracer getTracer() {
        if (tracer == null) {
            tracer = GlobalTracer.get();
        }
        return tracer;
    }
}