package com.muhammadalikarami.smartplug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.muhammadalikarami.smartplug.models.AlarmStatus;
import com.muhammadalikarami.smartplug.objects.Plug;
import com.muhammadalikarami.smartplug.utility.CustomTextView;

import java.util.ArrayList;

/**
 * Created by moden pal on 8/30/2015.
 */
public class AdapterPlugs extends ArrayAdapter<Plug> {

    private Context context;
    private FragmentControlPlugs fragment;
    private ArrayList<Plug> plugs;

    public AdapterPlugs(Context context, FragmentControlPlugs fragment, ArrayList<Plug> plugs) {
        super(context, R.layout.row_plug, plugs);

        this.context = context;
        this.fragment = fragment;
        this.plugs = plugs;

    }

    public class ViewHolder {
        RelativeLayout rlRow ;
        ImageView imgPlug ;
        ImageView imgPower ;
        CustomTextView txtName ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder ;
        final Plug curItem = getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_plug, parent, false);

            holder.rlRow = (RelativeLayout) convertView.findViewById(R.id.rlRow);
            holder.imgPlug = (ImageView) convertView.findViewById(R.id.imgPlug);
            holder.imgPower = (ImageView) convertView.findViewById(R.id.imgPower);
            holder.txtName = (CustomTextView) convertView.findViewById(R.id.txtName);

            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        // set objects
        holder.txtName.setText(curItem.getPlugName());
        if (curItem.getPlugStatus().equals(AlarmStatus.ON)) {
            holder.txtName.setTextColor(context.getResources().getColor(R.color.main_blue));
            holder.imgPower.setImageResource(R.drawable.img_power_on);
            holder.imgPlug.setImageResource(R.drawable.img_plug_on);
        }
        else {
            holder.txtName.setTextColor(context.getResources().getColor(R.color.main_black));
            holder.imgPower.setImageResource(R.drawable.img_power_off);
            holder.imgPlug.setImageResource(R.drawable.img_plug_off);
        }

        // set listeners
        holder.imgPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curItem.getPlugStatus().equals(AlarmStatus.ON)) {
                    fragment.powerRequest(position, curItem.getPlugNum(), AlarmStatus.OFF);
                }
                else {
                    fragment.powerRequest(position, curItem.getPlugNum(), AlarmStatus.ON);
                }
            }
        });


        return convertView;
    }
}
