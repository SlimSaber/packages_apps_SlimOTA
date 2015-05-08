SlimOTA
-------
A very simple OTA checker with Android Settings look and feel.

How it works
------------
It parses the OTA xml file that you put in your file hosting and compares the version number with the local one.
If the version is newer, it notifies the user for a new ROM update.

How to use
----------
* Prepare the OTA xml file. Use this [template](https://raw.githubusercontent.com/SlimSaber/packages_apps_SlimOTA/lp5.0/examples/ota_lollipop.xml).
* Upload it to your file hosting and create a hot link of it
* Copy the [ota_conf template](https://raw.githubusercontent.com/SlimSaber/packages_apps_SlimOTA/lp5.0/examples/ota_conf) to app/src/main/assets folder
  * If you are buiding this app as part of the ROM, you need to copy ota_conf in the android root folder.
  * The Android.mk will pick it up and copy it to app/src/main/assets folder automatically.
* Replace the "ota_url" with your OTA xml hot link
* Define how SlimOTA should know about the "version". The version must be parseable to a date.
  * Usually, the version is a part of a build name. For example, the 20150426 in the SlimSaber-bacon-5.0.2-20150426.
* Adjust the OTA configuration according to your build name on how should SlimOTA parse the version
  * Find a key in build.prop that represents the SlimSaber-bacon-5.0.2-20150426 and set it in the "version_name"
  * Set the delimiter in "version_delimiter" to "-"
  * Set the date format in "version_format" to "yyyyMMdd"
  * Set the position in "version_position" to "3" (zero based)
* Find a key in build.prop that represents your device name and set it in the "device_name"
  * SlimOTA will search this device name in the OTA xml file

How to build
------------
* As part of the ROM
  * [Add this repo in your manifest](https://github.com/SlimSaber/platform_manifest/commit/0c0debe17d03cbe911f8693c4da2789b9d94b08e)
  * [Include this app in the build process](https://github.com/SlimSaber/vendor_slim/commit/c8c7bae682d837e21af3062dded327cda2d781e0)
* As a standalone app
  * With Android.mk: make SlimOTA
  * With Android Studio: Import this repo to your Android Studio and build it from there
  
Credits
-------
* [Slim team](http://slimroms.net/)
  * For the original idea of the SlimCenter and app icon
* [CommonsWare Android Components](https://github.com/commonsguy/cwac-wakeful)
  * For the wakeful intent service that is used in this app

Screenshots
-----------
<img alt="Screenshot"
   width="270" height="480" 
   src="https://raw.githubusercontent.com/SlimSaber/packages_apps_SlimOTA/lp5.0/screenshots/Screenshot_20150505_1317.png" />
