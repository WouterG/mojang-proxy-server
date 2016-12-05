package net.wouto.proxy;

import java.util.UUID;

public class Util {

    public static UUID deserialize(String uuidStr) {
        if (uuidStr.length() == 32) {
            return UUID.fromString(uuidStr.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } else if (uuidStr.length() == 36) {
            return UUID.fromString(uuidStr);
        } else {
            return null;
        }
    }

}
