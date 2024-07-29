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

package software.amazon.awssdk.core.internal.http.pipeline.stages;

import static software.amazon.awssdk.core.internal.util.ProgressListenerTestUtils.createSdkHttpRequest;
import static software.amazon.awssdk.core.internal.util.ProgressListenerTestUtils.createSdkResponseBuilder;
import static software.amazon.awssdk.core.internal.util.ProgressListenerTestUtils.progressListenerContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.progress.listener.DeafultProgressUpdater;
import software.amazon.awssdk.core.progress.listener.ProgressListener;

class AfterExecutionProgressReportingStageTest {

    @Test
    void afterExecutionProgressListener_calledFrom_ExecutionPipeline() throws Exception {
        ProgressListener progressListener = Mockito.mock(ProgressListener.class);

        SdkRequestOverrideConfiguration config = SdkRequestOverrideConfiguration.builder()
                                                                                .addProgressListener(progressListener)
                                                                                .build();

        SdkRequest request = createSdkHttpRequest(config).build();

        DeafultProgressUpdater deafultProgressUpdater = new DeafultProgressUpdater(request, null);

        RequestExecutionContext requestExecutionContext = progressListenerContext(false, request,
                                                                                  deafultProgressUpdater);

        SdkResponse response = createSdkResponseBuilder().build();

        AfterExecutionProgressReportingStage afterExecutionUpdateProgressStage = new AfterExecutionProgressReportingStage();
        afterExecutionUpdateProgressStage.execute(response, requestExecutionContext);

        Mockito.verify(progressListener, Mockito.times(0)).requestPrepared(Mockito.any());
        Mockito.verify(progressListener, Mockito.times(0)).requestBytesSent(Mockito.any());
        Mockito.verify(progressListener, Mockito.times(0)).responseHeaderReceived(Mockito.any());
        Mockito.verify(progressListener, Mockito.times(0)).responseBytesReceived(Mockito.any());
        Mockito.verify(progressListener, Mockito.times(1)).executionSuccess(Mockito.any());
        Mockito.verify(progressListener, Mockito.times(0)).executionFailure(Mockito.any());
    }
}
