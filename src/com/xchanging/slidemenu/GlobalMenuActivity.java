package com.xchanging.slidemenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.debasmita.clickshare.MainFragment;
import com.debasmita.clickshare.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class GlobalMenuActivity extends SlidingFragmentActivity implements OnClickListener {

	protected SlidingMenu leftRightSlidingMenu;
	private ImageButton ivMenuBtnBottomLeft;
	private Fragment mContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initLeftRightSlidingMenu();
		setContentView(R.layout.activity_sidemenu);
		initView();
	}

	private void initView() {
		ivMenuBtnBottomLeft = (ImageButton)this.findViewById(R.id.ivMenuBtnBottomLeft);
		ivMenuBtnBottomLeft.setOnClickListener(this);
	}

	private void initLeftRightSlidingMenu() {
		// TODO Auto-generated method stub
		mContent = new MainFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
		setBehindContentView(R.layout.main_left_layout);
		FragmentTransaction leftFragementTransaction = getSupportFragmentManager().beginTransaction();
		Fragment leftFrag = new LeftSlidingMenuFragment();
		leftFragementTransaction.replace(R.id.main_left_fragment, leftFrag);
		leftFragementTransaction.commit();
		// customize the SlidingMenu

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int slideWidth = (int)(metrics.widthPixels / metrics.density)/3;
		
		leftRightSlidingMenu = getSlidingMenu();
		leftRightSlidingMenu.setMode(SlidingMenu.LEFT);// 设置是左滑还是�?�滑，还是左�?�都�?�以滑，我这里�?��?�了左滑
		leftRightSlidingMenu.setBehindOffset(slideWidth);// 设置�?��?�宽度
		leftRightSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例
		leftRightSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);//设置手势模�?
		leftRightSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置左�?��?�阴影图片
		leftRightSlidingMenu.setFadeEnabled(true);// 设置滑动时�?��?�的是�?�淡入淡出
		leftRightSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果
		leftRightSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        
		/*leftRightSlidingMenu.setSecondaryMenu(R.layout.main_right_layout);
		FragmentTransaction rightFragementTransaction = getSupportFragmentManager().beginTransaction();
		Fragment rightFrag = new RightSlidingMenuFragment();
		leftFragementTransaction.replace(R.id.main_right_fragment, rightFrag);
		rightFragementTransaction.commit();*/
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ivMenuBtnBottomLeft:
			leftRightSlidingMenu.showMenu();
			break;
		default:
			break;
		}
	}
	
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}
	
	/*Return Current fragment*/
	public Fragment getFragment(){
		return mContent;
	}
}
