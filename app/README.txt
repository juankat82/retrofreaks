In order to compile this app you need some changes:
There are 2 modules in this app, `app` and `installtimeassetpack` (as you can see in settings.gradle. I start by app:

-The PDF files mentioned in the method getConsoleHistoryFile() starting line 456 within the
com.juan.retrofreaks.activities/ConsoleDestination.kt class file in the `app` package must exist.
Currently they dont. These files are all located in the pack `installtimeassetpack/assets` folder.

-The PDF files mentioned in the method getConsoleAccesoriesFile() starting line 564 within the
com.juan.retrofreaks.activities/ConsoleDestination.kt class file in the `app` package must exist.
Currently they dont. These files are all located in the pack `installtimeassetpack/assets` folder.

-The PDF files mentioned in the method getConsoleCablesFile() starting line 649 within the
com.juan.retrofreaks.activities/ConsoleDestination.kt class file in the `app` package must exist.
Currently they dont. These files are all located in the pack `installtimeassetpack/assets` folder.

-The PDF files mentioned in the method getConsoleModsFile() starting line 732 within the
com.juan.retrofreaks.activities/ConsoleDestination.kt class file in the `app` package must exist.
Currently they dont. These files are all located in the pack `installtimeassetpack/assets` folder.

-The HTML files mentioned in the same file, within the method `getConsoleGameUrl()` (line 539)
com.juan.retrofreaks.activities/ConsoleDestination.kt class file in the `app` package must exist.
They are available.

-The PDF files mentioned in the method getFileName() starting line 155 within the
com.juan.retrofreaks.activities/MiscDestination.kt class file in the `app` package must exist.
Currently they dont. These files are all located in the pack `installtimeassetpack/assets` folder.

-Change applicationId "com..." to your apps main package in build.gradle (:app), also in namespace (reapeat your main package's name).
Eg: com.john.myapp

-Change in build.gradle (:installtimeassetpack) the lines like "com.myname.installtimeassetpack".
Recommendation is change "myname" with you own. Dont forget to refactor the whole app's folders's names beforehand, to
whatever you will call yours.

-Change the route to your Android SDK's base folder in local.properties.

-Change the file output-metadata.json, line "applicationId", to whatever the namespace of app is ("Eg: com.john.retroapp").
