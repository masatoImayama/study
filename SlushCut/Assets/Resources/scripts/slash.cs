//TODO: スワイプによる角度と開始位置の取得
//TODO: 当たり判定

using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody2D))]
public class slash : MonoBehaviour
{
	public int speed = 10;
	public float direction = 0.1f;
	
	// 画面右上のワールド座標をビューポートから取得
	public Vector2 max = Camera.main.ViewportToWorldPoint(new Vector2(1, 1));
	
	void Start ()
	{
		//rigidbody2D.velocity = transform.right.Normalize * speed;
		
		Vector2 v;
		v.x = Mathf.Cos (Mathf.Deg2Rad * direction) * speed;
		v.y = Mathf.Sin (Mathf.Deg2Rad * direction) * speed;
		rigidbody2D.velocity = v;
	}

	void Update()
	{
		if (1000 < Camera.main.WorldToScreenPoint(this.transform.position).x) {
			Destroy(this.gameObject);
		}
	}
	/*public void Create(float direction, float speed) {
		Vector2 v;
		v.x = Mathf.Cos (Mathf.Deg2Rad * direction) * speed;
		v.y = Mathf.Sin (Mathf.Deg2Rad * direction) * speed;
		rigidbody2D.velocity = v;
	}*/
}

