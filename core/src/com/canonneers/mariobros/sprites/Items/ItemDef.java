package com.canonneers.mariobros.sprites.Items;

import com.badlogic.gdx.math.Vector2;

public class ItemDef {

    public Vector2 pos;
    public Class<?> type;

    public ItemDef(Vector2 pos, Class<?> type) {
        this.pos = pos;
        this.type = type;
    }

}
