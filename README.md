* Introduction
  * fil2gvs is a handy utility to create a Global Value Set (gvs) in a format suitable for import into Salesforce.com Cloud (SCL), using the [ANT Migration Tool](https://developer.salesforce.com/docs/atlas.en-us.daas.meta/daas/meta_development.htm)
  * input is a file in csv format - an example file is part of the distrib. and can be find at resources/Example.txt

* Prerequisites
  * mandatory - to run fil2gvs & ant
    * java jdk/jre
      * you might already have java jre/jdk installed - however "ANT Migration Tool" demands version 11 or higher
      * check install., executing "%java_home%\bin\java" -version
      * in order to install: visit [Adopt Open JDK](https://adoptopenjdk.net/) , choose "OpenJDK 11 (LTS), download & install - in the following we will refer to java install. dir. by %java_home%
      * note regarding java_home env. variable: you can set it globally, which will save you some work later, however its inflexible esp. if you have multiple java jre/jdk installed
    * ant
      * [Download](https://downloads.apache.org//ant/binaries/apache-ant-1.10.11-bin.zip) , extract zip , move to appropriate location & create env. variable ant_home pointing to install. dir.
      * in case, you didn't created java_home env. var as stated above, edit "%ant_home%\bin\ant.bat"
        * insert below the header comment at about line 23 the following line - change accordingly to your java jdk/jre installation!
          * set JAVA_HOME=C:\Program Files\Java\openjdk-11.0_11.9
      * check install., executing "%ant_home%\bin\ant" -version
  * optional - to build from source
    * maven
      * [Download](https://maven.apache.org/download.cgi) , extract zip , move to appropriate location & create env. variable mvn_home pointing to install. dir.
      * in case, you didn't created java_home env. var as stated above, edit "%ant_home%\bin\mvn.cmd"
        * insert below the line "@REM ==== START VALIDATION ====" at about line 47 the following line - change accordingly to your java jdk/jre installation!
          * set JAVA_HOME=C:\Program Files\Java\openjdk-11.0_11.9
      * check install., executing "%mvn_home%\bin\mvn.cmd" -version

* Install
  * source release
    * in file explorer, select a dir. where you would like to create a local fil2gvs repo, open git bash and exec. the following commands:
      * $> git clone https://github.com/urban-in-town/fil2gvs.git
      * $> cd fil2gvs
      * $> $mvn_home/bin/mvn.cmd package
    * inspect build result, archive target/fil2gvs-1.0-SNAPSHOT.jar
    * for next steps see "binary release" section
  * binary release
    * <tbd>


