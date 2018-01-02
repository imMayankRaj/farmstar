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


public class rejectedFragment extends Fragment {

    private RecyclerView rejectedRecyclerView;
    private LinearLayoutManager llm;

    public static String param = "request";
    private ArrayList<paymentRequest.requestClass> list;


    public rejectedFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public rejectedFragment(ArrayList list) {
        // Required empty public constructor
        this.list = list;
    }


    public static rejectedFragment newInstance(ArrayList arrayList) {
        rejectedFragment fragment = new rejectedFragment();
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
        View view = inflater.inflate(R.layout.fragment_rejected, container, false);

        rejectedRecyclerView = view.findViewById(R.id.rejectedRecyclerView);
        llm = new LinearLayoutManager(getActivity());
        rejectedRecyclerView.setLayoutManager(llm);
        rejectedRecyclerView.setHasFixedSize(true);


        paymentRequestAdapter adapter = new paymentRequestAdapter(getActivity(), list);
        rejectedRecyclerView.setAdapter(adapter);


        return view;
    }


}
