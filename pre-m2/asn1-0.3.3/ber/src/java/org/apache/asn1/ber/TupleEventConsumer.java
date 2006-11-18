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
package org.apache.asn1.ber;

import java.nio.ByteBuffer;


/**
 * Experimental shared interface for both Tuple event producers and consumers.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public interface TupleEventConsumer
{
    public void tag( Tuple tlv );
    public void length( Tuple tlv );
    public void chunkedValue( Tuple tlv, ByteBuffer chunk );
    public void finish( Tuple tlv );
}