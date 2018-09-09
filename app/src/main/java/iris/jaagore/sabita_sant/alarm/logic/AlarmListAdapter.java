package iris.jaagore.sabita_sant.alarm.logic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import iris.jaagore.sabita_sant.alarm.R;
import iris.jaagore.sabita_sant.alarm.backend.Alarm;
import iris.jaagore.sabita_sant.alarm.backend.AlarmDatabase;
import iris.jaagore.sabita_sant.alarm.utils.Constants;

/**
 * Created by Sud on 8/22/18.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmListViewHolder> {
    private List<Alarm> alarmDataset;
    SimpleDateFormat df;
    private AlarmDatabase db;

    public AlarmListAdapter(Context context) {
        db = AlarmDatabase.getInstance(context);
        this.alarmDataset= db.alarmDao().getAll();
        df = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
    }

    @Override
    public AlarmListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_alarm_list, parent, false);
        AlarmListViewHolder viewHolder = new AlarmListViewHolder(parent.getContext(), view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlarmListViewHolder holder, int position) {
        Alarm alarm = alarmDataset.get(position);
        holder.alarmText_tv.setText(df.format(alarm.getAlarmTime()));
        holder.alarmSwitch.setChecked(alarm.isActive());
        setRepeatDays(holder, alarm.getRepeatDays());
    }

    private void setRepeatDays(AlarmListViewHolder holder, boolean[] repeatDays) {
        for (int i = 0; i < 7; i++) {
            holder.repeat_cb[i].setChecked(repeatDays[i]);
        }
    }

    @Override
    public int getItemCount() {
        return alarmDataset.size();
    }

    public Alarm getItem(int postion){
        return alarmDataset.get(postion);
    }

    public void removeAlarm(int position) {
        db.alarmDao().deleteAlarm(getItem(position));
        alarmDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreAlarm(int position, Alarm alarm) {
        db.alarmDao().addAlarm(alarm);
        alarmDataset.add(position, alarm);
        notifyItemInserted(position);
    }

    public class AlarmListViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private static final String TAG = "AlarmListViewHolder";
        TextView alarmText_tv;
        Switch alarmSwitch;
        CheckBox repeat_cb[] = new CheckBox[7];
        View foreground, background, left_bg, right_bg;
        Context context;

        public AlarmListViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            alarmText_tv = itemView.findViewById(R.id.alarm_time);
            alarmSwitch = itemView.findViewById(R.id.alarm_switch);
            setupRepeat(itemView, repeat_cb);
            alarmSwitch.setOnCheckedChangeListener(this);
            foreground = itemView.findViewById(R.id.view_foreground);
            background = itemView.findViewById(R.id.view_background);
            left_bg = itemView.findViewById(R.id.left_background);
            right_bg = itemView.findViewById(R.id.right_background);
        }

        private void setupRepeat(View parent, CheckBox[] repeat_cb) {
            for (int i = 0; i < 7; i++) {
                repeat_cb[i] = parent.findViewById(Constants.REPEAT_CHECKBOXES[i]);
                repeat_cb[i].setOnCheckedChangeListener(this);
            }

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Alarm alarm = alarmDataset.get(getAdapterPosition());
            boolean[] repeatDays = alarm.getRepeatDays();
            int repeat_count = alarm.getRepeatCount();
            int id = compoundButton.getId();
            // for status change
            if (id == R.id.alarm_switch) {
                alarmSwitch.setChecked(b);
                alarm.setActive(b);
                AlarmHelper helper = new AlarmHelper(context, alarm.getId());
                helper.setStatus(b);
            }
            // for repeat change
            else {
                for (int i = 0; i < 7; i++) {
                    if (Constants.REPEAT_CHECKBOXES[i] == id) {
                        repeatDays[i] = b;
                        if (b)
                            repeat_count++;
                        else
                            repeat_count--;
                        break;
                    }
                }
                alarm.setRepeatCount(repeat_count);
                alarm.setRepeatDays(repeatDays);
            }
            AlarmDatabase.getInstance(context).alarmDao().updateAlarm(alarm);
            Log.i(TAG, "repeat days in alarm list " + repeatDays[0] + repeatDays[1] + repeatDays[2] + repeatDays[3] + repeatDays[4] + repeatDays[5] + repeatDays[6]);

        }
    }
}
