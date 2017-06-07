package com.androidquanjiakan.activity.setting;

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

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanSetting;
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
import com.quanjiakanuser.widget.MsgListAdapter;
import com.quanjiakanuser.widget.MsgListAdapter.ContentLongClickListener;
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
public class ProblemDetailActivity extends Activity implements OnClickListener,OnLayoutChangeListener{
	
	private ListView listView;
	private MsgListAdapter msgListAdapter;
	private Context context;
	private ArrayList<Message> mList = new ArrayList<>();
	private TextView tv_title,tv_text,tv_image,tv_voice,tv_send;
	private EditText et_text;
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final String IMAGE_FILE_NAME = "qjk"+String.valueOf(System.currentTimeMillis()).substring(4)+".jpg";
	private Conversation conversation;
	private int screenHeight = 0;
	private View rootView;
	private Dialog mDialog;
	private ImageButton ibtn_back;
	private RecordUtil mRecordUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = ProblemDetailActivity.this;
		mRecordUtil = new RecordUtil(context);
		screenHeight = getResources().getDisplayMetrics().heightPixels;  
		setContentView(R.layout.layout_problem_detail);
		rootView = findViewById(R.id.rootview);
		final String fromid = getIntent().getStringExtra("fromid");
		conversation = JMessageClient.getSingleConversation(fromid, BaseConstants.jpush_doctor_appkey);
		NotificationClickEventReceiver.registerMessageCallback(ProblemDetailActivity.class.getName(), new MessageReceiveCallBack() {
			@Override
			public void messageReceived(MessageEvent event) {
				// TODO Auto-generated method stub
				if(event.getMessage().getFromID().equals(fromid)){
					msgListAdapter.addMsgToList(event.getMessage());
					listView.setSelection(listView.getBottom());
				}
			}
		});
		initView();
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			List<Message> _msg = (List<Message>)msg.obj;
			listView.setSelection(listView.getBottom());
			if(mDialog != null && mDialog.isShowing()){
				mDialog.dismiss();
			}
		}		
	};
	
	/**
	 * 加载所有消息
	 */
	protected void loadAllMessages(){
		mDialog = QuanjiakanDialog.getInstance().getDialog(context);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
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
		msgListAdapter = new MsgListAdapter(context, getIntent().getStringExtra("fromid"), BaseConstants.jpush_doctor_appkey,new ContentLongClickListener() {
			
			@Override
			public void onContentLongClick(int position, View view) {
				// TODO Auto-generated method stub
				
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
		tv_title.setText("咨询中");
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
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(intent, IMAGE_REQUEST_CODE);
					break;
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (QuanjiakanUtil.hasSdcard()) {
						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(BaseConstants.imageDir, IMAGE_FILE_NAME)));
					}
					startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
					break;
				}
			}
		}, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				save2SmallImage(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (QuanjiakanUtil.hasSdcard()) {
					File tempFile = new File(BaseConstants.imageDir + IMAGE_FILE_NAME);
					try {
						Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
								tempFile.getAbsolutePath(), null, null));
						// u就是拍摄获得的原始图片的uri，剩下的你想干神马坏事请便……
						save2SmallImage(u);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(ProblemDetailActivity.this,getString(R.string.error_no_sdcard_for_save), Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:
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
			// uploadImage(image_path,filename);
			sendImageMessage(image_path);
		}
	}
	
	public void sendImageMessage(String path){
		Message msg = null;
		try {
			msg = conversation.createSendImageMessage(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		JMessageClient.sendMessage(msg);
		msgListAdapter.addMsgToList(msg);
		msg.setOnSendCompleteCallback(new BasicCallback() {
			
			@Override
			public void gotResult(int arg0, String arg1) {
				// TODO Auto-generated method stub
				LogUtil.d("imagesend   "+arg0+"  "+ arg1);
				msgListAdapter.notifyDataSetChanged();
			}
		});
		listView.setSelection(listView.getBottom());
//		toggleKeyBoard();
	}

	protected void uploadImage(String path, String filename) {
		HashMap<String, String> params = new HashMap<>();
		params.put("file", path);
		params.put("filename", filename);
		Task task = new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(!val.equals("")){
					//上传文件成功，发送消息到服务器
					persistProblem(val);
				}else {
					//文件上传失败
				}
			}
		}, HttpUrls.postFile(), params, Task.TYPE_POST_FILE, null);
		MyHandler.putTask(this,task);
	}
	
	protected void persistProblem(String url){		
		JsonObject content = new JsonObject();
		content.addProperty("type", "image");
		content.addProperty("file", url);
		content.addProperty("id", 7);
		JsonArray array = new JsonArray();
		array.add(content);		
		HashMap<String, String> params = new HashMap<>();
		params.put("user_id", QuanjiakanSetting.getInstance().getUserId()+"");
		params.put("problem_id", getIntent().getStringExtra("problem_id"));
		params.put("content", array.toString());
		params.put("fromtoken", "ard"+QuanjiakanSetting.getInstance().getValue("push_token"));
		params.put("totoken", "iosadksjfkdjskfds");
		
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.getCode().equals("200")){
					
				}
			}
		}, HttpUrls.persistProblem(), params, Task.TYPE_POST_DATA_PARAMS, null));
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				mRecordUtil.stopRec();
				String filepath = mRecordUtil.getVoiceURL();
				try {
					Message msg = conversation.createSendVoiceMessage(new File(filepath), mRecordUtil.getDuration());
					JMessageClient.sendMessage(msg);
					msgListAdapter.addMsgToList(msg);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, null);
	}
	
	public void sendTextMessage(){
		if(et_text.length() == 0){
			Toast.makeText(context, "内容为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(conversation == null){
			Toast.makeText(context, "获取话题列表失败，请重新进入此页面", Toast.LENGTH_SHORT).show();
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
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);

		rootView.addOnLayoutChangeListener(this);
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
		// TODO Auto-generated method stub
		if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > (screenHeight/3))){  
            //显示键盘
			tv_send.setVisibility(View.VISIBLE);
			tv_text.setVisibility(View.GONE);
        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > (screenHeight/3))){  
            //隐藏键盘
//            listView.setSelection(listView.getBottom());
            tv_send.setVisibility(View.GONE);
            tv_text.setVisibility(View.VISIBLE);
        }
	}	
	
}