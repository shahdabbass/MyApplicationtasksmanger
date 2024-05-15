package com.example.myapplication_tasksmanger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton Fab;
    SearchView Sv;
    Spinner sspnr;
    ListView lstv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fab=findViewById(R.id.fabAdd);
        Sv=findViewById(R.id.srchV);
        sspnr=findViewById(R.id.spnr);
        lstv=findViewById(R.id.lstV);

    }
}