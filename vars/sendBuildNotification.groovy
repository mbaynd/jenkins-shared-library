def call(Map args = [:]) {
    def fromEmail = args.get('from', "QuickPay CI/CD <mbaynd@gmail.com>")
    def toEmail = args.get('to', "mbaynd@gmail.com;mbaynd@yahoo.fr;mbaye.ndiaye@quickpay.sn")

    def startTime = env.START_BUILD_DATETIME ? Date.parse("yyyy-MM-dd HH:mm:ss", env.START_BUILD_DATETIME).getTime() : System.currentTimeMillis()
    def endTime = System.currentTimeMillis()

    // Calculate and format duration
    def durationMillis = endTime - startTime
    def durationReadable = formatDuration(durationMillis)

    // Set end build time
    env.END_BUILD_DATETIME = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('UTC'))

    emailext attachLog: true,
        mimeType: 'text/html',
        subject: "${env.ENV}: ${env.JOB_BASE_NAME} --> ${currentBuild.result}",
        body: """<html>
            <p><strong>Project:</strong> ${env.JOB_NAME}</p>
            <p><strong>Start build time:</strong> ${env.START_BUILD_DATETIME}</p>
            <p><strong>End build time:</strong> ${env.END_BUILD_DATETIME}</p>
            <p><strong>Build Duration:</strong> ${durationReadable}</p>
            <p><strong>Build User Name:</strong> ${env.BUILD_USER}</p>
            <p><strong>Build User Email:</strong> ${env.BUILD_USER_EMAIL}</p>
            <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
            <p><strong>URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
        </html>""",
        from: fromEmail,
        to: toEmail
}

// Function to convert milliseconds to human-readable format
def formatDuration(long millis) {
    long seconds = millis / 1000 % 60
    long minutes = millis / (1000 * 60) % 60
    long hours = millis / (1000 * 60 * 60)

    return (hours > 0 ? "${hours}h " : "") +
           (minutes > 0 ? "${minutes}m " : "") +
           "${seconds}s"
}
