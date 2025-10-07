Walker
------
>Walker app using ScalaFx, ScalikeJdbc, Jsoniter, JoddMail, PostgreSql, HikariCP, Helidon, Ox and Scala 3.

Model
-----
>A session represents a walk.
* Walker 1 ---> * Session

Calculations
------------
1. [Calories Burned Walking](https://captaincalculator.com/health/calorie/calories-burned-walking-calculator/)
   
Build
-----
1. sbt clean compile

Test
----
1. sbt clean test

Run
---
1. sbt run

Client Assembly
---------------
1. sbt clean test assembly copyAssemblyJar

Execute Client
--------------
1. java -jar .assembly/walker-$version.jar ( or double-click executable jar )

Resources
---------
* [JavaFX](https://openjfx.io/index.html)
* [JavaFX Tutorial](https://jenkov.com/tutorials/javafx/index.html)
* [ScalaFX](http://www.scalafx.org/)
* [ScalikeJdbc](http://scalikejdbc.org/)

License
-------
>Copyright (c) [2025] [Objektwerks]

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    * http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.