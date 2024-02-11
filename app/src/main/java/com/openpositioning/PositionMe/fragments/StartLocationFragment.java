package com.openpositioning.PositionMe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLngBounds;
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
import android.widget.LinearLayout;
import android.widget.Switch;


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
public class StartLocationFragment extends Fragment {

    //Button to go to next fragment and save the location
    private Button button;
    //Singleton SesnorFusion class which stores data from all sensors
    private SensorFusion sensorFusion = SensorFusion.getInstance();
    //Google maps LatLong object to pass location to the map
    private LatLng position;
    //Start position of the user to be stored
    private float[] startPosition = new float[2];
    //Zoom of google maps
    private float zoom = 19f;
    private GoogleMap mMap;
    private NuclearBuildingManager nuclearBuildingManager;
    private LinearLayout FloorButtons;

    /**
     * Public Constructor for the class.
     * Left empty as not required
     */
    public StartLocationFragment() {
        // Required empty public constructor
    }

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
        //If not location found zoom the map out
        if (startPosition[0] == 0 && startPosition[1] == 0) {
            zoom = 1f;
        } else {
            zoom = 19f;
        }
        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.startMap);

        // Asynchronous map which can be configured
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            /**
             * {@inheritDoc}
             * Controls to allow scrolling, tilting, rotating and a compass view of the
             * map are enabled. A marker is added to the map with the start position and a marker
             * drag listener is generated to detect when the marker has moved to obtain the new
             * location.
             */
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;

                if (mMap != null) {
                    // 创建 NuclearBuildingManager 实例
                    nuclearBuildingManager = new NuclearBuildingManager(mMap);
                    nuclearBuildingManager.getIndoorMapManager().hideMap();
                }

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setTiltGesturesEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mMap.getUiSettings().setScrollGesturesEnabled(true);


                // Add a marker in current GPS location and move the camera
                position = new LatLng(startPosition[0], startPosition[1]);
                mMap.addMarker(new MarkerOptions().position(position).title("Start Position")).setDraggable(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

                //Drag listener for the marker to execute when the markers location is changed
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    /**
                     * {@inheritDoc}
                     * Updates the start position of the user.
                     */
                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        startPosition[0] = (float) marker.getPosition().latitude;
                        startPosition[1] = (float) marker.getPosition().longitude;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }
                });

                LatLngBounds buildingBounds = new LatLngBounds(
                        new LatLng(55.922720, -3.174631), // 平面图对应的南西角坐标
                        new LatLng(55.923406, -3.173832)  // 平面图对应的东北角坐标
                );

                mMap.setOnMapClickListener(latLng -> {
                    if (buildingBounds.contains(latLng)) {
                        FloorButtons.setVisibility(View.VISIBLE); // 显示楼层选择按钮
                        switchFloor(1); // 默认显示 Upper Ground 层，索引为1
                    } else {
                        //FloorButtons.setVisibility(View.GONE); // 点击区域外隐藏楼层选择按钮
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                    }
                });

            }
        });

        // Floor button initialization
        //setupFloorButtons(rootView);

        return rootView;
    }


    private void setupFloorSelectionButtons(View view) {
        LinearLayout FloorButtons = view.findViewById(R.id.FloorButtons);

        // 为每个按钮设置点击监听器
        view.findViewById(R.id.floor_lowground).setOnClickListener(v -> switchFloor(0));
        view.findViewById(R.id.floor_upground).setOnClickListener(v -> switchFloor(1));
        view.findViewById(R.id.floor_1).setOnClickListener(v -> switchFloor(2));
        view.findViewById(R.id.floor_2).setOnClickListener(v -> switchFloor(3));
        view.findViewById(R.id.floor_3).setOnClickListener(v -> switchFloor(4));

        // 设置地图点击监听器，当点击地图时隐藏楼层按钮
        //mMap.setOnMapClickListener(latLng -> FloorButtons.setVisibility(View.GONE));
    }

    private void switchFloor(int floorIndex) {
        if (nuclearBuildingManager != null) {
            nuclearBuildingManager.getIndoorMapManager().switchFloor(floorIndex);
        }
    }


    /**
     * {@inheritDoc}
     * Button onClick listener enabled to detect when to go to next fragment and start PDR recording.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.startMap);
        //mapFragment.getMapAsync(this);

        // 楼层按钮初始化
        FloorButtons = view.findViewById(R.id.FloorButtons); // 确保你的布局文件中有这个ID

        setupFloorSelectionButtons(view); // 初始化楼层选择按钮


        // Add button to begin PDR recording and go to recording fragment.
        this.button = (Button) getView().findViewById(R.id.startLocationDone);
        this.button.setOnClickListener(new View.OnClickListener() {
            /**
             * {@inheritDoc}
             * When button clicked the PDR recording can start and the start position is stored for
             * the {@link CorrectionFragment} to display. The {@link RecordingFragment} is loaded.
             */
            @Override
            public void onClick(View view) {
                // Starts recording data from the sensor fusion
                sensorFusion.startRecording();
                // Set the start location obtained
                sensorFusion.setStartGNSSLatitude(startPosition);
                // Navigate to the RecordingFragment
                NavDirections action = StartLocationFragmentDirections.actionStartLocationFragmentToRecordingFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

            // Find Switch and set up a listener
        Switch mapTypeSwitch = view.findViewById(R.id.switch_map_type);
        if (mapTypeSwitch != null) {
            mapTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mMap != null) { // 使用类级别的GoogleMap引用来切换地图类型
                        if (isChecked) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        } else {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                    }
                }
            });
        }
    }
}
