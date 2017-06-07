package com.androidquanjiakan.activity.index;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.util.AMapUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import static com.androidquanjiakan.activity.index.SpecialCar_Activity.LOVING_BEGINADDRESS;
import static com.androidquanjiakan.activity.index.SpecialCar_Activity.LOVING_DESTINATION;


/**
 * Created by Gin on 2016/10/11.
 */

public class SelectDestination_Activity extends Activity implements  TextWatcher, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener {

    private String city;
    private TextView tv_city;
    private EditText et_address;
    private TextView tv_search;
    private ListView lv_address;
    private List<String> locationList;
    private List<String>addressList;
    private int flag;
    public static final int LOVING_BEGINRESULT=3;
    public static final int LOVING_FINISHRESULT=4;
    private String cityCode;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdestion);
        BaseApplication.getInstances().setCurrentActivity(this);
        initData();
        initView();

    }

    private void initView() {
        tv_city = (TextView)findViewById(R.id.tv_city);
        tv_city.setText(city);
        //tv_search = (TextView)findViewById(R.id.tv_search);
        lv_address = (ListView)findViewById(R.id.lv_address);


        et_address = (AutoCompleteTextView)findViewById(R.id.et_address);
        if(flag==LOVING_BEGINADDRESS) {
            et_address.setHint("您在哪儿上车");
        }else if(flag==LOVING_DESTINATION) {
            et_address.setHint("您要去哪里");
        }

        et_address.addTextChangedListener(this);

        /*query = new PoiSearch.Query("", "", cityCode);
        query.setPageSize(5);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude,longitude),1000));
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();*/

        /*tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
                intent.putExtra("address",et_address.getText().toString().trim());

                if(flag== LOVING_BEGINADDRESS) {
                    setResult(LOVING_BEGINRESULT,intent);
                    finish();
                }else if(flag==LOVING_DESTINATION) {
                    setResult(LOVING_FINISHRESULT,intent);
                    finish();
                }
            }
        });*/
    }

    private void initData() {
        city = getIntent().getStringExtra("city");
        flag = getIntent().getIntExtra("flag", 0);
        cityCode = getIntent().getStringExtra("cityCode");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    //Edittext监听
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String s = charSequence.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(s)) {
            InputtipsQuery inputquery = new InputtipsQuery(s, cityCode);
            Inputtips inputTips = new Inputtips(SelectDestination_Activity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if(i==1000) {
            locationList=new ArrayList<>();
            addressList=new ArrayList<>();
            for (int t=0;t<list.size();t++){
                String name = list.get(t).getName();
                String address = list.get(t).getAddress();
                locationList.add(name);
                addressList.add(address);
            }
            AddressAdapter adapter= new AddressAdapter(SelectDestination_Activity.this, locationList, addressList);
            lv_address.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lv_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    et_address.setText(locationList.get(position));
                    //list.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("address",locationList.get(position));
                    intent.putExtra("addressdetail",addressList.get(position));
                    //setResult();
                    if(flag== LOVING_BEGINADDRESS) {
                        setResult(LOVING_BEGINRESULT,intent);
                        finish();
                    }else if(flag==LOVING_DESTINATION) {
                        setResult(LOVING_FINISHRESULT,intent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if(i==1000) {
            if(poiResult!=null) {
                List<SuggestionCity> searchSuggestionCitys = poiResult.getSearchSuggestionCitys();
                for (int t=0;i<searchSuggestionCitys.size();t++){
                    SuggestionCity suggestionCity = searchSuggestionCitys.get(t);
                    Toast.makeText(SelectDestination_Activity.this, suggestionCity.getCityName(), Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    class AddressAdapter extends BaseAdapter{
         Context context;
         List<String> name;
         List<String> address;

         public AddressAdapter (Context context,List<String> name,List<String> address){
             this.context=context;
             this.name=name;
             this.address=address;

         }

         @Override
         public int getCount() {
             return name!=null?name.size():0;
         }

         @Override
         public Object getItem(int i) {
             return i;
         }

         @Override
         public long getItemId(int i) {
             return i;
         }

         @Override
         public View getView(int i, View view, ViewGroup viewGroup) {
             ViewHolder holder;
             if(view==null) {
                 holder=new ViewHolder();
                 view = LayoutInflater.from(context).inflate(R.layout.search_address,null);
                 holder.tvName=(TextView) view.findViewById(R.id.tv_name);
                 holder.tvAddress=(TextView) view.findViewById(R.id.tv_address);
                 view.setTag(holder);
             }else {
                 holder=(ViewHolder) view.getTag();
             }

             holder.tvAddress.setText(address.get(i));
             holder.tvName.setText(name.get(i));
             return view;
         }
     }

     class ViewHolder{
         private TextView tvName;
         private TextView tvAddress;
     }
}
