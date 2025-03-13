package com.upf.bastionbreaker.model.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static Map<String, Sound> soundMap = new HashMap<>();
    private static Map<String, Long> playingSounds = new HashMap<>();
    private static float defaultVolume = 0.5f;

    static {
        loadSounds();
    }

    private static void loadSounds() {
        soundMap.put("tank_engine", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/tank_engine.ogg")));
        soundMap.put("tank_shot", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/tank_shot.ogg")));
        soundMap.put("robot_walk", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/robot_walk.ogg")));
        soundMap.put("robot_shot", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/robot_shot.ogg")));
        soundMap.put("canon_aiming", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/canon_aiming.ogg")));
        soundMap.put("helicopter", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/helicopter.ogg")));
    }

    public static void playLoopingSound(String soundName, float volume) {
        if (soundMap.containsKey(soundName) && !playingSounds.containsKey(soundName)) {
            long id = soundMap.get(soundName).loop(volume);
            playingSounds.put(soundName, id);
        }
    }

    public static void playSound(String soundName) {
        if (soundMap.containsKey(soundName)) {
            soundMap.get(soundName).play(defaultVolume);
        }
    }

    public static void stopSound(String soundName) {
        if (soundMap.containsKey(soundName) && playingSounds.containsKey(soundName)) {
            soundMap.get(soundName).stop(playingSounds.get(soundName));
            playingSounds.remove(soundName);
        }
    }

    public static void adjustVolume(String soundName, float volume) {
        if (soundMap.containsKey(soundName) && playingSounds.containsKey(soundName)) {
            soundMap.get(soundName).setVolume(playingSounds.get(soundName), volume);
        }
    }

    public static boolean isPlaying(String soundName) {
        return playingSounds.containsKey(soundName);
    }


    public static void dispose() {
        for (Sound sound : soundMap.values()) {
            sound.dispose();
        }
        soundMap.clear();
        playingSounds.clear();
    }
}
