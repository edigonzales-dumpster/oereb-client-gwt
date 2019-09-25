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

