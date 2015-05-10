package org.ws13.learning.sse.simpleweb4j.sse;

import fr.ybonnel.simpleweb4j.handlers.ContentType;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.SimpleWeb4jHandler;
import fr.ybonnel.simpleweb4j.handlers.eventsource.ReactiveStream;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ctranxuan
 */
public class SseSimpleWeb4jHandler extends SimpleWeb4jHandler {
  @Override
  protected void writeHttpResponseForEventSource(final HttpServletRequest request, final HttpServletResponse response, final ContentType contentType, final Response<?> handlerResponse) throws IOException {
    super.writeHttpResponseForEventSource(request, response, contentType, handlerResponse);

    if (handlerResponse.getAnswer() instanceof ReactiveStream) {
      final Continuation continuation = ContinuationSupport.getContinuation(request);
      // Infinite timeout because the continuation is never resumed,
      // but only completed on close
      continuation.setTimeout(0L);
      continuation.suspend(response);

      ((Response<ReactiveStream>) handlerResponse).getAnswer().setReactiveHandler(
          new SseReactiveHandler(continuation));
    }
  }
}
