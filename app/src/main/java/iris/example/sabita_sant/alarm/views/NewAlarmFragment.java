package iris.example.sabita_sant.alarm.views;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import iris.example.sabita_sant.alarm.controller.AlarmHelper;
import iris.example.sabita_sant.alarm.models.Alarm;
import iris.example.sabita_sant.alarm.models.AlarmDatabase;
import iris.example.sabita_sant.alarm.utils.AlarmType;
import iris.example.sabita_sant.alarm.utils.Animatation;
import iris.example.sabita_sant.alarm.utils.Constants;
import iris.example.sabita_sant.alarm.utils.Utils;

import static android.app.Activity.RESULT_OK;

public class NewAlarmFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "NewAlarmFragment";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
    private TextView alarmTime_tv, tone_tv;
    private EditText label_et;
    private View time_ll, tone_ll;
    private Calendar alarmCalendar;
    private TimePicker timePicker;
    private AlertDialog timePickerDialog;
    private ImageView setAlarm;
    private int snooze; // in mins
    private Spinner snooze_sp, type_sp, repeat_sp;
    private int repeat_count; // maintains the count off no of repeated days
    private boolean[] repeatDays;
    private AlarmType type;
    private View parent;
    private String alarmText, toneUri;
    private CheckBox repeat_cb[];
    private Boolean updateAlarm;
    private Alarm prevAlarm;

    public NewAlarmFragment() {
        updateAlarm = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_new_alarm, container, false);
        setUI(parent);
        setupSnoozeSpinner(parent);
        setupRepeat(parent);
        setupTypeSpinner(parent);
        return parent;
    }

    public void setUI(View parent) {
        alarmTime_tv = parent.findViewById(R.id.time_tv);
        time_ll = parent.findViewById(R.id.time_ll);
        tone_ll = parent.findViewById(R.id.tone_ll);
        setAlarm = parent.findViewById(R.id.set_alarm);
        tone_tv = parent.findViewById(R.id.tone_uri);
        label_et = parent.findViewById(R.id.label_et);
        setAlarm.setOnClickListener(this);
        time_ll.setOnClickListener(this);
        tone_ll.setOnClickListener(this);
        label_et.getText().clear();
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
                Animatation.spin(view)
                        .addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                timePickerDialog.dismiss();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                alarmCalendar = getAlarmCalendar();
                alarmText = Utils.getAlarmText(alarmCalendar.getTimeInMillis());
                alarmTime_tv.setText(alarmText);
                break;
            case R.id.set_alarm:
                if (!validParams())
                    break;
                setAlarm();
                break;
            case R.id.tone_ll:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {


                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                        // app-defined int constant that should be quite unique

                        return;
                    }
                    updateTone();
                    break;
                }
                updateTone();
        }

    }

    private void updateTone() {

        final Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_ALARM);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 99);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 99) {
            final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                toneUri = uri.toString();
                Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String title = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
                tone_tv.setText(title.split("\\.")[0]);
            } else toneUri = "";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    updateTone();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    private void setAlarm() {
        //alarm time, label
        Log.i(TAG, "setAlarm: start alarm type" + type);
        AlarmDatabase db = AlarmDatabase.getInstance(getContext());
        // if update delete prev alarm and create new
        if (updateAlarm) {
            AlarmHelper prevHelper = new AlarmHelper(getContext(), prevAlarm.getId());
            prevHelper.stopAlarm();
            db.alarmDao().deleteAlarm(prevAlarm);
        }
        String label = label_et.getText().toString();
        if (label.length() == 0)
            label = getResources().getString(R.string.no_label);
        Alarm alarm = new Alarm(alarmCalendar.getTimeInMillis(), snooze, repeat_count, repeatDays, true, toneUri, type, label);
        // store in alarm db
        db.alarmDao().addAlarm(alarm);
        AlarmHelper helper = new AlarmHelper(getContext(), alarm.getId());
        helper.setAlarm();
        //notifying user
        Utils.showSnackbar(getActivity(), getActivity().findViewById(android.R.id.content), "Alarm set at " + alarmText);
        Animatation.spin(setAlarm)
                .addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        HomeFragment fragment = new HomeFragment();
                        getFragmentManager().beginTransaction()
                                .hide(NewAlarmFragment.this)
                                .add(R.id.fragment_view, fragment)
                                .commit();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    private boolean validParams() {
        if (alarmCalendar == null) {
            Utils.showSnackbar(getActivity(), parent, "Please set alarm time");
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
        type = AlarmType.ARIHEMATIC;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.alarm_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_sp.setAdapter(adapter);
        type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = Constants.ALARM_TYPE[position];
                Log.i(TAG, "onItemSelected: alarm type " + type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = AlarmType.ARIHEMATIC;
            }

        });
    }

    public Calendar getAlarmCalendar() {
        Calendar alarmTimeCal = Calendar.getInstance();
        alarmTimeCal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        alarmTimeCal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        alarmTimeCal.set(Calendar.SECOND, 0);
        alarmTimeCal.set(Calendar.MILLISECOND, 0);
        return alarmTimeCal;
    }

    public void setupRepeat(View parent) {
        repeatDays = new boolean[7];
        repeat_cb = new CheckBox[7];
        /*for (int i = 0; i < 7; i++) {
            repeat_cb[i] = parent.findViewById(Constants.REPEAT_CHECKBOXES[i]);
            repeat_cb[i].setOnCheckedChangeListener(this);
        }*/
        repeat_sp = parent.findViewById(R.id.repeat_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.repeat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat_sp.setAdapter(adapter);
        repeat_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // None
                        repeatDays = new boolean[7];
                        repeat_count = 0;
                        break;
                    case 1: // Weekdays
                        repeatDays = new boolean[]{false, true, true, true, true, true, false};
                        repeat_count = 5;
                        break;
                    case 2: //Everyday
                        repeatDays = new boolean[]{true, true, true, true, true, true, true};
                        repeat_count = 7;
                        break;
                    case 3:
                        // show custom repeat selector dialog
                        showRepeatDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default None
                repeatDays = new boolean[7];
                repeat_count = 0;
            }

        });

    }

    private void showRepeatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View dialog = inflater.inflate(R.layout.dialog_custom_repeat, null);
        builder.setView(dialog)
                .setCancelable(true)
                .create()
                .show();
        for (int i = 0; i < 7; i++) {
            repeat_cb[i] = dialog.findViewById(Constants.REPEAT_CHECKBOXES[i]);
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
        Log.i(TAG, "repeat days " + repeatDays[0] + repeatDays[1] + repeatDays[2] + repeatDays[3] + repeatDays[4] + repeatDays[5] + repeatDays[6]);
    }

    public void loadPreviousAlarm(int alarmId) {
        if (getActivity() == null) {
            Log.d(TAG, "loadPreviousAlarm: empty context");
            return;
        }
        prevAlarm = AlarmDatabase.getInstance(getActivity()).alarmDao().getAlarm(alarmId);
        if (prevAlarm == null)
            return;
        updateAlarm = true;
        // alarm time
        alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTimeInMillis(prevAlarm.getAlarmTime());
        alarmTime_tv.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(prevAlarm.getAlarmTime()));
        // label
        label_et.setText(prevAlarm.getLabel());
        // repeat
        if (prevAlarm.getRepeatCount() > 0) {
            for (int i = 0; i < prevAlarm.getRepeatDays().length; i++) {
                repeat_cb[i].setChecked(prevAlarm.getRepeatDays()[i]);
            }
        }
        // alarm type
        type = prevAlarm.getType();
        int typePos = 0;
        for (int i = 0; i < Constants.ALARM_TYPE.length; i++) {
            if (Constants.ALARM_TYPE[i] == type) {
                typePos = i;
                break;
            }
        }
        type_sp.setSelection(typePos);
        // snooze time
        int snoozePos = 0;
        snooze = prevAlarm.getSnoozeDuration();
        for (int i = 0; i < getResources().getIntArray(R.array.snooze_time).length; i++) {
            if (Integer.valueOf(getResources().getStringArray(R.array.snooze_time)[i].split(" ")[0]) == snooze) {
                snoozePos = i;
                break;
            }
        }
        snooze_sp.setSelection(snoozePos);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        updateAlarm = false;
    }


}
