package io.github.duzhaokun123.yayamf.xposed

interface BaseServiceHook {
    val serviceName: String
    fun onServiceAdded(serviceClassLoader: ClassLoader)
}