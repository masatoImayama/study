package jp.ne.mimayama.ae.slashcut;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;

public class Slash {
	public void construct() {

	}
	private String COLLISION_IMG_PATH = "slash01.png";
	private String DISPLAY_IMG_PATH = "slash03.png";

	// 衝突判定用スプライト
	public AnimatedSprite collidesSprite;

	// 表示用スプライト
	public AnimatedSprite displaySprite;

	// 当たり判定格納用変数
	public Rectangle rectAngle;

	private float velocityX;
	private float velocityY;

	// 現在位置保持
	private float currentPosX;
	private float currentPosY;
	private double currentAngle;

	// 移動モード
	public static enum actionMode{
		STRIGHT,	// 直線移動
	}
	public actionMode actionModeStatus = actionMode.STRIGHT;

	// コンストラクタ	開始位置スプライト中央X座標,開始位置スプライト中央Y座標,角度,送出速度,対象画面
	public Slash(float StartCenterPosX, float StartCenterPosY, double startAngle, float speed, actionMode aM, KeyListenScene targetScene) throws Exception{
		try {
			collidesSprite = targetScene.getBaseActivity().getResourceUtil().getAnimatedSprite(
					COLLISION_IMG_PATH, 1, 3);
			displaySprite = targetScene.getBaseActivity().getResourceUtil().getAnimatedSprite(
					DISPLAY_IMG_PATH, 1, 8);

			// 初期状態の保持
			currentPosX = StartCenterPosX;
			currentPosY = StartCenterPosY;
			currentAngle = startAngle;
			actionModeStatus = aM;

			// ------------------------------------
			// スプライトをセット
			// ------------------------------------
			collidesSprite.setPosition(currentPosX, currentPosY);
			displaySprite.setPosition(currentPosX, currentPosY);
			//sprite.setRotationCenterX((float) currentAngle);
			targetScene.attachChild(collidesSprite);
			targetScene.attachChild(displaySprite);
			collidesSprite.setAlpha(0.0f);

			targetScene.sortChildren();

			// 移動モードの初期値設定
			switch (actionModeStatus) {
				case STRIGHT:
					velocityX = speed;
					velocityY = speed;
				default:
					velocityX = speed;
					velocityY = speed;
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
			case STRIGHT: actionSTRIGHT();
			default: actionSTRIGHT();
		}

		// ------------------------------------
		// 当たり判定の範囲を更新
		// ------------------------------------
/*		rectAngle = new Rectangle(
				collidesSprite.getX() + ((collidesSprite.getWidth() - collidesSprite.getWidthScaled()) / 2.0f),
				collidesSprite.getY() + ((collidesSprite.getHeight() - collidesSprite.getHeightScaled()) / 2.0f),
				collidesSprite.getWidthScaled(),
				collidesSprite.getHeightScaled(),
				targetScene.getBaseActivity().getVertexBufferObjectManager());*/
		rectAngle = new Rectangle(
				collidesSprite.getX(),
				collidesSprite.getY() + 20,
				collidesSprite.getWidthScaled(),
				collidesSprite.getHeightScaled(),
				targetScene.getBaseActivity().getVertexBufferObjectManager());
	}

	// 直線移動
	private void actionSTRIGHT() {
		// 角度90°をY軸移動値の100%として考える
		// 角度は画面の水平を0°とする
		currentPosX += velocityX;
		currentPosY += (((90*0.0003f) * currentAngle) * velocityY);


		// ------------------------------------
		// スプライトを移動
		// ------------------------------------
		collidesSprite.setPosition(currentPosX,currentPosY);
		displaySprite.setPosition(currentPosX,currentPosY);
	}

// 内部的に保持するもの
	// 見た目のスプライトを保持（TODO:できればアニメ）

	// ファイナライザ
	protected void finalize(KeyListenScene targetScene) {
		if(collidesSprite != null) {
			targetScene.detachChild(collidesSprite);
		}
		if(displaySprite != null) {
			displaySprite.stopAnimation();
			displaySprite.setAlpha(0.0f);
			targetScene.detachChild(displaySprite);
		}

		if(rectAngle != null) {
			rectAngle = null;
		}
	}
}
