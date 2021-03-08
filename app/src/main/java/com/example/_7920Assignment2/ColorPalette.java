package com.example._7920Assignment2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/*Color palette class for populating color picker on drawing screen*/
public class ColorPalette {

    //get all colors for color pallette
    public static ArrayList HSVColors() {
        ArrayList<Integer> colors = new ArrayList<>();

        for (int h = 0; h <= 360; h += 20) {
            colors.add(HSVColor(h, 1, 1));
        }

        for (int h = 0; h <= 360; h += 20) {
            colors.add(HSVColor(h, .25f, 1));
            colors.add(HSVColor(h, .5f, 1));
            colors.add(HSVColor(h, .75f, 1));
        }
        for (float b = 0; b <= 1; b += .10f) {
            colors.add(HSVColor(0, 0, b));
        }
        for (int h = 0; h <= 360; h += 20) {
            //colors.add(createColor(h, 1, .25f));
            colors.add(HSVColor(h, 1, .5f));
            colors.add(HSVColor(h, 1, .75f));
        }

        return colors;
    }

    // get color from android
    public static int HSVColor(float hue, float saturation, float black) {
        int color = Color.HSVToColor(255, new float[]{hue, saturation, black});
        return color;
    }

    //color list adapter to bind listview containing all colors
    public static class ColorListAdapter extends ArrayAdapter<Integer> {

        public ColorListAdapter(Activity context, ArrayList<Integer> colors) {
            super(context, 0, colors);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;

            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.colors_list, parent, false);
            }
            int color = getItem(position);
            ImageView colorImage =  listItemView.findViewById(R.id.color);
            colorImage.setBackgroundColor(color);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, 40);
            colorImage.setLayoutParams(layoutParams);
            return listItemView;
        }
    }
}
