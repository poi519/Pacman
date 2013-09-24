package com.example.Pacman;

interface ScriptAction {
    void act();
}

class CommonScriptActions {
    static ScriptAction setActiveGhostStatus(final GhostStatus s) {
        return new ScriptAction() {
            @Override
            public void act() {
                for(Ghost g : Game.getInstance().getGhosts().values()) {
                    switch(g.getStatus()) {
                        case FLEEING:case WAITING: case RETURNING: break;
                        default: g.setStatus(s);
                    }
                }
            }
        };
    }
}
