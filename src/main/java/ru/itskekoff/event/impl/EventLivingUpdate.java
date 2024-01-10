package ru.itskekoff.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import ru.itskekoff.event.Event;

public @AllArgsConstructor class EventLivingUpdate extends Event {
    private @Getter EntityLivingBase entity;
}
