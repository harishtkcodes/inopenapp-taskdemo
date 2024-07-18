package com.example.taskdemo.commons.util.persistent

interface KeyValuePersistentStorage {
    fun writeDateSet(dataSet: KeyValueDataSet, removes: Collection<String>)
    fun getDataSet(): KeyValueDataSet
}