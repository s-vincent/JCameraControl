# JCameraControl

Simple JavaFX application that displays all connected webcams streams.

The project uses [webcam-capture](https://github.com/sarxos/webcam-capture)
project for webcam access.

Although the UI elements of webcam-capture are in Java Swing, it is possible to
wrap them in JavaFX with javafx.embed.swing.SwingNode class.

## Pre-requisites

You need JDK 17 (either from Oracle or OpenJDK), JavaFX/OpenJFX and maven.

On GNU/Linux Debian like distributions you can use:

`apt-get install default-jdk openjfx mvn`

## Build

To build the project:

`mvn package`

To build the javadoc:

`mvn javadoc:javadoc`

## Usage

Once build, the compiled jar as well as dependencies jars are in target/.

To run from project directory:

`java -p target/lib --add-modules javafx.controls,javafx.swing 
-jar target/JCameraControl-1.0-SNAPSHOT.jar`

You can also run it via maven:

`mvn exec:java` or `mvn exec:exec`

## Eclipse

Eclipse can be used to build, debug and run this project as well.

To do so, use import option, select "Existing Maven projects" and finally set
the project directory as "Root Directory".

## License

All codes are under BSD-3 license.

## Links

 * https://github.com/sarxos/webcam-capture

