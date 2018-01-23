pipeline {
  agent any
  stages {
    stage('DataRelease') {
      steps {
        git(url: 'https://github.com/mpi2/PhenotypeData.git', branch: 'LoadsConfig')
      }
    }
  }
}