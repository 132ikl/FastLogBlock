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

package club.moddedminecraft.fastlogblockserver.models;

import java.nio.charset.Charset;

public class ASCIString implements CharSequence {
    private final static Charset ASCI = Charset.forName("ASCII");
    private final byte[] shortString;
    private int hashcode = -1;

    public ASCIString(final byte[] fatString) {
        shortString = fatString;

        initHash();
    }

    public ASCIString(final String fatString) {
        shortString = fatString.getBytes(ASCI);

        initHash();
    }

    @Override
    public int length() {
        return shortString.length;
    }

    @Override
    public char charAt(final int index) {
        return (char) shortString[index];
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new String(shortString, ASCI).subSequence(start, end);
    }

    @Override
    public int hashCode() {
        if (hashcode == -1) {
            initHash();
        }
        return hashcode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ASCIString)) {
            return false;
        }
        final byte[] alienBytes = ((ASCIString) obj).shortString;
        if (alienBytes.length != shortString.length) {
            return false;
        }
        for (int i = 0; i < alienBytes.length; i++) {
            if (alienBytes[i] != shortString[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return new String(shortString, ASCI);
    }

    public byte[] getShortString() {
        return shortString;
    }

    private void initHash() {
        int h = 0;
        for (int i = 0; i < length(); i++) {
            h = 31 * h + shortString[i];
        }
        hashcode = h;
    }
}
