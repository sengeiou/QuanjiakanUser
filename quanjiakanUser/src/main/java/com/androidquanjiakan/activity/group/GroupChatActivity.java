package com.androidquanjiakan.activity.group;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.GroupChatMsgListAdapter;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.GroupChatListEntity;
import com.androidquanjiakan.util.BitmapUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.receiver.NotificationClickEventReceiver;
import com.quanjiakanuser.receiver.NotificationClickEventReceiver.MessageReceiveCallBack;
import com.quanjiakanuser.util.ImageUtils;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanDialog.MyDialogCallback;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.quanjiakanuser.util.RecordUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

@SuppressLint("NewApi")
public class GroupChatActivity extends Activity implements OnClickListener,OnLayoutChangeListener{

	private ListView listView;
	private TextView tv_title,tv_text,tv_image,tv_voice,tv_send;
	private EditText et_text;
	private View rootView;
	private Dialog mDialog;
	private ImageButton ibtn_back;
	private RecordUtil mRecordUtil;
	private GroupChatMsgListAdapter msgListAdapter;
	private Context context;
	private Conversation conversation;
	private GroupChatListEntity entity;

	private static final String IMAGE_FILE_NAME = "qjk"+String.valueOf(System.currentTimeMillis()).substring(4)+".jpg";

	private int screenHeight = 0;
	private long fromid;

	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			List<Message> _msg = (List<Message>)msg.obj;
			listView.setSelection(listView.getBottom());
			if(mDialog != null && mDialog.isShowing()){
				mDialog.dismiss();
			}
		}
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = GroupChatActivity.this;
		setContentView(R.layout.layout_group_chat);

		mRecordUtil = new RecordUtil(context);
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		rootView = findViewById(R.id.rootview);
		fromid = getIntent().getLongExtra("fromid",0);


		entity = (GroupChatListEntity) getIntent().getSerializableExtra(BaseConstants.PARAMS_ENTITY);
		if(entity==null){
			BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),getString(R.string.error_params));
			finish();
			return;
		}
		conversation = JMessageClient.getGroupConversation(entity.getGid());

		if (null == conversation) {
			conversation = Conversation.createGroupConversation(entity.getGid());
		}

		NotificationClickEventReceiver.registerMessageCallback(GroupChatActivity.class.getName(), new MessageReceiveCallBack() {

			@Override
			public void messageReceived(MessageEvent event) {
				/**
				 *
				 */
				if(event.getMessage()!=null && BaseConstants.jpush_patient_appkey.equals(event.getMessage().getFromAppKey()) && event.getMessage().getTargetID().equals(Long.toString(entity.getGid()))){
					msgListAdapter.addMsgToList(event.getMessage());
					listView.setSelection(listView.getBottom());
				}
			}
		});
		initView();
	}

	/**
	 * 加载所有消息
	 */
	protected void loadAllMessages(){
		mDialog = QuanjiakanDialog.getInstance().getDialog(context);
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(conversation != null){
					android.os.Message msg = new android.os.Message();
					msg.obj = conversation.getAllMessage();
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}

	protected void initView(){
		et_text = (EditText)findViewById(R.id.et_text);
		listView = (ListView)findViewById(R.id.listview);
		msgListAdapter = new GroupChatMsgListAdapter(context, entity.getGid(),new GroupChatMsgListAdapter.ContentLongClickListener() {
			@Override
			public void onContentLongClick(int position, View view) {

			}
		});
		listView.setAdapter(msgListAdapter);
		msgListAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				listView.setSelection(listView.getBottom());
			}
		});
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText(entity.getName());
		tv_text = (TextView)findViewById(R.id.tv_text);
		tv_image = (TextView)findViewById(R.id.tv_image);
		tv_voice = (TextView)findViewById(R.id.tv_voice);
		tv_send = (TextView)findViewById(R.id.tv_send);
		tv_text.setOnClickListener(this);
		tv_image.setOnClickListener(this);
		tv_voice.setOnClickListener(this);
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setOnClickListener(this);
		ibtn_back.setVisibility(View.VISIBLE);
		tv_send.setOnClickListener(this);
	}

	protected void showOptionsDialog(){
		List<String> strings = new ArrayList<>();
		strings.add(getString(R.string.common_album));
		strings.add(getString(R.string.common_shot));
		QuanjiakanDialog.getInstance().getListDialogDefineHeight(context, strings, getString(R.string.common_album_select), new MyDialogCallback() {

			@Override
			public void onItemClick(int position, Object object) {
				switch (position) {
				case 0:
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseConstants.IMAGE_DATA_TYPE);
					startActivityForResult(intent, CommonRequestCode.IMAGE_REQUEST_CODE);
					break;
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (QuanjiakanUtil.hasSdcard()) {
						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(BaseConstants.imageDir, IMAGE_FILE_NAME)));
					}
					startActivityForResult(intentFromCapture, CommonRequestCode.CAMERA_REQUEST_CODE);
					break;
				}
			}
		}, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case CommonRequestCode.IMAGE_REQUEST_CODE:
				save2SmallImage(data.getData());
				break;
			case CommonRequestCode.CAMERA_REQUEST_CODE:
				if (QuanjiakanUtil.hasSdcard()) {
					File tempFile = new File(BaseConstants.imageDir + IMAGE_FILE_NAME);
					try {
						Uri u = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
								tempFile.getAbsolutePath(), null, null));
						save2SmallImage(u);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(GroupChatActivity.this, getString(R.string.error_no_sdcard_for_save), Toast.LENGTH_LONG).show();
				}
				break;
			case CommonRequestCode.RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void save2SmallImage(Uri data) {
		if (data == null) {
			return;
		}
		String path = ImageUtils.uri2Path(data, context);
		String smallImagePath = ImageUtils.saveBitmapToStorage("quanjiakan.jpg", BitmapUtil.getSmallBitmap(path));
		sendImageMessage(smallImagePath);
	}
		
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param
	 * 
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			String filename = (System.currentTimeMillis() + ".jpg").substring(3);
			String image_path = ImageUtils.saveBitmapToStorage(filename, photo);
			sendImageMessage(image_path);
		}
	}
	
	public void sendImageMessage(String path){
		Message msg = null;
		try {
			msg = conversation.createSendImageMessage(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		JMessageClient.sendMessage(msg);
		msgListAdapter.addMsgToList(msg);
		msg.setOnSendCompleteCallback(new BasicCallback() {
			
			@Override
			public void gotResult(int arg0, String arg1) {
				msgListAdapter.notifyDataSetChanged();
			}
		});
		listView.setSelection(listView.getBottom());
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if(id == R.id.tv_text){
			sendTextMessage();
		}else if (id == R.id.tv_image) {
			//select image
			showOptionsDialog();
		}else if (id == R.id.tv_voice) {
			showVoiceDialog();
		}else if (id == R.id.ibtn_back) {
			finish();
		}else if (id == R.id.tv_send) {
			sendTextMessage();
		}
	}
	
	/**
	 * 弹出录制语音对话框
	 */
	public void showVoiceDialog(){
		mRecordUtil.startRec();
		QuanjiakanDialog.getInstance().getVoiceRecordingDialog(context, "", new MyDialogCallback() {
			
			@Override
			public void onItemClick(int position, Object object) {
				mRecordUtil.stopRec();
				String filepath = mRecordUtil.getVoiceURL();
				try {
					Message msg = conversation.createSendVoiceMessage(new File(filepath), mRecordUtil.getDuration());
					JMessageClient.sendMessage(msg);
					msgListAdapter.addMsgToList(msg);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}, null);
	}
	
	public void sendTextMessage(){
		if(et_text.length() == 0){
			Toast.makeText(context, getString(R.string.error_empty_content), Toast.LENGTH_SHORT).show();
			return;
		}
		if(conversation == null){
			Toast.makeText(context, getString(R.string.error_for_get_theme), Toast.LENGTH_SHORT).show();
			return;
		}
		String text = et_text.getText().toString();
		Message msg = conversation.createSendTextMessage(text);		
		JMessageClient.sendMessage(msg);
		msgListAdapter.addMsgToList(msg);
		toggleKeyBoard();
		et_text.setText("");
	}
	
	protected void toggleKeyBoard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 		
	}

	@Override
	protected void onResume() {
		super.onResume();
		rootView.addOnLayoutChangeListener(this);
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	@Override
	public void onLayoutChange(View v, int left, int top, int right,  
            int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {  
		if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > (screenHeight/3))){
			tv_send.setVisibility(View.VISIBLE);
			tv_text.setVisibility(View.GONE);
        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > (screenHeight/3))){  
            tv_send.setVisibility(View.GONE);
            tv_text.setVisibility(View.VISIBLE);
        }
	}	
	
}