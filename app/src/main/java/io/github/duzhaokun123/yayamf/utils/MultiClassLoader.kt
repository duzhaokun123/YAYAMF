package io.github.duzhaokun123.yayamf.utils

object MultiClassLoader: ClassLoader() {
    private val classLoaders = mutableSetOf<ClassLoader>()

    fun addClassLoader(classLoader: ClassLoader) {
        classLoaders.add(classLoader)
    }

    override fun loadClass(name: String?): Class<*> {
        classLoaders.forEach {
            try {
                return it.loadClass(name)
            } catch (_: ClassNotFoundException) {
            }
        }
        throw ClassNotFoundException(name)
    }
}