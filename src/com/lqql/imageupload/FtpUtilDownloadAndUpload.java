package com.lqql.imageupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import android.util.Log;

public class FtpUtilDownloadAndUpload {
	/*
	 * 下载文件上传文件
	 */
	private String url;
    private int port;
    private String username;
    private String password;
    public FtpUtilDownloadAndUpload(String url, int port, String username, String password) {
        this.url = url;
        this.port = port;
        this.username = username;
        this.password = password;
        }
    public void seturl(String Url){
    	url=Url;
    }
    public void setusername(String userName){
    	username=userName;
    }
    public void setpassword(String Password){
    	password=Password;
    }
    
	//下载文件的方法
	public boolean downFile(
			String url,//Ftp服务器hostname
			int port,//Ftp服务器端口
			String username,//Ftp登录的账号
			String password,//ftp的登录密码
			String remotePath,//要下载的文件的服务器的相对路径
			String fileName,//要下载的文件名 
			String locaPath//需要保存到得本地路径
			){
		
		boolean success = false;
		
		FTPClient ftp = new FTPClient();
		int reply;
		Log.i("test", "文件下载了");
		try {
			ftp.setDefaultPort(port);
			ftp.configure(getFtpConfig());
			ftp.connect(url);
			//如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);//进行登录
			ftp.setControlEncoding("gbk");//设置编码集为GBK支持中文
			
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				 //连接不成功直接返回
				ftp.disconnect();
				return success;
			}
			ftp.enterLocalPassiveMode();//设为被动模式
			Log.i("test", "需要设置文件夹");
			ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器根目录
			Log.i("test", "这执行了");
			FTPFile[] fs = ftp.listFiles();//获得根目录下的所有文件
			Log.i("test", "文件的个数=="+fs.length);
			for(FTPFile ff:fs){
				Log.i("test", "这里执行 了ff.getName() == "+ ff.getName());
				
				
				if(ff.getName().equals(fileName)){
					Log.i("test", "123456找到了‘中文测试.txt文件’");
					File localFile = new File(locaPath+ "中文测试段峰.txt");//设置保存到本地的路径
					Log.i("test", "下载文件的存放在" + locaPath + "中文测试段峰.txt");
					OutputStream os = new FileOutputStream(localFile);
					String loadName = "/"+fileName;
					ftp.retrieveFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), os);//获取文件  以流的形式存到本地
					
					os.close();
				}
				
			}
			ftp.logout();//退出登录
			success = true;//提示成功
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(ftp.isConnected()){
				//如果连接还存在
				
				try {
					ftp.disconnect();//关闭连接
				} catch (IOException e) {
					e.printStackTrace();
				}//
				
			}
		}
		return success;
	}
	
	
	
	
	public boolean uploadFiles(
            String url,//服务器的hostname
            int port,//ftp的端口号
            String username,//ftp的用户账户
            String password,//ftp用户密码
            String path,//ftp服务器保存路径            
            List<String> files//需要上传的文件列表
            ){
        
        boolean success = false;
        
        FTPClient ftp = new FTPClient();//ftp客户端实体
        
        int reply;//用户是否登陆成功的状态码
        Log.i("test", "");
    
        
        
        try {
            //ftp.setDefaultPort(port);
            ftp.setControlEncoding("GBK");
           // ftp.configure(getFtpConfig());
            ftp.connect(url, 21);
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);//进行登录
           // ftp.setControlEncoding("gbk");//设置编码为GBK支持中文
            
            
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                 //连接不成功直接返回
                ftp.disconnect();
                return success;
            }
            ftp.enterLocalPassiveMode();//设置被动模式
            ftp.changeWorkingDirectory(path);//转移到FTP服务器根目录
            
            for (String filePath : files) {
                File file =new File(filePath);  
                String fileName = file.getName();  
                FileInputStream input = new FileInputStream(file); 
                ftp.storeFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), input);//з
                input.close();
            }
            ftp.logout();//退出登录
            success = true;//将状态设置为成功
            
            Log.i("test", "success == " + success);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(ftp.isConnected()){
                //如果服务器还是连接的则关闭连接
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        return success;
    }
	
	public boolean uploadFiles(
            String path,
            List<String> files
            ){
        
        boolean success = false;
        
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        
        int reply;
        
        try {
             
            Log.i("test", "ftp login");
            
            ftp.connect(url, port); 

            ftp.login(username, password);
            
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE); 
            
            reply = ftp.getReplyCode();
            
            
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return success;
            }
            
            ftp.changeWorkingDirectory(path);
            ftp.enterLocalPassiveMode();
            for (String filePath : files) {
                File file =new File(filePath);  
                String fileName = file.getName();  
                FileInputStream input = new FileInputStream(file); 
                ftp.storeFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), input);//з
                input.close();
            }
            
            ftp.logout();
            success = true;
            
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(ftp.isConnected()){
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        return success;
    }
	
	
	
    
    /**

     * 设置FTP客服端的配置--一般可以不设置

     * @return

     */

   private static FTPClientConfig getFtpConfig(){

        FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);

        ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);

       return ftpConfig;

    }

	

}
