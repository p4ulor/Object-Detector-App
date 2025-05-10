#!/bin/bash

# UPDATE: It turns out this is the process for submitting images that are greater than 20MB (see gemini test2.sh for what I use in the app)
# https://ai.google.dev/gemini-api/docs/vision?lang=rest#upload-image

# README: The process of submitting an image for Gemini to analyse is has follows:
# curl request 1 - Sends information about the file you want to upload. This includes the file size, content type (MIME type), and a display name
# curl request 2 - based on the response headers placed in the file "upload-header.tmp", upload the file to the upload_url indicated by google
# curl request 3 - prompt Gemini along with the file_uri in mind
# https://ai.google.dev/api/generate-content
# https://ai.google.dev/api/files#files_create_image-SHELL
# 2> /dev/null is used to redirect stderr to a special directory in linux that permanently deletes the contents sent to it
# sudo apt install jq
# Put key in API_KEY_FILE before using

API_KEY_FILE="api_key.txt"

if [ -f "$API_KEY_FILE" ]; then
    read -r API_KEY < "$API_KEY_FILE"
else
    echo "Error: File '$API_KEY_FILE' not found."
    exit 1
fi

GOOGLE_API_KEY=$API_KEY
BASE_URL=https://generativelanguage.googleapis.com
IMG_PATH=app_icon_og.png
MIME_TYPE=$(file -b --mime-type "$IMG_PATH")
NUM_BYTES=$(wc -c < "$IMG_PATH")
DISPLAY_NAME=TEXT
tmp_header_file=upload-response-headers.tmp

# Initial resumable request defining metadata.
# The upload url is in the response headers, dump them to a file.
# with -d, it is a POST request
curl "$BASE_URL/upload/v1beta/files?key=$GOOGLE_API_KEY" \
  -D "$tmp_header_file" \
  -H "X-Goog-Upload-Protocol: resumable" \
  -H "X-Goog-Upload-Command: start" \
  -H "X-Goog-Upload-Header-Content-Length: $NUM_BYTES" \
  -H "X-Goog-Upload-Header-Content-Type: $MIME_TYPE" \
  -H "Content-Type: application/json" \
  -d "{'file': {'display_name': '$DISPLAY_NAME'}}" 2> /dev/null

upload_url=$(grep -i "x-goog-upload-url: " "$tmp_header_file" | cut -d" " -f2 | tr -d "\r")
# Warning: this prints your key on the screen because its in the URL!
echo "Image saved in $upload_url"
# rm "${tmp_header_file}"

# Upload the actual bytes (the image)
# with --data-binary, it is a POST request
curl "$upload_url" \
  -H "Content-Length: $NUM_BYTES" \
  -H "X-Goog-Upload-Offset: 0" \
  -H "X-Goog-Upload-Command: upload, finalize" \
  --data-binary "@${IMG_PATH}" 2> /dev/null > file_info.json

file_uri=$(jq ".file.uri" file_info.json) # Use JSON processor (jq) to get the property file.uri from the input file_info.json
echo file_uri=$file_uri

# Now request gemini to generate content using that file
curl "$BASE_URL/v1beta/models/gemini-1.5-flash:generateContent?key=$GOOGLE_API_KEY" \
    -H 'Content-Type: application/json' \
    -X POST \
    -d '{
      "contents": [{
        "parts":[
          {"text": "What do you see in this photo?"},
          {"file_data": {
              "mime_type": "image/png", 
              "file_uri": '$file_uri'
          }
        }]
        }]
       }' 2> /dev/null > response.json

cat response.json
echo

jq ".candidates[].content.parts[].text" response.json
