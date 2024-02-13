package com.openpositioning.PositionMe.fragments;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.openpositioning.PositionMe.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.openpositioning.PositionMe.R;

public class NoreenandKennethMurrayLibraryMap {
    private IndoorMapManager indoorMapManager;

    public NoreenandKennethMurrayLibraryMap(GoogleMap map) {
        // The nuclear building has 5 floors
        indoorMapManager = new IndoorMapManager(map, 4);

        double N1 = 55.92281;
        double W1 = 3.175171;

        double N2 = 55.923065;
        double W2 = 3.174747;

        // Initialize the indoor map of each layer
        indoorMapManager.addFloor(0, R.drawable.libraryg, new LatLngBounds(new LatLng(N1, -W1), new LatLng(N2, -W2)));
        indoorMapManager.addFloor(1, R.drawable.library1, new LatLngBounds(new LatLng(N1, -W1), new LatLng(N2, -W2)));
        indoorMapManager.addFloor(2, R.drawable.library2, new LatLngBounds(new LatLng(N1, -W1), new LatLng(N2, -W2)));
        indoorMapManager.addFloor(3, R.drawable.library3, new LatLngBounds(new LatLng(N1, -W1), new LatLng(N2, -W2)));
        //indoorMapManager.addFloor(4, R.drawable.floor_3, new LatLngBounds(new LatLng(N1, -W1), new LatLng(N2, -W2)));

    }
    public IndoorMapManager getIndoorMapManager() {
        return indoorMapManager;
    }
}