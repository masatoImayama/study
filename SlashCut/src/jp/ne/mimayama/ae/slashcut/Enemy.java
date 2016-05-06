package jp.ne.mimayama.ae.slashcut;

import java.util.Random;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;

public class Enemy {
	//private int CAMERA_WIDTH = 800;
	private int CAMERA_HEIGHT = 480;

	private int LIMIT_HEIGHT = 0;
	private float UPPER_MARGIN = 3.0f;

	public void construct() {

	}

	private Random rnd = new Random();

	private String COLLISION_IMG_PATH = "enemy_002.png";

	// 表示用スプライト
	public AnimatedSprite sprite;

	// 当たり判定格納用変数
	public Rectangle rectAngle;

	private float velocityX;
	private float velocityY;

	// 現在位置保持
	private float currentPosX;
	private float currentPosY;
	private double currentAngle;

	private float backPosX;

	// 移動モード
	public static enum actionMode{
		STRIGHT,	// 直線移動
		CURVE,		// 曲線移動（上下移動のみ）
		BOUND,		// バウンド移動
	}
	public actionMode actionModeStatus = actionMode.STRIGHT;

	// 曲線移動用変数
	private boolean upperFlg = false;
	private float startPosY = 0.0f;

	// バウンド移動用変数
	//private boolean forwardFlg = true;

	// 残ライフ数
	public int life = 0;

	// コンストラクタ	開始位置スプライト中央X座標,開始位置スプライト中央Y座標,角度,送出速度,対象画面
	public Enemy(float StartCenterPosX, float StartCenterPosY, double startAngle, float speed, actionMode aM, KeyListenScene targetScene) throws Exception{
		try {
			sprite = targetScene.getBaseActivity().getResourceUtil().getAnimatedSprite(
					COLLISION_IMG_PATH, 1, 5);

			// 初期状態の保持
			currentPosX = StartCenterPosX;
			currentPosY = StartCenterPosY;
			currentAngle = startAngle;
			actionModeStatus = aM;
			startPosY = StartCenterPosY;
			if (startPosY < 50) {
				startPosY = 50;
			}else if (startPosY > 200) {
				startPosY = 200;
			}

			// ライフのセット
			life = rnd.nextInt(3);
			if(life == 0) {
				life = 1;
			}

			// 上昇下降フラグをセット
			if (currentAngle > 0) {
				upperFlg = false;
			}else{
				upperFlg = true;
			}

			// ------------------------------------
			// スプライトをセット
			// ------------------------------------
			sprite.setPosition(currentPosX, currentPosY);

			//TODO スプライトの傾き制御
			//sprite.setRotationCenterX((float) currentAngle);
			targetScene.attachChild(sprite);
			targetScene.sortChildren();

			// 移動モードの初期値設定
			switch (actionModeStatus) {
				case STRIGHT:
					velocityX = speed;
					velocityY = speed;
					break;

				case BOUND:
					velocityX = speed;
					velocityY = speed;
					break;

				default:
					velocityX = speed;
					velocityY = speed;
					break;
			}

			reload(targetScene);
		} catch (Exception e) {
			throw(e);
		}
	}

	// 位置更新関数
	public void reload(KeyListenScene targetScene){
		// 位置を更新
		switch (actionModeStatus) {
			case STRIGHT:
				actionSTRIGHT();
				break;
			case CURVE:
				actionCURVE();
				break;
			case BOUND:
				actionBOUND();
				break;
			default:
				actionSTRIGHT();
				break;
		}

		// ------------------------------------
		// 当たり判定の範囲を更新
		// ------------------------------------
		rectAngle = new Rectangle(
				sprite.getX() + ((sprite.getWidth() - sprite.getWidthScaled()) / 2.0f),
				sprite.getY() + ((sprite.getHeight() - sprite.getHeightScaled()) / 2.0f),
				sprite.getWidthScaled(),
				sprite.getHeightScaled(),
				targetScene.getBaseActivity().getVertexBufferObjectManager());
	}

	public void AttackBack(float pBackPosX) {
		backPosX = pBackPosX;
	}

	// 直線移動
	private void actionSTRIGHT() {
		if (backPosX > 0) {
			// 攻撃を食らって奥に引っ込む
			currentPosX += 10.0f;
			backPosX -= 10.0f;
		} else {
			// 角度90°をY軸移動値の100%として考える
			// 角度は画面の水平を0°とする
			currentPosX -= velocityX;
		}

		// ------------------------------------
		// スプライトを移動
		// ------------------------------------
		sprite.setPosition(currentPosX,currentPosY);
	}

	//
	private void actionCURVE() {
		float movePosY = 0.0f;

		if (backPosX > 0) {
			// 攻撃を食らって奥に引っ込む
			currentPosX += 10.0f;
			backPosX -= 10.0f;
		} else {
			currentPosX -= velocityX;

			if ((sprite.getY() <= (0 + startPosY + LIMIT_HEIGHT) ||
					sprite.getY() <= (0 + LIMIT_HEIGHT))) {
				// 上辺に到達
				upperFlg = false;
			}else if (((sprite.getY() + sprite.getHeight()) >= (startPosY + (CAMERA_HEIGHT - LIMIT_HEIGHT)) ||
					(sprite.getY() + sprite.getHeight()) >= CAMERA_HEIGHT)){
				// 下辺に到達
				upperFlg = true;
			}

			if (upperFlg == true) {
				// 上昇率代入
				movePosY = -(UPPER_MARGIN + (sprite.getY() / CAMERA_HEIGHT * 0.3f) * 100);
			}else{
				// 下降率代入
				movePosY = (UPPER_MARGIN + ((sprite.getY() + sprite.getHeight()) / CAMERA_HEIGHT * 0.3f) * 100);
			}

			// 移動処理
			currentPosY += (movePosY);
		}

		//currentPosY += (((90*0.0003f) * currentAngle) * velocityY);

		// ------------------------------------
		// スプライトを移動
		// ------------------------------------
		sprite.setPosition(currentPosX,currentPosY);
	}

	// バウンド（壁面に当たると反射する
	private void actionBOUND(){

		if (backPosX > 0) {
			// 攻撃を食らって奥に引っ込む
			currentPosX += 10.0f;
			backPosX -= 10.0f;
		}

		// 角度変更判定
		if (upperFlg == true && sprite.getY() <= 0) {
			currentAngle = this.changeAngle(currentAngle, 90.0f);
			upperFlg = false;
		}else if (upperFlg == false && (sprite.getY() + sprite.getHeight()) >= CAMERA_HEIGHT) {
			currentAngle = this.changeAngle(currentAngle, 90.0f);
			upperFlg = true;
		}

		currentPosX -= velocityX;

		currentPosY += (((90*0.0003f) * currentAngle) * velocityY);

		// ------------------------------------
		// スプライトを移動
		// ------------------------------------
		sprite.setPosition(currentPosX,currentPosY);
	}

	//TODO:反射ロジック見直し
	private float changeAngle(double angle, float angleVelocity) {
		float currentAngle = (float)angle;
		if (angle > 0.0f) {
			return currentAngle - angleVelocity;
		}else{
			return currentAngle + angleVelocity;
		}
	}


// 内部的に保持するもの
	// 見た目のスプライトを保持（TODO:できればアニメ）

	// ファイナライザ
	protected void finalize(KeyListenScene targetScene) {
		if(sprite != null) {
			targetScene.detachChild(sprite);
		}

		if(rectAngle != null) {
			rectAngle = null;
		}
	}
}
