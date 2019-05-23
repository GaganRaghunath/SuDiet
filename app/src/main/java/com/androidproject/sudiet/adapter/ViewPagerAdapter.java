package com.androidproject.sudiet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidproject.sudiet.R;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private int[] img_array_picture={R.drawable.heart,R.drawable.bp_checker,R.drawable.apple};
    private int[] img_array_heading={R.string.intro_head_1,R.string.intro_head_2,R.string.intro_head_3};
    private int[] img_array_description={R.string.into_desc_1,R.string.into_desc_2,R.string.into_desc_3};
    public ViewPagerAdapter(Context context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return img_array_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slidelayout,container,false);
        ImageView imageView = view.findViewById(R.id.imageView_slider);
        TextView textView_1 = view.findViewById(R.id.slider_textView1);
        TextView textView_2 = view.findViewById(R.id.slider_textView2);

        imageView.setImageResource(img_array_picture[position]);
        textView_1.setText(img_array_heading[position]);
        textView_2.setText(img_array_description[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position,@NonNull Object object){
        container.removeView((RelativeLayout)object);
    }
}
