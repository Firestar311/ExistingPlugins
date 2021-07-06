package com.stardevmc.enforcer.modules.punishments.gui.button;

import com.stardevmc.enforcer.objects.enums.RawType;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;

public class TypeButton extends GUIButton {
    
    private RawType type;
    
    public TypeButton(RawType type) {
        super(ItemBuilder.start(type.getMaterial()).withName(type.getColor() + type.name()).buildItem());
        this.type = type;
    }
    
    public RawType getType() {
        return type;
    }
}