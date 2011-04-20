package templates

import org.gradle.api.Project
import org.gradle.api.Plugin

class WebappTemplatesPlugin extends JavaTemplatesPlugin implements Plugin<Project> {
   void createBase(String path = System.getProperty("user.dir"), String projectName) {
      super.createBase(path)
      ProjectTemplate.fromRoot(path) {
         "src/main/webapp/WEB-INF" {
            "web.xml" template: "/templates/webapp/web-xml.tmpl", project: [name: projectName]
         }
      }
   }

   void apply(Project project) {
      project.apply(plugin: "java-templates")
      
      project.task("createWebappProject", group: TemplatesPlugin.group, description: "Creates a new Gradle Webapp project in a new directory named after your project.") << {
         def projectName = TemplatesPlugin.prompt("Project Name:")
         def useJetty = TemplatesPlugin.promptYesOrNo("Use Jetty Plugin?")
         if (projectName) {
            createBase(projectName, projectName)
            ProjectTemplate.fromRoot(projectName) {
               "build.gradle" template: "/templates/webapp/build.gradle.tmpl", useJetty: useJetty
            }
         } else {
            println "No project name provided."
         }
      }
      project.task("initWebappProject", group: TemplatesPlugin.group, description: "Initializes a new Gradle Webapp project in the current directory.") << {
         createBase(project.name)
         def useJetty = TemplatesPlugin.promptYesOrNo("Use Jetty Plugin?")
         if(useJetty) {
            TemplatesPlugin.prependPlugin "jetty", new File("build.gradle")
         }
         TemplatesPlugin.prependPlugin "war", new File("build.gradle")
      }

   }
}