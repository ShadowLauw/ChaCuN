package ch.epfl.chacun;

import java.util.List;

enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE;
    public static final List<PlayerColor> ALL = List.of(values());
}