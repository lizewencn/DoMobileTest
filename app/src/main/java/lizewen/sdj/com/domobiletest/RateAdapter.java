package lizewen.sdj.com.domobiletest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * created by Administrator
 * 2018/5/1
 * email：lizewencn@126.com
 * Desc:
 */
public class RateAdapter extends RecyclerView.Adapter<ViewHolder> implements ItemTouchHelperListener {

    private List<RateBean> rateBeanList;

    private Context context;
    //操作的位置
    private int actionPosition = -1;

    private DecimalFormat format;

    public RateAdapter(List<RateBean> rateBeanList, Context context) {
        this.rateBeanList = rateBeanList;
        this.context = context;
        format = new DecimalFormat(",###.0000");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RateBean rateBean = rateBeanList.get(position);
        holder.textView.setText(rateBean.getCurrencyName());
        String amount = rateBean.getAmount().toString();
        holder.editText.setText(amount);
        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    actionPosition = position;
                    Log.e("onFocusChange", "actionPosition = " + actionPosition + "position = " + position);

                    holder.editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            final int adapterPosition = holder.getAdapterPosition();
                            if (!holder.editText.hasFocus()) return;
                            String string = s.toString();
                            if (TextUtils.isEmpty(string)) {
                                string = "0";
                            }
                            if (string.endsWith(".")) string += "0";

                            BigDecimal bigDecimal = new BigDecimal(string);
                            rateBeanList.get(adapterPosition).setAmount(bigDecimal);

                            calculate(adapterPosition, bigDecimal);
                            notifyItemRangeChanged(0, adapterPosition);
                            notifyItemRangeChanged(adapterPosition + 1, getItemCount());

                        }
                    });
                } else {
                    Log.e("onFocusChange", "lost focu");
                    actionPosition = -1;
                }
            }

        });
    }

    /**
     * 金额换算
     *
     * @param position
     * @param bigDecimal
     */
    private void calculate(int position, BigDecimal bigDecimal) {
        //先换算成欧元
        BigDecimal rate = rateBeanList.get(position).getRate();
        BigDecimal euro = bigDecimal.divide(rate, 4, BigDecimal.ROUND_HALF_UP);
        for (int i = 0; i < getItemCount(); i++) {
            RateBean rateBean = rateBeanList.get(i);
//            if (i == position) {
//                continue;
//            }
            BigDecimal multiply = euro.multiply(rateBean.getRate());
            rateBean.setAmount(multiply);


        }
    }


    @Override
    public int getItemCount() {

        return rateBeanList == null ? 0 : rateBeanList.size();
    }

    public List<RateBean> getRateBeanList() {
        return rateBeanList;
    }

    @Override
    public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < rateBeanList.size() && toPosition < rateBeanList.size()) {
            //交换数据位置
            Collections.swap(rateBeanList, fromPosition, toPosition);
            //刷新位置交换
            notifyItemMoved(fromPosition, toPosition);
        }
        CharSequence text = ((ViewHolder) source).textView.getText();
        Log.e("swap", "" + text.toString() + "fromPosition=" + fromPosition + ",toPosition" + toPosition);
        for (int i = 0; i <rateBeanList.size() ; i++) {
            savePositionLocal(rateBeanList.get(i).getCurrencyName(),i);
        }
        //移动过程中移除view的放大效果
        onItemClear(source);
    }

    /**
     * 在本地保存人为变更的顺序
     */
    private void savePositionLocal(String currencyName, int newPosition) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("custom_position", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(currencyName, newPosition);
        editor.commit();
    }

    @Override
    public void onItemDissmiss(RecyclerView.ViewHolder source) {

    }

    @Override
    public void onItemSelect(RecyclerView.ViewHolder source) {

    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder source) {

    }
}

class ViewHolder extends RecyclerView.ViewHolder {


    TextView textView;
    EditText editText;

    public ViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        editText = itemView.findViewById(R.id.editText);
    }
}
