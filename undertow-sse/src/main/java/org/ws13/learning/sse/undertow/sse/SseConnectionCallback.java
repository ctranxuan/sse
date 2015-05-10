package org.ws13.learning.sse.undertow.sse;

import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author ctranxuan
 */
public class SseConnectionCallback implements ServerSentEventConnectionCallback {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseConnectionCallback.class);

  static class Data {
    String name;
    int value;
    String time;

    public Data(String name) {
      this.name = name;
      this.value = ThreadLocalRandom.current().nextInt(100);
      time = LocalTime.now().toString();
    }

    @Override
    public String toString() {
      return "{" +
          "name: '" + name + '\'' +
          ", value: '" + value + '\'' +
          ", time:'" + time + '\'' +
          '}';
    }
  }

  static class EventGenerator implements Runnable {
    private String name;
    private String type;
    private ServerSentEventConnection connection;

    public EventGenerator(String name,
                          String aType,
                          ServerSentEventConnection aServerSentEventConnection) {
      this.name = name;
      this.type = aType;
      this.connection = aServerSentEventConnection;
    }

    @Override
    public void run() {
      while (true) {
        Data data;
        data = new Data(name);

        connection.send(data.toString(), type, UUID.randomUUID().toString(), new ServerSentEventConnection.EventCallback() {
          @Override
          public void done(final ServerSentEventConnection connection, final String data, final String event, final String id) {
            LOGGER.info("data {} has been sent with event [type={}, id={}]", data, event, id);
          }

          @Override
          public void failed(final ServerSentEventConnection connection, final String data, final String event, final String id, final IOException e) {
            LOGGER.error("data {} has not been sent with event [type={}, id={}]", data, event, id, e);
          }
        });

        try {
          Thread.sleep(
              ThreadLocalRandom.current().nextInt(
                  (int) TimeUnit.SECONDS.toMillis(20)));

        } catch (InterruptedException ignore) {

        }
      }
    }
  }

  private ServerSentEventConnection connection;

  @Override
  public void connected(final ServerSentEventConnection aConnection, final String aLastEventId) {
    LOGGER.info("connection initiated with the eventId {}", aLastEventId);

    connection = aConnection;

    new Thread(new EventGenerator("Grenoble", "snapshot", connection)).start();
  }

}
