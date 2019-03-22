package com.psy.staff;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity {


//    File base = new File("base.b");
    private static final String BASE = "base.b";
    /**
     * KEYS
     */
    private static final String STAFF_LIST = "staffList";
    private static final String FIRST_VISIBLE_ITEM = "firstVisibleItem";
    private static final String CURRENT_SELECTED_ITEM_POS = "currentSelectedItemPosition";


    private int SCREEN_ORIENTATION;
    /**
     * Список сотрудников
     */
    ArrayList<Human> mStaff;
    ListView mLvStaff;

    /**
     * текущий выбранный элемент
     */
    int mCurSelectedPos = -1;
    View mCurSelectedView;
    /**
     * Цвет Фона не выбранного элемента
     */
    private int mNrmlColor = R.color.colorNormal;
    /**
     * Цвет Фона выбранного элемента
     */
    private int mSlctColor = R.color.colorSelected;
    /**
     * Пол сотрудника
     */
    private static final Gender MALE = new Gender(R.drawable.male,R.string.human_genderMale, true);
    private static final Gender FEMALE = new Gender(R.drawable.female,R.string.human_genderFemale, false);
    Gender[] genderList = new Gender[]{ MALE, FEMALE};
    ArrayAdapter<Gender> spinnerAdapter;

    int firstVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null)
        {
            mStaff = (ArrayList<Human>) savedInstanceState.getSerializable(STAFF_LIST);
            firstVisibleItem = savedInstanceState.getInt(FIRST_VISIBLE_ITEM);
            mCurSelectedPos = savedInstanceState.getInt(CURRENT_SELECTED_ITEM_POS);

        }
        if(mStaff==null)//&&base.exists())\
        {
            Byte[] buf = new Byte[32];
            try {
                FileInputStream FIS = openFileInput(BASE);
                ObjectInputStream OIS = new ObjectInputStream(FIS);
                mStaff = (ArrayList<Human>) OIS.readObject();
                OIS.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        //-------------------------------------ADAPTERS-------------------------------------------------

        spinnerAdapter = new ArrayAdapter<Gender>(MainActivity.this, R.layout.spinner_gender_item,R.id.tvSpinnerGenderLabel, genderList){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Gender curGender = this.getItem(position);

                ImageView ivGender = view.findViewById(R.id.ivSpinnerGenderImage);
                ivGender.setImageDrawable(getResources().getDrawable(curGender.mDrawableResId));

                TextView tvGenderLabel = view.findViewById(R.id.tvSpinnerGenderLabel);
                tvGenderLabel.setText(getResources().getString(curGender.mStringResId));

                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                Gender curGender = this.getItem(position);

                ImageView ivGender = view.findViewById(R.id.ivSpinnerGenderImage);
                ivGender.setImageDrawable(getResources().getDrawable(curGender.mDrawableResId));

                TextView tvGenderLabel = view.findViewById(R.id.tvSpinnerGenderLabel);
                tvGenderLabel.setText(getResources().getString(curGender.mStringResId));

                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_gender_item);

        if(mStaff == null)
            mStaff = new ArrayList<>();
        //find listView
        mLvStaff = findViewById(R.id.lvStuff);
        //Set adapter
        mLvStaff.setAdapter(new ArrayAdapter<Human>(this,R.layout.list_view_item,R.id.tvFirstName,mStaff)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Human human = this.getItem(position);

                TextView tvFirstName = view.findViewById(R.id.tvFirstName);
                TextView tvLastName = view.findViewById(R.id.tvLastName);
                TextView tvBirthDate = view.findViewById(R.id.tvBirthDate);
                ImageView ivGender = view.findViewById(R.id.ivGender);

                if(mCurSelectedPos ==position)
                {
                    view.setBackgroundColor(getResources().getColor(mSlctColor));
                    mCurSelectedView = view;
                }
                else
                {
                    view.setBackgroundColor(getResources().getColor(mNrmlColor));
                }
                tvFirstName.setText(human.mFirstName);
                tvLastName.setText(human.mLastName);
                tvBirthDate.setText(human.getBirthDateString());
                if(human.mGender)
                {
                    ivGender.setImageResource(R.drawable.male);
                }
                else
                {
                    ivGender.setImageResource(R.drawable.female);
                }
                return view;
            }

        });
        if(firstVisibleItem!=0)
            mLvStaff.setSelection(firstVisibleItem);



        //Create and set listener
        mLvStaff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCurSelectedPos !=-1)
                {
                    mCurSelectedView.setBackgroundColor(getResources().getColor(mNrmlColor));
                }
                if(mCurSelectedPos == position)
                {
                    //снимаем выделение с текущего элемента если нажат повторно
                    mCurSelectedView.setBackgroundColor(getResources().getColor(mNrmlColor));
                    mCurSelectedPos = -1;
                    mCurSelectedView = null;
                }
                else {
                    mCurSelectedPos = position;
                    mCurSelectedView = view;
                    mCurSelectedView.setBackgroundColor(getResources().getColor(mSlctColor));
                }
            }
        });
    }
// -----------------------------OPTIONS MENU--------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menuItemRotateScreen).setIcon(R.drawable.twotone_screen_rotation_white_24);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menuItemAdd:
                addHuman();
                break;
            case R.id.menuItemEdit:
                editHuman();
                break;
            case R.id.menuItemRemove:
                removeHuman();
                break;
            case R.id.menuItemRotateScreen:
                rotateScreen();
                break;
            case R.id.addDummyData:
                addDummyData();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STAFF_LIST,mStaff);
        outState.putInt(FIRST_VISIBLE_ITEM, mLvStaff.getFirstVisiblePosition());
        outState.putInt(CURRENT_SELECTED_ITEM_POS, mCurSelectedPos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            FileOutputStream FOS = openFileOutput(BASE, MODE_PRIVATE);
//                    new FileOutputStream(base);
//            DataOutputStream DOS = new DataOutputStream(FOS);
            ObjectOutputStream OOS = new ObjectOutputStream(FOS);
            OOS.writeObject(mStaff);
            OOS.flush();
            OOS.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ON DESTROY", e.getMessage());
        }
    }

    //----------------------------------------HELPER METHODS---------------------------------------
    void addHuman()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        builder.setTitle(R.string.dialog_titleAdd);
        LayoutInflater inflater = getLayoutInflater();
        final View view  = inflater.inflate(R.layout.dialog_box_main,null,false);

        final Spinner spGender = view.findViewById(R.id.spGender);
        spGender.setAdapter(spinnerAdapter);


        builder.setPositiveButton(R.string.dialog_btnPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText etFirstName = view.findViewById(R.id.etFirstName);
                EditText etLastName = view.findViewById(R.id.etLastName);

                DatePicker dpBirthDate = view.findViewById(R.id.dpBirthDate);
                if(etFirstName.getText().toString().isEmpty()||etFirstName.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, R.string.toast_warning_emptyField, Toast.LENGTH_SHORT).show();
                    return;
                }
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                boolean gender = ((Gender) spGender.getSelectedItem()).mGender;

                final Calendar birthDate = getDate(dpBirthDate);
                if(birthDate.getTimeInMillis()>System.currentTimeMillis())
                {
                    Toast.makeText(MainActivity.this, R.string.toast_warning_birthDate, Toast.LENGTH_SHORT).show();
                    return;
                }
                /*final Calendar birthDate = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dpBirthDate.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            birthDate.set(year,monthOfYear,dayOfMonth);
                        }
                    });
                }
*/
                Human tmp = new Human(firstName, lastName, gender,birthDate );
                ArrayAdapter <Human> adapter = (ArrayAdapter<Human>) mLvStaff.getAdapter();
                adapter.add(tmp);
            }
        });
        builder.setNegativeButton(R.string.dialog_btnNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*IGNORE*/
            }
        });

        builder.setView(view);
        builder.create().show();

    }

    void editHuman(){
        if(mCurSelectedPos==-1)
        {
            Toast.makeText(MainActivity.this, R.string.nothig_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        builder.setTitle(R.string.dialog_titleEdit);
        LayoutInflater inflater = getLayoutInflater();
        final View view  = inflater.inflate(R.layout.dialog_box_main,null,false);

        final Spinner spGender = view.findViewById(R.id.spGender);
        spGender.setAdapter(spinnerAdapter);


        final Human editableHuman = (Human) mLvStaff.getAdapter().getItem(mCurSelectedPos);


        final EditText etFirstName = view.findViewById(R.id.etFirstName);
        etFirstName.setText(editableHuman.mFirstName);

        final EditText etLastName = view.findViewById(R.id.etLastName);
        etLastName.setText(editableHuman.mLastName);

        if(editableHuman.mGender) {
            spGender.setSelection(spinnerAdapter.getPosition(MALE));
        }else{
            spGender.setSelection(spinnerAdapter.getPosition(FEMALE));
        }

        final Calendar tmpBirthDate = editableHuman.mBirthDate;

        final DatePicker dpBirthDate = view.findViewById(R.id.dpBirthDate);
        dpBirthDate.init(editableHuman.mBirthDate.get(Calendar.YEAR),
                editableHuman.mBirthDate.get(Calendar.MONTH),
                editableHuman.mBirthDate.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tmpBirthDate.set(year,monthOfYear,dayOfMonth);
                    }
                });

        builder.setPositiveButton(R.string.dialog_btnPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(etFirstName.getText().toString().isEmpty()||etFirstName.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, R.string.toast_warning_emptyField, Toast.LENGTH_SHORT).show();
                    return;
                }
                editableHuman.mFirstName = etFirstName.getText().toString().substring(0,1).toUpperCase() +
                        etFirstName.getText().toString().substring(1);
                editableHuman.mLastName = etLastName.getText().toString().substring(0,1).toUpperCase() +
                        etLastName.getText().toString().substring(1);;
                editableHuman.mGender = ((Gender) spGender.getSelectedItem()).mGender;
                if(tmpBirthDate.getTimeInMillis()>System.currentTimeMillis())
                {
                    Toast.makeText(MainActivity.this, R.string.toast_warning_birthDate, Toast.LENGTH_SHORT).show();
                    return;
                }
                editableHuman.mBirthDate = tmpBirthDate;

                ArrayAdapter <Human> adapter = (ArrayAdapter<Human>) mLvStaff.getAdapter();

                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.dialog_btnNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*IGNORE*/
            }
        });

        builder.setView(view);
        builder.create().show();
    }

    void removeHuman()
    {
        String msg;
        if(mCurSelectedPos!=-1)
        {
            ArrayAdapter<Human> adapter = (ArrayAdapter<Human>) mLvStaff.getAdapter();
            Human selHuman =adapter.getItem(mCurSelectedPos);
            msg = "Сотрудник " +
                    selHuman.mLastName + " " +
                    selHuman.mFirstName + " уделен.";
            mCurSelectedPos =-1;
            mCurSelectedView = null;
            adapter.remove(selHuman);

        }
        else {
            msg = getResources().getString(R.string.nothig_selected);
        }
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT ).show();
    }

    Calendar getDate(DatePicker dp)
    {
        int day = dp.getDayOfMonth();
        int month = dp.getMonth();
        int year = dp.getYear();
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        return date;

    }

    void rotateScreen()
    {
        SCREEN_ORIENTATION = getRequestedOrientation();
        SCREEN_ORIENTATION= Math.abs(SCREEN_ORIENTATION); //to Fix undefined screen state
        if(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == SCREEN_ORIENTATION) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == SCREEN_ORIENTATION){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    void addDummyData(){

        for (int i = 0; i < 5; i++) {
            Calendar c = Calendar.getInstance();
            c.set(200+i,3+i,15+i);
            Human tmp = new Human("firstName"+(i+1), "lastName"+(i+1), (i<3), c);
            ArrayAdapter <Human> adapter = (ArrayAdapter<Human>) mLvStaff.getAdapter();
            adapter.add(tmp);
        }

    }

}
