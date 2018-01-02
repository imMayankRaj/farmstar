package mayank.example.zendor.navigationDrawerOption;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mayank.example.zendor.R;


public class processedFragment extends Fragment {

    private RecyclerView processedRecyclerView;
    private LinearLayoutManager llm;

    public static String param = "request";
    private ArrayList<paymentRequest.requestClass> list;


    public processedFragment() {

    }

    @SuppressLint("ValidFragment")
    public processedFragment(ArrayList list) {

        this.list = list;
    }

    public static processedFragment newInstance(ArrayList arrayList) {
        processedFragment fragment = new processedFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_processed, container, false);

        processedRecyclerView = view.findViewById(R.id.processedRecyclerView);
        llm = new LinearLayoutManager(getActivity());
        processedRecyclerView.setLayoutManager(llm);
        processedRecyclerView.setHasFixedSize(true);

        paymentRequestAdapter adapter = new paymentRequestAdapter(getActivity(), list);
        processedRecyclerView.setAdapter(adapter);


        return view;
    }


}
