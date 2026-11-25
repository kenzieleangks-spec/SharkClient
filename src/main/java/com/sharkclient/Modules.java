package com.sharkclient;

import java.util.List;

public class Modules {
    public static final String[] CATEGORIES = {"Movement", "Render", "HUD", "Misc"};

    public static final Module AUTO_SPRINT = new Module("Auto Sprint", "Movement", false);
    public static final Module FULLBRIGHT  = new Module("Fullbright", "Render", false);
    public static final Module FPS         = new Module("FPS Display", "HUD", true);
    public static final Module PING        = new Module("Ping Display", "HUD", true);
    public static final Module COORDS      = new Module("Coordinates", "HUD", false);
    public static final Module KEYSTROKES  = new Module("Keystrokes", "HUD", false);
    public static final Module F3_NAME     = new Module("Name di F3", "Misc", true);
    public static final Module AUTO_GG     = new Module("Auto GG", "Misc", false);

    public static final List<Module> ALL = List.of(
            AUTO_SPRINT, FULLBRIGHT, FPS, PING, COORDS, KEYSTROKES, F3_NAME, AUTO_GG);
}
