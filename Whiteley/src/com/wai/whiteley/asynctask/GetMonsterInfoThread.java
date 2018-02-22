package com.wai.whiteley.asynctask;

import java.util.Collection;

import android.content.Context;

import com.wai.whiteley.DrakeCircusApplication;
import com.wai.whiteley.database.dao.MonsterInfoDAO;
import com.wai.whiteley.http.ResponseModel.Monster;
import com.wai.whiteley.http.ResponseModel.MonsterModel;
import com.wai.whiteley.http.Server;
import com.wai.whiteley.util.FileUtil;
import com.radiusnetworks.ibeacon.IBeacon;

public class GetMonsterInfoThread extends Thread {

	private Context mContext;
	
	private Collection<IBeacon> mBeacons;
	
	private OnDetectMonsterListener onDetectMonsterListener = null;
	private OnCompleteListener onCompleteListener = null;
	
	public GetMonsterInfoThread(Context context, Collection<IBeacon> beacons) {
		this.mContext = context;
		
		this.mBeacons = beacons;
	}
	
	@Override
	public void run() {
		super.run();
		
		for(IBeacon beacon: mBeacons) {
			
			Object result = Server.GetMonsterInfo(beacon.getProximityUuid(), beacon.getMajor(), beacon.getMinor());
			
			if (result != null) {
				if (result instanceof Monster) {
					Monster res_model = (Monster) result;
					if (res_model.status.equalsIgnoreCase("ok")) {
						
						MonsterModel model = res_model.result;
						
						if(model.id == 0)
							return;
						
						MonsterInfoDAO monster = DrakeCircusApplication.getInstance().dbHelper.getOneMonster(model.id);
						if (monster == null || !monster.monster_name.equals(model.name) ||
							!monster.monster_details.equals(model.details) || !monster.monster_image.equals(model.image) ||
							!monster.notification.equals(model.notification)) {
							// new or changed.
							if (monster == null) {
								monster = new MonsterInfoDAO(beacon.getProximityUuid(), beacon.getMajor(), beacon.getMinor()
										, model.id, model.name, model.details, model.image, model.notification);
								DrakeCircusApplication.mInstance.dbHelper.addOrUpdateOneMonster(monster);
							}
							else if( !monster.monster_image.equals(model.image) ) {
									DrakeCircusApplication.getInstance().dbHelper.deleteMonster(model.id);
									monster = new MonsterInfoDAO(beacon.getProximityUuid(), beacon.getMajor(), beacon.getMinor()
											, model.id, model.name, model.details, model.image, model.notification);
									DrakeCircusApplication.mInstance.dbHelper.addOrUpdateOneMonster(monster);
							}
							else
								DrakeCircusApplication.mInstance.dbHelper.addOrUpdateOneMonster(monster);
							
							FileUtil.downloadFileURL(mContext, model.image);
							
							if(onDetectMonsterListener != null)
								onDetectMonsterListener.onDetectMonster(monster);
						}
				
					}
				}
			}
		}
		
		if(onCompleteListener != null)
			onCompleteListener.onComplete();
	}
	
	public void setOnDetectMonsterListener(OnDetectMonsterListener listener) {
		this.onDetectMonsterListener = listener;
	}
	
	public void setOnCompleteListener(OnCompleteListener listener) {
		this.onCompleteListener = listener;
	}
	
	public interface OnDetectMonsterListener {
		public void onDetectMonster(MonsterInfoDAO monsterDao);
	}
	
	public interface OnCompleteListener {
		public void onComplete();
	}
}
