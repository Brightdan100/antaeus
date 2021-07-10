plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":pleo-antaeus-data"))
//    implementation("io.jooby:jooby-quartz:2.9.6")
    api(project(":pleo-antaeus-models"))
}