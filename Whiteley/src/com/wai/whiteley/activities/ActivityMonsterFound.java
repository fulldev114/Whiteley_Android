package com.wai.whiteley.activities;

import java.io.File;
import java.net.URL;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.database.dao.MonsterInfoDAO;
import com.wai.whiteley.util.FontUtils;

public class ActivityMonsterFound extends BaseActivity {

	public static ActivityMonsterFound instance = null;

	private TextView txt_title;
	private TextView txt_desc;
	private TextView txt_content;
	private ImageView img_monster;
	private Button btnViewMonster;

	int monster_id;
	String monster_image;
	String strUuid = "";
	int nMajor = 0;
	int nMinor = 0;

	Bitmap mBitmapMonster = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monster_found);

		instance = this;

		monster_id = getIntent().getIntExtra(Constants.MONSTER_ID, 0);
		MonsterInfoDAO monster = DrakeCircusApplication.getInstance().dbHelper.getOneMonster(monster_id);
		//String desc = getResources().getString(R.string.strMonsterDesc8) + " " + monster.monster_name.toUpperCase() + "!";
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_desc = (TextView) findViewById(R.id.txt_desc);
		img_monster = (ImageView) findViewById(R.id.img_monster);
		txt_content = (TextView) findViewById(R.id.txt_content);
		
		int monster_count = DrakeCircusApplication.getInstance().dbHelper.getAllMonsters().size();
		if ( monster_count >= 6 ) {
			txt_title.setText(getResources().getString(R.string.strEggFoundAllTitle));
			txt_desc.setText(getResources().getString(R.string.strEggFoundAllDesc));
			img_monster.setImageDrawable(getResources().getDrawable(R.drawable.monster_flay));
			txt_content.setVisibility(View.VISIBLE);
			String content = String.format(getString(R.string.strEggFoundAllContent), "THURSDAY 24TH - SATURDAY 26TH MARCH 2016");
			txt_content.setText(Html.fromHtml(content));
		}
		else {
			txt_title.setText(getResources().getString(R.string.strEggFoundTitle));
			txt_desc.setText(getResources().getString(R.string.strEggFoundDesc));
			txt_content.setVisibility(View.GONE);
			
			if (!TextUtils.isEmpty(monster.monster_image)) {			
				try {
					URL urlLabel = new URL(monster.monster_image);
		        	File fileLabel = new File(getFilesDir(), urlLabel.getFile());
		        	mBitmapMonster = BitmapFactory.decodeFile(fileLabel.getAbsolutePath());
					img_monster.setImageBitmap(mBitmapMonster);	
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		btnViewMonster = (Button) findViewById(R.id.button_view_monster);
		
		// set fonts
		FontUtils.setTypeface(txt_title, FontUtils.font_HelveticaNeue, true);
		FontUtils.setTypeface(txt_desc, FontUtils.font_HelveticaNeueThin, false);
		FontUtils.setTypeface(btnViewMonster, FontUtils.font_HelveticaNeue, true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	protected void onDestroy() {
		if(mBitmapMonster != null)
			mBitmapMonster.recycle();
		
		super.onDestroy();
	}
	
	public void onClose(View paramView) {
		onBackPressed();
	}

	public void onViewMonster(View paramView) {
		finish();
		Intent intent = new Intent(this, ActivityMonsterView.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_left, R.anim.out_left);
	}
}
