package com.pbg.lpg_delivery.model.enums;

import com.pbg.lpg_delivery.exceptionHandler.LpgException;

import java.util.Arrays;

public enum CylinderType {

    COMMERCIAL(18.5),
    DOMESTIC(14.5);

    private final double capacity;

    CylinderType(double capacity) {
        this.capacity = capacity;
    }

    public double getCapacity() {
        return capacity;
    }

    public static CylinderType fromString(String type) {
        return Arrays.stream(CylinderType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new LpgException("LH-405", "Invalid cylinder type. Must be 'COMMERCIAL' or 'DOMESTIC'."));
    }
}

