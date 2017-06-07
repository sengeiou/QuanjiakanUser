package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.QueryCallsEntity;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Gin on 2017/2/23.
 */

public class CallsTrafficQueryAdapter extends BaseAdapter {

    private Context context;
    private List<QueryCallsEntity> list;
    private final int TYPE1 = 0;
    private final int TYPE2 = 1;
    public CallsTrafficQueryAdapter(Context context, List<QueryCallsEntity> list){
        this.context=context;
        this.list=list;


    }

    public void resetdata(List<QueryCallsEntity> list){
        this.list=list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        if(view==null) {
            switch (type){
                //文字
                case TYPE1:
                    view = LayoutInflater.from(context).inflate(R.layout.query_calls_view1,null);
                    holder1=new ViewHolder1();
                    holder1.tv_message= (TextView) view.findViewById(R.id.tv_querymessage);
                    holder1.tv_time= (TextView) view.findViewById(R.id.tv_querytime);
                    view.setTag(holder1);
                    break;
                case TYPE2:
                    holder2=new ViewHolder2();
                    view=LayoutInflater.from(context).inflate(R.layout.query_calls_view2,null);
                    holder2.icon= (ImageView) view.findViewById(R.id.iv_icon);
                    holder2.name= (TextView) view.findViewById(R.id.tv_name);
                    holder2.receiveMes= (TextView) view.findViewById(R.id.tv_recieve_message);
                    view.setTag(holder2);
                    break;
            }

        }else {
            switch (type){
                case TYPE1:
                    holder1= (ViewHolder1) view.getTag();
                    break;
                case TYPE2:
                    holder2= (ViewHolder2) view.getTag();
                    break;
            }
        }

        //设置数据
        switch (type){
            case TYPE1:
               /* String message = (String) list.get(i);
                holder1.tv_message.setText(message);*/
                QueryCallsEntity queryCallsEntity = list.get(i);
                holder1.tv_message.setText(queryCallsEntity.getMessage());
                holder1.tv_time.setText(queryCallsEntity.getTime());
                break;
            case TYPE2:
               /* String message1 = (String) list.get(i);
                holder2.receiveMes.setText( message1);*/
                QueryCallsEntity queryCallsEntity1 = list.get(i);
                holder2.name.setText(queryCallsEntity1.getName());
                holder2.receiveMes.setText(queryCallsEntity1.getMessage());
                Picasso.with(context).load(R.drawable.message_pic_portrait).into(holder2.icon);
                break;
            
        }
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        QueryCallsEntity queryCallsEntity = list.get(position);
        if(queryCallsEntity.getType()==0) {
           return TYPE1;//发送文字
       }else {
           return TYPE2;//收到消息
       }
    }

    class ViewHolder1{
       private TextView tv_message;
       private TextView tv_time;
    }

    class ViewHolder2{
        private ImageView icon;
        private TextView receiveMes;
        private TextView name;

    }
}
