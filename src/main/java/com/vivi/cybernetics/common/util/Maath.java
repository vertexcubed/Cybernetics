package com.vivi.cybernetics.common.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

/**
 * Class containing shitty math functions that I still have to use for some reason
 */
public class Maath {


    /**
     * Adapted from <a href="https://github.com/TheComputerizer/The-Impossible-Library/blob/1.19.2-forge/src/main/java/mods/thecomputerizer/theimpossiblelibrary/util/MathUtil.java">The Impossible Library</a>
     */
    /*
    public static Vector3f getVertex(Vector3f center, double radius, double angle) {
        return new Vector3f((float)(center.x()+(radius*Math.cos(angle))),(float) (center.y()+(radius*Math.sin(angle))),0);
    }
     */

    /**
     * Returns a polar radius for the cartesian point (x,y).
     */
    public static float toRadius(float x, float y) {
        return Mth.sqrt(x*x + y*y);
    }

    /**
     * Returns an angle, in radians, for the cartesian point (x,y). Value is between 0 and 2π.
     */
    public static float toAngle(float x, float y) {
//        return wrapAngle((float) Mth.atan2(y, x));
        float f = (float) Mth.atan2(y, x);
        if(f < 0) f += (2 * Mth.PI);
        return f;
    }

    /**
     * Returns cartesian values from the polar point (r, t)
     */
    public static float toX(float r, float t) {
        return r * Mth.cos(t);
    }
    public static float toY(float r, float t) {
        return r * Mth.sin(t);
    }

    /**
     * Rotates a vector around angle theta
     * @param in        input vector
     * @param angle     angle to rotate (radians)
     * @return          the new vector
     */
    public static Vec2 rotateVec(Vec2 in, float angle) {
        return new Vec2(in.x * Mth.cos(angle) - in.y * Mth.sin(angle), in.x * Mth.sin(angle) + in.y * Mth.cos(angle));
    }
}
