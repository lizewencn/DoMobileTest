package lizewen.sdj.com.domobiletest;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * created by Administrator
 * 2018/5/1
 * email：lizewencn@126.com
 * Desc:
 */
public class RateBean implements Serializable {

    private String currencyName;

    private BigDecimal rate;

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
