package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.androidquanjiakan.entity.BankCardMes;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Gin on 2016/8/5.
 */
public class BankCardListAdapter extends BaseAdapter {

    private Context context;
    private List<BankCardMes> list;
    private String bank;

    public BankCardListAdapter(Context context,List<BankCardMes> data){
        this.context=context;
        this.list=data;

    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null) {
            holder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.item_bankcard,null);
            holder.bankBg= (ImageView) view.findViewById(R.id.iv_bankbg);
            holder.bankName= (TextView) view.findViewById(R.id.tv_bank);
            holder.cardType= (TextView) view.findViewById(R.id.tv_cardtype);
            holder.cardNumber= (TextView) view.findViewById(R.id.tv_carnumber);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }

        bank = list.get(i).getBank();
        int id = setBg();
        //防止银行背景图片错乱
        holder.bankBg.setTag(id);
        holder.bankBg.setImageResource(R.drawable.iv_null);
        if(holder.bankBg.getTag()!=null&&holder.bankBg.getTag().equals(id)) {
            switch (id){
                case 0:
                    holder.bankBg.setImageResource(R.drawable.iv_gongshang);
                    break;
                case 1:
                    holder.bankBg.setImageResource(R.drawable.iv_nonye);
                    break;
                case 2:
                    holder.bankBg.setImageResource(R.drawable.iv_zhongguo);
                    break;
                case 3:
                    holder.bankBg.setImageResource(R.drawable.iv_jianshe);
                    break;
                case 4:
                    holder.bankBg.setImageResource(R.drawable.iv_jiaotong);
                    break;
                case 5:
                    holder.bankBg.setImageResource(R.drawable.iv_guangfa);
                    break;
                case 6:
                    holder.bankBg.setImageResource(R.drawable.iv_zhaoshang);
                    break;
                default:
                    holder.bankBg.setImageResource(R.drawable.iv_null);
            }
        }

        holder.bankName.setText(bank);
        holder.cardType.setText(list.get(i).getCard_type());
        holder.cardNumber.setText((list.get(i).getCard_no()).substring((list.get(i).getCard_no()).length()-4,(list.get(i).getCard_no()).length()));
        return view;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder{
        ImageView bankBg;
        TextView bankName;
        TextView cardType;
        TextView cardNumber;
    }

    public int setBg(){
        switch (bank){
            case "工商银行":
                return 0;
            case "农业银行":
                return 1;
            case "中国银行":
                return 2;
            case "建设银行":
                return 3;
            case "交通银行":
                return 4;
            case "广发银行":
                return 5;
            case "招商银行":
                return 6;
            default:
                return 7;
        }
    }



}
