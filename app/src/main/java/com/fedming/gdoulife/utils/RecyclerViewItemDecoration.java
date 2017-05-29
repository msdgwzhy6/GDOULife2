package com.fedming.gdoulife.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int count;
    private int type;
    private int horizontalSpace;
    private boolean isHorizontal = false;
    private boolean isHeaderView;
    private String direction = "";

    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";

    public RecyclerViewItemDecoration(int space) {
        this.space = space;
    }

    public RecyclerViewItemDecoration(int space, String direction) {

        this.space = space;
        this.direction = direction;
    }

    public RecyclerViewItemDecoration(int space, int count) {

        this.space = space;
        this.count = count;
    }

    public RecyclerViewItemDecoration(int space, int count, boolean isHorizontal, boolean isHeaderView) {

        this.space = space;
        this.count = count;
        this.isHorizontal = isHorizontal;
        this.isHeaderView = isHeaderView;
        type = 0;
    }

    /**
     * @param space           垂直间距
     * @param count           一行两个Item 的情况
     * @param horizontalSpace 水平间距
     */
    public RecyclerViewItemDecoration(int space, int count, int horizontalSpace) {

        this.space = space;
        this.count = count;
        this.horizontalSpace = horizontalSpace;
        type = 1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (count > 0) {

            switch (type) {

                case 0:

                    // 判断是否要设置水平间距
                    if (isHorizontal) {
                        outRect.left = space;
                    }
                    outRect.bottom = space;
                    //由于每行都只有count个，所以第一个都是count的倍数，把左边距设为0

                    if (!isHeaderView && parent.getChildLayoutPosition(view) > 0) {

                        if (parent.getChildLayoutPosition(view) % count == 0) {
                            outRect.left = 0;
                        }
                    } else {

                        if (parent.getChildLayoutPosition(view) - 1 % count == 0) {
                            outRect.left = 0;
                        }
                    }

                    break;

                case 1:

                    if (parent.getChildLayoutPosition(view) % count == 0) {

                        outRect.left = horizontalSpace;
                        outRect.right = horizontalSpace / 2;
                    } else {

                        outRect.left = horizontalSpace / 2;
                        outRect.right = horizontalSpace;
                    }
                    outRect.bottom = space;

                    break;
            }
        } else {

            switch (direction) {

                case BOTTOM:
                    outRect.bottom = space;
                    break;

                case LEFT:
                    outRect.left = space;
                    break;

                case RIGHT:
                    outRect.right = space;
                    break;

                case TOP:
                default:

                    if (parent.getChildPosition(view) != 0) {
                        outRect.top = space;
                    }

                    break;
            }
        }
    }
}
