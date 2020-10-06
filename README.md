# ScissLib

[![Build Status](https://travis-ci.org/Sciss/ScissLib.svg?branch=main)](https://travis-ci.org/Sciss/ScissLib)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/scisslib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/scisslib)

## statement

ScissLib is a Java library that contains different functionality such as GUI building, application framework, audio
file I/O, etc. It is a core library used by other projects such as Eisenkraut, FScape, or SwingOSC.

ScissLib is (C)opyright 2004–2020 by Hanns Holger Rutz. All rights reserved. It is released under the
[GNU Lesser General Public License](https://raw.github.com/Sciss/ScissLib/main/licenses/ScissLib-License.txt) and
 omes with absolutely no warranties. Note that versions prior to v1.0.0 used the GPL and not the LGPL.

For project status and current version, visit [github.com/Sciss/ScissLib](https://github.com/Sciss/ScissLib).
To contact the author, send an email to `contact at sciss.de`

## requirements / building

ScissLib requires Java 8 and builds with [sbt](http://www.scala-sbt.org/).

The compile use `sbt compile`, to package up the jar use `sbt package`. For the javadocs, use `sbt doc`. The result
is found in `target/api/index.html`.

## linking

To use this library in your project, you can link to the following [Maven](http://search.maven.org) artifact:

    GroupId: de.sciss
    ArtifactId: scisslib
    Version: 1.1.3

## contributing

Please see the file [CONTRIBUTING.md](CONTRIBUTING.md)

## change history

 - v1.1.3 (oct 2020). fix string encoding (comments, markers) for AIFF, Wave, SND
 - v1.1.1 (oct 2019). avoid user borders on components, more pref-width for unit-label (WebLookAndFeel quirk)
 - v1.1.0 (mar 2016). supports dark look-and-feel. drop MRJ adaptor.; v1.1.1 - colour enhancements
 - v1.0.0 (aug 2013). fixes issue no. 1 (remove unnecessary scala-library dependency).
 - v0.15 (apr 2012). moved from SourceForge/svn/Eclipse/ant to GitHub/git/IDEA/sbt.
 - v0.12 (jul 2009)
 - v0.10 (oct 2008) the first separate release
