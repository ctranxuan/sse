package org.ws13.learning.sse.simpleweb4j.sse;

import fr.ybonnel.simpleweb4j.exception.FatalSimpleWeb4jException;
import fr.ybonnel.simpleweb4j.handlers.ContentType;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.SimpleWeb4jHandler;
import fr.ybonnel.simpleweb4j.handlers.eventsource.ReactiveStream;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author ctranxuan
 */
public class SseSimpleWeb4jHandler extends SimpleWeb4jHandler {
  private static final String EVENT_STREAM_CONTENT_TYPE = "text/event-stream;charset=" + Charset.defaultCharset().displayName();

  @Override
  protected void writeHttpResponseForEventSource(final HttpServletRequest request, final HttpServletResponse response, final ContentType contentType, final Response<?> handlerResponse) throws IOException {
    response.setContentType(EVENT_STREAM_CONTENT_TYPE);
    response.addHeader("Connection", "close");
    response.flushBuffer();

    final Continuation continuation = ContinuationSupport.getContinuation(request);
    // Infinite timeout because the continuation is never resumed,
    // but only completed on close
    continuation.setTimeout(0L);
    continuation.suspend(response);

    if (handlerResponse.getAnswer() instanceof ReactiveStream) {
      ((Response<ReactiveStream>) handlerResponse).getAnswer().setReactiveHandler(
          new SseReactiveHandler(continuation));

    } else {
      throw new FatalSimpleWeb4jException("Your answer is an unknown stream");

    }
  }
}
