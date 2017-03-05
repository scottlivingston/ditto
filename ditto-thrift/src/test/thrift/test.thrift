namespace java io.livingston.ditto.thrift
#@namespace scala io.livingston.ditto.thrift

struct EchoTest {
 1: required i64 int;
 2: optional string echo;
}

service EchoService {
    i32 echoInt(1: i32 i);
    string echoString(1: string str);
}
