package com.lqql.imageupload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.example.imagescan.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	public EditText hostName;	
    public EditText userName;    
    public EditText password;  
   	List<String> photos;    
	private GridView imageGridView,menuGridView;
	private ImageAdapter imageadapter;
	private ProgressDialog uploadDialog,refreshDialog;
	public ProgressDialog loginDialog;	
	int[] menu_image_array = {R.drawable.png_01,R.drawable.png_02,R.drawable.png_03,R.drawable.png_04};
	String[] menu_name_array;
	private FtpUtilDownloadAndUpload ftpDownAndUpload = new FtpUtilDownloadAndUpload(null, 21,null, null);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		menu_name_array=new String[]{getResources().getString(R.string.upload),
			getResources().getString(R.string.refresh),getResources().getString(R.string.delete),getResources().getString(R.string.set)};
		setContentView(R.layout.show_image_activity);
		imageGridView=(GridView)findViewById(R.id.child_grid);
		menuGridView = (GridView) super.findViewById(R.id.myGridView) ;	
		getImages();		
		imageadapter= new ImageAdapter(MainActivity.this, photos, imageGridView);
		imageGridView.setAdapter(imageadapter);				
		menuGridView.setAdapter(getMenuAdapter(menu_name_array, menu_image_array)) ;		
		menuGridView.setOnItemClickListener(new OnItemClickListenerImpl()) ;
		}
	
	
	private void getImages() {//获取本地图片
			Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver mContentResolver = MainActivity.this.getContentResolver();
			Cursor mCursor = mContentResolver.query(mImageUri, null,
					MediaStore.Images.Media.MIME_TYPE + "=? or "
							+ MediaStore.Images.Media.MIME_TYPE + "=?",
					new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
			photos = new ArrayList<String>();
			while (mCursor.moveToNext()) {
				String path = mCursor.getString(mCursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				
				photos.add(path);
			}
			mCursor.close();
	}
	
	
	private SimpleAdapter getMenuAdapter(String[] menuNameArray,int[] imageResourceArray) {
		//设置菜单样式组件
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.grid_layout, new String[] { "itemImage", "itemText" },
                new int[] { R.id.item_image, R.id.item_text });
        return simperAdapter;
    }
	

	private class OnItemClickListenerImpl implements OnItemClickListener {
		//设置菜单选项
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {			
			 switch (arg2) {
             case 0:            	
            	 uploadDialog=new ProgressDialog(MainActivity.this);
            	 uploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            	 uploadDialog.setMessage(getResources().getString(R.string.uploading));
            	 uploadDialog.setCancelable(false);
            	 uploadDialog.show();            	 
            	 UpLoadThread  upLoadThread = new UpLoadThread();
                 upLoadThread.start();                 
                 break;
             case 1: 
            	 refreshDialog=new ProgressDialog(MainActivity.this);
            	 refreshDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            	 refreshDialog.setMessage(getResources().getString(R.string.refreshing));
            	 refreshDialog.setCancelable(false);
            	 refreshDialog.show();        
            	 getImages();					
         		 imageadapter = new ImageAdapter(MainActivity.this, photos, imageGridView);
         		 imageGridView.setAdapter(imageadapter);
         		 refreshDialog.dismiss();  
                 break;
             case 2:

                 break;
             case 3:
            	 LayoutInflater layoutInflater = getLayoutInflater();  
            	 View myView = layoutInflater.inflate(R.layout.menulogin,
            			 (ViewGroup)findViewById(R.id.mylayout)); 
            	 hostName = (EditText)myView.findViewById(R.id.hostname);
            	 userName = (EditText)myView.findViewById(R.id.username);
            	 password=(EditText)myView.findViewById(R.id.password);            	            	 
     			 Dialog dialog = new AlertDialog.Builder(MainActivity.this)					
     				.setTitle(getResources().getString(R.string.setftp))		
     				.setView(myView)		// 设置组件
     				.setPositiveButton(getResources().getString(R.string.login), 	
     					new DialogInterface.OnClickListener() {
     					public void onClick(DialogInterface dialog, int arg0) {
     						String show;     		            	
     				 	    loginDialog=new ProgressDialog(MainActivity.this);
     				 		loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
     				 		loginDialog.setMessage(getResources().getString(R.string.loging));
     				 		loginDialog.setCancelable(false);
     				 		loginDialog.show();      				 		
     				        if (hostName.getText().toString().length() == 0)
     				         {
     				         	show = getResources().getString(R.string.attention1);
     				         	new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.loginwrong)).
     				         	setMessage(show).create().show();
     				         	loginDialog.dismiss();
     				         	return;
     				         }     		               
     				         else if (userName.getText().toString().length() == 0)
     				         {
     				         	show = getResources().getString(R.string.attention2);
     				         	new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.loginwrong)).
     				         	setMessage(show).create().show();
     				         	loginDialog.dismiss();
     				         	return;                	
     				         }
     				         else if (password.getText().toString().length() == 0)
     				         {
     				         	show = getResources().getString(R.string.attention3);
     				         	new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.loginwrong)).
     				         	setMessage(show).create().show();
     				         	loginDialog.dismiss();
     				         	return;                	
     				         }
     				        else{
     				        	
     				        LoginThread loginThread = new LoginThread();
     				        loginThread.start();
     				        
     				         }      
     						
     					}})
     					.setNegativeButton(getResources().getString(R.string.cancel),null).create();			// 设置取消按钮     						
     			       dialog.show();		
     			       
     		      }
			 }          
			
		}
	
	Handler uploadHandler=new Handler(){
	 	   public void handleMessage(Message msg){
	 		   switch (msg.what) {
	 		case 1:
	 			new AlertDialog.Builder(MainActivity.this).setTitle(getResources().
	 					getString(R.string.uploadstate)).
	 			setMessage(getResources().getString(R.string.uploadsuccess)).
	 			setPositiveButton(getResources().getString(R.string.confirm), null).show();	 			
	 			break;
	 		case 2:
	 			new AlertDialog.Builder(MainActivity.this).
	 			setTitle(getResources().
	 					getString(R.string.uploadstate)).setMessage(getResources().getString(R.string.uploadfail))
	 			.setPositiveButton(getResources().getString(R.string.confirm), null).show();	 			
	 			break;
	 		default:
	 			break;
	 		}
	 	   }
	    };  
	
	
    class UpLoadThread extends Thread{//上传图片线程
        
        public void run(){
            
            boolean flag = ftpDownAndUpload.uploadFiles("D:\\", imageadapter.getSelectItemsPath());            
            uploadDialog.dismiss();          	
            
            if (flag) {
                uploadHandler.sendEmptyMessage(1);            	
            }
            else {
                uploadHandler.sendEmptyMessage(2);
            }
            Log.i("test", "uploadfiles == "+ flag);
            
        }
        
    }  
   
    Handler loginHandler=new Handler(){
	 	   public void handleMessage(Message msg){
	 		   switch (msg.what) {
	 		case 1:
	 			ftpDownAndUpload.seturl(hostName.getText().toString());
	 			ftpDownAndUpload.setusername(userName.getText().toString());
	 			ftpDownAndUpload.setpassword(password.getText().toString());	 			
	 			new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.loginstate)).
	 			setMessage(getResources().getString(R.string.uploadsuccess)).
	 			setPositiveButton(getResources().getString(R.string.confirm), null).show();	 			
	 			break;
	 		case 2:
	 			new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.uploadstate)).
	 			setMessage(getResources().getString(R.string.loginfailed)).
	 			setPositiveButton(getResources().getString(R.string.confirm), null).show();	 			
	 			break;
	 		default:
	 			break;
	 		}
	 	   }
	    };  
    
   class LoginThread extends Thread{//登陆ftp服务器        
        public void run(){            
            boolean flag = GetLoginMessage();            
            loginDialog.dismiss();       	
             if (flag) {
                loginHandler.sendEmptyMessage(1);            	
            }
            else {
                loginHandler.sendEmptyMessage(2);
            }
            Log.i("test", "login == "+ flag);
            
        }
        
    }
   
   boolean GetLoginMessage()
   {//取得登录信息
   	boolean result = false;
   	 FTPClient ftp = new FTPClient(); 
   	 ftp.setControlEncoding("GBK");   	 
   	 try 
   	 { 
   	   int reply; 
   	   ftp.connect(hostName.getText().toString(), 21);   	   
   	   ftp.login(userName.getText().toString(), password.getText().toString());   	   
   	   reply = ftp.getReplyCode();   	   
   	   if (FTPReply.isPositiveCompletion(reply))
   	   {
   		   result = true;
   	   } 	   
   	   if (!FTPReply.isPositiveCompletion(reply)) 
   	   { 
   		   ftp.disconnect(); 
   	   } 
   	   ftp.logout();    	   
   	 }
   	 catch (IOException e)
   	 {
   		 e.printStackTrace(); 
   	 }
   	 finally 
   	 { 
   		 if (ftp.isConnected()) 
   		 {
   			 result = true;
   			 try 
   			 {
   				 ftp.disconnect(); 
   			 } 
   			 catch (IOException ioe) 
   			 {
   				 
   			 } 
   		 } 
   	}
   	
   	return result;
   }   
    
    

}

	
	

