## PositionMe
Indoor poistioning data collection application created for the University of Edinburgh's Embedded Wireless course. 

### Requirements

- Android Studio 4.2 or later
- Android SDK 30 or later

****
# Changelog

This file records every software update and development process.

====

## [0.1.1] - 2024-02-11
Under Development

### Added
:heavy_plus_sign: Add normal map display.<br>
:heavy_plus_sign: A new switch has been added so that users can choose between real scenes and normal maps at any time.<br>
:heavy_plus_sign: Add floor overlays to the map to display indoor maps.<br>
:heavy_plus_sign: Add new buttons that can be intelligently hidden or shown as needed. Click to switch interior floor plans of different floors.<br>
:heavy_plus_sign: Added the function of automatically displaying the indoor floor plan by clicking on the corresponding area, and automatically hiding the floor plan by clicking on other areas.

### Changed
 :recycle: The map UI has been partially optimized.

### Fixed
:hammer: Using Gradle version 7.2. Fixed the bug that the plugin version is not supported.


## [0.1.2] - 2024-02-12
Under Development

### Added
:heavy_plus_sign: Indoor map of second building.<br>
:heavy_plus_sign: UI interaction logic for indoor maps of multiple buildings.<br>
:heavy_plus_sign: New real-time location display function.<br>
:heavy_plus_sign: New arrow icon to show user location.<br>
:heavy_plus_sign: The function of displaying the user's orientation on the map in real time.

### Changed
 :recycle: UI designs. <br>
 :recycle: Optimize the UI interaction logic of the map interface.

### Fixed
:hammer: Fixed a bug where two maps were displayed at the same time and could not disappear.<br>
:hammer: Fixed the BUG of user orientation display deviation.


## [0.1.3] - 2024-02-13
Under Development

### Added
:heavy_plus_sign: The function of automatically displaying the indoor map when the user enters a specific area has been added.<br>
:heavy_plus_sign: The function of finding your own location with one click has been added.<br>

### Changed
 :recycle: Changed default map zoom level for a more comfortable viewing angle. <br>

### Fixed
:hammer: Fixed a bug where geofences could not be effectively triggered.<br>
:hammer: Geofence triggering logic bug fixed. <br>
:hammer: Fixed an error where the orientation did not match the user's actual situation in some cases.



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
