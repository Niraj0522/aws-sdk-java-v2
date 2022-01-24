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

package software.amazon.awssdk.auth.token;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.SdkAutoCloseable;

// TODO: Implement me
@SdkPublicApi
public final class DefaultAwsTokenProviderChain implements AwsTokenProvider, SdkAutoCloseable {

    private DefaultAwsTokenProviderChain() {
    }

    @Override
    public AwsToken resolveToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }

    public static DefaultAwsTokenProviderChain create() {
        return new DefaultAwsTokenProviderChain();
    }
}
