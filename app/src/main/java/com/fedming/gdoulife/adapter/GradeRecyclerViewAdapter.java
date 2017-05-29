package com.fedming.gdoulife.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fedming.gdoulife.R;

import java.util.Map;

/**
 * Created by Bruce on 2016/11/06.
 */
public class GradeRecyclerViewAdapter extends BaseRecyclerViewAdapter<Map<String, String>> {

    private BaseRecycleViewHolderView.MyItemClickListener myItemClickListener;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new HolderView(LayoutInflater.from(parent.getContext()).inflate(R.layout.grade_item, parent, false), myItemClickListener);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int position, Map<String, String> data) {

        if (!(viewHolder instanceof HolderView)) {
            return;
        }

        ((HolderView) viewHolder).yearTextView.setText(data.get("0"));
        ((HolderView) viewHolder).termTextView.setText(data.get("1"));
        ((HolderView) viewHolder).courseNameTextView.setText(data.get("3"));
        ((HolderView) viewHolder).creditTextView.setText(data.get("6"));
        ((HolderView) viewHolder).gradePointTextView.setText(data.get("7"));
        ((HolderView) viewHolder).gradeTextView.setText(data.get("8"));
        ((HolderView) viewHolder).bkGradeTextView.setText(data.get("10"));
        ((HolderView) viewHolder).cxGradeTextView.setText(data.get("11"));

    }

    @Override
    public void setItemClickListener(BaseRecycleViewHolderView.MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    private class HolderView extends BaseRecycleViewHolderView {

        private TextView courseNameTextView;
        private TextView yearTextView;
        private TextView termTextView;
        private TextView creditTextView;
        private TextView gradePointTextView;
        private TextView gradeTextView;
        private TextView bkGradeTextView;
        private TextView cxGradeTextView;

        HolderView(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView, myItemClickListener);

            courseNameTextView = (TextView) itemView.findViewById(R.id.course_name_textView);
            yearTextView = (TextView) itemView.findViewById(R.id.year_textView);
            termTextView = (TextView) itemView.findViewById(R.id.term_textView);
            creditTextView = (TextView) itemView.findViewById(R.id.credit_textView);
            gradePointTextView = (TextView) itemView.findViewById(R.id.grade_point_textView);
            gradeTextView = (TextView) itemView.findViewById(R.id.grade_textView);
            bkGradeTextView = (TextView) itemView.findViewById(R.id.bk_grade_textView);
            cxGradeTextView = (TextView) itemView.findViewById(R.id.cx_grade_textView);

        }
    }
}
