package com.floating_cloud.event_point_user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    LinearLayout panel;
    TextView sName;
    TextView sType;
    Button infB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        panel = view.findViewById(R.id.panel);
        sName = view.findViewById(R.id.storeName);
        sType = view.findViewById(R.id.storeType);
        infB = view.findViewById(R.id.infBTN);

        initMap();
        return view;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ApiClient apiClient = new ApiClient();
        apiClient.fetchSites(new ApiCallback<List<SiteData>>() {
            @Override
            public void onSuccess(List<SiteData> result) {
                // GET 요청의 결과 처리
                for (SiteData site : result) {
                    LatLng latLng = new LatLng(site.getLatitude(), site.getLongitude());
                    addMarkerWithStoreInfo(latLng, site.getName(), site.getTag(), site.getDescription());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true); // 내 위치 레이어 활성화
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
                }
            });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // 다이얼로그를 표시하여 가게 정보 입력받기
        panel.setVisibility(View.GONE);
    }

    private void addMarkerWithStoreInfo(LatLng latLng, String storeName, String storeType, String storeDescription) {
        // 마커에 가게 정보를 포함하여 추가
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(storeName)
                .snippet(storeType)
        );
        marker.setTag(storeDescription);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                panel.setVisibility(View.VISIBLE);
                sName.setText(marker.getTitle());
                sType.setText(marker.getSnippet());
                String description = (String) marker.getTag();
                LatLng latLng1 = marker.getPosition();
                infB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StoreInfFragment storeInfFragment = new StoreInfFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("key1", marker.getTitle());
                        bundle.putString("key2", marker.getSnippet());
                        bundle.putString("key3", description);
                        bundle.putDouble("key4",latLng1.latitude );
                        bundle.putDouble("key5", latLng1.longitude);
                        storeInfFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.main_frame, storeInfFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우, 내 위치 레이어 활성화
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        }
    }
}