﻿using UnityEngine;
using System.Collections;

public class GameBase : MonoBehaviour {

	/*
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}*/

	void OnGUI()
	{
		AudioManager.Instance.PlayBGM("main_bgm002", true);

	}

}
