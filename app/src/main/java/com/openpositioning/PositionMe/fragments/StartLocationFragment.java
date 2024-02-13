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
    private NoreenandKennethMurrayLibraryMap noreenandKennethMurrayLibry;
    private LinearLayout FloorButtons;
    private LinearLayout FloorButtonsNK;

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

                    noreenandKennethMurrayLibry = new NoreenandKennethMurrayLibraryMap(mMap);
                    noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();

                }

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setTiltGesturesEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mMap.getUiSettings().setScrollGesturesEnabled(true);


                // Add a marker in current GPS location and move the camera
                position = new LatLng(startPosition[0], startPosition[1]);
                mMap.addMarker(new MarkerOptions().position(position).title("Start Position")).setDraggable(false);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));



                LatLngBounds NuclearBuildingBounds = new LatLngBounds(
                        new LatLng(55.92279, -3.174643), // 平面图对应的南西角坐标
                        new LatLng(55.92335, -3.173829)  // 平面图对应的东北角坐标
                );

                LatLngBounds NoreenKennethBounds = new LatLngBounds(
                        new LatLng(55.92281, -3.175171), // 平面图对应的南西角坐标
                        new LatLng(55.923065, -3.174747)  // 平面图对应的东北角坐标
                );

                mMap.setOnMapClickListener(latLng -> {
                    if (NuclearBuildingBounds.contains(latLng)) {
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);
                        FloorButtons.setVisibility(View.VISIBLE); // 显示楼层选择按钮
                        switchFloorNU(1); // 默认显示 Upper Ground 层，索引为1
                    }
                    else if (NoreenKennethBounds.contains(latLng)) {
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                        FloorButtonsNK.setVisibility(View.VISIBLE); // 显示楼层选择按钮
                        switchFloorNK(0); // 默认显示 Ground 层，索引为0
                    }
                    else {
                        //FloorButtons.setVisibility(View.GONE); // 点击区域外隐藏楼层选择按钮
                        nuclearBuildingManager.getIndoorMapManager().hideMap();
                        FloorButtons.setVisibility(View.GONE);
                        noreenandKennethMurrayLibry.getIndoorMapManager().hideMap();
                        FloorButtonsNK.setVisibility(View.GONE);
                    }
                });
            }
        });
        return rootView;
    }


    private void setupFloorSelectionButtons(View view) {
        //LinearLayout FloorButtons = view.findViewById(R.id.FloorButtons);

        // 为每个按钮设置点击监听器
        view.findViewById(R.id.floor_lowground).setOnClickListener(v -> switchFloorNU(0));
        view.findViewById(R.id.floor_upground).setOnClickListener(v -> switchFloorNU(1));
        view.findViewById(R.id.floor_1).setOnClickListener(v -> switchFloorNU(2));
        view.findViewById(R.id.floor_2).setOnClickListener(v -> switchFloorNU(3));
        view.findViewById(R.id.floor_3).setOnClickListener(v -> switchFloorNU(4));
    }

    private void setupFloorSelectionButtonsNK(View view) {
        //LinearLayout FloorButtonsNK = view.findViewById(R.id.FloorButtonsNK);

        view.findViewById(R.id.floor_ground).setOnClickListener(v -> switchFloorNK(0));
        view.findViewById(R.id.floor_one).setOnClickListener(v -> switchFloorNK(1));
        view.findViewById(R.id.floor_two).setOnClickListener(v -> switchFloorNK(2));
        view.findViewById(R.id.floor_three).setOnClickListener(v -> switchFloorNK(3));
    }

    private void switchFloorNU(int floorIndex) {
        if (nuclearBuildingManager != null) {
            nuclearBuildingManager.getIndoorMapManager().switchFloor(floorIndex);
        }
    }

    private void switchFloorNK(int floorIndex) {
        if (noreenandKennethMurrayLibry != null) {
            noreenandKennethMurrayLibry.getIndoorMapManager().switchFloor(floorIndex);
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
        FloorButtonsNK = view.findViewById(R.id.FloorButtonsNK);

        setupFloorSelectionButtons(view); // 初始化楼层选择按钮
        setupFloorSelectionButtonsNK(view);


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
