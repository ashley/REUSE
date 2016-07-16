package ch.uzh.ifi.seal.changedistiller.ast.java;

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

import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ARRAY_ACCESS;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ARRAY_CREATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ARRAY_INITIALIZER;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ARRAY_TYPE;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ASSERT_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ASSIGNMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.BLOCK;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.BOOLEAN_LITERAL;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.BREAK_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CAST_EXPRESSION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CHARACTER_LITERAL;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CLASS;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CLASS_INSTANCE_CREATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CONDITIONAL_EXPRESSION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CONSTRUCTOR_INVOCATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CONTINUE_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.DO_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.EMPTY_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.FIELD;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.FIELD_ACCESS;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.FOREACH_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.FOR_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.IF_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.INFIX_EXPRESSION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.INSTANCEOF_EXPRESSION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.JAVADOC;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.LABELED_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.METHOD;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.METHOD_INVOCATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.NULL_LITERAL;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.NUMBER_LITERAL;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.PARAMETER;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.PARAMETERIZED_TYPE;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.POSTFIX_EXPRESSION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.PREFIX_EXPRESSION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.QUALIFIED_NAME;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.QUALIFIED_TYPE;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.RETURN_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.SIMPLE_NAME;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.SINGLE_TYPE;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.STRING_LITERAL;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.SWITCH_CASE;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.SWITCH_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.SYNCHRONIZED_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.THROW_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.TRY_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.TYPE_LITERAL;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.TYPE_PARAMETER;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.VARIABLE_DECLARATION_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.WHILE_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.WILDCARD_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import ch.uzh.ifi.seal.changedistiller.ast.ASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;

/**
 * Implementation of {@link ASTNodeTypeConverter} for the Java programming language.
 * 
 * @author Beat Fluri
 * @author Michael Wuersch
 */
public final class JavaASTNodeTypeConverter implements ASTNodeTypeConverter {

    private static Map<Class<? extends ASTNode>, JavaEntityType> sConversionMap =
            new HashMap<Class<? extends ASTNode>, JavaEntityType>();
    // FIXME: I fixed this to work with the entity types presently defined
    // we might consider whether we should add/remove, though it looks like they
    // are defined wrt original eclipse types, so it might be fine.
    // FIXME: ensure there are no repetitions here, when doing above check
    static {
        sConversionMap.put(Assignment.class, ASSIGNMENT);
        sConversionMap.put(PostfixExpression.class, POSTFIX_EXPRESSION);
        sConversionMap.put(PrefixExpression.class, PREFIX_EXPRESSION);
        sConversionMap.put(ClassInstanceCreation.class, CLASS_INSTANCE_CREATION);
        sConversionMap.put(AssertStatement.class, ASSERT_STATEMENT);
        sConversionMap.put(BreakStatement.class, BREAK_STATEMENT);
        sConversionMap.put(ConstructorInvocation.class, CONSTRUCTOR_INVOCATION);
        sConversionMap.put(ContinueStatement.class, CONTINUE_STATEMENT);
        sConversionMap.put(DoStatement.class, DO_STATEMENT);
        sConversionMap.put(EmptyStatement.class, EMPTY_STATEMENT);
        sConversionMap.put(EnhancedForStatement.class, FOREACH_STATEMENT);
        sConversionMap.put(ForStatement.class, FOR_STATEMENT);
        sConversionMap.put(IfStatement.class, IF_STATEMENT);
        sConversionMap.put(LabeledStatement.class, LABELED_STATEMENT);
        sConversionMap.put(VariableDeclarationStatement.class, VARIABLE_DECLARATION_STATEMENT);
        sConversionMap.put(MethodInvocation.class, METHOD_INVOCATION);
        sConversionMap.put(ReturnStatement.class, RETURN_STATEMENT);
        sConversionMap.put(SwitchStatement.class, SWITCH_STATEMENT);
        sConversionMap.put(SwitchCase.class, SWITCH_CASE);
        sConversionMap.put(SimpleType.class, SINGLE_TYPE); // Bug #14: Cannot distinguish between primitive and simple types without resolving bindings; FIXME: do we want to?
        sConversionMap.put(PrimitiveType.class, SINGLE_TYPE); // Bug #14: Cannot distinguish between primitive and simple types without resolving bindings
        sConversionMap.put(SynchronizedStatement.class, SYNCHRONIZED_STATEMENT);
        sConversionMap.put(ThrowStatement.class, THROW_STATEMENT);
        sConversionMap.put(TryStatement.class, TRY_STATEMENT);
        sConversionMap.put(WhileStatement.class, WHILE_STATEMENT);
        sConversionMap.put(ParameterizedType.class, PARAMETERIZED_TYPE);
        sConversionMap.put(Javadoc.class, JAVADOC);
        sConversionMap.put(QualifiedType.class, QUALIFIED_TYPE);
       // sConversionMap.put(Argument.class, PARAMETER); FIXME: how to handle parameters?
        sConversionMap.put(TypeParameter.class, TYPE_PARAMETER);
        sConversionMap.put(WildcardType.class, WILDCARD_TYPE);
        sConversionMap.put(StringLiteral.class, STRING_LITERAL);
        sConversionMap.put(BooleanLiteral.class, BOOLEAN_LITERAL);
        sConversionMap.put(NullLiteral.class, NULL_LITERAL);
        sConversionMap.put(NumberLiteral.class, NUMBER_LITERAL);
        sConversionMap.put(CharacterLiteral.class, CHARACTER_LITERAL);
        sConversionMap.put(InfixExpression.class, INFIX_EXPRESSION);
        sConversionMap.put(ArrayCreation.class, ARRAY_CREATION);
        sConversionMap.put(ArrayInitializer.class, ARRAY_INITIALIZER);
        sConversionMap.put(ArrayAccess.class, ARRAY_ACCESS);
        sConversionMap.put(ArrayType.class, ARRAY_TYPE);
        sConversionMap.put(Block.class, BLOCK);
        sConversionMap.put(CastExpression.class, CAST_EXPRESSION);
        sConversionMap.put(TypeLiteral.class, TYPE_LITERAL);
        sConversionMap.put(ConditionalExpression.class, CONDITIONAL_EXPRESSION);
        sConversionMap.put(FieldAccess.class, FIELD_ACCESS);
        sConversionMap.put(QualifiedName.class, QUALIFIED_NAME);
        sConversionMap.put(SimpleName.class, SIMPLE_NAME);
        sConversionMap.put(PrefixExpression.class, PREFIX_EXPRESSION);
        sConversionMap.put(InstanceofExpression.class, INSTANCEOF_EXPRESSION);
        sConversionMap.put(FieldDeclaration.class, FIELD);
        sConversionMap.put(MethodDeclaration.class, METHOD);
        sConversionMap.put(TypeDeclaration.class, CLASS);
        sConversionMap.put(Initializer.class, FIELD); // FIXME: not clear this should map to field
    }

    @Override
    public EntityType convertNode(Object node) {
        if (!(node instanceof ASTNode)) {
            throw new RuntimeException("Node must be of type ASTNode.");
        }
        
        return sConversionMap.get(node.getClass());
    }

}
