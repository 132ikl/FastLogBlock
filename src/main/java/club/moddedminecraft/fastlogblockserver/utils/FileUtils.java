/*
 * Copyright (c) 2020 LionZXY
 * Copyright (c) 2020 132ikl
 * This file is part of FastLogBlockServer.
 *
 * FastLogBlockServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FastLogBlockServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FastLogBlockServer.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.moddedminecraft.fastlogblockserver.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static final int BYTEBUFFERSIZE = 2048;

    public static void createFileIfNotExist(final File file) throws IOException {
        if (!file.exists()) {
            if (file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IOException("Can't create dir to file " + file.getParentFile().getAbsolutePath());
            }
            if (!file.createNewFile()) {
                throw new IOException("Can't create file " + file.getAbsolutePath());
            }
        }
    }
}
