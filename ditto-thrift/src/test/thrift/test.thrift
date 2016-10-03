namespace java io.livingston.ditto.thrift
#@namespace scala io.livingston.ditto.thrift

service EchoService {
    i32 echoInt(1: i32 i);
    string echoString(1: string str);
}
