package com.example.Pacman;

import java.util.HashMap;
import java.util.Map;

class Level {
    final private static int NUMBER_OF_LEVELS = 10;
    final private static float MAXIMUM_SPEED_FACTOR = 2;
    final private static float MAXIMUM_TIME_FACTOR = 2;
    final private static int NUMBER_OF_CHASE_WANDER_CYCLES = 4;

    final private float speedFactor;
    final private float timeFactor;
    final private Map<Float, ScriptAction> timedScript;

    float getSpeedFactor() {
        return speedFactor;
    }

    float getTimeFactor() {
        return timeFactor;
    }

    Map<Float, ScriptAction> getTimedScript() {
        return timedScript;
    }

    Level(int n) {
        speedFactor = (float) Math.pow(MAXIMUM_SPEED_FACTOR, n / NUMBER_OF_LEVELS);
        timeFactor = (float) Math.pow(MAXIMUM_TIME_FACTOR, n / NUMBER_OF_LEVELS);

        ScriptAction chase = CommonScriptActions.setActiveGhostStatus(GhostStatus.CHASING),
                    wander = CommonScriptActions.setActiveGhostStatus(GhostStatus.WANDERING);
        float time = 0;
        timedScript = new HashMap<Float, ScriptAction>();
        for(int i = 0; i < NUMBER_OF_CHASE_WANDER_CYCLES; i++) {
            time += 30 * timeFactor;
            timedScript.put(time, wander);
            time += 10 / timeFactor;
            timedScript.put(time, chase);
        }
    }

    void runScript(float t1, float t2) {
        for(float t : timedScript.keySet()) {
            if(t1 < t && t <= t2)
                timedScript.get(t).act();
        }
    }
}
