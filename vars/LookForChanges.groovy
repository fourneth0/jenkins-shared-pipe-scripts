@Field String STEP_NAME = getClass().getName()

void call(Map parameters = [:], Closure body = null) {
    echo "Running step name ${STEP_NAME}"
    echo  params
    withCredentials([usernamePassword(credentialId: 'username-password-id1', passwordVariable: 'pwd', usernameVariable: 'username')]) {
        echo username
    }
}