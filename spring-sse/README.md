This project uses [Spring](http://http://projects.spring.io/spring-framework//) to create a [Server-Sent Event W3C specification](http://www.w3.org/TR/eventsource/) server.
To launch the server, just run the main of the class `SpringSseApp`.

To test the server: 
```curl -H "Accept: text/event-stream" http://localhost:8080/sse ```