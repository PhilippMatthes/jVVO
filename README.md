```
The MIT License (MIT)

Copyright (c) 2017 Kilian Koeltzsch, Philipp Matthes

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

# 🚆jVVO
Java (Android) Framework for VVO (Verkehrsverbund Oberelbe) - Ported form kiliankoe/dvb

*`Adapted from kiliankoe/dvb`*

This is an unofficial Java package giving you a few options to query Dresden's public transport system for current bus- and tramstop data - ported from the Swift Version on kiliankoe/dvb.

Want something like this for another language, look [no further](https://github.com/kiliankoe/vvo#libraries) 🙂

## Installation

It is recommended to use gradle with jVVO to supply all needed packages. For a android project, use the following gradle build settings:

```gradle
implementation 'com.google.code.gson:gson:2.8.2'
compileOnly 'org.projectlombok:lombok:1.18.0'
annotationProcessor 'org.projectlombok:lombok:1.18.0'
implementation 'com.android.volley:volley:1.0.0'
```


## Quick Start

***Caveat***: Stops are always represented by their ID. You can get a stop's ID via `Stop.find()`. Some of the methods listed below offer convenience overloads, which are listed here since they look nicer. The downside to these is that they have to send of a find request for every stop first resulting in a significant overhead. Should you already have a stop's ID at hand I **strongly** suggest you use that instead.

### Monitor a single stop

Monitor a single stop to see every bus, tram or whatever leaving this stop. The necessary stop id can be found by using the `find()` function.


```java
RequestQueue queue = Volley.newRequestQueue(this);

Departure.monitorByName("Hauptbahnhof", queue, response -> {
    if (response.getResponse().isPresent()) {
        System.out.println(response.getResponse().get().getDepartures());
    } else {
        System.out.println(response.getError().get().getDescription());
    }
});
```

### Find a specific stop

Say you're looking for "Tharandter Straße". You can use the following to find a list of matches.

```java
RequestQueue queue = Volley.newRequestQueue(this);

Stop.find("Tharandter Straße", queue, response -> {
    if (response.getResponse().isPresent()) {
        System.out.println(response.getResponse().get().getStops());
    } else {
        System.out.println(response.getError().get().getDescription());
    }
});
```

### Find a route from A to B

Want to go somewhere?

```java
RequestQueue queue = Volley.newRequestQueue(this);

Route.findByName("Tharandter Straße", "Hauptbahnhof", queue, response -> {
    if (response.getResponse().isPresent()) {
        for (Route route : response.getResponse().get().getRoutes()) {
            // Do something with routes
        }
    } else {
        System.out.println(response.getError().get().getDescription());
    }
});
```

Or if you already have the stop IDs:

```java
RequestQueue queue = Volley.newRequestQueue(this);

Route.find("33000155", "33000028", queue, response -> {
    if (response.getResponse().isPresent()) {
        for (Route route : response.getResponse().get().getRoutes()) {
            // Do something with routes
        }
    } else {
        System.out.println(response.getError().get().getDescription());
    }
});
```

### Look up current route changes

Want to see if your favorite lines are currently being re-routed due to construction or some other reason? Check the published list of route changes.

```java
RequestQueue queue = Volley.newRequestQueue(this);

RouteChange.get(queue, response -> {
    if (response.getResponse().isPresent()) {
        for (RouteChange change : response.getResponse().get().getChanges()) {
            System.out.println(change.getTitle());
        }
    } else {
        System.out.println(response.getError().get().getDescription());
    }
});
```

### Lines running at a specific stop

Looking to find which lines service a specific stop? There's a func for that.

```java
RequestQueue queue = Volley.newRequestQueue(this);

Line.getByName("Hauptbahnhof", queue, response -> {
    if (response.getResponse().isPresent()) {
        System.out.println(response.getResponse().get().getLines());
    } else {
        System.out.println(response.getError().get().getDescription());
    }
});
```

## Authors of the Swift Library from [kiliankoe/dvb](https://github.com/kiliankoe/DVB)

Kilian Koeltzsch, [@kiliankoe](https://github.com/kiliankoe)

Max Kattner, [@maxkattner](https://github.com/maxkattner)

## Authors of the ported Java Library

Philipp Matthes, [@PhilippMatthes](https://github.com/philippmatthes)

## License

jVVO is available under the MIT license. See the LICENSE file for more info.

The main part of this library is ported from the Swift library on [kiliankoe/dvb](https://github.com/kiliankoe/DVB). Please refer to their LICENSE file as well.

## Terms of Service

Please refer to the [VVO Terms of Service](https://www.vvo-online.de/de/service/widgets/nutzungsbedingungen-1671.cshtml) regarding their widget. Take particular care not to use this library to hammer their servers through too many requests to their graciously-provided API.
