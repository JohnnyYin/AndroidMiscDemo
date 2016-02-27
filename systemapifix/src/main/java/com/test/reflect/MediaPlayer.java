package com.test.reflect;

public class MediaPlayer {
	private MyHandler mHandler = new MyHandler(this);

	public MediaPlayer() {
		System.out.println("new MediaPlayer");
	}

	private class MyHandler extends Handler {
		public MyHandler(MediaPlayer mp) {
			System.out.println("new MyHandler");
		}

		@Override
		public void handlMsg() {
			System.out.println("MyHandler.handlMsg");
			// 抛出异常，进行修复
			int i = 1 / 0;
		}
	}

	public void handleMsg() {
		if (mHandler != null) {
			mHandler.handlMsg();
		}
	}
}
