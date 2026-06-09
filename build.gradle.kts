
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

configurations {
    named("apiImplementation") { extendsFrom(configurations["implementation"]) }
    named("apiCompileOnly") { extendsFrom(configurations["compileOnly"]) }
}

tasks.named<Jar>("jar") {
    from(sourceSets["api"].output)
}

tasks.named<Jar>("shadowJar") {
    from(sourceSets["api"].output)
}