package lizewen.sdj.com.domobiletest;

import java.io.Serializable;

/**
 * created by Administrator
 * 2018/5/1
 * email：lizewencn@126.com
 * Desc:
 */
public class RateBean implements Serializable {

    private String currencyName;

    private float rate;

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
