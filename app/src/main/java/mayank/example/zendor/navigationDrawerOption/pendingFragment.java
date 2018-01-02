package mayank.example.zendor.navigationDrawerOption;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mayank.example.zendor.R;


public class pendingFragment extends Fragment {

    private RecyclerView pendingRecyclerView;
    private LinearLayoutManager llm;

    public static String param = "request";
    private ArrayList<paymentRequest.requestClass> list;


    public pendingFragment() {

    }

    @SuppressLint("ValidFragment")
    public pendingFragment(ArrayList arrayList) {
        list = arrayList;
    }
    public static pendingFragment newInstance(ArrayList arrayList) {
        pendingFragment fragment = new pendingFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(param, arrayList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        pendingRecyclerView = view.findViewById(R.id.pendingRecyclerView);
        llm = new LinearLayoutManager(getActivity());
        pendingRecyclerView.setLayoutManager(llm);
        pendingRecyclerView.setHasFixedSize(true);

        paymentRequestAdapter adapter = new paymentRequestAdapter(getActivity(), list);
        pendingRecyclerView.setAdapter(adapter);

        return view;
    }



}
