package iris.example.sabita_sant.alarm.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.controller.AlarmListAdapter;
import iris.example.sabita_sant.alarm.controller.RecyclerItemTouchHelper;
import iris.example.sabita_sant.alarm.models.Alarm;
import iris.example.sabita_sant.alarm.utils.Utils;


public class HomeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final int PERMISSION_REQUEST_CODE = 50;
    private AlarmListAdapter adapter;
    private View parent;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_home, container, false);
        adapter = new AlarmListAdapter(getActivity());
        RecyclerView alarmList = parent.findViewById(R.id.alarm_list);
        alarmList.setHasFixedSize(true);
        alarmList.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmList.setAdapter(adapter);
        // swipe action
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(alarmList);
        return parent;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof AlarmListAdapter.AlarmListViewHolder) {
            int position = viewHolder.getAdapterPosition();
            // get the removed item name to display it in snack bar
            String name = adapter.getItem(position).getLabel();

            // backup of removed item for undo purpose
            final Alarm deletedAlarm = adapter.getItem(position);
            final int deletedIndex = position;

            // remove the item from recycler view
            adapter.removeAlarm(position);

            // showing snack bar with Undo option
            Snackbar snackbar = Utils.showSnackbar(getActivity(), parent, "Alarm removed from list");
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restoreAlarm(deletedIndex, deletedAlarm);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public void updateAlarmList(Alarm alarm) {
        adapter.addNewAlarm(alarm);
    }
}
