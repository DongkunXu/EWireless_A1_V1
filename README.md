## PositionMe :earth_americas: :earth_africa: :earth_asia:
Indoor poistioning data collection application created for the :school_satchel: University of Edinburgh's Embedded Wireless course. <br>
Original contributors: Virginia Cangelosi (virginia-cangelosi), Michal Dvorak (dvoramicha) and Mate Stodulka (stodimp), New contributors: Francisco Zampella (fzampella-huawei)<br>
:recycle: Modification and maintenance of the current version: SteveXu

Require: :heavy_check_mark: Android Studio 4.2 or later :heavy_check_mark: Android SDK 30 or later

****
# Changelog

This file records every software update and development process.

====

## [V0.1.1 - "Try_Make_It_Work"] - 2024-02-11
Under Development

### Added
:sparkles: Add normal map display.<br>
:sparkles: A new switch has been added so that users can choose between real scenes and normal maps at any time.<br>
:sparkles: Add floor overlays to the map to display indoor maps.<br>
:sparkles: Add new buttons that can be intelligently hidden or shown as needed. Click to switch interior floor plans of different floors.<br>
:sparkles: Added the function of automatically displaying the indoor floor plan by clicking on the corresponding area, and automatically hiding the floor plan by clicking on other areas. <br>

### Changed
 :recycle: The map UI has been partially optimized. <br>

### Fixed
:hammer: Using Gradle version 7.2. Fixed the bug that the plugin version is not supported. <br>


## [V0.1.2 - "LOCATE_POINT"] - 2024-02-12
Under Development

### Added
:sparkles: Indoor map of second building.<br>
:sparkles: UI interaction logic for indoor maps of multiple buildings.<br>
:sparkles: New real-time location display function.<br>
:sparkles: New arrow icon to show user location.<br>
:sparkles: The function of displaying the user's orientation on the map in real time. <br>

### Changed
 :recycle: UI designs. <br>
 :recycle: Optimize the UI interaction logic of the map interface. <br>

### Fixed
:hammer: Fixed a bug where two maps were displayed at the same time and could not disappear.<br>
:hammer: Fixed the BUG of user orientation display deviation. <br>


## [V0.1.3 - "GeoFence"] - 2024-02-13
Under Development

### Added
:sparkles: The function of automatically displaying the indoor map when the user enters a specific area has been added.<br>
:sparkles: The function of finding your own location with one click has been added.<br>

### Changed
 :recycle: Changed default map zoom level for a more comfortable viewing angle. <br>

### Fixed
:hammer: Fixed a bug where geofences could not be effectively triggered.<br>
:hammer: Geofence triggering logic bug fixed. <br>
:hammer: Fixed an error where the orientation did not match the user's actual situation in some cases. <br>


## [V0.1.4 - "GeoFence_LatLon"] - 2024-02-15
Under Development

### Added
:sparkles: The function of using longitude and latitude to determine whether the user has entered the building has been added.<br>
:sparkles: Logic was added to let the user choose to use geofencing or latitude and longitude judgment.<br>

### Changed
 :recycle: The UI interface is changed to adapt to the added position judgment logic. <br>
:recycle: Log and Toast are added at key locations for debugging purposes. <br>

### Fixed
:hammer: The issue where location determination could sometimes not be completed using only geofences has been partially fixed (:pushpin:).<br>
:hammer: The logic of entering the building has been improved to avoid display conflicts. <br>

:pushpin: Occasional registration failure errors occur in geofences. Although there have been successful cases in virtual tests, they still cannot work stably after extensive debugging. The error is believed to be related to Google API. The current solution is to add additional judgment logic to determine whether the user has entered the building based on longitude and latitude. Users can control this logic to be enabled or disabled using a switch on the map. <br>


## [V0.1.5 - "PDR"] - 2024-02-16
Under Development

### Added
:sparkles: PDR data is used to replace the original position path data from the satellite.<br>
:sparkles: The ability to draw movement paths in real time using PDR data has been added.<br>

### Changed
:recycle: The startRecording page is hidden. <br>
:recycle: SensorFusion code has been changed for work with PDR (Callback).<br>

### Fixed
:hammer: The problem of PDR not having data is solved.<br>

### Known issues
:x: The PDR data is wrong and the path display is always a straight line. :confounded: <br>


## [V0.1.6 - "Repair PDR"] - 2024-02-16
Under Development

### Added
:sparkles: In PDR and Sensor Fusion, more Log outputs are added to monitor system health.<br>
:sparkles: In Sensor Fusion, the angle data is passed so that the pdr calculation can get reasonable data.:heavy_check_mark:<br>

### Changed
:recycle: Record button logic optimized to match PDR data.<br>
:recycle: The data type of the callback function is corrected to accommodate conversion from meters to latitude and longitude.<br>

### Fixed
:hammer: Fixed a bug where PDR had no angle information passed in.<br>
:hammer: The bug that the trajectory drawn by PDR is a straight line has been fixed.<br>


## [V0.2.0 - "PDR"] - 2024-02-16
First deliverable :white_check_mark: 

### Added
:sparkles: Updated new UI interface.<br>

### Changed
:recycle: Update image resources. <br>
:recycle: Update UI. <br>
:recycle: Optimize UI logic and visual style. <br>

### Fixed
:hammer: -- <br>

### Known issues
:x: PDR accuracy is not high. Long term use has visible cumulative errors. There will be a difference from the real position after multiple turns. :confounded: Introducing other external parameters or algorithms for reference correction can be considered later. <br>

****

### Installation

1. Clone the repository. :heavy_check_mark:
2. Open the project in Android Studio. :heavy_check_mark:
3. Add your own API key for Google Maps in AndroidManifest.xml :heavy_check_mark:
4. Set the website where you want to send your data. The application was built for use with openpositioning.org. :flushed:
5. Build and run the project on your device. :heavy_check_mark:

### Usage

1. Install the application on a compatible device using Android Studio. :heavy_check_mark:
2. Launch the application on your device. :heavy_check_mark:
3. Allow sensor, location and internet permissions when asked. :heavy_check_mark:
4. Follow the instructions on the screen to start collecting sensor data. :heavy_check_mark:

### Creators

### Original contributors ([CloudWalk](https://github.com/openpositioning/DataCollectionTeam6))
- Virginia Cangelosi (virginia-cangelosi)
- Michal Dvorak (dvoramicha)
- Mate Stodulka (stodimp)

#### New contributors
- Francisco Zampella (fzampella-huawei)
