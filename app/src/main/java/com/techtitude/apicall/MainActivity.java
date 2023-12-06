package com.techtitude.apicall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText inputUsername, inputUserid;
    private Double latitude=0.0, longitude=0.0;
    private ProgressBar progressBar;
    FusedLocationProviderClient fusedLocationProviderClient;
    private TextView submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Todo: I haven't enable permission enable method. Please give access to permission manually
        //Todo: I don't have endpoints so I have code for post data, but I have put my sharing details
        //Todo: to another screen without checking the response status code
        //Todo: You can check my retrofit code by entering th correct endpoints in RetrifitCall interface class
        //Todo: Please also check the BASE_URL

        String BASE_URL = "https://dev.channelier.com/api/public/whatsupCallback/";
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        progressBar = (ProgressBar) findViewById(R.id.submit_progress);
        inputUsername = (EditText) findViewById(R.id.input_username);
        inputUserid = (EditText) findViewById(R.id.input_userid);

        submit = (TextView) findViewById(R.id.input_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(inputUsername.getText().toString().trim())) {
                    inputUsername.setError("Invalid Username");
                    return;
                } else if (TextUtils.isEmpty(inputUserid.getText().toString().trim())) {
                    inputUserid.setError("Invalid Userid");
                    return;
                }
                else if(latitude==0.0||longitude==0.0)
                {
                    Toast.makeText(MainActivity.this, "Error Occurred : Try Again", Toast.LENGTH_SHORT).show();
                    submit.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    submit.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitCall retrofitCall = retrofit.create(RetrofitCall.class);

                    DataModel dataModel = new DataModel(inputUsername.getText().toString().trim(),
                            inputUserid.getText().toString().trim(),
                            latitude.toString(),
                            longitude.toString());

                    Call<DataModel> apiCall = retrofitCall.sendData(dataModel);

                    Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                    intent.putExtra("name",inputUsername.getText().toString().trim());
                    intent.putExtra("id",inputUserid.getText().toString().trim());
                    intent.putExtra("lat",latitude.toString());
                    intent.putExtra("long",longitude.toString());
                    startActivity(intent);

                    apiCall.enqueue(new Callback<DataModel>() {
                        @Override
                        public void onResponse(Call<DataModel> call, Response<DataModel> response) {

                            Log.d("MainActivity RC : ",String.valueOf(response.code()));
                            Toast.makeText(MainActivity.this,"Body : "+response.code(),Toast.LENGTH_SHORT).show();

                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                            /*if(response.code()==200)
                            {

                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Error Occurred",Toast.LENGTH_SHORT);
                                progressBar.setVisibility(View.GONE);
                                submit.setVisibility(View.VISIBLE);
                            }*/

                        }

                        @Override
                        public void onFailure(Call<DataModel> call, Throwable t) {
                            Toast.makeText(MainActivity.this,"Error Occurred : "+t.getLocalizedMessage().toString(),Toast.LENGTH_SHORT);
                            progressBar.setVisibility(View.GONE);
                            submit.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        fetchCurrentLocation();
    }

    private void fetchCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Boolean isLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isLocation) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isComplete()) {
                        Location location = task.getResult();
                        if (location == null) {
                            LocationRequest locationRequest = new LocationRequest();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationRequest.setInterval(5);
                            locationRequest.setFastestInterval(0);
                            locationRequest.setNumUpdates(1);

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                        }
                        else
                        {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Toast.makeText(MainActivity.this, "Latitude : "+latitude.toString()+ " and Longitude : "+longitude, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }



    }

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location locationDetails = locationResult.getLastLocation();

            latitude = locationDetails.getLatitude();
            longitude = locationDetails.getLongitude();

            Toast.makeText(MainActivity.this, "Latitude : "+latitude.toString()+ " and Longitude : "+longitude, Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
            Log.d("MainActivity Location","Latitude : "+latitude+ " and "+"Longitude : "+longitude);
            super.onLocationResult(locationResult);
        }
    };
}