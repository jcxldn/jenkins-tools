// vars/postJob.groovy

import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException
import java.lang.Exception

// Currently supported outcomes.

// - ABORTED
// - SUCCESS
// - FAILURE

// Not Built // Unstable is not supported.

// Default function to call when the caller does not specify the called on[..] Closure.
def default() {
  echo '[postJob] No script specified.'
}

def call(Closure body, Closure onSuccess=default, Closure onFailure=default, Closure onAborted=default) {
  try {
    body()
  }
  catch (Exception err) {
    if (err instanceof FlowInterruptedException) {
      // Job aborted.
        echo '[postJob] setting currentResult to interrupted. (aborted)'
        currentBuild.result = 'ABORTED'
    } else {
      // General job failure.
      echo '[postJob] setting currentResult to FAILURE.'
      currentBuild.result = 'FAILURE'
    }
  } finally {
    if (currentBuild.result == null) {
      echo "[postJob] currentBuild.result not set, setting to currentBuild.currentResult (${currentBuild.currentResult})"
      currentBuild.result = currentBuild.currentResult
    }
      
	echo "[postJob] finally block triggered, currentBuild.result: ${currentBuild.result}"
      
    if (currentBuild.result == 'ABORTED') {
      echo '[postJob] Running script: 'ABORTED'
	  onAborted()
    }
        
    if (currentBuild.result == 'SUCCESS') {
      echo '[postJob] Running script: SUCCESS'
	  onSuccess()
    }
        
    if (currentBuild.result == 'FAILURE') {
      echo '[postJob] Running script: FAILURE'
	  onFailure()
    }
  }
}