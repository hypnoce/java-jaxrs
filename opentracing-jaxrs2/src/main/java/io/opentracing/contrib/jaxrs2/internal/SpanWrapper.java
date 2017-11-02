package io.opentracing.contrib.jaxrs2.internal;

import io.opentracing.Span;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wrapper class used for exchanging span between filters.
 *
 * @author Pavol Loffay
 */
public class SpanWrapper {

    private Span span;
    private Closeable closeAction;
    private AtomicBoolean finished = new AtomicBoolean();

    public SpanWrapper(final Span span) {
        this(span, new Closeable() {
            @Override
            public void close() throws IOException {
                span.finish();
            }
        });
        this.span = span;
    }

    public SpanWrapper(Span span, Closeable closeAction) {
        this.span = span;
        this.closeAction = closeAction;
    }

    public Span get() {
        return span;
    }

    public synchronized void finish() throws IOException {
        if (!finished.get()) {
            finished.set(true);
            closeAction.close();
        }
    }

    public boolean isFinished() {
        return finished.get();
    }
}
