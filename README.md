# ScissLib

[![Flattr this](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=sciss&url=https%3A%2F%2Fgithub.com%2FSciss%2FScissLib&title=ScissLib&language=Scala&tags=github&category=software)
[![Build Status](https://travis-ci.org/Sciss/ScissLib.svg?branch=master)](https://travis-ci.org/Sciss/ScissLib)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/scisslib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/scisslib)

## statement

ScissLib is a Java library that contains different functionality such as GUI building, application framework, audio file I/O, etc. It is a core library used by other projects such as Eisenkraut, FScape, or SwingOSC.

ScissLib is (C)opyright 2004–2016 by Hanns Holger Rutz. All rights reserved. It is released under the [GNU Lesser General Public License](https://raw.github.com/Sciss/ScissLib/master/licenses/ScissLib-License.txt) and comes with absolutely no warranties. Note that versions prior to v1.0.0 used the GPL and not the LGPL.

For project status and current version, visit [github.com/Sciss/ScissLib](https://github.com/Sciss/ScissLib). To contact the author, send an email to `contact at sciss.de`

## requirements / building

ScissLib requires Java 1.4 and builds with [sbt](http://www.scala-sbt.org/) 0.12. For simplicity, a shell script named `sbt` is included 
(BSD-style license) which can be used instead of downloading and installing sbt.

The compile use `sbt compile`, to package up the jar use `sbt package`. For the javadocs, use `sbt doc`. The result is found in `target/api/index.html`.

## linking

To use this library in your project, you can link to the following [Maven](http://search.maven.org) artifact:

    GroupId: de.sciss
    ArtifactId: scisslib
    Version: 1.0.1

## contributing

Please see the file [CONTRIBUTING.md](CONTRIBUTING.md)

## source code

This project's source code is published on [github.com/Sciss/ScissLib](https://github.com/Sciss/ScissLib). It uses Steve Roy's MRJAdapter published under the Artistic License, source code provided through [java.net/projects/mrjadapter/sources/svn/show](https://java.net/projects/mrjadapter/sources/svn/show).

MRJAdapter is not included as a Maven dependency. Therefore, you must include MRJAdapter manually in your projects.

## change history

 - v1.0.0 (aug 2013). fixes issue no. 1 (remove unnecessary scala-library dependency).
 - v0.15 (apr 2012). moved from SourceForge/svn/Eclipse/ant to GitHub/git/IDEA/sbt.
 - v0.12 (jul 2009)
 - v0.10 (oct 2008) the first separate release
