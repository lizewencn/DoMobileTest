package lizewen.sdj.com.domobiletest;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Comparator;

/**
 * created by Administrator
 * 2018/5/3
 * email：lizewencn@126.com
 * Desc:
 */
public class MyComparator implements Comparator<RateBean>{

    private Context context;
    MyComparator(Context context){
        this.context = context;
    }
    @Override
    public int compare(RateBean o1, RateBean o2) {
        SharedPreferences preferences = context.getSharedPreferences("custom_position", Context.MODE_PRIVATE);
        int anInt1 = preferences.getInt(o1.getCurrencyName(), -1);
        int anInt2 = preferences.getInt(o2.getCurrencyName(), -1);
        return anInt1-anInt2;
    }

}
