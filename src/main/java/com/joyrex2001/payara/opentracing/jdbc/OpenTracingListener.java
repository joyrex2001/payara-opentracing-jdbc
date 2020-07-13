package com.joyrex2001.payara.opentracing.jdbc;

import org.glassfish.api.jdbc.SQLTraceListener;
import org.glassfish.api.jdbc.SQLTraceRecord;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

public class OpenTracingListener implements SQLTraceListener {

    private Tracer tracer;

    final private String PREPARE_STATEMENT = "prepareStatement";
    final private String EXECUTE_STATEMENT = "executeQuery";
    final private String CLOSE_STATEMENT = "close";

    ThreadLocal<Span> currentSpan = new ThreadLocal<>();

    @Override
    public void sqlTrace(SQLTraceRecord record) {
        switch(record.getMethodName()) {
            case PREPARE_STATEMENT:
                this.prepare(record);
                break;
            case EXECUTE_STATEMENT:
                this.execute(record);
                break;
            case CLOSE_STATEMENT:
                this.close();
                break;
        }
    }

    private void prepare(SQLTraceRecord record) {
        Span span = this.newSpan(record.getClassName());
        Tags.DB_STATEMENT.set(span, record.getParams()[0].toString());
    }

    private void execute(SQLTraceRecord record) {
        this.getSpan().log(record.toString());
    }

    private void close() {
        this.getSpan().finish();
    }

    private Span newSpan(String operationName) {
        Span span = this.getTracer().buildSpan(operationName).start();
        currentSpan.set(span);
        return span;
    }

    private Span getSpan() {
        return currentSpan.get();
    }

    private Tracer getTracer() {
        if (tracer == null) {
            return GlobalTracer.get();
        }
        return tracer;
    }
}