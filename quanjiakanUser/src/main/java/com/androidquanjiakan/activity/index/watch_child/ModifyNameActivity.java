package com.androidquanjiakan.activity.index.watch_child;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.EditTextFilter;
import com.quanjiakan.main.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：Administrator on 2017/5/27 14:53
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class ModifyNameActivity extends BaseActivity {

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.btn_save)
    TextView btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name);
        ButterKnife.bind(this);
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            if (name.contains("%")) {
                try {
                    etName.setText(URLDecoder.decode(name, "utf-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else {
                etName.setText(name);
            }
        }
        etName.setSelection(etName.getText().length());
        initTitle();
    }

    private void initTitle() {
        ibtnBack.setVisibility(View.VISIBLE);
        tvTitle.setText("修改昵称");
    }

    @OnClick({R.id.ibtn_back, R.id.iv_delete, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.iv_delete:
                etName.setText("");
                break;
            case R.id.btn_save:
                // TODO: 2017/5/27 保存接口
                if ((CheckUtil.isAllChineseChar(etName.getText().toString().trim()) && etName.getText().toString().trim().length() > 4)) {
                    Toast.makeText(ModifyNameActivity.this, "超过限定长度，请输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etName.getText().toString().trim().length() > 8) {
                    Toast.makeText(ModifyNameActivity.this, "超过限定长度，请输入四个中文或者八个英文字母", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etName.getText().toString())) {
                    BaseApplication.getInstances().toast(ModifyNameActivity.this, "请输入名称");
                    return;

                }

                if (EditTextFilter.containsSpace(etName.getText().toString()) || EditTextFilter.containsEmoji(etName.getText().toString().trim()) || EditTextFilter.containsUnChar(etName.getText().toString().trim())) {
                    BaseApplication.getInstances().toast(ModifyNameActivity.this, "含有非法字符！");
                    etName.setText("");
                    return;
                }
                if (etName.getText().toString().getBytes().length > 12) {
                    BaseApplication.getInstances().toast(ModifyNameActivity.this, "超过限定长度，请输入四个中文或者八个英文字母");
                    etName.setText("");
                    return;
                }
                
                save();
                break;
        }
    }

    private void save() {
        Intent intent = new Intent();
        intent.putExtra("update",etName.getText().toString());
        setResult(RESULT_OK,intent);
        finish();
    }
}
