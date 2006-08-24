/*
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

package org.apache.changepw.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.changepw.messages.ChangePasswordReply;
import org.apache.kerberos.io.encoder.ApplicationReplyEncoder;
import org.apache.kerberos.io.encoder.PrivateMessageEncoder;
import org.apache.kerberos.messages.application.ApplicationReply;
import org.apache.kerberos.messages.application.PrivateMessage;

public class ChangePasswordReplyEncoder
{
    public void encode( ByteBuffer buf, ChangePasswordReply message ) throws IOException
    {
        // Build application reply bytes
        ApplicationReply appReply = message.getApplicationReply();
        ApplicationReplyEncoder appEncoder = new ApplicationReplyEncoder();
        byte[] encodedAppReply = appEncoder.encode( appReply );

        // Build private message bytes
        PrivateMessage privateMessage = message.getPrivateMessage();
        PrivateMessageEncoder privateEncoder = new PrivateMessageEncoder();
        byte[] privateBytes = privateEncoder.encode( privateMessage );

        short headerLength = 6;

        short messageLength = (short) ( headerLength + encodedAppReply.length + privateBytes.length );

        short protocolVersion = 1;

        buf.putShort( messageLength );
        buf.putShort( protocolVersion );
        buf.putShort( (short) encodedAppReply.length );

        buf.put( encodedAppReply );
        buf.put( privateBytes );
    }
}