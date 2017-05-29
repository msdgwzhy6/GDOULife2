package com.fedming.gdoulife.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fedming.gdoulife.R;
import com.fedming.gdoulife.activity.GradeSearchActivity;
import com.fedming.gdoulife.activity.LifeInfoActivity;

/**
 * 更多
 * Created by Bruce on 2016/9/9.
 */
public class MoreFragment extends BaseFragment implements View.OnClickListener{

    private View moreView;
//    private RelativeLayout myCourseRelativeLayout;
    private RelativeLayout myGradeRelativeLayout;
    private RelativeLayout lifeOneRelativeLayout;
    private RelativeLayout lifeTwoRelativeLayout;
    private RelativeLayout lifeThreeRelativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity();
        moreView = inflater.inflate(R.layout.fragment_more,container,false);
        initView();

        return moreView;

    }

    private void initView(){

//        myCourseRelativeLayout = (RelativeLayout) moreView.findViewById(R.id.my_course_relativeLayout);
        myGradeRelativeLayout = (RelativeLayout) moreView.findViewById(R.id.my_grade_relativeLayout);
        lifeOneRelativeLayout = (RelativeLayout) moreView.findViewById(R.id.life_one_relativeLayout);
        lifeTwoRelativeLayout = (RelativeLayout) moreView.findViewById(R.id.life_two_relativeLayout);
        lifeThreeRelativeLayout = (RelativeLayout) moreView.findViewById(R.id.life_three_relativeLayout);

//        myCourseRelativeLayout.setOnClickListener(this);
        myGradeRelativeLayout.setOnClickListener(this);
        lifeOneRelativeLayout.setOnClickListener(this);
        lifeTwoRelativeLayout.setOnClickListener(this);
        lifeThreeRelativeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.my_grade_relativeLayout:
                startActivity(new Intent(mContext, GradeSearchActivity.class));
                break;
            case R.id.life_one_relativeLayout:
                startActivity(new Intent(mContext, LifeInfoActivity.class).putExtra("flag",0));
                break;
            case R.id.life_two_relativeLayout:
                startActivity(new Intent(mContext, LifeInfoActivity.class).putExtra("flag",1));
                break;
            case R.id.life_three_relativeLayout:
                startActivity(new Intent(mContext, LifeInfoActivity.class).putExtra("flag",2));
                break;
            default:
                break;
        }
    }
}
