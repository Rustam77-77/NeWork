plugins {
<<<<<<< HEAD
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
=======
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}