/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.server.dhcp.options.dhcp;


import java.nio.ByteBuffer;

import org.apache.directory.server.dhcp.options.DhcpOption;


/**
 * This option is used to identify a TFTP server when the 'sname' field
 * in the DHCP header has been used for DHCP options.
 * 
 * The code for this option is 66, and its minimum length is 1.
 */
public class TftpServerName extends DhcpOption
{
    private byte[] tftpServerName;


    public TftpServerName(byte[] tftpServerName)
    {
        super( 66, 1 );
        this.tftpServerName = tftpServerName;
    }


    protected void valueToByteBuffer( ByteBuffer out )
    {
        out.put( tftpServerName );
    }
}