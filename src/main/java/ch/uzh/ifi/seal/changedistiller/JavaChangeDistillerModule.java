package ch.uzh.ifi.seal.changedistiller;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.ast.ASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaSourceCodeChangeClassifier;
import ch.uzh.ifi.seal.changedistiller.distilling.DistillerFactory;
import ch.uzh.ifi.seal.changedistiller.distilling.SourceCodeChangeClassifier;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Injection module to configure ChangeDistiller with the Java programming language.
 * 
 * @author Beat Fluri
 */
public class JavaChangeDistillerModule extends AbstractModule {

    @Override
    protected void configure() {    	
        bind(ASTNodeTypeConverter.class).to(JavaASTNodeTypeConverter.class);
        bind(SourceCodeChangeClassifier.class).to(JavaSourceCodeChangeClassifier.class);
        install(new FactoryModuleBuilder().build(DistillerFactory.class));
        install(new FactoryModuleBuilder().implement(ASTHelper.class, JavaASTHelper.class)
                .build(ASTHelperFactory.class));
    }
}
