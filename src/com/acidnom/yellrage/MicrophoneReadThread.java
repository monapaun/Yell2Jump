package com.acidnom.yellrage;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
//import android.os.Handler;
import android.util.Log;

public class MicrophoneReadThread extends Thread {
	private volatile Thread runner;
	
	private AudioChunkReadyEvent acre;								 // This will be the main class, which will deal with the data when it receives it 
	private short buffers[][][];									 // buffer for audio data
	private ConcurrentLinkedQueue<AudioChunkMarker> dataMarkerQueue; // queue of markers for the data that's ready to be sent out
	private AudioRecord recorder;									 // this is what will do the recording
	private boolean stopped;										 // state variable that tells us if we're still recording or not
	
	private final int numFrames = 16; // number of frames (of frames) for the buffer array
	
	private int layersDeep;
	
	public static final float MAXAUDIOVALUE = (float) ((Math.pow(2, 16) - 1) / 2f); // maximum amplitude of data
	
	public MicrophoneReadThread(AudioChunkReadyEvent ie) {
		acre = ie;
		
		stopped = true;
		
		layersDeep = 0;
		
		dataMarkerQueue = new ConcurrentLinkedQueue<AudioChunkMarker>();
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	}

	public synchronized void startThread(){
	  if(runner == null){
	    runner = new Thread(this);
	    runner.start();
	  }
	}

	public synchronized void stopThread(){
	  if(runner != null){
	    Thread moribund = runner;
	    runner = null;
	    moribund.interrupt();
	  }
	}

	@Override
	public void run() { 
		stopped = false;
		recorder = null;
		
		buffers  = new short[numFrames][64][160];

		startRecording();
	}
	
	private void startRecording()
	{
		int ix = 0; // init our counters
		int z = 0;
		
		try { // start recording! 

			int N = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT); // get the minimum buffer size

			recorder = new AudioRecord(AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N*10); // init the audio record interface
			recorder.startRecording();
			
			//Log.v("Running","Microphone shenanigans started!");
			Log.d("MCR","Recording is Starting!!");
			layersDeep = 0;
			while(!stopped) { 
				short[] buffer = buffers[z][ix];
				ix = ix++ % buffers.length;
				
				//Log.v("Running","Ready to receive microphone input...");
				N = recorder.read(buffer,0,buffer.length); // read the buffer!
				//Log.v("Running","Reading success!");
				if (ix==0)
				{// once we complete a frame of data, we send its marker to the queue
					onFrameCompleted(z);
					z = ++z % numFrames; // increment z (cyclically within the set of frames)
					//Log.v("Microphone","FrameCompleted!");
				}
			}
			recorder.stop(); //once we send the stop signal, we should close everything neatly
		} catch(IllegalStateException x) {
			Log.d("MCR","FAILED!!");
//			Handler handler = new Handler(); 
//		    handler.postDelayed(new Runnable() { 
//		         public void run() { 
//		        	 layersDeep++;
//		        	 if (layersDeep < 10)
//		        		 Log.d("MCR","Retrying...");
//		        		 startRecording();
//		         } 
//		    }, 500);
			try {
				sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			layersDeep++;
			if (layersDeep < 15)
			{
				Log.d("MCR","Retrying...");
				startRecording();
			}

//			layersDeep++;
//			startRecording();
		} catch(Throwable x) { 
			Log.w("SoundLevel","Error reading voice audio",x);
		} finally { 
			stopped = true; // ensure that it will look like we are stopped no matter what
		}
	}
	
	public AudioRecord getRecorder() {
		return recorder;
	}

	public void close() { 
		stopped = true;
		this.interrupt();
	}
	
	public boolean isStopped()
	{
		return stopped;
	}
	
	public AudioChunkMarker getAudioChunkMarker() {
		return dataMarkerQueue.poll();
	}
	
	public short[][] getFrame(int pos) {
		return buffers[pos];
	}
	
	public void onFrameCompleted(int pos) {
		dataMarkerQueue.offer(new AudioChunkMarker(pos)); // queue our data
		acre.onAudioChunkReady();						  // tell the main function we have data that's ready!
	}
}
