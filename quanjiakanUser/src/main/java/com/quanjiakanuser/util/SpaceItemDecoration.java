package com.quanjiakanuser.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Gin on 2016/12/2.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration{


        private int space;
        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
}
