package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*Color palette class for populating color picker on drawing screen*/
public class ColorPalette {

    //get all colors for color pallette
    public static ArrayList HSVColors() {
        ArrayList<Integer> colors = new ArrayList<>();

        // Loop through hue channel, saturation and light full
        for (int h = 0; h <= 360; h += 20) {
            colors.add(HSVColor(h, 1, 1));
        }

        // Loop through hue channel, different saturation and light full
        for (int h = 0; h <= 360; h += 20) {
            colors.add(HSVColor(h, .25f, 1));
            colors.add(HSVColor(h, .5f, 1));
            colors.add(HSVColor(h, .75f, 1));
        }
        // Loop through the light channel, no hue no saturation
        // It will generate gray colors
        for (float b = 0; b <= 1; b += .10f) {
            colors.add(HSVColor(0, 0, b));
        }
        // Loop through hue channel, saturation full and light different
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

    //bind colors to gridview adapter
    public static android.widget.ListAdapter Create(Context context, int resource) {
        // Get the ArrayList of HSV colors
        final ArrayList colors = HSVColors();

        // Create an ArrayAdapter using colors list
        ArrayAdapter<Integer> ad = new ArrayAdapter<Integer>(context, resource, colors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                int currentColor = (int) colors.get(position);
                view.setBackgroundColor(currentColor);
                view.setText("");
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT
                );
                view.setLayoutParams(lp);
                AbsListView.LayoutParams params = (AbsListView.LayoutParams) view.getLayoutParams();
                params.width = 40;
                params.height = 40;
                view.setLayoutParams(params);
                view.requestLayout();
                return view;
            }
        };
        return ad;
    }


}
