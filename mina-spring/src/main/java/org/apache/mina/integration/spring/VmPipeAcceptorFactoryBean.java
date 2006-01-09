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
package org.apache.mina.integration.spring;

import java.net.SocketAddress;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.integration.spring.support.AbstractIoAcceptorFactoryBean;
import org.apache.mina.transport.vmpipe.VmPipeAcceptor;
import org.apache.mina.transport.vmpipe.VmPipeAddress;
import org.springframework.util.Assert;

/**
 * {@link AbstractIoAcceptorFactoryBean} implementation which allows for easy
 * configuration of {@link VmPipeAcceptor} instances using Spring. Example of
 * usage:
 * <p>
 * 
 * <pre>
 *   &lt;!-- My IoHandler implementation--&gt;
 *   &lt;bean id=&quot;myHandler&quot; class=&quot;com.foo.bar.MyHandler&quot;&gt;
 *       ...
 *   
 *   &lt;!-- Create a thread pool filter --&gt;
 *   &lt;bean id=&quot;threadPoolFilter&quot; 
 *         class=&quot;org.apache.mina.filter.ThreadPoolFilter&quot;&gt;
 *     &lt;!-- Threads will be named IoWorker-1, IoWorker-2, etc --&gt;
 *     &lt;constructor-arg value=&quot;IoWorker&quot;/&gt;
 *     &lt;property name=&quot;maximumPoolSize&quot; value=&quot;10&quot;/&gt;
 *   &lt;/bean&gt;
 *   
 *   &lt;!-- Create the DatagramAcceptor --&gt;
 *   &lt;bean id=&quot;vmPipeAcceptor&quot; 
 *        class=&quot;org.apache.mina.integration.spring.VmPipeAcceptorFactoryBean&quot;&gt;
 *    &lt;property name=&quot;filters&quot;&gt;
 *      &lt;list&gt;
 *        &lt;ref local=&quot;threadPoolFilter&quot;/&gt;
 *      &lt;/list&gt;
 *    &lt;/property&gt;
 *    &lt;property name=&quot;bindings&quot;&gt;
 *      &lt;list&gt;
 *        &lt;bean class=&quot;org.apache.mina.integration.spring.Binding&quot;&gt;
 *          &lt;property name=&quot;address&quot; value=&quot;:9287&quot;/&gt;
 *          &lt;property name=&quot;handler&quot; ref=&quot;myHandler&quot;/&gt;
 *        &lt;/bean&gt;
 *        &lt;bean class=&quot;org.apache.mina.integration.spring.Binding&quot;&gt;
 *          &lt;property name=&quot;address&quot; value=&quot;:6273&quot;/&gt;
 *          &lt;property name=&quot;handler&quot; ref=&quot;myHandler&quot;/&gt;
 *        &lt;/bean&gt;
 *      &lt;/list&gt;
 *    &lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * </p>
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public class VmPipeAcceptorFactoryBean extends AbstractIoAcceptorFactoryBean
{

    protected IoAcceptor createIoAcceptor() throws Exception
    {
        return new VmPipeAcceptor();
    }

    protected SocketAddress parseSocketAddress( String s )
    {
        Assert.notNull( s, "null SocketAddress string" );
        s = s.trim();
        if( s.startsWith( ":" ) )
        {
            s = s.substring( 1 );
        }
        try
        {
            return new VmPipeAddress( Integer.parseInt( s.trim() ) );
        }
        catch( NumberFormatException nfe )
        {
            throw new IllegalArgumentException( "Illegal vm pipe address: " + s );
        }
    }
}
