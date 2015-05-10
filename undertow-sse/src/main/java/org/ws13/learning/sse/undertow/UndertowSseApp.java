package org.ws13.learning.sse.undertow;

import io.undertow.Undertow;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import org.ws13.learning.sse.undertow.sse.SseConnectionCallback;

import static io.undertow.Handlers.path;

/**
 * @author ctranxuan
 */
public class UndertowSseApp {

  public static void main(String[] args) {
    final ServerSentEventHandler sseHandler;
    sseHandler = new ServerSentEventHandler(new SseConnectionCallback());

    Undertow server = Undertow.builder()
        .addHttpListener(8080, "localhost")
        .setHandler(path()
            .addPrefixPath("/sse", sseHandler))
        .build();
    server.start();
  }
}
