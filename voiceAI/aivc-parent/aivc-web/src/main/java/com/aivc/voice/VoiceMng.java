package com.aivc.voice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.sound.sampled.*;

@Component
public class VoiceMng {
	private static Logger logger = LogManager.getLogger(VoiceMng.class.getName());

	@Autowired
	private VoiceConfig voiceConfig;

	// 录音有效记录时间戳，用于捕获断句。
	Date lastRecordTime = new Date();
	// 定义录音格式
	AudioFormat audioFormat = null;
	// 定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。
	TargetDataLine td = null;
	// 定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
	SourceDataLine sd = null;
	// 定义字节数组输入输出流
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	// 定义音频输入流
	AudioInputStream ais = null;
	// 定义停止录音的标志，来控制录音线程的运行
	Boolean stopflag = false;

	// 输出音频文件序号
	volatile int fileOutputCount = 0;
	// 上传音频文件序号
	volatile int fileUploadputCount = 0;
	
	// 输出音频文件序号
	volatile Date dbLevelUpdateTime = new Date();
	// 构造函数
	@PostConstruct
	// 开始录音
	public void capture() {
		try {
			// audioFormat为AudioFormat也就是音频格式
			audioFormat = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
			td = (TargetDataLine) (AudioSystem.getLine(info));
			// 打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
			td.open(audioFormat);
			// 允许某一数据行执行数据 I/O
			td.start();

			// 创建播放录音的线程
			Record record = new Record();
			Thread t1 = new Thread(record);
			t1.start();
			

		} catch (Exception e) {
			logger.error("capture error");
			logger.error(e);
			return;
		}
	}

	// 停止录音
	public void stop() {
		stopflag = true;
	}

	// 设置AudioFormat的参数
	public AudioFormat getAudioFormat() {
		// 下面注释部分是另外一种音频格式，两者都可以
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		// 采样率是每秒播放和录制的样本数
		// 采样率8000,11025,16000,22050,44100
		float rate = 16000f;
		// sampleSize表示每个具有此格式的声音样本中的位数
		// 8,16
		int sampleSize = 16;
		// 大端模式和小端模式 true,false
		boolean bigEndian = false;
		// 单声道为1，立体声为2
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
	}

	// 识别音量
	private int calcDecibelLevel(byte[] byteBuffer, int readSize) {
		// byte数组转为short
		short[] buffer = new short[readSize / 2];
		ByteBuffer.wrap(byteBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer);
		long v = 0;
		// 将 buffer 内容取出，进行平方和运算
		for (int i = 0; i < buffer.length; i++) {
			v += buffer[i] * buffer[i];
		}
		// 平方和除以数据总长度，得到音量大小。
		double mean = v / (double) readSize;
		double volume = 10 * Math.log10(mean);
		// System.out.println("volume="+volume);
		if(new Date().getTime() - dbLevelUpdateTime.getTime() > voiceConfig.getDbCheckInterval()) {
			// logger.info("current db level = "+volume);
			voiceConfig.setCurrentDbLevel(""+volume);
			dbLevelUpdateTime = new Date();
		}
		return (int) volume;

	}

	// 录音类，因为要用到MyRecord类中的变量，所以将其做成内部类
	class Record implements Runnable {
		// 定义存放录音的字节数组,作为缓冲区
		byte bts[] = new byte[1000];
		int intervalCount = 0;
		int voiceCount = 0;
		// 将字节数组包装到流里，最终存入到baos中
		// 重写run函数
		public void run() {
			baos = new ByteArrayOutputStream();
			try {
				logger.info("Record thread start ok");

				stopflag = false;
				while (stopflag != true) {
					File rootFilePath = new File(voiceConfig.getFilePath());
					if (!rootFilePath.exists()) {// 如果文件不存在，则创建该目录
						rootFilePath.mkdir();
					}	
					File filePath = new File(voiceConfig.getFilePath()+"/" + voiceConfig.getMeetingId());
					if (!filePath.exists()) {// 如果文件不存在，则创建该目录
						filePath.mkdir();
					}
					
					// 是否需要储存标志
					Boolean saveFlag = false;
					// 当停止录音没按下时，该线程一直执行
					// 从数据行的输入缓冲区读取音频数据。
					// 要读取bts.length长度的字节,cnt 是实际读取的字节数
					int cnt = td.read(bts, 0, bts.length);
					int db = calcDecibelLevel(bts, cnt) ;
					// 如果当前有语音输入
					baos.write(bts, 0, cnt);
					
					// 如果有停顿
					if (db < voiceConfig.getDbBase()) {
						//logger.info("intervalCount="+intervalCount+" voiceCount"+voiceCount);
						if (intervalCount > voiceConfig.getInterval()) {
							if(voiceCount > 3) {
								saveFlag = true;
							}
							intervalCount = 0;
							voiceCount = 0;
						} else {
							if(intervalCount<voiceConfig.getInterval()+1) {
								intervalCount++;
							}
							// 如果没有人声
							if(voiceCount == 0) {
								baos = new ByteArrayOutputStream();
							}
						}
						
					}
					// 如果没有停顿
					else {
						voiceCount ++;
						intervalCount = 0;
					}
					// 如果文件超过上限
					if (baos.size()>voiceConfig.getFileSize()) {
						saveFlag = true;
						voiceCount = 0;
						intervalCount = 0;
					} 
					if (saveFlag) {
						// 取得录音输入流
						audioFormat = getAudioFormat();
						byte audioData[] = baos.toByteArray();
						bais = new ByteArrayInputStream(audioData);
						ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
						// 定义最终保存的文件名
						File file = null;
						// 写入文件
						try {
							// 以当前的时间命名录音的名字
							// 将录音的文件存放到语音文件夹下
							if(voiceConfig.getRecordFlag()) {		
								file = new File(filePath.getPath() + "/" + fileOutputCount + ".wav");
								logger.info("start record, path="+file.getAbsolutePath());
								AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
								baos = new ByteArrayOutputStream();
								fileOutputCount++;
								voiceConfig.setFileOutputCount(fileOutputCount);
							}
						} catch (Exception e) {
							logger.error("write file error");
							logger.error(e);
						} finally {
							// 关闭流
							try {

								if (bais != null) {
									bais.close();
								}
								if (ais != null) {
									ais.close();
								}
							} catch (Exception e) {
								logger.error("write file close error");
								logger.error(e);
							}
						}

					}
				}
			} catch (Exception e) {
				logger.error("Record thread error");
				logger.error(e);
			} finally {
				try {
					// 关闭打开的字节数组流
					if (baos != null) {
						baos.close();
					}
				} catch (IOException e) {
					logger.error("Record thread close error");
					logger.error(e);
				} finally {
					td.drain();
					td.close();
				}
			}
		}

	}
}
