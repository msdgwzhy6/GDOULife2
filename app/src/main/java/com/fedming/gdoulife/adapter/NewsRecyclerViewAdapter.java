package com.fedming.gdoulife.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fedming.gdoulife.R;
import com.fedming.gdoulife.utils.StringUtils;

import java.util.Map;

/**
 * Created by Bruce on 2016/9/12.
 */
public class NewsRecyclerViewAdapter extends BaseRecyclerViewAdapter<Map<String, String>> {

    private BaseRecycleViewHolderView.MyItemClickListener myItemClickListener;
    private Context context;
    private String TAG = "fdm";

    public NewsRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new HolderView(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_news_item, parent, false), myItemClickListener);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int position, Map<String, String> data) {

        if (!(viewHolder instanceof HolderView)) {
            return;
        }
        ((HolderView) viewHolder).fragmentNewsItemTitleTextView.setText(data.get("title"));
        ((HolderView) viewHolder).fragmentNewsItemDateTextView.setText(String.format("时间：%s", data.get("date")));
        ((HolderView) viewHolder).fragmentNewsItemReadCountTextView.setText(String.format("阅读：%s", data.get("number")));

        if (StringUtils.isFine(data.get("imgLink"))) {
            ((HolderView) viewHolder).fragmentNewsItemImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(data.get("imgLink"))
                    .thumbnail(0.1f)
                    .centerCrop()
                    .into(((HolderView) viewHolder).fragmentNewsItemImageView);
        } else {
            ((HolderView) viewHolder).fragmentNewsItemImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setItemClickListener(BaseRecycleViewHolderView.MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    private class HolderView extends BaseRecycleViewHolderView {

        private ImageView fragmentNewsItemImageView;
        private TextView fragmentNewsItemTitleTextView;
        private TextView fragmentNewsItemReadCountTextView;
        private TextView fragmentNewsItemDateTextView;

        HolderView(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView, myItemClickListener);

            fragmentNewsItemImageView = (ImageView) itemView.findViewById(R.id.fragment_news_item_imageView);
            fragmentNewsItemTitleTextView = (TextView) itemView.findViewById(R.id.fragment_news_item_title_textView);
            fragmentNewsItemReadCountTextView = (TextView) itemView.findViewById(R.id.fragment_news_item_read_count_textView);
            fragmentNewsItemDateTextView = (TextView) itemView.findViewById(R.id.fragment_news_item_date_textView);

        }
    }
}
