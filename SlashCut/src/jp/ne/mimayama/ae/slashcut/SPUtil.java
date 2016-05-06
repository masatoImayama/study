package jp.ne.mimayama.ae.slashcut;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

	// 自身のインスタンス
	private static SPUtil instance;

	// シングルトン
	public static synchronized SPUtil getInstance(Context context){
		if(instance == null) {
			instance = new SPUtil(context);
		}
		return instance;
	}

	private static SharedPreferences settings;

	private static SharedPreferences.Editor editor;

	private SPUtil(Context context){
		settings = context.getSharedPreferences("SharedPreference_1.0", 0);
		editor = settings.edit();

	}

	public int getHighScore() {
		return settings.getInt("highScore", 0);
	}

	public void setHighScore(int value) {
		editor.putInt("highScore", value);
		editor.commit();
	}
}
