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

package software.amazon.awssdk.services.sqs.internal.batchmanager;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sqs.batchmanager.BatchOverrideConfiguration;

@SdkInternalApi
public final class ResponseBatchConfiguration {

    public static final boolean LONG_POLL_DEFAULT = true;
    public static final Duration VISIBILITY_TIMEOUT_SECONDS_DEFAULT = null;
    public static final Duration LONG_POLL_WAIT_TIMEOUT_DEFAULT = Duration.ofSeconds(20);
    public static final Duration MIN_RECEIVE_WAIT_TIME_MS_DEFAULT = Duration.ofMillis(50);
    public static final List<String> RECEIVE_MESSAGE_ATTRIBUTE_NAMES_DEFAULT = Collections.emptyList();
    public static final List<String> RECEIVE_ATTRIBUTE_NAMES_DEFAULT = Collections.emptyList();
    public static final boolean ADAPTIVE_PREFETCHING_DEFAULT = false;
    public static final int MAX_BATCH_ITEMS_DEFAULT = 10;
    public static final int MAX_INFLIGHT_RECEIVE_BATCHES_DEFAULT = 10;
    public static final int MAX_DONE_RECEIVE_BATCHES_DEFAULT = 10;

    private final Duration visibilityTimeout;
    private final Duration longPollWaitTimeout;
    private final Duration minReceiveWaitTime;
    private final List<String> receiveAttributeNames;
    private final List<String> receiveMessageAttributeNames;
    private final Boolean adaptivePrefetching;
    private final Integer maxBatchItems;
    private final Integer maxInflightReceiveBatches;
    private final Integer maxDoneReceiveBatches;

    public ResponseBatchConfiguration(BatchOverrideConfiguration overrideConfiguration) {
        this.visibilityTimeout = overrideConfiguration.visibilityTimeout() != null
                                 ? overrideConfiguration.visibilityTimeout()
                                 : VISIBILITY_TIMEOUT_SECONDS_DEFAULT;
        this.longPollWaitTimeout = overrideConfiguration.longPollWaitTimeout() != null
                                   ? overrideConfiguration.longPollWaitTimeout()
                                   : LONG_POLL_WAIT_TIMEOUT_DEFAULT;
        this.minReceiveWaitTime = overrideConfiguration.minReceiveWaitTime() != null
                                  ? overrideConfiguration.minReceiveWaitTime()
                                  : MIN_RECEIVE_WAIT_TIME_MS_DEFAULT;
        this.receiveAttributeNames = overrideConfiguration.receiveAttributeNames().isEmpty()
                                     ? RECEIVE_ATTRIBUTE_NAMES_DEFAULT
                                     : overrideConfiguration.receiveAttributeNames();
        this.receiveMessageAttributeNames = overrideConfiguration.receiveMessageAttributeNames().isEmpty()
                                            ? RECEIVE_MESSAGE_ATTRIBUTE_NAMES_DEFAULT
                                            : overrideConfiguration.receiveMessageAttributeNames();
        this.adaptivePrefetching = overrideConfiguration.adaptivePrefetching() != null
                                   ? overrideConfiguration.adaptivePrefetching()
                                   : ADAPTIVE_PREFETCHING_DEFAULT;
        this.maxBatchItems = overrideConfiguration.maxBatchItems() != null
                             ? overrideConfiguration.maxBatchItems()
                             : MAX_BATCH_ITEMS_DEFAULT;
        this.maxInflightReceiveBatches = overrideConfiguration.maxInflightReceiveBatches() != null
                                         ? overrideConfiguration.maxInflightReceiveBatches()
                                         : MAX_INFLIGHT_RECEIVE_BATCHES_DEFAULT;
        this.maxDoneReceiveBatches = overrideConfiguration.maxDoneReceiveBatches() != null
                                     ? overrideConfiguration.maxDoneReceiveBatches()
                                     : MAX_DONE_RECEIVE_BATCHES_DEFAULT;
    }

    public Duration visibilityTimeout() {
        return visibilityTimeout;
    }

    public Duration longPollWaitTimeout() {
        return longPollWaitTimeout;
    }

    public Duration minReceiveWaitTime() {
        return minReceiveWaitTime;
    }

    public List<String> receiveAttributeNames() {
        return Collections.unmodifiableList(receiveAttributeNames);
    }

    public List<String> receiveMessageAttributeNames() {
        return Collections.unmodifiableList(receiveMessageAttributeNames);
    }

    public boolean adaptivePrefetching() {
        return adaptivePrefetching;
    }

    public int maxBatchItems() {
        return maxBatchItems;
    }

    public int maxInflightReceiveBatches() {
        return maxInflightReceiveBatches;
    }

    public int maxDoneReceiveBatches() {
        return maxDoneReceiveBatches;
    }
}