package com.zoe.slidemenu.view;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SlidingMenu extends FrameLayout {
	
	private ViewDragHelper mViewDragHelper;
	private View mainView;
	private View menuView;
	private int moveEnd;
	private int moveCenter;
	private FloatEvaluator fe;
	private Callback cb = new Callback() {
		
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == mainView || child == menuView;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if(changedView == menuView){
				menuView.layout(0, 0, menuView.getMeasuredWidth(), menuView.getMeasuredHeight());
				int leftMain=mainView.getLeft()+dx;
				if(leftMain < 0){
					leftMain=0;
				}else if(leftMain>moveEnd){
					leftMain=moveEnd;
				}
				mainView.layout(leftMain, mainView.getTop()+dy, 
						leftMain+mainView.getMeasuredWidth(), mainView.getTop()+dy+mainView.getMeasuredHeight());
			}
			float percent=mainView.getLeft()*1.0f/moveEnd;
			startAnim(percent);
			if(mainView.getLeft()<=0){
				if(mListener!=null){
					mListener.onClose();
					currState=SlidingMenuState.CLOSED;
				}
			}else if(mainView.getLeft()>=moveEnd){
				if(mListener!=null){
					mListener.onOpen();
					currState=SlidingMenuState.OPEN;
				}
			}else{
				if(mListener!=null){
					mListener.onSliding(percent);
					currState=SlidingMenuState.SLIDING;
				}
			}
		}

		
		@Override
		public int getViewHorizontalDragRange(View child) {
			return moveEnd;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if(mainView.getLeft()<=moveCenter){
				closeMenu();
			}else{
				openMenu();
			}
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (mainView == child) {
				if (left < 0) {
					left = 0;
				} else if (left > moveEnd) {
					left = moveEnd;
				}
			}
			return left;
		}
		
	};
	
	protected void startAnim(float percent) {
		ViewHelper.setScaleX(mainView, fe.evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mainView, fe.evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleX(menuView, fe.evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(menuView, fe.evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setTranslationX(menuView, fe.evaluate(percent, -menuView.getMeasuredWidth(), 0f));
		ViewHelper.setAlpha(menuView, fe.evaluate(percent, 0.2f, 1.0f));
	}
	public void closeMenu(){
		mViewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
	}
	public void openMenu(){
		mViewDragHelper.smoothSlideViewTo(mainView, moveEnd, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
		
	}
	@Override
	public void computeScroll() {
		if(mViewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
		}
	}
	
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}
	public SlidingMenu(Context context) {
		this(context, null);
	}
	private void initView() {
		fe = new FloatEvaluator();
		mViewDragHelper = ViewDragHelper.create(this, cb);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mViewDragHelper.shouldInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mViewDragHelper.processTouchEvent(event);
		return true;
	}
	
	@Override
	protected void onFinishInflate() {
		menuView=this.getChildAt(0);
		mainView=this.getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		moveEnd=(int) (this.getMeasuredWidth()*0.7);
		moveCenter=moveEnd/2;
	}
	
	private SlidingMenuStateChangeListener mListener;
	private SlidingMenuState currState = SlidingMenuState.CLOSED;
	public void setSlidingMenuStateChangeListener(SlidingMenuStateChangeListener listener){
		mListener=listener;
	}
	
	public interface SlidingMenuStateChangeListener{
		void onOpen();
		void onClose();
		void onSliding(float percent);
	}
	
	public SlidingMenuState getCurrState() {
		return currState;
	}



	public enum SlidingMenuState{
		OPEN, CLOSED, SLIDING;
	}
}
