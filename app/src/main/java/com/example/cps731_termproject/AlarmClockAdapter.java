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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        //if (count != count2) {
            count2++;
            holder.btnSwitch.setChecked(alarm.getState() == 0 ? true : false);
            Log.d(TAG, "test: " + position );
        //}

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
                if (alarm.getState() != 1){
                    alarm.setState(1);
                }
                else{
                    alarm.setState(0);
                }

                database.mainDao().updateState(sID, alarm.getState());

                //dataList.set(position, alarm);
                Log.d(TAG, "Switch is: " + (alarm.getState() != 1 ? "on" : "off"));

                dataList.clear();
                dataList.addAll(database.mainDao().getAll());
                notifyDataSetChanged();
            }
        });

        /*
        holder.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //Calendar c = Calendar.getInstance();
                    //c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
                    //c.set(Calendar.MINUTE, alarm.getMinutes());

                   // alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

                    //Intent myIntent = new Intent(context, AlarmReceiver.class);
                    //alarmIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
                    //alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), alarmIntent);

                    alarm.setState(0); // STANDBY
                }
                else {
                    alarm.setState(1); // STOPPED
                }
                //Update DB
                //holder.btnSwitch.setClickable(false);

                database.mainDao().updateState(sID, alarm.getState());

                //dataList.set(position, alarm);


                dataList.clear();
                dataList.addAll(database.mainDao().getAll());
                notifyDataSetChanged();
                //holder.btnSwitch.setClickable(true);

                Log.d(TAG, "Switch is: " + (isChecked ? "on" : "off"));
                //notifyItemChanged(position);

            }
        });
*/
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
                Button btnUpdate = dialog.findViewById(R.id.btn_update);

                editText.setText(sText);
                btnUpdate.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "position is: " + (position));
                        Log.d(TAG, "state is: " + (a.getState()));

                        dialog.dismiss();
                        //Get Updated Text
                        String uAlarmName = editText.getText().toString().trim();

                        //holder.btnSwitch.setClickable(false);
                        //Update DB
                        database.mainDao().update(sID, uAlarmName);
                        alarm.setAlarmName(uAlarmName);
                        dataList.set(position, a);

                        //dataList.clear();
                        //dataList.addAll(database.mainDao().getAll());
                        notifyItemChanged(position);
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

            Log.d(TAG, "COUNT: " + getItemCount());

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
