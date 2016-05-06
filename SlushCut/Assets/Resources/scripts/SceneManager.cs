using UnityEngine;

public class SceneManager : SingletonMonoBehaviour<SceneManager> {
	
	public void Awake()
	{
		if(this != Instance)
		{
			Destroy(this);
			return;
		}
		
		DontDestroyOnLoad(this.gameObject);
	}    
	
}

