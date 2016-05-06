package jp.ne.mimayama.ae.slashcut;

import java.io.IOException;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.util.HorizontalAlign;

import android.graphics.Point;
import android.view.KeyEvent;

public class MainScene extends KeyListenScene implements IOnSceneTouchListener, ButtonSprite.OnClickListener {

	// サウンド
	private Sound SlushSound;
	private Sound HitSound;
	private Sound ExplodeSound;
	private Sound StartSound;

	// BGM
	private Music MainBgm;

	private boolean isTouchEnabled;
	private boolean isDragging;

	private  boolean isPaused;
	private  boolean isGameOver;

	// 背景
	private Sprite mainBg;
	private Sprite meteoBg1;
	private Sprite meteoBg2;
	private float meteoSpd = 3;
	private static final int METEO_HIGHT = 300;

	private Sprite satelliteBg1;
	private Sprite satelliteBg2;
	private float satelliteSpd = 0.5f;
	private static final int SATELLITE_HIGHT = 50;


	// 一時停止メニュー画面背景
	private Rectangle pauseBg;

	// ゲームオーバーメニュー画面背景
	private Rectangle gameOverBg;

	// リトライボタン
	private Sprite retryBtn;

	// ゲームに戻るボタン
	private Sprite backGameBtn;

	// メインメニューボタン
	private Sprite mainMenuBtn;

	// ドラッグ開始座標
	private float[] touchStartPoint;

	// ベースライン衝突判定用Sprite
	private Sprite baseLine;
	private Sprite DispBaseLine;
	private Rectangle baseLineRectAngle;

	private static final int MENU_MAIN = 1;
	private static final int MENU_RETRY = 2;
	private static final int MENU_BACK_GAME = 3;
	private static final int PAUSE_BUTTON = 4;

	private static final int SLASH_MAX_COUNT = 3;
	private static final int LIFE_MAX_COUNT = 3;

	private static final int SWIPE_DISTANCE = 30;

	// スラッシュクラス
	private Slash[] slash = new Slash[SLASH_MAX_COUNT];

	// 敵機クラス
	private Enemy enemy;

	private boolean[] isSlash = new boolean[SLASH_MAX_COUNT];

	private boolean isEnemy = false;

	// 視覚効果
	private String ATTACK_EFFECT_IMG_PATH = "slush_damage.png";
	private String ENEMY_EXPLODE_EFFECT_IMG_PATH = "EnemyExplode.png";
	private String BASE_EXPLODE_EFFECT_IMG_PATH = "BaseExplode.png";
	public AnimatedSprite AttackEffect;
	public AnimatedSprite EnemyExplodeEffect;
	public AnimatedSprite BaseExplodeEffect;

	// 飛び出す角度
	private double flyAngle;

	// スラッシュのライフタイム管理
	private int[] slashLifeTime = new int[SLASH_MAX_COUNT];

	// 敵のライフタイム
	private int enemyLifeTime;

	// debug用
	//private Text enemyLifeTimeText;

	// 現在のスコアを表示するテキスト
	private Text currentScoreText;

	// 過去最高のスコアを表示するテキスト
	private Text highScoreText;

	// 新記録表示テキスト
	private Text newRecordText;

	// ライフを表示するテキスト
	private Text lifeText;

	// 現在のスコア
	private int currentScore;

	// 自身のライフ
	private int Life;

	// 敵機スピード係数
	private float EnemySpeedCoefficient = 1.0f;

	// 遊び方画面
	private Sprite instructionSprite;

	// 遊び方画面のボタン
	private ButtonSprite instructionBtn;

	private ButtonSprite btnPause;

	private Random rnd = new Random();

	public MainScene(MultiSceneActivity baseActivity) {
		super(baseActivity);

		// ハイスコアが0のとき（初プレイ時）のみヘルプ画面を出す
		if(SPUtil.getInstance(getBaseActivity()).getHighScore() > 0) {

			init();
		} else {
			showHelp();
		}
	}

	@Override
	public void init() {
		int i = 0;

		//効果音
		//StartSound.play();
		
		// 背景の設置
		mainBg = getBaseActivity().getResourceUtil().getSprite("game_bg_main.png");

		// ベースラインの配置
		baseLine = getBaseActivity().getResourceUtil().getSprite("base_line.png");
		DispBaseLine = getBaseActivity().getResourceUtil().getSprite("base_line2.png");

		placeToCenterY(mainBg,0);
		attachChild(mainBg);

		meteoBg1 = getBaseActivity().getResourceUtil().getSprite("game_bg01.png");
		meteoBg2 = getBaseActivity().getResourceUtil().getSprite("game_bg01.png");

		meteoBg1.setPosition(0, METEO_HIGHT);
		attachChild(meteoBg1);

		meteoBg2.setPosition(getBaseActivity().getEngine().getCamera().getWidth(), METEO_HIGHT);
		attachChild(meteoBg2);

		satelliteBg1 = getBaseActivity().getResourceUtil().getSprite("game_bg02.png");
		satelliteBg2 = getBaseActivity().getResourceUtil().getSprite("game_bg02.png");

		satelliteBg1.setPosition(0, SATELLITE_HIGHT);
		attachChild(satelliteBg1);

		satelliteBg2.setPosition(getBaseActivity().getEngine().getCamera().getWidth(), SATELLITE_HIGHT);
		attachChild(satelliteBg2);

		placeToCenterY(baseLine,100);
//		attachChild(baseLine);

		placeToCenterY(DispBaseLine,0);
		attachChild(DispBaseLine);

		baseLineRectAngle = new Rectangle(
				baseLine.getX(),
				baseLine.getY(),
				baseLine.getWidthScaled(),
				baseLine.getHeightScaled(),
				MainScene.this.getBaseActivity().getVertexBufferObjectManager());

		baseLineRectAngle.setAlpha(10.0f);
		baseLineRectAngle.setColor(10.0f, 0.0f, 0.0f, 10.0f);

		for(i = 0; i < (SLASH_MAX_COUNT); i++) {
			isSlash[i] = false;
		}

		// ライフのセット
		Life = LIFE_MAX_COUNT;

		touchStartPoint = new float[2];

		// Sceneのタッチリスナーを登録
		setOnSceneTouchListener(this);

		registerUpdateHandler(updateHandler);

		// BGMを繰り返し再生
		if (MainBgm.isPlaying() == false) {
			MainBgm.setLooping(true);
			MainBgm.play();
		}

		// ---スコア表示---
		currentScore = 0;

		BitmapFont bitmapFont = new BitmapFont(getBaseActivity().getTextureManager(),
				getBaseActivity().getAssets(), "font/score.fnt");
		bitmapFont.load();

		// ビットマップフォントを元にスコアを表示
		currentScoreText = new Text(20,20,bitmapFont, "Score " + currentScore, 20,
				new TextOptions(HorizontalAlign.LEFT),
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(currentScoreText);

		highScoreText = new Text(20,50,bitmapFont, "HighScore " + SPUtil.getInstance(getBaseActivity()).getHighScore(), 20,
				new TextOptions(HorizontalAlign.LEFT),
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(highScoreText);

		lifeText = new Text(20,80,bitmapFont, "Life " + Life, 20,
				new TextOptions(HorizontalAlign.LEFT),
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(lifeText);

		//一時停止ボタン
		btnPause =
				getBaseActivity().getResourceUtil().getButtonSprite("pause_btn.png", "pause_btn.png");
		btnPause.setPosition(600, 20);
		btnPause.setTag(PAUSE_BUTTON);
		btnPause.setOnClickListener(this);
		attachChild(btnPause);
		registerTouchArea(btnPause);

/*		enemyLifeTimeText = new Text(200
				,20,bitmapFont, "enemy " + enemyLifeTime, 20,
				new TextOptions(HorizontalAlign.LEFT),
				getBaseActivity().getVertexBufferObjectManager());
		attachChild(enemyLifeTimeText);
*/
	}

	public void showHelp() {
		instructionSprite = ResourceUtil.getInstance(getBaseActivity()).getSprite("instruction001.png");
		placeToCenter(instructionSprite);
		attachChild(instructionSprite);

		// ボタン
		instructionBtn = ResourceUtil.getInstance(getBaseActivity()).getButtonSprite("instruction_btn.png", "instruction_btn_p.png");
		placeToCenterX(instructionBtn, 350);
		attachChild(instructionBtn);
		registerTouchArea(instructionBtn);
		instructionBtn.setOnClickListener(new ButtonSprite.OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				instructionSprite.detachSelf();
				instructionBtn.detachSelf();
				unregisterTouchArea(instructionBtn);

				init();
			}
		});
	}
	@Override
	public void prepareSoundAndMusic() {
		try {

			MainBgm = MusicFactory.createMusicFromAsset(getBaseActivity().getMusicManager(), getBaseActivity(), "main_bgm002.mp3");

			StartSound = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(), getBaseActivity(), "start_bgm.wav");
			SlushSound = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(), getBaseActivity(), "Slush00.wav");
			HitSound = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(), getBaseActivity(), "Hit001.wav");
			ExplodeSound = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(), getBaseActivity(), "Explode001.wav");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		int i = 0;
		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();

		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN){
			this.isTouchEnabled = false;
			isDragging = true;

			touchStartPoint[0] = x;
			touchStartPoint[1] = y;
		} else
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP || pSceneTouchEvent.getAction() == TouchEvent.ACTION_CANCEL){
			float[] touchEndPoint = new float[2];
			touchEndPoint[0] = x;
			touchEndPoint[1] = y;

			if (isPaused == true || isGameOver == true) return false;

			// ドラッグの距離を制限
			if ((touchEndPoint[0] - touchStartPoint[0] < SWIPE_DISTANCE && touchEndPoint[0] - touchStartPoint[0] > -SWIPE_DISTANCE)
					&& (touchEndPoint[1] - touchStartPoint[1] < SWIPE_DISTANCE && touchEndPoint[1] - touchStartPoint[1] > -SWIPE_DISTANCE)
					) {
				isTouchEnabled = true;
				isDragging = false;
				return true;
			}

			flyAngle = getAngleByTwoPosition(touchStartPoint, touchEndPoint);
			flyAngle -= 270;

			// 左右の振れ角が上限を超えていないかチェック
			if (flyAngle < -80 || flyAngle > 80) {
				isTouchEnabled = true;
				isDragging = false;
				return true;
			}

			try {
				// スラッシュ配列最大容量を使い切っている場合は新たなスラッシュは生成できない
				// スラッシュ生成
				for(i = 0; i < (SLASH_MAX_COUNT); i++) {
					if (slash == null || isSlash[i] == false) {
						slash[i] = new Slash(50,touchStartPoint[1]-20,  flyAngle, 10.0f, Slash.actionMode.STRIGHT, MainScene.this);
						slash[i].displaySprite.animate(50);
						isSlash[i] = true;

						//効果音
						SlushSound.play();

						break;
					}

				}
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		}
		return true;
	}

	public TimerHandler updateHandler = new TimerHandler(1f / 30f, true, new ITimerCallback() {
		@Override
		public void onTimePassed (TimerHandler pTimerHandler) {
			int i = 0;
			Enemy.actionMode RndActionMode;
			try {
				refreshScene();

				// ------------------------------
				// スラッシュ管理
				// ------------------------------
				for(i = 0; i < (SLASH_MAX_COUNT); i++) {
					if (isSlash[i]) {
						if (slashLifeTime[i] > 100) {
							slashLifeTime[i] = 0;
							isSlash[i] = false;
							slashFinalize(slash[i]);

						}else{
							slashLifeTime[i] += 1;
						}
					} else {
						// TODO:現状処理なし
					}
				}


				// ------------------------------
				// 敵機管理
				// ------------------------------
				if (isEnemy) {
					// スコアをセット
					//enemyLifeTimeText.setText("enemy " + enemyLifeTime);

					if (enemyLifeTime > 400) {
						enemyLifeTime = 0;
						isEnemy = false;
						enemyFinalize(enemy);

					}else{
						enemyLifeTime += 1;
					}
				} else {
					// 動作モードをランダム指定
					RndActionMode = randomActionMode();

					// 出現するたび敵機のスピードアップ
					EnemySpeedCoefficient = EnemySpeedCoefficient + 0.05f;

					// 敵機生成
					enemy = new Enemy(850,100 + rnd.nextInt(50), -90 + rnd.nextInt(180), (3.0f * EnemySpeedCoefficient), RndActionMode, MainScene.this);
					isEnemy = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	private Object appCCloud;

	public void refreshScene(){
		int i = 0;

		Point enemy_point = new Point(0,0);

		// ----------------------------------------------
		// ゲームに直接関係ない処理（背景スクロールなど）
		this.BackGroundAnimate(meteoBg1,meteoBg2,meteoSpd);
		this.BackGroundAnimate(satelliteBg1,satelliteBg2,satelliteSpd);
		// ----------------------------------------------

		if (isPaused == true || isGameOver == true) return;

		if (enemy != null) {
			enemy.reload(this);
			enemy.sprite.animate(100);
		}

		for(i = 0; i < (SLASH_MAX_COUNT); i++) {
			if (slash != null &&  slash[i] != null) {
				slash[i].reload(this);
			}

			if(slash != null && ((slash[i] == null || enemy == null) == false) && this.collisionCheck(slash[i].rectAngle, enemy.rectAngle) == true){
				// スラッシュが敵機にヒット
				//slash[i].finalize(MainScene.this);
				slash[i].displaySprite.stopAnimation();
				slash[i].displaySprite.setAlpha(0.0f);
				MainScene.this.detachChild(slash[i].collidesSprite);
				MainScene.this.detachChild(slash[i].displaySprite);
				slash[i].collidesSprite.detachSelf();
				slash[i].displaySprite.detachSelf();
				slash[i] = null;
				isSlash[i] = false;

				enemy_point.x = (int)enemy.sprite.getX();
				enemy_point.y = (int)enemy.sprite.getY();

				if (enemy.life <= 0) {
					// TODO:スコア加算処理(暫定)
					currentScore += 1;

					// ハイスコア更新時
					if (currentScore > SPUtil.getInstance(getBaseActivity()).getHighScore()) {
						SPUtil.getInstance(getBaseActivity()).setHighScore(currentScore);
					}

					// スコアをセット
					currentScoreText.setText("Score " + currentScore);

					MainScene.this.detachChild(enemy.sprite);
					enemy.sprite.detachSelf();
					enemy = null;
					isEnemy = false;
					enemyLifeTime = 0;

					// 撃破時効果音
					ExplodeSound.play();

					// 撃破時視覚効果
					if (EnemyExplodeEffect == null) {
						EnemyExplodeEffect = getBaseActivity().getResourceUtil().getAnimatedSprite(ENEMY_EXPLODE_EFFECT_IMG_PATH, 1,9);
					}
					EnemyExplodeEffect.detachSelf();
					EnemyExplodeEffect.setPosition(enemy_point.x, enemy_point.y);
					this.attachChild(EnemyExplodeEffect);
					EnemyExplodeEffect.animate(50,false);
				}else{
					enemy.AttackBack(50.0f);
					enemy.life--;

					// ヒット時効果音
					HitSound.play();

					// ヒット時演出
					if (AttackEffect == null) {
						AttackEffect = getBaseActivity().getResourceUtil().getAnimatedSprite(ATTACK_EFFECT_IMG_PATH, 1,8);
					}
					//AttackEffect.detachSelf();
					AttackEffect.setPosition(enemy_point.x, enemy_point.y);
					this.attachChild(AttackEffect);
					AttackEffect.animate(50,false);
				}
			}

			// ベースラインに敵機が接触
			if (enemy != null && this.collisionCheck(baseLineRectAngle, enemy.rectAngle) == true){
				enemy_point.x = (int)enemy.sprite.getX();
				enemy_point.y = (int)enemy.sprite.getY();

				MainScene.this.detachChild(enemy.sprite);
				enemy.sprite.detachSelf();
				enemy = null;
				isEnemy = false;
				enemyLifeTime = 0;

				Life--;

				lifeText.setText("Life " + Life);

				// ベースライン接触時効果音
				ExplodeSound.play();

				// ベースライン接触時視覚効果
				if (BaseExplodeEffect == null) {
					BaseExplodeEffect = getBaseActivity().getResourceUtil().getAnimatedSprite(BASE_EXPLODE_EFFECT_IMG_PATH, 1,9);
				}
				BaseExplodeEffect.detachSelf();
				BaseExplodeEffect.setPosition(enemy_point.x, enemy_point.y);
				this.attachChild(BaseExplodeEffect);
				BaseExplodeEffect.animate(50,false);

				// 残ライフ判定

				if (Life <= 0) {
					unregisterUpdateHandler(updateHandler);

					// ゲームオーバー
					showGameOver();
				}
			}
		}
	}

	private boolean BackGroundAnimate(Sprite Bg1, Sprite Bg2, float ScrollSpeed) {
		float gmWidth = getBaseActivity().getEngine().getCamera().getWidth();

		Bg1.setX(Bg1.getX() - ScrollSpeed);
		if(Bg1.getX() <= -gmWidth) {
			Bg1.setX(Bg1.getX() + gmWidth * 2);
		}

		Bg2.setX(Bg2.getX() - ScrollSpeed);
		if(Bg2.getX() <= -gmWidth) {
			Bg2.setX(Bg2.getX() + gmWidth * 2);
		}

		return true;
	}

	private double getAngleByTwoPosition(float[] start, float[] end) {
		double result = 0;

		float xDistance = end[0] - start[0];
		float yDistance = end[1] - start[1];

		result = Math.atan2(yDistance,xDistance) * 180 /Math.PI;

		result += 270;

		return result;
	}

	private void slashFinalize(Slash pSlash){
		pSlash.finalize(this);
	}

	private void enemyFinalize(Enemy pEnemy){
		pEnemy.finalize(this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// ポーズ中ならポーズ画面を消去
			if(isPaused) {
				getBaseActivity().runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0;i < pauseBg.getChildCount(); i++) {
							// タッチの検知を無効に
							unregisterTouchArea((ITouchArea)pauseBg.getChildByIndex(i));
						}
						pauseBg.detachChildren();
						pauseBg.detachSelf();
					}
				});

				isPaused = false;
				isTouchEnabled = true;
				return true;
			// ポーズ中ならポーズ画面を消去
			} else if(isGameOver) {
				getBaseActivity().runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0;i < gameOverBg.getChildCount(); i++) {
							// タッチの検知を無効に
							unregisterTouchArea((ITouchArea)gameOverBg.getChildByIndex(i));
						}
						gameOverBg.detachChildren();
						gameOverBg.detachSelf();
					}
				});

				isGameOver = false;
				isTouchEnabled = true;
				return true;
			} else {
				return false;
			}
		}else if (e.getAction() == KeyEvent.ACTION_DOWN  && e.getKeyCode() == KeyEvent.KEYCODE_MENU){
			if(isPaused){
				showMenu();
			}
			return true;
		}

		return false;
	}

	// 二つの当たり判定が干渉しあっているかチェックする
	private boolean collisionCheck(Rectangle rectangle1,Rectangle rectangle2) {
		if (rectangle1 == null || rectangle2 == null) return false;

		if(rectangle1.collidesWith(rectangle2) == true) {
			return true;
		}else{
			return false;
		}
	}

	public void showMenu() {
		// 四角形を描画
		pauseBg = new Rectangle(0,0,
				getBaseActivity().getEngine().getCamera().getWidth(),
				getBaseActivity().getEngine().getCamera().getHeight(),
				getBaseActivity().getVertexBufferObjectManager());

		pauseBg.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		pauseBg.setColor(0,0,0);
		pauseBg.setAlpha(0.7f);
		attachChild(pauseBg);

		try {
			// メニューボタン
			ButtonSprite btnMenu =
					getBaseActivity().getResourceUtil().getButtonSprite("menu_btn_menu.png", "menu_btn_menu_p.png");
			placeToCenterX(btnMenu, 200);
			btnMenu.setTag(MENU_MAIN);
			btnMenu.setOnClickListener(this);
			pauseBg.attachChild(btnMenu);
			registerTouchArea(btnMenu);

		} catch (Exception e) {
			e.printStackTrace();
		}

		isPaused = true;
		isTouchEnabled = false;
	}

	public void showGameOver() {

		// 四角形を描画
		gameOverBg = new Rectangle(0,0,
				getBaseActivity().getEngine().getCamera().getWidth(),
				getBaseActivity().getEngine().getCamera().getHeight(),
				getBaseActivity().getVertexBufferObjectManager());

		gameOverBg.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverBg.setColor(0,0,0);
		gameOverBg.setAlpha(0.7f);
		attachChild(gameOverBg);

		try {
			// メニューボタン
			ButtonSprite btnMenu =
					getBaseActivity().getResourceUtil().getButtonSprite("menu_btn_menu.png", "menu_btn_menu_p.png");
			placeToCenterX(btnMenu, 200);
			btnMenu.setTag(MENU_MAIN);
			btnMenu.setOnClickListener(this);
			gameOverBg.attachChild(btnMenu);
			registerTouchArea(btnMenu);

			// リトライボタン
			ButtonSprite btnRetry =
					getBaseActivity().getResourceUtil().getButtonSprite("menu_btn_retry.png", "menu_btn_retry_p.png");
			placeToCenterX(btnRetry, 250);
			btnRetry.setTag(MENU_RETRY);
			btnRetry.setOnClickListener(this);
			gameOverBg.attachChild(btnRetry);
			registerTouchArea(btnRetry);

			BitmapFont bitmapFont = new BitmapFont(getBaseActivity().getTextureManager(),
					getBaseActivity().getAssets(), "font/score.fnt");
			bitmapFont.load();

			if (currentScore > SPUtil.getInstance(getBaseActivity()).getHighScore()) {
				// ビットマップフォントを元にスコアを表示
				newRecordText = new Text(20,40,bitmapFont, "NEW RECORD!!", 20,
						new TextOptions(HorizontalAlign.CENTER),
						getBaseActivity().getVertexBufferObjectManager());
				gameOverBg.attachChild(newRecordText);
			}

			// ビットマップフォントを元にスコアを表示
			currentScoreText = new Text(150,150,bitmapFont, "Now Score " + currentScore, 20,
					new TextOptions(HorizontalAlign.CENTER),
					getBaseActivity().getVertexBufferObjectManager());
			gameOverBg.attachChild(currentScoreText);

		} catch (Exception e) {
			e.printStackTrace();
		}

		isGameOver = true;
		isTouchEnabled = false;
	}

	public void showPause() {

		// 四角形を描画
		pauseBg = new Rectangle(0,0,
				getBaseActivity().getEngine().getCamera().getWidth(),
				getBaseActivity().getEngine().getCamera().getHeight(),
				getBaseActivity().getVertexBufferObjectManager());

		pauseBg.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		pauseBg.setColor(0,0,0);
		pauseBg.setAlpha(0.7f);
		attachChild(pauseBg);

		try {
			// メニューボタン
			ButtonSprite btnMenu =
					getBaseActivity().getResourceUtil().getButtonSprite("menu_btn_menu.png", "menu_btn_menu_p.png");
			placeToCenterX(btnMenu, 200);
			btnMenu.setTag(MENU_MAIN);
			btnMenu.setOnClickListener(this);
			pauseBg.attachChild(btnMenu);
			registerTouchArea(btnMenu);

			// ゲームに戻るボタン
			ButtonSprite btnBack =
					getBaseActivity().getResourceUtil().getButtonSprite("menu_btn_back.png", "menu_btn_back_p.png");
			placeToCenterX(btnBack, 250);
			btnBack.setTag(MENU_BACK_GAME);
			btnBack.setOnClickListener(this);
			pauseBg.attachChild(btnBack);
			registerTouchArea(btnBack);

			// リトライボタン
			ButtonSprite btnRetry =
					getBaseActivity().getResourceUtil().getButtonSprite("menu_btn_retry.png", "menu_btn_retry_p.png");
			placeToCenterX(btnRetry, 300);
			btnRetry.setTag(MENU_RETRY);
			btnRetry.setOnClickListener(this);
			pauseBg.attachChild(btnRetry);
			registerTouchArea(btnRetry);

		} catch (Exception e) {
			e.printStackTrace();
		}

		isPaused = true;
		isTouchEnabled = false;
	}

	public void closePause() {

		try {
			detachChild(pauseBg);

		} catch (Exception e) {
			e.printStackTrace();
		}

		isPaused = false;
		isTouchEnabled = true;
	}


	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		KeyListenScene scene = new MainScene(getBaseActivity());

		switch (pButtonSprite.getTag()){
			case MENU_MAIN:
				if (MainBgm.isPlaying() == true) {
					MainBgm.stop();
					MainBgm.release();
					MainBgm = null;
				}

				// メインメニュー遷移処理
				getBaseActivity().backToInitial();
				break;
			case MENU_RETRY:
				// ゲームリトライ処理

				if (MainBgm.isPlaying() == true) {
					MainBgm.stop();
					MainBgm.release();
					MainBgm = null;
				}
				// リソースの解放
				ResourceUtil.getInstance(getBaseActivity()).resetAllTexture();

				// MainSceneへ移動
				getBaseActivity().getEngine().setScene(scene);

				// 画面遷移管理用配列に追加
				getBaseActivity().appendScene(scene);
				break;
			case MENU_BACK_GAME:
				// ゲーム再開遷移処理
				// ゲーム中であればゲーム再開（そもそもゲーム中でなければ押されるはずがないため、エラーにするかｗ）
				closePause();

				registerUpdateHandler(updateHandler);

				registerTouchArea(btnPause);

/*				// MainSceneへ移動
				getBaseActivity().getEngine().setScene(scene);

				// 画面遷移管理用配列に追加
				getBaseActivity().appendScene(scene);*/
				break;
			case PAUSE_BUTTON:
				unregisterUpdateHandler(updateHandler);

				unregisterTouchArea(btnPause);

				// 一時停止画面描画
				showPause();
		}

	}

	private Enemy.actionMode randomActionMode() {
	    int pick = new Random().nextInt(Enemy.actionMode.values().length);
	    return Enemy.actionMode.values()[pick];
	}


}
