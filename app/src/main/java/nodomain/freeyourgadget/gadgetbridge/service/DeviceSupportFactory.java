/*  Copyright (C) 2015-2021 0nse, 115ek, Andreas Böhler, Andreas Shimokawa,
    angelpup, Carsten Pfeiffer, Cre3per, criogenic, DanialHanif, Daniel Dakhno,
    Daniele Gobbetti, Dmytro Bielik, Gordon Williams, Jean-François Greffier,
    João Paulo Barraca, José Rebelo, ladbsoft, Manuel Ruß, maxirnilian,
    mkusnierz, odavo32nof, opavlov, pangwalla, Pavel Elagin, protomors,
    Quallenauge, Sami Alaoui, Sebastian Kranz, Sergey Trofimov, Sophanimus,
    Taavi Eomäe, tiparega, Vadim Kaushan, Yukai Li

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
package nodomain.freeyourgadget.gadgetbridge.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.widget.Toast;

import java.lang.reflect.Constructor;

import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.devices.binary_sensor.BinarySensorSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.flipper.zero.support.FlipperZeroSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.nothing.Ear1Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pinetime.PineTimeJFSupport;
import nodomain.freeyourgadget.gadgetbridge.util.GB;

public class DeviceSupportFactory {
    private final BluetoothAdapter mBtAdapter;
    private final Context mContext;

    DeviceSupportFactory(Context context) {
        mContext = context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized DeviceSupport createDeviceSupport(GBDevice device) throws GBException {
        DeviceSupport deviceSupport;
        String deviceAddress = device.getAddress();
        int indexFirstColon = deviceAddress.indexOf(":");
        if (indexFirstColon > 0) {
            if (indexFirstColon == deviceAddress.lastIndexOf(":")) { // only one colon
                deviceSupport = createTCPDeviceSupport(device);
            } else {
                // multiple colons -- bt?
                deviceSupport = createBTDeviceSupport(device);
            }
        } else {
            // no colon at all, maybe a class name?
            deviceSupport = createClassNameDeviceSupport(device);
        }

        if (deviceSupport != null) {
            return deviceSupport;
        }

        // no device found, check transport availability and warn
        checkBtAvailability();
        return null;
    }

    private DeviceSupport createClassNameDeviceSupport(GBDevice device) throws GBException {
        String className = device.getAddress();
        try {
            Class<?> deviceSupportClass = Class.forName(className);
            Constructor<?> constructor = deviceSupportClass.getConstructor();
            DeviceSupport support = (DeviceSupport) constructor.newInstance();
            // has to create the device itself
            support.setContext(device, null, mContext);
            return support;
        } catch (ClassNotFoundException e) {
            return null; // not a class, or not known at least
        } catch (Exception e) {
            throw new GBException("Error creating DeviceSupport instance for " + className, e);
        }
    }

    private void checkBtAvailability() {
        if (mBtAdapter == null) {
            GB.toast(mContext.getString(R.string.bluetooth_is_not_supported_), Toast.LENGTH_SHORT, GB.WARN);
        } else if (!mBtAdapter.isEnabled()) {
            GB.toast(mContext.getString(R.string.bluetooth_is_disabled_), Toast.LENGTH_SHORT, GB.WARN);
        }
    }

    private ServiceDeviceSupport createServiceDeviceSupport(GBDevice device){
        switch (device.getType()) {
            case PEBBLE:
                return new ServiceDeviceSupport(new PebbleSupport());
            case PINETIME_JF:
                return new ServiceDeviceSupport(new PineTimeJFSupport());
           case NOTHING_EAR1:
                return new ServiceDeviceSupport(new Ear1Support());
            case BINARY_SENSOR:
                return new ServiceDeviceSupport(new BinarySensorSupport());
            case FLIPPER_ZERO:
                return new ServiceDeviceSupport(new FlipperZeroSupport());

        }
        return null;
    }

    private DeviceSupport createBTDeviceSupport(GBDevice gbDevice) throws GBException {
        if (mBtAdapter != null && mBtAdapter.isEnabled()) {
            try {
                DeviceSupport deviceSupport = createServiceDeviceSupport(gbDevice);
                if (deviceSupport != null) {
                    deviceSupport.setContext(gbDevice, mBtAdapter, mContext);
                    return deviceSupport;
                }
            } catch (Exception e) {
                throw new GBException(mContext.getString(R.string.cannot_connect_bt_address_invalid_), e);
            }
        }
        return null;
    }

    private DeviceSupport createTCPDeviceSupport(GBDevice gbDevice) throws GBException {
        try {
            DeviceSupport deviceSupport = new ServiceDeviceSupport(new PebbleSupport(), ServiceDeviceSupport.Flags.BUSY_CHECKING);
            deviceSupport.setContext(gbDevice, mBtAdapter, mContext);
            return deviceSupport;
        } catch (Exception e) {
            throw new GBException("cannot connect to " + gbDevice, e); // FIXME: localize
        }
    }

}
