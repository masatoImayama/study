using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody2D))]
public class field : MonoBehaviour {

	// スラッシュのPrefab
	public GameObject slash;
	public GameObject enemy;
	public int SlashCount = 0;
	public int EnemyCount = 0;
	private const int MAX_SLASH_COUNT = 3; 
	private const int MAX_ENEMY_COUNT = 1;

	void OnGUI ()
	{
		switch (Event.current.type) {
		case EventType.MouseDown:
			//Debug.Log ("touch!");
			break;
		case EventType.mouseDrag:
			//Debug.Log ("Drag!");
			break;
		case EventType.mouseUp:
			//Debug.Log ("up!");
			Vector3 vec = Input.mousePosition;
			vec.z = 10f;
			// スラッシュを生成
			//Instantiate (slash, transform.position, transform.rotation);
			SlashCount = (int)GameObject.FindGameObjectsWithTag("slash").Length;
			if (SlashCount < MAX_SLASH_COUNT) {
				Instantiate (slash,Camera.main.ScreenToWorldPoint(vec),transform.rotation);
			}
			break;
		}

		// TODO:敵生存判定
		EnemyCount = (int)GameObject.FindGameObjectsWithTag("enemy").Length;
		if (EnemyCount == 0) {
			// 敵がいなければ出現
			if (EnemyCount < MAX_ENEMY_COUNT) {
				Vector3 vec = new Vector3();
				vec.z = 10f;
				vec.x = 500f;
				vec.y = 200f;

				Instantiate (enemy,Camera.main.ScreenToWorldPoint(vec),transform.rotation);
			}
		}

	}
}
