package com.example.taskdemo.commons.util

data class PagedData<Key: Any, T>(
    val data: List<T>,
    val totalCount: Int,
    val prevKey: Key?,
    val nextKey: Key?
)