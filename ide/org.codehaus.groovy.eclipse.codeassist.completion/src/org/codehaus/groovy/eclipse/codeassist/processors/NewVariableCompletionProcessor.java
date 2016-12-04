/*
 * Copyright 2009-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.eclipse.codeassist.processors;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * Completes new variable and field names when the type is known.
 *
 * @author andrew
 * @created Apr 29, 2011
 */
public class NewVariableCompletionProcessor extends AbstractGroovyCompletionProcessor {

    public NewVariableCompletionProcessor(ContentAssistContext context, JavaContentAssistInvocationContext javaContext,
            SearchableEnvironment nameEnvironment) {
        super(context, javaContext, nameEnvironment);
    }

    public List<ICompletionProposal> generateProposals(IProgressMonitor monitor) {
        // a: if completion text is empty
        // if in statement/script, search text backwards for previous token. Do
        // an ast walk for it. Is it a Type?
        // if in clas body, search text backwards on current line for something
        // that looks like a type name.
        // (can't look for types here since what would be type name is
        // interpreted as a field name

        // b: if completion text not empty
        // if in statement/script, see if current node is variable expression.
        // If so, then look to see if the accessed variable is itself. If so, do
        // completion.
        // if in class body, see if current node is a field node.
        return Collections.emptyList();
    }

}
