/*  Copyright (C) 2015-2020 Andreas Böhler, Andreas Shimokawa, Carsten
    Pfeiffer, Cre3per, Daniel Dakhno, Daniele Gobbetti, Gordon Williams,
    Jean-François Greffier, João Paulo Barraca, José Rebelo, Kranz, ladbsoft,
    Manuel Ruß, maxirnilian, Pavel, Pavel Elagin, protomors, Quallenauge,
    Sami Alaoui, Sebastian Kranz, Sophanimus, tiparega, Vadim Kaushan

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import nodomain.freeyourgadget.gadgetbridge.R;

/**
 * For every supported device, a device type constant must exist.
 *
 * Note: they key of every constant is stored in the DB, so it is fixed forever,
 * and may not be changed.
 */
public enum DeviceType {
    UNKNOWN(-1, R.drawable.ic_device_unknown, R.drawable.ic_device_unknown_disabled, R.string.devicetype_unknown),
    PEBBLE(1, R.drawable.ic_device_pebble, R.drawable.ic_device_pebble_disabled, R.string.devicetype_pebble),
    PINETIME_JF(190, R.drawable.ic_device_pinetime, R.drawable.ic_device_pinetime_disabled, R.string.devicetype_pinetime_jf),
    NOTHING_EAR1(410, R.drawable.ic_device_nothingear, R.drawable.ic_device_nothingear_disabled, R.string.devicetype_nothingear1),
    BINARY_SENSOR(510, R.drawable.ic_device_unknown, R.drawable.ic_device_unknown_disabled, R.string.devicetype_binary_sensor),
    FLIPPER_ZERO(520, R.drawable.ic_device_flipper, R.drawable.ic_device_flipper_disabled, R.string.devicetype_flipper_zero),
    TEST(1000, R.drawable.ic_device_default, R.drawable.ic_device_default_disabled, R.string.devicetype_test);

    private final int key;
    @DrawableRes
    private final int defaultIcon;
    @DrawableRes
    private final int disabledIcon;
    @StringRes
    private final int name;

    DeviceType(int key, int defaultIcon, int disabledIcon, int name) {
        this.key = key;
        this.defaultIcon = defaultIcon;
        this.disabledIcon = disabledIcon;
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public boolean isSupported() {
        return this != UNKNOWN;
    }

    public static DeviceType fromKey(int key) {
        for (DeviceType type : values()) {
            if (type.key == key) {
                return type;
            }
        }
        return DeviceType.UNKNOWN;
    }

    @StringRes
    public int getName() {
        return name;
    }

    @DrawableRes
    public int getIcon() {
        return defaultIcon;
    }

    @DrawableRes
    public int getDisabledIcon() {
        return disabledIcon;
    }
}
