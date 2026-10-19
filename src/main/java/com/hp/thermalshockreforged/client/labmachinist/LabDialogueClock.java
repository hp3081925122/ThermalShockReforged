package com.hp.thermalshockreforged.client.labmachinist;

public final class LabDialogueClock {
    public int frame;
    public interface Owner {
        LabDialogueClock dialogueClock();
        String dialogueKey();
    }
}
