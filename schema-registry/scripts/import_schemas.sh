#!/usr/bin/env bash
#
# Import schemas into Confluent Schema Registry, keeping their original
# schema IDs and version numbers.
#
#   SR_URL=http://localhost:8081 EXPORT_DIR=./schema-export ./import_schemas.sh
#
# Needs: curl, jq.  The registry must allow subject mode changes
# (SCHEMA_REGISTRY_MODE_MUTABILITY=true).
#
# Expected input — the layout written by the schemas-ccompat-export command:
#
#   <EXPORT_DIR>/manifest.json        {"subjects": [...]} in dependency order
#   <EXPORT_DIR>/<subject>/v<n>.json  one file per version, posted unchanged
#
# Each version file is already a valid Confluent request body:
#   {"subject":..., "version":n, "id":n, "schema":"...", "references":[...]}
#
# What it does per subject:
#   1. PUT  /mode/<subject>        {"mode":"IMPORT"}     accept explicit id/version
#   2. POST /subjects/<subject>/versions                 once per version file
#   3. PUT  /mode/<subject>        {"mode":"READWRITE"}  back to normal
#
# Order matters: a schema that references another subject can only be imported
# after that subject exists, which is why manifest.json is followed rather than
# the directory listing.

set -e

SR_URL="${SR_URL:-http://localhost:8081}"
EXPORT_DIR="${EXPORT_DIR:-./schema-export}"

# ---------------------------------------------------------------------------
# Customise here: extra curl flags for your registry, for example
#   AUTH="-u my-api-key:my-api-secret"
#   AUTH="--cacert /path/to/ca.pem"
# Leave empty if the registry needs neither.
# ---------------------------------------------------------------------------
AUTH="${AUTH:-}"

CONTENT_TYPE="Content-Type: application/vnd.schemaregistry.v1+json"


if [ ! -d "$EXPORT_DIR" ]; then
  echo "No such directory: $EXPORT_DIR" >&2
  exit 1
fi

if [ ! -f "$EXPORT_DIR/manifest.json" ]; then
  echo "No manifest.json in $EXPORT_DIR." >&2
  echo "It records the order subjects must be imported in; re-run the export." >&2
  exit 1
fi

# The subject list goes to a file first. Reading it with a pipe instead would
# run the loop in a subshell, where "exit" cannot stop the whole script.
order_file=$(mktemp)
jq -r '.subjects[]' "$EXPORT_DIR/manifest.json" > "$order_file"


while read -r subject; do

  # The exporter swaps these characters for "_" when naming the directory
  # (sanitiseFileName), so apply the same rule to find it again.
  dir="$EXPORT_DIR/$(printf '%s' "$subject" | tr '/\\:*?"<>|' '_')"

  # Newest-last list of version files. sort -V keeps v10 after v2, which
  # plain alphabetical order would not.
  files=$(ls "$dir" 2>/dev/null | grep -E '^v[0-9]+\.json$' | sort -V)

  # A subject pulled in as a dependency only carries the referenced version,
  # so do not expect v1.json specifically — any version file will do.
  if [ -z "$files" ]; then
    echo "skipping $subject: no version files"
    continue
  fi

  # Percent-encode the name so a subject containing "/" or ":" is not read
  # as extra path segments in the URL.
  encoded=$(printf '%s' "$subject" | jq -rR @uri)

  echo "==> $subject"

  # IMPORT mode tells the registry to take the id and version from the file
  # rather than allocating new ones. force=true is required once the registry
  # holds any subject.
  curl -sS $AUTH -X PUT "$SR_URL/mode/$encoded?force=true" \
    -H "$CONTENT_TYPE" -d '{"mode": "IMPORT"}' > /dev/null

  for file in $files; do

    printf '    %s ... ' "$file"

    # --fail-with-body makes curl treat an HTTP error as a failure but still
    # hand back the registry's explanation, which is the part worth reading.
    if ! response=$(curl -sS --fail-with-body $AUTH -X POST \
        "$SR_URL/subjects/$encoded/versions" \
        -H "$CONTENT_TYPE" --data @"$dir/$file" 2>&1); then

      echo "FAILED"
      echo "$response" >&2

      # Return the subject to normal before giving up, so a failed run does
      # not leave it stuck in IMPORT mode.
      curl -sS $AUTH -X PUT "$SR_URL/mode/$encoded?force=true" \
        -H "$CONTENT_TYPE" -d '{"mode": "READWRITE"}' > /dev/null

      rm -f "$order_file"
      exit 1
    fi

    echo "ok"
  done

  curl -sS $AUTH -X PUT "$SR_URL/mode/$encoded?force=true" \
    -H "$CONTENT_TYPE" -d '{"mode": "READWRITE"}' > /dev/null

done < "$order_file"


rm -f "$order_file"

echo "Done."
