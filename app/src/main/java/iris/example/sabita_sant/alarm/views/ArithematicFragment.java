package iris.example.sabita_sant.alarm.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.controller.AlarmMethod;
import iris.example.sabita_sant.alarm.controller.ArithmeticHelper;


public class ArithematicFragment extends Fragment implements AlarmMethod {

    private TextView op1, op2, operator;
    private EditText res;
    private ArithmeticHelper helper;

    public ArithematicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_arithematic, container, false);
        op1 = parent.findViewById(R.id.operand1);
        op2 = parent.findViewById(R.id.operand2);
        operator = parent.findViewById(R.id.operator);
        res = parent.findViewById(R.id.res);
        helper = new ArithmeticHelper();
        populate();
        return  parent;
    }

    @Override
    public void populate() {
        op1.setText(String.valueOf(helper.getNum1()));
        op2.setText(String.valueOf(helper.getNum2()));
        operator.setText(String.valueOf(helper.getOperator()));
        res.setText("");

    }

    @Override
    public boolean isValidResponse() {
        String result[]=res.getText().toString().split("\\.");
        return String.valueOf(helper.getResult()).equals(result[0]);
    }
}
