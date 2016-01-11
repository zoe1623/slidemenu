package com.zoe.slidemenu.view;


import com.zoe.slidemenu.view.SlidingMenu.SlidingMenuState;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	private SlidingMenu menu;

	public MyLinearLayout(Context context) {
		super(context);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(menu.getCurrState() == SlidingMenuState.OPEN){
			if(event.getAction() == MotionEvent.ACTION_UP){
				menu.closeMenu();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		menu = (SlidingMenu) getParent();
		if(menu.getCurrState() == SlidingMenuState.OPEN){
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
}
