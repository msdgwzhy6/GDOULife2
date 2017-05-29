package com.fedming.gdoulife.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fedming.gdoulife.R;

import java.util.Map;

/**
 * Created by Bruce on 2016/10/15.
 */
public class BooksRecyclerViewAdapter extends BaseRecyclerViewAdapter<Map<String, String>> {

    private BaseRecycleViewHolderView.MyItemClickListener myItemClickListener;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new HolderView(LayoutInflater.from(parent.getContext()).inflate(R.layout.book_search_result_item, parent, false), myItemClickListener);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int position, Map<String, String> data) {

        if (!(viewHolder instanceof HolderView)) {
            return;
        }

        ((HolderView) viewHolder).bookNameTextView.setText(data.get("bookName"));
        ((HolderView) viewHolder).bookInfoTextView.setText(data.get("bookInfo"));
        ((HolderView) viewHolder).bookInfo1TextView.setText(data.get("bookInfo1"));
        ((HolderView) viewHolder).bookInfo2TextView.setText(data.get("bookInfo2"));

    }

    @Override
    public void setItemClickListener(BaseRecycleViewHolderView.MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    private class HolderView extends BaseRecycleViewHolderView {

        private TextView bookNameTextView;
        private TextView bookInfoTextView;
        private TextView bookInfo1TextView;
        private TextView bookInfo2TextView;

        HolderView(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView, myItemClickListener);

            bookNameTextView = (TextView) itemView.findViewById(R.id.search_result_book_name_textView);
            bookInfoTextView = (TextView) itemView.findViewById(R.id.search_result_book_info_textView);
            bookInfo1TextView = (TextView) itemView.findViewById(R.id.search_result_book_info1_textView);
            bookInfo2TextView = (TextView) itemView.findViewById(R.id.search_result_book_info2_textView);

        }
    }
}
