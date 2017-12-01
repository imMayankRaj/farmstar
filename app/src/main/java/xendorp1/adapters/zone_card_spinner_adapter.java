package xendorp1.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



import java.util.List;

import mayank.example.zendor.R;
import xendorp1.cards.zone_card;

/**
 * Created by GOTHAM on 29-10-2017.
 */

public class zone_card_spinner_adapter extends ArrayAdapter{
    private Context context;
    private int layout_resource_id;
    private List<zone_card> zone_cardList;
    private LayoutInflater inflater;
    public zone_card_spinner_adapter(Context context, int layout_resource_id, List<zone_card> zone_cardList,LayoutInflater inflater)
    {
        super(context,layout_resource_id,zone_cardList);
        this.context=context;
        this.layout_resource_id=layout_resource_id;
        this.zone_cardList=zone_cardList;
        this.inflater=inflater;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position==0) {
            TextView zone_name = new TextView(context);
            zone_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            return zone_name;
        }
        else {
            TextView zone_name = new TextView(context);
            zone_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            zone_name.setText(zone_cardList.get(position).getZone_name());
            return zone_name;
        }
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(position==0) {
            TextView zone_name = new TextView(context);
            zone_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            return zone_name;
        }
        else {
            View row = inflater.inflate(layout_resource_id, parent,
                    false);
            TextView zone_name = row.findViewById(R.id.zone_name);
            TextView zone_id = row.findViewById(R.id.zone_id);
            zone_name.setText(zone_cardList.get(position).getZone_name());
            zone_id.setText(zone_cardList.get(position).getZone_id());
            return row;
        }
    }

}
