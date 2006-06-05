/*
 *   @(#) $Id$
 *
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.mina.transport.socket.nio;

import org.apache.mina.common.IoSessionManager;

/**
 * An {@link IoSessionManager} which provides some properties related with
 * NIO sockets.
 *
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev$, $Date$
 *
 */
public interface SocketSessionManager
{
    /**
     * Returns the number of worker threads assigned to perform socket I/O.
     */
    int getProcessors();
    
    /**
     * Sets the number of worker threads assigned to perform socket I/O.
     * Decreasing this number won't stop threads immediately.  It will
     * not stop until all sessions assigned to it are closed.
     */
    void setProcessors( int nProcessor );
}