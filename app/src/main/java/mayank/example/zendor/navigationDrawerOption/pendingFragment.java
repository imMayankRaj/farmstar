package mayank.example.zendor.navigationDrawerOption;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mayank.example.zendor.R;

import static mayank.example.zendor.MainActivity.showToast;
import static mayank.example.zendor.navigationDrawerOption.paymentRequest.header;


public class pendingFragment extends Fragment {

    private RecyclerView pendingRecyclerView;
    private LinearLayoutManager llm;

    public static String param = "request";
    private ArrayList list;
    private LinearLayout layout;
    private TextView textView;


    public pendingFragment(){

    }


    @SuppressLint("ValidFragment")
    public pendingFragment(ArrayList list) {
        this.list = list;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        pendingRecyclerView = view.findViewById(R.id.pendingRecyclerView);
        llm = new LinearLayoutManager(getActivity());
        pendingRecyclerView.setLayoutManager(llm);
        pendingRecyclerView.setHasFixedSize(true);

        layout = view.findViewById(R.id.noDataLayout);
        textView = view.findViewById(R.id.text);


        layout.setVisibility(View.GONE);


        if(list !=null) {
            if (list.size() == 0) {
                layout.setVisibility(View.VISIBLE);
                textView.setText("No Pending Request.");
            }
        }else
            Toast.makeText(getActivity(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();

        paymentRequestAdapter adapter = new paymentRequestAdapter(getActivity(), list);
        pendingRecyclerView.setAdapter(adapter);

        return view;
    }



}
