package com.aivc.voice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aivc.voice.bean.MeetingContentBean;
import com.aivc.voice.service.VoiceService;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;
@Component
public class VoiceAnalysis {

	private static final String APPID = "5b28b7a5";

	private static StringBuffer mResult = new StringBuffer();
	
	private boolean mIsLoop = true;

	private static Logger logger = LogManager.getLogger(VoiceAnalysis.class.getName());
	
	Upload upload;
	
	@Autowired
	VoiceConfig voiceConfig;
	
	@Autowired
	private VoiceService voiceService;
	
	@PostConstruct
	public void init() {
		SpeechUtility.createUtility("appid=" + APPID);
		// 创建播放录音的线程
		upload = new Upload();
		Thread t2 = new Thread(upload);
		t2.start();
		logger.info("upload thread started");
	}

	private boolean onLoop() {
		boolean isWait = true;
		try {
			return Recognize();
		} catch (Exception e) {
			
		}
		
		return isWait;
	}

	// *************************************音频流听写*************************************

	/**
	 * 听写
	 */
	
	private boolean mIsEndOfSpeech = false;
	private boolean Recognize() {
		if (SpeechRecognizer.getRecognizer() == null)
			SpeechRecognizer.createRecognizer();
		mIsEndOfSpeech = false;
		return RecognizePcmfileByte();
	}

	/**
	 * 自动化测试注意要点 如果直接从音频文件识别，需要模拟真实的音速，防止音频队列的堵塞
	 * @return 
	 */
	public boolean RecognizePcmfileByte() {
		SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
		recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
		//写音频流时，文件是应用层已有的，不必再保存
//		recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
//				"./iat_test.pcm");
		recognizer.setParameter( SpeechConstant.SAMPLE_RATE, "16000" );
		recognizer.setParameter( SpeechConstant.RESULT_TYPE, "plain" );
		recognizer.startListening(recListener);
	
		FileInputStream fis = null;
		final byte[] buffer = new byte[64*1024];
		try {
			if (voiceConfig.getFileUploadputCount() < voiceConfig.getFileOutputCount()) {
				logger.info("start send file " + voiceConfig.getFileUploadputCount());
				File file = new File(voiceConfig.getFilePath()+"/" + voiceConfig.getMeetingId() + "/" + voiceConfig.getFileUploadputCount() + ".wav");
				voiceConfig.setFileUploadputCount(voiceConfig.getFileUploadputCount()+1);
				fis = new FileInputStream(file);
				if (0 == fis.available()) {
					mResult.append("no audio avaible!");
					recognizer.cancel();
				} else {
					int lenRead = buffer.length;
					while( buffer.length==lenRead && !mIsEndOfSpeech ){
						lenRead = fis.read( buffer );
						recognizer.writeAudio( buffer, 0, lenRead );
					}//end of while
					
					recognizer.stopListening();
					return true;
				}
			}else {
				Thread.sleep(500);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fis) {
					fis.close();
					fis = null;
				}
		} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
		
	}

	/**
	 * 听写监听器
	 */
	private RecognizerListener recListener = new RecognizerListener() {

		public void onBeginOfSpeech() {
			logger.info( "onBeginOfSpeech enter" );
			logger.info("*************开始录音*************");
		}

		public void onEndOfSpeech() {
			logger.info( "onEndOfSpeech enter" );
			mIsEndOfSpeech = true;
		}

		public void onVolumeChanged(int volume) {
			//logger.info( "onVolumeChanged enter" );
			//if (volume > 0)
				//logger.info("*************音量值:" + volume + "*************");

		}

		public void onResult(RecognizerResult result, boolean islast) {
			mResult.append(result.getResultString());
			if( islast ){
				logger.info("识别结果为:" + mResult.toString());
				if(mResult.length()>0) {
					MeetingContentBean bean = new MeetingContentBean();
					bean.setContent(mResult.toString());
					bean.setMeetingId(voiceConfig.meetingId);
					voiceService.addMeetingContent(bean);
				}
				mIsEndOfSpeech = true;
				mResult.delete(0, mResult.length());
				upload.waitupLoop();
			}
		}

		public void onError(SpeechError error) {
			mIsEndOfSpeech = true;
			logger.info("onError" + error.getErrorCode());
			upload.waitupLoop();
		}

		public void onEvent(int eventType, int arg1, int agr2, String msg) {
			logger.info( "onEvent enter" );
		}

	};
	// 上传类，因为要用到MyRecord类中的变量，所以将其做成内部类
	class Upload implements Runnable {
		private void waitupLoop(){
			synchronized(this){
				Upload.this.notify();
			}
		}
	
		public void run() {
		
			while (mIsLoop) {
				try {
			
					if (onLoop()) {
						synchronized(this){
							this.wait();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
