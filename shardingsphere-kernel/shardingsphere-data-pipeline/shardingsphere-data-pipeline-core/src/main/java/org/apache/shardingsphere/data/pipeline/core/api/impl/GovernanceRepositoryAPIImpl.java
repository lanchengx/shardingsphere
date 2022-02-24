/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.data.pipeline.core.api.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.data.pipeline.api.job.JobStatus;
import org.apache.shardingsphere.data.pipeline.api.job.progress.JobProgress;
import org.apache.shardingsphere.data.pipeline.api.task.progress.IncrementalTaskProgress;
import org.apache.shardingsphere.data.pipeline.api.task.progress.InventoryTaskProgress;
import org.apache.shardingsphere.data.pipeline.core.api.GovernanceRepositoryAPI;
import org.apache.shardingsphere.data.pipeline.core.constant.DataPipelineConstants;
import org.apache.shardingsphere.data.pipeline.core.job.progress.yaml.JobProgressYamlSwapper;
import org.apache.shardingsphere.data.pipeline.core.job.progress.yaml.YamlJobProgress;
import org.apache.shardingsphere.data.pipeline.core.task.IncrementalTask;
import org.apache.shardingsphere.data.pipeline.core.task.InventoryTask;
import org.apache.shardingsphere.data.pipeline.scenario.rulealtered.RuleAlteredJobContext;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;
import org.apache.shardingsphere.mode.repository.cluster.ClusterPersistRepository;
import org.apache.shardingsphere.mode.repository.cluster.listener.DataChangedEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Governance repository API impl.
 */
@RequiredArgsConstructor
@Slf4j
public final class GovernanceRepositoryAPIImpl implements GovernanceRepositoryAPI {
    
    private static final JobProgressYamlSwapper JOB_PROGRESS_YAML_SWAPPER = new JobProgressYamlSwapper();
    
    private final ClusterPersistRepository repository;
    
    @Override
    public void persistJobProgress(final RuleAlteredJobContext jobContext) {
        JobProgress jobProgress = new JobProgress();
        jobProgress.setStatus(jobContext.getStatus());
        jobProgress.setSourceDatabaseType(jobContext.getJobConfig().getHandleConfig().getSourceDatabaseType());
        jobProgress.setIncrementalTaskProgressMap(getIncrementalTaskProgressMap(jobContext));
        jobProgress.setInventoryTaskProgressMap(getInventoryTaskProgressMap(jobContext));
        String value = YamlEngine.marshal(JOB_PROGRESS_YAML_SWAPPER.swapToYaml(jobProgress));
        repository.persist(getOffsetPath(jobContext.getJobId(), jobContext.getShardingItem()), value);
    }
    
    private Map<String, IncrementalTaskProgress> getIncrementalTaskProgressMap(final RuleAlteredJobContext jobContext) {
        Map<String, IncrementalTaskProgress> result = new HashMap<>(jobContext.getIncrementalTasks().size(), 1);
        for (IncrementalTask each : jobContext.getIncrementalTasks()) {
            result.put(each.getTaskId(), each.getProgress());
        }
        return result;
    }
    
    private Map<String, InventoryTaskProgress> getInventoryTaskProgressMap(final RuleAlteredJobContext jobContext) {
        Map<String, InventoryTaskProgress> result = new HashMap<>(jobContext.getInventoryTasks().size(), 1);
        for (InventoryTask each : jobContext.getInventoryTasks()) {
            result.put(each.getTaskId(), each.getProgress());
        }
        return result;
    }
    
    @Override
    public JobProgress getJobProgress(final String jobId, final int shardingItem) {
        String data = repository.get(getOffsetPath(jobId, shardingItem));
        if (Strings.isNullOrEmpty(data)) {
            return null;
        }
        return JOB_PROGRESS_YAML_SWAPPER.swapToObject(YamlEngine.unmarshal(data, YamlJobProgress.class));
    }
    
    @Override
    public void persistJobCheckResult(final String jobId, final boolean checkSuccess) {
        log.info("persist job check result '{}' for job {}", checkSuccess, jobId);
        repository.persist(getCheckResultPath(jobId), String.valueOf(checkSuccess));
    }
    
    private String getCheckResultPath(final String jobId) {
        return String.format("%s/%s/check/result", DataPipelineConstants.DATA_PIPELINE_ROOT, jobId);
    }
    
    @Override
    public Optional<Boolean> getJobCheckResult(final String jobId) {
        String data = repository.get(getCheckResultPath(jobId));
        return Strings.isNullOrEmpty(data) ? Optional.empty() : Optional.of(Boolean.parseBoolean(data));
    }
    
    @Override
    public void deleteJob(final String jobId) {
        log.info("delete job {}", jobId);
        repository.delete(String.format("%s/%s", DataPipelineConstants.DATA_PIPELINE_ROOT, jobId));
    }
    
    @Override
    public List<String> getChildrenKeys(final String key) {
        return repository.getChildrenKeys(key);
    }
    
    @Override
    public void watch(final String key, final DataChangedEventListener listener) {
        repository.watch(key, listener);
    }
    
    @Override
    public void persist(final String key, final String value) {
        repository.persist(key, value);
    }
    
    @Override
    public void renewJobStatus(final JobStatus status, final String jobId) {
        List<String> offsetKeys = getChildrenKeys(String.format("%s/%s/offset", DataPipelineConstants.DATA_PIPELINE_ROOT, jobId));
        Map<Integer, JobProgress> progressMap = Maps.newHashMap();
        offsetKeys.forEach(each -> progressMap.put(Integer.parseInt(each), getJobProgress(jobId, Integer.parseInt(each))));
        progressMap.forEach((key, value) -> {
            value.setStatus(status);
            persist(getOffsetPath(jobId, key), YamlEngine.marshal(JOB_PROGRESS_YAML_SWAPPER.swapToYaml(value)));
        });
    }
    
    private String getOffsetPath(final String jobId, final int shardingItem) {
        return String.format("%s/%s/offset/%d", DataPipelineConstants.DATA_PIPELINE_ROOT, jobId, shardingItem);
    }
}
