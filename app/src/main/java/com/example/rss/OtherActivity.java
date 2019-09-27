package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
    }

    @Override
    public void onBackPressed() {
        //need an Intent just as a container for the data
        //to pass back
        //Intent intent = new Intent();
        //String name = etName.getText().toString();
        //boolean happy = cbHappy.isChecked();
        //intent.putExtra("name", name);
        //intent.putExtra("happy", happy);
        //set the result and finish (returning to the previous activity)
        //setResult(RESULT_OK, intent);
        //finish();
        //super.onBackPressed(); //calls finish() and close app.

        Intent i = new Intent(this, MainActivity.class);
        // i.putExtra("test", "some data");
        // i.putExtra("pi", 3.1415f);
        startActivity(i);

    }
}
