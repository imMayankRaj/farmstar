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

import static mayank.example.zendor.MainActivity.showToast;
import static mayank.example.zendor.navigationDrawerOption.paymentRequest.header;
import static mayank.example.zendor.navigationDrawerOption.sale.headerSale;

/**
 * A simple {@link Fragment} subclass.
 */
public class salePaymentReceived extends Fragment {


    ArrayList arrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private LinearLayout layout;
    private TextView textView;


    public salePaymentReceived(){

    }
    @SuppressLint("ValidFragment")
    public salePaymentReceived(ArrayList arrayList) {
        this.arrayList = arrayList;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale_payment_received, container, false);

        llm = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        layout = view.findViewById(R.id.noDataLayout);
        textView = view.findViewById(R.id.text);
        layout.setVisibility(View.GONE);


        if(arrayList.size() == 0 ){
            layout.setVisibility(View.VISIBLE);
            textView.setText("No Payment Received.");
        }


        saleAdapter adapter = new saleAdapter(getActivity(), arrayList, 0);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
