package com.gz.gzcar.weight;


import android.content.Context;
import android.view.View;

import com.flyco.dialog.widget.popup.BasePopup;
import com.gz.gzcar.R;

/**
 * Created by Endeavor on 2016/8/11.
 */

public class MyPop extends BasePopup<MyPop> {
    public MyPop(Context context) {
        super(context);
    }

    @Override
    public View onCreatePopupView() {
        return View.inflate(mContext, R.layout.activity_user_add, null);
//        View inflate = View.inflate(mContext, R.layout.popup_bubble_image, null);
//        mTvBubble = (TextView) inflate.findViewById(R.id.tv_bubble);
//        mIvBubble = (ImageView) inflate.findViewById(R.id.iv_bubble);
//        return inflate;
    }

    @Override
    public void setUiBeforShow() {
//        mTvBubble.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                T.showShort(mContext, "mTvBubble--->");
//            }
//        });
//        mIvBubble.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                T.showShort(mContext, "mIvBubble--->");
//            }
//        });
    }
}
