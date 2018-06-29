package com.xiahui.kmeansdecision;

/**
 * Created by XH on 2017/3/23.
 * 决策App中的决策算法接口
 */
public interface IDecision {
    /**
     * @param serviceExecuteTime 本地执行所需时间，单位ms
     * @param cloudExecuteTime  云端执行所需时间，单位ms
     * @param transmissionTime 网络传输所需时间，单位ms
     * @return ture:云端执行  false:本地执行
     */
    boolean makeDecision(long serviceExecuteTime, long cloudExecuteTime, long transmissionTime);
}