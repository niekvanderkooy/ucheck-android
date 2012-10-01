uCheck for Android
==============
This application aims to provide an Android client for [uCheck](http://ucheck.nl/), a service which aims to replace the overly complex uSis service provided by Leiden University.

License
--------------
Copyright (c) 2012 by Niek van der Kooy

This work is licensed under the Creative Commons 
Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
To view a copy of this license, visit 
http://creativecommons.org/licenses/by-nc-sa/3.0/ 

In addition to the terms stated in this license, the author asks you not to publish the compiled application to Google Play or other Android application distribution services,
but instead to refer people to this project's [GitHub page](https://github.com/niekvanderkooy/ucheck-android/) or the application page on [Google Play](https://play.google.com/store/apps/details?id=info.vanderkooy.ucheck)

Contribution to this project however are encouraged, and new features submitted by pull-request on GitHub are likely to be included in subsequent releases.

Accreditation
--------------
This application was developed by Niek van der Kooy, using the uSis API developed by [Hans Pinckaers](https://github.com/HansPinckaers/), which is publically available through the [uCheck PHP backend](https://github.com/HansPinckaers/ucheck-php)

All contributors are listed below:
* [Hans Pinckaers](https://github.com/HansPinckaers/)

Changelog
--------------
v1.1.1 (Current version):
* Fix for enrollments not being shown
* Better Google Analytics tracking of errors and exceptions

v1.1.0:
* Added support for changing orientations
* Added Google Analytics support
* Fixed alternating row colours disappearing

v1.0.2:
* Fixed occasional crash when logging in.

v1.0.1:
* When not logged in (and at the login screen), pressing the back button now shuts down the app.

v1.0.0:
Initial release, with support for:
* Viewing obtained grades, and their value in EC
* Viewing currently enrolled classes
* Viewing a progress chart