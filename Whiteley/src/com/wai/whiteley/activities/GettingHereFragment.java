package com.wai.whiteley.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.util.FontUtils;
import com.wai.whiteley.view.BaseFragment;

public class GettingHereFragment extends BaseFragment {

	private String mTitle = "car";
	
	private TextView txt_traveltitle;
	
	private LinearLayout viewCarDescription;
	private LinearLayout viewTrainDescription;
	private LinearLayout viewBusDescription;
	
	private TextView txtTrain;
	private TextView txtBus;
	private TextView txtCar;
	
	public static GettingHereFragment newInstance(String title) {
		GettingHereFragment fragment = new GettingHereFragment();
		fragment.mTitle = title;
		
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gettinghere_car, null);

		txt_traveltitle = (TextView) view.findViewById(R.id.txt_traveltitle);
		txt_traveltitle.setText(getResources().getString(R.string.strgettinghere4) + " " + mTitle);

		viewCarDescription = (LinearLayout) view.findViewById(R.id.view_car_description);
		viewTrainDescription = (LinearLayout) view.findViewById(R.id.view_train_description);
		viewBusDescription = (LinearLayout) view.findViewById(R.id.view_bus_description);
		
		viewCarDescription.setVisibility(View.GONE);
		viewTrainDescription.setVisibility(View.GONE);
		viewBusDescription.setVisibility(View.GONE);

		txtCar = (TextView)viewCarDescription.findViewById(R.id.txt_car_desc);
		txtBus = (TextView)viewBusDescription.findViewById(R.id.txt_bus_desc);
		txtTrain = (TextView)viewTrainDescription.findViewById(R.id.txt_train_desc);

		txtCar.setText(Html.fromHtml(getResources().getText(R.string.strCarDescription).toString()));
		txtBus.setText(Html.fromHtml(getResources().getText(R.string.strBusDescription).toString()));
		txtTrain.setText(Html.fromHtml(getResources().getText(R.string.strTrainDescription).toString()));
        
		txtCar.setMovementMethod(LinkMovementMethod.getInstance());
		txtBus.setMovementMethod(LinkMovementMethod.getInstance());
		txtTrain.setMovementMethod(LinkMovementMethod.getInstance());
		

		if(mTitle.equals("car"))
			viewCarDescription.setVisibility(View.VISIBLE);
		else if(mTitle.equals("train"))
			viewTrainDescription.setVisibility(View.VISIBLE);
		else if(mTitle.equals("bus"))
			viewBusDescription.setVisibility(View.VISIBLE);

		
		// set fonts
		FontUtils.setTypefaceAllView(viewCarDescription, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypefaceAllView(viewTrainDescription, FontUtils.font_HelveticaNeueThin);
		FontUtils.setTypefaceAllView(viewBusDescription, FontUtils.font_HelveticaNeueThin);

		return view;
	}
}
