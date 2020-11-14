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
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

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

    // Init variable
    private List<Alarm> dataList;
    private Activity context;
    private RoomDB database;

    // Constructor
    public AlarmClockAdapter(Activity context, List<Alarm> alarmList){
        this.context = context;
        this.dataList = alarmList;
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
        //Init database
        database = RoomDB.getInstance(context);
        //Set text on text view
        holder.textView.setText(alarm.getAlarmName());
        holder.textTime.setText(alarm.toString());


        Calendar c = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener d = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Init main data
                Alarm a = dataList.get(holder.getAdapterPosition());
                // Get id
                int sID = a.getId();

                Date currentTime = Calendar.getInstance().getTime();

                database.mainDao().updateTime(sID, hourOfDay, minute);

                dataList.clear();
                dataList.addAll(database.mainDao().getAll());
                notifyDataSetChanged();

            }
        };

        holder.textTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Init main data
                Alarm a = dataList.get(holder.getAdapterPosition());
                // Get id
                int sID = a.getId();

                new TimePickerDialog(context, d, a.hourOfDay, a.getMinutes(), false).show();

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
                        dialog.dismiss();
                        //Get Updated Text
                        String uAlarmName = editText.getText().toString().trim();
                        //Update DB
                        database.mainDao().update(sID, uAlarmName);

                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        notifyDataSetChanged();
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
        TextView textView, textTime;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assign variables
            textView = itemView.findViewById(R.id.text_name);
            textTime = itemView.findViewById(R.id.text_time);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

//public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.AlarmViewHolder> {



    /*
    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private AlarmViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    private final LayoutInflater mInflater;
    private static ClickListener clickListener;
    private List<Alarm> mAlarms;

    public AlarmClockAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        if (mAlarms != null) {
            Alarm currentAlarm = mAlarms.get(position);
            holder.wordItemView.setText(currentAlarm.getHours());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText(R.string.no_word);
        }
    }

    void setAlarms(List<Alarm> alarms) {
        mAlarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

     */
}
