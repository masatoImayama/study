package jp.ne.mimayama.ae.slashcut;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.sprite.ButtonSprite;

import android.view.KeyEvent;

public class InitialScene extends KeyListenScene implements ButtonSprite.OnClickListener {

	private static final int INITIAL_START = 1;
	private static final int INITIAL_RANKING = 2;
	private static final int INITIAL_RECOMMEND = 3;

	// ボタンが押されたときの効果音
	private Sound btnPressedSound;

	public InitialScene (MultiSceneActivity context) {
		super(context);
		init();
	}

	@Override
	public void init() {

		ButtonSprite btnStart = getBaseActivity().getResourceUtil().getButtonSprite("initial_btn_start02.png", "initial_btn_start02.png");
		placeToCenterX(btnStart,240);
		btnStart.setTag(INITIAL_START);
		btnStart.setOnClickListener(this);
		attachChild(btnStart);
		// ボタンをタップ可能に
		registerTouchArea(btnStart);
	}

	@Override
	public void prepareSoundAndMusic() {
		// 効果音をロード
		try {
			btnPressedSound = SoundFactory.createSoundFromAsset(getBaseActivity().getSoundManager(), getBaseActivity(), "clock00.wav");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		return false;
	}

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		// 効果音を再生
		btnPressedSound.play();

		switch (pButtonSprite.getTag()) {
		case INITIAL_START:
			// リソースの開放
			ResourceUtil.getInstance(getBaseActivity()).resetAllTexture();
			KeyListenScene scene = new MainScene(getBaseActivity());

			// MainSceneへの遷移
			getBaseActivity().getEngine().setScene(scene);

			// 遷移管理用配列に追加
			getBaseActivity().appendScene(scene);

			break;
		case INITIAL_RANKING:
			break;
		case INITIAL_RECOMMEND:
			break;
		}

	}
}
