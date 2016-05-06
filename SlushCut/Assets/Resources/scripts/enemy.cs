//TODO:動きのバリエーション実装
//TODO:出現数制御

using UnityEngine;
using System.Collections;

public class enemy : MonoBehaviour {
	public GameObject obj;
	public GameObject Explosion;
	public GameObject SceneManager;

	// 移動スピード
	public float speed = 20;

	void OnGUI(){
		// ローカル座標のY軸のマイナス方向に移動する
		this.Move (transform.right * -0.1f);
	}

	private void Move(Vector2 direction){
		rigidbody2D.velocity = direction * speed;
	}

	public void runExplosion(){
		Instantiate (Explosion, transform.position, transform.rotation);

		AudioManager.Instance.PlaySE ("Explode001");
	}

	// ぶつかった瞬間に呼び出される
	void OnTriggerEnter2D (Collider2D c)
	{
		//TODO:敵オブジェクトのみ削除
		// 敵の削除
		//if (c.gameObject.name != "base_line3") {
			
		//Destroy(c.gameObject);
		Destroy(this.gameObject);
		//}

		// 爆発する
		this.runExplosion();

		//TODO:ライフ計算

		//TODO:デバッグ用　シーン切り替え
		//Application.LoadLevel("gameOver");

	}
}
