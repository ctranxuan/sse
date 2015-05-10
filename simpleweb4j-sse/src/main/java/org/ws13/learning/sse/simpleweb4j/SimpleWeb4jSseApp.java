package org.ws13.learning.sse.simpleweb4j;

import com.google.common.collect.ImmutableList;
import fr.ybonnel.simpleweb4j.handlers.FunctionnalRouteUtil;
import fr.ybonnel.simpleweb4j.handlers.HttpMethod;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.eventsource.EndOfStreamException;
import fr.ybonnel.simpleweb4j.handlers.eventsource.ReactiveHandler;
import fr.ybonnel.simpleweb4j.handlers.eventsource.ReactiveStream;
import fr.ybonnel.simpleweb4j.server.SimpleWeb4jServer;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.ws13.learning.sse.simpleweb4j.sse.Sse;
import org.ws13.learning.sse.simpleweb4j.sse.SseSimpleWeb4jHandler;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.get;
/**
 * @author ctranxuan
 */
public class SimpleWeb4jSseApp {

  // Un événement
  static class Data {
    String name;
    int value;
    String time;

    public Data(String name) {
      this.name = name;
      this.value = ThreadLocalRandom.current().nextInt(100);
      time = LocalTime.now().toString();
    }
  }

  // Boucle infinie générant des événements.
  static class EventGenerator implements Runnable {
    private String name;
    public EventGenerator(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      while (true) {
        // Création d'un nouvel événement
        Data data = new Data(name);
        // Pour chaque handler, on envoie l'événement
        handlers.forEach(handler -> sendEventToHandler(handler, data));
        try {
          // Attente entre 0 et 20 secondes.
          Thread.sleep(
              ThreadLocalRandom.current().nextInt(
                  (int) TimeUnit.SECONDS.toMillis(20)));
        } catch (InterruptedException ignore) {
        }
      }
    }
  }

  // Envoi d'un événement à un handler.
  static void sendEventToHandler(ReactiveHandler<Sse<Data>> handler, Data aData) {
    try {
      handler.next(new Sse<>("snapshot", aData));
    } catch (EndOfStreamException e) {
      // En cas de fermeture du flux par le client, on supprimer le handler.
      handlers.remove(handler);
    }
  }

  private static ConcurrentHashSet<ReactiveHandler<Sse<Data>>> handlers = new ConcurrentHashSet<>();

  public static void main(String[] args) {

    // Démarrage des générateurs.
    new Thread(new EventGenerator("Grenoble")).start();
    new Thread(new EventGenerator("Rennes")).start();
    new Thread(new EventGenerator("Caen")).start();


    SseSimpleWeb4jHandler sseSimpleWeb4jHandler;
    sseSimpleWeb4jHandler = new SseSimpleWeb4jHandler();

    sseSimpleWeb4jHandler.addRoute(HttpMethod.GET,
        FunctionnalRouteUtil.functionnalRouteToRoute(
            (param, routeParam) -> {
              // Déclaration de la route SimpleWeb4j
              // C'est le fait que la réponse contienne un ReactiveStream qui
              // va nous permettre de faire du Server Sent Events.
              // ReactiveStream est une interface avec une seule méthode
              // qui va être appelée avec le handler SimpleWeb4j lors de
              // l'ouverture du flux, on peut donc utiliser une lambda.
              return new Response<ReactiveStream<Sse<Data>>>(h -> handlers.add(h));
            },
            "/reactive",
            Void.class));

    SimpleWeb4jServer server;
    server = new SimpleWeb4jServer(9999, "/public", null, ImmutableList.of(sseSimpleWeb4jHandler));
    server.start(true);
  }
}
