/*
 * Copyright 1996-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package sun.awt.windows;

import java.awt.*;
import java.awt.peer.*;
import java.awt.event.ItemEvent;

class WChoicePeer extends WComponentPeer implements ChoicePeer {

    // WComponentPeer overrides

    public Dimension getMinimumSize() {
        FontMetrics fm = getFontMetrics(((Choice)target).getFont());
        Choice c = (Choice)target;
        int w = 0;
        for (int i = c.getItemCount() ; i-- > 0 ;) {
            w = Math.max(fm.stringWidth(c.getItem(i)), w);
        }
        return new Dimension(28 + w, Math.max(fm.getHeight() + 6, 15));
    }
    public boolean isFocusable() {
        return true;
    }

    // ChoicePeer implementation

    public native void select(int index);

    public void add(String item, int index) {
        addItem(item, index);
    }

    public boolean shouldClearRectBeforePaint() {
        return false;
    }

    public native void removeAll();
    public native void remove(int index);

    /**
     * DEPRECATED, but for now, called by add(String, int).
     */
    public void addItem(String item, int index) {
        addItems(new String[] {item}, index);
    }
    public native void addItems(String[] items, int index);

    public synchronized native void reshape(int x, int y, int width, int height);

    // Toolkit & peer internals

    WChoicePeer(Choice target) {
        super(target);
    }

    native void create(WComponentPeer parent);

    void initialize() {
        Choice opt = (Choice)target;
        int itemCount = opt.getItemCount();
        if (itemCount > 0) {
            String[] items = new String[itemCount];
            for (int i=0; i < itemCount; i++) {
                items[i] = opt.getItem(i);
            }
            addItems(items, 0);
            if (opt.getSelectedIndex() >= 0) {
                select(opt.getSelectedIndex());
            }
        }
        super.initialize();
    }

    // native callbacks

    void handleAction(final int index) {
        final Choice c = (Choice)target;
        WToolkit.executeOnEventHandlerThread(c, new Runnable() {
            public void run() {
                c.select(index);
                postEvent(new ItemEvent(c, ItemEvent.ITEM_STATE_CHANGED,
                                c.getItem(index), ItemEvent.SELECTED));
            }
        });
    }

    int getDropDownHeight() {
        Choice c = (Choice)target;
        FontMetrics fm = getFontMetrics(c.getFont());
        int maxItems = Math.min(c.getItemCount(), 8);
        return fm.getHeight() * maxItems;
    }

    /**
     * DEPRECATED
     */
    public Dimension minimumSize() {
            return getMinimumSize();
    }

}
