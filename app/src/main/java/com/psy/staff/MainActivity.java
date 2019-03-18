package com.psy.staff;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Human> mStaff;
    ListView mLvStaff;
    ArrayAdapter<Human> mArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLvStaff = findViewById(R.id.lvStuff);
        mLvStaff.setAdapter(new ArrayAdapter<Human>(this,R.layout.list_view_item)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menuItemAdd:
                //TODO: add
                break;
            case R.id.menuItemEdit:
                //TODO: edit
                break;
            case R.id.menuItemRemove:
                //TODO: remove
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
