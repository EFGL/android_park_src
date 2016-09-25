package com.gz.gzcar.settingfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gz.gzcar.R;
import com.gz.gzcar.weight.MyPullText;

import java.util.ArrayList;

/**
 * Created by Endeavor on 2016/8/8.
 *
 * 收费规则
 */
public class MoneyFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_money,container,false);

        MyPullText temp = (MyPullText) v.findViewById(R.id.money_temp);// 临时
        MyPullText friends = (MyPullText) v.findViewById(R.id.money_friends);// 探亲

        ArrayList<String> templist = new ArrayList<>();
        templist.add("30");
        templist.add("60");
        templist.add("120");
        templist.add("180");
        temp.setPopList(templist);
        temp.setText(templist.get(0));

        ArrayList<String> friendlist = new ArrayList<>();
        friendlist.add("30");
        friendlist.add("60");
        friendlist.add("120");
        friendlist.add("180");
        friends.setPopList(friendlist);
        friends.setText(friendlist.get(1));
        return v;
    }
}
