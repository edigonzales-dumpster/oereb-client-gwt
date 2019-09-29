[![Build Status](https://travis-ci.org/edigonzales/oereb-client-gwt.svg?branch=master)](https://travis-ci.org/edigonzales/oereb-client-gwt)

# oereb-client-gwt

## Development

First Terminal:
```
mvn clean spring-boot:run
```

Second Terminal:
```
mvn gwt:generate-module gwt:codeserver
```

Or simple devmode (which worked better for java.xml.bind on client side):
```
mvn gwt:generate-module gwt:devmode 
```

Build fat jar and docker image:
```
TRAVIS_BUILD_NUMBER=9999 mvn package
```

## Running
```
docker run -p 8080:8080 sogis/oereb-client
```

### Behind a proxy
Läuft auf den ersten Blick problemlos: `localhost:8282` -> `localhost/client/`. Wichtig ist der trailing slash. Ohne den funktioniert es nicht. Aus diesem Grund gibt es noch eine Weiterleitung (`redir`).

Weiterführende Links:
- https://randling.wordpress.com/2013/05/26/caching-gwt-files-with-nginx/
- ...

