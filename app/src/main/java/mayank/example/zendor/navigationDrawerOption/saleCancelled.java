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

import java.util.ArrayList;

import mayank.example.zendor.R;
import mayank.example.zendor.onClickBuyer.saleAdapter;


@SuppressLint("ValidFragment")
public class saleCancelled extends Fragment {
    // TODO: Rename and change types of parameters
    ArrayList arrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private LinearLayout layout;
    private TextView textView;


    public saleCancelled(ArrayList arrayList) {
       this.arrayList = arrayList;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank2, container, false);


        llm = new LinearLayoutManager(getActivity());
        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        layout = v.findViewById(R.id.noDataLayout);
        textView = v.findViewById(R.id.text);
        layout.setVisibility(View.GONE);


        if(arrayList.size() == 0 ){
            layout.setVisibility(View.VISIBLE);
            textView.setText("No Sales Cancelled.");
        }


        saleAdapter adapter = new saleAdapter(getActivity(), arrayList, 0);
        recyclerView.setAdapter(adapter);

        return v;
    }

}
