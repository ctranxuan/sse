This project uses [SimpleWeb4j](https://github.com/ybonnel/SimpleWeb4j) to create a Server-Sent Event server.
To launch the server, just run the main of the class `SimpleWeb4jSseApp`. It's a modified version of the sample 
http://www.ybonnel.fr/2014/06/server-sent-events-reactifs-avec.html. This version supports the `id` and `event` pragma
as defined in the [Server-Sent Event W3C specification](http://www.w3.org/TR/eventsource/).

To test the server: 
```curl http://localhost:9999/reactive```