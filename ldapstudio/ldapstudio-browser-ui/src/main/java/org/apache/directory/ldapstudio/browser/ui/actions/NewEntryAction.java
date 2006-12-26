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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.wizards.NewEntryWizard;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;


public class NewEntryAction extends BrowserAction
{

    private IWorkbenchWindow window;


    public NewEntryAction()
    {
    }


    public NewEntryAction( IWorkbenchWindow window )
    {
        super();
        this.window = window;
    }


    public void dispose()
    {
        super.dispose();
        this.window = null;
    }


    public void init( IWorkbenchWindow window )
    {
        super.init( window );
        this.window = window;
    }


    public void run()
    {
        NewEntryWizard wizard = new NewEntryWizard();

        wizard.init( this.window.getWorkbench(), ( IStructuredSelection ) this.window.getSelectionService()
            .getSelection() );
        WizardDialog dialog = new WizardDialog( getShell(), wizard );
        dialog.setBlockOnOpen( true );
        dialog.create();
        dialog.open();
    }


    public String getText()
    {
        return "New Entry...";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_ADD );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return this.getSelectedEntries().length + this.getSelectedSearchResults().length
            + this.getSelectedBookmarks().length == 1;
        // return getSelectedEntries().length == 1;
    }
}