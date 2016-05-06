package jp.ne.mimayama.ae.slashcut;

import net.app_c.cloud.sdk.AppCCloud;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends MultiSceneActivity {
	// appC広告クラス
	private AppCCloud appCCloud;

	// 画面のサイズ。
	private int CAMERA_WIDTH = 800;
	private int CAMERA_HEIGHT = 480;
	private boolean EnabledMEFlg = true;
	private boolean EnabledSEFlg = true;

	@Override
	public EngineOptions onCreateEngineOptions() {
		// サイズを指定し描画範囲をインスタンス化
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		// ゲームのエンジンを初期化。
		// 第1引数 タイトルバーを表示しないモード
		// 第2引数 画面は縦向き（幅480、高さ800）
		// 第3引数 解像度の縦横比を保ったまま最大まで拡大する
		// 第4引数 描画範囲
		EngineOptions eo = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);

		// BGMの使用許可設定
		eo.getAudioOptions().setNeedsMusic(this.EnabledMEFlg);

		// 効果音の使用許可設定
		eo.getAudioOptions().setNeedsSound(this.EnabledSEFlg);

		return eo;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    try {
		    /*setContentView(R.layout.activity_main);
		    // appC cloud生成
		    appCCloud = new AppCCloud(this).start();
		    // シンプル設定
		    FrameLayout layout = (FrameLayout)findViewById(R.id.layout_simple);
		    // 背景：デフォルト 文字：デフォルト
		    // layoutSimple.addView(appCCloud.Ad.loadSimpleView());
		    // 背景：#00FF00 文字：デフォルト
		    // layoutSimple.addView(appCCloud.Ad.loadSimpleView("00FF00"));
		    // 背景：green 文字：white
		    layout.addView(appCCloud.Ad.loadSimpleView("green", "white"));*/
	    } catch (Exception e) {
	    	Log.d("appc", e.getMessage());
	    }

	}

    @Override
    public void finish() {
	    super.finish();
	    // appC cloud終了処理
	    appCCloud.finish();
    }

	@Override
	protected Scene onCreateScene() {
		// BGMファイルの格納場所を指定
		MusicFactory.setAssetBasePath("mfx/");

		// 効果音ファイルの格納場所を指定
		SoundFactory.setAssetBasePath("mfx/");

		// InitialSceneをインスタンス化し、エンジンにセット
		InitialScene initialScene = new InitialScene(this);

		// 遷移管理用配列に追加
		getSceneArray().add(initialScene);
		
		return initialScene;
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			getResourceUtil().resetAllTexture();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*@Override
	protected int getLayoutID() {
		// ActivityのレイアウトのIDを返す
		return R.layout.activity_main;
	}*/

/*	@Override
	protected int getRenderSurfaceViewID() {
		// SceneがセットされるViewのIDを返す
		return R.id.renderview;
	}*/

	@Override
	public void appendScene(KeyListenScene scene) {
		getSceneArray().add(scene);
	}

	@Override
	public void backToInitial() {
		getSceneArray().clear();
		KeyListenScene scene = new InitialScene(this);
		getSceneArray().add(scene);
		getEngine().setScene(scene);
	}

	@Override
	public void refreshRunningScene(KeyListenScene scene) {

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e){
		// バックボタンが押された時
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// 起動中のSceneのdispatchKeyEvent関数を呼び出し
			// 追加の処理が必要なときはfalseが返ってくるため処理
			if(!getSceneArray().get(getSceneArray().size() - 1).dispatchKeyEvent(e)){

				// シーンがひとつしか起動していない場合は、ゲームを終了
				if(getSceneArray().size() == 1) {
					ResourceUtil.getInstance(this).resetAllTexture();
					finish();
				}
				// 複数のシーンが起動している場合は、一つ前のシーンに戻る
				else {
					getEngine().setScene(getSceneArray().get(getSceneArray().size() -2));
					getSceneArray().remove(getSceneArray().size() - 1);
				}

			}
			return true;
		} else if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_MENU){
			getSceneArray().get(getSceneArray().size() - 1).dispatchKeyEvent(e);
			return true;
		}
		return false;
	}

	@Override
	public void onUserLeaveHint(){

	}

	@Override
	protected int getLayoutID() {
		return R.layout.activity_main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.renderview;
	}

	/*@Override
	protected int getRenderSurfaceViewID() {
		return R.id.renderview;
	}

	@Override
	protected int getLayoutID() {
		return R.layout.activity_main;
	}*/
}
