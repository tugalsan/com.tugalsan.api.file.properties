module com.tugalsan.api.file.properties {
    
    requires com.tugalsan.api.union;
    requires com.tugalsan.api.tuple;
    requires com.tugalsan.api.function;
    requires com.tugalsan.api.stream;
    requires com.tugalsan.api.string;
    //exports com.tugalsan.api.file.properties.client; NOT GWT able
    exports com.tugalsan.api.file.properties.server;
}
