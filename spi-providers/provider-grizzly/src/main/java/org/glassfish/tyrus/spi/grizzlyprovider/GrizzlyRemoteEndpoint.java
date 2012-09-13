/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.tyrus.spi.grizzlyprovider;

import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.tyrus.spi.SPIRemoteEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author dannycoward
 */

public class GrizzlyRemoteEndpoint implements SPIRemoteEndpoint {
    private WebSocket socket;
    private static Set<GrizzlyRemoteEndpoint> sockets = Collections.newSetFromMap(new ConcurrentHashMap<GrizzlyRemoteEndpoint, Boolean>());

    public GrizzlyRemoteEndpoint(WebSocket socket) {
        this.socket = socket;
    }

    public static GrizzlyRemoteEndpoint get(WebSocket socket) {
        for (GrizzlyRemoteEndpoint gs : sockets) {
            if (gs.socket == socket) {
                return gs;
            }
        }
        GrizzlyRemoteEndpoint s = new GrizzlyRemoteEndpoint(socket);
        sockets.add(s);
        return s;
    }


    @Override
    public boolean isConnected() {
        return  this.socket.isConnected();
    }

    @Override
    public void send(String data) throws IOException {
        try {
            this.socket.send(data);
        } catch (Throwable t) {
            throw new IOException(t);
        }
    }

    public void send(byte[] data) throws IOException {
        this.socket.send(data);
    }

    @Override
    public void close(int code, String reason) throws IOException {
        this.socket.close(code, reason);
    }

    @Override
    public String getUri() {
        if(socket instanceof GrizzlySocket){
            return ((GrizzlySocket)socket).getRequest().getRequestURI();
        }
        return null;
    }

}