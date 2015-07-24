package org.ws13.learning.sse.spring.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author ctranxuan
 */
@RestController
public class SseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SseController.class);

  @RequestMapping("/sse")
  public SseEmitter sse() {
    final SseEmitter emitter = new SseEmitter();

    Observable.interval(5, TimeUnit.SECONDS, Schedulers.io())
        .subscribe(new Subscriber<Long>() {
          @Override
          public void onCompleted() {
            emitter.complete();
          }

          @Override
          public void onError(final Throwable e) {
            emitter.completeWithError(e);
          }

          @Override
          public void onNext(final Long aLong) {
            try {
              SseEmitter.SseEventBuilder eventBuilder;
              eventBuilder = SseEmitter.event();

              eventBuilder.id(UUID.randomUUID().toString());
              eventBuilder.name("snapshot");
              eventBuilder.data(new Data("Grenoble"));

              emitter.send(eventBuilder);

            } catch (IOException e) {
              throw new RuntimeException(e);

            }
          }
        });

    return emitter;
  }

  @RequestMapping("/sse2")
  public SseEmitter sse2() {
    SseEmitter emitter = new SseEmitter();
    new Thread(new SseController.EventGenerator("Grenoble", "snaphot", emitter)).start();

    return emitter;
  }

  static class Data {
    String name;
    int value;
    String time;

    public Data(String name) {
      this.name = name;
      this.value = ThreadLocalRandom.current().nextInt(100);
      time = LocalTime.now().toString();
    }

    public String getTime() {
      return time;
    }

    public int getValue() {
      return value;
    }

    public String getName() {
      return name;
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
    private SseEmitter emitter;

    public EventGenerator(String name,
                          String aType,
                          SseEmitter aSseEmitter) {
      this.name = name;
      this.type = aType;
      this.emitter = aSseEmitter;
    }

    @Override
    public void run() {
      while (true) {
        Data data;
        data = new Data(name);

        SseEmitter.SseEventBuilder eventBuilder;
        eventBuilder = SseEmitter.event();

        eventBuilder.id(UUID.randomUUID().toString());
        eventBuilder.name(type);
        eventBuilder.data(data);
        eventBuilder.reconnectTime(5000);

        try {
          emitter.send(eventBuilder);

        } catch (IOException e) {
          e.printStackTrace();

        }

        try {
          Thread.sleep(
              ThreadLocalRandom.current().nextInt(
                  (int) TimeUnit.SECONDS.toMillis(30)));

        } catch (InterruptedException ignore) {
          emitter.complete();

        }
      }
    }
  }

}
