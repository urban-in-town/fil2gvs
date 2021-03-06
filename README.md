* __Introduction__
  * fil2gvs is a handy utility to create a Global Value Set (gvs) in a format suitable for import into Salesforce.com Cloud (SCL), using the [Ant Migration Tool](https://developer.salesforce.com/docs/atlas.en-us.daas.meta/daas/meta_development.htm) (SCL mig tool)
  * input is a file in csv format - an example input file is part of the distrib. archive (/ExampleGvs.txt)

* __Prerequisites__
  * mandatory - to run fil2gvs & SCL mig tool
    * java jdk/jre
      * you might already have java jre/jdk installed - however SCL mig tool demands version 11 or higher
      * check install., executing "%java_home%\bin\java" -version
      * in order to install: visit [Adopt Open JDK](https://adoptopenjdk.net/) , choose "OpenJDK 11 (LTS), download & install - in the following we will refer to java install. dir. by %java_home%
      * note regarding java_home env. variable: you can set it globally, which will save you some work later, however its inflexible esp. if you have multiple java jre/jdk installed
    * ant
      * [Download](https://downloads.apache.org//ant/binaries/apache-ant-1.10.11-bin.zip) , extract zip , move to appropriate location & create env. variable ant_home pointing to install. dir.
      * in case, you didn't created java_home env. var as stated above, edit "%ant_home%\bin\ant.bat"
        * insert below the header comment at about line 23 the following line - change accordingly to your java jdk/jre installation!
          * set JAVA_HOME=C:\Program Files\Java\openjdk-11.0_11.9
      * check install., executing "%ant_home%\bin\ant" -version
    * SCL mig tool
      * [Download](https://gs0.salesforce.com/dwnld/SfdcAnt/salesforce_ant_52.0.zip) , extract zip & copy ant-salesforce.jar to %ant_home%\lib
      * notes
        * SCL mig tool consists of a bunch of Ant tasks, implemented by the above mentioned jar - copying it to Ant's lib dir. makes it available for any SCL mig project
        * fil2gvs comes with a SCL mig tool project template to give you a headstart
        * you will usually create a lot of those SCL mig tool projects - even in the simplest case with a dev/prod SCl instance you will need already two of them ...    
  * optional - to build from source
    * git for windows
      * in order to install, visit [Git for Windows](https://github.com/git-for-windows/git/releases/latest) , choose an 64 Bit installer, download and  install
    * maven
      * [Download](https://maven.apache.org/download.cgi) , extract zip , move to appropriate location & create env. variable mvn_home pointing to install. dir.
      * in case, you didn't created java_home env. var as stated above, edit "%ant_home%\bin\mvn.cmd"
        * insert below the line "@REM ==== START VALIDATION ====" at about line 47 the following line - change accordingly to your java jdk/jre installation!
          * set JAVA_HOME=C:\Program Files\Java\openjdk-11.0_11.9
      * check install., executing "%mvn_home%\bin\mvn.cmd" -version

* __Installation__
  * source release
    * in file explorer, select a dir. where you would like to create a local fil2gvs repo, open git bash and exec. the following commands:
      * $> git clone https://github.com/urban-in-town/fil2gvs.git
      * $> cd fil2gvs
      * $> $mvn_home/bin/mvn.cmd package
    * inspect build result, archive target/fil2gvs-1.0-SNAPSHOT.jar
    * for next steps see "binary release" section
  * binary release
    * create a directory fil2gvs in suitable place and create a env. variable fil2gvs_home pointing to it
    * put archive fil2gvs-1.0-SNAPSHOT.jar to newly created dir. %fil2gvs_home%
      * either taking it from source build's directory target
      * or downloading it from [latest fil2gvs release](https://github.com/urban-in-town/fil2gvs/releases/latest)
    * check install. as follow
      * open fil2gvs-1.0-SNAPSHOT.jar in an archive viewer [7zip](https://www.7-zip.org/) or other and drag fil2gvs.bat to %fil2gvs_home%
      * open %fil2gvs_home%\fil2gvs.bat in an editor (notepad++ etc.), check/change the java_home setup at line 5 and save
      * in command window execute
        * c> "%fil2gvs_home%\fil2gvs"
        * the expected output shell read like "Exception i n thread "main" java.lang.RuntimeException: Invalid call, ch.menticorp.sfdc.tools.File2GlobalValueSet expects one <file> param!"
        * if it reads like "The system cannot find the path specified." then your java_home setup is incorrect 

* __Transform ExampleGvs.txt into ExampleGvs.globalValueSet__ 
  * open fil2gvs-1.0-SNAPSHOT.jar in an archive viewer [7zip](https://www.7-zip.org/) or other and drag ExampleGvs.txt to %fil2gvs_home%
  * in command window execute
    * c> cfil2gvs.bat %fil2gvs_home%\ExampleGvs.txt
  * inspect the result %fil2gvs_home%\ExampleGvs.globalValueSet, a gvs ready for SCL deployment

* __Deploy ExampleGvs.globalValueSet to a SCL__
  * open fil2gvs-1.0-SNAPSHOT.jar in an archive viewer [7zip](https://www.7-zip.org/) or other and drag directory _scl-mig-tool-project-template to %fil2gvs_home%
  * steps to enable our project for SCL deployment
    * login to any SCL sandbox via SCL/GUI & open in web browser dev tools (&lt;ctrl&gt;&lt;shift&gt;+I), select tab "Application", then in navigation bar "Storage/Cookies" and there cookie named "sid"
    * open and edit build.properties in projec dir. as described in the comments esp.
      * assign property sf.sessionId with the above retrieved sid cookie
      * assign property sf.serverurl with the SCL server url after login (the same url, under which the sid cookie is placed)
      * save your changes
    * to check setup, open command window and execute:
      * c> cd %fil2gvs_home%\_scl-mig-tool-project-template
      * c> %ant_home%\bin\ant retrieveUnpackaged
      * expected results
        * expected output: "[sf:retrieve] Finished request ... successfully."
        * expected mata data download: %fil2gvs_home%\_scl-mig-tool-project-template\retrieveUnpackaged\objects\Account.object
  * steps to SCL deploy ExampleGvs
    * copy %fil2gvs_home%\ExampleGvs.globalValueSet to %fil2gvs_home%\_scl-mig-tool-project-template\deployUnpackaged\globalValueSets
    * delete file %fil2gvs_home%\_scl-mig-tool-project-template\deployUnpackaged\globalValueSets\.gitkeep
    * to deploy, open command window and execute:
      * c> cd %fil2gvs_home%\_scl-mig-tool-project-template
      * c> %ant_home%\bin\ant deployUnpackaged
      * expected results
        * expected output: "[sf:deploy] Finished request ... successfully."
        * in SCL/GUI go to setup, open Global Value Sets (via search topic "pick"), then search and inspect the newly created gvs ExampleGvs 

