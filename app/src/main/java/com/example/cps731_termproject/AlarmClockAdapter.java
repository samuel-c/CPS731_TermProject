/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cps731_termproject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cps731_termproject.utils.Alarm;
import com.example.cps731_termproject.utils.Converters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

/**
 * Adapter for the RecyclerView that displays a list of words.
 */

public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.ViewHolder>{

    private final String TAG = "AlarmClockAdapter";

    // Init variable
    private List<Alarm> dataList;
    private int count;
    private int count2 = 0;
    private Activity context;
    private RoomDB database;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    boolean var = false;

    // Constructor
    public AlarmClockAdapter(Activity context, List<Alarm> alarmList){
        this.context = context;
        this.dataList = alarmList;
        count = alarmList.size();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmClockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Init view
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmClockAdapter.ViewHolder holder, int position) {

        //Init main data
        Alarm alarm = dataList.get(position);
        int sID = alarm.getId();
        //Init database
        database = RoomDB.getInstance(context);
        //Set text on text view
        holder.textView.setText(alarm.getAlarmName());
        holder.textTime.setText(alarm.toString());
        holder.btnSwitch.setChecked(alarm.getState() == 0 ? true : false);

        Calendar c = Calendar.getInstance();

        // Create Alarm
        TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Init main data
                //Alarm a = dataList.get(holder.getAdapterPosition());
                // Get id
                //int sID = a.getId();

                Date currentTime = Calendar.getInstance().getTime();


                dataList.add(alarm);

                holder.btnSwitch.setClickable(false);
                database.mainDao().updateTime(sID, hourOfDay, minute);


                //dataList.clear();
                //dataList.addAll(database.mainDao().getAll());
                notifyItemInserted(position);
                holder.btnSwitch.setClickable(true);

            }
        };

        // Alarm state (switch button)
        holder.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize current Alarm
                Calendar currentAlarmTime = Calendar.getInstance();
                currentAlarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHours());
                currentAlarmTime.set(Calendar.MINUTE, alarm.getMinutes());
                currentAlarmTime.set(Calendar.SECOND, 0);

                if (currentAlarmTime.before(Calendar.getInstance()))
                    currentAlarmTime.add(Calendar.DAY_OF_MONTH, 1); // Go next day

                Log.d(TAG, "Cal: " + currentAlarmTime.getTime().toString());

                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("alarm", alarm);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, sID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Alarm Started: " + alarm.getAlarmName() + sID);


                Log.d(TAG, "SWITCH CLICKED");
                if (alarm.getState() != 1){
                    alarm.setState(1); // Set to OFF
                    alarmManager.cancel(pendingIntent);
                    Log.d(TAG, "Alarm Canceled: Name: " + alarm.getAlarmName() + ", ID: " + alarm.getId());

                }
                else{
                    alarm.setState(0); // Set to Standby
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentAlarmTime.getTimeInMillis(), pendingIntent);
                    Log.d(TAG, "Alarm Started: Name: " + alarm.getAlarmName() + ", ID: " + alarm.getId());

                }

                // Update current alarm state
                database.mainDao().updateState(sID, alarm.getState());

                Log.d(TAG, "Switch is: " + (alarm.getState() != 1 ? "on" : "off"));

                // Update dataset
                dataList.clear();
                dataList.addAll(database.mainDao().getAll());
                notifyDataSetChanged();
            }
        });

        // Alarm update (time)
        holder.textTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TimePickerDialog(context, d, alarm.hourOfDay, alarm.getMinutes(), false).show();

            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Init main data
                Alarm a = dataList.get(holder.getAdapterPosition());
                // Get id
                int sID = a.getId();
                //Get txt

                Alarm tAlarm = database.mainDao().getAlarm(sID);
                String sText = tAlarm.getAlarmName();
                boolean tVib = tAlarm.isVibration();
                boolean[] tDaysOfWeek = tAlarm.getDaysOfWeek();

                //Dialog
                Dialog dialog = new Dialog(context);
                //dialog.setContentView(R.layout.content_main);
                dialog.setContentView(R.layout.dialog_update);


                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width,height);
                dialog.show();

                //Init and assign variables
                EditText editText = dialog.findViewById(R.id.edit_text);
                CheckBox vib = dialog.findViewById(R.id.checkBoxVibration);
                CheckBox sun = dialog.findViewById(R.id.checkBoxSun);
                CheckBox mon = dialog.findViewById(R.id.checkBoxMon);
                CheckBox tue = dialog.findViewById(R.id.checkBoxTue);
                CheckBox wed = dialog.findViewById(R.id.checkBoxWed);
                CheckBox thu = dialog.findViewById(R.id.checkBoxThu);
                CheckBox fri = dialog.findViewById(R.id.checkBoxFri);
                CheckBox sat = dialog.findViewById(R.id.checkBoxSat);
                Button btnUpdate = dialog.findViewById(R.id.btn_update);

                editText.setText(sText);
                vib.setChecked(tVib);
                sun.setChecked(tDaysOfWeek[0]);
                mon.setChecked(tDaysOfWeek[1]);
                tue.setChecked(tDaysOfWeek[2]);
                wed.setChecked(tDaysOfWeek[3]);
                thu.setChecked(tDaysOfWeek[4]);
                fri.setChecked(tDaysOfWeek[5]);
                sat.setChecked(tDaysOfWeek[6]);

                btnUpdate.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "position is: " + (position));
                        Log.d(TAG, "state is: " + (a.getState()));

                        dialog.dismiss();
                        //Get Updated Text
                        String uAlarmName = editText.getText().toString().trim();

                        boolean[] temp = new boolean[] {false, false, false, false, false, false, false};
                        temp[0] = sun.isChecked();
                        temp[1] = mon.isChecked();
                        temp[2] = tue.isChecked();
                        temp[3] = wed.isChecked();
                        temp[4] = thu.isChecked();
                        temp[5] = fri.isChecked();
                        temp[6] = sat.isChecked();
                        Log.d(TAG, "Array[0]: " + (temp[0]));
                        Log.d(TAG, "Array[1]: " + (temp[1]));
                        Log.d(TAG, "Array[2]: " + (temp[2]));
                        Log.d(TAG, "Array[3]: " + (temp[3]));
                        Log.d(TAG, "Array[4]: " + (temp[4]));
                        Log.d(TAG, "Array[5]: " + (temp[5]));
                        Log.d(TAG, "Array[6]: " + (temp[6]));
                        //Converters.arrayToString(temp)
                        //holder.btnSwitch.setClickable(false);
                        //Update DB
                        database.mainDao().update(sID, uAlarmName);
                        database.mainDao().updateVib(sID, vib.isChecked());
                        database.mainDao().updateDOW(sID, Converters.arrayToString(temp));
                        alarm.setAlarmName(uAlarmName);
                        alarm.setVibration(vib.isChecked());
                        alarm.setDaysOfWeek(temp);
                        dataList.set(position, a);

                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        notifyDataSetChanged();
                        //notifyItemChanged(position);
                        //holder.btnSwitch.setClickable(true);
                    }
                });
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Init Alarm
                Alarm alarm = dataList.get(holder.getAdapterPosition());
                // Delete
                database.mainDao().delete(alarm);

                int position = holder.getAdapterPosition();
                dataList.remove(position);


                notifyItemRemoved(position);
                notifyItemRangeChanged(position, dataList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Init variable
        Switch btnSwitch;
        TextView textView, textTime;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Assign variables
            btnSwitch = itemView.findViewById(R.id.alarm_switch1);

            //btnSwitch.setChecked(itemView. .getState() != 1 ? true : false);
            textView = itemView.findViewById(R.id.text_name);
            textTime = itemView.findViewById(R.id.text_time);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

}
