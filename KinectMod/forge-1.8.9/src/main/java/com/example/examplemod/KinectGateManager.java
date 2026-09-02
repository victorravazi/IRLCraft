package com.example.examplemod;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class KinectGateManager {

    private final List<KinectLeverGate> gates = new ArrayList<>();

    public void addGate(KinectLeverGate gate) {
        gates.add(gate);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        for (KinectLeverGate gate : gates) {
            gate.update();
        }
    }
}