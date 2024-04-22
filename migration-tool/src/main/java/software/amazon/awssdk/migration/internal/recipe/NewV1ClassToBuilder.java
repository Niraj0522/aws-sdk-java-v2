/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.migration.internal.recipe;

import java.util.Collections;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JContainer;
import org.openrewrite.java.tree.JRightPadded;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.Space;
import org.openrewrite.marker.Markers;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.migration.internal.utils.NamingUtils;
import software.amazon.awssdk.migration.internal.utils.SdkTypeUtils;
import software.amazon.awssdk.migration.recipe.NewV1ModelClassToV2;

/**
 * Internal recipe that converts V1 model creation to the builder pattern.
 *
 * @see NewV1ModelClassToV2
 */
@SdkInternalApi
public class NewV1ClassToBuilder extends Recipe {
    @Override
    public String getDisplayName() {
        return "Transform 'new' expressions to builders";
    }

    @Override
    public String getDescription() {
        return "Transforms 'new' expression for V1 model objects to the equivalent builder()..build() expression in V2.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new NewV1ClassToBuilderVisitor();
    }

    // Change a new Foo() to Foo.builder().build()
    // Any withers called on new Foo() are moved to before .build()
    // Make any appropriate v1 -> v2 type changes
    private static class NewV1ClassToBuilderVisitor extends JavaVisitor<ExecutionContext> {
        // Rearrange a [...].build().with*() to [...].with*().build()
        @Override
        public J visitMethodInvocation(J.MethodInvocation method, ExecutionContext executionContext) {
            method = super.visitMethodInvocation(method, executionContext).cast();

            // [...].with*()
            if (!NamingUtils.isWither(method.getSimpleName())) {
                return method;
            }

            Expression select = method.getSelect();

            if (!(select instanceof J.MethodInvocation)) {
                return method;
            }

            // [...]
            J.MethodInvocation selectInvoke = (J.MethodInvocation) select;

            // [...] == [...].build()
            if (!selectInvoke.getSimpleName().equals("build")) {
                return method;
            }

            // Do the reordering
            Expression selectInvokeSelect = selectInvoke.getSelect();

            J.MethodInvocation newWith = method.withSelect(selectInvokeSelect);

            return selectInvoke.withSelect(newWith);
        }

        // new Foo() -> Foo.builder().build()
        @Override
        public J visitNewClass(J.NewClass newClass, ExecutionContext executionContext) {
            newClass = super.visitNewClass(newClass, executionContext).cast();

            JavaType classType = newClass.getType();
            if (!SdkTypeUtils.isV1ModelClass(classType)) {
                return newClass;
            }

            JavaType.FullyQualified v2Type = SdkTypeUtils.asV2Type((JavaType.FullyQualified) classType);
            JavaType.FullyQualified v2TypeBuilder = SdkTypeUtils.v2ModelBuilder(v2Type);

            J.Identifier modelId = new J.Identifier(
                Tree.randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                Collections.emptyList(),
                v2Type.getClassName(),
                v2Type,
                null
            );

            J.Identifier builderMethod = new J.Identifier(
                Tree.randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                Collections.emptyList(),
                "builder",
                null,
                null
            );

            JavaType.Method methodType = new JavaType.Method(
                null,
                0L,
                v2Type,
                "builder",
                v2TypeBuilder,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            );

            J.MethodInvocation builderInvoke = new J.MethodInvocation(
                Tree.randomId(),
                Space.EMPTY,
                Markers.EMPTY,
                JRightPadded.build(modelId),
                null,
                builderMethod,
                JContainer.empty(),
                methodType

            );

            J.MethodInvocation buildInvoke = new J.MethodInvocation(
                Tree.randomId(),
                newClass.getPrefix(),
                Markers.EMPTY,
                JRightPadded.build(builderInvoke),
                null,
                new J.Identifier(
                    Tree.randomId(),
                    Space.EMPTY,
                    Markers.EMPTY,
                    Collections.emptyList(),
                    "build",
                    v2Type,
                    null
                ),
                JContainer.empty(),
                new JavaType.Method(
                    null,
                    0L,
                    v2TypeBuilder,
                    "build",
                    v2Type,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
                )
            );

            return buildInvoke;
        }
    }
}
