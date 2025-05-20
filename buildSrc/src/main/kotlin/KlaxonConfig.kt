object KlaxonConfig {
  val version = "6.0.0-SNAPSHOT"
  val groupId = "com.beust"
  val artifactId = "klaxon"
  val description = "A JSON parsing library"
  val url = "https://github.com/cbeust/klaxon"
  val scm = "github.com/cbeust/klaxon.git"

  // Should not need to change anything below
  val issueManagementUrl = "https://$scm/issues"
  val isSnapshot = version.contains("SNAPSHOT")
  val developers = listOf(
    Developer(
        id = "cbeust",
        name = "Cedric Beust",
        email = "cedric@beust.com",
        role = "author"
    ),
    Developer(
        id = "desiderantes",
        name = "Mario Daniel Ruiz Saavedra",
        email = "desiderantes93@gmail.com",
        role = "maintainer"
    )
  )
}

data class Developer(val id: String, val name: String, val email: String, val role: String)