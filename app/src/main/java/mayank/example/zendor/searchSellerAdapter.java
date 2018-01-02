package mayank.example.zendor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mayank on 12/11/2017.
 */

public class searchSellerAdapter extends ArrayAdapter {

    ArrayList<MainActivity.searchClass> arrayList;
    Context context;

    public searchSellerAdapter(@NonNull Context context, @NonNull ArrayList<MainActivity.searchClass> arrayList) {
        super(context, R.layout.search_seller, arrayList);
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MainActivity.searchClass n = arrayList.get(position);
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.search_seller, parent, false);
        }

        TextView name = view.findViewById(R.id.sellerName);
        TextView number = view.findViewById(R.id.number);
        TextView sid = view.findViewById(R.id.sellerId);
        number.setText(n.getNumber());
        name.setText(n.getName());
        sid.setText(n.getId());

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }


    Filter filter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            MainActivity.searchClass sr = (MainActivity.searchClass) resultValue;
            return sr.getNumber();

        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            arrayList = MainActivity.searchList1;
            Log.e("size is", arrayList.size()+"");
            Log.e("qwertyuiop",constraint+"");
            FilterResults filterResults = new FilterResults();
            ArrayList<MainActivity.searchClass> tempList = new ArrayList<>();
            if(constraint != null && arrayList != null){
                int length = arrayList.size();
                int i = 0;
                while(i < length){

                    MainActivity.searchClass item = arrayList.get(i);
                    MainActivity.searchClass current = arrayList.get(i);

                    String[] number = item.getNumber().split(",");

                    for(int j =0;j<number.length;j++){
                        if(number[j].contains(constraint)) {
                            tempList.add(current);
                        }
                    }

                    i++;
                }
                filterResults.values = tempList;
                filterResults.count = tempList.size();

            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            ArrayList<MainActivity.searchClass> list = (ArrayList<MainActivity.searchClass>) results.values;
            if(results.count >0){
                clear();
                for (MainActivity.searchClass cust : list) {
                    add(cust);
                }
                notifyDataSetChanged();
            }else {
                notifyDataSetInvalidated();
            }
        }
    };
}
