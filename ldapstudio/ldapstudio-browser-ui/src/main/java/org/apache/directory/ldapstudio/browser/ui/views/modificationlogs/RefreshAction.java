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

package org.apache.directory.ldapstudio.browser.ui.views.modificationlogs;


import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.actions.BrowserAction;

import org.eclipse.jface.resource.ImageDescriptor;


public class RefreshAction extends BrowserAction implements ModelModifier
{

    private ModificationLogsView view;


    public RefreshAction( ModificationLogsView view )
    {
        super();
        this.view = view;
    }


    public void dispose()
    {
        super.dispose();
    }


    public void run()
    {
        // int topIndex = view.getMainWidget().getSourceViewer().getTopIndex();
        view.getUniversalListener().refreshInput();
        view.getUniversalListener().scrollToNewest();
        // view.getMainWidget().getSourceViewer().setTopIndex(topIndex);
    }


    public String getText()
    {
        return "Refresh";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_REFRESH );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return getInput() != null && ( getInput() instanceof ModificationLogsViewInput );
    }

}