package org.ws13.learning.sse.simpleweb4j.sse;

import fr.ybonnel.simpleweb4j.handlers.ContentType;
import fr.ybonnel.simpleweb4j.handlers.eventsource.EndOfStreamException;
import fr.ybonnel.simpleweb4j.handlers.eventsource.ReactiveEventSourceTask;
import org.eclipse.jetty.continuation.Continuation;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ctranxuan
 */
public class SseReactiveHandler extends ReactiveEventSourceTask {
  private Continuation continuation;

  /**
   * Constructor.
   *
   * @param aContinuation continuation object for async responses.
   */
  public SseReactiveHandler(final Continuation aContinuation) {
    super(ContentType.JSON, aContinuation);
    continuation = aContinuation;
  }

  @Override
  public void next(final Object aObject) throws EndOfStreamException {
    if (aObject instanceof Sse) {
      Sse<?> sse = (Sse) aObject;

      try {
        PrintWriter writer;
        writer = continuation.getServletResponse().getWriter();

        writer.print("event: ");
        writer.print(sse.getType());
        writer.println();
        writer.print("id: ");
        writer.print(sse.getId());
        writer.println();
        writer.print("data: ");
        writer.print(ContentType.JSON.convertObject(sse.getData()));
        writer.print("\n\n");
        writer.flush();

        continuation.getServletResponse().flushBuffer();
      } catch (IOException e) {
        continuation.complete();
        throw new EndOfStreamException(e);
      }

    } else {
      super.next(aObject);

    }
  }
}
