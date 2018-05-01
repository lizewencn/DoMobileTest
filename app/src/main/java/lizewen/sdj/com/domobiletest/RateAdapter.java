package lizewen.sdj.com.domobiletest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * created by Administrator
 * 2018/5/1
 * email：lizewencn@126.com
 * Desc:
 */
public class RateAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<RateBean> rateBeanList;

    private Context context;

    public RateAdapter(List<RateBean> rateBeanList, Context context) {
        this.rateBeanList = rateBeanList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RateBean rateBean = rateBeanList.get(position);
        holder.textView.setText(rateBean.getCurrencyName());
        holder.editText.setText(String.valueOf(rateBean.getRate()));

    }

    @Override
    public int getItemCount() {
        return rateBeanList == null ? 0 : rateBeanList.size();
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
