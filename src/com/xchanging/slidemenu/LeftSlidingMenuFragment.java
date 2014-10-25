package com.xchanging.slidemenu;



import com.debasmita.clickshare.MainFragment;
import com.debasmita.clickshare.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class LeftSlidingMenuFragment extends Fragment implements OnClickListener{
	private View homeBtnLayout;
	private View learnHowBtnLayout;
	private View myAlphaBtnLayout;
	private View shareBtnLayout;
	private View tryAlphaBtnLayout;
	private View helpBtnLayout;
	private ImageView homeSideMarker;
	private ImageView learnHowMarker;
	private ImageView myAlphaMarker;
	private ImageView shareMarker;
	
	private ImageView helpSideMarker;
	private ImageView tryAlphaSideMarker;
	private RoundedImageView roundedImageView;
     @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    }
     
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	 View view = inflater.inflate(R.layout.main_left_fragment, container,false);
    	 homeBtnLayout = view.findViewById(R.id.homeBtnLayout);
    	 homeBtnLayout.setOnClickListener(this);
    	 
    	 helpBtnLayout = view.findViewById(R.id.helpBtnLayout);
    	 helpBtnLayout.setOnClickListener(this);
    	 
    	 tryAlphaBtnLayout = view.findViewById(R.id.tryAlphaBtnLayout);
    	 tryAlphaBtnLayout.setOnClickListener(this);
    	 
    	 learnHowBtnLayout = view.findViewById(R.id.learnHowBtnLayout);
    	 learnHowBtnLayout.setOnClickListener(this);
    	 
    	 myAlphaBtnLayout = view.findViewById(R.id.myAlphaBtnLayout);
    	 myAlphaBtnLayout.setOnClickListener(this);
    	 
    	 shareBtnLayout = view.findViewById(R.id.shareBtnLayout);
    	 shareBtnLayout.setOnClickListener(this);
    	 
    	 roundedImageView = (RoundedImageView)view.findViewById(R.id.headImageView);
   	  	 roundedImageView.setOnClickListener(this);
   	  	 
    	 //myAlphaBtnLayout;shareBtnLayout
    	 
    	 
    	 helpSideMarker = (ImageView)view.findViewById(R.id.side_marker_help);
    	 homeSideMarker = (ImageView)view.findViewById(R.id.side_marker_home);
    	 tryAlphaSideMarker = (ImageView)view.findViewById(R.id.side_marker_tryalpha);

    	 learnHowMarker = (ImageView)view.findViewById(R.id.side_marker_learn_how);
    	 myAlphaMarker = (ImageView)view.findViewById(R.id.side_marker_myalpha);
    	 shareMarker = (ImageView)view.findViewById(R.id.side_marker_share);
    	 return view;
    }

	@Override
	public void onClick(View v) {
		Fragment newContent = null;
		switch (v.getId()) {
		case R.id.homeBtnLayout:
			newContent = new MainFragment();
			homeBtnLayout.setSelected(true);
			learnHowBtnLayout.setSelected(false);
			myAlphaBtnLayout.setSelected(false);
			shareBtnLayout.setSelected(false);
			helpBtnLayout.setSelected(false);
			tryAlphaBtnLayout.setSelected(false);
			homeSideMarker.setVisibility(View.VISIBLE);
			learnHowMarker.setVisibility(View.INVISIBLE);
			myAlphaMarker.setVisibility(View.INVISIBLE);
			shareMarker.setVisibility(View.INVISIBLE);
			helpSideMarker.setVisibility(View.INVISIBLE);
			tryAlphaSideMarker.setVisibility(View.INVISIBLE);
			break;
		/*case R.id.helpBtnLayout:
			newContent = new RegistrationScrollFragment();
			helpBtnLayout.setSelected(true);
			
			//learnHowBtnLayout.setSelected(false);
			//myAlphaBtnLayout.setSelected(false);
			//shareBtnLayout.setSelected(false);
			homeBtnLayout.setSelected(false);
			tryAlphaBtnLayout.setSelected(false);
			helpSideMarker.setVisibility(View.VISIBLE);
			homeSideMarker.setVisibility(View.INVISIBLE);
			tryAlphaSideMarker.setVisibility(View.INVISIBLE);
			break;*/
			
		case R.id.myAlphaBtnLayout:
			newContent = new TestFragment();
			homeBtnLayout.setSelected(false);
			learnHowBtnLayout.setSelected(false);
			myAlphaBtnLayout.setSelected(true);
			shareBtnLayout.setSelected(false);
			helpBtnLayout.setSelected(false);
			tryAlphaBtnLayout.setSelected(false);
			homeSideMarker.setVisibility(View.INVISIBLE);
			learnHowMarker.setVisibility(View.INVISIBLE);
			myAlphaMarker.setVisibility(View.VISIBLE);
			shareMarker.setVisibility(View.INVISIBLE);
			helpSideMarker.setVisibility(View.INVISIBLE);
			tryAlphaSideMarker.setVisibility(View.INVISIBLE);
			break;	
			
		case R.id.shareBtnLayout:
			
			newContent = new TestFragment();
			homeBtnLayout.setSelected(false);
			learnHowBtnLayout.setSelected(false);
			myAlphaBtnLayout.setSelected(false);
			shareBtnLayout.setSelected(true);
			helpBtnLayout.setSelected(false);
			tryAlphaBtnLayout.setSelected(false);
			homeSideMarker.setVisibility(View.INVISIBLE);
			learnHowMarker.setVisibility(View.INVISIBLE);
			myAlphaMarker.setVisibility(View.INVISIBLE);
			shareMarker.setVisibility(View.VISIBLE);
			helpSideMarker.setVisibility(View.INVISIBLE);
			tryAlphaSideMarker.setVisibility(View.INVISIBLE);
			break;		
			
		case R.id.tryAlphaBtnLayout:
			newContent = null;
			homeBtnLayout.setSelected(false);
			learnHowBtnLayout.setSelected(false);
			myAlphaBtnLayout.setSelected(false);
			shareBtnLayout.setSelected(false);
			helpBtnLayout.setSelected(false);
			tryAlphaBtnLayout.setSelected(true);
			homeSideMarker.setVisibility(View.INVISIBLE);
			learnHowMarker.setVisibility(View.INVISIBLE);
			myAlphaMarker.setVisibility(View.INVISIBLE);
			shareMarker.setVisibility(View.INVISIBLE);
			helpSideMarker.setVisibility(View.INVISIBLE);
			tryAlphaSideMarker.setVisibility(View.VISIBLE);
			Intent intent = new Intent(this.getActivity(), TestFragment.class);
			startActivity(intent);
			break;
			
		case R.id.learnHowBtnLayout:
			newContent = new TestFragment();

			homeBtnLayout.setSelected(false);
			learnHowBtnLayout.setSelected(true);
			myAlphaBtnLayout.setSelected(false);
			shareBtnLayout.setSelected(false);
			helpBtnLayout.setSelected(false);
			tryAlphaBtnLayout.setSelected(false);
			homeSideMarker.setVisibility(View.INVISIBLE);
			learnHowMarker.setVisibility(View.VISIBLE);
			myAlphaMarker.setVisibility(View.INVISIBLE);
			shareMarker.setVisibility(View.INVISIBLE);
			helpSideMarker.setVisibility(View.INVISIBLE);
			tryAlphaSideMarker.setVisibility(View.INVISIBLE);
			break;
			
			default:
			break;
		}
		
		if (newContent != null)
			switchFragment(newContent);
		
	}
	
	
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
			GlobalMenuActivity ra = (GlobalMenuActivity) getActivity();
			ra.switchContent(fragment);
		
	}
}
