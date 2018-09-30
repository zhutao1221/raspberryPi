package com.aivc.common;

import java.awt.*;

import javax.swing.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.*;
import java.io.*;
import java.util.Date;

import javax.sound.sampled.*;
import com.baidu.aip.speech.AipSpeech;
public class AIvoice extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//设置APPID/AK/SK
    public static final String APP_ID = "11395091";
    public static final String API_KEY = "cQ3FQ7iMch4dEyxxYbbKxMx3";
    public static final String SECRET_KEY = "RD6NjGKGK1fZ9kG2GI1CDNP3GCCnhTAC";
    AipSpeech client = null;
    
    // 录音有效记录时间戳，用于捕获断句。
    Date lastRecordTime = new Date();
    //定义录音格式
    AudioFormat audioFormat = null;
    //定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。
    TargetDataLine td = null;
    //定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
    SourceDataLine sd = null;
    //定义字节数组输入输出流
    ByteArrayInputStream bais = null;
    ByteArrayOutputStream baos = null;
    //定义音频输入流
    AudioInputStream ais = null;
    //定义停止录音的标志，来控制录音线程的运行
    Boolean stopflag = false;
    // 会议序号
    //String meetingId = "1529723050890";
    String meetingId = ""+new Date().getTime();
    // 输出音频文件序号
    volatile int fileOutputCount =0;
    // 上传音频文件序号
    volatile int fileUploadputCount = 0;
    
    //定义所需要的组件
    JPanel jp1,jp2,jp3;
    JLabel jl1=null;
    JButton captureBtn,stopBtn;
    public static void main(String[] args) {
        
        new AIvoice();

    }
    //构造函数
    public AIvoice()
    {
        //组件初始化
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        
        //定义字体
        Font myFont = new Font("华文新魏",Font.BOLD,30);
        jl1 = new JLabel("管理终端");
        jl1.setFont(myFont);
        jp1.add(jl1);
        
        captureBtn = new JButton("开始录音");
        //对开始录音按钮进行注册监听
        captureBtn.addActionListener(this);
        captureBtn.setActionCommand("captureBtn");
        //对停止录音进行注册监听
        stopBtn = new JButton("停止录音");
        stopBtn.addActionListener(this);
        stopBtn.setActionCommand("stopBtn");

        
        this.add(jp1,BorderLayout.NORTH);
        this.add(jp2,BorderLayout.CENTER);
        this.add(jp3,BorderLayout.SOUTH);
        jp3.setLayout(null);
        jp3.setLayout(new GridLayout(1, 4,10,10));
        jp3.add(captureBtn);
        jp3.add(stopBtn);

        //设置按钮的属性
        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);

        //设置窗口的属性
        this.setSize(400,300);
        this.setTitle("智能会议室管理");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        // 初始化一个AipSpeech
        client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        
    }
    
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().equals("captureBtn"))
        {
            //点击开始录音按钮后的动作
            //停止按钮可以启动
            captureBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            
            //调用录音的方法
            capture();
        }else if (e.getActionCommand().equals("stopBtn")) {
            //点击停止录音按钮的动作
            captureBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            //调用停止录音的方法     
            stop();
            
        }
        
    }
    //开始录音
    public void capture()
    {
        try {
            //audioFormat为AudioFormat也就是音频格式
            audioFormat = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,audioFormat);
            td = (TargetDataLine)(AudioSystem.getLine(info));
            //打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
            td.open(audioFormat);
            //允许某一数据行执行数据 I/O
            td.start();
            
            //创建播放录音的线程
            Record record = new Record();
            Thread t1 = new Thread(record);
            t1.start();
            
            //创建播放录音的线程
            Upload upload = new Upload();
            Thread t2 = new Thread(upload);
            t2.start();
            
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            return;
        }
    }
    //停止录音
    public void stop()
    {
        stopflag = true;            
    }
    
    //设置AudioFormat的参数
    public AudioFormat getAudioFormat() 
    {
        //下面注释部分是另外一种音频格式，两者都可以
        AudioFormat.Encoding encoding = AudioFormat.Encoding.
        PCM_SIGNED ;
        float rate = 16000f;
        int sampleSize = 16;
        boolean bigEndian = true;
        int channels = 1;
        return new AudioFormat(encoding, rate, sampleSize, channels,
                (sampleSize / 8) * channels, rate, bigEndian);
//        //采样率是每秒播放和录制的样本数
//        float sampleRate = 16000.0F;
//        // 采样率8000,11025,16000,22050,44100
//        //sampleSizeInBits表示每个具有此格式的声音样本中的位数
//        int sampleSizeInBits = 16;
//        // 8,16
//        int channels = 1;
//        // 单声道为1，立体声为2
//        boolean signed = true;
//        // true,false
//        boolean bigEndian = true;
//        // true,false
//        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
    }
    // byte 转short
    public static short byteToShort(byte[] b) { 
        short s = 0; 
        short s0 = (short) (b[0] & 0xff);// 最低位 
        short s1 = (short) (b[1] & 0xff); 
        s1 <<= 8; 
        s = (short) (s0 | s1); 
        return s; 
    }
    // 识别音量
    private int calcDecibelLevel(byte[] byteBuffer, int readSize) {
    	short[] buffer = new short[readSize/2];
    	int j =0;
    	for(int i=0;i<readSize;i=i+2) {
    	  	short s0 = (short) (byteBuffer[i+0] & 0xff);// 最低位 
    	  	short s1 = (short) (byteBuffer[i+1] & 0xff); 
    	   	s1 <<= 8; 
    	   	buffer[j] = (short) (s0 | s1); 
    	 	j++;
    	}
    	readSize= readSize/2;
        double sum = 0;

        for (short rawSample : buffer) {
            double sample = rawSample / 32768.0;
            sum += sample * sample;
        }

        double rms = Math.sqrt(sum / readSize);
        final double db = 20 * Math.log10(rms);

        int mVolume = (int)db+15;
       // System.out.println( "calcDecibelLevel:volume = " + mVolume + ", readSize = " + readSize);
        return mVolume;
    }
    //上传类，因为要用到MyRecord类中的变量，所以将其做成内部类
    class Upload implements Runnable{

		public void run() {
			System.out.println("Upload thread start ok");
			stopflag = false;
          	while(stopflag != true)
         	{
          		//System.out.println(fileUploadputCount+" "+fileOutputCount);
          		if(fileUploadputCount<fileOutputCount) {
          			System.out.println("start send file "+fileUploadputCount);
	          		File file = new File("d:/语音文件/"+meetingId+"/"+fileUploadputCount+".wav");
	          		if(file.exists()) {
		                // 调用接口
		                JSONObject res = client.asr("d:/语音文件/"+meetingId+"/"+fileUploadputCount+".wav", "wav", 16000, null);
		                try {
		        			System.out.println(res.toString(2));
		        		} catch (JSONException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		}
	          			fileUploadputCount++;
	          		}
          		}
         	}
		}
    	
    }
    
    //录音类，因为要用到MyRecord类中的变量，所以将其做成内部类
    class Record implements Runnable
    {
        //定义存放录音的字节数组,作为缓冲区
        byte bts[] = new byte[48000];
        //将字节数组包装到流里，最终存入到baos中
        //重写run函数
        public void run() {    
            baos = new ByteArrayOutputStream();        
            try {
                System.out.println("Record thread start ok");
                stopflag = false;
                while(stopflag != true)
                {
                	// 是否需要储存标志
                	Boolean saveFlag = false;
                    //当停止录音没按下时，该线程一直执行    
                    //从数据行的输入缓冲区读取音频数据。
                    //要读取bts.length长度的字节,cnt 是实际读取的字节数
                    int cnt = td.read(bts, 0, bts.length);
                    // 如果当前有语音输入
                    if(calcDecibelLevel(bts,cnt) > 0)
                    {
                        baos.write(bts, 0, cnt);
                        if(baos.size()>480000) {
                        	saveFlag=true;
                        }
                    }
                    // 如果没声音，且通话内容超过标准，则上传分析。
                    else {
                    	if(baos.size()>100000) {
                    		saveFlag=true;
                    	}
                    }
                    if(saveFlag) {
	                    //取得录音输入流
	                    audioFormat = getAudioFormat();
	                    // 满100K数据，保存一次文件
	                    if(baos.size()>480000) {
		                    byte audioData[] = baos.toByteArray();
		                    bais = new ByteArrayInputStream(audioData);
		                    ais = new AudioInputStream(bais,audioFormat, audioData.length / audioFormat.getFrameSize());
		                    //定义最终保存的文件名
		                    File file = null;
		                    //写入文件
		                    try {    
		                        //以当前的时间命名录音的名字
		                        //将录音的文件存放到F盘下语音文件夹下
		                        File filePath = new File("d:/语音文件/"+meetingId);
		                        if(!filePath.exists())
		                        {//如果文件不存在，则创建该目录
		                            filePath.mkdir();
		                        }
		                 
		                        file = new File(filePath.getPath()+"/"+fileOutputCount+".wav");      
		                        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
		                        baos = new ByteArrayOutputStream();  
		                        fileOutputCount++;
	
		                    } catch (Exception e) {
		                        e.printStackTrace();
		                    }finally{
		                        //关闭流
		                        try {
		                            
		                            if(bais != null)
		                            {
		                                bais.close();
		                            } 
		                            if(ais != null)
		                            {
		                                ais.close();        
		                            }
		                        } catch (Exception e) {
		                            e.printStackTrace();
		                        }       
		                    }
	                    }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    //关闭打开的字节数组流
                    if(baos != null)
                    {
                        baos.close();
                    }    
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    td.drain();
                    td.close();
                }
            }
        }
        
    }
    //播放类,同样也做成内部类
    class Play implements Runnable
    {
        //播放baos中的数据即可
        public void run() {
            byte bts[] = new byte[10000];
            try {
                int cnt;
                //读取数据到缓存数据
                while ((cnt = ais.read(bts, 0, bts.length)) != -1) 
                {
                    if (cnt > 0) 
                    {
                        //写入缓存数据
                        //将音频数据写入到混频器
                        sd.write(bts, 0, cnt);
                    }
                }
               
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                 sd.drain();
                 sd.close();
            }
        }        
    }    
}
