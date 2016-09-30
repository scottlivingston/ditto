namespace java io.livingston.ditto.thrift
#@namespace scala io.livingston.ditto.thrift

service Test {
    void test();
    i64 rand();
    i32 randMax(1: i32 max);
}
