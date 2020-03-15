import groovyx.net.http.FromServer
import groovyx.net.http.JavaHttpBuilder
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.text.SimpleDateFormat
import java.util.function.BiFunction

class RancherPluginExtension {
    String rancherUrl = null
    String apiBearerToken = null
    String projectId = null
    String workloadId = null
}

class RancherPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('rancher', RancherPluginExtension)

        project.task('redeployWorkload') {
            group = 'Rancher'
            description = 'Redeploy workload'

            doLast {
                validateExtension(extension)

                def headers = [Authorization: "Bearer ${extension.apiBearerToken}"]
                def workloadApiUri = "/v3/projects/${extension.projectId}/workloads/${extension.workloadId}"
                def workload

                def config = {
                    request.uri = extension.rancherUrl
                    request.uri.path = workloadApiUri
                    request.headers = headers
                    request.contentType = 'application/json'
                }

                JavaHttpBuilder.configure(config).get {
                    response.when("200", new BiFunction<FromServer, Object, Object>() {
                        @Override
                        Object apply(FromServer fromServer, Object response) {
                            println "Successfully got workload"
                            modifyAndStoreWorkload(response)
                            return response
                        }

                        private Object modifyAndStoreWorkload(response) {
                            workload = response
                            def now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date())
                            workload['annotations']['cattle.io/timestamp'] = now
                        }
                    })
                }

                JavaHttpBuilder.configure(config).put {
                    println("Updating workload to: $workload")
                    request.body = workload
                    response.success {
                        println "Successfully update workload"
                    }
                }
            }

        }
    }

    private static void validateExtension(RancherPluginExtension extension) {
        if (extension.rancherUrl == null) {
            throw new GradleException("rancherUrl is not set");
        }
        if (extension.apiToken == null) {
            throw new GradleException("apiToken is not set");
        }
        if (extension.projectId == null) {
            throw new GradleException("projectId is not set");
        }
        if (extension.workloadId == null) {
            throw new GradleException("workloadId is not set");
        }
    }

}