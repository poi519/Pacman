package com.example.Pacman;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

class SoundSystem {
    public static final int EAT = R.raw.eat,
                            EAT_ENERGIZER = R.raw.eatdzhajzer,
                            EAT_GHOST = R.raw.eatghost,
                            GAME_OVER = R.raw.gameover,
                            GHOST_SPAWN = R.raw.ghostspawn;

    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;

    public static void initSounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>(3);
        int[] sounds = {EAT, EAT_ENERGIZER, EAT_GHOST, GAME_OVER, GHOST_SPAWN};
        int sound;
        for(int i = 0; i < sounds.length; i++) {
            sound = sounds[i];
            soundPoolMap.put(sound, soundPool.load(context, sound, i));
        }
    }

    public static void playSound(int sound) {
        soundPool.play(soundPoolMap.get(sound), 1f, 1f, 1, 0, 1f);
    }
}
