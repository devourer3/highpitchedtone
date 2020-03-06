package com.mymusic.orvai.high_pitched_tone.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mymusic.orvai.high_pitched_tone.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class School extends Fragment {

    Context mCtx;
    MapView mMapView;
    private GoogleMap googleMap;
    String[] tel_number = {"tel:0226320101", "tel:025659396", "tel:01085140616", "tel:0222380100", "tel:025563998"};


    public School() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab5_school, container, false);
        mCtx = view.getContext();

        mMapView = (MapView) view.findViewById(R.id.map_fragment);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // 구글맵이 바로 보이도록 하기 위함


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true); // 내 위치 보기 버튼

                // For dropping a marker at a point on the Map
                final LatLng bodysound_location = new LatLng(37.520885, 126.895264);
                final LatLng haby_location = new LatLng(37.504922, 127.036090);
                final LatLng ham_voice = new LatLng(37.486812, 126.957507);
                final LatLng power_vocal = new LatLng(37.550651, 127.007877);
                final LatLng fnc_academy = new LatLng(37.507270, 127.065307);

                googleMap.addMarker(new MarkerOptions().position(bodysound_location).title("바디사운드").snippet("서울시 영등포구 당산로 67").icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_fragment_bodysound_icon))).setTag("http://www.xn--hy1b4dx7tp4eu2i.kr/");
                googleMap.addMarker(new MarkerOptions().position(haby_location).title("하비 실용 음악학원").snippet("서울 강남구 논현로 102길 12").icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_fragment_havy_icon))).setTag("http://havyvocal.com/");
                googleMap.addMarker(new MarkerOptions().position(ham_voice).title("함보이스").snippet("서울 관악구 관악로 284").icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_fragment_ham_icon))).setTag("http://www.hamvoice.com/");
                googleMap.addMarker(new MarkerOptions().position(power_vocal).title("파워보컬").snippet("서울 중구 다산로 70").icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_fragment_power_icon))).setTag("http://www.powervocal.com/");
                googleMap.addMarker(new MarkerOptions().position(fnc_academy).title("FNC 아카데미").snippet("서울 강남구 영동대로82길 9").icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_fragment_fnc_icon))).setTag("http://www.fncacademy.com/");


                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(37.483255, 126.974189)).zoom(11.5f).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // 마커 클릭 했을 시
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        Snackbar.make(container, "홈페이지를 방문하실 수 있습니다.", Snackbar.LENGTH_LONG).setAction("홈페이지 방문", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(marker.getTag())));
                                startActivity(intent);
                            }
                        }).show();
                        return false;
                    }
                });

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18f));
                    }
                });

                googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                    @Override
                    public void onInfoWindowLongClick(Marker marker) {
                        switch (marker.getId()) {
                            case "m0":
                                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse(tel_number[0]));
                                startActivity(intent);
                                break;
                            case "m1":
                                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse(tel_number[1]));
                                startActivity(intent1);
                                break;
                            case "m2":
                                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse(tel_number[2]));
                                startActivity(intent2);
                                break;
                            case "m3":
                                Intent intent3 = new Intent(Intent.ACTION_DIAL, Uri.parse(tel_number[3]));
                                startActivity(intent3);
                                break;
                            case "m4":
                                Intent intent4 = new Intent(Intent.ACTION_DIAL, Uri.parse(tel_number[4]));
                                startActivity(intent4);
                                break;
                            default:
                                break;
                        }
                    }
                });


            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}

