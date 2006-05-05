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
package org.apache.mina.handler.multiton;

import org.apache.mina.common.IoSession;

/**
 * A factory that creates {@link SingleSessionIoHandler} to be used with one
 * particular session.
 * 
 * @see SingleSessionIoHandler
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public interface SingleSessionIoHandlerFactory {
	
	/**
	 * Returns a {@link SingleSessionIoHandler} for the given session.
	 * 
	 * @param session the session for which a handler is requested
	 */
	SingleSessionIoHandler getHandler(IoSession session);
}
