package com.openpositioning.PositionMe.fragments;

import static com.google.common.reflect.Reflection.getPackageName;
import static com.openpositioning.PositionMe.fragments.GeofenceManager.GEOFENCE_ID_NKLIB;
import static com.openpositioning.PositionMe.fragments.GeofenceManager.GEOFENCE_ID_NUCLEAR;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.openpositioning.PositionMe.PdrProcessing;
import com.openpositioning.PositionMe.R;
import com.openpositioning.PositionMe.sensors.SensorFusion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
//import com.example.yourapp.GeofenceBroadcastReceiver;

import android.content.BroadcastReceiver;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import com.openpositioning.PositionMe.PdrProcessing;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


// ---------------------------------------------------------- ↑↑↑↑↑↑ ------------------------------------------------------------------ P 1
// ---------------------------------------------------------- Import ------------------------------------------------------------------ P 1
// ---------------------------------------------------------- ↑↑↑↑↑↑ ------------------------------------------------------------------ P 1

/**
 * A simple {@link Fragment} subclass. The startLocation fragment is displayed before the trajectory
 * recording starts. This fragment displays a map in which the user can adjust their location to
 * correct the PDR when it is complete
 *
 * @see HomeFragment the previous fragment in the nav graph.
 * @see RecordingFragment the next fragment in the nav graph.
 * @see SensorFusion the class containing sensors and recording.
 *
 * @author Virginia Cangelosi
 */

/*
There are many log or toast statements in the code,
which are temporarily kept in the code for maintenance and debugging in future versions.
 */

public class StartLocationFragment extends Fragment {

    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 2
    // ---------------------------------------------------------- Global variables ------------------------------------------------------------------ P 2
    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 2

    //Button to go to next fragment and save the location
    private Button button;
    //Singleton SesnorFusion class which stores data from all sensors
    private SensorFusion sensorFusion = SensorFusion.getInstance();
    //Google maps LatLong object to pass location to the map
    private LatLng position;
    //Start position of the user to be stored
    private float[] startPosition = new float[2];
    private float[] PDRPosition = new float[2]; //----------------------------------------//
    //Zoom of google maps
    private float zoom = 18f;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private NuclearBuildingManager nuclearBuildingManager;
    private NoreenandKennethMurrayLibraryMap noreenandKennethMurrayLibry;
    private LinearLayout FloorButtons;
    private LinearLayout FloorButtonsNK;
    private Marker currentPositionMarker;
    private BroadcastReceiver geofenceBroadcastReceiver;
    private int FloorNU = 1;
    private int FloorNK = 0;
    private boolean isRecording = false; // Store recoring button state
    private ArrayList<LatLng> pathPoints; // track point
    private MarkerOptions markerOptions;

    // Area used to determine whether the indoor map is displayed when the user clicks on the map ↓↓↓
    private LatLngBounds NuclearBuildingBounds = new LatLngBounds(
            new LatLng(55.92279, -3.174643), // SW corner
            new LatLng(55.92335, -3.173829)  // NE corner
    );
    private LatLngBounds NoreenKennethBounds = new LatLngBounds(
            new LatLng(55.92281, -3.175171), // SW corner
            new LatLng(55.923065, -3.174747) // NE corner
    );
    // Area used to determine whether the indoor map is displayed when the user clicks on the map ↑↑↑


    // A larger area used to determine whether the indoor map should displayed when the user walks into the building.  ↓↓↓
    private LatLngBounds NuclearBigger = new LatLngBounds(
            new LatLng(55.922679, -3.174672), // SW corner
            new LatLng(55.923316, -3.173781)  // NE corner
    );
    private LatLngBounds NoreenKBigger = new LatLngBounds(
            new LatLng(55.922679, -3.175295), // SW corner
            new LatLng(55.923043, -3.174704) // NE corner
    );
    // A larger area used to determine whether the indoor map should displayed when the user walks into the building. ↑↑↑

    private Switch geoOrNotSwitch;
    private Polyline currentPolyline; // The currently drawn trajectory
    private LatLng GPSPosition;
    //private LatLng PDRPosition;
    private LatLng STARTPosition; // Store the PDR starting point
    private Animation blinkingAnimation;
    private ImageView redDot; // Red dot ImageView
    private TextView Accuracy; // display accuracy
    private float  Accuracy_number = 0; // Storage accuracy, initialized to 0
    private Circle accuracyCircle; // Show accuracy circle



    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 2
    // ---------------------------------------------------------- Global variables ------------------------------------------------------------------ P 2
    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 2

    /**
     * Public Constructor for the class.
     * Left empty as not required
     */
    public StartLocationFragment() {
        // Required empty public constructor
    }

    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 3
    // ---------------------------------------------------------- onCreateView ------------------------------------------------------------------ P 3
    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 3

    /**
     * {@inheritDoc}
     * The map is loaded and configured so that it displays a draggable marker for the start location
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        View rootView = inflater.inflate(R.layout.fragment_startlocation, container, false);

        //Obtain the start position from the GPS data from the SensorFusion class
        startPosition = sensorFusion.getGNSSLatitude(false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.startMap);

        // Set SensorFusion callback ---------------------------------------------------------------------------------- SensorFusion ↓↓↓
        sensorFusion.setSensorUpdateCallback(new SensorFusion.SensorUpdateCallback() {
            @Override
            public void onLocationChanged(LatLng newPosition) {
                // Update location
                GPSPosition = newPosition;
                //Log.d("SensorFusionCallback", "onLocationChanged: " + newPosition);
                updatePositionMarker(newPosition);

                if (accuracyCircle != null) {
                    accuracyCircle.remove(); // Remove old circles
                }

                // draw new circle
                CircleOptions circleOptions = new CircleOptions()
                        .center(newPosition) // Set the circle center to the new position
                        .radius(Accuracy_number)
                        .fillColor(0x30FFA500)
                        .strokeColor(0xFFA500) // border
                        .strokeWidth(4); // border width
                accuracyCircle = mMap.addCircle(circleOptions);

                //Toast.makeText(getContext(), "Location Changed", Toast.LENGTH_SHORT).show();

                /*
                Due to the restrictions on the use of geofences by Google API,
                when the geofence cannot be registered,
                the longitude and latitude can be used to actively determine whether the user has entered the building.
                 */

                // Determine whether to perform position judgment based on Switch status
                if (geoOrNotSwitch != null && geoOrNotSwitch.isChecked()) {
                    // Determine whether the user's location is within the boundaries of the nuclear energy building
                    if (NuclearBigger.contains(newPosition)) {
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);
                        Log.d("GeofenceReceiver", "Nuclear");
                        FloorButtons.setVisibility(View.VISIBLE);
                        switchFloorNU(FloorNU);
                    }
                    // Determine whether the user's location is within the library boundary
                    else if (NoreenKBigger.contains(newPosition)) {
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                        Log.d("GeofenceReceiver", "NoreenK");
                        FloorButtonsNK.setVisibility(View.VISIBLE);
                        switchFloorNK(FloorNK);
                    } else {
                        //hideAllFloors();
                        Log.d("GeofenceReceiver", "OUT");
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                    }
                }
            }

            // Update new angle value when angle changes
            @Override
            public void onOrientationChanged(float newOrientation) {
                // update Orientation
                //Log.d("SensorFusionCallback", "Trying to update orientation to: " + newOrientation);
                if (currentPositionMarker != null) {
                    currentPositionMarker.setRotation(newOrientation);
                    //Log.d("SensorFusionCallback", "Orientation updated successfully.");
                } else {
                    //Log.d("SensorFusionCallback", "currentPositionMarker is null.");
                }
            }

            // Update the pdr position when the PDR data changes
            @Override // --------------------------------------------------------------------------------------------- NEW PDR >>>
            public void onLocationPDRChanged(float[] newPosition) {

                if (newPosition == null) {
                    Log.d("SensorFusionCallback", "Received PDR position update is null");
                    return;
                }

                // Avoid passing in null
                if (newPosition != null) {
                    // Update pdr location
                    PDRPosition = newPosition;
                    updatePosition(TruePosition(PDRPosition));
                }
                //Toast.makeText(getContext(), "Walking detected", Toast.LENGTH_SHORT).show();
                Log.d("SensorFusionCallback", "PDR location update: " + Arrays.toString(newPosition));
            }

            // Get new Accuracy when Accuracy changes
            @Override
            public void onAccuracyChanged(float accuracy) {
                Accuracy_number = accuracy;
                Log.d("LocationAccuracy", "Current location accuracy: " + accuracy + " meters.");
                if(Accuracy != null) {
                    Accuracy.setText("  Accuracy: " + accuracy + " m  ");
                }
            }

        });
        // Set SensorFusion callback ---------------------------------------------------------------------------------- SensorFusion ↑↑↑

        // Initialize the switch and set up the listener
        // Use to determine whether to enable longitude and latitude judgment
        geoOrNotSwitch = rootView.findViewById(R.id.geoOrNotSwitch);
        geoOrNotSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.setText(isChecked ? "  LLJ" : "  GF");
        });

        // Asynchronous map which can be configured
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            /**
             * {@inheritDoc}
             * Controls to allow scrolling, tilting, rotating and a compass view of the
             * map are enabled. A marker is added to the map with the start position
             */
            @Override
            public void onMapReady(GoogleMap googleMap) {

                GeofenceManager geofenceManager = new GeofenceManager(getActivity());
                geofenceManager.registerAllGeofences();

                // Register BroadcastReceiver
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(geofenceBroadcastReceiver,
                        new IntentFilter("ACTION_GEOFENCE_EVENT"));

                mMap = googleMap;
                pathPoints = new ArrayList<>();
                markerOptions = new MarkerOptions();

                if (mMap != null) {
                    // Create NuclearBuildingManager instance
                    nuclearBuildingManager = new NuclearBuildingManager(mMap);
                    nuclearBuildingManager.getIndoorMapManager().hideMap();
                    // Create noreenandKennethMurrayLibry instance
                    noreenandKennethMurrayLibry = new NoreenandKennethMurrayLibraryMap(mMap);
                    noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                }

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setTiltGesturesEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mMap.getUiSettings().setScrollGesturesEnabled(true);

                position = new LatLng(startPosition[0], startPosition[1]);
                // Use new image resources and set marker
                currentPositionMarker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.trana))
                        .icon(resizeMapIcons("trana",90,90))
                        .anchor(0.5f, 0.5f)
                        .flat(true));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

                // Used to determine whether the user's click is within the building area with indoor map
                mMap.setOnMapClickListener(latLng -> {
                    if (NuclearBuildingBounds.contains(latLng)) {
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);
                        FloorButtons.setVisibility(View.VISIBLE);
                        switchFloorNU(1);
                        Toast.makeText(getActivity(), "Nuclear building map", Toast.LENGTH_SHORT).show();
                    }
                    else if (NoreenKennethBounds.contains(latLng)) {
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                        FloorButtonsNK.setVisibility(View.VISIBLE);
                        switchFloorNK(0);
                        Toast.makeText(getActivity(), "NKM Library map", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);
                        //Toast.makeText(getActivity(), "Leaving Building", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return rootView;
    }

    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 3
    // ---------------------------------------------------------- onCreateView ------------------------------------------------------------------ P 3
    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 3

    /**
     * {@inheritDoc}
     * Button onClick listener enabled to detect when to go to next fragment and start PDR recording.
     */

    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 4
    // ---------------------------------------------------------- onViewCreated ------------------------------------------------------------------ P 4
    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 4
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.startMap);
        //mapFragment.getMapAsync(this);

        // Floor button initialization
        FloorButtons = view.findViewById(R.id.FloorButtons);
        FloorButtonsNK = view.findViewById(R.id.FloorButtonsNK);

        setupFloorSelectionButtons(view);
        setupFloorSelectionButtonsNK(view);

        // --------------------------------Red Dot--------------------------------↓↓↓
        // Initialize red dot ImageView
        redDot = view.findViewById(R.id.RedDot);

        // Set flash animation
        blinkingAnimation = new AlphaAnimation(1, 0);
        blinkingAnimation.setDuration(800);
        blinkingAnimation.setInterpolator(new LinearInterpolator());
        blinkingAnimation.setRepeatCount(Animation.INFINITE);
        blinkingAnimation.setRepeatMode(Animation.REVERSE);
        // --------------------------------Red Dot--------------------------------↑↑↑

        // Position button
        Button locateButton = view.findViewById(R.id.locateButton);
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sensorFusion != null) {
                    float[] currentPosition = sensorFusion.getGNSSLatitude(false);
                    LatLng userLocation = new LatLng(currentPosition[0], currentPosition[1]);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoom));
                }
            }
        });

        // Initialize TextView to display positioning accuracy
        Accuracy = view.findViewById(R.id.Accuracy);

        // Initialize record button
        Button Rec = view.findViewById(R.id.Rec);
        Rec.setText(">");
        redDot.clearAnimation(); // Stop flashing animation
        redDot.setVisibility(View.GONE);

        Rec.setOnClickListener(new View.OnClickListener() {
            // Stop recording if it is already recording, and start recording if there is no recording
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    Toast.makeText(getActivity(), "Stop Rec", Toast.LENGTH_SHORT).show();
                    // Stop recording
                    isRecording = false;
                    Rec.setText(">");
                    Rec.setBackgroundResource(R.drawable.round_button);
                    sensorFusion.stopRecording();
                    redDot.clearAnimation();
                    redDot.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity(), "Start Rec", Toast.LENGTH_SHORT).show();
                    // Start recording
                    clearPreviousPath();
                    isRecording = true;
                    Rec.setText("||");
                    Rec.setBackgroundResource(R.drawable.round_button_red);
                    redDot.setVisibility(View.VISIBLE);
                    redDot.startAnimation(blinkingAnimation);// Start flashing red dot
                    if (GPSPosition != null) {
                        STARTPosition = GPSPosition;
                    }
                    sensorFusion.startRecording();
                }
            }
        });


        // ----------------------------------------------- Register a geofence event receiver --------------------------- ↓↓↓

        geofenceBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String geofenceId = intent.getStringExtra("GEOFENCE_ID");
                int transitionType = intent.getIntExtra("GEOFENCE_TRANSITION_TYPE", -1);

                if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    if (GEOFENCE_ID_NUCLEAR.equals(geofenceId)) {
                        // --- User enters the nuclear energy building area ---
                        Log.d("GeofenceReceiver", "IN Nuclear building");
                        Toast.makeText(context, "Entering the nuclear building", Toast.LENGTH_SHORT).show();
                        FloorButtons.setVisibility(View.VISIBLE);
                        switchFloorNU(1);

                    } else if (GEOFENCE_ID_NKLIB.equals(geofenceId)) {
                        // User enters library area
                        Log.d("GeofenceReceiver", "IN Library");
                        Toast.makeText(context, "Entering the Library", Toast.LENGTH_SHORT).show();
                        FloorButtonsNK.setVisibility(View.VISIBLE);
                        switchFloorNK(0);
                    }
                } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    if (GEOFENCE_ID_NUCLEAR.equals(geofenceId)) {
                        // --- The user leaves the nuclear energy building area ---
                        Log.d("GeofenceReceiver", "OUT Nuclear building");
                        Toast.makeText(context, "Leaving the nuclear building", Toast.LENGTH_SHORT).show();
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);

                    } else if (GEOFENCE_ID_NKLIB.equals(geofenceId)) {
                        // --- User leaves library area ---
                        Log.d("GeofenceReceiver", "OUT Library");
                        Toast.makeText(context, "Leaving the Library", Toast.LENGTH_SHORT).show();
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);

                    }
                }
            }
        };

        // Find Switch and set up a listener
        // Allow users to switch map types through switch ↓↓↓
        Switch mapTypeSwitch = view.findViewById(R.id.switch_map_type);
        if (mapTypeSwitch != null) {
            mapTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setText(isChecked ? "  HYBRID" : "  NORMAL");
                    if (mMap != null) {
                        if (isChecked) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            Toast.makeText(getActivity(), "HYBRID Map", Toast.LENGTH_SHORT).show();
                            Log.d("Switch", "Sat");
                        } else {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            Toast.makeText(getActivity(), "NORMAL Map", Toast.LENGTH_SHORT).show();
                            //float number = 20;
                            Log.d("Switch", "Nor");
                        }
                    }
                }
            });
        }
    }

    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 4
    // ---------------------------------------------------------- onViewCreated ------------------------------------------------------------------ P 4
    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 4


    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 5
    // ---------------------------------------------------------- Member function ------------------------------------------------------------------ P 5
    // ---------------------------------------------------------- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ ------------------------------------------------------------------ P 5

    private BitmapDescriptor resizeMapIcons(String iconName, int width, int height){  //  ----- Change icon ↓↓↓
        // Loading icon resources
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        if (imageBitmap != null) {
            // If loaded successfully, zoom it to fit
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
            return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
        } else {
            // resource load fails
            Log.e("StartLocationFragment", "Unable to load icon resource: " + iconName);
            return null;
        }
    }

    // -------------------------------------------------------------------------------------- Update Marker ↓↓↓
    private void updatePosition(LatLng newPosition) {

        if (newPosition == null) {
            Log.e("StartLocationFragment", "New position is null, not updating the marker.");
            return;
        }
        //currentPositionMarker.setPosition(newPosition);
        //Toast.makeText(getContext(), "Position Update", Toast.LENGTH_SHORT).show();
        Log.e("StartLocationFragment", "updatePosition" + newPosition);

        if(isRecording) {
            pathPoints.add(newPosition);
            refreshMap();
        }
    }

    private void updatePositionMarker(LatLng newPosition) {
        //Toast.makeText(getContext(), "Marker Update", Toast.LENGTH_SHORT).show();
        currentPositionMarker.setPosition(newPosition);
        Log.e("StartLocationFragment", "updatePositionM" + newPosition);
    }

    // ------------------------------------------------------------------------- Refresh the map and redraw the trajectory ↓↓↓
    private void refreshMap() {
        int Orange = Color.argb(255, 255, 128, 0);

        if (currentPolyline != null) {
            currentPolyline.remove(); // Remove previous tracks to avoid overlap
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(pathPoints)
                .width(7)
                .color(Orange);
        currentPolyline = mMap.addPolyline(polylineOptions); // Draw a trajectory using the latest trajectory point list
    }

    // ------------------------------------------------------------------------- Clear the trajectory ↓↓↓
    private void clearPreviousPath() {
        pathPoints.clear();

        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register sensor listener to update location
        sensorFusion.resumeListening();
        Log.e("SensorFusionCallback", "Sensor Resume");
    }
    @Override
    public void onPause() {
        super.onPause();
        // Unregister the sensor listener to stop updating location
        sensorFusion.stopListening();
        Log.e("SensorFusionCallback", "Sensor Stop");
    }

    private void setupFloorSelectionButtons(View view) {
        // Set click listener for each button
        view.findViewById(R.id.floor_lowground).setOnClickListener(v -> switchFloorNU(0));
        view.findViewById(R.id.floor_upground).setOnClickListener(v -> switchFloorNU(1));
        view.findViewById(R.id.floor_1).setOnClickListener(v -> switchFloorNU(2));
        view.findViewById(R.id.floor_2).setOnClickListener(v -> switchFloorNU(3));
        view.findViewById(R.id.floor_3).setOnClickListener(v -> switchFloorNU(4));
    }
    private void setupFloorSelectionButtonsNK(View view) {
        // Set click listener for each button
        view.findViewById(R.id.floor_ground).setOnClickListener(v -> switchFloorNK(0));
        view.findViewById(R.id.floor_one).setOnClickListener(v -> switchFloorNK(1));
        view.findViewById(R.id.floor_two).setOnClickListener(v -> switchFloorNK(2));
        view.findViewById(R.id.floor_three).setOnClickListener(v -> switchFloorNK(3));
    }

    // ---------------------------------------------------- Implement floor switching function ↓↓↓
    private void switchFloorNU(int floorIndex) {
        FloorNU = floorIndex;
        if (nuclearBuildingManager != null) {
            nuclearBuildingManager.getIndoorMapManager().switchFloor(floorIndex);
        }
    }
    private void switchFloorNK(int floorIndex) {
        FloorNK = floorIndex;
        if (noreenandKennethMurrayLibry != null) {
            noreenandKennethMurrayLibry.getIndoorMapManager().switchFloor(floorIndex);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(geofenceBroadcastReceiver);
    }


    private LatLng TruePosition(float[] pdrDisplacement) {
        // Check startPosition is not null and pdrDisplacement is of length 2
        // for debugging
        if (STARTPosition == null) {
            Log.e("StartLocationFragment", "Starting position is empty");
            return null;
        }
        if (pdrDisplacement == null) {
            Log.e("StartLocationFragment", "PDR location is empty");
            return null;
        }
        if (pdrDisplacement.length != 2) {
            Log.e("StartLocationFragment", "Incorrect format");
            return null;
        }

        // Convert the incoming distance in m to latitude and longitude
        double latitudeOffset = pdrDisplacement[0] / 111319.9;
        double longitudeOffset = pdrDisplacement[1] / (111319.9 * Math.cos(Math.toRadians(STARTPosition.latitude)));

        // Add the starting point to get the true position
        double newLatitude = STARTPosition.latitude + latitudeOffset;
        double newLongitude = STARTPosition.longitude + longitudeOffset;

        return new LatLng(newLatitude, newLongitude);
    }

    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 5
    // ---------------------------------------------------------- Member function ------------------------------------------------------------------ P 5
    // ---------------------------------------------------------- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ ------------------------------------------------------------------ P 5

}
