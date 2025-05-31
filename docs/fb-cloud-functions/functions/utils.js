const fs = require('fs')
const readline = require('readline')
const path = require('path')

async function getObjectNames() {
    const filePath = path.resolve(__dirname, './mediapipe_detectable_objects.txt');
    const objectNames = []

    const fileStream = fs.createReadStream(filePath)
    const lines = readline.createInterface({
        input: fileStream,
        crlfDelay: Infinity, // recognize all instances of CR LF ('\r\n')
    })

    for await (const line of lines) { // readline.Interface implements the asynchronous iterator protocol, thus the await
        const trimmedLine = line.trim()
        if (trimmedLine && !trimmedLine.includes('???')) {
            objectNames.push(trimmedLine)
        }
    }

    return objectNames
}

module.exports = { getObjectNames }