# Rancher Gradle Plugin ![Java CI with Gradle](https://github.com/aremann/rancher-gradle-plugin/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

Redeploy a a workload via a gradle task.

## Usage

```shell
./gradlew redeployWorkload
```

## Installation

Add the plugin to your `build.gradle`

```groovy
plugins {
  id 'de.signal7.rancher-gradle-plugin' version '0.1.2'
}
```

And set the properties:

```
rancher {
    rancherUrl = https://my-rancher-installation.com
    apiBearerToken = token-4711:123456789
    projectId = 'local:p-xxxx'
    workloadId = 'deployment:namespace:workload'
}
```

