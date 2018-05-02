package lizewen.sdj.com.domobiletest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * created by Administrator
 * 2018/5/1
 * email：lizewencn@126.com
 * Desc:
 */
public class RateBean implements Serializable {

    private String currencyName;

    private BigDecimal rate;

    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
//        String formatAmount = formatAmount(amount);
        this.amount = amount;
//        this.amount = new BigDecimal(formatAmount);
    }
    private String formatAmount(BigDecimal amount){
        DecimalFormat format = new DecimalFormat("#.####");
        return format.format(amount);
    }
}
