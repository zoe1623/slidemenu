package com.zoe.slidemenu;

import java.util.Random;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zoe.slidemenu.view.SlidingMenu;
import com.zoe.slidemenu.view.SlidingMenu.SlidingMenuStateChangeListener;

public class MainActivity extends Activity {

	private ListView mMainLv;
	private ListView mMenuLv;
	private ImageView mIvHead;
	private FloatEvaluator fe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMainLv = (ListView) findViewById(R.id.main_listview);
		mMenuLv = (ListView) findViewById(R.id.menu_listview);
		mMainLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.NAMES));
		mMenuLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv=(TextView) super.getView(position, convertView, parent);
				tv.setTextColor(Color.WHITE);
				return tv;
			}
		});
		fe = new FloatEvaluator();
		mIvHead = (ImageView) findViewById(R.id.iv_main_head);
		SlidingMenu slidingMenu = (SlidingMenu) findViewById(R.id.slidemenu);
		slidingMenu.setSlidingMenuStateChangeListener(new SlidingMenuStateChangeListener() {
			
			@Override
			public void onSliding(float percent) {
				System.out.println("=======onSliding=========");
				ViewHelper.setAlpha(mIvHead, fe.evaluate(percent, 1.0f, 0));
			}
			
			@Override
			public void onOpen() {
				System.out.println("=======onOpen=========");
				mMenuLv.smoothScrollToPosition(new Random().nextInt(mMenuLv.getCount()));
			}
			
			@Override
			public void onClose() {
				System.out.println("=======onClose=========");
				ViewPropertyAnimator.animate(mIvHead)
				.translationX(10.0f)
				.setInterpolator(new CycleInterpolator(6))
				.setDuration(500).start();
			}
		});
	}

}
