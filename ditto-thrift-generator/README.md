# Ditto Thrift Response Generator

## Language Definition

### string
| Key | Values | Default |
|-----|--------|---------|
|content|alpha, numeric, alphaNumeric|alpha|
|length|[1,10], 1, [10]|[8-12]|
|style|firstUpper, upper, lower|lower|

#### Examples
`string` containing only alphabetical characters between 5 and 10 characters
long where the first character is upper case and the rest are lower case.
```json
{
  "content":"alpha",
  "length":[5,10],
  "style":"firstUpper"
}
```

### i16, i32, i64, double
| Key | Values | Default |
|-----|--------|---------|
|range|[1,10], 1, [10]|[Type.Min-Type.Max]|

#### Examples
number between 0 and 10.
```json
{"range":[0,10]}
```
number between 0 and the maximum value for that numeric type.
```json
{"range":"positive"}
```

### bool
| Key | Values | Default |
|-----|--------|---------|
|content|true, false|either true or false|

#### Examples
`bool` that's always false.
```json
{"content":"false"}
```

### binary
| Key | Values | Default |
|-----|--------|---------|
|length|[0,10], 1, [10]|[8,12]|

`binary` containing between 5 and 10 random bytes.
#### Examples
```json
{"length":[5,10]}
```

### map\<k, v\>
| Key | Values | Default |
|-----|--------|---------|
|size|[1,10], 1, [10]|[8-12]|
|k|object from primitive or container|NA|
|v|object from primitive or container|NA|

#### Examples
`map` of size 1 to 10 with a key that is a 1 element `list` or `set` container
using default constraints on the type the list holds, and a value of type 
`string` containing only lower case alphabetical characters.
```json
{
  "size":[1,10], 
  "k":{
    "size":1,
    "v":{}
  }, 
  "v":{
    "content":"alpha"
  }
}
```

### list\<t\>, Set\<t\>
| Key | Values | Default |
|-----|--------|---------|
|size|[1,10], 1, [10]|[8-12]|
|v|object from primitive or container|NA|

#### Examples
`list` or `set` of size 1 to 10 with a value of type `string` that containers
both alphabetical and numeric characters.
```json
{
  "size":[1,10], 
  "v":{
    "content":"alphaNumeric"
  }
}
```
