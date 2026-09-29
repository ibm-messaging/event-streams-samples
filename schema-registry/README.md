# IBM Event Streams Schema Registry Scripts

This directory contains utility scripts for managing schemas in the [IBM Event Streams](https://cloud.ibm.com/docs/services/EventStreams?topic=eventstreams-getting_started) Schema Registry.

## Scripts

### `import_schemas.sh` - Batch schema import

Imports all schemas from a local export directory into the Schema Registry, preserving the original schema IDs using subject-level `IMPORT` mode.

This is useful when migrating schemas from IBM Event Streams schema registry to Confluent schema registry.

#### Prerequisites

- [`curl`](https://curl.se/) 7.76.0 or later
- [`jq`](https://stedolan.github.io/jq/) 1.6 or later
- A Schema Registry that allows subject mode changes (`SCHEMA_REGISTRY_MODE_MUTABILITY=true`).

#### Expected export directory layout

The script expects schemas to be arranged in the layout created by Event Streams CLI schema export command:

```
schema-export/
  manifest.json
  <subject-name>/
    v1.json
    v2.json
    ...
```

`manifest.json` lists the subjec`manifest.json` lists the subjects in the order they must be imported, so that a schema referencing another subject is imported after its dependency:

```json
{
  "subjects": ["my-topic-value", "my-other-topic-value"]
}
```

Subject directories are named after the subject with the characters `/\:*?"<>|` replaced by `_`. A subject exported only as a dependency may contain a single version file that is not `v1.json`.

Each JSON file must contain the fields exported by the Schema Registry API, at minimum:

```json
{
  "subject": "my-topic-value",
  "version": 1,
  "id": 100001,
  "schema": "{\"type\":\"record\", ...}",
  "schemaType": "AVRO"
}
```

#### Usage

Set the `SR_URL` environment variable to your Schema Registry endpoint and run the script (default: `http://localhost:8081`):

```bash
SR_URL=https://<your-schema-registry-url> ./import_schemas.sh
```

You can also override the export directory (default: `./schema-export`):

```bash
SR_URL=https://<your-schema-registry-url> \
  EXPORT_DIR=/path/to/schema-export \
  ./import_schemas.sh
```

#### What the script does

For each subject listed in `manifest.json`, the script:

1. Switches the subject into `IMPORT` mode - this allows schemas to be registered with their original IDs.
2. Posts each versioned schema file in order.
3. Restores the subject to `READWRITE` mode.

If a schema fails to register, the subject is restored to `READWRITE` before the script exits, so a failed run does not leave it stuck in `IMPORT` mode.

#### Authentication

If your Schema Registry requires authentication, set the `AUTH` environment variable (or edit the `AUTH` variable in the script). It is passed to every `curl` call:

```bash
AUTH="-u token:<api-key>" SR_URL=https://<your-schema-registry-url> ./import_schemas.sh
```

The same variable can carry other curl flags your registry needs, for example `AUTH="--cacert /path/to/ca.pem"`.
