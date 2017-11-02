# A-SUM
A-SUM (Automatic Smartphone Usage Monotoring) blocks apps in social and unhealthy scenarios where smartphone usage is not ideal.

The idea for the app is focuses on younger generation who are glued to their smartphones 24 7. Currently, three social- Restaurant, Movie Theatre, and Religious places and two unhealthy- using phone in the bed in darkness and walking are considered. A user could configure a rule to block apps in any of these scenarios. 

The location of a device is fetched using FusedLocationProviderApi. These location coordinates are passed to google places api which returns a list of places. including a place_type object. If the user is in one of the above mentioned social scenarios and a rule is configures, A-SUM would not let a user access the blocked applications.

