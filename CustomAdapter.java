package com.myapp.thiranja.showwifipassword;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class CustomAdapter extends ArrayAdapter<WifiDetail>{

    private ArrayList<WifiDetail> dataSet;
    private ArrayList<WifiDetail> rawDataSet = new ArrayList<>();
    private Context mContext;

    CustomAdapter(ArrayList<WifiDetail> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.rawDataSet.addAll(data);
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WifiDetail dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtSsid = convertView.findViewById(R.id.ssid);
            viewHolder.txtPsk = convertView.findViewById(R.id.psk);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtSsid.setText(dataModel.getSsid());
        viewHolder.txtPsk.setText(dataModel.getPsk());

        // Return the completed view to render on screen
        return convertView;
    }

    private int lastPosition = -1;

    void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        dataSet.clear();
        if (charText.length() == 0){
            dataSet.addAll(rawDataSet);
        }else{
            for (WifiDetail dataModel : rawDataSet){
                if (dataModel.getSsid().toLowerCase(Locale.getDefault()).contains(charText)){
                    dataSet.add(dataModel);
                }
            }
        }
        notifyDataSetChanged();
    }

    void setRawDataSet(){
        this.rawDataSet.clear();
        this.rawDataSet.addAll(dataSet);
    }

    // View lookup cache
    static class ViewHolder {
        TextView txtSsid;
        TextView txtPsk;
    }
}

