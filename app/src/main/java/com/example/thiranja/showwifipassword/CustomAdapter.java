package com.example.thiranja.showwifipassword;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<WifiDetail> implements View.OnClickListener{

    private ArrayList<WifiDetail> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtSsid;
        TextView txtType;
        TextView txtPsk;
    }

    public CustomAdapter(ArrayList<WifiDetail> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        WifiDetail dataModel=(WifiDetail) object;

        /*switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Priority " +dataModel.getPriority(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }*/
    }

    private int lastPosition = -1;

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
            viewHolder.txtSsid = (TextView) convertView.findViewById(R.id.ssid);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtPsk = (TextView) convertView.findViewById(R.id.psk);

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
        viewHolder.txtType.setText(dataModel.getType());
        viewHolder.txtPsk.setText(dataModel.getPsk());

        // Return the completed view to render on screen
        return convertView;
    }
}

