package com.techtitude.apicall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //Todo: I haven't enable permission enable method. Please give access to permission manually
        //Todo: I don't have endpoints so I have code for post data, but I have put my sharing details
        //Todo: to another screen without checking the response status code
        //Todo: You can check my retrofit code by entering th correct endpoints in RetrifitCall interface class
        //Todo: Please also check the BASE_URL

        data = (TextView) findViewById(R.id.data);

        data.setText("Name : "+getIntent().getStringExtra("name")
                +"\n Id : "+getIntent().getStringExtra("id")
                +"\n Latitude : "+getIntent().getStringExtra("lat")
                +"\n Longitude : "+getIntent().getStringExtra("long"));
    }
}