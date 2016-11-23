# Udacity Blitz Interview Assessment - Distance Tracker

You're going to build a distance tracking app. The app will have three activities in portrait orientation. Please submit a zip file with your files (source code, not APK). We encourage you to take the time you need to try to finish, but you may also submit your in-progress code if you wish.

## Deliverables

Main activity with 3 buttons

* “Start Tracking” / “Stop Tracking”. “Start Tracking” button changes to display “Stop Tracking” after pressed, in order to let the user stop distance tracking.
* “Show distance”. “Show distance” opens activity 2.
* “Show location”. “Show location” opens activity 3.

Distance activity shows distance travelled. The distance travelled should be continuously updated every 10 seconds while this screen is displayed.
Location activity shows current GPS location. The location should be updated every 10 seconds while this screen is displayed.

When the “Start/Stop” button is pressed, show the user an AlertDialog that asks if they are ok with their location being tracked in the background. If they press “Ok”, then change the title of the “Start Tracking” button to “Stop Tracking”.

Distance need to be tracked even if the app is not visible on the screen and recovered if the device randomly reboots. The project should be created in Android studio using Gradle and ready for localization. The output/result will be two APK files, release builds, obfuscated by proguard:

* dTracker/km (package name: com.distance_tracker.km) - shows distance data in kilometers
* dTracker/mi (package name: com.distance_tracker.mi) - shows distance data in miles