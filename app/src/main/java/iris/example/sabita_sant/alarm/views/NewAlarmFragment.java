package iris.example.sabita_sant.alarm.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.backend.Alarm;
import iris.example.sabita_sant.alarm.backend.AlarmDatabase;
import iris.example.sabita_sant.alarm.controller.AlarmHelper;
import iris.example.sabita_sant.alarm.utils.AlarmType;
import iris.example.sabita_sant.alarm.utils.Animatation;
import iris.example.sabita_sant.alarm.utils.Constants;
import iris.example.sabita_sant.alarm.utils.Message;

public class NewAlarmFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "NewAlarmFragment";
    private TextView alarmTime_tv;
    private EditText label_et;
    private View time_ll;
    private Calendar alarmCalendar;
    private TimePicker timePicker;
    private AlertDialog timePickerDialog;
    private ImageView setAlarm;
    private int snooze; // in mins
    private Spinner snooze_sp, type_sp;
    private int repeat_count; // maintains the count off no of repeated days
    private boolean[] repeatDays;
    private AlarmType type;
    private View parent;
    private String alarmText;
    private CheckBox repeat_cb[];

    public NewAlarmFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_new_alarm, container, false);
        alarmTime_tv = parent.findViewById(R.id.time_tv);
        time_ll = parent.findViewById(R.id.time_ll);
        setAlarm = parent.findViewById(R.id.set_alarm);
        label_et = parent.findViewById(R.id.label_et);
        setAlarm.setOnClickListener(this);
        time_ll.setOnClickListener(this);
        setupSnoozeSpinner(parent);
        setupRepeat(parent);
        setupTypeSpinner(parent);
        return parent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_ll:
                showTimePicker();
                break;
            case R.id.submit_time:
                if (timePickerDialog == null)
                    return;
                Animatation.spin(view);
                alarmCalendar = getAlarmCalendar();
                SimpleDateFormat tf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
                //SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
                alarmText = tf.format(alarmCalendar.getTimeInMillis());
                alarmTime_tv.setText(alarmText);
                timePickerDialog.dismiss();
                break;
            case R.id.set_alarm:
                if (!validParams())
                    break;
                setAlarm();

        }

    }

    private void setAlarm() {
        //alarm time, label
        Log.i(TAG, "setAlarm: start alarm type" + type);
        String label = label_et.getText().toString();
        if (label.length() == 0)
            label = getResources().getString(R.string.no_label);
        Alarm alarm = new Alarm(alarmCalendar.getTimeInMillis(), snooze, repeat_count, repeatDays, true, null, type, label);
        // store in alarm db
        AlarmDatabase db = AlarmDatabase.getInstance(getContext());

        db.alarmDao().addAlarm(alarm);
        AlarmHelper helper = new AlarmHelper(getContext(), alarm.getId());
        helper.setAlarm();
        //notifying user
        Message.showSnackbar(getActivity(), parent, "Alarm set at " + alarmText);
        Animatation.spin(setAlarm);
        Log.i(TAG, "setAlarm: alarm type" + alarm.getType());
    }

    private boolean validParams() {
        if (alarmCalendar == null) {
            Message.showSnackbar(getActivity(), parent, "Please set alarm time");
            return false;
        }
        return true;

    }

    public void showTimePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View dialog = inflater.inflate(R.layout.dialog_timepicker, null);
        builder.setView(dialog)
                .setCancelable(true);
        timePickerDialog = builder.create();
        timePickerDialog.show();
        timePicker = dialog.findViewById(R.id.time_picker);
        ImageView submitTime = dialog.findViewById(R.id.submit_time);
        submitTime.setOnClickListener(NewAlarmFragment.this);

    }

    private void setupSnoozeSpinner(View parent) {
        snooze_sp = parent.findViewById(R.id.snooze_spinner);
        snooze = 2;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.snooze_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snooze_sp.setAdapter(adapter);
        snooze_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                snooze = Integer.valueOf(getResources().getStringArray(R.array.snooze_time)[position].split(" ")[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                snooze = 2;
            }

        });
    }

    private void setupTypeSpinner(View parent) {
        type_sp = parent.findViewById(R.id.type_spinner);
        type = AlarmType.PHRASE;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.alarm_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_sp.setAdapter(adapter);
        type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        type = AlarmType.PHRASE;
                        break;
                    case 1:
                        type = AlarmType.ARIHEMATIC;
                        break;
                    case 2:
                        type = AlarmType.SIMPLE;
                        break;
                }
                Log.i(TAG, "onItemSelected: alarm type " + type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = AlarmType.PHRASE;
            }

        });
    }

    public Calendar getAlarmCalendar() {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        alarmTime.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        return alarmTime;
    }

    public void setupRepeat(View parent) {
        repeatDays = new boolean[7];
        repeat_cb = new CheckBox[7];
        for (int i = 0; i < 7; i++) {
            repeat_cb[i] = parent.findViewById(Constants.REPEAT_CHECKBOXES[i]);
            repeat_cb[i].setOnCheckedChangeListener(this);
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (b)
            repeat_count++;
        else
            repeat_count--;
        for (int i = 0; i < 7; i++) {
            if (Constants.REPEAT_CHECKBOXES[i] == id) {
                repeatDays[i] = b;
                break;
            }
        }


    }
    //Log.info(TAG,"repeat days "+repeatDays[0]+repeatDays[1]+repeatDays[2]+repeatDays[3]+repeatDays[4]+repeatDays[5]+repeatDays[6]);

    public void loadPreviousAlarm(int alarmId) {
        Alarm prevAlarm = AlarmDatabase.getInstance(getContext()).alarmDao().getAlarm(alarmId);
        alarmTime_tv.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(prevAlarm.getAlarmTime()));
        label_et.setText(prevAlarm.getLabel());
        if (prevAlarm.getRepeatCount() > 0) {
            for (int i = 0; i < prevAlarm.getRepeatDays().length; i++) {
                repeat_cb[i].setChecked(prevAlarm.getRepeatDays()[i]);
            }
        }
        int snoozePos = 0;
        for (int i = 0; i < getResources().getIntArray(R.array.snooze_time).length; i++) {
            if (getResources().getIntArray(R.array.snooze_time)[i] == prevAlarm.getSnoozeDuration()) {
                snoozePos = i;
                break;
            }
        }
        snooze_sp.setSelection(snoozePos);
    }


}
