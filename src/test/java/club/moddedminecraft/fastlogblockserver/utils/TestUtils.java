package club.moddedminecraft.fastlogblockserver.utils;

import java.io.File;

public class TestUtils {
    public static void removeByteLog() {
        File[] files = new File("./").listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".bytelog")) {
                file.delete();
            }
        }
    }
}
