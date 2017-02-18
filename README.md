# ditto

Yaml powered service mocking server for HTTP and Thrift protocols.

## Usage
ditto is powered entirely by the YAML config file it reads.  A basic config
that mocks a simple HTTP based service with one endpoint could look like this:

```yaml
http:
- port: 8081
  endpoints:
  - uri: "/get"
    method: "GET"
    status: 200
    body: "OK"
    latency:
      min: 10
      max: 50
```

This will bind to port `8081` and respond to `GET` requests to `/get` with an
HTTP status code of `200` and a body that contains `OK`. Each call to the mock
will take somewhere within 10 to 50 milliseconds.

A more complete default `config.yml` is located in the root directory. Each 
protocol is separated out into its own YAML document. This is mandatory to 
correctly load each protocols mock. The current YAML structure is also 
mandatory. If a field is missing, the system will not work.

### Different Config File
The file ditto reads can be overridden by setting the `ditto.config-file` 
property to a different file path.

### How it works
Each protocol is located in a sub project and implements the `Responder` trait 
in `ditto-core` settings its `protocol` field to match the yaml root level YAML
key. The `ditto-core` project uses a `ServiceLoader` to dynamically load all
protocols and give them the correct YAML document to parse and start service
mocks.

Both `ditto-http` and `ditto-thrift` use [finagle](https://github.com/twitter/finagle)
to create the mocks, but that is not a requirement. Anything that listens to a
port on its own thread will work correctly.

### Thrift
When using thrift, ditto will parse the supplied IDL files for specific 
annotations and generate scala files that will output random valid responses.
The responses are deterministic based on the input of the service call that way
calling the function twice with the same input will always return the same
response. 

#### Annotations
##### bool
- `true`
- `false`
- `both`

##### byte
- `???`

##### i16
- `range` Inclusive range from minimum value to maxium value
- ``
- 

##### i32


##### i64


##### double


##### binary


##### string
