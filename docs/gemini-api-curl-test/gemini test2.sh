# For submitting images that are lower than 20MB (which is what I use in the app)
# https://ai.google.dev/gemini-api/docs/vision?lang=rest#image-input

IMG_PATH="app_icon_og.png"

API_KEY_FILE="api_key.txt"

if [ -f "$API_KEY_FILE" ]; then
  read -r API_KEY < "$API_KEY_FILE"
else
  echo "Error: File '$API_KEY_FILE' not found."
  exit 1
fi

GOOGLE_API_KEY=$API_KEY

# disable line wrapping (output the base64 encoded data on a single line)
B64FLAGS="-w0"

# Encode the image data using base64 with appropriate flags
encoded_image_data=$(base64 $B64FLAGS $IMG_PATH)

# Create a temporary file to store the JSON request body, since curl has size limitations https://stackoverflow.com/questions/54090784/curl-argument-list-too-long
tempfile=$(mktemp)

echo "Created temp file (will be deleted at the end): ${tempfile}"

# cat <<EOF -> cat is used for reading a the next lines (starting at <<EOF) until it encounters a line containing only EOF
# > "$tempfile" -> is used to redirect the output of cat (what it read) to the file
cat <<EOF > "$tempfile"
{
  "contents": [{
    "parts":[
      {"text": "Caption this image."},
      {
        "inline_data": {
          "mime_type":"image/jpeg",
          "data": "$encoded_image_data"
        }
      }
    ]
  }]
}
EOF

curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$GOOGLE_API_KEY" \
  -H 'Content-Type: application/json' \
  -X POST \
  -d "@$tempfile"

# Clean up the temporary file
rm "$tempfile"
