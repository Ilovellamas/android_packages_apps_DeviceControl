/*
 *  Copyright (C) 2013 - 2015 Alexander "Evisceration" Martinz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.namelessrom.devicecontrol.configuration;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.namelessrom.devicecontrol.Logger;
import org.namelessrom.devicecontrol.objects.BootupItem;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Bootup configuration which auto serializes itself to a file
 */
public class BootupConfiguration extends BaseConfiguration<BootupConfiguration> {
    public ArrayList<BootupItem> items = new ArrayList<>();

    private static BootupConfiguration sInstance;

    private BootupConfiguration(Context context) {
        loadConfiguration(context);
    }

    public static BootupConfiguration get(Context context) {
        if (sInstance == null) {
            sInstance = new BootupConfiguration(context);
        }
        return sInstance;
    }

    @Override protected String getConfigurationFile() {
        return "bootup_configuration.json";
    }

    @Override public BootupConfiguration loadConfiguration(Context context) {
        final BootupConfiguration config = loadRawConfiguration(context, BootupConfiguration.class);
        if (config == null) {
            return this;
        }

        this.items = config.items != null ? config.items : new ArrayList<BootupItem>();

        return this;
    }

    @Override public BootupConfiguration saveConfiguration(Context context) {
        saveConfigurationInternal(context);
        return this;
    }

    public ArrayList<BootupItem> getItemsByCategory(String category) {
        final ArrayList<BootupItem> filteredItems = new ArrayList<>();

        for (final BootupItem item : items) {
            if (item != null && category.equals(item.category)) {
                filteredItems.add(item);
            }
        }

        return filteredItems;
    }

    @Nullable public BootupItem getItemByName(String name) {
        for (final BootupItem item : items) {
            if (item != null && name.equals(item.name)) {
                return item;
            }
        }

        return null;
    }

    public synchronized BootupConfiguration addItem(@NonNull BootupItem bootupItem) {
        deleteItem(bootupItem);
        items.add(bootupItem);
        Logger.d(this, "added item -> %s", bootupItem.toString());

        return this;
    }

    public BootupConfiguration deleteItem(@NonNull BootupItem bootupItem) {
        final Iterator<BootupItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            BootupItem item = iterator.next();
            if (bootupItem.equals(item)) {
                iterator.remove();
                Logger.d(this, "removed item -> %s", item.toString());
            }
        }

        return this;
    }

    public static synchronized BootupConfiguration setBootup(Context context,
            @NonNull BootupItem item) {
        final BootupConfiguration config = BootupConfiguration.get(context);
        config.deleteItem(item);
        config.addItem(item);
        config.saveConfiguration(context);
        return config;
    }
}
