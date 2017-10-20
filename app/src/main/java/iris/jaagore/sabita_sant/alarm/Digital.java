package iris.jaagore.sabita_sant.alarm;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sabita_Sant on 08/10/16.
 */
public class Digital extends Fragment {

  public Digital(){

  }
  @Override

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    return inflater.inflate(R.layout.digital_clk,container,false);
  }
}
