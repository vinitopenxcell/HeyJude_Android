package com.heyjude.androidapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heyjude.androidapp.R;

/**
 * Created by dipen on 1/10/15.
 */
public class PagerIndicatorAdapter extends PagerAdapter {

    // Declare Variables
    private Context context;
    private String[] content;
    private String[] title;
    private LayoutInflater inflater;
    private TextView tvTitle, tvContent;
    private ImageView ivIconSearch;

    public PagerIndicatorAdapter(Context context, String[] content, String[] title) {
        this.context = context;
        this.content = content;
        this.title = title;
    }


    @Override
    public int getCount() {
        return content.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager_item, container, false);

        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        ivIconSearch = (ImageView) itemView.findViewById(R.id.ivIconSearch);

        // Capture position and set to the TextViews
        tvContent.setText(content[position]);

        /*if (!title[position].equals("")) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title[position]);
        }

        if (flag[position] == 1) {
            ivIconSearch.setVisibility(View.VISIBLE);
        }*/

        switch (position) {
            case 0:
                tvTitle.setVisibility(View.VISIBLE);
                ivIconSearch.setVisibility(View.VISIBLE);

                tvTitle.setText(title[position]);
                break;
            case 1:
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title[position]);
                break;
        }

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}

